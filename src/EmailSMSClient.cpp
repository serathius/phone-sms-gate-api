#include "EmailSMSClient.h"

EmailSMSClient::EmailSMSClient(MailConnection* mc) : _mc(mc)
{

}

void EmailSMSClient::Send(std::string author, std::string recipient, std::string subject, std::string body)
{
    if (_mc->isValid())
        _mc->Send(author, recipient, subject, body);
}
