package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;


public class GenNet extends Command {
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet", "");
    	int nodes = getIntegerParam("nodes", 1000);
    	int edges = getIntegerParam("edges", 10000);
    	
        String outNet = getStringParam("onet");
        String progFile = getStringParam("prg");
        double sr = getDoubleParam("sr", 0.0006);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        if (!netfile.equals("")) {
        	Net net = Net.load(netfile, directed, par);
        	nodes = net.getNodeCount();
        	edges = net.getEdgeCount();
        }
        System.out.println("nodes: " + nodes);
        System.out.println("edges: " + edges);
        	
        Generator gen = GeneratorFactory.create(gentype, nodes, edges, directed, par, sr);
        gen.load(progFile);
        gen.run();
        Net syntNet = gen.getNet();
        
        // write net
        syntNet.save(outNet, NetFileType.SNAP);
    	
        System.out.println("done.");
        
        return true;
    }
}