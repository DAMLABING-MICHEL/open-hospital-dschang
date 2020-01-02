package org.isf.menu.model;

import java.util.GregorianCalendar;

public class User {

	private String userName;
	private String userGroupName;
	private String passwd;
	private String desc;
	private String wardCode;
	//added julio
	private String name;
	private String surname;
	private String phone;
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	
	public User(){
		
	}
	public User(String aName, String aGroup, String aPasswd, String aDesc){
		this.userName = aName;
		this.userGroupName = aGroup;
		this.passwd = aPasswd;
		this.desc = aDesc;
	}
	
	public User(String aName, String aGroup, String aPasswd, String aDesc, String aWardCode){
		this(aName, aGroup, aPasswd, aDesc);
		this.wardCode=aWardCode;
	}
	
	public User(String aName, String aGroup, String aPasswd, String aDesc, String aWardCode, String name, String surname, String phone){
		this(aName, aGroup, aPasswd, aDesc, aWardCode);
		this.name=name;
		this.surname=surname;
		this.phone=phone;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getUserGroupName() {
		return userGroupName;
	}
	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getWardCode() {
		return wardCode;
	}
	
	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}
	public String toString(){
		return getUserName();		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	
}//class User
