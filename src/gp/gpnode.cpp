/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include <stdlib.h>
#include <stdio.h>
#include "gpnode.h"


gpnode* create_gpnode(gpnode_type nodetype,
                        gpnode_fun fun,
                        gpval val,
                        unsigned int var,
                        gpnode* parent)
{
    gpnode* node = (gpnode *)malloc(sizeof(gpnode));
    node->type = nodetype;
    node->parent = parent;

    if (nodetype == FUN) {
        node->fun = fun;
        node->arity = fun_arity(fun);
        node->condpos = fun_condpos(fun);
    }
    else if (nodetype == VAR) {
        node->var = var;
        node->arity = 0;
        node->condpos = -1;
    }
    else {
        node->val = val;
        node->arity = 0;
        node->condpos = -1;
    }

    node->stoppos = node->arity;

    return node;
}


void destroy_gpnode(gpnode* node)
{
    free(node);
}


int fun_condpos(gpnode_fun fun)
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


unsigned int fun_arity(gpnode_fun fun)
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


void print_gpnode(gpnode* node)
{
    if (node->type == VAL) {
        printf("%f", node->val);
        return;
    }

    if (node->type == VAR) {
        printf("$%d", node->var);
        return;
    }

    if (node->type != FUN) {
        printf("???");
        return;
    }

    switch(node->fun) {
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
        default:
            printf("F??");
            return;
    }
}

