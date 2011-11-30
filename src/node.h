/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "edge.h"

namespace syn
{

class Node {
public:
    Node(unsigned int type, unsigned int id);
    virtual ~Node();

    int add_edge(Node* target, unsigned long timestamp);
    int edge_exists(Node* target);

    unsigned int get_id(){return id;}
    unsigned int get_in_degree(){return in_degree;}
    unsigned int get_out_degree(){return out_degree;}
    double get_pr_in(){return pr_in;}
    double get_pr_out(){return pr_out;}

    // node list
    Node* next;

    unsigned int type;
    unsigned int id;
    Edge* targets;
    Edge* origins;
    unsigned int in_degree;
    unsigned int out_degree;
    int birth;
    
    // pageranks
    double pr_in;
    double pr_in_last;
    double pr_out;
    double pr_out_last;

    // for generators
    double genweight;
};

}