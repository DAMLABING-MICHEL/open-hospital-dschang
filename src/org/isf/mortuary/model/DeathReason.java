package org.isf.mortuary.model;

import java.util.GregorianCalendar;

public class DeathReason {
	@Override
	public String toString() {
		return description;
	}
	private int id;
	private String code;
	private String description;
	
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	//
	
	public DeathReason(int id, String code, String description) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
