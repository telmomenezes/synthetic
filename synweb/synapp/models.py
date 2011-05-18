from django.db import models
from syn.io import load_net
from synweb.settings import DB_DIR


class Network(models.Model):
    name = models.CharField(max_length=200)
    nodes = models.PositiveIntegerField(default=0)
    edges = models.PositiveIntegerField(default=0)

    def getnet(self):
        net_path = '%s/net_%d' % (DB_DIR, self.id)
        net = load_net(net_path)
        return net


class DRMap(models.Model):
    net = models.ForeignKey('Network')
    bins = models.PositiveIntegerField()
    data = models.TextField()
    image = models.TextField()
