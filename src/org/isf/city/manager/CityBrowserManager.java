package org.isf.city.manager;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.isf.city.model.City;
import org.isf.city.service.IoOperation;
import org.isf.utils.exception.OHException;

public class CityBrowserManager {
	private IoOperation ioOperations = new IoOperation();

	/**
	 * Returns all the available {@link Cities}s.
	 * @return a list of cities or <code>null</code> if the operation fails.
	 */
	public ArrayList<City> getCities() {
		try {
			return ioOperations.getCity();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns an {@link City} by it code.
	 * @return an city or <code>null</code> if there is no city for the provided code.
	 */
	public City getCity(String code) {
		try {
			return ioOperations.getCity(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Stores a new {@link City}.
	 * @param city the city to store.
	 * @return <code>true</code> if the city has been stored, <code>false</code> otherwise.
	 */
	public boolean newCity(City city) {
		try {
			return ioOperations.newCity(city);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Updates the specified {@link City}.
	 * @param city the city to update.
	 * @return <code>true</code> if the city has been updated, <code>false</code> otherwise.
	 */
	public boolean updateCity(City city) {
		try {
			return ioOperations.updateCity(city);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Checks if the specified Code is already used by others {@link City}s.
	 * @param code the city code to check.
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
	 * Deletes the specified {@link City}.
	 * @param city the city to delete.
	 * @return <code>true</code> if the city has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteCity(City city) {
		try {
			return ioOperations.deleteCity(city);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

}
