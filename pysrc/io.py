import string


def snap2syn(in_file, out_file):
    fin = open(in_file)
    fout = open(out_file, 'w')

    nodes = {}
    node_count = 0
    edge_count = 0
    
    # nodes
    fout.write("[nodes]\n")
    for line in fin:
        if (line[0] != '#'):
            strings = string.split(line)
            if len(strings) == 2:
                for s in strings:
                    if not nodes.has_key(s):
                        nodes[s] = node_count
                        fout.write("id=%d\n" % node_count)
                        node_count += 1

    # edges
    fout.write("[edges]\n")
    fin.seek(0)
    for line in fin:
        if (line[0] != '#'):
            strings = string.split(line)
            if len(strings) == 2:
                fout.write("orig=%d targ=%d\n" % (nodes[strings[0]], nodes[strings[1]]))
                edge_count += 1

    fin.close()
    fout.close()

    return node_count, edge_count
