import netgens.generator as gen
from netgens.commands.command import *


class Const(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'const'
        self.description = 'check if generator weight is constant'
        self.mandatory_args = ['prg']
        self.optional_args = ['undir', 'sr', 'nodes', 'edges', 'gentype']

    def run(self, args):
        self.error_msg = None

        prog = args['prg']

        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        directed = not args['undir']
        nodes = arg_with_default(args, 'nodes', DEFAULT_NODES)
        edges = arg_with_default(args, 'edges', DEFAULT_EDGES)
        gentype = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        generator = gen.load(prog, gentype, directed)
        generator.run(nodes, edges, sr)
        print('is constant? %s' % generator.is_constant())

        return True
