/*
 * This class is an implementation of the EMD-L1 algorithm, originally
 * described in:
 * 
 * H. Ling and K. Okada, An Efficient Earth Mover's Distance Algorithm for 
 * Robust Histogram Comparison, IEEE Transaction on Pattern Analysis and Machine
 * Intelligence (PAMI), 29(5), pp. 840-853, 2007.
 */


package com.telmomenezes.synthetic.emd;

import java.util.Vector;


/**
 * This class implements a fast variant of the Earth Mover's Distance (EMD)
 * between two n-dimensional histograms.
 * 
 * More specifically, it implements the EMD-L1 algorithm, which uses
 * Manhattan distance (L1) as the ground distance, allowing for optimizations
 * that achieve O(N) time complexity, instead of the O(N^2) time complexity of
 * classical EMD. The EMD-L1 algorithm was originally described in:
 * 
 * H. Ling and K. Okada, An Efficient Earth Mover's Distance Algorithm for 
 * Robust Histogram Comparison, IEEE Transaction on Pattern Analysis and Machine
 * Intelligence (PAMI), 29(5), pp. 840-853, 2007.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 * @version $Id: EMDL1.java 196 2010-09-24 22:09:20Z tmenezes $
 */
public class EMDL1 {

	private class Node {
		private int d;
		private int u;
		private int[] pos;
		private int level;
		private Node parent;
		private Link child;
		private Link link;
		
		private Node(int ndim) {
			pos = new int[ndim];
		}
	};

	
	private class Link {
		private int flow;
		private boolean dir;
		private Node parent;
		private Node child;
		private Link next;
	};

	
	// histograms
	int[] hist1;
	int[] hist2;

	private int[] dimensions;
	private int ndim;
	private int nodeCount;

	// non-basic variable count
	private int nbv;

	// BV tree root
	private Node root;
	
	// BV links
	private Link enter;
	private int enterIndex;
	private Link leave;
	
	private int iteration;

	private Node[] nodes;
	private Link[][] links;

	// non-BV links
	private Link[] nbvLinks;
	
	private Node[] auxQueue;
	private Link[] fromLoop;
	private Link[] toLoop;

	int from;
	int to;

	
	public static int distance(int[] hist1, int[] hist2, int[] dimensions) {
		EMDL1 emd = new EMDL1(hist1, hist2, dimensions);
		return emd.computeDistance();
	}
	
	
	private EMDL1(int[] hist1, int[] hist2, int[] dimensions) {
		
		this.hist1 = hist1;
		this.hist2 = hist2;
		
		root = null;

		this.dimensions = dimensions;
		ndim = dimensions.length;
		
		nodeCount = 1;
		
		for (int i = 0; i < ndim; i++) {
			nodeCount *= dimensions[i];
		}

		nodes = new Node[nodeCount];
		links = new Link[ndim][nodeCount];

		for (int i = 0; i < nodeCount; i++)
			nodes[i] = new Node(ndim);
		
		for (int d = 0; d < ndim; d++)
			for (int i = 0; i < nodeCount; i++)
				links[d][i] = new Link();

		int extra = (int)Math.pow(2, ndim - 1);
		
		nbvLinks = new Link[nodeCount * ndim * 2 + extra];
		for (int i = 0; i < (nodeCount * ndim * 2 + extra); i++)
			nbvLinks[i] = new Link();
		
		auxQueue = new Node[nodeCount + extra];
		for (int i = 0; i < (nodeCount + extra); i++)
			auxQueue[i] = new Node(ndim);
		
		fromLoop = new Link[nodeCount + extra];
		for (int i = 0; i < (nodeCount + extra); i++)
			fromLoop[i] = new Link();
		
		toLoop = new Link[nodeCount + 2];
		for (int i = 0; i < (nodeCount + 2); i++)
			toLoop[i] = new Link();
		
		for (int i = 0; i < nodeCount; i++) {
			nodes[i].d = hist1[i] - hist2[i];
			nodes[i].pos = getCoordsFromIndex(i);
			nodes[i].parent = null;
			nodes[i].child = null;
			nodes[i].level = -1;
		}
	
		for (int d = 0; d < ndim; d++)
			for (int i = 0; i < nodeCount; i++) {
				int[] coords = getCoordsFromIndex(i);
				coords[d] = (coords[d] + 1) % dimensions[d];
				links[d][i].parent = nodes[i];
				links[d][i].child = nodes[getIndexFromCoords(coords)];
				links[d][i].flow = 0;
				links[d][i].dir = true;
				links[d][i].next = null;
			}
	}


