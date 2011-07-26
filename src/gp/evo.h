/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#pragma once

#include "network.h"
#include "mempool.h"
#include "gpmempool.h"


class Evo
{
public:
    unsigned int population_size;
    Net** population;

    unsigned int generations;
    unsigned int net_cycles;
    unsigned int tournament_size;
    float recomb_prob;
    float mut_prob;

    unsigned int interface_size;

    float best_fitness;

    
    Evo(unsigned int population_size_p,
            unsigned int node_link_chem_size,
            unsigned int node_chem_size,
            unsigned int node_state_size,
            unsigned int link_chem_size,
            unsigned int link_state_size,
            unsigned int interface_size_p);

    virtual ~Evo();

    void run();

    Net* select_parent();

    void print_pop_stats();

    /* canfigurabel behaviors */
    virtual float compute_fitness(Net *net){return 0;}
    virtual void after_net_cycle(Net *net){}
    virtual void after_generation(Net* bestnet){}

private:
    MemPool* _mempool;
    GPMemPool* _gpmempool;
};

