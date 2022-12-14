package org.isf.opd.model;

/*------------------------------------------
 * Opd - model for OPD
 * -----------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  pupo
 * 21/11/2006 - ross - renamed from Surgery 
 *                   - added visit date, disease 2, diseas3
 *                   - disease is not mandatory if re-attendance
 * 			         - version is now 1.0 
 * 12/06/2008 - ross - added referral from / to
 * 16/06/2008 - ross - added patient detail
 * 05/09/2008 - alex - added fullname e notefield
 * 09/01/2009 - fabrizio - date field modified to type Date
 *------------------------------------------*/

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;

public class Opd {
	/**
	 * @author Vero, Rick, Pupo
	 */

	private int code;
	private Date date;
	private GregorianCalendar visitDate;

	private int patientCode;
	private String fullName; // ADDED : alex
	private String firstName;
	private String secondName;
	private int age;
	// private char sex;
	private String sex;
	private String address;
	private String city;
	private String nextKin;
	private String note; // ADDED: Alex

	private int year;
	private int progMonth;
	private String disease;
	private String disease2;
	private String disease3;
	private int lock;
	private String diseaseType;
	private String diseaseDesc;
	private String diseaseTypeDesc;
	private String newPatient; // n=NEW R=REATTENDANCE

	private String referralFrom; // R=referral from another unit; null=no
									// referral from
	private String referralTo; // R=referral to another unit; null=no referral
								// to
	
	private String referralFromHospital; 
	
    private String referralToHospital;

    private boolean isPregnant;
    
	private String userID;
	
	private GregorianCalendar nextVisitDate;
	private String patientComplaint;
	
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//

	public String getNewPatient() {
		return newPatient;
	}

	public void setNewPatient(String newPatient) {
		this.newPatient = newPatient;
	}
	
	public Opd(){
		super();
	}

	/**
	 * @param aYear
	 * @param aSex
	 * @param aDate
	 * @param aAge
	 * @param aDisease
	 * @param aLock
	 */

	public Opd(int aYear, char aSex, int aAge, String aDisease, int aLock) {
		year = aYear;
		sex = new String("" + aSex);
		age = aAge;
		disease = aDisease;
		lock = aLock;
	}

	// ADDED: Alex
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	// ///////////////////
	public int getpatientCode() {
		return patientCode;
	}

	public void setpatientCode(int patientCode) {
		this.patientCode = patientCode;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getfirstName() {
		return firstName;
	}

	public void setfirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getsecondName() {
		return secondName;
	}

	public void setsecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getnextKin() {
		return nextKin;
	}

	public void setnextKin(String nextKin) {
		this.nextKin = nextKin;
	}

	public String getcity() {
		return city;
	}

	public void setcity(String city) {
		this.city = city;
	}

	public String getaddress() {
		return address;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public String getReferralTo() {
		return referralTo;
	}

	public void setReferralTo(String referralTo) {
		this.referralTo = referralTo;
	}

	public String getReferralFrom() {
		return referralFrom;
	}

	public void setReferralFrom(String referralFrom) {
		this.referralFrom = referralFrom;
	}
	
	/*****************************/
	public String getReferralToHospital() {
		return referralToHospital;
	}

	public void setReferralToHospital(String referralToHospital) {
		this.referralToHospital = referralToHospital;
	}
	
	public String getReferralFromHospital() {
		return referralFromHospital;
	}

	public void setReferralFromHospital(String referralFromHospital) {
		this.referralFromHospital = referralFromHospital;
	}
	
	public boolean isPregnant() {
		return isPregnant;
	}

	public void setIsPregnant(boolean isPregnant) {
		this.isPregnant = isPregnant;
	}
	/*****************************/
	
	

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDisease() {
		return disease;
	}

	public String getDisease2() {
		return disease2;
	}

	public String getDisease3() {
		return disease3;
	}

	public void setDisease(String disease) {
		this.disease = disease;
		if (disease != null) {
			if (disease.equals("")) {
				this.disease = null;
			}
		}
	}

	public void setDisease2(String disease) {
		this.disease2 = disease;
		if (disease != null) {
			if (disease.equals("")) {
				this.disease2 = null;
			}
		}
	}

	public void setDisease3(String disease) {
		this.disease3 = disease;
		if (disease != null) {
			if (disease.equals("")) {
				this.disease3 = null;
			}
		}
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(GregorianCalendar visDate) {
		this.visitDate = visDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = "" + sex;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDiseaseDesc() {
		return diseaseDesc;
	}

	public void setDiseaseDesc(String diseaseDesc) {
		this.diseaseDesc = diseaseDesc;
	}

	public String getDiseaseTypeDesc() {
		return diseaseTypeDesc;
	}

	public void setDiseaseTypeDesc(String diseaseTypeDesc) {
		this.diseaseTypeDesc = diseaseTypeDesc;
	}

	public String getDiseaseType() {
		return diseaseType;
	}

	public void setDiseaseType(String diseaseType) {
		this.diseaseType = diseaseType;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public GregorianCalendar getNextVisitDate() {
		return nextVisitDate;
	}

	public void setNextVisitDate(GregorianCalendar nextVisitDate) {
		this.nextVisitDate = nextVisitDate;
	}

	public String getPatientComplaint() {
		return patientComplaint;
	}

	public void setPatientComplaint(String patientComplaint) {
		this.patientComplaint = patientComplaint;
	}

	public int getProgMonth() {
		return progMonth;
	}

	public void setProgMonth(int progMonth) {
		this.progMonth = progMonth;
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
