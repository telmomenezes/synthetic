package com.telmomenezes.synthetic;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class NetBuilder {
    private int nodeCount;
    private int edgeCount;

    private boolean directed;
    private boolean selfEdges;
    
    private Vector<Set<Integer>> adjMatrix;
    
    
    public NetBuilder() {
        this.nodeCount = 0;
        this.edgeCount = 0;
        
        // defaults
        directed = true;
        selfEdges = false;
        
        adjMatrix = new Vector<Set<Integer>>();
    }
    
    
    public NetBuilder(boolean directed, boolean selfEdges) {
        this();
        this.directed = directed;
        this.selfEdges = selfEdges;
    }
    
    
    public int addNode() {
        int id = nodeCount;
    	nodeCount++;
    	adjMatrix.add(new HashSet<Integer>());
        return id;
    }

    
    public boolean edgeExists(int origin, int target) {
    	return adjMatrix.get(origin).contains(target);
    }
    
    
    public boolean addEdge(int origin, int target) {
        if ((!selfEdges) && (origin == target)) {
            return false;
        }

        if (edgeExists(origin, target)) {
            return false;
        }
        
        adjMatrix.get(origin).add(target);

        
        edgeCount++;
        
        return true;
    }
    
    
    public Net buildNet() {
    	Net net = new Net(nodeCount, edgeCount, directed, selfEdges);
    	
    	Node[] nodeMap = new Node[nodeCount];
    	
    	for (int i = 0; i < nodeCount; i++) {
    		nodeMap[i] = net.addNode();
    	}
    	
    	for (int orig = 0; orig < nodeCount; orig++) {
    		for (int targ : adjMatrix.get(orig)) {
    			net.addEdge(nodeMap[orig], nodeMap[targ]);
    		}
    	}
    	
    	return net;
    }
}