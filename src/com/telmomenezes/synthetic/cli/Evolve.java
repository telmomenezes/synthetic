package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Evo;
import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.samplers.DownSampler;


public class Evolve extends Command {

    @Override
    public boolean run() throws SynCliException {
        
        String netfile = getStringParam("inet");
        String outdir = getStringParam("odir");
        int generations = getIntegerParam("gens", 10000);
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        int maxNodes = getIntegerParam("maxnodes", 999999999);
        int maxEdges = getIntegerParam("maxedges", 999999999);
        boolean directed = !paramExists("undir");
        double tolerance = getDoubleParam("tolerance", 0.01);
        
        Net net = Net.load(netfile, directed);
        
        // down sampling if needed
        Net sampleNet = DownSampler.sample(net, maxNodes, maxEdges);
        
     	Generator baseGenerator = new Generator(sampleNet.getNodeCount(), sampleNet.getEdgeCount(), directed, sr);
     	
        Evo evo = new Evo(sampleNet, generations, bins, tolerance, baseGenerator, outdir);
        
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