#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *
import sys


if __name__ == '__main__':
    gen = create_generator(3)

    node_count = 1000
    edge_count = 10000
    max_cycles = 1000
    max_walk_length = 100
    net = generate_networks(gen, node_count, edge_count, max_cycles, max_walk_length)
