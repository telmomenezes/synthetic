package com.telmomenezes.synthetic.gp;


import java.util.Vector;

import com.telmomenezes.synthetic.gp.GPFun;



public class GenericFunSet {

	private static GenericFunSet _instance = null;
	
	private Vector<Integer> funset;

	private GenericFunSet() {
		funset = new Vector<Integer>();
		funset.add(GPFun.SUM);
        funset.add(GPFun.SUB);
        funset.add(GPFun.MUL);
        funset.add(GPFun.DIV);
        funset.add(GPFun.EQ);
        funset.add(GPFun.GRT);
        funset.add(GPFun.LRT);
        funset.add(GPFun.ZER);
        funset.add(GPFun.EXP);
        funset.add(GPFun.LOG);
        //funset.add(GPFun.SIN);
        funset.add(GPFun.ABS);
        funset.add(GPFun.MIN);
        funset.add(GPFun.MAX);
	}

	public static GenericFunSet instance() {
		if (_instance == null)
			_instance = new GenericFunSet();

		return _instance;
	}

    public Vector<Integer> getFunset() {
        return funset;
    }

    public void setFunset(Vector<Integer> funset) {
        this.funset = funset;
    }
}