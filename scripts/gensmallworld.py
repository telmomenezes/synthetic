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


def gen_small_world(dbpath, nodes, edges):
    random.seed()
    
    net = Net(dbpath)
    node_table = {}
    cycle = 0
    for i in range(nodes):
        node_table[i] = 1
        net.add_node('%s' % i)
    cur_edges = nodes

    # initial connections
    for i in range(nodes):
        targ = random.randint(0, nodes - 1)
        net.add_edge(i, targ, cycle)

    # preferential attachement
    while cur_edges < edges:
        cycle += 1
        targ_point = random.randint(0, cur_edges - 1)
        acc = 0
        for i in range(nodes):
            acc += node_table[i]
            if acc > targ_point:
                targ = i
                orig = random.randint(0, nodes - 2)
                # prevent orig == targ
                if orig >= targ:
                    orig += 1
                net.add_edge(orig, targ, cycle)
                cur_edges += 1
                break


if __name__ == '__main__':
    nodes = int(sys.argv[2])
    edges = int(sys.argv[3])
    print 'Generating small world network with %d nodes and %d edges...' % (nodes, edges)
    gen_small_world(sys.argv[1], nodes, edges)
    print 'Done.'
