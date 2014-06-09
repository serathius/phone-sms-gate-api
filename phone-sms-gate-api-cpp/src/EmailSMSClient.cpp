#include "../include/EmailSMSClient.h"

EmailSMSClient::EmailSMSClient(MailConnection* mc) : _mc(mc)
{

}

std::string EmailSMSClient::Send(std::string recipient, std::string body, std::string loc)
{
    std::string author = "MailToSMS";
    std::string subject = "SMS-GATE";
    std::string result;
    result.clear(); // clear just in case
    if (_mc->isValid())
        result = _mc->Send(author, recipient, subject, body, loc);
    return result; // return empty string if there is no result
}

void EmailSMSClient::Update()
{
    if (_mc->isValid())
        _mc->Update();
}

std::string EmailSMSClient::Check(std::string sha)
{
    std::string result;
    result.clear(); // clear just in case
    if (_mc->isValid())
        result = _mc->Check(sha);
    return result; // return empty string if there is no result
}

void EmailSMSClient::CloseConnection()
{
    if (_mc->isValid())
        _mc->CloseConnection();
}
