package com.telmomenezes.synthetic.cli;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.NetParams;
import com.telmomenezes.synthetic.io.NetFileType;


public class Gen extends Command {
    
	@Override
	public String name() {return "gen";}
	
	
	@Override
	public String help() {
		String help = "Generates network.\n";
		help += "$ synt gen -prg <generator> -onet <network>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-nodes <n> number of nodes (default is 1000).\n";
		help += "-edges <n> number or edges (default is 10000).\n";
		help += "-sr <n> sample ratio (default is 0.0006).\n";
		return help;
    }
	
	
	@Override
	public boolean run() throws SynCliException {
    	String netfile = getStringParam("inet", "");
    	int nodes = getIntegerParam("nodes", 1000);
    	int edges = getIntegerParam("edges", 10000);
    	
        String outNet = getStringParam("onet");
        String progFile = getStringParam("prg");
        double sr = getDoubleParam("sr", 0.0006);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        if (!netfile.equals("")) {
        	Net net = Net.load(netfile, directed, par);
        	nodes = net.getNodeCount();
        	edges = net.getEdgeCount();
        }
        System.out.println("nodes: " + nodes);
        System.out.println("edges: " + edges);
        
        NetParams netParams = new NetParams(nodes, edges, directed, par);
        Generator gen = GeneratorFactory.create(gentype, netParams, sr);
        if (gen == null) {
            System.err.println("could not load program.");
            return false;
        }
        gen.load(progFile);
        
        gen.run();
        Net syntNet = gen.getNet();
        
        // write net
        syntNet.save(outNet, NetFileType.SNAP);
    	
        System.out.println("done.");
        
        return true;
    }
}