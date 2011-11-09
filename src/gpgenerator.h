/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include "network.h"
#include "gp/gptree.h"

namespace syn
{

class GPGenerator
{
public:
	GPGenerator();
	virtual ~GPGenerator();

	GPGenerator* clone();

	Net* run(unsigned int node_count, unsigned int edge_count, unsigned int max_cycles);

	void print();

	GPGenerator* recombine(GPGenerator* gen);

	unsigned int get_cycle() {return cycle;}
	unsigned int get_edges() {return edges;}

private:
    gptree* prog_origin;
    gptree* prog_target;
    unsigned int edges;
    unsigned int cycle;
};

}
