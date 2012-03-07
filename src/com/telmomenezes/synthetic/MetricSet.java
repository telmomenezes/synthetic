package com.telmomenezes.synthetic;


import java.util.Vector;


/**
 * A set of metrics that can be used to generate an histogram. 
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class MetricSet {

	private Vector<Metric> metrics;
	
	
	public MetricSet() {
		metrics = new Vector<Metric>();
	}
	
	
	public void addMetric(int type, int classes) {
		Metric m = new Metric(type, classes);
		metrics.add(m);
	}
	
	
	public Histogram generateHistogram(double[][] cases) {
		
		// number of classes & dimensions array
		int[] dimensions = new int[metrics.size()];
		int classCount = 1;
		int dimPos = 0;
		for (Metric m : metrics) {
			int classes = m.getClasses();
			classCount *= classes;
			dimensions[dimPos++] = classes;
		}
		
		int[] classes = new int[classCount];
		
		// classify
		for (double[] c : cases) {
			int factor = classCount;
			int index = 0;
			int pos = 0;
			for (Metric m : metrics) {
				factor /= m.getClasses();
				int classif = m.classify(c[pos++]);
				index +=  classif * factor;
			}
			
			classes[index]++;
		}
		
		Histogram h = new Histogram(classes, dimensions);
		return h;
	}

	
	public String toString() {
		String str = "";
		
		for (Metric m : metrics) {
			str += m;
			str += "\n";
		}
		
		return str;
	}

	
	public Vector<Metric> getMetrics() {
		return metrics;
	}
	
	
	public int getMetricsCount() {
		return metrics.size();
	}
}