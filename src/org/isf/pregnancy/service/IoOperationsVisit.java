package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.db.DbQuery;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;


/**
 * @author Martin Reinstadler this class performs database readings and
 *         insertions for the tables: PREGNANCY, PREGNANCYVISIT
 * 
 */
public class IoOperationsVisit {
	/**
	 * @param pat_id
	 *            the code of the {@link PregnancyPatient}
	 * @return the list of all pregnancyvisits related to a pregnancy of the
	 *         patient
	 */
	public ArrayList<PregnancyVisit> getPregnancyVisits(int pat_id) {
		ArrayList<PregnancyVisit> pregnancyvisits = new ArrayList<PregnancyVisit>();
		StringBuffer stringBfr = new StringBuffer("SELECT "
				+ "PREG.PREG_ID,"
				+ "PREG.PREG_NR,"
				+ "PREG.PREG_PAT_ID,"
				+ "PREG.PREG_LMP,"
				+ "PREG.PREG_CALC_DELIVERY,"
				+ "PREG.PREG_ACTIVE,"
				
				+ "PVIS.PVIS_ID,"
				+ "PVIS.PVIS_PREG_ID,"
				+ "PVIS.PVIS_DATE,"
				+ "PVIS.PVIS_NEXTDATE,"
				+ "PVIS.PVIS_PTT_ID_A,"
				+ "PVIS.PVIS_NOTE,"
				+ "PVIS.PVIS_TYPE ");
		
		stringBfr.append("FROM PREGNANCY PREG,  PREGNANCYVISIT PVIS ");
		//stringBfr.append("LEFT JOIN PREGNANTTREATMENTTYPE PTT ON PTT.PTT_ID_A = PVIS.PVIS_PTT_ID_A ");
		stringBfr.append("WHERE PREG.PREG_ID= PVIS.PVIS_PREG_ID  "
				+ "AND PREG.PREG_PAT_ID = ");
		stringBfr.append((Integer) pat_id + " ");
		stringBfr.append("UNION ALL (SELECT DISTINCT(ADM.ADM_ID), 0 AS PREG_NR, ADM.ADM_PAT_ID AS PREG_PAT_ID, ");
		stringBfr.append("ADM.ADM_DATE_ADM AS PREG_LMP, ADM.ADM_DATE_ADM AS PREG_CALC_DELIVERY, 'true' AS PREG_ACTIVE, ");
		stringBfr.append("ADM.ADM_ID AS PVIS_ID, ADM.ADM_ID AS PVIS_PREG_ID, PDEL.PDEL_DATE_DEL AS PVIS_DATE, ");
		stringBfr.append("ADM.ADM_DATE_ADM AS PVIS_NEXT_DATE, ");
		stringBfr.append("PDEL.PDEL_DLT_ID_A AS PVIS_PTT_ID_A,ADM.ADM_NOTE AS PVIS_NOTE, ");
		stringBfr.append("0 AS PVIS_TYPE FROM ADMISSION ADM LEFT JOIN PREGNANCYDELIVERY PDEL ON " );
		stringBfr.append("PDEL.PDEL_ADM_ID = ADM.ADM_ID ");
		stringBfr.append(" WHERE ADM.ADM_PAT_ID = ");
		stringBfr.append((Integer) pat_id + " ");
		stringBfr.append(" AND ADM.ADM_DELETED = 'N' AND ");
		stringBfr.append("(PDEL.PDEL_DATE_DEL IS NOT NULL )) ORDER BY PVIS_DATE DESC; ");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				int pregid = resultSet.getInt("PREG_ID");
				PregnancyVisit pvisit = new PregnancyVisit(pat_id, pregid, -1);
				if(resultSet.getObject("PVIS_DATE")!= null)
					pvisit.setTime(resultSet.getTimestamp("PVIS_DATE"));
				else
					pvisit.setTime(new GregorianCalendar().getTime());
				if (resultSet.getString("PVIS_NOTE") != null)
					pvisit.setNote(resultSet.getString("PVIS_NOTE"));
				pvisit.setType(resultSet.getInt("PVIS_TYPE"));
				
				if (resultSet.getObject("PVIS_NEXTDATE") != null){
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(resultSet.getTimestamp("PVIS_NEXTDATE"));
					pvisit.setNextVisitdate1(cal);
				}
				if (resultSet.getObject("PVIS_PTT_ID_A") != null)
					pvisit.setTreatmenttype(resultSet
							.getString("PVIS_PTT_ID_A"));
				pvisit.setVisitId(resultSet.getInt("PVIS_ID"));
				pvisit.setPregnancyNr(resultSet.getInt("PREG_NR"));
				pregnancyvisits.add(pvisit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pregnancyvisits;
	}

	/**
	 * 
	 * @param pregid
	 *            the id of the {@link Pregnancy}
	 * @param date
	 *            the date of the {@link PregnancyVisit}
	 * @param nextdate
	 *            the date of the next scheduled {@link PregnancyVisit}
	 * @param note
	 *            the note of the {@link PregnancyVisit}
	 * @param visittype
	 *            the type of the {@link PregnancyVisit}
	 * @param treatment
	 *            code of the {@link PregnantTreatmentType}
	 * @return the if of the inserted tuple
	 */
	public int insertPregnancyVisit(int pregid, GregorianCalendar date,
			GregorianCalendar nextdate, String note, int visittype,
			String treatment) {
		int key = -1;
		java.sql.Timestamp create_date = new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime());
		java.sql.Timestamp visitdate = new Timestamp(new GregorianCalendar()
				.getTime().getTime());

		if (date != null)
			visitdate = new java.sql.Timestamp(date.getTime().getTime());
		String sqlString = "INSERT INTO PREGNANCYVISIT (	PVIS_PREG_ID , "
				+ "PVIS_DATE , PVIS_NEXTDATE, PVIS_PTT_ID_A, "
				+ "PVIS_NOTE, PVIS_TYPE, PVIS_CREATE_BY, PVIS_CREATE_DATE) VALUES ( "
				+ pregid +" , "
				+ "'"+ visitdate+ "', "
				+ (nextdate == null ? "null, " : "'"
						+ new java.sql.Timestamp(nextdate.getTime().getTime())
						+ "',")
				+ (treatment == null ? "null, " : "'" + treatment + "',")
				+ (note == null ? "null, " : "'" + note + "',") 
				+ visittype +",'"+MainMenu.getUser()+"',"
				+ "'"+ create_date+ "'"+
				
				")";
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet rs = dbQuery.setDataReturnGeneratedKey(sqlString, true);
			if (rs.first()) {
				key = rs.getInt(1);
			}
			dbQuery.releaseConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return key;
	}

