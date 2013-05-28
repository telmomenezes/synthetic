package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.Net;


public class EvalStats extends Command {
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
        
        Generator gen = GeneratorFactory.create(gentype, net.getNodeCount(), net.getEdgeCount(), directed, par, sr);
        gen.load(progFile);
        gen.run();
        
        gen.getProg().write(outProg);
    	
        System.out.println("done.");
        
        return true;
    }
}