#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *
from syn.drmap import draw_drmap
import sys


if __name__ == '__main__':
    gen = create_generator(10)
    generator_init_random(gen) 

    node_count = 10000
    edge_count = 100000
    max_cycles = 100000
    max_walk_length = 1000
    net = generate_network(gen, node_count, edge_count, max_cycles, max_walk_length)
    draw_drmap(net, 'drmap.png')
