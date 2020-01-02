package org.isf.pregnancy.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.isf.patient.model.Patient;

/**
 * @author Martin Reinstadler
 * this class represents the database table PREGNANCY. The information related to a 
 * pregnancy is encoded together with a reference to a {@link Patient} and a listo of
 * {@link PregnancyVisit}
 *
 */
public class Pregnancy {
	/**
	 * @uml.property  name="pregnancynr"
	 */
	private int pregnancynr=1;
	/**
	 * @uml.property  name="lmp"
	 * @uml.associationEnd  
	 */
	private GregorianCalendar lmp= null;
	/**
	 * @uml.property  name="scheduled_delivery"
	 */
	private GregorianCalendar scheduled_delivery = null;
	/**
	 * @uml.property  name="active"
	 */
	private boolean active= true;
	/**
	 * @uml.property  name="visits"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="org.isf.pregnancy.model.PregnancyVisit"
	 */
	private ArrayList<PregnancyVisit> visits= null;
	/**
	 * @uml.property  name="patId"
	 */
	private int patId = 0;
	/**
	 * @uml.property  name="pregId"
	 */
	private int pregId= 0;
	
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//
	
	/**
	 * Initializes a new Preganancy instance by setting
	 * pregnancynr to -1, lmp to a new GregorianCalendar, 
	 * preg_del to 9 monts after lmp, active to true,
	 * the visits to a new List of visits
	 * @pat_id the identifier of the {@link Patient}
	 */
	public Pregnancy(int pat_id){
		pregnancynr =1;
		lmp = new GregorianCalendar();
		this.scheduled_delivery = (GregorianCalendar)this.lmp.clone();
		scheduled_delivery.add(2, 9);
		visits = new ArrayList<PregnancyVisit>();
		this.patId= pat_id;
		this.pregId = -1;
	}
	/**
	 * Creates a new instance of Pregnancy by setting automatically 
	 * the prev_delivery 9 months after the lmpdate.
	 * @param pregnr the number of the patients pregnancy
	 * @param lmp last menstrual period
	 * @param pat_id the id of the {@link Patient}
	 * @param vis the list of {@link PregnancyVisit}
	 * @param act true if the pregnancy is still active
	 */
	public Pregnancy(int pregnr, GregorianCalendar lmpdate,  
			int pat_id, ArrayList<PregnancyVisit> vis){
		this.lmp= lmpdate;
		this.patId = pat_id;
		this.pregnancynr = pregnr;
		this.scheduled_delivery = (GregorianCalendar)this.lmp.clone();
		this.scheduled_delivery.add(2, 9);
		this.visits = vis;
	}
	/**
	 * @return  the id of the  {@link Patient}  
	 * @uml.property  name="patId"
	 */
	public int getPatId() {
		return patId;
	}
	/**
	 * @param patid  the id of the  {@link Patient}  
	 * @uml.property  name="patId"
	 */
	public void setPatId(int patid) {
		this.patId = patid;
	}
	/**
	 * @return  the number of the patients pregnancy
	 * @uml.property  name="pregnancynr"
	 */
	public int getPregnancynr() {
		return pregnancynr;
	}
	/**
	 * @param pregnancynr  the number of the patients pregnancy
	 * @uml.property  name="pregnancynr"
	 */
	public void setPregnancynr(int pregnancynr) {
		this.pregnancynr = pregnancynr;
	}
	/**
	 * @return  the date of the last menstrual periode of this pregnancy as  {@link GregorianCalendar}  
	 * @uml.property  name="lmp"
	 */
	public GregorianCalendar getLmp() {
		return lmp;
	}
	/**
	 * this method sets the scheduled delivery to 9 months after the  specified date of the lmp
	 * @param lmp  the date of the last menstrual periode of this pregnancy
	 * @uml.property  name="lmp"
	 */
	public void setLmp(GregorianCalendar lmp) {
		this.lmp = lmp;
		this.scheduled_delivery = (GregorianCalendar)this.lmp.clone();
		this.scheduled_delivery.add(2, 9);
		
	}
	/**
	 * @return  the date of the scheduled delivery as  {@link GregorianCalendar}  
	 * @uml.property  name="scheduled_delivery"
	 */
	public GregorianCalendar getScheduled_delivery() {
		return scheduled_delivery;
	}
	/**
	 * @param sched_delivery  the date of the scheduled delivery
	 * @uml.property  name="scheduled_delivery"
	 */
	public void setScheduled_delivery(GregorianCalendar sched_delivery) {
		this.scheduled_delivery= sched_delivery;
	}
	/**
	 * @return  true if the pregnancy is set to active
	 * @uml.property  name="active"
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active  whether the pregnancy should be acitve or not
	 * @uml.property  name="active"
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * 
	 * @return the list of {@link PregnancyVisit}
	 */
	public ArrayList<PregnancyVisit> getVisits() {
		return visits;
	}
	/**
	 * 
	 * @param visits the list of {@link PregnancyVisit}
	 */
	public void setVisits(ArrayList<PregnancyVisit> visits) {
		this.visits = visits;
	}
	/**
	 * 
	 * @param visit a single {@link PregnancyVisit}
	 */
	public void addVisit(PregnancyVisit visit){
		this.visits.add(visit);
	}
	/**
	 * @return  the id of the pregnancy (primary key of the database table)
	 * @uml.property  name="pregId"
	 */
	public int getPregId(){
		return this.pregId;
	}
	/**
	 * @param id  the id of the pregnancy (primary key of the database table)
	 * @uml.property  name="pregId"
	 */
	public void setPregId(int id){
		this.pregId= id;
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
