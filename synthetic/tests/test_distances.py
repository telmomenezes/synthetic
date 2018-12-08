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
        g4 = random_graph_sparse(directed=False)
        g5 = random_graph_medium(directed=False)
        g6 = random_graph_dense(directed=False)

        d2n_1 = create_distances_to_net(g1, bins=10, max_dist=10, norm=Norm.ER_MEAN_RATIO, norm_samples=30)
        dists_1_2 = d2n_1.compute(g2)
        dists_1_3 = d2n_1.compute(g3)
        dists_1_4 = d2n_1.compute(g4)
        dists_1_5 = d2n_1.compute(g5)
        dists_1_6 = d2n_1.compute(g6)

        self.assertEqual(dists_1_3[1], 0.)
        self.assertEqual(dists_1_2[2], dists_1_3[2])
        for i in {0, 2, 3}:
            self.assertGreater(dists_1_4[i], dists_1_5[i])
            self.assertGreater(dists_1_5[i], dists_1_6[i])

    def test_dir_misc(self):
        g1 = full_graph(directed=True)
        g2 = out_star_graph()
        g3 = ring_graph(directed=True)
        g4 = random_graph_sparse(directed=True)
        g5 = random_graph_medium(directed=True)
        g6 = random_graph_dense(directed=True)

        d2n_1 = create_distances_to_net(g1, bins=10, max_dist=10, norm=Norm.ER_MEAN_RATIO, norm_samples=30)
        dists_1_2 = d2n_1.compute(g2)
        dists_1_3 = d2n_1.compute(g3)
        dists_1_4 = d2n_1.compute(g4)
        dists_1_5 = d2n_1.compute(g5)
        dists_1_6 = d2n_1.compute(g6)

        self.assertEqual(dists_1_3[2], 0.)
        self.assertEqual(dists_1_2[4], dists_1_3[4])
        for i in {0, 1, 5, 6}:
            self.assertGreater(dists_1_4[i], dists_1_5[i])
            self.assertGreater(dists_1_5[i], dists_1_6[i])


if __name__ == '__main__':
    unittest.main()
