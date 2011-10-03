#!/usr/bin/env python
# encoding: utf-8

__author__ = "Telmo Menezes (telmo@telmomenezes.com)"
__date__ = "Jul 2011"


from syn.core import *
from syn.net import Net
from syn.dramp import draw_drmap
import sys
from PIL import Image, ImageDraw


def syndrmap(dbpath, img_file):
    net = Net(dbpath)

    ints = net.get_number_intervals()

    for i in range(ints):
        print 'Generating drmap for interval %d of %d.' % (i, ints)
        syn_net = net.load_interval_net(i)

        if ints == 1:
            draw_drmap('%s.png' % (img_file,))
        else:
            draw_drmap('%s%d.png' % (img_file, i))

        destroy_net(syn_net)


if __name__ == '__main__':
    syndrmap(sys.argv[1], sys.argv[2])
