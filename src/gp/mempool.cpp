/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "mempool.h"
#include <stdlib.h>


MemPool::MemPool()
{
    first_node = NULL;
    first_link = NULL;
}


MemPool::~MemPool()
{
    Node* curnode;
    Node* nextnode;
    Link* curlink;
    Link* nextlink;
    
    curnode = first_node;
    while (curnode) {
        nextnode = curnode->next;
        delete(curnode);
        curnode = nextnode;
    }

    curlink = first_link;
    while (curlink) {
        nextlink = curlink->next_free;
        delete(curlink);
        curlink = nextlink;
    }
}


Node* MemPool::get_node(unsigned int reservoir_size)
{
    Node* node;
    if (first_node) {
        node = first_node;
        first_node = node->next;
        node->init(reservoir_size);
        return node;
    }
    else
        return new Node(reservoir_size);
}


void MemPool::return_node(Node* node)
{
    node->next = first_node;
    first_node = node;
}


Link* MemPool::get_link(unsigned int reservoir_size)
{
    Link* link;
    if (first_link) {
        link = first_link;
        first_link = link->next_free;
        link->init(reservoir_size);
        return link;
    }
    else {
        return new Link(reservoir_size);
    }
}


void MemPool::return_link(Link* link)
{
    link->next_free = first_link;
    first_link = link;
}

