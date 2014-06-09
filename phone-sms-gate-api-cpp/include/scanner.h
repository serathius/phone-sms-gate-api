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
    // this struct helps with keywords recognition
    struct KeyRec
    {
        std::string keyword;        // keyword
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

    /* Check if given string is Sha1 code */
    bool isSha(std::string s);
    /* Check if given symbol is a number */
    bool isNumber(char c);
    /* Check if given symbol is a hex number */
    bool isHexNumber(char c);
    /* Check if given string is a return code from android application */
    bool isCode(std::string s);
    /* rewind text pointer by given number of elements */
    void back(int size);
public:
    /* Create scanner and immediately get next char from source */
    Scanner(Source &s) : src(s) { Nextc(); }
    /* Return next symbol from source file / stream */
    Symbol NextSymbol();
    /* Return last symbol from stream */
    const std::string& Spell() { return spell; }
};

#endif // SCANNER_H
