package org.isf.medicalinventory.model;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;

public class MedicalInventoryRow {
	private int id;
	private double theoreticqty;
	private double realqty;
	private MedicalInventory inventory;
	private Medical medical;
	private Lot lot;
	private double cost;
	
	public MedicalInventoryRow() { }
	
	public MedicalInventoryRow(int id, double theoreticqty,  double realqty, MedicalInventory inventory, Medical medical, Lot lot, double cost ){
		this.id=id;
		this.theoreticqty=theoreticqty;
		this.realqty=realqty;
		this.inventory=inventory;	
		this.medical=medical;
		this.lot=lot;
		this.cost=cost;	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTheoreticqty() {
		return theoreticqty;
	}

	public void setTheoreticqty(double theoreticqty) {
		this.theoreticqty = theoreticqty;
	}

	public double getRealqty() {
		return realqty;
	}

	public void setRealqty(double realqty) {
		this.realqty = realqty;
	}

	public MedicalInventory getInventory() {
		return inventory;
	}

	public void setInventory(MedicalInventory inventory) {
		this.inventory = inventory;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	public String getSearchString() {
		StringBuffer sbNameCode = new StringBuffer();
		sbNameCode.append(getMedical()!=null?getMedical().getDescription().toLowerCase():"");
		sbNameCode.append(getMedical()!=null?getMedical().getProd_code().toLowerCase():"");
		return sbNameCode.toString();
	}
}
