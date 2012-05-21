package com.telmomenezes.synthetic.samplers;

import com.telmomenezes.synthetic.Net;

public class DownSampler {
    
    private double step;
    private double attenuation;
    private ForestFire ff;
    private double ratio;
    
    public DownSampler(Net net, double attenuation) {
        ff = new ForestFire(net);
        this.attenuation = attenuation;
        step = 0.0;
    }
    
    public Net sampleDown() {
        step += 1.0;
        ratio = Math.exp(-step / attenuation);
        Net sample = ff.sample(ratio);
        return sample;
    }

    public double getRatio() {
        return ratio;
    }
}