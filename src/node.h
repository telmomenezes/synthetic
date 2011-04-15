/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

typedef struct {
    unsigned int type;
    unsigned int id;
    vector<Node*> targets;
    vector<Node*> origins;
    unsigned int in_degree;
    unsigned int out_degree;
    Node* visiting;
    int marked;
    unsigned long last_walk_id;
    
    // eigenvector centrality
    double evc_in;
    double evc_in_last;
    double evc_out;
    double evc_out_last;
} syn_node;


struct syn_node *syn_create_node(unsigned int type, unsigned int id);
void syn_destroy_node(struct syn_node *node);

int syn_add_edge(struct syn_node *origin, struct syn_node *target);
int syn_edge_exists(struct syn_node *target);

struct syn_node *syn_get_random_target(struct syn_node *origin);
unsigned int syn_walk(unsigned long walk_id);

