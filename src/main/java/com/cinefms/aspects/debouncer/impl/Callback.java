package com.cinefms.aspects.debouncer.impl;

public interface Callback<T> {
	public void call(T arg);
}