#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


import random
import math
import sys
from syn.core import *
from syn.drmap import *


class Evo:
    def __init__(self, targ_net, max_effort=5, map_limit=7.0, bins=10):
        self.targ_net = targ_net
        self.population = []

        self.syn_net = targ_net.load_net()
        self.nodes = net_node_count(self.syn_net)
        self.edges = net_edge_count(self.syn_net)
        
        # edge to node ratio
        en_ratio = float(net_edge_count(self.syn_net)) / float(net_node_count(self.syn_net))
        
        # max effort
        me = float(max_effort * 1000000) 

        # total effort
        e = en_ratio * (self.nodes * self.nodes)

        # check if max effort was reached
        if e > me:
            n = math.sqrt(me / en_ratio)
            self.nodes = int(n)
            self.edges = int(n * en_ratio)
        self.map_limit = map_limit
        self.bins = bins

        seed_random()

        compute_pageranks(self.syn_net)
        self.targ_drmap = get_drmap_with_limits(self.syn_net, self.bins, -self.map_limit, self.map_limit, -self.map_limit, self.map_limit)
        drmap_log_scale(self.targ_drmap)
        drmap_normalize_max(self.targ_drmap)

        draw_drmap(self.syn_net, 'target.png', bins=self.bins, limit=self.map_limit)

    def __del__(self):
        destroy_drmap(self.targ_drmap)
        for p in self.population:
            destroy_gpgenerator(p)
        destroy_net(self.syn_net)