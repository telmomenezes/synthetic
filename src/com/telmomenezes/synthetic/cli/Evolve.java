package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
//import com.telmomenezes.synthetic.Evo;
//import com.telmomenezes.synthetic.GA;
import com.telmomenezes.synthetic.Pareto;
import com.telmomenezes.synthetic.generators.FastGenerator;
import com.telmomenezes.synthetic.generators.FullGenerator;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.samplers.DownSampler;


public class Evolve extends Command {

    @Override
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("odir")) {
            setErrorMessage("output directory must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        String outdir = cline.getOptionValue("odir");
        
        boolean fastGen = true;
        if(cline.hasOption("gentype")) {
            if (cline.getOptionValue("gentype").equals("full")) {
            	fastGen = false;
            }
        }
        
        int generations = 999999999;
        if(cline.hasOption("gens")) {
            generations = new Integer(cline.getOptionValue("gens"));
        }
        
        Net net = Net.load(netfile);
        
        // down sampling if needed
     	// TODO: configure attenuation and maxNodes
        int maxNodes = 999999999;
        int maxEdges = 999999999;
        if(cline.hasOption("maxnodes")) {
            maxNodes = new Integer(cline.getOptionValue("maxnodes"));
        }
        if(cline.hasOption("maxedges")) {
            maxEdges = new Integer(cline.getOptionValue("maxedges"));
        }
        Net sampleNet = DownSampler.sample(net, maxNodes, maxEdges);
        
     	Generator baseGenerator = null;
     	if (fastGen) {
     		baseGenerator = new FastGenerator(sampleNet.getNodeCount(), sampleNet.getEdgeCount());
     	}
     	else {
     		baseGenerator = new FullGenerator(sampleNet.getNodeCount(), sampleNet.getEdgeCount());
     	}
     	
     	int bins = 100;
     	if(cline.hasOption("bins")) {
            bins = new Integer(cline.getOptionValue("bins"));
        }
     	
        //Evo evo = new Evo(sampleNet, generations, bins, baseGenerator, outdir);
        //GA evo = new GA(sampleNet, 200, generations, bins, baseGenerator, outdir);
        Pareto evo = new Pareto(sampleNet, generations, bins, baseGenerator, outdir);
        
        System.out.println("target net: " + netfile);
        System.out.println(evo.infoString());
        System.out.println(baseGenerator);
        
        // write experiment params to file
        try {
            FileWriter fstream = new FileWriter(outdir + "/params.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(evo.infoString());
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        evo.run();
        
        return true;
    }
}