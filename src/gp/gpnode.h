/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "types.h"


#define GPFUN_COUNT 10


enum gpnode_type {FUN, VAR, VAL};

enum gpnode_fun {SUM=0, SUB=1, MUL=2, DIV=3, EQ=4, GRT=5, LRT=6, GET=7, LET=8, ZER=9};


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

