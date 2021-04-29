from synthetic.consts import DEFAULT_BINS, DEFAULT_MAX_DIST
from synthetic.net import load_net
from synthetic.stats import StatsSet
from synthetic.commands.command import (Command, arg_with_default,
                                        get_stat_dist_types)


class Compare(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'compare'
        self.description = 'compare two networks'
        self.mandatory_args = ['inet', 'inet2']
        self.optional_args = ['undir', 'bins', 'maxdist']

    def run(self, args):
        self.error_msg = None

        netfile1 = args['inet']
        netfile2 = args['inet2']

        bins = arg_with_default(args, 'bins', DEFAULT_BINS)
        max_dist = arg_with_default(args, 'maxdist', DEFAULT_MAX_DIST)
        directed = not args['undir']

        # load nets
        net1 = load_net(netfile1, directed)
        net2 = load_net(netfile2, directed)

        stat_dist_types = get_stat_dist_types(args)

        print('first network: {}'.format(netfile1))
        print(net1)
        stats1 = StatsSet(net1, stat_dist_types, bins, max_dist,
                          ref_stats=None)
        print(stats1)

        print('\n\n')

        print('second network: {}'.format(netfile2))
        print(net2)
        stats2 = StatsSet(net1, stat_dist_types, bins, max_dist,
                          ref_stats=None)
        print(stats2)

        return True
