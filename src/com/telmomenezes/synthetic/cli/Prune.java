package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.Net;


public class Prune extends Command {
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
    	String progFile = getStringParam("prg");
    	String outProg = getStringParam("oprg");
    	double sr = getDoubleParam("sr", 0.0006);
    	boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        System.out.println(net);
        
        Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
        gen.load(progFile);
        gen.run();
        
        gen.getProg().dynPruning();
        gen.getProg().write(outProg, false);
    	
        System.out.println("done.");
        
        return true;
    }
}
