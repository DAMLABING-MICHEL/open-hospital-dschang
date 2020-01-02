package org.isf.patvac.model;

/*------------------------------------------
* PatientVaccine - class 
* -----------------------------------------
* modification history
* 25/08/2011 - claudia - first beta version
*------------------------------------------*/

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;
import org.isf.vaccine.model.Vaccine;


public class PatientVaccine {
	private int code;
	private int progr;
	private GregorianCalendar vaccineDate;
	private int patId;
	private Vaccine vaccine;
	private int lock;
	private String patName;
	private int    patAge;
	private String patSex;	
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//

	public PatientVaccine(){
		super();
	}
			
	public PatientVaccine(int codeIn, int progIn, GregorianCalendar vacDateIn, 
			               int patIdIn, Vaccine vacIn, int lockIn) {
		code = codeIn;
		progr = progIn;
		vaccineDate = vacDateIn;
		patId = patIdIn;
		vaccine = vacIn;
		lock = lockIn ;
	}
	
	public PatientVaccine(int codeIn, int progIn, GregorianCalendar vacDateIn, 
                          int patIdIn, Vaccine vacIn, int lockIn,
                          String patNameIn, int patAgeIn, String patSexIn) {
		code = codeIn;
		progr = progIn;
		vaccineDate = vacDateIn;
		patId = patIdIn;
		vaccine = vacIn;
		lock = lockIn ;
		patName = patNameIn;
		patAge = patAgeIn;
		patSex = patSexIn;
	}
	
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getProgr() {
		return progr;
	}

	public void setProgr(int progr) {
		this.progr = progr;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getVaccineDate() {
		return vaccineDate;
	}

	
	public void setVaccineDate(GregorianCalendar vaccineDate) {
		this.vaccineDate = vaccineDate;
	}

	public int getPatId() {
		return patId;
	}

	public void setPatId(int patId) {
		this.patId = patId;
	}

	public Vaccine getVaccine() {
		return vaccine;
	}

	public void setVaccine(Vaccine vaccine) {
		this.vaccine = vaccine;
	}
	

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getPatName() {
		return patName;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public int getPatAge() {
		return patAge;
	}

	public void setPatAge(int patAge) {
		this.patAge = patAge;
	}

	public String getPatSex() {
		return patSex;
	}

	public void setPatSex(String patSex) {
		this.patSex = patSex;
	}


	/*
	public void stampa(String cntx) {
		System.out.println("Inizio contesto ====>"+cntx);
		System.out.println("Codice="+code);
		System.out.println("Paziente="+patId);
		System.out.println("vaccino="+vaccine.getCode()+"-"+vaccine.getDescription());
		System.out.println("vaccine.pati.code="+vaccine.getPati().getCode());
		System.out.println("vaccine.pati.descr="+vaccine.getPati().getDescription());
		System.out.println("progressivo="+progr);
		System.out.println("nome="+patName);
		System.out.println("sesso="+patSex);
		System.out.println("age="+patAge);
		System.out.println("Fine contesto ====>"+cntx);
		
	}
	*/
	
	
	public boolean equals(Object other){
		if((other==null)|| (!(other instanceof PatientVaccine))) return false;
		else if((getPatId() == ((PatientVaccine)other).getPatId())
		  		 && getVaccine().equals(((PatientVaccine)other).getVaccine())
		  		 && getVaccineDate().equals(((PatientVaccine)other).getVaccineDate()))
			      return true;
			 else return false;
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
