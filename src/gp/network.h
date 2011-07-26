/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "node.h"
#include "link.h"
#include "gptree.h"
#include "mempool.h"
#include "gpmempool.h"


class Net
{
public:
    Node* first_node;
    Node* first_inter_node;
    Node* seed_node;

    unsigned int node_reservoir_size;
    unsigned int node_link_chem_size;
    unsigned int node_chem_size;
    unsigned int node_state_size;
    unsigned int link_reservoir_size;
    unsigned int link_chem_size;
    unsigned int link_state_size;
    unsigned int progcount;

    GPTree** progs;

    double fitness;

    unsigned int node_count;
    unsigned int link_count;

    unsigned int interface_size;
    unsigned int connected_interface;
    Node** interface_nodes;

    unsigned int max_nodes;

    unsigned int killed;

    unsigned int cycle;


    /* connectivity checking */
    unsigned long conn_iter;
    unsigned int conn_changed;

    /* prog positions */
    unsigned int LINK_CHEM;
    unsigned int LINK_FREE_CHEM;
    unsigned int LINK_NODE_CHEM_IN;
    unsigned int LINK_NODE_CHEM_OUT;
    unsigned int NODE_CHEM;
    unsigned int NEW_NODE_IN;
    unsigned int NEW_NODE_OUT;
    unsigned int NODE_BIRTH;
    unsigned int SPAWN_IN_LINK;
    unsigned int SPAWN_OUT_LINK;
    unsigned int KILL_NODE;
    unsigned int SPLIT_LINK;
    unsigned int KILL_LINK;
    unsigned int ATTACH_LINK;
    unsigned int EXPLORE_IN;
    unsigned int EXPLORE_OUT;
    unsigned int EXPLORE_GIVEUP;


    Net(unsigned int node_link_chem_size_p,
        unsigned int node_chem_size_p,
        unsigned int node_state_size_p,
        unsigned int link_chem_size_p,
        unsigned int link_state_size_p,
        unsigned int interface_size_p,
        MemPool* mempool,
        GPMemPool* gpmempool_p);

    virtual ~Net();

    virtual Net* create(unsigned int node_link_chem_size,
                        unsigned int node_chem_size,
                        unsigned int node_state_size,
                        unsigned int link_chem_size,
                        unsigned int link_state_size,
                        unsigned int interface_size,
                        MemPool* mempool,
                        GPMemPool* gpmempool);

    void init_progs_random();

    Net* recombine_progs(Net* parent2);

    Net* clone_progs();

    void runfor(unsigned int cycles);

    void runcycle();

    void add_node(Node* node);

    Node* clone_node(Node* node,
                        unsigned int reservoir_size,
                        MemPool* mempool);

    void remove_node(Node* node, unsigned int force);

    void add_link(Link* link);

    void remove_link_from_free_list(Link* link, unsigned int unspawn);

    void remove_link(Link* link);

    unsigned int link_exists(Node* orig, Node* targ);

    void write();

    unsigned int genotype_size();

    void print();

    int can_create_node();

    /* configurable behaviors */
    virtual void after_net_create(){}
    virtual void before_net_destroy(){}
    virtual void after_net_cycle(){}

private:
    void match_interface();

    MemPool* _mempool;
    GPMemPool* _gpmempool;
};

