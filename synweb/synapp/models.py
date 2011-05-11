from django.db import models

class Network(models.Model):
    name = models.CharField(max_length=200)
    net_file = models.FileField(upload_to='nets')
    nodes = models.PositiveIntegerField()
    edges = models.PositiveIntegerField()

class Node(models.Model):
    net = models.ForeignKey('Network')

class Edge(models.Model):
    net = models.ForeignKey('Network')
    orig = models.ForeignKey('Node')
    targ = models.ForeignKey('Edge')

class Histogram(models.Model):
    net = models.ForeignKey('Network')
    bins = models.PositiveIntegerField()
    data = models.TextField()
