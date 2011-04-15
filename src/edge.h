/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

struct syn_node;

typedef struct {
    struct syn_node *orig;
    struct syn_node *targ;
    struct syn_edge *next_orig;
    struct syn_edge *next_targ;
} syn_edge;

struct syn_edge *syn_edge_create(void);
