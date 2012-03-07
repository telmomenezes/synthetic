package com.telmomenezes.synthetic;


public class ParseArgs {
	
	private String[] args;
	
	
	public ParseArgs(String[] args)
	{
		this.args = args;
	}
	
	public String getStringArg(String arg)
	{
		boolean found = false;
		
		for (String str : args) {
			if (found)
				return str;
			
			if (str.equals("-" + arg))
				found = true;
		}
		
		return null;
	}
	
	public Integer getIntegerArg(String arg)
	{
		try {
			return new Integer(getStringArg(arg));
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public Double getDoubleArg(String arg)
	{
		try {
			return new Double(getStringArg(arg));
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public String strValue(String arg, String def)
	{
		String res = getStringArg(arg);
		
		if (res == null)
			return def;
		else
			return res;
	}
	
	public int intValue(String arg, int def)
	{
		Integer res = getIntegerArg(arg);
		
		if (res == null)
			return def;
		else
			return res;
	}
	
	public double doubleValue(String arg, double def)
	{
		Double res = getDoubleArg(arg);
		
		if (res == null)
			return def;
		else
			return res;
	}
}
