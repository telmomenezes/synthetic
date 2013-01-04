package com.telmomenezes.synthetic.io;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
        Map<String, Node> nodeMap = new HashMap<String, Node>();
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            
            // read header
            String line = in.readLine();
            String[] tokens = line.split(",");
            boolean first = true;
            for (String t : tokens) {
                if (first) {
                    first = false;
                }
                else {
                    Node node = net.addNode();
                    nodes.add(node);
                    nodeMap.put(t, node);
                    //System.out.println("group: " + t);
                }
            }
            
            while ((line = in.readLine()) != null) {
                tokens = line.split(",");
                for (int j = 0; j < nodes.size(); j++) {
                    //double val = Double.parseDouble(tokens[j + 1]);
                    Node orig = nodeMap.get(tokens[0]);
                    if (orig != null) {
                        net.addEdge(orig, nodes.get(j));
                    }
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
        // TODO
    }
    
    static public void main(String[] args) {
        MatrixFile mf = new MatrixFile();
        Net net = mf.load("alliance_nets/Chimane_AGNATES.csv");
        System.out.println(net);
    }
}