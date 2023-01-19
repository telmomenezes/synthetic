from synthetic.consts import (DEFAULT_GENERATIONS, DEFAULT_SAMPLE_RATE, DEFAULT_BINS, DEFAULT_MAX_DIST,
                              DEFAULT_TOLERANCE, DEFAULT_GEN_TYPE, DEFAULT_NORM_SAMPLES)
from synthetic.net import load_net
from synthetic.generator import create_generator
from synthetic.distances import DistancesToNet, Norm
from synthetic.evo import Evo
from synthetic.commands.command import Command, arg_with_default, get_stat_dist_types


class Evolve(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'evo'
        self.description = 'evolve network generator'
        self.mandatory_args = ['inet', 'odir']
        self.optional_args = ['undir', 'gens', 'sr', 'bins', 'maxdist', 'tolerance', 'gentype', 'rw']

    def run(self, args):
        self.error_msg = None

        netfile = args['inet']
        outdir = args['odir']
        generations = arg_with_default(args, 'gens', DEFAULT_GENERATIONS)
        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        bins = arg_with_default(args, 'bins', DEFAULT_BINS)
        max_dist = arg_with_default(args, 'maxdist', DEFAULT_MAX_DIST)
        directed = not args['undir']
        tolerance = arg_with_default(args, 'tolerance', DEFAULT_TOLERANCE)
        gen_type = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)
        rw = args['rw']

        # load net
        net = load_net(netfile, directed)

        # some reports to screen
        info_params = ['target net: {}'.format(netfile),
                       'stable generations: {}'.format(generations),
                       'directed: {}'.format(directed),
                       'target net node count: {}'.format(net.graph.vcount()),
                       'target net edge count: {}'.format(net.graph.ecount()),
                       'distribution bins: {}'.format(bins),
                       'tolerance: {}'.format(tolerance),
                       'random walk distance (fitness): {}'.format(rw)]
        info_str = '\n'.join(info_params)
        print(info_str)
        print()

        # write experiment params to file
        with open('{}/params.txt'.format(outdir), 'w') as text_file:
            text_file.write(info_str)

        # create base generator
        base_generator = create_generator(directed, gen_type)
        if base_generator is None:
            self.error_msg = 'unknown generator type: {}'.format(gen_type)
            return False

        # create fitness calculator
        # TODO: norm samples configurable
        print('computing target network statistics...')
        dists2net = DistancesToNet(net, get_stat_dist_types(args), bins, max_dist, rw, norm=Norm.ER_MEAN_RATIO,
                                   norm_samples=DEFAULT_NORM_SAMPLES)

        # create evolutionary search
        print('evolutionary search started...')
        evo = Evo(net, dists2net, generations, tolerance, base_generator, outdir, sr)

        # run search
        evo.run()

        return True
