package com.skjlls.aspects.debounce.impl;

public interface Key extends Comparable<Key> {

	int compareTo(Key o2);

	public abstract void setArgs(Object[] args);

}
