package com.telmomenezes.synthetic.gp;

import java.util.Vector;

import com.telmomenezes.synthetic.Model;
import com.telmomenezes.synthetic.RandomGenerator;


/**
 * Implements a tournament population generator strategy.
 * 
 * Based on the widely used tournament selection strategy for genetic
 * algorithms.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Tournament implements PopGenerator {
	
	private int tournamentSize;
	private double recombProb;
	private double mutProb;
	
	
	public Tournament(int tournamentSize, double mutProb, double recombProb) {
		this.tournamentSize = tournamentSize;
		
		// default values
		this.mutProb = mutProb;
		this.recombProb = recombProb;
	}
	
	
	public Vector<Model> newGeneration(EvoGen evo) {
		Vector<Model> newPopulation = new Vector<Model>();
		for (int j = 0; j < evo.getPopulationSize(); j++) {

			Model parent1, parent2, child, childm;
			
			// select first parent
			parent1 = selectParent(evo);

			if (RandomGenerator.instance().random.nextDouble() < recombProb) {
				// select second parent
				parent2 = selectParent(evo);
				// recombine
				child = parent1.recombine(parent2);
			}
			else
				child = parent1.cloneProgs();
			
			// mutate
			if (RandomGenerator.instance().random.nextDouble() < mutProb) {
				parent1 = child.clone();
				parent1.initProgsRandom();
				childm = child.recombine(parent1);
				child = childm;
			}

			newPopulation.add(child);
		}
		
		return newPopulation;
	}


	private Model selectParent(EvoGen evo)
	{
		int bestIndex = - 1;
		double bestFitness = 0;

		for (int i = 0; i < tournamentSize; i++)
		{
			int index = RandomGenerator.instance().random.nextInt(evo.getPopulationSize());
			if ((i == 0) || (evo.getPopulation().get(index).postFitness < bestFitness)) {
				bestFitness = evo.getPopulation().get(index).postFitness;
				bestIndex = index;
			}
		}

		return evo.getPopulation().get(bestIndex);
	}
	
	
	public String paramsString()
	{
		String tmpstr = "";
		tmpstr += "Algorithm: Genetic Algorithm with Tournament Selection\n";
		tmpstr += "recombination probability: " + recombProb + "\n";
		tmpstr += "mutation probability: " + mutProb + "\n";
		return tmpstr;
	}
}