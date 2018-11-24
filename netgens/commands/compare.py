from netgens.net import load_net
from netgens.stats import StatsSet
from netgens.commands.command import *


class Compare(Command):
    def __init__(self):
        Command.__init__(self)
        self.name = 'compare'
        self.mandatory_args = ['inet', 'inet2']

    def help(self):
        lines = ['Compares two networks.',
                 '$ netgens compare -inet <network1> -inet2 <network2>',
                 'Optional parameters:',
                 '-undir if network is undirected',
                 '-bins <n> distribution bins (default is 100)']
        return '\n'.join(lines)

    def run(self, args):
        self.error_msg = None

        netfile1 = args['inet']
        netfile2 = args['inet2']

        bins = arg_with_default(args, 'bins', 100)
        directed = not args['undir']

        # load nets
        net1 = load_net(netfile1, directed)
        net2 = load_net(netfile2, directed)

        stat_dist_types = get_stat_dist_types(args)

        print('first network: %s' % netfile1)
        print(net1)
        stats1 = StatsSet(net1, stat_dist_types, bins, ref_stats=None)
        print(stats1)

        print('\n\n')

        print('second network: %s' % netfile2)
        print(net2)
        stats2 = StatsSet(net1, stat_dist_types, bins, ref_stats=None)
        print(stats2)

        return True
