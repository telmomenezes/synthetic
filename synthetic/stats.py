from enum import Enum
from abc import ABC, abstractmethod
import math
import numpy as np
import igraph
from pyemd import emd


class StatsSet(object):
    def __init__(self, net, stat_types, bins, max_dist, ref_stats=None):
        self.stat_types = stat_types
        if ref_stats is None:
            self.stats = [create_stat(net, stat_type, bins, max_dist) for stat_type in stat_types]
        else:
            assert(len(stat_types) == len(ref_stats))
            self.stats = [create_stat(net, stat_types[i], bins, max_dist, ref_stat=ref_stats[i])
                          for i in range(len(stat_types))]


class StatType(Enum):
    DEGREES = 0
    IN_DEGREES = 1
    OUT_DEGREES = 2
    U_PAGERANKS = 3
    D_PAGERANKS = 4
    TRIAD_CENSUS = 5
    U_DISTS = 6
    D_DISTS = 7


stat_types__names = {}
names__type_stats = {}

for st in StatType:
    stat_name = st.name
    stat_types__names[st] = stat_name
    names__type_stats[stat_name] = st


def str2stat_type(name):
    if name in names__type_stats:
        return names__type_stats[name]
    return None


def create_stat(net, stat_type, bins=None, max_dist=None, ref_stat=None):
    if stat_type == StatType.DEGREES:
        stat = Degrees(bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.IN_DEGREES:
        stat = InDegrees(bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.OUT_DEGREES:
        stat = OutDegrees(bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.U_PAGERANKS:
        stat = UndirectedPageRanks(bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.D_PAGERANKS:
        stat = DirectedPageRanks(bins=bins, ref_stat=ref_stat)
    elif stat_type == StatType.TRIAD_CENSUS:
        stat = TriadCensus()
    elif stat_type == StatType.U_DISTS:
        stat = UndirectedDistances(max_dist=max_dist)
    elif stat_type == StatType.D_DISTS:
        stat = DirectedDistances(max_dist=max_dist)
    else:
        raise ValueError('unknown statistic type: %s' % stat_type)
    stat.compute(net)
    return stat


class DistanceType(Enum):
    SIMPLE = 0
    EARTH_MOVER = 1


class Stat(ABC):
    def __init__(self):
        self.data = None

    @abstractmethod
    def compute(self, net):
        pass

    def set_data(self, values):
        self.data = values

    def distance(self, stat, distance_type):
        if distance_type == DistanceType.SIMPLE:
            return abs(self.data - stat.data)
        else:
            # TODO: exception
            pass


# Simple distributions
class Distrib(Stat):
    @abstractmethod
    def compute(self, net):
        pass

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


class TriadCensus(Distrib):
    def compute(self, net):
        motifs = net.motifs_randesu(size=3, cut_prob=None)
        counts = []
        for count in motifs:
            if math.isnan(count):
                counts.append(0)
            else:
                counts.append(count)
        self.set_data(counts)


# Histograms
class Histogram(Distrib):
    def __init__(self, bins, ref_stat):
        self.bins = bins
        self.ref_stat = ref_stat
        self.min_value = 0
        self.max_value = None
        self.bin_edges = None
        super().__init__()

    @abstractmethod
    def compute(self, net):
        pass

    def set_data(self, values):
        if self.max_value is None:
            if self.ref_stat is None:
                self.max_value = np.max(values)
            else:
                self.max_value = self.ref_stat.max_value
        self.data, self.bin_edges = np.histogram(values, bins=self.bins, range=(self.min_value, self.max_value))

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
            return Distrib.distance(self, stat, distance_type)


class Degrees(Histogram):
    def compute(self, net):
        values = net.degree(net.vs, mode=igraph.ALL)
        self.set_data(values)


class InDegrees(Histogram):
    def compute(self, net):
        values = net.indegree(net.vs)
        self.set_data(values)


class OutDegrees(Histogram):
    def compute(self, net):
        values = net.outdegree(net.vs)
        self.set_data(values)


class UndirectedPageRanks(Histogram):
    def compute(self, net):
        values = net.pagerank(vertices=net.vs, directed=False)
        self.set_data(values)


class DirectedPageRanks(Histogram):
    def compute(self, net):
        values = net.pagerank(vertices=net.vs, directed=True)
        self.set_data(values)


# Distance histograms
class DistanceHistogram(Histogram):
    def __init__(self, max_dist):
        super().__init__(bins=max_dist, ref_stat=None)
        self.min_value = 1
        self.max_value = max_dist

    @abstractmethod
    def compute(self, net):
        pass


class UndirectedDistances(DistanceHistogram):
    def compute(self, net):
        sp = net.shortest_paths_dijkstra(mode=igraph.ALL)
        # flatten shortest paths length matrix and truncate distance
        values = [min(item, self.max_value) for sublist in sp for item in sublist if item > 0]
        self.set_data(values)


class DirectedDistances(DistanceHistogram):
    def compute(self, net):
        sp = net.shortest_paths_dijkstra(mode=igraph.OUT)
        # flatten shortest paths length matrix and truncate distance
        values = [min(item, self.max_value) for sublist in sp for item in sublist if item > 0]
        self.set_data(values)
