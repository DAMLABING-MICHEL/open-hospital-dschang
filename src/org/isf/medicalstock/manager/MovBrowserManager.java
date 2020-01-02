package org.isf.medicalstock.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.IoOperations;
import org.isf.medtype.model.MedicalType;
import org.isf.serviceprinting.manager.WardEntryItems;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.MovementReportBean;
import org.isf.ward.model.Ward;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;


public class MovBrowserManager {
	
	IoOperations ioOperations;
	
	public MovBrowserManager(){
		ioOperations = new IoOperations();
	}

	/**
	 * Retrieves all the {@link Movement}s.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the retrieved movements.
	 */
	public ArrayList<Movement> getMovements(){
		try {
			return ioOperations.getMovements();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the movement associated to the specified {@link Ward}.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param wardId the ward id.
	 * @param dateTo 
	 * @param dateFrom 
	 * @return the retrieved movements.
	 */
	public ArrayList<Movement> getMovements(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo, boolean fromWard){
		try {
			return ioOperations.getMovements(wardId, dateFrom, dateTo,fromWard);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<Movement> getMovements(GregorianCalendar dateFrom, GregorianCalendar dateTo, String movType){
		try {
			return ioOperations.getMovements(dateFrom, dateTo, movType);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Retrieves all the movement associated to the specified reference number.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param refNo the reference number.
	 * @return the retrieved movements.
	 */
	public ArrayList<Movement> getMovementsByReference(String refNo){
		try {
			return ioOperations.getMovementsByReference(refNo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link Movement}s with the specified criteria.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param medicalCode the medical code.
	 * @param medicalType the medical type.
	 * @param wardId the ward type.
	 * @param movType the movement type.
	 * @param movFrom the lower bound for the movement date range.
	 * @param movTo the upper bound for the movement date range.
	 * @param lotPrepFrom the lower bound for the lot preparation date range.
	 * @param lotPrepTo the upper bound for the lot preparation date range.
	 * @param lotDueFrom the lower bound for the lot due date range.
	 * @param lotDueTo the lower bound for the lot due date range.
	 * @param tOTAL_ROWS 
	 * @param sTART_INDEX 
	 * @return the retrieved movements.
	 */
	public ArrayList<Movement> getMovements(Integer medicalCode,String medicalType,
			String wardId,String movType,GregorianCalendar movFrom,GregorianCalendar movTo,
			GregorianCalendar lotPrepFrom,GregorianCalendar lotPrepTo,
			GregorianCalendar lotDueFrom,GregorianCalendar lotDueTo) {

		if (medicalCode == null && 
				medicalType == null && 
				movType == null && 
				movFrom == null &&
				movTo == null && 
				lotPrepFrom == null && 
				lotPrepTo == null && 
				lotDueFrom == null && 
				lotDueTo == null) {
			return getMovements();
		}

		if (movFrom == null || movTo == null) {
			if (!(movFrom == null && movTo == null)) {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.medicalstock.chooseavalidpreparationdate"));
				return null;
			}
		}

		if (lotPrepFrom == null || lotPrepTo == null) {
			if (!(lotPrepFrom == null && lotPrepTo == null)) {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.medicalstock.chooseavalidpreparationdate"));
				return null;
			}
		}

		if (lotDueFrom == null || lotDueTo == null) {
			if (!(lotDueFrom == null && lotDueTo == null)) {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.medicalstock.chooseavalidduedate"));
				return null;
			}
		}

		try {
			return ioOperations.getMovements(medicalCode,medicalType,wardId,movType,movFrom,movTo,lotPrepFrom,lotPrepTo,lotDueFrom,lotDueTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<Movement> getMovements(Integer medicalCode,String medicalType,
			String wardId,String movType,GregorianCalendar movFrom,GregorianCalendar movTo,
			GregorianCalendar lotPrepFrom,GregorianCalendar lotPrepTo,
			GregorianCalendar lotDueFrom,GregorianCalendar lotDueTo, int sTART_INDEX, int tOTAL_ROWS) {

		if (medicalCode == null && 
				medicalType == null && 
				movType == null && 
				movFrom == null &&
				movTo == null && 
				lotPrepFrom == null && 
				lotPrepTo == null && 
				lotDueFrom == null && 
				lotDueTo == null) {
			return getMovements();
		}

		if (movFrom == null || movTo == null) {
			if (!(movFrom == null && movTo == null)) {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.medicalstock.chooseavalidpreparationdate"));
				return null;
			}
		}

		if (lotPrepFrom == null || lotPrepTo == null) {
			if (!(lotPrepFrom == null && lotPrepTo == null)) {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.medicalstock.chooseavalidpreparationdate"));
				return null;
			}
		}

		if (lotDueFrom == null || lotDueTo == null) {
			if (!(lotDueFrom == null && lotDueTo == null)) {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.medicalstock.chooseavalidduedate"));
				return null;
			}
		}

		try {
			return ioOperations.getMovements(medicalCode,medicalType,wardId,movType,movFrom,movTo,lotPrepFrom,lotPrepTo,lotDueFrom,lotDueTo, sTART_INDEX, tOTAL_ROWS);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<MovementReportBean> getMovements(Integer medicalCode, GregorianCalendar fromDate, GregorianCalendar toDate, String wardCode){
		try {
			return ioOperations.getMovements(medicalCode, fromDate, toDate, wardCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public ArrayList<MovementReportBean> getMovementsMagasin(Integer medicalCode, GregorianCalendar fromDate, GregorianCalendar toDate){
		try {
			return ioOperations.getMovementsMagasin(medicalCode, fromDate, toDate);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public boolean updateMedicalWardInitialQuantity(String wardCode, int medicalCode, double initialQuantity){
		try {
			return ioOperations.updateMedicalWardInitialQuantity(wardCode, medicalCode, initialQuantity);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public Integer getStockQty(Integer medical_code, Timestamp mmv_date, String ward_code){
		try {
			return ioOperations.getStockQty(medical_code, mmv_date, ward_code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return -1;
		}
	}

	public ArrayList<Movement> getMovements(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo,
			boolean fromWard, MedicalType medicalTypeSelected) {
	 
		try {
			return ioOperations.getMovements(wardId, dateFrom, dateTo,fromWard,medicalTypeSelected);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	

	public ArrayList<WardEntryItems> getWardEntries(String code, GregorianCalendar dateFrom, GregorianCalendar dateTo,
			boolean fromWard, MedicalType medicalTypeSelected) {

		try {
			return ioOperations.getWardEntries(code, dateFrom, dateTo,fromWard,medicalTypeSelected);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
}
