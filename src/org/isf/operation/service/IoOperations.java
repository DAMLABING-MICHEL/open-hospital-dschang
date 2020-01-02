package org.isf.operation.service;

/*----------------------------------------------------------
 * modification history
 * ====================
 * 13/02/09 - Alex - modified query for ordering resultset
 *                   by description only
 * 13/02/09 - Alex - added Major/Minor control
 -----------------------------------------------------------*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.opetype.model.OperationType;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

/**
 * This class offers the io operations for recovering and managing operations
 * records from the database
 * 
 * @author Rick, Vero, pupo
 */
public class IoOperations {

	/**
	 * return the {@link Operation}s whose type matches specified string
	 * 
	 * @param typeDescription
	 *            - a type description
	 * @return the list of {@link Operation}s. It could be <code>empty</code> or
	 *         <code>null</code>.
	 * @throws OHException
	 */
	public ArrayList<Operation> getOperation(String typeDescription) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Operation> operationList = null;
		ResultSet resultSet;

		if (typeDescription == null) {
			String sqlString = "SELECT * FROM OPERATION JOIN OPERATIONTYPE ON OPE_OCL_ID_A = OCL_ID_A ORDER BY OPE_DESC";
			resultSet = dbQuery.getData(sqlString, true);
		} else {
			String sqlString = "SELECT * FROM OPERATION JOIN OPERATIONTYPE ON OPE_OCL_ID_A = OCL_ID_A WHERE OCL_DESC LIKE CONCAT('%', ? , '%') ORDER BY OPE_DESC";
			List<Object> parameters = Collections.<Object>singletonList(typeDescription);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}
		try {
			operationList = new ArrayList<Operation>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Operation operation = new Operation(resultSet.getString("OPE_ID_A"), resultSet.getString("OPE_DESC"),
						new OperationType(resultSet.getString("OPE_OCL_ID_A"), resultSet.getString("OCL_DESC")),
						resultSet.getInt("OPE_STAT"), resultSet.getInt("OPE_LOCK"));
				operationList.add(operation);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operationList;
	}

	public ArrayList<OperationRow> getOperationRow() throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<OperationRow> operationList = null;
		ResultSet resultSet;
		String sqlString = "SELECT * FROM OPERATIONROW ORDER BY OPER_OPDATE DESC";
		resultSet = dbQuery.getData(sqlString, true);

		DateFormat df = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
		Date date = new Date();
		GregorianCalendar dateop = new GregorianCalendar();

		try {
			operationList = new ArrayList<OperationRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				date = resultSet.getDate("OPER_OPDATE");
				dateop = new GregorianCalendar();
				dateop.setTime(date);
				OperationRow operation = new OperationRow(resultSet.getInt("OPER_ID_A"), resultSet.getString("OPER_ID"),
						resultSet.getString("OPER_PRESCRIBER"), resultSet.getString("OPER_RESULT"), dateop,
						resultSet.getString("OPER_REMARKS"), resultSet.getInt("OPER_ADMISSION_ID"),
						resultSet.getInt("OPER_OPD_ID"), resultSet.getInt("OPER_BILL_ID"),
						resultSet.getFloat("OPER_TRANS_UNIT"));
				operationList.add(operation);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operationList;
	}

	/**
	 * return the {@link Operation} whose code matches specified string
	 * 
	 * @param code
	 *            - a operation code
	 * @return the {@link Operation}. that matches the given code.
	 * @throws OHException
	 */
	public Operation getOperationByCode(String code) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		Operation operation = null;
		ResultSet resultSet;

