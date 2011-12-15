#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Dec 2011"


from syn.core import net_node_count, net_edge_count
from syn.net import Net
import sys


if __name__ == '__main__':
    net = Net(sys.argv[1])
    syn_net = net.load_net()
    nodes = net_node_count(syn_net)
    edges = net_edge_count(syn_net)
    print 'Nodes: ', nodes
    print 'Edges: ', edges