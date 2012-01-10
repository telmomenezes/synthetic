/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpgenerator.h"
#include "node.h"
#include "utils.h"
#include "distmatrix.h"

#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <sstream>


using std::cout;
using std::endl;


using namespace std;


namespace syn
{

GPGenerator::GPGenerator()
{
    edges = 0;
    cycle = 0;
    prog = new GPTree(10);
    prog->init_random(0.2, 2, 5);
}


GPGenerator::~GPGenerator()
{
    delete prog;
}


GPGenerator* GPGenerator::clone()
{
    GPGenerator* gen_clone = new GPGenerator();

    gen_clone->edges = 0;
    gen_clone->cycle = 0;
    gen_clone->prog = prog->clone();

    return gen_clone;
}


Net* GPGenerator::run(unsigned int node_count, unsigned int edge_count)
{
    // TODO: configure this somewhere
    unsigned int TEST_EDGES = 1000;

    // init DistMatrix
    DistMatrix::get_instance().set_nodes(node_count);

    Net* net = new Net();

    cycle = 0;

    // create nodes
    Node* node_array[node_count];
    Node* orig_array[TEST_EDGES];
    Node* targ_array[TEST_EDGES];
    double weight_array[TEST_EDGES];
    for (unsigned int i = 0; i < node_count; i++) {
        node_array[i] = net->add_node_with_id(i, 0);
    }

    // create edges
    double weight;
    for (unsigned int i = 0; i < edge_count; i++) {
        double po, pt, io, oo, it, ot, t, ud, dd, rd;     
        double total_weight = 0;

        Node* orig_node;
        Node* targ_node;

        // test TEST_EDGES edges
        for (unsigned int j = 0; j < TEST_EDGES; j++) {
            bool new_edge = false;
            while (!new_edge) {
                unsigned int orig_index = RANDOM_UINT(node_count);
                unsigned int targ_index = RANDOM_UINT(node_count - 1);
                if (targ_index >= orig_index) {
                    targ_index += 1;
                }
                orig_node = node_array[orig_index];
                targ_node = node_array[targ_index];
                if (!orig_node->edge_exists(targ_node)) {
                    new_edge = true;
                }
            }

            orig_array[j] = orig_node;
            targ_array[j] = targ_node;

            po = (double)orig_node->id;
            pt = (double)targ_node->id;
        
            io = oo = it = ot = t = 0;

            if (edges > 0) {
                io = (double)orig_node->in_degree;
                oo = (double)orig_node->out_degree;
                it = (double)targ_node->in_degree;
                ot = (double)targ_node->out_degree;
                t = (double)cycle;
            }

            unsigned int dist = DistMatrix::get_instance().get_udistance(orig_node->id, targ_node->id);
            if (dist > 0) {
                ud = 1.0 / ((double)dist);
            }
            // lim d->inf 1/d
            else {
                ud = 0;
            }

            dist = DistMatrix::get_instance().get_ddistance(orig_node->id, targ_node->id);
            if (dist > 0) {
                dd = 1.0 / ((double)dist);
            }
            // lim d->inf 1/d
            else {
                dd = 0;
            }
            
            dist = DistMatrix::get_instance().get_ddistance(targ_node->id, orig_node->id);
            if (dist > 0) {
                dd = 1.0 / ((double)dist);
            }
            // lim d->inf 1/d
            else {
                rd = 0;
            }            

            prog->vars[0] = po;
            prog->vars[1] = io;
            prog->vars[2] = oo;
            prog->vars[3] = t;
            prog->vars[4] = pt;
            prog->vars[5] = it;
            prog->vars[6] = ot;
            prog->vars[7] = ud;
            prog->vars[8] = dd;
            prog->vars[9] = rd;

            weight = prog->eval();
            if (weight < 0) {
                weight = 0;
            }
        
            weight_array[j] = weight;
            total_weight += weight;
        }

        // if total weight is zero, make every pair's weight = 1
        if (total_weight == 0) {
            for (unsigned int j = 0; j < TEST_EDGES; j++) {
                weight_array[j] = 1.0;
                total_weight += 1.0;
            }
        }

        weight = RANDOM_UNIFORM * total_weight;
        unsigned int j = 0;
        total_weight = weight_array[j];
        while (total_weight < weight) {
            j++;
            total_weight += weight_array[j];
        }

        orig_node = orig_array[j];
        targ_node = targ_array[j];

        // set ages
        if (orig_node->birth < 0) {
            orig_node->birth = cycle;
        }
        if (targ_node->birth < 0) {
            targ_node->birth = cycle;
        }

        net->add_edge_to_net(orig_node, targ_node, cycle);
        // update distances
        DistMatrix::get_instance().update_distances(orig_node->id, targ_node->id);
            
        cycle++;
    }

    return net;
}


void GPGenerator::write(string file_path)
{
    ofstream f;
    f.open(file_path.c_str());

    f << "#PROG" << endl;
    f << prog->to_string();

    f.close();
}


GPGenerator* GPGenerator::recombine(GPGenerator* gen)
{
    GPGenerator* child = new GPGenerator();

    edges = 0;
    cycle = 0;
    child->prog = prog->recombine(gen->prog);

    return child;
}


void GPGenerator::load(string filepath)
{
    std::ifstream file(filepath.c_str());
    string line;

    std::getline(file, line);
         
    while ((line.compare("") == 0)
            || (line[0] == '\n')
            || (line[0] == '#')) {
        std::getline(file, line);
    }
        
    std::stringstream sprog;
    while (line.compare("")
            && (line[0] != '\n')
            && (line[0] != '#')) {
        sprog << line;
        if (!std::getline(file, line))
            break;
    }

    prog->parse(sprog.str());
}


void GPGenerator::simplify() {
    prog->dyn_pruning();    
}

}
