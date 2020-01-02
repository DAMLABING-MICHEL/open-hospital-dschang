package org.isf.mortuary.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.service.DeathReasonIoOperations;
import org.isf.utils.exception.OHException;

public class DeathReasonBrowserManager {
	
	private DeathReasonIoOperations ioOperations = new DeathReasonIoOperations();

	/**
	 * Returns all the stored {@link DeathReason}s.
	 * @return a list of death reason, <code>null</code> if the operation is failed.
	 */
	public ArrayList<DeathReason> getDeathReasons() {
		try {
			return ioOperations.getDeathReasons();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Store the specified {@link DeathReason}.
	 * @param deathReason the death reason to store.
	 * @return <code>true</code> if the {@link DeathReason} has been stored, <code>false</code> otherwise.
	 */
	public boolean newDeathReason(DeathReason deathReason) {
		try {
			return ioOperations.newDeathReason(deathReason);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the specified {@link DeathReason}.
	 * @param deathReason the death reason to update.
	 * @return <code>true</code> if the death reason has been updated, false otherwise.
	 */
	public boolean updateDeathReason(DeathReason deathReason) {
		try {
			return ioOperations.updateDeathReason(deathReason);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if the specified code is already used by any {@link DeathReason}.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, false otherwise.
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
	 * Deletes the specified {@link DeathReason}.
	 * @param deathReason the death reason to remove.
	 * @return <code>true</code> if the death reason has been removed, <code>false</code> otherwise.
	 */
	public boolean deleteDeathReason(DeathReason deathReason) {
		try {
			return ioOperations.deleteDeathReason(deathReason.getCode());
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Deletes the specified {@link DeathReason}.
	 * @param code the code of the death reason to remove.
	 * @return <code>true</code> if the death reason has been removed, <code>false</code> otherwise.
	 */
	public boolean deleteDeathReason(String code) {
		try {
			return ioOperations.deleteDeathReason(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Get the DeathReason by id.
	 * @param id the id to find.
	 * @return <code>DeathReason</code>.
	 */
	public DeathReason getDeathReason(int id) {
		try {
			return ioOperations.getDeathReasonById(id);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
}
