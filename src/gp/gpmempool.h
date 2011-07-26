/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "gpnode.h"
#include <stdlib.h>
#include <list>


using std::list;


class GPMemPool {
public:
    GPMemPool(size_t buffer_size);
    virtual ~GPMemPool();

    GPNode* get_node();
    void return_node(GPNode* node);

private:
    GPNode* _first_gpnode;
    list<GPNode*> _buffers;
    size_t _buffer_size;

    void add_buffer();
};

