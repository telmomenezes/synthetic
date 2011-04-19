/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "node.h"
#include "utils.h"
#include <math.h>


syn_node *syn_create_node(unsigned int type, unsigned int id)
{
    syn_node *node = (syn_node *)malloc(sizeof(syn_node));
    node->type = type;
    node->id = id;
    node->visiting = NULL;
    node->in_degree = 0;
    node->out_degree = 0;
    node->marked = 0;
    node->last_walk_id = 0;
    node->targets = NULL;
    node->origins = NULL;
    
    return node;
}


void syn_destroy_node(syn_node *node)
{
    free(node);
}


int syn_add_edge(syn_node *origin, syn_node *target)
{
    if (origin == target)
        return 0;
        
    if (syn_edge_exists(origin, target))
        return 0;

    syn_edge *edge = syn_edge_create();
    edge->orig = origin;
    edge->targ = target;
    edge->next_orig = origin->targets;
    origin->targets = edge;
    origin->out_degree++;
    edge->next_targ = target->origins;
    target->origins = edge;
    target->in_degree++;

    return 1;
}


int syn_edge_exists(syn_node *origin, syn_node *target)
{
    syn_edge *edge = origin->targets;
    
    while (edge) {
        if (edge->targ == target) {
            return 1;
        }
        edge = edge->next_orig;
    }

    return 0;
}


syn_node *syn_get_random_target(syn_node *origin)
{
    if (origin->out_degree == 0) {
        return NULL;
    }

    unsigned int index = RANDOM_UINT(origin->out_degree);

    syn_edge *edge = origin->targets;
    unsigned int i;
    for (i = 0; i < index; i++) {
        edge = edge->next_orig;
    }
    
    return edge->targ;
}
