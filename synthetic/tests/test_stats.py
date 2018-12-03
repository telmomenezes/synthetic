import unittest
from igraph import Graph
from synthetic.stats import create_stat, StatType


def full_graph(directed):
    return Graph.Full(100, directed=directed)


def star_graph():
    return Graph.Star(100, 'undirected')


def in_star_graph():
    return Graph.Star(100, 'in')


def out_star_graph():
    return Graph.Star(100, 'out')


def ring_graph(directed):
    return Graph.Ring(100, directed=directed)


class TestStats(unittest.TestCase):
    def test_degrees_undir(self):
        g = full_graph(directed=False)
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = star_graph()
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = ring_graph(directed=False)
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 2.)

    def test_degrees_dir(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 198.)

        g = in_star_graph()
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = out_star_graph()
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 2.)

    def test_in_degrees(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.IN_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = in_star_graph()
        s = create_stat(g, StatType.IN_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = out_star_graph()
        s = create_stat(g, StatType.IN_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [1, 0, 0, 0, 0, 0, 0, 0, 0, 99])
        self.assertEqual(s.bin_edges[-1], 1.)

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.IN_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 1.)

    def test_out_degrees(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.OUT_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = in_star_graph()
        s = create_stat(g, StatType.OUT_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [1, 0, 0, 0, 0, 0, 0, 0, 0, 99])
        self.assertEqual(s.bin_edges[-1], 1.)

        g = out_star_graph()
        s = create_stat(g, StatType.OUT_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertEqual(s.bin_edges[-1], 99.)

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.OUT_DEGREES, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertEqual(s.bin_edges[-1], 1.)

    def test_u_page_ranks_undir(self):
        g = full_graph(directed=False)
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

        g = star_graph()
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertAlmostEqual(s.bin_edges[-1], .46, places=2)

        g = ring_graph(directed=False)
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

    def test_u_page_ranks_dir(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

        g = in_star_graph()
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertAlmostEqual(s.bin_edges[-1], .46, places=2)

        g = out_star_graph()
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertAlmostEqual(s.bin_edges[-1], .46, places=2)

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.U_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

    def test_d_page_ranks(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.D_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

        g = in_star_graph()
        s = create_stat(g, StatType.D_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 1])
        self.assertAlmostEqual(s.bin_edges[-1], .46, places=2)

        g = out_star_graph()
        s = create_stat(g, StatType.D_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.D_PAGERANKS, bins=10)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 100])
        self.assertAlmostEqual(s.bin_edges[-1], .01, places=2)

    def test_triad_census_undir(self):
        g = full_graph(directed=False)
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 0, 161700])

        g = star_graph()
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 4851, 0])

        g = ring_graph(directed=False)
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 100, 0])

    def test_triad_census_dir(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161700])

        g = in_star_graph()
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 4851, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])

        g = out_star_graph()
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 4851, 0, 0, 0, 0, 0, 0, 0, 0, 0])

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])

    def test_u_dists_undir(self):
        g = full_graph(directed=False)
        s = create_stat(g, StatType.U_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [9900, 0, 0, 0, 0, 0, 0, 0, 0, 0])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)

        g = star_graph()
        s = create_stat(g, StatType.U_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [198, 9702, 0, 0, 0, 0, 0, 0, 0, 0])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)

        g = ring_graph(directed=False)
        s = create_stat(g, StatType.U_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [200, 200, 200, 200, 200, 200, 200, 200, 200, 8100])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)


    def test_d_dists_dir(self):
        g = full_graph(directed=True)
        s = create_stat(g, StatType.D_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [9900, 0, 0, 0, 0, 0, 0, 0, 0, 0])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)

        g = in_star_graph()
        s = create_stat(g, StatType.D_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 9801])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)

        g = out_star_graph()
        s = create_stat(g, StatType.D_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [99, 0, 0, 0, 0, 0, 0, 0, 0, 9801])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.D_DISTS, max_dist=10)
        self.assertListEqual(list(s.data), [100, 100, 100, 100, 100, 100, 100, 100, 100, 9000])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)


if __name__ == '__main__':
    unittest.main()
