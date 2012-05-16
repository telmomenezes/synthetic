package com.telmomenezes.synthetic.motifs;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;

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
        int count = 0;
        for (Node node : net.getNodes()) {
            triad[0] = node;
            triadProfileRec(triad, 0, profile);
            System.out.println("#" + count++);
            for (long p : profile)
                System.out.print(" " + p);
            System.out.println();
            node.setFlag(false);
        }

        return profile;
    }
    
    public long[] sampleTriadProfile() {
        Node[] triad = new Node[3];
        long[] profile = new long[13];

        for (int i = 0; i < 13; i++)
            profile[i] = 0;

        // search for triads starting on each node
        int count = 0;
        while (count < 1000000) {
            int node1 = RandomGenerator.instance().random.nextInt(net.getNodeCount());
            int node2 = RandomGenerator.instance().random.nextInt(net.getNodeCount());
            if (node1 == node2)
                continue;
            int node3 = RandomGenerator.instance().random.nextInt(net.getNodeCount());
            if ((node3 == node1) || (node3 == node2))
                continue;
            triad[0] = net.getNodes().get(node1);
            triad[1] = net.getNodes().get(node2);
            triad[2] = net.getNodes().get(node3);
            updateTriadProfile(triad, profile);
            count++;
        }

        return profile;
    }
}
