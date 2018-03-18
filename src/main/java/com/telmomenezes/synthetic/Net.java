package com.telmomenezes.synthetic;

import com.telmomenezes.synthetic.io.NetFile;
import com.telmomenezes.synthetic.io.NetFileType;
import com.telmomenezes.synthetic.random.RandomGenerator;
import com.telmomenezes.synthetic.randomwalkers.RandomWalkers;


public class Net implements Cloneable {
    protected Node[] nodes;
    protected Edge[] edges;
    private boolean[][] adjMatrix;
    
    private int nodeCount;
    private int edgeCount;

    public boolean directed;
    private boolean selfEdges;
    boolean parallels;
    
    private boolean pageRanksComputed;
    
    public MetricsBag metricsBag;
    public RandomWalkers dRandomWalkers;
    public RandomWalkers uRandomWalkers;
    
    
    public Net(int maxNodeCount, int maxEdgeCount) {
        nodeCount = 0;
        edgeCount = 0;
        nodes = new Node[maxNodeCount];
        edges = new Edge[maxEdgeCount];
        adjMatrix = new boolean[maxNodeCount][maxNodeCount];
        
        // defaults
        directed = true;
        selfEdges = false;
        parallels = false;
        
        pageRanksComputed = false;
    }
    
    public Net(int maxNodeCount, int maxEdgeCount, boolean directed, boolean selfEdges, boolean parallels) {
        this(maxNodeCount, maxEdgeCount);
        this.directed = directed;
        this.selfEdges = selfEdges;
        this.parallels = parallels;
    }
    
    @Override
    public Net clone()
    {
        Net clonedNet = new Net(nodeCount, edgeCount);
        
        clonedNet.directed = directed;
        clonedNet.selfEdges = selfEdges;
        clonedNet.parallels = parallels;
        
        // clone all nodes
        for (Node node : nodes) {
            clonedNet.addNode(node.clone());
        }
        
        // recreate all edges
        for (Edge edge : edges) {
            Node orig = edge.getOrigin();
            Node targ = edge.getTarget();
            
            Node corig = clonedNet.getNodeById(orig.getId());
            Node ctarg = clonedNet.getNodeById(targ.getId());
            
            clonedNet.addEdge(corig, ctarg);
        }
        
        return clonedNet;
    }
    
