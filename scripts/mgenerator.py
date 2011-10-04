#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *
from syn.drmap import draw_drmap, drmap_distance
import sys


if __name__ == '__main__':
    gen = create_generator(1)
    #generator_init_random(gen) 
    generator_set_follow(gen, 0, 0, 1.0)
    generator_set_rfollow(gen, 0, 0, 0.1)
    generator_set_random(gen, 0, 0, 0.001)
    generator_set_link(gen, 0, 0, 0.1)
    generator_set_stop(gen, 0, 0.1)
    generator_set_weight(gen, 0, 1)

    gen2 = create_generator(1)
    generator_init_random(gen2) 

    node_count = 1000
    edge_count = 10000
    max_cycles = 10000
    max_walk_length = 100
    net1 = generate_network(gen, node_count, edge_count, max_cycles, max_walk_length)
    net2 = generate_network(gen2, node_count, edge_count, max_cycles, max_walk_length)

    #draw_drmap(net, 'drmap.png')
    print drmap_distance(net1, net2)
