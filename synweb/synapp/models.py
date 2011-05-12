from django.db import models

class Network(models.Model):
    name = models.CharField(max_length=200)
    nodes = models.PositiveIntegerField(default=0)
    edges = models.PositiveIntegerField(default=0)

class Histogram(models.Model):
    net = models.ForeignKey('Network')
    bins = models.PositiveIntegerField()
    data = models.TextField()
