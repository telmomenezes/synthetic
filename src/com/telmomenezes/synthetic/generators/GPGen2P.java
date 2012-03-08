package com.telmomenezes.synthetic.generators;

import java.util.Vector;

import com.telmomenezes.synthetic.DistMatrix;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;
import com.telmomenezes.synthetic.RandomGenerator;
import com.telmomenezes.synthetic.gp.GPFun;
import com.telmomenezes.synthetic.gp.GPTree;

public class GPGen2P {
    GPTree prog_origin;
    GPTree prog_target;
    int edges;
    int cycle;
    
    public GPGen2P() {
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
        
        prog_origin = new GPTree(4, funset, null);
        prog_origin.initRandom(0.2, 2, 5);
        prog_target = new GPTree(9, funset, null);
        prog_target.initRandom(0.2, 2, 5);
    }

    public GPGen2P clone() {
        GPGen2P gen_clone = new GPGen2P();

        gen_clone.edges = 0;
        gen_clone.cycle = 0;
        gen_clone.prog_origin = prog_origin.clone();
        gen_clone.prog_target = prog_target.clone();

        return gen_clone;
    }


    Net run(int node_count, int edge_count) {
        // init DistMatrix
        DistMatrix.instance().set_nodes(node_count);

        Net net = new Net();

        cycle = 0;

        // create nodes
        for (int i = 0; i < node_count; i++) {
            net.addNodeWithId(i);
        }

        // create edges
        Node orig_node;
        Node targ_node;
        double total_weight;
        double weight;

        double po, pt, io, oo, it, ot, t, ud, dd;

        for (int i = 0; i < edge_count; i++) {
            total_weight = 0;
            orig_node = net.getNodes();
            while (orig_node != null) {
                po = (double)orig_node.getId();
                io = oo = t = 0;
                if (edges > 0) {
                    io = (double)orig_node.getInDegree();
                    oo = (double)orig_node.getOutDegree();
                    t = (double)cycle;
                }

                prog_origin.vars[0] = po;
                prog_origin.vars[1] = io;
                prog_origin.vars[2] = oo;
                prog_origin.vars[3] = t;
                weight = prog_origin.eval();
                if (weight < 0) {
                    weight = 0;
                }

                orig_node.setGenweight(weight);
                total_weight += weight;

                orig_node = orig_node.getNext();
            }

            // if total weight is zero, make every node's weight = 1
            if (total_weight == 0) {
                orig_node = net.getNodes();
                while (orig_node != null) {
                    orig_node.setGenweight(1.0);
                    total_weight += 1.0;
                    orig_node = orig_node.getNext();
                }
            }

            weight = RandomGenerator.instance().random.nextDouble() * total_weight;
            orig_node = net.getNodes();
            total_weight = orig_node.getGenweight();
            while (total_weight < weight) {
                orig_node = orig_node.getNext();
                total_weight += orig_node.getGenweight();
            }
        
            total_weight = 0;
            targ_node = net.getNodes();
            while (targ_node != null) {
                po = (double)orig_node.getId();
                pt = (double)targ_node.getId();

                io = oo = it = ot = t = 0;

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

                prog_target.vars[0] = po;
                prog_target.vars[1] = io;
                prog_target.vars[2] = oo;
                prog_target.vars[3] = t;
                prog_target.vars[4] = pt;
                prog_target.vars[5] = it;
                prog_target.vars[6] = ot;
                prog_target.vars[7] = ud;
                prog_target.vars[8] = dd;

                weight = prog_target.eval();
                if (weight < 0) {
                    weight = 0;
                }
            
                if (orig_node == targ_node) {
                    weight = 0;
                }
        
                targ_node.setGenweight(weight);
                total_weight += weight;

                targ_node = targ_node.getNext();
            }

            // if total weight is zero, make every node's weight = 1
            if (total_weight == 0) {
                targ_node = net.getNodes();
                while (targ_node != null) {
                    targ_node.setGenweight(1.0);
                    total_weight += 1.0;
                    targ_node = targ_node.getNext();
                }
            }

            weight = RandomGenerator.instance().random.nextDouble() * total_weight;
            targ_node = net.getNodes();
            total_weight = targ_node.getGenweight();
            while (total_weight < weight) {
                targ_node = targ_node.getNext();
                total_weight += targ_node.getGenweight();
            }

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
void GPGenerator::write(string file_path)
{
    ofstream f;
    f.open(file_path.c_str());

    f << "#PROG ORIGIN" << endl;
    f << prog_origin->to_string();
    f << endl << "#PROG TARGET" << endl;
    f << prog_target->to_string();

    f.close();
}*/


    GPGen2P recombine(GPGen2P gen) {
        GPGen2P child = new GPGen2P();

        edges = 0;
        cycle = 0;
        switch(RandomGenerator.instance().random.nextInt(3)) {
        case 0:
            child.prog_origin = prog_origin.recombine(gen.prog_origin);
            child.prog_target = prog_target.clone();
            break;
        case 1:
            child.prog_origin = prog_origin.clone();
            child.prog_target = prog_target.recombine(gen.prog_target);
            break;
        case 2:
            child.prog_origin = prog_origin.recombine(gen.prog_origin);
            child.prog_target = prog_target.recombine(gen.prog_target);
            break;
        }

        return child;
    }

/*
void GPGenerator::load(string filepath)
{
    std::ifstream file(filepath.c_str());
    string line;

    std::getline(file, line);
         
    for (int i = 0; i < 2; i++) {
        while ((line.compare("") == 0)
                || (line[0] == '\n')
                || (line[0] == '#')) {
            std::getline(file, line);
        }
        
        std::stringstream prog;
        while (line.compare("")
                && (line[0] != '\n')
                && (line[0] != '#')) {
            prog << line;
            if (!std::getline(file, line))
                break;
        }

        if (i == 0) {
            prog_origin->parse(prog.str());
        }
        else {
            prog_target->parse(prog.str());
        }
    }
}*/


    void simplify() {
        prog_origin.dynPruning();
        prog_target.dynPruning();    
    }
}