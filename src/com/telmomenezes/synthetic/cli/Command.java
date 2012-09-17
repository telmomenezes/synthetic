package com.telmomenezes.synthetic.cli;

import org.apache.commons.cli.CommandLine;

public abstract class Command {
	private String errorMessage;
	
	public abstract boolean run(CommandLine cline);

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
