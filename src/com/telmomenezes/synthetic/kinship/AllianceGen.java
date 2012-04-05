package com.telmomenezes.synthetic.kinship;


import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.generators.Generator;
import com.telmomenezes.synthetic.gp.GenericFunSet;
import com.telmomenezes.synthetic.gp.ProgSet;


public class AllianceGen extends Generator {

    // topological indices
    private TopologicalIndices indices;
    private TopologicalIndices targetIndices;
    
    public AllianceGen(int nodeCount, int edgeCount, TopologicalIndices targetIndices) {
        super(nodeCount, edgeCount);
        
        this.targetIndices = targetIndices;
        indices = null;
    }

    @Override
    public void createProgSet() {
        progcount = 1;
        progset = new ProgSet(progcount, null);
        
        progset.varcounts.set(0, 15);
        progset.funsets.set(0, GenericFunSet.instance().getFunset());
        progset.prognames.set(0, "Prog\n");
    }

    @Override
    public Net run() {
        // init DistMatrix
        DistMatrix.instance().setNodes(nodeCount);

        Net net = new Net();

        // create nodes
        Node[] nodeArray = new Node[nodeCount];
        double[][] weightArray = new double[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            nodeArray[i] = net.addNodeWithId(i);
        }

        // create edges
        for (int i = 0; i < edgeCount; i++) {
            double totalWeight = 0;
            for (int origIndex = 0; origIndex < nodeCount; origIndex++) {
                for (int targIndex = 0; targIndex < nodeCount; targIndex++) {
                    Node origNode = nodeArray[origIndex];
                    Node targNode = nodeArray[targIndex];
        
                    double undirectedDistance = 0;
                    int dist = DistMatrix.instance().getUDist(origNode.getId(), targNode.getId());
                    if (dist > 0) {
                        undirectedDistance = 1.0 / ((double)dist);
                    }
                    // lim d->inf 1/d
                    else {
                        undirectedDistance = 0;
                    }

                    double directDistance = 0;
                    dist = DistMatrix.instance().getDDist(origNode.getId(), targNode.getId());
                    if (dist > 0) {
                        directDistance = 1.0 / ((double)dist);
                    }
                    // lim d->inf 1/d
                    else {
                        directDistance = 0;
                    }
                    
                    double reverseDistance = 0;
                    dist = DistMatrix.instance().getDDist(targNode.getId(), origNode.getId());
                    if (dist > 0) {
                        reverseDistance = 1.0 / ((double)dist);
                    }
                    // lim d->inf 1/d
                    else {
                        reverseDistance = 0;
                    }
                    
                    double directStrength = 0;
                    Edge edge = net.getEdge(origNode, targNode);
                    if (edge != null) {
                        directStrength = edge.getWeight();
                    }
                    double reverseStrength = 0;
                    edge = net.getEdge(targNode, origNode);
                    if (edge != null) {
                        reverseStrength = edge.getWeight();
                    }
                    
                    progset.progs[0].vars[0] = (double)origIndex;
                    progset.progs[0].vars[1] = (double)targIndex;
                    progset.progs[0].vars[2] = (double)origNode.getInDegree();
                    progset.progs[0].vars[3] = (double)origNode.getOutDegree();
                    progset.progs[0].vars[4] = (double)targNode.getInDegree();
                    progset.progs[0].vars[5] = (double)targNode.getOutDegree();
                    progset.progs[0].vars[6] = origNode.getTotalInputWeight();
                    progset.progs[0].vars[7] = origNode.getTotalOutputWeight();
                    progset.progs[0].vars[8] = targNode.getTotalInputWeight();
                    progset.progs[0].vars[9] = targNode.getTotalOutputWeight();
                    progset.progs[0].vars[10] = undirectedDistance;
                    progset.progs[0].vars[11] = directDistance;
                    progset.progs[0].vars[12] = reverseDistance;
                    progset.progs[0].vars[13] = directStrength;
                    progset.progs[0].vars[14] = reverseStrength;
                    
                    double weight = progset.progs[0].eval();
                    if (weight < 0) {
                        weight = 0;
                    }
        
                    weightArray[origIndex][targIndex] = weight;
                    totalWeight += weight;
                }
            }

            // if total weight is zero, make every pair's weight = 1
            if (totalWeight == 0) {
                for (int x = 0; x < nodeCount; x++) {
                    for (int y = 0; y < nodeCount; y++) {
                        weightArray[x][y] = 1.0;
                        totalWeight += 1.0;
                    }
                }
            }

            double weight = RandomGenerator.instance().random.nextDouble() * totalWeight;
            int origIndex = 0;
            int targIndex = 0;
            totalWeight = weightArray[origIndex][targIndex];
            while (totalWeight < weight) {
                origIndex++;
                if (origIndex >= nodeCount) {
                    targIndex++;
                    origIndex = 0;
                }
                totalWeight += weightArray[origIndex][targIndex];
            }

            Node origNode = nodeArray[origIndex];
            Node targNode = nodeArray[targIndex];   

            Edge edge = net.getEdge(origNode, targNode);
            if (edge == null) {
                net.addEdge(origNode, targNode, i);
            }
            else {
                edge.setWeight(edge.getWeight() + 1.0);
            }
            
            // update distances
            DistMatrix.instance().updateDistances(origIndex, targIndex);
        }

        return net;
    }
    
    public double distance(Generator generator) {
        return 0;
    }
    
    @Override
    public Generator clone() {
        return new AllianceGen(nodeCount, edgeCount, targetIndices);
    }
    
    public int compareTo(Generator generator) {
        double delta1[] = indices.delta(targetIndices);
        double delta2[] = ((AllianceGen)generator).indices.delta(targetIndices);
        
        int better = 0;
        int worse = 0;
        
        for (int i = 0; i < 4; i++) {
            if (delta1[0] < delta2[0]) {
                better++;
            }
            else if (delta1[0] > delta2[0]) {
                worse++;
            }
        }
        
        if (better > worse)
            return -1;
        else if (better < worse)
            return 1;
        else
            return 0;
    }

    public TopologicalIndices getIndices() {
        return indices;
    }

    public void setIndices(TopologicalIndices indices) {
        this.indices = indices;
    }

    public TopologicalIndices getTargetIndices() {
        return targetIndices;
    }

    public void setTargetIndices(TopologicalIndices targetIndices) {
        this.targetIndices = targetIndices;
    }
}