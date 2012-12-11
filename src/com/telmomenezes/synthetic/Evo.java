package com.telmomenezes.synthetic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Vector;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;


/**
 * Basic generation based evolutionary algorithm.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Evo {
    
	private Vector<Generator> population;
	private double bestFitness;
	
    // parameters
	private Net targNet;
	private int generations;
	private Generator baseGenerator;

    // state
	private Generator bestGenerator;
	private int curgen;
	private double bestGenFitness;
	private double meanGenoSize;
	private double genTime;
	private double simTime;
	private double fitTime;
	
	private String outDir;
    private int bestCount;
    
    private MetricsBag targBag;
    
    private int bins;
	
	
	public Evo(Net targNet, int generations, int bins, Generator baseGenerator, String outDir)
	{
		this.targNet = targNet;
		this.generations = generations;
		this.baseGenerator = baseGenerator;
		this.outDir = outDir;
		
		this.bins = bins;
		targBag = new MetricsBag(targNet, bins);
	}
	
	public void run()
	{
		// init state
		meanGenoSize = 0;
		genTime = 0;
		simTime = 0;
		fitTime = 0;
		bestGenerator = null;
		bestFitness = Double.MAX_VALUE;
		bestGenFitness = Double.MAX_VALUE;
		bestCount = 0;
		writeLogHeader();
	
		// init population
		population = new Vector<Generator>();
		for (int i = 0; i < 2; i++) {
			Generator gen = baseGenerator.instance();
			gen.initRandom();
			population.add(gen);
		}
		
		// evolve
		for(curgen = 0; curgen < generations; curgen++) {

			long startTime = System.currentTimeMillis();
			meanGenoSize = 0;
			
			simTime = 0;
			fitTime = 0;

			bestGenFitness = Double.MAX_VALUE;
			
			Generator generator;
			boolean first = false;
			for (int j = 0; j < 2; j++) {
				generator = population.get(j);

				meanGenoSize += generator.getProg().size();

				if (!generator.simulated) {
					long time0 = System.currentTimeMillis();
					generator.run();
					simTime += System.currentTimeMillis() - time0;
					time0 = System.currentTimeMillis();
					generator.fitness = computeFitness(generator);
					fitTime += System.currentTimeMillis() - time0;
				
				    if (first || (generator.fitness < bestGenFitness)) {
				        first = false;
				        bestGenFitness = generator.fitness;
				    }
				    generator.simulated = true;
				}

				if (((curgen == 0) && (j == 0)) || (generator.fitness < bestFitness)) {
					bestFitness = generator.fitness;
					bestGenerator = generator;
					onNewBest();
				}
			}
			
			meanGenoSize /= 2.0;

			// assign new population
			population = newGeneration();

			// time it took to compute the generation
			genTime = System.currentTimeMillis() - startTime;
			genTime /= 1000;
			simTime /= 1000;
			fitTime /= 1000;
			
			// onGeneration callback
			onGeneration();
		}
	}
	

	private Vector<Generator> newGeneration() {
		
		// send the parents to the start of the vector by sorting
		Collections.sort(population);
		Generator parent = population.get(0);
		
		Vector<Generator> newPopulation = new Vector<Generator>();
		
		
		// place parent in new population
		newPopulation.add(parent);
		
		// generate offspring
		Generator child = parent.clone();
			
		// mutate
		newPopulation.add(child.mutate());
		
		return newPopulation;
	}
	
	
	private double computeFitness(Generator gen) {
        Net net = gen.getNet();
        
        MetricsBag genBag = new MetricsBag(net, bins, targBag);

        gen.setMetricsBag(genBag);

        return genBag.getDistance();
    }
    
    private void onNewBest() {
        String suffix = "" + bestCount + "_gen" + curgen;
        Generator bestGen = bestGenerator;
        
        // write net
        bestGen.getNet().save(outDir + "/bestnet" + suffix + ".txt", NetFileType.SNAP);
        bestGen.getNet().save(outDir + "/bestnet" + ".txt", NetFileType.SNAP);
        
        // write progs
        bestGen.getProg().write(outDir + "/bestprog" + suffix + ".txt");
        bestGen.getProg().write(outDir + "/bestprog" + ".txt");
        bestCount++;
    }

    private void writeLogHeader() {
    	// write header of evo.csv
    	try {
    		FileWriter fwriter = new FileWriter(outDir + "/evo.csv");
    		BufferedWriter writer = new BufferedWriter(fwriter);
    		writer.write("gen,best_fit,best_gen_fit,best_geno_size,mean_geno_size,gen_comp_time,sim_comp_time,fit_comp_time,in_degrees_dist,out_degrees_dist,pageranks_dist,triadic_profile_dist\n");
    		writer.close() ;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void onGeneration() {
        Generator bestGen = bestGenerator;
        double inDegreesDist = bestGen.getMetricsBag().getInDegreesDist();
        double outDegreesDist = bestGen.getMetricsBag().getOutDegreesDist();
        double inPageRanksDist = bestGen.getMetricsBag().getInPageRanksDist();
        double outPageRanksDist = bestGen.getMetricsBag().getOutPageRanksDist();
        double triadicProfileDist = bestGen.getMetricsBag().getInDegreesDist();
        
        // write evo log
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv", true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("" + curgen + ","
                    + bestFitness + ","
                    + bestGenFitness + ","
                    + bestGenerator.getProg().size() + ","
                    + meanGenoSize + ","
                    + genTime + ","
                    + simTime + ","
                    + fitTime + ","
                    + inDegreesDist + ","
                    + outDegreesDist + ","
                    + inPageRanksDist + ","
                    + outPageRanksDist + ","
                    + triadicProfileDist + "\n");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(genInfoString());
        System.out.println("inDegreesDist: " + inDegreesDist + "; outDegreesDist: " + outDegreesDist + "; inPageRanksDist: " + inPageRanksDist + "; outPageRanksDist: " + outPageRanksDist + "; triadicProfileDist: " + triadicProfileDist );
    }
	
	
	public String infoString()
	{
		String str = "generations: " + generations + "\n";
		str += "target net node count: " + targNet.getNodeCount() + "\n";
        str += "target net edge count: " + targNet.getEdgeCount() + "\n";
        str += "distribution bins: " + bins + "\n";
		return str;
	}


	private String genInfoString()
	{
		String tmpstr = "gen #" + curgen
        	+ "; best fitness: " + bestFitness
        	+ "; best gen fitness: " + bestGenFitness
        	+ "; best genotype size: " + bestGenerator.getProg().size()
        	+ "; mean genotype size: " + meanGenoSize
        	+ "; gen comp time: " + genTime + "s."
			+ "; sim comp time: " + simTime + "s."
			+ "; fit comp time: " + fitTime + "s.";
        	return tmpstr;
	}
}