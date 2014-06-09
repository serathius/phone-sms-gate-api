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

    Result Analyze();
private:
    Scanner &scan;
    Symbol symbol; // current symbol
    std::string resultCode;
    std::string resultSha;

    void Nexts() { symbol = scan.NextSymbol(); }

    void accept(Symbol atom);
    void HandleSyntaxError(Symbol symbol, Symbol expected);
    void Message();
    void Recipient();
};

#endif // PARSER_H
