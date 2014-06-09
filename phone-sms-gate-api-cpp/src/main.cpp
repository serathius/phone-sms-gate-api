#include "../include/EmailSMSClient.h"

int main(int argc, char** argv)
{
    // open connection to SMTP server
    MailConnection m = MailConnection("smtp.gmail.com", "phonesmsgateapi@gmail.com", "phonesms", TYPE_SMTP);
    std::string sha;
    if (m.isValid())
    {
        EmailSMSClient emc = EmailSMSClient(&m);
        std::string author = "Tester";
        std::string recipient = "756789213"; // phone number
        std::string subject = "SMS-GATE";
        std::string text = "Random text.";
        std::string loc = "1"; // national
        sha = emc.Send(author, recipient, subject, text, loc);
        emc.CloseConnection();
    }
    sleep(5);
    // open connection to POP3 server
    MailConnection mc = MailConnection("pop.gmail.com", "phonesmsgateapi@gmail.com", "phonesms", TYPE_POP3, 995);
    if (mc.isValid())
    {
        EmailSMSClient emc = EmailSMSClient(&mc);
        emc.Update();
        emc.Check(sha);
        emc.CloseConnection();
    }
    return 0;
}
