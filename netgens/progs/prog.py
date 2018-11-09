import math
import random
import numpy as np
from netgens.progs.node import Node, NodeType, NodeDynStatus
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

    node = Node(prog)

    try:
        val = float(token)
        node.init_val(val, parent)
    except ValueError:
        if token[0] == '$':
            var = prog.variable_indices[token.substring[1:]]
            node.init_var(var, parent)
        else:
            fun = str2fun(token)
            node.init_fun(fun, parent)

            prog.parse_pos = end

            for i in range(node.arity):
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

    def init_random2(self, prob_term, parent, min_depth, grow, depth):
        node = Node(self)
        p = random.random()
        if ((not grow) or p > prob_term) and depth < min_depth:
            fun = random.randrange(len(Fun))
            node.init_fun(fun, parent)
            for i in range(node.arity):
                node.params[i] = self.init_random2(prob_term, node, min_depth, grow, depth + 1)
        else:
            if random.randrange(2) == 0 and self.varcount > 0:
                var = random.randint(self.varcount)
                node.init_var(var, parent)
            else:
                r = random.randrange(10)
                if r == 0:
                    val = 0.
                elif r > 5:
                    val = random.randint(10)
                else:
                    val = random.random()
                node.init_val(val, parent)
        return node

    def init_random(self):
        prob_term = .4
        depth_low_limit = 2
        depth_high_limit = 5
        grow = random.randrange(2) == 0
        max_depth = depth_low_limit + random.randrange(depth_high_limit - depth_low_limit)
        self.root = self.init_random2(prob_term, None, max_depth, grow, 0)

    def clone_node(self, node, parent):
        cnode = Node(self)
        if node.type == NodeType.VAL:
            cnode.init_val(node.val, parent)
        elif node.type == NodeType.VAR:
            cnode.init_var(node.var, parent)
        else:
            cnode.init_fun(node.fun, parent)
        cnode.curval = node.curval
        cnode.branching = node.branching
        cnode.dyn_status = node.dyn_status

        for i in range(node.arity):
            cnode.params[i] = self.clone_node(node.params[i], cnode)
        return cnode

    def clone(self):
        ctree = Prog(self.var_names)
        ctree.root = self.clone_node(self.root, None)
        return ctree

    def size2(self, node):
        c = 1
        for i in range(node.arity):
            c += self.size2(node.params[i])
        return c

    def size(self):
        return self.size2(self.root)

    def node_by_pos2(self, node, pos, curpos):
        if pos == curpos[0]:
            return node

        curpos[0] += 1
        for i in range(node.arity):
            nodefound = self.node_by_pos2(node.params[i], pos, curpos)
            if nodefound is not None:
                return nodefound

        return None

    def node_by_pos(self, pos):
        curpos = [0]
        return self.node_by_pos2(self.root, pos, curpos)

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
        point2clone = self.clone_node(point2, point1parent)
        if point1parent is not None:
            point1parent.params[parampos] = point2clone
        else:
            child.root = point2clone

        return child

    def clear_branching2(self, node):
        node.branching = -1
        for i in range(node.arity):
            self.clear_branching2(node.params[i])

    def branching_distance(self, tree):
        return self.branching_distance2(self.root, tree.root)

    def branching_distance2(self, node1, node2):
        distance = 0
        if node1.branching != node2.branching:
            distance += 1
        for i in range(node1.arity):
            distance += self.branching_distance2(node1.params[i], node2.params[i])
        return distance

    def compare_branching(self, tree):
        return self.branching_distance(tree) == 0

    def move_up(self, orig_node, targ_node):
        if orig_node.type == NodeType.VAL:
            targ_node.init_val(orig_node.val, orig_node.parent)
        elif orig_node.type == NodeType.VAR:
            targ_node.init_var(orig_node.var, orig_node.parent)
        else:
            targ_node.init_fun(orig_node.fun, orig_node.parent)
        targ_node.branching = orig_node.branching
        targ_node.dyn_status = orig_node.dyn_status

        for i in range(orig_node.arity):
            targ_node.params[i] = orig_node.params[i]
            targ_node.params[i].parent = targ_node

    def dyn_pruning(self):
        self.dyn_pruning2(self.root)

    def dyn_pruning2(self, node):
        # nodes with constant value
        if node.dyn_status == NodeDynStatus.CONSTANT:
            node.init_val(node.curval, node.parent)

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
                self.move_up(node.params[branch], node)

        for i in range(node.arity):
            self.dyn_pruning2(node.params[i])

    def build_str(self, node, indent, cur_str):
        out = cur_str
        ind = indent

        if node.arity > 0:
            if node.parent is not None:
                out = '%s\n' % out
            out = '%s%s(' % (out, ' ' * indent)
            ind += 1

        out = '%s%s' % (out, node)

        for i in range(node.arity):
            out = '%s ' % out
            out = self.build_str(node.params[i], ind, out)

        if node.arity > 0:
            out = '%s)' % out

        return out

    def __str__(self):
        return self.build_str(self.root, 0, '')
