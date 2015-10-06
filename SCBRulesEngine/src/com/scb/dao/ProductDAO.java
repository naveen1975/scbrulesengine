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
import com.scb.data.FundProduct;
import com.scb.data.ThematicFundProduct;


public class ProductDAO {
	

	protected Log LOG = LogFactory.getLog(ProductDAO.class);
	
	
	public Connection getConnection()
	{
		DataSourceConnectionFactory factory = (DataSourceConnectionFactory) CacheService.get(IConstants.DATASOURCE_FACTORY);
		
		return factory.getConnection();
	}	


	public List<FundProduct> getFundProductList()
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<FundProduct> fundList = new ArrayList<FundProduct>();
		
		String sqlSelect = "SELECT   fnd_ut_cd AS PRODUCT_ID, "
				  +"FUND_NM AS PRODUCT_NAME, "
				  +"ASST_CLS_L2_CD AS ASSET_CLASS_ID, "
				  +"ASST_CLS_L2_DSC, "
	              +"FND_RSK_RTG AS PRODUCT_RISK_RATING, "
	              +"' ' AS FND_APPRVED_SGMT, "
	              +"RECO_FLG AS FOCUS_FUND_INDICATOR, "
	              +"'F' AS FUND_TYPE "
				  + "FROM VW_PROD_FUND WHERE THEMATIC_FND_IND='N' AND "
	              + "FND_RSK_RTG <> 'Not Available' AND RECO_FLG = 'Y'  ";

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
	
	

	public List<ThematicFundProduct> getThematicFundProductList()
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<ThematicFundProduct> fundList = new ArrayList<ThematicFundProduct>();
		
		String sqlSelect = "SELECT   fnd_ut_cd AS PRODUCT_ID, "
				  +"FUND_NM AS PRODUCT_NAME, "
				  +"ASST_CLS_L2_CD AS ASSET_CLASS_ID, "
				  +"ASST_CLS_L2_DSC, "
	              +"FND_RSK_RTG AS PRODUCT_RISK_RATING, "
	              +"' ' AS FND_APPRVED_SGMT, "
	              +"RECO_FLG AS FOCUS_FUND_INDICATOR, "
	              +"'F' AS FUND_TYPE "
				  + "FROM VW_PROD_FUND WHERE THEMATIC_FND_IND='Y' AND "
	              + "FND_RSK_RTG <> 'Not Available' ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					ThematicFundProduct product = new ThematicFundProduct();					
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
				
				LOG.info("Loaded <" + fundList.size() + "> thematic funds.");
				
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
				
				LOG.info("getThematicFundProductList() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}
	
		
}
