#ifndef MAILCONNECTION_H
#define MAILCONNECTION_H

#include "base64.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <netinet/in.h>
#include <iostream>
#include <cstring>
#include <openssl/bio.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <unistd.h>

typedef struct
{
    int socket;
    SSL *sslHandle;
    SSL_CTX *sslContext;
} SSLConnection;


class MailConnection
{
    std::string _address;
    std::string _login; // login in base64
    std::string originalLogin;
    std::string _password; // password in base64
    int _port;
    int _sock;
    bool valid; // tells if initialization was successful
    std::string error; // last occured error
    SSLConnection* conn;
    const int BUFSIZE = 1024;

    int tcpConnect();
    int sslConnect();
    void sslDisconnect();
    std::string sslRead();
    void sslWrite(const char *text);
    int authenticate();
public:
    MailConnection(std::string address, std::string login, std::string password, int port = 465);
    ~MailConnection();
    std::string GetError() { return error; }
    bool isValid() { return valid; }
    void Send(std::string author, std::string recipient, std::string subject, std::string body);
    void CloseConnection();

};

#endif // MAILCONNECTION_H
