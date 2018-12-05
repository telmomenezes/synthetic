import unittest
from synthetic.fitness import *
from synthetic.tests.graphs import *


class TestFitness(unittest.TestCase):
    def test_undir_self(self):
        g = full_graph(directed=False)
        fit = create_fitness(g, bins=10, max_dist=10, norm=Norm.NONE)
        fitness_max, fitness_mean, dists = fit.compute(g)
        self.assertEqual(fitness_max, 0.)
        self.assertEqual(fitness_mean, 0.)
        self.assertListEqual(dists, [0., 0., 0., 0.])

    def test_dir_self(self):
        g = full_graph(directed=True)
        fit = create_fitness(g, bins=10, max_dist=10, norm=Norm.NONE)
        fitness_max, fitness_mean, dists = fit.compute(g)
        self.assertEqual(fitness_max, 0.)
        self.assertEqual(fitness_mean, 0.)
        self.assertListEqual(dists, [0., 0., 0., 0., 0., 0., 0.])


if __name__ == '__main__':
    unittest.main()
