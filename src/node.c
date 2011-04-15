/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "Node.h"
#include "utils.h"
#include <math.h>


Node::Node(unsigned int type, unsigned int id)
{
    _type = type;
    _id = id;
    _visiting = NULL;
    _inDegree = 0;
    _outDegree = 0;
    _marked = false;
    _lastWalkId = 0;
}


Node::~Node()
{
}


bool Node::addEdge(Node* target)
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


bool Node::edgeExists(Node* target)
{
    for (vector<Node*>::iterator iterNode = _targets.begin();
            iterNode != _targets.end();
            iterNode++) {
    
        if ((*iterNode) == target)
            return true;
    }

    return false;
}


Node* Node::getRandomTarget()
{
    if (_outDegree == 0)
        return NULL;

    unsigned int index = 0;
    
    if (_outDegree > 0)
        index = RANDOM_UINT(_outDegree);

    return _targets[index];
}

