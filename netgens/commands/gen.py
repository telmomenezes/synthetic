from netgens.generator import load_generator
from netgens.commands.command import *


class Gen(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'gen'
        self.description = 'generate network'
        self.mandatory_args = ['prg', 'onet']
        self.optional_args = ['undir', 'sr', 'nodes', 'edges', 'gentype']

    def run(self, args):
        self.error_msg = None

        prog = args['prg']
        onet = args['onet']

        sr = arg_with_default(args, 'sr', DEFAULT_SAMPLE_RATE)
        directed = not args['undir']
        nodes = arg_with_default(args, 'nodes', DEFAULT_NODES)
        edges = arg_with_default(args, 'edges', DEFAULT_EDGES)
        gentype = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        print('nodes: %s' % nodes)
        print('edges: %s' % edges)

        # load and run generator
        gen = load_generator(prog, directed, gentype)
        net = gen.run(nodes, edges, sr)
        
        # write net
        net.save(onet)

        print('done.')

        return True
