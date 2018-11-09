from enum import Enum
from netgens.progs.funs import fun_arity, fun_cond_pos, fun2str


class NodeType(Enum):
    FUN = 0
    VAR = 1
    VAL = 2


class NodeDynStatus(Enum):
    UNUSED = 0
    CONSTANT = 1
    DYNAMIC = 2


def create_val(val, prog, parent):
    node = Node(prog, parent)
    node.type = NodeType.VAL
    node.val = val
    return node


def create_var(var, prog, parent):
    node = Node(prog, parent)
    node.type = NodeType.VAR
    node.var = var
    return node


def create_fun(fun, prog, parent):
    node = Node(prog, parent)
    node.type = NodeType.FUN
    node.fun = fun
    node.condpos = fun_cond_pos(fun)
    node.stoppos = fun_arity(fun)
    return node


class Node(object):
    def __init__(self, prog, parent):
        self.prog = prog
        self.parent = parent
        self.params = []
        self.type = 0
        self.val = 0.
        self.var = 0
        self.fun = 0
        self.curval = 0.
        self.curpos = 0
        self.condpos = -1
        self.stoppos = 0
        self.branching = 0
        self.dyn_status = NodeDynStatus.UNUSED

    def clone(self, prog, parent):
        if self.type == NodeType.VAL:
            cnode = create_val(self.val, prog, parent)
        elif self.type == NodeType.VAR:
            cnode = create_var(self.var, prog, parent)
        else:
            cnode = create_fun(self.fun, prog, parent)
        cnode.curval = self.curval
        cnode.branching = self.branching
        cnode.dyn_status = self.dyn_status

        for param in self.params:
            cnode.params.append(param.clone(prog, cnode))

        return cnode

    def arity(self):
        if self.type == NodeType.FUN:
            return fun_arity(self.fun)
        else:
            return 0

    def __str__(self):
        if self.type == NodeType.VAL:
            return '%s' % self.val
        elif self.type == NodeType.VAR:
            return "$%s" % self.prog.variable_names[self.var]
        elif self.type == NodeType.FUN:
            return fun2str(self.fun)
        else:
            return '???'
