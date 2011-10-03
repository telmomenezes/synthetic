#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.net import Net
import sys
import random


def gen_random_walks(dbpath, nodes, edges):
    random.seed()
    
    p_stop = 0.01
    p_rand = 0.01
    
    net = Net(dbpath)
    node_table = []
    inlinks = []
    outlinks = []
    cycle = 0
    for i in range(nodes):
        node_table.append(net.add_node('%s' % i))
        inlinks.append([])
        outlinks.append([])
    cur_edges = 0

    # random walks
    orig = random.randint(0, nodes - 1)
    while cur_edges < edges:
        cycle += 1

        cur_node = orig
        # walk
        walking = True
        while walking:
            indegree = len(inlinks[cur_node])
            outdegree = len(outlinks[cur_node])
            degree = indegree + outdegree
            if (degree == 0) or (random.random() < p_rand):
                cur_node = random.randint(0, nodes - 1)
            else:
                n = random.randint(0, degree - 1)
                if n < indegree:
                    cur_node = inlinks[cur_node][n]
                else:
                    cur_node = outlinks[cur_node][n - indegree]

            if random.random() < p_stop:
                walking = False

        targ = cur_node

        if (orig != targ) and (targ not in outlinks[orig]):
            net.add_edge(node_table[orig], node_table[targ], cycle)
            inlinks[targ].append(orig)
            outlinks[orig].append(targ)
            cur_edges += 1

        orig = targ

    net.divide_in_intervals(1)


if __name__ == '__main__':
    nodes = int(sys.argv[2])
    edges = int(sys.argv[3])
    print 'Generating network by random walks with %d nodes and %d edges...' % (nodes, edges)
    gen_random_walks(sys.argv[1], nodes, edges)
    print 'Done.'
