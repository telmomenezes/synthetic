#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


import random
from syn.core import *
from syn.drmap import *


node_count = 1000
edge_count = 10000

net = create_net()
nodes = []
weights = []
indegree = []
outdegree = []

for i in range(node_count):
    node = add_node(net, 0)
    nodes.append(node)
    weights.append(0)
    indegree.append(0)
    outdegree.append(0)

for i in range(edge_count):
    if (i % 1000) == 0:
        print i, 'edges'
    # find origin
    total_weight = 0.0
    for orig_pos in range(node_count):
        orig = nodes[orig_pos]
        weight = orig_pos
        total_weight += weight
        weights[orig_pos] = weight
    targ_weight = random.uniform(0, 1) * total_weight

    orig_pos = 0
    total_weight = 0.0
    while total_weight < targ_weight:
        total_weight += weights[orig_pos]
        orig_pos += 1

    orig_pos -= 1
    orig = nodes[orig_pos]

    # find target
    total_weight = 0.0
    for targ_pos in range(node_count):
        targ = nodes[targ_pos]
        weight = 1 + indegree[targ_pos]
        total_weight += weight
        weights[targ_pos] = weight
    targ_weight = random.uniform(0, 1) * total_weight

    targ_pos = 0
    total_weight = 0
    while total_weight < targ_weight:
        total_weight += weights[targ_pos]
        targ_pos += 1
    
    targ_pos -= 1
    targ = nodes[targ_pos]

    # create edge
    add_edge_to_net(net, orig, targ, 0)
    indegree[targ_pos] += 1
    outdegree[orig_pos] += 1


draw_drmap(net, 'test.png')
#print node_pr_in(nodes[0]), node_pr_out(nodes[0])
