/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once
#include <vector>
#include <map>
#include <string>


using std::vector;
using std::map;
using std::string;


class CLOption {
public:
    string sopt;
    string lopt;
    string desc;
    bool hasValue;
    string value;
};


class CLParser {
public:
    CLParser(int minOperands, int maxOperands);
    virtual ~CLParser();

    void addOption(string name, string sopt, string lopt, bool hasValue, string desc);
    void parse(int argc, char* argv[]);
    string getStringOption(string name, string def);
    const char* getOperand(unsigned int index);
    int operandCount() {return _operands.size();}

private:
    void error(string message);
    CLOption* findOption(char* token);

    map<string, CLOption*> _options;
    vector<char*> _operands;

    int _minOperands;
    int _maxOperands;
};

