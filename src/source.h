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
    bool skipLine() { return NextLine(); }
    void back(int size) { tpos.pos = (tpos.pos > size ? tpos.pos - size : 0); }
    const TextPos& GetPos() const { return tpos; } // position in stringstream
    void Reset();
};

#endif // SOURCE_H
