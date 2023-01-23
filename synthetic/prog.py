from enum import Enum
import math
import numpy as np


###############
#  FUNCTIONS  #
###############

N_FUNS = 15

SUM = 0
SUB = 1
MUL = 2
DIV = 3
EQ = 4
GRT = 5
LRT = 6
ZER = 7
EXP = 8
LOG = 9
ABS = 10
MIN = 11
MAX = 12
POW = 13
AFF = 14


funs_names = {SUM: '+',
              SUB: '-',
              MUL: '*',
              DIV: '/',
              ZER: 'ZER',
              EQ: '==',
              GRT: '>',
              LRT: '<',
              EXP: 'EXP',
              LOG: 'LOG',
              ABS: 'ABS',
              MIN: 'MIN',
              MAX: 'MAX',
              AFF: 'AFF',
              POW: '^'}


names_funs = {}
for fn, name in funs_names.items():
    names_funs[name] = fn


def str2fun(st):
    if st in names_funs:
        return names_funs[st]
    return None


def fun2str(func):
    if func in funs_names:
        return funs_names[func]
    return None


def fun_cond_pos(func):
    if func == ZER or func == AFF:
        return 1
    elif func == EQ or func == GRT or func == LRT:
        return 2
    else:
        return -1


def fun_arity(func):
    if func in {EXP, LOG, ABS}:
        return 1
    elif func in {SUM, SUB, MUL, DIV, MIN, MAX, POW}:
        return 2
    elif func in {ZER, AFF}:
        return 3
    elif func in {EQ, GRT, LRT}:
        return 4
    # this should not happen
    return 0


###################
#  PROGRAM NODES  #
###################

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
    p = np.random.random()
    if ((not grow) or p > prob_term) and depth < min_depth:
        fun = np.random.randint(0, N_FUNS)
        node = create_fun(fun, prog, parent)
        for i in range(node.arity()):
            node.params.append(create_random_node_tree(prog, prob_term, node, min_depth, grow, depth + 1))
    else:
        if np.random.randint(0, 2) == 0 and prog.varcount > 0:
            var = np.random.randint(0, prog.varcount)
            node = create_var(var, prog, parent)
        else:
            r = np.random.randint(0, 10)
            if r == 0:
                val = 0.
            elif r > 5:
                val = np.random.randint(0, 10)
            else:
                val = np.random.random()
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
            s += param.size()
        return s

    def node_by_pos(self, pos):
        if pos == 0:
            return self
        cur_pos = 1
        for i in range(len(self.params)):
            param = self.params[i]
            s = param.size()
            if pos < cur_pos + s:
                return param.node_by_pos(pos - cur_pos)
            cur_pos += s
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
            return str(self.val)
        elif self.type == NodeType.VAR:
            return '${}'.format(self.prog.var_names[self.var])
        elif self.type == NodeType.FUN:
            return fun2str(self.fun)
        else:
            return '???'


##############
#  PROGRAMS  #
##############

def token_start(prog_str, pos):
    curpos = pos
    curchar = prog_str[curpos]
    while curchar in {' ', '\n', '\t', '\r', ')', '(', 0}:
        curpos += 1
        curchar = prog_str[curpos]
    return curpos


def token_end(prog_str, pos):
    curpos = pos
    curchar = prog_str[curpos]
    while curchar not in {' ', '\n', '\t', '\r', ')', '(', 0}:
        curpos += 1
        if curpos >= len(prog_str):
            return curpos
        curchar = prog_str[curpos]
    return curpos


def parse(prog_str, var_names, prog=None, parent=None):
    if prog is None:
        prog = Prog(var_names)

    start = token_start(prog_str, prog.parse_pos)
    end = token_end(prog_str, start)
    token = prog_str[start:end]

    try:
        val = float(token)
        node = create_val(val, prog, parent)
    except ValueError:
        if token[0] == '$':
            var = prog.variable_indices[token[1:]]
            node = create_var(var, prog, parent)
        else:
            fun = str2fun(token)
            node = create_fun(fun, prog, parent)

            prog.parse_pos = end

            for i in range(node.arity()):
                parse(prog_str, vars, prog, node)
                param = prog.root
                node.params.append(param)

            prog.root = node
            return prog

    prog.parse_pos = end

    prog.root = node
    return prog


