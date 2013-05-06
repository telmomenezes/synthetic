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

	private int[] steps;

	public RandomWalker(Node node, boolean directed, int maxLength) {
		this.orig = node;
		
		this.directed = directed;
		this.maxLength = maxLength;
		
		steps = new int[maxLength + 1];
		
		restart();
	}
	
	
	public void restart() {
		targ = orig;
		length = 0;
		forward = RandomGenerator.random.nextBoolean();
	}
	
	
	private boolean repeated(int id) {
		for (int i = 0; i < length; i++) {
			if (steps[i] == id) {
				return true;
			}
		}
		
		return false;
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
		else if (orig == next) {
			restart();
		}
		else if (repeated(next.getId())) {
			restart();
		}
		else {
			steps[length] = next.getId();
			length++;
			
			targ = next;
			
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
