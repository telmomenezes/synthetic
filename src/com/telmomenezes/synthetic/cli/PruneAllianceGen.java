package com.telmomenezes.synthetic.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.kinship.AllianceGen;
import com.telmomenezes.synthetic.kinship.TopologicalIndices;


public class PruneAllianceGen extends Command {

    @Override
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("prg")) {
            setErrorMessage("generator program file must be specified");
            return false;
        }
        
        if(!cline.hasOption("oprg")) {
            setErrorMessage("generator output program file must be specified");
            return false;
        }
        
        String netfile = cline.getOptionValue("inet");
        String prgFile = cline.getOptionValue("prg");
        String oprgFile = cline.getOptionValue("oprg");
        
        System.out.println("target net: " + netfile);
        
        Net net = Net.load(netfile, NetFileType.MAT);
        AllianceGen gen = new AllianceGen(net.getNodeCount(), net.getEdgeCount(), new TopologicalIndices(net));
        try {
            gen.loadProgs(prgFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        gen.run();
        gen.dynPruning();
        
        try {
            gen.writeProgs(oprgFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return true;
    }
}