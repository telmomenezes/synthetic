from django.db import models
from syn.io import load_net
from synweb.settings import DB_DIR


class Network(models.Model):
    name = models.CharField(max_length=200)
    nodes = models.PositiveIntegerField(default=0)
    edges = models.PositiveIntegerField(default=0)
    temporal = models.PositiveIntegerField(default=0)
    min_ts = models.PositiveIntegerField(default=0)
    max_ts = models.PositiveIntegerField(default=0)
    notes = models.TextField()
    drmap = models.ForeignKey('DRMap', related_name='+', null=True, blank=True)

    def getnet(self, min_ts=-1, max_ts=-1):
        net_path = '%s/net_%d' % (DB_DIR, self.id)
        net = load_net(net_path, min_ts, max_ts)
        return net


class DRMap(models.Model):
    net = models.ForeignKey('Network', related_name='+')
    bins = models.PositiveIntegerField()
    steps = models.PositiveIntegerField()
    data = models.TextField()
    min_hor = models.FloatField()
    max_hor = models.FloatField()
    min_ver = models.FloatField()
    max_ver = models.FloatField()
