package org.isf.priceslist.model;

import java.util.GregorianCalendar;

/**
 * List model: represent a List
 * @author alex
 *
 */
public class List {
    
	private int id;
    private String code;
    private String name;
    private String description;
    private String currency;
  //fields for history	
  	private String create_by ; 
  	private String modify_by ; 
  	private GregorianCalendar create_date;
  	private GregorianCalendar modify_date;
  	//
	
    public List() {
		super();		
	}
    
    public List(int id, String code, String name, String description, String currency) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.currency = currency;
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
	
    public String getName() {
		return name;
	}
	
    public void setName(String name) {
		this.name = name;
	}
	
    public String getDescription() {
		return description;
	}
	
    public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return name;
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
