package org.isf.medicalinventory.model;

import java.util.GregorianCalendar;

import org.isf.exa.model.Exam;
import org.isf.ward.model.Ward;


public class MedicalInventory {
	private int id;
	private String state;
	private GregorianCalendar inventoryDate;
	private String user;
	private String inventoryReference;
	private String inventoryType;
	private String ward;
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
    public MedicalInventory() { }
	
//	public MedicalInventory(int id, String state,  GregorianCalendar inventoryDate, String user, String reference, String type){
//		this.id=id;
//		this.state=state;
//		this.inventoryDate=inventoryDate;
//		this.user=user;	
//		this.inventoryReference=reference;
//		this.inventoryType=type;
//	}
	
	public MedicalInventory(int id, String state,  GregorianCalendar inventoryDate, String user, String reference, String type, String ward){
		this.id=id;
		this.state=state;
		this.inventoryDate=inventoryDate;
		this.user=user;	
		this.inventoryReference=reference;
		this.inventoryType=type;
		this.ward=ward;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public GregorianCalendar getInventoryDate() {
		return inventoryDate;
	}

	public void setInventoryDate(GregorianCalendar inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getInventoryReference() {
		return inventoryReference;
	}

	public void setInventoryReference(String inventoryReference) {
		this.inventoryReference = inventoryReference;
	}

	public String getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
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

}
