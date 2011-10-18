/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#define GPFUN_COUNT 10


typedef double gpval;

typedef enum gpnode_type_e {FUN, VAR, VAL} gpnode_type;

typedef enum gpnode_fun_e {SUM=0, SUB=1, MUL=2, DIV=3, EQ=4, GRT=5, LRT=6, GET=7, LET=8, ZER=9} gpnode_fun;


typedef struct gpnode_s
{
    gpnode_type type;
    gpval val;
    unsigned int var;
    gpnode_fun fun;
    unsigned int arity;
    struct gpnode_s* params[4];
    gpval vals[4];
    struct gpnode_s* parent;

    int curpos;
    unsigned int stoppos;
    int condpos;

    
} gpnode;


gpnode* create_gpnode(gpnode_type nodetype,
                        gpnode_fun fun,
                        gpval val,
                        unsigned int var,
                        gpnode* parent);

void destroy_gpnode(gpnode* node);

unsigned int fun_arity(gpnode_fun fun);

int fun_condpos(gpnode_fun fun);

void print_gpnode(gpnode* node);

