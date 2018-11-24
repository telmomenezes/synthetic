from enum import Enum
import igraph
from netgens.stats import StatType, DistanceType, StatsSet


DEFAULT_UNDIRECTED = ((StatType.DEGREES, DistanceType.EARTH_MOVER),
                      (StatType.U_PAGERANKS, DistanceType.EARTH_MOVER),
                      (StatType.TRIAD_CENSUS, DistanceType.SIMPLE),
                      (StatType.U_DISTS, DistanceType.SIMPLE))


DEFAULT_DIRECTED = ((StatType.IN_DEGREES, DistanceType.EARTH_MOVER),
                    (StatType.OUT_DEGREES, DistanceType.EARTH_MOVER),
                    (StatType.U_PAGERANKS, DistanceType.EARTH_MOVER),
                    (StatType.D_PAGERANKS, DistanceType.EARTH_MOVER),
                    (StatType.TRIAD_CENSUS, DistanceType.SIMPLE),
                    (StatType.U_DISTS, DistanceType.SIMPLE),
                    (StatType.D_DISTS, DistanceType.SIMPLE))


class Norm(Enum):
    NONE = 0
    ER_MEAN_RATIO = 1


class Fitness(object):
    def __init__(self, net, stat_dist_types, bins, norm=Norm.NONE, norm_samples=0):
        assert(norm == Norm.NONE or norm_samples > 0)

        self.stat_dist_types = stat_dist_types
        self.bins = bins
        self.norm = norm

        self.nstats = len(self.stat_dist_types)
        self.stat_types = [item[0] for item in stat_dist_types]
        self.dist_types = [item[1] for item in stat_dist_types]
        self.targ_stats_set = StatsSet(net, self.stat_types, bins)

        if norm != Norm.NONE:
            self.norm_values = self.__compute_norm_values(net, norm_samples)
        else:
            self.norm_values = None

    def __compute_norm_values(self, net, norm_samples):
        vcount = net.vcount()
        ecount = net.ecount()
        directed = net.is_directed()

        norm_values = [.0] * self.nstats
        dists = Fitness(net, self.stat_dist_types, self.bins, norm=Norm.NONE)

        for i in range(norm_samples):
            sample_net = igraph.Graph.Erdos_Renyi(n=vcount, m=ecount, directed=directed)
            values = dists.compute(sample_net)
            for j in range(self.nstats):
                norm_values[j] += values[j]

        norm_values = [x / norm_samples for x in norm_values]
        return norm_values

    def compute(self, net):
        stats_set = StatsSet(net, self.stat_dist_types, self.bins, ref_stats=self.targ_stats_set)

        dists = [self.targ_stats_set.stats[i].distance(stats_set.stats[i], self.dist_types[self.stat_types[i]])
                 for i in range(self.nstats)]
        if self.norm == Norm.ER_MEAN_RATIO:
            very_small = .999
            dists = [max(d, very_small) for d in dists]
            dists = [dists[i] / self.norm_values[i] for i in range(self.nstats)]

        fitness_max = max(dists)
        fitness_avg = sum(dists) / len(dists)
        return fitness_max, fitness_avg, dists
