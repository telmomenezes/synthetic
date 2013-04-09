package com.telmomenezes.synthetic.motifs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;


public abstract class TriadicProfile {
    
	protected Net net;
    protected long[] profile;
    protected int numberOfMotifs;

    
    public TriadicProfile(Net net) {
        this.net = net;
    }
    

    public static TriadicProfile create(Net net) {
    	if (net.isDirected()) {
    		return new DTriadicProfile(net);
    	}
    	else {
    		return new UTriadicProfile(net);
    	}
    }
    
    
    protected abstract int triadType(Node a, Node b, Node c);
    
    
    protected abstract double motifEdges(int motif);

    
    protected void triadProfile() {
        Node[] triad = new Node[3];
        profile = new long[numberOfMotifs];

        for (int i = 0; i < numberOfMotifs; i++) {
            profile[i] = 0;
        }

        // search for triads starting on each node
        for (Node node : net.getNodes()) {
            triad[0] = node;
            triadProfileRec(triad, 0, profile);
        }
    }
    
    
    protected void updateTriadProfile(Node[] triad, long[] profile) {
        int type = triadType(triad[0], triad[1], triad[2]);
        if (type < 0)
            type = triadType(triad[0], triad[2], triad[1]);
        if (type < 0)
            type = triadType(triad[1], triad[0], triad[2]);
        if (type < 0)
            type = triadType(triad[1], triad[2], triad[0]);
        if (type < 0)
            type = triadType(triad[2], triad[0], triad[1]);
        if (type < 0)
            type = triadType(triad[2], triad[1], triad[0]);

        if (type < 0) {
            //System.out.println("negative type!");
            return;
        }

        profile[type - 1]++;
    }
    
    
    protected void triadProfileRec(Node[] triad, int depth, long[] profile) {
        if (depth == 2) {
            updateTriadProfile(triad, profile);
            return;
        }

        Node node = triad[depth];

        for (Edge orig : node.getInEdges()) {
            Node nextNode = orig.getOrigin();
            if (nextNode.getId() > triad[depth].getId()) {
                triad[depth + 1] = nextNode;
                triadProfileRec(triad, depth + 1, profile);
            }
        }

        for (Edge targ : node.getOutEdges()) {
            Node nextNode = targ.getTarget();
            if (nextNode.getId() > triad[depth].getId()) {
                triad[depth + 1] = nextNode;
                triadProfileRec(triad, depth + 1, profile);
            }
        }
    }
    
    
    public long total() {
    	long total = 0;
    	for (int i = 0; i < numberOfMotifs; i++) {
    		total += profile[i];
    	}
    	return total;
    }
    
    
    public double simpleDistance(TriadicProfile tp) {
    	double distance = 0;
    	
        for (int i = 0; i < numberOfMotifs; i++) {
            distance += Math.abs(profile[i] - tp.profile[i]) * (i + 1);
        }

        return distance;
    }
    
    
    public double proportionalDistance(TriadicProfile tp, TriadicProfile rp) {
    	double distance = 0;
    	
        for (int i = 0; i < numberOfMotifs; i++) {
        	double d = tp.profile[i];
            if (d <= 0) {
            	d = 1;
            }
            
            double r = rp.profile[i];
            if (r <= 0) {
            	r = 1;
            }
            
        	//distance += (Math.abs((profile[i] / r) - (tp.profile[i] / r)) / d);// * motifEdges(i + 1);
            distance += (Math.abs(profile[i] - tp.profile[i]) / d) * motifEdges(i + 1);
        }

        return distance;
    }
    
    
    public double maxError(TriadicProfile tp) {
    	double maxError = 0;
    	
        for (int i = 0; i < numberOfMotifs; i++) {
        	double d = tp.profile[i];
            if (d <= 0) {
            	d = 1;
            }
            
            double error = (Math.abs(profile[i] - tp.profile[i]) / d);
            
            if (error > maxError) {
            	maxError = error;
            }
        }

        return maxError;
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

            for (int i = 0; i < numberOfMotifs; i++) {
                out.write("" + i + "," + profile[i] + '\n');
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
    			
    	for (int i = 0; i < numberOfMotifs; i++) {
    		str += "[" + (i + 1) + "]:" + profile[i] + " ";
    	}
    	
    	return str;
    }
    
    
    public long[] getProfile() {
        return profile;
    }
}