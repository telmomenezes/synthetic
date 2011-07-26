/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "network.h"
#include "debug.h"
#include "probs.h"
#include <stdlib.h>
#include <stdio.h>


Net::Net(unsigned int node_link_chem_size_p,
            unsigned int node_chem_size_p,
            unsigned int node_state_size_p,
            unsigned int link_chem_size_p,
            unsigned int link_state_size_p,
            unsigned int interface_size_p,
            MemPool* mempool,
            GPMemPool* gpmempool)
{
    
    first_node = NULL;
    first_inter_node = NULL;

    node_reservoir_size = node_link_chem_size_p + node_chem_size_p + node_state_size_p;
    node_link_chem_size = node_link_chem_size_p;
    node_chem_size = node_chem_size_p;
    node_state_size = node_state_size_p;
    link_reservoir_size = link_chem_size_p + link_state_size_p;
    link_chem_size = link_chem_size_p;
    link_state_size = link_state_size_p;

    cycle = 0;

    _mempool = mempool;
    _gpmempool = gpmempool;

    unsigned int i = 0;
    LINK_CHEM = i; i += link_chem_size;
    LINK_FREE_CHEM = i; i += link_chem_size;
    LINK_NODE_CHEM_IN = i; i += node_link_chem_size;
    LINK_NODE_CHEM_OUT = i; i += node_link_chem_size;
    NODE_CHEM = i; i += node_chem_size;
    NEW_NODE_IN = i; i++;
    NEW_NODE_OUT = i; i++;
    NODE_BIRTH = i; i += node_link_chem_size + node_chem_size;
    SPAWN_IN_LINK = i; i++;
    SPAWN_OUT_LINK = i; i++;
    KILL_NODE = i; i++;
    SPLIT_LINK = i; i++;
    KILL_LINK = i; i++;
    ATTACH_LINK = i; i++;
    EXPLORE_IN = i; i++;
    EXPLORE_OUT = i; i++;
    EXPLORE_GIVEUP = i; i++;

    /* init progs */
    progcount = i;
    progs = (GPTree**)malloc(sizeof(GPTree*) * progcount);
    for (i = 0; i < progcount; i++)
        progs[i] = NULL;

    fitness = 0.0;
    node_count = 0;
    link_count = 0;
    killed = 0;

    /* default values */
    max_nodes = 25;

    /* connectivity checking */
    conn_iter = 0;
    conn_changed = 0;

    /* create interface */
    interface_size = interface_size_p;
    connected_interface = 0;
    interface_nodes = (Node**)malloc(sizeof(Node*) * interface_size);
    for (i = 0; i < interface_size; i++)
        interface_nodes[i] = new Node(node_reservoir_size);

    /* create seed node */
    Node* node = mempool->get_node(node_reservoir_size);
    for (i = 0; i < node_reservoir_size; i++)
        node->reservoir[i] = 0;
    seed_node = node;
    add_node(node);
    node->fixed = 1;

    after_net_create();
}


Net::~Net()
{
    before_net_destroy();

    Node* curnode;
    Node* nextnode;
    
    curnode = first_node;
    while (curnode) {
        nextnode = curnode->next;
        remove_node(curnode, 1);
        curnode = nextnode;
    }
    first_node = NULL;

    for (unsigned int i = 0; i < progcount; i++)
        delete(progs[i]);
    free(progs);
    for (unsigned int i = 0; i < interface_size; i++)
        delete(interface_nodes[i]);
    free(interface_nodes);
}


Net* Net::create(unsigned int node_link_chem_size,
                        unsigned int node_chem_size,
                        unsigned int node_state_size,
                        unsigned int link_chem_size,
                        unsigned int link_state_size,
                        unsigned int interface_size,
                        MemPool* mempool,
                        GPMemPool* gpmempool)
{
    return new Net(node_link_chem_size,
                        node_chem_size,
                        node_state_size,
                        link_chem_size,
                        link_state_size,
                        interface_size,
                        mempool,
                        gpmempool);
}


void Net::init_progs_random()
{
    float term_prob = 0.4;
    unsigned int mdl = 1;
    unsigned int mdh = 5;

    for (unsigned int i = 0; i < progcount; i++)
        progs[i] = GPTree::create_random(
            (node_reservoir_size * 2) + link_reservoir_size,
            term_prob, mdl, mdh, _gpmempool);
}


