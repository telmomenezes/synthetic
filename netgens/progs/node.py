from netgens.progs.node_type import NodeType
from netgens.progs.funs import fun_arity, fun_cond_pos, fun2str
from netgens.progs.node_dyn_status import NodeDynStatus


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
            return fun2str(self.fun)
        else:
            return '???'
