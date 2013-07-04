package com.telmomenezes.synthetic.generators;

import com.telmomenezes.synthetic.NetParams;


public class GeneratorFactory {
	
	public static Generator create(String genType, NetParams netParams, double sr) {
		
		if (genType.equals("endo")) {
			return new EndoGenerator(netParams, sr);
		}
		else if (genType.equals("exo")) {
			return new ExoGenerator(netParams, sr);
		}
		
		return null;
	}
}