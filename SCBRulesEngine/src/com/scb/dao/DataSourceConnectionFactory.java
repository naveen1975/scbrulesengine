package com.scb.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.scb.cache.Configuration;
import com.scb.constants.IConstants;

public class DataSourceConnectionFactory {
	
	protected DataSource ds = null;
	
	boolean isInited = false;
	
	DBConnection myConn = null;
	
	
	public void initDataSource()
	{
		try
		{
			//InitialContext ctx = new InitialContext();
			//ds = (DataSource)ctx.lookup(Configuration.getConfig(IConstants.DATASOURCE_FACTORY));
			
			//Connection conn = ds.getConnection();
			//System.out.println("Connection:" +  conn);
			try 
		    {       

		      Class.forName("com.ibm.db2.jcc.DB2Driver");
		      isInited = true;
		    } 
		    catch (ClassNotFoundException e)
		    {
		       System.err.println("Could not load DB2 driver \n");
		       System.err.println(e.getMessage());
		       System.exit(1);
		    }

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
			if(!isInited)
				initDataSource();
			
			if(myConn == null || myConn.isClosed())
			{
				String url = "jdbc:db2j:net://9.121.57.142:50000/SCBPOC";
		        Connection conn = DriverManager.getConnection(url,"db2inst1", "db2inst1");
		        
		        myConn = new DBConnection(conn);
		        
				
		        System.out.println("Got Connection");
			}
	        
			return myConn;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
