package com.scb.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AssetClassLevel2 {

	public String id = null;
	
	public String name = null;
	
	public double gap = 0.0;
	
	public String houseViewId = null;
	
	//Products under this category
	public ArrayList<IProduct> products = new ArrayList<IProduct>();	
	
	public AssetClassLevel2(String id, double gap, String houseViewId)
	{
		this.id = id;
		this.gap = gap;
		this.houseViewId = houseViewId;
	}
	
	public static class SortBySuggestedValue implements Comparator<AssetClassLevel2> {

        @Override
        public int compare(AssetClassLevel2 o1, AssetClassLevel2 o2) {
        	 return ((o1.gap)> (o2.gap)) ? 1 : ((o1.gap) < (o2.gap) ? -1 : 0);
            //return o1.cashOnHand.compareTo(o2.cashOnHand);
        }
    }	
}
