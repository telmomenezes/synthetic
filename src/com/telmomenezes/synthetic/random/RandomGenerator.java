package com.telmomenezes.synthetic.random;

import java.util.Random;


public class RandomGenerator {

	private static RandomGenerator _instance = null;
	public Random random;
	
	
	private RandomGenerator()
	{
		random = new MTRandom();
		//random = new Random();
	}
	
	
	public static RandomGenerator instance()
	{
		if (_instance == null)
			_instance = new RandomGenerator();

		return _instance;
	}
	
	
	public boolean testProb(double prob) {
		return random.nextDouble() < prob;
	}
	
	
	public int nextGeometric(double p) {
	    double r = random.nextDouble();
	    double g = Math.log(r) / Math.log(1.0 - r);
	    return (int)g;
	}
}