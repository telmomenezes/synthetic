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


void syn_write_net(syn_net *net, const char *file_path)
{
    FILE *f;
    f = fopen(file_path, "w");

    syn_node *orig_node = net->nodes;
    syn_edge *edge;

    while (orig_node) {
        edge = orig_node->targets;

        while(edge) {
            fprintf(f, "%d,%d\n", orig_node->id, edge->targ->id);
            edge = edge->next_orig;
        }
        
        orig_node = orig_node->next;
    }

    fclose(f);
}


void syn_write_gexf(syn_net *net, const char *file_path)
{
    FILE *f;
    f = fopen(file_path, "w");

    // start file
    fprintf(f, "<gexf xmlns=\"http://www.gexf.net/1.1draft\" version=\"1.1\">\n");
    fprintf(f, "<graph mode=\"static\" defaultedgetype=\"directed\">\n");

    // write nodes
    fprintf(f, "<nodes>\n");
    syn_node *node = net->nodes;
    while (node) {
        fprintf(f, "<node id=\"%d\">\n", node->id);
        fprintf(f, "</node>\n");
        node = node->next;
    }
    fprintf(f, "</nodes>\n");

    // write edges
    unsigned int edge_id = 0;
    fprintf(f, "<edges>\n");
    node = net->nodes;
    syn_edge *edge;
    while (node) {
        edge = node->targets;
        while (edge) {
            fprintf(f, "<edge id=\"%d\" source=\"%d\" target=\"%d\" />\n", edge_id++, node->id, edge->targ->id);
            edge = edge->next_orig;
        }
        node = node->next;
    }
    fprintf(f, "</edges>\n");

    // end file
    fprintf(f, "</graph>\n");
    fprintf(f, "</gexf>\n");

    fclose(f);
}

syn_histogram2d *syn_get_evc_histogram(syn_net *net, unsigned int bin_number)
{
    return syn_get_evc_histogram_with_limits(net, bin_number, net->min_evc_in, net->max_evc_in, net->min_evc_out, net->max_evc_out);
}

syn_histogram2d *syn_get_evc_histogram_with_limits(syn_net *net, unsigned int bin_number, double min_val_hor,
                                        double max_val_hor, double min_val_ver, double max_val_ver)
{
    double interval_hor = (max_val_hor - min_val_hor) / ((double)bin_number);
    double interval_ver = (max_val_ver - min_val_ver) / ((double)bin_number);

    syn_histogram2d *hist = syn_histogram2d_create(bin_number + 1, min_val_hor - interval_hor,
        max_val_hor, min_val_ver - interval_ver, max_val_ver);

    syn_node *node = net->nodes;
    while (node) {
        int x = 0;
        int y = 0;

        if (isfinite(node->evc_in)) {
            if (node->evc_in < min_val_hor) {
                x = -1;
            }
            else if (node->evc_in > max_val_hor) {
                x = -1;
            }
            else {
                x = (unsigned int)ceil((node->evc_in - min_val_hor) / interval_hor);
            }
        }
        if (isfinite(node->evc_out)) {
            if (node->evc_out < min_val_ver) {
                y = -1;
            }
            else if (node->evc_out > max_val_ver) {
                y = -1;
            }
            else {
                y = (unsigned int)ceil((node->evc_out - min_val_ver) / interval_ver);
            }
        }

        if ((x >= 0) && (y >= 0)) {
            syn_historgram2d_inc_value(hist, x, y);
        }
        
        node = node->next;
    }

    return hist;
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
                origin = origin->next_targ;
            }
            node->evc_in += (1.0 - 0.85) / ((double)net->node_count);
            //node->evc_in += (1.0 - 0.85);
            node->evc_in *= 0.85;
            acc_evc_in += node->evc_in;

            node->evc_out = 0;
            syn_edge *target = node->targets;
            while (target) {
                node->evc_out += target->targ->evc_out_last;
                target = target->next_orig;
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
            node->evc_in_last = node->evc_in;
            node->evc_out_last = node->evc_out;
            
            node = node->next;
        }

        //printf("delta in: %f; delta out: %f\n", delta_evc_in, delta_evc_out);
        i++;
    }

    // use log scale
    /*
    node = net->nodes;
    while (node) {
        node->evc_in = log(node->evc_in);
        node->evc_out = log(node->evc_out);
        node = node->next;
    }
    */

    // compute max EVC in and out
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
   
    fprintf(f, "evc_in, evc_out\n");

    
    syn_node *node = net->nodes;
    while (node) {
        fprintf(f, "%.10f,%.10f,%d,%d\n", node->evc_in, node->evc_out, node->in_degree, node->out_degree);
        node = node->next;
    }

    fclose(f);
}


void syn_load_net(syn_net *net, const char *file_path)
{   
    FILE *f;
    f = fopen(file_path, "r");
    
    unsigned int MAX_LEN = 1000;
    char line[MAX_LEN];
    
    int max_node = -1;
    
    while(fgets(line, MAX_LEN, f) != NULL) {
        
        if (strlen(line) == 0) {
            break;
        }
        char *orig_str = strtok(line, ",");
        char *targ_str = strtok(NULL, ",");
        int orig = atoi(orig_str);
        int targ = atoi(targ_str);
        if (orig > max_node) {
            max_node = orig;
        }
        if (targ > max_node) {
            max_node = targ;
        }
    }
    
    int node_count = max_node + 1;
    if (node_count <= 0) {
        return;
    }
    
    // create nodes
    syn_node *nodes[node_count];
    unsigned int i = 0;
    for (i = 0; i < node_count; i++) {
        nodes[i] = syn_add_node(net, 0);
    }

    // add links
    rewind(f);

    while(fgets(line, MAX_LEN, f) != NULL) {
        if (strlen(line) == 0) {
            break;
        }
        char *orig_str = strtok(line, ",");
        char *targ_str = strtok(NULL, ",");
        int orig = atoi(orig_str);
        int targ = atoi(targ_str);

        syn_add_edge_to_net(net, nodes[orig], nodes[targ]);
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
