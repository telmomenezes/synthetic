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
