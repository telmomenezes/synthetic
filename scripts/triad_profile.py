#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Dec 2011"


from syn.core import *
from syn.net import Net
import sys
import math


def triad(dbpath):
    rcount = 100

    seed_random()

    net = Net(dbpath)
    syn_net = net.load_net()

    print 'Node count: ', net_node_count(syn_net)
    print 'Edge count: ', net_edge_count(syn_net)

    p = net_triad_profile(syn_net)

    print p

    # generate profiles of random nets
    rprofs = []
    for i in range(rcount):
        #print '#%d' % i
        syn_rand = create_net()
        gen_degree_seq(syn_rand, syn_net)
        rprofs.append(net_triad_profile(syn_rand))

    # compute profile average
    avgs = []
    for i in range(13):
        avgs.append(0)
    for rp in rprofs:
        for i in range(13):
            avgs[i] += rp[i]
    for i in range(13):
        avgs[i] = float(avgs[i]) / float(rcount)

    # compute profile standard deviation
    sds = []
    for i in range(13):
        sds.append(0)
    for rp in rprofs:
        for i in range(13):
            delta = avgs[i] - rp[i]
            sds[i] += (delta * delta)
    for i in range(13):
        sds[i] = float(sds[i]) / float(rcount)
        sds[i] = math.sqrt(sds[i])

    # compute Z score
    sp = []
    for i in range(13):
        if sds[i] != 0:
            sp.append((float(p[i]) - avgs[i]) / sds[i])
        else:
            sp.append(0)

    # compute SP
    denom = 0
    for i in range(13):
        denom += (sp[i] * sp[i])
    denom = math.sqrt(denom)

    for i in range(13):
        sp[i] = sp[i] / denom

    print sp


if __name__ == '__main__':
    triad(sys.argv[1])
