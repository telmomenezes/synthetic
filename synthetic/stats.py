from enum import Enum
import numpy as np
import igraph
from pyemd import emd


class StatsSet(object):
    def __init__(self, net, stat_types, bins, ref_stats=None):
        self.stat_types = stat_types
        if ref_stats is None:
            self.stats = [create_stat(net, stat_type, bins) for stat_type in stat_types]
        else:
            assert(len(stat_types) == len(ref_stats))
            self.stats = [create_stat(net, stat_types[i], bins, ref_stat=ref_stats[i])
                          for i in range(len(stat_types))]


class StatType(Enum):
    DEGREES = 0
    IN_DEGREES = 1
    OUT_DEGREES = 2
    D_PAGERANKS = 3
    U_PAGERANKS = 4
    TRIAD_CENSUS = 5
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


def create_stat(net, stat_type, bins=None, ref_stat=None):
    if stat_type == StatType.DEGREES:
        return Degrees(net, stat_type, bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.INDEGREES:
        return InDegrees(net, stat_type, bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.OUTDEGREES:
        return OutDegrees(net, stat_type, bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.D_PAGERANKS:
        return DirectedPageRanks(net, stat_type, bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.U_PAGERANKS:
        return UndirectedPageRanks(net, stat_type, bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.TRIAD_CENSUS:
        return TriadCensus(net, stat_type)
    elif stat_type == StatType.D_DISTS:
        return DirectedDistances(net, stat_type, bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.U_DISTS:
        return UndirectedDistances(net, stat_type, bins=bins, ref_stat=ref_stat)
    else:
        # TODO: exception
        pass


class DistanceType(Enum):
    SIMPLE = 0
    EARTH_MOVER = 1


class Stat(object):
    def __init__(self, net, stat_type):
        self.stat_type = stat_type
        self.data = self.__compute(net)

    def __compute(self, net):
        # TODO: exception
        return None

    def distance(self, stat, distance_type):
        if distance_type == DistanceType.SIMPLE:
            return abs(self.data - stat.data)
        else:
            # TODO: exception
            pass

    def name(self):
        return stat_type2str(self.stat_type)


class Distrib(Stat):
    def distance(self, stat, distance_type):
        assert(isinstance(stat, Distrib))
        if distance_type == DistanceType.SIMPLE:
            dist = 0
            for i in range(len(self.data)):
                d = stat.data[i]
                if d == 0:
                    d = 1
                dist += abs(self.data[i] - stat.data[i]) / d
            return dist
        elif distance_type == DistanceType.EARTH_MOVER:
            pass
        else:
            # TODO: exception
            pass


class Histogram(Distrib):
    def __init__(self, net, stat_type, bins, ref_stat):
        self.bins = bins
        self.ref_stat = ref_stat
        self.max_value = 0
        self.bin_edges = None
        Distrib.__init__(self, net, stat_type)

    def __compute(self, net):
        values = self.__values(net)
        if self.ref_stat is None:
            self.max_value = np.max(values)
        else:
            self.max_value = self.ref_stat.max_value
        self.bin_edges, histogram = np.histogram(values, bins=self.bins, range=(0, self.max_value))
        return histogram

    def distance(self, stat, distance_type):
        assert(isinstance(stat, Histogram))
        if distance_type == DistanceType.EARTH_MOVER:
            bin_locs = np.mean([self.bin_edges[:-1], self.bin_edges[1:]], axis=0)
            bins = len(bin_locs)

            distance_matrix = np.abs(np.repeat(bin_locs, bins) - np.tile(bin_locs, bins))
            distance_matrix = distance_matrix.reshape(bins, bins)
            assert(len(distance_matrix) == len(distance_matrix[0]))
            assert(self.data.shape[0] <= len(distance_matrix))
            assert(stat.data.shape[0] <= len(distance_matrix))

            return emd(self.data, stat.data, distance_matrix)
        else:
            return Histogram.distance(self, stat, distance_type)

    def __values(self, net):
        # TODO: exception
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


class DirectedPageRanks(Histogram):
    def __values(self, net):
        return net.pagerank(vertices=net.vs, directed=True)


class UndirectedPageRanks(Histogram):
    def __values(self, net):
        return net.pagerank(vertices=net.vs, directed=False)


class TriadCensus(Distrib):
    def __values(self, net):
        return net.triad_census()


class DirectedDistances(Histogram):
    def __values(self, net):
        sp = net.shortest_paths_dijkstra(mode=igraph.OUT)
        # flatten shortest paths length matrix
        return [item for sublist in sp for item in sublist]


class UndirectedDistances(Histogram):
    def __values(self, net):
        sp = net.shortest_paths_dijkstra(mode=igraph.ALL)
        # flatten shortest paths length matrix
        return [item for sublist in sp for item in sublist]
