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
		List<FundProduct> fundList = new ArrayList<FundProduct>();
		
		String sqlSelect = "SELECT CUST_ID, "
				  +"CUST_NAME, "
				  +"RISK_PROFILE, "
				  +"ASSET_CLASS, "
	              +"GAP_PERCENT, "
	              +"GAP_AMOUNT "
				 + "FROM V_CUST ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					FundProduct product = new FundProduct();					
					//product.isin = rs.getString("PRODUCT_ID");
					product.productId = rs.getString("PRODUCT_ID");
					product.productName = rs.getString("PRODUCT_NAME");
					product.assetClassId = rs.getString("ASSET_CLASS_ID");
					product.riskRating = rs.getString("PRODUCT_RISK_RATING");
					product.recommendedSegment = rs.getString("FND_APPRVED_SGMT");
					product.focusFundIndicator = rs.getString("FOCUS_FUND_INDICATOR");
					product.fundType = rs.getString("FUND_TYPE");
					
		            fundList.add(product);
				}
				
				LOG.info("Loaded <" + fundList.size() + "> funds.");
				
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
					if(conn!=null) conn.close();
				}
				catch(Exception ignore)
				{
				}
				
				LOG.info("getFundProductList() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	

}
