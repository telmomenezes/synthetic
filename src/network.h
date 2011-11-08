/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "node.h"
#include "drmap.h"

namespace syn
{

class Net 
{
public:
    Net();
    virtual ~Net();

    syn_node* add_node(unsigned int type);
    syn_node* add_node_with_id(unsigned int nid, unsigned int type);
    int add_edge_to_net(syn_node* orig, syn_node* targ, unsigned long timestamp);

    syn_node* get_random_node();

    void compute_pageranks();
    void write_pageranks(const char *file_path);
    DRMap* get_drmap(unsigned int bin_number);
    DRMap* get_drmap_with_limits(unsigned int bin_number, double min_val_hor, double max_val_hor, double min_val_ver, double max_val_ver);

    void print_net_info();

    unsigned int get_node_count(){return node_count;}
    unsigned int get_edge_count(){return edge_count;}
    unsigned int get_temporal(){return temporal;}
    unsigned long get_min_ts(){return min_ts;}
    unsigned long get_max_ts(){return max_ts;}
    syn_node* get_nodes() {return nodes;}

private:
    double min_pr_in;
    double min_pr_out;
    double max_pr_in;
    double max_pr_out;
    
    syn_node* nodes;

    unsigned int node_count;
    unsigned int edge_count;

    unsigned int temporal;
    unsigned long min_ts;
    unsigned long max_ts;

    DRMap* last_map;
};

}
