package com.telmomenezes.synthetic.cli;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class DetailFit extends Command {
	
    public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String dir = getStringParam("dir");
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        int runs = getIntegerParam("runs", 30);
        boolean directed = !paramExists("undir");
        
        Net net = Net.load(netfile, directed);
        
        MetricsBag targBag = new MetricsBag(net, bins);
        
        List<String> prgFiles = textFiles(dir);
        
        DecimalFormat df = new DecimalFormat("##");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        
        String str = "prog";
    	str += ",fit";
    	str += ",program_size";
    	
    	if (directed) {
    		str += ",in_degrees_dist";
    		str += ",out_degrees_dist";
    		str += ",d_pageranks_dist";
    	}
    	else {
    		str += ",degrees_dist";
    	}
    	str += ",u_pageranks_dist";
    	str += ",triadic_profile_dist";
    	if (directed) {
    		str += ",d_dists_dist";
    	}
    	str += ",u_dists_dist";
    	
    	System.out.println(str);
    	
        for (String progFile : prgFiles) {
        
        	double fit = 0;
        	double degreesDist = 0;
        	double inDegreesDist = 0;
        	double outDegreesDist = 0;
        	double dPageRanksDist = 0;
        	double uPageRanksDist = 0;
        	double triadicProfileDist = 0;
        	double dDistsDist = 0;
        	double uDistsDist = 0;
        		
        	for (int i = 0; i < runs; i++) {
        		Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
        		gen.load(dir + "/" + progFile);
        		gen.run();
            	
        		fit += gen.computeFitness(targBag, bins);
            	MetricsBag bag = gen.getGenBag();
        		
        		degreesDist += bag.getdDistsDist();
            	inDegreesDist += bag.getInDegreesDist();
            	outDegreesDist += bag.getOutDegreesDist();
            	dPageRanksDist += bag.getDPageRanksDist();
            	uPageRanksDist += bag.getUPageRanksDist();
            	triadicProfileDist += bag.getTriadicProfileDist();
            	dDistsDist += bag.getdDistsDist();
            	uDistsDist += bag.getuDistsDist();
        	}
        	
        	// determine prunned program size
        	Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, sr);
    		gen.load(dir + "/" + progFile);
    		gen.run();
    		gen.getProg().dynPruning();
        	
        	fit /= runs;
        	degreesDist /= runs;
        	inDegreesDist /= runs;
        	outDegreesDist /= runs;
        	dPageRanksDist /= runs;
        	uPageRanksDist /= runs;
        	triadicProfileDist /= runs;
        	dDistsDist /= runs;
        	uDistsDist /= runs;
        	
        	str = progFile.split("\\.")[0];
        	str += "," + df.format(fit);
        	str += "," + gen.getProg().size();
        	
        	if (directed) {
        		str += "," + df.format(inDegreesDist);
        		str += "," + df.format(outDegreesDist);
        		str += "," + df.format(dPageRanksDist);
        	}
        	else {
        		str += "," + df.format(degreesDist);
        	}
        	str += "," + df.format(uPageRanksDist);
        	str += "," + df.format(triadicProfileDist);
        	if (directed) {
        		str += "," + df.format(dDistsDist);
        	}
        	str += "," + df.format(uDistsDist);
        	
        	System.out.println(str);
        }
        
        return true;
    }
}