package com.telmomenezes.synthetic;

import java.util.Random;


/**
 * Singleton to encapsulate a random number generator.
 * 
 * All random generation processes in synthetic should depend on this class,
 * so that we have control over the random number generator seed.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class RandomGenerator {

	private static RandomGenerator _instance = null;
	public Random random;
	
	private RandomGenerator()
	{
		random = new Random();
	}
	
	public static RandomGenerator instance()
	{
		if (_instance == null)
			_instance = new RandomGenerator();

		return _instance;
	}
	
	public int nextGeometric(double p) {
	    double r = random.nextDouble();
	    double g = Math.log(r) / Math.log(1.0 - r);
	    return (int)g;
	}
}