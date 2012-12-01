package com.telmomenezes.synthetic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.telmomenezes.synthetic.io.NetFile;
import com.telmomenezes.synthetic.io.NetFileType;


public class Net implements Cloneable {
    private int CURID;

    private double minPRIn;
    private double minPROut;
    private double maxPRIn;
    private double maxPROut;

    protected Vector<Node> nodes;
    protected Vector<Edge> edges;
    protected Map<Integer, Node> nodeMap;
    
    private int nodeCount;
    private int edgeCount;

    private boolean selfEdges;

    private boolean pageRanksComputed;
    
    public Net() {
    	CURID = 0;
    	
        nodeCount = 0;
        edgeCount = 0;
        nodes = new Vector<Node>();
        edges = new Vector<Edge>();
        nodeMap = new HashMap<Integer, Node>();
        selfEdges = false;
        pageRanksComputed = false;
    }
    
    public Net(boolean selfEdges) {
        this();
        this.selfEdges = selfEdges;
    }
    
    @Override
    public Net clone()
    {
        Net clonedNet = new Net();
        
        // clone all nodes
        for (Node node : nodes) {
            clonedNet.addNode(node.clone());
        }
        
        // recreate all edges
        for (Edge edge : edges) {
            Node orig = edge.getOrigin();
            Node targ = edge.getTarget();
            long timestamp = edge.getTimestamp();
            
            Node corig = clonedNet.getNodeById(orig.getId());
            Node ctarg = clonedNet.getNodeById(targ.getId());
            
            clonedNet.addEdge(corig, ctarg, timestamp);
        }
        
        return clonedNet;
    }
    
    public Net cloneFlagged()
    {
        Net clonedNet = new Net();
        
        // clone nodes
        for (Node node : nodes) {
            if (node.getFlag()) {
                clonedNet.addNode(node.clone());
            }
        }
        
        // recreate edges
        for (Edge edge : edges) {
            Node orig = edge.getOrigin();
            Node targ = edge.getTarget();
            
            if (orig.getFlag() && targ.getFlag()) {
                long timestamp = edge.getTimestamp();
            
                Node corig = clonedNet.getNodeById(orig.getId());
                Node ctarg = clonedNet.getNodeById(targ.getId());
            
                clonedNet.addEdge(corig, ctarg, timestamp);
            }
        }
        
        return clonedNet;
    }
    
    public static Net load(String filePath) {
        return NetFile.loadNet(filePath);
    }

    public void save(String filePath, NetFileType fileType) {
        NetFile.saveNet(this, filePath, fileType);
    }
    
    private Node addNode(Node node) {
        nodeCount++;
        nodes.add(node);
        nodeMap.put(node.getId(), node);
        return node;
    }
    
    public Node addNode() {
        Node node = new Node(CURID++);
        addNode(node);
        return node;
    }
    
    public Node getNodeById(int id) {
        return nodeMap.get(id);
    }
    
    public boolean addEdge(Node origin, Node target, double weight, long timestamp) {
        if ((!selfEdges) && (origin == target)) {
            return false;
        }

        if (edgeExists(origin, target)) {
            return false;
        }

        Edge edge = new Edge(origin, target, timestamp);
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
    
    public void removeEdge(Edge edge) {
        edge.getOrigin().removeOutput(edge);
        edge.getTarget().removeInput(edge);
        edges.remove(edge);
        edgeCount--;
    }
    
    public void removeNode(Node node) {
        List<Edge> remEdges = new LinkedList<Edge>(node.getInEdges());
        for (Edge e : remEdges) {
            removeEdge(e);
        }
        remEdges = new LinkedList<Edge>(node.getOutEdges());
        for (Edge e : remEdges) {
            removeEdge(e);
        }
        
        nodes.remove(node);
        nodeMap.remove(node.getId());
        nodeCount--;
    }

    public Node getRandomNode() {
        int pos = RandomGenerator.instance().random.nextInt(nodeCount);
        return nodes.get(pos);
    }

    public void computePageranks() {
        if (pageRanksComputed) {
            return;
        }
        
        pageRanksComputed = true;
        
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

    public void printNetInfo() {
        System.out.println("node number: " + nodeCount);
        System.out.println("edge number: " + edgeCount);
        System.out.println(String.format("log(pr_in): [%f, %f]\n", minPRIn,
                maxPRIn));
        System.out.println(String.format("log(pr_out): [%f, %f]\n", minPROut,
                maxPROut));
    }

    public double[] inDegSeq() {
        double seq[] = new double[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getInDegree();
            i++;
        }

        return seq;
    }

    public double[] outDegSeq() {
        double seq[] = new double[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getOutDegree();
            i++;
        }

        return seq;
    }
    
    public double[] prInSeq() {
        computePageranks();
        double seq[] = new double[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getPrIn();
            i++;
        }

        return seq;
    }
    
    public double[] prOutSeq() {
        computePageranks();
        double seq[] = new double[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getPrOut();
            i++;
        }

        return seq;
    }

    void genDegreeSeq(Net refNet) {
        double[] inDegSeq = refNet.inDegSeq();
        double[] outDegSeq = refNet.outDegSeq();

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

    public Vector<Node> getNodes() {
        return nodes;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
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
    
    public void clearFlags() {
        for (Node node : nodes) {
            node.setFlag(false);
        }
    }
}