package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

import com.telmomenezes.synthetic.DRMap;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.io.NetFileType;


/**
 * @author telmo
 *
 */
public class EMDDist extends Command {

    @Override
    public boolean run(CommandLine cline) {
        if(!cline.hasOption("inet")) {
            setErrorMessage("input network file must be specified");
            return false;
        }
        
        if(!cline.hasOption("inet2")) {
            setErrorMessage("second input network file must be specified");
            return false;
        }
        
        String netfile1 = cline.getOptionValue("inet");
        String netfile2 = cline.getOptionValue("inet2");
        
        Net net1 = Net.load(netfile1, NetFileType.SNAP);
        Net net2 = Net.load(netfile2, NetFileType.SNAP);
  
        net1.computePageranks();
        net2.computePageranks();
        
        DRMap drmap1 = net1.getDRMapWithLimit(10, -7, 7, -7, 7);
        //drmap1.logScale();
        drmap1.normalizeMax();
        
        DRMap drmap2 = net2.getDRMapWithLimit(10, -7, 7, -7, 7);
        //drmap2.logScale();
        drmap2.normalizeMax();
        
        System.out.println("total 1: " + drmap1.total());
        System.out.println("total 2: " + drmap2.total());
        System.out.println("emd distance: " + drmap1.emdDistance(drmap2));
        
        return true;
    }

}
