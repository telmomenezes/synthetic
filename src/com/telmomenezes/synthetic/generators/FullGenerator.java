package com.telmomenezes.synthetic.generators;


import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;


/**
 * Network generator.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class FullGenerator extends Generator {
	
	public FullGenerator(int nodeCount, int edgeCount) {
		super(nodeCount, edgeCount);
	}
	
	 
	@Override
	public Generator instance() {
		Generator generator = new FullGenerator(nodeCount, edgeCount);
		return generator;
	}
	
	
	@Override
	public Generator clone() {
		Generator generator = new FullGenerator(nodeCount, edgeCount);
		generator.prog = prog.clone();
		return generator;
	}
	
    
	@Override
	public void run() {
        // reset eval stats
        prog.clearEvalStats();
        
        // init DistMatrix
        DistMatrix distMatrix = new DistMatrix(nodeCount);

        net = new Net();

        // create nodes
        Node[] nodeArray = new Node[nodeCount];

        for (int i = 0; i < nodeCount; i++) {
            nodeArray[i] = net.addNode();
        }

        // init weight matrix
        double weights[][] = new double[nodeCount][nodeCount];
        
        // create edges
        for (int i = 0; i < edgeCount; i++) {
        	//System.out.println("edge #" + i);
        
        	double totalWeight = 0;
        	
            for (int origIndex = 0; origIndex < nodeCount; origIndex++) {
            	for (int targIndex = 0; targIndex < nodeCount; targIndex++) {
            		double weight = 0;
            	
            		if (distMatrix.getDist(origIndex, targIndex) > 2) {
            			Node origNode = nodeArray[origIndex];
            			Node targNode = nodeArray[targIndex];
        
            			double directDistance = distMatrix.getDist(origNode.getId(), targNode.getId());
            			double reverseDistance = distMatrix.getDist(targNode.getId(), origNode.getId());
                    
            			prog.vars[0] = (double)origIndex;
            			prog.vars[1] = (double)targIndex;
            			prog.vars[2] = (double)origNode.getInDegree();
            			prog.vars[3] = (double)origNode.getOutDegree();
            			prog.vars[4] = (double)targNode.getInDegree();
            			prog.vars[5] = (double)targNode.getOutDegree();
            			prog.vars[6] = directDistance;
            			prog.vars[7] = reverseDistance;
                    
            			weight = prog.eval(i);
            			if (weight < 0) {
            				weight = 0;
            			}
            			
            			if (Double.isNaN(weight)) {
                    		weight = 0;
                    	}
            		}
            			
            		weights[origIndex][targIndex] = weight;
            			
            		totalWeight += weight;
            	}
            }
            
            double targWeight = RandomGenerator.instance().random.nextDouble() * totalWeight;
            
            int selectedOrigIndex = -1;
            int selectedTargIndex = -1;
            
            if (totalWeight > 0) {
            	//System.out.println("#A total weight: " + totalWeight);
            	//System.out.println("targ weight: " + targWeight);
            	totalWeight = 0;
            	boolean selected = false;
            	for (int origIndex = 0; (origIndex < nodeCount) && (!selected); origIndex++) {
            		for (int targIndex = 0; (targIndex < nodeCount) && (!selected); targIndex++) {
            			totalWeight += weights[origIndex][targIndex];
            			
            			if (totalWeight >= targWeight) {
            				selectedOrigIndex = origIndex;
            				selectedTargIndex = targIndex;
            				selected = true;
            			}
            		}
            	}
            }
            else {
            	//System.out.println("#B");
            	while (selectedOrigIndex == selectedTargIndex) {
            		selectedOrigIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            		selectedTargIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            	}
            }

            //System.out.println("selectedOrigIndex: " + selectedOrigIndex);
            //System.out.println("selectedTargIndex: " + selectedTargIndex);
            Node origNode = nodeArray[selectedOrigIndex];
            Node targNode = nodeArray[selectedTargIndex];

            net.addEdge(origNode, targNode, i);
            
            // update distances
            distMatrix.updateDistances(net, selectedOrigIndex, selectedTargIndex);
            
            simulated = true;
        }
    }
	
	
	@Override
	public String toString() {
		return "using full generator"; 
	}
}