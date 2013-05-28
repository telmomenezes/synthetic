package com.telmomenezes.synthetic.generators;

import com.telmomenezes.synthetic.Net;


public class GeneratorFactory {
	
	public static Generator create(String genType, Net net, double sr) {
		
		if (genType.equals("endo")) {
			return new EndoGenerator(net, sr);
		}
		else if (genType.equals("exo")) {
			return new ExoGenerator(net, sr);
		}
		else if (genType.equals("redblue")) {
			return new RedBlueGenerator(net, sr);
		}
		
		return null;
	}
}