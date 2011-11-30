/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "node.h"
#include "utils.h"
#include <cmath>
#include <stdio.h>

namespace syn
{

Node::Node(unsigned int type, unsigned int id)
{
    this->type = type;
    this->id = id;
    in_degree = 0;
    out_degree = 0;
    birth = -1;
    targets = NULL;
    origins = NULL;
}


Node::~Node()
{
    // we destroy the outgoing edges of every node
    Edge* edge = targets;
    Edge* next_edge;
    while (edge) {
        next_edge = edge->next_targ;
        delete edge;
        edge = next_edge;
    }
}


int Node::add_edge(Node* target, unsigned long timestamp)
{
    if (this == target) {
        return 0;
    }
        
    if (edge_exists(target)) {
        return 0;
    }

    Edge* edge = new Edge();
    edge->orig = this;
    edge->targ = target;
    edge->timestamp = timestamp;
    edge->next_targ = targets;
    targets = edge;
    out_degree++;
    edge->next_orig = target->origins;
    target->origins = edge;
    target->in_degree++;

    return 1;
}


int Node::edge_exists(Node* target)
{
    Edge* edge = targets;
    
    while (edge) {
        if (edge->targ == target) {
            return 1;
        }
        edge = edge->next_targ;
    }

    return 0;
}

}
