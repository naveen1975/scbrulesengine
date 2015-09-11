package com.scb.data;

import java.util.Comparator;

public class AssetClassLevel2 {

	public String id = null;
	
	public String name = null;
	
	public double gap = 0.0;
	
	public AssetClassLevel2(String id, double gap)
	{
		this.id = id;
		this.gap = gap;
	}
	
	public static class SortBySuggestedValue implements Comparator<AssetClassLevel2> {

        @Override
        public int compare(AssetClassLevel2 o1, AssetClassLevel2 o2) {
        	 return ((o1.gap)> (o2.gap)) ? 1 : ((o1.gap) < (o2.gap) ? -1 : 0);
            //return o1.cashOnHand.compareTo(o2.cashOnHand);
        }
    }	
}
