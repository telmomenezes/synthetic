from enum import Enum
import igraph
from netgens.stats.stats_set import StatsSet


class Norm(Enum):
    NONE = 0
    ER_MEAN_RATIO = 1


class Distances(object):
    def __init__(self, net, stat_types, dist_types, bins, norm=Norm.NONE, norm_samples=0):
        assert(norm == Norm.NONE or norm_samples > 0)

        self.stat_types = stat_types
        self.dist_types = dist_types
        self.bins = bins
        self.norm = norm

        self.nstats = len(self.stat_types)
        self.targ_stats_set = StatsSet(net, stat_types, bins)

        if norm != Norm.NONE:
            self.norm_values = self.__compute_norm_values(net, norm_samples)
        else:
            self.norm_values = None

    def __compute_norm_values(self, net, norm_samples):
        vcount = net.vcount()
        ecount = net.ecount()
        directed = net.is_directed()

        norm_values = [.0] * self.nstats
        dists = Distances(net, self.stat_types, self.bins, norm=Norm.NONE)

        for i in range(norm_samples):
            sample_net = igraph.Graph.Erdos_Renyi(n=vcount, m=ecount, directed=directed)
            values = dists.compute(sample_net)
            for j in range(self.nstats):
                norm_values[j] += values[j]

        norm_values = [x / norm_samples for x in norm_values]
        return norm_values

    def compute(self, net):
        stats_set = StatsSet(net, self.stat_types, self.bins, ref_stats=self.targ_stats_set)

        dists = [self.targ_stats_set.stats[i].distance(stats_set.stats[i], self.dist_types[self.stat_types[i]])
                 for i in range(self.nstats)]
        if self.norm == Norm.ER_MEAN_RATIO:
            very_small = .999
            dists = [max(d, very_small) for d in dists]
            dists = [dists[i] / self.norm_values[i] for i in range(self.nstats)]

        return dists
