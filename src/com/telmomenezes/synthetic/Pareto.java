package com.telmomenezes.synthetic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;


public class Pareto {
    
	private Vector<Generator> paretoFront;
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
	
	
	public Pareto(Net targNet, int generations, int bins, Generator baseGenerator, String outDir)
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
	
		// init pareto front
		paretoFront = new Vector<Generator>();
		Generator gen = baseGenerator.instance();
		gen.initRandom();
		gen.run();
		gen.fitness = computeFitness(gen);
		bestGenFitness = gen.getFitness();
		paretoFront.add(gen);
		
		// evolve
		for(curgen = 0; curgen < generations; curgen++) {
			long startTime = System.currentTimeMillis();
			meanGenoSize = 0;
			
			simTime = 0;
			fitTime = 0;

			Generator parent1 = paretoFront.get(RandomGenerator.instance().random.nextInt(paretoFront.size()));
			
			Generator child = null;
			
			if (paretoFront.size() == 1) {
				child = parent1.clone().mutate();
			}
			else {
				Generator parent2 = paretoFront.get(RandomGenerator.instance().random.nextInt(paretoFront.size()));
				child = parent1.recombine(parent2).mutate();
			}
			
			long time0 = System.currentTimeMillis();
			child.run();
			simTime += System.currentTimeMillis() - time0;
			time0 = System.currentTimeMillis();
			child.fitness = computeFitness(child);
			fitTime += System.currentTimeMillis() - time0;

			if (child.fitness < bestFitness) {
				bestFitness = child.fitness;
				bestGenerator = child;
				onNewBest();
			}
			
			updateParetoFront(child);
			
			//writeParetoFront();
			
			//System.out.println(targBag.getOutDegrees());
			//System.out.println(bestGenerator.getMetricsBag().getOutDegrees());

			// time it took to compute the generation
			genTime = System.currentTimeMillis() - startTime;
			genTime /= 1000;
			simTime /= 1000;
			fitTime /= 1000;
			
			// onGeneration callback
			onGeneration();
			
			printBestMetrics();
		}
	}
	
	
	private double computeFitness(Generator gen) {
        Net net = gen.getNet();
        
        MetricsBag genBag = new MetricsBag(net, bins, targBag);

        gen.setMetricsBag(genBag);
        
        return genBag.getDistance();
    }
    
	
	private void updateParetoFront(Generator gen) {
		paretoFront.add(gen);
		
		Vector<Generator> newFront = new Vector<Generator>();
		
		for (Generator g : paretoFront) {
			if (paretoDominates(g)) {
				newFront.add(g);
			}
		}
		
		paretoFront = newFront;
		System.out.println("pareto front size: " + paretoFront.size());
	}
	
	
	private boolean paretoDominates(Generator gen) {
		for (Generator g : paretoFront) {
			if (g != gen) {
				if (!gen.getMetricsBag().paretoDominates(g.getMetricsBag())) {
					return false;
				}
			}
		}
		
		return true;
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
        double triadicProfileDist = bestGen.getMetricsBag().getTriadicProfileDist();
        
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
	
	
	private void printBestMetrics() {
		double bestInDegreesDist = Double.POSITIVE_INFINITY;
		double bestOutDegreesDist = Double.POSITIVE_INFINITY;
		double bestInPageRanksDist = Double.POSITIVE_INFINITY;
		double bestOutPageRanksDist = Double.POSITIVE_INFINITY;
		double bestTriadicProfileDist = Double.POSITIVE_INFINITY;
		
		double avgInDegreesDist = 0;
		double avgOutDegreesDist = 0;
		double avgInPageRanksDist = 0;
		double avgOutPageRanksDist = 0;
		double avgTriadicProfileDist = 0;
	    
		double count = 0;
		
		for (Generator g : paretoFront) {
			MetricsBag bag = g.getMetricsBag();
			
			count += 1.0;
			
			avgInDegreesDist += bag.getInDegreesDist();
			avgOutDegreesDist += bag.getOutDegreesDist();
			avgInPageRanksDist += bag.getInPageRanksDist();
			avgOutPageRanksDist += bag.getOutPageRanksDist();
			avgTriadicProfileDist += bag.getTriadicProfileDist();
			
			if (bag.getInDegreesDist() < bestInDegreesDist) {
				bestInDegreesDist = bag.getInDegreesDist();
			}
			if (bag.getOutDegreesDist() < bestOutDegreesDist) {
				bestOutDegreesDist = bag.getOutDegreesDist();
			}
			if (bag.getInPageRanksDist() < bestInPageRanksDist) {
				bestInPageRanksDist = bag.getInPageRanksDist();
			}
			if (bag.getOutPageRanksDist() < bestOutPageRanksDist) {
				bestOutPageRanksDist = bag.getOutPageRanksDist();
			}
			if (bag.getTriadicProfileDist() < bestTriadicProfileDist) {
				bestTriadicProfileDist = bag.getTriadicProfileDist();
			}
		}
		
		avgInDegreesDist /= count;
		avgOutDegreesDist /= count;
		avgInPageRanksDist /= count;
		avgOutPageRanksDist /= count;
		avgTriadicProfileDist /= count;
		
		String str = "bestInDegreesDist: " + bestInDegreesDist;
		str += "; bestOutDegreesDist: " + bestOutDegreesDist;
		str += "; bestInPageRanksDist: " + bestInPageRanksDist;
		str += "; bestOutPageRanksDist: " + bestOutPageRanksDist;
		str += "; bestTriadicProfileDist: " + bestTriadicProfileDist;
		
		System.out.println(str);
		
		str = "avgInDegreesDist: " + avgInDegreesDist;
		str += "; avgOutDegreesDist: " + avgOutDegreesDist;
		str += "; avgInPageRanksDist: " + avgInPageRanksDist;
		str += "; avgOutPageRanksDist: " + avgOutPageRanksDist;
		str += "; avgTriadicProfileDist: " + avgTriadicProfileDist;
		
		System.out.println(str);
	}
	
	public void writeParetoFront() {
    	try {
    		FileWriter fwriter = new FileWriter(outDir + "/pareto.csv");
    		BufferedWriter writer = new BufferedWriter(fwriter);
    		writer.write("fit, geno_size, in_degrees_dist, out_degrees_dist, in_pageranks_dist, out_pageranks_dist, triadic_profile_dist\n");
    		
    		for (Generator gen : paretoFront) {
    			MetricsBag bag = gen.getMetricsBag();
    			
    			String str = "" + gen.fitness;
    			str += ", " + gen.getProg().size();
    			str += ", " + bag.getInDegreesDist();
    			str += ", " + bag.getOutDegreesDist();
    			str += ", " + bag.getInPageRanksDist();
    			str += ", " + bag.getOutPageRanksDist();
    			str += ", " + bag.getTriadicProfileDist();
    			str += "\n";
    			
    			writer.write(str);
    		}
    		
    		writer.close() ;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}