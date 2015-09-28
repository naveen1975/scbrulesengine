package com.scb.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.scb.data.AssetClassLevel2;
import com.scb.data.IProduct;

public class RecoAssetCategory {

	public List<AssetClassLevel2> assetClassList = new ArrayList<AssetClassLevel2>();
	
	public String category = null; //Category by the houseview and portfolio gap
	
	//Product by asset class - sort by focus fund and sharpe value
	
	
	public RecoAssetCategory(String category)
	{
		this.category = category;
	}
	
	
	public void addAssetClass(String assetClass, double gap, String houseViewId)
	{
		assetClassList.add(new AssetClassLevel2(assetClass, gap, houseViewId));
	}
	
	public void addAssetClass(AssetClassLevel2 assetClass)
	{
		assetClassList.add(assetClass);
	}
	


	
}
