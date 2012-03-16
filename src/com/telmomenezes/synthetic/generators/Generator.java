package com.telmomenezes.synthetic.generators;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.gp.ProgSet;


/**
 * Abstract base class for a network generator.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public abstract class Generator implements Comparable<Generator> {
    protected int nodeCount;
    protected int edgeCount;
    
    protected int cycle;
    protected int curEdges;
    
	protected ProgSet progset;
    protected int progcount;
    public boolean simulated;

    public double fitness;
    public double postFitness;

    private Vector<ProgSet> executionPaths;
    protected boolean checkPaths;


	public Generator(int nodeCount, int edgeCount) {
	    this.nodeCount = nodeCount;
	    this.edgeCount = edgeCount;
	    
	    cycle = 0;
	    
		progset = null;

		simulated = false;
		
		// init fitness
		fitness = 0.0;
		postFitness = 0.0;

		// misc
		checkPaths = false;
	}

	
	/**
     * Creates a clone of this generator.
     * 
     * @return clone generator object
     */
	public abstract Generator clone();
	
	
	/**
     * Creates the program set.
     */
    public abstract void createProgSet();
    
    
    /**
     * Computes the fitness of this generator, after it has been run.
     * 
     * @return the fitness as a double value
     */
    public abstract double computeFitness();
	
    
    /**
     * Runs a simulation based on this generator.
     */
    public abstract Net run();

   
    /**
     * Compute a distance between two generators.
     * 
     * @param generator the generator against which the distance should be computed
     * @return the distance as a double value
     */
    public abstract double distance(Generator generator);
    
    
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
		createProgSet();
		progset.init();
	}


	/**
     * Randomly initializes the program set.
     */
	public void initProgsRandom()
	{
		createProgSet();
		progset.initRandom();
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
		generator.progset = progset.recombine(parent2.progset);
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
		generator.progset = progset.clone(true);
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
		return progset.size();
	}


	/**
     * Writes the program set to a text file.
     * 
     * @param filePath path to file where programs are to be written
     * @throws IOException
     */
	public void writeProgs(String filePath) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filePath);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8"); 
		progset.write(out);
		out.close();
		fos.close();
	}


	public void loadProgs(String filePath) throws IOException
	{
		createProgSet();
		progset.load(filePath);
	}


	public void dynPruning()
    {
		progset.dynPruning();
    }
	
	
	/**
     * Reset branching information on the program set.
     */
	public void progsClearBranching()
	{
		progset.clearBranching();
	}


	/**
     * Compare branching information between this and the parameter generator.
     * 
     * @param generator generator to compare branching information against
     * @return true if branching is equal, false otherwise
     */
	public boolean progsCompareBranching(Generator generator)
	{
		return progset.compareBranching(generator.progset);
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
	public int executionPath(ProgSet ps)
	{
		int pos = 0;
		for (ProgSet path : executionPaths) {
			if (ps.compareBranching(path))
				return pos;

			pos++;
		}

		executionPaths.add(ps.clone(true));
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


	public double getPostFitness() {
		return postFitness;
	}


	public double getFitness() {
		return fitness;
	}


	public void setCheckPaths(boolean checkPaths) {
		this.checkPaths = checkPaths;
		if (checkPaths)
			executionPaths = new Vector<ProgSet>();
	}


	public Vector<ProgSet> getExecutionPaths() {
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
}