#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Nov 2011"


import string
from syn.net import Net
import sys


def gml2syn(in_file, out_file):
    print 'gml to syn: %s -> %s' % (in_file, out_file)

    fin = open(in_file)
    net = Net(out_file)

    nodes = {}
   
    inedge = False
    orig_node = None
    targ_node = None
    for l in fin:
        line = l.strip()
        if inedge:
            if line == ']':
                net.add_edge(orig_node, targ_node)
                inedge = False
            else:
                parts = line.split()
                if len(parts) >= 2:
                    param = parts[0].strip()
                    val = parts[1].strip()
                    if param == 'source':
                        if val in nodes:
                            orig_node = nodes[val]
                        else:
                            orig_node = net.add_node(val)
                            nodes[val] = orig_node
                    elif param == 'target':
                        if val in nodes:
                            targ_node = nodes[val]
                        else:
                            targ_node = net.add_node(val)
                            nodes[val] = targ_node

        else:
            if line == 'edge':
                inedge = True

    fin.close()

    net.divide_in_intervals(1)


if __name__ == '__main__':
    gml2syn(sys.argv[1], sys.argv[2])