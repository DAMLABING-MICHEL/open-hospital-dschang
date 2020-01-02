package org.isf.mortuary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.mortuary.model.DeathReason;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

/**
 * Persistence class for the Death Reason Types.
 */

public class DeathReasonIoOperations {
	/**
	 * Returns all the stored {@link DeathReason}s.
	 * @return a list of death reason type.
	 * @throws OHException if an error occurs retrieving the Death reasons list.
	 */
	public ArrayList<DeathReason> getDeathReasons() throws OHException {
		ArrayList<DeathReason> deathReasons = null;
		String query = "select * from MOTIF_DECES order by MOTIF_CODE";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try{
			ResultSet resultSet = dbQuery.getData(query,true);
			deathReasons = new ArrayList<DeathReason>(resultSet.getFetchSize());
		
			while (resultSet.next()) {
				deathReasons.add(new DeathReason(resultSet.getInt("MOTIF_ID"), resultSet.getString("MOTIF_CODE"), resultSet.getString("MOTIF_DESCRIPTION")));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return deathReasons;
	}

	/**
	 * Updates the specified {@link DeathReason}.
	 * @param deathReason the death reason type to update.
	 * @return <code>true</code> if the death reason type has been updated, false otherwise.
	 * @throws OHException if an error occurs during the update operation.
	 */
	public boolean updateDeathReason(DeathReason deathReason) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(deathReason.getCode());
			parameters.add(deathReason.getDescription());
			
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			
			parameters.add(deathReason.getId());
			String query = "update MOTIF_DECES set MOTIF_CODE=?, "
					+ "MOTIF_DESCRIPTION=?, "
					+ "MOTIF_MODIFY_BY=?, "
					+ "MOTIF_MODIFY_DATE=? where MOTIF_ID=?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Store the specified {@link DeathReason}.
	 * @param deathReason the death reason to store.
	 * @return <code>true</code> if the {@link DeathReason} has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public boolean newDeathReason(DeathReason deathReason) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(deathReason.getCode());
			parameters.add(deathReason.getDescription());
			
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			
			String query = "insert into MOTIF_DECES (MOTIF_CODE, MOTIF_DESCRIPTION, MOTIF_CREATE_BY, MOTIF_CREATE_DATE) values (?, ?, ?, ?)";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * Deletes the specified {@link DeathReason}.
	 * @param code the code of the  death reason to remove.
	 * @return <code>true</code> if the death reason has been removed, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the delete procedure.
	 */
	public boolean deleteDeathReason(String code) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			List<Object> parameters = Collections.<Object>singletonList(code);
			String query = "delete from MOTIF_DECES where MOTIF_CODE = ?";
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Checks if the specified code is already used by any {@link DeathReason}.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, false otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present=false;
		try{
			List<Object> parameters = Collections.<Object>singletonList(code);
			String query = "SELECT MOTIF_CODE FROM MOTIF_DECES where MOTIF_CODE = ?";
			ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
			present = set.first();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return present;
	}
	
	/**
	 * Get the DeathReason by id.
	 * @param id the id of DeathReason to search.
	 * @return <code>DeathReason</code>.
	 * @throws OHException if an error occurs during the check.
	 */
	public DeathReason getDeathReasonById(int id) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		DeathReason reason= null;
		try{
			List<Object> parameters = Collections.<Object>singletonList(id);
			String query = "SELECT * FROM MOTIF_DECES where MOTIF_ID = ?";
			ResultSet res = dbQuery.getDataWithParams(query, parameters, true);
			while(res.next()) {
				reason = new DeathReason(res.getInt("MOTIF_ID"), res.getString("MOTIF_CODE"), res.getString("MOTIF_DESCRIPTION"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return reason;
	}

}
