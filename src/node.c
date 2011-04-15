/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "node.h"
#include "utils.h"
#include <math.h>


struct syn_node *syn_create_node(unsigned int type, unsigned int id)
{
    _type = type;
    _id = id;
    _visiting = NULL;
    _inDegree = 0;
    _outDegree = 0;
    _marked = false;
    _lastWalkId = 0;
}


void syn_destroy_node(struct syn_node *node)
{
}


int syn_add_edge(struct syn_node *origin, struct syn_node *target)
{
    if (edgeExists(target))
        return false;
    if (target == this)
        return false;

    _targets.push_back(target);
    _outDegree++;
    target->_origins.push_back(this);
    target->_inDegree++;

    return true;
}


int syn_edge_exists(struct syn_node *target)
{
    for (vector<Node*>::iterator iterNode = _targets.begin();
            iterNode != _targets.end();
            iterNode++) {
    
        if ((*iterNode) == target)
            return true;
    }

    return false;
}


struct syn_node *syn_get_random_target(struct syn_node *origin)
{
    if (_outDegree == 0)
        return NULL;

    unsigned int index = 0;
    
    if (_outDegree > 0)
        index = RANDOM_UINT(_outDegree);

    return _targets[index];
}

