package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Compare extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("inet2")) {
            setErrorMessage("sconde input network file must be specified");
            return false;
        }
        
        String netfile1 = cline.getOptionValue("inet");
        Net net1 = Net.load(netfile1);
        
        String netfile2 = cline.getOptionValue("inet2");
        Net net2 = Net.load(netfile2);
        
        int bins = 100;
     	if(cline.hasOption("bins")) {
            bins = new Integer(cline.getOptionValue("bins"));
        }
        
     	System.out.println("NET: " + netfile1);
     	System.out.println(net1);
        MetricsBag bag1 = new MetricsBag(net1, bins);
        System.out.println(bag1);
        
        System.out.println("\n\n");
        
        System.out.println("NET: " + netfile2);
        System.out.println(net2);
        MetricsBag bag2 = new MetricsBag(net2, null, null, bins, bag1);
        System.out.println(bag2);
        
        return true;
    }
}
