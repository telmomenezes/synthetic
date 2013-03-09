package com.telmomenezes.synthetic;

import java.util.HashSet;
import java.util.Set;

public class Node implements Cloneable {
    private int id;
    private Set<Edge> inEdges;
    private Set<Edge> outEdges;
    private int inDegree;
    private int outDegree;

    // pageranks
    private double prD;
    private double prDLast;
    private double prU;
    private double prULast;

    // for generators
    private double genweight;

    // Auxiliary flag for algorithms that need to know if this node was already
    // visited
    private boolean flag;

    public Node(int id) {
        this.id = id;
        inDegree = 0;
        outDegree = 0;
        inEdges = new HashSet<Edge>();
        outEdges = new HashSet<Edge>();
    }
    
    @Override
    public Node clone()
    {
        Node clonedNode = new Node(id);
        return clonedNode;
    }

    public int getId() {
        return id;
    }

    public Set<Edge> getInEdges() {
        return inEdges;
    }
    
    public Set<Edge> getOutEdges() {
        return outEdges;
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }
    
    public int getDegree() {
        return inDegree + outDegree;
    }

    public double getPrD() {
        return prD;
    }

    public void setPrD(double prD) {
        this.prD = prD;
    }

    double getPrDLast() {
        return prDLast;
    }

    void setPrDLast(double prDLast) {
        this.prDLast = prDLast;
    }

    public double getPrU() {
        return prU;
    }

    public void setPrU(double prU) {
        this.prU = prU;
    }

    double getPrULast() {
        return prULast;
    }

    void setPrULast(double prULast) {
        this.prULast = prULast;
    }

    public double getGenweight() {
        return genweight;
    }

    public void setGenweight(double genweight) {
        this.genweight = genweight;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
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
    
    public void removeInput(Edge edge) {
        inEdges.remove(edge);
        inDegree--;
    }
    
    public void removeOutput(Edge edge) {
        outEdges.remove(edge);
        outDegree--;
    }
    
    public Node getRandomOutputNode() {
    	if (outEdges.size() == 0) {
    		return null;
    	}
    	
    	int size = outEdges.size();
    	int index = RandomGenerator.instance().random.nextInt(size);
    	int i = 0;
    	for(Edge edge : outEdges)
    	{
    	    if (i == index) {
    	        return edge.getTarget();
    	    }
    	    i++;
    	}
    	
    	return null;
    }
    
    
    public Node getRandomInputNode() {
    	if (inEdges.size() == 0) {
    		return null;
    	}
    	
    	int size = inEdges.size();
    	int index = RandomGenerator.instance().random.nextInt(size);
    	int i = 0;
    	for(Edge edge : inEdges)
    	{
    	    if (i == index) {
    	        return edge.getOrigin();
    	    }
    	    i++;
    	}
    	
    	return null;
    }
    
    
    public Node getRandomNeighbour() {
    	double ins = inDegree;
    	double outs = outDegree;
    	double total = ins + outs;
    	
    	if (total <= 0) {
    		return null;
    	}
    	
    	double probIn = ins / total;
    	
    	if (RandomGenerator.instance().random.nextDouble() < probIn) {
    		return getRandomInputNode();
    	}
    	else {
    		return getRandomOutputNode();
    	}
    }
}