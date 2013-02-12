package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;


public class Convert extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("onet")) {
            setErrorMessage("output network file must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);
        
        String outfile = cline.getOptionValue("onet");

        net.save(outfile, NetFileType.SNAP);
        
        System.out.println(net);
        System.out.println("Done.");
        
        return true;
    }
}
