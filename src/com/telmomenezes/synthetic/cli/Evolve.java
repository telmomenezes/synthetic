package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.evo.EvoDRMap2P;
import com.telmomenezes.synthetic.evo.EvoGen;
import com.telmomenezes.synthetic.evo.EvoStrategy;
import com.telmomenezes.synthetic.io.NetFileType;


public class Evolve extends Command {

    @Override
    public boolean run(CommandLine cline) {
        long maxEffort = 1000 * 1000 * 5;
        
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
        
        Net net = Net.load(netfile, NetFileType.SNAP);
        EvoDRMap2P callbacks = new EvoDRMap2P(net, outdir, maxEffort);
        EvoStrategy popGen = new EvoStrategy(1, 1, 1);
        EvoGen evo = new EvoGen(popGen, callbacks);
        
        System.out.println("target net: " + netfile);
        System.out.println(evo.infoString());
        
        evo.run();
        
        return true;
    }
}