package com.telmomenezes.synthetic.generators;

import java.util.Vector;

import com.telmomenezes.synthetic.NetParams;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.gp.Prog;


public class EndoGenerator extends Generator {

	EndoGenerator(NetParams netParams, double sr) {
		super(netParams, sr);
		
		Vector<String> variableNames = new Vector<String>();
		
		if (netParams.getDirected()) {
			variableNames.add("origInDeg");
			variableNames.add("origOutDeg");
			variableNames.add("targInDeg");
			variableNames.add("targOutDeg");
			variableNames.add("dist");
			variableNames.add("dirDist");
			variableNames.add("revDist");
        
			prog = new Prog(7, variableNames);
		}
		else {
			variableNames.add("origDeg");
			variableNames.add("targDeg");
			variableNames.add("dist");
        
			prog = new Prog(3, variableNames);
		}
	} 
	
	
	public Generator instance() {
		return new EndoGenerator(netParams, sr);
	}
	
	
	public Generator clone() {
		Generator generator = new EndoGenerator(netParams, sr);
		generator.prog = prog.clone();
		return generator;
	}
	
	
	protected void setProgVars(int origIndex, int targIndex) {
		Node origNode = net.getNodes()[origIndex];
		Node targNode = net.getNodes()[targIndex];    
		
        double distance = net.uRandomWalkers.getDist(origNode.getId(), targNode.getId());
		
        if (netParams.getDirected()) {
        	double directDistance = net.dRandomWalkers.getDist(origNode.getId(), targNode.getId());
        	double reverseDistance = net.dRandomWalkers.getDist(targNode.getId(), origNode.getId());
                
        	prog.vars[0] = (double)origNode.getInDegree();
        	prog.vars[1] = (double)origNode.getOutDegree();
        	prog.vars[2] = (double)targNode.getInDegree();
        	prog.vars[3] = (double)targNode.getOutDegree();
        	prog.vars[4] = distance;
        	prog.vars[5] = directDistance;
        	prog.vars[6] = reverseDistance;
        }
        else {
        	prog.vars[0] = (double)origNode.getDegree();
        	prog.vars[1] = (double)targNode.getDegree();
        	prog.vars[2] = distance;
        }
	}
	
	
	@Override
	public String toString() {
		return "EndoGenerator -> " + super.toString(); 
	}
}