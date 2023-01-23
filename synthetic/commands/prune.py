from synthetic.consts import DEFAULT_SAMPLE_RATE, DEFAULT_NODES, DEFAULT_EDGES, DEFAULT_GEN_TYPE
from synthetic.generator import load_generator
from synthetic.commands.command import Command, arg_with_default


class Prune(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'prune'
        self.description = 'simplify generator program'
        self.mandatory_args = ['prg', 'oprg']
        self.optional_args = ['undir', 'sr', 'nodes', 'edges', 'gentype']

    def run(self, args):
        self.error_msg = None

        prog = args['prg']
        out_prog = args['oprg']

        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        directed = not args['undir']
        nodes = arg_with_default(args, 'nodes', DEFAULT_NODES)
        edges = arg_with_default(args, 'edges', DEFAULT_EDGES)
        gentype = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        print('nodes: {}'.format(nodes))
        print('edges: {}'.format(edges))

        # load and run generator
        gen = load_generator(prog, directed, gentype)
        gen.run(nodes, edges, sr)

        # prune and save
        gen.prog.dyn_pruning()
        gen.prog.write(out_prog)

        print('done.')

        return True
