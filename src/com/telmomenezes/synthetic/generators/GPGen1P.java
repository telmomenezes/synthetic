package com.telmomenezes.synthetic.generators;

import java.util.Vector;

import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.gp.GPFun;
import com.telmomenezes.synthetic.gp.GPTree;

public class GPGen1P {
    GPTree prog;
    int edges;
    int cycle;
    
    public GPGen1P() {
        edges = 0;
        cycle = 0;
        Vector<Integer> funset = new Vector<Integer>();
        funset.add(GPFun.SUM);
        funset.add(GPFun.SUB);
        funset.add(GPFun.MUL);
        funset.add(GPFun.DIV);
        funset.add(GPFun.EQ);
        funset.add(GPFun.GRT);
        funset.add(GPFun.LRT);
        funset.add(GPFun.ZER);
        funset.add(GPFun.EXP);
        funset.add(GPFun.LOG);
        funset.add(GPFun.SIN);
        funset.add(GPFun.ABS);
        funset.add(GPFun.MIN);
        funset.add(GPFun.MAX);
        prog = new GPTree(10, funset, null);
        prog.initRandom(0.2, 2, 5);
    }

    public GPGen1P clone() {
        GPGen1P gen_clone = new GPGen1P();

        gen_clone.edges = 0;
        gen_clone.cycle = 0;
        gen_clone.prog = prog.clone();

        return gen_clone;
    }

    public Net run(int node_count, int edge_count) {
        // TODO: configure this somewhere
        int TEST_EDGES = 1000;

        // init DistMatrix
        DistMatrix.instance().set_nodes(node_count);

        Net net = new Net();

        cycle = 0;

        // create nodes
        Node[] node_array = new Node[node_count];
        Node[] orig_array = new Node[TEST_EDGES];
        Node[] targ_array = new Node[TEST_EDGES];
        double[] weight_array = new double[TEST_EDGES];
        for (int i = 0; i < node_count; i++) {
            node_array[i] = net.addNodeWithId(i);
        }

        // create edges
        double weight;
        for (int i = 0; i < edge_count; i++) {
            double po, pt, io, oo, it, ot, t, ud, dd, rd;     
            double total_weight = 0;

            Node orig_node = null;
            Node targ_node = null;

            // test TEST_EDGES edges
            for (int j = 0; j < TEST_EDGES; j++) {
                boolean new_edge = false;
                while (!new_edge) {
                    int orig_index = RandomGenerator.instance().random.nextInt(node_count);
                    int targ_index = RandomGenerator.instance().random.nextInt(node_count - 1);
                    if (targ_index >= orig_index) {
                        targ_index += 1;
                    }
                    orig_node = node_array[orig_index];
                    targ_node = node_array[targ_index];
                    if (!orig_node.edgeExists(targ_node)) {
                        new_edge = true;
                    }
                }

                orig_array[j] = orig_node;
                targ_array[j] = targ_node;

                po = (double)orig_node.getId();
                pt = (double)targ_node.getId();
        
                io = oo = it = ot = t = ud = rd = dd = 0;

                if (edges > 0) {
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
            
                dist = DistMatrix.instance().getDDist(targ_node.getId(), orig_node.getId());
                if (dist > 0) {
                    rd = 1.0 / ((double)dist);
                }
                // lim d->inf 1/d
                else {
                    rd = 0;
                }

                prog.vars[0] = po; // orig node ID
                prog.vars[1] = io; // orig node indegree
                prog.vars[2] = oo; // orig node outdegree
                prog.vars[3] = t;  // time
                prog.vars[4] = pt; // targ node ID
                prog.vars[5] = it; // targ node indegree
                prog.vars[6] = ot; // targ node outdegree
                prog.vars[7] = ud; // undirected distance
                prog.vars[8] = dd; // directed distance
                prog.vars[9] = rd; // reverse distance

                weight = prog.eval();
                if (weight < 0) {
                    weight = 0;
                }
        
                weight_array[j] = weight;
                total_weight += weight;
            }

            // if total weight is zero, make every pair's weight = 1
            if (total_weight == 0) {
                for (int j = 0; j < TEST_EDGES; j++) {
                    weight_array[j] = 1.0;
                    total_weight += 1.0;
                }
            }

            weight = RandomGenerator.instance().random.nextDouble() * total_weight;
            int j = 0;
            total_weight = weight_array[j];
            while (total_weight < weight) {
                j++;
                total_weight += weight_array[j];
            }

            orig_node = orig_array[j];
            targ_node = targ_array[j];

            // set ages
            if (orig_node.getBirth() < 0) {
                orig_node.setBirth(cycle);
            }
            if (targ_node.getBirth() < 0) {
                targ_node.setBirth(cycle);
            }

            net.addEdgeToNet(orig_node, targ_node, cycle);
            // update distances
            DistMatrix.instance().update_distances(orig_node.getId(), targ_node.getId());
            
            cycle++;
        }

        return net;
    }


    /*
    void write(String file_path) {
        ofstream f;
        f.open(file_path.c_str());

        f << "#PROG" << endl;
        f << prog->to_string();

        f.close();
    }*/


    GPGen1P recombine(GPGen1P gen) {
        GPGen1P child = new GPGen1P();

        edges = 0;
        cycle = 0;
        child.prog = prog.recombine(gen.prog);

        return child;
    }

/*
void GPGenerator::load(string filepath)
{
    std::ifstream file(filepath.c_str());
    string line;

    std::getline(file, line);
         
    while ((line.compare("") == 0)
            || (line[0] == '\n')
            || (line[0] == '#')) {
        std::getline(file, line);
    }
        
    std::stringstream sprog;
    while (line.compare("")
            && (line[0] != '\n')
            && (line[0] != '#')) {
        sprog << line;
        if (!std::getline(file, line))
            break;
    }

    prog->parse(sprog.str());
}*/


    void simplify() {
        prog.dynPruning();    
    }
}