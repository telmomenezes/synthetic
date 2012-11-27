package com.telmomenezes.synthetic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Vector;

import com.telmomenezes.synthetic.Distrib;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.generators.GPGen1PSampler;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;


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
    
    private Distrib targInDegrees;
    private Distrib targOutDegrees;
    private Distrib targPageRanks;
    private TriadicProfile targTriadicProfile;
    
    private int bins;
	
	
	public Evo(Net targNet, int generations, String outDir)
	{
		this.targNet = targNet;
		this.outDir = outDir;
		        
		bestCount = 0;
		        
		// compute target distributions
		bins = 10;
		targInDegrees = new Distrib(targNet.inDegSeq(), bins);
		targOutDegrees = new Distrib(targNet.outDegSeq(), bins);
		targPageRanks = new Distrib(targNet.prInSeq(), bins);
		        
		targTriadicProfile = new TriadicProfile(targNet);
		        
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
		
		population = new Vector<Generator>();
		bestFitness = Double.MAX_VALUE;
		
		for (int i = 0; i < 2; i++) {
			Generator gen = new GPGen1PSampler(targNet.getNodeCount(), targNet.getEdgeCount());
			gen.initProgsRandom();
			population.add(gen);
		}
		
		// default values
		this.generations = generations;

		// init state
		bestGenerator = null;
		curgen = 0;
		bestFitness = Double.MAX_VALUE;
		bestGenFitness = Double.MAX_VALUE;
		meanGenoSize = 0;
		genTime = 0;
		simTime = 0;
		fitTime = 0;
	}
	
	public void run()
	{
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

				meanGenoSize += generator.genotypeSize();

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
		Generator child = parent.cloneProgs();
			
		// mutate
		Generator random = child.clone();
		random.initProgsRandom();
		newPopulation.add(child.recombine(random));
		
		return newPopulation;
	}
	
	
	private double computeFitness(Generator gen) {
        Net net = gen.getNet();
        
        double inDegreesDist = (new Distrib(net.inDegSeq(), bins, targInDegrees)).emdDistance(targInDegrees);
        double outDegreesDist = (new Distrib(net.outDegSeq(), bins, targOutDegrees)).emdDistance(targOutDegrees);
        double pageRanksDist = (new Distrib(net.prInSeq(), bins, targPageRanks)).emdDistance(targPageRanks);
        double triadicProfileDist = (new TriadicProfile(net)).emdDistance(targTriadicProfile);
    
        gen.setMetric("inDegreesDist", inDegreesDist);
        gen.setMetric("outDegreesDist", outDegreesDist);
        gen.setMetric("pageRanksDist", pageRanksDist);
        gen.setMetric("triadicProfileDist", triadicProfileDist);
        
        double verySmall = 0.999;
        if (inDegreesDist == 0) inDegreesDist = verySmall;
        if (outDegreesDist == 0) outDegreesDist = verySmall;
        if (pageRanksDist == 0) pageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        
        double dist = inDegreesDist * outDegreesDist * pageRanksDist * triadicProfileDist;
        dist = Math.pow(dist, 1.0 / 4.0);
        
        return dist;
    }
    
    private void onNewBest() {
        String suffix = "" + bestCount + "_gen" + curgen;
        Generator bestGen = bestGenerator;
        
        // write net
        bestGen.getNet().save(outDir + "/bestnet" + suffix + ".txt", NetFileType.SNAP);
        bestGen.getNet().save(outDir + "/bestnet" + ".txt", NetFileType.SNAP);
        
        // write progs
        bestGen.getProgset().write(outDir + "/bestprog" + suffix + ".txt");
        bestGen.getProgset().write(outDir + "/bestprog" + ".txt");
        bestCount++;
    }

    private void onGeneration() {
        Generator bestGen = bestGenerator;
        double inDegreesDist = bestGen.getMetric("inDegreesDist");
        double outDegreesDist = bestGen.getMetric("outDegreesDist");
        double pageRanksDist = bestGen.getMetric("pageRanksDist");
        double triadicProfileDist = bestGen.getMetric("triadicProfileDist");
        
        // write evo log
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv", true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("" + curgen + ","
                    + bestFitness + ","
                    + bestGenFitness + ","
                    + bestGenerator.genotypeSize() + ","
                    + meanGenoSize + ","
                    + genTime + ","
                    + simTime + ","
                    + fitTime + ","
                    + inDegreesDist + ","
                    + outDegreesDist + ","
                    + pageRanksDist + ","
                    + triadicProfileDist + "\n");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(genInfoString());
        System.out.println("inDegreesDist: " + inDegreesDist + "; outDegreesDist: " + outDegreesDist + "; pageRanksDist: " + pageRanksDist + "; triadicProfileDist: " + triadicProfileDist );
    }
	
	
	public String infoString()
	{
		String str = "generations: " + generations + "\n";
		str += "target net node count: " + targNet.getNodeCount() + "\n";
        str += "target net edge count: " + targNet.getEdgeCount() + "\n";
		return str;
	}


	private String genInfoString()
	{
		String tmpstr = "gen #" + curgen
        	+ "; best fitness: " + bestFitness
        	+ "; best gen fitness: " + bestGenFitness
        	+ "; best genotype size: " + bestGenerator.genotypeSize()
        	+ "; mean genotype size: " + meanGenoSize
        	+ "; gen comp time: " + genTime + "s."
			+ "; sim comp time: " + simTime + "s."
			+ "; fit comp time: " + fitTime + "s.";
        	return tmpstr;
	}
}