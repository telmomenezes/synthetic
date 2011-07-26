/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "link.h"
#include <stdlib.h>
#include <string.h>


Link::Link(unsigned int reservoir_size)
{
    if (reservoir_size > 0)
        reservoir = (synword *)malloc(sizeof(synword) * reservoir_size);
    else
        reservoir = NULL;
    init(reservoir_size);
}


Link::~Link()
{
    if (reservoir)
        free(reservoir);
}


void Link::init(unsigned int reservoir_size)
{
    bzero(reservoir, sizeof(synword) * reservoir_size);
}

