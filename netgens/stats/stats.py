from enum import Enum
import numpy as np
import igraph


class StatType(Enum):
    DEGREES = 0
    IN_DEGREES = 1
    OUT_DEGREES = 2
    D_PAGERANKS = 3
    U_PAGERANKS = 4
    TRIADIC_PROFILE = 5
    D_DISTS = 6
    U_DISTS = 7


def stat_type2str(stat_type):
    return stat_type.name


stat_types__names = {}
names__type_stats = {}

for st in StatType:
    stat_name = stat_type2str(st)
    stat_types__names[st] = stat_name
    names__type_stats[stat_name] = st


def str2stat_type(name):
    if name in names__type_stats:
        return names__type_stats[name]
    return None


def create_stat(net, stat_type, bins=None):
    if stat_type == StatType.DEGREES:
        return Degrees(net, stat_type, bins=bins)
    elif stat_type == StatType.INDEGREES:
        return InDegrees(net, stat_type, bins=bins)
    elif stat_type == StatType.OUTDEGREES:
        return OutDegrees(net, stat_type, bins=bins)
    else:
        # TODO: exception
        pass


class Stat(object):
    def __init__(self, net, stat_type):
        self.stat_type = stat_type
        self.data = self.__compute(net)

    def __compute(self, net):
        return None

    def distance(self, stat):
        return 0

    def name(self):
        return stat_type2str(self.stat_type)


class Distrib(Stat):
    def distance(self, stat):
        assert(isinstance(stat, Distrib))
        return 0


class Histogram(Distrib):
    def __init__(self, net, stat_type, bins):
        self.bins = bins
        Distrib.__init__(self, net, stat_type)

    def __compute(self, net):
        return np.histogram(self.__values(net), bins=self.bins)

    def __values(self, net):
        return []


class Degrees(Histogram):
    def __values(self, net):
        return net.degree(net.vs, mode=igraph.ALL)


class InDegrees(Histogram):
    def __values(self, net):
        return net.indegree(net.vs)


class OutDegrees(Histogram):
    def __values(self, net):
        return net.outdegree(net.vs)
