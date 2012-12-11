package com.telmomenezes.synthetic.samplers;

import com.telmomenezes.synthetic.Net;

public class DownSampler {
    
    private double step;
    private double attenuation;
    private int maxNodes;
    private int origNodes;
    private ForestFire ff;
    private double ratio;
    
    public DownSampler(Net net, double attenuation, int maxNodes) {
        ff = new ForestFire(net);
        this.attenuation = attenuation;
        this.maxNodes = maxNodes;
        this.origNodes = net.getNodeCount();
        step = 0.0;
        ratio = 1.0;
    }
    
    private Net sampleDown() {
        while (true) {
            step += 1.0;
            ratio = Math.exp(-step / attenuation);
            int sampleNodes = (int)(ratio * (double)origNodes);
            if (sampleNodes <= maxNodes) {
                Net sample = ff.sample(ratio);
                return sample;
            }
        }
    }
    
    public static Net sample(Net net, int maxNodes, int maxEdges) {
    	Net sampleNet = net;
     	DownSampler sampler = new DownSampler(net, 5, maxNodes);
     	while ((sampleNet.getNodeCount() > maxNodes) || (sampleNet.getEdgeCount() > maxEdges)) {
     		sampleNet = sampler.sampleDown();
     		double samplingRatio = sampler.getRatio();
     		System.out.println("sampling down: " + samplingRatio + "; nodes: " + sampleNet.getNodeCount() + "; edges: " + sampleNet.getEdgeCount());
     	}
     	
     	return sampleNet;
    }

    public double getRatio() {
        return ratio;
    }
}