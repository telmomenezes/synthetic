import math
import random
import numpy as np
from netgens.progs.node import create_fun, create_val, create_var, create_random_node_tree, NodeType, NodeDynStatus
from netgens.progs.funs import Fun, str2fun


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
            var = prog.variable_indices[token.substring[1:]]
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
        grow = random.randrange(2) == 0
    max_depth = depth_low_limit + random.randrange(depth_high_limit - depth_low_limit)
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
                    if curnode.fun == Fun.EQ:
                        if curnode.params[0].curval == curnode.params[1].curval:
                            curnode.stoppos = 3
                        else:
                            curnode.stoppos = 4
                            curnode.curpos += 1
                    elif curnode.fun == Fun.GRT:
                        if curnode.params[0].curval > curnode.params[1].curval:
                            curnode.stoppos = 3
                        else:
                            curnode.stoppos = 4
                            curnode.curpos += 1
                    elif curnode.fun == Fun.LRT:
                        if curnode.params[0].curval < curnode.params[1].curval:
                            curnode.stoppos = 3
                        else:
                            curnode.stoppos = 4
                            curnode.curpos += 1
                    elif curnode.fun == Fun.ZER:
                        if curnode.params[0].curval == 0:
                            curnode.stoppos = 2
                        else:
                            curnode.stoppos = 3
                            curnode.curpos += 1
                    elif curnode.fun == Fun.AFF:
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
                    if curnode.fun == Fun.SUM:
                        val = curnode.params[0].curval + curnode.params[1].curval
                    elif curnode.fun == Fun.SUB:
                        val = curnode.params[0].curval - curnode.params[1].curval
                    elif curnode.fun == Fun.MUL:
                        val = curnode.params[0].curval * curnode.params[1].curval
                    elif curnode.fun == Fun.DIV:
                        if curnode.params[1].curval == 0:
                            val = 0
                        else:
                            val = curnode.params[0].curval / curnode.params[1].curval
                    elif curnode.fun == Fun.MIN:
                        val = curnode.params[0].curval
                        if curnode.params[1].curval < val:
                            val = curnode.params[1].curval
                    elif curnode.fun == Fun.MAX:
                        val = curnode.params[0].curval
                        if curnode.params[1].curval > val:
                            val = curnode.params[1].curval
                    elif curnode.fun == Fun.EXP:
                        val = math.exp(curnode.params[0].curval)
                    elif curnode.fun == Fun.LOG:
                        if curnode.params[0].curval == 0:
                            val = 0
                        else:
                            val = math.log(curnode.params[0].curval)
                    elif curnode.fun == Fun.ABS:
                        val = abs(curnode.params[0].curval)
                    elif curnode.fun == Fun.POW:
                        val = math.pow(curnode.params[0].curval, curnode.params[1].curval)
                    elif curnode.fun in {Fun.EQ, Fun.GRT, Fun.LRT, Fun.ZER, Fun.AFF}:
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
        if random.randrange(2) == 0:
            parent_a = parent2.clone()
            parent_b = self.clone()
        else:
            parent_b = parent2.clone()
            parent_a = self.clone()

        child = parent_a.clone()
        size1 = parent_a.size()
        size2 = parent_b.size()
        pos1 = random.randrange(size1)
        pos2 = random.randrange(size2)

        point1 = child.node_by_pos(pos1)
        point2 = parent_b.node_by_pos(pos2)
        point1parent = point1.parent

        parampos = 0

        # remove sub-tree from child
        # find point1 position in it's parent's param array
        if point1parent is not None:
            for i in range(point1parent.arity):
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
                out = '%s\n' % out
            out = '%s%s(' % (out, ' ' * indent)
            ind += 1

        out = '%s%s' % (out, node)

        for param in node.params:
            out = '%s ' % out
            out = self.build_str(param, ind, out)

        if node.arity() > 0:
            out = '%s)' % out

        return out

    def __str__(self):
        return self.build_str(self.root, 0, '')
