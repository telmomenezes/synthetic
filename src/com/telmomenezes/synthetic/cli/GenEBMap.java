package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.EdgeBalanceMap;
import com.telmomenezes.synthetic.Net;


public class GenEBMap extends Command {

    @Override
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("mimg")) {
            setErrorMessage("file path to write EdgeBalanceMap image to must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        String outfile = cline.getOptionValue("mimg");
        
        Net net = Net.load(netfile);
        
        EdgeBalanceMap map = new EdgeBalanceMap(net, 10);
        map.logScale1();
        map.normalizeTotal();
        
        map.draw(outfile, 500);
        
        return true;
    }
}