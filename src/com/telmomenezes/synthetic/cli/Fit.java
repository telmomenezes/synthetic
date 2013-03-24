package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Fit extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        if(!cline.hasOption("prg")) {
            setErrorMessage("program file must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);

        String progFile = cline.getOptionValue("prg");
        
        int bins = 100;
     	if(cline.hasOption("bins")) {
            bins = new Integer(cline.getOptionValue("bins"));
        }
     	
     	int trials = 50;
     	if(cline.hasOption("trials")) {
            trials = new Integer(cline.getOptionValue("trials"));
        }
     	
     	int runs = 1;
     	if(cline.hasOption("runs")) {
            runs = new Integer(cline.getOptionValue("runs"));
        }
        
        boolean directed = true;
        
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
