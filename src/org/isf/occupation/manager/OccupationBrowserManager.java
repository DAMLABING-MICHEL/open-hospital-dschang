package org.isf.occupation.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.occupation.model.Occupation;
import org.isf.occupation.service.IoOperation;
import org.isf.utils.exception.OHException;

public class OccupationBrowserManager {
	private IoOperation ioOperations = new IoOperation();

	/**
	 * Returns all the available {@link Occupations}s.
	 * @return a list of occupation or <code>null</code> if the occupation fails.
	 */
	public ArrayList<Occupation> getOccupations() {
		try {
			return ioOperations.getOccupation();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns an {@link Occupation} by it code.
	 * @return an occupation or <code>null</code> if there is no occupation for the provided code.
	 */
	public Occupation getOccupation(String code) {
		try {
			return ioOperations.getOccupation(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Stores a new {@link Occupation}.
	 * @param occupation the occupation to store.
	 * @return <code>true</code> if the occupation has been stored, <code>false</code> otherwise.
	 */
	public boolean newOccupation(Occupation occupation) {
		try {
			return ioOperations.newOccupation(occupation);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the specified {@link Occupation}.
	 * @param occupation the occupation to update.
	 * @return <code>true</code> if the occupation has been updated, <code>false</code> otherwise.
	 */
	public boolean updateOccupation(Occupation occupation) {
		try {
			return ioOperations.updateOccupation(occupation);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if the specified Code is already used by others {@link Occupation}s.
	 * @param code the occupation code to check.
	 * @return <code>true</code> if the code is already used, <code>false</code> otherwise.
	 */
	public boolean codeControl(String code) {
		try {
			return ioOperations.isCodePresent(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes the specified {@link Occupation}.
	 * @param occupation the occupation to delete.
	 * @return <code>true</code> if the occupation has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteOccupation(Occupation occupation) {
		try {
			return ioOperations.deleteOccupation(occupation);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
}
