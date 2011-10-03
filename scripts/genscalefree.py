#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.net import Net
import sys
import random


def gen_scale_free(dbpath, nodes, edges):
    alpha = 1
    beta = 1
    random.seed()
    
    net = Net(dbpath)
    node_table = {}
    node_inacc = {}
    node_outacc = {}
    cycle = 0
    for i in range(nodes):
        node_inacc[i] = (i * alpha) + alpha
        node_outacc[i] = (i * beta) + beta
        node_table[i] = net.add_node('%s' % i)
    cur_edges = 0

    # initial connections
    for i in range(1):
        orig = random.randint(0, nodes - 1)
        targ = random.randint(0, nodes - 2)
        if targ >= orig:
            orig += 1
        net.add_edge(node_table[orig], node_table[targ], cycle)
        for j in range(orig, nodes):
            node_outacc[j] += 1
        for j in range(targ, nodes):
            node_inacc[j] += 1
        cur_edges += 1

    # preferential attachement
    while cur_edges < edges:
        cycle += 1
        orig_point = random.randint(0, node_outacc[nodes - 1] - 1)
        targ_point = random.randint(0, node_inacc[nodes - 1] - 1)
        orig = -1
        targ = -1
        for i in range(nodes):
            if (targ < 0) and (node_inacc[i] > targ_point):
                targ = i
                for j in range(targ, nodes):
                    node_inacc[j] += 1
            if (orig < 0) and (node_outacc[i] > orig_point):
                orig = i
                for j in range(orig, nodes):
                    node_outacc[j] += 1
            
            if (orig >= 0) and (targ >= 0):
                break

        if orig != targ:
            net.add_edge(node_table[orig], node_table[targ], cycle)
            cur_edges += 1


if __name__ == '__main__':
    nodes = int(sys.argv[2])
    edges = int(sys.argv[3])
    print 'Generating scale-free network with %d nodes and %d edges...' % (nodes, edges)
    gen_scale_free(sys.argv[1], nodes, edges)
    print 'Done.'
