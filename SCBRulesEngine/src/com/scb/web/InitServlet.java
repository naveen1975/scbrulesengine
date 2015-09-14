package com.scb.web;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.scb.cache.CacheService;
import com.scb.cache.Configuration;
import com.scb.constants.IConstants;
import com.scb.dao.DataSourceConnectionFactory;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config)
	{
		try
		{
			super.init(config);
		}
		catch(Exception t)
		{
			t.printStackTrace();
		}
		
		String webAppPath = getServletContext().getRealPath("/");
		String log4jPath = webAppPath + "/WEB-INF/log4j.xml";
		
		String propsPath = config.getInitParameter("propertyFilePath");
		
		Configuration.loadProperties(propsPath);
		
		System.out.println("Properties loaded.");
		
		initApplicationObjects();
		
	}
	
	public void initApplicationObjects()
	{
		
		//Connect to DB
		DataSourceConnectionFactory dsConnFactory = new DataSourceConnectionFactory();
		Connection conn = dsConnFactory.getConnection();
		if (conn!= null){
			CacheService.put(IConstants.DATASOURCE_FACTORY , dsConnFactory);
			try
			{
				conn.close();
			}
			catch(Exception ignore)
			{
				
			}
		}		

	}
	
	public void destroy()
	{
		
	}
}
