#include "../include/MailConnection.h"
#include "../include/parser.h"

MailConnection::MailConnection(std::string address, std::string login, std::string password, int type, int port/* = 465*/)
    : _address(address), _type(type), _port(port), valid(true), _sock(0), conn(nullptr), originalLogin(login), originalPass(password)
{
    // convert login and password to base64 encoding
    _login = base64_encode(reinterpret_cast<const unsigned char*>(login.c_str()),login.size());
    _password = base64_encode(reinterpret_cast<const unsigned char*>(password.c_str()),password.size());

    if (!tcpConnect())
    {
        valid = false;
        return;
    }
    if (!sslConnect())
    {
        valid = false;
        return;
    }
    if (!authenticate())
        valid = false;
}

MailConnection::~MailConnection()
{
    if (conn)
        sslDisconnect();
}

int MailConnection::tcpConnect()
{
    std::cout << "Creating socket" << std::endl;
    _sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    // handle errors
    if (_sock == -1)
    {
        std::cerr << "Cannot create socket!" << std::endl;
        return 0;
    }

    // retrieve server address by name
    struct sockaddr_in server;
    struct hostent *host;
    server.sin_family = AF_INET;
    std::cout << "Resolving host address..." << std::endl;
    host = gethostbyname(_address.c_str());

    // handle incorrect host name
    if (host == static_cast<struct hostent*>(0))
    {
        std::cerr << "Unknown host: " << _address << std::endl;
        return 0;
    }

    server.sin_addr = *(reinterpret_cast<in_addr*>(host->h_addr));
    server.sin_port = htons(_port);

    // try to connect to host
    int result;
    int counter = 3;
    std::cout << "Connecting..." << std::endl;
    result = connect(_sock, reinterpret_cast<struct sockaddr*>(&server), sizeof(struct sockaddr_in));
    while (result == -1)
    {
        std::cerr << "Cannot connect to host: " << _address << std::endl;
        if (--counter >= 0)
        {
            std::cout << "Trying again..." << std::endl;
            result = connect(_sock, reinterpret_cast<struct sockaddr*>(&server), sizeof(struct sockaddr_in));
        }
        else
        {
            _sock = 0;
            return 0;
        }
    }
    return 1;
}

int MailConnection::sslConnect()
{
    conn = new SSLConnection();
    conn->sslHandle = NULL;
    conn->sslContext = NULL;

    // socket from TCP connection
    conn->socket = _sock;

    if (conn->socket)
    {
        // Register the error strings for libcrypto & libssl
        SSL_load_error_strings();
        // Register the available ciphers and digests
        SSL_library_init();

        // New context saying we are a client, and using SSL 2 or 3
        conn->sslContext = SSL_CTX_new(SSLv23_client_method());
        if (conn->sslContext == NULL)
        {
            std::cerr << "Cannont create sslContext!" << std::endl;
            return 0;
        }

        // Create an SSL struct for the connection
        conn->sslHandle = SSL_new(conn->sslContext);
        if (conn->sslHandle == NULL)
        {
            std::cerr << "Cannot create new SSL connection!" << std::endl;
            return 0;
        }

        // Connect the SSL struct to our connection
        if (!SSL_set_fd(conn->sslHandle, conn->socket))
        {
            std::cerr << "Cannot connect SSL struct to connection!" << std::endl;
            return 0;
        }

        // Initiate SSL handshake
        std::cout << "SSL Connecting..." << std::endl;
        if (SSL_connect(conn->sslHandle) != 1)
        {
            std::cerr << "SSL cannot connect to host!" << std::endl;
            return 0;
        }
    }
    else
    {
        std::cerr << "SSL cannot find TCP socket!" << std::endl;
        return 0;
    }

    return 1;
}

void MailConnection::CloseConnection()
{
    valid = false;
    if (conn)
        sslDisconnect();
}

void MailConnection::sslDisconnect()
{
    if (!conn)
        return;
    // close tcp connection
    std::string response;
    sslWrite("quit\r\n");
	response = sslRead();
    // and free ssl connection
    if (conn->socket)
        close(conn->socket);
    if (conn->sslHandle)
    {
        SSL_shutdown(conn->sslHandle);
        SSL_free(conn->sslHandle);
    }
    if (conn->sslContext)
        SSL_CTX_free(conn->sslContext);

    delete conn;
    conn = nullptr;
}


