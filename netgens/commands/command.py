from netgens.fitness import DEFAULT_UNDIRECTED, DEFAULT_DIRECTED
from netgens.commands.evo import Evolve


def get_stat_dist_types(args):
    directed = not args['undir']
    if directed:
        return DEFAULT_DIRECTED
    else:
        return DEFAULT_UNDIRECTED


def create_command(name):
    if name == 'evo':
        return Evolve()
    return None


def arg_with_default(args, arg_name, default):
    if args[arg_name] is None:
        return default
    return args[arg_name]


class Command(object):
    def __init__(self):
        self.name = None
        self.mandatory_args = None
        self.error_msg = None

    def help(self):
        # TODO: throw exception
        pass

    def check_args(self, args):
        for arg in self.mandatory_args:
            if args[arg] is None:
                self.error_msg('argument %s is mandatory.' % arg)
                return False
        return True

    def run(self, args):
        # TODO: throw exception
        pass
