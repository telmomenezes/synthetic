/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "gpnode.h"


namespace syn
{

class GPTree
{
public:
    GPTree(unsigned int varcount);
    virtual ~GPTree();

    void r_destroy_gpnode(GPNode* node);

    void init_random(float prob_term,
                        unsigned int max_depth_low_limit,
                        unsigned int max_depth_high_limit);
    GPNode* init_random2(float prob_term,
                            GPNode* parent,
                            unsigned int max_depth,
                            unsigned int grow,
                            unsigned int depth);

    gpval eval();

    void print();
    GPTree* clone();
    unsigned int size();
    GPNode* gpnode_by_pos(unsigned int pos);
    GPTree* recombine(GPTree* parent2);

    double* vars;

private:
    GPNode* root;
    unsigned int varcount;

    void print2(GPNode* node, unsigned int indent);
    GPNode* clone2(GPNode* node, GPNode* parent);
    unsigned int size2(GPNode* node);
    GPNode* gpnode_by_pos2(GPNode* node,
                            unsigned int pos,
                            unsigned int *curpos);
};

}
