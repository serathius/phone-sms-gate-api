#include "../include/parser.h"

Result Parser::Analyze()
{
    Result result;
    Nexts();
    Message();
    result.sha = resultSha;
    result.code = resultCode;
    return result;
}

void Parser::accept(Symbol atom)
{
    if (symbol == atom)
        Nexts();
    else
        HandleSyntaxError(symbol, atom);
}

void Parser::Message()
{
    while (symbol != subjectsy)
    {
        if (symbol == eof) // prevent infinite loop if there is no subjectsy in entire mail
            return;

        Nexts();
    }
    accept(subjectsy);
    if (symbol == smsgatesy)
        accept(smsgatesy);
    else return; // its not response mail
    if (symbol == replysy)
        accept(replysy);
    else return; // its not response mail
    accept(tosy);
    Recipient();
    if (symbol == sha)
        resultSha = scan.Spell();
    accept(sha);
    if (symbol == code)
        resultCode = scan.Spell();
    accept(code);
}

void Parser::Recipient()
{
    accept(lbracket);       // symbol <
    accept(another);        // author name (ex. phonesmsgateapi)
    accept(at);             // symbol @
    accept(another);        // mail name (ex. gmail)
    accept(dot);            // symbol .
    accept(another);        // domain (ex. com)
    accept(rbracket);       // symbol >
}

void Parser::HandleSyntaxError(Symbol symbol, Symbol expected)
{
    std::cout << "Syntax Error! Expected: " << expected << ", Read symbol: " << symbol << std::endl;
    std::cout << "Current spell: " << scan.Spell() << std::endl;
    abort(); // @todo: find better way to handle syntax error (if any) :P
}
