#include "scanner.h"

Scanner::KeyRec Scanner::keywordTable[NKEYS] =
{
    { "Subject:",subjectsy },
    { "SMS-GATE",smsgatesy },
    { "To:",     tosy      },
    { "REPLY",   replysy   },
};

bool Scanner::isNumber(char c)
{
    if (c >= '0' && c <= '9')
        return true;
    return false;
}

bool Scanner::isHexNumber(char c)
{
    if (isNumber(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
        return true;
    return false;
}

bool Scanner::isSha(std::string s)
{
    if (s.size() != 40)
        return false;

    for (unsigned i = 0; i < s.size(); ++i)
    {
        if (isNumber(s[i]) || s[i] >= 'a' && s[i] <= 'f' || s[i] >= 'A' && s[i] <= 'F')
            continue;
        return false;
    }
    return true;
}

bool Scanner::isCode(std::string s)
{
    for (unsigned i = 0; i < s.size(); ++i)
    {
        if (isNumber(s[i]))
            continue;
        else
            return false;
    }
    return true;
}

void Scanner::back(int size)
{
    src.back(size);
}

Symbol Scanner::NextSymbol()
{
    while (c > 255 || c < -1 || isspace(c))
        if (c > 255 || c < -1)
        {
            Nextc();
            return outofrange;
        }
        else Nextc(); // skip whitespaces

    if (isHexNumber(c)) // sha1
    {
        spell.clear();
        do
        {
            spell.push_back(c);
            Nextc();
        } while (isalnum(c));

        if (isSha(spell))
            return sha;
        if (isCode(spell))
            return code;

        back(spell.size()+1);
        Nextc();
    }

    if (isalpha(c) || c == '_') // check if known keyword
    {
        spell.clear();
        do
        {
            spell.push_back(c);
            Nextc();
        } while (isalnum(c) || c == '_' || c == ':' || c == '-');

        for (int i = 0; i < NKEYS; ++i)
        {
            if (spell.compare(keywordTable[i].keyword) == 0)
                return keywordTable[i].atom;
        }
        return another;
    }

    spell.clear();
    spell.push_back(c);
    switch (c)
    {
        case '<':
            Nextc();
            return lbracket;
        case '>':
            Nextc();
            return rbracket;
        case '.':
            Nextc();
            return dot;
        case '@':
            Nextc();
            return at;
        case EOF:
            return eof;
    }
    Nextc();
    return unknown;
}

bool Scanner::skipLine()
{
    bool result = src.skipLine();
    Nextc();
    return result;
}
