/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpnode.h"
#include <iostream>


using std::cout;
using std::endl;
using std::flush;


GPNode::GPNode()
{
}


GPNode::~GPNode()
{
}


void GPNode::init(gpnode_type nodetype,
                    gpnode_fun fun_p,
                    gpval val_p,
                    GPNode* parent_p)
{
    type = nodetype;
    parent = parent_p;

    if (nodetype == FUN) {
        fun = fun_p;
        arity = fun_arity(fun);
        condpos = fun_condpos(fun);
    }
    else {
        val = val_p;
        arity = 0;
        condpos = -1;
    }

    stoppos = arity;
}


int GPNode::fun_condpos(gpnode_fun fun)
{
    switch(fun) {
        case ZER:
            return 1;
        case EQ:
        case GRT:
        case LRT:
        case GET:
        case LET:
            return 2;
        default:
            return -1;
    }
}


unsigned int GPNode::fun_arity(gpnode_fun fun)
{
    switch(fun) {
        case ON:
        case OFF:
        case NOT:
        case BNOT:
            return 1;
        case AND:
        case OR:
        case XOR:
        case SUM:
        case SUB:
        case MUL:
        case DIV:
        case SHL:
        case SHR:
        case ROTL:
        case ROTR:
            return 2;
        case ZER:
            return 3;
        case EQ:
        case GRT:
        case LRT:
        case GET:
        case LET:
            return 4;
        default:
            return 0;
    }
}

void GPNode::print()
{
    if (type == VAL) {
        cout << val;
        return;
    }

    if (type == VAR) {
        cout << "$" << val;
        return;
    }

    if (type != FUN) {
        cout << "???";
        return;
    }

    switch(fun) {
        case ON:
            cout << "ON";
            return;
        case OFF:
            cout << "OFF";
            return;
        case NOT:
            cout << "NOT";
            return;
        case BNOT:
            cout << "BNOT";
            return;
        case AND:
            cout << "AND";
            return;
        case OR:
            cout << "OR";
            return;
        case XOR:
            cout << "XOR";
            return;
        case SUM:
            cout << "+";
            return;
        case SUB:
            cout << "-";
            return;
        case MUL:
            cout << "*";
            return;
        case DIV:
            cout << "/";
            return;
        case ZER:
            cout << "ZER";
            return;
        case EQ:
            cout << "==";
            return;
        case GRT:
            cout << ">";
            return;
        case LRT:
            cout << "<";
            return;
        case GET:
            cout << ">=";
            return;
        case LET:
            cout << "<=";
            return;
        case SHL:
            cout << "<<";
            return;
        case SHR:
            cout << ">>";
            return;
        case ROTL:
            cout << "ROTL";
            return;
        case ROTR:
            cout << "ROTR";
            return;
        default:
            cout << "F??";
            return;
    }
}

