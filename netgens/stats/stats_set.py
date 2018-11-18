from netgens.stats.stats import create_stat


class StatsSet(object):
    def __init__(self, net, stat_types, bins, ref_stats=None):
        self.stat_types = stat_types
        if ref_stats is None:
            self.stats = [create_stat(net, stat_type, bins) for stat_type in stat_types]
        else:
            assert(len(stat_types) == len(ref_stats))
            self.stats = [create_stat(net, stat_types[i], bins, ref_stat=ref_stats[i])
                          for i in range(len(stat_types))]
