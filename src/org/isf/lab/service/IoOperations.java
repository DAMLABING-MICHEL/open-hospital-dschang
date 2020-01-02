package org.isf.lab.service;

/*------------------------------------------
 * lab.service.IoOperations - laboratory exam database io operations
 * -----------------------------------------
 * modification history
 * 02/03/2006 - theo - first beta version
 * 10/11/2006 - ross - added editing capability. 
 * 					   new fields data esame, sex, age, material, inout flag added
 * 21/06/2008 - ross - do not add 1 to toDate!. 
 *                     the selection date switched to exam date, 
 * 04/01/2009 - ross - do not use roll, use add(week,-1)!
 *                     roll does not change the year!
 * 16/11/2012 - mwithi - added logging capability
 * 					   - to do lock management
 * 04/02/2013 - mwithi - lock management done
 *------------------------------------------*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.isf.exa.model.Exam;
import org.isf.exatype.model.ExamType;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

public class IoOperations {

	/**
	 * Return a list of results ({@link LaboratoryRow}s) for passed lab entry.
	 * 
	 * @param code
	 *            - the {@link Laboratory} record ID.
	 * @return the list of {@link LaboratoryRow}s. It could be
	 *         <code>empty</code>
	 * @throws OHException
	 */
	public ArrayList<LaboratoryRow> getLabRow(Integer code) throws OHException {
		ArrayList<LaboratoryRow> row = new ArrayList<LaboratoryRow>();
		String query = "SELECT * FROM LABORATORYROW WHERE LABR_LAB_ID = ? ORDER BY LABR_DESC";
		List<Object> params = Collections.<Object>singletonList(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			while (resultSet.next()) {
				row.add(new LaboratoryRow(resultSet.getInt("LABR_ID"), resultSet.getInt("LABR_LAB_ID"),
						resultSet.getString("LABR_DESC"), resultSet.getString("LABR_RES_VALUE")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return row;
	}
	
	
	public boolean isHasResults(Integer code) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String query1 = "SELECT LAB_RES_VALUE FROM LABORATORY WHERE LAB_ID = ?";
		List<Object> params1 = Collections.<Object>singletonList(code);
		boolean lab_res_value = false;
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, params1, true);
			while (resultSet.next()) {				
				lab_res_value = resultSet.getString("LAB_RES_VALUE")!=null?true:false;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		
		String query = "SELECT * FROM LABORATORYROW WHERE LABR_LAB_ID = ? ORDER BY LABR_DESC";
		List<Object> params = Collections.<Object>singletonList(code);
		
		boolean rows = false;
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if (resultSet.next()) {
				rows = true;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		
		boolean result = false;
		if(rows) 
			result =  true;
		else if(lab_res_value) 
			result =  true;
		return result;
	}

	/*
	 * NO LONGER USED
	 * 
	 * public ArrayList<Laboratory> getLaboratory(String aCode) {
	 * GregorianCalendar time1 = new GregorianCalendar(); GregorianCalendar
	 * time2 = new GregorianCalendar(); // 04/1/2009 ross: no roll, use add!!
	 * //time1.roll(GregorianCalendar.WEEK_OF_YEAR, false);
	 * time1.add(GregorianCalendar.WEEK_OF_YEAR, -1); // 21/6/2008 ross: no
	 * rolling !! //time2.roll(GregorianCalendar.DAY_OF_YEAR, true); return
	 * getLaboratory(aCode, time1, time2); }
	 */

	/**
	 * Return the whole list of exams ({@link Laboratory}s) within last year.
	 * 
	 * @return the list of {@link Laboratory}s
	 * @throws OHException
	 */
	public ArrayList<Laboratory> getLaboratory() throws OHException {
		GregorianCalendar time1 = TimeTools.getServerDateTime();
		GregorianCalendar time2 = TimeTools.getServerDateTime();
		// 04/1/2009 ross: no roll, use add!!
		// time1.roll(GregorianCalendar.WEEK_OF_YEAR, false);
		time1.add(GregorianCalendar.WEEK_OF_YEAR, -1);
		// 21/6/2008 ross: no rolling !!
		// time2.roll(GregorianCalendar.DAY_OF_YEAR, true);
		return getLaboratory(null, time1, time2);
	}

	/**
	 * Return a list of exams ({@link Laboratory}s) between specified dates and
	 * matching passed exam name
	 * 
	 * @param exam
	 *            - the exam name as <code>String</code>
	 * @param dateFrom
	 *            - the lower date for the range
	 * @param dateTo
	 *            - the highest date for the range
	 * @return the list of {@link Laboratory}s
	 * @throws OHException
	 */
	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo)
			throws OHException {

		ArrayList<Laboratory> pLaboratory = new ArrayList<Laboratory>();
		// 21/6/2008 ross: no rolling !!
		// dateTo.roll(GregorianCalendar.DAY_OF_YEAR, true);

		ArrayList<Object> params = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM LABORATORY JOIN EXAM ON LAB_EXA_ID_A = EXA_ID_A "
				+ " LEFT JOIN BILLS on LAB_BLL_ID = BLL_ID ");		
		query.append(" WHERE LAB_EXAM_DATE >= ? AND LAB_EXAM_DATE <= ?");
		params.add(convertToSQLDateLimited(dateFrom)); // + " LAB_EXAM_DATE >='"
														// +
														// convertToSQLDateLimited(dateFrom)
														// + "'"
		params.add(convertToSQLDateLimited(dateTo)); // +
														// " and LAB_EXAM_DATE
														// <='"
														// +
														// convertToSQLDateLimited(dateTo)
														// + "'";
		if (exam != null) {
			query.append(" AND EXA_DESC = ?");
			params.add(exam);
		}
		query.append(" ORDER BY LAB_EXAM_DATE");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			while (resultSet.next()) {
				Laboratory lab = new Laboratory(resultSet.getInt("LAB_ID"),
						new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
								resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
						convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")), resultSet.getString("LAB_RES"),
						resultSet.getInt("LAB_LOCK"), resultSet.getString(("LAB_NOTE")), resultSet.getInt("LAB_PAT_ID"),
						resultSet.getString("LAB_PAT_NAME"));
				lab.setAge(resultSet.getInt("LAB_AGE"));
				lab.setSex(resultSet.getString("LAB_SEX"));
				lab.setMaterial(resultSet.getString("LAB_MATERIAL"));
				lab.setResultValue(resultSet.getString("LAB_RES_VALUE"));
				lab.setInOutPatient(resultSet.getString("LAB_PAT_INOUT"));
				lab.setBillId(resultSet.getInt("LAB_BLL_ID"));
				
				lab.setMProg(resultSet.getInt("LAB_MPROG"));
				lab.setPrescriber(resultSet.getString("LAB_PRESCRIBER"));
				
				
				GregorianCalendar examDate = new GregorianCalendar();
				if (resultSet.getDate("LAB_EXAM_DATE") == null)
					examDate = null;
				else
					examDate.setTime(resultSet.getDate("LAB_EXAM_DATE"));
				lab.setExamDate(examDate);
				//lab.setPaidStatus(resultSet.getString("BLL_STATUS")!=null?resultSet.getString("BLL_STATUS"):"");
				lab.setPaidStatus(resultSet.getString("BLL_STATUS"));
				pLaboratory.add(lab);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pLaboratory;
	}

	private ArrayList<Laboratory> readLabFromResiltset(ResultSet resultSet) throws SQLException {
		ArrayList<Laboratory> pLaboratory = new ArrayList<Laboratory>();
		while (resultSet.next()) {
			Laboratory lab = new Laboratory(resultSet.getInt("LAB_ID"),
					new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
							resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
					convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")), resultSet.getString("LAB_RES"),
					resultSet.getInt("LAB_LOCK"), resultSet.getString(("LAB_NOTE")), resultSet.getInt("LAB_PAT_ID"),
					resultSet.getString("LAB_PAT_NAME"));
			lab.setAge(resultSet.getInt("LAB_AGE"));
			lab.setSex(resultSet.getString("LAB_SEX"));
			lab.setMaterial(resultSet.getString("LAB_MATERIAL"));
			lab.setResultValue(resultSet.getString("LAB_RES_VALUE"));
			lab.setInOutPatient(resultSet.getString("LAB_PAT_INOUT"));
			lab.setBillId(resultSet.getInt("LAB_BLL_ID"));
			
			lab.setMProg(resultSet.getInt("LAB_MPROG"));
			lab.setPrescriber(resultSet.getString("LAB_PRESCRIBER"));
			
			GregorianCalendar examDate = new GregorianCalendar();
			if (resultSet.getDate("LAB_EXAM_DATE") == null)
				examDate = null;
			else
				examDate.setTime(resultSet.getDate("LAB_EXAM_DATE"));
			lab.setExamDate(examDate);
			pLaboratory.add(lab);
		}
		return pLaboratory;
	}

	public List<Laboratory> getLabWithoutBill(String idPatient) throws OHException {

		ArrayList<Laboratory> pLaboratory = null;
        if(idPatient == null){
        	return null;
        }
        else{
        	StringBuilder query = new StringBuilder("SELECT * FROM LABORATORY JOIN EXAM ON LAB_EXA_ID_A = EXA_ID_A");
    		query.append(" WHERE LAB_BLL_ID IS NULL OR LAB_BLL_ID <=0");
    		query.append(" AND LAB_PAT_ID = ?");

    		DbQueryLogger dbQuery = new DbQueryLogger();

    		try {
    			List<Object> parameters = new ArrayList<Object>();
    			parameters.add(idPatient);
    			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
    			//ResultSet resultSet = dbQuery.getData(query.toString(), true);
    			pLaboratory = this.readLabFromResiltset(resultSet);
    		} catch (SQLException e) {
    			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
    		} finally {
    			dbQuery.releaseConnection();
    		}
        }
		return pLaboratory;
	}

	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo,
			int resultFilter, String patientCode) throws OHException {

		ArrayList<Laboratory> pLaboratory = new ArrayList<Laboratory>();
		// 21/6/2008 ross: no rolling !!
		// dateTo.roll(GregorianCalendar.DAY_OF_YEAR, true);

		ArrayList<Object> params = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM LABORATORY JOIN EXAM ON LAB_EXA_ID_A = EXA_ID_A");
		query.append(" WHERE LAB_EXAM_DATE >= ? AND LAB_EXAM_DATE <= ?");
		params.add(convertToSQLDateLimited(dateFrom)); // + " LAB_EXAM_DATE >='"
		// +
		// convertToSQLDateLimited(dateFrom)
		// + "'"
		params.add(convertToSQLDateLimited(dateTo)); // +
		// " and LAB_EXAM_DATE <='"
		// +
		// convertToSQLDateLimited(dateTo)
		// + "'";
		if (exam != null) {
			query.append(" AND EXA_DESC = ?");
			params.add(exam);
		}
		if (patientCode != null) {
			query.append(" AND LAB_PAT_ID = ?");
			params.add(patientCode);
		}

		if (resultFilter == 0) {
			// with empty result
			query.append(" AND LAB_RES = ?");
			params.add("");
		} else if (resultFilter == 1) {
			// with not empty result
			query.append(" AND LAB_RES <> ?");
			params.add("");
		}

		query.append(" ORDER BY LAB_EXAM_DATE");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			while (resultSet.next()) {
				Laboratory lab = new Laboratory(resultSet.getInt("LAB_ID"),
						new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
								resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
						convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")), resultSet.getString("LAB_RES"),
						resultSet.getInt("LAB_LOCK"), resultSet.getString(("LAB_NOTE")), resultSet.getInt("LAB_PAT_ID"),
						resultSet.getString("LAB_PAT_NAME"));
				lab.setAge(resultSet.getInt("LAB_AGE"));
				lab.setSex(resultSet.getString("LAB_SEX"));
				lab.setMaterial(resultSet.getString("LAB_MATERIAL"));
				lab.setResultValue(resultSet.getString("LAB_RES_VALUE"));
				lab.setInOutPatient(resultSet.getString("LAB_PAT_INOUT"));
				lab.setBillId(resultSet.getInt("LAB_BLL_ID"));
				
				lab.setMProg(resultSet.getInt("LAB_MPROG"));
				
				GregorianCalendar examDate = new GregorianCalendar();
				if (resultSet.getDate("LAB_EXAM_DATE") == null)
					examDate = null;
				else
					examDate.setTime(resultSet.getDate("LAB_EXAM_DATE"));
				lab.setExamDate(examDate);
				pLaboratory.add(lab);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pLaboratory;
	}
	
	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo,
			int resultFilter, String patientCode , String userCode, String paidCode) throws OHException {

		ArrayList<Laboratory> pLaboratory = new ArrayList<Laboratory>();
		ArrayList<Object> params = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM LABORATORY JOIN EXAM ON LAB_EXA_ID_A = EXA_ID_A "
				+ " LEFT JOIN BILLS on LAB_BLL_ID = BLL_ID ");
		query.append(" WHERE LAB_EXAM_DATE >= ? AND LAB_EXAM_DATE <= ?");
		params.add(convertToSQLDateLimited(dateFrom)); // + " LAB_EXAM_DATE >='"
		params.add(convertToSQLDateLimited(dateTo)); // +
		if (exam != null) {
			query.append(" AND EXA_DESC = ?");
			params.add(exam);
		}
		if (patientCode != null) {
			query.append(" AND LAB_PAT_ID = ?");
			params.add(patientCode);
		}			
		if (userCode != null) {
			query.append(" AND LAB_PRESCRIBER = ?");
			params.add(userCode);
		}
		
		if (paidCode != null && !paidCode.equals("0")) {			
			query.append(" AND BLL_STATUS = ?");
			params.add(paidCode);
		}
		
		if(paidCode != null && paidCode.equals("0")){
			query.append(" AND LAB_BLL_ID <= ?");
			params.add(Integer.parseInt(paidCode));
		}

		if (resultFilter == 0) {
			// with empty result
			query.append(" AND LAB_RES = ?");
			params.add("");
		} else if (resultFilter == 1) {
			// with not empty result
			query.append(" AND LAB_RES <> ?");
			params.add("");
		}

		query.append(" ORDER BY LAB_EXAM_DATE");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			while (resultSet.next()) {
				Laboratory lab = new Laboratory(resultSet.getInt("LAB_ID"),
						new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
								resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
						convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")), resultSet.getString("LAB_RES"),
						resultSet.getInt("LAB_LOCK"), resultSet.getString(("LAB_NOTE")), resultSet.getInt("LAB_PAT_ID"),
						resultSet.getString("LAB_PAT_NAME"));
				lab.setAge(resultSet.getInt("LAB_AGE"));
				lab.setSex(resultSet.getString("LAB_SEX"));
				lab.setMaterial(resultSet.getString("LAB_MATERIAL"));
				lab.setResultValue(resultSet.getString("LAB_RES_VALUE"));
				lab.setInOutPatient(resultSet.getString("LAB_PAT_INOUT"));
				lab.setBillId(resultSet.getInt("LAB_BLL_ID"));
				
				lab.setMProg(resultSet.getInt("LAB_MPROG"));
				lab.setPrescriber(resultSet.getString("LAB_PRESCRIBER"));
				lab.setPaidStatus(resultSet.getString("BLL_STATUS"));
				
				GregorianCalendar examDate = new GregorianCalendar();
				if (resultSet.getDate("LAB_EXAM_DATE") == null)
					examDate = null;
				else
					examDate.setTime(resultSet.getDate("LAB_EXAM_DATE"));
				lab.setExamDate(examDate);
				pLaboratory.add(lab);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pLaboratory;
	}
	
	public int getLaboratoryCount(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo,
			int resultFilter, String patientCode , String userCode, String paidCode) throws OHException {
		
		ArrayList<Object> params = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT COUNT(LAB_ID) AS TOTAL FROM LABORATORY JOIN EXAM ON LAB_EXA_ID_A = EXA_ID_A "
				+ " LEFT JOIN BILLS on LAB_BLL_ID = BLL_ID ");
		query.append(" WHERE LAB_EXAM_DATE >= ? AND LAB_EXAM_DATE <= ?");
		params.add(convertToSQLDateLimited(dateFrom)); // + " LAB_EXAM_DATE >='"
		params.add(convertToSQLDateLimited(dateTo)); // +
		if (exam != null) {
			query.append(" AND EXA_DESC = ?");
			params.add(exam);
		}
		if (patientCode != null) {
			query.append(" AND LAB_PAT_ID = ?");
			params.add(patientCode);
		}			
		if (userCode != null) {
			query.append(" AND LAB_PRESCRIBER = ?");
			params.add(userCode);
		}					
		if (paidCode != null && !paidCode.equals("0")) {			
			query.append(" AND BLL_STATUS = ?");
			params.add(paidCode);
		}
		
		if(paidCode != null && paidCode.equals("0")){
			query.append(" AND LAB_BLL_ID <= ?");
			params.add(Integer.parseInt(paidCode));
		}

		if (resultFilter == 0) {
			// with empty result
			query.append(" AND LAB_RES = ?");
			params.add("");
		} else if (resultFilter == 1) {
			// with not empty result
			query.append(" AND LAB_RES <> ?");
			params.add("");
		}

		query.append(" ORDER BY LAB_EXAM_DATE");
	    int number = 0;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			while (resultSet.next()) {
				number = resultSet.getInt("TOTAL");
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return number;
	}
	
	

	/**
	 * Return a list of exams ({@link Laboratory}s) related to a {@link Patient}
	 * .
	 * 
	 * @param aPatient
	 *            - the {@link Patient}.
	 * @return the list of {@link Laboratory}s related to the {@link Patient}.
	 * @throws OHException
	 */
	public ArrayList<Laboratory> getLaboratory(int patID) throws OHException {
		ArrayList<Laboratory> pLaboratory = new ArrayList<Laboratory>();
		String query = "SELECT * FROM (LABORATORY JOIN EXAM ON LAB_EXA_ID_A=EXA_ID_A)"
				+ " LEFT JOIN LABORATORYROW ON LABR_LAB_ID = LAB_ID WHERE LAB_PAT_ID = ? "
				+ " ORDER BY LAB_DATE, LAB_ID";
		List<Object> params = Collections.<Object>singletonList(patID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			while (resultSet.next()) {
				String procedure = resultSet.getString("LAB_RES");
				String resValue = resultSet.getString("LAB_RES_VALUE");
				if (procedure != null && procedure.equals(MessageBundle.getMessage("angal.lab.multipleresults"))) {
					procedure = resultSet.getString("LABR_DESC");
					resValue = resultSet.getString("LABR_RES_VALUE");
					if (procedure == null || (procedure != null && procedure.trim().equals(""))) {
						procedure = MessageBundle.getMessage("angal.lab.allnegative");
					} else {
						procedure = procedure + " " + MessageBundle.getMessage("angal.lab.positive");
					}
				}
				Laboratory lab = new Laboratory(resultSet.getInt("LAB_ID"),
						new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
								resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
						convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")), procedure,
						resultSet.getInt("LAB_LOCK"), resultSet.getString(("LAB_NOTE")), resultSet.getInt("LAB_PAT_ID"),
						resultSet.getString("LAB_PAT_NAME"));
				lab.setAge(resultSet.getInt("LAB_AGE"));
				lab.setSex(resultSet.getString("LAB_SEX"));
				lab.setMaterial(resultSet.getString("LAB_MATERIAL"));
				lab.setResultValue(resValue);
				lab.setInOutPatient(resultSet.getString("LAB_PAT_INOUT"));
				lab.setBillId(resultSet.getInt("LAB_BLL_ID"));
				
				lab.setMProg(resultSet.getInt("LAB_MPROG"));
				
				GregorianCalendar examDate = new GregorianCalendar();
				if (resultSet.getDate("LAB_EXAM_DATE") == null)
					examDate = null;
				else
					examDate.setTime(resultSet.getDate("LAB_EXAM_DATE"));
				lab.setExamDate(examDate);
				pLaboratory.add(lab);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pLaboratory;
	}

	/**
	 * Return an exam ({@link Laboratory}s) found by its code.
	 * 
	 * @param aPatient
	 *            - the {@link Patient}.
	 * @return the list of {@link Laboratory}s related to the {@link Patient}.
	 * @throws OHException
	 */
	public Laboratory getLaboratoryByCode(int code) throws OHException {
		Laboratory lab = null;
		String query = "SELECT * FROM (LABORATORY JOIN EXAM ON LAB_EXA_ID_A=EXA_ID_A)"
				+ " LEFT JOIN LABORATORYROW ON LABR_LAB_ID = LAB_ID WHERE LAB_ID = ? ";
		List<Object> params = Collections.<Object>singletonList(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			if (resultSet.next()) {
				String procedure = resultSet.getString("LAB_RES");
				if (procedure != null && procedure.equals(MessageBundle.getMessage("angal.lab.multipleresults"))) {
					procedure = resultSet.getString("LABR_DESC");
					if (procedure == null || (procedure != null && procedure.trim().equals(""))) {
						procedure = MessageBundle.getMessage("angal.lab.allnegative");
					} else {
						procedure = procedure + " " + MessageBundle.getMessage("angal.lab.positive");
					}
				}
				lab = new Laboratory(resultSet.getInt("LAB_ID"),
						new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
								resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
						convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")), procedure,
						resultSet.getInt("LAB_LOCK"), resultSet.getString(("LAB_NOTE")), resultSet.getInt("LAB_PAT_ID"),
						resultSet.getString("LAB_PAT_NAME"));
				lab.setAge(resultSet.getInt("LAB_AGE"));
				lab.setSex(resultSet.getString("LAB_SEX"));
				lab.setMaterial(resultSet.getString("LAB_MATERIAL"));
				lab.setResultValue(resultSet.getString("LAB_RES_VALUE"));
				lab.setInOutPatient(resultSet.getString("LAB_PAT_INOUT"));
				lab.setBillId(resultSet.getInt("LAB_BLL_ID"));
				
				lab.setMProg(resultSet.getInt("LAB_MPROG"));
				
				GregorianCalendar examDate = new GregorianCalendar();
				if (resultSet.getDate("LAB_EXAM_DATE") == null)
					examDate = null;
				else
					examDate.setTime(resultSet.getDate("LAB_EXAM_DATE"));
				lab.setExamDate(examDate);

			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return lab;
	}

	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}
	 * s) within last year
	 * 
	 * @param exam
	 *            - the exam name as <code>String</code>
	 * @param dateFrom
	 *            - the lower date for the range
	 * @param dateTo
	 *            - the highest date for the range
	 * @return the list of {@link LaboratoryForPrint}s
	 * @throws OHException
	 */
	public ArrayList<LaboratoryForPrint> getLaboratoryForPrint() throws OHException {
		GregorianCalendar time1 = TimeTools.getServerDateTime();
		GregorianCalendar time2 = TimeTools.getServerDateTime();
		// time1.roll(GregorianCalendar.WEEK_OF_YEAR, false);
		time1.add(GregorianCalendar.WEEK_OF_YEAR, -1);
		// 21/6/2008 ross: no rolling !!
		// time2.roll(GregorianCalendar.DAY_OF_YEAR, true);
		return getLaboratoryForPrint(null, time1, time2);
	}

	/*
	 * NO LONGER USED
	 * 
	 * public ArrayList<LaboratoryForPrint> getLaboratoryForPrint(String exam,
	 * String result) { GregorianCalendar time1 = new GregorianCalendar();
	 * GregorianCalendar time2 = new GregorianCalendar();
	 * //time1.roll(GregorianCalendar.WEEK_OF_YEAR, false);
	 * time1.add(GregorianCalendar.WEEK_OF_YEAR, -1); // 21/6/2008 ross: no
	 * rolling !! //time2.roll(GregorianCalendar.DAY_OF_YEAR, true); return
	 * getLaboratoryForPrint(exam, time1, time2); }
	 */

	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}
	 * s) between specified dates and matching passed exam name
	 * 
	 * @param exam
	 *            - the exam name as <code>String</code>
	 * @param dateFrom
	 *            - the lower date for the range
	 * @param dateTo
	 *            - the highest date for the range
	 * @return the list of {@link LaboratoryForPrint}s
	 * @throws OHException
	 */
	public ArrayList<LaboratoryForPrint> getLaboratoryForPrint(String exam, GregorianCalendar dateFrom,
			GregorianCalendar dateTo) throws OHException {
		ArrayList<LaboratoryForPrint> pLaboratory = new ArrayList<LaboratoryForPrint>();
		;
		dateTo.roll(GregorianCalendar.DAY_OF_YEAR, true);

		ArrayList<Object> params = new ArrayList<Object>();
		StringBuilder query = new StringBuilder(
				"SELECT * FROM (LABORATORY JOIN EXAM ON LAB_EXA_ID_A = EXA_ID_A) JOIN EXAMTYPE ON EXC_ID_A = EXA_EXC_ID_A");
		query.append(" WHERE LAB_DATE >= ? AND LAB_DATE <= ?");
		params.add(convertToSQLDateLimited(dateFrom)); // + " LAB_EXAM_DATE >='"
														// +
														// convertToSQLDateLimited(dateFrom)
														// + "'"
		params.add(convertToSQLDateLimited(dateTo)); // +
														// " and LAB_EXAM_DATE
														// <='"
														// +
														// convertToSQLDateLimited(dateTo)
														// + "'";
		if (exam != null) {
			query.append(" AND EXA_DESC LIKE ?");
			params.add('%' + exam + '%');
		}
		query.append(" ORDER BY EXC_DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			while (resultSet.next()) {
				pLaboratory.add(new LaboratoryForPrint(resultSet.getInt("LAB_ID"),
						new Exam(resultSet.getString("EXA_ID_A"), resultSet.getString("EXA_DESC"), new ExamType("", ""),
								resultSet.getInt("EXA_PROC"), resultSet.getString("EXA_DEFAULT"), 0),
						convertToGregorianDate((Date) resultSet.getObject("LAB_DATE")),
						resultSet.getString("LAB_RES")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pLaboratory;
	}

	/**
	 * Insert a Laboratory exam {@link Laboratory} and return generated key. No
	 * commit is performed.
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to insert
	 * @param dbQuery
	 *            - the connection manager
	 * @return the generated key
	 * @throws OHException
	 */
	private Integer newLaboratory(Laboratory laboratory, DbQueryLogger dbQuery) throws OHException {
		Integer newCode = -1;
		ArrayList<Object> params = new ArrayList<Object>();
		try {

			String sqlString = "INSERT INTO LABORATORY (LAB_EXA_ID_A ,LAB_DATE, " + "LAB_RES, LAB_NOTE, LAB_PAT_NAME, "
					+ "LAB_PAT_ID, LAB_AGE, LAB_SEX, "
					+ "LAB_MATERIAL, LAB_EXAM_DATE, LAB_PAT_INOUT, LAB_RES_VALUE, LAB_BLL_ID, LAB_MPROG, LAB_CREATE_BY, LAB_CREATE_DATE, LAB_PRESCRIBER) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			params.add(laboratory.getExam().getCode());
			params.add(new java.sql.Timestamp(laboratory.getDate().getTime().getTime()));
			params.add(laboratory.getResult());
			params.add(laboratory.getNote());
			params.add(laboratory.getPatName());
			params.add(laboratory.getPatId() > 0 ? "" + laboratory.getPatId() : null);
			params.add(laboratory.getAge());
			params.add(laboratory.getSex());
			params.add(laboratory.getMaterial());
			params.add(new java.sql.Date(laboratory.getExamDate().getTime().getTime()));
			params.add(laboratory.getInOutPatient());
			params.add(laboratory.getResultValue());
			params.add(laboratory.getBillId());
			
			params.add(laboratory.getMProg());

			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			params.add(laboratory.getPrescriber());
			
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(sqlString, params, false);
			if (result.next()) {
				newCode = result.getInt(1);
			} else
				return -1;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		return newCode;
	}

	private Integer newLaboratory2(Laboratory laboratory, DbQueryLogger dbQuery) throws OHException {
		Integer newCode = -1;
		ArrayList<Object> params = new ArrayList<Object>();
		try {
			String sqlString = "INSERT INTO LABORATORY (LAB_EXA_ID_A ,LAB_DATE, " + "LAB_RES, LAB_NOTE, LAB_PAT_NAME, "
					+ "LAB_PAT_ID, LAB_AGE, LAB_SEX, "
					+ "LAB_MATERIAL, LAB_EXAM_DATE, LAB_PAT_INOUT, LAB_RES_VALUE, LAB_BLL_ID, LAB_MPROG, LAB_CREATE_BY, LAB_CREATE_DATE,LAB_PRESCRIBER) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			params.add(laboratory.getExam().getCode());
			params.add(new java.sql.Timestamp(laboratory.getDate().getTime().getTime()));
			params.add(laboratory.getResult());
			params.add(laboratory.getNote());
			params.add(laboratory.getPatName());
			params.add(laboratory.getPatId() > 0 ? "" + laboratory.getPatId() : null);
			params.add(laboratory.getAge());
			params.add(laboratory.getSex());
			params.add(laboratory.getMaterial());
			params.add(new java.sql.Date(laboratory.getExamDate().getTime().getTime()));
			params.add(laboratory.getInOutPatient());
			params.add(laboratory.getResultValue());
			params.add(laboratory.getBillId());
			
			params.add(laboratory.getMProg());
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			params.add(laboratory.getPrescriber());
			
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(sqlString, params, false);
			if (result.next()) {
				newCode = result.getInt(1);
			} else
				return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		return newCode;
	}

	/**
	 * Inserts one Laboratory exam {@link Laboratory} (Procedure One)
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to insert
	 * @param dbQuery
	 *            - the connection manager
	 * @return <code>true</code> if the exam has been inserted,
	 *         <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean newLabFirstProcedure(Laboratory laboratory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			Integer newCode = newLaboratory(laboratory, dbQuery);
			if (newCode > 0)
				dbQuery.commit();
			laboratory.setCode(newCode);
			return (newCode > 0);

		} finally {
			dbQuery.releaseConnection();
		}
	}

	public boolean newLabFirstProcedure2(Laboratory laboratory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			Integer newCode = newLaboratory2(laboratory, dbQuery);
			if (newCode > 0)
				dbQuery.commit();
			laboratory.setCode(newCode);
			return (newCode > 0);

		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Inserts one Laboratory exam {@link Laboratory} with multiple results
	 * (Procedure Two)
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to insert
	 * @param labRow
	 *            - the list of results ({@link String}s)
	 * @return <code>true</code> if the exam has been inserted with all its
	 *         results, <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean newLabSecondProcedure(Laboratory laboratory, ArrayList<LaboratoryRow> labRow) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Object> params;
		boolean result = false;
		try {			
			Integer newCode = newLaboratory(laboratory, dbQuery);
			if (newCode > 0) {
				result = true;
				laboratory.setCode(newCode);				
				String query = "INSERT INTO LABORATORYROW (LABR_LAB_ID,LABR_DESC, LABR_RES_VALUE) VALUES (?,?,?)";
				for (LaboratoryRow row : labRow) {
					params = new ArrayList<Object>(2);
					params.add(laboratory.getCode());
					params.add(row.getDescription());
					params.add(row.getResultValue());
					result = dbQuery.setDataWithParams(query, params, false);
				}

				if (result) {
					dbQuery.commit();
				}
			}
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	public boolean newLabSecondProcedure2(Laboratory laboratory, ArrayList<LaboratoryRow> labRow) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Object> params;
		boolean result = false;		
		try {
			Integer newCode = newLaboratory2(laboratory, dbQuery);
			if (newCode > 0) {
				result = true;
				laboratory.setCode(newCode);				
				String query = "INSERT INTO LABORATORYROW (LABR_LAB_ID,LABR_DESC, LABR_RES_VALUE) VALUES (?,?,?)";
				for (LaboratoryRow row : labRow) {
					params = new ArrayList<Object>(2);
					params.add(laboratory.getCode());
					params.add(row.getDescription());
					params.add(row.getResultValue());
					result = dbQuery.setDataWithParams(query, params, false);
				}
				if (result) {
					dbQuery.commit();
				}
			}
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory}. No commit
	 * is performed.
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to update
	 * @param dbQuery
	 *            - the connection manager
	 * @return <code>true</code> if the exam has been updated with all its
	 *         results, <code>false</code> otherwise
	 * @throws OHException
	 */
	private boolean updateLaboratory(Laboratory laboratory, DbQueryLogger dbQuery) throws OHException {
		String query = "UPDATE LABORATORY SET " + "LAB_EXA_ID_A = ?, " + "LAB_DATE = ?, " + "LAB_RES = ?, "
				+ "LAB_NOTE = ?, " + "LAB_PAT_NAME = ?, " + "LAB_PAT_ID = ?, " + "LAB_AGE = ?, " + "LAB_SEX = ?, "
				+ "LAB_MATERIAL = ?, " + "LAB_EXAM_DATE = ?, " + "LAB_PAT_INOUT = ?, " + "LAB_RES_VALUE = ?, "
				+ " LAB_BLL_ID = ?, LAB_MPROG=? , LAB_MODIFY_BY=? , LAB_MODIFY_DATE=? , LAB_PRESCRIBER=?"  + "WHERE LAB_ID = ?";

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(laboratory.getExam().getCode());
		params.add(new java.sql.Timestamp(laboratory.getDate().getTime().getTime()));
		params.add(laboratory.getResult());
		params.add(laboratory.getNote());
		params.add(laboratory.getPatName());
		params.add(laboratory.getPatId() > 0 ? "" + laboratory.getPatId() : null);
		params.add(laboratory.getAge());
		params.add(laboratory.getSex());
		params.add(laboratory.getMaterial());
		params.add(new java.sql.Date(laboratory.getExamDate().getTime().getTime()));
		params.add(laboratory.getInOutPatient());
		params.add(laboratory.getResultValue());
		params.add(laboratory.getBillId());
		
		params.add(laboratory.getMProg());
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(laboratory.getPrescriber());
		params.add(laboratory.getCode());

		return dbQuery.setDataWithParams(query, params, false);
	}
	
	public boolean updateBillIdLaboratory(int laboratoryId, int billId) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
//		try {
			String sqlString = "UPDATE LABORATORY SET LAB_BLL_ID = ? , LAB_MODIFY_BY=? , LAB_MODIFY_DATE=? " + "WHERE LAB_ID = ?";
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(billId);
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(laboratoryId);
			
			result = dbQuery.setDataWithParams(sqlString, parameters, true);
//		} finally {
//			dbQuery.releaseConnection();
//		}
		return result;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure
	 * One). If old exam was Procedure Two all its releated result are deleted.
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated,
	 *         <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean updateLabFirstProcedure(Laboratory laboratory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = updateLaboratory(laboratory, dbQuery);
		// se cambio da procedura 2 a procedura 1
		try {
			String query = "SELECT * FROM LABORATORYROW WHERE LABR_LAB_ID = ?";
			List<Object> params = Collections.<Object>singletonList(laboratory.getCode());
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, false);

			if (resultSet.next()) {
				query = "DELETE FROM LABORATORYROW WHERE LABR_LAB_ID = ?";
				result = dbQuery.setDataWithParams(query, params, false);
			}
			if (result) {
				dbQuery.commit();
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure
	 * Two). Previous results are deleted and replaced with new ones.
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated with all its
	 *         results, <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean editLabSecondProcedure(Laboratory laboratory, ArrayList<LaboratoryRow> labRow) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = updateLaboratory(laboratory, dbQuery);
		try {
			if (result) {

				String query = "DELETE FROM LABORATORYROW WHERE LABR_LAB_ID = ?";
				List<Object> params = Collections.<Object>singletonList(laboratory.getCode());

				dbQuery.setDataWithParams(query, params, false);

				query = "INSERT INTO LABORATORYROW (LABR_LAB_ID, LABR_DESC, LABR_RES_VALUE) values (?,?,?)";

				for (LaboratoryRow row : labRow) {
					params = new ArrayList<Object>(2);
					params.add(laboratory.getCode());
					params.add(row.getDescription());
					params.add(row.getResultValue());

					result = dbQuery.setDataWithParams(query, params, false);
				}
				if (result) {
					dbQuery.commit();
				}
			} else
				return false;
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Delete a Laboratory exam {@link Laboratory} (Procedure One or Two).
	 * Previous results, if any, are deleted as well.
	 * 
	 * @param laboratory
	 *            - the {@link Laboratory} to delete
	 * @return <code>true</code> if the exam has been deleted with all its
	 *         results, if any. <code>false</code> otherwise
	 * @throws OHException
	 */
	public boolean deleteLaboratory(Laboratory aLaboratory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try {
			String query = "";
			List<Object> params = Collections.<Object>singletonList(aLaboratory.getCode());

			if (aLaboratory.getExam().getProcedure() == 2) {
				query = "DELETE FROM LABORATORYROW WHERE LABR_LAB_ID = ?";
				dbQuery.setDataWithParams(query, params, false);
			}

			query = "DELETE FROM LABORATORY WHERE LAB_ID = ?";
			result = dbQuery.setDataWithParams(query, params, false);

			dbQuery.commit();
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * This method express a date saved in a GregorianCalendar object in a
	 * String form, understandable by MySql It contains also the hours,minutes
	 * and seconds
	 * 
	 * @param time
	 *            (GregorianCalendar)
	 * @return String
	 */
	public String convertToSQLDate(GregorianCalendar time) {
		return String.valueOf(time.get(GregorianCalendar.YEAR)) + "-"
				+ String.valueOf(time.get(GregorianCalendar.MONTH) + 1) + "-" + time.get(GregorianCalendar.DAY_OF_MONTH)
				+ " " + time.get(GregorianCalendar.HOUR_OF_DAY) + ":" + time.get(GregorianCalendar.MINUTE) + ":"
				+ time.get(GregorianCalendar.SECOND);
	}

	/**
	 * This method express a date saved in a GregorianCalendar object in a
	 * String form, understandable by MySql It doesn't contain also the
	 * hours,minutes and seconds
	 * 
	 * @param time
	 *            (GregorianCalendar)
	 * @return String
	 */
	public String convertToSQLDateLimited(GregorianCalendar time) {
		return time.get(GregorianCalendar.YEAR) + "-" + String.valueOf(time.get(GregorianCalendar.MONTH) + 1) + "-"
				+ time.get(GregorianCalendar.DAY_OF_MONTH);
	}

	/**
	 * It sets a date contained in a Date object into a GregorianCalendar object
	 * When we load the date from the database, in fact, we get a Date object
	 * 
	 * @param aDate
	 *            (Date)
	 * @return GregorianCalendar
	 */
	public GregorianCalendar convertToGregorianDate(Date aDate) {
		GregorianCalendar time = new GregorianCalendar();
		time.setTime(aDate);
		return time;
	}
	
	public HashMap<String, Integer> getCountLaboratory(int year) throws OHException, ParseException{
		String query1 = "SELECT count(LAB_ID) as TOTAL  from laboratory "
				+ " where  LAB_DATE BETWEEN ? AND ? and LAB_DATE IS NOT NULL";
				
//		String query2 = "SELECT SUM(unix_timestamp(ADM_DATE_DIS)-unix_timestamp(ADM_DATE_ADM)) as TOTAL  from admission "
//				+ " where  ADM_DATE_DIS BETWEEN ? AND ? and ADM_DATE_DIS IS NOT NULL AND ADM_DATE_ADM IS NOT NULL";
//		
		List<Object> parameters1 = new ArrayList<Object>(2);
		//List<Object> parameters2 = new ArrayList<Object>(2);
		
		//DateFormat dateFromat = new SimpleDateFormat("dd/MM/yyyy");  unix_timestamp
		
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
		//parameters2.add(stringDateFrom);
		//parameters2.add(stringDateTo);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalLaboratory", resultSet.getInt("TOTAL"));
			}
//			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
//			if (resultSet.next()) {
//				results.put("totalAvgAdm", resultSet.getInt("TOTAL"));
//			}			
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	
	public HashMap<String, Integer> getCountLaboratoryByID(int year,int idLab) throws OHException, ParseException{
		String query1 = "SELECT count(LAB_ID) as TOTAL  from laboratory "
				+ " where  LAB_DATE BETWEEN ? AND ? and LAB_DATE IS NOT NULL and LAB_EXA_ID_A=?";
				
//		String query2 = "SELECT SUM(unix_timestamp(ADM_DATE_DIS)-unix_timestamp(ADM_DATE_ADM)) as TOTAL  from admission "
//				+ " where  ADM_DATE_DIS BETWEEN ? AND ? and ADM_DATE_DIS IS NOT NULL AND ADM_DATE_ADM IS NOT NULL";
//		
		List<Object> parameters1 = new ArrayList<Object>(2);
		//List<Object> parameters2 = new ArrayList<Object>(2);
		
		//DateFormat dateFromat = new SimpleDateFormat("dd/MM/yyyy");  unix_timestamp
		
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
		parameters1.add(idLab);
		//parameters2.add(stringDateFrom);
		//parameters2.add(stringDateTo);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalLaboratoryById", resultSet.getInt("TOTAL"));
			}
//			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
//			if (resultSet.next()) {
//				results.put("totalAvgAdm", resultSet.getInt("TOTAL"));
//			}			
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	public HashMap<String, Integer> getCountLaboratoryByIDPositive(int year,String examId,String valuePositive) throws OHException, ParseException{
		String query1 = "SELECT count(LAB_ID) as TOTAL  from laboratory "
				+ " where  LAB_DATE BETWEEN ? AND ? and LAB_DATE IS NOT NULL and LAB_EXA_ID_A=? and LAB_RES=?";
				
		List<Object> parameters1 = new ArrayList<Object>(4);
		
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
		parameters1.add(examId);
		parameters1.add(valuePositive);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalLaboratoryById", resultSet.getInt("TOTAL"));
			}		
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	
	public HashMap<String, Integer> getCountLaboratoryHIV(int year,String examId,String valuePositive) throws OHException, ParseException{
		String query1 = "SELECT count(LAB_ID) as TOTAL  from laboratory "
				+ " where  LAB_DATE BETWEEN ? AND ? and LAB_DATE IS NOT NULL and LAB_EXA_ID_A=? and LAB_RES=?";
		
		String query2 = "SELECT count(LAB_ID) as TOTAL  from laboratory "
				+ " where  LAB_DATE BETWEEN ? AND ? and LAB_DATE IS NOT NULL and LAB_EXA_ID_A=?";		
		List<Object> parameters1 = new ArrayList<Object>(4);
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
		parameters1.add(examId);
		parameters1.add(valuePositive);
		
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters2.add(examId);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalLaboratoryHivPositive", resultSet.getInt("TOTAL"));
			}	
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalLaboratoryHiv", resultSet.getInt("TOTAL"));
			}	
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	
	public HashMap<String, Integer> getCountLaboratoryHivPregnant(int year,String examId,String valuePositive) throws OHException, ParseException{
//		String query1 = "SELECT count(*) as TOTAL  from laboratory lab "
//				+ " inner join pregnancyvisit pvis on "
//				+ " (DATE_FORMAT(lab.LAB_DATE, '%d/%m/%Y') = DATE_FORMAT(pvis.PVIS_DATE, '%d/%m/%Y') "
//				+ " and  lab.LAB_PAT_ID in (select PREG_PAT_ID from pregnancy))"
//				+ " where  lab.LAB_DATE BETWEEN ? AND ? and lab.LAB_DATE IS NOT NULL and lab.LAB_EXA_ID_A=? and lab.LAB_RES=?";
//		
		String query1 = "SELECT count(LAB_ID) as TOTAL  from laboratory lab "
				+ " where  lab.LAB_DATE BETWEEN ? AND ? and lab.LAB_DATE IS NOT NULL and lab.LAB_EXA_ID_A=? and lab.LAB_RES=? and  lab.LAB_PAT_ID in (select PREG_PAT_ID from pregnancy)"
				+ "and DATE_FORMAT(lab.LAB_DATE, '%d/%m/%Y') = (select DATE_FORMAT(PVIS_DATE, '%d/%m/%Y') from pregnancyvisit join pregnancy on PVIS_PREG_ID=PREG_ID where PREG_PAT_ID=lab.LAB_PAT_ID)";
		
		String query2 = "SELECT count(LAB_ID) as TOTAL  from laboratory lab "
				+ " where  lab.LAB_DATE BETWEEN ? AND ? and lab.LAB_DATE IS NOT NULL and lab.LAB_EXA_ID_A=? and  lab.LAB_PAT_ID in (select PREG_PAT_ID from pregnancy)"
				+ " and DATE_FORMAT(lab.LAB_DATE, '%d/%m/%Y') = (select DATE_FORMAT(PVIS_DATE, '%d/%m/%Y') from pregnancyvisit join pregnancy on PVIS_PREG_ID=PREG_ID where PREG_PAT_ID=lab.LAB_PAT_ID )";
		
		List<Object> parameters1 = new ArrayList<Object>(4);
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
		parameters1.add(examId);
		parameters1.add(valuePositive);
		
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters2.add(examId);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalLabPregnantHivPositive", resultSet.getInt("TOTAL"));				
			}	
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalLabPregnantHiv", resultSet.getInt("TOTAL"));
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
	/**
	 * Returns the max progressive number within specified month of specific
	 * year .
	 * 
	 * @param month
	 * @param year
	 * @return <code>int</code> - the progressive number in the month of the year
	 * @throws OHException
	 */
	public int getProgMonth(int month, int year) throws OHException {
		int progMonth = 0;
		DbQueryLogger dbQuery = new DbQueryLogger();
		ResultSet resultSet;
		StringBuilder sqlString = new StringBuilder("SELECT MAX(LAB_MPROG) FROM LABORATORY");
		sqlString.append(" WHERE YEAR(LAB_DATE	) = ? AND MONTH(LAB_DATE ) = ? ");
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(year);
		parameters.add(month);		
		resultSet = dbQuery.getDataWithParams(sqlString.toString(), parameters, true);
		try {
			resultSet.next();
			progMonth = resultSet.getInt("MAX(LAB_MPROG)");			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return progMonth;
	}
	
	public ArrayList<String> getPrescriber() throws OHException {		
		DbQueryLogger dbQuery = new DbQueryLogger();
		ResultSet resultSet;
		StringBuilder sqlString = new StringBuilder("SELECT DISTINCT LAB_PRESCRIBER FROM LABORATORY");		
		resultSet = dbQuery.getData(sqlString.toString(), false);
		ArrayList<String> list = new  ArrayList<String>();
		try {
			while (resultSet.next()) {	
				if(resultSet.getString("LAB_PRESCRIBER")!=null)
					list.add(resultSet.getString("LAB_PRESCRIBER"));	
			}
					
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return list;
	}
}
