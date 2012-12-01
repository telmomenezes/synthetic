/**
 * 
 */
package com.telmomenezes.synthetic.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.telmomenezes.synthetic.Edge;
import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;

/**
 * @author telmo
 *
 */
public class SNAPNetFile extends NetFile {
    @Override
    public Net load(String filePath) {
        Net net = new Net();
        Map<String, Node> nodes = new HashMap<String, Node>();
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.charAt(0) != '#') {
                    String[] tokens = line.split("\t");
                    if (tokens.length == 2) {
                        for (String t : tokens) {
                            if (!nodes.containsKey(t)) {
                                nodes.put(t, net.addNode());
                            }
                        }
                        net.addEdge(nodes.get(tokens[0]), nodes.get(tokens[1]), 0);
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
        SNAPNetFile snf = new SNAPNetFile();
        Net net = snf.load("ownership.txt");
        System.out.println(net);
    }
}
