package com.scb.recommendation;

import com.scb.data.Customer;

public interface IRule {
	
	public RecoResult execute(Customer customer) throws Exception;

}
