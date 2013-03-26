package com.telmomenezes.synthetic.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Generator;
import com.telmomenezes.synthetic.MetricsBag;
import com.telmomenezes.synthetic.Net;


public class CompFit extends Command {
	
	private List<String> textFiles(String directory) {
		List<String> textFiles = new ArrayList<String>();
		File dir = new File(directory);
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith((".txt"))) {
				textFiles.add(file.getName());
		    }
		}
		
		return textFiles;
	}
	
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        if(!cline.hasOption("dir")) {
            setErrorMessage("directory must be specified");
            return false;
        }
        if(!cline.hasOption("out")) {
            setErrorMessage("out file must be specified");
            return false;
        }
        
        
        String netfile = cline.getOptionValue("inet");
        Net net = Net.load(netfile);

        String dir = cline.getOptionValue("dir");
        
        String outFile = cline.getOptionValue("out");
     	
     	int trials = 50;
     	if(cline.hasOption("trials")) {
            trials = new Integer(cline.getOptionValue("trials"));
        }
        
     	int bins = 100;
     	if(cline.hasOption("bins")) {
            bins = new Integer(cline.getOptionValue("bins"));
        }
     	
     	int runs = 1;
     	if(cline.hasOption("runs")) {
            runs = new Integer(cline.getOptionValue("runs"));
        }
     	
        boolean directed = true;
        
        System.out.println(net);
        MetricsBag targBag = new MetricsBag(net, bins);
        
        List<String> prgFiles = textFiles(dir);
        
        try{
        	FileWriter fstream = new FileWriter(outFile);
        	BufferedWriter out = new BufferedWriter(fstream);
        
        	out.write("prog,fit\n");
    	
        	for (String progFile : prgFiles) {
        		System.out.println("-> " + progFile);
        		for (int i = 0; i < runs; i++) {
        			System.out.println("run #" + i);
            	
        			Generator gen = new Generator(net.getNodeCount(), net.getEdgeCount(), directed, trials);
        			gen.load(dir + "/" + progFile);
        			gen.run();
            	
        			double fit = gen.computeFitness(targBag, bins, false);
            	
        			String[] tokens = progFile.split("\\.");
        			out.write(tokens[0] + "," + fit + "\n");
        		}
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