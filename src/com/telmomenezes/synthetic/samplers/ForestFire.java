package com.telmomenezes.synthetic.samplers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;


public class ForestFire {
    
    private Net origNet;
    private int targNodeCount;
    private int sampleNodeCount;
    
    public ForestFire(Net net) {
        origNet = net;
    }
    
    private void sampleRec(Node node) {
        if (sampleNodeCount >= targNodeCount) {
            return;
        }
        
        List<Node> candidates = new LinkedList<Node>();
        for (Edge edge : node.getOutEdges()) {
            Node outNode = edge.getTarget();
            if (outNode.isFlag()) {
                candidates.add(outNode);
            }
        }
        
        Collections.shuffle(candidates);
        
        int count = RandomGenerator.instance().nextGeometric(0.2);
        
        for (Node c : candidates) {
            if (count <= 0) {
                break;
            } 
            c.setFlag(true);
            sampleNodeCount++;
            if (sampleNodeCount >= targNodeCount) {
                return;
            }
        }
        for (Node c : candidates) {
            if (count <= 0) {
                break;
            } 
            sampleRec(c);
        }
    }
    
    public Net sample() {
        targNodeCount = (int)((double)origNet.getNodeCount() * 0.2);
        sampleNodeCount = 0;
        
        Net sampleNet = new Net();
        
        origNet.clearFlags();
        
        // select random node from origNet
        while (sampleNodeCount < targNodeCount) {
            int index = RandomGenerator.instance().random.nextInt(origNet.getNodeCount());
            Node node = origNet.getNodes().get(index);
            node.setFlag(true);
            sampleNodeCount++;
            sampleRec(node);
        }
        
        return sampleNet;
    }
}