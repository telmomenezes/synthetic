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
    net->temporal = 0;
    net->min_ts = 0;
    net->max_ts = 0;
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

syn_node *syn_add_node_with_id(syn_net *net, unsigned int nid, unsigned int type)
{
    net->node_count++;
    if (nid >= SYN_CURID) {
        SYN_CURID = nid + 1;
    }
    syn_node *node = syn_create_node(type, nid);
    node->next = net->nodes;
    net->nodes = node;
    return node;
}

int syn_add_edge_to_net(syn_net *net, syn_node* orig, syn_node* targ, unsigned long timestamp)
{
    if (syn_add_edge(orig, targ, timestamp)) {
        net->edge_count++;

        if (timestamp > 0) {
            net->temporal = 1;
            if ((net->min_ts == 0) || (timestamp < net->min_ts)) {
                net->min_ts = timestamp;
            }
            if ((net->max_ts == 0) || (timestamp > net->max_ts)) {
                net->max_ts = timestamp;
            }
        }

        return 1;
    }
    return 0;
}

syn_drmap *syn_get_drmap(syn_net *net, unsigned int bin_number)
{
    return syn_get_drmap_with_limits(net, bin_number, net->min_pr_in, net->max_pr_in, net->min_pr_out, net->max_pr_out);
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

        if (isfinite(node->pr_in)) {
            if (node->pr_in <= min_val_hor) {
                x = 0;
            }
            else if (node->pr_in >= max_val_hor) {
                x = bin_number - 1;
            }
            else {
                x = (unsigned int)floor((node->pr_in - min_val_hor) / interval_hor);
            }
        }
        if (isfinite(node->pr_out)) {
            if (node->pr_out <= min_val_ver) {
                y = 0;
            }
            else if (node->pr_out >= max_val_ver) {
                y = bin_number - 1;
            }
            else {
                y = (unsigned int)floor((node->pr_out - min_val_ver) / interval_ver);
            }
        }

        if ((x >= 0) && (y >= 0) && ((node->in_degree != 0) || (node->out_degree != 0))) {
            syn_drmap_inc_value(map, x, y);
        }
        
        node = node->next;
    }

    return map;
}


void syn_compute_pageranks(syn_net *net)
{
    // TODO: config
    unsigned int max_iter = 100;

    syn_node *node = net->nodes;
    while (node) {
        node->pr_in_last = 1;
        node->pr_out_last = 1;
        node = node->next;
    }

    unsigned int i = 0;

    double delta_pr_in = 999;
    double delta_pr_out = 999;
    
    double zero_test = 0.0001;

    while (((delta_pr_in > zero_test) || (delta_pr_out > zero_test)) && (i < max_iter)) {
        
        double acc_pr_in = 0;
        double acc_pr_out = 0;

        node = net->nodes;
        while(node) {
            node->pr_in = 0;
            syn_edge *origin = node->origins;
            while (origin) {
                //node->_prIn += origin->_prInLast / ((double)origin->getOutDegree());
                node->pr_in += origin->orig->pr_in_last;
                origin = origin->next_orig;
            }
            node->pr_in += (1.0 - 0.85) / ((double)net->node_count);
            //node->pr_in += (1.0 - 0.85);
            node->pr_in *= 0.85;
            acc_pr_in += node->pr_in;

            node->pr_out = 0;
            syn_edge *target = node->targets;
            while (target) {
                node->pr_out += target->targ->pr_out_last;
                target = target->next_targ;
            }
            node->pr_out += (1.0 - 0.85) / ((double)net->node_count);
            //node->pr_out += (1.0 - 0.85);
            node->pr_out *= 0.85;
            acc_pr_out += node->pr_out;
            
            node = node->next;
        }
        
        //printf("acc_pr_in: %f; acc_pr_out: %f\n", acc_pr_in, acc_pr_out);

        delta_pr_in = 0;
        delta_pr_out = 0;

        node = net->nodes;
        while (node) {
            node->pr_in /= acc_pr_in;
            node->pr_out /= acc_pr_out;
            delta_pr_in += fabs(node->pr_in - node->pr_in_last);
            delta_pr_out += fabs(node->pr_out - node->pr_out_last);    
            //printf("pr_in: %f; pr_in_last: %f; delta_pr_in: %f\n", node->pr_in, node->pr_in_last, delta_pr_in);
            //printf("pr_out: %f; pr_out_last: %f; delta_pr_out: %f\n", node->pr_out, node->pr_out_last, delta_pr_out);
            node->pr_in_last = node->pr_in;
            node->pr_out_last = node->pr_out;
            
            node = node->next;
        }

        //printf("delta in: %f; delta out: %f\n", delta_pr_in, delta_pr_out);
        i++;
    }

    // relative pr
    double base_pr = 1.0 / ((double)net->node_count);
    node = net->nodes;
    while (node) {
        node->pr_in = node->pr_in / base_pr;
        node->pr_out = node->pr_out / base_pr;
        node = node->next;
    }
    
    // use log scale
    node = net->nodes;
    while (node) {
        node->pr_in = log(node->pr_in);
        node->pr_out = log(node->pr_out);
        node = node->next;
    }

    // compute min/max EVC in and out
    net->min_pr_in = 0;
    net->min_pr_out = 0;
    net->max_pr_in = 0;
    net->max_pr_out = 0;
    int first = 1;
    node = net->nodes;
    while (node) {
        if (isfinite(node->pr_in) && (first || (node->pr_in < net->min_pr_in))) {
            net->min_pr_in = node->pr_in;
        }
        if (isfinite(node->pr_out) && (first || (node->pr_out < net->min_pr_out))) {
            net->min_pr_out = node->pr_out;
        }
        if (isfinite(node->pr_in) && (first || (node->pr_in > net->max_pr_in))) {
            net->max_pr_in = node->pr_in;
        }
        if (isfinite(node->pr_out) && (first || (node->pr_out > net->max_pr_out))) {
            net->max_pr_out = node->pr_out;
        }

        first = 0;
        
        node = node->next;
    }
}


void syn_write_pageranks(syn_net *net, const char *file_path)
{
    FILE *f;
    f = fopen(file_path, "w");
   
    fprintf(f, "id, pr_in, pr_out, in_degree, out_degree\n");

    
    syn_node *node = net->nodes;
    while (node) {
        fprintf(f, "%d,%.10f,%.10f,%d,%d\n", node->id, node->pr_in, node->pr_out, node->in_degree, node->out_degree);
        node = node->next;
    }

    fclose(f);
}

void syn_print_net_info(syn_net *net)
{
    printf("node number: %d\n", net->node_count);
    printf("edge number: %d\n", net->edge_count);
    printf("log(pr_in): [%f, %f]\n", net->min_pr_in, net->max_pr_in);
    printf("log(pr_out): [%f, %f]\n", net->min_pr_out, net->max_pr_out);
}