std::string MailConnection::sslRead()
{
    int received, count = 0;
    char buffer[BUFSIZE];
    std::string result;
    result.clear();
    if (conn)
    {
        while (true)
        {
            received = SSL_read(conn->sslHandle, buffer, BUFSIZE);

            if (received < BUFSIZE)
                buffer[received] = '\0';

            if (received > 0)
                result += buffer;
            // buffer is not full, so there is nothing more to read
            if (received < BUFSIZE)
                break;
            count++;
        }
    }

    return result;
}

void MailConnection::sslWrite(const char *text)
{
    if (conn)
        SSL_write(conn->sslHandle, text, strlen(text));
}

int MailConnection::authenticate()
{
    char buffer[BUFSIZE];
    std::string response;
    response = sslRead();
    std::cout << response;
    if (_type == TYPE_SMTP)
    {
        // say hello
        sslWrite(("ehlo "+_address+"\r\n").c_str());
        response = sslRead();
        std::cout << response;

        // start login
        sslWrite("AUTH LOGIN\r\n");
        response = sslRead();
        std::cout << response;

        // send encoded login through ssl
        sslWrite((_login+"\r\n").c_str());
        response = sslRead();
        std::cout << response;

        // send encoded password through ssl
        sslWrite((_password+"\r\n").c_str());
        response = sslRead();
        std::cout << response;

        // check if we are accepted
        if (response.compare("235 2.7.0 Accepted\r\n") == 0)
            return 1;
        return 0;
    }
    if (_type == TYPE_POP3)
    {
        // send non encoded login through ssl
        sslWrite(("user "+originalLogin+"\r\n").c_str());
        response = sslRead();
        std::cout << response;

        // send non encoded password through ssl
        sslWrite(("pass "+originalPass+"\r\n").c_str());
        response = sslRead();
        std::cout << response << std::endl;
    }
}

std::string MailConnection::Send(std::string author, std::string recipient, std::string subject, std::string body, std::string loc)
{
    std::string response;

    // author
	sslWrite(("MAIL FROM: <" + originalLogin + ">\r\n").c_str());
	response = sslRead();
	std::cout << response;

    // recipient
	sslWrite(("RCPT TO: <phonesmsgateapi@gmail.com>\r\n"));
	response = sslRead();
	std::cout << response;

    // mail body
	sslWrite("DATA\r\n");
    response = sslRead();
    std::cout << response;

    // check timestamp and calculate hash
    unsigned char hash[20];
    char hexstring[41];
    char timestamp[20];
    snprintf(timestamp, 20, "%d", time(NULL));
    char temp[80];
    // include timestamp to hash value
    strcpy(temp, body.c_str());
    strcpy(temp, timestamp);
    // calc hash
    sha1::calc(temp, sizeof(temp), hash);
    // convert to string
    sha1::toHexString(hash, hexstring);
    // concatenate body of the email
    std::string _body;
    _body  = "From: " + author + " <" + originalLogin + ">"
	   "\nSubject: " + subject +
	   "\nTo: <phonesmsgateapi@gmail.com>"
	   "\n\n" + hexstring + "\n" + timestamp + "\n" + recipient + "\n" + loc + "\n" + body;

	sslWrite(_body.c_str());
	sslWrite("\r\n.\r\n");
	// return sha1 code of sent message
	return hexstring;
}

void MailConnection::Update()
{
    std::string response;
    std::ofstream ofs;
    do
    {
        // get first email
        sslWrite("RETR 1\r\n");
        response = sslRead();
        std::stringstream ss;
        ss.clear();
        ss.str(response);

        // extract sha1 identifier and return code from message
        Source src = Source(ss);
        Scanner scan = Scanner(src);
        Parser prs = Parser(scan);
        Result result = prs.Analyze();
        // we dont get any sha code, so email is wrong
        if (!result.sha.size())
            break;
        // write sha and error to the end of log file
        ofs.open("log.txt", std::ios::app);
        ofs << result.sha << " " << result.code << "\n";
        ofs.close();
        sslWrite("DELE 1\r\n");
        response = sslRead();
    }
    while (true);
}

std::string MailConnection::Check(std::string sha)
{
    std::ifstream ifs;
    std::string buffer;
    buffer.clear(); // clear just in case
    ifs.open("log.txt");
    do
    {
        getline(ifs, buffer);
        // found given sha
        if (sha.compare(buffer.substr(0, 40)) == 0)
            return buffer.substr(41, buffer.size());
    } while (buffer.size() > 2);
    // send empty string if there is no confirmation
    buffer.clear();
    return buffer;
}
