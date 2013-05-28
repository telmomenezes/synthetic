package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.RandomNet;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;


public class Random extends Command {
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
        String outDir = getStringParam("odir");
        int bins = getIntegerParam("bins", 100);
        int runs = getIntegerParam("runs", 30);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
    	
        Net net = Net.load(netfile, directed, par);
     	
        int nodeCount = net.getNodeCount();
        int edgeCount = net.getEdgeCount();
        
        System.out.println(net);
        
        MetricsBag realBag = new MetricsBag(net, bins);
        
        boolean append = false;
        for (int i = 0; i < runs; i++) {
        	System.out.println("run #" + i);
        
        	Net randomNet = RandomNet.generate(nodeCount, edgeCount, directed, par);
        
        	// write net
        	randomNet.save(outDir + "/randomnet.txt", NetFileType.SNAP);
        
        	// compute distribs
        	MetricsBag bag = new MetricsBag(randomNet, null, null, bins, realBag);
        
        	// write distributions
        	
        	if (directed) {
        		bag.getInDegrees().write(outDir + "/random_in_degrees.csv", append);
        		bag.getOutDegrees().write(outDir + "/random_out_degrees.csv", append);
        		bag.getDPageRanks().write(outDir + "/random_d_pagerank.csv", append);
        		bag.getdDists().write(outDir + "/random_d_dists.csv", append);
        	}
        	else {
        		bag.getDegrees().write(outDir + "/random_degrees.csv", append);
        	}
        	bag.getUPageRanks().write(outDir + "/random_u_pagerank.csv", append);
        	(TriadicProfile.create(randomNet)).write(outDir + "/random_triadic_profile.csv", append);
        	bag.getuDists().write(outDir + "/random_u_dists.csv", append);
        	
        	append = true;
        }
        
        System.out.println("done.");
    	
        return true;
    }
}