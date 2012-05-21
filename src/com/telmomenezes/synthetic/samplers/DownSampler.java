package com.telmomenezes.synthetic.samplers;

import com.telmomenezes.synthetic.Net;

public class DownSampler {
    
    private double step;
    private double attenuation;
    private ForestFire ff;
    
    public DownSampler(Net net, double attenuation) {
        ff = new ForestFire(net);
        this.attenuation = attenuation;
        step = 0.0;
    }
    
    public Net sampleDown() {
        step += 1.0;
        double percent = Math.exp(-step / attenuation);
        Net sample = ff.sample(percent);
        return sample;
    }

    public double getStep() {
        return step;
    }
}