package org.isf.patient.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.SmsParameters;
import org.isf.patient.model.Patient;
import org.isf.patient.service.IoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.ValidationPatientGroup;
import org.isf.video.manager.VideoManager;

public class PatientBrowserManager {

	private IoOperations ioOperations = new IoOperations();
	
	/**
	 * methot that insert a new Patient in the db
	 * 
	 * @param patient
	 * @return true - if the new Patient has been inserted
	 */
	public boolean newPatient(Patient patient) {
		try {
			return ioOperations.newPatient(patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * 
	 * method that update an existing Patient in the db
	 * 
	 * @param patient
	 * @return true - if the existing Patient has been updated
	 */
	public boolean updatePatient(Patient patient) {
		return this.updatePatient(patient, true);
	}
	
	/**
	 * 
	 * method that update an existing Patient in the db
	 * 
	 * @param patient
	 * @param check - if true check if the Patient has been modified since last read
	 * @return true - if the existing Patient has been updated
	 */
	private boolean updatePatient(Patient patient, boolean check) {
		try {
			if (!ioOperations.updatePatient(patient, check)) {
				int ok = JOptionPane.showConfirmDialog(null,
						MessageBundle.getMessage("angal.patient.thedatahasbeenupdatedbysomeoneelse") + ".\n" + MessageBundle.getMessage("angal.patient.doyouwanttooverwritethedata") + "?",
						MessageBundle.getMessage("angal.patient.select"), JOptionPane.YES_NO_OPTION);
				if (ok != JOptionPane.OK_OPTION)
					return false;
				else 
					return this.updatePatient(patient, false);
			} else 
				return true;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * method that returns the full list of Patients not logically deleted
	 * 
	 * @return the list of patients (could be empty)
	 */
	public ArrayList<Patient> getPatient() {
		try {
			return ioOperations.getPatients();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Patient>();
		}
	}
	public ArrayList<Patient> getPatientEmployee() {
		try {
			return ioOperations.getPatientsEmployee();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Patient>();
		}
	}
	
	/**
	 * method that get a Patient by his/her name
	 * 
	 * @param name
	 * @return the Patient that match specified name (could be null)
	 */
	public Patient getPatient(String name) {
		try {
			return ioOperations.getPatient(name);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * method that get a Patient by his/her ID
	 * 
	 * @param code
	 * @return the Patient (could be null)
	 */
	public Patient getPatient(Integer code) {
		try {
			return ioOperations.getPatient(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * get a Patient by his/her ID, even if he/her has been logically deleted
	 * 
	 * @param code
	 * @return the list of Patients (could be null)
	 */
	public Patient getPatientAll(Integer code) {
		try {
			return ioOperations.getPatientAll(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * methot that get next PAT_ID is going to be used.
	 * 
	 * @return code
	 */
	public int getNextPatientCode() {
		try {
			return ioOperations.getNextPatientCode();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}
	
	/**
	 * method that merge all clinic details under the same PAT_ID
	 * 
	 * @param mergedPatient
	 * @param patient2
	 * @return true - if no OHExceptions occurred
	 */
	public boolean mergePatientHistory(Patient mergedPatient, Patient patient2) {
		boolean admitted = false;
		AdmissionBrowserManager admMan = new AdmissionBrowserManager();
		if (admMan.getCurrentAdmission(mergedPatient) != null) admitted = true;
		else if (admMan.getCurrentAdmission(patient2) != null) admitted = true;
		if (admitted) {
			JOptionPane.showMessageDialog(null, 
					MessageBundle.getMessage("angal.admission.cannotmergeadmittedpatients"),
					MessageBundle.getMessage("angal.admission.merge"),
					JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, 
					MessageBundle.getMessage("angal.admission.patientscannothavependingtask"), 
					MessageBundle.getMessage("angal.admission.merge"),
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		boolean billPending = false;
		BillBrowserManager billMan = new BillBrowserManager();
		ArrayList<Bill> bills = billMan.getPendingBills(mergedPatient.getCode());
		if (bills != null && !bills.isEmpty()) billPending = true;
		else {
			bills = billMan.getPendingBills(patient2.getCode());
			if (bills != null && !bills.isEmpty()) billPending = true;
		}
		if (billPending) {
			JOptionPane.showMessageDialog(null, 
					MessageBundle.getMessage("angal.admission.cannotmergewithpendingbills"),
					MessageBundle.getMessage("angal.admission.merge"),
					JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, 
					MessageBundle.getMessage("angal.admission.patientscannothavependingtask"), 
					MessageBundle.getMessage("angal.admission.merge"),
					JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		try {
			return ioOperations.mergePatientHistory(mergedPatient, patient2);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * method that logically delete a Patient (not phisically deleted)
	 * 
	 * @param aPatient
	 * @return true - if the Patient has beeb deleted (logically)
	 */
	public boolean deletePatient(Patient patient) {
		try {
			return ioOperations.deletePatient(patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * method that check if a Patient is already present in the DB by his/her name
	 * 
	 * @param name
	 * @return true - if the patient is already present
	 */
	public boolean isPatientPresent(String name) {
		try {
			return ioOperations.isPatientPresent(name);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * method that returns the full list of Patients not logically deleted with Height and Weight 
	 * 
	 * @param regex
	 * @return the full list of Patients with Height and Weight (could be empty)
	 * @throws OHException
	 */
	public ArrayList<Patient> getPatientWithHeightAndWeight(String regex){
		try {
			return ioOperations.getPatientsWithHeightAndWeight(regex);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Patient>();
		}
	}
	public ArrayList<Patient> getPatientHeadWithHeightAndWeight(){
		try {
			return ioOperations.getPatientsHeadWithHeightAndWeight();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Patient>();
		}
	}
	public ArrayList<Patient> getPatientWithHeightAndWeight2(String regex){
		try {
			return ioOperations.getPatientsWithHeightAndWeight2(regex);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Patient>();
		}
	}
	public static String getPatientPhotoPath(int patID){
		return "patient-"+patID+""+VideoManager.shotPhotosExtension;
	}
	
	public static String checkPatientInformation(Patient patient, String validationGroup){
		String result = "";
		if (patient.getFirstName()==null || patient.getFirstName().equals("")){
			result = MessageBundle.getMessage("angal.patient.insertfirstname")+"\n";
		}
		if (patient.getSecondName()==null || patient.getSecondName().equals("")){
			result = result + MessageBundle.getMessage("angal.patient.insertsecondname")+"\n";
		}
		if (patient.getSex()==null || patient.getSex().equals("")){
			result = result + MessageBundle.getMessage("angal.patient.insertsex")+"\n";
		}
		if(ValidationPatientGroup.GLOBAL.equals(validationGroup)){
			if (patient.getBirthDate()==null || ! (patient.getAge() >= 0)){
				result = result + MessageBundle.getMessage("angal.patient.insertvalidage")+"\n";
			}
		}
		if (patient.getCity()==null || patient.getCity().equals("")){
			result = result + MessageBundle.getMessage("angal.patient.insertcity")+"\n";
		}
		if (patient.getAddress()==null || patient.getAddress().equals("")){
			result = result + MessageBundle.getMessage("angal.patient.insertaddress")+"\n";
		}
		if (patient.getNextKin()==null || patient.getNextKin().equals("")){
			result = result + MessageBundle.getMessage("angal.patient.insertparent")+"\n";
		}
		SmsParameters.getSmsParameters();
		if (patient.getTelephone()==null || patient.getTelephone().equals("") || patient.getTelephone().equals(SmsParameters.ICC)){
			result = result + MessageBundle.getMessage("angal.patient.insertelephone");
		}
		return result;
	}

	public Patient alreadyExistingPatient(Patient patient) {
		try {
			return  ioOperations.alreadyExistingPatient(patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
}
