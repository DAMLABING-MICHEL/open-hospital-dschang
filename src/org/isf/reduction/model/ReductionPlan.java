package org.isf.reduction.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;

/***
 * Store reduction plan that will help manage reduction rate for patients whom hospital offer preferential prices
 * @author magloire
 *
 */
public class ReductionPlan {

	/**
	 * Id of the element
	 */
	int id;
	/**
	 * Description of the billing info
	 */
	String description;
	/***
	 * Reduction rate for operation
	 */
	double operationRate;
	/***
	 * Reduction rate for medical
	 */
	double medicalRate;
	/**
	 * Reduction rate for exams
	 */
	double examRate;
	/**
	 * Reduction rate for other services
	 */
	double otherRate;
	/**
	 * Exceptional reduction rate for some exams
	 */
	ArrayList<ExamsReduction> examReductions;
	/**
	 * exceptional reduction rate for some medical
	 */
	ArrayList<MedicalsReduction> medicalsReductions;
	/**
	 * Exceptional reduction rate for some operations
	 */
	ArrayList<OperationReduction> operationReductions;
	/**
	 * exceptional reduction rate for some other services
	 */
	ArrayList<OtherReduction> otherReductions;
	
	/**
	 * creation date of this informations
	 */
	private GregorianCalendar date;
	/**
	 * Last update date
	 */
	private GregorianCalendar update;
	
	
	
	public ReductionPlan(){
		
	}
	
	public ReductionPlan( int id, String description,  double operationRate, double medicalRate,
			double examRate, double otherRate, GregorianCalendar date, GregorianCalendar update) {
		super();
		this.id=id;
		this.description = description;
		this.operationRate = operationRate;
		this.medicalRate = medicalRate;
		this.examRate = examRate;
		this.otherRate = otherRate;
		this.date=date;
		this.update=update;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getOperationRate() {
		return operationRate;
	}
	public void setOperationRate(double operationRate) {
		this.operationRate = operationRate;
	}
	public double getMedicalRate() {
		return medicalRate;
	}
	public void setMedicalRate(double medicalRate) {
		this.medicalRate = medicalRate;
	}
	public double getExamRate() {
		return examRate;
	}
	public void setExamRate(double examRate) {
		this.examRate = examRate;
	}
	public double getOtherRate() {
		return otherRate;
	}
	public void setOtherRate(double otherRate) {
		this.otherRate = otherRate;
	}
	
	public ArrayList<ExamsReduction> getExamreductions() {
		return examReductions;
	}

	public void setExamReductions(ArrayList<ExamsReduction> examReductions) {
		this.examReductions = examReductions;
	}

	public ArrayList<MedicalsReduction> getMedicalsReductions() {
		return medicalsReductions;
	}

	public void setMedicalsReductions(ArrayList<MedicalsReduction> medicalsReductions) {
		this.medicalsReductions = medicalsReductions;
	}

	public ArrayList<OperationReduction> getOperationReductions() {
		return operationReductions;
	}

	public void setOperationreductions(
			ArrayList<OperationReduction> operationReductions) {
		this.operationReductions = operationReductions;
	}

	public ArrayList<OtherReduction> getOtherReductions() {
		return otherReductions;
	}

	public void setOtherReductions(ArrayList<OtherReduction> otherReductions) {
		this.otherReductions = otherReductions;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getDate() {
		if(date==null){
			date=new GregorianCalendar();
			date.setTime(new Date());
		}
		return date;
	}
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getUpdate() {
		if(update==null){
			update=new GregorianCalendar();
			update.setTime(new Date());
		}
		return update;
	}
	public void setUpdate(GregorianCalendar update) {
		this.update = update;
	}
	
	@Override
	public String toString(){
		return description;		
	}
	
	
}
