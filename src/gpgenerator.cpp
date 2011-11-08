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
    gen->prog_origin = create_random_gptree(4, 0.2, 2, 5);
    gen->prog_target = create_random_gptree(7, 0.2, 2, 5);

    return gen;
}


void syn_destroy_gpgenerator(syn_gpgen *gen)
{
    destroy_gptree(gen->prog_origin);
    destroy_gptree(gen->prog_target);
    free(gen);
}


syn_gpgen *syn_clone_gpgenerator(syn_gpgen *gen)
{
    syn_gpgen *gen_clone = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen_clone->edges = 0;
    gen_clone->cycle = 0;
    gen_clone->prog_origin = clone_gptree(gen->prog_origin);
    gen_clone->prog_target = clone_gptree(gen->prog_target);

    return gen_clone;
}


syn_net* syn_gpgen_run(syn_gpgen *gen, unsigned int nodes, unsigned int edges, unsigned int max_cycles)
{
    unsigned int i, j;
    double po, pt, io, oo, it, ot, ep;

    syn_net *net = syn_create_net();

    gen->edges = 0;
    gen->cycle = 0;

    // create nodes
    for (i = 0; i < nodes; i++) {
        syn_add_node_with_id(net, i, 0);
    }

    syn_node* orig_node;
    syn_node* targ_node;
    double total_weight;
    double weight;

    for (i = 0; i < edges; i++) {
        total_weight = 0;
        orig_node = net->nodes;
        while (orig_node) {
            po = (double)orig_node->id;
            io = oo = ep = 0;
            if (gen->edges > 0) {
                io = (double)orig_node->in_degree;
                oo = (double)orig_node->out_degree;
                ep = (double)gen->edges;
            }

            gen->prog_origin->vars[0] = po;
            gen->prog_origin->vars[1] = io;
            gen->prog_origin->vars[2] = oo;
            gen->prog_origin->vars[3] = ep;
            weight = eval_gptree(gen->prog_origin);
            if (weight < 0)
                weight = 0;

            orig_node->genweight = weight;
            total_weight += weight;

            orig_node = orig_node->next;
        }

        // if total weight is zero, make every node's weight = 1
        if (total_weight == 0) {
            orig_node = net->nodes;
            while (orig_node) {
                orig_node->genweight = 1.0;
                total_weight += 1.0;
                orig_node = orig_node->next;
            }
        }

        weight = RANDOM_UNIFORM * total_weight;
        orig_node = net->nodes;
        total_weight = orig_node->genweight;
        //printf("w: %f; tw: %f\n", weight, total_weight);
        while (total_weight < weight) {
            orig_node = orig_node->next;
            total_weight += orig_node->genweight;
        }
        
        total_weight = 0;
        targ_node = net->nodes;
        while (targ_node) {
            po = (double)orig_node->id;
            pt = (double)targ_node->id;
        
            io = oo = it = ot = ep = 0;

            if (gen->edges > 0) {
                io = (double)orig_node->in_degree;
                oo = (double)orig_node->out_degree;
                it = (double)targ_node->in_degree;
                ot = (double)targ_node->out_degree;
                ep = (double)gen->edges;
            }

            gen->prog_target->vars[0] = po;
            gen->prog_target->vars[1] = pt;
            gen->prog_target->vars[2] = io;
            gen->prog_target->vars[3] = oo;
            gen->prog_target->vars[4] = it;
            gen->prog_target->vars[5] = ot;
            gen->prog_target->vars[6] = ep;
            weight = eval_gptree(gen->prog_target);
            if (weight < 0)
                weight = 0;
            //printf("weight: %f; po: %f; pt: %f; io: %f; oo: %f; it: %f; ot: %f; ep: %f\n", weight, po, pt, io, oo, it, ot, ep);

            if (orig_node == targ_node)
                weight = 0;
        
            targ_node->genweight = weight;
            total_weight += weight;

            targ_node = targ_node->next;
        }

        // if total weight is zero, make every node's weight = 1
        if (total_weight == 0) {
            targ_node = net->nodes;
            while (targ_node) {
                targ_node->genweight = 1.0;
                total_weight += 1.0;
                targ_node = targ_node->next;
            }
        }

        weight = RANDOM_UNIFORM * total_weight;
        targ_node = net->nodes;
        total_weight = targ_node->genweight;
        while (total_weight < weight) {
            targ_node = targ_node->next;
            total_weight += targ_node->genweight;
        }

        syn_add_edge_to_net(net, orig_node, targ_node, gen->cycle);
        gen->edges++;
            
        gen->cycle++;
    }

    return net;
}


void syn_print_gpgen(syn_gpgen* gen)
{
    printf("PROG ORIGIN\n\n");
    print_gptree(gen->prog_origin);
    printf("\nPROG TARGET\n");
    print_gptree(gen->prog_target);
    printf("\n");
}


syn_gpgen* syn_recombine_gpgens(syn_gpgen* g1, syn_gpgen* g2)
{
    syn_gpgen *gen = (syn_gpgen*)malloc(sizeof(syn_gpgen));

    gen->edges = 0;
    gen->cycle = 0;
    switch(random() % 3) {
    case 0:
        gen->prog_origin = recombine_gptrees(g1->prog_origin, g2->prog_origin);
        gen->prog_target = clone_gptree(g1->prog_target);
        break;
    case 1:
        gen->prog_origin = clone_gptree(g1->prog_origin);
        gen->prog_target = recombine_gptrees(g1->prog_target, g2->prog_target);
        break;
    case 2:
        gen->prog_origin = recombine_gptrees(g1->prog_origin, g2->prog_origin);
        gen->prog_target = recombine_gptrees(g1->prog_target, g2->prog_target);
        break;
    }

    return gen;
}

