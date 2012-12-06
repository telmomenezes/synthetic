package com.telmomenezes.synthetic;

import java.util.Arrays;


public class DistMatrix {
    int[] dmatrix;
    int nodes;

    
    public DistMatrix(int nodes) {
        this.nodes = nodes;
        
        dmatrix = new int[nodes * nodes];

        // clear matrices
        int largeVal = 9999999;
        Arrays.fill(dmatrix, largeVal);
    }
    
    
    public int getDist(int x, int y) {
        if (x == y) {
            return 0;
        }
        return dmatrix[(y * nodes) + x];
    }
    
    
    public void setDist(int x, int y, int d) {
        dmatrix[(y * nodes) + x] = d;
    }
    
    
    private void updateDistanceTarg_r(Net net, int origPos, int targPos, int distance) {
    	if (distance > 3)
    		return;
    	
    	if (getDist(origPos, targPos) <= distance)
    		return;
    	
    	setDist(origPos, targPos, distance);
    	
    	Node targ = net.getNodeById(targPos);
    	
    	for (Edge edge : targ.getOutEdges()) {
    		updateDistanceTarg_r(net, origPos, edge.getTarget().getId(), distance + 1);
    	}
    }
    
    
    private void updateDDistanceOrig_r(Net net, int origPos, int targPos, int distance) {
    	if (distance > 3)
    		return;
    	
    	if (getDist(origPos, targPos) <= distance)
    		return;
    	
    	updateDistanceTarg_r(net, origPos, targPos, distance);
    	
    	Node orig = net.getNodeById(origPos);
    	
    	for (Edge edge : orig.getInEdges()) {
    		updateDDistanceOrig_r(net, origPos, edge.getOrigin().getId(), distance + 1);
    	}
    }
    
    
    public void updateDistances(Net net, int origPos, int targPos) {
    	updateDDistanceOrig_r(net, origPos, targPos, 1);
    }
}
