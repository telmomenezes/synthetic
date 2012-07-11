package com.telmomenezes.synthetic.emd;

/**
 * @author Telmo Menezes (telmo@telmomenezes.com)
 *
 */
public class Feature1D implements Feature {
    private double x;

    public Feature1D(double x) {
        this.x = x;
    }
    
    public double groundDist(Feature f) {
        Feature1D f1d = (Feature1D)f;
        return Math.abs(x - f1d.x);
    }
}