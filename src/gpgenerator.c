/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpgenerator.h"
#include "utils.h"
#include <stdlib.h>
#include <stdio.h>


syn_gpgen *syn_create_gpgenerator()
{
    syn_gpgen *gen = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen->edges = 0;
    gen->cycle = 0;
    gen->prog = create_random_gptree(7, 0.2, 2, 5);

    return gen;
}


void syn_destroy_gpgenerator(syn_gpgen *gen)
{
    destroy_gptree(gen->prog);
    free(gen);
}


syn_gpgen *syn_clone_gpgenerator(syn_gpgen *gen)
{
    syn_gpgen *gen_clone = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen_clone->edges = 0;
    gen_clone->cycle = 0;
    gen_clone->prog = clone_gptree(gen->prog);

    return gen_clone;
}


syn_net* syn_gpgen_run(syn_gpgen *gen, unsigned int nodes, unsigned int edges, unsigned int max_cycles)
{
    unsigned int i;
    double po, pt, io, oo, it, ot, ep;

    syn_net *net = syn_create_net();

    gen->edges = 0;
    gen->cycle = 0;

    for (i = 0; i < nodes; i++) {
        syn_add_node_with_id(net, i, 0);
    }

    syn_node* orig_node;
    syn_node* targ_node;
    double prob;

    while ((gen->edges < edges) && (gen->cycle < max_cycles)) {
        orig_node = syn_get_random_node(net);
        targ_node = orig_node;
        while (targ_node == orig_node) {
            targ_node = syn_get_random_node(net);
        }

        po = ((double)orig_node->id) / ((double)nodes);
        pt = ((double)targ_node->id) / ((double)nodes);
        
        io = oo = it = ot = ep = 0;

        if (gen->edges > 0) {
            io = ((double)orig_node->in_degree) / ((double)gen->edges);
            oo = ((double)orig_node->out_degree) / ((double)gen->edges);
            it = ((double)targ_node->in_degree) / ((double)gen->edges);
            ot = ((double)targ_node->out_degree) / ((double)gen->edges);
            ep = ((double)gen->edges) / ((double)edges);
        }

        gen->prog->vars[0] = po;
        gen->prog->vars[1] = pt;
        gen->prog->vars[2] = io;
        gen->prog->vars[3] = oo;
        gen->prog->vars[4] = it;
        gen->prog->vars[5] = ot;
        gen->prog->vars[6] = ep;
        prob = eval_gptree(gen->prog);
        //printf("prob: %f; po: %f; pt: %f; io: %f; oo: %f; it: %f; ot: %f; ep: %f\n", prob, po, pt, io, oo, it, ot, ep);

        if (RANDOM_TESTPROB(prob)) {
            syn_add_edge(orig_node, targ_node, gen->cycle);
            gen->edges++;
        }
            
        gen->cycle++;
        //printf("%d\n", gen->cycle);
    }

    return net;
}


void syn_print_gpgen(syn_gpgen* gen)
{
    print_gptree(gen->prog);
}


syn_gpgen* syn_recombine_gpgens(syn_gpgen* g1, syn_gpgen* g2)
{
    syn_gpgen *gen = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen->edges = 0;
    gen->cycle = 0;
    gen->prog = recombine_gptrees(g1->prog, g2->prog);

    return gen;
}

