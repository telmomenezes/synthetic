from netgens.gp.node_type import NodeType
from netgens.gp.fun import Fun
from netgens.gp.node_dyn_status import NodeDynStatus


def fun_cond_pos(fun):
    if fun == Fun.ZER or fun == Fun.AFF:
        return 1
    elif fun == Fun.EQ or fun == Fun.GRT or fun == Fun.LRT:
        return 2
    else:
        return -1


def fun_arity(fun):
    if fun in {Fun.EXP, Fun.LOG, Fun.ABS}:
        return 1
    elif fun in {Fun.SUM, Fun.SUB, Fun.MUL, Fun.DIV, Fun.MIN, Fun.MAX, Fun.POW}:
        return 2
    elif fun in {Fun.ZER, Fun.AFF}:
        return 3
    elif fun in {Fun.EQ, Fun.GRT, Fun.LRT}:
        return 4
    # this should not happen
    return 0


class Node(object):
    def __init__(self, tree):
        self.tree = tree
        self.params = []
        for i in range(4):
            self.params.append(Node(tree))
        self.type = 0
        self.val = 0
        self.var = 0
        self.fun = 0
        self.arity = 0
        self.curval = 0.
        self.parent = None
        self.curpos = 0
        self.stoppos = 0
        self.condpos = 0
        self.branching = 0
        self.dyn_status = 0

    def init_val(self, val, parent):
        self.type = NodeType.VAL
        self.parent = parent
        self.val = val
        self.arity = 0
        self.condpos = -1
        self.stoppos = 0
        self.dyn_status = NodeDynStatus.UNUSED

    def init_var(self, var, parent):
        self.type = NodeType.VAR
        self.parent = parent
        self.var = var
        self.arity = 0
        self.condpos = -1
        self.stoppos = 0
        self.dyn_status = NodeDynStatus.UNUSED

    def init_fun(self, fun, parent):
        self.type = NodeType.FUN
        self.parent = parent
        self.fun = fun
        self.arity = fun_arity(fun)
        self.condpos = fun_cond_pos(fun)
        self.stoppos = self.arity
        self.dyn_status = NodeDynStatus.UNUSED

    def write(self, out):
        if self.type == NodeType.VAL:
            out.write('%s' % self.val)
        elif self.type == NodeType.VAR:
            out.write("$%s" % self.tree.variable_names[self.var])
        elif self.type == NodeType.FUN:
            if self.fun == Fun.SUM:
                out.write('+')
            elif self.fun == Fun.SUB:
                out.write('-')
            elif self.fun == Fun.MUL:
                out.write('*')
            elif self.fun == Fun.DIV:
                out.write('/')
            elif self.fun == Fun.ZER:
                out.write('ZER')
            elif self.fun == Fun.EQ:
                out.write('==')
            elif self.fun == Fun.GRT:
                out.write('>')
            elif self.fun == Fun.LRT:
                out.write('<')
            elif self.fun == Fun.EXP:
                out.write('EXP')
            elif self.fun == Fun.LOG:
                out.write('LOG')
            elif self.fun == Fun.ABS:
                out.write('ABS')
            elif self.fun == Fun.MIN:
                out.write('MIN')
            elif self.fun == Fun.MAX:
                out.write('MAX')
            elif self.fun == Fun.AFF:
                out.write('AFF')
            elif self.fun == Fun.POW:
                out.write('^')
            else:
                out.write('F??')
        else:
            out.write('???')
