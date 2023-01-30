import numpy as np

from synthetic.consts import (DEFAULT_SAMPLE_RATE, DEFAULT_BINS, DEFAULT_MAX_DIST, DEFAULT_RUNS, DEFAULT_GEN_TYPE,
                              DEFAULT_NORM_SAMPLES)
from synthetic.net import load_net
from synthetic.generator import create_generator, load_generator
from synthetic.distances import DistancesToNet, Norm
from synthetic.commands.command import Command, arg_with_default, get_stat_dist_types
from statistics import mean


class Fit(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'fit'
        self.description = 'mean fitness of several runs of a generator'
        self.mandatory_args = ['inet', 'prg']
        self.optional_args = ['undir', 'sr', 'bins', 'maxdist', 'runs', 'gen_type', 'rw']

    def run(self, args):
        self.error_msg = None

        netfile = args['inet']
        prog = args['prg']
        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        bins = arg_with_default(args, 'bins', DEFAULT_BINS)
        max_dist = arg_with_default(args, 'maxdist', DEFAULT_MAX_DIST)
        rw = args['rw']
        directed = not args['undir']
        runs = arg_with_default(args, 'runs', DEFAULT_RUNS)
        gen_type = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        # load net
        net = load_net(netfile, directed)

        # create base generator
        base_generator = create_generator(directed, gen_type)
        if base_generator is None:
            self.error_msg = 'unknown generator type: {}'.format(gen_type)
            return False

        # create fitness calculator
        # TODO: norm samples configurable
        fitness = DistancesToNet(net, get_stat_dist_types(args), bins, max_dist, rw, norm=Norm.NONE,
                                 norm_samples=DEFAULT_NORM_SAMPLES)

        fit_maxes = []
        fit_means = []
        fit_geoms = []
        for i in range(runs):
            print('run #{}'.format(i))

            gen = load_generator(prog, directed, gen_type)
            synth_net = gen.run(net.graph.vcount(), net.graph.ecount(), sr)
            distances = fitness.compute(synth_net)
            fit_max = max(distances)
            fit_mean = mean(distances)
            fit_geom = (np.array([max(distance, 0.000001) for distance in distances]).prod() ** (1.0 / len(distances)))

            fit_maxes.append(fit_max)
            fit_means.append(fit_mean)
            fit_geoms.append(fit_geom)

            print('fitness (max): {}; fitness (mean): {}; fitness (geom): {}'.format(fit_max, fit_mean, fit_geom))
            print([stat_type.name for stat_type in fitness.stat_types])
            print(distances)

        mean_fit_max = sum(fit_maxes) / runs
        max_fit_max = max(fit_maxes)
        min_fit_max = min(fit_maxes)

        mean_fit_mean = sum(fit_means) / runs
        max_fit_mean = max(fit_means)
        min_fit_mean = min(fit_means)

        mean_fit_geom = sum(fit_geoms) / runs
        max_fit_geom = max(fit_geoms)
        min_fit_geom = min(fit_geoms)

        print('\n\n')

        print('mean fitness (max): {}; min fitness (max): {}; max fitness (max): {}'.format(
            mean_fit_max, min_fit_max, max_fit_max))
        print('mean fitness (mean): {}; min fitness (mean): {}; max fitness (mean): {}'.format(
            mean_fit_mean, min_fit_mean, max_fit_mean))
        print('mean fitness (geom): {}; min fitness (geom): {}; max fitness (geom): {}'.format(
            mean_fit_geom, min_fit_geom, max_fit_geom))

        print('done.')

        return True
