package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Fit extends Command {
	
	@Override
	public String name() {return "fit";}
	
	
	@Override
	public String help() {
		String help = "Computes mean fitness for several runs of a generator.\n";
		help += "$ synt fit -inet <network> -prg <generator>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-sr <n> sample ratio (default is 0.0006).\n";
		help += "-bins <n> distribution bins (default is 100).\n";
		help += "-runs <n> number of runs per program (default is 30).\n";
		return help;
    }
	
	
	@Override
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
    	String progFile = getStringParam("prg");
    	double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        int runs = getIntegerParam("runs", 30);
    	boolean directed = !paramExists("undir");
    	boolean par = paramExists("par");
    	String gentype = getStringParam("gentype", "exo");
    	
        Net net = Net.load(netfile, directed, par);
        System.out.println(net);
        
        MetricsBag targBag = new MetricsBag(net, bins);

        double meanFitAvg = 0;
        double maxFitAvg = Double.NEGATIVE_INFINITY;
        double minFitAvg = Double.POSITIVE_INFINITY;
        
        double meanFitMax = 0;
        double maxFitMax = Double.NEGATIVE_INFINITY;
        double minFitMax = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < runs; i++) {
        	System.out.println("run #" + i);
        	
        	Generator gen = GeneratorFactory.create(gentype, net.getNetParams(), sr);
        	gen.load(progFile);
        	gen.run();
        	
        	gen.computeFitness(targBag, bins, true);
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

        meanFitAvg /= runs;
        meanFitMax /= runs;
        
        System.out.println("\n\n");

        System.out.println("mean fitness (avg): " + meanFitAvg + "; min fitness (avg): "
                + minFitAvg + "; max fitness (avg): " + maxFitAvg);
        System.out.println("mean fitness (max): " + meanFitMax + "; min fitness (max): "
                + minFitMax + "; max fitness (max): " + maxFitMax);
        
        System.out.println("done.");
        
        return true;
    }
}
