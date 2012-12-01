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
            if (!outNode.getFlag()) {
                candidates.add(outNode);
            }
        }
        
        Collections.shuffle(candidates);
        
        int count = RandomGenerator.instance().nextGeometric(0.2);
        
        for (Node c : candidates) {
            if (count <= 0) {
                break;
            }
            count--;
            
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
    
    public Net sample(double samplePercentage) {
        targNodeCount = (int)((double)origNet.getNodeCount() * samplePercentage);
        sampleNodeCount = 0;
        
        origNet.clearFlags();
        
        while (sampleNodeCount < targNodeCount) {
            Node node = origNet.getRandomNode();
            if (!node.getFlag()) {
                node.setFlag(true);
                sampleNodeCount++;
                sampleRec(node);
            }
        }
     
        return origNet.cloneFlagged();
    }
    
    public static void main(String[] args) {
        Net net = Net.load("wiki-Vote.txt");
        System.out.println(net);
        //net.printDegDistInfo();
        ForestFire ff = new ForestFire(net);
        Net sample = ff.sample(0.3);
        System.out.println(sample);
        //sample.printDegDistInfo();
    }
}