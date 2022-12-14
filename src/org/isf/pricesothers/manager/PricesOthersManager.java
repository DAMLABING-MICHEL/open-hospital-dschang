package org.isf.pricesothers.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.generaldata.MessageBundle;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.IoOperations;
import org.isf.utils.exception.OHException;

public class PricesOthersManager {

	private IoOperations ioOperations = new IoOperations();

	/**
	 * return the list of {@link PriceOthers}s in the DB
	 * 
	 * @return the list of {@link PriceOthers}s
	 */
	public ArrayList<PricesOthers> getOthers() {
		try {
			return ioOperations.getOthers();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	/**
	 * return the  {@link PriceOthers} that matches the given ID
	 * 
	 * @param priceOtherID the id
	 * @return the {@link PriceOthers} or null 
	 */
	public PricesOthers getOther(int priceOtherID) {
		try {
			return ioOperations.getOther(priceOtherID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	 public PricesOthers getOtherByCode(String priceOtherCode) {
			try {
				return ioOperations.getOtherByCode(priceOtherCode);
			} catch (OHException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				return null;
			}
		}

	/**
	 * insert a new {@link PriceOthers} in the DB
	 * 
	 * @param other - the {@link PriceOthers} to insert
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 */
	public boolean newOther(PricesOthers other) {
		try {
			return ioOperations.newOthers(other);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * delete a {@link PriceOthers} in the DB
	 * 
	 * @param other - the {@link PriceOthers} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 */
	public boolean deleteOther(PricesOthers other) {
		if (other.getId() == 1) {
			JOptionPane.showMessageDialog(null,	MessageBundle.getMessage("angal.sql.operationnotpermittedprotectedelement"));
			return false;
		} else {
			try {
				return ioOperations.deleteOthers(other);
			} catch (OHException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				return false;
			}
		}
	}
	
	public boolean deleteOther(int otherID) {
		if (otherID == 1) {			
			return false;
		} else {
			try {
				return ioOperations.deleteOthers(otherID);
			} catch (OHException e) {				
				return false;
			}
		}
	}
	public boolean deleteOther(String code) {
		
			try {
				return ioOperations.deleteOthers(code);
			} catch (OHException e) {				
				return false;
			}
	}
	/**
	 * update a {@link PriceOthers} in the DB
	 * 
	 * @param other - the {@link PriceOthers} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 */
	public boolean updateOther(PricesOthers other) {
		try {
			return ioOperations.updateOther(other);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	/**
	 * update a {@link PriceOthers} in the DB
	 * 
	 * @param other - the {@link PriceOthers} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 */
	public boolean updateOtherByCode(PricesOthers other) {
		try {
			return ioOperations.updateOtherByCode(other);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
}