package com.telmomenezes.synthetic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Net {
	static int CURID = 0;

	double minPRIn;
    double minPROut;
    double maxPRIn;
    double maxPROut;
    
    Node nodes;

    int nodeCount;
    int edgeCount;

    int temporal;
    long minTS;
    long maxTS;

    DRMap last_map;
	
	public Net() {
		nodeCount = 0;
		edgeCount = 0;
		nodes = null;
		temporal = 0;
		minTS = 0;
		maxTS = 0;
	}

	public Node addNode(int type) {
		nodeCount++;
		Node node = new Node(type, CURID++);
		node.next = nodes;
		nodes = node;
		return node;
	}

	public Node addNodeWithId(int nid, int type) {
		nodeCount++;
		if (nid >= CURID) {
			CURID = nid + 1;
		}
		Node node = new Node(type, nid);
		node.next = nodes;
		nodes = node;
		return node;
	}

	public int addEdgeToNet(Node orig, Node targ, long timestamp) {
		if (orig.addEdge(targ, timestamp)) {
			edgeCount++;

			if (timestamp > 0) {
				temporal = 1;
				if ((minTS == 0) || (timestamp < minTS)) {
					minTS = timestamp;
				}
				if ((maxTS == 0) || (timestamp > maxTS)) {
					maxTS = timestamp;
				}
			}

			return 1;
		}
		return 0;
	}

	Node getRandomNode() {
		int pos = RandomGenerator.instance().random.nextInt(nodeCount);
		Node curnode = nodes;
		for (int i = 0; i < pos; i++) {
			curnode = curnode.next;
		}
		return curnode;
	}

	DRMap getDRMap(int binNumber) {
		return getDRMapWithLimit(binNumber, minPRIn, maxPRIn, minPROut, maxPROut);
	}

	DRMap getDRMapWithLimit(int binNumber, double minValHor,
        double maxValHor, double minValVer, double maxValVer) {
		
		double inervalHor = (maxValHor - minValHor) / ((double)binNumber);
		double intervalVer = (maxValVer - minValVer) / ((double)binNumber);

		DRMap map = new DRMap(binNumber, minValHor - inervalHor,
				maxValHor, minValVer - intervalVer, maxValVer);

		Node node = nodes;
		while (node != null) {
			int x = 0;
			int y = 0;

			if ((new Double(node.prIn)).isInfinite()) {
				if (node.prIn <= minValHor) {
					x = 0;
				}
				else if (node.prIn >= maxValHor) {
					x = binNumber - 1;
				}
				else {
					x = (int) Math.floor((node.prIn - minValHor) / inervalHor);
				}
			}
			if ((new Double(node.prOut)).isInfinite()) {
				if (node.prOut <= minValVer) {
					y = 0;
				}
				else if (node.prOut >= maxValVer) {
					y = binNumber - 1;
				}
				else {
					y = (int) Math.floor((node.prOut - minValVer) / intervalVer);
				}
			}

			if ((x >= 0) && (y >= 0) && ((node.inDegree != 0) || (node.outDegree != 0))) {
				map.incValue(x, y);
			}
        
			node = node.next;
		}

		return map;
	}


	public void computePageranks() {
		// TODO: config
		int maxIter = 10;
		double drag = 0.999;

		Node node = nodes;
		while (node != null) {
			node.prInLast = 1;
			node.prOutLast = 1;
			node = node.next;
		}

		int i = 0;

		//double delta_pr_in = 999;
		//double delta_pr_out = 999;
		//double zero_test = 0.0001;

		//while (((delta_pr_in > zero_test) || (delta_pr_out > zero_test)) && (i < max_iter)) {
		while (i < maxIter) {
			double accPRIn = 0;
			double accPROut = 0;

			node = nodes;
			while(node != null) {
				node.prIn = 0;
				Edge origin = node.origins;
				while (origin != null) {
					node.prIn += origin.orig.prInLast / ((double)origin.orig.outDegree);
					origin = origin.nextOrig;
				}
            
				node.prIn *= drag;
				node.prIn += (1.0 - drag) / ((double)nodeCount);
            
				accPRIn += node.prIn;

				node.prOut = 0;
				Edge target = node.targets;
				while (target != null) {
					node.prOut += target.targ.prOutLast / ((double)target.targ.inDegree);
					target = target.nextTarg;
				}
            
				node.prOut *= drag;
				node.prOut += (1.0 - drag) / ((double)nodeCount);
            
				accPROut += node.prOut;
            
				node = node.next;
			}

			//delta_pr_in = 0;
			//delta_pr_out = 0;

			node = nodes;
			while (node != null) {
				node.prIn /= accPRIn;
				node.prOut /= accPROut;
				//delta_pr_in += Math.abs(node.pr_in - node.pr_in_last);
				//delta_pr_out += Math.abs(node.pr_out - node.pr_out_last);    
				node.prInLast = node.prIn;
				node.prOutLast = node.prOut;
            
				node = node.next;
			}
			
			i++;
		}

		// relative pr
		double basePR = 1.0 / ((double)nodeCount);
		node = nodes;
		while (node != null) {
			node.prIn = node.prIn / basePR;
			node.prOut = node.prOut / basePR;
			node = node.next;
		}
    
		// use log scale
		node = nodes;
		while (node != null) {
			node.prIn = Math.log(node.prIn);
			node.prOut = Math.log(node.prOut);
			node = node.next;
		}

		// compute min/max EVC in and out
		minPRIn = 0;
		minPROut = 0;
		maxPRIn = 0;
		maxPROut = 0;
		boolean first = true;
		node = nodes;
		while (node != null) {
			if ((new Double(node.prIn)).isInfinite() && (first || (node.prIn < minPRIn))) {
				minPRIn = node.prIn;
			}
			if ((new Double(node.prOut)).isInfinite() && (first || (node.prOut < minPROut))) {
				minPROut = node.prOut;
			}
			if ((new Double(node.prIn)).isInfinite() && (first || (node.prIn > maxPRIn))) {
				maxPRIn = node.prIn;
			}
			if ((new Double(node.prOut)).isInfinite() && (first || (node.prOut > maxPROut))) {
				maxPROut = node.prOut;
			}

			first = true;
        
			node = node.next;
		}
	}


	public void writePageranks(String filePath)
	{
		try {
			FileWriter outFile = new FileWriter(filePath);
			PrintWriter out = new PrintWriter(outFile);
		
			out.println("id, pr_in, pr_out, in_degree, out_degree");

			Node node = nodes;
			while (node != null) {
				out.println(String.format("%d,%.10f,%.10f,%d,%d\n", node.id, node.prIn, node.prOut, node.inDegree, node.outDegree));
				node = node.next;
			}

			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printNetInfo()
	{
		System.out.println("node number: " + nodeCount);
		System.out.println("edge number: " + edgeCount);
		System.out.println(String.format("log(pr_in): [%f, %f]\n", minPRIn, maxPRIn));
		System.out.println(String.format("log(pr_out): [%f, %f]\n", minPROut, maxPROut));
	}

	public int triadType(Node a, Node b, Node c)
	{
		int type = -1;

		boolean ab = a.edgeExists(b);
		boolean ac = a.edgeExists(c);
		boolean ba = b.edgeExists(a);
		boolean bc = b.edgeExists(c);
		boolean ca = c.edgeExists(a);
		boolean cb = c.edgeExists(b);

		if      (ab && ac && !ba && !bc && !ca && !cb) type = 1;
		else if (!ab && !ac && ba && !bc && ca && !cb) type = 2;
		else if (!ab && !ac && !ba && bc && ca && !cb) type = 3;
		else if (!ab && ac && ba && !bc && ca && !cb)  type = 4;
		else if (ab && ac && ba && !bc && !ca && !cb)  type = 5;
		else if (ab && ac && ba && !bc && ca && !cb)   type = 6;
		else if (ab && ac && !ba && bc && !ca && !cb)  type = 7;
		else if (!ab && ac && ba && !bc && !ca && cb)  type = 8;
		else if (ab && ac && !ba && bc && !ca && cb)   type = 9;
		else if (!ab && !ac && ba && bc && ca && cb)   type = 10;
		else if (ab && ac && !ba && bc && ca && !cb)   type = 11;
		else if (!ab && ac && ba && bc && ca && cb)    type = 12;
		else if (ab && ac && ba && bc && ca && cb)     type = 13;

		return type;
	}

	void updateTriadProfile(Node[] triad, long[] profile)
	{
		int type = triadType(triad[0], triad[1], triad[2]);
		if (type < 0) type = triadType(triad[0], triad[2], triad[1]);
		if (type < 0) type = triadType(triad[1], triad[0], triad[2]);
		if (type < 0) type = triadType(triad[1], triad[2], triad[0]);
		if (type < 0) type = triadType(triad[2], triad[0], triad[1]);
		if (type < 0) type = triadType(triad[2], triad[1], triad[0]);

		if (type < 0) {
			System.out.println("negative type!");
			return;
		}

		profile[type - 1]++;
	}

	public boolean notInTriad(Node node, Node[] triad, int depth)
	{
		for (int i = 0; i <= depth; i++) {
			if (triad[i] == node)
				return false;
		}

		return true;
	}

	public void triadProfile_r(Node[] triad, int depth, long[] profile) {
		if (depth == 2) {
			updateTriadProfile(triad, profile);
			return;    
		}

		Node node = triad[depth];

		Edge orig = node.origins;
		while (orig != null) {
			Node next_node = orig.orig;
			if (next_node.flag && notInTriad(next_node, triad, depth)) {
				triad[depth + 1] = next_node;
				triadProfile_r(triad, depth + 1, profile);
			}
			orig = orig.nextOrig;
		}

		Edge targ = node.targets;
		while (targ != null) {
			Node next_node = targ.targ;
			if (next_node.flag && notInTriad(next_node, triad, depth)) {
				triad[depth + 1] = next_node;
				triadProfile_r(triad, depth + 1, profile);
			}
			targ = targ.nextTarg;
		}
	}

	public long[] triadProfile()
	{
		Node[] triad = new Node[3];
		long[] profile = new long[13];

		for (int i = 0; i < 13; i++)
			profile[i] = 0;
    
		// set all node flags to true
		Node node = nodes;
		while (node != null) {
			node.flag = true;
			node = node.next;
		}

		// search for triads starting on each node
		node = nodes;
		while (node != null) {
			triad[0] = node;
			triadProfile_r(triad, 0, profile);
			node.flag = false;
			node = node.next;
		}

		return profile;
	}

	public int[] inDegSeq()
	{
		int seq[] = new int[nodeCount];
		Node curnode = nodes;
		int i = 0;
		while (curnode != null) {
			seq[i] = curnode.inDegree;
			curnode = curnode.next;
			i++;
		}

		return seq;
	}

	public int[] outDegSeq()
	{
		int seq[] = new int[nodeCount];
		Node curnode = nodes;
		int i = 0;
		while (curnode != null) {
			seq[i] = curnode.outDegree;
			curnode = curnode.next;
			i++;
		}

		return seq;
	}

	void genDegreeSeq(Net refNet)
	{
		int[] inDegSeq = refNet.inDegSeq();
    	int[] outDegSeq = refNet.outDegSeq();

    	int totalDegree = refNet.edgeCount;

    	// create nodes
    	Node[] newNodes = new Node[refNet.nodeCount];
    	for (int i = 0; i < refNet.nodeCount; i++) {
    		newNodes[i] = addNode(0);
    	}

    	// create edges
    	for (int i = 0; i < refNet.edgeCount; i++) {
    		int origPos = RandomGenerator.instance().random.nextInt(totalDegree);
    		int targPos = RandomGenerator.instance().random.nextInt(totalDegree);

    		int curpos = 0;
    		int origIndex = -1;
    		while (curpos <= origPos) {
    			origIndex++;
    			curpos += outDegSeq[origIndex];
    		}
    		outDegSeq[origIndex]--;

    		curpos = 0;
    		int targ_index = -1;
    		while (curpos <= targPos) {
    			targ_index++;
    			curpos += inDegSeq[targ_index];
    		}
    		inDegSeq[targ_index]--;

    		addEdgeToNet(newNodes[origIndex], newNodes[targ_index], 0);

    		totalDegree--;
    	}
	}
}