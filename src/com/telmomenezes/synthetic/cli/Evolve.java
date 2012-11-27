package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.evo.EvoMix;
import com.telmomenezes.synthetic.evo.EvoGen;
import com.telmomenezes.synthetic.evo.EvoStrategy;


public class Evolve extends Command {

    @Override
    public boolean run(CommandLine cline) {
        // TODO: make configurable
        double maxEffort = 100000.0;
        int generations = 999999999;
        
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
        
        Net net = Net.load(netfile);
        EvoMix callbacks = new EvoMix(net, outdir, maxEffort);
        EvoStrategy popGen = new EvoStrategy(1, 1, 1);
        EvoGen evo = new EvoGen(popGen, callbacks, generations);
        
        System.out.println("target net: " + netfile);
        System.out.println(evo.infoString());
        
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