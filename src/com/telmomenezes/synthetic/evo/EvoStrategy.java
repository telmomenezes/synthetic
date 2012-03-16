package com.telmomenezes.synthetic.evo;

import java.util.Collections;
import java.util.Vector;

import com.telmomenezes.synthetic.Model;
import com.telmomenezes.synthetic.RandomGenerator;


/**
 * Population generator that implements an Evolutionary Strategy .
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class EvoStrategy implements PopGenerator {
	
	private int parents;
	private int mixing;
	
	
	public EvoStrategy(int parents, int mixing) {
		this.parents = parents;
		this.mixing = mixing;
	}
	
	
	public Vector<Model> newGeneration(EvoGen evo) {
		
		// send the parents to the start of the vector by sorting
		Collections.shuffle(evo.population);
		Collections.sort(evo.population);
		
		Vector<Model> newPopulation = new Vector<Model>();
		
		// place parents in new population
		for (int i = 0; i < parents; i++)
			newPopulation.add(evo.population.get(i));
		
		// generate offspring
		for (int i = 0; i < evo.getPopulationSize() - parents; i++) {

			Model parent, child, child2;
			
			// select first parent
			parent = selectParent(evo);
			child = parent.cloneProgs();
			
			for (int j = 1; j < mixing; j++) {
				parent = selectParent(evo);
				child2 = child.recombine(parent);
				child = child2;
			}
			
			// mutate
			parent = child.clone();
			parent.initProgsRandom();
			child2 = child.recombine(parent);
			child = child2;

			newPopulation.add(child);
		}
		
		return newPopulation;
	}


	private Model selectParent(EvoGen evo)
	{
		int index = RandomGenerator.instance().random.nextInt(parents);
		return evo.getPopulation().get(index);
	}
	
	
	public String paramsString()
	{
		String tmpstr = "";
		tmpstr += "Algorithm: Evolutionary Strategy\n";
		tmpstr += "parents: " + parents + "\n";
		tmpstr += "mixing: " + mixing + "\n";
		return tmpstr;
	}
}