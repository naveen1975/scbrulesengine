package com.scb.cache;

import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {
	
	protected static Properties props = new Properties();
	
	public static void loadProperties(String path)
	{
		try
		{
			props.load(new FileInputStream(path));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getConfig(String propName)
	{
		return props.getProperty(propName);
	}
	
	
	
}
