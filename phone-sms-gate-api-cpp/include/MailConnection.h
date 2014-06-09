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
#include <fstream>

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
    std::string _address;       // host address
    std::string _login;         // login in base64
    std::string originalLogin;  // original login (before encode)
    std::string originalPass;   // original password (before encode)
    std::string _password;      // password in base64
    int _port;                  // connection's port
    int _type;                  // SMTP or POP3
    int _sock;                  // socket descriptor
    bool valid;                 // tells if initialization was successful
    SSLConnection* conn;        // pointer to ssl connection
    const int BUFSIZE = 8192;   // size of read / write buffer

    /* Create tcp session to given in constructor host */
    int tcpConnect();
    /* Create ssl connection to host from tcp session */
    int sslConnect();
    /* Disconnect ssl connection */
    void sslDisconnect();
    /* Read bytes from ssl connection */
    std::string sslRead();
    /* Write bytes to ssl connection */
    void sslWrite(const char *text);
    /* Authenticates user with given login and password */
    int authenticate();
public:
    /* Class constructor and destructor */
    MailConnection(std::string address, std::string login, std::string password, int type, int port = 465);
    ~MailConnection();
    /* true if connection is valid, false otherwise */
    bool isValid() { return valid; }
    /* Send email with given subject, body and loc to recipient */
    std::string Send(std::string author, std::string recipient, std::string subject, std::string body, std::string loc);
    /* Closes connection (ssl connection) */
    void CloseConnection();
    /* Update's informations from mail */
    void Update();
    /* Check if we have confirmation to given sha */
    std::string Check(std::string sha);
};

#endif // MAILCONNECTION_H
