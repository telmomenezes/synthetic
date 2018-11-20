import sys
import argparse
from netgens.commands.command import create_command


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
