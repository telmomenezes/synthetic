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
public class FastGenerator extends Generator {

	private double trialRatio;
	private int trials;
	
	public FastGenerator(int nodeCount, int edgeCount) {
		super(nodeCount, edgeCount);

	    trialRatio = 0.01;
	    trials = (int)((nodeCount * nodeCount) * trialRatio * trialRatio);
	}
	
	
	@Override
	public Generator instance() {
		Generator generator = new FastGenerator(nodeCount, edgeCount);
		return generator;
	}
	
	
	@Override
	public Generator clone() {
		Generator generator = new FastGenerator(nodeCount, edgeCount);
		generator.prog = prog.clone();
		return generator;
	}
	
    
	@Override
	public void run() {
		//System.out.println("running generator; trials: " + trials);
		
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

        // create edges
        for (int i = 0; i < edgeCount; i++) {
        	//System.out.println("edge #" + i);
            double bestWeight = -1;
            int bestOrigIndex = -1;
            int bestTargIndex = -1;
            for (int j = 0; j < trials; j++) {
            	int origIndex = -1;
            	int targIndex = -1;
            	
            	while ((origIndex == targIndex)
            			|| (distMatrix.getDist(origIndex, targIndex) < 2)) {
            		origIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            		targIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            	}
            		
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
                    
            	double weight = prog.eval(i);
            	if (weight < 0) {
            		weight = 0;
            	}
        
            	//System.out.println("weight: " + weight + "; bestWeight: " + bestWeight);
            	if (weight > bestWeight) {
            		//System.out.println("* best weight");
            		bestWeight = weight;
            		bestOrigIndex = origIndex;
            		bestTargIndex = targIndex;
            	}
            }

            Node origNode = nodeArray[bestOrigIndex];
            Node targNode = nodeArray[bestTargIndex];

            net.addEdge(origNode, targNode, i);
            
            // update distances
            distMatrix.updateDistances(net, bestOrigIndex, bestTargIndex);
            
            simulated = true;
        }
    }
	
	
	@Override
	public String toString() {
		return "using fast generator; trialRatio: " + trialRatio + "; trials: " + trials; 
	}
}