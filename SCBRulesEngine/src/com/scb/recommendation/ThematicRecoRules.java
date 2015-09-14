package com.scb.recommendation;

import java.util.ArrayList;
import java.util.List;

import com.scb.data.Customer;
import com.scb.data.HouseView;
import com.scb.data.IProduct;
import com.scb.data.RiskProfileToProductRiskMap;
import com.scb.data.ThematicFundProduct;

public class ThematicRecoRules implements IRule {
	
	
	
	private HouseView houseView = null;
	
	private List<ThematicFundProduct> fundProductList = null;
	
	private RiskProfileToProductRiskMap map = null;
	
	//Set the data
	public void setHouseView(HouseView houseView)
	{
		this.houseView = houseView;
	}
	
	public HouseView getHouseView()
	{
		return houseView;
	}
	
	public void setThematicProductList(List<ThematicFundProduct> fundProductList)
	{
		this.fundProductList = fundProductList;
	}
	
	public List<ThematicFundProduct> getThematicProductList()
	{
		return this.fundProductList;
	}
	
	public void setRiskProfileToProductRiskMap(RiskProfileToProductRiskMap map)
	{
		this.map = map;
	}
	
	public RiskProfileToProductRiskMap getRiskProfileToProductRiskMap()
	{
		return map;
	}	

	public ThematicRecoRules()
	{
		
	}
	
	public RecoResult execute(Customer customer) throws Exception
	{
		RecoResult result = new RecoResult();
		//The original list is already sorted by focus indiatory and sharpe value when retrieved from DB.
		for(ThematicFundProduct product : getThematicProductList())
		{
			if(matchCustomerComplance(customer.riskProfile, product.riskRating))
			{					
				result.thematicFundsCategory.add(product);
			}
		}
		
		return result;
	}
	
	boolean matchCustomerComplance(String riskProfile, String productRating)
	{
		if(riskProfile!=null && productRating!=null)
		{
			String riskRating = getRiskProfileToProductRiskMap().getRiskRating(riskProfile);
		
			return productRating.equalsIgnoreCase(riskRating);
		}
		
		return false;
	}	
}
