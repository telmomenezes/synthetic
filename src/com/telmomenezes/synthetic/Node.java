package com.telmomenezes.synthetic;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private int id;
    private Set<Edge> inEdges;
    private Set<Edge> outEdges;
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
        inEdges = new HashSet<Edge>();
        outEdges = new HashSet<Edge>();
    }

    public int getId() {
        return id;
    }

    Set<Edge> getInEdges() {
        return inEdges;
    }
    
    Set<Edge> getOutEdges() {
        return outEdges;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
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

    public double getGenweight() {
        return genweight;
    }

    public void setGenweight(double genweight) {
        this.genweight = genweight;
    }

    boolean isFlag() {
        return flag;
    }

    void setFlag(boolean flag) {
        this.flag = flag;
    }
    
    void addInEdge(Edge edge) {
        inEdges.add(edge);
        inDegree++;
    }
    
    void addOutEdge(Edge edge) {
        outEdges.add(edge);
        outDegree++;
    }
}