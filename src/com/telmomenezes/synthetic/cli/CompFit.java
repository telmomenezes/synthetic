package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class CompFit extends Command {
	
    public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String dir = getStringParam("dir");
        String outFile = getStringParam("out");
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        int runs = getIntegerParam("runs", 30);
        boolean directed = !paramExists("undir");
        boolean mean = paramExists("mean");
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        Net net = Net.load(netfile, directed, par);
        
        System.out.println(net);
        MetricsBag targBag = new MetricsBag(net, bins);
        
        List<String> prgFiles = textFiles(dir);
        
        try{
        	FileWriter fstream = new FileWriter(outFile);
        	BufferedWriter out = new BufferedWriter(fstream);
        
        	out.write("prog,fit_max,fit_avg\n");
    	
        	for (String progFile : prgFiles) {
        		System.out.println("-> " + progFile);
        		
        		String[] tokens = progFile.split("\\.");
        		
        		double fitMax = 0;
        		double fitAvg = 0;
        		
        		for (int i = 0; i < runs; i++) {
        			System.out.println("run #" + i);
            	
        			Generator gen = GeneratorFactory.create(gentype, net, sr);
        			gen.load(dir + "/" + progFile);
        			gen.run();
        			gen.computeFitness(targBag, bins);
        			
        			if (mean) {
        				fitMax += gen.fitnessMax;
        				fitAvg += gen.fitnessAvg;
        			}
        			else {
        				fitMax = gen.fitnessMax;
        				fitAvg = gen.fitnessAvg;
        			
        				out.write(tokens[0] + "," + fitMax + "," + fitAvg + "\n");
        			}
        		}
        		
        		if (mean) {
        			fitMax /= runs;
        			fitAvg /= runs;
        			out.write(tokens[0] + "," + fitMax + "," + fitAvg + "\n");
        		}
        	}
        
        	out.close();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        
        System.out.println("done.");
        
        return true;
    }
}