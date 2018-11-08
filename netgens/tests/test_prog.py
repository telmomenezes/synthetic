import unittest
from netgens.progs.prog import Prog


class TestProg(unittest.TestCase):
    def test_prog_sum(self):
        prog = Prog([])
        prog.parse('(+ 1 1)')
        self.assertEqual(str(prog), '(+ 1.0 1.0)')
        self.assertEqual(prog.eval(), 2.)

    def test_prog_sub(self):
        prog = Prog([])
        prog.parse('(- 2 1)')
        self.assertEqual(str(prog), '(- 2.0 1.0)')
        self.assertEqual(prog.eval(), 1.)

    def test_prog_mul(self):
        prog = Prog([])
        prog.parse('(* -5 10)')
        self.assertEqual(str(prog), '(* -5.0 10.0)')
        self.assertEqual(prog.eval(), -50.)

    def test_prog_div(self):
        prog = Prog([])
        prog.parse('(/ 1 2)')
        self.assertEqual(str(prog), '(/ 1.0 2.0)')
        self.assertEqual(prog.eval(), .5)

    def test_prog_eq_true(self):
        prog = Prog([])
        prog.parse('(== 1 1 0 1)')
        self.assertEqual(str(prog), '(== 1.0 1.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 0.)

    def test_prog_eq_false(self):
        prog = Prog([])
        prog.parse('(== 1 2 0 1)')
        self.assertEqual(str(prog), '(== 1.0 2.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 1.)


if __name__ == '__main__':
    unittest.main()
