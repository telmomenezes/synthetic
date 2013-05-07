package com.telmomenezes.synthetic.randomwalkers;

import java.util.Arrays;

import com.telmomenezes.synthetic.DiscreteDistrib;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;


public class RandomWalkers {
	private int maxLength;
	private int steps;
	
	private Net net;
	private boolean directed;
	
	private int[] dmatrix;
    private int nodes;

    private RandomWalker[] walkers;
    
    
    public RandomWalkers(Net net, boolean directed) {
    	maxLength = 5;
    	steps = 1;
    	
    	this.net = net;
    	this.directed = directed;
        
    	nodes = net.getNodeCount();
    	
        dmatrix = new int[nodes * nodes];

        init();
    }
    
    
    public void init() {
    	// clear matrices
        int largeVal = 9999999;
        //int largeVal = maxLength + 1;
        Arrays.fill(dmatrix, largeVal);
        for (int i = 0; i < nodes; i++) {
        	dmatrix[(i * nodes) + i] = 0;
        }
        
        // init walkers
        walkers = new RandomWalker[nodes];
        int i = 0;
        for (Node node : net.getNodes()) {
        	walkers[i] = new RandomWalker(node, directed, maxLength);
        	i++;
        }
    }
    
    
    public int getDist(int x, int y) {
        if (x == y) {
            return 0;
        }
        return dmatrix[(y * nodes) + x];
    }
    
    
    public void setDist(int x, int y, int d) {
    	if (getDist(x, y) <= d) {
    		return;
    	}
    	
        dmatrix[(y * nodes) + x] = d;
        
        if (!directed) {
        	dmatrix[(x * nodes) + y] = d;
        }
    }
    
    
    public void step() {
    	for (int i = 0; i < nodes; i++) {
    		RandomWalker walker = walkers[i];
    		if (walker.getOrig().getDegree() > 0) {
    			for (int j = 0; j < steps; j++) {
    				walker.step();
    				if (walker.isForward()) {
    					setDist(walker.getOrig().getId(), walker.getTarg().getId(), walker.getLength());
    				}
    				else {
    					setDist(walker.getTarg().getId(), walker.getOrig().getId(), walker.getLength());
    				}
    			}
    		}
    	}
    }
    
    
    public RandomWalkers allSteps() {
    	for (int i = 0; i < net.getEdgeCount(); i++) {
    		step();
    	}
    	
    	return this;
    }
    
    
    public void recompute() {
    	init();
    	allSteps();
    }
    
    
    public DiscreteDistrib getDistrib() {
    	DiscreteDistrib distrib = new DiscreteDistrib(dmatrix, maxLength);
    	return distrib;
    }
    
    
    @Override
    public String toString() {
    	return (new DiscreteDistrib(dmatrix, maxLength)).toString();
    }
}