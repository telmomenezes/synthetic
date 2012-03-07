package com.telmomenezes.synthetic;

import com.telmomenezes.synthetic.emd.EMDL1;


/**
 * N-dimensional histogram.
 * 
 * By histogram it is not meant the visual representation, but rather a matrix
 * that contains the number of cases in a distribution classified according to
 * N metrics. Typically used to represent a distribution of nodes in a network,
 * according to a set of node-level metrics.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Histogram {
	
	private int classes[];
	private int dimensions[];
	
	
	public Histogram(int[] classes, int[] dimensions) {
		this.classes = classes;
		this.dimensions = dimensions;
	}
	
	
	public int distance(Histogram h)
	{
		return EMDL1.distance(classes, h.classes, dimensions);
	}
	
	
	public String toString()
	{
		String str = "";

		for (int i = 0; i < classes.length; i++) {
			
			if (classes[i] > 0) {
				int rest = i;
				int factor = classes.length;
				for (int d = 0; d < dimensions.length; d++) {
					factor /= dimensions[d];
					int coord = rest / factor;
					rest -= coord * factor;
					str += coord + ", ";
				}
				str += "class " + i + ": " + classes[i] + "\n";
			}
		}
		
		return str;
	}
}