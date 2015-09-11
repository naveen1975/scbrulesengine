package com.scb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scb.cache.CacheService;
import com.scb.constants.IConstants;
import com.scb.data.Customer;
import com.scb.data.FundProduct;
import com.scb.data.IProduct;

public class CustomerDAO {
	
	protected Log LOG = LogFactory.getLog(CustomerDAO.class);
	
	
	public Connection getConnection()
	{
		DataSourceConnectionFactory factory = (DataSourceConnectionFactory) CacheService.get(IConstants.DATASOURCE_FACTORY);
		
		return factory.getConnection();
	}	


	public List<Customer> getCustomerList()
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Customer> customerList = new ArrayList<Customer>();
		
		String sqlSelect = "SELECT CUST_ID, "
				  +"CUST_NAME, "
				  +"RISK_PROFILE "
				 + "FROM V_CUST ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					Customer customer = new Customer();					
					
					customer.customerId = rs.getString("CUST_ID");
					customer.customerName = rs.getString("CUST_NAME");
					customer.riskProfile = rs.getString("RISK_PROFILE");
					
					customer.customerHoldings = getCustomerHolding(conn, customer.customerId);
					
					customerList.add(customer);
				}
				
				LOG.info("Loaded <" + customerList.size() + "> customers.");
				
				return customerList;
			}
			catch(Exception err)
			{
				LOG.error("Exception : " + err.getMessage());
				err.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs!=null) rs.close();
					if(stmt!=null) stmt.close();
					if(conn!=null) conn.close();
				}
				catch(Exception ignore)
				{
				}
				
				LOG.info("getCustomerList() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}
	
	public List<IProduct> getCustomerHolding(Connection conn, String customerId)
	{
		long startTime = System.currentTimeMillis();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<IProduct> fundList = new ArrayList<IProduct>();
		
		String sqlSelect = "SELECT PRODUCT_ID, "
				  +"ASSET_CLASS "
				 + "FROM V_CUST_HOLDINGS WHERE CUST_ID = '" + customerId + "'";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					FundProduct product = new FundProduct();					
					
					product.productId = rs.getString("PRODUCT_ID");
					product.assetClassId = rs.getString("ASSET_CLASS");
					
					fundList.add(product);
				}
				
				LOG.info("Loaded <" + fundList.size() + "> customer holdings.");
				
				return fundList;
			}
			catch(Exception err)
			{
				LOG.error("Exception : " + err.getMessage());
				err.printStackTrace();
			}
			finally
			{
				try
				{
					if(rs!=null) rs.close();
					if(stmt!=null) stmt.close();
				}
				catch(Exception ignore)
				{
				}
				
				LOG.info("getCustomerHolding() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	
	
	

}
