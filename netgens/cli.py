import argparse
from netgens.net import Net


# commands
def compare(args):
    netfile1 = args.inet
    netfile2 = args.inet2
    bins = args.bins
    directed = args.undir
    par = args.par

    net1 = net.load(netfile1, directed, par)
    net2 = net.load(netfile2, directed, par)

    print('NET: %s' % netfile1)
    print(net1)
    bag1 = MetricsBag(net1, bins)
    print(bag1)

    print('\n\n')

    print('NET: %s' % netfile2)
    print(net2)
    bag2 = MetricsBag(net2, null, null, bins, bag1)
    print(bag2)


def cli():
    parser = argparse.ArgumentParser()

    parser.add_argument('command', type=str, help='command to execute')
    parser.add_argument('--inet', type=str, help='input net file')
    parser.add_argument('--inet2', type=str, help='second input net file')
    parser.add_argument('--onet', type=str, help='output net file')
    parser.add_argument('--dir', type=str, help='directory')
    parser.add_argument('--odir', type=str, help='output directory')
    parser.add_argument('--prg', type=str, help='generator program file')
    parser.add_argument('--prg2', type=str, help='second generator program file')
    parser.add_argument('--oprg', type=str, help='generator output program file')
    parser.add_argument('--out', type=str, help='output file')
    parser.add_argument('--gens', type=str, help='number of generations')
    parser.add_argument('--bins', type=int, help='number of distribution bins', default=100)
    parser.add_argument('--maxnodes', type=str, help='max nodes (sampling)')
    parser.add_argument('--maxedges', type=str, help='max edges (sampling)')
    parser.add_argument('--trials', type=str, help='number of trials for the fast generator')
    parser.add_argument('--runs', type=str, help='number of generator runs')
    parser.add_argument('--undir', help='undirected network', action='store_true')
    parser.add_argument('--tolerance', type=str, help='antibloat tolerance')
    parser.add_argument('--nodes', type=str, help='number of nodes')
    parser.add_argument('--edges', type=str, help='number of edges')
    parser.add_argument('--mean', help='compute mean', action='store_true')
    parser.add_argument('--par', help='parallel edges allowed', action='store_true')
    parser.add_argument('--gentype', type=str, help='generator type')

    args = parser.parse_args()

    command = args.command

    if command == 'compare':
        compare(args)
    else:
        print('unkown command: %s' % command)


if __name__ == '__main__':
    cli()
