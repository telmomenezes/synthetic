import unittest
from synthetic.consts import *
from synthetic.generator import generator_from_prog_str
from synthetic.distances import create_distances_to_net, Norm
from synthetic.evo import EvaluatedIndividual


class TestEvo(unittest.TestCase):
    def test_evaluated_individual_undir(self):
        gen_targ = generator_from_prog_str('1', False)
        net_targ = gen_targ.run(100, 100, DEFAULT_SAMPLE_RATE)
        dists2net = create_distances_to_net(net_targ, bins=DEFAULT_BINS,
                                            max_dist=DEFAULT_MAX_DIST,
                                            norm=Norm.ER_MEAN_RATIO,
                                            norm_samples=DEFAULT_NORM_SAMPLES)

        gen1 = generator_from_prog_str('$orig_deg', False)
        net1 = gen1.run(100, 100, DEFAULT_SAMPLE_RATE)
        ei1 = EvaluatedIndividual(dists2net, gen1, net1)

        gen2 = generator_from_prog_str('0', False)
        net2 = gen2.run(100, 100, DEFAULT_SAMPLE_RATE)
        ei2 = EvaluatedIndividual(dists2net, gen2, net2)

        self.assertGreater(ei1.fitness, ei2.fitness)

    def test_evaluated_individual_dir(self):
        gen_targ = generator_from_prog_str('1', True)
        net_targ = gen_targ.run(100, 100, DEFAULT_SAMPLE_RATE)
        dists2net = create_distances_to_net(net_targ, bins=DEFAULT_BINS,
                                            max_dist=DEFAULT_MAX_DIST,
                                            norm=Norm.ER_MEAN_RATIO,
                                            norm_samples=DEFAULT_NORM_SAMPLES)

        gen1 = generator_from_prog_str('$orig_in_deg', True)
        net1 = gen1.run(100, 100, DEFAULT_SAMPLE_RATE)
        ei1 = EvaluatedIndividual(dists2net, gen1, net1)

        gen2 = generator_from_prog_str('0', True)
        net2 = gen2.run(100, 100, DEFAULT_SAMPLE_RATE)
        ei2 = EvaluatedIndividual(dists2net, gen2, net2)

        self.assertGreater(ei1.fitness, ei2.fitness)


if __name__ == '__main__':
    unittest.main()
