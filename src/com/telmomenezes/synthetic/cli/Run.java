package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.DiscreteDistrib;
import com.telmomenezes.synthetic.Distrib;
import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;


public class Run extends Command {
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
    	
        String outDir = getStringParam("odir");
        String progFile = getStringParam("prg");
        int bins = getIntegerParam("bins", 100);
        int trials = getIntegerParam("trials", 50);
        int runs = getIntegerParam("runs", 30);
        boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        
        Distrib dPageRankReal = new Distrib(net.prDSeq(), bins);
    	Distrib uPageRankReal = new Distrib(net.prUSeq(), bins);
        
        System.out.println(net);
        
        boolean append = false;
        for (int i = 0; i < runs; i++) {
        	System.out.println("run #" + i);
        	
        	Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, trials);
        	gen.load(progFile);
        	gen.run();
        	Net syntNet = gen.getNet();
        
        	// write net
        	syntNet.save(outDir + "/syntnet" + i + ".txt", NetFileType.SNAP);
        
        	// write distributions
        	if (directed) {
        		DiscreteDistrib inDegrees = new DiscreteDistrib(syntNet.inDegSeq());
        		DiscreteDistrib outDegrees = new DiscreteDistrib(syntNet.outDegSeq());
        		Distrib dPageRank = new Distrib(syntNet.prDSeq(), bins, dPageRankReal);
        		DiscreteDistrib dDistsDist = gen.getNet().dDistMatrix.getDistrib();
        		
        		inDegrees.write(outDir + "/in_degrees.csv", append);
            	outDegrees.write(outDir + "/out_degrees.csv", append);
            	dPageRank.write(outDir + "/d_pagerank.csv", append);
            	dDistsDist.write(outDir + "/d_dists.csv", append);
        	}
        	else {
        		DiscreteDistrib degrees = new DiscreteDistrib(syntNet.degSeq());
        		
        		degrees.write(outDir + "/degrees.csv", append);
        	}
        	Distrib uPageRank = new Distrib(syntNet.prUSeq(), bins, uPageRankReal);
        	DiscreteDistrib uDistsDist = gen.getNet().uDistMatrix.getDistrib();
    	
        	
        	uPageRank.write(outDir + "/u_pagerank.csv", append);
        	(TriadicProfile.create(syntNet)).write(outDir + "/triadic_profile.csv", append);
        	uDistsDist.write(outDir + "/u_dists.csv", append);
        	
        	append = true;
        }
    	
        System.out.println("done.");
        
        return true;
    }
}
