package org.isf.parameters.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.model.Parameter;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

public class IoOperation {
	
	/**
	 * Return the list of {@link Parameter}s.
	 * @return the list of {@link Parameter}s.
	 * @throws OHException
	 */
	public ArrayList<Parameter> getParameters() throws OHException {
		ArrayList<Parameter> parameters = null;
		//String string = "SELECT * FROM parameters WHERE PRMS_DELETED is null or PRMS_DELETED <> ?  ORDER BY PRMS_CODE ASC";
		String string = "SELECT * FROM parameters WHERE PRMS_DELETED is null or PRMS_DELETED <> ?  ";
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add("D");
			
			ResultSet resultSet = dbQuery.getDataWithParams(string,params, true);
			parameters = new ArrayList<Parameter>(resultSet.getFetchSize());
			while (resultSet.next()) {
				parameters.add(new Parameter(
						resultSet.getInt("PRMS_ID"),
						resultSet.getString("PRMS_CODE"),
						resultSet.getString("PRMS_DESCRIPTION"),
						resultSet.getString("PRMS_VALUE"),
						resultSet.getString("PRMS_DEFAULT_VALUE"),						
						resultSet.getString("PRMS_DELETED")
						));
			}
			
		} catch (SQLException e) {
			
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return parameters;
	}
	
	/**
	 * Return the list of {@link Parameter}s by scope.
	 * @return the list of {@link Parameter}s.
	 * @throws OHException
	 */
	public ArrayList<Parameter> getParameters(String type) throws OHException {
		ArrayList<Parameter> parameters = null;
		//String string = "SELECT * FROM parameters WHERE PRMS_DELETED is null or PRMS_DELETED <> ?  ORDER BY PRMS_CODE ASC";
		StringBuilder query = new StringBuilder("SELECT * FROM parameters WHERE (PRMS_DELETED is null or PRMS_DELETED <> ?)  ");
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("D");
		if(type!=null){
			query.append(" and PRMS_SCOPE = ? ");
			params.add(type);
		}
		//query.append(" ORDER BY PRMS_CODE ASC ");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			
			
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(),params, true);
			parameters = new ArrayList<Parameter>(resultSet.getFetchSize());
			while (resultSet.next()) {
				parameters.add(new Parameter(
						resultSet.getInt("PRMS_ID"),
						resultSet.getString("PRMS_CODE"),
						resultSet.getString("PRMS_DESCRIPTION"),
						resultSet.getString("PRMS_VALUE"),
						resultSet.getString("PRMS_DEFAULT_VALUE"),						
						resultSet.getString("PRMS_DELETED")
						));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return parameters;
	}
	
	
	/**
	 * Update an already existing {@link Parameter}.
	 * @param Param - the {@link Parameter} to update
	 * @return <code>true</code> if the Parameter has been updated, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean updateParameter(Parameter parameter) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "UPDATE parameters SET "
					+ " PRMS_DESCRIPTION = ?,"
					+ " PRMS_VALUE = ?,"
					+ " PRMS_DEFAULT_VALUE = ? ,"					
					+ " PRMS_MODIFY_BY = ? ,"
					+ " PRMS_MODIFY_DATE = ? "					
					+ " WHERE 	PRMS_ID = ?";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(parameter.getDescription());
			params.add(parameter.getValue());
			params.add(parameter.getDefault_value());			
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			params.add(parameter.getId());
			result = dbQuery.setDataWithParams(query, params, true);
			
		} finally {
			dbQuery.releaseConnection();
		}		
		return result;
	}
	
	/**
	 * Insert a new {@link Parameter} in the DB.
	 * @param medicalinventory - the {@link Parameter} to insert.
	 * @return <code>true</code> if the Parameter has been inserted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean newParameter(Parameter parameter) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "INSERT INTO parameters ("
					+ " PRMS_CODE, "
					+ " PRMS_DESCRIPTION, "
					+ " PRMS_VALUE, "
					+ " PRMS_DEFAULT_VALUE,"
					
					+ " PRMS_CREATE_BY,"
					+ " PRMS_CREATE_DATE) "					
					+ " VALUES (?, ?, ?, ?, ?, ?)";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(parameter.getCode());
			params.add(parameter.getDescription());
			params.add(parameter.getValue());	
			params.add(parameter.getDefault_value());						
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			result = dbQuery.setDataWithParams(query, params, true);			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	public boolean isKeyPresent(String code) throws OHException {
		String query = "SELECT * FROM parameters WHERE PRMS_CODE = ?";
		List<Object> params = Collections.<Object> singletonList(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		ResultSet result;
		try {
			result = dbQuery.getDataWithParams(query, params, true);
			if (result.first()) {
				return true;
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return false;
	}
	
	
	
	/**
	 * Delete the passed {@link Parameter}.
	 * @param Param - the {@link Parameter} to delete.
	 * @return <code>true</code> if the Parameter has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteParameter(int code) throws OHException {
		String query1 = "update parameters set PRMS_DELETED = ?,  "
				+ " PRMS_DELETED_DATE=? , "
				+ " PRMS_DELETED_BY =? "
				+ " WHERE PRMS_ID = ?";		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("D");
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(MainMenu.getUser());
		params.add(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query1, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * get the passed {@link Parameter}.
	 * @param Param - the {@link Parameter} to delete.
	 * @return <code>Parameter</code> if the Parameter has been retrieved, <code>null</code> otherwise.
	 * @throws OHException
	 */
	public Parameter getParameter(String parameterCode) throws OHException {
		String string = "select * from parameters where  PRMS_CODE = ?";		
		DbQueryLogger dbQuery = new DbQueryLogger();
		Parameter parameter = null;
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(parameterCode);
		try {			
			ResultSet resultSet = dbQuery.getDataWithParams(string,params, true);			
			while (resultSet.next()) {
				parameter = new Parameter(
						resultSet.getInt("PRMS_ID"),
						resultSet.getString("PRMS_CODE"),
						resultSet.getString("PRMS_DESCRIPTION"),
						resultSet.getString("PRMS_VALUE"),
						resultSet.getString("PRMS_DEFAULT_VALUE"),						
						resultSet.getString("PRMS_DELETED")
						);
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return parameter;
	}
	
	protected GregorianCalendar toCalendar(Date date){
		if (date == null) return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}
	
}
