#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Dec 2011"


import string
from syn.net import Net
import sys


def syn2mfinder(in_file, out_file):
    print 'syn to mfinder: %s -> %s' % (in_file, out_file)

    net = Net(in_file)
    fout = open(out_file, 'w')

    edges = {}

    net.cur.execute("SELECT orig, targ FROM edge")
    for row in net.cur:
        orig = row[0]
        targ = row[1]

        duplicate = False
        if orig in edges:
            if targ in edges[orig]:
                duplicate = True
            else:
                edges[orig].append(targ)
        else:
            edges[orig] = [targ,]

        if not duplicate:
            fout.write('%d %d 1\n' % (orig, targ))

    fout.close()

    print 'Done.'


if __name__ == '__main__':
    syn2mfinder(sys.argv[1], sys.argv[2])