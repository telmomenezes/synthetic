package com.telmomenezes.synthetic.generators;

import java.util.Vector;

import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.gp.GenericFunSet;
import com.telmomenezes.synthetic.gp.ProgSet;


/**
 * @author Telmo Menezes
 *
 */
public class GPGen1P extends Generator {
    public GPGen1P(int nodeCount, int edgeCount) {
        super(nodeCount, edgeCount);
    }
    
    @Override
    public void createProgSet() {
        progcount = 1;
        
        Vector<String> variableNames = new Vector<String>();
        variableNames.add("origId");
        variableNames.add("targId");
        variableNames.add("origInDeg");
        variableNames.add("origOutDeg");
        variableNames.add("targInDeg");
        variableNames.add("targOutDeg");
        variableNames.add("undirDist");
        variableNames.add("dirDist");
        variableNames.add("revDist");
        
        progset = new ProgSet(progcount, variableNames);
        
        progset.varcounts.set(0, 9);
        progset.funsets.set(0, GenericFunSet.instance().getFunset());
        progset.prognames.set(0, "Prog\n");
    }

    @Override
    public void run() {
        // reset eval stats
        progset.clearEvalStats();
        
        // init DistMatrix
        DistMatrix.instance().setNodes(nodeCount);

        net = new Net();

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
                    if (origIndex != targIndex) {
                        Node origNode = nodeArray[origIndex];
                        Node targNode = nodeArray[targIndex];
        
                        double undirectedDistance = DistMatrix.instance().getUDist(origNode.getId(), targNode.getId());
                        double directDistance = DistMatrix.instance().getDDist(origNode.getId(), targNode.getId());
                        double reverseDistance = DistMatrix.instance().getDDist(targNode.getId(), origNode.getId());
                    
                        progset.progs[0].vars[0] = (double)origIndex;
                        progset.progs[0].vars[1] = (double)targIndex;
                        progset.progs[0].vars[2] = (double)origNode.getInDegree();
                        progset.progs[0].vars[3] = (double)origNode.getOutDegree();
                        progset.progs[0].vars[4] = (double)targNode.getInDegree();
                        progset.progs[0].vars[5] = (double)targNode.getOutDegree();
                        progset.progs[0].vars[6] = undirectedDistance;
                        progset.progs[0].vars[7] = directDistance;
                        progset.progs[0].vars[8] = reverseDistance;
                    
                        double weight = progset.progs[0].eval(i);
                        if (weight < 0) {
                            weight = 0;
                        }
        
                        weightArray[origIndex][targIndex] = weight;
                        totalWeight += weight;
                    }
                }
            }

            // if total weight is zero, make every pair's weight = 1
            if (totalWeight == 0) {
                for (int x = 0; x < nodeCount; x++) {
                    for (int y = 0; y < nodeCount; y++) {
                        if (x != y) {
                            weightArray[x][y] = 1.0;
                            totalWeight += 1.0;
                        }
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

            net.addEdge(origNode, targNode, i);
            
            // update distances
            DistMatrix.instance().updateDistances(origIndex, targIndex);
        }
    }
    
    public double distance(Generator generator) {
        return 0;
    }
    
    @Override
    public Generator clone() {
        return new GPGen1P(nodeCount, edgeCount);
    }
}