Net* Net::recombine_progs(Net* parent2)
{
    Net* net = create(node_link_chem_size,
                        node_chem_size,
                        node_state_size,
                        link_chem_size,
                        link_state_size,
                        interface_size,
                        _mempool,
                        _gpmempool);

    net->max_nodes = max_nodes;

    unsigned int rprog = random() % progcount;
    for (unsigned int i = 0; i < progcount; i++) {
        if (i == rprog)
            net->progs[i] = progs[i]->recombine(parent2->progs[i]);
        else
            net->progs[i] = progs[i]->clone();
    }

    return net;
}


Net* Net::clone_progs()
{
    Net* net = create(node_link_chem_size,
                        node_chem_size,
                        node_state_size,
                        link_chem_size,
                        link_state_size,
                        interface_size,
                        _mempool,
                        _gpmempool);

    net->max_nodes = max_nodes;

    for (unsigned int i = 0; i < progcount; i++)
        net->progs[i] = progs[i]->clone();

    return net;
}


void Net::runfor(unsigned int cycles)
{
    for (unsigned int i = 0; i < cycles; i++) {
        runcycle();
        after_net_cycle();
        if (killed)
            return;
    }
}


void Net::runcycle()
{
    Node* curnode;
    Node* nextnode;
    Node* nodeorig;
    Node* nodetarg;
    Node* newnode;
    Node* bestnode;
    Node* explorenode;
    Link* curlink;
    Link* nextlink;
    Link* newlink;
    Link* explorelink;
    unsigned int i;
    unsigned int j;
    unsigned int flag;
    unsigned int first;
    synword result;
    synword bestresult;
    synword evalres;

    /* connectivity checking */
    if (conn_changed) {
        /* remove unconnected nodes */
        curnode = first_node;
        while (curnode) {
            nextnode = curnode->next;
            curnode->connected = 0;
            if (curnode->conn_iter != conn_iter) {
                remove_node(curnode, 0);
            }
            else {
                curnode->connected = 1;
            }
            curnode = nextnode;
        }
        conn_iter++;
        first_node->conn_iter = conn_iter;
    }
    conn_changed = 0;

    /* update nodes with interface states */
    for (i = 0; i < interface_size; i++) {
        if (interface_nodes[i]->interface) {
            nodeorig = interface_nodes[i];
            nodetarg = interface_nodes[i]->interface;
            for (j = 1; j <= node_state_size; j++) {
                nodetarg->reservoir[node_reservoir_size - j]
                    = nodeorig->reservoir[node_reservoir_size - j];
            }
        }
    }

    /* run link and link-node chem programs, also test connectivity */
    curnode = first_node;
    while (curnode) {
        curlink = curnode->first_in;
        while (curlink) {
            nodeorig = curlink->orig;
            nodetarg = curlink->targ;

            /* use this cycle to propagate connectivity */
            if (nodeorig->conn_iter != nodetarg->conn_iter) {
                if (nodeorig->conn_iter == conn_iter) {
                    nodetarg->conn_iter = conn_iter;
                    conn_changed = 1;
                }
                else if (nodetarg->conn_iter == conn_iter) {
                    nodeorig->conn_iter = conn_iter;
                    conn_changed = 1;
                }
            }

            /* link chem */
            for (j = 0; j < link_chem_size; j++) {
                for (i = 0; i < node_reservoir_size; i++)
                    progs[LINK_CHEM + j]->vars[i] = nodeorig->reservoir[i];
                for (i = 0; i < node_reservoir_size; i++)
                    progs[LINK_CHEM + j]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
                for (i = 0; i < link_reservoir_size; i++)
                    progs[LINK_CHEM + j]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                evalres = progs[LINK_CHEM + j]->eval(1);
                if (progs[LINK_CHEM + j]->active)
                    curlink->reservoir[j] = evalres;
            }

            /* link-node in chem */
            for (j = 0; j < node_link_chem_size; j++) {
                for (i = 0; i < node_reservoir_size; i++)
                    progs[LINK_NODE_CHEM_IN + j]->vars[i] = nodeorig->reservoir[i];
                for (i = 0; i < node_reservoir_size; i++)
                    progs[LINK_NODE_CHEM_IN + j]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
                for (i = 0; i < link_reservoir_size; i++)
                    progs[LINK_NODE_CHEM_IN + j]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                evalres = progs[LINK_NODE_CHEM_IN + j]->eval(1);

                if (progs[LINK_NODE_CHEM_IN + j]->active) {
                    nodeorig->reservoir[j] = evalres;
                }
            }

            curlink = curlink->next_in;
        }

        /* link-node out chem */
        curlink = curnode->first_out;
        while (curlink) {
            nodeorig = curlink->orig;
            nodetarg = curlink->targ;

            for (j = 0; j < node_link_chem_size; j++) {
                for (i = 0; i < node_reservoir_size; i++)
                    progs[LINK_NODE_CHEM_OUT + j]->vars[i] = nodeorig->reservoir[i];
                for (i = 0; i < node_reservoir_size; i++)
                    progs[LINK_NODE_CHEM_OUT + j]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
                for (i = 0; i < link_reservoir_size; i++)
                    progs[LINK_NODE_CHEM_OUT + j]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                evalres = progs[LINK_NODE_CHEM_OUT + j]->eval(1);
                if (progs[LINK_NODE_CHEM_OUT + j]->active)
                    nodetarg->reservoir[j] = evalres;
            }

            curlink = curlink->next_out;
        }

        curnode = curnode->next;
    }

    /* run node chem programs */
    curnode = first_node;
    while (curnode) {
        for (j = 0; j < node_chem_size; j++) {
            for (i = 0; i < node_reservoir_size; i++) {
                progs[NODE_CHEM + j]->vars[i] = curnode->reservoir[i];
                progs[NODE_CHEM + j]->vars[i + node_reservoir_size] = curnode->reservoir[i];
            }
            evalres = progs[NODE_CHEM + j]->eval(1);
            if (progs[NODE_CHEM + j]->active)
                curnode->reservoir[node_link_chem_size + j] = evalres;
        }
        curnode = curnode->next;
    }

    /* run node morph programs */
    curnode = first_node;
    while (curnode) {
        for (i = 0; i < node_reservoir_size; i++) {
            progs[NEW_NODE_IN]->vars[i] = curnode->reservoir[i];
            progs[NEW_NODE_IN]->vars[i + node_reservoir_size] = curnode->reservoir[i];
            progs[NEW_NODE_OUT]->vars[i] = curnode->reservoir[i];
            progs[NEW_NODE_OUT]->vars[i + node_reservoir_size] = curnode->reservoir[i];
            progs[SPAWN_IN_LINK]->vars[i] = curnode->reservoir[i];
            progs[SPAWN_IN_LINK]->vars[i + node_reservoir_size] = curnode->reservoir[i];
            progs[SPAWN_OUT_LINK]->vars[i] = curnode->reservoir[i];
            progs[SPAWN_OUT_LINK]->vars[i + node_reservoir_size] = curnode->reservoir[i];
            progs[KILL_NODE]->vars[i] = curnode->reservoir[i];
            progs[KILL_NODE]->vars[i + node_reservoir_size] = curnode->reservoir[i];
        }

        nextnode = curnode->next;

        /* new node, out link */
        progs[NEW_NODE_OUT]->eval(0);
        if (progs[NEW_NODE_OUT]->active) {
            if (can_create_node()) {
                DMSG("new node, out link")
                newnode = clone_node(curnode, node_reservoir_size, _mempool);
                newnode->conn_iter = conn_iter;
                add_node(newnode);
                newlink = _mempool->get_link(link_reservoir_size);
                newlink->orig = curnode;
                newlink->targ = newnode;
                newlink->state = ATTACHED;
                add_link(newlink);
            
                /* node birth */
                for (j = 0; j < (node_link_chem_size + node_chem_size); j++) {
                    for (i = 0; i < node_reservoir_size; i++) {
                        progs[NODE_BIRTH + j]->vars[i] = curnode->reservoir[i];
                        progs[NODE_BIRTH + j]->vars[i + node_reservoir_size] = curnode->reservoir[i];
                    }
                    evalres = progs[NODE_BIRTH + j]->eval(1);
                    newnode->reservoir[j] = evalres;
                }
            }
        }

        /* new node, in link */
        progs[NEW_NODE_IN]->eval(0);
        if (progs[NEW_NODE_IN]->active) {
            if (can_create_node()) {
                DMSG("new node, in link")
                newnode = clone_node(curnode, node_reservoir_size, _mempool);
                newnode->conn_iter = conn_iter;
                add_node(newnode);
                newlink = _mempool->get_link(link_reservoir_size);
                newlink->orig = newnode;
                newlink->targ = curnode;
                newlink->state = ATTACHED;
                add_link(newlink);

                /* node birth */
                for (j = 0; j < (node_link_chem_size + node_chem_size); j++) {
                    for (i = 0; i < node_reservoir_size; i++) {
                        progs[NODE_BIRTH + j]->vars[i] = curnode->reservoir[i];
                        progs[NODE_BIRTH + j]->vars[i + node_reservoir_size] = curnode->reservoir[i];
                    }
                    evalres = progs[NODE_BIRTH + j]->eval(1);
                    newnode->reservoir[j] = evalres;
                }
            }
        }

        /* check for node limit */
        if (node_count > max_nodes) {
            killed = 1;
            return;
        }
            
        /* spawn in link */
        if (!curnode->spawning)
        {
            progs[SPAWN_IN_LINK]->eval(0);
            if (progs[SPAWN_IN_LINK]->active) {
                DMSG("spawn in link")
                newlink = _mempool->get_link(link_reservoir_size);
                newlink->orig = curnode;
                newlink->targ = curnode;
                curnode->spawning = newlink;
                newlink->state = ORIG_FREE;
                newlink->prev_free = NULL;
                newlink->next_free = curnode->first_free_link;
                if (curnode->first_free_link)
                    curnode->first_free_link->prev_free = newlink;
                curnode->first_free_link = newlink;
            }
        }

        /* spawn out link */
        if (!curnode->spawning)
        {
            progs[SPAWN_OUT_LINK]->eval(0);
            if (progs[SPAWN_OUT_LINK]->active) {
                DMSG("spawn out link")
                newlink = _mempool->get_link(link_reservoir_size);
                newlink->orig = curnode;
                newlink->targ = curnode;
                curnode->spawning = newlink;
                newlink->state = TARG_FREE;
                newlink->prev_free = NULL;
                newlink->next_free = curnode->first_free_link;
                if (curnode->first_free_link)
                    curnode->first_free_link->prev_free = newlink;
                curnode->first_free_link = newlink;
            }
        }

        /* kill node */
        progs[KILL_NODE]->eval(0);
        if (progs[KILL_NODE]->active) {
            DMSG("kill node")
            remove_node(curnode, 0);
        }

        curnode = nextnode;
    }

    /* run link morph programs */
    curnode = first_node;
    while (curnode) {
        curlink = curnode->first_in;
        while (curlink) {
            nodeorig = curlink->orig;
            nodetarg = curlink->targ;
            for (i = 0; i < node_reservoir_size; i++) {
                progs[KILL_LINK]->vars[i] = nodeorig->reservoir[i];
                progs[SPLIT_LINK]->vars[i] = nodeorig->reservoir[i];
            }
            for (i = 0; i < node_reservoir_size; i++) {
                progs[KILL_LINK]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
                progs[SPLIT_LINK]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
            }
            for (i = 0; i < link_reservoir_size; i++) {
                progs[KILL_LINK]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                progs[SPLIT_LINK]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
            }

            nextlink = curlink->next_in;

            /* kill link */
            progs[KILL_LINK]->eval(0);
            if (progs[KILL_LINK]->active) {
                DMSG("kill link")
                remove_link(curlink);
            }

            /* split link */
            progs[SPLIT_LINK]->eval(0);
            if (progs[SPLIT_LINK]->active) {
                if (can_create_node()) {
                    DMSG("split link")
                    newnode = clone_node(curlink->orig, node_reservoir_size, _mempool);
                    newnode->conn_iter = conn_iter;
                    add_node(newnode);

                    /* create new links */
                    newlink = _mempool->get_link(link_reservoir_size);
                    newlink->orig = curlink->orig;
                    newlink->targ = newnode;
                    newlink->state = ATTACHED;
                    add_link(newlink);
                    newlink = _mempool->get_link(link_reservoir_size);
                    newlink->orig = newnode;
                    newlink->targ = curlink->targ;
                    newlink->state = ATTACHED;
                    add_link(newlink);
            
                    /* node birth */
                    for (j = 0; j < (node_link_chem_size + node_chem_size); j++) {
                        for (i = 0; i < node_reservoir_size; i++) {
                            progs[NODE_BIRTH + j]->vars[i] = curlink->orig->reservoir[i];
                            progs[NODE_BIRTH + j]->vars[i + node_reservoir_size] = curlink->targ->reservoir[i];
                        }
                        for (i = 0; i < link_reservoir_size; i++)
                            progs[NODE_BIRTH + j]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                        evalres = progs[NODE_BIRTH + j]->eval(1);
                        newnode->reservoir[j] = evalres;
                    }

                    /* remove original link */
                    remove_link(curlink);
                }
            }

            curlink = nextlink;
        }
        curnode = curnode->next;
    }

    /* process free links */
    curnode = first_node;
    while (curnode) {
        nextnode = curnode->next;

        curlink = curnode->spawning;
        if (!curlink) {
            curnode = nextnode;
            continue;
        }

        flag = 0;

        /* link free chem */
        for (j = 0; j < link_chem_size; j++) {
            for (i = 0; i < node_reservoir_size; i++)
                progs[LINK_FREE_CHEM + j]->vars[i] = nodeorig->reservoir[i];
            for (i = 0; i < node_reservoir_size; i++)
                progs[LINK_FREE_CHEM + j]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
            for (i = 0; i < link_reservoir_size; i++)
                progs[LINK_FREE_CHEM + j]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
            evalres = progs[LINK_FREE_CHEM + j]->eval(1);
            if (progs[LINK_FREE_CHEM + j]->active)
                curlink->reservoir[j] = evalres;
        }

        /* try to attach */
        if ((curlink->orig != curlink->targ) && (!link_exists(curlink->orig, curlink->targ))) {
        //if (!link_exists(curlink->orig, curlink->targ)) {
            nodeorig = curlink->orig;
            nodetarg = curlink->targ;
            for (i = 0; i < node_reservoir_size; i++)
                progs[ATTACH_LINK]->vars[i] = nodeorig->reservoir[i];
            for (i = 0; i < node_reservoir_size; i++)
                progs[ATTACH_LINK]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
            for (i = 0; i < link_reservoir_size; i++)
                progs[ATTACH_LINK]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
            
            progs[ATTACH_LINK]->eval(0);

            /* attach */
            if (progs[ATTACH_LINK]->active) {
                DMSG("attach link")
                flag = 1;
                /* add to network */
                add_link(curlink);
            }
        }

        /* give up exploring? */
        if (!flag) {
            nodeorig = curlink->orig;
            nodetarg = curlink->targ;
            for (i = 0; i < node_reservoir_size; i++)
                progs[EXPLORE_GIVEUP]->vars[i] = nodeorig->reservoir[i];
            for (i = 0; i < node_reservoir_size; i++)
                progs[EXPLORE_GIVEUP]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
            for (i = 0; i < link_reservoir_size; i++)
                progs[EXPLORE_GIVEUP]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
            
            progs[EXPLORE_GIVEUP]->eval(0);

            if (progs[EXPLORE_GIVEUP]->active) {
                DMSG("give up exploring")
                flag = 1;
                /* remove free link */
                remove_link(curlink);
            }
        }

        /* explore */
        if (!flag) {
            if (curlink->state == ORIG_FREE)
                explorenode = curlink->orig;
            else
                explorenode = curlink->targ;

            bestnode = explorenode;
          
            first = 1;
            /* explore out-neighbourhood */
            explorelink = explorenode->first_out;
            while (explorelink) {
                if (curlink->state == ORIG_FREE) {
                    nodeorig = explorelink->targ;
                    nodetarg = curlink->targ;
                }
                else {
                    nodeorig = curlink->orig;
                    nodetarg = explorelink->targ;
                }
                for (i = 0; i < node_reservoir_size; i++)
                    progs[EXPLORE_OUT]->vars[i] = nodeorig->reservoir[i];
                for (i = 0; i < node_reservoir_size; i++)
                    progs[EXPLORE_OUT]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
                for (i = 0; i < link_reservoir_size; i++)
                    progs[EXPLORE_OUT]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                result = progs[EXPLORE_OUT]->eval(0);
                if ((progs[EXPLORE_OUT]->active) && (first || (result > bestresult))) {
                    first = 0;
                    bestresult = result;
                    bestnode = explorelink->targ;
                }

                explorelink = explorelink->next_out;
            }
            /* explore in-neighbourhood */
            explorelink = explorenode->first_in;
            while (explorelink) {
                if (curlink->state == ORIG_FREE) {
                    nodeorig = explorelink->orig;
                    nodetarg = curlink->targ;
                }
                else {
                    nodeorig = curlink->orig;
                    nodetarg = explorelink->orig;
                }
                for (i = 0; i < node_reservoir_size; i++)
                    progs[EXPLORE_IN]->vars[i] = nodeorig->reservoir[i];
                for (i = 0; i < node_reservoir_size; i++)
                    progs[EXPLORE_IN]->vars[i + node_reservoir_size] = nodetarg->reservoir[i];
                for (i = 0; i < link_reservoir_size; i++)
                    progs[EXPLORE_IN]->vars[i + (node_reservoir_size * 2)] = curlink->reservoir[i];
                result = progs[EXPLORE_IN]->eval(0);
                if ((progs[EXPLORE_IN]->active) && (first || (result > bestresult))) {
                    first = 0;
                    bestresult = result;
                    bestnode = explorelink->orig;
                }

                explorelink = explorelink->next_in;
            }

            /* move to next node or give up */
            if (first) {
                DMSG("give up exploring")
                /* remove free link */
                remove_link(curlink);
            }
            else {
                remove_link_from_free_list(curlink, 0);
                curlink->prev_free = NULL;
                curlink->next_free = bestnode->first_free_link;
                if (bestnode->first_free_link)
                    bestnode->first_free_link->prev_free = curlink;
                bestnode->first_free_link = curlink;
                if (curlink->state == ORIG_FREE)
                    curlink->orig = bestnode;
                else
                    curlink->targ = bestnode;
            }
        }

        curnode = nextnode;
    }

    /* update interface with node states */
    for (i = 0; i < interface_size; i++) {
        if (interface_nodes[i]->interface) {
            nodetarg = interface_nodes[i];
            nodeorig = interface_nodes[i]->interface;
            for (j = 0; j < (node_reservoir_size - node_state_size); j++)
                nodetarg->reservoir[j] = nodeorig->reservoir[j];
        }
    }

    cycle++;
}


