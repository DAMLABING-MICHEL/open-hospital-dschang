package org.isf.city.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.isf.city.model.City;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

public class IoOperation {


	/**
	 * Returns all the available {@link Cities}s.
	 * @return a list of cities.
	 * @throws OHException if an error occurs.
	 */
	public ArrayList<City> getCity() throws OHException {
		ArrayList<City> city = null;
		String query = "select * from CITY order by CITY_LIBELLE";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getData(query,true);
			city = new ArrayList<City>(resultSet.getFetchSize());
			while (resultSet.next()) {
				city.add(new City(resultSet.getString("CITY_LIBELLE")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return city;
	}
	
	/**
	 * Returns and {@link City} by its libelle.
	 * @return an city.
	 * @throws OHException if an error occurs.
	 */
	public City getCity(String libelle) throws OHException {
		City city = null;
		String query = "select * from CITY WHERE CITY_LIBELLE = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> params = Collections.<Object> singletonList(libelle);
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if (resultSet.next()) {
				city=new City(resultSet.getString("CITY_LIBELLE"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return city;
	}

	/**
	 * Updates the specified {@link City}.
	 * @param city the city to update.
	 * @return <code>true</code> if the city has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	public boolean updateCity(City city) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(city.getLibelle());
			parameters.add(city.getLibelle());
			String query = "update CITY set CITY_LIBELLE=? where CITY_LIBELLE= ?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Stores a new {@link City}.
	 * @param city the city to store.
	 * @return <code>true</code> if the city has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the storing operation.
	 */
	public boolean newCity(City city) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(1);
			parameters.add(city.getLibelle());
			String query = "insert into CITY (CITY_LIBELLE) values (?)";				
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Deletes the specified {@link City}.
	 * @param city the city to delete.
	 * @return <code>true</code> if the city has been deleted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the delete operation.
	 */
	public boolean deleteCity(City city) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(city.getLibelle());
		String query = "delete from CITY where CITY_LIBELLE = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Checks if the specified Code is already used by others {@link CITIES}s.
	 * @param code the city code to check.
	 * @return <code>true</code> if the code is already used, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean isCodePresent(String libelle) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present=false;
		try{
			List<Object> parameters = Collections.<Object>singletonList(libelle);
			String query = "SELECT CITY_LIBELLE FROM CITY where CITY_LIBELLE = ?";
			ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
			present = set.first();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return present;
	}
}
