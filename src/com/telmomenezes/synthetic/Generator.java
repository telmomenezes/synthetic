package com.telmomenezes.synthetic;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.gp.GPTree;


/**
 * Abstract base class for a network generator.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Generator implements Comparable<Generator> {
    protected int nodeCount;
    protected int edgeCount;
    
    protected int cycle;
    protected int curEdges;
    
	protected GPTree prog;
    public boolean simulated;

    public double fitness;

    private Vector<GPTree> executionPaths;
    protected boolean checkPaths;
    
    protected Net net;
    
    private Map<String, Double> metricTable;
    
    private int samples;
    
	public Generator(int nodeCount, int edgeCount) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;
	    
	    cycle = 0;
	    
		prog = null;

		simulated = false;
		
		// init fitness
		fitness = 0.0;

		// misc
		checkPaths = false;
		
		metricTable = new HashMap<String, Double>();
		
		samples = nodeCount;
	}

	
	public Generator clone() {
        return new Generator(nodeCount, edgeCount);
    }
	
	
	public void createProg() {
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
        
        prog = new GPTree(9, variableNames);
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

   
	public double distance(Generator generator) {
        return 0;
    }
    
    
    /**
     * Copies the generator in the parameter to this generator.
     * 
     * @param generator to copy from
     */
    public void copy(Generator generator) {}

    
	/**
     * Creates a String with information about the parameterization of this
     * generator.
     * 
     * @return string with parameters information
     */
    public String paramsString() {return "";}
    
    
    /**
     * Initializes the program set.
     */
	public void initProgs()
	{
		createProg();
	}


	/**
     * Randomly initializes the program set.
     */
	public void initProgsRandom()
	{
		createProg();
		prog.initRandom();
	}


	/**
     * Recombines the programs in this generator with the ones in the parameter
     * generator, returning a new child generator.
     * 
     * @param parent2 the other parent generator
     * @return new child generator
     */
	public Generator recombine(Generator parent2)
	{
		Generator generator = null;
		generator = (Generator)clone();
		generator.prog = prog.recombine(parent2.prog);
		return generator;
	}


	/**
     * Creates a new generator with a program set copied from this one.
     * 
     * @return new generator with cloned program set
     */
	public Generator cloneProgs()
	{
		Generator generator = null;
		generator = (Generator)clone();
		generator.prog = prog.clone();
		return generator;
	}


	/**
     * A measure of the genotype size for this generator.
     * 
     * Computes the total number of nodes in all the programs in the program set
     * of this generator.
     * 
     * @return total number of nodes in program set
     */
	public int genotypeSize()
	{
		return prog.size();
	}


	/**
     * Writes the program set to a text file.
     * 
     * @param filePath path to file where programs are to be written
     * @throws IOException
     */
	public void writeProgs(String filePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8"); 
		prog.write(out, false);
		out.close();
		fos.close();
	}

	public void printProgs(boolean evalStats) { 
        prog.print(evalStats);
    }

	public void loadProgs(String filePath) throws IOException
	{
		createProg();
		prog.load(filePath);
	}


	public void dynPruning()
    {
		prog.dynPruning();
    }
	
	
	/**
     * Reset branching information on the program set.
     */
	public void progsClearBranching()
	{
		prog.clearBranching();
	}


	/**
     * Compare branching information between this and the parameter generator.
     * 
     * @param generator generator to compare branching information against
     * @return true if branching is equal, false otherwise
     */
	public boolean progsCompareBranching(Generator generator)
	{
		return prog.compareBranching(generator.prog);
	}


	/**
     * Feed a program set to the generator, for the purpose of maintaining a record
     * of execution paths.
     * 
     * Checks if the program set passed in the parameter contains a new
     * branching configuration. If so, adds it to the set of recorded execution
     * paths. In any case, returns the index of the execution path in the
     * program set.
     * 
     * @param ps Program set representing an execution path
     * @return index of execution path in the generator record
     */
	public int executionPath(GPTree tree)
	{
		int pos = 0;
		for (GPTree path : executionPaths) {
			if (tree.compareBranching(path))
				return pos;

			pos++;
		}

		executionPaths.add(tree.clone());
		return pos;
	}


	/**
     * Clears record of execution paths.
     */
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
			executionPaths = new Vector<GPTree>();
	}


	public Vector<GPTree> getExecutionPaths() {
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

    public GPTree getProg() {
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