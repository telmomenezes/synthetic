package com.telmomenezes.synthetic.io;

import java.io.FileReader;
import java.util.Dictionary;
import java.util.Hashtable;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;


public class GEXFParser extends DefaultHandler
{
	
	private GEXFParserCallbacks callbacks;


    public GEXFParser(GEXFParserCallbacks callbacks) {
    	super();
    	this.callbacks = callbacks;
    }


    public void parse(String file) throws Exception {
    	
    	XMLReader xr = XMLReaderFactory.createXMLReader();
    	xr.setContentHandler(this);
    	xr.setErrorHandler(this);

    	FileReader r = new FileReader(file);
    	xr.parse(new InputSource(r));
    }
    
    
    public void startElement (String uri, String name,
			      String qName, Attributes atts) {
    	
    	if (name.equals("node")) {
    		
    		Dictionary<String, String> extraAtts = new Hashtable<String, String>();
    		String nodeId = "";
    		String nodeLabel = "";
    		
    		for (int i = 0; i < atts.getLength(); i++) {
    			String attName = atts.getQName(i);
    			String attValue = atts.getValue(i);
    			
    			if (attName.equals("id"))
    				nodeId = attValue;
    			else if (attName.equals("label"))
    				nodeLabel = attValue;
    			else
    				extraAtts.put(attName, attValue);
    		}

    		callbacks.onNode(nodeId, nodeLabel, extraAtts);
    	}
    	else if (name.equals("edge")) {
    		
    		Dictionary<String, String> extraAtts = new Hashtable<String, String>();
    		String edgeId = "";
    		String edgeSource = "";
    		String edgeTarget = "";
    		double edgeWeight = 1;
    		boolean edgeDirected = false;
    		
    		for (int i = 0; i < atts.getLength(); i++) {
    			String attName = atts.getQName(i);
    			String attValue = atts.getValue(i);
    			
    			if (attName.equals("id"))
    				edgeId = attValue;
    			else if (attName.equals("source"))
    				edgeSource = attValue;
    			else if (attName.equals("target"))
    				edgeTarget = attValue;
    			else if (attName.equals("weight"))
    				try {
    	    			edgeWeight= Double.parseDouble(atts.getValue("weight"));
    	    		}
    	    		catch (Exception e) {}
    	    		else if (attName.equals("type"))
    	    			try {
    	        			if (atts.getValue("type").equals("directed"))
    	        				edgeDirected = true;
    	        		}
    	        		catch (Exception e) {}
    			else
    				extraAtts.put(attName, attValue);
    		}
    		
    		callbacks.onEdge(edgeId, edgeSource, edgeTarget, edgeWeight,
    				edgeDirected, extraAtts);
    	}
    }
}