package org.isf.pregnancy.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyExam;
import org.isf.pregnancy.model.PregnancyExamResult;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregnancy.service.IoOperationsDelivery;
import org.isf.pregnancy.service.IoOperationsExam;
import org.isf.pregnancy.service.IoOperationsPatient;
import org.isf.pregnancy.service.IoOperationsPregnancy;
import org.isf.pregnancy.service.IoOperationsVisit;
import org.isf.utils.exception.OHException;

/**
 * Martin Reinstadler
 * This class manages the IO operations by calling the various methods from them. 
 * The GUI instantiates only this class and no IoOperaitions class for simplicity
 *
 */
public class PregnancyCareManager {
	/**
	 * @uml.property  name="ioOperationsPatient"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IoOperationsPatient ioOperationsPatient = new IoOperationsPatient();
	/**
	 * @uml.property  name="ioOperationsPregnancy"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IoOperationsPregnancy ioOperationsPregnancy = new IoOperationsPregnancy();
	/**
	 * @uml.property  name="ioOperationsVisit"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IoOperationsVisit ioOperationsVisit = new IoOperationsVisit();
	/**
	 * @uml.property  name="ioOperationsAdmission"
	 * @uml.associationEnd  
	 */
	private IoOperationsDelivery ioOperationsAdmission = null;
	/**
	 * @uml.property  name="ioOperationsExam"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IoOperationsExam ioOperationsExam = new IoOperationsExam();
	
	
	/**
	 * @param regex the searchkey
	 * @return a list of female {@link Patient} 
	 */
	public ArrayList<AdmittedPatient> getPregnancyPatients(String regex){
		AdmissionBrowserManager adManager=new AdmissionBrowserManager();
		return adManager.getPregnancyAdmittedPatients(regex);
	}
	/**
	 * @param regex the searchkey
	 * @param pAGE_SIZE 
	 * @param sTART_INDEX 
	 * @return a list of female {@link Patient} 
	 */
	public ArrayList<AdmittedPatient> getPregnancyPatients(String regex, int sTART_INDEX, int pAGE_SIZE){
		AdmissionBrowserManager adManager=new AdmissionBrowserManager();
		return adManager.getPregnancyAdmittedPatients(regex, sTART_INDEX, pAGE_SIZE);
	}
	 
	 public int getPregnancyPatientsCount(String regex){
		AdmissionBrowserManager adManager=new AdmissionBrowserManager();
		return adManager.getPregnancyPatientsCount(regex);
	 }
	/**
	 * 
	 * @param patid the od of the {@link Patient}
	 * @return a list of the patients pregnancies
	 */
	public ArrayList<Pregnancy>getPatientsPregnancies(int patid){
		return ioOperationsPregnancy.getPregnancy(patid);
	}
	/**
	 * 
	 * @param pregid the id of the {@link Pregnancy}
	 * @return the pregnancy for the given id
	 */
	public Pregnancy getPregnancy(int pregid){
		return ioOperationsPregnancy.getPregnancy_byId(pregid);
	}
	/**
	 * @param patientid the id of the {@link PregnancyPatient}
	 * @return a list of visits the patient has performed during her live
	 */
	public ArrayList<PregnancyVisit> getPregnancyVisits(int patientid){
		return ioOperationsVisit.getPregnancyVisits(patientid);
	}
	
	/**
	 * @param visittype the type of the {@link PregnancyVisit}, which can be
	 * prenatal(-1) or postnatal(1)
	 * @return the list of exams previously specified for this type
	 */
	public ArrayList<PregnancyExam> getVisitExams_byVisitType(int visittype){
			return ioOperationsExam.getPregnancyExams(visittype);
		
	}
	
