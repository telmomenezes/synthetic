/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "gpgenerator.h"
#include "node.h"
#include "utils.h"
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
    prog_origin = new GPTree(5);
    prog_origin->init_random(0.2, 2, 5);
    prog_target = new GPTree(10);
    prog_target->init_random(0.2, 2, 5);
}


GPGenerator::~GPGenerator()
{
    delete prog_origin;
    delete prog_target;
}


GPGenerator* GPGenerator::clone()
{
    GPGenerator* gen_clone = new GPGenerator();

    gen_clone->edges = 0;
    gen_clone->cycle = 0;
    gen_clone->prog_origin = prog_origin->clone();
    gen_clone->prog_target = prog_target->clone();

    return gen_clone;
}


Net* GPGenerator::run(unsigned int node_count, unsigned int edge_count)
{
    Net* net = new Net();

    cycle = 0;

    // create nodes
    for (unsigned int i = 0; i < node_count; i++) {
        net->add_node_with_id(i, 0);
    }

    // create edges
    Node* orig_node;
    Node* targ_node;
    double total_weight;
    double weight;

    double po, pt, io, oo, it, ot, t, ao, at, r;

    for (unsigned int i = 0; i < edge_count; i++) {
        total_weight = 0;
        orig_node = net->get_nodes();
        while (orig_node) {
            po = (double)orig_node->id;
            io = oo = t = ao = at = 0;
            if (edges > 0) {
                io = (double)orig_node->in_degree;
                oo = (double)orig_node->out_degree;
                t = (double)cycle;
            }

            if (orig_node->birth >= 0) {
                ao = (double)(cycle - orig_node->birth);
            }

            prog_origin->vars[0] = po;
            prog_origin->vars[1] = io;
            prog_origin->vars[2] = oo;
            prog_origin->vars[3] = ao;
            prog_origin->vars[4] = t;
            weight = prog_origin->eval();
            if (weight < 0) {
                weight = 0;
            }

            orig_node->genweight = weight;
            total_weight += weight;

            orig_node = orig_node->next;
        }

        // if total weight is zero, make every node's weight = 1
        if (total_weight == 0) {
            orig_node = net->get_nodes();
            while (orig_node) {
                orig_node->genweight = 1.0;
                total_weight += 1.0;
                orig_node = orig_node->next;
            }
        }

        weight = RANDOM_UNIFORM * total_weight;
        orig_node = net->get_nodes();
        total_weight = orig_node->genweight;
        //printf("w: %f; tw: %f\n", weight, total_weight);
        while (total_weight < weight) {
            orig_node = orig_node->next;
            total_weight += orig_node->genweight;
        }
        
        total_weight = 0;
        targ_node = net->get_nodes();
        while (targ_node) {
            po = (double)orig_node->id;
            pt = (double)targ_node->id;
        
            io = oo = it = ot = t = ao = at = 0;

            if (edges > 0) {
                io = (double)orig_node->in_degree;
                oo = (double)orig_node->out_degree;
                it = (double)targ_node->in_degree;
                ot = (double)targ_node->out_degree;
                t = (double)cycle;
            }

            if (orig_node->birth >= 0) {
                ao = (double)(cycle - orig_node->birth);
            }
            if (targ_node->birth >= 0) {
                at = (double)(cycle - targ_node->birth);
            }

            //cout << "ages: " << ao << "; " << at << endl;

            r = targ_node->edge_exists(orig_node);

            prog_target->vars[0] = po;
            prog_target->vars[1] = io;
            prog_target->vars[2] = oo;
            prog_target->vars[3] = ao;
            prog_target->vars[4] = t;
            prog_target->vars[5] = pt;
            prog_target->vars[6] = it;
            prog_target->vars[7] = ot;
            prog_target->vars[8] = at;
            prog_target->vars[9] = r;
            weight = prog_target->eval();
            if (weight < 0) {
                weight = 0;
            }
            //printf("weight: %f; po: %f; pt: %f; io: %f; oo: %f; it: %f; ot: %f; ep: %f\n", weight, po, pt, io, oo, it, ot, ep);

            if (orig_node == targ_node) {
                weight = 0;
            }
        
            targ_node->genweight = weight;
            total_weight += weight;

            targ_node = targ_node->next;
        }

        // if total weight is zero, make every node's weight = 1
        if (total_weight == 0) {
            targ_node = net->get_nodes();
            while (targ_node) {
                targ_node->genweight = 1.0;
                total_weight += 1.0;
                targ_node = targ_node->next;
            }
        }

        weight = RANDOM_UNIFORM * total_weight;
        targ_node = net->get_nodes();
        total_weight = targ_node->genweight;
        while (total_weight < weight) {
            targ_node = targ_node->next;
            total_weight += targ_node->genweight;
        }

        // set ages
        if (orig_node->birth < 0) {
            orig_node->birth = cycle;
        }
        if (targ_node->birth < 0) {
            targ_node->birth = cycle;
        }

        net->add_edge_to_net(orig_node, targ_node, cycle);
            
        cycle++;
    }

    return net;
}


void GPGenerator::write(string file_path)
{
    ofstream f;
    f.open(file_path.c_str());

    f << "#PROG ORIGIN" << endl;
    f << prog_origin->to_string();
    f << endl << "#PROG TARGET" << endl;
    f << prog_target->to_string();

    f.close();
}


GPGenerator* GPGenerator::recombine(GPGenerator* gen)
{
    GPGenerator* child = new GPGenerator();

    edges = 0;
    cycle = 0;
    switch(random() % 3) {
    case 0:
        child->prog_origin = prog_origin->recombine(gen->prog_origin);
        child->prog_target = prog_target->clone();
        break;
    case 1:
        child->prog_origin = prog_origin->clone();
        child->prog_target = prog_target->recombine(gen->prog_target);
        break;
    case 2:
        child->prog_origin = prog_origin->recombine(gen->prog_origin);
        child->prog_target = prog_target->recombine(gen->prog_target);
        break;
    }

    return child;
}


void GPGenerator::load(string filepath)
{
    std::ifstream file(filepath.c_str());
    string line;

    std::getline(file, line);
         
    for (int i = 0; i < 2; i++) {
        while ((line.compare("") == 0)
                || (line[0] == '\n')
                || (line[0] == '#')) {
            std::getline(file, line);
        }
        
        std::stringstream prog;
        while (line.compare("")
                && (line[0] != '\n')
                && (line[0] != '#')) {
            prog << line;
            if (!std::getline(file, line))
                break;
        }

        if (i == 0) {
            prog_origin->parse(prog.str());
        }
        else {
            prog_target->parse(prog.str());
        }
    }
}


void GPGenerator::simplify() {
    prog_origin->dyn_pruning();
    prog_target->dyn_pruning();    
}

}
