package com.telmomenezes.synthetic.evo;


/**
 * Post fitness processor interface.
 * 
 * Post fitness processors can be used by Evo objects to recompute the fitness
 * of an individual after the first evaluation, according to some criteria.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public interface PostFitness {
	void postProcessFitness(Evo evo);
}
