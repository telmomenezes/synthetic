package com.telmomenezes.synthetic.cli;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.Net;


public class Dists extends Command {
	
    public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String dir = getStringParam("dir");
        String outFile = getStringParam("out");
        double sr = getDoubleParam("sr", 0.0006);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        Net net = Net.load(netfile, directed, par);
        
        System.out.println(net);
        
        List<String> prgFiles = textFiles(dir);
        int progCount = prgFiles.size();
        
        try {
        	FileWriter fstream = new FileWriter(outFile);
        	BufferedWriter out = new BufferedWriter(fstream);
        
        	out.write("X");
        	for (int x = 0; x < progCount; x++) {
        		String progFile1 = prgFiles.get(x);
        		out.write("," + progFile1);
        	}
        	out.write("\n");
        	
        	for (int x = 0; x < progCount; x++) {
        		String progFile1 = prgFiles.get(x);
        		out.write(progFile1);
        		for (int y = 0; y < progCount; y++) {
        			String progFile2 = prgFiles.get(y);
        		
        			System.out.println(progFile1 + " -> " + progFile2);
        		
        			Generator gen1 = GeneratorFactory.create(gentype, net.getNodeCount(), net.getEdgeCount(), directed, par, sr);
        			gen1.load(dir + "/" + progFile1);
        		
        			Generator gen2 = GeneratorFactory.create(gentype, net.getNodeCount(), net.getEdgeCount(), directed, par, sr);
        			gen2.load(dir + "/" + progFile2);
        		
        			double dist1 = gen1.run(gen2);
        			double dist2 = gen2.run(gen1);
        			//double dist = Math.min(dist1, dist2);
        			double dist = (dist1 + dist2) / 2;
        			
        			System.out.println("dist: " + dist);
        			out.write("," + dist);
        		}
        		out.write("\n");
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