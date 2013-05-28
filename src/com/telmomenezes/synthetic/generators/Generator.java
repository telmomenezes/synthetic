package com.telmomenezes.synthetic.generators;

import java.io.IOException;
import java.util.Vector;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomBag;
import com.telmomenezes.synthetic.random.RandomGenerator;
import com.telmomenezes.synthetic.randomwalkers.RandomWalkers;
import com.telmomenezes.synthetic.gp.Prog;


public abstract class Generator { 
	
    protected double sr;
    
    protected int trials;
    
    protected Prog prog;

    public double fitnessAvg;
    public double fitnessMax;

    protected Vector<Prog> executionPaths;
    //private boolean checkPaths;
    
    protected Net refNet;
    protected Net net;
    
    protected int time;
    
    public double[] labels;
    
    protected int[] sampleOrigs;
    protected int[] sampleTargs;
    protected double[] sampleWeights;
    
    protected MetricsBag genBag;
    
    protected boolean valid;
    
    
    public abstract Generator instance();
	public abstract Generator clone();
	protected abstract void setProgVars(int origIndex, int targIndex);
	protected abstract void createNodes();
	
    
	public Generator(Net refNet, double sr) {
	    this.refNet = refNet;
	    this.sr = sr;
	    
	    this.trials = (int)(sr * (refNet.nodeCount * refNet.nodeCount));
	    
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
		return RandomGenerator.random.nextInt(refNet.nodeCount);
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
            		if (refNet.parallels || (!net.edgeExists(origIndex, targIndex))) {
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
			
        int bestOrigIndex = -1;
        int bestTargIndex = -1;
        
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
		double dist = 0;
		
		net = new Net(refNet.nodeCount, refNet.edgeCount, refNet.directed, false, refNet.parallels);
		labels = new double[refNet.nodeCount];
		
		if (shadow != null) {
			shadow.net = net;
			shadow.labels = labels;
		}
        
        createNodes();
        
        // init DistMatrix
        net.dRandomWalkers = null;
        if (refNet.directed) {
        	net.dRandomWalkers = new RandomWalkers(net, true);
        }
        net.uRandomWalkers = new RandomWalkers(net, false);

        // create edges
        time = 0;
        while (time < refNet.edgeCount) {
        	Edge newEdge = cycle(null);
        	
        	if (shadow != null) {
        		shadow.cycle(this);
        		dist += weightsDist(shadow);
        	}
        	
        	Node orig = newEdge.getOrigin();
        	Node targ = newEdge.getTarget();
        	
        	net.addEdge(orig, targ);
            
        	// update distances
        	if (refNet.directed) {
        		net.dRandomWalkers.step();
        	}
        	net.uRandomWalkers.step();
        	
        	time++;
        }
        
        dist /= refNet.edgeCount;
        return dist;
    }


	public void initRandom() {
		prog.initRandom();
	}


	public Generator recombine(Generator parent2) {
		Generator generator = instance();
		generator.prog = prog.recombine(parent2.prog);
		return generator;
	}
	
	
	public Generator mutate() {
		Generator random = instance();
		random.initRandom();
		return recombine(random);
	}

	
	public void computeFitness(MetricsBag targBag, int bins) {
		if (net.directed) {
			computeFitnessDirected(targBag, bins);
		}
		else {
			computeFitnessUndirected(targBag, bins);
		}
	}
	
	
	private void computeFitnessDirected(MetricsBag targBag, int bins) {
		net.dRandomWalkers.recompute();
		net.uRandomWalkers.recompute();
        genBag = new MetricsBag(net, net.dRandomWalkers, net.uRandomWalkers, bins, targBag);

        net.metricsBag = genBag;
        
        double inDegreesDist = genBag.getInDegreesDist();
        double outDegreesDist = genBag.getOutDegreesDist();
        double dPageRanksDist = genBag.getDPageRanksDist();
        double uPageRanksDist = genBag.getUPageRanksDist();
        double triadicProfileDist = genBag.getTriadicProfileDist();
        double dDistsDist = genBag.getdDistsDist();
        double uDistsDist = genBag.getuDistsDist();
        
        // random baseline
        RandomBag randomBag = targBag.getRandomBag();
        double inDegreesDistR = randomBag.inDegreesDistAvg;
        double outDegreesDistR = randomBag.outDegreesDistAvg;
        double dPageRanksDistR = randomBag.dPageRanksDistAvg;
        double uPageRanksDistR = randomBag.uPageRanksDistAvg;
        double triadicProfileDistR = randomBag.triadicProfileDistAvg;
        double dDistsDistR = randomBag.dDistsDistAvg;
        double uDistsDistR = randomBag.uDistsDistAvg;
        	
        fitnessAvg = (inDegreesDist / inDegreesDistR)
        			+ (outDegreesDist / outDegreesDistR)
        			+ (dPageRanksDist / dPageRanksDistR)
        			+ (uPageRanksDist / uPageRanksDistR) 
        			+ (triadicProfileDist / triadicProfileDistR) 
        			+ (dDistsDist / dDistsDistR)
        			+ (uDistsDist / uDistsDistR);
        fitnessAvg /= 7;
        	
        
        fitnessMax = 0;
        fitnessMax = Math.max(fitnessMax, inDegreesDist / inDegreesDistR);
        fitnessMax = Math.max(fitnessMax, outDegreesDist / outDegreesDistR);
        fitnessMax = Math.max(fitnessMax, dPageRanksDist / dPageRanksDistR);
        fitnessMax = Math.max(fitnessMax, uPageRanksDist / uPageRanksDistR);
        fitnessMax = Math.max(fitnessMax, triadicProfileDist / triadicProfileDistR);
        fitnessMax = Math.max(fitnessMax, dDistsDist / dDistsDistR);
        fitnessMax = Math.max(fitnessMax, uDistsDist / uDistsDistR);
    }
	
	
	private void computeFitnessUndirected(MetricsBag targBag, int bins) {
		net.uRandomWalkers.recompute();
        MetricsBag genBag = new MetricsBag(net, null, net.uRandomWalkers, bins, targBag);

        net.metricsBag = genBag;
        
        double degreesDist = genBag.getDegreesDist();
        double uPageRanksDist = genBag.getUPageRanksDist();
        double triadicProfileDist = genBag.getTriadicProfileDist();
        double uDistsDist = genBag.getuDistsDist();
        
        // random baseline
        RandomBag randomBag = targBag.getRandomBag();
        double degreesDistR = randomBag.degreesDistAvg;
        double uPageRanksDistR = randomBag.uPageRanksDistAvg;
        double triadicProfileDistR = randomBag.triadicProfileDistAvg;
        double uDistsDistR = randomBag.uDistsDistAvg;
        
        fitnessAvg = (degreesDist / degreesDistR) 
        			+ (uPageRanksDist / uPageRanksDistR) 
        			+ (triadicProfileDist / triadicProfileDistR) 
        			+ (uDistsDist / uDistsDistR);
        fitnessAvg /= 4;

        fitnessMax = 0;
        fitnessMax = Math.max(fitnessMax, degreesDist / degreesDistR);
        fitnessMax = Math.max(fitnessMax, uPageRanksDist / uPageRanksDistR);
        fitnessMax = Math.max(fitnessMax, triadicProfileDist / triadicProfileDistR);
        fitnessMax = Math.max(fitnessMax, uDistsDist / uDistsDistR);
    }
	
	
	private boolean withinTolerance(double bestFitnessMax, double tolerance) {
		return Math.abs(fitnessMax - bestFitnessMax) < tolerance;
	}
	
	
	public boolean isBetterThan(Generator gen, double bestFitnessMax, double bestFitnessAvg, double tolerance) {
		if (!gen.valid) {
			return true;
		}
		
		if ((!valid) && (gen.valid)) {
			return false;
		}
		
		if (tolerance <= 0) {
			return fitnessMax < gen.fitnessMax;
		}
		
		if (!withinTolerance(bestFitnessMax, tolerance)) {
			return false;
		}
		
		if (!gen.withinTolerance(bestFitnessMax, tolerance)) {
			return true;
		}
		
		return prog.size() < gen.prog.size();
	}
	

	public int executionPath(Prog tree) {
		int pos = 0;
		for (Prog path : executionPaths) {
			if (tree.compareBranching(path))
				return pos;

			pos++;
		}

		executionPaths.add(tree.clone());
		return pos;
	}


	public void clearExecutionPaths() {
		executionPaths.clear();
	}


	/*
	public void setCheckPaths(boolean checkPaths) {
		this.checkPaths = checkPaths;
		if (checkPaths)
			executionPaths = new Vector<Prog>();
	}*/


	public Vector<Prog> getExecutionPaths() {
		return executionPaths;
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


	@Override
	public String toString() {
		return "trials: " + trials; 
	}
}