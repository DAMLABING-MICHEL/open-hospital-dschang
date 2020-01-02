package org.isf.pregnancy.model;

import java.util.GregorianCalendar;

import org.isf.patient.model.Patient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.visits.model.Visit;

/**
 * @author Martin Reinstadler
 * this class represents the database table PREGNANCYVISIT
 * Besides the visit date, the date of the next scheduled visit, 
 * the visitnote and the type of visit it has an attribute to 
 * reference to a{@link PregnancyVisit}
 *
 */
public class PregnancyVisit extends Visit{
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property  name="visittype"
	 */
	private int visitType= -1;
	/**
	 * @uml.property  name="firstnextVisitdate"
	 */
	private GregorianCalendar nextVisitDate= null;
	/**
	 * @uml.property  name="pregnancyId"
	 */
	private int pregnancyId=0;
	/**
	 * @uml.property  name="patientId"
	 */
	private int patientId=0;
	/**
	 * @uml.property  name="pregnancyNr"
	 */
	private int pregnancyNr=1;
	/**
	 * @uml.property  name="visitId"
	 */
	private int visitId=0;
	/**
	 * @uml.property  name="treatmenttype"
	 */
	private String treatmentType= null;	
	
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
	/**
	 * @return  the code of the  {@link PregnantTreatmentType}  
	 * @uml.property  name="treatmenttype"
	 */
	public String getTreatmenttype() {
		return treatmentType;
	}
	
	/**
	 * @param treatmenttype  the code of the  {@link PregnantTreatmentType}  
	 * @uml.property  name="treatmenttype"
	 */
	public void setTreatmenttype(String treatmenttype) {
		this.treatmentType = treatmenttype;
	}
	
	/**
	 * @return  the id of the  {@link PregnancyVisit}  
	 * @uml.property  name="visitId"
	 */
	public int getVisitId() {
		return visitId;
	}
	/**
	 * @param vid  the id of the  {@link PregnancyVisit}  
	 * @uml.property  name="visitId"
	 */
	public void setVisitId(int visit_id) {
		this.visitId = visit_id;
	}
	/**
	 * @return  the id of the  {@link Pregnancy}  
	 * @uml.property  name="pregnancyId"
	 */
	public int getPregnancyId() {
		return pregnancyId;
	}
	/**
	 * 
	 * @param pregid the id of the {@link Pregnancy}
	 */
	public void setPregnancId(int pregid) {
		this.pregnancyId = pregid;
	}
	/**
	 * 
	 * @return the id of the {@link Patient}
	 */
	public int getPatientnr() {
		return patientId;
	}
	/**
	 * @return  the number of the patients pregnancy
	 * @uml.property  name="pregnancyNr"
	 */
	public int getPregnancyNr() {
		return pregnancyNr;
	}
	/**
	 * @param pregnancynr  the number of the patients pregnancy
	 * @uml.property  name="pregnancyNr"
	 */
	public void setPregnancyNr(int pregnancynr) {
		this.pregnancyNr = pregnancynr;
	}
	
	/**
	 * 
	 * @return the type of the visit (-1= prenatal, 1= postnatal)
	 */
	public int getType() {
		return visitType;
	}
	/**
	 * 
	 * @param type the type of the visit (-1= prenatal, 1= postnatal)
	 */
	public void setType(int type) {
		this.visitType = type;
	}
	/**
	 * 
	 * @return the date of the next scheduled visit
	 */
	public GregorianCalendar getNextVisitdate(){
		return this.nextVisitDate;
	}
	/**
	 * 
	 * @param date the date of the next scheduled visit
	 */
	public void setNextVisitdate1(GregorianCalendar date){
		this.nextVisitDate = date;
	}
	
	
	
	/**
	 * A pregnancy visit must be related to a patient. This initializes a new PregnancyVisit
	 * instance
	 * @param pat_id the identifier of the {@link Patient}
	 * @pregId the id of the {@link Pregnancy}
	 * @type the type of the visit
	 */
	public PregnancyVisit(int pat_id, int pregId, int type){
		//this.visit = new Visit();
		super();
		setNote("");
		visitType = type;
		this.pregnancyId = pregId;
		this.patientId = pat_id;
	}
	/**
	 * A PregnancyVisit must be related to a {@link Patient}. This initializes a new PregnancyVisit
	 * instance
	 * @param pat_id the identifier of the {@link Patient}
	 * @pregId the id of the {@link Pregnancy}
	 * @type the type of the visit
	 * @pregnancynr the number of the patients pregnancy
	 */
	public PregnancyVisit(int pat_id, int pregId, int type, int pregnancynr){
		//this.visit = new Visit();
		super();
		setNote("");
		visitType = type;
		this.pregnancyId = pregId;
		this.patientId = pat_id;
		this.pregnancyNr = pregnancynr;
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
