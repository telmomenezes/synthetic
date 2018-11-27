import unittest
from synthetic.consts import *
from synthetic.generator import generator_from_prog_str


class TestGenerator(unittest.TestCase):
    def test_gen_random(self):
        gen = generator_from_prog_str('1', True)
        net = gen.run(100, 10, DEFAULT_SAMPLE_RATE)
        self.assertEqual(len(net.vs), 100)


if __name__ == '__main__':
    unittest.main()
