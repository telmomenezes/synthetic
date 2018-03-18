package com.telmomenezes.synthetic;

class RandomBag {
	
	double degreesDistAvg;
	double inDegreesDistAvg;
	double outDegreesDistAvg;
	double dPageRanksDistAvg;
	double uPageRanksDistAvg;
	double triadicProfileDistAvg;
	double dDistsDistAvg;
	double uDistsDistAvg;
	
	RandomBag(MetricsBag targBag, int bins, int samples) {
		
		degreesDistAvg = 0;
	    inDegreesDistAvg = 0;
	    outDegreesDistAvg = 0;
	    dPageRanksDistAvg = 0;
	    uPageRanksDistAvg = 0;
	    triadicProfileDistAvg = 0;
	    dDistsDistAvg = 0;
	    uDistsDistAvg = 0;
	    
		for (int i = 0; i < samples; i ++) {
			System.out.println("random run #" + i);
			
			Net randomNet = RandomNet.generate(targBag.getNet());
			MetricsBag randomBag = new MetricsBag(randomNet, null, null, bins, targBag, false);
			
			degreesDistAvg += randomBag.getDegreesDist();
		    inDegreesDistAvg += randomBag.getInDegreesDist();
		    outDegreesDistAvg += randomBag.getOutDegreesDist();
		    dPageRanksDistAvg += randomBag.getDPageRanksDist();
		    uPageRanksDistAvg += randomBag.getUPageRanksDist();
		    triadicProfileDistAvg += randomBag.getTriadicProfileDist();
		    dDistsDistAvg += randomBag.getdDistsDist();
		    uDistsDistAvg += randomBag.getuDistsDist();
		}
		
		degreesDistAvg /= samples;
	    inDegreesDistAvg /= samples;
	    outDegreesDistAvg /= samples;
	    dPageRanksDistAvg /= samples;
	    uPageRanksDistAvg /= samples;
	    triadicProfileDistAvg /= samples;
	    dDistsDistAvg /= samples;
	    uDistsDistAvg /= samples;
	}
}
