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


from syn.core import *
from syn.net import Net
import sys
from PIL import Image, ImageDraw



def syndrmap(dbpath, img_file):
    img_side = 500

    net = Net(dbpath)
    syn_net = net.load_net()

    # TODO: use precomputed values
    compute_pageranks(syn_net)
    
    bins = 50

    bin_side = img_side / bins

    drmap = get_drmap_with_limits(syn_net, bins, -7.0, 7.0, -7.0, 7.0)
    drmap_log_scale(drmap)
    drmap_normalize(drmap)

    im = Image.new('RGBA', (img_side, img_side), (0, 0, 0, 0))
    draw = ImageDraw.Draw(im)

    for x in range(bins):
        for y in range(bins):
            val = drmap_get_value(drmap, x, y)
            color = (0, 0, 255)
            if val > 0.0:
                color = (int(255.0 * val), 0, 0)
            draw.rectangle((x * bin_side, img_side - (y * bin_side), (x + 1) * bin_side, img_side - ((y + 1) * bin_side)), fill=color)

    destroy_net(syn_net)
    destroy_drmap(drmap)

    # draw grid
    draw.line(((img_side / 2, 0), (img_side / 2, img_side)), fill=(0, 200, 0))
    draw.line(((0, img_side / 2), (img_side, img_side / 2)), fill=(0, 200, 0))

    im.save(img_file)


if __name__ == '__main__':
    syndrmap(sys.argv[1], sys.argv[2])
