/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "gpnode.h"


typedef struct gptree_s {
    gpnode* root;
    double* vars;
    unsigned int varcount;
} gptree;


gptree* create_gptree(unsigned int varcount);

void destroy_gptree(gptree* tree);

void r_destroy_gpnode(gpnode* node);

gptree* create_random_gptree(unsigned int varcount,
                                float prob_term,
                                unsigned int max_depth_low_limit,
                                unsigned int max_depth_high_limit);

gpnode* create_random_gptree2(unsigned int varcount,
                                float prob_term,
                                gpnode* parent,
                                unsigned int max_depth,
                                unsigned int grow,
                                unsigned int depth);

gpval eval_gptree(gptree* tree);


void print_gptree(gptree* tree);

void print_gptree2(gpnode* node, unsigned int indent);

gptree* clone_gptree(gptree* tree);

gpnode* clone_gpnode(gpnode* node, gpnode* parent);

unsigned int gptree_size();

unsigned int gptree_size2(gpnode* node);

gpnode* gpnode_by_pos(gptree* tree, unsigned int pos);

gpnode* gpnode_by_pos2(gpnode* node,
                        unsigned int pos,
                        unsigned int *curpos);

gptree* recombine_gptrees(gptree* parent1, gptree* parent2);

