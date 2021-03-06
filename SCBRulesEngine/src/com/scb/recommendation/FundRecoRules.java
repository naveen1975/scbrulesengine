package com.scb.recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.scb.constants.IConstants;
import com.scb.dao.CustomerDAO;
import com.scb.data.AssetClassLevel2;
import com.scb.data.Customer;
import com.scb.data.FundProduct;
import com.scb.data.HouseView;
import com.scb.data.IProduct;
import com.scb.data.ModelPortfolio;
import com.scb.data.RiskProfileToProductRiskMap;

public class FundRecoRules implements IRule {
	
	protected Log LOG = LogFactory.getLog(FundRecoRules.class);
	
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
	
	public String getHouseViewId(String assetClassId)
	{
		for(HouseView houseView : getHouseViewList())
		{
			if(houseView.assetClassId.equalsIgnoreCase(assetClassId))
				return houseView.id;
		}
		
		return null;
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
		LOG.info("Running fund rules for customer : " + customer.customerId);
		RecoResult result = new RecoResult();
		
		result.customer = customer;
		
		//For each asset class check the portfolio gap and houseview and assign the category
		//List<ModelPortfolio> modelPortfolioList = getModelPortfolioForCustomer(customer);
		
		if(modelPortfolioList == null)
		{
			LOG.error("No model portfolio for the customer <" + customer + "> with riskprofile : " + customer.riskProfile);
			return result;
		}
		
		//for(ModelPortfolio portfolio : modelPortfolioList)
		for(String custAssetClass : customer.portfolioGap.keySet())
		{
			//Get HouseView
			//String houseView = getHouseView(custAssetClass);
			String houseView = getHouseView(custAssetClass);
			
			//Get Portfolio Gap
			//Double gap = customer.getPortfolioGap(custAssetClass);
			Double gap = customer.getPortfolioGap(custAssetClass);
			if(gap==null || gap.doubleValue()==0.0)
			{
				LOG.error("No GAP in portfolio for assetclass <" + custAssetClass + "> for the customer <" + customer.customerId + "> with riskprofile : " + customer.riskProfile);				
				gap = new Double(0).doubleValue();
				continue;
			}

			LOG.info("GAP for cust asset class : " + custAssetClass + " for customer " + customer.customerId + " houseView : " + houseView);
			
			//Based on category add to the result list
			if(houseView.equalsIgnoreCase(IConstants.NEGATIVE) && gap.doubleValue() < 0.0) //Sell Category
			{
				AssetClassLevel2 assetClass = new AssetClassLevel2(custAssetClass, gap.doubleValue(), houseView);
				result.sellCategory.addAssetClass(assetClass);
				//Populate the holding fund list to sell recommendation
				recommendFundsToSell(customer, assetClass, custAssetClass);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.POSITIVE) && gap.doubleValue() > 0.0) //Buy Category
			{
				AssetClassLevel2 assetClass = new AssetClassLevel2(custAssetClass, gap.doubleValue(), houseView);
				result.buyCategory.addAssetClass(assetClass);
				recommendFunds(customer, assetClass, custAssetClass);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.NEUTRAL) && gap.doubleValue() < 0.0) //Hold Neutral Category
			{
				AssetClassLevel2 assetClass = new AssetClassLevel2(custAssetClass, gap.doubleValue(), houseView);
				
				result.holdNeutralCategory.addAssetClass(assetClass);
				recommendFunds(customer, assetClass, custAssetClass);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.NEGATIVE) && gap.doubleValue() > 0.0) //RM Negative Category
			{
				AssetClassLevel2 assetClass = new AssetClassLevel2(custAssetClass, gap.doubleValue(), houseView);

				result.rmNegCategory.addAssetClass(assetClass);
				recommendFunds(customer, assetClass, custAssetClass);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.POSITIVE) && gap.doubleValue() < 0.0) //Hold Positive Category
			{
				AssetClassLevel2 assetClass = new AssetClassLevel2(custAssetClass, gap.doubleValue(), houseView);

				result.holdPositiveCategory.addAssetClass(assetClass);
				recommendFundsToSell(customer, assetClass, custAssetClass);
			}
			
			if(houseView.equalsIgnoreCase(IConstants.NEUTRAL) && gap.doubleValue() > 0.0) //Hold Neutral Category
			{
				AssetClassLevel2 assetClass = new AssetClassLevel2(custAssetClass, gap.doubleValue(), houseView);

				result.rmNeutralCategory.addAssetClass(assetClass);
				recommendFundsToSell(customer, assetClass, custAssetClass);
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
	
	public void recommendFundsToSell(Customer customer, AssetClassLevel2 assetClass,  String assetClassId)
	{
		
		for(IProduct product : customer.customerHoldings)
		{
			
			if(product.assetClassId.equalsIgnoreCase(assetClassId))
			{
				/*
				if(assetClass.products.get(assetClassId) == null)
					assetClass.products.put(assetClassId, new ArrayList<IProduct>());
				
				List<IProduct> products = assetClass.products.get(assetClassId);
				*/
				assetClass.products.add(product);
			}
		}
	}
	
	public void recommendFunds(Customer customer, AssetClassLevel2 assetClass, String assetClassId)
	{
		
		for(IProduct product : getFundProductList())
		{
			
			if(product.assetClassId.equalsIgnoreCase(assetClassId))
			{
				if(matchCustomerComplance(customer.intRiskProfile, product.riskRating))
				{
					/*
					if(assetClass.products.get(assetClassId) == null)
						assetClass.products.put(assetClassId, new ArrayList<IProduct>());
									
					List<IProduct> products = assetClass.products.get(assetClassId);
					*/
					assetClass.products.add(product);
				}
			}
		}
	}
	
	boolean matchCustomerComplance(int riskProfile, String productRating)
	{
		if(riskProfile!=0 && productRating!=null)
		{
			//String riskRating = getRiskProfileToProductRiskMap().getRiskRating(riskProfile);
		
			//return productRating.equalsIgnoreCase(riskRating);
			
			try
			{
				return riskProfile >= Integer.parseInt(productRating);
			}
			catch(Exception ignore)
			{
				
			}
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
