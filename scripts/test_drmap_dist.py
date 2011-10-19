#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Oct 2011"


from syn.core import *

map1 = create_drmap(50, -7.0, 7.0, -7.0, 7.0)
map2 = create_drmap(50, -7.0, 7.0, -7.0, 7.0)

destroy_drmap(map1)
destroy_drmap(map2)
