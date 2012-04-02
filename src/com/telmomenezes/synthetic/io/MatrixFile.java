package com.telmomenezes.synthetic.io;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;

/**
 * @author telmo
 *
 */
public class MatrixFile extends NetFile {
    @Override
    public Net load(String filePath) {
        Net net = new Net(true);
        Vector<Node> nodes = new Vector<Node>();
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            
            // read header
            String line = in.readLine();
            String[] tokens = line.split("\t");
            for (String t : tokens) {
                if (!t.equals("")) {
                    nodes.add(net.addNode());
                    //System.out.println("group: " + t);
                }
            }
            
            for (int i = 0; i < nodes.size(); i++) {
                line = in.readLine();
                tokens = line.split("\t");
                for (int j = 0; j < nodes.size(); j++) {
                    double val = Double.parseDouble(tokens[j + 1]);
                    net.addEdge(nodes.get(i), nodes.get(j), val);
                }
            }
            
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return net;
    }

    @Override
    public void save(Net net, String filePath) {
        try{ 
            FileWriter fstream = new FileWriter(filePath);
            BufferedWriter out = new BufferedWriter(fstream);
            
            for (Edge edge : net.getEdges()) {
                out.write("" + edge.getOrigin().getId() + '\t' + edge.getTarget().getId() + '\n');
            }
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static public void main(String[] args) {
        MatrixFile mf = new MatrixFile();
        Net net = mf.load("Dogon_1987_AllianceMatrix.txt");
        System.out.println(net);
    }
}