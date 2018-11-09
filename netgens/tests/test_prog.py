import unittest
import numpy as np
from netgens.progs.prog import parse


class TestProg(unittest.TestCase):
    def test_prog_sum(self):
        prog = parse('(+ 1 1)', [])
        self.assertEqual(str(prog), '(+ 1.0 1.0)')
        self.assertEqual(prog.eval(), 2.)

    def test_prog_sub(self):
        prog = parse('(- 2 1)', [])
        self.assertEqual(str(prog), '(- 2.0 1.0)')
        self.assertEqual(prog.eval(), 1.)

    def test_prog_mul(self):
        prog = parse('(* -5 10)', [])
        self.assertEqual(str(prog), '(* -5.0 10.0)')
        self.assertEqual(prog.eval(), -50.)

    def test_prog_div(self):
        prog = parse('(/ 1 2)', [])
        self.assertEqual(str(prog), '(/ 1.0 2.0)')
        self.assertEqual(prog.eval(), .5)

    def test_prog_eq_true(self):
        prog = parse('(== 1 1 0 1)', [])
        self.assertEqual(str(prog), '(== 1.0 1.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 0.)

    def test_prog_eq_false(self):
        prog = parse('(== 1 2 0 1)', [])
        self.assertEqual(str(prog), '(== 1.0 2.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 1.)

    def test_prog_grt_true(self):
        prog = parse('(> 2 1 0 1)', [])
        self.assertEqual(str(prog), '(> 2.0 1.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 0.)

    def test_prog_grt_false(self):
        prog = parse('(> -2 1 0 1)', [])
        self.assertEqual(str(prog), '(> -2.0 1.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 1.)

    def test_prog_lrt_true(self):
        prog = parse('(< -2 1 0 1)', [])
        self.assertEqual(str(prog), '(< -2.0 1.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 0.)

    def test_prog_lrt_false(self):
        prog = parse('(< 2 1 0 1)', [])
        self.assertEqual(str(prog), '(< 2.0 1.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 1.)

    def test_prog_exp(self):
        prog = parse('(EXP 1)', [])
        self.assertEqual(str(prog), '(EXP 1.0)')
        np.testing.assert_almost_equal(prog.eval(), 2.718281828459045)

    def test_prog_log(self):
        prog = parse('(LOG 2)', [])
        self.assertEqual(str(prog), '(LOG 2.0)')
        np.testing.assert_almost_equal(prog.eval(), 0.6931471805599453)

    def test_prog_abs(self):
        prog = parse('(ABS -5)', [])
        self.assertEqual(str(prog), '(ABS -5.0)')
        self.assertEqual(prog.eval(), 5.)

    def test_prog_min(self):
        prog = parse('(MIN -5 5)', [])
        self.assertEqual(str(prog), '(MIN -5.0 5.0)')
        self.assertEqual(prog.eval(), -5.)

    def test_prog_max(self):
        prog = parse('(MAX -5 5)', [])
        self.assertEqual(str(prog), '(MAX -5.0 5.0)')
        self.assertEqual(prog.eval(), 5.)

    def test_prog_pow(self):
        prog = parse('(^ 2.5 3.5)', [])
        self.assertEqual(str(prog), '(^ 2.5 3.5)')
        np.testing.assert_almost_equal(prog.eval(), 24.705294220065465)

    def test_prog_aff_true(self):
        prog = parse('(AFF 2 0 1)', ['id1', 'id2'])
        prog.vars[0] = 10
        prog.vars[1] = 20
        self.assertEqual(str(prog), '(AFF 2.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 0.)

    def test_prog_aff_false(self):
        prog = parse('(AFF 3 0 1)', ['id1', 'id2'])
        prog.vars[0] = 10
        prog.vars[1] = 20
        self.assertEqual(str(prog), '(AFF 3.0 0.0 1.0)')
        self.assertEqual(prog.eval(), 1.)

if __name__ == '__main__':
    unittest.main()
