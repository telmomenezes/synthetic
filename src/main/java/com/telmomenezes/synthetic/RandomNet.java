package com.telmomenezes.synthetic;

import java.util.Vector;

import com.telmomenezes.synthetic.random.RandomGenerator;


public class RandomNet {
	public static Net generate(int nodeCount, int edgeCount, boolean directed, boolean parallels) {
		Net net = new Net(nodeCount, edgeCount, directed, false, parallels);
		
		Vector<Node> nodes = new Vector<Node>();
		
		// create nodes
		for (int i = 0; i < nodeCount; i++) {
			nodes.add(net.addNode());
		}
		
		int curedges = 0;
		
		while (curedges < edgeCount) {
			int origIndex = RandomGenerator.random.nextInt(nodeCount);
			int targIndex = RandomGenerator.random.nextInt(nodeCount);
		
			Node origin = nodes.get(origIndex);
			Node target = nodes.elementAt(targIndex);
			if (net.addEdge(origin, target)) {
				curedges += 1;
			}
		}
		
		return net;
	}
	
	
	public static Net generate(Net net) {
		return generate(net.getNodeCount(), net.getEdgeCount(), net.directed, net.parallels);
	}
}