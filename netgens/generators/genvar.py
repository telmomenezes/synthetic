from enum import Enum


class GenVar(Enum):
    ORIGID = 0
    TARGID = 1
    ORIGDEG = 2
    ORIGINDEG = 3
    ORIGOUTDEG = 4
    TARGDEG = 5
    TARGINDEG = 6
    TARGOUTDEG = 7
    DIST = 8
    DIRDIST = 9
    REVDIST = 10


genvars_names = {GenVar.ORIGID: 'origId',
                 GenVar.TARGID: 'targId',
                 GenVar.ORIGDEG: 'origDeg',
                 GenVar.ORIGINDEG: 'origInDeg',
                 GenVar.ORIGOUTDEG: 'origOutDeg',
                 GenVar.TARGDEG: 'targDeg',
                 GenVar.TARGINDEG: 'targInDeg',
                 GenVar.TARGOUTDEG: 'targOutDeg',
                 GenVar.DIST: 'dist',
                 GenVar.DIRDIST: 'dirDist',
                 GenVar.REVDIST: 'revDist'}


names_genvars = {}
for genvar, name in genvars_names.items():
    names_genvars[name] = genvar


def str2genvar(st):
    if st in names_genvars:
        return names_genvars[st]
    return None


def genvar2str(gvar):
    if gvar in genvars_names:
        return genvars_names[gvar]
    return None