		if (code == null) {
			dbQuery.releaseConnection();
			return null;
		} else {
			String sqlString = "SELECT * FROM OPERATION JOIN OPERATIONTYPE ON OPE_OCL_ID_A = OCL_ID_A WHERE OPE_ID_A =  ? ";
			List<Object> parameters = Collections.<Object>singletonList(code);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}
		try {

			if (resultSet.next()) {
				operation = new Operation(resultSet.getString("OPE_ID_A"), resultSet.getString("OPE_DESC"),
						new OperationType(resultSet.getString("OPE_OCL_ID_A"), resultSet.getString("OCL_DESC")
								,resultSet.getString("OCL_ACCOUNT")),
						resultSet.getInt("OPE_STAT"), resultSet.getInt("OPE_LOCK"));
				///,resultSet.getString("OCL_EXPENSE_ACCOUNT")
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operation;
	}

	public OperationRow getOperationRowById(String id) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		OperationRow operation = null;
		ResultSet resultSet;

		if (id == null) {
			dbQuery.releaseConnection();
			return null;
		} else {
			String sqlString = "SELECT * FROM OPERATIONROW WHERE OPER_ID =  ? ";
			List<Object> parameters = Collections.<Object>singletonList(id);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}

		DateFormat df = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
		Date date = new Date();
		GregorianCalendar dateop = new GregorianCalendar();
		dateop.setTime(date);
		try {

			if (resultSet.next()) {
				date = resultSet.getDate("OPER_OPDATE");
				dateop = new GregorianCalendar();
				dateop.setTime(date);
				operation = new OperationRow(resultSet.getInt("OPER_ID_A"), resultSet.getString("OPER_ID"),
						resultSet.getString("OPER_PRESCRIBER"), resultSet.getString("OPER_RESULT"), dateop,
						resultSet.getString("OPER_REMARKS"), resultSet.getInt("OPER_ADMISSION_ID"),
						resultSet.getInt("OPER_OPD_ID"), resultSet.getInt("OPER_BILL_ID"),
						resultSet.getFloat("OPER_TRANS_UNIT"));

			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operation;
	}

	public ArrayList<OperationRow> getOperationRowByPatient(String idPatient) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		OperationRow operation = null;
		ResultSet resultSet;
		ArrayList<OperationRow> operationList = null;

		if (idPatient == null) {
			dbQuery.releaseConnection();
			return null;
		} else {
			String sqlString = "SELECT * FROM OPERATIONROW o"
					+ " LEFT JOIN ADMISSION a ON (o.OPER_ADMISSION_ID = a.ADM_ID) "
					+ " LEFT JOIN OPD d ON (o.OPER_OPD_ID = d.OPD_ID) "
					+ " WHERE a.ADM_PAT_ID = ?  OR d.OPD_PAT_ID = ? ";
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(idPatient);
			parameters.add(idPatient);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}

		DateFormat df = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
		Date date = new Date();
		GregorianCalendar dateop = new GregorianCalendar();
		dateop.setTime(date);
		try {
			operationList = new ArrayList<OperationRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				date = resultSet.getDate("OPER_OPDATE");
				dateop = new GregorianCalendar();
				dateop.setTime(date);
				operation = new OperationRow(resultSet.getInt("OPER_ID_A"), resultSet.getString("OPER_ID"),
						resultSet.getString("OPER_PRESCRIBER"), resultSet.getString("OPER_RESULT"), dateop,
						resultSet.getString("OPER_REMARKS"), resultSet.getInt("OPER_ADMISSION_ID"),
						resultSet.getInt("OPER_OPD_ID"), resultSet.getInt("OPER_BILL_ID"),
						resultSet.getFloat("OPER_TRANS_UNIT"));
				operationList.add(operation);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operationList;
	}

	public List<OperationRow> getOperationByIdAdmission(String idadmission) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<OperationRow> operationList = null;
		ResultSet resultSet;

		if (idadmission == null) {
			dbQuery.releaseConnection();
			return null;
		} else {
			String sqlString = "SELECT * FROM OPERATIONROW WHERE OPER_ADMISSION_ID =  ? ";
			List<Object> parameters = Collections.<Object>singletonList(idadmission);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}

		DateFormat df = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
		Date date = new Date();
		GregorianCalendar dateop = new GregorianCalendar();
		dateop.setTime(date);
		try {
			operationList = new ArrayList<OperationRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				date = resultSet.getDate("OPER_OPDATE");
				dateop = new GregorianCalendar();
				dateop.setTime(date);

				OperationRow operation = new OperationRow(resultSet.getInt("OPER_ID_A"), resultSet.getString("OPER_ID"),
						resultSet.getString("OPER_PRESCRIBER"), resultSet.getString("OPER_RESULT"), dateop,
						resultSet.getString("OPER_REMARKS"), resultSet.getInt("OPER_ADMISSION_ID"),
						resultSet.getInt("OPER_OPD_ID"), resultSet.getInt("OPER_BILL_ID"),
						resultSet.getFloat("OPER_TRANS_UNIT"));
				operationList.add(operation);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operationList;
	}

	public List<OperationRow> getOperationByIdOpd(String idopd) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<OperationRow> operationList = null;
		ResultSet resultSet;

		if (idopd == null) {
			dbQuery.releaseConnection();
			return null;
		} else {
			String sqlString = "SELECT * FROM OPERATIONROW WHERE OPER_OPD_ID =  ? ";
			List<Object> parameters = Collections.<Object>singletonList(idopd);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}

		// DateFormat df = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
		Date date = new Date();
		GregorianCalendar dateop = new GregorianCalendar();
		dateop.setTime(date);
		try {

			operationList = new ArrayList<OperationRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				date = resultSet.getDate("OPER_OPDATE");
				dateop = new GregorianCalendar();
				dateop.setTime(date);

				OperationRow operation = new OperationRow(resultSet.getInt("OPER_ID_A"), resultSet.getString("OPER_ID"),
						resultSet.getString("OPER_PRESCRIBER"), resultSet.getString("OPER_RESULT"), dateop,
						resultSet.getString("OPER_REMARKS"), resultSet.getInt("OPER_ADMISSION_ID"),
						resultSet.getInt("OPER_OPD_ID"), resultSet.getInt("OPER_BILL_ID"),
						resultSet.getFloat("OPER_TRANS_UNIT"));
				operationList.add(operation);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}

		finally {
			dbQuery.releaseConnection();
		}
		return operationList;
	}

	public List<OperationRow> getOperationWithoutBill(String idPatient) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<OperationRow> operationList = null;
		ResultSet resultSet;
        
		if (idPatient == null) {
			dbQuery.releaseConnection();
			return null;
		}
		else{
			//String sqlString = "SELECT * FROM OPERATIONROW WHERE OPER_BILL_ID IS NULL OR OPER_BILL_ID <= 0 ";
			String sqlString = "SELECT * FROM OPERATIONROW o"
					+ " LEFT JOIN ADMISSION a ON (o.OPER_ADMISSION_ID = a.ADM_ID) "
					+ " LEFT JOIN OPD d ON (o.OPER_OPD_ID = d.OPD_ID) "
					+ " WHERE (a.ADM_PAT_ID = ?  OR d.OPD_PAT_ID = ?)  AND (o.OPER_BILL_ID IS NULL OR o.OPER_BILL_ID <= 0)";
			
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(idPatient);
			parameters.add(idPatient);
			resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
		}
		
		//resultSet = dbQuery.getData(sqlString1, true);

		// DateFormat df = new SimpleDateFormat("yyyy MM dd hh:mm:ss");
		Date date = new Date();
		GregorianCalendar dateop = new GregorianCalendar();
		dateop.setTime(date);
		try {

			operationList = new ArrayList<OperationRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				date = resultSet.getDate("OPER_OPDATE");
				dateop = new GregorianCalendar();
				dateop.setTime(date);

				OperationRow operation = new OperationRow(resultSet.getInt("OPER_ID_A"), resultSet.getString("OPER_ID"),
						resultSet.getString("OPER_PRESCRIBER"), resultSet.getString("OPER_RESULT"), dateop,
						resultSet.getString("OPER_REMARKS"), resultSet.getInt("OPER_ADMISSION_ID"),
						resultSet.getInt("OPER_OPD_ID"), resultSet.getInt("OPER_BILL_ID"),
						resultSet.getFloat("OPER_TRANS_UNIT"));
				operationList.add(operation);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}

		finally {
			dbQuery.releaseConnection();
		}
		return operationList;
	}

