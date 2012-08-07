package com.telmomenezes.synthetic;


public class RandomNet extends Net {
    public RandomNet(int nodeCount, int edgeCount) {
        super();
        
        for (int i = 0; i < nodeCount; i++) {
            addNode();
        }
        
        for (int i = 0; i < edgeCount; i++) {
            boolean found = false;
            
            while (!found) {
                int orig = RandomGenerator.instance().random.nextInt(nodeCount);
                int targ = RandomGenerator.instance().random.nextInt(nodeCount);
            
                if ((orig != targ) && (!edgeExists(nodes.get(orig), nodes.get(targ)))) {
                    addEdge(nodes.get(orig), nodes.get(targ));
                    found = true;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        RandomNet net = new RandomNet(100, 1000);
        System.out.println(net);
    }
}
