package com.telmomenezes.synthetic.evo;

import java.util.Vector;

import com.telmomenezes.synthetic.Model;


/**
 * Abstract base class for all evolutionary algorithms.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com) 
 */
public abstract class Evo {
	
	protected Vector<Model> population;
    
	protected PostFitness postFitness;

    // state
	protected Model bestmodel;
	protected double bestFitness;
	
	
	public Evo()
	{
		population = new Vector<Model>();

		postFitness = null;
		
		// init state
		bestFitness = 0;
		bestmodel = null;
	}


	public abstract void run();


	public abstract String paramsString();


	public Model getBestmodel() {
		return bestmodel;
	}


	public double getBestFitness() {
		return bestFitness;
	}


	public Vector<Model> getPopulation() {
		return population;
	}


	public void setPostFitness(PostFitness postFitness) {
		this.postFitness = postFitness;
	}
	
	public int getPopulationSize() {
		return population.size();
	}
}