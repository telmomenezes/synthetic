package com.telmomenezes.synthetic.evo;

import java.util.ArrayList;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.RandomGenerator;


/**
 * Post fitness processor aimed at implementing novelty search.
 * 
 * The novelty search algorithm is described in the following paper:
 * 
 * Joel Lehman and Kenneth O. Stanley, Abandoning Objectives: Evolution Through
 * the Search for Novelty Alone, Evolutionary Computation journal. Cambridge, 
 * MA: MIT Press, 2010. 
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Novelty implements PostFitness {

	private int k;
	protected double[] neighbours;
	private double min;
	protected ArrayList<Generator> archive;
	double meanSpar;
	Evo evo;
	
	
	public void PostFitness() {
		k = 10;
		neighbours = new double[k];
	}
	
	
	public void postProcessFitness(Evo evo) {

		this.evo = evo;
		meanSpar = 0.0;

		// compute sparseness
		for (int j = 0; j < evo.getPopulationSize(); j++) {
			Generator model = evo.getPopulation().get(j);
			meanSpar += computeSparseness(model);

			// add some to archive
			if (RandomGenerator.instance().random.nextDouble() < 0.1) {
				archive.add(model);
			}
		}

		meanSpar /= (double)evo.getPopulationSize();
	}
	
	
	private void updateNeighbours(double val)
	{
		double localmax = -1;
		int maxpos = 0;

		for (int j = 0; j < k; j++) {
			if (neighbours[j] < 0.0) {
				localmax = -1;
				maxpos = j;
				break;
			}
			else if ((localmax < 0.0) || (neighbours[j] > localmax)) {
				localmax = neighbours[j];
				maxpos = j;
			}
		}

		neighbours[maxpos] = val;

		localmax = -1;
		for (int j = 0; j < k; j++)
			if (neighbours[j] < 0.0) {
				localmax = -1;
				break;
			}
			else if ((localmax < 0.0) || (neighbours[j] > localmax))
				localmax = neighbours[j];

		min = localmax;
	}


	private double computeSparseness(Generator model)
	{
		for (int i = 0; i < k; i++)
			neighbours[i] = -1.0;
		min = -1.0;

		// compare to current population
		for (int i = 0; i < evo.getPopulationSize(); i++) {
			if (evo.getPopulation().get(i) != model) {
				double d = model.distance(evo.getPopulation().get(i));

				if ((min < 0.0) || (d < min))
					updateNeighbours(d); 
			}
		}

		// compare to archive
		for (Generator archModel : archive) {
            double d = model.distance(archModel);

            if ((min < 0.0) || (d < min))
                updateNeighbours(d);
		}


		double spar = 0.0;
		for (int i = 0; i < k; i++)
			spar += neighbours[i];
		spar /= (double)k;

		model.postFitness = -spar;
		return spar;
	}
}