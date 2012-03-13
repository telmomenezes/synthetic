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
        args = new String[]{"netstats", "-inet", "hello.txt"};
        
        CommandLineParser parser = new GnuParser();
        options = new Options();
        options.addOption("inet", true, "input net file");
        
        try {
            cline = parser.parse(options, args);

            String tool = args[0];
            
            if (tool == "netstats") {
                NetStats.run(cline);
            }
            else {
                printErrorMessage("Command '" + tool + "' does not exist.");
            }
        }
        catch (ParseException e) {
           String msg = e.getMessage();
           if (msg == null) {
               msg = "unkown error";
           }
           printErrorMessage(msg);
        }
    }
    
    public static void main(String[] args) {
        (new SynCLI()).run(args);
    }

}
