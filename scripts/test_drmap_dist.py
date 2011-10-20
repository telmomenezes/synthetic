#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *

map1 = create_drmap(3, -7.0, 7.0, -7.0, 7.0)
map2 = create_drmap(3, -7.0, 7.0, -7.0, 7.0)

drmap_set_value(map1, 0, 0, 0.5)
drmap_set_value(map1, 2, 2, 0.5)
drmap_set_value(map2, 0, 0, 1.0)
#drmap_set_value(map2, 2, 0, 1)

dist = drmap_emd_dist(map1, map2)

drmap_print(map1)
drmap_print(map2)

print 'dist:', dist

destroy_drmap(map1)
destroy_drmap(map2)
