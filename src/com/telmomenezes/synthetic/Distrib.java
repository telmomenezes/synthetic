package com.telmomenezes.synthetic;

import com.telmomenezes.synthetic.emd.Feature1D;
import com.telmomenezes.synthetic.emd.JFastEMD;
import com.telmomenezes.synthetic.emd.Signature;


public class Distrib {
    private double[] freqs;
    private int bins;
    private double min;
    private double max;
    private double interval;
    
    
    public Distrib(double[] valueSeq, int bins) {
        init(valueSeq, bins);
    }
    
    
    public Distrib(double[] valueSeq, int bins, double min, double max) {
        init(valueSeq, bins, min, max);
    }
    
    
    public Distrib(double[] valueSeq, int bins, Distrib distrib) {
        if (distrib == null) {
            init(valueSeq, bins);
        }
        else {
            init(valueSeq, bins, distrib.getMin(), distrib.getMax());
        }
    }
    
    
    protected void init(double[] valueSeq, int bins, double min, double max) {
        this.min = min;
        this.max = max;
        
        this.bins = bins;
        freqs = new double[bins];
        interval = (max - min) / ((double)this.bins);
        
        for (double x0 : valueSeq) {
            double x = x0;
            if (x < min) {
                x = min;
            }
            else if (x > max) {
                x = max;
            }

            double delta = x - min;
            int pos = (int)(delta / interval);
            if (pos >= this.bins) {
                pos = this.bins - 1;
            }
            freqs[pos]++;
        }
    }
    
    
    protected void init(double[] valueSeq, int bins) {
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        
        for (double x : valueSeq) {
            if (x < min) {
                min = x;
            }
            if (x > max) {
                max = x;
            }
        }
        
        init(valueSeq, bins, min, max);
    }
    
    
    public double total() {
        double t = 0;
        for (double x : freqs) {
            t += x;
        }
        return t;
    }
    
    
    private Signature getEmdSignature() {
        int n = 0;
        for (int x = 0; x < bins; x++) {
            if (freqs[x] > 0) {
                n++;
            }
        }

        Feature1D[] features = new Feature1D[n];
        double[] weights = new double[n];

        int i = 0;
        for (int x = 0; x < bins; x++) {
            double val = freqs[x];
            if (val > 0) {
                Feature1D f = new Feature1D(x);
                features[i] = f;
                weights[i] = val;
                i++;
            }
        }

        Signature signature = new Signature();
        signature.setNumberOfFeatures(n);
        signature.setFeatures(features);
        signature.setWeights(weights);

        return signature;
    }
    
    
    public double emdDistance(Distrib fd)
    {
        double infinity = Double.MAX_VALUE;

        if ((total() <= 0) || (fd.total() <= 0)) {
            return infinity;
        }

        Signature sig1 = getEmdSignature();
        Signature sig2 = fd.getEmdSignature();
        
        return JFastEMD.distance(sig1, sig2, -1);
    }
    
    
    @Override
    public String toString() {
    	String str = "";
        for (int i = 0; i < bins; i++) {
            double start = min + (interval * i);
            double end = start + interval;
            str += "[" + start + ", " + end + "] -> " + freqs[i] + " ";
        }
        
        return str;
    }
    
    
    public static void main(String[] args) {
        double[] seq1 = {0, 0, 0, 1, 1, 1, 10};
        double[] seq2 = {0, 0, 0, 0, 0, 10, 10};
        Distrib d1 = new Distrib(seq1, 10);
        Distrib d2 = new Distrib(seq2, 10);
        System.out.println(d1);
        System.out.println("EMD dist: " + d1.emdDistance(d2));
    }

    
    public double getMin() {
        return min;
    }

    
    public double getMax() {
        return max;
    }
}
