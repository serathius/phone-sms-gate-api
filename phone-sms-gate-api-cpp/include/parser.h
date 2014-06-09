#ifndef PARSER_H
#define PARSER_H

#include "scanner.h"

struct Result
{
    std::string sha;
    std::string code;
};

class Parser
{
public:
    Parser(Scanner &s) : scan(s)
    {
    }

    /* Starts parsing process */
    Result Analyze();
private:
    Scanner &scan;          // reference to scanner
    Symbol symbol;          // current symbol
    std::string resultCode; // result code from mail
    std::string resultSha;  // parsed sha code from mail

    /* Takes next symbol from scanner */
    void Nexts() { symbol = scan.NextSymbol(); }

    /* Check if symbol is correct, calls HandleSyntaxError else */
    void accept(Symbol atom);
    /* Tells that mail is incorrect */
    void HandleSyntaxError(Symbol symbol, Symbol expected);
    /* Parse message from email */
    void Message();
    /* Parse recipient from email */
    void Recipient();
};

#endif // PARSER_H
