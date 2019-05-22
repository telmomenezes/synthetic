import unittest
from synthetic.stats import create_stat, StatType, DistanceType
from synthetic.tests.graphs import *


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
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                            0, 0, 0, 161700])

        g = in_star_graph()
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 4851, 0, 0, 0, 0, 0, 0, 0, 0,
                                            0, 0, 0, 0, 0])

        g = out_star_graph()
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 0, 0, 4851, 0, 0, 0, 0,
                                            0, 0, 0, 0, 0])

        g = ring_graph(directed=True)
        s = create_stat(g, StatType.TRIAD_CENSUS)
        self.assertListEqual(list(s.data), [0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0,
                                            0, 0, 0, 0, 0])

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
        self.assertListEqual(list(s.data), [200, 200, 200, 200, 200, 200, 200,
                                            200, 200, 8100])
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
        self.assertListEqual(list(s.data), [100, 100, 100, 100, 100, 100, 100,
                                            100, 100, 9000])
        self.assertAlmostEqual(s.bin_edges[-1], 10., places=2)

    def test_unknown_stat_throws_exception(self):
        g = star_graph()
        with self.assertRaises(ValueError):
            create_stat(g, 999999999)

    def test_unsupported_distance_throws_exception(self):
        g = star_graph()
        s = create_stat(g, StatType.TRIAD_CENSUS)
        with self.assertRaises(NotImplementedError):
            s.distance(s, DistanceType.EARTH_MOVER)

    def test_normalized_manhattan_distance_undir(self):
        g1 = full_graph(directed=False)
        s1 = create_stat(g1, StatType.TRIAD_CENSUS)

        g2 = star_graph()
        s2 = create_stat(g2, StatType.TRIAD_CENSUS)

        g3 = ring_graph(directed=False)
        s3 = create_stat(g3, StatType.TRIAD_CENSUS)

        g4 = random_graph_sparse(directed=False)
        s4 = create_stat(g4, StatType.TRIAD_CENSUS)

        g5 = random_graph_sparse(directed=False)
        s5 = create_stat(g5, StatType.TRIAD_CENSUS)

        self.assertEqual(
            s1.distance(s1, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s2.distance(s2, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s3.distance(s3, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s4.distance(s4, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s5.distance(s5, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertAlmostEqual(
            s1.distance(s2, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s1.distance(s3, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s2.distance(
                s3, DistanceType.NORMALIZED_MANHATTAN), 0.979, places=2)
        self.assertLess(s4.distance(s5, DistanceType.NORMALIZED_MANHATTAN), 2.)
        self.assertGreaterEqual(
            s1.distance(s3, DistanceType.NORMALIZED_MANHATTAN),
            s4.distance(s5, DistanceType.NORMALIZED_MANHATTAN))

    def test_normalized_manhattan_distance_dir(self):
        g1 = full_graph(directed=True)
        s1 = create_stat(g1, StatType.TRIAD_CENSUS)

        g2 = in_star_graph()
        s2 = create_stat(g2, StatType.TRIAD_CENSUS)

        g3 = out_star_graph()
        s3 = create_stat(g3, StatType.TRIAD_CENSUS)

        g4 = ring_graph(directed=True)
        s4 = create_stat(g4, StatType.TRIAD_CENSUS)

        g5 = random_graph_sparse(directed=True)
        s5 = create_stat(g5, StatType.TRIAD_CENSUS)

        g6 = random_graph_sparse(directed=True)
        s6 = create_stat(g6, StatType.TRIAD_CENSUS)

        self.assertEqual(
            s1.distance(s1, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s2.distance(s2, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s3.distance(s3, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s4.distance(s4, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s5.distance(s5, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertEqual(
            s6.distance(s6, DistanceType.NORMALIZED_MANHATTAN), 0.)
        self.assertAlmostEqual(
            s1.distance(s2, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s1.distance(s3, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s1.distance(s4, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s2.distance(s3, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s2.distance(s4, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertAlmostEqual(
            s3.distance(s4, DistanceType.NORMALIZED_MANHATTAN), 2., places=2)
        self.assertLess(
            s5.distance(s6, DistanceType.NORMALIZED_MANHATTAN), 10.)

    def test_earth_mover_distance_undir(self):
        g1 = full_graph(directed=False)
        s1 = create_stat(g1, StatType.DEGREES, bins=10)

        g2 = star_graph()
        s2 = create_stat(g2, StatType.DEGREES, bins=10)

        g3 = ring_graph(directed=False)
        s3 = create_stat(g3, StatType.DEGREES, bins=10)

        g4 = random_graph_sparse(directed=False)
        s4 = create_stat(g4, StatType.DEGREES, bins=10)

        g5 = random_graph_sparse(directed=False)
        s5 = create_stat(g5, StatType.DEGREES, bins=10)

        self.assertEqual(s1.distance(s1, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s2.distance(s2, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s3.distance(s3, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s4.distance(s4, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s5.distance(s5, DistanceType.EARTH_MOVER), 0.)
        self.assertAlmostEqual(
            s1.distance(s2, DistanceType.EARTH_MOVER), 8820.9, places=2)
        self.assertAlmostEqual(
            s1.distance(s3, DistanceType.EARTH_MOVER), 0., places=2)
        self.assertAlmostEqual(
            s2.distance(s3, DistanceType.EARTH_MOVER), 8820.9, places=2)
        self.assertLess(
            s4.distance(s5, DistanceType.EARTH_MOVER), 1000.)
        self.assertLessEqual(
            s1.distance(s3, DistanceType.EARTH_MOVER),
            s4.distance(s5, DistanceType.EARTH_MOVER))

    def test_earth_mover_distance_undir_rel(self):
        g1 = full_graph(directed=False)
        s1 = create_stat(g1, StatType.DEGREES, bins=10)

        g2 = star_graph()
        s2 = create_stat(g2, StatType.DEGREES, bins=10, ref_stat=s1)

        g3 = ring_graph(directed=False)
        s3 = create_stat(g3, StatType.DEGREES, bins=10, ref_stat=s1)

        g4 = random_graph_sparse(directed=False)
        s4 = create_stat(g4, StatType.DEGREES, bins=10, ref_stat=s1)

        g5 = random_graph_sparse(directed=False)
        s5 = create_stat(g5, StatType.DEGREES, bins=10, ref_stat=s1)

        self.assertEqual(s1.distance(s1, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s2.distance(s2, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s3.distance(s3, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s4.distance(s4, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s5.distance(s5, DistanceType.EARTH_MOVER), 0.)
        self.assertAlmostEqual(
            s1.distance(s2, DistanceType.EARTH_MOVER), 8820.9, places=2)
        self.assertAlmostEqual(
            s1.distance(s3, DistanceType.EARTH_MOVER), 8910., places=2)
        self.assertAlmostEqual(
            s2.distance(s3, DistanceType.EARTH_MOVER), 89.1, places=2)
        self.assertLess(s4.distance(s5, DistanceType.EARTH_MOVER), 1000.)
        self.assertGreater(
            s1.distance(s3, DistanceType.EARTH_MOVER),
            s4.distance(s5, DistanceType.EARTH_MOVER))

    def test_earth_mover_distance_dir(self):
        g1 = full_graph(directed=True)
        s1 = create_stat(g1, StatType.IN_DEGREES, bins=10)

        g2 = in_star_graph()
        s2 = create_stat(g2, StatType.IN_DEGREES, bins=10)

        g3 = out_star_graph()
        s3 = create_stat(g3, StatType.IN_DEGREES, bins=10)

        g4 = ring_graph(directed=True)
        s4 = create_stat(g4, StatType.IN_DEGREES, bins=10)

        g5 = random_graph_sparse(directed=True)
        s5 = create_stat(g5, StatType.IN_DEGREES, bins=10)

        g6 = random_graph_sparse(directed=True)
        s6 = create_stat(g6, StatType.IN_DEGREES, bins=10)

        self.assertEqual(s1.distance(s1, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s2.distance(s2, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s3.distance(s3, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s4.distance(s4, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s5.distance(s5, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s6.distance(s6, DistanceType.EARTH_MOVER), 0.)
        self.assertAlmostEqual(
            s1.distance(s2, DistanceType.EARTH_MOVER), 8820.9, places=2)
        self.assertAlmostEqual(
            s1.distance(s3, DistanceType.EARTH_MOVER), 89.1, places=2)
        self.assertAlmostEqual(
            s1.distance(s4, DistanceType.EARTH_MOVER), 0., places=2)
        self.assertAlmostEqual(
            s2.distance(s3, DistanceType.EARTH_MOVER), 8731.8, places=2)
        self.assertAlmostEqual(
            s2.distance(s4, DistanceType.EARTH_MOVER), 8820.9, places=2)
        self.assertAlmostEqual(
            s3.distance(s4, DistanceType.EARTH_MOVER), 0.9, places=2)
        self.assertLess(s5.distance(s6, DistanceType.EARTH_MOVER), 1000.)

    def test_earth_mover_distance_dir_rel(self):
        g1 = full_graph(directed=True)
        s1 = create_stat(g1, StatType.IN_DEGREES, bins=10)

        g2 = in_star_graph()
        s2 = create_stat(g2, StatType.IN_DEGREES, bins=10, ref_stat=s1)

        g3 = out_star_graph()
        s3 = create_stat(g3, StatType.IN_DEGREES, bins=10, ref_stat=s1)

        g4 = ring_graph(directed=True)
        s4 = create_stat(g4, StatType.IN_DEGREES, bins=10, ref_stat=s1)

        g5 = random_graph_sparse(directed=True)
        s5 = create_stat(g5, StatType.IN_DEGREES, bins=10, ref_stat=s1)

        g6 = random_graph_sparse(directed=True)
        s6 = create_stat(g6, StatType.IN_DEGREES, bins=10, ref_stat=s1)

        self.assertEqual(s1.distance(s1, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s2.distance(s2, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s3.distance(s3, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s4.distance(s4, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s5.distance(s5, DistanceType.EARTH_MOVER), 0.)
        self.assertEqual(s6.distance(s6, DistanceType.EARTH_MOVER), 0.)
        self.assertAlmostEqual(
            s1.distance(s2, DistanceType.EARTH_MOVER), 8820.9, places=2)
        self.assertAlmostEqual(
            s1.distance(s3, DistanceType.EARTH_MOVER), 8910., places=2)
        self.assertAlmostEqual(
            s1.distance(s4, DistanceType.EARTH_MOVER), 8910., places=2)
        self.assertAlmostEqual(
            s2.distance(s3, DistanceType.EARTH_MOVER), 89.1, places=2)
        self.assertAlmostEqual(
            s2.distance(s4, DistanceType.EARTH_MOVER), 89.1, places=2)
        self.assertAlmostEqual(
            s3.distance(s4, DistanceType.EARTH_MOVER), 0., places=2)
        self.assertLess(s5.distance(s6, DistanceType.EARTH_MOVER), 1000.)


if __name__ == '__main__':
    unittest.main()
