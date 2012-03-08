package com.telmomenezes.synthetic;


public class Node {
	// node list
    Node next;

    int type;
    int id;
    Edge targets;
    Edge origins;
    int inDegree;
    int outDegree;
    int birth;
    
    // pageranks
    public double prIn;
    double prInLast;
    public double prOut;
    double prOutLast;

    // for generators
    double genweight;

    // Auxiliary flag for algorithms that need to know if this node was already visited
    boolean flag;
	
    public Node(int type, int id) {
    	this.type = type;
    	this.id = id;
    	inDegree = 0;
    	outDegree = 0;
    	birth = -1;
    	targets = null;
    	origins = null;
    }

    public boolean addEdge(Node target, long timestamp) {
    	if (this == target) {
    		return false;
    	}
        
    	if (edgeExists(target)) {
    		return false;
    	}

    	Edge edge = new Edge();
    	edge.orig = this;
    	edge.targ = target;
    	edge.timestamp = timestamp;
    	edge.nextTarg = targets;
    	targets = edge;
    	outDegree++;
    	edge.nextOrig = target.origins;
    	target.origins = edge;
    	target.inDegree++;

    	return true;
    }


    public boolean edgeExists(Node target) {
    	Edge edge = targets;
    
    	while (edge != null) {
    		if (edge.targ == target) {
    			return true;
    		}
    		edge = edge.nextTarg;
    	}

    	return false;
    }
}