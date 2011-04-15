/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */

#include "edge.h"
#include <stdlib.h>


syn_edge *syn_edge_create(void)
{
    syn_edge *edge = (syn_edge *)malloc(sizeof(syn_edge));
    return edge;
}
