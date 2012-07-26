package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.DRMap;
import com.telmomenezes.synthetic.Net;

public class GenDRMap extends Command {

    @Override
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("mimg")) {
            setErrorMessage("file path to write DRMap image to must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        String outfile = cline.getOptionValue("mimg");
        
        Net net = Net.load(netfile);
  
        net.computePageranks();
        
        DRMap drmap = net.getDRMapWithLimit(10, -7, 7, -7, 7);
        drmap.logScale();
        drmap.normalizeMax();
        
        System.out.println(drmap);
        //System.out.println(drmap.cArray());
        drmap.draw(outfile, 500);
        
        return true;
    }

}
