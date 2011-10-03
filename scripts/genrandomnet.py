#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.net import Net
import sys
import random


def gen_scale_free(dbpath, nodes, edges):
    random.seed()
    
    net = Net(dbpath)
    node_table = {}
    for i in range(nodes):
        node_table[i] = net.add_node('%s' % i)
    cur_edges = 0
    cycle = 0

    # connections
    while cur_edges < edges:
        orig = random.randint(0, nodes - 1)
        targ = random.randint(0, nodes - 2)
        if targ >= orig:
            orig += 1
        net.add_edge(node_table[orig], node_table[targ], cycle)
        cur_edges += 1
        cycle += 1

    net.divide_in_intervals(1)


if __name__ == '__main__':
    nodes = int(sys.argv[2])
    edges = int(sys.argv[3])
    print 'Generating random network with %d nodes and %d edges...' % (nodes, edges)
    gen_scale_free(sys.argv[1], nodes, edges)
    print 'Done.'
