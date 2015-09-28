package com.scb.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.scb.constants.IConstants;
import com.scb.data.Customer;
import com.scb.data.ThematicFundProduct;

public class RecoResult {
	
	public String recoId;
	
	public Customer customer = null;
	
	public RecoAssetCategory sellCategory = new RecoAssetCategory(IConstants.OC_NEG);
	public RecoAssetCategory buyCategory = new RecoAssetCategory(IConstants.UC_POS);
	public RecoAssetCategory rmNeutralCategory = new RecoAssetCategory(IConstants.UC_NUT);
	public RecoAssetCategory rmNegCategory = new RecoAssetCategory(IConstants.UC_NEG);
	public RecoAssetCategory holdPositiveCategory = new RecoAssetCategory(IConstants.OC_POS);
	public RecoAssetCategory holdNeutralCategory = new RecoAssetCategory(IConstants.OC_NUT);
	
	public List<ThematicFundProduct> thematicFundsCategory = new ArrayList<ThematicFundProduct>();
	
	
	public int getRecoCount()
	{
		return sellCategory.assetClassList.size() + buyCategory.assetClassList.size() + rmNeutralCategory.assetClassList.size()
				+ rmNegCategory.assetClassList.size() + holdPositiveCategory.assetClassList.size() 
				+ holdNeutralCategory.assetClassList.size();
	}
		
}
