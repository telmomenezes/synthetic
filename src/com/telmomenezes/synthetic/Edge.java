package com.telmomenezes.synthetic;

public class Edge {
    private Node origin;
    private Node target;
    private long timestamp;
    
    public Edge(Node origin, Node target, long timestamp) {
        this.origin = origin;
        this.target = target;
        this.timestamp = timestamp;
    }
    
    public Edge(Node origin, Node target) {
        this(origin, target, 0);
    }
    
    public Node getOrigin() {
        return origin;
    }

    public Node getTarget() {
        return target;
    }

    public long getTimestamp() {
        return timestamp;
    }
}