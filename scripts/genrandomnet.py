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
