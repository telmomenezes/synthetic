package com.telmomenezes.synthetic;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Represents a metric, to be part of a MetricSet. 
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Metric {

	private int type;
	private int divExp;
	private double[] limits;
	
	
	public Metric(int type, int divExp) {
		this.type = type;
		this.divExp = divExp;
		limits = new double[(2 << (divExp - 1)) - 1];
	}

	
	private void buildLimits(List<Double> values, List<Double> limits, int maxDepth) {
		if (maxDepth <= 0)
			return;
		
		double mean = 0;
		for (Double d : values)
			mean += d;
		
		if (values.size() > 0)
			mean /= (double)(values.size());
		
		limits.add(mean);
		
		List<Double> l1 = new LinkedList<Double>();
		List<Double> l2 = new LinkedList<Double>();
		
		for (Double d : values)
			if (d < mean)
				l1.add(d);
			else
				l2.add(d);
		
		buildLimits(l1, limits, maxDepth - 1);
		buildLimits(l2, limits, maxDepth - 1);
	}
	
	
	
	public void setLimits(double[] vector) {
		List<Double> values = new LinkedList<Double>();
		for (double x : vector) {
			values.add(x);
		}
		
		List<Double> lims = new LinkedList<Double>();
		buildLimits(values, lims, divExp);
		
		Collections.sort(lims);

		int pos = 0;
		for (double x : lims) {
			limits[pos++] = x;
		}
	}
	
	
	public int classify(double value) {

		for (int i = 0; i < limits.length; i++)
			if (value < limits[i])
				return i;
		
		return limits.length;
	}

	
	public int getType() {
		return type;
	}


	public int getClasses() {
		return 2 << (divExp - 1);
	}


	public double[] getLimits() {
		return limits;
	}
	
	
	public String toString() {
		String str = "Metric type: " + type;
		str += "; numb. classes: " + getClasses();
		str += "\n";
		str += "limits:";
		for (int i = 0; i < (getClasses() - 1); i++)
			str += " " + limits[i];
		return str;
	}
}
