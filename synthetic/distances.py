from enum import Enum

import progressbar

from synthetic.consts import DEFAULT_BINS, DEFAULT_MAX_DIST, DEFAULT_NORM_SAMPLES, SMALL_VALUE
from synthetic.net import create_random_net
from synthetic.stats import StatType, DistanceType, StatsSet
from synthetic.utils import current_time_millis


DEFAULT_UNDIRECTED = ((StatType.DEGREES, DistanceType.EARTH_MOVER),
                      (StatType.U_PAGERANKS, DistanceType.EARTH_MOVER),
                      (StatType.TRIAD_CENSUS, DistanceType.NORMALIZED_MANHATTAN),
                      (StatType.U_DISTS, DistanceType.NORMALIZED_MANHATTAN))


DEFAULT_DIRECTED = ((StatType.IN_DEGREES, DistanceType.EARTH_MOVER),
                    (StatType.OUT_DEGREES, DistanceType.EARTH_MOVER),
                    (StatType.U_PAGERANKS, DistanceType.EARTH_MOVER),
                    (StatType.D_PAGERANKS, DistanceType.EARTH_MOVER),
                    (StatType.TRIAD_CENSUS, DistanceType.NORMALIZED_MANHATTAN),
                    (StatType.U_DISTS, DistanceType.NORMALIZED_MANHATTAN),
                    (StatType.D_DISTS, DistanceType.NORMALIZED_MANHATTAN))


class Norm(Enum):
    NONE = 0
    ER_MEAN_RATIO = 1


def create_distances_to_net(net, bins=DEFAULT_BINS, max_dist=DEFAULT_MAX_DIST, rw=False, norm=Norm.ER_MEAN_RATIO,
                            norm_samples=DEFAULT_NORM_SAMPLES):
    if net.graph.is_directed():
        stat_dist_types = DEFAULT_DIRECTED
    else:
        stat_dist_types = DEFAULT_UNDIRECTED
    return DistancesToNet(net, stat_dist_types, bins, max_dist, rw, norm, norm_samples)


class DistancesToNet(object):
    def __init__(self, net, stat_dist_types, bins, max_dist, rw, norm=Norm.ER_MEAN_RATIO,
                 norm_samples=DEFAULT_NORM_SAMPLES, targ_stats_set=None):
        assert(norm == Norm.NONE or norm_samples > 0)

        self.net = net
        self.stat_dist_types = stat_dist_types
        self.bins = bins
        self.max_dist = max_dist
        self.rw = rw
        self.norm = norm

        self.nstats = len(self.stat_dist_types)
        self.stat_types = [item[0] for item in stat_dist_types]
        self.dist_types = [item[1] for item in stat_dist_types]

        if targ_stats_set is None:
            self.targ_stats_set = StatsSet(net, self.stat_types, bins, max_dist, rw)
        else:
            self.targ_stats_set = targ_stats_set

        if norm != Norm.NONE:
            self.norm_values = self._compute_norm_values(net, norm_samples)
        else:
            self.norm_values = None

    def _compute_norm_values(self, net, norm_samples):
        start_time = current_time_millis()

        norm_values = [.0] * self.nstats
        dists2net = DistancesToNet(net, self.stat_dist_types, self.bins, self.max_dist, self.rw, norm=Norm.NONE,
                                   targ_stats_set=self.targ_stats_set)
        i = 0
        print('computing normalization samples...')
        with progressbar.ProgressBar(max_value=norm_samples) as bar:
            for i in range(norm_samples):
                bar.update(i)
                sample_net = create_random_net(net.graph.vcount(), net.graph.ecount(), net.graph.is_directed())
                dists = dists2net.compute(sample_net)
                for j in range(self.nstats):
                    norm_values[j] += dists[j]
        norm_values = [max(x / norm_samples, SMALL_VALUE) for x in norm_values]

        comp_time = (current_time_millis() - start_time) / 1000
        print('{}s'.format(comp_time))

        return norm_values

    def compute(self, net):
        stats_set = StatsSet(net, self.stat_types, self.bins, self.max_dist, self.rw, ref_stats=self.targ_stats_set)

        dists = [self.targ_stats_set.stats[i].distance(stats_set.stats[i], self.dist_types[i])
                 for i in range(self.nstats)]
        if self.norm == Norm.ER_MEAN_RATIO:
            dists = [dists[i] / self.norm_values[i] for i in range(self.nstats)]

        return dists
