package com.telmomenezes.synthetic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Net {
    private static int CURID = 0;

    private double minPRIn;
    private double minPROut;
    private double maxPRIn;
    private double maxPROut;

    private Node nodes;

    private int nodeCount;
    private int edgeCount;

    private int temporal;
    private long minTS;
    private long maxTS;

    private DRMap lastMap;

    public Net() {
        nodeCount = 0;
        edgeCount = 0;
        nodes = null;
        setTemporal(0);
        minTS = 0;
        maxTS = 0;
    }

    public Node addNode() {
        nodeCount++;
        Node node = new Node(CURID++);
        node.setNext(nodes);
        nodes = node;
        return node;
    }

    public Node addNodeWithId(int nid) {
        nodeCount++;
        if (nid >= CURID) {
            CURID = nid + 1;
        }
        Node node = new Node(nid);
        node.setNext(nodes);
        nodes = node;
        return node;
    }

    public int addEdgeToNet(Node orig, Node targ, long timestamp) {
        if (orig.addEdge(targ, timestamp)) {
            edgeCount++;

            if (timestamp > 0) {
                setTemporal(1);
                if ((minTS == 0) || (timestamp < minTS)) {
                    minTS = timestamp;
                }
                if ((maxTS == 0) || (timestamp > maxTS)) {
                    maxTS = timestamp;
                }
            }

            return 1;
        }
        return 0;
    }

    Node getRandomNode() {
        int pos = RandomGenerator.instance().random.nextInt(nodeCount);
        Node curnode = nodes;
        for (int i = 0; i < pos; i++) {
            curnode = curnode.getNext();
        }
        return curnode;
    }

    DRMap getDRMap(int binNumber) {
        return getDRMapWithLimit(binNumber, minPRIn, maxPRIn, minPROut,
                maxPROut);
    }

    DRMap getDRMapWithLimit(int binNumber, double minValHor, double maxValHor,
            double minValVer, double maxValVer) {

        double inervalHor = (maxValHor - minValHor) / ((double) binNumber);
        double intervalVer = (maxValVer - minValVer) / ((double) binNumber);

        DRMap map = new DRMap(binNumber, minValHor - inervalHor, maxValHor,
                minValVer - intervalVer, maxValVer);

        Node node = nodes;
        while (node != null) {
            int x = 0;
            int y = 0;

            if ((new Double(node.getPrIn())).isInfinite()) {
                if (node.getPrIn() <= minValHor) {
                    x = 0;
                } else if (node.getPrIn() >= maxValHor) {
                    x = binNumber - 1;
                } else {
                    x = (int) Math.floor((node.getPrIn() - minValHor)
                            / inervalHor);
                }
            }
            if ((new Double(node.getPrOut())).isInfinite()) {
                if (node.getPrOut() <= minValVer) {
                    y = 0;
                } else if (node.getPrOut() >= maxValVer) {
                    y = binNumber - 1;
                } else {
                    y = (int) Math.floor((node.getPrOut() - minValVer)
                            / intervalVer);
                }
            }

            if ((x >= 0)
                    && (y >= 0)
                    && ((node.getInDegree() != 0) || (node.getOutDegree() != 0))) {
                map.incValue(x, y);
            }

            node = node.getNext();
        }

        return map;
    }

    public void computePageranks() {
        // TODO: config
        int maxIter = 10;
        double drag = 0.999;

        Node node = nodes;
        while (node != null) {
            node.setPrInLast(1);
            node.setPrOutLast(1);
            node = node.getNext();
        }

        int i = 0;

        // double delta_pr_in = 999;
        // double delta_pr_out = 999;
        // double zero_test = 0.0001;

        // while (((delta_pr_in > zero_test) || (delta_pr_out > zero_test)) &&
        // (i < max_iter)) {
        while (i < maxIter) {
            double accPRIn = 0;
            double accPROut = 0;

            node = nodes;
            while (node != null) {
                node.setPrIn(0);
                Edge origin = node.getOrigins();
                while (origin != null) {
                    node.setPrIn(node.getPrIn()
                            + origin.getOrig().getPrInLast()
                            / ((double) origin.getOrig().getOutDegree()));
                    origin = origin.getNextOrig();
                }

                node.setPrIn(node.getPrIn() * drag);
                node.setPrIn(node.getPrIn() + (1.0 - drag)
                        / ((double) nodeCount));

                accPRIn += node.getPrIn();

                node.setPrOut(0);
                Edge target = node.getTargets();
                while (target != null) {
                    node.setPrOut(node.getPrOut()
                            + target.getTarg().getPrOutLast()
                            / ((double) target.getTarg().getInDegree()));
                    target = target.getNextTarg();
                }

                node.setPrOut(node.getPrOut() * drag);
                node.setPrOut(node.getPrOut() + (1.0 - drag)
                        / ((double) nodeCount));

                accPROut += node.getPrOut();

                node = node.getNext();
            }

            // delta_pr_in = 0;
            // delta_pr_out = 0;

            node = nodes;
            while (node != null) {
                node.setPrIn(node.getPrIn() / accPRIn);
                node.setPrOut(node.getPrOut() / accPROut);
                // delta_pr_in += Math.abs(node.pr_in - node.pr_in_last);
                // delta_pr_out += Math.abs(node.pr_out - node.pr_out_last);
                node.setPrInLast(node.getPrIn());
                node.setPrOutLast(node.getPrOut());

                node = node.getNext();
            }

            i++;
        }

        // relative pr
        double basePR = 1.0 / ((double) nodeCount);
        node = nodes;
        while (node != null) {
            node.setPrIn(node.getPrIn() / basePR);
            node.setPrOut(node.getPrOut() / basePR);
            node = node.getNext();
        }

        // use log scale
        node = nodes;
        while (node != null) {
            node.setPrIn(Math.log(node.getPrIn()));
            node.setPrOut(Math.log(node.getPrOut()));
            node = node.getNext();
        }

        // compute min/max EVC in and out
        minPRIn = 0;
        minPROut = 0;
        maxPRIn = 0;
        maxPROut = 0;
        boolean first = true;
        node = nodes;
        while (node != null) {
            if ((new Double(node.getPrIn())).isInfinite()
                    && (first || (node.getPrIn() < minPRIn))) {
                minPRIn = node.getPrIn();
            }
            if ((new Double(node.getPrOut())).isInfinite()
                    && (first || (node.getPrOut() < minPROut))) {
                minPROut = node.getPrOut();
            }
            if ((new Double(node.getPrIn())).isInfinite()
                    && (first || (node.getPrIn() > maxPRIn))) {
                maxPRIn = node.getPrIn();
            }
            if ((new Double(node.getPrOut())).isInfinite()
                    && (first || (node.getPrOut() > maxPROut))) {
                maxPROut = node.getPrOut();
            }

            first = true;

            node = node.getNext();
        }
    }

    public void writePageranks(String filePath) {
        try {
            FileWriter outFile = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(outFile);

            out.println("id, pr_in, pr_out, in_degree, out_degree");

            Node node = nodes;
            while (node != null) {
                out.println(String.format("%d,%.10f,%.10f,%d,%d\n",
                        node.getId(), node.getPrIn(), node.getPrOut(),
                        node.getInDegree(), node.getOutDegree()));
                node = node.getNext();
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printNetInfo() {
        System.out.println("node number: " + nodeCount);
        System.out.println("edge number: " + edgeCount);
        System.out.println(String.format("log(pr_in): [%f, %f]\n", minPRIn,
                maxPRIn));
        System.out.println(String.format("log(pr_out): [%f, %f]\n", minPROut,
                maxPROut));
    }

    public int triadType(Node a, Node b, Node c) {
        int type = -1;

        boolean ab = a.edgeExists(b);
        boolean ac = a.edgeExists(c);
        boolean ba = b.edgeExists(a);
        boolean bc = b.edgeExists(c);
        boolean ca = c.edgeExists(a);
        boolean cb = c.edgeExists(b);

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

    void updateTriadProfile(Node[] triad, long[] profile) {
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
            System.out.println("negative type!");
            return;
        }

        profile[type - 1]++;
    }

    public boolean notInTriad(Node node, Node[] triad, int depth) {
        for (int i = 0; i <= depth; i++) {
            if (triad[i] == node)
                return false;
        }

        return true;
    }

    public void triadProfile_r(Node[] triad, int depth, long[] profile) {
        if (depth == 2) {
            updateTriadProfile(triad, profile);
            return;
        }

        Node node = triad[depth];

        Edge orig = node.getOrigins();
        while (orig != null) {
            Node next_node = orig.getOrig();
            if (next_node.isFlag() && notInTriad(next_node, triad, depth)) {
                triad[depth + 1] = next_node;
                triadProfile_r(triad, depth + 1, profile);
            }
            orig = orig.getNextOrig();
        }

        Edge targ = node.getTargets();
        while (targ != null) {
            Node next_node = targ.getTarg();
            if (next_node.isFlag() && notInTriad(next_node, triad, depth)) {
                triad[depth + 1] = next_node;
                triadProfile_r(triad, depth + 1, profile);
            }
            targ = targ.getNextTarg();
        }
    }

    public long[] triadProfile() {
        Node[] triad = new Node[3];
        long[] profile = new long[13];

        for (int i = 0; i < 13; i++)
            profile[i] = 0;

        // set all node flags to true
        Node node = nodes;
        while (node != null) {
            node.setFlag(true);
            node = node.getNext();
        }

        // search for triads starting on each node
        node = nodes;
        while (node != null) {
            triad[0] = node;
            triadProfile_r(triad, 0, profile);
            node.setFlag(false);
            node = node.getNext();
        }

        return profile;
    }

    public int[] inDegSeq() {
        int seq[] = new int[nodeCount];
        Node curnode = nodes;
        int i = 0;
        while (curnode != null) {
            seq[i] = curnode.getInDegree();
            curnode = curnode.getNext();
            i++;
        }

        return seq;
    }

    public int[] outDegSeq() {
        int seq[] = new int[nodeCount];
        Node curnode = nodes;
        int i = 0;
        while (curnode != null) {
            seq[i] = curnode.getOutDegree();
            curnode = curnode.getNext();
            i++;
        }

        return seq;
    }

    void genDegreeSeq(Net refNet) {
        int[] inDegSeq = refNet.inDegSeq();
        int[] outDegSeq = refNet.outDegSeq();

        int totalDegree = refNet.edgeCount;

        // create nodes
        Node[] newNodes = new Node[refNet.nodeCount];
        for (int i = 0; i < refNet.nodeCount; i++) {
            newNodes[i] = addNode();
        }

        // create edges
        for (int i = 0; i < refNet.edgeCount; i++) {
            int origPos = RandomGenerator.instance().random
                    .nextInt(totalDegree);
            int targPos = RandomGenerator.instance().random
                    .nextInt(totalDegree);

            int curpos = 0;
            int origIndex = -1;
            while (curpos <= origPos) {
                origIndex++;
                curpos += outDegSeq[origIndex];
            }
            outDegSeq[origIndex]--;

            curpos = 0;
            int targ_index = -1;
            while (curpos <= targPos) {
                targ_index++;
                curpos += inDegSeq[targ_index];
            }
            inDegSeq[targ_index]--;

            addEdgeToNet(newNodes[origIndex], newNodes[targ_index], 0);

            totalDegree--;
        }
    }

    double getMinPRIn() {
        return minPRIn;
    }

    void setMinPRIn(double minPRIn) {
        this.minPRIn = minPRIn;
    }

    double getMinPROut() {
        return minPROut;
    }

    void setMinPROut(double minPROut) {
        this.minPROut = minPROut;
    }

    double getMaxPRIn() {
        return maxPRIn;
    }

    void setMaxPRIn(double maxPRIn) {
        this.maxPRIn = maxPRIn;
    }

    double getMaxPROut() {
        return maxPROut;
    }

    void setMaxPROut(double maxPROut) {
        this.maxPROut = maxPROut;
    }

    Node getNodes() {
        return nodes;
    }

    void setNodes(Node nodes) {
        this.nodes = nodes;
    }

    int getNodeCount() {
        return nodeCount;
    }

    void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    int getEdgeCount() {
        return edgeCount;
    }

    void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
    }

    int getTemporal() {
        return temporal;
    }

    void setTemporal(int temporal) {
        this.temporal = temporal;
    }

    long getMinTS() {
        return minTS;
    }

    void setMinTS(long minTS) {
        this.minTS = minTS;
    }

    long getMaxTS() {
        return maxTS;
    }

    void setMaxTS(long maxTS) {
        this.maxTS = maxTS;
    }

    DRMap getLastMap() {
        return lastMap;
    }

    void setLastMap(DRMap lastMap) {
        this.lastMap = lastMap;
    }
}