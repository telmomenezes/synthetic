package com.telmomenezes.synthetic.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.telmomenezes.synthetic.Net;
import com.telmomenezes.synthetic.NetBuilder;


public class MITAB25NetFile extends NetFile {

	private NetBuilder nb;
	private Map<String, Integer> nodeMap;
	
	
	private int getNode(String namesStr) {
		int node = -1;
		
		String[] names = namesStr.split("\\|");
		
		for (String name : names) {
			if (nodeMap.containsKey(name)) {
				node = nodeMap.get(name);
				break;
			}
		}
		
		if (node == -1) {
			node = nb.addNode();
		}
		
		for (String name : names) {
			nodeMap.put(name, node);
		}
		
		return node;
	}
	
	
	@Override
	public Net load(String filePath, boolean directed, boolean parallels) {
		nb = new NetBuilder(directed, false, parallels);
		nodeMap = new HashMap<String, Integer>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = br.readLine()) != null) {
			   String[] cols = line.split("\t");
			   
			   int origNode = getNode(cols[0]);
			   int targNode = getNode(cols[1]);
			   nb.addEdge(origNode, targNode, 1);
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return nb.buildNet();
	}

	@Override
	public void save(Net net, String filePath) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		MITAB25NetFile mitab25 = new MITAB25NetFile();
		Net net = mitab25.load("Hsapi20120818CR.mitab", false, false);
		System.out.println(net);
	}

}
