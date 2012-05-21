package com.telmomenezes.synthetic.evo;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.DRMap;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.generators.GPGen2P;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.samplers.DownSampler;


public class EvoDRMap2P implements EvoGenCallbacks {
    private String outDir;
    private DRMap targDRMap;
    private Generator gen;
    private int targNodeCount;
    private int targEdgeCount;
    private int sampleNodeCount;
    private int sampleEdgeCount;
    private double enratio;
    private long effort;
    private long maxEffort;
    private int bestCount;
    private double samplingRatio;
    
    
    public EvoDRMap2P(Net targNet, String outDir, long maxEffort) {
        this.outDir = outDir;
        this.maxEffort = maxEffort;
        
        targNodeCount = targNet.getNodeCount();
        targEdgeCount = targNet.getEdgeCount();
        
        Net sampleNet = targNet;
        samplingRatio = 1;
        
        // down sampling if needed
        // TODO: configure attenuation
        DownSampler sampler = new DownSampler(targNet, 5);
        while (computeEffort(sampleNet) > maxEffort) {
            sampleNet = sampler.sampleDown();
            samplingRatio = sampler.getRatio();
            System.out.println("sampling down: " + samplingRatio + "; max effort: " + maxEffort + "; current effort: " + computeEffort(sampleNet));
        }
        
        effort = computeEffort(sampleNet);
        
        sampleNodeCount = sampleNet.getNodeCount();
        sampleEdgeCount = sampleNet.getEdgeCount();
        
        targDRMap = genDRMap(sampleNet);
        
        gen = new GPGen2P(sampleNodeCount, sampleEdgeCount);
        
        bestCount = 0;
        
        // write target and sample drmaps
        genDRMap(targNet).draw(outDir + "/target.png");
        genDRMap(sampleNet).draw(outDir + "/targetSample.png");
        
        // write header of evo.csv
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv");
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("gen,best_fit,best_gen_fit,best_geno_size,mean_geno_size,gen_comp_time,sim_comp_time,fit_comp_time\n");
            writer.close() ;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static long computeEffort(Net net) {
        return 2 * net.getNodeCount() * net.getEdgeCount();
    }
    
    public Generator baseGenerator() {
        return gen;
    }

    public double computeFitness(Generator gen) {
        Net net = gen.run();
        DRMap drmap = genDRMap(net);
        gen.setNet(net);
        return targDRMap.emdDistance(drmap);
    }
    
    public void onNewBest(EvoGen evo) {
        String suffix = "" + bestCount + "_gen" + evo.getCurgen();
        Generator bestGen = evo.getBestGenerator();
        // write net
        bestGen.getNet().save(outDir + "/bestnet" + suffix + ".txt", NetFileType.SNAP);
        bestGen.getNet().save(outDir + "/bestnet" + ".txt", NetFileType.SNAP);
        // write drmap
        genDRMap(bestGen.getNet()).draw(outDir + "/best" + suffix + ".png");
        genDRMap(bestGen.getNet()).draw(outDir + "/best" + ".png");
        // write progs
        bestGen.getProgset().write(outDir + "/bestprog" + suffix + ".txt");
        bestGen.getProgset().write(outDir + "/bestprog" + ".txt");
        bestCount++;
    }

    public void onGeneration(EvoGen evo) {
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
                    + evo.getFitTime() + "\n");
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(evo.genInfoString());
    }

    private DRMap genDRMap(Net net) {
        net.computePageranks();
        
        DRMap drmap = net.getDRMapWithLimit(10, -7, 7, -7, 7);
        drmap.logScale();
        drmap.normalizeTotal();
        
        return drmap;
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
