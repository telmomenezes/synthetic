/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "network.h"
#include "gp/gptree.h"

using syn::Net;

typedef struct syn_gpgen_s {
    gptree* prog_origin;
    gptree* prog_target;
    unsigned int edges;
    unsigned int cycle;
} syn_gpgen;


syn_gpgen *syn_create_gpgenerator();

void syn_destroy_gpgenerator(syn_gpgen *gen);

syn_gpgen *syn_clone_gpgenerator(syn_gpgen *gen);

Net* syn_gpgen_run(syn_gpgen *gen, unsigned int nodes, unsigned int edges, unsigned int max_cycles);

void syn_print_gpgen(syn_gpgen* gen);

syn_gpgen* syn_recombine_gpgens(syn_gpgen* g1, syn_gpgen* g2);