	/**
	 * insert an {@link Operation} in the DB
	 * 
	 * @param operation
	 *            - the {@link Operation} to insert
	 * @return <code>true</code> if the operation has been inserted,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean newOperation(Operation operation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String sqlString = "INSERT INTO OPERATION (OPE_ID_A, OPE_OCL_ID_A, OPE_DESC, OPE_STAT) VALUES (?, ?, ?, ?)";
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(operation.getCode());
		parameters.add(operation.getType().getCode());
		parameters.add(operation.getDescription());
		parameters.add(operation.getMajor());

		return dbQuery.setDataWithParams(sqlString, parameters, true);
	}

	public boolean newOperationRow(OperationRow operation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String sqlString = "INSERT INTO OPERATIONROW ( OPER_ID,OPER_PRESCRIBER,OPER_RESULT, OPER_OPDATE, OPER_REMARKS, OPER_ADMISSION_ID,OPER_OPD_ID, OPER_BILL_ID,OPER_TRANS_UNIT"
				+ ",OPER_CREATE_BY, OPER_CREATE_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(operation.getOperationId());
		parameters.add(operation.getPrescriber());
		parameters.add(operation.getOpResult());
		parameters.add(toTimestamp(operation.getOpDate()));
		parameters.add(operation.getRemarks());
		parameters.add(operation.getAdmissionId());
		parameters.add(operation.getOpdId());
		parameters.add(operation.getBillId());
		parameters.add(operation.getTransUnit());
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		return dbQuery.setDataWithParams(sqlString, parameters, true);
	}

	/**
	 * Checks if the specified {@link Operation} has been modified.
	 * 
	 * @param operation
	 *            - the {@link Operation} to check.
	 * @return <code>true</code> if has been modified, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs during the check.
	 */
	public boolean hasOperationModified(Operation operation) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;

