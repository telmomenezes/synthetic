package com.telmomenezes.synthetic.evo;

import com.telmomenezes.synthetic.generators.Generator;


/**
 * Basic generation based evolutionary algorithm.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class EvoGen extends Evo {
    
	private PopGenerator popgen;
	private EvoGenCallbacks callbacks;
	
    // parameters
	private int generations;

    // state
	private Generator bestGenerator;
	protected int curgen;
	protected double bestGenFitness;
	protected double meanGenoSize;
	protected double genTime;
	protected double simTime;
	protected double fitTime;
	
	
	public EvoGen(PopGenerator popgen, EvoGenCallbacks callbacks, int generations)
	{
		this.popgen = popgen;
		this.callbacks = callbacks;
		
		for (int i = 0; i < popgen.popSize(); i++) {
			Generator gen = callbacks.baseGenerator().clone();
			gen.initProgsRandom();
			population.add(gen);
		}
		
		// default values
		this.generations = generations;

		// init state
		bestGenerator = null;
		curgen = 0;
		bestFitness = Double.MAX_VALUE;
		bestGenFitness = Double.MAX_VALUE;
		meanGenoSize = 0;
		genTime = 0;
		simTime = 0;
		fitTime = 0;
	}

	public int getPopulationSize() {
	    return popgen.popSize();
	}
	
	public void run()
	{
		for(curgen = 0; curgen < generations; curgen++) {

			long startTime = System.currentTimeMillis();
			meanGenoSize = 0;
			
			simTime = 0;
			fitTime = 0;

			bestGenFitness = Double.MAX_VALUE;
			
			Generator generator;
			boolean first = false;
			for (int j = 0; j < popgen.popSize(); j++) {
				generator = population.get(j);

				meanGenoSize += generator.genotypeSize();

				if (!generator.simulated) {
					long time0 = System.currentTimeMillis();
					generator.run();
					simTime += System.currentTimeMillis() - time0;
					time0 = System.currentTimeMillis();
					generator.fitness = callbacks.computeFitness(generator);
					fitTime += System.currentTimeMillis() - time0;
					generator.postFitness = generator.fitness;
				
				    if (first || (generator.fitness < bestGenFitness)) {
				        first = false;
				        bestGenFitness = generator.fitness;
				    }
				    generator.simulated = true;
				}

				if (((curgen == 0) && (j == 0)) || (generator.fitness < bestFitness)) {
					bestFitness = generator.fitness;
					bestGenerator = generator;
					callbacks.onNewBest(this);
				}
			}

			if (postFitness != null) {
				postFitness.postProcessFitness(this);
			}
			
			meanGenoSize /= (double)popgen.popSize();

			// assign new population
			population = popgen.newGeneration(this);

			// time it took to compute the generation
			genTime = System.currentTimeMillis() - startTime;
			genTime /= 1000;
			simTime /= 1000;
			fitTime /= 1000;
			
			// onGeneration callback
			callbacks.onGeneration(this);
		}
	}
	

	public String infoString()
	{
		String str = "population size: " + popgen.popSize() + "\n";
		str += "generations: " + generations + "\n";
		str += callbacks.infoString();
		str += popgen.infoString();
		return str;
	}


	public String genInfoString()
	{
		String tmpstr = "gen #" + curgen
        	+ "; best fitness: " + bestFitness
        	+ "; best gen fitness: " + bestGenFitness
        	+ "; best genotype size: " + bestGenerator.genotypeSize()
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


    public Generator getBestGenerator() {
        return bestGenerator;
    }
}