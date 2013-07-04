package com.telmomenezes.synthetic.cli;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class SynCLI {
	private static String version = "1.0";
	
    private Options options;
    private CommandLine cline;
    
    Map<String, Command> commands;
    
    
    public SynCLI() {
    	commands = new HashMap<String, Command>();
    	addCommand(new NetStats());
    	addCommand(new Evolve());
    	addCommand(new Random());
    	addCommand(new Convert());
    	addCommand(new Run());
    	addCommand(new Compare());
    	addCommand(new Prune());
    	addCommand(new Fit());
    	addCommand(new CompFit());
    	addCommand(new DetailFit());
    	addCommand(new Gen());
    	addCommand(new Dists());
    }
    
    
    private void addCommand(Command cmd) {
    	commands.put(cmd.name(), cmd);
    }
    
    
    private void printHelpMessage() {
    	System.err.println("\nSynthetic ver " + version);
    	
    	System.err.print("use one of the commands: ");
    	boolean first = true;
    	for (String k : commands.keySet()) {
    		if (first) {
    			first = false;
    		}
    		else {
    			System.err.print(", ");
    		}
    		System.err.print(k);
    	}
    	System.err.println("");
    	System.err.println("\nUse 'synt help <command>' to get help on a specific command.\n");
    	System.exit(0);
    }
    
    private void printErrorMessage(String msg, boolean help) {
    	System.err.println("");
    	if (msg == null) {
    		System.err.println("unkown error.");
    	}
    	else {
    		System.err.println(msg);
    	}
    	
    	if (help) {
    		printHelpMessage();
    	}
    	else {
    		System.err.println("");
    		System.exit(0);
    	}
    }
    
    private void printErrorMessage(String msg) {
    	printErrorMessage(msg, false);
    }
    
    public void run(String[] args) {
    	
    	if (args.length == 0) {
    		printErrorMessage("no command given.", true);
    	}

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
        options.addOption("runs", true, "number of generator runs");
        options.addOption("undir", false, "undirected network");
        options.addOption("tolerance", true, "antibloat tolerance");
        options.addOption("nodes", true, "number of nodes");
        options.addOption("edges", true, "number of edges");
        options.addOption("mean", false, "compute mean");
        options.addOption("par", false, "parallel edges allowed");
        options.addOption("gentype", true, "generator type");
        
        try {
            cline = parser.parse(options, args);

            String cmd = args[0];
            Command cmdObj = null;
            
            if (cmd.equals("help")) {
            	if (args.length == 1) {
            		printHelpMessage();
            	}
            	else {
            		System.err.println("");
            		String cmdHelp = args[1];
            		if (!commands.containsKey(cmdHelp)) {
                        printErrorMessage("Command '" + cmdHelp + "' does not exist.", true);
                    }
            		System.err.println(commands.get(cmdHelp).help());
            		System.exit(0);
            	}
            }
            
            if (!commands.containsKey(cmd)) {
                printErrorMessage("Command '" + cmd + "' does not exist.", true);
            }
            
            cmdObj = commands.get(cmd);
            cmdObj.setCline(cline);
            if (!cmdObj.run()) {
                printErrorMessage(cmdObj.getErrorMessage());
            }
        }
        catch (ParseException e) {
           printErrorMessage(e.getMessage());
        }
        catch (SynCliException e) {
            printErrorMessage(e.getMessage());
         }
        
        System.exit(0);
    }
    
    public static void main(String[] args) {
        (new SynCLI()).run(args);
    }

}