package com.telmomenezes.synthetic;

import java.util.Vector;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.random.RandomGenerator;


public class RandomNet {
	public static Net generate(int nodeCount, int edgeCount, boolean directed) {
		Net net = new Net(nodeCount, edgeCount, directed, false);
		
		Vector<Node> nodes = new Vector<Node>();
		
		// create nodes
		for (int i = 0; i < nodeCount; i++) {
			nodes.add(net.addNode());
		}
		
		int curedges = 0;
		
		while (curedges < edgeCount) {
			int origIndex = RandomGenerator.instance().random.nextInt(nodeCount);
			int targIndex = RandomGenerator.instance().random.nextInt(nodeCount);
		
			Node origin = nodes.get(origIndex);
			Node target = nodes.elementAt(targIndex);
			if (net.addEdge(origin, target)) {
				curedges += 1;
			}
		}
		
		return net;
	}
}