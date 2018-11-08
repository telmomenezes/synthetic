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

    def __str__(self):
        if self.type == NodeType.VAL:
            return '%s' % self.val
        elif self.type == NodeType.VAR:
            return "$%s" % self.tree.variable_names[self.var]
        elif self.type == NodeType.FUN:
            if self.fun == Fun.SUM:
                return '+'
            elif self.fun == Fun.SUB:
                return '-'
            elif self.fun == Fun.MUL:
                return '*'
            elif self.fun == Fun.DIV:
                return '/'
            elif self.fun == Fun.ZER:
                return 'ZER'
            elif self.fun == Fun.EQ:
                return '=='
            elif self.fun == Fun.GRT:
                return '>'
            elif self.fun == Fun.LRT:
                return '<'
            elif self.fun == Fun.EXP:
                return 'EXP'
            elif self.fun == Fun.LOG:
                return 'LOG'
            elif self.fun == Fun.ABS:
                return 'ABS'
            elif self.fun == Fun.MIN:
                return 'MIN'
            elif self.fun == Fun.MAX:
                return 'MAX'
            elif self.fun == Fun.AFF:
                return 'AFF'
            elif self.fun == Fun.POW:
                return '^'
            else:
                return 'F??'
        else:
            return '???'
