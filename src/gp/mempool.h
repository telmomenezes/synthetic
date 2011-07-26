/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "node.h"
#include "link.h"


class MemPool {
public:
    MemPool();
    virtual ~MemPool();

    Node* get_node(unsigned int reservoir_size);

    void return_node(Node* node);

    Link* get_link(unsigned int reservoir_size);

    void return_link(Link* link);

private:
    Node* first_node;
    Link* first_link;
};

