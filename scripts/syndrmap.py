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

    ints = net.get_number_intervals()

    for i in range(ints):
        print 'Generating drmap for interval %d of %d.' % (i, ints)
        syn_net = net.load_interval_net(i)

        # TODO: use precomputed values
        compute_pageranks(syn_net)
    
        bins = 50

        bin_side = img_side / bins

        # colors
        grid_color = (255, 255, 0)

        drmap = get_drmap_with_limits(syn_net, bins, -7.0, 7.0, -7.0, 7.0)
        drmap_log_scale(drmap)
        drmap_normalize(drmap)

        im = Image.new('RGBA', (img_side, img_side), (0, 0, 0, 0))
        draw = ImageDraw.Draw(im)

        for x in range(bins):
            for y in range(bins):
                val = drmap_get_value(drmap, x, y)
                color = (0, 150, 200)
                if val > 0.0:
                    color = (int(255.0 * val), 0, 0)
                draw.rectangle((x * bin_side, img_side - (y * bin_side), (x + 1) * bin_side, img_side - ((y + 1) * bin_side)), fill=color)

        destroy_net(syn_net)
        destroy_drmap(drmap)

        # draw grid
        center = img_side / 2.0
        draw.line(((center, 0), (center, img_side)), fill=grid_color)
        draw.line(((0, center), (img_side, center)), fill=grid_color)

        for j in range(6):
            y = center - ((center / 7.0) * (j + 1))
            draw.line(((center - 5, y), (center + 5, y)), fill=grid_color)
            draw.text((center + 10, y - 5), '%d' % (j + 1), fill=grid_color)

            y = center + ((center / 7.0) * (j + 1))
            draw.line(((center - 5, y), (center + 5, y)), fill=grid_color)
            draw.text((center + 10, y - 5), '-%d' % (j + 1), fill=grid_color)

            x = center - ((center / 7.0) * (j + 1))
            draw.line(((x, center - 5), (x, center + 5)), fill=grid_color)
            draw.text((x - 5, center + 10), '-%d' % (j + 1), fill=grid_color)

            x = center + ((center / 7.0) * (j + 1))
            draw.line(((x, center - 5), (x, center + 5)), fill=grid_color)
            draw.text((x - 2, center + 10), '%d' % (j + 1), fill=grid_color)

        draw.text((center - 10, 5), 'R', fill=grid_color)
        draw.text((img_side - 15, center - 15), 'D', fill=grid_color)

        if ints == 1:
            im.save('%s.png' % (img_file,))
        else:
            im.save('%s%d.png' % (img_file, i))


if __name__ == '__main__':
    syndrmap(sys.argv[1], sys.argv[2])
