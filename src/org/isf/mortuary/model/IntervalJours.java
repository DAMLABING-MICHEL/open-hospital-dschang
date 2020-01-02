package org.isf.mortuary.model;

public class IntervalJours {

	private int minjours;
	private int maxjours;
	
	@Override
	public String toString() {
		return "IntervalJours [minjours=" + minjours + ", maxjours=" + maxjours + "]";
	}
	public IntervalJours(int min, int max) {
		this.minjours = min;
		this.maxjours = max;
	}
	public int getMinjours() {
		return minjours;
	}

	public int getMaxjours() {
		return maxjours;
	}
	
}
