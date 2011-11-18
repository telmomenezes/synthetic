/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once


#include <string>
#include "network.h"
#include "gp/gptree.h"


using std::string;


namespace syn
{

class GPGenerator
{
public:
	GPGenerator();
	virtual ~GPGenerator();

	GPGenerator* clone();

	Net* run(unsigned int node_count, unsigned int edge_count, unsigned int max_cycles);

	void write(string to_string);

	GPGenerator* recombine(GPGenerator* gen);

	unsigned int get_cycle() {return cycle;}
	unsigned int get_edges() {return edges;}

	void load(string filepath);

	void simplify();
	
private:
    GPTree* prog_origin;
    GPTree* prog_target;
    unsigned int edges;
    unsigned int cycle;
};

}
