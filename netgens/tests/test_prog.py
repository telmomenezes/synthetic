import unittest
from netgens.gp.prog import *


class TestProg(unittest.TestCase):
    def test_prog_1(self):
        prog = Prog([])
        prog.parse('(+ 1 1)')
        self.assertEqual(str(prog), '(+ 1.0 1.0)')
        self.assertEqual(prog.eval(), 2.)


if __name__ == '__main__':
    unittest.main()
