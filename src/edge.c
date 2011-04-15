/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */

#include "edge.h"
#include <stdlib.h>


struct syn_edge *syn_edge_create(void)
{
    struct syn_edge *edge = (struct syn_edge *)malloc(sizeof(syn_edge));
    return edge;
}
