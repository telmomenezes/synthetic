package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.DiscreteDistrib;
import com.telmomenezes.synthetic.Distrib;
import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;


public class Run extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        if(!cline.hasOption("odir")) {
            setErrorMessage("output directory must be specified");
            return false;
        }
        if(!cline.hasOption("prg")) {
            setErrorMessage("program file must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);

        String progFile = cline.getOptionValue("prg");
        
        String outDir = cline.getOptionValue("odir");
        
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
        	DiscreteDistrib inDegrees = new DiscreteDistrib(syntNet.inDegSeq());
        	DiscreteDistrib outDegrees = new DiscreteDistrib(syntNet.outDegSeq());
        	Distrib dPageRank = new Distrib(syntNet.prDSeq(), bins, dPageRankReal);
        	Distrib uPageRank = new Distrib(syntNet.prUSeq(), bins, uPageRankReal);
        	DiscreteDistrib dDistsDist = gen.getDistMatrixD().getDistrib();
        	DiscreteDistrib uDistsDist = gen.getDistMatrixU().getDistrib();
    	
        	inDegrees.write(outDir + "/in_degrees.csv", append);
        	outDegrees.write(outDir + "/out_degrees.csv", append);
        	dPageRank.write(outDir + "/d_pagerank.csv", append);
        	uPageRank.write(outDir + "/u_pagerank.csv", append);
        	(new TriadicProfile(syntNet)).write(outDir + "/triadic_profile.csv", append);
        	dDistsDist.write(outDir + "/d_dists.csv", append);
        	uDistsDist.write(outDir + "/u_dists.csv", append);
        	
        	append = true;
        }
    	
        System.out.println("done.");
        
        return true;
    }
}
