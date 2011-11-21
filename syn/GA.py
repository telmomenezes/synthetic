#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Nov 2011"


import random
import math
import sys
from syn.evo import Evo
from syn.core import *
from syn.drmap import *


class GA(Evo):
    def run(self, pop=100, mrate=0.3, rrate=0.7, tournament=3):
        print 'Synthetic - Evolving GPGenerator [genetic algorithm]'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Map limit:', self.map_limit
        print 'Population:', pop
        print 'Mutation rate:', mrate
        print 'Recombination rate:', rrate
        print 'Tournament:', tournament

        # init population
        self.population = []
        fitness = []
        for i in range(pop):
            gen = create_gpgenerator()
            self.population.append(gen)
            fitness.append(0)

        print 'Population initialized.'

        cycle = 0
        best_fit = 9999999
        # evolutionary loop
        while True:
            # eval fitness
            best_gen_fit = 9999999
            for i in range(pop):
                gen = self.population[i]
                net = gpgen_run(gen, self.nodes, self.edges)

                compute_pageranks(net)

                sim_drmap = get_drmap_with_limits(net, self.bins, -self.map_limit, self.map_limit, -self.map_limit, self.map_limit)
                drmap_log_scale(sim_drmap)
                drmap_normalize_total(sim_drmap)

                fit = drmap_emd_dist(self.targ_drmap, sim_drmap)

                destroy_drmap(sim_drmap)
                fitness[i] = fit
                print i, fit
                if fit < best_gen_fit:
                    best_gen_fit = fit
                if fit < best_fit:
                    best_fit = fit
                    write_gpgen(self.population[i], 'best%d.prog' % cycle)
                    draw_drmap(net, 'best%d.png' % cycle, bins=self.bins, limit=self.map_limit)
                destroy_net(net)

            print '%d, %f, %f' % (cycle, best_gen_fit, best_fit)
            cycle += 1

            # next generation
            newgen = []
            
            for i in range(pop):
                parent1 = -1
                for i in range(tournament):
                    parent1 = random.randint(0, pop - 1)
                    if (parent1 < 0) or (fitness[i] < fitness[parent1]):
                        parent1 = i

                child = None
                # recombine or clone
                if random.uniform(0, 1) < rrate:
                    parent2 = -1
                    for i in range(tournament):
                        parent2 = random.randint(0, pop - 1)
                        if (parent2 < 0) or (fitness[i] < fitness[parent2]):
                            parent2 = i

                    child = recombine_gpgens(self.population[parent1], self.population[parent2])
                else:
                    child = clone_gpgenerator(self.population[parent1])

                # mutate
                if random.uniform(0, 1) < mrate:
                    chicken = create_gpgenerator()
                    child2 = recombine_gpgens(child, chicken)
                    destroy_gpgenerator(chicken)
                    destroy_gpgenerator(child)
                    child = child2

                newgen.append(child)

            # replace generations
            for i in range(pop):
                destroy_gpgenerator(self.population[i])
            self.population = newgen