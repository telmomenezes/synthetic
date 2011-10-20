/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "edge.h"

typedef struct syn_node_s {
    unsigned int type;
    unsigned int id;
    syn_edge* targets;
    syn_edge* origins;
    unsigned int in_degree;
    unsigned int out_degree;
    int marked;
    unsigned long last_walk_id;
    
    // node list
    struct syn_node_s* next;
    
    // pageranks
    double pr_in;
    double pr_in_last;
    double pr_out;
    double pr_out_last;

    // for generators
    struct syn_node_s* gentarget;
    double genweight;
} syn_node;


syn_node *syn_create_node(unsigned int type, unsigned int id);
void syn_destroy_node(syn_node *node);

int syn_add_edge(syn_node *origin, syn_node *target, unsigned long timestamp);
int syn_edge_exists(syn_node *origin, syn_node *target);