		// we establish if someone else has updated/deleted the record since the
		// last read
		String query = "SELECT OPE_LOCK FROM OPERATION WHERE OPE_ID_A = ?";
		List<Object> parameters = Collections.<Object>singletonList(operation.getCode());

		try {
			// we use manual commit of the transaction
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			if (resultSet.first()) {
				// ok the record is present, it was not deleted
				result = resultSet.getInt("OPE_LOCK") != operation.getLock();
			} else {
				throw new OHException(MessageBundle.getMessage("angal.sql.couldntfindthedataithasprobablybeendeleted"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * updates an {@link Operation} in the DB
	 * 
	 * @param operation
	 *            - the {@link Operation} to update
	 * @return <code>true</code> if the item has been updated,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean updateOperation(Operation operation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;

		try {
			String sqlString = "UPDATE OPERATION set " + " OPE_DESC = ?," + " OPE_LOCK = OPE_LOCK + 1, "
					+ " OPE_STAT = ?" + " WHERE OPE_ID_A = ?";

			List<Object> parameters = new ArrayList<Object>();
			parameters.add(operation.getDescription());
			parameters.add(operation.getMajor());
			parameters.add(operation.getCode());

			result = dbQuery.setDataWithParams(sqlString, parameters, true);
			if (result)
				operation.setLock(operation.getLock() + 1);

		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	public boolean updateOperationRow(OperationRow operation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String sqlString = "UPDATE OPERATIONROW set " + " OPER_ID = ?," + " OPER_PRESCRIBER = ?,"
					+ " OPER_RESULT = ?," + " OPER_OPDATE = ?," + " OPER_REMARKS = ?," + " OPER_ADMISSION_ID = ?,"
					+ " OPER_OPD_ID = ?," + " OPER_BILL_ID = ?, " + " OPER_TRANS_UNIT = ?, OPER_MODIFY_BY= ?, OPER_MODIFY_DATE = ? "
					// + " WHERE OPROW_ID = ?";
					+ " WHERE OPER_ID_A = ?";
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(operation.getOperationId());
			parameters.add(operation.getPrescriber());
			parameters.add(operation.getOpResult());
			parameters.add(toTimestamp(operation.getOpDate()));
			parameters.add(operation.getRemarks());
			parameters.add(operation.getAdmissionId());
			parameters.add(operation.getOpdId());
			parameters.add(operation.getBillId());
			parameters.add(operation.getTransUnit());
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(operation.getId());
			result = dbQuery.setDataWithParams(sqlString, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	public boolean updateBillIdOperationRow(int OperationRowId, int billId) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
//		try {
			String sqlString = "UPDATE OPERATIONROW set OPER_BILL_ID = ? , OPER_MODIFY_BY= ?, OPER_MODIFY_DATE = ?"
					+ " WHERE OPER_ID_A = ?";
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(billId);
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(OperationRowId);
			result = dbQuery.setDataWithParams(sqlString, parameters, false);
//		} finally {
//			dbQuery.releaseConnection();
//		}
		return result;
	}

	/**
	 * Delete a {@link Operation} in the DB
	 * 
	 * @param operation
	 *            - the {@link Operation} to delete
	 * @return <code>true</code> if the item has been updated,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteOperation(Operation operation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String sqlString = "DELETE FROM OPERATION WHERE OPE_ID_A = ?";
		List<Object> parameters = Collections.<Object>singletonList(operation.getCode());

		return dbQuery.setDataWithParams(sqlString, parameters, true);
	}

	public boolean deleteOperation(String operationID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String sqlString = "DELETE FROM OPERATION WHERE OPE_ID_A = ?";
		List<Object> parameters = Collections.<Object>singletonList(operationID);

		return dbQuery.setDataWithParams(sqlString, parameters, true);
	}

	public boolean deleteOperationRow(OperationRow operation) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String sqlString = "DELETE FROM OPERATIONROW WHERE OPER_ID = ? LIMIT 1";
		List<Object> parameters = Collections.<Object>singletonList(operation.getOperationId());

		return dbQuery.setDataWithParams(sqlString, parameters, true);
	}

	public boolean deleteOperationRow(String operationID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String sqlString = "DELETE FROM OPERATIONROW WHERE OPER_ID = ? LIMIT 1";
		List<Object> parameters = Collections.<Object>singletonList(operationID);

		return dbQuery.setDataWithParams(sqlString, parameters, true);
	}

	/**
	 * checks if an {@link Operation} code has already been used
	 * 
	 * @param code
	 *            - the code
	 * @return <code>true</code> if the code is already in use,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean isCodePresent(String code) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present = false;
		try {
			String sqlstring = "SELECT OPE_ID_A FROM OPERATION WHERE OPE_ID_A = ? ";
			List<Object> parameters = Collections.<Object>singletonList(code);
			ResultSet set = dbQuery.getDataWithParams(sqlstring, parameters, true);
			if (set.first())
				present = true;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return present;
	}

	/**
	 * checks if an {@link Operation} description has already been used within
	 * the specified {@link OperationType}
	 * 
	 * @param description
	 *            - the {@link Operation} description
	 * @param typeCode
	 *            - the {@link OperationType} code
	 * @return <code>true</code> if the description is already in use,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean isDescriptionPresent(String description, String typeCode) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean present = false;
		try {
			String sqlstring = "SELECT OPE_DESC FROM OPERATION WHERE OPE_DESC = ? AND OPE_OCL_ID_A = ?";
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(description);
			parameters.add(typeCode);
			ResultSet set = dbQuery.getDataWithParams(sqlstring, parameters, true);
			if (set.first())
				present = true;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return present;
	}
	
	public HashMap<String, Integer> getCountMinorMajorOperation(int year,int minorOpeCode,int majorOpeCode) throws OHException, ParseException{
		String query1 = "SELECT count(OPER_ID_A) as TOTAL  from operationrow join operation on OPER_ID=OPE_ID_A"
				+ " where  OPER_OPDATE BETWEEN ? AND ? and 	OPE_STAT=? ";
		
//		String query2 = "SELECT count(OPER_ID_A) as TOTAL  from operationrow join operation on OPER_ID=OPE_ID_A"
//				+ " where  OPER_OPDATE BETWEEN ? AND ? and 	OPE_IMPORTANCE=? ";
		
		List<Object> parameters1 = new ArrayList<Object>(3);
		List<Object> parameters2 = new ArrayList<Object>(3);
		
		Calendar cal = Calendar.getInstance();	    
	    cal.set( cal.YEAR, year );
	    cal.set( cal.MONTH, cal.JANUARY );
	    cal.set( cal.DATE, 1 );
		Date dateFrom = new Date(cal.getTime().getTime());
		cal.set( cal.YEAR, year );
	    cal.set( cal.MONTH, cal.DECEMBER );
	    cal.set( cal.DATE, 31 );
		Date dateTo = new Date(cal.getTime().getTime());
		String stringDateFrom = convertToSQLDateLimited(dateFrom);
		String stringDateTo = convertToSQLDateLimited(dateTo);
				
		parameters1.add(stringDateFrom);
		parameters1.add(stringDateTo);
		parameters1.add(minorOpeCode);
		
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters2.add(majorOpeCode);
		
		
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalMinorOpe", resultSet.getInt("TOTAL"));
			}
			resultSet = dbQuery.getDataWithParams(query1, parameters2, true);
			if (resultSet.next()) {
				results.put("totalMajorOpe", resultSet.getInt("TOTAL"));
			}
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	
	private String convertToSQLDateLimited(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	protected Timestamp toTimestamp(GregorianCalendar calendar) {
		if (calendar == null)
			return null;
		return new Timestamp(calendar.getTimeInMillis());
	}
}
