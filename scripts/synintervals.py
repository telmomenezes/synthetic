#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.net import Net
import sys


def syn_intervals(dbpath, count, min_ts=0):
    net = Net(dbpath, True)
    net.divide_in_intervals(int(count), int(min_ts))


if __name__ == '__main__':
    syn_intervals(*sys.argv[1:])
