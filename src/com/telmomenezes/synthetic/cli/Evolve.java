package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Evo;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.generators.GeneratorFactory;
import com.telmomenezes.synthetic.samplers.DownSampler;


public class Evolve extends Command {

	@Override
	public String help() {
		String help = "Evolve network generator.\n";
		help += "$ synt evo -inet <network> -odir <dir>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-gens <n> number of stable generations before search stops (default is 1000).\n";
		help += "-sr <n> sample ratio (default is 0.0006).\n";
		help += "-bins <n> distribution bins (default is 100).\n";
		help += "-tolerance <n> accepted fitness loss for shorter program (default is 0.1).\n";
		return help;
    }
	
	
    @Override
    public boolean run() throws SynCliException {
        
        String netfile = getStringParam("inet");
        String outdir = getStringParam("odir");
        int generations = getIntegerParam("gens", 1000);
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        int maxNodes = getIntegerParam("maxnodes", 999999999);
        int maxEdges = getIntegerParam("maxedges", 999999999);
        boolean directed = !paramExists("undir");
        double tolerance = getDoubleParam("tolerance", 0.1);
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        Net net = Net.load(netfile, directed, par);
        
        // down sampling if needed
        Net sampleNet = DownSampler.sample(net, maxNodes, maxEdges);
        
     	Generator baseGenerator = GeneratorFactory.create(gentype, sampleNet.getNetParams(), sr);
     	
        Evo evo = new Evo(sampleNet, generations, bins, tolerance, baseGenerator, outdir);
        
        System.out.println("target net: " + netfile);
        System.out.println(evo.infoString());
        System.out.println(baseGenerator);
        
        // write experiment params to file
        try {
            FileWriter fstream = new FileWriter(outdir + "/params.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(evo.infoString());
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        evo.run();
        
        return true;
    }
}