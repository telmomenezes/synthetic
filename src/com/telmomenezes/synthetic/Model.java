package com.telmomenezes.synthetic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;


/**
 * Abstract base class for a multi-agent model.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public abstract class Model implements Comparable<Model> {

	protected ProgSet progset;
    protected int progcount;
    public boolean simulated;

    public double fitness;
    public double postFitness;

    private Vector<ProgSet> executionPaths;
    protected boolean checkPaths;


	public Model() {
		progset = null;

		simulated = false;
		
		// init fitness
		fitness = 0.0;
		postFitness = 0.0;

		// misc
		checkPaths = false;
	}

	
	/**
     * Creates a clone of this model.
     * 
     * @return clone Model object
     */
	public abstract Model clone();
	
	
	/**
     * Creates the program set.
     */
    public abstract void createProgSet();
    
    
    /**
     * Computes the fitness of this model, after it has been run.
     * 
     * @return the fitness as a double value
     */
    public abstract double computeFitness();
	
    
    /**
     * Runs a simulation based on this model.
     */
    public abstract void run();

   
    /**
     * Compute a distance between two models.
     * 
     * @param model the model against which the distance should be computed
     * @return the distance as a double value
     */
    public abstract double distance(Model model);
    
    
    /**
     * Copies the model in the parameter to this model.
     * 
     * @param model to copy from
     */
    public void copy(Model model) {}
	
	
    /**
     * Removes unnecessary data from the model after the fitness has been
     * computed.
     * 
     * Useful to preserve memory in models that generate a large amount of
     * auxiliary data during the simulation run.
     */
	public void trim() {}

    
	/**
     * Creates a String with information about the parameterization of this
     * model.
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
     * Recombines the programs in this model with the ones in the parameter
     * model, returning a new child model.
     * 
     * @param parent2 the other parent model
     * @return new child model
     */
	public Model recombine(Model parent2)
	{
		Model model = null;
		model = (Model)clone();
		model.progset = progset.recombine(parent2.progset);
		return model;
	}


	/**
     * Creates a new model with a program set copied from this one.
     * 
     * @return new model with cloned program set
     */
	public Model cloneProgs()
	{
		Model model = null;
		model = (Model)clone();
		model.progset = progset.clone(true);
		return model;
	}


	/**
     * A measure of the genotype size for this model.
     * 
     * Computes the total number of nodes in all the programs in the program set
     * of this model.
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
     * Compare branching information between this and the parameter model.
     * 
     * @param model Model to compare branching information against
     * @return true if branching is equal, false otherwise
     */
	public boolean progsCompareBranching(Model model)
	{
		return progset.compareBranching(model.progset);
	}


	/**
     * Feed a program set to the model, for the purpose of maintaining a record
     * of execution paths.
     * 
     * Checks if the program set passed in the parameter contains a new
     * branching configuration. If so, adds it to the set of recorded execution
     * paths. In any case, returns the index of the execution path in the
     * program set.
     * 
     * @param ps Program set representing an execution path
     * @return index of execution path in the model record
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
	
	
	public int compareTo(Model model) {
		
        if (fitness < model.fitness)
        	return -1;
        else if (fitness > model.fitness)
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
}