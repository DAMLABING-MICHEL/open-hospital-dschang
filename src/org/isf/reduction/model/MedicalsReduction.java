package org.isf.reduction.model;

public class MedicalsReduction {

	int rpID;
	int medID;
	double reductionRate;
	
	public MedicalsReduction(){
		
	}
	
	public MedicalsReduction(int rpID, int medID, double reductionRate) {
		super();
		this.rpID = rpID;
		this.medID = medID;
		this.reductionRate = reductionRate;
	}
	public int getReductionPlanID() {
		return rpID;
	}
	public void setReductionPlanID(int rpID) {
		this.rpID = rpID;
	}
	public int getMedID() {
		return medID;
	}
	public void setMedID(int medID) {
		this.medID = medID;
	}
	public double getReductionRate() {
		return reductionRate;
	}
	public void setReductionRate(double reductionRate) {
		this.reductionRate = reductionRate;
	}
	
	
	
}