void Net::match_interface()
{
    /* find first non-interface node in network */
    Node* netnode = seed_node;
    while (netnode && netnode->interface)
        netnode = netnode->prev;
    if (!netnode)
        return;

    /* find first free interface slot */
    unsigned int i = 0;
    Node* internode = interface_nodes[i];
    while (internode->interface) {
        i++;
        internode = interface_nodes[i];
    }

    /* connect network and interface nodes */
    netnode->interface = internode;
    internode->interface = netnode;
    
    connected_interface++;
}


void Net::add_node(Node* node)
{
    node->next = first_node;
    node->prev = NULL;
    if (first_node)
        first_node->prev = node;
    first_node = node;
    node_count++;

    if (interface_size > connected_interface) {
        match_interface();
    }
}


Node* Net::clone_node(Node* node,
                        unsigned int reservoir_size,
                        MemPool* mempool)
{
    Node* newnode = mempool->get_node(reservoir_size);
    for (unsigned int i = 0; i < reservoir_size; i++) {
        newnode->reservoir[i] = node->reservoir[i];
    }

    return newnode;
}


void Net::remove_node(Node* node, unsigned int force)
{
    if ((node->fixed) && (!force))
        return;

    /* update interface if necessary */
    if (node->interface) {
        node->interface->interface = NULL;
        connected_interface--;
    }

    /* remove associated links */
    Link* curlink = node->first_in;
    Link* nextlink;
    while (curlink) {
        nextlink = curlink->next_in;
        remove_link(curlink);
        curlink = nextlink;
    }
    curlink = node->first_out;
    while (curlink) {
        nextlink = curlink->next_out;
        remove_link(curlink);
        curlink = nextlink;
    }

    /* remove associated free links */
    if (node->spawning)
        remove_link(node->spawning);
    curlink = node->first_free_link;
    while (curlink) {
        nextlink = curlink->next_free;
        remove_link(curlink);
        curlink = nextlink;
    }

    /* remove node */
    if (node->prev)
        node->prev->next = node->next;
    else
        first_node = node->next;
    if (node->next)
        node->next->prev = node->prev;
    _mempool->return_node(node);

    node_count--;

    /* update interface if necessary */
    if (interface_size > connected_interface)
        match_interface();
}