	/**
	 * 
	 * @param visitId the id of the {@link PregnancyVisit}
	 * @param date new date of the {@link PregnancyVisit}
	 * @param nextdate the scheduled first next date of the {@link PregnancyVisit}
	 * @param treatmenttype the id of the {@link PregnantTreatmentType}
	 * @param note the note of the visit
	 * @param visittype the type of the visit
	 * @return true if the tuple is updated correctly
	 */
	public boolean updatePregnancyVisit(int visitId, GregorianCalendar date, GregorianCalendar nextdate,
			String treatmenttype, String note, int visittype) {
		DbQuery dbQuery = new DbQuery();
		Connection conn;
		try {
			java.sql.Timestamp modify_date = new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime());
			conn = dbQuery.getConnection();
			String query = "UPDATE PREGNANCYVISIT SET PVIS_DATE = ?, PVIS_NEXTDATE = ?,"
					+ " PVIS_PTT_ID_A = ? , PVIS_NOTE = ? ," +
					" PVIS_TYPE =? , PVIS_MODIFY_BY=?, PVIS_MODIFY_DATE=? WHERE PVIS_ID = ?  ";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setTimestamp(1, new java.sql.Timestamp(date.getTime()
					.getTime()));
			if (nextdate != null)
				pstmt.setTimestamp(2, new java.sql.Timestamp(nextdate.getTime()
						.getTime()));
			else
				pstmt.setNull(2, java.sql.Types.NULL);
			if (treatmenttype != null)
				pstmt.setString(3, treatmenttype);
			else
				pstmt.setNull(3, java.sql.Types.NULL);
			if (note != null)
				pstmt.setString(4, note);
			else
				pstmt.setNull(4, java.sql.Types.NULL);
			
			pstmt.setInt(5, visittype);
			 
			pstmt.setString(6, MainMenu.getUser());
			pstmt.setTimestamp(7, modify_date);
								
			pstmt.setInt(8, visitId);
			pstmt.executeUpdate();
			return true;

		} catch (Exception e) {
			System.out.println(e.toString()+" ERROR ERROR");
		}

		return false;

	}

	/**
	 * 
	 * @param visitid
	 *            the id of the {@link PregnancyVisit}
	 * @return true if the visit is deleted correctly
	 */
	public boolean deleteVisit(int visitid) {
		StringBuffer stringBfr = new StringBuffer(
				"DELETE FROM PREGNANCYVISIT WHERE PVIS_ID = " + visitid
						+ " ;");
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
	
	//////////////////////////////
	public HashMap<String, Integer> getCountCPN(int year) throws OHException, ParseException{
		String query  = "SELECT count(OPD_ID) as TOTAL  from opd where  OPD_DATE BETWEEN ? AND ?";
		String query1 = "SELECT count(PVIS_ID) as TOTAL  from pregnancyvisit where  PVIS_DATE BETWEEN ? AND ? and PVIS_PTT_ID_A =?";
		String query2 = "SELECT count(PVIS_ID) as TOTAL  from pregnancyvisit where  PVIS_DATE BETWEEN ? AND ? and PVIS_PTT_ID_A != ?";
		List<Object> parameters = new ArrayList<Object>(2);
		List<Object> parameters1 = new ArrayList<Object>(3);
		List<Object> parameters2 = new ArrayList<Object>(3);
		
		DateFormat dateFromat = new SimpleDateFormat("dd/MM/yyyy");
		Date dateFrom = new Date();
		Date dateTo = new Date();
		dateFrom = dateFromat.parse("01/01/"+year);
		dateTo = dateFromat.parse("31/12/"+year);
		String stringDateFrom = convertToSQLDateLimited(dateFrom);
		String stringDateTo = convertToSQLDateLimited(dateTo);
		
		parameters.add(stringDateFrom);
		parameters.add(stringDateTo);
		parameters1.add(stringDateFrom);
		parameters1.add(stringDateTo);
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters1.add("N");
		parameters2.add("N");
		
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
//			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
//			if (resultSet.next()) {
//				results.put("total",resultSet.getInt("TOTAL"));
//			}
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalCpnN",resultSet.getInt("TOTAL"));
			}
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalCpnR",resultSet.getInt("TOTAL"));
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
}
