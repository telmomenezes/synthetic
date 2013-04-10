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
        int runs = getIntegerParam("runs", 30);
    	boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        System.out.println(net);
        
        MetricsBag targBag = new MetricsBag(net, bins);
        
        double meanFit = 0;
        double maxFit = Double.NEGATIVE_INFINITY;
        double minFit = Double.POSITIVE_INFINITY;
        
        double meanFitAvg = 0;
        double maxFitAvg = Double.NEGATIVE_INFINITY;
        double minFitAvg = Double.POSITIVE_INFINITY;
        
        double meanFitMax = 0;
        double maxFitMax = Double.NEGATIVE_INFINITY;
        double minFitMax = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < runs; i++) {
        	System.out.println("run #" + i);
        	
        	Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, trials);
        	gen.load(progFile);
        	gen.run();
        	
        	gen.computeFitness(targBag, bins);
        	double fitAvg = gen.fitnessAvg;
        	double fitMax = gen.fitnessMax;
        	meanFitAvg += fitAvg;
        	meanFitMax += fitMax;
        	
        	if (fitAvg > maxFitAvg) maxFitAvg = fitAvg;
        	if (fitAvg < minFitAvg) minFitAvg = fitAvg;
        	if (fitMax > maxFitMax) maxFitMax = fitMax;
        	if (fitMax < minFitMax) minFitMax = fitMax;
        	
        	System.out.println("fitness (avg): " + fitAvg + "; fitness (max): " + fitMax);
        	System.out.println(gen.getGenBag());
        }
    	
        meanFit /= runs;
        meanFitAvg /= runs;
        meanFitMax /= runs;
        
        System.out.println("\n\n");
        
        System.out.println("mean fitness: " + meanFit + "; min fitness: " + minFit + "; max fitness: " + maxFit);
        System.out.println("mean fitness (avg): " + meanFitAvg + "; min fitness (avg): " + minFitAvg + "; max fitness (avg): " + maxFitAvg);
        System.out.println("mean fitness (max): " + meanFitMax + "; min fitness (max): " + minFitMax + "; max fitness (max): " + maxFitMax);
        
        System.out.println("done.");
        
        return true;
    }
}
