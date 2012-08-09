package com.telmomenezes.synthetic.evo;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.Distrib;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.RandomNet;
import com.telmomenezes.synthetic.generators.GPGen1P;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.motifs.TriadicProfile;
import com.telmomenezes.synthetic.samplers.DownSampler;


public class EvoMix implements EvoGenCallbacks {
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
    
    private double inDegreesRandomDist;
    private double outDegreesRandomDist;
    private double pageRanksRandomDist;
    private double triadicProfileRandomDist;
    
    public EvoMix(Net targNet, String outDir, double maxEffort) {
        this.outDir = outDir;
        this.maxEffort = maxEffort;
        
        targNodeCount = targNet.getNodeCount();
        targEdgeCount = targNet.getEdgeCount();
        
        Net sampleNet = targNet;
        samplingRatio = 1;
        
        // down sampling if needed
        // TODO: configure attenuation and maxNodes
        DownSampler sampler = new DownSampler(targNet, 5, 2000);
        while (computeEffort(sampleNet) > maxEffort) {
            sampleNet = sampler.sampleDown();
            samplingRatio = sampler.getRatio();
            System.out.println("sampling down: " + samplingRatio + "; max effort: " + maxEffort + "; current effort: " + computeEffort(sampleNet));
        }
        
        effort = computeEffort(sampleNet);
        
        sampleNodeCount = sampleNet.getNodeCount();
        sampleEdgeCount = sampleNet.getEdgeCount();
        
        gen = new GPGen1P(sampleNodeCount, sampleEdgeCount);
        
        bestCount = 0;
        
        // compute target distributions
        bins = 10;
        targInDegrees = new Distrib(sampleNet.inDegSeq(), bins);
        targOutDegrees = new Distrib(sampleNet.outDegSeq(), bins);
        targPageRanks = new Distrib(sampleNet.prInSeq(), bins);
        targTriadicProfile = new TriadicProfile(sampleNet);
        
        computeRandomDistances();
        
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
    
    private void computeRandomDistances() {
        inDegreesRandomDist = 0.0;
        outDegreesRandomDist = 0.0;
        pageRanksRandomDist = 0.0;
        triadicProfileRandomDist = 0.0;
        
        int samples = 100;
        
        for (int i = 0; i < samples; i++) {
            RandomNet rnet = new RandomNet(sampleNodeCount, sampleEdgeCount);
            inDegreesRandomDist += (new Distrib(rnet.inDegSeq(), bins, targInDegrees)).emdDistance(targInDegrees);
            outDegreesRandomDist += (new Distrib(rnet.outDegSeq(), bins, targOutDegrees)).emdDistance(targOutDegrees);
            pageRanksRandomDist += (new Distrib(rnet.prInSeq(), bins, targPageRanks)).emdDistance(targPageRanks);
            triadicProfileRandomDist += (new TriadicProfile(rnet)).emdDistance(targTriadicProfile);
        }
        
        inDegreesRandomDist /= samples;
        outDegreesRandomDist /= samples;
        pageRanksRandomDist /= samples;
        triadicProfileRandomDist /= samples;
        
        System.out.println("Random distances: inDegreesRandomDist=" + inDegreesRandomDist + "; outDegreesRandomDist=" + outDegreesRandomDist + "; pageRanksRandomDist=" + pageRanksRandomDist + "; triadicProfileRandomDist=" + triadicProfileRandomDist);
    }
    
    private static double computeEffort(Net net) {
        return ((double)net.getNodeCount()) * ((double)net.getNodeCount()) * ((double)net.getEdgeCount()) * 0.001;
    }
    
    public Generator baseGenerator() {
        return gen;
    }

    public double computeFitness(Generator gen) {
        gen.run();
        Net net = gen.getNet();
        
        double inDegreesDist = (new Distrib(net.inDegSeq(), bins, targInDegrees)).emdDistance(targInDegrees);
        double outDegreesDist = (new Distrib(net.outDegSeq(), bins, targOutDegrees)).emdDistance(targOutDegrees);
        double pageRanksDist = (new Distrib(net.prInSeq(), bins, targPageRanks)).emdDistance(targPageRanks);
        double triadicProfileDist = (new TriadicProfile(net)).emdDistance(targTriadicProfile);
    
        gen.setMetric("inDegreesDist", inDegreesDist);
        gen.setMetric("outDegreesDist", outDegreesDist);
        gen.setMetric("pageRanksDist", pageRanksDist);
        gen.setMetric("triadicProfileDist", triadicProfileDist);

        double d1a = inDegreesDist / inDegreesRandomDist;
        double d1b = outDegreesDist / outDegreesRandomDist;
        double d1 = (d1a + d1b) / 2;
        double d2 = pageRanksDist / pageRanksRandomDist;
        double d3 = triadicProfileDist / triadicProfileRandomDist;
        
        return d1 + d2 + d3;
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