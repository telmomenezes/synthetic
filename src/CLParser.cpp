/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "CLParser.h"
#include <iostream>
#include <stdlib.h>
#include <string.h>


using std::cerr;
using std::endl;


CLParser::CLParser(int minOperands, int maxOperands)
{
    _minOperands = minOperands;
    _maxOperands = maxOperands;
}


CLParser::~CLParser()
{
    for (map<string, CLOption*>::iterator iterOpt = _options.begin();
            iterOpt != _options.end();
            iterOpt++) {

        CLOption* opt = (*iterOpt).second;
        delete opt;
    }
}


void CLParser::addOption(string name, string sopt, string lopt, bool hasValue, string desc)
{
    CLOption* opt = new CLOption();
    opt->sopt = sopt;
    opt->lopt = lopt;
    opt->desc = desc;
    opt->hasValue = hasValue;
    opt->value = "";

    _options[name] = opt;
}


void CLParser::parse(int argc, char* argv[])
{
    bool operands = false;
    CLOption* curOption = NULL;

    for (int i = 1; i < argc; i++) {
        char* token = argv[i];

        if (operands || curOption) {
            if (token[0] == '-')
                error("bad parameters.");

            if (curOption) {
                curOption->value = string(token);
                curOption = NULL;
            }
            else {
                _operands.push_back(token);
            }
        }
        else {
            if (token[0] == '-') {
                curOption = findOption(token);
            }
            else {
                operands = true;
                _operands.push_back(token);
            }
        }
    }

    int opCount = operandCount();
    if (opCount < _minOperands)
        error("too few operands.");
    if ((_maxOperands >= 0) && (opCount > _maxOperands))
        error("too many operands.");
}


void CLParser::error(string message)
{
    cerr << "error: " << message << endl;
    exit(1);
}


CLOption* CLParser::findOption(char* token)
{
    for (map<string, CLOption*>::iterator iterOpt = _options.begin();
            iterOpt != _options.end();
            iterOpt++) {

        CLOption* opt = (*iterOpt).second;

        if ((strlen(token) > 1) && (!strcmp(token + 1, opt->sopt.c_str())))
            return opt;

        if ((strlen(token) > 2)
            && (token[1] == '-')
            && (!strcmp(token + 2, opt->lopt.c_str()))) {

            return opt;
        }
    }

    error("unknown option.");

    return NULL;
}


string CLParser::getStringOption(string name, string def)
{
    CLOption* opt = _options[name];
    if (!opt)
        return def;
    if(opt->value.size() == 0)
        return def;
    return opt->value;
}


const char* CLParser::getOperand(unsigned int index)
{
    if (index < _operands.size())
        return _operands[index];
    return "";
}

