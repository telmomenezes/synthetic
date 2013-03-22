/**
 * 
 */
package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author telmo
 *
 */
public class SynCLI {
    private Options options;
    private CommandLine cline;
    
    private void printHelpMessage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("syn", options);
    }
    
    private void printErrorMessage(String msg) {
        System.err.println(msg);
        printHelpMessage();
    }
    
    public void run(String[] args) {
        //args = new String[]{"evo", "-inet", "ownership.txt", "-odir", "test"};
    	//args = new String[]{"evo", "-inet", "/Users/telmo/Desktop/Networks/polblogs/polblogs.gml", "-odir", "test"};
    	//args = new String[]{"evo", "-inet", "celegansneural.gml", "-odir", "test"};
    	//args = new String[]{"evo", "-inet", "wiki-Vote.snap", "-odir", "test", "-maxnodes", "100"};//, "-gentype", "full"};
    	//args = new String[]{"netstats", "-inet", "polblogs.gml"};
    	//args = new String[]{"run", "-inet", "celegansneural.gml", "-prg", "prog.txt", "-odir", "test", "-runs", "30"};
    	//args = new String[]{"evo", "-inet", "syntnet.txt", "-odir", "test"};
    	//args = new String[]{"compare", "-inet", "syntnet0.txt", "-inet2", "syntnet1.txt"};
    	//args = new String[]{"evo", "-inet", "Hsapi20120818CR.mitab", "-odir", "test", "-undir"};
    	//args = new String[]{"evo", "-inet", "power.gml", "-odir", "test", "-undir"};
    	//args = new String[]{"comprun", "-inet", "celegansneural.gml", "-dir", "/Users/telmo/amazon_syn6/celegansprogs", "-out", "distances.csv"};
    	//args = new String[]{"comprun", "-inet", "polblogs.gml", "-dir", "/Users/telmo/amazon_syn6/polblogsprogs"};
    	//args = new String[]{"fit", "-inet", "celegansneural.gml", "-prg", "/Users/telmo/amazon_syn6/celegans6/bestprog.txt", "-runs", "30"};
    	
        CommandLineParser parser = new GnuParser();
        options = new Options();
        options.addOption("inet", true, "input net file");
        options.addOption("inet2", true, "second input net file");
        options.addOption("onet", true, "output net file");
        options.addOption("dir", true, "directory");
        options.addOption("odir", true, "output directory");
        options.addOption("prg", true, "generator program file");
        options.addOption("prg2", true, "second generator program file");
        options.addOption("oprg", true, "generator output program file");
        options.addOption("out", true, "output file");
        
        options.addOption("gens", true, "number of generations");
        options.addOption("bins", true, "number of distribution bins");
        options.addOption("maxnodes", true, "max nodes (sampling)");
        options.addOption("maxedges", true, "max edges (sampling)");
        options.addOption("trials", true, "number of trials for the fast generator");
        options.addOption("antibloat", true, "anti bloat (ON / off)");
        options.addOption("runs", true, "number of generator runs");
        options.addOption("undir", false, "undirected network");
        
        try {
            cline = parser.parse(options, args);

            String cmd = args[0];
            Command cmdObj = null;
            
            if (cmd.equals("help")) {
                printHelpMessage();
            }
            else if (cmd.equals("netstats")) {
                cmdObj = new NetStats();
            }
            else if (cmd.equals("evo")) {
                cmdObj = new Evolve();
            }
            else if (cmd.equals("random")) {
                cmdObj = new Random();
            }
            else if (cmd.equals("convert")) {
                cmdObj = new Convert();
            }
            else if (cmd.equals("run")) {
                cmdObj = new Run();
            }
            else if (cmd.equals("compare")) {
                cmdObj = new Compare();
            }
            else if (cmd.equals("prune")) {
                cmdObj = new Prune();
            }
            else if (cmd.equals("evalstats")) {
                cmdObj = new EvalStats();
            }
            else if (cmd.equals("comprun")) {
                cmdObj = new CompRun();
            }
            else if (cmd.equals("fit")) {
                cmdObj = new Fit();
            }
            else {
                printErrorMessage("Command '" + cmd + "' does not exist.");
            }
            
            if (cmdObj != null) {
                if (!cmdObj.run(cline)) {
                    printErrorMessage(cmdObj.getErrorMessage());
                }
            }
        }
        catch (ParseException e) {
           String msg = e.getMessage();
           if (msg == null) {
               msg = "unkown error";
           }
           printErrorMessage(msg);
        }
        
        System.exit(0);
    }
    
    public static void main(String[] args) {
        (new SynCLI()).run(args);
    }

}