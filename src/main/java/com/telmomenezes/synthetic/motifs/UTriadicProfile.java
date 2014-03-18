package com.telmomenezes.synthetic.motifs;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;


public class UTriadicProfile extends TriadicProfile {
    
    public UTriadicProfile(Net net) {
        super(net);
        numberOfMotifs = 2;
        triadProfile();
    }
    
    @Override
    protected int triadType(Node a, Node b, Node c) {
        int type = -1;

        boolean ab = net.edgeExists(a, b);
        boolean ac = net.edgeExists(a, c);
        boolean bc = net.edgeExists(b, c);

        if (ab && ac && !bc)
            type = 1;
        else if (ab && ac && bc)
            type = 2;

        return type;
    }
    
    
    @Override
    protected double motifEdges(int motif) {
    	switch (motif) {
    	case 1:
    		return 2;
    	case 2:
    		return 3;
    	default:
    		return 0;
    	}
    }
}