	private int computeDistance() {
		
		generateInitialSolution();

		iteration = 0;
		boolean found = false;
		while (!found && (iteration < 1000)) {

			if (iteration == 0)
				updateSubtree(root);
			else
				updateSubtree(enter.child);

			found = optimumFound();

			if (!found)
				findNewSolution();

			iteration++;
		}

		return totalFlow();
	}

	
	private int[] getCoordsFromIndex(int index) {
		
		int[] coords = new int[ndim];
		
		int residual = index;
		
		for (int i = ndim - 1; i >= 0; i--) {
			coords[i] = residual % dimensions[i];
			residual /= dimensions[i];
		}
		
		return coords;
	}
	
	
	private int getIndexFromCoords(int[] coords) {
		
		int index = 0;
		
		int multiplier = 1;
		for (int i = ndim - 1; i >= 0; i-- ) {
			index += multiplier * coords[i];
			multiplier *= dimensions[i];
		}
		
		return index;
	}


	private void generateInitialSolution() {

		int[] dist = new int[nodeCount];
		for(int i = 0; i < nodeCount; i++) {	
			dist[i] = nodes[i].d;
		}
		
		Vector<int[]> ds = new Vector<int[]>();
		for (int d = 0; d < ndim; d++) {
			int[] dsa = new int[dimensions[d]];
			for (int i = 1; i < nodeCount; i++) {
				int[] coords = getCoordsFromIndex(i);
				dsa[coords[d]] -= dist[i];
			}
			ds.add(dsa);
		}

		Link bv;
		int flow;
		int[] f = new int[ndim];
		nbv = 0;

		for (int i = 0; i < nodeCount; i++) {
		
			int[] coords = getCoordsFromIndex(i);
			
			boolean stop = true;
			for (int d = 0; (d < ndim) && stop; d++)
				stop &= (coords[d] == dimensions[d] - 1);
			if (stop)
				break;
			
			// decide direction
			flow = dist[i];
			for (int d = 0; d < ndim; d++)
				if (coords[d] < dimensions[d] - 1)
					f[d] = Math.abs(flow + ds.get(d)[coords[d] + 1]);
				else
					f[d] = 999999999;

			stop = false;
			int dpos = 0;
			for (int d = 0; (d < ndim) & (!stop); d++) {
				stop = true;
				for (int j = d + 1; (j < ndim) & stop; j++) {
					stop &= f[d] < f[j];
				}
				
				if (stop)
					dpos = d;
			}
			
			bv = links[dpos][i];
			
			for (int d = 0; d < ndim; d++)
				if ((coords[d] < dimensions[d] - 1) && (d != dpos))
					nbvLinks[nbv++] = links[d][i];
			
			coords[dpos]++;
			dist[getIndexFromCoords(coords)] += flow;
			ds.get(dpos)[coords[dpos]] += flow;

			bv.flow = Math.abs(flow);

			if (flow > 0)
				bv.dir = true;
			else
				bv.dir = false;
			bv.parent.child = bv;
		}
		
		// set root to center of the graph
		int[] coords = new int[ndim];
		for (int d = 0; d < ndim; d++)
			coords[d] = (int) (0.5 * dimensions[d] - .5);
		root = nodes[getIndexFromCoords(coords)];
		root.u = 0;
		root.level = 0;
		root.parent = null;
		root.link = null;

		auxQueue[0] = root;
		int queueSize = 1;
		int queuePos = 0;

		// create subtrees
		Link curLink = null;
		Link nextLink = null;
		Node curNode = null;
		Node nextNode = null;

		while (queuePos < queueSize && queueSize < nodeCount) {
			
			curNode = auxQueue[queuePos++];
			
			coords = curNode.pos;

			curLink = curNode.child;
			if (curLink != null) {
				nextNode = curLink.child;
				nextNode.parent = curNode;
				nextNode.link = curLink;
				auxQueue[queueSize++] = nextNode;
			}

			// check neighbor nodes
			for (int k = 0; k < ndim * 2; ++k) {
				int d = k / 2;
				int dir = k % 2;
				
				int[] nCoords = new int[ndim];
				System.arraycopy(coords, 0, nCoords, 0, ndim);
				if (dir == 0 && coords[d] > 0) {
					nCoords[d]--;
					nextNode = nodes[getIndexFromCoords(nCoords)];
				}
				else if (dir == 1 && coords[d] < dimensions[d] - 1) {
					nCoords[d]++;
					nextNode = nodes[getIndexFromCoords(nCoords)];
				}
				else
					continue;

				if (nextNode != curNode.parent) {
					nextLink = nextNode.child;
					if ((nextLink != null) && (nextLink.child == curNode))
					{
						nextNode.parent = curNode;
						nextNode.link = nextLink;
						nextNode.child = null;
						auxQueue[queueSize++] = nextNode;

						nextLink.parent = curNode;
						nextLink.child = nextNode;
						nextLink.dir = !nextLink.dir;

						if (curLink != null)
							curLink.next = nextLink;
						else
							curNode.child = nextLink;
						curLink = nextLink;
					}
				}
			}
		}
	}


