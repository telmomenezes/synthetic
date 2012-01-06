/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "distmatrix.h"

#include <stdlib.h>
#include <strings.h>


#define UDIST(x, y) (_umatrix[((y) * _nodes) + (x)])
#define DDIST(x, y) (_dmatrix[((y) * _nodes) + (x)])


namespace syn
{

DistMatrix::DistMatrix()
{
    _nodes = 0;
}


DistMatrix::~DistMatrix()
{
    if (_nodes > 0) {
        free(_umatrix);
        free(_dmatrix);
    }
}


void DistMatrix::set_nodes(unsigned int nodes)
{
    if (nodes != _nodes) {
        if (_nodes > 0) {
            free(_umatrix);
            free(_dmatrix);
        }
        _nodes = nodes;
        if (_nodes > 0) {
            _umatrix = (unsigned int *)malloc(sizeof(unsigned int) * _nodes * _nodes);
            _dmatrix = (unsigned int *)malloc(sizeof(unsigned int) * _nodes * _nodes);
        }
    }

    // clear matrices
    if (_nodes > 0) {
        bzero(_umatrix, sizeof(unsigned int) * _nodes * _nodes);
        bzero(_dmatrix, sizeof(unsigned int) * _nodes * _nodes);
    }
}


void DistMatrix::update_distances(unsigned int new_orig, unsigned int new_targ)
{   
    // update distances between new_orig and new_targ
    UDIST(new_orig, new_targ) = 1;
    UDIST(new_targ, new_orig) = 1;
    DDIST(new_orig, new_targ) = 1;

    // iterate through all pairs of nodes
    for (unsigned int i = 0; i < _nodes; i++) {
        if ((UDIST(i, new_orig) == 0) && (UDIST(i, new_targ) == 0)) {
            continue;
        }

        for (unsigned int j = 0; j < _nodes; j++) {
            // update undirected distances
            unsigned int d0 = UDIST(i, new_orig);
            unsigned int d1 = UDIST(new_targ, j);
            unsigned int d = d0 + d1;
            if ((d0 > 0) && (d1 > 0)) {
                if ((d < UDIST(i, j)) || (UDIST(i, j) == 0)) {
                    UDIST(i, j) = d;
                }
            }

            d0 = UDIST(j, new_orig);
            d1 = UDIST(new_targ, i);
            d = d0 + d1;
            if ((d0 > 0) && (d1 > 0)) {
                if ((d < UDIST(i, j)) || (UDIST(i, j) == 0)) {
                    UDIST(i, j) = d;
                }
            }

            // update directed distances
            d0 = DDIST(i, new_orig);
            d1 = DDIST(new_targ, j);
            d = d0 + d1;
            if ((d0 > 0) && (d1 > 0)) {
                if ((d < DDIST(i, j)) || (DDIST(i, j) == 0)) {
                    DDIST(i, j) = d;
                }
            }
        }
    }
}


unsigned int DistMatrix::get_udistance(unsigned int orig, unsigned int targ)
{
    return UDIST(orig, targ);
}


unsigned int DistMatrix::get_ddistance(unsigned int orig, unsigned int targ)
{
    return DDIST(orig, targ);
}

}
