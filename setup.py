#!/usr/bin/env python
# encoding: utf-8
"""
setup.py

Created by Telmo Menezes on 2011-04-15.
Copyright (c) 2011 Telmo Menezes. All rights reserved.
"""

from distutils.core import setup, Extension

module1 = Extension('syn',
                    define_macros = [('MAJOR_VERSION', '1'),
                                     ('MINOR_VERSION', '0')],
                    libraries = ['m'],
                    include_dirs = ['src'],
                    sources = ['src/synpython.c', 'src/edge.c', 'src/node.c', 'src/emd.c'])

setup (name = 'syn',
       version = '1.0',
       description = 'TBD',
       author = 'Telmo Menezes',
       author_email = 'telmo@telmomenezes.com',
       url = 'http://telmomenezes.com',
       long_description = '''
TBD.
''',
       ext_modules = [module1])
