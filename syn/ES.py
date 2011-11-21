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


class ES(Evo):
    def run(self, pop=10):
        print 'Synthetic - Evolving GPGenerator [evolutionary strategy]'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Map limit:', self.map_limit
        print 'Population:', pop

        # init population
        self.population = []
        for i in range(pop):
            gen = create_gpgenerator()
            self.population.append(gen)

        print 'Population initialized.'

        cycle = 0
        
        # evolutionary loop
        best_fit = 9999999
        while True:
            best_gen_fit = 9999999
            best_gen = None
            # eval fitness
            for i in range(pop):
                gen = self.population[i]
                net = gpgen_run(gen, self.nodes, self.edges)

                compute_pageranks(net)

                sim_drmap = get_drmap_with_limits(net, self.bins, -self.map_limit, self.map_limit, -self.map_limit, self.map_limit)
                drmap_log_scale(sim_drmap)
                drmap_normalize_total(sim_drmap)

                fit = drmap_emd_dist(self.targ_drmap, sim_drmap)

                destroy_drmap(sim_drmap)
                #print i, fit
                if fit < best_gen_fit:
                    best_gen_fit = fit
                    best_gen = self.population[i]
                
                if fit < best_fit:
                    best_fit = fit
                    write_gpgen(self.population[i], 'best%d.prog' % cycle)
                    draw_drmap(net, 'best%d.png' % cycle, bins=self.bins, limit=self.map_limit)

                destroy_net(net)

            print '%d, %f, %f' % (cycle, best_gen_fit, best_fit)
            cycle += 1

            # next generation
            newgen = []
            
            newgen.append(clone_gpgenerator(best_gen))

            for i in range(1, pop):
                clone = clone_gpgenerator(best_gen)
                chicken = create_gpgenerator()
                child = recombine_gpgens(clone, chicken)
                destroy_gpgenerator(chicken)
                destroy_gpgenerator(clone)
                newgen.append(child)

            # replace generations
            for i in range(pop):
                destroy_gpgenerator(self.population[i])
            self.population = newgen
