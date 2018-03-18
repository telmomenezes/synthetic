package com.telmomenezes.synthetic.generators;

import java.io.IOException;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.NetParams;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.random.RandomGenerator;
import com.telmomenezes.synthetic.randomwalkers.RandomWalkers;
import com.telmomenezes.synthetic.gp.Prog;


public abstract class Generator { 
	
    protected double sr;
    
    private int trials;
    
    protected Prog prog;

    public double fitnessAvg;
    public double fitnessMax;
    
    NetParams netParams;
    protected Net net;
    
    protected int time;
    
    private int[] sampleOrigs;
    private int[] sampleTargs;
    private double[] sampleWeights;
    
    private MetricsBag genBag;
    
    protected boolean valid;
    
    private double lastWeight;
    private boolean constant;
    
    public abstract Generator instance();
	public abstract Generator clone();
	protected abstract void setProgVars(int origIndex, int targIndex);

	
	private void createNodes() {
        for (int i = 0; i < netParams.getNodeCount(); i++) {
            net.addNode();
        }
	}
	
    
	public Generator(NetParams netParams, double sr) {
	    this.netParams = netParams;
	    this.sr = sr;
	    
	    this.trials = (int)(sr * (netParams.getNodeCount() * netParams.getNodeCount()));
	    if (trials < 2) trials = 2;
	    
	    sampleOrigs = new int[trials];
	    sampleTargs = new int[trials];
	    sampleWeights = new double[trials];
	    
	    valid = true;
		
		fitnessAvg = 0;
		fitnessMax = 0;
	}
	
	
	public void load(String filePath) {
		try {
			prog.load(filePath);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private int getRandomNode() {
		return RandomGenerator.random.nextInt(netParams.getNodeCount());
	}
	
	
	private void generateSample() {
		for (int i = 0; i < trials; i++) {
            int origIndex = -1;
            int targIndex = -1;
            boolean found = false;
            	
            while (!found) {
            	origIndex = getRandomNode();
            	targIndex = getRandomNode();
            		
            	if (origIndex != targIndex) {
            		if (netParams.getParallels() || (!net.edgeExists(origIndex, targIndex))) {
            			found = true;
            		}
            	}
            }
            
            sampleOrigs[i] = origIndex;
            sampleTargs[i] = targIndex;
		}
	}
	
	
	private Edge cycle(Generator master) {
		if (master == null) {
			generateSample();
		}
		else {
			sampleOrigs = master.sampleOrigs;
			sampleTargs = master.sampleTargs;
		}
			
        int bestOrigIndex;
        int bestTargIndex;
        
        double totalWeight = 0;
        
        for (int i = 0; i < trials; i++) {
            setProgVars(sampleOrigs[i], sampleTargs[i]);
                    
            double weight = prog.eval();
            if (weight < 0) {
            	weight = 0;
            }
            	
            if (Double.isNaN(weight)) {
            	weight = 0;
            	valid = false;
            }
            
            sampleWeights[i] = weight;
            totalWeight += weight;
            
            if (constant) {
            	if (lastWeight < 0) {
            		lastWeight = weight;
            	}
            	else {
            		if (lastWeight != weight) {
            			constant = false;
            		}
            	}
            }
        }
        
        if (totalWeight == 0) {
        	for (int i = 0; i < trials; i++) {
        		sampleWeights[i] = 1;
        		totalWeight += 1;
        	}
        }
        
        double targWeight = RandomGenerator.random.nextDouble() * totalWeight;
        int i = 0;
        totalWeight = sampleWeights[i];
        while (targWeight > totalWeight) {
        	i++;
        	totalWeight += sampleWeights[i];
        }
        bestOrigIndex = sampleOrigs[i];
        bestTargIndex = sampleTargs[i];
        
        Node origNode = net.getNodes()[bestOrigIndex];
        Node targNode = net.getNodes()[bestTargIndex];

        return new Edge(origNode, targNode);
    }
	
	
	private double weightsDist(Generator gen) {
		double totalWeight1 = 0;
		double totalWeight2 = 0;
		
		for (int i = 0; i < trials; i++) {
    		totalWeight1 += sampleWeights[i];
    		totalWeight2 += gen.sampleWeights[i];
    	}
		
		double dist = 0;
		
		for (int i = 0; i < trials; i++) {
    		sampleWeights[i] /= totalWeight1;
    		gen.sampleWeights[i] /= totalWeight2;
    		
    		dist += Math.abs(sampleWeights[i] - gen.sampleWeights[i]);
    	}
		
		dist /= trials;
		
		return dist;
	}
	
	
	public double run() {
		return run(null);
	}
	
	
	public double run(Generator shadow) {
		lastWeight = -1;
		constant = true;
		
		double dist = 0;
		
		net = new Net(netParams.getNodeCount(), netParams.getEdgeCount(), netParams.getDirected(), false, netParams.getParallels());
		
		if (shadow != null) {
			shadow.net = net;
		}
        
        createNodes();
        
        // init DistMatrix
        net.dRandomWalkers = null;
        if (netParams.getDirected()) {
        	net.dRandomWalkers = new RandomWalkers(net, true);
        }
        net.uRandomWalkers = new RandomWalkers(net, false);

        // create edges
        time = 0;
        while (time < netParams.getEdgeCount()) {
        	Edge newEdge = cycle(null);
        	
        	if (shadow != null) {
        		shadow.cycle(this);
        		dist += weightsDist(shadow);
        	}
        	
        	Node orig = newEdge.getOrigin();
        	Node targ = newEdge.getTarget();
        	
        	net.addEdge(orig, targ);
            
        	// update distances
        	if (netParams.getDirected()) {
        		net.dRandomWalkers.step();
        	}
        	net.uRandomWalkers.step();
        	
        	time++;
        }
        
        dist /= netParams.getEdgeCount();
        return dist;
    }


	public void initRandom() {
		prog.initRandom();
	}


	private Generator recombine(Generator parent2) {
		Generator generator = instance();
		generator.prog = prog.recombine(parent2.prog);
		return generator;
	}
	
	
	public Generator mutate() {
		Generator random = instance();
		random.initRandom();
		return recombine(random);
	}

	
	public void computeFitness(MetricsBag targBag, int bins, boolean useRandom) {
		if (net.directed) {
			computeFitnessDirected(targBag, bins, useRandom);
		}
		else {
			computeFitnessUndirected(targBag, bins, useRandom);
		}
	}


	public void computeFitness(MetricsBag targBag, int bins) {
		computeFitness(targBag, bins, true);
	}
	
	
	private void computeFitnessDirected(MetricsBag targBag, int bins, boolean useRandom) {
		net.dRandomWalkers.recompute();
		net.uRandomWalkers.recompute();
        genBag = new MetricsBag(net, net.dRandomWalkers, net.uRandomWalkers, bins, targBag, useRandom);

        net.metricsBag = genBag;

		double inDegreesDist;
		double outDegreesDist;
		double dPageRanksDist;
		double uPageRanksDist;
		double triadicProfileDist;
		double dDistsDist;
		double uDistsDist;

		if (useRandom) {
            inDegreesDist = genBag.getRelInDegreesDist();
            outDegreesDist = genBag.getRelOutDegreesDist();
            dPageRanksDist = genBag.getRelDPageRanksDist();
            uPageRanksDist = genBag.getRelUPageRanksDist();
            triadicProfileDist = genBag.getRelTriadicProfileDist();
            dDistsDist = genBag.getRelDDistsDist();
            uDistsDist = genBag.getRelUDistsDist();
        }
        else {
            inDegreesDist = genBag.getInDegreesDist();
            outDegreesDist = genBag.getOutDegreesDist();
            dPageRanksDist = genBag.getDPageRanksDist();
            uPageRanksDist = genBag.getUPageRanksDist();
            triadicProfileDist = genBag.getTriadicProfileDist();
            dDistsDist = genBag.getdDistsDist();
            uDistsDist = genBag.getuDistsDist();
        }
        	
        fitnessAvg = inDegreesDist
        			+ outDegreesDist
        			+ dPageRanksDist
        			+ uPageRanksDist
        			+ triadicProfileDist
        			+ dDistsDist
        			+ uDistsDist;
        fitnessAvg /= 7;
        	
        
        fitnessMax = 0;
        fitnessMax = Math.max(fitnessMax, inDegreesDist);
        fitnessMax = Math.max(fitnessMax, outDegreesDist);
        fitnessMax = Math.max(fitnessMax, dPageRanksDist);
        fitnessMax = Math.max(fitnessMax, uPageRanksDist);
        fitnessMax = Math.max(fitnessMax, triadicProfileDist);
        fitnessMax = Math.max(fitnessMax, dDistsDist);
        fitnessMax = Math.max(fitnessMax, uDistsDist);
    }
	
	
	private void computeFitnessUndirected(MetricsBag targBag, int bins, boolean useRandom) {
		net.uRandomWalkers.recompute();
        genBag = new MetricsBag(net, null, net.uRandomWalkers, bins, targBag, useRandom);

        net.metricsBag = genBag;
        
        double degreesDist;
        double uPageRanksDist;
        double triadicProfileDist;
        double uDistsDist;

        if (useRandom) {
            degreesDist = genBag.getRelDegreesDist();
            uPageRanksDist = genBag.getRelUPageRanksDist();
            triadicProfileDist = genBag.getRelTriadicProfileDist();
            uDistsDist = genBag.getRelUDistsDist();
        }
        else {
            degreesDist = genBag.getDegreesDist();
            uPageRanksDist = genBag.getUPageRanksDist();
            triadicProfileDist = genBag.getTriadicProfileDist();
            uDistsDist = genBag.getuDistsDist();
        }

        fitnessAvg = degreesDist
        			+ uPageRanksDist
        			+ triadicProfileDist
        			+ uDistsDist;
        fitnessAvg /= 4;

        fitnessMax = 0;
        fitnessMax = Math.max(fitnessMax, degreesDist);
        fitnessMax = Math.max(fitnessMax, uPageRanksDist);
        fitnessMax = Math.max(fitnessMax, triadicProfileDist);
        fitnessMax = Math.max(fitnessMax, uDistsDist);
    }
	
	
	private boolean withinTolerance(double bestFitnessMax, double tolerance) {
		return Math.abs(fitnessMax - bestFitnessMax) < tolerance;
	}
	
	
	public boolean isBetterThan(Generator gen, double bestFitnessMax, double tolerance) {
		if (!gen.valid) {
			return true;
		}
		
		if (!valid) {
			return false;
		}
		
		if (tolerance <= 0) {
			return fitnessMax < gen.fitnessMax;
		}
		
		return withinTolerance(bestFitnessMax, tolerance) &&
                (!gen.withinTolerance(bestFitnessMax, tolerance)) || prog.size() < gen.prog.size();
	}

    
    public Prog getProg() {
        return prog;
    }
 
    
    public Net getNet() {
        return net;
    }


	public MetricsBag getGenBag() {
		return genBag;
	}


	public boolean isConstant() {
		return constant;
	}
	
	
	@Override
	public String toString() {
		return "trials: " + trials; 
	}
}