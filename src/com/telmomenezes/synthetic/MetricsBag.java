package com.telmomenezes.synthetic;


import com.telmomenezes.synthetic.motifs.TriadicProfile;
import com.telmomenezes.synthetic.randomwalkers.RandomWalkers;

public class MetricsBag {
	private DiscreteDistrib inDegrees;
    private DiscreteDistrib outDegrees;
    private Distrib dPageRanks;
    private Distrib uPageRanks;
    private TriadicProfile triadicProfile;
    private DiscreteDistrib dDists;
    private DiscreteDistrib uDists;
    
    private int bins;
    
    private double inDegreesDist;
    private double outDegreesDist;
    private double dPageRanksDist;
    private double uPageRanksDist;
    private double triadicProfileDist;
    private double dDistsDist;
    private double uDistsDist;
    
    public MetricsBag(Net net, int bins) {
    	this.bins = bins;
		inDegrees = new DiscreteDistrib(net.inDegSeq());
		outDegrees = new DiscreteDistrib(net.outDegSeq());
		dPageRanks = new Distrib(net.prDSeq(), this.bins);
		uPageRanks = new Distrib(net.prUSeq(), this.bins);
		triadicProfile = new TriadicProfile(net);
		
		// distances
		if (net.isDirected()) {
			RandomWalkers dRW = new RandomWalkers(net, true);
			dRW.allSteps();
			dDists = dRW.getDistrib();
			System.out.println(dDists);
		}
		else {
			dDists = null;
		}
		RandomWalkers uRW = new RandomWalkers(net, false);
		uRW.allSteps();
		uDists = uRW.getDistrib();
		System.out.println(uDists);
		
	    inDegreesDist = 0;
	    outDegreesDist = 0;
	    dPageRanksDist = 0;
	    uPageRanksDist = 0;
	    triadicProfileDist = 0;
	    dDistsDist = 0;
	    uDistsDist = 0;
    }
    
    public MetricsBag(Net net, RandomWalkers dRW, RandomWalkers uRW, int bins, MetricsBag bag) {
    	this.bins = bins;
		inDegrees = new DiscreteDistrib(net.inDegSeq(), bag.inDegrees);
		outDegrees = new DiscreteDistrib(net.outDegSeq(), bag.outDegrees);
		dPageRanks = new Distrib(net.prDSeq(), this.bins, bag.dPageRanks);
		uPageRanks = new Distrib(net.prUSeq(), this.bins, bag.uPageRanks);
		triadicProfile = new TriadicProfile(net);
		if (dRW != null) {
			dDists = dRW.getDistrib();
		}
		else {
			DistMatrix dMatrix = new DistMatrix(net.getNodeCount(), true);
			dMatrix.calc(net);
			dDists = dMatrix.getDistrib();
		}
		if (uRW != null) {
			uDists = uRW.getDistrib();
		}
		else {
			DistMatrix uMatrix = new DistMatrix(net.getNodeCount(), false);
			uMatrix.calc(net);
			uDists = uMatrix.getDistrib();
		}
		
		calcDistances(bag);
    }
    
    private void calcDistances(MetricsBag bag) {
        inDegreesDist = inDegrees.emdDistance(bag.inDegrees);
        outDegreesDist = outDegrees.emdDistance(bag.outDegrees);
        dPageRanksDist = dPageRanks.emdDistance(bag.dPageRanks);
        uPageRanksDist = uPageRanks.emdDistance(bag.uPageRanks);
        triadicProfileDist = triadicProfile.proportionalDistance(bag.triadicProfile);
        if (dDists != null) {
        	dDistsDist = dDists.proportionalDistance(bag.dDists);
        }
        else {
        	dDistsDist = 0;
        }
        uDistsDist = uDists.proportionalDistance(bag.uDists);
        
        double verySmall = 0.999;
        if (inDegreesDist == 0) inDegreesDist = verySmall;
        if (outDegreesDist == 0) outDegreesDist = verySmall;
        if (dPageRanksDist == 0) dPageRanksDist = verySmall;
        if (uPageRanksDist == 0) uPageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        if (dDistsDist == 0) dDistsDist = verySmall;
        if (uDistsDist == 0) uDistsDist = verySmall;
    }
    
    
    public boolean paretoDominates(MetricsBag bag) {
    	if (inDegreesDist < bag.inDegreesDist) {
    		return true;
    	}
    	if (outDegreesDist < bag.outDegreesDist) {
    		return true;
    	}
    	if (dPageRanksDist < bag.dPageRanksDist) {
    		return true;
    	}
    	if (uPageRanksDist < bag.uPageRanksDist) {
    		return true;
    	}
    	if (triadicProfileDist < bag.triadicProfileDist) {
    		return true;
    	}
    	
    	return false;
    }

	public double getInDegreesDist() {
		return inDegreesDist;
	}

	public double getOutDegreesDist() {
		return outDegreesDist;
	}

	public double getDPageRanksDist() {
		return dPageRanksDist;
	}
	
	public double getUPageRanksDist() {
		return uPageRanksDist;
	}

	public double getTriadicProfileDist() {
		return triadicProfileDist;
	}

	public DiscreteDistrib getInDegrees() {
		return inDegrees;
	}

	public Distrib getDPageRanks() {
		return dPageRanks;
	}
	
	public Distrib getUPageRanks() {
		return uPageRanks;
	}

	public DiscreteDistrib getOutDegrees() {
		return outDegrees;
	}
	
	public double getdDistsDist() {
		return dDistsDist;
	}

	public double getuDistsDist() {
		return uDistsDist;
	}

	@Override
	public String toString() {
		String str = "";
		
		str += triadicProfile;
		str += "\n";
		
		str += "inDegreesDist: " + inDegreesDist;
		str += " outDegreesDist: " + outDegreesDist;
		str += "\n";
		str += "dPageRanksDist: " + dPageRanksDist;
		str += " uPageRanksDist: " + uPageRanksDist;
		str += "\n";
		str += "dDistsDist: " + dDistsDist;
		str += " uDistsDist: " + uDistsDist;
		str += "\n";
		str += "triadicProfileDist: " + triadicProfileDist;
		
		return str;
	}

	public TriadicProfile getTriadicProfile() {
		return triadicProfile;
	}

	public DiscreteDistrib getdDists() {
		return dDists;
	}

	public DiscreteDistrib getuDists() {
		return uDists;
	}
}