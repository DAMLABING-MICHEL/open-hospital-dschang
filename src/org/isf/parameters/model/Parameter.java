package org.isf.parameters.model;

import java.util.GregorianCalendar;

import org.isf.exa.model.Exam;
import org.isf.ward.model.Ward;


public class Parameter {
	private int id;
	private String code;
	private String description;
	private String value;
	private String default_value;
	private String deleted;
	//private String scope;
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private String deleted_by ;
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		private GregorianCalendar deleted_date;
		//
	
    public Parameter() { }
	

	public Parameter(int id, String code,  String description, String value, String default_value, String deleted){
		this.id=id;
		this.code=code;
		this.description=description;
		this.value=value;	
		this.default_value=default_value;
		this.deleted=deleted;
		//this.scope=scope;
		
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


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getDefault_value() {
		return default_value;
	}


	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}


	public String getDeleted() {
		return deleted;
	}


	public void setDeleted(String deleted) {
		this.deleted = deleted;
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


	public String getDeleted_by() {
		return deleted_by;
	}


	public void setDeleted_by(String deleted_by) {
		this.deleted_by = deleted_by;
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


	public GregorianCalendar getDeleted_date() {
		return deleted_date;
	}


	public void setDeleted_date(GregorianCalendar deleted_date) {
		this.deleted_date = deleted_date;
	}


//	public String getScope() {
//		return scope;
//	}
//
//
//	public void setScope(String scope) {
//		this.scope = scope;
//	}

	

}
