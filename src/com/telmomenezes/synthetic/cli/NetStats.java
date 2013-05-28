package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.Net;


public class NetStats extends Command {
    public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet");
    	boolean directed = !paramExists("undir");
    	boolean par = paramExists("par");

        Net net = Net.load(netfile, directed, par);

        System.out.println("Stats for network '" + netfile+ "'");
        System.out.println(net);
        
        return true;
    }
}
