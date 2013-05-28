package com.telmomenezes.synthetic.generators;


public class GeneratorFactory {
	
	public static Generator create(String genType, int nodeCount, int edgeCount,
			boolean directed, boolean parallels, double sr) {
		
		if (genType.equals("endo")) {
			return new EndoGenerator(nodeCount, edgeCount, directed, parallels, sr);
		}
		else if (genType.equals("exo")) {
			return new ExoGenerator(nodeCount, edgeCount, directed, parallels, sr);
		}
		else if (genType.equals("redblue")) {
			return new RedBlueGenerator(nodeCount, edgeCount, directed, parallels, sr);
		}
		
		return null;
	}
}