	private void updateSubtree(Node subRoot) {

		auxQueue[0] = subRoot;
		int queueSize = 1;
		int queueHead = 0;

		Node curNode = null;
		Node nextNode = null;
		Link curLink = null;
		while (queueHead < queueSize) {
			curNode = auxQueue[queueHead++];
			curLink = curNode.child;

			while (curLink != null) {
				nextNode = curLink.child;
				nextNode.level = curNode.level + 1;
				if (curLink.dir)
					nextNode.u = curNode.u - 1;
				else
					nextNode.u = curNode.u + 1;
				curLink = curLink.next;
				auxQueue[queueSize++] = nextNode;
			}
		}
	}


	private boolean optimumFound() {
		int c;
		int minC = 0;
		Link curLink;
		enter = null;
		enterIndex = -1;

		for (int k = 0; k < nbv; ++k) {
			curLink = nbvLinks[k];
			c = 1 - curLink.parent.u + curLink.child.u;
			if (c < minC) {
				minC = c;
				enterIndex = k;
			}
			else {
				c = 1 + curLink.parent.u - curLink.child.u;
				if (c < minC) {
					minC = c;
					enterIndex = k;
				}
			}
		}

		if (enterIndex >= 0) {
			enter = nbvLinks[enterIndex];
			if (minC == (1 - enter.child.u + enter.parent.u)) {
				Node curNode = enter.parent;
				enter.parent = enter.child;
				enter.child = curNode;
			}

			enter.dir = true;
		}

		return (enterIndex == -1);
	}


