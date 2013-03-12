package com.telmomenezes.synthetic.randomwalkers;

import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.random.RandomGenerator;


public class RandomWalker {
	private Node orig;
	private Node targ;
	private int length;
	private boolean directed;
	private int maxLength;
	private boolean forward;


	public RandomWalker(Node node, boolean directed, int maxLength) {
		this.orig = node;
		
		this.directed = directed;
		this.maxLength = maxLength;
		
		restart();
	}
	
	
	public void restart() {
		targ = orig;
		length = 0;
		forward = RandomGenerator.instance().random.nextBoolean();
	}
	
	
	public void step() {
		Node next = null;
		if (directed) {
			if (forward) {
				next = targ.getRandomOutputNode();
			}
			else {
				next = targ.getRandomInputNode();
			}
		}
		else {
			next = targ.getRandomNeighbour();
		}
		if (next == null) {
			restart();
		}
		else {
			targ = next;
			length++;
			
			if (length > maxLength) {
				restart();
			}
		}
	}


	public Node getOrig() {
		return orig;
	}
	
	
	public Node getTarg() {
		return targ;
	}
	
	
	public int getLength() {
		return length;
	}
	
	
	public boolean isForward() {
		return forward;
	}
}
