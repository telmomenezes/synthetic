#!/usr/bin/env python
# encoding: utf-8
"""
SVG.py

Created by Telmo Menezes on 2011-04-21.
Copyright (c) 2011 Telmo Menezes. All rights reserved.
"""

class SVG:
    def __init__(self, height=400, width=400):
        self.fill = "blue"
        self.stroke = "black"
        self.stroke_width = 1.0
        self.fill_opacity = 1.0
        self.stroke_opacity = 1.0
        self.xml = "<?xml version=\"1.0\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<svg height=\"%d\" width=\"%d\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" >\n" % (height, width)

    def draw_rect(self, x, y, width, height):
        self.xml = "%s<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"fill:%s;stroke:%s;stroke-width:%f;fill-opacity:%f;stroke-opacity:%f\" />\n" % (self.xml, x, y, width, height, self.fill, self.stroke, self.stroke_width, self.fill_opacity, self.stroke_opacity)
        
    def write(self, filename):
        self.xml = "%s</svg>\n" % (self.xml)
        file = open(filename, 'w')
        file.write(self.xml)
        file.close()


if __name__ == '__main__':
    svg = SVG()
    svg.stroke_width = 0.0
    svg.fill = "rgb(0, 255, 0)"
    svg.draw_rect(0, 0, 100, 100)
    svg.fill = "rgb(0, 0, 255)"
    svg.draw_rect(100, 0, 100, 100)
    svg.fill = "rgb(255, 0, 0)"
    svg.draw_rect(0, 100, 100, 100)
    svg.write("test.svg")

