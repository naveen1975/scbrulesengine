package com.scb.rules;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scb.cache.CacheService;
import com.scb.constants.IConstants;
import com.scb.dao.CommonDAO;
import com.scb.dao.CustomerDAO;
import com.scb.dao.DataSourceConnectionFactory;
import com.scb.dao.ProductDAO;
import com.scb.data.Customer;
import com.scb.recommendation.FundRecoRules;
import com.scb.recommendation.RecoResult;
import com.scb.recommendation.ThematicRecoRules;

public class SCBRulesEngine {
	
	static Log LOG = LogFactory.getLog(SCBRulesEngine.class);
	
	static boolean isInited = false;
	static FundRecoRules fundRules = new FundRecoRules();
	static ThematicRecoRules thematicRules = new ThematicRecoRules(); 
	
	static List<Customer> customerList = null;
	
	static CommonDAO commonDao = new CommonDAO();
	static CustomerDAO customerDao = new CustomerDAO();
	static ProductDAO productDao = new ProductDAO();
	
	public static void initialize()
	{
		if(!isInited)
		{
			DataSourceConnectionFactory ds = new DataSourceConnectionFactory();
			ds.initDataSource();
			
			CacheService.put(IConstants.DATASOURCE_FACTORY, ds);
			
			//Load Cache
			fundRules.setModelPortfolioList(commonDao.getModelPortfolioList());
			fundRules.setHouseViewList(commonDao.getHouseViewList());
			fundRules.setFundProductList(productDao.getFundProductList());
			//fundRules.setRiskProfileToProductRiskMap(commonDao.getRiskProfileToProductRiskRatingMap());
			
			thematicRules.setThematicProductList(productDao.getThematicFundProductList());
			//thematicRules.setRiskProfileToProductRiskMap(fundRules.getRiskProfileToProductRiskMap());
			
			customerList = customerDao.getCustomerList();
			//CacheService.put(key, obj);
			//Initialize rules engines
			
		}
	}
	
	public static RecoResult runRules(String customerId) throws Exception
	{
		Customer customer = getCustomer(customerId);
		
		RecoResult fundResult = fundRules.execute(customer);
		RecoResult thematicResult = thematicRules.execute(customer);
		
		fundResult.thematicFundsCategory = thematicResult.thematicFundsCategory;
		
		return fundResult;
	}
	
	public static boolean writeRulesToDB(RecoResult result) throws Exception
	{
		if(result.getRecoCount()>0)
		{
			return customerDao.writeRecommendations(result);
		}
		else
		{
			LOG.info("No Recommendations for customer : " + result.customer.customerId);
		}
		
		return false;
	}
	
	static Customer getCustomer(String customerId)
	{
		for(Customer customer : customerList)
		{
			if(customer.customerId.equalsIgnoreCase(customerId))
				return customer;
		}
		
		return null;
	}
	
	public static void main(String[] args) throws Exception
	{
		initialize();
		int genCount = 0;
		String selCustId = args.length > 0 ? args[0]:null;
		
		customerDao.deleteRecommendations();
		for(Customer customer : customerList)
		{
			if(selCustId==null || customer.customerId.equalsIgnoreCase(selCustId))
			{
				try
				{
					if(writeRulesToDB(runRules(customer.customerId))) genCount++;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.out.println("Unable to write rules for customer " + customer.customerId);
				}
			}
		}
		
		LOG.info("Number of customers for which the recommendations are generated : " + genCount);
		LOG.info("Number of customers for whom the recommendations are not generated : " + (customerList.size() - genCount));		
	}

}
