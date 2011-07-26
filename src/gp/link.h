/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "types.h"

enum syn_link_state {ATTACHED, ORIG_FREE, TARG_FREE};

class Node;

class Link {
public:
    Node* orig;
    Node* targ;

    synword* reservoir;

    Link* next_in;
    Link* prev_in;
    Link* next_out;
    Link* prev_out;
    Link* next_free;
    Link* prev_free;
    
    enum syn_link_state state;


    Link(unsigned int reservoir_size);

    virtual ~Link();

    void init(unsigned int reservoir_size);
};

