#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Nov 2011"


from syn.net import Net
from syn.core import *
import sys


if __name__ == '__main__':
    gen = create_gpgenerator()
    load_gpgen(gen, sys.argv[1])
    print_gpgen(gen)

    net = Net(sys.argv[2]).load_net()
    nodes = 2500
    edges = nodes * (net_edge_count(net) / net_node_count(net))
    max_cycles = edges * 100
    map_limit = 5.0
    bins = 10

    gpgen_run(gen, nodes, edges, max_cycles)
    simplify_gpgen(gen)
    print_gpgen(gen)