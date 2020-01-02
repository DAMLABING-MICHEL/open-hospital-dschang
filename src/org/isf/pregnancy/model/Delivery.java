package org.isf.pregnancy.model;

import java.util.GregorianCalendar;

public class Delivery {

	/**
	 * @uml.property  name="date"
	 */
	private GregorianCalendar deliverydate;
	/**
	 * @uml.property  name="sex"
	 */
	private String sex;
	/**
	 * @uml.property  name="weight"
	 */
	private float  weight;
	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="delrestypeid"
	 */
	private String delrestypeid;
		
	private String deltypeid;
	
	private String hiv_status;
	
	//added julio
	private String father_name;
	private String father_occupation;
	private String father_residence;
	private String father_birth_place;
	private int    father_age;
	
	private String child_name;
	//
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
	public Delivery(){
		deliverydate = new GregorianCalendar();
		sex = "F";
		weight = 0.0f;
		id =0;
		delrestypeid = null;
		deltypeid = null;
	}
	/**
	 * @return  the date of the delivery
	 * @uml.property  name="date"
	 */
	public GregorianCalendar getDeliveryDate() {
		return deliverydate;
	}
	
	public String getDeltypeid() {
		return deltypeid;
	}
	public void setDeltypeid(String deltypeid) {
		this.deltypeid = deltypeid;
	}
	/**
	 * @param date  the date of the delivery
	 * @uml.property  name="date"
	 */
	public void setDeliveryDate(GregorianCalendar date) {
		this.deliverydate = date;
	}
	/**
	 * @return  the sex of the newborn child 'F' or 'M'
	 * @uml.property  name="sex"
	 */
	public String getSex() {
		return sex;
	}
	/**
	 * @param sex   the sex of the newborn child 'F' or 'M'
	 * @uml.property  name="sex"
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * @return  the id of the deliveryresulttype
	 * @uml.property  name="delrestypeid"
	 */
	public String getDelrestypeid() {
		return delrestypeid;
	}
	/**
	 * @param delrestypeid  the id of the deliveryresulttype
	 * @uml.property  name="delrestypeid"
	 */
	public void setDelrestypeid(String delrestypeid) {
		this.delrestypeid = delrestypeid;
	}
	/**
	 * @return   the weight of the newborn child
	 * @uml.property  name="weight"
	 */
	public float getWeight() {
		return weight;
	}
	/**
	 * @param weight  the weight of the newborn child
	 * @uml.property  name="weight"
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}
	/**
	 * @return  the id of the record in the database
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id  the id of the record in the database
	 * @uml.property  name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}
	public String getHiv_status() {
		return hiv_status;
	}
	public void setHiv_status(String hiv_status) {
		this.hiv_status = hiv_status;
	}
	public String getFather_name() {
		return father_name;
	}
	public void setFather_name(String father_name) {
		this.father_name = father_name;
	}
	public String getFather_occupation() {
		return father_occupation;
	}
	public void setFather_occupation(String father_occupation) {
		this.father_occupation = father_occupation;
	}
	public String getFather_residence() {
		return father_residence;
	}
	public void setFather_residence(String father_residence) {
		this.father_residence = father_residence;
	}
	public String getFather_birth_place() {
		return father_birth_place;
	}
	public void setFather_birth_place(String father_birth_place) {
		this.father_birth_place = father_birth_place;
	}
	public int getFather_age() {
		return father_age;
	}
	public void setFather_age(int father_age) {
		this.father_age = father_age;
	}
	public String getChild_name() {
		return child_name;
	}
	public void setChild_name(String child_name) {
		this.child_name = child_name;
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
