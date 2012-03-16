package com.telmomenezes.synthetic.evo;

import java.util.Vector;

import com.telmomenezes.synthetic.Model;


/**
 * Population generator interface.
 * 
 * Population generators are used by EvoGen
 * objects to select individuals in a population to create the next generation.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public interface PopGenerator {
	public Vector<Model> newGeneration(EvoGen evo);
	
	public String paramsString();
}