package com.scb.rules;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scb.cache.CacheService;
import com.scb.dao.CommonDAO;
import com.scb.dao.CustomerDAO;
import com.scb.dao.ProductDAO;
import com.scb.data.Customer;
import com.scb.recommendation.FundRecoRules;
import com.scb.recommendation.RecoResult;
import com.scb.recommendation.ThematicRecoRules;

public class SCBRulesEngine {
	
	Log LOG = LogFactory.getLog(SCBRulesEngine.class);
	
	static boolean isInited = false;
	static FundRecoRules fundRules = new FundRecoRules();
	static ThematicRecoRules thematicRules = new ThematicRecoRules(); 
	
	static List<Customer> customerList = null;
	
	public static void initialize()
	{
		if(!isInited)
		{
			//Load Configuration
			CommonDAO commonDao = new CommonDAO();
			CustomerDAO customerDao = new CustomerDAO();
			ProductDAO productDao = new ProductDAO();
			
			//Load Cache
			fundRules.setModelPortfolioList(commonDao.getModelPortfolioList());
			fundRules.setHouseViewList(commonDao.getHouseViewList());
			fundRules.setFundProductList(productDao.getFundProductList());
			fundRules.setRiskProfileToProductRiskMap(commonDao.getRiskProfileToProductRiskRatingMap());
			
			thematicRules.setThematicProductList(productDao.getThematicFundProductList());
			thematicRules.setRiskProfileToProductRiskMap(fundRules.getRiskProfileToProductRiskMap());
			
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
	
	static Customer getCustomer(String customerId)
	{
		for(Customer customer : customerList)
		{
			if(customer.customerId.equalsIgnoreCase(customerId))
				return customer;
		}
		
		return null;
	}

}
