package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.Net;


public class EvalStats extends Command {
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        if(!cline.hasOption("oprg")) {
            setErrorMessage("output program file must be specified");
            return false;
        }
        if(!cline.hasOption("prg")) {
            setErrorMessage("program file must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);

        String progFile = cline.getOptionValue("prg");   
        String outProg = cline.getOptionValue("oprg");
     
     	int trials = 50;
     	if(cline.hasOption("trials")) {
            trials = new Integer(cline.getOptionValue("trials"));
        }
        
        boolean directed = true;
        
        System.out.println(net);
        
        Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, trials);
        gen.load(progFile);
        gen.run();
        
        gen.getProg().write(outProg, true);
    	
        System.out.println("done.");
        
        return true;
    }
}
