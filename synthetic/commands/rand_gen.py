from synthetic.generator import create_generator
from synthetic.commands.command import *


class RandGen(Command):
    def __init__(self, cli_name):
        Command.__init__(self, cli_name)
        self.name = 'rand_gen'
        self.description = 'create a random generator program'
        self.mandatory_args = ['oprg']
        self.optional_args = ['undir', 'gentype']

    def run(self, args):
        self.error_msg = None

        out_prog = args['oprg']

        directed = not args['undir']
        gentype = arg_with_default(args, 'gentype', DEFAULT_GEN_TYPE)

        gen = create_generator(directed, gentype, init_random=True)
        gen.prog.write(out_prog)

        print('done.')

        return True
