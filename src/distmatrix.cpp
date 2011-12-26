/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "distmatrix.h"

#include <stdlib.h>
#include <strings.h>


#define DIST(x, y) (_matrix[((y) * _nodes) + (x)])


namespace syn
{

DistMatrix::DistMatrix()
{
    _nodes = 0;
}


DistMatrix::~DistMatrix()
{
    if (_nodes > 0) {
        free(_matrix);
    }
}


void DistMatrix::set_nodes(unsigned int nodes)
{
    if (nodes != _nodes) {
        if (_nodes > 0) {
            free(_matrix);
        }
        _nodes = nodes;
        if (_nodes > 0) {
            _matrix = (unsigned int *)malloc(sizeof(unsigned int) * _nodes * _nodes);
        }
    }

    // clear matrix
    if (_nodes > 0) {
        bzero(_matrix, sizeof(unsigned int) * _nodes * _nodes);
    }
}


void DistMatrix::update_distances(unsigned int new_orig, unsigned int new_targ)
{   
    // update direct distance between new_orig and new_targ
    DIST(new_orig, new_targ) = 1;
    DIST(new_targ, new_orig) = 1;

    // iterate through all paris of nodes
    for (unsigned int i = 0; i < _nodes; i++) {
        for (unsigned int j = 0; j < _nodes; j++) {
            
            unsigned int d0 = DIST(i, new_orig);
            unsigned int d1 = DIST(new_targ, j);
            unsigned int d = d0 + d1;
            if ((d0 > 0) && (d1 > 0)) {
                if ((d < DIST(i, j)) || (DIST(i, j) == 0)) {
                    DIST(i, j) = d;
                }
            }

            d0 = DIST(j, new_orig);
            d1 = DIST(new_targ, i);
            d = d0 + d1;
            if ((d0 > 0) && (d1 > 0)) {
                if ((d < DIST(i, j)) || (DIST(i, j) == 0)) {
                    DIST(i, j) = d;
                }
            }
        }
    }
}


unsigned int DistMatrix::get_distance(unsigned int new_orig, unsigned int new_targ)
{
    return DIST(new_orig, new_targ);
}

}
