package com.cinefms.metrics.services;

import com.skjlls.aspects.debounce.Debounce;


public class TestService01 {

	int count = 0;
	
	@Debounce
	public String getString() {
		return "AAA";
	}
	
	@Debounce(delay=500)
	public String getString(String in) {
		count++;
		if(in==null) {
			return null;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println(Thread.currentThread().getName()+" completed ... ");
		return in.toLowerCase()+" / "+in.toUpperCase()+" / "+count;
	}
	
}
