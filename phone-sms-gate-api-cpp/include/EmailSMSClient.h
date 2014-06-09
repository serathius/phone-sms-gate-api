#ifndef EMAILSMSCLIENT_H
#define EMAILSMSCLIENT_H

#include "MailConnection.h"

class EmailSMSClient
{
    MailConnection* _mc; // our own MailConnection;
public:
    EmailSMSClient(MailConnection* mc);
    void Send(std::string author, std::string recipient, std::string subject, std::string body, std::string loc);
    void Receive();
};

#endif // EMAILSMSCLIENT_H
