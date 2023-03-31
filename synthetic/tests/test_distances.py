import unittest

from synthetic.distances import *
from synthetic.net import Net
from synthetic.tests.graphs import *


class TestDistances(unittest.TestCase):
    def test_undir_self(self):
        n = Net(full_graph(directed=False))
        d2n = create_distances_to_net(n, bins=10, max_dist=10, norm=Norm.NONE)
        dists = d2n.compute(n)
        self.assertListEqual(dists, [0., 0., 0., 0.])

    def test_dir_self(self):
        n = Net(full_graph(directed=True))
        d2n = create_distances_to_net(n, bins=10, max_dist=10, norm=Norm.NONE)
        dists = d2n.compute(n)
        self.assertListEqual(dists, [0., 0., 0., 0., 0., 0., 0.])

    def test_undir_misc(self):
        n1 = Net(full_graph(directed=False))
        n2 = Net(star_graph())
        n3 = Net(ring_graph(directed=False))
        n4 = Net(random_graph_sparse(directed=False))
        n5 = Net(random_graph_medium(directed=False))
        n6 = Net(random_graph_dense(directed=False))

        d2n_1 = create_distances_to_net(n1, bins=10, max_dist=10, norm=Norm.ER_MEAN_RATIO, norm_samples=30)
        dists_1_2 = d2n_1.compute(n2)
        dists_1_3 = d2n_1.compute(n3)
        dists_1_4 = d2n_1.compute(n4)
        dists_1_5 = d2n_1.compute(n5)
        dists_1_6 = d2n_1.compute(n6)

        self.assertEqual(dists_1_3[1], 0.)
        self.assertEqual(dists_1_2[2], dists_1_3[2])
        for i in {0, 2, 3}:
            self.assertGreater(dists_1_4[i], dists_1_5[i])
            self.assertGreater(dists_1_5[i], dists_1_6[i])

    def test_dir_misc(self):
        n1 = Net(full_graph(directed=True))
        n2 = Net(out_star_graph())
        n3 = Net(ring_graph(directed=True))
        n4 = Net(random_graph_sparse(directed=True))
        n5 = Net(random_graph_medium(directed=True))
        n6 = Net(random_graph_dense(directed=True))

        d2n_1 = create_distances_to_net(n1, bins=10, max_dist=10, norm=Norm.ER_MEAN_RATIO, norm_samples=30)
        dists_1_2 = d2n_1.compute(n2)
        dists_1_3 = d2n_1.compute(n3)
        dists_1_4 = d2n_1.compute(n4)
        dists_1_5 = d2n_1.compute(n5)
        dists_1_6 = d2n_1.compute(n6)

        self.assertEqual(dists_1_3[2], 0.)
        self.assertEqual(dists_1_2[4], dists_1_3[4])
        for i in {0, 1, 5, 6}:
            self.assertGreater(dists_1_4[i], dists_1_5[i])
            self.assertGreater(dists_1_5[i], dists_1_6[i])


if __name__ == '__main__':
    unittest.main()
