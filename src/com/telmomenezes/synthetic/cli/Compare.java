package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class Compare extends Command {
	
	@Override
	public String name() {return "compare";}
	
	
	@Override
	public String help() {
		String help = "Compares two networks.\n";
		help += "$ synt compare -inet <network1> -inet2 <network2>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-bins <n> distribution bins (default is 100).\n";
		return help;
    }
	
	
	@Override
    public boolean run() throws SynCliException {
        String netfile1 = getStringParam("inet");
        String netfile2 = getStringParam("inet2");
        int bins = getIntegerParam("bins", 100);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        
        Net net1 = Net.load(netfile1, directed, par);
        Net net2 = Net.load(netfile2, directed, par);
        
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