void Net::add_link(Link* link)
{
    /* if free link, remove from free list */
    if (link->state != ATTACHED) {
        remove_link_from_free_list(link, 1);
        link->state = ATTACHED;
    }

    link->next_out = link->orig->first_out;
    link->prev_out = NULL;
    if (link->orig->first_out)
        link->orig->first_out->prev_out = link;
    link->orig->first_out = link;
    link->next_in = link->targ->first_in;
    link->prev_in = NULL;
    if (link->targ->first_in)
        link->targ->first_in->prev_in = link;
    link->targ->first_in = link;
    link_count++;
    link->orig->outlink_count++;
    link->targ->inlink_count++;
}


void Net::remove_link_from_free_list(Link* link, unsigned int unspawn)
{
    Node* curnode;

    if (link->state == ORIG_FREE) {
        curnode = link->orig;
        if (unspawn)
            link->targ->spawning = NULL;
    }
    else {
        curnode = link->targ;
        if (unspawn)
            link->orig->spawning = NULL;
    }
    if (link->prev_free)
        link->prev_free->next_free = link->next_free;
    else
        curnode->first_free_link = link->next_free;
    if (link->next_free)
        link->next_free->prev_free = link->prev_free;
}


void Net::remove_link(Link* link)
{
    if (link->state == ATTACHED) {
        if (link->prev_out)
            link->prev_out->next_out = link->next_out;
        else
            link->orig->first_out = link->next_out;
        if (link->next_out)
            link->next_out->prev_out = link->prev_out;

        if (link->prev_in)
            link->prev_in->next_in = link->next_in;
        else
            link->targ->first_in = link->next_in;
        if (link->next_in)
            link->next_in->prev_in = link->prev_in;

        link->orig->outlink_count--;
        link->targ->inlink_count--;
        link_count--;
    }
    else
        remove_link_from_free_list(link, 1);

    _mempool->return_link(link);
}


unsigned int Net::link_exists(Node* orig, Node* targ)
{
    Link* curlink = orig->first_out;

    while (curlink) {
        if (curlink->targ == targ)
            return 1;
        curlink = curlink->next_out;
    }

    return 0;
}


void Net::write()
{
    Node* curnode;
    Link* curlink;

    FILE* f = fopen("bestnet.net", "w");

    /* write edges */
    curnode = first_node;
    while (curnode) {
        fprintf(f, "node %lud %d %d\n", (unsigned long)curnode, curnode->fixed, curnode->interface);
        curlink = curnode->first_out;
        while (curlink) {
            fprintf(f, "edge %lud %lud\n", (unsigned long)curlink->orig, (unsigned long)curlink->targ);
            curlink = curlink->next_out;
        }
        curnode = curnode->next;
    }
    fclose(f);
}


unsigned int Net::genotype_size()
{
    unsigned int gsize = 0;

    for (unsigned int i = 0; i < progcount; i++)
        gsize += progs[i]->size();

    return gsize;
}


void Net::print()
{
    Node* curnode = first_node;
    while(curnode) {
        curnode->print(node_reservoir_size);
        curnode = curnode->next;
    }
}


int Net::can_create_node()
{
    return (max_nodes > node_count);
}

