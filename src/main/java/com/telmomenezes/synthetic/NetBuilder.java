package com.telmomenezes.synthetic;


import java.util.Vector;


public class NetBuilder {
    private int nodeCount;
    private int edgeCount;

    private boolean directed;
    private boolean selfEdges;
    private boolean parallels;
    
    private Vector<Vector<Integer>> adjMatrix;
    private Vector<Integer> nodeValues;
    
    
    private NetBuilder() {
        this.nodeCount = 0;
        this.edgeCount = 0;
        
        // defaults
        directed = true;
        selfEdges = false;
        
        adjMatrix = new Vector<Vector<Integer>>();
        nodeValues = new Vector<Integer>();
    }
    
    
    public NetBuilder(boolean directed, boolean selfEdges, boolean parallels) {
        this();
        this.directed = directed;
        this.selfEdges = selfEdges;
        this.parallels = parallels;
    }
    
    
    public int addNode() {
    	return addNode(0);
    }
    
    
    public int addNode(int value) {
        int id = nodeCount;
    	nodeCount++;
    	adjMatrix.add(new Vector<Integer>());
    	nodeValues.add(value);
        return id;
    }

    
    private boolean edgeExists(int origin, int target) {
    	if (directed) {
    		return adjMatrix.get(origin).contains(target);
    	}
    	else {
    		return adjMatrix.get(origin).contains(target) || adjMatrix.get(target).contains(origin);
    	}
    }
    
    
    public boolean addEdge(int origin, int target, int value) {
        if ((!selfEdges) && (origin == target)) {
            return false;
        }

        if ((!parallels) && edgeExists(origin, target)) {
            return false;
        }
        
        int count = 1;
        if (parallels) {
        	count = value;
        }
        
        for (int i = 0; i < count; i++) {
        	adjMatrix.get(origin).add(target);
        	edgeCount++;
        }
        
        return true;
    }
    
    
    public Net buildNet() {
    	Net net = new Net(nodeCount, edgeCount, directed, selfEdges, parallels);
    	
    	Node[] nodeMap = new Node[nodeCount];
    	
    	for (int i = 0; i < nodeCount; i++) {
    		nodeMap[i] = net.addNode(nodeValues.get(i));
    	}
    	
    	for (int orig = 0; orig < nodeCount; orig++) {
    		for (int targ : adjMatrix.get(orig)) {
    			net.addEdge(nodeMap[orig], nodeMap[targ]);
    		}
    	}
    	
    	return net;
    }
}