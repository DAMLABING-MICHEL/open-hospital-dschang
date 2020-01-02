package org.isf.reduction.model;

public class OtherReduction {

	int rpID;
	int othID;
	double reductionRate;
	
	public OtherReduction(){
		
	}
	
	public OtherReduction(int rpID, int othID, double reductionRate) {
		super();
		this.rpID = rpID;
		this.othID = othID;
		this.reductionRate = reductionRate;
	}
	public int getReductionPlanID() {
		return rpID;
	}
	public void setReductionPlanID(int rpID) {
		this.rpID = rpID;
	}
	public int getOthID() {
		return othID;
	}
	public void setOthID(int othID) {
		this.othID = othID;
	}
	public double getReductionRate() {
		return reductionRate;
	}
	public void setReductionRate(double reductionRate) {
		this.reductionRate = reductionRate;
	}
	
}
