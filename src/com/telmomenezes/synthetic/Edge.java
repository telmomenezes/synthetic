package com.telmomenezes.synthetic;

public class Edge {
    private Node origin;
    private Node target;
    private long timestamp;
    private double weight;
    
    public Edge(Node origin, Node target, double weight, long timestamp) {
        this.origin = origin;
        this.target = target;
        this.weight = weight;
        this.timestamp = timestamp;
    }
    
    public Edge(Node origin, Node target, long timestamp) {
        this(origin, target, 0.0, timestamp);
    }
    
    public Edge(Node origin, Node target, double weight) {
        this(origin, target, weight, 0);
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}