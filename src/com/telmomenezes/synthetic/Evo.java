package com.telmomenezes.synthetic;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.random.RandomGenerator;


public class Evo {
    // best generators
	private Generator bestGenerator;
	private Generator bestFitGenerator;
	private double bestFitnessMax;
	private double bestFitnessAvg;
	
    // parameters
	private Net targNet;
	private int generations;
	private Generator baseGenerator;
	private String outDir;
	
    // state
	private int curgen;
	private double genTime;
	private double simTime;
	private double fitTime;
	
    private int bestCount;
    
    private MetricsBag targBag;
    
    private int bins;
    
    private double tolerance;
    
	
	public Evo(Net targNet, int generations, int bins, double tolerance, Generator baseGenerator, String outDir) {
		this.targNet = targNet;
		this.generations = generations;
		this.baseGenerator = baseGenerator;
		this.outDir = outDir;
		this.bins = bins;
		this.tolerance = tolerance;
		
		targBag = new MetricsBag(targNet, bins);
		writeDistribs(targBag, "targ");
	}
	
	
	public void run() {
		// init state
		genTime = 0;
		simTime = 0;
		fitTime = 0;
		bestCount = 0;
		writeLogHeader();
	
		// init population
		bestFitGenerator = baseGenerator.instance();
		bestFitGenerator.initRandom();
		bestFitGenerator.run();
		bestFitGenerator.computeFitness(targBag, bins);
		bestFitGenerator.getNet().clean();
		bestGenerator = bestFitGenerator;
		
		bestFitnessMax = bestFitGenerator.fitnessMax;
		bestFitnessAvg = bestFitGenerator.fitnessAvg;
		
		// evolve
		int stableGens = 0;
		for(curgen = 0; stableGens < generations; curgen++) {
			stableGens++;
			
			long startTime = System.currentTimeMillis();
			
			simTime = 0;
			fitTime = 0;
			
			Generator generator;
			if (RandomGenerator.random.nextBoolean()) {
				generator = bestFitGenerator.clone();
			}
			else {
				generator = bestGenerator.clone();
			}
			
			generator = generator.mutate();

			long time0 = System.currentTimeMillis();
			generator.run();
			simTime += System.currentTimeMillis() - time0;
			time0 = System.currentTimeMillis();
			generator.computeFitness(targBag, bins);
			generator.getNet().clean();
			fitTime += System.currentTimeMillis() - time0;	
			
			if (generator.isBetterThan(bestFitGenerator, bestFitnessMax, bestFitnessAvg, 0)) {
				bestFitnessMax = generator.fitnessMax;
				bestFitnessAvg = generator.fitnessAvg;
				bestFitGenerator = generator;
				stableGens = 0;
			}
			
			if (generator.isBetterThan(bestGenerator, bestFitnessMax, bestFitnessAvg, tolerance)) {
				bestGenerator = generator;
				onNewBest();
				stableGens = 0;
			}

			// time it took to compute the generation
			genTime = System.currentTimeMillis() - startTime;
			genTime /= 1000;
			simTime /= 1000;
			fitTime /= 1000;
			
			System.out.println("stable generation: " + stableGens);
			onGeneration();
		}
		
		System.out.println("Done.");
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
        
        // write distribs
        writeDistribs(bestGen.getNet().metricsBag, "best");
        
        bestCount++;
    }

