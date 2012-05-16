package com.telmomenezes.synthetic;


public class StarNet extends Net {
    public StarNet(int nodeCount, boolean inwards) {
        super();
        
        for (int i = 0; i < nodeCount; i++) {
            addNode();
        }
        
        for (int i = 1; i < nodeCount; i++) {
            if (inwards) {
                addEdge(nodes.get(i), nodes.get(0));
            }
            else {
                addEdge(nodes.get(0), nodes.get(i));
            }
        }
    }
    
    public static void main(String[] args) {
        StarNet net = new StarNet(100, true);
        net.computePageranks();
        net.printPRInfo();
    }
}
