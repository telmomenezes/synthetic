from enum import Enum
import random
from netgens.progs.funs import Fun, fun_arity, fun_cond_pos, fun2str


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


def create_random_node_tree(prog, prob_term, parent, min_depth, grow, depth):
    p = random.random()
    if ((not grow) or p > prob_term) and depth < min_depth:
        fun = random.randrange(len(Fun))
        node = create_fun(fun, prog, parent)
        for i in range(node.arity()):
            node.params.append(create_random_node_tree(prog, prob_term, node, min_depth, grow, depth + 1))
    else:
        if random.randrange(2) == 0 and prog.varcount > 0:
            var = random.randint(prog.varcount)
            node = create_var(var, prog, parent)
        else:
            r = random.randrange(10)
            if r == 0:
                val = 0.
            elif r > 5:
                val = random.randint(10)
            else:
                val = random.random()
            node = create_val(val, prog, parent)
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

    def size(self):
        s = 1
        for param in self.params:
            s += param.size(param)
        return s

    def node_by_pos(self, pos):
        if pos == 0:
            return self
        for i in range(len(self.params)):
            nodefound = self.params[i].node_by_pos(pos + 1 + i)
            if nodefound is not None:
                return nodefound
        return None

    def branching_distance(self, node):
        distance = 0
        if self.branching != node.branching:
            distance += 1
        # TODO: check both have same number of params!
        for i in range(len(self.params)):
            distance += self.params[i].branching_distance2(node.params[i])
        return distance

    def clear_branching(self):
        self.branching = -1
        for param in self.params:
            param.clear_branching()

    def __str__(self):
        if self.type == NodeType.VAL:
            return '%s' % self.val
        elif self.type == NodeType.VAR:
            return "$%s" % self.prog.var_names[self.var]
        elif self.type == NodeType.FUN:
            return fun2str(self.fun)
        else:
            return '???'
