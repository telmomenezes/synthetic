package com.telmomenezes.synthetic.evo;

import com.telmomenezes.synthetic.generators.Generator;


public interface EvoGenCallbacks {
    public Generator baseGenerator();
    public double computeFitness(Generator gen);
	public void onNewBest(EvoGen evo);
    public void onGeneration(EvoGen evo);
	public String infoString();
}
