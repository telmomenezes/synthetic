#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.net import Net
import sys
import random
import math


def test_prob(p):
    r = random.uniform(0, 1)
    return (r < p)


def truncate(x):
    if x < 0:
        return 0
    if x > 1:
        return 1
    return x


def gen_probs(dbpath, nodes, edges):
    random.seed()
    
    net = Net(dbpath)
    node_table = []
    for i in range(nodes):
        node_table.append(net.add_node('%s' % i))

    cur_edges = 0
    cycle = 0

    # connections
    while cur_edges < edges:
        orig = random.randint(0, nodes - 1)
        targ = random.randint(0, nodes - 2)
        if targ >= orig:
            targ += 1
        
        xo = float(orig) / float(nodes)
        xt = float(targ) / float(nodes)

        p = random.uniform(0, 1)
        if xo < 0.3:
            p = 1
        elif xt > 0.7:
            p = 1
        if xo > 0.7:
            p = 0
        elif xt < 0.3:
            p = 0
        p = truncate(p)
        print p, cur_edges

        if test_prob(p):
            net.add_edge(node_table[orig], node_table[targ], cycle)
            cur_edges += 1
        cycle += 1

    net.divide_in_intervals(1)


if __name__ == '__main__':
    nodes = int(sys.argv[2])
    edges = int(sys.argv[3])
    print 'Probbilistic Generator: Generating network with %d nodes and %d edges...' % (nodes, edges)
    gen_probs(sys.argv[1], nodes, edges)
    print 'Done.'
