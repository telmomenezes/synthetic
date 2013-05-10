package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.telmomenezes.synthetic.Generator;
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
        
        Net net = Net.load(netfile, directed);
        
        System.out.println(net);
        MetricsBag targBag = new MetricsBag(net, bins);
        
        List<String> prgFiles = textFiles(dir);
        
        try{
        	FileWriter fstream = new FileWriter(outFile);
        	BufferedWriter out = new BufferedWriter(fstream);
        
        	out.write("prog,fit\n");
    	
        	for (String progFile : prgFiles) {
        		System.out.println("-> " + progFile);
        		for (int i = 0; i < runs; i++) {
        			System.out.println("run #" + i);
            	
        			Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
        			gen.load(dir + "/" + progFile);
        			gen.run();
            	
        			double fit = gen.computeFitness(targBag, bins);
        			
        			String[] tokens = progFile.split("\\.");
        			out.write(tokens[0] + "," + fit + "\n");
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