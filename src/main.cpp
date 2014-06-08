#include "EmailSMSClient.h"

int main(int argc, char** argv)
{
    MailConnection m = MailConnection("smtp.gmail.com", "phonesmsgateapi@gmail.com", "phonesms", TYPE_SMTP);
    if (m.isValid())
    {
        EmailSMSClient emc = EmailSMSClient(&m);
        std::string author = "Tester";
        std::string recipient = "756789213"; // phone number
        std::string subject = "SMS-GATE";
        std::string text = "This is test message body.\nThis is test message body.\n";
        std::string loc = "1"; // national
        emc.Send(author, recipient, subject, text, loc);
    }
    m.CloseConnection();
    MailConnection mc = MailConnection("pop.gmail.com", "phonesmsgateapi@gmail.com", "phonesms", TYPE_POP3, 995);
    if (mc.isValid())
    {
        EmailSMSClient emc = EmailSMSClient(&mc);
        emc.Receive();
    }
    mc.CloseConnection();
    return 0;
}
