/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include <string>
#include "gpnode.h"


using std::string;


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

    string to_string();
    GPTree* clone();
    unsigned int size();
    GPNode* gpnode_by_pos(unsigned int pos);
    GPTree* recombine(GPTree* parent2);

    void parse(string prog);

    void dyn_pruning();

    double* vars;

private:
    GPNode* root;
    unsigned int varcount;

    int parse_pos;

    string to_string2(GPNode* node, unsigned int indent);
    GPNode* clone2(GPNode* node, GPNode* parent);
    unsigned int size2(GPNode* node);
    GPNode* gpnode_by_pos2(GPNode* node,
                            unsigned int pos,
                            unsigned int *curpos);
    int token_end(string prog, int pos);
    int token_start(string prog);
    GPNode* parse2(string prog, GPNode* parent);

    void move_up(GPNode* orig_node, GPNode* targ_node);
    void dyn_pruning2(GPNode* node);
};

}
