package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.NetParams;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;


public class Const extends Command {
    
	@Override
	public String name() {return "const";}
	
	
	@Override
	public String help() {
		String help = "Check if generator weight is constant (random network generator).\n";
		help += "$ synt const -prg <generator>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-sr <n> sample ratio (default is 0.0006).\n";
		help += "-nodes <n> number of nodes (default is 1000).\n";
		help += "-edges <n> number or edges (default is 10000).\n";
		return help;
    }
	
	
	@Override
	public boolean run() throws SynCliException {
        String progFile = getStringParam("prg");
        double sr = getDoubleParam("sr", 0.0006);
        boolean directed = !paramExists("undir");
        int nodes = getIntegerParam("nodes", 1000);
    	int edges = getIntegerParam("edges", 10000);
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        	
        NetParams netParams = new NetParams(nodes, edges, directed, par);
        Generator gen = GeneratorFactory.create(gentype, netParams, sr);
        if (gen != null) {
            gen.load(progFile);
            gen.run();
            System.out.println("constant: " + gen.isConstant());
        }
        else {
            System.err.println("could not load program file.");
        }

        return true;
    }
}
