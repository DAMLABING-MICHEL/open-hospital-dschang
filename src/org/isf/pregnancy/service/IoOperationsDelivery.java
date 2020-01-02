package org.isf.pregnancy.service;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.model.Delivery;
import org.isf.utils.db.DbQuery;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
public class IoOperationsDelivery {

	/**
	 * 
	 * @param admId
	 *            the id of the admission record
	 * @param deliveryresult
	 *            the id of the deliverytyperesult table
	 * @param weight
	 *            the weight associated to the newborn
	 * @param sex
	 *            the sex of the newborn
	 * @return true if the tuple is inserted correctly
	 */
	public boolean insertPregnancyDelivery(int admId, Delivery delivery) {
		java.sql.Timestamp create_date = new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime());
		java.sql.Timestamp modify_date = new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime());
		
		StringBuffer stringBfr = new StringBuffer("INSERT INTO PREGNANCYDELIVERY ");
		stringBfr.append("(	PDEL_ADM_ID, PDEL_DRT_ID_A, PDEL_WEIGHT, PDEL_SEX,");
		stringBfr.append("PDEL_DATE_DEL,PDEL_DLT_ID_A,PDEL_HIV_STATUT,PDEL_FATHER_NAME,PDEL_FATHER_OCCUPATION,"
				+ " PDEL_FATHER_RESIDENCE, PDEL_FATHER_AGE, PDEL_FATHER_BIRTH_PLACE, PDEL_CHILD_NAME, PDEL_CREATE_BY, PDEL_CREATE_DATE, PDEL_MODIFY_BY, PDEL_MODIFY_DATE)");
		
		stringBfr.append("VALUES ( " + admId + " , ");
		if(delivery.getDelrestypeid()==null)
			stringBfr.append("NULL" + " , ");
		else
			stringBfr.append("'" +delivery.getDelrestypeid() +"' , ");
		stringBfr.append(delivery.getWeight()+" , '"+delivery.getSex()+" ', ");
		
		java.sql.Timestamp visitdate = new Timestamp(new GregorianCalendar()
		.getTime().getTime());
		if (delivery.getDeliveryDate() != null)
			visitdate = new java.sql.Timestamp(delivery.getDeliveryDate().getTime().getTime());
		stringBfr.append("'"+ visitdate+"', ");
		if(delivery.getDeltypeid()== null)
		  //stringBfr.append("NULL" + " )");
			stringBfr.append("NULL" + " ,");
		else
		  //stringBfr.append("'" +delivery.getDeltypeid() +"') ");
			stringBfr.append("'" +delivery.getDeltypeid() +"', ");
		stringBfr.append("'"+ delivery.getHiv_status()+"', ");
		
		stringBfr.append("'"+ delivery.getFather_name()+"', ");
		stringBfr.append("'"+ delivery.getFather_occupation()+"', ");
		stringBfr.append("'"+ delivery.getFather_residence()+"', ");
		stringBfr.append("'"+ delivery.getFather_age()+"', ");
		stringBfr.append("'"+ delivery.getFather_birth_place()+"', ");
		stringBfr.append("'"+ delivery.getChild_name()+"', ");
		stringBfr.append("'"+ MainMenu.getUser()+"', ");
		stringBfr.append("'"+ create_date+"', ");
		stringBfr.append("'"+ MainMenu.getUser()+"', ");
		stringBfr.append("'"+ modify_date+"') ");
		DbQuery dbQuery = new DbQuery();
		try {

			return dbQuery.setData(stringBfr.toString(), true);
		} catch (SQLException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		}
		return false;

	}

	/**
	 * Deletes all the delivery records related to an admission
	 * 
	 * @param admId
	 *            the id of the Admission tuple
	 * @return true if the record is deleted correctly
	 */
	public boolean deleteAllDeliveryOfAdmission(int admId) {
		StringBuffer stringBfr = new StringBuffer();
		stringBfr.append("DELETE FROM PREGNANCYDELIVERY WHERE PDEL_ADM_ID =( ");
		stringBfr.append(admId + ")");
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
	 * Deletes one single record of the pregnancydelivery table
	 * 
	 * @param admId
	 *            the id of the {@link Admission}
	 * @return true if the record is deleted correctly
	 */
	public boolean deleteSinglePregnancyDelivery(int pregdelId) {
		StringBuffer stringBfr = new StringBuffer(
				"DELETE FROM PREGNANCYDELIVERY WHERE PDEL_ID = " + pregdelId
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


	/**
	 * 
	 * @param admId
	 *            the id of the {@link Admission}
	 * @return a List of Deliveries related to the {@link Admission}
	 */
	public ArrayList<Delivery> getDeliveriesOfAdmission(int admId) {
		ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
		String sqlString = "SELECT PDEL.* FROM PREGNANCYDELIVERY PDEL "
				+ "WHERE PDEL.PDEL_ADM_ID = "
				+ admId
				+ " "
				+ "ORDER BY PDEL.PDEL_ID ASC";
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(sqlString, true);
			while (resultSet.next()) {
				Delivery del = new Delivery();
				
				del.setDelrestypeid(resultSet.getString("PDEL_DRT_ID_A"));
				del.setId(resultSet.getInt("PDEL_ID"));
				del.setSex(resultSet.getString("PDEL_SEX"));
				del.setWeight(resultSet.getFloat("PDEL_WEIGHT"));
				del.setDeltypeid(resultSet.getString("PDEL_DLT_ID_A"));
				GregorianCalendar deliverydate = new GregorianCalendar();
				deliverydate.setTime(resultSet.getDate("PDEL_DATE_DEL"));
				del.setDeliveryDate(deliverydate);
				
				del.setHiv_status(resultSet.getString("PDEL_HIV_STATUT"));
				del.setFather_age(resultSet.getInt("PDEL_FATHER_AGE"));
				del.setFather_birth_place(resultSet.getString("PDEL_FATHER_BIRTH_PLACE"));
				del.setFather_occupation(resultSet.getString("PDEL_FATHER_OCCUPATION"));
				del.setFather_residence(resultSet.getString("PDEL_FATHER_RESIDENCE"));
				del.setFather_name(resultSet.getString("PDEL_FATHER_NAME"));
				del.setChild_name(resultSet.getString("PDEL_CHILD_NAME"));
				deliveries.add(del);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deliveries;

	}
	/**
	 * 
	 * @param patId
	 *            the id of the {@link Patient}
	 * @return a {@link HashMap} with the deliveryresulttype as key and the
	 *         number of such deliveryresulttype as value
	 */
	public HashMap<String, Integer> getDeliveryCount(int patId) {
		HashMap<String, Integer> delCount = new HashMap<String, Integer>();
		StringBuffer stringBfr = new StringBuffer();
		stringBfr.append("Select ADM.*, PDEL.*, DRT.DRT_DESC ");
		stringBfr
				.append("FROM ADMISSION ADM RIGHT JOIN PREGNANCYDELIVERY PDEL ON ");
		stringBfr.append("PDEL.PDEL_ADM_ID = ADM.ADM_ID ");
		stringBfr
				.append("LEFT JOIN DELIVERYRESULTTYPE DRT ON DRT.DRT_ID_A = PDEL.PDEL_DRT_ID_A ");
		stringBfr.append("WHERE ADM.ADM_DELETED = 'N' AND ADM.ADM_PAT_ID =  ");
		stringBfr.append(patId);
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			while (resultSet.next()) {
				String drtdescStr = "";
				Object drtdesc = resultSet.getObject("DRT_DESC");
				if (drtdesc == null)
					drtdescStr = MessageBundle.getMessage("angal.pregnancy.unknowndeliveryresult");
				else
					drtdescStr = drtdesc.toString();

				if (delCount.containsKey(drtdescStr)) {
					Integer val = delCount.get(drtdescStr);
					val++;
					delCount.put((String) drtdescStr, val);
				} else {
					delCount.put((String) drtdescStr, new Integer(1));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return delCount;
	}
	/**
	 * 
	 * @param patId
	 *            the id of the {@link Patient}
	 * @return the total number of Pregnancy visits performed by the Patient
	 */
	public int selectVisitCount(int patId) {
		int visits = 0;
		StringBuffer stringBfr = new StringBuffer();
		stringBfr
				.append("SELECT COUNT(*) AS COUNT FROM PREGNANCYVISIT PVIS, PREGNANCY PREG ");
		stringBfr
				.append("WHERE PVIS.PVIS_PREG_ID = PREG.PREG_ID AND PREG.PREG_PAT_ID = ");
		stringBfr.append(patId);
		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			resultSet.next();
			visits = resultSet.getInt("COUNT");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return visits;
	}
	
	public HashMap<String, Integer> getCountDelivery(int year) throws OHException, ParseException{
		String query1 = "SELECT count(PDEL_ID) as TOTAL  from pregnancydelivery "
				+ " where  PDEL_DATE_DEL BETWEEN ? AND ? and PDEL_DATE_DEL IS NOT NULL";
	
		List<Object> parameters1 = new ArrayList<Object>(2);
		
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
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalDelivery", resultSet.getInt("TOTAL"));
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
