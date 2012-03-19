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
        args = new String[]{"evo", "-inet", "wiki-Vote.txt", "-mimg", "wiki-Vote.png"};
        
        CommandLineParser parser = new GnuParser();
        options = new Options();
        options.addOption("inet", true, "input net file");
        options.addOption("mimg", true, "file path to write DRMap image to");
        
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
            else if (cmd.equals("gendrmap")) {
                cmdObj = new GenDRMap();
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
