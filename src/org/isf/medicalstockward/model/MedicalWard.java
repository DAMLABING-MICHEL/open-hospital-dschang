package org.isf.medicalstockward.model;

import java.util.GregorianCalendar;

import org.isf.medicals.model.Medical;

public class MedicalWard implements Comparable<Object> {
	
	private Medical medical;
	private Double qty;
	
	private double lastprice;
	private double initialstock;
	
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
	public MedicalWard(Medical medical, Double qty) {
		super();
		this.medical = medical;
		this.qty = qty;
	}
	public MedicalWard(Medical medical, Double qty, Double lastprice) {
		super();
		this.medical = medical;
		this.qty = qty;
		this.lastprice = lastprice;
	}
	
	public MedicalWard(Medical medical, Double qty, Double lastprice, double initialstock) {
		super();
		this.medical = medical;
		this.qty = qty;
		this.lastprice = lastprice;
		this.initialstock = initialstock;
	}
	public double getInitialstock() {
		return initialstock;
	}
	public void setInitialstock(double initialstock) {
		this.initialstock = initialstock;
	}
	public Medical getMedical() {
		return medical;
	}
	
	public void setMedical(Medical medical) {
		this.medical = medical;
	}
	
	public Double getQty() {
		return qty;
	}
	
	public void setQty(Double qty) {
		this.qty = qty;
	}
	
	public int compareTo(Object anObject) {
		if (anObject instanceof MedicalWard)
			return (medical.getDescription().toUpperCase().compareTo(
					((MedicalWard)anObject).getMedical().getDescription().toUpperCase()));
		else return 0;
	}

	public String getCreate_by() {
		return create_by;
	}

	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}

	public String getModify_by() {
		return modify_by;
	}

	public void setModify_by(String modify_by) {
		this.modify_by = modify_by;
	}

	public GregorianCalendar getCreate_date() {
		return create_date;
	}

	public void setCreate_date(GregorianCalendar create_date) {
		this.create_date = create_date;
	}

	public GregorianCalendar getModify_date() {
		return modify_date;
	}

	public void setModify_date(GregorianCalendar modify_date) {
		this.modify_date = modify_date;
	}
	
	public double getLastprice() {
		return lastprice;
	}

	public void setLastprice(double lastprice) {
		this.lastprice = lastprice;
	}
	
	public String getSearchString() {
		StringBuffer sbNameCode = new StringBuffer();
		sbNameCode.append(getMedical().getCode());
		sbNameCode.append(getMedical().getProd_code());
		sbNameCode.append(getMedical().getDescription().toLowerCase());
		return sbNameCode.toString();
	}
}
