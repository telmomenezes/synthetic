#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.net import Net
from syn.ES import ES
from syn.GA import GA
import sys


if __name__ == '__main__':
    net = Net(sys.argv[1])
    #evo = ES(net)
    evo = GA(net)
    evo.run()
