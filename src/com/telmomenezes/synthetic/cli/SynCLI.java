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
    	args = new String[]{"evo", "-inet", "celegansneural.gml", "-odir", "test"};//, "-gentype", "full"};
    	//args = new String[]{"evo", "-inet", "ownership.txt", "-odir", "test"};
    	
        CommandLineParser parser = new GnuParser();
        options = new Options();
        options.addOption("inet", true, "input net file");
        options.addOption("inet2", true, "second input net file");
        options.addOption("odir", true, "output directory");
        options.addOption("prg", true, "generator program file");
        options.addOption("oprg", true, "generator output program file");
        
        options.addOption("gentype", true, "generator type (FAST / full)");
        options.addOption("gens", true, "number of generations");
        options.addOption("bins", true, "number of distribution bins");
        options.addOption("maxnodes", true, "max nodes (sampling)");
        options.addOption("maxedges", true, "max edges (sampling)");
        
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