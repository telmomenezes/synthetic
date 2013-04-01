package com.telmomenezes.synthetic.random;

import java.util.Random;


public class RandomGenerator {

	public static Random random = new MTRandom();
	
	
	public static boolean testProb(double prob) {
		return random.nextDouble() < prob;
	}
	
	
	public static int nextGeometric(double p) {
	    double r = random.nextDouble();
	    double g = Math.log(r) / Math.log(1.0 - r);
	    return (int)g;
	}
}