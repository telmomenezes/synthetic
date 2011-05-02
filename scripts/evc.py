#!/usr/bin/env python
# encoding: utf-8
"""
evc.py

Created by Telmo Menezes on 2011-04-19.
Copyright (c) 2011 Telmo Menezes. All rights reserved.
"""

import sys
import getopt
import syn
import histogram2svg


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
        
    net = syn.create_net()
    syn.load_net(net, args[0])
    syn.compute_evc(net)
    syn.print_net_info(net)
    syn.write_evc(net, args[1])
    
    #syn.write_gexf(net, "test.gexf")
    
    hist = syn.get_evc_histogram(net, 25)
    #syn.histogram2d_print(hist)
    
    histogram2svg.histogram2svg(hist, "test.svg")
    
    syn.destroy_net(net)

if __name__ == "__main__":
    sys.exit(main())
