/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "node.h" 
#include <strings.h>
#include <iostream>


using std::cout;
using std::endl;
using std::flush;


Node::Node(unsigned int reservoir_size)
{
    reservoir = (synword *)malloc(sizeof(synword) * reservoir_size);
    init(reservoir_size);
}


Node::~Node()
{
    free(reservoir);
}


void Node::init(unsigned int reservoir_size)
{
    bzero(reservoir, sizeof(synword) * reservoir_size);
    first_in = NULL;
    first_out = NULL;
    interface = NULL;
    spawning = NULL;
    first_free_link = NULL;
    inlink_count = 0;
    outlink_count = 0;
    fixed = 0;
    conn_iter = 0;
}


void Node::print(unsigned int reservoir_size)
{
    cout << "node" << (unsigned int)this
        << " fixed:" << fixed
        << " interface:" << interface
        << " indegree:" << inlink_count
        << " outdegree:" << outlink_count << endl;
    for (unsigned int i = 0; i < reservoir_size; i++)
        cout << "r" << i << ":" << reservoir[i];
    cout << endl;
}

