/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "link.h"
#include "types.h"


class Node {
public:
    synword *reservoir;

    Node* next;
    Node* prev;

    Node* interface;

    Link* first_in;
    Link* first_out;

    Link* spawning;
    Link* first_free_link;

    unsigned int inlink_count;
    unsigned int outlink_count;

    unsigned int fixed;

    unsigned int connected;

    /* to test connectivity */
    unsigned long conn_iter;


    Node(unsigned int reservoir_size);

    virtual ~Node();

    void init(unsigned int reservoir_size);

    void print(unsigned int reservoir_size);
};

