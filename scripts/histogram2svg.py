#!/usr/bin/env python
# encoding: utf-8
"""
histogram2svg.py

Created by Telmo Menezes on 2011-04-21.
Copyright (c) 2011 Telmo Menezes. All rights reserved.
"""

import sys
import getopt
import syn
import SVG
from math import *


def histogram2svg(hist, svgfile):
    svg = SVG.SVG(500, 500)
    
    bin_number = syn.histogram2d_bin_number(hist)
    bin_width = 500.0 / bin_number
    
    max_val = 0
    for x in range(bin_number):
        for y in range(bin_number):
            val = syn.histogram2d_get_value(hist, x, y)
            if val > max_val:
                max_val = val
                
    max_val = log(max_val)
    
    svg.stroke_width = 0.0
    
    for x in range(bin_number):
        for y in range(bin_number):
            val = syn.histogram2d_get_value(hist, x, y)
            if val > 0:
                val = log(val)
                svg.fill = "rgb(%d,0,0)" % ((val / max_val) * 255.0)
            else:
                svg.fill = "rgb(0,0,255)"

            svg.draw_rect(x * bin_width, y * bin_width, bin_width, bin_width)
            
    svg.write(svgfile)


help_message = '''
The help message goes here.
''' 

class Usage(Exception):
	def __init__(self, msg):
		self.msg = msg


def main(argv=None):
	if argv is None:
		argv = sys.argv
	try:
		try:
			opts, args = getopt.getopt(argv[1:], "ho:v", ["help", "output="])
		except getopt.error, msg:
			raise Usage(msg)
	
		# option processing
		for option, value in opts:
			if option == "-v":
				verbose = True
			if option in ("-h", "--help"):
				raise Usage(help_message)
			if option in ("-o", "--output"):
				output = value
	
	except Usage, err:
		print >> sys.stderr, sys.argv[0].split("/")[-1] + ": " + str(err.msg)
		print >> sys.stderr, "\t for help use --help"
		return 2


if __name__ == "__main__":
	sys.exit(main())
