package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.Net;


public class Prune extends Command {
    
	@Override
	public String help() {
		String help = "Simplify generator program.\n";
		help += "$ synt prune -inet <network> -prg <in_generator> -oprg <out_generator>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-sr <n> sample ratio (default is 0.0006).\n";
		return help;
    }
	
	
	@Override
	public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
    	String progFile = getStringParam("prg");
    	String outProg = getStringParam("oprg");
    	double sr = getDoubleParam("sr", 0.0006);
    	boolean directed = !paramExists("undir");
    	boolean par = paramExists("par");
    	String gentype = getStringParam("gentype", "exo");
    	
        Net net = Net.load(netfile, directed, par);
        System.out.println(net);
        
        Generator gen = GeneratorFactory.create(gentype, net.getNetParams(), sr);
        gen.load(progFile);
        gen.run();
        
        gen.getProg().dynPruning();
        gen.getProg().write(outProg);
    	
        System.out.println("done.");
        
        return true;
    }
}
