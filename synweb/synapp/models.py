from django.db import models
from syn.io import load_net
from synweb.settings import DB_DIR


class Network(models.Model):
    name = models.CharField(max_length=200)
    nodes = models.PositiveIntegerField(default=0)
    edges = models.PositiveIntegerField(default=0)
    notes = models.TextField()
    drmap = models.ForeignKey('DRMap', related_name='+', null=True, blank=True)

    def getnet(self):
        net_path = '%s/net_%d' % (DB_DIR, self.id)
        net = load_net(net_path)
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
