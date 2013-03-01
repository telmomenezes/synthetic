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

	private int trials;
	
	public FastGenerator(int nodeCount, int edgeCount, boolean directed, int trials) {
		super(nodeCount, edgeCount, directed);
		this.trials = trials;
	}
	
	
	@Override
	public Generator instance() {
		Generator generator = new FastGenerator(nodeCount, edgeCount, directed, trials);
		return generator;
	}
	
	
	@Override
	public Generator clone() {
		Generator generator = new FastGenerator(nodeCount, edgeCount, directed, trials);
		generator.prog = prog.clone();
		return generator;
	}
	
    
	@Override
	public void run() {
		//System.out.println("running generator; trials: " + trials);
		
        // reset eval stats
        prog.clearEvalStats();
        
        // init DistMatrix
        DistMatrix distMatrixD = null;
        if (directed) {
        	distMatrixD = new DistMatrix(nodeCount, true);
        }
        DistMatrix distMatrixU = new DistMatrix(nodeCount, false);

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
            	Node origNode = null;
            	Node targNode = null;
            	int origIndex = -1;
            	int targIndex = -1;
            	boolean found = false;
            	
            	while (!found) {
            		origIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            		targIndex = RandomGenerator.instance().random.nextInt(nodeCount);
            		
            		if (origIndex != targIndex) {
            			origNode = nodeArray[origIndex];
            			targNode = nodeArray[targIndex];
            			
            			if (!net.edgeExists(origNode, targNode)) {
            				found = true;
            			}
            		}
            	}
            	
        
            	double distance = distMatrixU.getDist(origNode.getId(), targNode.getId());
            	
            	if (directed) {
            		double directDistance = distMatrixD.getDist(origNode.getId(), targNode.getId());
            		double reverseDistance = distMatrixD.getDist(targNode.getId(), origNode.getId());
                    
            		prog.vars[0] = (double)origIndex;
            		prog.vars[1] = (double)targIndex;
            		prog.vars[2] = (double)origNode.getInDegree();
            		prog.vars[3] = (double)origNode.getOutDegree();
            		prog.vars[4] = (double)targNode.getInDegree();
            		prog.vars[5] = (double)targNode.getOutDegree();
            		prog.vars[6] = distance;
            		prog.vars[7] = directDistance;
            		prog.vars[8] = reverseDistance;
            	}
            	else {
            		prog.vars[0] = (double)origIndex;
            		prog.vars[1] = (double)targIndex;
            		prog.vars[2] = (double)origNode.getDegree();
            		prog.vars[3] = (double)targNode.getDegree();
            		prog.vars[4] = distance;
            	}
                    
            	double weight = prog.eval(i);
            	if (weight < 0) {
            		weight = 0;
            	}
            	
            	if (Double.isNaN(weight)) {
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

            net.addEdge(origNode, targNode);
            
            // update distances
            if (directed) {
            	distMatrixD.updateDistances(net, bestOrigIndex, bestTargIndex);
            }
            distMatrixU.updateDistances(net, bestOrigIndex, bestTargIndex);
            
            simulated = true;
        }
    }
	
	
	@Override
	public String toString() {
		return "using fast generator; trials: " + trials; 
	}
}