    public Net cloneFlagged()
    {
        Net clonedNet = new Net(nodeCount, edgeCount);
        
        clonedNet.directed = directed;
        clonedNet.selfEdges = selfEdges;
        clonedNet.parallels = parallels;
        
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
                Node corig = clonedNet.getNodeById(orig.getId());
                Node ctarg = clonedNet.getNodeById(targ.getId());
            
                clonedNet.addEdge(corig, ctarg);
            }
        }
        
        return clonedNet;
    }
    
    
    public static Net load(String filePath) {
        return load(filePath, true, false);
    }
    
    
    public static Net load(String filePath, boolean directed, boolean parallels) {
        return NetFile.loadNet(filePath, directed, parallels);
    }

    
    public void save(String filePath, NetFileType fileType) {
        NetFile.saveNet(this, filePath, fileType);
    }
    
    
    private Node addNode(Node node) {
        nodes[nodeCount] = node;
    	nodeCount++;
        return node;
    }
    
    
    Node addNode(int value) {
        Node node = new Node(nodeCount, value);
        addNode(node);
        return node;
    }
    
    
    public Node addNode() {
        return addNode(0);
    }
    
    private Node getNodeById(int id) {
        return nodes[id];
    }
    
    public boolean addEdge(Node origin, Node target) {
        if ((!selfEdges) && (origin == target)) {
        	return false;
        }

        if ((!parallels) && edgeExists(origin, target)) {
        	return false;
        }
        
        Edge edge = new Edge(origin, target);
        edges[edgeCount] = edge;
        origin.addOutEdge(edge);
        target.addInEdge(edge);
        
        adjMatrix[origin.getId()][target.getId()] = true;

        edgeCount++;
        
        return true;
    }

    
    public boolean edgeExists(int origin, int target) {
        if (adjMatrix[origin][target]) {
        	return true;
        }
        
        // If net is undirected, we must also check the reverse edge
        if (!directed) {
        	if (adjMatrix[target][origin]) {
            	return true;
            }
        }

        return false;
    }
    
    
    public boolean edgeExists(Node origin, Node target) {
    	return edgeExists(origin.getId(), target.getId());
    }

    
    public Node getRandomNode() {
        int pos = RandomGenerator.random.nextInt(nodeCount);
        return nodes[pos];
    }

    
    private void computePageranks() {
        if (pageRanksComputed) {
            return;
        }
        
        pageRanksComputed = true;
        
        // TODO: config
        int maxIter = 100;
        double damping = 0.85;

        for (Node node : nodes) {
            node.setPrDLast(1);
            node.setPrULast(1);
        }

        int i = 0;

        double deltaPrD = 999;
        double deltaPrU = 999;
        double zero_test = 0.0001;

        while (((deltaPrD > zero_test) || (deltaPrU > zero_test)) && (i < maxIter)) {
            double accPRD = 0;
            double accPRU = 0;

            for (Node node : nodes) {
                // update directed page ranks
            	node.setPrD(0);
                for (Edge origin : node.getInEdges()) {
                    node.setPrD(node.getPrD()
                            + origin.getOrigin().getPrDLast()
                            / ((double)origin.getOrigin().getOutDegree()));
                }

                node.setPrD(node.getPrD() * damping);
                node.setPrD(node.getPrD() + (1.0 - damping)
                        / ((double) nodeCount));

                accPRD += node.getPrD();

                // update undirected page ranks
                node.setPrU(0);
                for (Edge origin : node.getInEdges()) {
                    node.setPrU(node.getPrU()
                            + origin.getOrigin().getPrULast()
                            / ((double)origin.getOrigin().getDegree()));
                }
                for (Edge target : node.getOutEdges()) {
                    node.setPrU(node.getPrU()
                            + target.getTarget().getPrULast()
                            / ((double)target.getTarget().getDegree()));
                }

                node.setPrU(node.getPrU() * damping);
                node.setPrU(node.getPrU() + (1.0 - damping)
                        / ((double)nodeCount));

                accPRU += node.getPrU();
            }

            deltaPrD = 0;
            deltaPrU = 0;

            for (Node node : nodes) {
                node.setPrD(node.getPrD() / accPRD);
                node.setPrU(node.getPrU() / accPRU);
                deltaPrD += Math.abs(node.getPrD() - node.getPrDLast());
                deltaPrU += Math.abs(node.getPrU() - node.getPrULast());
                node.setPrDLast(node.getPrD());
                node.setPrULast(node.getPrU());
            }

            i++;
        }

        // use log scale
        for (Node node : nodes) {
            node.setPrD(Math.log(node.getPrD()));
            node.setPrU(Math.log(node.getPrU()));
        }
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
    
    public int[] degSeq() {
    	int seq[] = new int[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getDegree();
            i++;
        }

        return seq;
    }
    
    public double[] prDSeq() {
        computePageranks();
        double seq[] = new double[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getPrD();
            i++;
        }

        return seq;
    }
    
    public double[] prUSeq() {
        computePageranks();
        double seq[] = new double[nodeCount];
        int i = 0;
        for (Node curnode : nodes) {
            seq[i] = curnode.getPrU();
            i++;
        }

        return seq;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }
    
    public Edge[] getEdges() {
        return edges;
    }
    
    
    public void clean() {
		dRandomWalkers = null;
		uRandomWalkers = null;
	}
    
    
    public NetParams getNetParams() {
    	return new NetParams(nodeCount, edgeCount, directed, parallels);
    }
    
    
    @Override
    public String toString() {
        String str = "node count: " + nodeCount + "\n";
        str += "edge count: " + edgeCount + "\n";
        str += "directed: " + directed + "\n";
        return str;
    }
    
    public void clearFlags() {
        for (Node node : nodes) {
            node.setFlag(false);
        }
    }
}