def load(var_names, file_path):
    with open(file_path) as f:
        lines = f.readlines()
    lines = [x.strip() for x in lines]

    prog_str = ''
    for line in lines:
        if len(line) > 0 and line[0] != '#':
            prog_str += line

    return parse(prog_str, var_names)


def create_random(var_names, prob_term=.4, depth_low_limit=2, depth_high_limit=5, grow=None):
    if grow is None:
        grow = np.random.randint(0, 2) == 0
    max_depth = depth_low_limit + np.random.randint(0, depth_high_limit - depth_low_limit)
    prog = Prog(var_names)
    prog.root = create_random_node_tree(prog, prob_term, None, max_depth, grow, 0)
    return prog


class Prog(object):
    def __init__(self, var_names):
        self.varcount = len(var_names)
        self.vars = np.zeros(self.varcount)
        self.root = None
        self.var_names = var_names
        self.variable_indices = {}
        for i in range(self.varcount):
            self.variable_indices[var_names[i]] = i
        self.parse_pos = 0

    def clone(self):
        cprog = Prog(self.var_names)
        if self.root is not None:
            cprog.root = self.root.clone(cprog, None)
        return cprog

    def eval(self):
        curnode = self.root
        curnode.curpos = -1
        val = 0.

        while curnode is not None:
            curnode.curpos += 1
            if curnode.curpos < curnode.stoppos:
                if curnode.curpos == curnode.condpos:
                    if curnode.fun == EQ:
                        if curnode.params[0].curval == curnode.params[1].curval:
                            curnode.stoppos = 3
                        else:
                            curnode.stoppos = 4
                            curnode.curpos += 1
                    elif curnode.fun == GRT:
                        if curnode.params[0].curval > curnode.params[1].curval:
                            curnode.stoppos = 3
                        else:
                            curnode.stoppos = 4
                            curnode.curpos += 1
                    elif curnode.fun == LRT:
                        if curnode.params[0].curval < curnode.params[1].curval:
                            curnode.stoppos = 3
                        else:
                            curnode.stoppos = 4
                            curnode.curpos += 1
                    elif curnode.fun == ZER:
                        if curnode.params[0].curval == 0:
                            curnode.stoppos = 2
                        else:
                            curnode.stoppos = 3
                            curnode.curpos += 1
                    elif curnode.fun == AFF:
                        g = round(curnode.params[0].curval)
                        id1 = round(self.vars[0])
                        id2 = round(self.vars[1])
                        if (g == 0) or ((id1 % g) == (id2 % g)):
                            curnode.stoppos = 2
                        else:
                            curnode.stoppos = 3
                            curnode.curpos += 1

                    # update branching info
                    if curnode.branching < 0:
                        curnode.branching = curnode.stoppos
                    elif curnode.branching != curnode.stoppos:
                        curnode.branching = 0

                curnode = curnode.params[curnode.curpos]
                curnode.curpos = -1
            else:
                if curnode.type == NodeType.FUN:
                    if curnode.fun == SUM:
                        val = curnode.params[0].curval + curnode.params[1].curval
                    elif curnode.fun == SUB:
                        val = curnode.params[0].curval - curnode.params[1].curval
                    elif curnode.fun == MUL:
                        val = curnode.params[0].curval * curnode.params[1].curval
                    elif curnode.fun == DIV:
                        if curnode.params[1].curval == 0:
                            val = 0
                        else:
                            val = curnode.params[0].curval / curnode.params[1].curval
                    elif curnode.fun == MIN:
                        val = curnode.params[0].curval
                        if curnode.params[1].curval < val:
                            val = curnode.params[1].curval
                    elif curnode.fun == MAX:
                        val = curnode.params[0].curval
                        if curnode.params[1].curval > val:
                            val = curnode.params[1].curval
                    elif curnode.fun == EXP:
                        try:
                            val = math.exp(curnode.params[0].curval)
                        except OverflowError:
                            # TODO: not sure if best solution, but using
                            # a very large float could lead to more overflows
                            val = 0
                    elif curnode.fun == LOG:
                        if curnode.params[0].curval <= 0:
                            val = 0
                        else:
                            val = math.log(curnode.params[0].curval)
                    elif curnode.fun == ABS:
                        val = abs(curnode.params[0].curval)
                    elif curnode.fun == POW:
                        try:
                            val = math.pow(curnode.params[0].curval, curnode.params[1].curval)
                        except OverflowError:
                            # TODO: not sure if best solution, but using
                            # a very large float could lead to more overflows
                            val = 0
                        except ValueError:
                            val = 0
                    elif curnode.fun in {EQ, GRT, LRT, ZER, AFF}:
                        val = curnode.params[curnode.stoppos - 1].curval
                elif curnode.type == NodeType.VAR:
                    val = self.vars[curnode.var]
                elif curnode.type == NodeType.VAL:
                    val = curnode.val

                # update dynamic status
                if curnode.dyn_status == NodeDynStatus.UNUSED:
                    curnode.dyn_status = NodeDynStatus.CONSTANT
                elif curnode.dyn_status == NodeDynStatus.CONSTANT:
                    if curnode.curval != val:
                        curnode.dyn_status = NodeDynStatus.DYNAMIC

                # update and move to next node
                curnode.curval = val
                curnode = curnode.parent

        return val

    def write(self, file_path):
        with open(file_path, 'w') as f:
            f.write(str(self))

    def size(self):
        return self.root.size()

    def node_by_pos(self, pos):
        return self.root.node_by_pos(pos)

    def recombine(self, parent2):
        if np.random.randint(0, 2) == 0:
            parent_a = parent2.clone()
            parent_b = self.clone()
        else:
            parent_b = parent2.clone()
            parent_a = self.clone()

        child = parent_a.clone()
        size1 = parent_a.size()
        size2 = parent_b.size()
        pos1 = np.random.randint(0, size1)
        pos2 = np.random.randint(0, size2)

        point1 = child.node_by_pos(pos1)
        point2 = parent_b.node_by_pos(pos2)
        point1parent = point1.parent

        parampos = 0

        # remove sub-tree from child
        # find point1 position in it's parent's param array
        if point1parent is not None:
            for i in range(point1parent.arity()):
                if point1parent.params[i] == point1:
                    parampos = i

        # copy sub-tree from parent 2 to parent 1
        point2clone = point2.clone(child, point1parent)
        if point1parent is not None:
            point1parent.params[parampos] = point2clone
        else:
            child.root = point2clone

        return child

    def clear_branching(self):
        self.clear_branching()

    def branching_distance(self, prg):
        return self.root.branching_distance(prg.root)

    def compare_branching(self, prg):
        return self.branching_distance(prg) == 0

    def dyn_pruning(self, node=None, parent=None, param_pos=-1):
        if node is None:
            node = self.root
        else:
            # nodes with constant value
            if node.dyn_status == NodeDynStatus.CONSTANT:
                parent[param_pos] = create_val(node.curval, self, parent)

            # conditions with constant branching
            if node.condpos > 0:
                branch1 = node.params[node.condpos]
                branch2 = node.params[node.condpos + 1]

                branch = -1

                if branch1.dyn_status == NodeDynStatus.UNUSED:
                    branch = node.condpos + 1
                elif branch2.dyn_status == NodeDynStatus.UNUSED:
                    branch = node.condpos

                if branch > 0:
                    node.params[branch].branching = node.branching
                    node.params[branch].dyn_status = node.dyn_status
                    parent[param_pos] = node.params[branch]

        for i in range(len(node.params)):
            self.dyn_pruning(node.params[i], node, i)

    def build_str(self, node, indent, cur_str):
        out = cur_str
        ind = indent

        if node.arity() > 0:
            if node.parent is not None:
                out = '{}\n'.format(out)
            out = '{}{}('.format(out, ' ' * indent)
            ind += 1

        out = '{}{}'.format(out, node)

        for param in node.params:
            out = '{} '.format(out)
            out = self.build_str(param, ind, out)

        if node.arity() > 0:
            out = '{})'.format(out)

        return out

    def __str__(self):
        return self.build_str(self.root, 0, '')
