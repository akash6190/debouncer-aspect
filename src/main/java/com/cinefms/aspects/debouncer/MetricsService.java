package com.cinefms.aspects.debouncer;

public interface MetricsService {

	public void count(int count, String... names);

	public void timing(long time, String... names);

	public void event(String... names);

	public void event(int count, String[] names);
	
}
