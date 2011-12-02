#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Nov 2011"


import random
import math
import sys
from syn.net import Net
from syn.evo import Evo
from syn.core import *
from syn.drmap import *


class ES(Evo):
    def compute_fitness(self, net):
        compute_pageranks(net)

        sim_drmap = get_drmap_with_limits(net, self.bins, -self.map_limit, self.map_limit, -self.map_limit, self.map_limit)
        drmap_log_scale(sim_drmap)
        drmap_normalize_max(sim_drmap)

        fit = drmap_emd_dist(self.targ_drmap, sim_drmap)

        destroy_drmap(sim_drmap)

        return fit

    def run(self):
        print 'Synthetic - Evolving GPGenerator [evolutionary strategy]'
        print 'Nodes:', self.nodes
        print 'Edges:', self.edges
        print 'Map limit:', self.map_limit
        print 'Bins:', self.bins

        # open log file
        log = open('log.csv', 'w')

        cycle = 0

        # create parent
        parent = create_gpgenerator()
        net = gpgen_run(parent, self.nodes, self.edges)
        best_fit = self.compute_fitness(net)

        write_gpgen(parent, 'best%d.prog' % cycle)
        draw_drmap(net, 'best%d.png' % cycle, bins=self.bins, limit=self.map_limit)
        n = Net('best%d.syn' % cycle)
        n.save(net)
        n.close()
        destroy_net(net)

        log.write('%d, %f, %f\n' % (cycle, best_fit, best_fit))
        print 'cycle: %d; fit: %f; best: %f [parent init]' % (cycle, best_fit, best_fit)

        cycle += 1
        
        # evolutionary loop
        while True:
            
            # create mutation
            chicken = create_gpgenerator()
            child = recombine_gpgens(parent, chicken)
            destroy_gpgenerator(chicken)

            # eval fitness
            net = gpgen_run(child, self.nodes, self.edges)
            fit = self.compute_fitness(net)

            #print i, fit
            if fit < best_fit:
                best_fit = fit
                destroy_gpgenerator(parent)
                parent = child
                write_gpgen(parent, 'best%d.prog' % cycle)
                draw_drmap(net, 'best%d.png' % cycle, bins=self.bins, limit=self.map_limit)
                n = Net('best%d.syn' % cycle)
                n.save(net)
                n.close()
            else:
                destroy_gpgenerator(child)

            destroy_net(net)

            log.write('%d, %f, %f\n' % (cycle, fit, best_fit))
            print 'cycle: %d; fit: %f; best: %f' % (cycle, fit, best_fit)
            
            cycle += 1