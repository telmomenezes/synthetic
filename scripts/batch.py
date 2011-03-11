#!/usr/bin/python


import sys
import os
import fnmatch
import string
import subprocess


def batch(command, in_dir, file_pattern, out_dir, postfix):
    for filename in os.listdir(in_dir):
        if (fnmatch.fnmatch(filename, file_pattern)):
            outfile = string.split(filename, '.')[0]
            outfile = '%s%s' % (outfile, postfix)
            outfile = os.path.join(out_dir, outfile)
            cargs = '%s %s' % (os.path.join(in_dir, filename), outfile)
            print('Executing: %s %s' % (command, cargs))
            try:
                subprocess.call([command, os.path.join(in_dir, filename), outfile])
            except OSError, e:
                print('Error: %s' % e)


if __name__ == '__main__':
    batch(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5])
