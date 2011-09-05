#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


"""
Copyright (C) 2011 Telmo Menezes.

This program is free software; you can redistribute it and/or modify
it under the terms of the version 2 of the GNU General Public License 
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
"""


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
