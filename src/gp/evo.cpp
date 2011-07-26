/*
 * Copyright (C) 2010 Telmo Menezes.
 * telmo@telmomenezes.com
 */


#include "evo.h"
#include "probs.h"
#include <time.h>
#include <iostream>


using std::cout;
using std::endl;
using std::flush;


Evo::Evo(unsigned int population_size_p,
            unsigned int node_link_chem_size,
            unsigned int node_chem_size,
            unsigned int node_state_size,
            unsigned int link_chem_size,
            unsigned int link_state_size,
            unsigned int interface_size_p)
{
    interface_size = interface_size_p;

    // create mempools
    _mempool = new MemPool();
    _gpmempool = new GPMemPool(100000);

    population_size = population_size_p;
    population = (Net**)malloc(sizeof(Net*) * population_size);

    for (unsigned int i = 0; i < population_size; i++) {
        Net* net = new Net(node_link_chem_size,
                                node_chem_size,
                                node_state_size,
                                link_chem_size,
                                link_state_size,
                                interface_size,
                                _mempool,
                                _gpmempool);
        net->init_progs_random();
        population[i] = net;
    }

    // default values
    generations = 1000;
    net_cycles = 100;
    tournament_size = 5;
    recomb_prob = 0.5;
    mut_prob = 0.0;

    best_fitness = 0.0;
}


Evo::~Evo()
{
    for (unsigned int i = 0; i < population_size; i++) {
        delete(population[i]);
    }
    free(population);

    delete(_mempool);
    delete(_gpmempool);
}


Net* Evo::select_parent()
{
    unsigned int best_index;
    float best_fitness;

    for (unsigned int i = 0; i < tournament_size; i++)
    {
        unsigned int index = random() % population_size;
        if ((i == 0) || (population[index]->fitness > best_fitness)) {
            best_fitness = population[index]->fitness;
            best_index = index;
        }
    }

    return population[best_index];
}


void Evo::run()
{
    Net *net, *parent1, *parent2, *child, *childm, *bestnet;
    Net** new_population;
    float best_fitness;
    time_t start_time;
    double mean_geno_size;

    for(unsigned int i = 0; i < generations; i++) {

        start_time = time(NULL);
        mean_geno_size = 0;

        for (unsigned int j = 0; j < population_size; j++) {
            net = population[j];
        
            // run net
            net->runfor(net_cycles);
        
            mean_geno_size += net->genotype_size();

            // compute fitness
            compute_fitness(net);
            if ((j == 0) || (net->fitness > best_fitness)) {
                best_fitness = net->fitness;
                bestnet = net;
            }
        }

        mean_geno_size /= (double)population_size;

        cout << "Generation " << i << endl;
        cout << "Best fitness: " << best_fitness << endl;
        cout << "Best genotype size: " << bestnet->genotype_size() << endl;
        cout << "Mean genotype size: " << mean_geno_size << endl;
        cout << "Fitness computation time: " << (unsigned int)(time(NULL) - start_time)
            << " secs" << endl;

        print_pop_stats();

        start_time = time(NULL);

        // write best net so far
        bestnet->write();

        after_generation(bestnet);

        // generate new population
        new_population = (Net**)malloc(sizeof(Net*) * population_size);
        for (unsigned int j = 0; j < population_size; j++) {

            // select first parent
            parent1 = select_parent();

            if (test_prob(recomb_prob)) {
                // select second parent
                parent2 = select_parent();
                // recombine
                child = parent1->recombine_progs(parent2);
            }
            else
                child = parent1->clone_progs();
            // mutate
            if (test_prob(mut_prob)) {
                parent1 = new Net(child->node_link_chem_size,
                                            child->node_chem_size,
                                            child->node_state_size,
                                            child->link_chem_size,
                                            child->link_state_size,
                                            interface_size,
                                            _mempool,
                                            _gpmempool);
                parent1->init_progs_random();
                childm = child->recombine_progs(parent1);
                delete(child);
                delete(parent1);
                child = childm;
            }

            new_population[j] = child;
        }

        // remove old population
        for (unsigned int j = 0; j < population_size; j++)
            delete(population[j]);
        free(population);

        // assign new population
        population = new_population;

        cout << "Generate new population time: "
            << (unsigned int)(time(NULL) - start_time)
            << " secs" << endl;
        cout << flush;
    }
}


void Evo::print_pop_stats()
{
    float avg_nodes = 0;
    float avg_links = 0;
    float min_nodes, max_nodes, min_links, max_links;

    for (unsigned int i = 0; i < population_size; i++) {
        Net* net = population[i];
        if ((i == 0) || (net->node_count < min_nodes))
            min_nodes = net->node_count;
        if ((i == 0) || (net->node_count > max_nodes))
            max_nodes = net->node_count;
        if ((i == 0) || (net->link_count < min_links))
            min_links = net->link_count;
        if ((i == 0) || (net->link_count > max_links))
            max_links = net->link_count;
        avg_nodes += net->node_count;
        avg_links += net->link_count;
    }

    avg_nodes /= (float)population_size;
    avg_links /= (float)population_size;

    cout << "NODES "
        << min_nodes << " "
        << avg_nodes << " "
        << max_nodes
        << "; LINKS "
        << min_links << " "
        << avg_links << " "
        << max_links << endl;
}

