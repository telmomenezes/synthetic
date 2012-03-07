package com.telmomenezes.synthetic;


/**
 * A base class for agent models aimed at generating networks that are similar
 * to some target, known network.
 * 
 * This type of model is used when the goal of the multi-agent simulation is
 * to generate a network that approximates some know network, possibly to
 * create an hypothesis for the low level phenomena that underlay some real 
 * world network.
 * 
 * The distance between networks is defined as the earth mover's distance
 * between histograms that are generated according to a set of metrics.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public abstract class MetricsModel extends Model {

	protected MetricsModel targetModel;
	protected MetricSet metricSet;
	protected Histogram histogram;


	public MetricsModel() {
		super();
		
		targetModel = null;
		metricSet = null;
		histogram = null;
	}


	public abstract void generateHistogram();
	
	
	public void copy(Model model) {
		MetricsModel metMod = (MetricsModel)model;
		targetModel = metMod.targetModel;
		metricSet = metMod.metricSet;
	}
	
	
	public double computeFitness()
	{
		generateHistogram();
		fitness = distance(targetModel);
		return fitness;
	}
	
	
	public double distance(Model model)
	{
		MetricsModel mm = (MetricsModel)model;
		return histogram.distance(mm.histogram);
	}
	
	
	public void setTargetModel(MetricsModel targetModel) {
		this.targetModel = targetModel;
		this.metricSet = targetModel.metricSet;
	}


	public MetricSet getMetricSet() {
		return metricSet;
	}


	public Histogram getHistogram() {
		return histogram;
	}
}