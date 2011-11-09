#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.core import *
import sys
from PIL import Image, ImageDraw


def draw_drmap(syn_net, img_file, bins=50, img_side=500, limit=7.0):
    # TODO: use precomputed values
    compute_pageranks(syn_net)

    bin_side = img_side / bins

    # colors
    grid_color = (255, 255, 0)

    drmap = get_drmap_with_limits(syn_net, bins, -limit, limit, -limit, limit)
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

    im.save(img_file)


def drmap_distance(net1, net2, bins=50):
    # TODO: use precomputed values
    compute_pageranks(net1)
    compute_pageranks(net2)

    drmap1 = get_drmap_with_limits(net1, bins, -7.0, 7.0, -7.0, 7.0)
    drmap_log_scale(drmap1)
    drmap_normalize(drmap1)

    drmap2 = get_drmap_with_limits(net2, bins, -7.0, 7.0, -7.0, 7.0)
    drmap_log_scale(drmap2)
    drmap_normalize(drmap2)

    dist = drmap_emd_dist(drmap1, drmap2)

    destroy_drmap(drmap1)
    destroy_drmap(drmap2)

    return dist
