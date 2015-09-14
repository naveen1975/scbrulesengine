package com.scb.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.scb.constants.IConstants;
import com.scb.data.AssetClassLevel2;
import com.scb.data.Customer;
import com.scb.data.FundProduct;
import com.scb.data.HouseView;
import com.scb.data.IProduct;
import com.scb.data.ModelPortfolio;
import com.scb.data.RiskProfileToProductRiskMap;

public class FundRecoRules implements IRule {
	
	private List<HouseView> houseViewList = null;
	
	private List<FundProduct> fundProductList = null;
	
	private List<ModelPortfolio> modelPortfolioList = null;
	
	private RiskProfileToProductRiskMap map = null;
	
	//Set the data
	public void setHouseViewList(List<HouseView> houseViewList)
	{
		this.houseViewList = houseViewList;
	}
	
	public List<HouseView> getHouseViewList()
	{
		return houseViewList;
	}
	
	public String getHouseView(String assetClassId)
	{
		for(HouseView houseView : getHouseViewList())
		{
			if(houseView.assetClassId.equalsIgnoreCase(assetClassId))
				return houseView.sentiment;
		}
		
		return IConstants.NEUTRAL;
	}
	
	public void setFundProductList(List<FundProduct> fundProductList)
	{
		this.fundProductList = fundProductList;
	}
	
	public List<FundProduct> getFundProductList()
	{
		return this.fundProductList;
	}
	
	public void setModelPortfolioList(List<ModelPortfolio> modelPortfolioList)
	{
		this.modelPortfolioList = modelPortfolioList;
	}
	
	public List<ModelPortfolio> getModelPortfolioList()
	{
		return modelPortfolioList;
	}
	
	public void setRiskProfileToProductRiskMap(RiskProfileToProductRiskMap map)
	{
		this.map = map;
	}
	
	public RiskProfileToProductRiskMap getRiskProfileToProductRiskMap()
	{
		return map;
	}
	
	public RecoResult execute(Customer customer) throws Exception
	{
		
		RecoResult result = new RecoResult();
		
		//For each asset class check the portfolio gap and houseview and assign the category
		List<ModelPortfolio> modelPortfolioList = getModelPortfolioForCustomer(customer);
		
		for(ModelPortfolio portfolio : modelPortfolioList)
		{
			//Get HouseView
			String houseView = getHouseView(portfolio.assetClassLevel2);
			
			//Get Portfolio Gap
			Double gap = customer.getPortfolioGap(portfolio.assetClassLevel2);
			
			//Based on category add to the result list
			if(houseView.equalsIgnoreCase(IConstants.NEGATIVE) || gap.doubleValue() < 0.0) //Sell Category
			{
				result.sellCategory.addAssetClass(portfolio.assetClassLevel2, gap.doubleValue());
				//Populate the holding fund list to sell recommendation
				recommendFundsToSell(customer, result, portfolio.assetClassLevel2);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.POSITIVE) || gap.doubleValue() > 0.0) //Buy Category
			{
				result.buyCategory.addAssetClass(portfolio.assetClassLevel2, gap.doubleValue());
				recommendFunds(customer, result.buyCategory, portfolio.assetClassLevel2);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.NEUTRAL) || gap.doubleValue() < 0.0) //RM Neutral Category
			{
				result.rmNeutralCategory.addAssetClass(portfolio.assetClassLevel2, gap.doubleValue());
				recommendFunds(customer, result.rmNeutralCategory, portfolio.assetClassLevel2);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.NEGATIVE) || gap.doubleValue() < 0.0) //RM Negative Category
			{
				result.rmNegCategory.addAssetClass(portfolio.assetClassLevel2, gap.doubleValue());
				recommendFunds(customer, result.rmNegCategory, portfolio.assetClassLevel2);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.POSITIVE) || gap.doubleValue() > 0.0) //Hold Positive Category
			{
				result.holdPositiveCategory.addAssetClass(portfolio.assetClassLevel2, gap.doubleValue());
				recommendFunds(customer, result.holdPositiveCategory, portfolio.assetClassLevel2);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.NEUTRAL) || gap.doubleValue() > 0.0) //Hold Neutral Category
			{
				result.holdNeutralCategory.addAssetClass(portfolio.assetClassLevel2, gap.doubleValue());
				recommendFunds(customer, result.holdNeutralCategory, portfolio.assetClassLevel2);
			}
		}
		
		// Rank asset classes with each category by portfolio value in the assetlcass
		rankBySuggestedValue(result.sellCategory);
		rankBySuggestedValue(result.buyCategory);
		rankBySuggestedValue(result.rmNegCategory);
		rankBySuggestedValue(result.rmNeutralCategory);
		rankBySuggestedValue(result.holdPositiveCategory);
		rankBySuggestedValue(result.holdNeutralCategory);
		
		
		return result;
	}
	
	public void recommendFundsToSell(Customer customer, RecoResult result, String assetClassId)
	{
		
		for(IProduct product : customer.customerHoldings)
		{
			
			if(product.assetClassId.equalsIgnoreCase(assetClassId))
			{
				if(result.sellCategory.products.get(assetClassId) == null)
					result.sellCategory.products.put(assetClassId, new ArrayList<IProduct>());
				
				List<IProduct> products = result.sellCategory.products.get(assetClassId);
				products.add(product);
			}
		}
	}
	
	public void recommendFunds(Customer customer, RecoAssetCategory category, String assetClassId)
	{
		
		for(IProduct product : getFundProductList())
		{
			
			if(product.assetClassId.equalsIgnoreCase(assetClassId))
			{
				if(matchCustomerComplance(customer.riskProfile, product.riskRating))
				{
					if(category.products.get(assetClassId) == null)
						category.products.put(assetClassId, new ArrayList<IProduct>());
									
					List<IProduct> products = category.products.get(assetClassId);
					products.add(product);
				}
			}
		}
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
	
	public void rankBySuggestedValue(RecoAssetCategory category)
	{
		rankBySuggestedValue(category, true);
	}
	
	
	public void rankBySuggestedValue(RecoAssetCategory category, boolean order)
	{
		Collections.sort(category.assetClassList, 
					order?Collections.reverseOrder( new AssetClassLevel2.SortBySuggestedValue() )
						 : new AssetClassLevel2.SortBySuggestedValue()
						 );
	}
	
	public List<ModelPortfolio> getModelPortfolioForCustomer(Customer customer)
	{
		ArrayList<ModelPortfolio> result = new ArrayList<ModelPortfolio>();
				
		for(ModelPortfolio portfolio : getModelPortfolioList())
		{
			if(portfolio.riskProfile.equalsIgnoreCase(customer.riskProfile))
				result.add(portfolio);
		}
		
		return result;
	}

}
