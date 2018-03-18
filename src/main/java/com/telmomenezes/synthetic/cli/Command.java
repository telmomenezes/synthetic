package com.telmomenezes.synthetic.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;


public abstract class Command {
	private CommandLine cline;
	
	
	public abstract String name();
	
	
	public abstract String help();
	
	
	public abstract boolean run() throws SynCliException;
	
	
	void setCline(CommandLine cline) {
		this.cline = cline;
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


	int getIntegerParam(String param, int def) throws SynCliException {
		String str = getStringParam(param, false, "");

		if (str.equals("")) {
			return def;
		}
		else {
			return new Integer(str);
		}
	}

	
	double getDoubleParam(String param, double def) throws SynCliException {
		String str = getStringParam(param, false, "");
		
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
	
	
	List<String> textFiles(String directory) {
		List<String> textFiles = new ArrayList<String>();
		File dir = new File(directory);
		File[] files = dir.listFiles();
		if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith((".txt"))) {
                    textFiles.add(file.getName());
                }
            }
        }
		
		return textFiles;
	}
}
