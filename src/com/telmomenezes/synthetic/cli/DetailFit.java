package com.telmomenezes.synthetic.cli;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.generators.GeneratorFactory;


public class DetailFit extends Command {
	
	@Override
	public String name() {return "detailfit";}
	
	
	@Override
	public String help() {
		String help = "Computes fitness and detailed metrics for a set of generators.\n";
		help += "$ synt detailfit -inet <network> -dir <dir> -out <csv_file>\n";
		help += "Optional parameters:\n";
		help += "-undir if network is undirected.\n";
		help += "-sr <n> sample ratio (default is 0.0006).\n";
		help += "-bins <n> distribution bins (default is 100).\n";
		help += "-runs <n> number of runs per program (default is 30).\n";
		return help;
    }
	
	
	@Override
    public boolean run() throws SynCliException {
        String netfile = getStringParam("inet");
        String dir = getStringParam("dir");
        String outFile = getStringParam("out");
        double sr = getDoubleParam("sr", 0.0006);
        int bins = getIntegerParam("bins", 100);
        int runs = getIntegerParam("runs", 30);
        boolean directed = !paramExists("undir");
        boolean par = paramExists("par");
        String gentype = getStringParam("gentype", "exo");
        
        Net net = Net.load(netfile, directed, par);
        
        MetricsBag targBag = new MetricsBag(net, bins);
        
        List<String> prgFiles = textFiles(dir);

        try {
            FileWriter fstream = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fstream);

            String str = "prog";
            str += ",fit";
            str += ",program_size";

            // absolute
            if (directed) {
                str += ",in_degrees_dist";
                str += ",out_degrees_dist";
                str += ",d_pageranks_dist";
            }
            else {
                str += ",degrees_dist";
            }
            str += ",u_pageranks_dist";
            str += ",triadic_profile_dist";
            if (directed) {
                str += ",d_dists_dist";
            }
            str += ",u_dists_dist";
            
            // relative
            if (directed) {
                str += ",rel_in_degrees_dist";
                str += ",rel_out_degrees_dist";
                str += ",rel_d_pageranks_dist";
            }
            else {
                str += ",rel_degrees_dist";
            }
            str += ",rel_u_pageranks_dist";
            str += ",rel_triadic_profile_dist";
            if (directed) {
                str += ",rel_d_dists_dist";
            }
            str += ",rel_u_dists_dist";

            out.write(str + "\n");

            for (String progFile : prgFiles) {
            	System.out.println("processing " + progFile);
            	
                double fit = 0;
                double degreesDist = 0;
                double inDegreesDist = 0;
                double outDegreesDist = 0;
                double dPageRanksDist = 0;
                double uPageRanksDist = 0;
                double triadicProfileDist = 0;
                double dDistsDist = 0;
                double uDistsDist = 0;
                double relDegreesDist = 0;
                double relInDegreesDist = 0;
                double relOutDegreesDist = 0;
                double relDPageRanksDist = 0;
                double relUPageRanksDist = 0;
                double relTriadicProfileDist = 0;
                double relDDistsDist = 0;
                double relUDistsDist = 0;

                for (int i = 0; i < runs; i++) {
                    Generator gen = GeneratorFactory.create(gentype, net.getNetParams(), sr);
                    gen.load(dir + "/" + progFile);
                    gen.run();

                    gen.computeFitness(targBag, bins);
                    fit += gen.fitnessMax;
                    MetricsBag bag = gen.getGenBag();

                    degreesDist += bag.getDegreesDist();
                    inDegreesDist += bag.getInDegreesDist();
                    outDegreesDist += bag.getOutDegreesDist();
                    dPageRanksDist += bag.getDPageRanksDist();
                    uPageRanksDist += bag.getUPageRanksDist();
                    triadicProfileDist += bag.getTriadicProfileDist();
                    dDistsDist += bag.getdDistsDist();
                    uDistsDist += bag.getuDistsDist();
                    
                    relDegreesDist += bag.getRelDegreesDist();
                    relInDegreesDist += bag.getRelInDegreesDist();
                    relOutDegreesDist += bag.getRelOutDegreesDist();
                    relDPageRanksDist += bag.getRelDPageRanksDist();
                    relUPageRanksDist += bag.getRelUPageRanksDist();
                    relTriadicProfileDist += bag.getRelTriadicProfileDist();
                    relDDistsDist += bag.getRelDDistsDist();
                    relUDistsDist += bag.getRelUDistsDist();
                }

                // determine prunned program size
                Generator gen = GeneratorFactory.create(gentype, net.getNetParams(), sr);
                gen.load(dir + "/" + progFile);
                gen.run();
                gen.getProg().dynPruning();

                fit /= runs;
                degreesDist /= runs;
                inDegreesDist /= runs;
                outDegreesDist /= runs;
                dPageRanksDist /= runs;
                uPageRanksDist /= runs;
                triadicProfileDist /= runs;
                dDistsDist /= runs;
                uDistsDist /= runs;
                relDegreesDist /= runs;
                relInDegreesDist /= runs;
                relOutDegreesDist /= runs;
                relDPageRanksDist /= runs;
                relUPageRanksDist /= runs;
                relTriadicProfileDist /= runs;
                relDDistsDist /= runs;
                relUDistsDist /= runs;

                str = progFile.split("\\.")[0];
                str += "," + fit;
                str += "," + gen.getProg().size();

                // absolute
                if (directed) {
                    str += "," + inDegreesDist;
                    str += "," + outDegreesDist;
                    str += "," + dPageRanksDist;
                }
                else {
                    str += "," + degreesDist;
                }
                str += "," + uPageRanksDist;
                str += "," + triadicProfileDist;
                if (directed) {
                    str += "," + dDistsDist;
                }
                str += "," + uDistsDist;
                
                // relative
                if (directed) {
                    str += "," + relInDegreesDist;
                    str += "," + relOutDegreesDist;
                    str += "," + relDPageRanksDist;
                }
                else {
                    str += "," + relDegreesDist;
                }
                str += "," + relUPageRanksDist;
                str += "," + relTriadicProfileDist;
                if (directed) {
                    str += "," + relDDistsDist;
                }
                str += "," + relUDistsDist;

                out.write(str + "\n");
            }
            
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("done.");

        return true;
    }
}