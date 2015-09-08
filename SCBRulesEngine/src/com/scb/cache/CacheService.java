package com.scb.cache;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.scb.constants.IConstants;
import com.scb.dao.ProductDAO;
import com.scb.data.FundProduct;

public class CacheService {

	
	//public static final String OUTLOOKQUES = "OUTLOOKQUES";
	public static Hashtable<String, Object> cache = new Hashtable<String, Object>();
	
	public static Hashtable<String, Long> expiryList = new Hashtable<String, Long>();
	
	public static int prodLastUpdateDay = -1;
	
	public static ProductDAO productDao = new com.scb.dao.ProductDAO();
	
	public static Object get(String key)
	{
		Object obj = cache.get(key);
		
		if(obj!=null && !isExpired(key))
			return obj;
		
		return null;
	}
	
	public static void put(String key, Object obj)
	{
		cache.put(key, obj);
	}	
	
	public static void put(String key, Object obj, long expiry)
	{
		cache.put(key, obj);
		
		if(expiry != -1)
		{
			expiryList.put(key, Long.valueOf(System.currentTimeMillis() + expiry * 1000));
		}
	}
	
	public static boolean isRefreshTime()
	{
		Calendar cal = Calendar.getInstance();
		String hr = Configuration.getConfig("PROD_REFRESH_HOUR");
		String mn = Configuration.getConfig("PROD_REFRESH_MINUTE");
		
		boolean isDateChanged = cal.get(Calendar.DAY_OF_MONTH) != prodLastUpdateDay;
		boolean isHourPassed = false;

		if(hr!=null && mn!=null)
		{
			int hour = Integer.parseInt(hr);
			int minute = Integer.parseInt(mn);
			isHourPassed = cal.get(Calendar.HOUR_OF_DAY) > hour && cal.get(Calendar.MINUTE) > minute;
		}
		return (isDateChanged && isHourPassed);
		
	}

	public static List<FundProduct> getFundMasterList()
	{
		boolean refresh = isRefreshTime();
		
		List<FundProduct> prodList = (List<FundProduct>) get(IConstants.FUND);
		if(refresh || prodList==null)
		{
			Calendar cal = Calendar.getInstance();
			prodLastUpdateDay = cal.get(Calendar.DAY_OF_MONTH);
			put(IConstants.FUND, productDao.getFundProductList());
		}
		prodList = (List<FundProduct>) get(IConstants.FUND);

		return prodList;
	}
	
	static boolean isExpired(Object key)
	{
		Long exp = expiryList.get(key);
		
		if(exp != null)
		{
			return System.currentTimeMillis() < exp.longValue();
		}
		
		return false;
	}
	
}
