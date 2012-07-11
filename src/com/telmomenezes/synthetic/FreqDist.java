package com.telmomenezes.synthetic;

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
    
    public void print() {
        for (int i = 0; i < bins; i++) {
            double start = min + (interval * i);
            double end = start + interval;
            System.out.println("bin " + i + " [" + start + ", " + end + "] -> \t" + freqs[i]);
        }
    }
    
    public static void main(String[] args) {
        double[] seq = {0, 0, 0, 1, 1, 1, 10};
        FreqDist fd = new FreqDist(seq, 10);
        fd.print();
    }
}
