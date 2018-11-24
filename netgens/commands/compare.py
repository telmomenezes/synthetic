from netgens.net import load_net
from netgens.stats import StatsSet
from netgens.commands.command import *


class Compare(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'compare'
        self.description = 'compare two networks'
        self.mandatory_args = ['inet', 'inet2']
        self.optional_args = ['undir', 'bins']

    def run(self, args):
        self.error_msg = None

        netfile1 = args['inet']
        netfile2 = args['inet2']

        bins = arg_with_default(args, 'bins', DEFAULT_BINS)
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
