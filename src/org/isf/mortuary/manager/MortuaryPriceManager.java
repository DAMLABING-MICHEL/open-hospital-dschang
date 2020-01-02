package org.isf.mortuary.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.PlagePrixMorgue;
import org.isf.mortuary.service.PriceIoOperations;
import org.isf.utils.exception.OHException;

public class MortuaryPriceManager {
	
	private PriceIoOperations ioOperations = new PriceIoOperations();

	/**
	 * Returns all the stored {@link DeathReason}s.
	 * @return a list of death reason, <code>null</code> if the operation is failed.
	 */
	public ArrayList<PlagePrixMorgue> getPrices() {
		try {
			return ioOperations.getPrices();
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
	public boolean newPrice(PlagePrixMorgue plage) {
		try {
			return ioOperations.newPrice(plage);
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
	public boolean updatePrice(PlagePrixMorgue plage) {
		try {
			return ioOperations.updatePrice(plage);
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
	public boolean isPriceRangeCoherent(int id, int min, int max) {
		try {
			return ioOperations.isPriceRangeCoherent(id, min, max);
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
	public boolean deletePrice(PlagePrixMorgue plage) {
		try {
			return ioOperations.deletePrice(plage.getId());
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
	public boolean deletePrice(int id) {
		try {
			return ioOperations.deletePrice(id);
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
	public PlagePrixMorgue getPrice(int id) {
		try {
			return ioOperations.getPriceById(id);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
}
