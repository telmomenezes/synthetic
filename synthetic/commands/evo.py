from synthetic.consts import *
from synthetic.net import load_net
from synthetic.generator import create_generator
from synthetic.fitness import Fitness, Norm
from synthetic.evo import Evo
from synthetic.commands.command import *


class Evolve(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'evo'
        self.description = 'evolve network generator'
        self.mandatory_args = ['inet', 'odir']
        self.optional_args = ['undir', 'gens', 'sr', 'bins', 'maxdist', 'tolerance']

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

        # create evolutionary search
        evo = Evo(net, fitness, generations, tolerance, base_generator, outdir, sr)

        # some reports to screen
        print('target net: %s' + netfile)
        print(evo.info_string())
        print(base_generator)
        
        # write experiment params to file
        with open('%s/params.txt' % outdir, 'w') as text_file:
            text_file.write(evo.info_string())

        # run search
        evo.run()
        
        return True
