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

    def test_undir_misc(self):
        g1 = full_graph(directed=False)
        g2 = star_graph()
        g3 = ring_graph(directed=False)
        g4 = random_graph(directed=False)

        fit = create_fitness(g1, bins=10, max_dist=10, norm=Norm.ER_MEAN_RATIO, norm_samples=30)

        fitness_max, fitness_mean, dists = fit.compute(g2)
        print(dists)
        self.assertEqual(fitness_max, 0.)
        self.assertEqual(fitness_mean, 0.)
        self.assertListEqual(dists, [0., 0., 0., 0.])


if __name__ == '__main__':
    unittest.main()
