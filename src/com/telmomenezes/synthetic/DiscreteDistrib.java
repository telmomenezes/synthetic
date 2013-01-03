package com.telmomenezes.synthetic;


import java.io.BufferedWriter;
import java.io.FileWriter;

import com.telmomenezes.synthetic.emd.Feature1D;
import com.telmomenezes.synthetic.emd.JFastEMD;
import com.telmomenezes.synthetic.emd.Signature;


public class DiscreteDistrib {
    private int[] freqs;
    private int max;
    
    
    public DiscreteDistrib(int[] valueSeq) {
        init(valueSeq);
    }
    
    
    public DiscreteDistrib(int[] valueSeq, int max) {
        init(valueSeq, max);
    }
    
    
    public DiscreteDistrib(int[] valueSeq, DiscreteDistrib distrib) {
        if (distrib == null) {
            init(valueSeq);
        }
        else {
            init(valueSeq, distrib.getMax());
        }
    }
    
    
    protected void init(int[] valueSeq, int max) {
        this.max = max;
        
        freqs = new int[max + 1];
        
        for (int x0 : valueSeq) {
            int x = x0;
            
            if (x > max) {
                x = max;
            }

            freqs[x]++;
        }
    }
    
    
    protected void init(int[] valueSeq) {
    	max = 0;
        for (int x : valueSeq) {
        	if (x > max) {
        		max = x;
        	}
        }
        
        init(valueSeq, max);
    }
    
    
    public int total() {
        int t = 0;
        for (int x : freqs) {
            t += x;
        }
        return t;
    }
    
    
    private Signature getEmdSignature() {
        int n = 0;
        for (int x = 0; x < (max + 1); x++) {
            if (freqs[x] > 0) {
                n++;
            }
        }

        Feature1D[] features = new Feature1D[n];
        double[] weights = new double[n];

        int i = 0;
        for (int x = 0; x < (max + 1); x++) {
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
    
    
    public double emdDistance(DiscreteDistrib fd)
    {
        double infinity = Double.MAX_VALUE;

        if ((total() <= 0) || (fd.total() <= 0)) {
            return infinity;
        }

        Signature sig1 = getEmdSignature();
        Signature sig2 = fd.getEmdSignature();
        
        return JFastEMD.distance(sig1, sig2, -1);
    	
    	
    	/*double dist = 0;
    	for (int i = 0; i < freqs.length; i++) {
    		dist += Math.abs(freqs[i] - fd.freqs[i]);
    	}
    	
    	return dist;*/
    }
    
    
    public void write(String filePath) {
    	try{ 
            FileWriter fstream = new FileWriter(filePath);
            BufferedWriter out = new BufferedWriter(fstream);
            
            out.write("value,freq\n");
            for (int i = 0; i < freqs.length; i++) {
                out.write("" + i + "," + freqs[i] + '\n');
            }
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public String toString() {
    	String str = "";
        for (int i = 0; i < (max + 1); i++) {
            str += "[" + i + "] -> " + freqs[i] + " ";
        }
        
        return str;
    }
    

    public int getMax() {
        return max;
    }
}