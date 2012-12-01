package com.telmomenezes.synthetic;


import com.telmomenezes.synthetic.motifs.TriadicProfile;

public class MetricsBag {
	private Distrib inDegrees;
    private Distrib outDegrees;
    private Distrib pageRanks;
    private TriadicProfile triadicProfile;
    
    private int bins;
    
    private double distance;
    private double inDegreesDist;
    private double outDegreesDist;
    private double pageRanksDist;
    private double triadicProfileDist;
    
    MetricsBag(Net net, int bins) {
    	this.bins = bins;
		inDegrees = new Distrib(net.inDegSeq(), this.bins);
		outDegrees = new Distrib(net.outDegSeq(), this.bins);
		pageRanks = new Distrib(net.prInSeq(), this.bins);
		triadicProfile = new TriadicProfile(net);
		
		distance = 0;
	    inDegreesDist = 0;
	    outDegreesDist = 0;
	    pageRanksDist = 0;
	    triadicProfileDist = 0;
    }
    
    MetricsBag(Net net, int bins, MetricsBag bag) {
    	this.bins = bins;
    	//System.out.println("#A");
		inDegrees = new Distrib(net.inDegSeq(), this.bins, bag.inDegrees);
		//System.out.println("#B");
		outDegrees = new Distrib(net.outDegSeq(), this.bins, bag.outDegrees);
		//System.out.println("#C");
		pageRanks = new Distrib(net.prInSeq(), this.bins, bag.pageRanks);
		//System.out.println("#D");
		triadicProfile = new TriadicProfile(net);
		//System.out.println("#E");
		
		calcDistances(bag);
    }
    
    private void calcDistances(MetricsBag bag) {
    	//System.out.println("#1");
        inDegreesDist = inDegrees.emdDistance(bag.inDegrees);
        //System.out.println("#2");
        outDegreesDist = outDegrees.emdDistance(bag.outDegrees);
        //System.out.println("#3");
        pageRanksDist = pageRanks.emdDistance(bag.pageRanks);
        //System.out.println("#4");
        triadicProfileDist = triadicProfile.emdDistance(bag.triadicProfile);
        //System.out.println("#5");
        
        double verySmall = 0.999;
        if (inDegreesDist == 0) inDegreesDist = verySmall;
        if (outDegreesDist == 0) outDegreesDist = verySmall;
        if (pageRanksDist == 0) pageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        
        distance = inDegreesDist * outDegreesDist * pageRanksDist * triadicProfileDist;
        distance = Math.pow(distance, 1.0 / 4.0);
        //System.out.println("#6");
    }

	public double getDistance() {
		return distance;
	}

	public double getInDegreesDist() {
		return inDegreesDist;
	}

	public double getOutDegreesDist() {
		return outDegreesDist;
	}

	public double getPageRanksDist() {
		return pageRanksDist;
	}

	public double getTriadicProfileDist() {
		return triadicProfileDist;
	}
}
