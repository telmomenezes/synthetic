package com.telmomenezes.synthetic;

import java.io.IOException;
import java.util.Vector;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.random.RandomGenerator;
import com.telmomenezes.synthetic.randomwalkers.RandomWalkers;
import com.telmomenezes.synthetic.gp.Prog;


public class Generator { 
	
    private int nodeCount;
    private int edgeCount;
    private boolean directed;
    private boolean parallels;
    private double sr;
    
    private int trials;
    
	private Prog prog;

    public double fitnessAvg;
    public double fitnessMax;

    private Vector<Prog> executionPaths;
    //private boolean checkPaths;
    
    private Net net;
    
    private int time;
    
    public double[] labels;
    
    private int[] sampleOrigs;
    private int[] sampleTargs;
    private double[] sampleWeights;
    
    private MetricsBag genBag;
    
    private boolean valid;
    
    
	public Generator(int nodeCount, int edgeCount, boolean directed, boolean parallels, double sr) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;
	    this.directed = directed;
	    this.parallels = parallels;
	    this.sr = sr;
	    
	    this.trials = (int)(sr * (nodeCount * nodeCount));
	    
	    sampleOrigs = new int[trials];
	    sampleTargs = new int[trials];
	    sampleWeights = new double[trials];
	    
	    valid = true;
		
		fitnessAvg = 0;
		fitnessMax = 0;
		
		Vector<String> variableNames = new Vector<String>();
		
		if (directed) {
			variableNames.add("ovar");
			variableNames.add("tvar");
			variableNames.add("origInDeg");
			variableNames.add("origOutDeg");
			variableNames.add("targInDeg");
			variableNames.add("targOutDeg");
			variableNames.add("dist");
			variableNames.add("dirDist");
			variableNames.add("revDist");
        
			prog = new Prog(9, variableNames);
		}
		else {
			variableNames.add("ovar");
			variableNames.add("tvar");
			variableNames.add("origDeg");
			variableNames.add("targDeg");
			variableNames.add("dist");
        
			prog = new Prog(5, variableNames);
		}
	}
	
	
	public void load(String filePath) {
		try {
			prog.load(filePath);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Generator instance() {
		return new Generator(nodeCount, edgeCount, directed, parallels, sr);
	}
	
	
	public Generator clone() {
		Generator generator = new Generator(nodeCount, edgeCount, directed, parallels, sr);
		generator.prog = prog.clone();
		return generator;
	}
	
	
	private int getRandomNode() {
		return RandomGenerator.random.nextInt(nodeCount);
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
            		if (parallels || (!net.edgeExists(origIndex, targIndex))) {
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
        	int origIndex = sampleOrigs[i];
            int targIndex = sampleTargs[i];
            
            Node origNode = net.getNodes()[origIndex];
    		Node targNode = net.getNodes()[targIndex];    
    		
            double distance = net.uRandomWalkers.getDist(origNode.getId(), targNode.getId());
            
			prog.vars[0] = labels[origIndex];
			prog.vars[1] = labels[targIndex];
			
            if (directed) {
            	double directDistance = net.dRandomWalkers.getDist(origNode.getId(), targNode.getId());
            	double reverseDistance = net.dRandomWalkers.getDist(targNode.getId(), origNode.getId());
                    
            	prog.vars[2] = (double)origNode.getInDegree();
            	prog.vars[3] = (double)origNode.getOutDegree();
            	prog.vars[4] = (double)targNode.getInDegree();
            	prog.vars[5] = (double)targNode.getOutDegree();
            	prog.vars[6] = distance;
            	prog.vars[7] = directDistance;
            	prog.vars[8] = reverseDistance;
            }
            else {
            	prog.vars[2] = (double)origNode.getDegree();
            	prog.vars[3] = (double)targNode.getDegree();
            	prog.vars[4] = distance;
            }
                    
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
		
		net = new Net(nodeCount, edgeCount, directed, false, parallels);
		labels = new double[nodeCount];
		
		if (shadow != null) {
			shadow.net = net;
			shadow.labels = labels;
		}
		
        // create nodes
        for (int i = 0; i < nodeCount; i++) {
            net.addNode();
            labels[i] = RandomGenerator.random.nextDouble();
        }
        
        // init DistMatrix
        net.dRandomWalkers = null;
        if (directed) {
        	net.dRandomWalkers = new RandomWalkers(net, true);
        }
        net.uRandomWalkers = new RandomWalkers(net, false);

        // create edges
        time = 0;
        while (time < edgeCount) {
        	Edge newEdge = cycle(null);
        	
        	if (shadow != null) {
        		shadow.cycle(this);
        		dist += weightsDist(shadow);
        	}
        	
        	Node orig = newEdge.getOrigin();
        	Node targ = newEdge.getTarget();
        	
        	net.addEdge(orig, targ);
            
        	// update distances
        	if (directed) {
        		net.dRandomWalkers.step();
        	}
        	net.uRandomWalkers.step();
        	
        	time++;
        }
        
        dist /= edgeCount;
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


    public int getNodeCount() {
        return nodeCount;
    }


    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }


    public int getEdgeCount() {
        return edgeCount;
    }


    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
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
		return "Generator-> trials: " + trials; 
	}
}