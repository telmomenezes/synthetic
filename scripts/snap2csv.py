#!/usr/bin/python


import sys
import string


def snap2csv(in_file, out_file):
    fin = open(in_file)
    fout = open(out_file, 'w')
    nodes = {}
    node_id = 0
    
    line = fin.readline()
    while (line != ""):
        if (line[0] != '#'):
            strings = string.split(line, ',')
            if strings[0] not in nodes:
                nodes[strings[0]] = node_id
                node_id += 1
            if strings[1] not in nodes:
                nodes[strings[1]] = node_id
                node_id += 1
            fout.write("%d,%d\n" % (nodes[strings[0]], nodes[strings[1]]))
        line = fin.readline()

    fin.close()
    fout.close()


if __name__ == '__main__':
    snap2csv(sys.argv[1], sys.argv[2])
