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
import com.scb.data.HouseView;
import com.scb.data.ModelPortfolio;
import com.scb.data.RiskProfileToProductRiskMap;
import com.scb.vo.ClsCodeVO;


public class CommonDAO {
	
	Log LOG = LogFactory.getLog(CommonDAO.class);
	
	public Connection getConnection()
	{
		DataSourceConnectionFactory factory = (DataSourceConnectionFactory) CacheService.get(IConstants.DATASOURCE_FACTORY);
		
		return factory.getConnection();
	}
	
	public List<ClsCodeVO> getClsList(String schemeCode)
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<ClsCodeVO> list = new ArrayList<ClsCodeVO>();
		try
		{
			String sqlSelect = "SELECT CLS_ID, A.CLS_SCHM_ID, CLS_CD, CLS_SHRT_NM, CLS_CD_NM, CLS_CD_DESC, CLS_SCHM_CD, CLS_SCHM_NM FROM T_CL A, T_CL_SCHM B "
						 + "WHERE A.CLS_SCHM_ID = B.CLS_SCHM_ID AND B.CLS_SCHM_CD = ?";
		
			stmt = conn.prepareStatement(sqlSelect);
			stmt.setString(1, schemeCode);
			
			LOG.info(sqlSelect + " (" + schemeCode + ").");
			rs = stmt.executeQuery();
			while(rs.next())
			{
				ClsCodeVO clsCode = new ClsCodeVO();
				
				clsCode.clsId = rs.getString("CLS_ID");
				clsCode.schemeId = rs.getString("CLS_SCHM_ID");
				clsCode.code = rs.getString("CLS_CD");
				clsCode.shortName = rs.getString("CLS_SHRT_NM");
				clsCode.descriptionName = rs.getString("CLS_CD_NM");
				clsCode.descriptionName = rs.getString("CLS_CD_DESC");
				clsCode.schemeCode= rs.getString("CLS_SCHM_CD");
				clsCode.schemeName = rs.getString("CLS_SCHM_NM");
				
				list.add(clsCode);
			}
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
			
			LOG.info("getClsList() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
		}
		return list;
	}

	public List<HouseView> getHouseViewList()
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<HouseView> houseViewList = new ArrayList<HouseView>();
		
		String sqlSelect = "SELECT   ASST_CLS_L2_CD AS ASSET_CLASS_ID, "
				  +"ASST_CLS_L2_DSC AS ASSET_CLASS, "
	              +"HSVW_CD AS SENTIMENT "
				 + "FROM VW_GIC_HOUSE_VIEW ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					HouseView houseView = new HouseView();					

					houseView.assetClassId = rs.getString("ASSET_CLASS_ID");
					houseView.sentiment = rs.getString("SENTIMENT");
					houseView.id = rs.getString("ASSET_CLASS_ID");
					
					houseViewList.add(houseView);
				}
				
				LOG.info("Loaded <" + houseViewList.size() + "> houseviews.");
				
				return houseViewList;
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
				
				LOG.info("getHouseViewList() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	
	
	public List<ModelPortfolio> getModelPortfolioList()
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<ModelPortfolio> modelPortfolioList = new ArrayList<ModelPortfolio>();
		
		String sqlSelect = "SELECT  CUST_RSK_PRFL_CLS_ID AS RISK_PROFILE, "
	              +"ASST_CLASS_CLS_ID AS ASSET_CLASS_2, "
	              +"PRTFL_ALLOC_PCNT AS LEVEL_2_GAP "	              
				 + "FROM T_MODEL_PRTFL_ALLOC ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					ModelPortfolio portfolio = new ModelPortfolio();					

					portfolio.riskProfile = rs.getString("RISK_PROFILE");
					//portfolio.assetClassLevel1 = rs.getString("ASSET_CLASS_1");
					portfolio.assetClassLevel2 = rs.getString("ASSET_CLASS_2");
					//portfolio.modelGapLevel1 = rs.getDouble("LEVEL_1_GAP");
					portfolio.modelGapLevel2 = rs.getDouble("LEVEL_2_GAP");
					
					modelPortfolioList.add(portfolio);
				}
				
				LOG.info("Loaded <" + modelPortfolioList.size() + "> model portoflio.");
				
				return modelPortfolioList;
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
				
				LOG.info("getModelPortfolioList() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	
	
	
	public RiskProfileToProductRiskMap getRiskProfileToProductRiskRatingMap()
	{
		long startTime = System.currentTimeMillis();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		RiskProfileToProductRiskMap map = new RiskProfileToProductRiskMap();
		
		String sqlSelect = "SELECT   RISK_PROFILE, "
				  +"RISK_RATING "	              
				 + "FROM V_RISKPROFILE_RISKRATING_MAP ";

			try
			{
				stmt = conn.prepareStatement(sqlSelect);
				
				LOG.info(sqlSelect);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					map.addMapping(rs.getString("RISK_PROFILE"), rs.getString("RISK_RATING"));					
				}
				
				return map;
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
				
				LOG.info("getRiskProfileToProductRiskRatingMap() Time Taken " + (System.currentTimeMillis() - startTime) + " msecs");			
			}
		return null;
	}	
		
	
	public String getNextSequenceNo(String seq) throws Exception
	{
		String sqlSel = "SELECT NEXT VALUE FOR " + seq + " FROM sysibm.sysdummy1";
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
		
			stmt = conn.prepareStatement(sqlSel);
			
			LOG.info(sqlSel);
			rs = stmt.executeQuery();
			String val = "";
			while(rs.next())
			{
				val = rs.getString(1);
			}
			return val;
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
			
		}
		return null;
	}	
	
	
}
