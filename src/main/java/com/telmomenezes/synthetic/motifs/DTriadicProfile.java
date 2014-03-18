package com.telmomenezes.synthetic.motifs;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;


public class DTriadicProfile extends TriadicProfile {
    
    public DTriadicProfile(Net net) {
        super(net);
        numberOfMotifs = 13;
        triadProfile();
    }
    
    @Override
    protected int triadType(Node a, Node b, Node c) {
        int type = -1;

        boolean ab = net.edgeExists(a, b);
        boolean ac = net.edgeExists(a, c);
        boolean ba = net.edgeExists(b, a);
        boolean bc = net.edgeExists(b, c);
        boolean ca = net.edgeExists(c, a);
        boolean cb = net.edgeExists(c, b);

        if (ab && ac && !ba && !bc && !ca && !cb)
            type = 1;
        else if (!ab && !ac && ba && !bc && ca && !cb)
            type = 2;
        else if (!ab && !ac && !ba && bc && ca && !cb)
            type = 3;
        else if (!ab && ac && ba && !bc && ca && !cb)
            type = 4;
        else if (ab && ac && ba && !bc && !ca && !cb)
            type = 5;
        else if (ab && ac && ba && !bc && ca && !cb)
            type = 6;
        else if (ab && ac && !ba && bc && !ca && !cb)
            type = 7;
        else if (!ab && ac && ba && !bc && !ca && cb)
            type = 8;
        else if (ab && ac && !ba && bc && !ca && cb)
            type = 9;
        else if (!ab && !ac && ba && bc && ca && cb)
            type = 10;
        else if (ab && ac && !ba && bc && ca && !cb)
            type = 11;
        else if (!ab && ac && ba && bc && ca && cb)
            type = 12;
        else if (ab && ac && ba && bc && ca && cb)
            type = 13;

        return type;
    }
    
    
    @Override
    protected double motifEdges(int motif) {
    	switch (motif) {
    	case 1:
    		return 2;
    	case 2:
    		return 2;
    	case 3:
    		return 2;
    	case 4:
    		return 3;
    	case 5:
    		return 3;
    	case 6:
    		return 4;
    	case 7:
    		return 3;
    	case 8:
    		return 3;
    	case 9:
    		return 4;
    	case 10:
    		return 4;
    	case 11:
    		return 4;
    	case 12:
    		return 5;
    	case 13:
    		return 6;
    	default:
    		return 0;
    	}
    }
}