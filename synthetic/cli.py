import sys
import argparse
from termcolor import colored
from synthetic.consts import (CLI_NAME, ASCII_LOGO, DEFAULT_BINS,
                              DEFAULT_SAMPLE_RATE, DEFAULT_MAX_DIST)
from synthetic.commands.evo import Evolve
from synthetic.commands.compare import Compare
from synthetic.commands.const import Const
from synthetic.commands.eval_distance import EvalDistance
from synthetic.commands.fit import Fit
from synthetic.commands.gen import Gen
from synthetic.commands.prune import Prune
from synthetic.commands.rand_gen import RandGen
import time


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


def show_logo():
    print()
    print(colored(ASCII_LOGO, 'magenta'))
    print()


def cli():
    t0= time.time()
    
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
    parser.add_argument('--bins', type=int, help='number of distribution bins', default=DEFAULT_BINS)
    parser.add_argument('--runs', type=int, help='number of generator runs')
    parser.add_argument('--undir', help='undirected network', action='store_true')
    parser.add_argument('--tolerance', type=float, help='antibloat tolerance')
    parser.add_argument('--nodes', type=int, help='number of nodes')
    parser.add_argument('--edges', type=int, help='number of edges')
    parser.add_argument('--mean', help='compute mean', action='store_true')
    parser.add_argument('--gentype', type=str, help='generator type')
    parser.add_argument('--sr', type=float, help='stample rate', default=DEFAULT_SAMPLE_RATE)
    parser.add_argument('--maxdist', type=int, help='maximum distance', default=DEFAULT_MAX_DIST)
    parser.add_argument('--rw', help='use random walk distance in fitness function', action='store_true')

    args = vars(parser.parse_args())

    show_logo()

    command = create_command(args['command'])

    if command is None:
        print('unkown command: {}'.format(command))
        sys.exit(2)
    else:
        if not command.check_args(args):
            print('error: {}'.format(command.error_msg))
            sys.exit(2)
        if not command.run(args):
            print('error: {}'.format(command.error_msg))
            sys.exit(1)
    print("Time taken: {} minutes and {} seconds".format(int((time.time() - t0) // 60), round((time.time() - t0) % 60)))


if __name__ == '__main__':
    cli()
