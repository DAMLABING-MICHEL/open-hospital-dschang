package org.isf.disctype.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.isf.disctype.model.DischargeType;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;

public class IoOperation {

	/**
	 * method that returns all DischargeTypes in a list
	 * 
	 * @return the list of all DischargeTypes
	 * @throws OHException
	 */
	public ArrayList<DischargeType> getDischargeType() throws OHException {
		ArrayList<DischargeType> pdischargetype = null;
		String string = "SELECT * FROM DISCHARGETYPE ORDER BY DIST_DESC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(string, true);
			pdischargetype = new ArrayList<DischargeType>(resultSet.getFetchSize());
			while (resultSet.next()) {
				pdischargetype.add(new DischargeType(resultSet.getString("DIST_ID_A"), resultSet.getString("DIST_DESC")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pdischargetype;
	}
	/**
	 * method that returns a discharge type identified by the given code
	 * @param code the code of the discharge type
	 * @return the DischargeType identified by the given code
	 * @throws OHException
	 */
	public DischargeType getDischargeType(String code) throws OHException {
		DischargeType dischargetype = null;
		String string = "SELECT * FROM DISCHARGETYPE WHERE DIST_ID_A = ?";
		List<Object> params = Collections.<Object> singletonList(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(string, params, true);
			while (resultSet.next()) {
				dischargetype=new DischargeType(resultSet.getString("DIST_ID_A"), resultSet.getString("DIST_DESC"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return dischargetype;
	}

	/**
	 * method that updates an already existing DischargeType
	 * 
	 * @param dischargeType
	 * @return true - if the existing DischargeType has been updated
	 * @throws OHException
	 */
	public boolean UpdateDischargeType(DischargeType dischargeType) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "UPDATE DISCHARGETYPE SET DIST_DESC = ? WHERE DIST_ID_A = ?";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(dischargeType.getDescription());
			params.add(dischargeType.getCode());

			result = dbQuery.setDataWithParams(query, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * method that create a new DischargeType
	 * 
	 * @param dischargeType
	 * @return true - if the new DischargeType has been inserted
	 * @throws OHException
	 */
	public boolean newDischargeType(DischargeType dischargeType) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "INSERT INTO DISCHARGETYPE (DIST_ID_A,DIST_DESC) VALUES (?, ?)";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(dischargeType.getCode());
			params.add(dischargeType.getDescription());

			result = dbQuery.setDataWithParams(query, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * method that delete a DischargeType
	 * 
	 * @param dischargeType
	 * @return true - if the DischargeType has been deleted
	 * @throws OHException
	 */
	public boolean deleteDischargeType(DischargeType dischargeType) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "DELETE FROM DISCHARGETYPE WHERE DIST_ID_A = ?";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(dischargeType.getCode());

			result = dbQuery.setDataWithParams(query, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * method that check if a DischargeType already exists
	 * 
	 * @param code
	 * @return true - if the DischargeType already exists
	 * @throws OHException 
	 */
	public boolean isCodePresent(String code) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present = false;
		try {
			String query = "SELECT DIST_ID_A FROM DISCHARGETYPE WHERE DIST_ID_A = ?";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(code);
			
			ResultSet set = dbQuery.getDataWithParams(query, params, true);
			if (set.first()) {
				present = true;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return present;
	}
}
