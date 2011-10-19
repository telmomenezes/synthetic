#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


import random
from syn.core import *
from syn.drmap import drmap_distance


class Evo:
    def __init__(self, targ_net, mrate=0.05, rrate=0.05, pop=25):
        self.targ_net = targ_net
        self.mrate = mrate
        self.rrate = rrate
        self.pop = pop
        self.population = []

        self.syn_net = targ_net.load_net()
        self.nodes = net_node_count(self.syn_net)
        self.edges = net_edge_count(self.syn_net)
        self.nodes = 5000
        self.edges = 50000
        self.max_cycles = self.edges * 10

    def __del__(self):
        for i in range(self.pop):
            destroy_gpgenerator(self.population[i])
        destroy_net(self.syn_net)

    def run(self):
        print 'Evolving gpgenerator'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Population:', self.pop
        print 'Mutation rate:', self.mrate
        print 'Recombination rate:', self.rrate

        # init population
        self.population = []
        self.fitness = []
        for i in range(self.pop):
            gen = create_gpgenerator()
            self.population.append(gen)
            self.fitness.append(0)

        print 'Population initialized.'

        cycle = 0
        best_fit = 9999999
        # evolutionary loop
        while True:
            # eval fitness
            for i in range(self.pop):
                gen = self.population[i]
                net = gpgen_run(gen, self.nodes, self.edges, self.max_cycles)
                fit = drmap_distance(self.syn_net, net)
                self.fitness[i] = fit
                print fit
                if fit < best_fit:
                    best_fit = fit
                    print_gpgen(self.population[i])
                destroy_net(net)

            print 'Generation %d => best fitness: %f' % (cycle, best_fit)
            cycle += 1

            # next generation
            newgen = []
            
            for i in range(self.pop):
                parent1 = -1
                for i in range(3):
                    parent1 = random.randint(0, self.pop - 1)
                    if (parent1 < 0) or (self.fitness[i] < self.fitness[parent1]):
                        parent1 = i

                child = None
                # recombine or clone
                if random.uniform(0, 1) < self.rrate:
                    parent2 = -1
                    for i in range(3):
                        parent2 = random.randint(0, self.pop - 1)
                        if (parent2 < 0) or (self.fitness[i] < self.fitness[parent2]):
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
