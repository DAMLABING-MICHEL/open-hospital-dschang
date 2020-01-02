package org.isf.admission.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.IoOperations;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class AdmissionBrowserManager {

	private IoOperations ioOperations = new IoOperations();

	/**
	 * Returns all patients with ward in which they are admitted.
	 * @return the patient list with associated ward or <code>null</code> if the operation fails.
	 */
	public ArrayList<AdmittedPatient> getAdmittedPatients(){
		try {
			return ioOperations.getAdmittedPatients();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list or <code>null</code> if the operation fails.
	 */ 
	public ArrayList<AdmittedPatient> getAdmittedPatients(String searchTerms){
		try {
			return ioOperations.getAdmittedPatients(searchTerms);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	} 
	public ArrayList<AdmittedPatient> getAdmittedPatients(ArrayList<String> wardCodeList, String searchTerms){
		try {
			return ioOperations.getAdmittedPatients(wardCodeList, searchTerms);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	} 
	public ArrayList<AdmittedPatient> getAdmittedPatientsSearch(String searchTerms){
		try {
			return ioOperations.getAdmittedPatientsSearch(searchTerms);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	} 
	
	public ArrayList<AdmittedPatient> getAdmittedPatients(int sTART_INDEX, int pAGE_SIZE, String searchTerms){
		try {
			return ioOperations.getAdmittedPatients(sTART_INDEX, pAGE_SIZE, searchTerms);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<AdmittedPatient> getAdmittedPatientsSearch(int sTART_INDEX, int pAGE_SIZE, String searchTerms){
		try {
			return ioOperations.getAdmittedPatientsSearch(sTART_INDEX, pAGE_SIZE, searchTerms);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public int getCountPatients(String searchTerms){
		try {
			return ioOperations.getCountPatients(searchTerms);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Returns the admission with the selected id.
	 * @param id the admission id.
	 * @return the admission with the specified id, <code>null</code> otherwise.
	 */
	public Admission getAdmission(int id){
		try {
			return ioOperations.getAdmission(id);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the only one admission without dimission date (or null if none) for the specified patient.
	 * @param patient the patient target of the admission.
	 * @return the patient admission or <code>null</code> if the operation fails.
	 */
	public Admission getCurrentAdmission(Patient patient){
		try {
			return ioOperations.getCurrentAdmission(patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the admissions for the specified patient.
	 * @param patient the patient.
	 * @return the admission list or <code>null</code> if the operation fails.
	 */
	public ArrayList<Admission> getAdmissions(Patient patient){
		try {
			return ioOperations.getAdmissions(patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns the next prog in the year for a certain ward.
	 * @param wardId the ward id.
	 * @return the next prog or <code>null</code> if the operation fails.
	 */
	public int getNextYProg(String wardId){
		try {
			return ioOperations.getNextYProg(wardId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 1;
		}
	}
	
	/**
	 * Returns the max progressive number within specified month of specified year.
	 * 
	 * @param month
	 * @param year
	 * @return <code>int</code> - the progressive number in the month
	 */
	public int getProgMonth(int month, int year) {
		try {
			return ioOperations.getProgMonth(month, year);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 * @return the admission types  or <code>null</code> if the operation fails.
	 */
	public ArrayList<AdmissionType> getAdmissionType(){	
		try {
			return ioOperations.getAdmissionType();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Lists the {@link DischargeType}s.
	 * @return the discharge types  or <code>null</code> if the operation fails.
	 */
	public ArrayList<DischargeType> getDischargeType(){	
		try {
			return ioOperations.getDischargeType();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Inserts a new admission.
	 * @param admission the admission to insert.
	 * @return <code>true</code> if the admission has been successfully inserted, <code>false</code> otherwise.
	 */
	public boolean newAdmission(Admission admission){
		try {
			return ioOperations.newAdmission(admission);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 * @param admission the admission to insert.
	 * @return the generated id or <code>null</code> if the operation fails.
	 */
	public int newAdmissionReturnKey(Admission admission){
		try {
			return ioOperations.newAdmissionReturnKey(admission);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return -1;
		}
	}

	/**
	 * Updates the specified {@link Admission} object.
	 * @param admission the admission object to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 */
	public boolean updateAdmission(Admission admission){
		try {
			boolean recordUpdated = ioOperations.hasAdmissionModified(admission);

			if (!recordUpdated) { 
				// it was not updated
				return ioOperations.updateAdmission(admission);
			} else { 
				// it was updated by someone else
				String message = MessageBundle.getMessage("angal.admission.thedatahasbeenupdatedbysomeoneelse")	+ MessageBundle.getMessage("angal.admission.doyouwanttooverwritethedata");
				int response = JOptionPane.showConfirmDialog(null, message, MessageBundle.getMessage("angal.admission.select"), JOptionPane.YES_NO_OPTION);
				boolean overWrite = response== JOptionPane.OK_OPTION;

				if (overWrite) {
					// the user has confirmed he wants to overwrite the record
					return ioOperations.updateAdmission(admission);
				}
			}
			return false;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Sets an admission record to deleted.
	 * @param admissionId the admission id.
	 * @return <code>true</code> if the record has been set to delete.
	 */
	public boolean setDeleted(int admissionId){
		try {
			return ioOperations.setDeleted(admissionId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Counts the number of used bed for the specified ward.
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 */
	public int getUsedWardBed(String wardId) {
		try {
			return ioOperations.getUsedWardBed(wardId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Deletes the patient photo.
	 * @param patientId the patient id.
	 * @return <code>true</code> if the photo has been deleted, <code>false</code> otherwise.
	 */
	public boolean deletePatientPhoto(int id) {
		try {
			return ioOperations.deletePatientPhoto(id);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public ArrayList<AdmittedPatient> getPregnancyAdmittedPatients(String regex) {
		try {
			return ioOperations.getPregnancyAdmittedPatients(regex);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<AdmittedPatient> getPregnancyAdmittedPatients(String regex, int sTART_INDEX, int pAGE_SIZE) {
		try {
			return ioOperations.getPregnancyAdmittedPatients(regex, sTART_INDEX, pAGE_SIZE);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public int getPregnancyPatientsCount(String regex) {
		try {
			return ioOperations.getPregnancyPatientsCount(regex);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}
	public  HashMap<String, Integer> getCountADM(int year,String dichargetype) throws ParseException {
		try {
			return ioOperations.getCountADM(year,dichargetype);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public  HashMap<String, Integer> getCountTransByAdmissionAndOpd(int year,String dichargetype, String referralTo) throws ParseException {
		try {
			return ioOperations.getCountTransByAdmissionAndOpd(year,dichargetype,  referralTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public  HashMap<String, Integer> getCountDelivryResultDeath(int year, String motherDeathCode, String childDeathCode) throws ParseException {
		try {
			return ioOperations.getCountDelivryResultDeath(year,motherDeathCode,  childDeathCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public  HashMap<String, Integer> getCountDelivryRChildHivNegMotherPos(int year, String childNegativeCode, String hivExamCode, String hivPositiveCode,String childPositiveCode) throws ParseException {
		try {
			return ioOperations.getCountDelivryRChildHivNegMotherPos(year,childNegativeCode,  hivExamCode, hivPositiveCode, childPositiveCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	
}
