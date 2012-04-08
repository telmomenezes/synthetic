package com.telmomenezes.synthetic.kinship;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.evo.EvoGen;
import com.telmomenezes.synthetic.evo.EvoGenCallbacks;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.io.NetFileType;


public class EvoAllianceGen implements EvoGenCallbacks {
    private String outDir;
    private AllianceGen gen;
    private TopologicalIndices targIndices;
    private int targNodeCount;
    private int targEdgeCount;
    private int nodeCount;
    private int edgeCount;
    private int bestCount;
    
    
    public EvoAllianceGen(Net targNet, String outDir) {
        this.outDir = outDir;
        targIndices = genIndices(targNet);
        System.out.println(targIndices);
        
        nodeCount = targNet.getNodeCount();
        edgeCount = targNet.getEdgeCount();
        targNodeCount = nodeCount;
        targEdgeCount = edgeCount;
        
        gen = new AllianceGen(nodeCount, edgeCount, targIndices);
        
        bestCount = 0;
        
        // write header of evo.csv
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv");
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("gen,best_fit,best_gen_fit,best_ep,best_nc,best_enc,best_ns,best_geno_size,mean_geno_size,gen_comp_time,sim_comp_time,fit_comp_time\n");
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
        TopologicalIndices indices = new TopologicalIndices(net);
        ((AllianceGen)gen).setIndices(indices);
        gen.setNet(net);
        
        double x1 = indices.getEndogamousPercentage() - targIndices.getEndogamousPercentage();
        double x2 = indices.getNetworkConcentration() - targIndices.getNetworkConcentration();
        double x3 = indices.getEndogamicNetworkConcentration() - targIndices.getEndogamicNetworkConcentration();
        double x4 = indices.getNetworkSymmetry() - targIndices.getNetworkSymmetry();
        x1 *= x1;
        x2 *= x2;
        x3 *= x3;
        x4 *= x4;
        
        double dist = Math.sqrt(x1 + x2 + x3 + x4) / 2.0; // max_dist = sqrt(4) = 2
        
        return dist;
    }
    
    public void onNewBest(EvoGen evo) {
        String suffix = "" + bestCount + "_gen" + evo.getCurgen();
        Generator bestGen = evo.getBestGenerator();
        
        System.out.println("targ: " + targIndices);
        System.out.println("best: " + ((AllianceGen)bestGen).getIndices());
        
        // write net
        bestGen.getNet().save(outDir + "/bestnet" + suffix + ".txt", NetFileType.SNAP);
        bestGen.getNet().save(outDir + "/bestnet" + ".txt", NetFileType.SNAP);
        
        // write progs
        bestGen.getProgset().write(outDir + "/bestprog" + suffix + ".txt");
        bestGen.getProgset().write(outDir + "/bestprog" + ".txt");
        bestCount++;
    }

    public void onGeneration(EvoGen evo) {
        AllianceGen bestGen = (AllianceGen)evo.getBestGenerator();
        
        // write evo log
        try {
            FileWriter fwriter = new FileWriter(outDir + "/evo.csv", true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write("" + evo.getCurgen() + ","
                    + evo.getBestFitness() + ","
                    + evo.getBestGenFitness() + ","
                    + bestGen.getIndices().getEndogamousPercentage() + ","
                    + bestGen.getIndices().getNetworkConcentration() + ","
                    + bestGen.getIndices().getEndogamicNetworkConcentration() + ","
                    + bestGen.getIndices().getNetworkSymmetry() + ","
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
        
        System.out.println("" + evo.getCurgen() + ","
                + evo.getBestFitness() + ","
                + evo.getBestGenFitness() + ","
                + bestGen.getIndices().getEndogamousPercentage() + ","
                + bestGen.getIndices().getNetworkConcentration() + ","
                + bestGen.getIndices().getEndogamicNetworkConcentration() + ","
                + bestGen.getIndices().getNetworkSymmetry() + ","
                + evo.getBestGenerator().genotypeSize() + ","
                + evo.getMeanGenoSize() + ","
                + evo.getGenTime() + ","
                + evo.getSimTime() + ","
                + evo.getFitTime() + "\n");
    }

    private TopologicalIndices genIndices(Net net) {
        return new TopologicalIndices(net);
    }
    
    public String infoString() {
        String str = "target net node count: " + targNodeCount + "\n";
        str += "target net edge count: " + targEdgeCount + "\n";
        str += "generated nets node count: " + nodeCount + "\n";
        str += "generated nets edge count: " + edgeCount + "\n";
        
        return str;
    }
}