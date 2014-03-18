package com.telmomenezes.synthetic.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;


public abstract class Command {
	private String errorMessage;
	private CommandLine cline;
	
	
	public abstract String name();
	
	
	public abstract String help();
	
	
	public abstract boolean run() throws SynCliException;
	
	
	public void setCline(CommandLine cline) {
		this.cline = cline;
	}
	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	protected String getStringParam(String param) throws SynCliException {
		return getStringParam(param, true, "");
	}
	
	
	protected String getStringParam(String param, String def) throws SynCliException {
		return getStringParam(param, false, def);
	}
	
	
	protected String getStringParam(String param, boolean required, String def) throws SynCliException {
		if(!cline.hasOption(param)) {
			if (required) {
				throw new SynCliException("parameter " + param + " must be specified.");
			}
			else {
				return def;
			}
        }
		
		return cline.getOptionValue(param);
	}
	
	
	protected int getIntegerParam(String param) throws SynCliException {
		return getIntegerParam(param, true, -1);
	}
	
	
	protected int getIntegerParam(String param, int def) throws SynCliException {
		return getIntegerParam(param, false, def);
	}
	
	
	protected int getIntegerParam(String param, boolean required, int def) throws SynCliException {
		String str = getStringParam(param, required, "");
		
		if (str.equals("")) {
			return def;
		}
		else {
			return new Integer(str);
		}
	}
	
	
	protected double getDoubleParam(String param) throws SynCliException {
		return getDoubleParam(param, true, -1);
	}
	
	
	protected double getDoubleParam(String param, double def) throws SynCliException {
		return getDoubleParam(param, false, def);
	}
	
	
	protected double getDoubleParam(String param, boolean required, double def) throws SynCliException {
		String str = getStringParam(param, required, "");
		
		if (str.equals("")) {
			return def;
		}
		else {
			return new Double(str);
		}
	}
	
	
	protected boolean paramExists(String param) {
		return cline.hasOption(param);
	}
	
	
	protected List<String> textFiles(String directory) {
		List<String> textFiles = new ArrayList<String>();
		File dir = new File(directory);
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith((".txt"))) {
				textFiles.add(file.getName());
		    }
		}
		
		return textFiles;
	}
}
