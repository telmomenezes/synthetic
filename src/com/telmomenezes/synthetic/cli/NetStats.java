package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

public class NetStats {
    public static void run(CommandLine cline) {
        if(cline.hasOption("inet")) {
            System.out.println("input network file: " + cline.getOptionValue("inet"));
        }
    }
}
