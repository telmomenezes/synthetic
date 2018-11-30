import sys
import argparse
from termcolor import colored
from synthetic.consts import *
from synthetic.fitness import DEFAULT_UNDIRECTED, DEFAULT_DIRECTED
from synthetic.commands.evo import Evolve
from synthetic.commands.compare import Compare
from synthetic.commands.const import Const
from synthetic.commands.eval_distance import EvalDistance
from synthetic.commands.fit import Fit
from synthetic.commands.gen import Gen
from synthetic.commands.prune import Prune
from synthetic.commands.rand_gen import RandGen


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
            'tolerance': 'accepted fitness loss for shorter program (default is .1)',
            'nodes': 'number of nodes (default is 1000)',
            'edges': 'number or edges (default is 10000)',
            'gentype': 'type of generator to use (default is "exo")',
            'runs': 'number of runs per program (default is 30)',
            'mean': 'compute mean'}


def get_stat_dist_types(args):
    directed = not args['undir']
    if directed:
        return DEFAULT_DIRECTED
    else:
        return DEFAULT_UNDIRECTED


def create_command(name):
    if name == 'evo':
        return Evolve(CLI_NAME)
    elif name == 'compare':
        return Compare(CLI_NAME)
    elif name == 'const':
        return Const(CLI_NAME)
    elif name == 'eval_distance':
        return EvalDistance(CLI_NAME)
    elif name == 'fit':
        return Fit(CLI_NAME)
    elif name == 'gen':
        return Gen(CLI_NAME)
    elif name == 'prune':
        return Prune(CLI_NAME)
    elif name == 'rand_gen':
        return RandGen(CLI_NAME)
    return None


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


def show_logo():
    print(colored(ascii_logo, 'magenta'))
    print()


def cli():
    parser = argparse.ArgumentParser()

    parser.add_argument('command', type=str, help='command to execute')
    parser.add_argument('--inet', type=str, help='input net file')
    parser.add_argument('--inet2', type=str, help='second input net file')
    parser.add_argument('--onet', type=str, help='output net file')
    parser.add_argument('--dir', type=str, help='directory')
    parser.add_argument('--odir', type=str, help='output directory')
    parser.add_argument('--prg', type=str, help='generator program file')
    parser.add_argument('--prg2', type=str, help='second generator program file')
    parser.add_argument('--oprg', type=str, help='generator output program file')
    parser.add_argument('--out', type=str, help='output file')
    parser.add_argument('--gens', type=int, help='number of generations')
    parser.add_argument('--bins', type=int, help='number of distribution bins', default=100)
    parser.add_argument('--runs', type=int, help='number of generator runs')
    parser.add_argument('--undir', help='undirected network', action='store_true')
    parser.add_argument('--tolerance', type=float, help='antibloat tolerance')
    parser.add_argument('--nodes', type=int, help='number of nodes')
    parser.add_argument('--edges', type=int, help='number of edges')
    parser.add_argument('--mean', help='compute mean', action='store_true')
    parser.add_argument('--gentype', type=str, help='generator type')

    args = vars(parser.parse_args())

    show_logo()

    command = create_command(args['command'])

    if command is None:
        print('unkown command: %s' % command)
        sys.exit(2)
    else:
        if not command.check_args(args):
            print('error: %s' % command.error_msg)
            sys.exit(2)
        if not command.run(args):
            print('error: %s' % command.error_msg)
            sys.exit(1)


if __name__ == '__main__':
    cli()
