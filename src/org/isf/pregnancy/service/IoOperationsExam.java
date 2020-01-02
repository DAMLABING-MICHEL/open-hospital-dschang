package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.isf.pregnancy.model.PregnancyExam;
import org.isf.pregnancy.model.PregnancyExamResult;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.utils.db.DbQuery;


/**
 * @author Martin Reinstadler
 * this class performs database readings and insertions for the tables:
 * PREGNANCYEXAM, PREGNANCYEXAMRESULT
 *
 */
public class IoOperationsExam {

	/**
	 * @param visittype the type of the {@link PregnancyVisit} from -1 to 1
	 * @return the list of {@link PregnancyExam} for the visittype
	 */
	public ArrayList<PregnancyExam> getPregnancyExams(int visittype) {
		ArrayList<PregnancyExam> exams = new ArrayList<PregnancyExam>();
		StringBuffer stringBfr = new StringBuffer();
		stringBfr = new StringBuffer(
				"SELECT * FROM PREGNANCYEXAM PEX ");
		String comparison = visittype== -1? " <= 0 ": " >=0 ";
		stringBfr.append("WHERE PEX.PREGEX_TYPE " + comparison);
		stringBfr.append(" ORDER BY PREGEX_DESC");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				
				exams.add(new PregnancyExam(resultSet.getString("PREGEX_ID"),
						resultSet.getString("PREGEX_DESC") ,resultSet.getInt("PREGEX_TYPE"), 
						resultSet.getString("PREGEX_DEFAULT"), resultSet.getString("PREGEX_VALUES")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exams;
	}

	/**
	 * 
	 * @param visitId the id of the {@link PregnancyVisit}
	 * @return a {@link HashMap} wit the id of the {@link PregnancyExam} as key and the
	 *  {@link PregnancyExamResult} asvalue;
	 */
	public HashMap<String, PregnancyExamResult> getExamResults(int visitId) {
		HashMap<String, PregnancyExamResult> result = new HashMap<String, PregnancyExamResult>();
		StringBuffer stringBfr = new StringBuffer(
				"SELECT * FROM PREGNANCYEXAMRESULT ");
		stringBfr.append("WHERE PEXRES_PVIS_ID= " + visitId);
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				PregnancyExamResult res = new PregnancyExamResult();
				res.setVisitid(resultSet.getInt("PEXRES_PVIS_ID"));
				res.setExamCode(resultSet.getString("PEXRES_PREGEX_ID"));
				res.setOutcome(resultSet.getString("PEXRES_OUTCOME"));
				res.setPregnancyExamResultId(resultSet.getInt("PEXRES_ID"));
				result.put(res.getExamCode(), res);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * inserts a set of {@link PregnancyExamResult} in the database
	 * @param visitid the id of the {@link PregnancyVisit}
	 * @param examresults the list of {@link PregnancyExamResult}
	 * @return true if the examresults are inserted correctly
	 */
	public boolean insertExamResult(int visitid,
			ArrayList<PregnancyExamResult> examresults) {
		StringBuffer stringBfr = new StringBuffer(
				"INSERT INTO PREGNANCYEXAMRESULT (	PEXRES_PVIS_ID , 	PEXRES_PREGEX_ID , PEXRES_OUTCOME) VALUES ");
		for (int a = 0; a < examresults.size() - 1; a++) {
			PregnancyExamResult ex = examresults.get(a);
			stringBfr.append(" (" + visitid + " , '" + ex.getExamCode()
					+ "' , '" + ex.getOutcome() + "'), ");
		}
		PregnancyExamResult ex = examresults.get(examresults.size() - 1);
		stringBfr.append(" (" + visitid + " , '" + ex.getExamCode() + "' , '"
				+ ex.getOutcome() + "');");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param visitId
	 *            the id of the {@link PregnancyVisit}
	 * @return true if all the {@link PregnancyExamResult} for 
	 * the specified visit are deleted
	 */
	public boolean deletePregnancyExamResults(int visitId) {
		StringBuffer stringBfr = new StringBuffer(
				"DELETE FROM PREGNANCYEXAMRESULT WHERE PEXRES_PVIS_ID = "
						+ visitId);
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param visitid
	 *            the id of the {@link PregnancyVisit}
	 * @param examCode
	 *            the id of the {@link PregnancyExam}
	 * @param outcome
	 *            the result of the {@link PregnancyExamResult}
	 * @return true if the tuple is updated correctly
	 */
	public boolean updateExamResult(int visitid, String examCode, String outcome) {

		StringBuffer stringBfr = new StringBuffer(
				"UPDATE PREGNANCYEXAMRESULT SET PEXRES_OUTCOME= '"
						+ outcome + "' ");
		stringBfr.append("WHERE PEXRES_PVIS_ID = " + visitid
				+ " AND PEXRES_PREGEX_ID= '" + examCode + "'");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
    
		
}
