/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "gpnode.h"
#include "gpmempool.h"


class GPTree {
public:
    double* vars;
    unsigned int active;

    GPTree(unsigned int varcount, GPMemPool* mempool);
    virtual ~GPTree();

    static GPTree* create_random(unsigned int varcount,
                                  float prob_term,
                                  unsigned int max_depth_low_limit,
                                  unsigned int max_depth_high_limit,
                                  GPMemPool* mempool);

    gpval eval(unsigned int active_p);

    void destroy_gpnode(GPNode* node);

    void print();

    GPNode* clone_gpnode(GPNode* node, GPNode* parent);

    GPTree* clone();

    unsigned int size();

    GPNode* gpnode_by_pos(unsigned int pos);

    GPTree* recombine(GPTree* parent2);

private:
    GPNode* _root;
    unsigned int _varcount;
    GPMemPool* _mempool;
    
    static GPNode* create_random2(unsigned int varcount,
                            float prob_term,
                            GPNode *parent,
                            unsigned int max_depth,
                            unsigned int grow,
                            unsigned int depth,
                            GPMemPool* mempool);

    unsigned int size2(GPNode* node);

    void print2(GPNode* node, unsigned int indent);

    GPNode* gpnode_by_pos2(GPNode* node,
                            unsigned int pos,
                            unsigned int *curpos);
};

