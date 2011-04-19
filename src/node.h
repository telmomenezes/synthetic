/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "edge.h"

typedef struct syn_node_s {
    unsigned int type;
    unsigned int id;
    syn_edge *targets;
    syn_edge *origins;
    unsigned int in_degree;
    unsigned int out_degree;
    struct syn_node_s *visiting;
    int marked;
    unsigned long last_walk_id;
    
    // node list
    struct syn_node_s *next;
    
    // eigenvector centrality
    double evc_in;
    double evc_in_last;
    double evc_out;
    double evc_out_last;
} syn_node;


syn_node *syn_create_node(unsigned int type, unsigned int id);
syn_node *syn_destroy_node(syn_node *node);

int syn_add_edge(syn_node *origin, syn_node *target);
int syn_edge_exists(syn_node *origin, syn_node *target);

syn_node *syn_get_random_target(syn_node *origin);
