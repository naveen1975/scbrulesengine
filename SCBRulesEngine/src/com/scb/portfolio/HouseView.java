package com.scb.portfolio;

import java.util.ArrayList;
import java.util.List;

import com.scb.constants.IConstants;

public class HouseView {

	public String id = null;
	
	public String assetClassId = null;
	public String sentiment = IConstants.NEUTRAL;
	
	public List<String> passages = new ArrayList<String>();
}
