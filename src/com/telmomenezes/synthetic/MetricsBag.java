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
		inDegrees = new Distrib(net.inDegSeq(), this.bins, bag.inDegrees);
		outDegrees = new Distrib(net.outDegSeq(), this.bins, bag.outDegrees);
		pageRanks = new Distrib(net.prInSeq(), this.bins, bag.pageRanks);
		triadicProfile = new TriadicProfile(net);
		
		calcDistances(bag);
    }
    
    private void calcDistances(MetricsBag bag) {
        inDegreesDist = inDegrees.emdDistance(bag.inDegrees);
        outDegreesDist = outDegrees.emdDistance(outDegrees);
        pageRanksDist = pageRanks.emdDistance(bag.pageRanks);
        triadicProfileDist = triadicProfile.emdDistance(bag.triadicProfile);
        
        double verySmall = 0.999;
        if (inDegreesDist == 0) inDegreesDist = verySmall;
        if (outDegreesDist == 0) outDegreesDist = verySmall;
        if (pageRanksDist == 0) pageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        
        distance = inDegreesDist * outDegreesDist * pageRanksDist * triadicProfileDist;
        distance = Math.pow(distance, 1.0 / 4.0);
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
