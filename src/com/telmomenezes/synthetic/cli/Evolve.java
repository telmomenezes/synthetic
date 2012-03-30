package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.evo.EvoDRMap2P;
import com.telmomenezes.synthetic.evo.EvoGen;
import com.telmomenezes.synthetic.evo.EvoStrategy;
import com.telmomenezes.synthetic.io.NetFileType;


public class Evolve extends Command {

    @Override
    public boolean run(CommandLine cline) {
        // TODO: make configurable
        long maxEffort = 1000 * 1000 * 5;
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
        
        EvoDRMap2P callbacks = new EvoDRMap2P(Net.load(netfile, NetFileType.SNAP), outdir, maxEffort);
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