from synthetic.consts import DEFAULT_BINS, DEFAULT_MAX_DIST, DEFAULT_NORM_SAMPLES
from synthetic.distances import DistancesToNet, Norm
from synthetic.net import load_net
from synthetic.commands.command import Command, arg_with_default, get_stat_dist_types


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
        rw = args['rw']
        directed = not args['undir']

        # load nets
        net1 = load_net(netfile1, directed)
        net2 = load_net(netfile2, directed)

        print('Network 1: {}'.format(netfile1))
        print('Network 2: {}'.format(netfile2))
        
        fitness = DistancesToNet(net1, get_stat_dist_types(args), bins, max_dist, rw, norm=Norm.ER_MEAN_RATIO,
                                 norm_samples=DEFAULT_NORM_SAMPLES)

        distances = fitness.compute(net2)
        print("\nDistance of network 2 (candidate) from network 1 (target):")
        print([stat_type.name for stat_type in fitness.stat_types])
        print(distances)
        
        fitness = DistancesToNet(net2, get_stat_dist_types(args), bins, max_dist, rw, norm=Norm.ER_MEAN_RATIO,
                                 norm_samples=DEFAULT_NORM_SAMPLES)
        
        
        distances = fitness.compute(net1)
        print("\nDistance of network 1 (candidate) from network 2 (target):")
        print([stat_type.name for stat_type in fitness.stat_types])
        print(distances)
        print('\n')

        return True
