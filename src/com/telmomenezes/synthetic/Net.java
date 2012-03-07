package com.telmomenezes.synthetic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Net {
	
	protected int verticesCount;
	protected HashMap<Integer, HashMap<Integer, Double>> inEdges;
	protected HashMap<Integer, HashMap<Integer, Double>> outEdges;
	
	
	public Net(int verticesCount) {
		this.verticesCount = verticesCount;
		inEdges = new HashMap<Integer, HashMap<Integer, Double>>();
		outEdges = new HashMap<Integer, HashMap<Integer, Double>>();
	}
	
	public boolean edgeExists(int origin, int target)
	{
		if (outEdges.containsKey(origin)) {
			HashMap<Integer, Double> origEdges = outEdges.get(origin);
			
			if (origEdges.containsKey(target))
				return true;
		}
		
		return false;
	}
	
	public void setEdge(int origin, int target, double value)
	{
		// add edge to outEdges
		HashMap<Integer, Double> origEdges;
		
		origEdges = outEdges.get(origin);
		
		if (origEdges == null) {
			origEdges = new HashMap<Integer, Double>();
			outEdges.put(origin, origEdges);
		}
		
		origEdges.put(target, value);
		
		// add edge to inEdges
		HashMap<Integer, Double> targEdges;
		
		targEdges = inEdges.get(target);
		
		if (targEdges == null) {
			targEdges = new HashMap<Integer, Double>();
			inEdges.put(target, targEdges);
		}
		
		targEdges.put(origin, value);
	}
	
	public int inDegree(int node)
	{
		HashMap<Integer, Double> targEdges = inEdges.get(node);
		
		if (targEdges == null)
			return 0;
		else
			return targEdges.size();
	}
	
	public int outDegree(int node)
	{
		HashMap<Integer, Double> origEdges = outEdges.get(node);
		
		if (origEdges == null)
			return 0;
		else
			return origEdges.size();
	}
	
	public Set<Integer> getNeighbors(int node)
	{
		Set<Integer> neighbors = new HashSet<Integer>();
		
		HashMap<Integer, Double> out = outEdges.get(node);
		for (int target : out.keySet())
			neighbors.add(target);
		
		HashMap<Integer, Double> in = inEdges.get(node);
		for (int origin : in.keySet())
			neighbors.add(origin);
		
		return neighbors;
	}
	
	public double leadership(int node)
	{
		Set<Integer> neighborhood = getNeighbors(node);
		
		// find max in-degree
		int maxDegree = 0;		
		for (int ni : neighborhood) {
			int degree = inDegree(ni);
			if(degree > maxDegree)
				maxDegree = degree;
		}
		
		// summation of differences to max in-degree
		int sum = 0;
		for (int ni : neighborhood) {
			int degree = inDegree(ni);
			sum += maxDegree - degree;
		}
		
		// neighborhood size
		int n = neighborhood.size();
		
		// calc leadership
		double l = ((double)sum) / ((double)((n- 2) * (n - 1)));
		
		return l;
	}
	
	public double bonding(int node)
	{
		Set<Integer> neighborhood = getNeighbors(node);
		
		// find number of edges in the neighborhood
		int edgeCount = 0;
		
		for (int origin : neighborhood) {
			HashMap<Integer, Double> origEdges = outEdges.get(origin);
			
			for (int target : origEdges.keySet())
				if (neighborhood.contains(target))
					edgeCount++;
		}
		
		// neighborhood size
		int n = neighborhood.size();
	
		// calc bonding
		double b = ((double)edgeCount) / ((double)(n * (n - 1)));
		
		return b;
	}
}
