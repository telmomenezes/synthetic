package com.telmomenezes.synthetic.evo;

import java.util.Collections;
import java.util.Vector;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.RandomGenerator;


/**
 * Population generator that implements an Evolutionary Strategy .
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class EvoStrategy {
	
	private int parents;
	private int mixing;
	private int children;
	
	
	public EvoStrategy(int parents, int mixing, int children) {
		this.parents = parents;
		this.mixing = mixing;
		this.children = parents;
	}
	
	public int popSize() {
	    return parents + children;
	}
	
	public Vector<Generator> newGeneration(EvoGen evo) {
		
		// send the parents to the start of the vector by sorting
		Collections.shuffle(evo.getPopulation());
		Collections.sort(evo.getPopulation());
		
		Vector<Generator> newPopulation = new Vector<Generator>();
		
		// place parents in new population
		for (int i = 0; i < parents; i++)
			newPopulation.add(evo.getPopulation().get(i));
		
		// generate offspring
		for (int i = 0; i < children; i++) {

			Generator parent, child, child2;
			
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


	private Generator selectParent(EvoGen evo)
	{
		int index = RandomGenerator.instance().random.nextInt(parents);
		return evo.getPopulation().get(index);
	}
	
	
	public String infoString()
	{
		String str = "";
		str += "search algorithm: evolutionary strategy\n";
		str += "parents: " + parents + "\n";
		str += "mixing: " + mixing + "\n";
		return str;
	}
}