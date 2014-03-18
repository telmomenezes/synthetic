package com.telmomenezes.synthetic;

public class NetParams {

	private int nodeCount;
	private int edgeCount;
	private boolean directed;
	private boolean selfEdges;
	private boolean parallels;
	
	public NetParams(int nodeCount, int edgeCount, boolean directed, boolean selfEdges, boolean parallels) {
		this.nodeCount = nodeCount;
		this.edgeCount = edgeCount;
		this.directed = directed;
		this.selfEdges = selfEdges;
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

	public boolean getSelfEdges() {
		return selfEdges;
	}

	public boolean getParallels() {
		return parallels;
	}
}
