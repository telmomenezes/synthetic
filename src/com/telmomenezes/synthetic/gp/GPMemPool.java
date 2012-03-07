package com.telmomenezes.synthetic.gp;


import java.util.ArrayList;


/**
 * Memory pool of genetic program nodes.
 * 
 * Allows for the reuse of GPNode objects. Since these objects are created
 * and destroyed in large quantities during the evolutionary process, creating
 * a pool for their reuse is a relevant optimization.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class GPMemPool {


	private static GPMemPool _instance = null;

    private ArrayList<GPNode> buffer;
    

	private GPMemPool(int initial_buffer_size)
	{
		buffer = new ArrayList<GPNode>(initial_buffer_size);
	}


	static GPMemPool instance()
	{
		if (_instance == null)
			_instance = new GPMemPool(100000);

		return _instance;
	}


	public GPNode getNode()
	{
		GPNode node;
		
		if (buffer.size() == 0) {
			node = new GPNode();
		}
		else {
			node = buffer.get(0);
			buffer.remove(0);
			
		}
		
		return node;
	}


	public void returnNode(GPNode node)
	{
		buffer.add(node);
	}
}