package com.telmomenezes.synthetic.cli;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.Net;


public class CompProgs extends Command {
	
    public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String dir = getStringParam("dir");
        String outFile = getStringParam("out");
        double sr = getDoubleParam("sr", 0.0006);
        boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        
        System.out.println(net);
        
        List<String> prgFiles = textFiles(dir);
        int progCount = prgFiles.size();
        
        double[][] dists = new double[progCount][progCount];
        
        int x = 0;
        int y = 0;
        for (String progFile1 : prgFiles) {
        	for (String progFile2 : prgFiles) {
        		System.out.println(progFile1 + " -> " + progFile2);
        		
        		Generator gen1 = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
        		gen1.load(dir + "/" + progFile1);
        		Generator gen2 = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
        		gen2.load(dir + "/" + progFile2);
        		
        		double dist = gen1.runCompare(gen2);
        		
        		dists[x][y] = dist;
        		
        		System.out.println("distance: " + dist);
        		
        		x++;
        	}
        	y++;
        	x = 0;
        }
        
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
        }
        
        System.out.println("done.");
        
        return true;
    }
}