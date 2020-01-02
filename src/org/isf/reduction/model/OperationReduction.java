package org.isf.reduction.model;

public class OperationReduction {

	int rpID;
	String opeCode;
	double reductionRate;
	
	public OperationReduction(){
		
	}
	public OperationReduction(int rpID, String opeCode, double reductionRate) {
		super();
		this.rpID = rpID;
		this.opeCode = opeCode;
		this.reductionRate = reductionRate;
	}
	public int getReductionPlanID() {
		return rpID;
	}
	public void setReductionPlanID(int rpID) {
		this.rpID = rpID;
	}
	public String getOpeCode() {
		return opeCode;
	}
	public void setOpeCode(String opeCode) {
		this.opeCode = opeCode;
	}
	public double getReductionRate() {
		return reductionRate;
	}
	public void setReductionRate(double reductionRate) {
		this.reductionRate = reductionRate;
	}
	
}
