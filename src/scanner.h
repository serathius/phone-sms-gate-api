#ifndef SCANNER_H
#define SCANNER_H

#include <string>
#include "source.h"

enum Symbol
{
    subjectsy         = 0,          // Subject:
    smsgatesy,                      // SMS-GATE
    tosy,                           // To:
    replysy,                        // REPLY

    MAXKEY            = replysy,    // max keyword
    NKEYS             = MAXKEY+1,   // keyword count

    lbracket          = NKEYS + 1,  // <
    rbracket,                       // >
    dot,                            // .
    at,                             // @
    sha,                            // sha1 code
    code,                           // return code from android
    another,                        // random string
    unknown,                        // unknown symbols
    eof,                            // end of file

    outofrange,                     // out of std::char range (0, 255)
    MAXSYM
};

class Scanner
{
private:
    struct KeyRec
    {
        std::string keyword;
        Symbol atom;                // lexical atom
    };
    static KeyRec keywordTable[NKEYS];

    int c;                          // current source char
    std::string spell;              // last read identificator
    Source& src;

    void Nextc()
    {
        c = src.NextChar();
    }

    bool isSha(std::string s);
    bool isNumber(char c);
    bool isHexNumber(char c);
    bool isCode(std::string s);
    void back(int size);
public:
    Scanner(Source &s) : src(s) { Nextc(); }
    Symbol NextSymbol();
    const std::string& Spell() { return spell; }
};

#endif // SCANNER_H
