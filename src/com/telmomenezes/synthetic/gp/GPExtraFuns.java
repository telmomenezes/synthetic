package com.telmomenezes.synthetic.gp;


/**
 * Interface to implement extra genetic programming functions.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public interface GPExtraFuns {

	public double eval(GPTree tree, GPNode node);
	
	public int funArity(int fun);
	
	public String funName(int fun);
}
