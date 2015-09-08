package com.scb.data;

import java.util.*;

import com.scb.constants.IConstants;

public interface Customer {

	public String customerId = null;
	public String customerName = null;
	
	public HashMap<String, Double> portfolioGap = new HashMap<String, Double>();	
	public String riskRating = IConstants.RISK_RATING_1;
	public String segment = IConstants.SEGMENT_PRIORITY;
	public double cashBalance = 0.0;
	
	public int age = 0;
	public double loanValue = 0.0;
	
	
	//Customer Holdings
	public List<IProduct> customerHoldings = new ArrayList<IProduct>();

}
