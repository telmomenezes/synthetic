package com.telmomenezes.synthetic.cli;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class DistFit extends Command {
	
    public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String dir = getStringParam("dir");
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        
        System.out.println(net);
        
        List<String> prgFiles = textFiles(dir);
        int progCount = prgFiles.size();
        
        Map<String, Generator> genMap = new HashMap<String, Generator>();
        Map<String, MetricsBag> bagMap = new HashMap<String, MetricsBag>();
        for (String progFile : prgFiles) {
        	System.out.println("running " + progFile);
        	Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
    		gen.load(dir + "/" + progFile);
    		gen.run();
    		
    		genMap.put(progFile, gen);
    		
    		MetricsBag genBag = new MetricsBag(gen.getNet(), bins);
    		//MetricsBag genBag = new MetricsBag(net, bins);
    		bagMap.put(progFile, genBag);
        }
        
        double[] meanDists = new double[progCount];
        for (int x = 0; x < progCount; x++) {
        	for (int y = 0; y < progCount; y++) {
        		String progFile1 = prgFiles.get(x);
        		String progFile2 = prgFiles.get(y);
        		
        		System.out.println(progFile1 + " -> " + progFile2);
        		
        		Generator gen = genMap.get(progFile1);
        		MetricsBag bag = bagMap.get(progFile2);
        		
        		double dist = gen.computeFitness(bag, bins);
        		meanDists[x] += dist;
        		meanDists[y] += dist;
        	}
        }
        
        for (int i = 0; i < progCount; i++) {
        	meanDists[i] /= progCount * 2;
        }
        
        for (int i = 0; i < progCount; i++) {
        	System.out.println("prog" + i + ": meanDist=" + meanDists[i]);
        }
        
        /*
        System.out.println("Writing output file...");
        try {
        	FileWriter fstream = new FileWriter(outFile);
        	BufferedWriter out = new BufferedWriter(fstream);
        
        	for (int i = 0; i < progCount; i++) {
        		if (i > 0) out.write(",");
        		out.write("p" + i);
        	}
        	out.write("\n");
        	
        	for (y = 0; y < progCount; y++) {
        		for (x = 0; x < progCount; x++) {
        			if (x > 0) out.write(",");
        			out.write("" + dists[x][y]);
        		}
        		out.write("\n");
        	}
        
        	out.close();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }*/
        
        System.out.println("done.");
        
        return true;
    }
}