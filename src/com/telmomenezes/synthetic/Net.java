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
            
            Node corig = clonedNet.getNodeById(orig.getId());
            Node ctarg = clonedNet.getNodeById(targ.getId());
            
            clonedNet.addEdge(corig, ctarg);
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
                Node corig = clonedNet.getNodeById(orig.getId());
                Node ctarg = clonedNet.getNodeById(targ.getId());
            
                clonedNet.addEdge(corig, ctarg);
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
    
    public boolean addEdge(Node origin, Node target) {
        if ((!selfEdges) && (origin == target)) {
            return false;
        }

        if (edgeExists(origin, target)) {
            return false;
        }

        Edge edge = new Edge(origin, target);
        edges.add(edge);
        origin.addOutEdge(edge);
        target.addInEdge(edge);

        edgeCount++;
        
        return true;
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

                node.setPrD(node.getPrD() * drag);
                node.setPrD(node.getPrD() + (1.0 - drag)
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

                node.setPrU(node.getPrU() * drag);
                node.setPrU(node.getPrU() + (1.0 - drag)
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

    public void printNetInfo() {
        System.out.println("node number: " + nodeCount);
        System.out.println("edge number: " + edgeCount);
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