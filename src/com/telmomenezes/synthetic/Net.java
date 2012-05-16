package com.telmomenezes.synthetic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.telmomenezes.synthetic.io.MatrixFile;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.io.SNAPNetFile;


public class Net {
    private static int CURID = 0;

    private double minPRIn;
    private double minPROut;
    private double maxPRIn;
    private double maxPROut;

    protected Vector<Node> nodes;
    protected Vector<Edge> edges;

    private int nodeCount;
    private int edgeCount;

    private boolean selfEdges;
    
    private DRMap lastMap;

    public Net() {
        nodeCount = 0;
        edgeCount = 0;
        nodes = new Vector<Node>();
        edges = new Vector<Edge>();
        selfEdges = false;
    }
    
    public Net(boolean selfEdges) {
        this();
        this.selfEdges = selfEdges;
    }
    
    public static Net load(String filePath, NetFileType fileType) {
        Net net = null;
        switch (fileType) {
        case SNAP:
            net = (new SNAPNetFile()).load(filePath);
            return net;
        case MAT:
            net = (new MatrixFile()).load(filePath);
            return net;
        default:
            return net;
        }
    }

    public void save(String filePath, NetFileType fileType) {
        switch (fileType) {
        case SNAP:
            (new SNAPNetFile()).save(this, filePath);
            break;
        default:
            break;
        }
    }
    
    public Node addNode() {
        nodeCount++;
        Node node = new Node(CURID++);
        nodes.add(node);
        return node;
    }

    public Node addNodeWithId(int nid) {
        nodeCount++;
        if (nid >= CURID) {
            CURID = nid + 1;
        }
        Node node = new Node(nid);
        nodes.add(node);
        return node;
    }
    
    public boolean addEdge(Node origin, Node target, double weight, long timestamp) {
        if ((!selfEdges) && (origin == target)) {
            return false;
        }

        if (edgeExists(origin, target)) {
            return false;
        }

        Edge edge = new Edge(origin, target, weight, timestamp);
        edges.add(edge);
        origin.addOutEdge(edge);
        target.addInEdge(edge);

        edgeCount++;
        
        return true;
    }
    
    public boolean addEdge(Node origin, Node target) {
        return addEdge(origin, target, 0.0, 0l);
    }
    
    public boolean addEdge(Node origin, Node target, double weight) {
        return addEdge(origin, target, weight, 0l);
    }
    
    public boolean addEdge(Node origin, Node target, long timestamp) {
        return addEdge(origin, target, 0.0, timestamp);
    }

    public boolean edgeExists(Node origin, Node target) {
        for (Edge edge : origin.getOutEdges()) {
            if (edge.getTarget() == target) {
                return true;
            }
        }

        return false;
    }
    
    public Edge getEdge(Node origin, Node target) {
        for (Edge edge : origin.getOutEdges()) {
            if (edge.getTarget() == target) {
                return edge;
            }
        }

        return null;
    }
    
    public Edge getInverseEdge(Edge edge) {
        return getEdge(edge.getTarget(), edge.getOrigin());
    }

    Node getRandomNode() {
        int pos = RandomGenerator.instance().random.nextInt(nodeCount);
        return nodes.get(pos);
    }

    public DRMap getDRMap(int binNumber) {
        return getDRMapWithLimit(binNumber, minPRIn, maxPRIn, minPROut,
                maxPROut);
    }

