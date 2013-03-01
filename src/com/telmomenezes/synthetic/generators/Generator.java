package com.telmomenezes.synthetic.generators;


import java.io.IOException;
import java.util.Vector;

import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.gp.Prog;


/**
 * Network generator.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public abstract class Generator implements Comparable<Generator> {
    protected int nodeCount;
    protected int edgeCount;
    protected boolean directed;
    
	protected Prog prog;
    public boolean simulated;

    public double fitness;

    private Vector<Prog> executionPaths;
    protected boolean checkPaths;
    
    protected Net net;
    
    private MetricsBag metricsBag;
    
    
	public Generator(int nodeCount, int edgeCount, boolean directed) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;
	    this.directed = directed;
	    
		simulated = false;
		
		fitness = 0.0;

		checkPaths = false;
		
		metricsBag = null;
		
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
	
	
	public abstract Generator instance();
	
	
	public abstract Generator clone();
	
    
	public abstract void run();


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