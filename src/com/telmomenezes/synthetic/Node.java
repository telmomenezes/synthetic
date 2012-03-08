package com.telmomenezes.synthetic;

public class Node {
    // node list
    private Node next;

    private int id;
    private Edge targets;
    private Edge origins;
    private int inDegree;
    private int outDegree;
    private int birth;

    // pageranks
    private double prIn;
    private double prInLast;
    private double prOut;
    private double prOutLast;

    // for generators
    private double genweight;

    // Auxiliary flag for algorithms that need to know if this node was already
    // visited
    private boolean flag;

    public Node(int id) {
        this.id = id;
        inDegree = 0;
        outDegree = 0;
        birth = -1;
        targets = null;
        origins = null;
    }

    public boolean addEdge(Node target, long timestamp) {
        if (this == target) {
            return false;
        }

        if (edgeExists(target)) {
            return false;
        }

        Edge edge = new Edge();
        edge.setOrig(this);
        edge.setTarg(target);
        edge.setTimestamp(timestamp);
        edge.setNextTarg(targets);
        targets = edge;
        outDegree++;
        edge.setNextOrig(target.origins);
        target.origins = edge;
        target.inDegree++;

        return true;
    }

    public boolean edgeExists(Node target) {
        Edge edge = targets;

        while (edge != null) {
            if (edge.getTarg() == target) {
                return true;
            }
            edge = edge.getNextTarg();
        }

        return false;
    }

    Node getNext() {
        return next;
    }

    void setNext(Node next) {
        this.next = next;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    Edge getTargets() {
        return targets;
    }

    void setTargets(Edge targets) {
        this.targets = targets;
    }

    Edge getOrigins() {
        return origins;
    }

    void setOrigins(Edge origins) {
        this.origins = origins;
    }

    int getInDegree() {
        return inDegree;
    }

    void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    int getOutDegree() {
        return outDegree;
    }

    void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    int getBirth() {
        return birth;
    }

    void setBirth(int birth) {
        this.birth = birth;
    }

    public double getPrIn() {
        return prIn;
    }

    public void setPrIn(double prIn) {
        this.prIn = prIn;
    }

    double getPrInLast() {
        return prInLast;
    }

    void setPrInLast(double prInLast) {
        this.prInLast = prInLast;
    }

    public double getPrOut() {
        return prOut;
    }

    public void setPrOut(double prOut) {
        this.prOut = prOut;
    }

    double getPrOutLast() {
        return prOutLast;
    }

    void setPrOutLast(double prOutLast) {
        this.prOutLast = prOutLast;
    }

    double getGenweight() {
        return genweight;
    }

    void setGenweight(double genweight) {
        this.genweight = genweight;
    }

    boolean isFlag() {
        return flag;
    }

    void setFlag(boolean flag) {
        this.flag = flag;
    }
}