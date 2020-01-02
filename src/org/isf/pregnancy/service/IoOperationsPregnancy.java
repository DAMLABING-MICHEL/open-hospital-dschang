package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.isf.menu.gui.MainMenu;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.utils.db.DbQuery;
import org.isf.utils.time.TimeTools;


/**
 *@author Martin Reinstadler
 * this class performs database readings and insertions for the table:
 * PREGNANCY
 *
 */
public class IoOperationsPregnancy {

	/**
	 * @param patid
	 *            the id of the {@link PregnancyPatient}
	 * @return the list of pregnancies given a patientcode
	 */
	public ArrayList<Pregnancy> getPregnancy(int patid) {
		ArrayList<Pregnancy> pregnancies = null;
		StringBuffer stringBfr = new StringBuffer("SELECT PREG.*");
		stringBfr.append("FROM PREGNANCY PREG ");
		stringBfr.append("WHERE (PREG_PAT_ID= ");
		stringBfr.append((Integer) patid + ") ");
		stringBfr.append("ORDER BY PREG.PREG_NR DESC");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			pregnancies = new ArrayList<Pregnancy>();
			while (resultSet.next()) {
				Pregnancy preg = new Pregnancy(-1);
				GregorianCalendar lmpcal = new GregorianCalendar();
				lmpcal.setTime(resultSet.getDate("PREG_LMP"));
				preg.setLmp(lmpcal);
				preg.setPregnancynr(resultSet.getInt("PREG_NR"));
				GregorianCalendar prevcal = new GregorianCalendar();
				prevcal.setTime(resultSet.getDate("PREG_CALC_DELIVERY"));
				preg.setScheduled_delivery(prevcal);
				preg.setActive(resultSet.getBoolean("PREG_ACTIVE"));
				preg.setPatId(resultSet.getInt("PREG_PAT_ID"));
				preg.setPregId(resultSet.getInt("PREG_ID"));
				pregnancies.add(preg);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pregnancies;
	}
	
	/**
	 * 
	 * @param pregid
	 *            the id of the {@link Pregnancy}
	 * @return the {@link Pregnancy} for the given id
	 */
	public Pregnancy getPregnancy_byId(int pregid) {
		Pregnancy preg = new Pregnancy(-1);
		StringBuffer stringBfr = new StringBuffer("SELECT PREG.*");
		stringBfr.append("FROM PREGNANCY PREG ");
		stringBfr.append("WHERE (PREG_ID= ");
		stringBfr.append((Integer) pregid + ") ");
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);

			while (resultSet.next()) {
				GregorianCalendar lmpcal = new GregorianCalendar();
				lmpcal.setTime(resultSet.getDate("PREG_LMP"));
				preg.setLmp(lmpcal);
				preg.setPregnancynr(resultSet.getInt("PREG_NR"));
				GregorianCalendar prevcal = new GregorianCalendar();
				prevcal.setTime(resultSet.getDate("PREG_CALC_DELIVERY"));
				preg.setScheduled_delivery(prevcal);
				preg.setActive(resultSet.getBoolean("PREG_ACTIVE"));
				preg.setPatId(resultSet.getInt("PREG_PAT_ID"));
				preg.setPregId(resultSet.getInt("PREG_ID"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return preg;
	}

	/**
	 * @param pregnr
	 *            the number of the patients {@link Pregnancy}
	 * @param patid
	 *            the id of the {@link PregnancyPatient}
	 * @param lmp
	 *            last menstrual period of the {@link Pregnancy}
	 * @param preg_del
	 *            previsted delivery date of the {@link Pregnancy}
	 * @return the id of the last inserted {@link Pregnancy}
	 */
	public int insertPregnancy(int pregnr, int patid, GregorianCalendar lmp,
			GregorianCalendar preg_del) {
		int key = -1;
		java.sql.Date datelmp = new java.sql.Date(lmp.getTimeInMillis());
		java.sql.Date datedel = new java.sql.Date(preg_del.getTimeInMillis());
		StringBuffer stringBfr = new StringBuffer();
		java.sql.Timestamp create_date = new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime());
		stringBfr
				.append("INSERT INTO PREGNANCY (PREG_NR , PREG_PAT_ID , PREG_LMP , PREG_CALC_DELIVERY, PREG_CREATE_BY, PREG_CREATE_DATE) ");
		stringBfr.append("VALUES (" + pregnr + " , " + patid + " , " + "'"
				+ datelmp + "'" + " , " + "'" + datedel + "'," +
				"'"+MainMenu.getUser()+"',"
				+ "'"+ create_date+ "'"+				
				" );");

		
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet rs = dbQuery.setDataReturnGeneratedKey(stringBfr.toString(), true);
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
	 * @param pregnancyId
	 *            the id of the {@link Pregnancy}
	 * @param patcode
	 *            the code of the {@link PregnancyPatient}
	 * @return true if the record is deleted from the database
	 */
	public boolean deletePregnancy(int pregnancyId) {
		StringBuffer stringBfr = new StringBuffer(
				"DELETE FROM PREGNANCY WHERE ");
		stringBfr.append("PREG_ID = " + pregnancyId);
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
	 * @param pregId
	 *            the id of the {@link Pregnancy}
	 * @param new_pregnancynr
	 *            the new number of the {@link Pregnancy}
	 * @param lmp
	 *            the new last menstrual period of the {@link Pregnancy}
	 * @param scheduled_delivery
	 *            the scheduled delivery of the {@link Pregnancy}
	 * @param active
	 *            if the {@link Pregnancy} is active
	 * @return true if the tuple is updated correctly
	 */
	public boolean updatePregnancy(int pregId, int new_pregnancynr,
			GregorianCalendar lmp, GregorianCalendar scheduled_delivery,
			boolean active) {
		java.sql.Timestamp modify_date = new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime());
		java.sql.Date datelmp = new java.sql.Date(lmp.getTimeInMillis());
		java.sql.Date datedel = new java.sql.Date(
				scheduled_delivery.getTimeInMillis());
		String act = active == true ? "Y" : "N";
		StringBuffer stringBfr = new StringBuffer("UPDATE PREGNANCY SET ");
		stringBfr.append("PREG_NR = " + new_pregnancynr + " , ");
		stringBfr.append("PREG_LMP = '" + datelmp + "' , ");
		stringBfr.append("PREG_CALC_DELIVERY = '" + datedel + "' , ");
		stringBfr.append("PREG_ACTIVE = '" + act + "', ");
		
		stringBfr.append("PREG_MODIFY_BY = '" + MainMenu.getUser() + "' , ");
		stringBfr.append("PREG_MODIFY_DATE = '" + modify_date + "' ");
		 
		stringBfr.append("WHERE PREG_ID = " + pregId);
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
