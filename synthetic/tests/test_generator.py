import unittest
from synthetic.consts import *
from synthetic.generator import generator_from_prog_str


class TestGenerator(unittest.TestCase):
    def test_gen_random_dir(self):
        gen = generator_from_prog_str('1', True)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)

    def test_gen_random_undir(self):
        gen = generator_from_prog_str('1', False)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)

    def test_gen_pa_dir(self):
        gen = generator_from_prog_str('$orig_in_deg', True)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)

    def test_gen_pa_undir(self):
        gen = generator_from_prog_str('$orig_deg', False)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)

    def test_gen_zero(self):
        gen = generator_from_prog_str('0', True)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)

    def test_gen_weird_dir(self):
        gen = generator_from_prog_str(
            '(AFF $dist (/ $orig_in_deg $orig_out_deg) (MIN $orig $targ))',
            True)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)

    def test_gen_weird_undir(self):
        gen = generator_from_prog_str(
            '(AFF $dist (/ $orig_deg $orig_deg) (MIN $orig $targ))', False)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)


if __name__ == '__main__':
    unittest.main()
