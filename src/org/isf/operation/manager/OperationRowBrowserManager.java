package org.isf.operation.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.generaldata.MessageBundle;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.IoOperations;
import org.isf.opetype.model.OperationType;
import org.isf.utils.exception.OHException;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dinamic data (memory)
 * 
 * @author Rick, Vero, Pupo
 * 
 */
public class OperationRowBrowserManager {

	private IoOperations ioOperations = new IoOperations();

	/**
	 * return the list of {@link OperationRow}s
	 * 
	 * @return the list of {@link Operationow}s. It could be <code>empty</code> or <code>null</code>.
	 */
	public ArrayList<OperationRow> getOperationRow() {
		try {
			return ioOperations.getOperationRow();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * return the {@link OperationRow}s whose type matches specified string
	 * 
	 * @param typeDescription - a type description
	 * @return the list of {@link OperationRow}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHException 
	 */
	public OperationRow getOperationRowById(String id) {
		try {
			return ioOperations.getOperationRowById(id);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * return the {@link Operation} whose code matches specified string
	 * 
	 * @param code - an operation code
	 * @return the {@link Operation} that matched the given code or null if none.
	 * @throws OHException 
	 */
	public List<OperationRow> getOperationByIdAdmission(String idAdmission) {
		try {
			return ioOperations.getOperationByIdAdmission(idAdmission);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public List<OperationRow> getOperationRowByPatient(String idApatient) {
		try {
			return ioOperations.getOperationRowByPatient(idApatient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * return the {@link Operation} whose code matches specified string
	 * 
	 * @param code - an operation code
	 * @return the {@link Operation} that matched the given code or null if none.
	 * @throws OHException 
	 */
	public List<OperationRow> getOperationByIdOpd(String idOpd) {
		try {
			return ioOperations.getOperationByIdOpd(idOpd);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * insert an {@link Operation} in the DB
	 * 
	 * @param operation - the {@link Operation} to insert
	 * @return <code>true</code> if the operation has been inserted, <code>false</code> otherwise.
	 */
	public boolean newOperationRow(OperationRow operationRow) {
		try {
			return ioOperations.newOperationRow(operationRow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Return the list of {@link OperationRow} that has not yet been billed
	 * @return <code>List<OperationRow></code>
	 * @throws OHException
	 */
	public List<OperationRow> getOperationWithoutBill(String idPatient) throws OHException{
		return ioOperations.getOperationWithoutBill(idPatient);
	}
	
	public boolean hasOperationWithoutBill(String patCode){
		try {
			List<OperationRow> opeList=this.getOperationWithoutBill(patCode);
			return opeList!=null && opeList.size()>0;
		} catch (OHException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}

	/** 
	 * updates an {@link Operation} in the DB
	 * 
	 * @param operation - the {@link Operation} to update
	 * @return <code>true</code> if the item has been updated. <code>false</code> other
	 * @throws OHException 
	 */
	public boolean updateOperationRow(OperationRow operationRow) {
		try {
				return ioOperations.updateOperationRow(operationRow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean updateBillIdOperationRow(int OperationRowId, int billId) {
		try {
				return ioOperations.updateBillIdOperationRow(OperationRowId,billId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/** 
	 * Delete a {@link Operation} in the DB
	 * @param operation - the {@link Operation} to delete
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise.
	 */
	public boolean deleteOperationRow(OperationRow operationRow) {
		try {
			return ioOperations.deleteOperationRow(operationRow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public boolean deleteOperationRow(String operationRowID) {
		try {
			return ioOperations.deleteOperationRow(operationRowID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * checks if an {@link Operation} code has already been used
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
	 */
//	public boolean idControl(String id) {
//		try {
//			return ioOperations.isIdPresent(id);
//		} catch (OHException e) {
//			JOptionPane.showMessageDialog(null, e.getMessage());
//			return false;
//		}
//	}
	
	/**
	 * checks if an {@link Operation} description has already been used within the specified {@link OperationType} 
	 * 
	 * @param description - the {@link Operation} description
	 * @param typeCode - the {@link OperationType} code
	 * @return <code>true</code> if the description is already in use, <code>false</code> otherwise.
	 */
	public boolean descriptionControl(String description, String typeCode) {
		try {
			return ioOperations.isDescriptionPresent(description,typeCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public HashMap<String, Integer> getCountMinorMajorOperation(int year,int minorOpeCode,int majorOpeCode){	
		try {
			return ioOperations.getCountMinorMajorOperation(year,minorOpeCode,majorOpeCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
