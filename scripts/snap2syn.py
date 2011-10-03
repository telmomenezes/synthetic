#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


import string
from syn.net import Net
import sys


def snap2syn(in_file, out_file):
    print 'SNAP to syn: %s -> %s' % (in_file, out_file)

    fin = open(in_file)
    net = Net(out_file)

    nodes = {}
   
    #nodes
    for line in fin:
        if (line[0] != '#'):
            strings = string.split(line)
            if len(strings) == 2:
                for s in strings:
                    if not nodes.has_key(s):
                        nodes[s] = net.add_node(s)

    # edges
    fin.seek(0)
    for line in fin:
        if (line[0] != '#'):
            strings = string.split(line)
            if len(strings) == 2:
                net.add_edge(nodes[strings[0]], nodes[strings[1]])

    fin.close()

    net.divide_in_intervals(1)


if __name__ == '__main__':
    snap2syn(sys.argv[1], sys.argv[2])
