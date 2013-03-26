package com.telmomenezes.synthetic.cli;


import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Fit extends Command {
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
    	String progFile = getStringParam("prg");
    	int trials = getIntegerParam("trials", 50);
        int bins = getIntegerParam("bins", 100);
        int runs = getIntegerParam("runs", 1);
    	boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        System.out.println(net);
        
        MetricsBag targBag = new MetricsBag(net, bins);
        
        double meanFit = 0;
        double maxFit = Double.NEGATIVE_INFINITY;
        double minFit = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < runs; i++) {
        	System.out.println("run #" + i);
        	
        	Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, trials);
        	gen.load(progFile);
        	gen.run();
        	
        	double fit = gen.computeFitness(targBag, bins, false);
        	meanFit += fit;
        	
        	if (fit > maxFit) {
        		maxFit = fit;
        	}
        	if (fit < minFit) {
        		minFit = fit;
        	}
        	
        	System.out.println("fitness: " + fit);
        	System.out.println(gen.getGenBag());
        }
    	
        meanFit /= runs;
        
        System.out.println("\n" + "mean fitness: " + meanFit + "; min fitness: " + minFit + "; max fitness: " + maxFit + "\n");
        
        System.out.println("done.");
        
        return true;
    }
}
