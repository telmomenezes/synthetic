package com.telmomenezes.synthetic;


public class Edge {
    private Node origin;
    private Node target;
    
    
    public Edge(Node origin, Node target) {
        this.origin = origin;
        this.target = target;
    }
    
    
    public Node getOrigin() {
        return origin;
    }

    
    public Node getTarget() {
        return target;
    }
    
    
    @Override
    public String toString() {
    	String str = "orig: " + origin;
    	str += "; targ: " + target;
    	return str;
    }
}