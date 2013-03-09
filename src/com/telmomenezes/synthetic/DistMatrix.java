package com.telmomenezes.synthetic;

import java.util.Arrays;
import java.util.Vector;


public class DistMatrix {
    private int[] dmatrix;
    private int nodes;
    private boolean directed;
    private int maxDist;

    
    public DistMatrix(int nodes, boolean directed) {
    	maxDist = 2;
    	
    	this.nodes = nodes;
    	this.directed = directed;
        
        dmatrix = new int[nodes * nodes];

        // clear matrices
        int largeVal = 9999999;
        //int largeVal = maxDist + 1;
        Arrays.fill(dmatrix, largeVal);
        for (int i = 0; i < nodes; i++) {
        	dmatrix[(i * nodes) + i] = 0;
        }
    }
    
    
    public int getDist(int x, int y) {
        if (x == y) {
            return 0;
        }
        return dmatrix[(y * nodes) + x];
    }
    
    
    public void setDist(int x, int y, int d) {
        dmatrix[(y * nodes) + x] = d;
        
        if (!directed) {
        	dmatrix[(x * nodes) + y] = d;
        }
    }
    
    
    private void updateDistanceTarg(Net net, int origPos, int targPos, int distance) {
    	if (distance > maxDist) {
    		return;
    	}
    	
    	if (getDist(origPos, targPos) > distance) {
    		setDist(origPos, targPos, distance);
    	}
    	
    	Node targ = net.getNodeById(targPos);
    	
    	for (Edge edge : targ.getOutEdges()) {
    		updateDistanceTarg(net, origPos, edge.getTarget().getId(), distance + 1);
    	}
    	
    	if (!directed) {
    		for (Edge edge : targ.getInEdges()) {
        		updateDistanceTarg(net, origPos, edge.getOrigin().getId(), distance + 1);
        	}	
    	}
    }
    
    
    private void updateDistanceOrig(Net net, int origPos, int targPos, int distance) {
    	if (distance > maxDist)
    		return;
    	
    	updateDistanceTarg(net, origPos, targPos, distance);
    	
    	Node orig = net.getNodeById(origPos);
    	
    	for (Edge edge : orig.getInEdges()) {
    		//updateDDistanceOrig_r(net, origPos, edge.getOrigin().getId(), distance + 1, directed);
    		updateDistanceOrig(net, edge.getOrigin().getId(), origPos, distance + 1);
    	}
    	
    	if (!directed) {
    		for (Edge edge : orig.getOutEdges()) {
        		updateDistanceOrig(net, edge.getTarget().getId(), origPos, distance + 1);
        	}
    	}
    }
    
    
    public void updateDistances(Net net, int origPos, int targPos) {
    	updateDistanceOrig(net, origPos, targPos, 1);
    }
    
    
    public void calc(Net net) {
    	Vector<Edge> edges = net.getEdges();
    	
    	for (Edge edge : edges) {
    		updateDistances(net, edge.getOrigin().getId(), edge.getTarget().getId());
    	}
    }
    
    
    public DiscreteDistrib getDistrib() {
    	DiscreteDistrib distrib = new DiscreteDistrib(dmatrix, maxDist);
    	return distrib;
    }
}