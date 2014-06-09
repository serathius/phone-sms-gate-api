#ifndef EMAILSMSCLIENT_H
#define EMAILSMSCLIENT_H

#include "MailConnection.h"

class EmailSMSClient
{
    MailConnection* _mc; // our own MailConnection;
public:
    /* Create EmailSMSClient over mail connection */
    EmailSMSClient(MailConnection* mc);
    /* Sends sms to given recipient */
    std::string Send(std::string author, std::string recipient, std::string subject, std::string body, std::string loc);
    /* Update replies from android application */
    void Update();
    /* Check if we have confirmation of message with given sha code */
    std::string Check(std::string sha);
    /* Close current connection */
    void CloseConnection();
};

#endif // EMAILSMSCLIENT_H
