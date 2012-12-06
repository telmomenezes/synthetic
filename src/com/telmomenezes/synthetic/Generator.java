package com.telmomenezes.synthetic;


import java.util.Vector;

import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.gp.Prog;


/**
 * Network generator.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Generator implements Comparable<Generator> {
    protected int nodeCount;
    protected int edgeCount;
    
	protected Prog prog;
    public boolean simulated;

    public double fitness;

    private Vector<Prog> executionPaths;
    protected boolean checkPaths;
    
    protected Net net;
    
    private int trials;
    
    private MetricsBag metricsBag;
    
    
	public Generator(int nodeCount, int edgeCount) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;

	    double trialRatio = 0.001;
	    trials = (int)((nodeCount * nodeCount) * trialRatio * trialRatio);
	    
		simulated = false;
		
		fitness = 0.0;

		checkPaths = false;
		
		metricsBag = null;
		
		Vector<String> variableNames = new Vector<String>();
        variableNames.add("origId");
        variableNames.add("targId");
        variableNames.add("origInDeg");
        variableNames.add("origOutDeg");
        variableNames.add("targInDeg");
        variableNames.add("targOutDeg");
        variableNames.add("dirDist");
        variableNames.add("revDist");
        
        prog = new Prog(8, variableNames);
	}
	
	
	public Generator clone() {
		Generator generator = new Generator(nodeCount, edgeCount);
		generator.prog = prog.clone();
		return generator;
	}
	
    
	public void run() {
		//System.out.println("running generator; trials: " + trials);
		
        // reset eval stats
        prog.clearEvalStats();
        
        // init DistMatrix
        DistMatrix distMatrix = new DistMatrix(nodeCount);

        net = new Net();

        // create nodes
        Node[] nodeArray = new Node[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            nodeArray[i] = net.addNode();
        }

        // create edges
        for (int i = 0; i < edgeCount; i++) {
        	//System.out.println("edge #" + i);
            double bestWeight = -1;
            int bestOrigIndex = -1;
            int bestTargIndex = -1;
            for (int j = 0; j < trials; j++) {
            	int origIndex = -1;
            	int targIndex = -1;
            	
            	while ((origIndex == targIndex)
            			|| (distMatrix.getDist(origIndex, targIndex) < 2)) {
            		origIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            		targIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            	}
            		
            	Node origNode = nodeArray[origIndex];
            	Node targNode = nodeArray[targIndex];
        
            	double directDistance = distMatrix.getDist(origNode.getId(), targNode.getId());
            	double reverseDistance = distMatrix.getDist(targNode.getId(), origNode.getId());
                    
            	prog.vars[0] = (double)origIndex;
            	prog.vars[1] = (double)targIndex;
            	prog.vars[2] = (double)origNode.getInDegree();
            	prog.vars[3] = (double)origNode.getOutDegree();
            	prog.vars[4] = (double)targNode.getInDegree();
            	prog.vars[5] = (double)targNode.getOutDegree();
            	prog.vars[6] = directDistance;
            	prog.vars[7] = reverseDistance;
                    
            	double weight = prog.eval(i);
            	if (weight < 0) {
            		weight = 0;
            	}
        
            	//System.out.println("weight: " + weight + "; bestWeight: " + bestWeight);
            	if (weight > bestWeight) {
            		//System.out.println("* best weight");
            		bestWeight = weight;
            		bestOrigIndex = origIndex;
            		bestTargIndex = targIndex;
            	}
            }

            Node origNode = nodeArray[bestOrigIndex];
            Node targNode = nodeArray[bestTargIndex];

            net.addEdge(origNode, targNode, i);
            
            // update distances
            distMatrix.updateDistances(net, bestOrigIndex, bestTargIndex);
            
            simulated = true;
        }
    }


	public void initRandom() {
		prog.initRandom();
	}


	public Generator recombine(Generator parent2) {
		Generator generator = new Generator(nodeCount, edgeCount);
		generator.prog = prog.recombine(parent2.prog);
		return generator;
	}
	
	
	public Generator mutate() {
		Generator random = new Generator(nodeCount, edgeCount);
		random.initRandom();
		return recombine(random);
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


	public void setCheckPaths(boolean checkPaths) {
		this.checkPaths = checkPaths;
		if (checkPaths)
			executionPaths = new Vector<Prog>();
	}


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


	public MetricsBag getMetricsBag() {
		return metricsBag;
	}


	public void setMetricsBag(MetricsBag metricsBag) {
		this.metricsBag = metricsBag;
	}
}