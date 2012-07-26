package com.telmomenezes.synthetic.motifs;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;


public class TriadicProfile {
    
    private Net net;
    
    public TriadicProfile(Net net) {
        this.net = net;
    }
    
    private int triadType(Node a, Node b, Node c) {
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

    private void updateTriadProfile(Node[] triad, long[] profile) {
        int type = triadType(triad[0], triad[1], triad[2]);
        if (type < 0)
            type = triadType(triad[0], triad[2], triad[1]);
        if (type < 0)
            type = triadType(triad[1], triad[0], triad[2]);
        if (type < 0)
            type = triadType(triad[1], triad[2], triad[0]);
        if (type < 0)
            type = triadType(triad[2], triad[0], triad[1]);
        if (type < 0)
            type = triadType(triad[2], triad[1], triad[0]);

        if (type < 0) {
            //System.out.println("negative type!");
            return;
        }

        profile[type - 1]++;
    }

    private void triadProfileRec(Node[] triad, int depth, long[] profile) {
        if (depth == 2) {
            updateTriadProfile(triad, profile);
            return;
        }

        Node node = triad[depth];

        for (Edge orig : node.getInEdges()) {
            Node nextNode = orig.getOrigin();
            if (nextNode.getId() > triad[depth].getId()) {
                triad[depth + 1] = nextNode;
                triadProfileRec(triad, depth + 1, profile);
            }
        }

        for (Edge targ : node.getOutEdges()) {
            Node nextNode = targ.getTarget();
            if (nextNode.getId() > triad[depth].getId()) {
                triad[depth + 1] = nextNode;
                triadProfileRec(triad, depth + 1, profile);
            }
        }
    }

    public long[] triadProfile() {
        Node[] triad = new Node[3];
        long[] profile = new long[13];

        for (int i = 0; i < 13; i++)
            profile[i] = 0;

        // search for triads starting on each node
        for (Node node : net.getNodes()) {
            triad[0] = node;
            triadProfileRec(triad, 0, profile);
            node.setFlag(false);
        }

        return profile;
    }
    
    static public void main(String[] args) {
        Net net = Net.load("celegansneural.gml");
        TriadicProfile tp = new TriadicProfile(net);
        System.out.println(net);
        long[] profile = tp.triadProfile();
        
        for (long p : profile)
            System.out.print(" " + p);
        System.out.println();
    }
}
