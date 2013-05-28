package com.telmomenezes.synthetic.generators;

import java.util.Vector;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.gp.Prog;
import com.telmomenezes.synthetic.random.RandomGenerator;



public class ExoGenerator extends Generator {

	public ExoGenerator(Net refNet, double sr) {
		super(refNet, sr);
		
		Vector<String> variableNames = new Vector<String>();
		
		if (refNet.directed) {
			variableNames.add("ovar");
			variableNames.add("tvar");
			variableNames.add("origInDeg");
			variableNames.add("origOutDeg");
			variableNames.add("targInDeg");
			variableNames.add("targOutDeg");
			variableNames.add("dist");
			variableNames.add("dirDist");
			variableNames.add("revDist");
        
			prog = new Prog(9, variableNames);
		}
		else {
			variableNames.add("ovar");
			variableNames.add("tvar");
			variableNames.add("origDeg");
			variableNames.add("targDeg");
			variableNames.add("dist");
        
			prog = new Prog(5, variableNames);
		}
	} 
	
	
	public Generator instance() {
		return new ExoGenerator(refNet, sr);
	}
	
	
	public Generator clone() {
		Generator generator = new ExoGenerator(refNet, sr);
		generator.prog = prog.clone();
		return generator;
	}
	
	
	protected void setProgVars(int origIndex, int targIndex) {
		Node origNode = net.getNodes()[origIndex];
		Node targNode = net.getNodes()[targIndex];    
		
        double distance = net.uRandomWalkers.getDist(origNode.getId(), targNode.getId());
        
		prog.vars[0] = labels[origIndex];
		prog.vars[1] = labels[targIndex];
		
        if (refNet.directed) {
        	double directDistance = net.dRandomWalkers.getDist(origNode.getId(), targNode.getId());
        	double reverseDistance = net.dRandomWalkers.getDist(targNode.getId(), origNode.getId());
                
        	prog.vars[2] = (double)origNode.getInDegree();
        	prog.vars[3] = (double)origNode.getOutDegree();
        	prog.vars[4] = (double)targNode.getInDegree();
        	prog.vars[5] = (double)targNode.getOutDegree();
        	prog.vars[6] = distance;
        	prog.vars[7] = directDistance;
        	prog.vars[8] = reverseDistance;
        }
        else {
        	prog.vars[2] = (double)origNode.getDegree();
        	prog.vars[3] = (double)targNode.getDegree();
        	prog.vars[4] = distance;
        }
	}
	
	
	protected void createNodes() {
        for (int i = 0; i < refNet.nodeCount; i++) {
            net.addNode();
            labels[i] = RandomGenerator.random.nextDouble();
        }
	}
	
	
	@Override
	public String toString() {
		return "ExoGenerator -> " + super.toString(); 
	}
}