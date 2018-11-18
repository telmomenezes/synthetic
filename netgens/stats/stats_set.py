from netgens.stats.stats import create_stat


class StatsSet(object):
    def __init__(self, net, stat_types, bins):
        self.stat_types = stat_types
        self.stats = [create_stat(net, stat_type, bins) for stat_type in stat_types]
