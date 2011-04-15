/*
 * Copyright (C) 2011 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

struct syn_node_s;

typedef struct syn_edge_s {
    struct syn_node_s *orig;
    struct syn_node_s *targ;
    struct syn_edge_s *next_orig;
    struct syn_edge_s *next_targ;
} syn_edge;

syn_edge *syn_edge_create(void);
