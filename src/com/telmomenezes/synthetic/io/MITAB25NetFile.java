package com.telmomenezes.synthetic.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.Node;

public class MITAB25NetFile extends NetFile {

	private Net net;
	private Map<String, Node> nodeMap;
	
	
	private Node getNode(String namesStr) {
		Node node = null;
		
		String[] names = namesStr.split("\\|");
		
		for (String name : names) {
			if (nodeMap.containsKey(name)) {
				node = nodeMap.get(name);
				break;
			}
		}
		
		if (node == null) {
			node = net.addNode();
		}
		
		for (String name : names) {
			nodeMap.put(name, node);
		}
		
		return node;
	}
	
	
	@Override
	public Net load(String filePath) {
		net = new Net();
		nodeMap = new HashMap<String, Node>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = br.readLine()) != null) {
			   String[] cols = line.split("\t");
			   
			   Node origNode = getNode(cols[0]);
			   Node targNode = getNode(cols[1]);
			   net.addEdge(origNode, targNode);
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return net;
	}

	@Override
	public void save(Net net, String filePath) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		MITAB25NetFile mitab25 = new MITAB25NetFile();
		Net net = mitab25.load("Hsapi20120818CR.mitab");
		System.out.println(net);
	}

}
