#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.net import Net
import sys


def synpr(dbpath):
    net = Net(dbpath)
    net.compute_page_ranks()


if __name__ == '__main__':
    synpr(sys.argv[1])
