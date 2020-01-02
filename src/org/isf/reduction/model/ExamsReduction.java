package org.isf.reduction.model;

public class ExamsReduction {

	int rpID;
	String exaCode;
	double reductionRate;
	
	
	public ExamsReduction() {
		super();
	}
	
	public ExamsReduction(int rpID, String exaID, double reductionRate) {
		super();
		this.rpID = rpID;
		this.exaCode = exaID;
		this.reductionRate = reductionRate;
	}
	public int getReductionPlanID() {
		return rpID;
	}
	public void setReductionPlanID(int rpID) {
		this.rpID = rpID;
	}
	public String getExaCode() {
		return exaCode;
	}
	public void setExaCode(String exaCode) {
		this.exaCode = exaCode;
	}
	public double getReductionRate() {
		return reductionRate;
	}
	public void setReductionRate(double reductionRate) {
		this.reductionRate = reductionRate;
	}
	
	
}
