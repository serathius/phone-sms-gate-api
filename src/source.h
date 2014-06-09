#ifndef SOURCE_H
#define SOURCE_H

#include <iostream>
#include <string>
#include <sstream>

class TextPos
{
public:
    int line; // line in string
    int pos; // position in line
    TextPos(int l = 0, int p = 0) : line(l), pos(p) { }
};

class Source
{
    std::stringstream& str;

    TextPos tpos;

    bool NextLine(); // get next line from str
    std::string line; // current line

public:
    Source(std::stringstream& content);
    ~Source();
    int NextChar(); // next char from input
    const TextPos& GetPos() const { return tpos; } // position in stringstream
};

#endif // SOURCE_H
