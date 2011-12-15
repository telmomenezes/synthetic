#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Dec 2011"


import sys
from syn.net import Net
from syn.core import *


def degree_freqs(netfile, in_path, out_path):
    net = Net(netfile)

    in_freqs = {}
    out_freqs = {}

    syn_net = net.load_net()

    node = net_first_node(syn_net)
    while node != 0:
        in_degree = node_in_degree(node)
        out_degree = node_out_degree(node)

        if in_degree in in_freqs:
            in_freqs[in_degree] += 1
        else:
            in_freqs[in_degree] = 1

        if out_degree in out_freqs:
            out_freqs[out_degree] += 1
        else:
            out_freqs[out_degree] = 1

        node = node_next_node(node)

    destroy_net(syn_net)

    f = open(in_path, 'w')
    f.write('degree, freq\n')
    for degree in in_freqs.keys():
        f.write('%d, %d\n' % (degree, in_freqs[degree]))
    f.close()

    f = open(out_path, 'w')
    f.write('degree, freq\n')
    for degree in out_freqs.keys():
        f.write('%d, %d\n' % (degree, out_freqs[degree]))
    f.close()

    print 'done.'


if __name__ == '__main__':
    degree_freqs(*sys.argv[1:])
