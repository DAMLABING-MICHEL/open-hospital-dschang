package org.isf.medicalinventory.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import org.isf.exatype.model.ExamType;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.IoOperation;
import org.isf.utils.exception.OHException;

public class MedicalInventoryManager {

	private IoOperation ioOperations = new IoOperation();

	/**
	 * Return the list of {@link MedicalInventory}s.
	 * @return the list of {@link MedicalInventory}s. It could be <code>null</code>
	 */
	public ArrayList<MedicalInventory> getMedicalInventory() {
		try {
			return ioOperations.getMedicalInventory();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public int getInventoryInProgress() throws OHException {
		try {
			return ioOperations.getInventoryInProgress();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return -1;
		}
	}
	
	public int getInventoryWardInProgress(String wardId) throws OHException {
		try {
			return ioOperations.getInventoryWardInProgress(wardId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return -1;
		}
	}
	
	public ArrayList<MedicalInventory> getMedicalInventory(GregorianCalendar dateFrom, GregorianCalendar dateTo, String state, String type) {
		try {
			return ioOperations.getMedicalInventory(dateFrom, dateTo, state, type);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	public ArrayList<MedicalInventory> getMedicalInventory(GregorianCalendar dateFrom, GregorianCalendar dateTo, String state, String type, int start_index, int page_size) {
		try {
			return ioOperations.getMedicalInventory(dateFrom, dateTo, state, type, start_index, page_size);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Insert a new {@link MedicalInventory} in the DB.
	 * 
	 * @param examType - the {@link MedicalInventory} to insert.
	 * @return <code>true</code> if the MedicalInventory has been inserted, <code>false</code> otherwise.
	 */
	public boolean newMedicalInventory(MedicalInventory medicalinventory) {
		try {
			return ioOperations.newMedicalInventory(medicalinventory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public int newMedicalInventoryGetId(MedicalInventory medicalinventory) throws OHException {
		try {
			return ioOperations.newMedicalInventoryGetId(medicalinventory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Update an already existing {@link MedicalInventory}.
	 * @param examType - the {@link MedicalInventory} to update
	 * @return <code>true</code> if the MedicalInventory has been updated, <code>false</code> otherwise.
	 */
	public boolean updateMedicalInventory(MedicalInventory medicalinventory) {
		try {
			return ioOperations.updateMedicalInventory(medicalinventory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	

	/**
	 * Delete the passed {@link MedicalInventory}.
	 * @param medicalinventory - the {@link MedicalInventory} to delete.
	 * @return <code>true</code> if the MedicalInventory has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteMedicalInventory(MedicalInventory medicalinventory) {
		try {
			return ioOperations.deleteMedicalInventory(medicalinventory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Delete the passed {@link ExamType}.
	 * @param examType - the {@link ExamType} to delete.
	 * @return <code>true</code> if the examType has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteMedicalInventory(int medicalinventoryId) {
		try {
			return ioOperations.deleteMedicalInventory(medicalinventoryId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	/////////////////////////////////////////////////
	public ArrayList<MedicalInventoryRow> getMedicalInventoryRowByInventory(int inventoryCode){
		try {
			return ioOperations.getMedicalInventoryRowByInventory(inventoryCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<MedicalInventoryRow> getMedicalInventoryRowByInventoryAndByMedicalCode(int inventoryCode, String medicalCode){
		try {
			return ioOperations.getMedicalInventoryRowByInventoryAndByMedicalCode(inventoryCode, medicalCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public boolean newMedicalInventoryRow(MedicalInventoryRow medicalinventoryrow) throws OHException {
		try {
			return ioOperations.newMedicalInventoryRow(medicalinventoryrow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public int newMedicalInventoryRowGetId(MedicalInventoryRow medicalinventoryrow) throws OHException, SQLException {
		try {
			return ioOperations.newMedicalInventoryRowGetId(medicalinventoryrow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}
	/**
	 * check if the reference number is already used
	 * 
	 * @return <code>true</code> if is already used, <code>false</code>
	 *         otherwise.
	 */
	public boolean refNoExists(String refNo) {
		try {
			return ioOperations.refNoExists(refNo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return true;
		}
	}
		
	
	public boolean updateMedicalInventoryRow(MedicalInventoryRow medicalinventoryrow) throws OHException {
		try {
			return ioOperations.updateMedicalInventoryRow(medicalinventoryrow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean deleteMedicalInventoryRow(int code) throws OHException {
		try {
			return ioOperations.deleteMedicalInventoryRow(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public boolean deleteMedicalInventoryRow(MedicalInventoryRow medicalinventoryrow) throws OHException {
		try {
			return ioOperations.deleteMedicalInventoryRow(medicalinventoryrow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
}
