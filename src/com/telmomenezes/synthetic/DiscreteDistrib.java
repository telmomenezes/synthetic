package com.telmomenezes.synthetic;

import java.io.BufferedWriter;
import java.io.File;
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
            
            if (x <= max) {
            	freqs[x]++;
            }
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
        int n = max + 1;

        Feature1D[] features = new Feature1D[n];
        double[] weights = new double[n];

        for (int i = 0; i < n; i++) {
            Feature1D f = new Feature1D(i);
            features[i] = f;
            weights[i] = freqs[i];
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
    
    
    public double simpleDistance(DiscreteDistrib fd)
    {
        double dist = 0;
    	for (int i = 0; i < freqs.length; i++) {
    		dist += Math.abs(freqs[i] - fd.freqs[i]);// * (i + 1);
    	}
    	
    	return dist;
    }
    
    
    public double proportionalDistance(DiscreteDistrib fd)
    {
        double dist = 0;
    	for (int i = 0; i < freqs.length; i++) {
    		double d = fd.freqs[i];
    		if (d == 0) {
    			d = 1;
    		}
    		dist += (Math.abs(freqs[i] - fd.freqs[i]) / d) * (i + 1);
    	}
    	
    	return dist;
    }
    
    
    public void write(String filePath, boolean append) {
    	try{
    		boolean header = !append;
    		if (append) {
    			File f = new File(filePath);
    			if(f.exists()) {
    				header = false;
    			}
    		}
    		
            FileWriter fstream = new FileWriter(filePath, append);
            BufferedWriter out = new BufferedWriter(fstream);
            
            if(header) {
            	out.write("value,freq\n");
            }
            
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
        int sum = 0;
    	for (int i = 0; i < (max + 1); i++) {
            str += "[" + i + "] -> " + freqs[i] + " ";
            sum += freqs[i];
        }
        
    	str += "sum: " + sum;
    	
        return str;
    }
    

    public int getMax() {
        return max;
    }
    
    
    public static void main(String[] args) {
    	int[] seq1 = {1, 1, 0, 0, 0, 0};
    	int[] seq2 = {1, 0, 0, 0, 5, 5};
    	
    	DiscreteDistrib d1 = new DiscreteDistrib(seq1);
    	DiscreteDistrib d2 = new DiscreteDistrib(seq2);
    	System.out.println(d1.emdDistance(d2));
    }
}