#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Nov 2011"


from syn.net import Net
from syn.zoo import Zoo
import sys


if __name__ == '__main__':
    net = Net(sys.argv[1])
    zoo = Zoo(net)
    zoo.run()