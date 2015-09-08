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
