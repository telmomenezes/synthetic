package com.telmomenezes.synthetic.evo;

import java.util.Vector;

import com.telmomenezes.synthetic.generators.Generator;


/**
 * Abstract base class for all evolutionary algorithms.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com) 
 */
public abstract class Evo {
	
	protected Vector<Generator> population;
    
	protected PostFitness postFitness;

    // state
	protected Generator bestmodel;
	protected double bestFitness;
	
	
	public Evo()
	{
		population = new Vector<Generator>();

		postFitness = null;
		
		// init state
		bestFitness = 0;
		bestmodel = null;
	}


	public abstract void run();


	public abstract String paramsString();


	public Generator getBestmodel() {
		return bestmodel;
	}


	public double getBestFitness() {
		return bestFitness;
	}


	public Vector<Generator> getPopulation() {
		return population;
	}


	public void setPostFitness(PostFitness postFitness) {
		this.postFitness = postFitness;
	}
	
	public int getPopulationSize() {
		return population.size();
	}
}