package com.telmomenezes.synthetic.io;

import java.util.Dictionary;


/**
 * Interface with callbacks to be used by the GEFXParser class.
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public interface GEXFParserCallbacks {
	
	public void onNode(String id, String label,
			Dictionary<String, String> extraAtts);
	
	public void onEdge(String id, String source, String target, double weight, 
			boolean directed, Dictionary<String, String> extraAtts);
}
