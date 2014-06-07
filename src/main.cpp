#include "EmailSMSClient.h"

int main(int argc, char** argv)
{
    MailConnection m = MailConnection("smtp.gmail.com", "phonesmsgateapi@gmail.com", "phonesms");
    if (m.isValid())
    {
        EmailSMSClient emc = EmailSMSClient(&m);
        std::string author = "Tester";
        std::string recipient = "phonesmsgateapi@gmail.com";
        std::string subject = "Test Message Subject";
        std::string text = "This is test message body.\nThis is test message body.\n";
        emc.Send(author, recipient, subject, text);
    }
    m.CloseConnection();
    return 0;
}