	private void findNewSolution() {
		findLoop();

		Link curLink = null;
		int minFlow = leave.flow;
		
		for (int k = 0; k < from; ++k) {
			curLink = fromLoop[k];
			if (curLink.dir)
				curLink.flow += minFlow;
			else
				curLink.flow -= minFlow;
		}
		
		for (int k = 0; k < to; ++k) {
			curLink = toLoop[k];
			if (curLink.dir)
				curLink.flow -= minFlow;
			else
				curLink.flow += minFlow;
		}

		Node leaveParent = leave.parent;
		Node leaveChild = leave.child;
		Link preLink = leaveParent.child;
		if (preLink == leave)
			leaveParent.child = leave.next;
		else {
			while (preLink.next != leave)
				preLink = preLink.next;
			preLink.next = leave.next;
		}
		leaveChild.parent = null;
		leaveChild.link = null;

		nbvLinks[enterIndex] = leave;

		Node enterParent = enter.parent;
		Node enterChild = enter.child;

		enter.flow = minFlow;
		enter.next = enterParent.child;
		enterParent.child = enter;

		Node preNode = enterParent;
		Node curNode = enterChild;
		Node nextNode;
		Link nextLink;
		preLink = enter;
		
		while (curNode != null) {
			nextNode = curNode.parent;
			nextLink = curNode.link;
			curNode.parent = preNode;
			curNode.link = preLink;

			if (nextNode != null) {
				if (nextNode.child == nextLink)
					nextNode.child = nextLink.next;
				else {
					Link auxLink = nextNode.child;
					while (auxLink.next != nextLink)
						auxLink = auxLink.next;
					auxLink.next = nextLink.next;
				}

				nextLink.parent = curNode;
				nextLink.child = nextNode;
				nextLink.dir = !nextLink.dir;
				nextLink.next = curNode.child;
				curNode.child = nextLink;

				preLink = nextLink;
				preNode = curNode;
			}

			curNode = nextNode;
		}

		if (enter.dir)
			enterChild.u = enterParent.u - 1;
		else
			enterChild.u = enterParent.u + 1;
		enterChild.level = enterParent.level + 1;
	}


	private void findLoop() {
		int minFlow = 999999999;
		Link curLink = null;
		int flag = 0;

		Node fromNode = enter.parent;
		Node toNode = enter.child;
		from = 0;
		to = 0;
		leave = null;

		while (fromNode.level > toNode.level) {
			curLink = fromNode.link;
			fromLoop[from++] = curLink;
			if (!curLink.dir && curLink.flow < minFlow) {
				minFlow = curLink.flow;
				leave = curLink;
				flag = 0;
			}
			fromNode = fromNode.parent;
		}

		while (toNode.level > fromNode.level) {
			curLink = toNode.link;
			toLoop[to++] = curLink;
			if (curLink.dir && curLink.flow < minFlow) {
				minFlow = curLink.flow;
				leave = curLink;
				flag = 1;
			}
			toNode = toNode.parent;
		}

		// bidirectional search
		while (toNode != fromNode) {
			curLink = fromNode.link;
			fromLoop[from++] = curLink;
			if (!curLink.dir && curLink.flow < minFlow) {
				minFlow = curLink.flow;
				leave = curLink;
				flag = 0;
			}
			fromNode = fromNode.parent;

			curLink = toNode.link;
			toLoop[to++] = curLink;
			if (curLink.dir && curLink.flow < minFlow) {
				minFlow = curLink.flow;
				leave = curLink;
				flag = 1;
			}
			toNode = toNode.parent;
		}

		if (flag == 0) {
			Node curNode = enter.parent;
			enter.parent = enter.child;
			enter.child = curNode;
			enter.dir = !enter.dir;
		}
	}


	private int totalFlow() {
		int f = 0;

		auxQueue[0] = root;
		int queueSize = 1;
		int queueHead = 0;

		Node curNode = null;
		Node nextNode = null;
		Link curLink = null;
		while (queueHead < queueSize) {
			curNode = auxQueue[queueHead++];
			curLink = curNode.child;

			while (curLink != null) {
				f += curLink.flow;
				nextNode = curLink.child;
				curLink = curLink.next;
				auxQueue[queueSize++] = nextNode;
			}
		}

		return f;
	}

	
	public static void main(String[] args) {
		
		int[] hist1 = {0, 0, 0, 0, 0, 0, 0, 0, 1};
		int[] hist2 = {1, 0, 0, 0, 0, 0, 0, 0, 0};
		//int[] hist3 = {1, 2, 3, 4, 5, 6, 7, 8};
		int[] dimensions = {3, 3};
		System.out.println(EMDL1.distance(hist2, hist1, dimensions));
	}
}