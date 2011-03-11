#!/usr/bin/python


import sys
import string


def snap2csv(in_file, out_file):
    fin = open(in_file)
    fout = open(out_file, 'w')
    
    line = fin.readline()
    while (line != ""):
        if (line[0] != '#'):
            strings = string.split(line)
            fout.write("%s,%s\n" % (strings[0], strings[1]))
        line = fin.readline()

    fin.close()
    fout.close()


if __name__ == '__main__':
    snap2csv(sys.argv[1], sys.argv[2])
