from synthetic.fitness import DEFAULT_UNDIRECTED, DEFAULT_DIRECTED


ARG_PLACEHOLDERS = {'inet': 'network',
                    'inet2': 'network',
                    'onet': 'output_network',
                    'out': 'output_file',
                    'dir': 'dir',
                    'odir': 'dir',
                    'undir': None,
                    'gens': 'n',
                    'sr': 'n',
                    'bins': 'n',
                    'maxdist': 'n',
                    'tolerance': 'n',
                    'nodes': 'n',
                    'edges': 'n',
                    'gentype': 'generator_type',
                    'runs': 'n',
                    'mean': None,
                    'prg': 'program_file',
                    'prg2': 'program_file',
                    'oprg': 'output_program_file'}

ARG_HELP = {'undir': 'undirected network(s)',
            'gens': 'number of stable generations before search stops (default is 1000)',
            'sr': 'sample ratio (default is .0006)',
            'bins': 'histogram bins (default is 100)',
            'maxdist': 'maximum network distance (default is 10)',
            'tolerance': 'accepted fitness loss for shorter program (default is .1)',
            'nodes': 'number of nodes (default is 1000)',
            'edges': 'number or edges (default is 10000)',
            'gentype': 'type of generator to use (default is "exo")',
            'runs': 'number of runs per program (default is 30)',
            'mean': 'compute mean'}


def arg_str(arg):
    assert(arg in ARG_PLACEHOLDERS)
    placeholder = ARG_PLACEHOLDERS[arg]
    st = '-%s' % arg
    if placeholder is None:
        return st
    return '%s <%s>' % (arg, placeholder)


def arg_help(arg):
    assert(arg in ARG_HELP)
    return '%s %s' % (arg_str(arg), ARG_HELP[arg])


def arg_with_default(args, arg_name, default):
    if args[arg_name] is None:
        return default
    return args[arg_name]


def get_stat_dist_types(args):
    directed = not args['undir']
    if directed:
        return DEFAULT_DIRECTED
    else:
        return DEFAULT_UNDIRECTED


class Command(object):
    def __init__(self, cli_name):
        self.cli_name = cli_name
        self.name = None
        self.description = None
        self.mandatory_args = None
        self.optional_args = None
        self.error_msg = None

    def help(self):
        args_str = ' '.join([arg_str(arg) for arg in self.mandatory_args])
        command_line = '$ %s %s %s' % (self.cli_name, self.name, args_str)
        lines = [self.description, command_line]

        if len(self.optional_args) > 0:
            lines.append('\noptional arguments:')
            for arg in self.optional_args:
                lines.append(arg_help(arg))
        return '\n'.join(lines)

    def check_args(self, args):
        for arg in self.mandatory_args:
            if args[arg] is None:
                self.error_msg('argument %s is mandatory.' % arg)
                return False
        return True

    def run(self, args):
        # TODO: throw exception
        pass
