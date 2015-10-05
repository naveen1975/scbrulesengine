package com.scb.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scb.cache.CacheService;
import com.scb.constants.IConstants;
import com.scb.data.AssetClassLevel2;
import com.scb.data.Customer;
import com.scb.data.FundProduct;
import com.scb.data.IProduct;
import com.scb.recommendation.RecoResult;

public class CustomerDAO {
	
	protected Log LOG = LogFactory.getLog(CustomerDAO.class);
	
	public static int unqId = 0;
	
	
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
		
		String sqlSelect = "SELECT DISTINCT a.CUST_ID, "
				  +"CUST_NM, "
				  +"a.CUST_RSK_PRFL_CD "
				 + "FROM VW_CUST A, vw_cust_inv_prtfl_summ b WHERE a.cust_id=b.cust_id ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				
				//Get All customer holdings
				HashMap<String,List<IProduct>> allCustomerHoldings = getAllCustomerHolding(conn);
				HashMap<String, HashMap<String, Double>> allPortfolioGaps = getAllPortfolioGap(conn);
				
				while(rs.next())
				{
					Customer customer = new Customer();					
					
					customer.customerId = rs.getString("CUST_ID");
					customer.customerName = rs.getString("CUST_NM");
					
					customer.riskProfile = rs.getString("CUST_RSK_PRFL_CD");
					
					if(customer.riskProfile != null && 
						(customer.riskProfile.equalsIgnoreCase("CIP_3") || 
						 customer.riskProfile.equalsIgnoreCase("CIP_4") ||
						 customer.riskProfile.equalsIgnoreCase("CIP_5") ||
						 customer.riskProfile.equalsIgnoreCase("CIP_6")
								)
						)
					{
						customer.customerHoldings = allCustomerHoldings.get(customer.customerId);
						customer.portfolioGap = allPortfolioGaps.get(customer.customerId);
						
						LOG.info("Portfolio gap for customer : " + customer.portfolioGap.keySet().size() + " for cusotmer : " + customer.customerId);
	
						//customer.customerHoldings = getCustomerHolding(conn, customer.customerId);
						//customer.portfolioGap = getPortfolioGap(conn, customer.customerId);
						
						if(customer.riskProfile.equalsIgnoreCase("CIP_3") )
							customer.intRiskProfile = 3;
						if(customer.riskProfile.equalsIgnoreCase("CIP_4") )
							customer.intRiskProfile = 4;						
						if(customer.riskProfile.equalsIgnoreCase("CIP_5") )
							customer.intRiskProfile = 5;						
						if(customer.riskProfile.equalsIgnoreCase("CIP_6") )
							customer.intRiskProfile = 6;
						
						customerList.add(customer);
					}
					
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
		
		String sqlSelect = "SELECT PRD_ID AS PRODUCT_ID, "
				  +"ASST_CLS_L2_CD AS ASSET_CLASS "
				 + "FROM T_IP_CUST_INV_SUMM A, VW_PROD_FUND B WHERE A.PRD_ID=B.fnd_ut_cd AND CUST_ID = '" + customerId + "'";

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
	
	public HashMap<String, Double> getPortfolioGap(Connection conn, String customerId)
	{
		long startTime = System.currentTimeMillis();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap<String, Double> portfolioGap = new HashMap<String,Double>();
		
		String sqlSelect = "SELECT "
				  +"ASST_CLS_L2_CD AS ASSET_CLASS, "
				  +"GAP_L2_AMT AS GAP "
				 + "FROM vw_cust_inv_prtfl_summ WHERE CUST_ID = '" + customerId + "'";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{	
					//Double gap=new BigDecimal(rs.getDouble("GAP") ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					portfolioGap.put(rs.getString("ASSET_CLASS"), rs.getDouble("GAP"));
				}
				
				LOG.info("Loaded <" + portfolioGap.size() + "> customer portfoliogaps.");
				
				return portfolioGap;
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
	

	
	public HashMap<String,List<IProduct>> getAllCustomerHolding(Connection conn)
	{
		long startTime = System.currentTimeMillis();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap<String, List<IProduct>> result = new HashMap<String, List<IProduct>>();

		
		String sqlSelect = "SELECT PRD_ID AS PRODUCT_ID, "
				  +"ASST_CLS_L2_CD AS ASSET_CLASS, CUST_ID "
				 + "FROM T_IP_CUST_INV_SUMM A, VW_PROD_FUND B WHERE A.PRD_ID=B.fnd_ut_cd ORDER BY CUST_ID";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					List<IProduct> fundList = result.get(rs.getString("CUST_ID"));
					if(fundList==null)
					{
						fundList = new ArrayList<IProduct>();
						result.put(rs.getString("CUST_ID"), fundList);
					}
					FundProduct product = new FundProduct();
					
					product.productId = rs.getString("PRODUCT_ID");
					product.assetClassId = rs.getString("ASSET_CLASS");
					
					fundList.add(product);
				}
				
				//LOG.info("Loaded <" + fundList.size() + "> customer holdings.");
				
				return result;
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
				
				LOG.info("getAllCustomerHolding() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	
	
	public HashMap<String, HashMap<String, Double>> getAllPortfolioGap(Connection conn)
	{
		long startTime = System.currentTimeMillis();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		HashMap<String, HashMap<String, Double>> result = new HashMap<String, HashMap<String,Double>>();
		
		
		String sqlSelect = "SELECT "
				  +"ASST_CLS_L2_CD AS ASSET_CLASS, "
				  +"GAP_L2_AMT AS GAP, CUST_ID "
				 + "FROM vw_cust_inv_prtfl_summ ORDER BY CUST_ID";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{	
					//Double gap=new BigDecimal(rs.getDouble("GAP") ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					HashMap<String, Double> portfolioGap = result.get(rs.getString("CUST_ID"));
					
					if(portfolioGap == null)
					{
						portfolioGap = new HashMap<String,Double>();
						result.put(rs.getString("CUST_ID"), portfolioGap);
					}
					
					portfolioGap.put(rs.getString("ASSET_CLASS"), rs.getDouble("GAP"));
				}
				
				
				
				return result;
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
				
				LOG.info("getAllPortfolioGap() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	
		
	
	public boolean writeRecommendations(RecoResult result) throws Exception
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		result.recoId = result.customer.customerId + "_" + System.currentTimeMillis();

		String sqlRecInsert = "INSERT INTO T_CUST_RECO "
				  +"(RECO_ID, RECO_EMP_ID, RECO_CUST_ID, RECO_ACL2_CLS_ID, RECO_HW_CLS_ID, RECO_SUGST_VAL_AMT, "
				 + "RECO_TYP_CLS_ID, RECO_UNQ_ID, RECO_BTH_ID, RECO_SEQ_ID, "
				  +"RECO_TS, REC_INSRT_DT, REC_INSRT_TS) VALUES "
				 + "(?, ?, ?, ?, ?, ?, ?,? ,? ,? ,CURRENT TIMESTAMP ,CURRENT DATE ,CURRENT TIMESTAMP) ";

			try
			{
				stmt = conn.prepareStatement(sqlRecInsert);
				int seq = 0;
				
				for(AssetClassLevel2 assetClass : result.sellCategory.assetClassList)
				{
					seq++;
					String recoId = "" + System.currentTimeMillis();					
					stmt.setString(1, recoId);
					stmt.setString(2, "");
					stmt.setString(3, result.customer.customerId);
					stmt.setString(4, assetClass.id);
					stmt.setString(5, assetClass.houseViewId);
					LOG.info("Writing Gap : " + assetClass.gap);
					stmt.setBigDecimal(6, new BigDecimal(assetClass.gap));
					//stmt.setDouble(6, assetClass.gap);
					stmt.setString(7, IConstants.OC_NEG);
					stmt.setString(8, result.recoId);
					stmt.setInt(9, 1); //Batch ID???
					stmt.setInt(10, seq);
					
					stmt.execute();

					//Write products for this recommendation of assetclass
					writeProducts(conn, recoId, assetClass.id, result.recoId, seq, assetClass.products);
				}
				
				seq = 0;
				
				for(AssetClassLevel2 assetClass : result.buyCategory.assetClassList)
				{
					seq++;
					String recoId = "" + System.currentTimeMillis();					
					stmt.setString(1, recoId);
					stmt.setString(2, "");
					stmt.setString(3, result.customer.customerId);
					stmt.setString(4, assetClass.id);
					stmt.setString(5, assetClass.houseViewId);
					stmt.setBigDecimal(6, new BigDecimal(assetClass.gap));
					stmt.setString(7, IConstants.UC_POS);
					stmt.setString(8, result.recoId);
					stmt.setInt(9, 1); //Batch ID???
					stmt.setInt(10, seq);
					
					stmt.execute();

					//Write products for this recommendation of assetclass
					writeProducts(conn, recoId, assetClass.id, result.recoId, seq, assetClass.products);
				}

				seq = 0;
				
				for(AssetClassLevel2 assetClass : result.rmNeutralCategory.assetClassList)
				{
					seq++;
					String recoId = "" + System.currentTimeMillis();					
					stmt.setString(1, recoId);
					stmt.setString(2, "");
					stmt.setString(3, result.customer.customerId);
					stmt.setString(4, assetClass.id);
					stmt.setString(5, assetClass.houseViewId);
					stmt.setBigDecimal(6, new BigDecimal(assetClass.gap));
					stmt.setString(7, IConstants.UC_NUT);
					stmt.setString(8, result.recoId);
					stmt.setInt(9, 1); //Batch ID???
					stmt.setInt(10, seq);
					
					stmt.execute();

					//Write products for this recommendation of assetclass
					writeProducts(conn, recoId, assetClass.id, result.recoId, seq, assetClass.products);
				}
				
				
				seq = 0;
				
				for(AssetClassLevel2 assetClass : result.rmNegCategory.assetClassList)
				{
					seq++;
					String recoId = "" + System.currentTimeMillis();					
					stmt.setString(1, recoId);
					stmt.setString(2, "");
					stmt.setString(3, result.customer.customerId);
					stmt.setString(4, assetClass.id);
					stmt.setString(5, assetClass.houseViewId);
					stmt.setBigDecimal(6, new BigDecimal(assetClass.gap));;
					stmt.setString(7, IConstants.UC_NEG);
					stmt.setString(8, result.recoId);
					stmt.setInt(9, 1); //Batch ID???
					stmt.setInt(10, seq);
					
					stmt.execute();

					//Write products for this recommendation of assetclass
					writeProducts(conn, recoId, assetClass.id, result.recoId, seq, assetClass.products);
				}				

				seq = 0;
				
				for(AssetClassLevel2 assetClass : result.holdPositiveCategory.assetClassList)
				{
					seq++;
					String recoId = "" + System.currentTimeMillis();					
					stmt.setString(1, recoId);
					stmt.setString(2, "");
					stmt.setString(3, result.customer.customerId);
					stmt.setString(4, assetClass.id);
					stmt.setString(5, assetClass.houseViewId);
					stmt.setBigDecimal(6, new BigDecimal(assetClass.gap));
					stmt.setString(7, IConstants.OC_POS);
					stmt.setString(8, result.recoId);
					stmt.setInt(9, 1); //Batch ID???
					stmt.setInt(10, seq);
					
					stmt.execute();

					//Write products for this recommendation of assetclass
					writeProducts(conn, recoId, assetClass.id, result.recoId, seq, assetClass.products);
				}
				
				seq = 0;
				
				for(AssetClassLevel2 assetClass : result.holdNeutralCategory.assetClassList)
				{
					seq++;
					String recoId = "" + System.currentTimeMillis();					
					stmt.setString(1, recoId);
					stmt.setString(2, "");
					stmt.setString(3, result.customer.customerId);
					stmt.setString(4, assetClass.id);
					stmt.setString(5, assetClass.houseViewId);
					stmt.setBigDecimal(6, new BigDecimal(assetClass.gap));
					stmt.setString(7, IConstants.OC_NUT);
					stmt.setString(8, result.recoId);
					stmt.setInt(9, 1); //Batch ID???
					stmt.setInt(10, seq);
					
					stmt.execute();

					//Write products for this recommendation of assetclass
					writeProducts(conn, recoId, assetClass.id, result.recoId, seq, assetClass.products);
					
					conn.commit();
				}				
				return true;
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
				
				LOG.info("writeRecommendations() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return false;		
	}
	

	public boolean writeProducts(Connection conn, String recoId, String assetClassId, String recoUniqueId, int seqNo, List<IProduct> productList)
	{
		long startTime = System.currentTimeMillis();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sqlRecInsert = "INSERT INTO T_CUST_RECO_PRD "
							+"(RECO_ID, RECO_ACL2_CLS_ID, RECO_PRD_ID, RECO_UNQ_ID, RECO_SEQ_ID, REC_INSRT_DT, REC_INSRT_TS) "
							+ " VALUES "
							+ "(?, ?, ?, ?, ? ,CURRENT DATE ,CURRENT TIMESTAMP) ";


			try
			{
				stmt = conn.prepareStatement(sqlRecInsert);
				
				LOG.info(sqlRecInsert);
				int seq = 0;
				
				for(IProduct product : productList)
				{
					seq++;
					stmt.setString(1, recoId);
					stmt.setString(2, assetClassId);
					stmt.setString(3, product.productId);					
					stmt.setString(4, recoUniqueId + "_" + assetClassId);
					stmt.setInt(5, seq);
					
					stmt.execute();
				}

				LOG.info("Wrote <" + productList.size() + "> records to db.");
				return true;
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
				
				LOG.info("writeProducts() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return false;
	}	
}
