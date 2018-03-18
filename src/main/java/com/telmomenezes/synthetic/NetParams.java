package com.telmomenezes.synthetic;

public class NetParams {

	private int nodeCount;
	private int edgeCount;
	private boolean directed;
	private boolean parallels;
	
	public NetParams(int nodeCount, int edgeCount, boolean directed, boolean parallels) {
		this.nodeCount = nodeCount;
		this.edgeCount = edgeCount;
		this.directed = directed;
		this.parallels = parallels;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public boolean getDirected() {
		return directed;
	}

	public boolean getParallels() {
		return parallels;
	}
}
