#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Nov 2011"


import random
import math
from syn.core import *
from syn.drmap import *


class Zoo:
    def __init__(self, targ_net, mrate=0.3, rrate=0.7, pop=100, tournament=3):
        self.mrate = mrate
        self.rrate = rrate
        self.tournament = tournament
        self.pop = pop
        self.population = []
        self.archive = []

        self.nodes = 1000
        self.edges = 10000
        self.max_cycles = self.edges * 100

        self.bins = 10
        
        self.syn_net = targ_net.load_net()
        compute_pageranks(self.syn_net)
        self.targ_drmap = get_drmap_with_limits(self.syn_net, self.bins, -7.0, 7.0, -7.0, 7.0)
        drmap_log_scale(self.targ_drmap)
        drmap_normalize(self.targ_drmap)
        
        seed_random()

    def __del__(self):
        for i in range(self.pop):
            destroy_gpgenerator(self.population[i])
        destroy_net(self.syn_net)

    def eval_fitness(self, gen):
        net = gpgen_run(gen, self.nodes, self.edges, self.max_cycles)
        compute_pageranks(net)
        drmap = get_drmap_with_limits(net, self.bins, -7.0, 7.0, -7.0, 7.0)
        drmap_log_scale(drmap)
        drmap_normalize(drmap)

        # target
        d = drmap_emd_dist(drmap, self.targ_drmap)
        if d < self.best_fitness:
            self.best_fitness = d
            draw_drmap(net, '_best.png', bins=self.bins)
            print 'best fitness found: %f' % self.best_fitness

        # novelty
        dists = []

        for a in self.archive:
            d = drmap_emd_dist(drmap, a)
            if len(dists) < 5:
                dists.append(d)
                dists.sort()
            else:
                if d < dists[4]:
                    dists[4] = d
                    dists.sort()

        meandist = 0
        for d in dists:
            meandist += d

        meandist /= 5.0

        if (dists == []) or (meandist > 0.1):
            self.archive.append(drmap)
            print 'archived.'
            draw_drmap(net, 'arch%d.png' % len(self.archive), bins=self.bins)
        else:
            destroy_drmap(drmap)

        destroy_net(net)
        return meandist

    def run(self):
        print 'Evolving gpgenerator'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Population:', self.pop
        print 'Mutation rate:', self.mrate
        print 'Tournament:', self.tournament
        print 'Recombination rate:', self.rrate

        # init population
        self.best_fitness = 999999
        self.population = []
        self.fitness = []
        for i in range(self.pop):
            gen = create_gpgenerator()
            self.population.append(gen)
            self.fitness.append(0)

        print 'Population initialized.'

        cycle = 0
        # evolutionary loop
        while True:
            # eval fitness
            for i in range(self.pop):
                gen = self.population[i]
                fit = self.eval_fitness(gen)

                self.fitness[i] = fit
                print i, fit

            print 'Generation %d -> best fitness: %f' % (cycle, self.best_fitness)
            cycle += 1

            # next generation
            newgen = []
            
            for i in range(self.pop):
                parent1 = -1
                for i in range(self.tournament):
                    parent1 = random.randint(0, self.pop - 1)
                    if (parent1 < 0) or (self.fitness[i] > self.fitness[parent1]):
                        parent1 = i

                child = None
                # recombine or clone
                if random.uniform(0, 1) < self.rrate:
                    parent2 = -1
                    for i in range(self.tournament):
                        parent2 = random.randint(0, self.pop - 1)
                        if (parent2 < 0) or (self.fitness[i] > self.fitness[parent2]):
                            parent2 = i

                    child = recombine_gpgens(self.population[parent1], self.population[parent2])
                else:
                    child = clone_gpgenerator(self.population[parent1])

                # mutate
                if random.uniform(0, 1) < self.mrate:
                    chicken = create_gpgenerator()
                    child2 = recombine_gpgens(child, chicken)
                    destroy_gpgenerator(chicken)
                    destroy_gpgenerator(child)
                    child = child2

                newgen.append(child)

            # replace generations
            for i in range(self.pop):
                destroy_gpgenerator(self.population[i])
            self.population = newgen
