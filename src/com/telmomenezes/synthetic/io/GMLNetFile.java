package com.telmomenezes.synthetic.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;

public class GMLNetFile extends NetFile {

    public enum State {
        START, GRAPH, NODE, EDGE 
    }
    
    @Override
    public Net load(String filePath) {
        Net net = new Net();
        Map<String, Node> nodes = new HashMap<String, Node>();
        
        try {
            State state = State.START;
            
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line;
            String source = "";
            String target = "";
            while ((line = in.readLine()) != null) {
                line = line.trim();
                
                switch (state) {
                case START:
                    if (line.startsWith("graph")) {
                        state = State.GRAPH;
                    }
                    break;
                case GRAPH:
                    if (line.startsWith("node")) {
                        state = State.NODE;
                    }
                    else if (line.startsWith("edge")) {
                        state = State.EDGE;
                    }
                    else if (line.startsWith("]")) {
                        return net;
                    }
                    break;
                case NODE:
                    if (line.startsWith("id")) {
                        String[] tokens = line.split(" ");
                        if (tokens.length >= 2) {
                            nodes.put(tokens[1], net.addNode());
                        }
                    }
                    else if (line.startsWith("]")) {
                        state = State.GRAPH;
                    }
                    break;
                case EDGE:
                    if (line.startsWith("source")) {
                        String[] tokens = line.split(" ");
                        if (tokens.length >= 2) {
                            source = tokens[1];
                        }
                    }
                    else if (line.startsWith("target")) {
                        String[] tokens = line.split(" ");
                        if (tokens.length >= 2) {
                            target = tokens[1];
                        }
                    }
                    else if (line.startsWith("]")) {
                        net.addEdge(nodes.get(source), nodes.get(target), 0);
                        state = State.GRAPH;
                    }
                    break;
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
        // TODO Auto-generated method stub

    }

    static public void main(String[] args) {
        GMLNetFile nf = new GMLNetFile();
        Net net = nf.load("celegansneural.gml");
        System.out.println(net);
    }
}
