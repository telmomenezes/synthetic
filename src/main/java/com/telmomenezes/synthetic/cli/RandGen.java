package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.NetParams;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;

public class RandGen extends Command {
    
	@Override
	public String name() {return "randgen";}
	
	
	@Override
	public String help() {
		String help = "Create a random generator program.\n";
		help += "$ synt randgen -oprg <out_generator>\n";
		help += "-undir if network is undirected.\n";
		return help;
    }
	
	
	@Override
	public boolean run() throws SynCliException {
    	String outProg = getStringParam("oprg");
    	boolean directed = !paramExists("undir");
        
    	NetParams netParams = new NetParams(0, 0, directed,false);
        Generator gen = GeneratorFactory.create("exo", netParams, 0);
		if (gen == null) {
			System.err.println("could not load program.");
			return false;
		}
        gen.initRandom();
        
        gen.getProg().write(outProg);
    	
        System.out.println("done.");
        
        return true;
    }
}