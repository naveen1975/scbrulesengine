package com.scb.dao;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.scb.cache.Configuration;
import com.scb.constants.IConstants;

public class DataSourceConnectionFactory {
	
	protected DataSource ds = null;
	
	
	public void initDataSource()
	{
		try
		{
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(Configuration.getConfig(IConstants.DATASOURCE_FACTORY));
			
			//Connection conn = ds.getConnection();
			//System.out.println("Connection:" +  conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public Connection getConnection()
	{
		try
		{
			System.out.println("Get DB Connection: ");
			if(ds == null)
				initDataSource();
			
			return ds.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
