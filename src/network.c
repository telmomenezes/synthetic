/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "network.h"
#include "utils.h"
#include <math.h>
#include <string.h>
#include <stdio.h>

static unsigned int SYN_CURID = 0;

syn_net *syn_create_net()
{
    syn_net *net = (syn_net *)malloc(sizeof(syn_net));
    net->node_count = 0;
    net->edge_count = 0;
    net->nodes = NULL;
    return net;
}


void syn_destroy_net(syn_net *net)
{
    syn_node *node = net->nodes;
    syn_node *next_node;
    while (node) {
        next_node = node->next;
        syn_destroy_node(node);
        node = next_node;
    }
    free(net);
}


syn_node *syn_add_node(syn_net *net, unsigned int type)
{
    net->node_count++;
    syn_node *node = syn_create_node(type, SYN_CURID++);
    node->next = net->nodes;
    net->nodes = node;
    return node;
}


int syn_add_edge_to_net(syn_net *net, syn_node* orig, syn_node* targ)
{
    if (syn_add_edge(orig, targ)) {
        net->edge_count++;
        return 1;
    }
    return 0;
}

syn_drmap *syn_get_drmap(syn_net *net, unsigned int bin_number)
{
    return syn_get_drmap_with_limits(net, bin_number, net->min_evc_in, net->max_evc_in, net->min_evc_out, net->max_evc_out);
}

syn_drmap *syn_get_drmap_with_limits(syn_net *net, unsigned int bin_number, double min_val_hor,
                                        double max_val_hor, double min_val_ver, double max_val_ver)
{
    double interval_hor = (max_val_hor - min_val_hor) / ((double)bin_number);
    double interval_ver = (max_val_ver - min_val_ver) / ((double)bin_number);

    syn_drmap *map = syn_drmap_create(bin_number, min_val_hor - interval_hor,
        max_val_hor, min_val_ver - interval_ver, max_val_ver);

    syn_node *node = net->nodes;
    while (node) {
        int x = 0;
        int y = 0;

        if (isfinite(node->evc_in)) {
            if (node->evc_in < min_val_hor) {
                x = 0;
            }
            else if (node->evc_in > max_val_hor) {
                x = bin_number - 1;
            }
            else {
                x = (unsigned int)ceil((node->evc_in - min_val_hor) / interval_hor);
            }
        }
        if (isfinite(node->evc_out)) {
            if (node->evc_out < min_val_ver) {
                y = 0;
            }
            else if (node->evc_out > max_val_ver) {
                y = bin_number - 1;
            }
            else {
                y = (unsigned int)ceil((node->evc_out - min_val_ver) / interval_ver);
            }
        }

        if ((x >= 0) && (y >= 0)) {
            syn_drmap_inc_value(map, x, y);
        }
        
        node = node->next;
    }

    return map;
}


void syn_compute_evc(syn_net *net)
{
    // TODO: config
    unsigned int max_iter = 100;

    syn_node *node = net->nodes;
    while (node) {
        node->evc_in_last = 1;
        node->evc_out_last = 1;
        node = node->next;
    }

    unsigned int i = 0;

    double delta_evc_in = 999;
    double delta_evc_out = 999;
    
    double zero_test = 0.0001;

    while (((delta_evc_in > zero_test) || (delta_evc_out > zero_test)) && (i < max_iter)) {
        
        double acc_evc_in = 0;
        double acc_evc_out = 0;

        node = net->nodes;
        while(node) {
            node->evc_in = 0;
            syn_edge *origin = node->origins;
            while (origin) {
                //node->_evcIn += origin->_evcInLast / ((double)origin->getOutDegree());
                node->evc_in += origin->orig->evc_in_last;
                origin = origin->next_orig;
            }
            node->evc_in += (1.0 - 0.85) / ((double)net->node_count);
            //node->evc_in += (1.0 - 0.85);
            node->evc_in *= 0.85;
            acc_evc_in += node->evc_in;

            node->evc_out = 0;
            syn_edge *target = node->targets;
            while (target) {
                node->evc_out += target->targ->evc_out_last;
                target = target->next_targ;
            }
            node->evc_out += (1.0 - 0.85) / ((double)net->node_count);
            //node->evc_out += (1.0 - 0.85);
            node->evc_out *= 0.85;
            acc_evc_out += node->evc_out;
            
            node = node->next;
        }
        
        //printf("acc_evc_in: %f; acc_evc_out: %f\n", acc_evc_in, acc_evc_out);

        delta_evc_in = 0;
        delta_evc_out = 0;

        node = net->nodes;
        while (node) {
            node->evc_in /= acc_evc_in;
            node->evc_out /= acc_evc_out;
            delta_evc_in += fabs(node->evc_in - node->evc_in_last);
            delta_evc_out += fabs(node->evc_out - node->evc_out_last);    
            //printf("evc_in: %f; evc_in_last: %f; delta_evc_in: %f\n", node->evc_in, node->evc_in_last, delta_evc_in);
            //printf("evc_out: %f; evc_out_last: %f; delta_evc_out: %f\n", node->evc_out, node->evc_out_last, delta_evc_out);
            node->evc_in_last = node->evc_in;
            node->evc_out_last = node->evc_out;
            
            node = node->next;
        }

        //printf("delta in: %f; delta out: %f\n", delta_evc_in, delta_evc_out);
        i++;
    }

    // relative evc
    double base_evc = 1.0 / ((double)net->node_count);
    node = net->nodes;
    while (node) {
        node->evc_in = node->evc_in / base_evc;
        node->evc_out = node->evc_out / base_evc;
        node = node->next;
    }
    
    // use log scale
    node = net->nodes;
    while (node) {
        node->evc_in = log(node->evc_in);
        node->evc_out = log(node->evc_out);
        node = node->next;
    }

    // compute min/max EVC in and out
    net->min_evc_in = 0;
    net->min_evc_out = 0;
    net->max_evc_in = 0;
    net->max_evc_out = 0;
    int first = 1;
    node = net->nodes;
    while (node) {
        if (isfinite(node->evc_in) && (first || (node->evc_in < net->min_evc_in))) {
            net->min_evc_in = node->evc_in;
        }
        if (isfinite(node->evc_out) && (first || (node->evc_out < net->min_evc_out))) {
            net->min_evc_out = node->evc_out;
        }
        if (isfinite(node->evc_in) && (first || (node->evc_in > net->max_evc_in))) {
            net->max_evc_in = node->evc_in;
        }
        if (isfinite(node->evc_out) && (first || (node->evc_out > net->max_evc_out))) {
            net->max_evc_out = node->evc_out;
        }

        first = 0;
        
        node = node->next;
    }
}


void syn_write_evc(syn_net *net, const char *file_path)
{
    FILE *f;
    f = fopen(file_path, "w");
   
    fprintf(f, "id, evc_in, evc_out, in_degree, out_degree\n");

    
    syn_node *node = net->nodes;
    while (node) {
        fprintf(f, "%d,%.10f,%.10f,%d,%d\n", node->id, node->evc_in, node->evc_out, node->in_degree, node->out_degree);
        node = node->next;
    }

    fclose(f);
}

void syn_print_net_info(syn_net *net)
{
    printf("node number: %d\n", net->node_count);
    printf("edge number: %d\n", net->edge_count);
    printf("log(evc_in): [%f, %f]\n", net->min_evc_in, net->max_evc_in);
    printf("log(evc_out): [%f, %f]\n", net->min_evc_out, net->max_evc_out);
}

