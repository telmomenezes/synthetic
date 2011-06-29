/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "node.h"
#include "drmap.h"


typedef struct syn_net_s {
    double min_evc_in;
    double min_evc_out;
    double max_evc_in;
    double max_evc_out;
    
    syn_node *nodes;

    unsigned int node_count;
    unsigned int edge_count;

    unsigned int temporal;
    unsigned long min_ts;
    unsigned long max_ts;

    syn_drmap *last_map;
} syn_net;


syn_net *syn_create_net(void);
void syn_destroy_net(syn_net *net);

syn_node *syn_add_node(syn_net *net, unsigned int type);
syn_node *syn_add_node_with_id(syn_net *net, unsigned int nid, unsigned int type);
int syn_add_edge_to_net(syn_net *net, syn_node* orig, syn_node* targ, unsigned long timestamp);

void syn_compute_evc(syn_net *net);
void syn_write_evc(syn_net *net, const char *file_path);
syn_drmap *syn_get_drmap(syn_net *net, unsigned int bin_number);
syn_drmap *syn_get_drmap_with_limits(syn_net *net, unsigned int bin_number, double min_val_hor, double max_val_hor, double min_val_ver, double max_val_ver);

void syn_print_net_info(syn_net *net);
