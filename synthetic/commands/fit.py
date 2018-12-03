from synthetic.consts import *
from synthetic.net import load_net
from synthetic.generator import create_generator, load_generator
from synthetic.fitness import Fitness, Norm
from synthetic.commands.command import *


class Fit(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'fit'
        self.description = 'compute mean fitness for several runs of a generator'
        self.mandatory_args = ['inet', 'prg']
        self.optional_args = ['undir', 'sr', 'bins', 'maxdist', 'runs', 'gen_type']

    def run(self, args):
        self.error_msg = None

        netfile = args['inet']
        prog = args['prg']
        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        bins = arg_with_default(args, 'bins', DEFAULT_BINS)
        max_dist = arg_with_default(args, 'maxdist', DEFAULT_MAX_DIST)
        directed = not args['undir']
        runs = arg_with_default(args, 'runs', DEFAULT_RUNS)
        gen_type = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        # load net
        net = load_net(netfile, directed)

        # create base generator
        base_generator = create_generator(directed, gen_type)
        if base_generator is None:
            self.error_msg = 'unknown generator type: %s' % gen_type
            return False

        # create fitness calculator
        # TODO: norm samples configurable
        fitness = Fitness(net, get_stat_dist_types(args), bins, max_dist,
                          norm=Norm.ER_MEAN_RATIO, norm_samples=DEFAULT_NORM_SAMPLES)

        fit_maxes = []
        fit_means = []
        for i in range(runs):
            print('run #%s' % i)

            gen = load_generator(prog, directed, gen_type)
            synth_net = gen.run(len(net.vs), len(net.es), sr)
            fit_max, fit_mean, distances = fitness.compute(synth_net)

            fit_maxes.append(fit_max)
            fit_means.append(fit_mean)

            print('fitness (max): %s; fitness (mean): %s' % (fit_max, fit_mean))
            print(distances)

        mean_fit_max = sum(fit_maxes) / runs
        max_fit_max = max(fit_maxes)
        min_fit_max = min(fit_maxes)

        mean_fit_mean = sum(fit_means) / runs
        max_fit_mean = max(fit_means)
        min_fit_mean = min(fit_means)
        
        print('\n\n')

        print('mean fitness (max): %s; min fitness (max): %s; max fitness (max): %s'
              % (mean_fit_max, min_fit_max, max_fit_max))
        print('mean fitness (mean): %s; min fitness (mean): %s; max fitness (mean): %s'
              % (mean_fit_mean, min_fit_mean, max_fit_mean))
        
        print('done.')
        
        return True
