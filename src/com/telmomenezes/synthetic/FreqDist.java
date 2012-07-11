package com.telmomenezes.synthetic;

import com.telmomenezes.synthetic.emd.Feature1D;
import com.telmomenezes.synthetic.emd.JFastEMD;
import com.telmomenezes.synthetic.emd.Signature;


public class FreqDist {
    private double[] freqs;
    private int bins;
    private double min;
    private double max;
    private double interval;
    
    public FreqDist(double[] valueSeq, int bins) {
        this.bins = bins;
        freqs = new double[bins];
        
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
        
        interval = (max - min) / ((double)this.bins);
        
        for (double x : valueSeq) {
            double delta = x - min;
            int pos = (int)(delta / interval);
            if (pos >= this.bins) {
                pos = this.bins - 1;
            }
            freqs[pos]++;
        }
    }
    
    public double total() {
        double t = 0;
        for (double x : freqs) {
            t += x;
        }
        return t;
    }
    
    private Signature getEmdSignature()
    {
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
    
    public double emdDistance(FreqDist fd)
    {
        double infinity = Double.MAX_VALUE;

        if ((total() <= 0) || (fd.total() <= 0)) {
            return infinity;
        }

        Signature sig1 = getEmdSignature();
        Signature sig2 = fd.getEmdSignature();
        
        return JFastEMD.distance(sig1, sig2, -1);
    }
    
    public void print() {
        for (int i = 0; i < bins; i++) {
            double start = min + (interval * i);
            double end = start + interval;
            System.out.println("bin " + i + " [" + start + ", " + end + "] -> \t" + freqs[i]);
        }
    }
    
    public static void main(String[] args) {
        double[] seq1 = {0, 0, 0, 1, 1, 1, 10};
        double[] seq2 = {0, 0, 0, 0, 0, 10, 10};
        FreqDist fd1 = new FreqDist(seq1, 10);
        FreqDist fd2 = new FreqDist(seq2, 10);
        fd1.print();
        System.out.println("EMD dist: " + fd1.emdDistance(fd2));
    }
}
