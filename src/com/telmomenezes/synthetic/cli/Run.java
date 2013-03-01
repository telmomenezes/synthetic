package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.DiscreteDistrib;
import com.telmomenezes.synthetic.Distrib;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.RandomNet;
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

        String outDir = cline.getOptionValue("odir");
        
        int bins = 100;
     	if(cline.hasOption("bins")) {
            bins = new Integer(cline.getOptionValue("bins"));
        }
        
        int nodeCount = net.getNodeCount();
        int edgeCount = net.getEdgeCount();
        
        Net randomNet = RandomNet.generate(nodeCount, edgeCount);
        
        // write net
        randomNet.save(outDir + "/randomnet.txt", NetFileType.SNAP);
        
        // write distributions
        DiscreteDistrib inDegrees = new DiscreteDistrib(randomNet.inDegSeq());
    	DiscreteDistrib outDegrees = new DiscreteDistrib(randomNet.outDegSeq());
    	Distrib dPageRank = new Distrib(randomNet.prDSeq(), bins);
    	Distrib uPageRank = new Distrib(randomNet.prUSeq(), bins);
    	
    	inDegrees.write(outDir + "/random_in_degrees.csv");
    	outDegrees.write(outDir + "/random_out_degrees.csv");
    	dPageRank.write(outDir + "/random_d_pagerank.csv");
    	uPageRank.write(outDir + "/random_u_pagerank.csv");
    	(new TriadicProfile(randomNet)).write(outDir + "/random_triadic_profile.csv");
        
        return true;
    }
}
