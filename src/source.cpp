#include "source.h"

Source::Source(std::stringstream& content) : str(content)
{
    if (str.eof())
        return;
    tpos = TextPos();
    NextLine();
}

Source::~Source()
{
}

bool Source::NextLine()
{
    if (str.eof())
        return false; // false if end of file

    getline(str, line);
    line.push_back('\n');

    tpos.line++;
    tpos.pos = 0;
    while (line[tpos.pos] == ' ' || line[tpos.pos] == '\t')
        tpos.pos++; // skip whitespaces

    return true;
}

int Source::NextChar()
{
    bool next = true;
    if (tpos.pos == line.size()) // end of line
        next = NextLine();

    if (next) // new line found
        return line.at(tpos.pos++);
    // no new line found, so its end of file
    return EOF;
}
