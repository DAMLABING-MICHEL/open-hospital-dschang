package org.isf.lab.model;

/*------------------------------------------
 * Laboratory - laboratory exam execution model
 * -----------------------------------------
 * modification history
 * 02/03/2006 - theo - first beta version
 * 10/11/2006 - ross - new fields data esame, sex, age, material, inout flag added
 *------------------------------------------*/


import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.exa.model.Exam;
import org.isf.utils.jobjects.DateAdapter;

public class Laboratory {

	private int code;
	private String material;
	private Exam exam;
	private GregorianCalendar registrationDate;
	private GregorianCalendar examDate;
	private String result;
	private int lock;
	private String note;
	private int patId;
	private String patName;
	private String InOutPatient;
	private int age;
	private String sex;
	private String resultValue;
	private int billId=-1;
	
	private String paidStatus="";
	
	
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
	private int mProg;						// a progr. in month for each ward
	private String prescriber;

	public Laboratory() { }
	
	public Laboratory(int aCode,Exam aExam,GregorianCalendar aDate,String aResult,
			int aLock, String aNote, int aPatId, String aPatName){
		code=aCode;
		exam=aExam;
		registrationDate=aDate;
		result=aResult;
		lock=aLock;
		note=aNote;
		patId=aPatId;
		patName=aPatName;
	}
	public Exam getExam(){
		return exam;
	}
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getDate(){
		return registrationDate;
	}
	public String getResult(){
		return result;
	}
	public int getCode(){
		return code;
	}
	public int getLock(){
		return lock;
	}
	public void setCode(int aCode){
		code=aCode;
	}
	public void setExam(Exam aExam){
		exam=aExam;
	}
	public void setLock(int aLock){
		lock=aLock;
	}
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getExamDate() {
		return examDate;
	}
	public void setExamDate(GregorianCalendar exDate) {
		this.examDate = exDate;
	}	
	public void setDate(GregorianCalendar aDate){
		registrationDate=aDate;
	}
	public void setResult(String aResult){
		result=aResult;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public int getPatId() {
		return patId;
	}
	public void setPatId(int patId) {
		this.patId = patId;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getInOutPatient() {
		return InOutPatient;
	}
	public void setInOutPatient(String InOut) {
		if (InOut==null) InOut="";
		this.InOutPatient = InOut;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}
	
	public int getMProg() {
		return mProg;
	}

	public void setMProg(int prog) {
		this.mProg = prog;
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

	public String getPrescriber() {
		return prescriber;
	}

	public void setPrescriber(String prescriber) {
		this.prescriber = prescriber;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}
	
}

