package com.telmomenezes.synthetic.cli;


import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;


public class Convert extends Command {
    
	@Override
	public String help() {
		String help = "Converts network to a simple format.\n";
		help += "$ synt convert -inet <in_network> -onet <out_network>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		return help;
    }
	
	
	@Override
	public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String outfile = getStringParam("onet");
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        
        Net net = Net.load(netfile, directed, par);
        System.out.println(net);
        
        net.save(outfile, NetFileType.SNAP);
        
        System.out.println("Done.");
        
        return true;
    }
}
