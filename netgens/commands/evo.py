import igraph
from netgens.generator import create_generator
from netgens.fitness import Fitness, Norm, DEFAULT_UNDIRECTED, DEFAULT_DIRECTED
from netgens.evo import Evo
from netgens.commands.command import *


class Evolve(Command):
    def __init__(self):
        Command.__init__(self)
        self.name = 'evo'
        self.mandatory_args = ['inet', 'odir']

    def help(self):
        lines = ['Evolve network generator.',
                 '$ synt evo -inet <network> -odir <dir>',
                 'Optional parameters:',
                 '-undir if network is undirected',
                 '-gens <n> number of stable generations before search stops (default is 1000)',
                 '-sr <n> sample ratio (default is .0006)',
                 '-bins <n> distribution bins (default is 100)',
                 '-tolerance <n> accepted fitness loss for shorter program (default is .1)']
        return '\n'.join(lines)

    def run(self, args):
        self.error_msg = None

        netfile = args['inet']
        outdir = args['odir']
        generations = arg_with_default(args, 'gens', 1000)
        sr = arg_with_default(args, 'sr', 0.0006)
        bins = arg_with_default(args, 'bins', 100)
        directed = not args['undir']
        tolerance = arg_with_default(args, 'tolerance', 0.1)
        gen_type = arg_with_default(args, 'gentype', 'exo')

        # load net
        net = igraph.Graph.Load(netfile)

        # force directed / undirected
        if net.is_directed() and not directed:
            net = net.as_undirected()
        if not net.is_directed() and directed:
            net = net.as_directed()

        # prevent loops and multiple edges
        net = net.simplify()

        assert (net.is_directed() == directed)

        # create base generator
        base_generator = create_generator(gen_type, directed)
        if base_generator is None:
            self.error_msg = 'unknown generator type: %s' % gen_type
            return False

        # create fitness calculator
        if directed:
            stat_dist_types = DEFAULT_DIRECTED
        else:
            stat_dist_types = DEFAULT_UNDIRECTED
        fitness = Fitness(net, stat_dist_types, bins, norm=Norm.ER_MEAN_RATIO, norm_samples=30)

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
