package org.isf.occupation.service;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.isf.occupation.model.Occupation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

public class IoOperation {


	/**
	 * Returns all the available {@link Occupations}s.
	 * @return a list of occupations.
	 * @throws OHException if an error occurs.
	 */
	public ArrayList<Occupation> getOccupation() throws OHException {
		ArrayList<Occupation> occupation = null;
		String query = "select * from OCCUPATION order by OCC_LIBELLE";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getData(query,true);
			occupation = new ArrayList<Occupation>(resultSet.getFetchSize());
			while (resultSet.next()) {
				occupation.add(new Occupation(resultSet.getString("OCC_LIBELLE")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return occupation;
	}
	
	/**
	 * Returns and {@link Occupation} by its code.
	 * @return an occupation.
	 * @throws OHException if an error occurs.
	 */
	public Occupation getOccupation(String libelle) throws OHException {
		Occupation occupation = null;
		String query = "select * from OCCUPATION WHERE OCC_LIBELLE = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> params = Collections.<Object> singletonList(libelle);
		try{
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if (resultSet.next()) {
				occupation=new Occupation(resultSet.getString("OCC_LIBELLE"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return occupation;
	}

	/**
	 * Updates the specified {@link Occupation}.
	 * @param occupation the occupation to update.
	 * @return <code>true</code> if the occupation has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the update.
	 */
	public boolean updateOccupation(Occupation occupation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(occupation.getLibelle());
			parameters.add(occupation.getLibelle());
			String query = "update OCCUPATION set OCC_LIBELLE=? where OCC_LIBELLE= ?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Stores a new {@link Occupation}.
	 * @param occupation the occupation to store.
	 * @return <code>true</code> if the occupation has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the storing operation.
	 */
	public boolean newOccupation(Occupation occupation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(1); 
			parameters.add(occupation.getLibelle());
			String query = "insert into OCCUPATION (OCC_LIBELLE) values (?)";				
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Deletes the specified {@link Occupation}.
	 * @param occupation the occupation to delete.
	 * @return <code>true</code> if the occupation has been deleted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the delete operation.
	 */
	public boolean deleteOccupation(Occupation occupation) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(occupation.getLibelle());
		String query = "delete from OCCUPATION where OCC_LIBELLE = ?";
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
	 * Checks if the specified Code is already used by others {@link OCCUPATIONS}s.
	 * @param code the occupation code to check.
	 * @return <code>true</code> if the code is already used, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean isCodePresent(String libelle) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present=false;
		try{
			List<Object> parameters = Collections.<Object>singletonList(libelle);
			String query = "SELECT OCC_LIBELLE FROM OCCUPATION where OCC_LIBELLE = ?";
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
