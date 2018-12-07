import unittest
from synthetic.distances import *
from synthetic.tests.graphs import *


class TestDistancesToNet(unittest.TestCase):
    def test_undir_self(self):
        g = full_graph(directed=False)
        d2n = create_distances_to_net(g, bins=10, max_dist=10, norm=Norm.NONE)
        dists = d2n.compute(g)
        self.assertListEqual(dists, [0., 0., 0., 0.])

    def test_dir_self(self):
        g = full_graph(directed=True)
        d2n = create_distances_to_net(g, bins=10, max_dist=10, norm=Norm.NONE)
        dists = d2n.compute(g)
        self.assertListEqual(dists, [0., 0., 0., 0., 0., 0., 0.])

    def test_undir_misc(self):
        g1 = full_graph(directed=False)
        g2 = star_graph()
        g3 = ring_graph(directed=False)
        g4 = random_graph(directed=False)

        d2n = create_distances_to_net(g1, bins=10, max_dist=10, norm=Norm.ER_MEAN_RATIO, norm_samples=30)
        dists = d2n.compute(g2)
        print(dists)
        self.assertListEqual(dists, [0., 0., 0., 0.])


if __name__ == '__main__':
    unittest.main()