	/**
	 * Inserts a new pregnancy in the database
	 * @param pregnancy the {@link Pregnancy} to insert
	 * @return the id of the new inserted {@link Pregnancy};
	 */
	public int newPregnancy(Pregnancy pregnancy){
		return ioOperationsPregnancy.insertPregnancy(pregnancy.getPregnancynr(),pregnancy.getPatId(), 
				pregnancy.getLmp(), pregnancy.getScheduled_delivery() );
	}
	/**
	 * Inserts a new {@link PregnancyVisit} in the database
	 * @param visit the {@link PregnancyVisit} to be inserted
	 * @return the id of the new {@link PregnancyVisit}
	 * 
	 */
	public int newVisit(PregnancyVisit visit){
		return ioOperationsVisit.insertPregnancyVisit(visit.getPregnancyId(), visit, visit.getNextVisitdate(), 
				visit.getNote(), visit.getType(),visit.getTreatmenttype());
	}
	/**
	 * 
	 * @param visitid the id of the {@link PregnancyVisit}
	 * @param examresults the list of {@link PregnancyExamResult} with outcomes
	 * @return true if the tuples are inserted correctly
	 */
	public boolean newExamOutcomes(int visitid, ArrayList<PregnancyExamResult> examresults){
			return ioOperationsExam.insertExamResult(visitid, examresults);
	}
	/**
	 * 
	 * @param preg the {@link Pregnancy} to be updated
	 * @return true if the tuple is updated correctly
	 */
	public boolean updatePregnancy(Pregnancy preg){
		return ioOperationsPregnancy.updatePregnancy(preg.getPregId(), preg.getPregnancynr(), 
				preg.getLmp(), preg.getScheduled_delivery(), preg.isActive());
	}
	/**
	 * 
	 * @param visit the {@link PregnancyVisit} to be updated
	 * @return true if the tuple is updated correctly
	 */
	public boolean updateVisit(PregnancyVisit visit){
		return ioOperationsVisit.updatePregnancyVisit(visit.getVisitId(), visit, visit.getNextVisitdate(),
				visit.getTreatmenttype(), visit.getNote(), visit.getType());
	}
	/**
	 * 
	 * @param visitid the id of the visit
	 * @param examresult the {@link PregnancyExamResult} to be updated
	 * @return true if the tuple is updated correctly
	 */
	public boolean updateExamResult(int visitid, PregnancyExamResult examresult){
			return ioOperationsExam.updateExamResult(visitid, examresult.getExamCode(), examresult.getOutcome());
	}
	/**
	 * 
	 * @param visitid the id of the {@link PregnancyVisit}
	 * @return true if the visit and the related examresults are deleted coorectly
	 */
	public boolean deletePregnancyVisitAndResults(int visitid){
		boolean res = false;
		ioOperationsExam.deletePregnancyExamResults(visitid);
		res= ioOperationsVisit.deleteVisit(visitid);
		return res;
	}
	/**
	 * deletes a Pregnancy from the database, but leaves Admission records untouched
	 * @param pregid the id of the {@link Pregnancy}
	 * @return true if the pregnancy is deleted correctly
	 */
	public boolean deletePregnancy(int pregid){
		//ioOperationsPregnancy.deleteAllDeliveryOfPregnancy(pregid);
		//ioOperationsPregnancy.deletePregnancyAdmission(pregid);
		return ioOperationsPregnancy.deletePregnancy(pregid);
	}
	/**
	 * 
	 * @param visitid the {@link PregnancyVisit}
	 * @return the Hashmap with the examcode as key and the Examresult as value
	 */
	public HashMap<String, PregnancyExamResult> getExamResults(int visitid){
			return ioOperationsExam.getExamResults(visitid);
	}
//	/**
//	 * 
//	 * @param admId the id of the {@link Admission}
//	 * @return the {@link Admission} with the specified id
//	 */
//	public Admission getAdmission(int admId){
//		if(ioOperationsAdmission == null)
//			ioOperationsAdmission = new IoOperationsDelivery();
//		return ioOperationsAdmission.getAdmission(admId);
//	}
	/**
	 * 
	 * @param patient the {@link Patient}
	 * @return true if the patient has an admission without a discharge date defined
	 */
	public boolean isPatientCurrentlyAdmitted(Patient patient){
		return ioOperationsPatient.isPatientCurrentlyAdmitted(patient);
	}
//////statistics functions
	public HashMap<String, Integer> getCountCPN(int year){	
		try {
			return ioOperationsVisit.getCountCPN(year);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	////////////////////////////
}
