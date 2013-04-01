package com.telmomenezes.synthetic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.random.RandomGenerator;
import com.telmomenezes.synthetic.gp.Prog;


public class Generator implements Comparable<Generator> {
	private static double MAXSCALE = 10; 
	
    private int nodeCount;
    private int edgeCount;
    private boolean directed;
    private int trials;
    
	private Prog prog;
    public boolean simulated;

    public double fitness;
    public double fitnessAvg;
    public double fitnessMax;

    private Vector<Prog> executionPaths;
    //private boolean checkPaths;
    
    private Net net;
    
    private int time;
    
    private int[] sampleOrigs;
    private int[] sampleTargs;
    
    private MetricsBag genBag;
    
    
	public Generator(int nodeCount, int edgeCount, boolean directed, int trials) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;
	    this.directed = directed;
	    this.trials = trials;
	    
	    sampleOrigs = new int[trials];
	    sampleTargs = new int[trials];
	    
		simulated = false;
		
		fitness = 0;
		fitnessAvg = 0;
		fitnessMax = 0;
		
		Vector<String> variableNames = new Vector<String>();
		
		if (directed) {
			variableNames.add("origId");
			variableNames.add("targId");
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
			variableNames.add("origId");
			variableNames.add("targId");
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
		return new Generator(nodeCount, edgeCount, directed, trials);
	}
	
	
	public Generator clone() {
		Generator generator = new Generator(nodeCount, edgeCount, directed, trials);
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
            		if (!net.edgeExists(origIndex, targIndex)) {
            			found = true;
            		}
            	}
            }
            
            sampleOrigs[i] = origIndex;
            sampleTargs[i] = targIndex;
		}
	}
	
	
	private void copySamplesTo(Generator gen) {
		for (int i = 0; i < trials; i++) {
			gen.sampleOrigs[i] = sampleOrigs[i];
            gen.sampleTargs[i] = sampleTargs[i];
		}
	}
	
    
	private Edge cycle() {
		return cycle(null, null, true);
	}
	
	
	private Edge cycle(Set<Edge> topSet, double[] weights, boolean newSample) {
		
		if (newSample) {
			generateSample();
		}
		
		if (topSet != null) {
        	topSet.clear();
		}
		
		double bestWeight = -1;
        int bestOrigIndex = -1;
        int bestTargIndex = -1;
        
        for (int i = 0; i < trials; i++) {
        	int origIndex = sampleOrigs[i];
            int targIndex = sampleTargs[i];
            
            Node origNode = net.getNodes()[origIndex];
    		Node targNode = net.getNodes()[targIndex];    
        
            double distance = net.uDistMatrix.getDist(origNode.getId(), targNode.getId());
            	
            if (directed) {
            	double directDistance = net.dDistMatrix.getDist(origNode.getId(), targNode.getId());
            	double reverseDistance = net.dDistMatrix.getDist(targNode.getId(), origNode.getId());
                    
            	prog.vars[0] = (double)origIndex;
            	prog.vars[1] = (double)targIndex;
            	prog.vars[2] = (double)origNode.getInDegree();
            	prog.vars[3] = (double)origNode.getOutDegree();
            	prog.vars[4] = (double)targNode.getInDegree();
            	prog.vars[5] = (double)targNode.getOutDegree();
            	prog.vars[6] = distance;
            	prog.vars[7] = directDistance;
            	prog.vars[8] = reverseDistance;
            }
            else {
            	prog.vars[0] = (double)origIndex;
            	prog.vars[1] = (double)targIndex;
            	prog.vars[2] = (double)origNode.getDegree();
            	prog.vars[3] = (double)targNode.getDegree();
            	prog.vars[4] = distance;
            }
                    
            double weight = prog.eval(time);
            if (weight < 0) {
            	weight = 0;
            }
            	
            if (Double.isNaN(weight)) {
            	weight = 0;
            }
        
            // update top set
            if (topSet != null) {
            	if (weight > bestWeight) {
            		topSet.clear();
            	}
            	if (weight >= bestWeight) {
            		topSet.add(new Edge(net.getNodes()[origIndex], net.getNodes()[targIndex]));
            	}
            }
            
            // update weight array
            if (weights != null) {
            	weights[i] = weight;
            }
            
            //System.out.println("weight: " + weight + "; bestWeight: " + bestWeight);
            if (weight > bestWeight) {
            	//System.out.println("* best weight");
            	bestWeight = weight;
            	bestOrigIndex = origIndex;
            	bestTargIndex = targIndex;
            	prog.root.setWinPath();
            }
        }

        // update win evals
        prog.root.updateWinEvals(time);
        
        Node origNode = net.getNodes()[bestOrigIndex];
        Node targNode = net.getNodes()[bestTargIndex];

        return new Edge(origNode, targNode);
    }
	
	
	public void run() {
		net = new Net(nodeCount, edgeCount, directed, false);
		
        // reset eval stats
        prog.clearEvals();
        
        // create nodes
        for (int i = 0; i < nodeCount; i++) {
            net.addNode();
        }
        
        // init DistMatrix
        net.dDistMatrix = null;
        if (directed) {
        	net.dDistMatrix = new DistMatrix(nodeCount, true);
        }
        net.uDistMatrix = new DistMatrix(nodeCount, false);

        // create edges
        time = 0;
        while (time < edgeCount) {
        	Edge newEdge = cycle();
        	
        	Node orig = newEdge.getOrigin();
        	Node targ = newEdge.getTarget();
        	
        	net.addEdge(orig, targ);
            
            // update distances
            if (directed) {
                net.dDistMatrix.updateDistances(net, orig.getId(), targ.getId());
            }
            net.uDistMatrix.updateDistances(net, orig.getId(), targ.getId());
        	
        	time++;
        }
        
        simulated = true;
    }
	
	
	public double runCompare(Generator gen) {
		net = new Net(nodeCount, edgeCount, directed, false);
		gen.net = net;
        
        // create nodes
        for (int i = 0; i < nodeCount; i++) {
            net.addNode();
        }
        
        // init DistMatrix
        net.dDistMatrix = null;
        if (directed) {
        	net.dDistMatrix = new DistMatrix(nodeCount, true);
        }
        net.uDistMatrix = new DistMatrix(nodeCount, false);

        double overlap = 0;
        
        // create edges
        time = 0;
        while (time < edgeCount) {
        	Set<Edge> topSet1 = new HashSet<Edge>();
        	Set<Edge> topSet2 = new HashSet<Edge>();
        	double[] weights1 = new double[trials];
        	double[] weights2 = new double[trials];
        	
        	Edge newEdge1 = cycle(topSet1, weights1, true);
        	copySamplesTo(gen);
        	gen.cycle(topSet2, weights2, false);
        	
        	//System.out.println("" + topSet1.size() + " " + topSet2.size());
        	
        	boolean matches = false;
        	for (Edge e : topSet1) {
        		if (topSet2.contains(e)) {
        			matches = true;
        			break;
        		}
        	}
        	
        	if ((topSet1.size() == 0) && (topSet2.size() == 0)) {
        		matches = true;
        	}
        	
        	//System.out.println(matches);
        	
        	if (matches) {
        		//overlap += 1;
        	}
        	
        	double dist = 0;
        	for (int i = 0; i < trials; i++) {
        		for (int j = 0; j < trials; j++) {
        			double r1 = 0.0;
        			if (weights1[i] == weights1[j]) r1 = 0.5;
        			else if ((weights1[i] > weights1[j])) r1 = 1.0;
        			
        			double r2 = 0.0;
        			if (weights2[i] == weights2[j]) r2 = 0.5;
        			else if ((weights2[i] > weights2[j])) r2 = 1.0;
        			
        			//dist += Math.abs(r1 - r2);
        			if (r1 != r2) {
        				dist += 1;
        			}
        		}
        	}
        	dist /= (trials * trials);
        	overlap += dist;
        	
        	Node orig = newEdge1.getOrigin();
        	Node targ = newEdge1.getTarget();
        	
        	net.addEdge(orig, targ);
            
            // update distances
            if (directed) {
                net.dDistMatrix.updateDistances(net, orig.getId(), targ.getId());
            }
            net.uDistMatrix.updateDistances(net, orig.getId(), targ.getId());
        	
        	time++;
        }
        
        return overlap / edgeCount;
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

	
	public double computeFitness(MetricsBag targBag, int bins) {
		if (net.isDirected()) {
			computeFitnessDirected(targBag, bins);
		}
		else {
			computeFitnessUndirected(targBag, bins);
		}
		
		return fitness;
	}
	
	
	private void computeFitnessDirected(MetricsBag targBag, int bins) {
        genBag = new MetricsBag(net, net.dDistMatrix, net.uDistMatrix, bins, targBag);

        net.metricsBag = genBag;
        
        double inDegreesDist = genBag.getInDegreesDist();
        double outDegreesDist = genBag.getOutDegreesDist();
        double dPageRanksDist = genBag.getDPageRanksDist();
        double uPageRanksDist = genBag.getUPageRanksDist();
        double triadicProfileDist = genBag.getTriadicProfileDist();
        double dDistsDist = genBag.getdDistsDist();
        double uDistsDist = genBag.getuDistsDist();
        
        // random baseline
        MetricsBag randomBag = targBag.getRandomBag();
        double inDegreesDistR = randomBag.getInDegreesDist();
        double outDegreesDistR = randomBag.getOutDegreesDist();
        double dPageRanksDistR = randomBag.getDPageRanksDist();
        double uPageRanksDistR = randomBag.getUPageRanksDist();
        double triadicProfileDistR = randomBag.getTriadicProfileDist();
        double dDistsDistR = randomBag.getdDistsDist();
        double uDistsDistR = randomBag.getuDistsDist();
        	
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
        	
        fitness = fitnessAvg + (fitnessMax * MAXSCALE);
        fitness /= MAXSCALE + 1;
    }
	
	
	private void computeFitnessUndirected(MetricsBag targBag, int bins) {
        MetricsBag genBag = new MetricsBag(net, null, net.uDistMatrix, bins, targBag);

        net.metricsBag = genBag;
        
        double degreesDist = genBag.getDegreesDist();
        double uPageRanksDist = genBag.getUPageRanksDist();
        double triadicProfileDist = genBag.getTriadicProfileDist();
        double uDistsDist = genBag.getuDistsDist();
        
        // random baseline
        MetricsBag randomBag = targBag.getRandomBag();
        double degreesDistR = randomBag.getDegreesDist();
        double uPageRanksDistR = randomBag.getUPageRanksDist();
        double triadicProfileDistR = randomBag.getTriadicProfileDist();
        double uDistsDistR = randomBag.getuDistsDist();
        
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
        	
        fitness = fitnessAvg + (fitnessMax * MAXSCALE);
        fitness /= MAXSCALE + 1;
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
	
	public int compareTo(Generator generator) {
		
        if (fitness < generator.fitness)
        	return -1;
        else if (fitness > generator.fitness)
        	return 1;
        else
        	return 0;
	}


	public boolean isSimulated() {
		return simulated;
	}


	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}


	public double getFitness() {
		return fitness;
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