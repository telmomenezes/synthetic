/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include <stdlib.h>
#include <stdio.h>
#include "gpnode.h"


namespace syn {

GPNode::GPNode(gpnode_type nodetype,
                gpnode_fun fun,
                gpval val,
                unsigned int var,
                GPNode* parent)
{
    this->type = nodetype;
    this->parent = parent;

    if (nodetype == FUN) {
        this->fun = fun;
        this->arity = fun_arity(fun);
        this->condpos = fun_condpos(fun);
    }
    else if (nodetype == VAR) {
        this->var = var;
        this->arity = 0;
        this->condpos = -1;
    }
    else {
        this->val = val;
        this->arity = 0;
        this->condpos = -1;
    }

    this->stoppos = this->arity;
}


GPNode::~GPNode()
{
}


int GPNode::fun_condpos(gpnode_fun fun)
{
    switch(fun) {
        case ZER:
            return 1;
        case EQ:
        case GRT:
        case LRT:
            return 2;
        default:
            return -1;
    }
}


unsigned int GPNode::fun_arity(gpnode_fun fun)
{
    switch(fun) {
        case EXP:
        case LOG:
        case SIN:
        case ABS:
            return 1;
        case SUM:
        case SUB:
        case MUL:
        case DIV:
        case MIN:
        case MAX:
            return 2;
        case ZER:
            return 3;
        case EQ:
        case GRT:
        case LRT:
            return 4;
        default:
            return 0;
    }
}


void GPNode::print()
{
    if (this->type == VAL) {
        printf("%f", this->val);
        return;
    }

    if (this->type == VAR) {
        printf("$%d", this->var);
        return;
    }

    if (this->type != FUN) {
        printf("???");
        return;
    }

    switch(this->fun) {
        case SUM:
            printf("+");
            return;
        case SUB:
            printf("-");
            return;
        case MUL:
            printf("*");
            return;
        case DIV:
            printf("/");
            return;
        case ZER:
            printf("ZER");
            return;
        case EQ:
            printf("==");
            return;
        case GRT:
            printf(">");
            return;
        case LRT:
            printf("<");
            return;
        case EXP:
            printf("EXP");
            return;
        case LOG:
            printf("LOG");
            return;
        case SIN:
            printf("SIN");
            return;
        case ABS:
            printf("ABS");
            return;
        case MIN:
            printf("MIN");
            return;
        case MAX:
            printf("MAX");
            return;
        default:
            printf("F??");
            return;
    }
}

}
