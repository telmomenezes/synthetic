#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


import random
from syn.core import *
from syn.drmap import drmap_distance


class Evo:
    def __init__(self, targ_net, mrate=0.01, pop=10):
        self.targ_net = targ_net
        self.mrate = mrate
        self.pop = pop
        self.population = []

        self.syn_net = targ_net.load_net()
        self.nodes = net_node_count(self.syn_net)
        self.edges = net_edge_count(self.syn_net)
        #self.nodes = 500
        #self.edges = 10000
        self.max_cycles = 100
        self.max_walk_length = 100

    def __del__(self):
        for i in range(self.pop):
            destroy_generator(self.population[i])
        destroy_net(self.syn_net)

    def run(self):
        print 'Evolving mgenerator'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Population:', self.pop
        print 'Mutation rate:', self.mrate

        # init population
        self.population = []
        self.fitness = []
        for i in range(self.pop):
            gen = create_generator(5)
            generator_init_random(gen)
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
                net = generate_network(gen, self.nodes, self.edges, self.max_cycles, self.max_walk_length)
                fit = drmap_distance(self.syn_net, net)
                self.fitness[i] = fit
                print fit
                if fit < best_fit:
                    best_fit = fit
                destroy_net(net)

            print 'Generation %d => best fitness: %f' % (cycle, best_fit)
            cycle += 1

            # next generation
            newgen = []
            for i in range(self.pop):
                parent = random.randint(0, self.pop - 1)
                pos2 = random.randint(0, self.pop - 1)

                if self.fitness[pos2] < self.fitness[parent]:
                    parent = pos2

                child = clone_generator(self.population[parent])
                generator_mutate(child, self.mrate)
                newgen.append(child)

            # replace generations
            for i in range(self.pop):
                destroy_generator(self.population[i])
            self.population = newgen
