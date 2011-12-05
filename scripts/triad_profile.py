#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Dec 2011"


from syn.core import *
from syn.net import Net
import sys


def triad(dbpath):
    net = Net(dbpath)
    syn_net = net.load_net()
    net_triad_profile(syn_net)


if __name__ == '__main__':
    triad(sys.argv[1])
