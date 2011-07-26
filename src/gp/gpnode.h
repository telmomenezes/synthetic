/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "types.h"


#define GPFUN_COUNT 21


enum gpnode_type {FUN, VAR, VAL};

enum gpnode_fun {ON=0, OFF=1, AND=2, OR=3, XOR=4, BNOT=5, NOT=6, SUM=7, SUB=8, MUL=9, DIV=10, EQ=11, GRT=12, LRT=13, GET=14, LET=15, ZER=16, SHL=17, SHR=18, ROTL=19, ROTR=20};


class GPNode
{
public:
    gpnode_type type;
    gpval val;
    gpnode_fun fun;
    unsigned int arity;
    GPNode* params[4];
    gpval vals[4];
    GPNode* parent;

    int curpos;
    unsigned int stoppos;
    int condpos;

    GPNode();
    virtual ~GPNode();

    void init(gpnode_type nodetype,
               gpnode_fun fun_p,
               gpval val_p,
               GPNode* parent_p);

    unsigned int fun_arity(gpnode_fun fun);

    void print();

private:
    int GPNode::fun_condpos(gpnode_fun fun);
};

