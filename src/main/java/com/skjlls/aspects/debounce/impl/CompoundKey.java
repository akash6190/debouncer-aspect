package com.skjlls.aspects.debounce.impl;

public class CompoundKey implements Key {
	
	private Object[] args;
	
	public CompoundKey() {
	}
	
	public CompoundKey(Object[] args) {
		this.setArgs(args);
	}
	
	@Override
	public int compareTo(Key o2) {
		return toString().compareTo(o2.toString());
	}

	@Override
	public String toString() {
		StringBuffer sba = new StringBuffer();
		for(Object o : args) {
			sba.append('|');
			sba.append(o);
		}
		return sba.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
	
	public Object[] getArgs() {
		return args;
	}

	@Override
	public void setArgs(Object[] args) {
		this.args = args;
	}
	

}
