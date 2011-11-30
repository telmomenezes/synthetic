/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include <stdlib.h>
#include <stdio.h>
#include <sstream>
#include "gpnode.h"


using namespace std;


namespace syn {

GPNode::GPNode(gpnode_type nodetype,
                gpnode_fun fun,
                gpval val,
                unsigned int var,
                GPNode* parent)
{
    init(nodetype, fun, val, var, parent);
}


GPNode::~GPNode()
{
}


void GPNode::init(gpnode_type nodetype,
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

    this->dyn_status = UNUSED;
}


void GPNode::clone(GPNode* node)
{
    init(node->type, node->fun, node->val, node->var, node->parent);
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


string GPNode::to_string()
{
    if (this->type == VAL) {
        std::stringstream sstm;
        sstm << "" << this->val;
        return sstm.str();
    }

    if (this->type == VAR) {
        std::stringstream sstm;
        sstm << "$" << this->var;
        return sstm.str();
    }

    if (this->type != FUN) {
        return "???";
    }

    switch(this->fun) {
        case SUM:
            return "+";
        case SUB:
            return "-";
        case MUL:
            return "*";
        case DIV:
            return "/";
        case ZER:
            return "ZER";
        case EQ:
            return "==";
        case GRT:
            return ">";
        case LRT:
            return "<";
        case EXP:
            return "EXP";
        case LOG:
            return "LOG";
        case SIN:
            return "SIN";
        case ABS:
            return "ABS";
        case MIN:
            return "MIN";
        case MAX:
            return "MAX";
        default:
            return "F??";
    }
}

}
