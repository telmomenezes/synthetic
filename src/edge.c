/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */

#include "edge.h"
#include <stdlib.h>


syn_edge *syn_create_edge(void)
{
    syn_edge *edge = (syn_edge *)malloc(sizeof(syn_edge));
    return edge;
}


void syn_destroy_edge(syn_edge *edge)
{
    free(edge);
}

