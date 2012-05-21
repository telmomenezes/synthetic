package com.telmomenezes.synthetic.evo;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.DRMap;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.generators.GPGen2P;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;


public class EvoDRMap2P implements EvoGenCallbacks {
    private String outDir;
    private DRMap targDRMap;
    private Generator gen;
    private int targNodeCount;
    private int targEdgeCount;
    private int nodeCount;
    private int edgeCount;
    private double enratio;
    private long effort;
    private long maxEffort;
    private int bestCount;
    
    
    public EvoDRMap2P(Net targNet, String outDir, long maxEffort) {
        this.outDir = outDir;
        targDRMap = genDRMap(targNet);
        this.maxEffort = maxEffort;
        
        nodeCount = targNet.getNodeCount();
        edgeCount = targNet.getEdgeCount();
        targNodeCount = nodeCount;
        targEdgeCount = edgeCount;
        
        effort = 2 * nodeCount * edgeCount;
        
        // in case max effort is exceeded...
        if (effort > maxEffort) {
            enratio = ((double)edgeCount) / ((double)nodeCount);
            nodeCount = (int)Math.sqrt(((double)maxEffort) / enratio);
            edgeCount = (int)(nodeCount * enratio);
            effort = maxEffort;
        }
        
        gen = new GPGen2P(nodeCount, edgeCount);
        
        bestCount = 0;
        
        // write target drmap
        genDRMap(targNet).draw(outDir + "/target.png");
        
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
        str += "generated nets node count: " + nodeCount + "\n";
        str += "generated nets edge count: " + edgeCount + "\n";
        str += "edge/node ratio: " + enratio + "\n";
        str += "max effort: " + maxEffort + "\n";
        str += "effort: " + effort + "\n";
        
        return str;
    }
}
