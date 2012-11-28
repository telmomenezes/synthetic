package com.telmomenezes.synthetic;


import java.util.HashMap;
import java.util.Map;
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
    
    private Map<String, Double> metricTable;
    
    private int samples;
    
	public Generator(int nodeCount, int edgeCount) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;

		simulated = false;
		
		fitness = 0.0;

		checkPaths = false;
		
		metricTable = new HashMap<String, Double>();
		
		samples = nodeCount;
		
		Vector<String> variableNames = new Vector<String>();
        variableNames.add("origId");
        variableNames.add("targId");
        variableNames.add("origInDeg");
        variableNames.add("origOutDeg");
        variableNames.add("targInDeg");
        variableNames.add("targOutDeg");
        variableNames.add("undirDist");
        variableNames.add("dirDist");
        variableNames.add("revDist");
        
        prog = new Prog(9, variableNames);
	}
	
	
	public Generator clone()
	{
		Generator generator = new Generator(nodeCount, edgeCount);
		generator.prog = prog.clone();
		return generator;
	}
	
    
	public void run() {
    	System.out.println("running generator");
        // reset eval stats
        prog.clearEvalStats();
        
        // init DistMatrix
        DistMatrix.instance().setNodes(nodeCount);

        net = new Net();

        // create nodes
        Node[] nodeArray = new Node[nodeCount];
        double[] weightArray = new double[samples];
        int[] origArray = new int[samples];
        int[] targArray = new int[samples];
        for (int i = 0; i < nodeCount; i++) {
            nodeArray[i] = net.addNode();
        }

        // create edges
        for (int i = 0; i < edgeCount; i++) {
        	//System.out.println("creating edge: " + i);
            double totalWeight = 0;
            for (int j = 0; j < samples; j++) {
            	int origIndex = 0;
            	int targIndex = 0;
            	while (origIndex == targIndex) {
            		origIndex = RandomGenerator.instance().random.nextInt(samples);
            		targIndex = RandomGenerator.instance().random.nextInt(samples);
            	}
            	
                Node origNode = nodeArray[origIndex];
                Node targNode = nodeArray[targIndex];
        
                double undirectedDistance = DistMatrix.instance().getUDist(origNode.getId(), targNode.getId());
                double directDistance = DistMatrix.instance().getDDist(origNode.getId(), targNode.getId());
                double reverseDistance = DistMatrix.instance().getDDist(targNode.getId(), origNode.getId());
                    
                prog.vars[0] = (double)origIndex;
                prog.vars[1] = (double)targIndex;
                prog.vars[2] = (double)origNode.getInDegree();
                prog.vars[3] = (double)origNode.getOutDegree();
                prog.vars[4] = (double)targNode.getInDegree();
                prog.vars[5] = (double)targNode.getOutDegree();
                prog.vars[6] = undirectedDistance;
                prog.vars[7] = directDistance;
                prog.vars[8] = reverseDistance;
                    
                double weight = prog.eval(i);
                if (weight < 0) {
                    weight = 0;
                }
        
                weightArray[j] = weight;
                origArray[j] = origIndex;
                targArray[j] = targIndex;
                totalWeight += weight;
            }

            // if total weight is zero, make every pair's weight = 1
            if (totalWeight == 0) {
                for (int k = 0; k < samples; k++) {
                	weightArray[k] = 1.0;
                    totalWeight += 1.0;
                }
            }

            double weight = RandomGenerator.instance().random.nextDouble() * totalWeight;
            int k = 0;
            totalWeight = weightArray[k];
            while (totalWeight < weight) {
                k++;
                totalWeight += weightArray[k];
            }

            Node origNode = nodeArray[origArray[k]];
            Node targNode = nodeArray[targArray[k]];

            net.addEdge(origNode, targNode, i);
            
            // update distances
            DistMatrix.instance().updateDistancesSmart(net, origArray[k], targArray[k]);
            
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


	public void clearExecutionPaths()
	{
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
    
    public void setMetric(String key, double value) {
        metricTable.put(key, value);
    }
    
    public double getMetric(String key) {
        return metricTable.get(key);
    }
}