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
    def compute_fitness(self, net):
        compute_pageranks(net)

        sim_drmap = get_drmap_with_limits(net, self.bins, -self.map_limit, self.map_limit, -self.map_limit, self.map_limit)
        drmap_log_scale(sim_drmap)
        drmap_normalize_total(sim_drmap)

        fit = drmap_emd_dist(self.targ_drmap, sim_drmap)

        destroy_drmap(sim_drmap)

        return fit

    def run(self, npars=5):
        print 'Synthetic - Evolving GPGenerator [evolutionary strategy]'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Map limit:', self.map_limit
        print 'Parents:', npars

        # open log file
        log = open('log.csv', 'w')

        cycle = 0

        # init population
        parents = []
        fitnesses = []
        best_fit = 0
        thr_fit = 0
        thr_pos = 0
        for i in range(npars):
            gen = create_gpgenerator()
            parents.append(gen)
            net = gpgen_run(gen, self.nodes, self.edges)
            fit = self.compute_fitness(net)
            fitnesses.append(fit)

            if i == 0:
                best_fit = fit
                thr_fit = fit
            else:
                if fit < best_fit:
                    best_fit = fit
                    write_gpgen(gen, 'best%d.prog' % cycle)
                    draw_drmap(net, 'best%d.png' % cycle, bins=self.bins, limit=self.map_limit)
                if fit > thr_fit:
                    thr_fit = fit
                    thr_pos = i
            destroy_net(net)

            log.write('%d, %f, %f\n' % (cycle, fit, best_fit))
            print 'cycle: %d; fit: %f; best: %f [parent init]' % (cycle, fit, best_fit)

            cycle += 1

        print 'Initial parents initialized.'
        print 'Fitness range: %f -> %f' % (best_fit, thr_fit)
        
        # evolutionary loop
        while True:
            mode = random.randint(0, 2)
            p1_index = random.randint(0, npars - 1)

            child = None
            # recombine or clone
            if mode < 2:
                p2_index = random.randint(0, npars - 1)
                child = recombine_gpgens(parents[p1_index], parents[p2_index])
            else:
                child = clone_gpgenerator(parents[p1_index])

            # mutate
            if mode > 0:
                chicken = create_gpgenerator()
                child2 = recombine_gpgens(child, chicken)
                destroy_gpgenerator(chicken)
                destroy_gpgenerator(child)
                child = child2

            # eval fitness
            net = gpgen_run(child, self.nodes, self.edges)
            fit = self.compute_fitness(net)

            #print i, fit
            if fit < best_fit:
                best_fit = fit
                write_gpgen(child, 'best%d.prog' % cycle)
                draw_drmap(net, 'best%d.png' % cycle, bins=self.bins, limit=self.map_limit)

            destroy_net(net)

            # check if new individual should replace one of the parents
            if fit < thr_fit:
                destroy_gpgenerator(parents[thr_pos])
                parents[thr_pos] = child
                fitnesses[thr_pos] = fit

                for i in range(npars):
                    if i == 0:
                        thr_fit = fitnesses[i]
                        thr_pos = 0
                    else:
                        if fitnesses[i] > thr_fit:
                            thr_fit = fitnesses[i]
                            thr_pos = i
            else:
                destroy_gpgenerator(child)

            log.write('%d, %f, %f\n' % (cycle, fit, best_fit))
            print 'cycle: %d; fit: %f; best: %f' % (cycle, fit, best_fit)
            
            cycle += 1