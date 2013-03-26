package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Compare extends Command {
    public boolean run() throws SynCliException {
        String netfile1 = getStringParam("inet");
        String netfile2 = getStringParam("inet2");
        int bins = getIntegerParam("bins", 100);
        boolean directed = !paramExists("undir");
        
        Net net1 = Net.load(netfile1, directed);
        Net net2 = Net.load(netfile2, directed);
        
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
