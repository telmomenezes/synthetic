/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "network.h"


typedef struct syn_gen_s {
    unsigned int types_count;
    double *m_link;
    double *m_random;
    double *m_follow;
    double *m_rfollow;
    double *m_weight;
    double *m_stop;
    double fitness;

    unsigned int r_edges;
    unsigned int l_edges;
    unsigned int total_edges;
    unsigned int cycles;
} syn_gen;


syn_gen *syn_create_generator(unsigned int types_count);
void syn_destroy_generator(syn_gen *gen);

syn_gen *syn_clone_generator(syn_gen *gen);

void syn_generate_nodes(syn_gen *gen, syn_net *net, unsigned int node_count);

syn_node *syn_get_random_target(syn_gen *gen, syn_node *origin);

syn_net *syn_generate_network(syn_gen *gen, unsigned int node_count, unsigned int edge_count,
                                unsigned int max_cycles, unsigned int max_walk_length);

/*
void initRandom();
void mutate();
Generator* recombine(Generator* parent2);
*/

