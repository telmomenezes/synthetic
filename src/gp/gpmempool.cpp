/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpmempool.h"


GPMemPool::GPMemPool(size_t buffer_size)
{
    _first_gpnode = NULL;
    _buffer_size = buffer_size;
    add_buffer();
}


GPMemPool::~GPMemPool()
{
    for (list<GPNode*>::iterator iterBuff = _buffers.begin();
            iterBuff != _buffers.end();
            iterBuff++)
    {
        free(*iterBuff);
    }
}


GPNode* GPMemPool::get_node()
{
    GPNode* node;
    if (_first_gpnode) {
        node = _first_gpnode;
        _first_gpnode = node->parent;
        return node;
    }
    else {
        add_buffer();
        return get_node();
    }
}


void GPMemPool::return_node(GPNode* node)
{
    node->parent = _first_gpnode;
    _first_gpnode = node;
}


void GPMemPool::add_buffer()
{
    GPNode* buffer = (GPNode *)malloc(sizeof(GPNode) * _buffer_size);
    for (unsigned int i = 0; i < _buffer_size; i++)
        if (i == 0)
            buffer[i].parent = NULL;
        else
            buffer[i].parent = &(buffer[i - 1]);

    _first_gpnode = &(buffer[_buffer_size - 1]);
    _buffers.push_back(buffer);
}

