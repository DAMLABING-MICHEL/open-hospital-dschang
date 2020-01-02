package org.isf.pregnancy.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.Delivery;
import org.isf.pregnancy.service.IoOperationsDelivery;
import org.isf.utils.exception.OHException;

public class PregnancyDeliveryManager {
	/**
	 * @uml.property name="ioOperationsAdmission"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private IoOperationsDelivery ioOperationsDelivery = new IoOperationsDelivery();
	/**
	 * @uml.property name="ioOperationPregnancy"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */



	/**
	 * deletes all the records related to an admission in the PregnancyAdmission
	 * and PregnancyDelivery database table
	 * 
	 * @param admId
	 *            the id of the admission
	 * @return true if the records are deleted correctly
	 */
	public boolean deleteDeliveries(int admId) {
		return ioOperationsDelivery.deleteAllDeliveryOfAdmission(admId);
		
	}
	/**
	 * 
	 * @param admId the id of the Admission
	 * @param delivery the {@link Delivery}
	 * @return true if the delivery is inserted correctly
	 */
	public boolean insertDelivery(int admId, Delivery delivery){
		//System.out.println("isert delivery");
		return ioOperationsDelivery.insertPregnancyDelivery(admId, delivery);
	}
	/**
	 * 
	 * @param admId the id of the {@link Admission}
	 * @return the list of Deliveries associated to the Admission
	 */
	public ArrayList<Delivery> getDeliveriesOfAdmission(int admId){
		return ioOperationsDelivery.getDeliveriesOfAdmission(admId);
	}
	/**
	 * 
	 * @param patId the id of the patient
	 * @return a {@link HashMap} with the deliveryresulttype as key and the cound as value
	 */
	public HashMap<String, Integer> getDeliveriesOfPatient(int patId){
		return ioOperationsDelivery.getDeliveryCount(patId);
	}
	/**
	 * 
	 * @param patId the id of the {@link Patient}
	 * @return the number of visits performed by the patient
	 */
	public int patientVisitcount(int patId){
		return ioOperationsDelivery.selectVisitCount(patId);
	}
	
	public HashMap<String, Integer> getCountDelivery(int year){	
		try {
			return ioOperationsDelivery.getCountDelivery(year);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
