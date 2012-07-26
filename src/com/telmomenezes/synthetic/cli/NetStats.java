package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;


public class NetStats extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);

        System.out.println("Stats for network '" + netfile+ "'");
        System.out.println(net);
        
        return true;
    }
}
