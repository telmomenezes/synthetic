package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.RandomNet;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;


public class Random extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        if(!cline.hasOption("odir")) {
            setErrorMessage("output directory must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);

        String outDir = cline.getOptionValue("odir");
        
        int bins = 100;
     	if(cline.hasOption("bins")) {
            bins = new Integer(cline.getOptionValue("bins"));
        }
        
     	int runs = 1;
     	if(cline.hasOption("runs")) {
            runs = new Integer(cline.getOptionValue("runs"));
        }
     	
        int nodeCount = net.getNodeCount();
        int edgeCount = net.getEdgeCount();
        
        System.out.println(net);
        
        MetricsBag realBag = new MetricsBag(net, bins);
        
        boolean append = false;
        for (int i = 0; i < runs; i++) {
        	System.out.println("run #" + i);
        
        	Net randomNet = RandomNet.generate(nodeCount, edgeCount);
        
        	// write net
        	randomNet.save(outDir + "/randomnet.txt", NetFileType.SNAP);
        
        	// compute distribs
        	MetricsBag bag = new MetricsBag(randomNet, null, null, bins, realBag);
        
        	// write distributions
        	bag.getInDegrees().write(outDir + "/random_in_degrees.csv", append);
        	bag.getOutDegrees().write(outDir + "/random_out_degrees.csv", append);
        	bag.getDPageRanks().write(outDir + "/random_d_pagerank.csv", append);
        	bag.getUPageRanks().write(outDir + "/random_u_pagerank.csv", append);
        	(new TriadicProfile(randomNet)).write(outDir + "/random_triadic_profile.csv", append);
        	bag.getdDists().write(outDir + "/random_d_dists.csv", append);
        	bag.getuDists().write(outDir + "/random_u_dists.csv", append);
        	
        	append = true;
        }
        
        System.out.println("done.");
    	
        return true;
    }
}