#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *

map1 = create_drmap(10, -7.0, 7.0, -7.0, 7.0)
map2 = create_drmap(10, -7.0, 7.0, -7.0, 7.0)

drmap_set_value(map1, 0, 0, 0.25)
drmap_set_value(map1, 1, 1, 0.25)
drmap_set_value(map1, 2, 2, 0.25)
drmap_set_value(map1, 3, 3, 0.25)
#drmap_set_value(map2, 0, 0, 0.25)
#drmap_set_value(map2, 1, 1, 0.25)
#drmap_set_value(map2, 2, 2, 0.25)
#drmap_set_value(map2, 4, 4, 0.25)

dist = drmap_emd_dist(map1, map2)

drmap_print(map1)
print '\n'
drmap_print(map2)

print 'dist:', dist

destroy_drmap(map1)
destroy_drmap(map2)
