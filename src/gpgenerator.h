/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "network.h"
#include "gp/gptree.h"


typedef struct syn_gpgen_s {
    gptree* prog;
    unsigned int edges;
    unsigned int cycle;
} syn_gpgen;


syn_gpgen *syn_create_gpgenerator();

void syn_destroy_gpgenerator(syn_gpgen *gen);

syn_gpgen *syn_clone_gpgenerator(syn_gpgen *gen);

syn_net *syn_gene_run(syn_gpgen *gen, unsigned int nodes, unsigned int edges, unsigned int max_cycles);

