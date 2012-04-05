package com.telmomenezes.synthetic.generators;


import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.gp.GenericFunSet;
import com.telmomenezes.synthetic.gp.ProgSet;

public class GPGen2P extends Generator {
    
    public GPGen2P(int nodeCount, int edgeCount) {
        super(nodeCount, edgeCount);
    }
    
    /**
     * Creates the program set.
     */
    public void createProgSet() {
        progcount = 2;
        progset = new ProgSet(progcount, null);
        
        // Origin
        progset.varcounts.set(0, 4);
        progset.funsets.set(0, GenericFunSet.instance().getFunset());
        progset.prognames.set(0, "Origin\n");

        // Target
        progset.varcounts.set(1, 9);
        progset.funsets.set(1, GenericFunSet.instance().getFunset());
        progset.prognames.set(1, "Target\n");
    }

    /**
     * Compute a distance between two generators.
     * 
     * @param generator the generator against which the distance should be computed
     * @return the distance as a double value
     */
    public double distance(Generator generator) {
        return 0;
    }


    public Net run() {
        // init DistMatrix
        DistMatrix.instance().setNodes(getNodeCount());

        Net net = new Net();

        cycle = 0;
        curEdges = 0;

        // create nodes
        for (int i = 0; i < getNodeCount(); i++) {
            net.addNodeWithId(i);
        }

        // create edges
        double total_weight;
        double weight;

        double po, pt, io, oo, it, ot, t, ud, dd;

        for (int i = 0; i < getEdgeCount(); i++) {
            total_weight = 0;
            for (Node orig_node : net.getNodes()) {
                po = (double)orig_node.getId();
                io = oo = t = 0;
                if (curEdges > 0) {
                    io = (double)orig_node.getInDegree();
                    oo = (double)orig_node.getOutDegree();
                    t = (double)cycle;
                }

                progset.progs[0].vars[0] = po;
                progset.progs[0].vars[1] = io;
                progset.progs[0].vars[2] = oo;
                progset.progs[0].vars[3] = t;
                weight = progset.progs[0].eval();
                if (weight < 0) {
                    weight = 0;
                }

                orig_node.setGenweight(weight);
                total_weight += weight;
            }

            // if total weight is zero, make every node's weight = 1
            if (total_weight == 0) {
                for (Node orig_node : net.getNodes()) {
                    orig_node.setGenweight(1.0);
                    total_weight += 1.0;
                }
            }

            Node orig_node = null;
            weight = RandomGenerator.instance().random.nextDouble() * total_weight;
            total_weight = 0;
            for (Node node : net.getNodes()) {
                orig_node = node;
                total_weight += node.getGenweight();
                if (total_weight >= weight)
                    break;
            }
        
            total_weight = 0;
            for (Node targ_node : net.getNodes()) {
                po = (double)orig_node.getId();
                pt = (double)targ_node.getId();

                io = oo = it = ot = t = 0;

                if (curEdges > 0) {
                    io = (double)orig_node.getInDegree();
                    oo = (double)orig_node.getOutDegree();
                    it = (double)targ_node.getInDegree();
                    ot = (double)targ_node.getOutDegree();
                    t = (double)cycle;
                }

                int dist = DistMatrix.instance().getUDist(orig_node.getId(), targ_node.getId());
                if (dist > 0) {
                    ud = 1.0 / ((double)dist);
                }
                // lim d->inf 1/d
                else {
                    ud = 0;
                }

                dist = DistMatrix.instance().getDDist(orig_node.getId(), targ_node.getId());
                if (dist > 0) {
                    dd = 1.0 / ((double)dist);
                }
                // lim d->inf 1/d
                else {
                    dd = 0;
                }            

                progset.progs[1].vars[0] = po;
                progset.progs[1].vars[1] = io;
                progset.progs[1].vars[2] = oo;
                progset.progs[1].vars[3] = t;
                progset.progs[1].vars[4] = pt;
                progset.progs[1].vars[5] = it;
                progset.progs[1].vars[6] = ot;
                progset.progs[1].vars[7] = ud;
                progset.progs[1].vars[8] = dd;

                weight = progset.progs[1].eval();
                if (weight < 0) {
                    weight = 0;
                }
            
                if (orig_node == targ_node) {
                    weight = 0;
                }
        
                targ_node.setGenweight(weight);
                total_weight += weight;
            }

            // if total weight is zero, make every node's weight = 1
            if (total_weight == 0) {
                for (Node node : net.getNodes()) {
                    node.setGenweight(1.0);
                    total_weight += 1.0;
                }
            }

            weight = RandomGenerator.instance().random.nextDouble() * total_weight;
            Node targ_node = null;
            total_weight = 0;
            for (Node node : net.getNodes()) {
                targ_node = node;
                total_weight += targ_node.getGenweight();
                if (total_weight >= weight)
                    break;
            }

            // set ages
            if (orig_node.getBirth() < 0) {
                orig_node.setBirth(cycle);
            }
            if (targ_node.getBirth() < 0) {
                targ_node.setBirth(cycle);
            }

            net.addEdge(orig_node, targ_node, cycle);
            curEdges++;
            
            // update distances
            DistMatrix.instance().updateDistances(orig_node.getId(), targ_node.getId());
            
            cycle++;
        }

        return net;
    }

    @Override
    public Generator clone() {
        return new GPGen2P(nodeCount, edgeCount);
    }
}