    private void writeLogHeader() {
    	// write header of evo.csv
    	try {
    		FileWriter fwriter = new FileWriter(outDir + "/evo.csv");
    		BufferedWriter writer = new BufferedWriter(fwriter);
    		if (isDirected()) {
    			writer.write("gen,best_fit_max,best_fit_avg,best_geno_size,gen_comp_time,sim_comp_time,fit_comp_time,in_degrees_dist,out_degrees_dist,d_pageranks_dist,u_pageranks_dist,triadic_profile_dist,d_dists_dist,u_dists_dist\n");
    		}
    		else {
    			writer.write("gen,best_fit_max,best_fit_avg,best_geno_size,gen_comp_time,sim_comp_time,fit_comp_time,degrees_dist,u_pageranks_dist,triadic_profile_dist,u_dists_dist\n");
    		}
    		writer.close() ;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    private void writeDistribs(MetricsBag bag, String prefix) {
    	if (isDirected()) {
    		bag.getInDegrees().write(outDir + "/" + prefix + "_in_degrees.csv", false);
    		bag.getOutDegrees().write(outDir + "/" + prefix + "_out_degrees.csv", false);
    		bag.getDPageRanks().write(outDir + "/" + prefix + "_d_pagerank.csv", false);
    		bag.getdDists().write(outDir + "/" + prefix + "_d_dists.csv", false);
    	}
    	else {
    		bag.getDegrees().write(outDir + "/" + prefix + "_degrees.csv", false);
    	}
    	bag.getUPageRanks().write(outDir + "/" + prefix + "_u_pagerank.csv", false);
    	bag.getTriadicProfile().write(outDir + "/" + prefix + "_triadic_profile.csv", false);
    	bag.getuDists().write(outDir + "/" + prefix + "_u_dists.csv", false);
    }
    
    
    private void onGeneration() {
    	if (isDirected()) {
    		onGenerationDirected();
    	}
    	else {
    		onGenerationUndirected();
    	}
    }
    
    
    private void onGenerationDirected() {
        Generator bestGen = bestGenerator;
        double inDegreesDist = bestGen.getNet().metricsBag.getInDegreesDist();
        double outDegreesDist = bestGen.getNet().metricsBag.getOutDegreesDist();
        double dPageRanksDist = bestGen.getNet().metricsBag.getDPageRanksDist();
        double uPageRanksDist = bestGen.getNet().metricsBag.getUPageRanksDist();
        double triadicProfileDist = bestGen.getNet().metricsBag.getTriadicProfileDist();
        double dDistsDist = bestGen.getNet().metricsBag.getdDistsDist();
        double uDistsDist = bestGen.getNet().metricsBag.getuDistsDist();
        
        // write evo log
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv", true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("" + curgen + ","
                    + bestFitnessMax + ","
                    + bestFitnessAvg + ","
                    + bestGenerator.getProg().size() + ","
                    + genTime + ","
                    + simTime + ","
                    + fitTime + ","
                    + inDegreesDist + ","
                    + outDegreesDist + ","
                    + dPageRanksDist + ","
                    + uPageRanksDist + ","
                    + dDistsDist + ","
            		+ uDistsDist + ","
                    + triadicProfileDist + "\n");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(genInfoString());
        System.out.println("inDegreesDist: " + inDegreesDist + "; outDegreesDist: " + outDegreesDist + "; dPageRanksDist: " + dPageRanksDist + "; uPageRanksDist: " + uPageRanksDist + "; triadicProfileDist: " + triadicProfileDist + "; dDistsDist: " + dDistsDist + "; uDistsDist: " + uDistsDist);
    }
	
    
    private void onGenerationUndirected() {
        Generator bestGen = bestGenerator;
        double degreesDist = bestGen.getNet().metricsBag.getDegreesDist();
        double uPageRanksDist = bestGen.getNet().metricsBag.getUPageRanksDist();
        double triadicProfileDist = bestGen.getNet().metricsBag.getTriadicProfileDist();
        double uDistsDist = bestGen.getNet().metricsBag.getuDistsDist();
        
        // write evo log
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv", true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("" + curgen + ","
                    + bestFitnessMax + ","
                    + bestFitnessAvg + ","
                    + bestGenerator.getProg().size() + ","
                    + genTime + ","
                    + simTime + ","
                    + fitTime + ","
                    + degreesDist + ","
                    + uPageRanksDist + ","
            		+ uDistsDist + ","
                    + triadicProfileDist + "\n");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(genInfoString());
        System.out.println("degreesDist: " + degreesDist + "; uPageRanksDist: " + uPageRanksDist + "; triadicProfileDist: " + triadicProfileDist + "; uDistsDist: " + uDistsDist);
    }
    
    
    public boolean isDirected() {
    	return targNet.directed;
    }
    
	
	public String infoString()
	{
		String str = "stable generations: " + generations + "\n";
		str += "directed: " + isDirected() + "\n";
		str += "target net node count: " + targNet.getNodeCount() + "\n";
        str += "target net edge count: " + targNet.getEdgeCount() + "\n";
        str += "distribution bins: " + bins + "\n";
        str += "tolerance: " + tolerance + "\n";
		return str;
	}


	private String genInfoString()
	{
		String tmpstr = "gen #" + curgen
        	+ "; best fitness max: " + bestFitnessMax
        	+ "; best fitness avg: " + bestFitnessAvg
        	+ "; best genotype size: " + bestGenerator.getProg().size()
        	+ "; gen comp time: " + genTime + "s."
			+ "; sim comp time: " + simTime + "s."
			+ "; fit comp time: " + fitTime + "s.";
        	return tmpstr;
	}
}