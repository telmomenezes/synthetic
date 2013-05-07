package com.telmomenezes.synthetic.cli;


import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Metrics extends Command {
	public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet", "");
    	int nodes = getIntegerParam("nodes", 1000);
    	int edges = getIntegerParam("edges", 10000);
    	
        String progFile = getStringParam("prg", "");
        int trials = getIntegerParam("trials", 50);
        int bins = getIntegerParam("bins", 100);
        boolean directed = !paramExists("undir");
        
        Net net = null;
        
        if (!netfile.equals("")) {
        	net = Net.load(netfile, directed);
        	nodes = net.getNodeCount();
        	edges = net.getEdgeCount();
        }
        else {
        	Generator gen = new Generator(nodes, edges, directed, trials);
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
