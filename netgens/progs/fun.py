from enum import Enum


class Fun(Enum):
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


funs_names = {Fun.SUM: '+',
              Fun.SUB: '-',
              Fun.MUL: '*',
              Fun.DIV: '/',
              Fun.ZER: 'ZER',
              Fun.EQ: '==',
              Fun.GRT: '>',
              Fun.LRT: '<',
              Fun.EXP: 'EXP',
              Fun.LOG: 'LOG',
              Fun.ABS: 'ABS',
              Fun.MIN: 'MIN',
              Fun.MAX: 'MAX',
              Fun.AFF: 'AFF',
              Fun.POW: '^'}


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
    if func == Fun.ZER or func == Fun.AFF:
        return 1
    elif func == Fun.EQ or func == Fun.GRT or func == Fun.LRT:
        return 2
    else:
        return -1


def fun_arity(func):
    if func in {Fun.EXP, Fun.LOG, Fun.ABS}:
        return 1
    elif func in {Fun.SUM, Fun.SUB, Fun.MUL, Fun.DIV, Fun.MIN, Fun.MAX, Fun.POW}:
        return 2
    elif func in {Fun.ZER, Fun.AFF}:
        return 3
    elif func in {Fun.EQ, Fun.GRT, Fun.LRT}:
        return 4
    # this should not happen
    return 0
