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
import com.telmomenezes.synthetic.NetBuilder;


/**
 * @author telmo
 *
 */
public class SNAPNetFile extends NetFile {
    @Override
    public Net load(String filePath, boolean directed) {
        NetBuilder nb = new NetBuilder(directed, false);
        Map<String, Integer> nodes = new HashMap<String, Integer>();
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.charAt(0) != '#') {
                    String[] tokens = line.split("\t");
                    if (tokens.length == 2) {
                        for (String t : tokens) {
                            if (!nodes.containsKey(t)) {
                                nodes.put(t, nb.addNode());
                            }
                        }
                        nb.addEdge(nodes.get(tokens[0]), nodes.get(tokens[1]));
                    }
                }
            }
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return nb.buildNet();
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
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static public void main(String[] args) {
        SNAPNetFile snf = new SNAPNetFile();
        Net net = snf.load("ownership.txt", true);
        System.out.println(net);
    }
}
