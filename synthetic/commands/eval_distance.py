from synthetic.consts import *

from synthetic.generator import load_generator
from synthetic.commands.command import *


class EvalDistance(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'eval_distance'
        self.description = 'compute behavioral distance between two generators'
        self.mandatory_args = ['prg', 'prg2']
        self.optional_args = ['undir', 'sr', 'nodes', 'edges', 'gentype']

    def run(self, args):
        self.error_msg = None

        prog1 = args['prg']
        prog2 = args['prg2']

        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        directed = not args['undir']
        nodes = arg_with_default(args, 'nodes', DEFAULT_NODES)
        edges = arg_with_default(args, 'edges', DEFAULT_EDGES)
        gentype = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        gen1 = load_generator(prog1, directed, gentype)
        gen2 = load_generator(prog2, directed, gentype)

        gen1.run(nodes, edges, sr, shadow=gen2)
        dist1 = gen1.eval_distance
        gen2.run(nodes, edges, sr, shadow=gen1)
        dist2 = gen2.eval_distance
        dist = (dist1 + dist2) / 2

        print('eval distance: %s' % dist)

        return True
