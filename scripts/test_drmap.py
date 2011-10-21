#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *
from syn.drmap import *

net = create_net()
nodes = []

n = 10000

for i in range(n):
    node = add_node(net, 0)
    nodes.append(node)

for i in range(1, n):
    add_edge_to_net(net, nodes[i], nodes[0], 0)

for i in range(0, n):
    add_edge_to_net(net, nodes[n - 1], nodes[i], 0)

for i in range(10, 200):
    for j in range(10, 200):
        if i != j:
            if (i % 2) == 0:
                add_edge_to_net(net, nodes[i], nodes[j], 0)
            if (j % 3) == 0:
                add_edge_to_net(net, nodes[j], nodes[i], 0)

#for i in range(0, 249):
#    add_edge_to_net(net, nodes[i], nodes[i+1], 0)

draw_drmap(net, 'test.png')
print node_pr_in(nodes[0]), node_pr_out(nodes[0])
print node_pr_in(nodes[1])
print node_pr_in(nodes[2])
