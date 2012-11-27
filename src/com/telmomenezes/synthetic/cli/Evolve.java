package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.evo.EvoGen;


public class Evolve extends Command {

    @Override
    public boolean run(CommandLine cline) {
        // TODO: make configurable
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
        EvoGen evo = new EvoGen(generations, net, outdir);
        
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