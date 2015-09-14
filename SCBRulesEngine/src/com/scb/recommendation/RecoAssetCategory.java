package com.scb.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.scb.data.AssetClassLevel2;
import com.scb.data.IProduct;

public class RecoAssetCategory {

	public List<AssetClassLevel2> assetClassList = new ArrayList<AssetClassLevel2>();
	
	public String category = null; //Category by the houseview and portfolio gap
	
	//Product by asset class - sorty by focus fund and sharpe value
	
	//public List<IProduct> products; //Products under this category
	HashMap<String, List<IProduct>> products = new HashMap<String, List<IProduct>>();
	
	
	public RecoAssetCategory(String category)
	{
		this.category = category;
	}
	
	
	public void addAssetClass(String assetClass, double gap)
	{
		assetClassList.add(new AssetClassLevel2(assetClass, gap));
	}
	
	public void addAssetClass(AssetClassLevel2 assetClass)
	{
		assetClassList.add(assetClass);
	}
	


	
}
