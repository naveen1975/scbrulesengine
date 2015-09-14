package com.scb.data;

import java.util.HashMap;


public class RiskProfileToProductRiskMap {
	
	
	HashMap<String, String> map = new HashMap<String,String>();
	

	public void addMapping(String riskProfile, String riskRating)
	{
		map.put(riskProfile, riskRating);
	}
	
	public String getRiskRating(String riskProfile)
	{
		return map.get(riskProfile);
	}
}