    public DRMap getDRMapWithLimit(int binNumber, double minValHor, double maxValHor,
            double minValVer, double maxValVer) {

        double inervalHor = (maxValHor - minValHor) / ((double) binNumber);
        double intervalVer = (maxValVer - minValVer) / ((double) binNumber);

        DRMap map = new DRMap(binNumber, minValHor - inervalHor, maxValHor,
                minValVer - intervalVer, maxValVer);

        for (Node node : nodes) {
            int x = 0;
            int y = 0;
            
            if (!(new Double(node.getPrIn())).isInfinite()) {
                if (node.getPrIn() <= minValHor) {
                    x = 0;
                } else if (node.getPrIn() >= maxValHor) {
                    x = binNumber - 1;
                } else {
                    x = (int) Math.floor((node.getPrIn() - minValHor)
                            / inervalHor);
                }
            }
            if (!(new Double(node.getPrOut())).isInfinite()) {
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
        }

        return map;
    }

    public void computePageranks() {
        // TODO: config
        int maxIter = 10;
        double drag = 0.999;

        for (Node node : nodes) {
            node.setPrInLast(1);
            node.setPrOutLast(1);
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

            for (Node node : nodes) {
                node.setPrIn(0);
                for (Edge origin : node.getInEdges()) {
                    node.setPrIn(node.getPrIn()
                            + origin.getOrigin().getPrInLast()
                            / ((double) origin.getOrigin().getOutDegree()));
                }

                node.setPrIn(node.getPrIn() * drag);
                node.setPrIn(node.getPrIn() + (1.0 - drag)
                        / ((double) nodeCount));

                accPRIn += node.getPrIn();

                node.setPrOut(0);
                for (Edge target : node.getOutEdges()) {
                    node.setPrOut(node.getPrOut()
                            + target.getTarget().getPrOutLast()
                            / ((double) target.getTarget().getInDegree()));
                }

                node.setPrOut(node.getPrOut() * drag);
                node.setPrOut(node.getPrOut() + (1.0 - drag)
                        / ((double) nodeCount));

                accPROut += node.getPrOut();
            }

            // delta_pr_in = 0;
            // delta_pr_out = 0;

            for (Node node : nodes) {
                node.setPrIn(node.getPrIn() / accPRIn);
                node.setPrOut(node.getPrOut() / accPROut);
                // delta_pr_in += Math.abs(node.pr_in - node.pr_in_last);
                // delta_pr_out += Math.abs(node.pr_out - node.pr_out_last);
                node.setPrInLast(node.getPrIn());
                node.setPrOutLast(node.getPrOut());
            }

            i++;
        }

        // relative pr
        double basePR = 1.0 / ((double) nodeCount);
        for (Node node : nodes) {
            node.setPrIn(node.getPrIn() / basePR);
            node.setPrOut(node.getPrOut() / basePR);
        }

        // use log scale
        for (Node node : nodes) {
            node.setPrIn(Math.log(node.getPrIn()));
            node.setPrOut(Math.log(node.getPrOut()));
        }

        // compute min/max EVC in and out
        minPRIn = 0;
        minPROut = 0;
        maxPRIn = 0;
        maxPROut = 0;
        boolean first = true;
        for (Node node : nodes) {
            if ((!(new Double(node.getPrIn())).isInfinite())
                    && (first || (node.getPrIn() < minPRIn))) {
                minPRIn = node.getPrIn();
            }
            if ((!(new Double(node.getPrOut())).isInfinite())
                    && (first || (node.getPrOut() < minPROut))) {
                minPROut = node.getPrOut();
            }
            if ((!(new Double(node.getPrIn())).isInfinite())
                    && (first || (node.getPrIn() > maxPRIn))) {
                maxPRIn = node.getPrIn();
            }
            if ((!(new Double(node.getPrOut())).isInfinite())
                    && (first || (node.getPrOut() > maxPROut))) {
                maxPROut = node.getPrOut();
            }

            first = false;
        }
    }

    public void writePageranks(String filePath) {
        try {
            FileWriter outFile = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(outFile);

            out.println("id, pr_in, pr_out, in_degree, out_degree");

            for (Node node : nodes) {
                out.println(String.format("%d,%.10f,%.10f,%d,%d\n",
                        node.getId(), node.getPrIn(), node.getPrOut(),
                        node.getInDegree(), node.getOutDegree()));
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

    public int[] inDegSeq() {
        int seq[] = new int[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getInDegree();
            i++;
        }

        return seq;
    }

    public int[] outDegSeq() {
        int seq[] = new int[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getOutDegree();
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
        int stable = 0;
        while (stable < 1000) {
            //System.out.println("totalDegree: " + totalDegree);
            int origPos = RandomGenerator.instance().random.nextInt(totalDegree);
            int targPos = RandomGenerator.instance().random.nextInt(totalDegree);

            int curpos = 0;
            int origIndex = -1;
            while (curpos <= origPos) {
                origIndex++;
                curpos += outDegSeq[origIndex];
            }

            curpos = 0;
            int targIndex = -1;
            while (curpos <= targPos) {
                targIndex++;
                curpos += inDegSeq[targIndex];
            }
            //System.out.println("" + inDegSeq[targIndex]);
                
            //System.out.println("orig: " + origIndex + "; targ: " + targIndex);
            
            if (addEdge(newNodes[origIndex], newNodes[targIndex], 0)) {
                outDegSeq[origIndex]--;
                inDegSeq[targIndex]--;
                totalDegree--;
                stable = 0;
            }
            stable++;
        }
    }

    double getMinPRIn() {
        return minPRIn;
    }

    double getMinPROut() {
        return minPROut;
    }

    double getMaxPRIn() {
        return maxPRIn;
    }

    double getMaxPROut() {
        return maxPROut;
    }
    
    void printPRInfo() {
        System.out.println("Input PR > min: " + getMinPRIn() + "; max: " + getMaxPRIn());
        System.out.println("Output PR > min: " + getMinPROut() + "; max: " + getMaxPROut());
    }

    public Vector<Node> getNodes() {
        return nodes;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
    }

    DRMap getLastMap() {
        return lastMap;
    }

    void setLastMap(DRMap lastMap) {
        this.lastMap = lastMap;
    }
    
    public Vector<Edge> getEdges() {
        return edges;
    }
    
    @Override
    public String toString() {
        String str = "node count: " + nodeCount + "\n";
        str += "edge count: " + edgeCount + "\n";
        return str;
    }
    
    void printDegDistInfo() {
        int[] inDegSeq = inDegSeq();
        int[] outDegSeq = outDegSeq();
        
        int[] inDegrees = new int[10];
        int[] outDegrees = new int[10];
        
        for (int i = 0; i < 10; i++) {
            inDegrees[i] = 0;
            outDegrees[i] = 0;
        }
        
        int maxIn = 0;
        for (int i : inDegSeq) {
            if (i > maxIn) {
                maxIn = i;
            }
            if (i < 10) {
                inDegrees[i]++;
            }
        }
        
        int maxOut = 0;
        for (int i : outDegSeq) {
            if (i > maxOut) {
                maxOut = i;
            }
            if (i < 10) {
                outDegrees[i]++;
            }
        }
        
        System.out.println("max in: " + maxIn + "; max out: " + maxOut);
        System.out.println(">>> in degrees");
        for (int i = 0; i < 10; i++) {
            System.out.print("" + i + ": " + inDegrees[i] + " ");
        }
        System.out.println("\n>>> out degrees");
        for (int i = 0; i < 10; i++) {
            System.out.print("" + i + ": " + outDegrees[i] + " ");
        }
        System.out.println("\n");
    }
    
    public void clearFlags() {
        for (Node node : nodes) {
            node.setFlag(false);
        }
    }
}