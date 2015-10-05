package com.scb.data;

import java.util.*;

import com.scb.constants.IConstants;

public class Customer {

	public String customerId = null;
	public String customerName = null;
	
	public int intRiskProfile = 0;
	
	public HashMap<String, Double> portfolioGap = new HashMap<String, Double>();	
	public String segment = IConstants.SEGMENT_PRIORITY;
	
	public double cashBalance = 0.0;
	public double loanValue = 0.0;
	
	//Customer Holdings
	public List<IProduct> customerHoldings = new ArrayList<IProduct>();
	public String riskProfile = null;
	
	
	public boolean isCustomerHoldingProduct(String productId)
	{
		for(IProduct product : customerHoldings)
		{
			if(product.productId.equalsIgnoreCase(productId))
				return true;
		}
		
		return false;
	}
	
	public Double getPortfolioGap(String assetClassId)
	{
		return portfolioGap.get(assetClassId);
	}

}
