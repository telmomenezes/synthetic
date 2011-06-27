#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jun 2011"


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


import sys
from syn.net import Net


def gendrmap(netfile):
    bins = 50
    steps = 100
    cur_ts = 0

    net = Net(netfile)

    interval = (net.max_ts - net.min_ts) / steps
    cur_ts = net.min_ts + interval

    for step in range(steps):
        min_ts = net.min_ts
        max_ts = cur_ts
        syn_net = net.load_net(min_ts, max_ts)
        compute_evc(syn_net)

        node = net_first_node(syn_net)
        while node != 0:
            
            node = node_next_node(node)

        destroy_net(syn_net)

        cur_ts += interval

    map = DRMap(net=net, bins=bins, steps=1, data=map_data, min_hor=-7.0, max_hor=7.0,
        min_ver=-7.0, max_ver=7.0)
    map.save()

    Network.objects.filter(id=net_id).update(drmap=map.id)

    return HttpResponseRedirect('/net/%s' % net_id)


def drmap_positions(dbpath, outpath):

    net = Net(outpath)
    f = open(outpath, 'w')
    net.create_db()

    conn = sqlite3.connect(dbpath)
    cur = conn.cursor()
    
    nodes = {}
    cur.execute("SELECT id FROM authors")
    for row in cur:
        nodes[row[0]] = net.add_node()

    cur.execute("SELECT orig_id, targ_id, timestamp FROM author_citations")
    for row in cur:
        net.add_edge(nodes[row[0]], nodes[row[1]], row[2])

    cur.close()
    conn.close()

    print('Done.')


    


if __name__ == '__main__':
 
