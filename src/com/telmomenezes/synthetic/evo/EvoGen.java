package com.telmomenezes.synthetic.evo;

import com.telmomenezes.synthetic.Model;


/**
 * Basic generation based evolutionary algorithm.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class EvoGen extends Evo {
    
	PopGenerator popgen;
	
    // parameters
	private int generations;

    // state
	protected int curgen;
	protected double bestGenFitness;
	protected double meanGenoSize;
	protected double genTime;
	protected double simTime;
	protected double fitTime;
    
    // callbacks
	private EvoGenCallbacks callbacks;
	
	
	public EvoGen(PopGenerator popgen, int populationSize, Model baseModel)
	{
		this.popgen = popgen;
	
		callbacks = null;

		for (int i = 0; i < populationSize; i++) {
			Model model = (Model)baseModel.clone();
			model.initProgsRandom();
			population.add(model);
		}
		
		// default values
		generations = 1000;

		// init state
		curgen = 0;
		bestGenFitness = 0;
		meanGenoSize = 0;
		genTime = 0;
		simTime = 0;
		fitTime = 0;
	}

	
	public void run()
	{
		for(curgen = 0; curgen < generations; curgen++) {

			long startTime = System.currentTimeMillis();
			meanGenoSize = 0;
			
			simTime = 0;
			fitTime = 0;

			Model model;
			
			for (int j = 0; j < getPopulationSize(); j++) {
				model = population.get(j);

				meanGenoSize += model.genotypeSize();

				if (!model.simulated) {
					long time0 = System.currentTimeMillis();
					model.run();
					simTime += System.currentTimeMillis() - time0;
					time0 = System.currentTimeMillis();
					model.computeFitness();
					fitTime += System.currentTimeMillis() - time0;
					model.postFitness = model.fitness;
					model.trim();
					System.gc();
				}

				if ((j == 0) || (model.fitness < bestGenFitness))
					bestGenFitness = model.fitness;

				if (((curgen == 0) && (j == 0)) || (model.fitness < bestFitness)) {
					bestFitness = model.fitness;
					bestmodel = model;
				}
			}

			if (postFitness != null)
				postFitness.postProcessFitness(this);
			
			meanGenoSize /= (double)getPopulationSize();

			// assign new population
			population = popgen.newGeneration(this);

			// time it took to compute the generation
			genTime = System.currentTimeMillis() - startTime;
			genTime /= 1000;
			simTime /= 1000;
			fitTime /= 1000;
			
			// onGeneration callback
			if (callbacks != null) {
				callbacks.onGeneration(this);
			}
		}
	}
	

	public String paramsString()
	{
		String tmpstr = "";
		tmpstr += "EVOLUTIONARY PARAMETERES\n";
		tmpstr += "population size: " + getPopulationSize() + "\n";
		tmpstr += "generations: " + generations + "\n";
		tmpstr += popgen.paramsString();
		return tmpstr;
	}


	public String genInfoString()
	{
		String tmpstr = "gen #" + curgen
        	+ "; best fitness: " + bestFitness
        	+ "; best gen fitness: " + bestGenFitness
        	+ "; best genotype size: " + bestmodel.genotypeSize()
        	+ "; mean genotype size: " + meanGenoSize
        	+ "; gen comp time: " + genTime + "s."
			+ "; sim comp time: " + simTime + "s."
			+ "; fit comp time: " + fitTime + "s.";
        	return tmpstr;
	}


	public void setGenerations(int generations) {
		this.generations = generations;
	}


	public void setCallbacks(EvoGenCallbacks callbacks) {
		this.callbacks = callbacks;
	}


	public int getCurgen() {
		return curgen;
	}


	public double getBestGenFitness() {
		return bestGenFitness;
	}


	public double getMeanGenoSize() {
		return meanGenoSize;
	}


	public double getGenTime() {
		return genTime;
	}


	public double getSimTime() {
		return simTime;
	}


	public double getFitTime() {
		return fitTime;
	}
}