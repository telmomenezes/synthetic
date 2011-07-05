#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


"""
Copyright (C) 2011 Telmo Menezes.

This program is free software; you can redistribute it and/or modify
it under the terms of the version 2 of the GNU General Public License 
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
"""


import string
from syn.net import Net
import sys


def snap2syn(in_file, out_file):
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


if __name__ == '__main__':
    snap2syn(sys.argv[1], sys.argv[2])
