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
	protected double bestFitness;
	
	
	public Evo()
	{
		population = new Vector<Generator>();

		postFitness = null;
		
		// init state
		bestFitness = Double.MAX_VALUE;
	}


	public abstract void run();

	public abstract int getPopulationSize();
	
	public abstract String infoString();


	public double getBestFitness() {
		return bestFitness;
	}


	public Vector<Generator> getPopulation() {
		return population;
	}


	public void setPostFitness(PostFitness postFitness) {
		this.postFitness = postFitness;
	}
}