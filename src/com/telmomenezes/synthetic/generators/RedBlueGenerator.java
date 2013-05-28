package com.telmomenezes.synthetic.generators;

import java.util.Vector;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.gp.Prog;
import com.telmomenezes.synthetic.random.RandomGenerator;



public class RedBlueGenerator extends Generator {

	private double blueRatio;
	
	public RedBlueGenerator(Net refNet, double sr) {
		super(refNet, sr);
		
		Vector<String> variableNames = new Vector<String>();
		
		if (refNet.directed) {
			variableNames.add("ovar");
			variableNames.add("tvar");
			variableNames.add("aff");
			variableNames.add("origInDeg");
			variableNames.add("origOutDeg");
			variableNames.add("targInDeg");
			variableNames.add("targOutDeg");
			variableNames.add("dist");
			variableNames.add("dirDist");
			variableNames.add("revDist");
        
			prog = new Prog(10, variableNames);
		}
		else {
			variableNames.add("ovar");
			variableNames.add("tvar");
			variableNames.add("aff");
			variableNames.add("origDeg");
			variableNames.add("targDeg");
			variableNames.add("dist");
        
			prog = new Prog(6, variableNames);
		}
		
		blueRatio = refNet.valueRatio(0);
	} 
	
	
	public Generator instance() {
		return new RedBlueGenerator(refNet, sr);
	}
	
	
	public Generator clone() {
		Generator generator = new RedBlueGenerator(refNet, sr);
		generator.prog = prog.clone();
		return generator;
	}
	
	
	protected void setProgVars(int origIndex, int targIndex) {
		Node origNode = net.getNodes()[origIndex];
		Node targNode = net.getNodes()[targIndex];    
		
        double distance = net.uRandomWalkers.getDist(origNode.getId(), targNode.getId());
        
		prog.vars[0] = labels[origIndex];
		prog.vars[1] = labels[targIndex];
		
		if (origNode.value == targNode.value) {
			prog.vars[2] = 1;
		}
		else {
			prog.vars[2] = 0;
		}
		
        if (refNet.directed) {
        	double directDistance = net.dRandomWalkers.getDist(origNode.getId(), targNode.getId());
        	double reverseDistance = net.dRandomWalkers.getDist(targNode.getId(), origNode.getId());
                
        	prog.vars[3] = (double)origNode.getInDegree();
        	prog.vars[4] = (double)origNode.getOutDegree();
        	prog.vars[5] = (double)targNode.getInDegree();
        	prog.vars[6] = (double)targNode.getOutDegree();
        	prog.vars[7] = distance;
        	prog.vars[8] = directDistance;
        	prog.vars[9] = reverseDistance;
        }
        else {
        	prog.vars[3] = (double)origNode.getDegree();
        	prog.vars[4] = (double)targNode.getDegree();
        	prog.vars[5] = distance;
        }
	}
	
	
	protected void createNodes() {
        for (int i = 0; i < refNet.nodeCount; i++) {
            Node node = net.addNode();
            
            if (RandomGenerator.random.nextDouble() < blueRatio) {
            	node.value = 0;
            }
            else {
            	node.value = 1;
            }
            
            labels[i] = RandomGenerator.random.nextDouble();
        }
	}
	
	
	@Override
	public String toString() {
		return "RedBlueGenerator -> " + super.toString(); 
	}
}