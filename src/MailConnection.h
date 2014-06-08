#ifndef MAILCONNECTION_H
#define MAILCONNECTION_H

#include "base64.h"
#include "sha1.h"
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
#include <time.h>
#include <stdlib.h>

typedef struct
{
    int socket;
    SSL *sslHandle;
    SSL_CTX *sslContext;
} SSLConnection;

enum types
{
    TYPE_SMTP    = 0,
    TYPE_POP3    = 1,
};

class MailConnection
{
    std::string _address;
    std::string _login; // login in base64
    std::string originalLogin;
    std::string originalPass;
    std::string _password; // password in base64
    int _port;
    int _type; // SMTP or POP3
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
    MailConnection(std::string address, std::string login, std::string password, int type, int port = 465);
    ~MailConnection();
    std::string GetError() { return error; }
    bool isValid() { return valid; }
    void Send(std::string author, std::string recipient, std::string subject, std::string body, std::string loc);
    void CloseConnection();
    void Receive();

};

#endif // MAILCONNECTION_H
