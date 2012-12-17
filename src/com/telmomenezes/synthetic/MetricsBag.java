package com.telmomenezes.synthetic;


import com.telmomenezes.synthetic.motifs.TriadicProfile;

public class MetricsBag {
	private Distrib inDegrees;
    private Distrib outDegrees;
    private Distrib inPageRanks;
    private Distrib outPageRanks;
    private TriadicProfile triadicProfile;
    
    private int bins;
    
    private double distance;
    private double inDegreesDist;
    private double outDegreesDist;
    private double inPageRanksDist;
    private double outPageRanksDist;
    private double triadicProfileDist;
    
    MetricsBag(Net net, int bins) {
    	this.bins = bins;
		inDegrees = new Distrib(net.inDegSeq(), this.bins);
		outDegrees = new Distrib(net.outDegSeq(), this.bins);
		inPageRanks = new Distrib(net.prInSeq(), this.bins);
		outPageRanks = new Distrib(net.prOutSeq(), this.bins);
		triadicProfile = new TriadicProfile(net);
		
		distance = 0;
	    inDegreesDist = 0;
	    outDegreesDist = 0;
	    inPageRanksDist = 0;
	    outPageRanksDist = 0;
	    triadicProfileDist = 0;
    }
    
    MetricsBag(Net net, int bins, MetricsBag bag) {
    	this.bins = bins;
		inDegrees = new Distrib(net.inDegSeq(), this.bins, bag.inDegrees);
		outDegrees = new Distrib(net.outDegSeq(), this.bins, bag.outDegrees);
		inPageRanks = new Distrib(net.prInSeq(), this.bins, bag.inPageRanks);
		outPageRanks = new Distrib(net.prOutSeq(), this.bins, bag.outPageRanks);
		triadicProfile = new TriadicProfile(net);
		
		calcDistances(bag);
    }
    
    private void calcDistances(MetricsBag bag) {
        inDegreesDist = inDegrees.emdDistance(bag.inDegrees);
        outDegreesDist = outDegrees.emdDistance(bag.outDegrees);
        inPageRanksDist = inPageRanks.emdDistance(bag.inPageRanks);
        outPageRanksDist = outPageRanks.emdDistance(bag.outPageRanks);
        triadicProfileDist = triadicProfile.emdDistance(bag.triadicProfile);
        
        double verySmall = 0.999;
        if (inDegreesDist == 0) inDegreesDist = verySmall;
        if (outDegreesDist == 0) outDegreesDist = verySmall;
        if (inPageRanksDist == 0) inPageRanksDist = verySmall;
        if (outPageRanksDist == 0) outPageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        
        distance = inDegreesDist * outDegreesDist * inPageRanksDist * outPageRanksDist * triadicProfileDist;
        distance = Math.pow(distance, 1.0 / 5.0);
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

	public double getInPageRanksDist() {
		return inPageRanksDist;
	}
	
	public double getOutPageRanksDist() {
		return outPageRanksDist;
	}

	public double getTriadicProfileDist() {
		return triadicProfileDist;
	}
}
