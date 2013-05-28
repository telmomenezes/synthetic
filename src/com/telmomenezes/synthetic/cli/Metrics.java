package com.telmomenezes.synthetic.cli;


import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Metrics extends Command {
	public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet", "");
    	int nodes = getIntegerParam("nodes", 1000);
    	int edges = getIntegerParam("edges", 10000);
    	
        String progFile = getStringParam("prg", "");
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        Net net = null;
        
        if (!netfile.equals("")) {
        	net = Net.load(netfile, directed, par);
        	nodes = net.getNodeCount();
        	edges = net.getEdgeCount();
        }
        else {
        	Generator gen = GeneratorFactory.create(gentype, nodes, edges, directed, par, sr);
            gen.load(progFile);
            gen.run();
            net = gen.getNet();
        }
        
        System.out.println("nodes: " + nodes);
        System.out.println("edges: " + edges);
        
        MetricsBag bag = new MetricsBag(net, bins);
        
        System.out.println("Undirected Distances:");
        System.out.println(bag.getuDists());
        
        System.out.println("Directed Distances:");
        System.out.println(bag.getdDists());
        
        return true;
    }
}
