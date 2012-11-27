package com.telmomenezes.synthetic.evo;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.Distrib;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.generators.GPGen1PSampler;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;
//import com.telmomenezes.synthetic.samplers.DownSampler;


public class EvoMix {
    private String outDir;
    private Generator gen;
    private int targNodeCount;
    private int targEdgeCount;
    private int sampleNodeCount;
    private int sampleEdgeCount;
    private double enratio;
    private double effort;
    private double maxEffort;
    private int bestCount;
    private double samplingRatio;
    
    private Distrib targInDegrees;
    private Distrib targOutDegrees;
    private Distrib targPageRanks;
    private TriadicProfile targTriadicProfile;
    
    private int bins;
    
    public EvoMix(Net targNet, String outDir, double maxEffort) {
        this.outDir = outDir;
        this.maxEffort = maxEffort;
        
        targNodeCount = targNet.getNodeCount();
        targEdgeCount = targNet.getEdgeCount();
        
        Net sampleNet = targNet;
        samplingRatio = 1;
        
        // down sampling if needed
        // TODO: configure attenuation and maxNodes
        /*
        DownSampler sampler = new DownSampler(targNet, 5, 2000);
        while (computeEffort(sampleNet) > maxEffort) {
            sampleNet = sampler.sampleDown();
            samplingRatio = sampler.getRatio();
            System.out.println("sampling down: " + samplingRatio + "; max effort: " + maxEffort + "; current effort: " + computeEffort(sampleNet));
        }*/
        
        effort = computeEffort(sampleNet);
        
        sampleNodeCount = sampleNet.getNodeCount();
        sampleEdgeCount = sampleNet.getEdgeCount();
        
        gen = new GPGen1PSampler(sampleNodeCount, sampleEdgeCount);
        
        bestCount = 0;
        
        // compute target distributions
        bins = 10;
        targInDegrees = new Distrib(sampleNet.inDegSeq(), bins);
        targOutDegrees = new Distrib(sampleNet.outDegSeq(), bins);
        targPageRanks = new Distrib(sampleNet.prInSeq(), bins);
        
        targTriadicProfile = new TriadicProfile(sampleNet);
        
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
    
    private static double computeEffort(Net net) {
        return ((double)net.getNodeCount()) * ((double)net.getNodeCount()) * ((double)net.getEdgeCount()) * 0.001;
    }
    
    public Generator baseGenerator() {
        return gen;
    }

    public double computeFitness(Generator gen) {
    	System.out.println("#1");
        gen.run();
        System.out.println("#2");
        Net net = gen.getNet();
        
        System.out.println("#3");
        double inDegreesDist = (new Distrib(net.inDegSeq(), bins, targInDegrees)).emdDistance(targInDegrees);
        System.out.println("#4");
        double outDegreesDist = (new Distrib(net.outDegSeq(), bins, targOutDegrees)).emdDistance(targOutDegrees);
        System.out.println("#5");
        double pageRanksDist = (new Distrib(net.prInSeq(), bins, targPageRanks)).emdDistance(targPageRanks);
        System.out.println("#6");
        double triadicProfileDist = (new TriadicProfile(net)).emdDistance(targTriadicProfile);
    
        System.out.println("#7");
        gen.setMetric("inDegreesDist", inDegreesDist);
        gen.setMetric("outDegreesDist", outDegreesDist);
        gen.setMetric("pageRanksDist", pageRanksDist);
        gen.setMetric("triadicProfileDist", triadicProfileDist);
        
        System.out.println("#8");
        
        double verySmall = 0.999;
        if (inDegreesDist == 0) inDegreesDist = verySmall;
        if (outDegreesDist == 0) outDegreesDist = verySmall;
        if (pageRanksDist == 0) pageRanksDist = verySmall;
        if (triadicProfileDist == 0) triadicProfileDist = verySmall;
        
        double dist = inDegreesDist * outDegreesDist * pageRanksDist * triadicProfileDist;
        dist = Math.pow(dist, 1.0 / 4.0);
        
        System.out.println("#9");
        
        return dist;
    }
    
    public void onNewBest(EvoGen evo) {
        String suffix = "" + bestCount + "_gen" + evo.getCurgen();
        Generator bestGen = evo.getBestGenerator();
        // write net
        bestGen.getNet().save(outDir + "/bestnet" + suffix + ".txt", NetFileType.SNAP);
        bestGen.getNet().save(outDir + "/bestnet" + ".txt", NetFileType.SNAP);
        // write progs
        bestGen.getProgset().write(outDir + "/bestprog" + suffix + ".txt");
        bestGen.getProgset().write(outDir + "/bestprog" + ".txt");
        bestCount++;
    }

    public void onGeneration(EvoGen evo) {
        Generator bestGen = evo.getBestGenerator();
        double inDegreesDist = bestGen.getMetric("inDegreesDist");
        double outDegreesDist = bestGen.getMetric("outDegreesDist");
        double pageRanksDist = bestGen.getMetric("pageRanksDist");
        double triadicProfileDist = bestGen.getMetric("triadicProfileDist");
        
        // write evo log
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv", true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("" + evo.getCurgen() + ","
                    + evo.getBestFitness() + ","
                    + evo.getBestGenFitness() + ","
                    + evo.getBestGenerator().genotypeSize() + ","
                    + evo.getMeanGenoSize() + ","
                    + evo.getGenTime() + ","
                    + evo.getSimTime() + ","
                    + evo.getFitTime() + ","
                    + inDegreesDist + ","
                    + outDegreesDist + ","
                    + pageRanksDist + ","
                    + triadicProfileDist + "\n");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(evo.genInfoString());
        System.out.println("inDegreesDist: " + inDegreesDist + "; outDegreesDist: " + outDegreesDist + "; pageRanksDist: " + pageRanksDist + "; triadicProfileDist: " + triadicProfileDist );
    }
    
    public String infoString() {
        String str = "target net node count: " + targNodeCount + "\n";
        str += "target net edge count: " + targEdgeCount + "\n";
        str += "edge/node ratio: " + enratio + "\n";
        str += "sample net node count: " + sampleNodeCount + "\n";
        str += "sample net edge count: " + sampleEdgeCount + "\n";
        str += "sampling ratio: " + samplingRatio + "\n";
        str += "max effort: " + maxEffort + "\n";
        str += "effort: " + effort + "\n";
        
        return str;
    }
}