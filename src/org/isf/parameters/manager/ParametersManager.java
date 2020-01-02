package org.isf.parameters.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import org.isf.exatype.model.ExamType;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.parameters.service.IoOperation;
import org.isf.parameters.model.Parameter;
import org.isf.utils.exception.OHException;

public class ParametersManager {

	private IoOperation ioOperations = new IoOperation();

	/**
	 * Return the list of {@link Parameter}s.
	 * @return the list of {@link Parameter}s. It could be <code>null</code>
	 */
	public ArrayList<Parameter> getParameters() {
		try {
			return ioOperations.getParameters();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<Parameter> getParameters(String type) {
		try {
			return ioOperations.getParameters(type);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	

	/**
	 * Insert a new {@link Parameter} in the DB.
	 * 
	 * @param examType - the {@link Parameter} to insert.
	 * @return <code>true</code> if the Parameter has been inserted, <code>false</code> otherwise.
	 */
	public boolean newParameter(Parameter parameter) {
		try {
			return ioOperations.newParameter(parameter);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Update an already existing {@link Parameter}.
	 * @param examType - the {@link Parameter} to update
	 * @return <code>true</code> if the Parameter has been updated, <code>false</code> otherwise.
	 */
	public boolean updateParameter(Parameter parameter) {
		try {
			return ioOperations.updateParameter(parameter);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Delete the passed {@link Parameter}.
	 * @param Param - the {@link Parameter} to delete.
	 * @return <code>true</code> if the Parameter has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteParameter(int parameterId) {
		try {
			return ioOperations.deleteParameter(parameterId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * get the passed {@link Parameter}.
	 * @param Param - the {@link Parameter} to delete.
	 * @return <code>Parameter</code> if the Parameter has been retrieved, <code>null</code> otherwise.
	 * @throws OHException
	 */
	public Parameter getParameter(String parameterCode) {
		try {
			return ioOperations.getParameter(parameterCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public boolean isKeyPresent(String code) {
		try {
			return ioOperations.isKeyPresent(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return true;
		}
	}
}
