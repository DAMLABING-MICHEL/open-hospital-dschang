package org.isf.priceslist.model;

import java.util.GregorianCalendar;

/**
 * Price model: represent a price
 * @author alex
 *
 */
public class Price {
    
	private int id;
    private int list;
    private String group;
    private String item;
    private String desc;
    private Double price;
    private boolean editable;
    //fields for history	
  	private String create_by ; 
  	private String modify_by ; 
  	private GregorianCalendar create_date;
  	private GregorianCalendar modify_date;
  	//
    public Price() {
		super();		
	}
    
    /**
     * 
     * @param id
     * @param list
     * @param group
     * @param item
     * @param desc
     * @param price
     * @param editable
     */
    public Price(int list, String group, String item, String desc,
			Double price, boolean editable) {
		super();
		this.list = list;
		this.group = group;
		this.item = item;
		this.desc = desc;
		this.price = price;
		this.editable = editable;
	}
    
    /**
     * 
     * @param id
     * @param list
     * @param group
     * @param item
     * @param desc
     * @param price
     */
	public Price(int id, int list, String group, String item, String desc,
			Double price) {
		super();
		this.id = id;
		this.list = list;
		this.group = group;
		this.item = item;
		this.desc = desc;
		this.price = price;
		this.editable = true;
	}

	/**
	 * 
	 * @param list
	 * @param group
	 * @param item
	 * @param desc
	 * @param price
	 */
	public Price(int list, String group, String item, String desc, Double price) {		
		this.list = list;
		this.group = group;
		this.item = item;
		this.desc = desc;
		this.price = price;
		this.editable = true;
	}

	public int getId() {
		return id;
	}
	
    public void setId(int id) {
		this.id = id;
	}
	
    public int getList() {
		return list;
	}
	
    public void setList(int list) {
		this.list = list;
	}
	
    public String getGroup() {
		return group;
	}
	
    public void setGroup(String group) {
		this.group = group;
	}
	
    public String getItem() {
		return item;
	}
	
    public void setItem(String item) {
		this.item = item;
	}
	
    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Double getPrice() {
		return price;
	}
	
    public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isPrice() {
		return item.compareTo("") != 0;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public String toString() {
		return desc;
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
