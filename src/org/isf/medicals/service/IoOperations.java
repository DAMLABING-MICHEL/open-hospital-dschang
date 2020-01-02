/**
 * 11-dec-2005
 * 14-jan-2006
 * author bob
 */
package org.isf.medicals.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.isf.medicals.model.*;
import org.isf.medicalstock.model.Lot;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.generaldata.MessageBundle;

/**
 * This class offers the io operations for recovering and managing medical
 * records from the database
 * 
 * @author bob modified by alex: - column product code - column pieces per
 *         packet
 */
public class IoOperations {

	/**
	 * Retrieves the specified {@link Medical}.
	 * 
	 * @param code
	 *            the medical code
	 * @return the stored medical.
	 * @throws OHException
	 *             if an error occurs retrieving the stored medical.
	 */
	public Medical getMedical(int code) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(code);
		String query = "select * from MEDICALDSR join MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A where MDSR_ID = ?";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			if (resultSet.next()) {

				MedicalType medicalType = new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"),
						resultSet.getString("MDSRT_DESC"), resultSet.getString("MDSRT_ACCOUNT"),
						resultSet.getString("MDSRT_EXPENSE_ACCOUNT"));

				Medical medical = new Medical(resultSet.getInt("MDSR_ID"), medicalType,
						resultSet.getString("MDSR_CODE"), resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"), resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"), resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"),
						resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING"));
				return medical;

			} else
				return null;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}


	public List<MedicalLot> getMedicalLots() throws OHException {

		List<MedicalLot> medicalLots = new ArrayList<MedicalLot>();
		List<MedicalLot> resultList = new ArrayList<MedicalLot>();
		List<Medical> medList = getMedicals();
		
		for (Iterator<Medical> iterator = medList.iterator(); iterator.hasNext();) {
			Medical medical = (Medical) iterator.next();
			MedicalLot medLot = new MedicalLot();
			medLot.setMedical(medical);
			medLot.setParent(true);
			resultList.add(medLot);
		}
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String chargeQuery = "select MEDICALDSR.*, MEDICALDSRTYPE.*, LT_ID_A,LT_PREP_DATE,LT_DUE_DATE, LT_REDUCTION_RATE,SUM(MMV_QTY) as qty,LT_COST from "
					+ "((MEDICALDSRLOT join MEDICALDSRSTOCKMOV on MMV_LT_ID_A=LT_ID_A) join MEDICALDSR on MMV_MDSR_ID=MDSR_ID)"
					+ " join MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A JOIN MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A "
					+ " where (MMVT_TYPE='+') group by LT_ID_A order by MDSRT_ID_A, MDSR_ID, LT_DUE_DATE";

			ResultSet resultSet = dbQuery.getData(chargeQuery, false);
			medicalLots = new ArrayList<MedicalLot>(resultSet.getFetchSize());
			while (resultSet.next()) {
				MedicalLot medicalLot = toMedicalLot(resultSet);
				medicalLot.getLot().setQuantity(resultSet.getInt("qty"));
				medicalLots.add(medicalLot);
			}
			String dischargeQuery = "select sum(MMV_QTY) as SUM_MMV_QTY, MMV_LT_ID_A from 	MEDICALDSRSTOCKMOV where (MMV_MMVT_ID_A='discharge') group by MMV_LT_ID_A";
			resultSet = dbQuery.getData(dischargeQuery, false);
			ArrayList<Lot> lotList = new ArrayList<Lot>();
			while(resultSet.next()){
				Lot lot = new Lot(resultSet.getString("MMV_LT_ID_A"), null, null);
				lot.setQuantity(resultSet.getDouble("SUM_MMV_QTY"));
				lotList.add(lot);
			}
			for (MedicalLot medLot : medicalLots) {
				for (Lot lot : lotList) {
					if (medLot.getLot().getCode().equals(lot.getCode()))
						medLot.getLot().setQuantity(medLot.getLot().getQuantity() - lot.getQuantity());
				}
			}
			for (MedicalLot medLot : medicalLots) {
				if (medLot.getLot().getQuantity() != 0)
					for (Iterator<MedicalLot> iterator = resultList.iterator(); iterator.hasNext();) {

						MedicalLot medLotParent = (MedicalLot) iterator.next();
						int parentCode = medLotParent.getMedical().getCode();
						int childCode = medLot.getMedical().getCode();
						if (parentCode == childCode) {
							medLotParent.addChild(medLot);
						}
					}
			}
			dbQuery.commit();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return resultList;
	}
	
	public List<MedicalLot> getMedicalLots(int sTART_INDEX, int pAGE_SIZE) throws OHException {

		List<MedicalLot> medicalLots = new ArrayList<MedicalLot>();
		List<MedicalLot> resultList = new ArrayList<MedicalLot>();
		List<Medical> medList = getMedicals(sTART_INDEX, pAGE_SIZE);
		
		for (Iterator<Medical> iterator = medList.iterator(); iterator.hasNext();) {
			Medical medical = (Medical) iterator.next();
			MedicalLot medLot = new MedicalLot();
			medLot.setMedical(medical);
			medLot.setParent(true);
			resultList.add(medLot);
		}
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String chargeQuery = "select MEDICALDSR.*, MEDICALDSRTYPE.*, LT_ID_A,LT_PREP_DATE,LT_DUE_DATE, LT_REDUCTION_RATE,SUM(MMV_QTY) as qty,LT_COST from "
					+ "((MEDICALDSRLOT join MEDICALDSRSTOCKMOV on MMV_LT_ID_A=LT_ID_A) join MEDICALDSR on MMV_MDSR_ID=MDSR_ID)"
					+ " join MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A JOIN MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A "
					+ " where (MMVT_TYPE='+') group by LT_ID_A order by MDSRT_ID_A, MDSR_ID, LT_DUE_DATE ";

		
			ResultSet resultSet = dbQuery.getData(chargeQuery, false);
			medicalLots = new ArrayList<MedicalLot>(resultSet.getFetchSize());
			while (resultSet.next()) {
				MedicalLot medicalLot = toMedicalLot(resultSet);
				medicalLot.getLot().setQuantity(resultSet.getInt("qty"));
				medicalLots.add(medicalLot);
			}
			String dischargeQuery = "select sum(MMV_QTY) as SUM_MMV_QTY, MMV_LT_ID_A from 	MEDICALDSRSTOCKMOV where (MMV_MMVT_ID_A='discharge') group by MMV_LT_ID_A";
			resultSet = dbQuery.getData(dischargeQuery, false);
			ArrayList<Lot> lotList = new ArrayList<Lot>();
			while(resultSet.next()){
				Lot lot = new Lot(resultSet.getString("MMV_LT_ID_A"), null, null);
				lot.setQuantity(resultSet.getDouble("SUM_MMV_QTY"));
				lotList.add(lot);
			}
			for (MedicalLot medLot : medicalLots) {
				for (Lot lot : lotList) {
					if (medLot.getLot().getCode().equals(lot.getCode()))
						medLot.getLot().setQuantity(medLot.getLot().getQuantity() - lot.getQuantity());
				}
			}
			for (MedicalLot medLot : medicalLots) {
				if (medLot.getLot().getQuantity() != 0)
					for (Iterator<MedicalLot> iterator = resultList.iterator(); iterator.hasNext();) {

						MedicalLot medLotParent = (MedicalLot) iterator.next();
						int parentCode = medLotParent.getMedical().getCode();
						int childCode = medLot.getMedical().getCode();
						if (parentCode == childCode) {
							medLotParent.addChild(medLot);
						}
					}
			}
			dbQuery.commit();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return resultList;
	}
	
	public List<MedicalLot> getMedicalLots(String description) throws OHException {

		if (description == null || description.trim().equals("")) {
			return getMedicalLots();
		}
		List<MedicalLot> medicalLots = new ArrayList<MedicalLot>();
		List<MedicalLot> resultList = new ArrayList<MedicalLot>();
		List<Medical> medList = getMedicals(description);
		for (Iterator<Medical> iterator = medList.iterator(); iterator.hasNext();) {
			Medical medical = (Medical) iterator.next();
			MedicalLot medLot = new MedicalLot();
			medLot.setMedical(medical);
			medLot.setParent(true);
			resultList.add(medLot);
		} 
		
		List<Object> parameters = Collections.<Object>singletonList(description);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String chargeQuery = "select MEDICALDSR.*, MEDICALDSRTYPE.*, MDSR_ID, MDSR_CODE, LT_ID_A,LT_PREP_DATE,LT_DUE_DATE, LT_REDUCTION_RATE,SUM(MMV_QTY) as qty,LT_COST from "
					+ "((MEDICALDSRLOT join MEDICALDSRSTOCKMOV on MMV_LT_ID_A=LT_ID_A) join MEDICALDSR on MMV_MDSR_ID=MDSR_ID)"
					+ " join MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A JOIN MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A "
					+ " where (MDSRT_DESC like ? AND MMVT_TYPE='+') group by LT_ID_A order by MDSRT_ID_A, MDSR_ID, LT_DUE_DATE";

			ResultSet resultSet = dbQuery.getDataWithParams(chargeQuery, parameters, false);
			medicalLots = new ArrayList<MedicalLot>(resultSet.getFetchSize());
			while (resultSet.next()) {
				MedicalLot medicalLot = toMedicalLot(resultSet);
				medicalLot.getLot().setQuantity(resultSet.getInt("qty"));
				medicalLots.add(medicalLot);
			}
			for (MedicalLot medLot : medicalLots) {
				parameters = Collections.<Object>singletonList(medLot.getLot().getCode());
				String dischargeQuery = "select SUM(MMV_QTY) as qty2 from "
					+ "MEDICALDSRSTOCKMOV join MEDICALDSRSTOCKMOVTYPE on MMV_MMVT_ID_A=MMVT_ID_A "
					+ "where (MMVT_TYPE='-') and (MMV_LT_ID_A=?) group by MMV_LT_ID_A order by MMV_QTY desc";
				ResultSet resultSet2 = dbQuery.getDataWithParams(dischargeQuery, parameters, false);
				resultSet2.next();
				if (resultSet2.first())
					medLot.getLot().setQuantity(medLot.getLot().getQuantity() - resultSet2.getInt("qty2"));
			}

			for (MedicalLot medLot : medicalLots) {
				if (medLot.getLot().getQuantity() != 0)
					for (Iterator<MedicalLot> iterator = resultList.iterator(); iterator.hasNext();) {
						MedicalLot medLotParent = (MedicalLot) iterator.next();
						int parentCode = medLotParent.getMedical().getCode();
						int childCode = medLot.getMedical().getCode();
						if (parentCode == childCode) {
							medLotParent.addChild(medLot);
						}
					}
			}
			dbQuery.commit();

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return resultList;
	}

	private MedicalLot toMedicalLot(ResultSet resultSet) throws SQLException, OHException {

		String code = resultSet.getString("LT_ID_A");
		GregorianCalendar preparationDate = toCalendar(resultSet.getDate("LT_PREP_DATE"));
		GregorianCalendar dueDate = toCalendar(resultSet.getDate("LT_DUE_DATE"));
		Double cost = resultSet.getDouble("LT_COST");
		Double reduction_rate = resultSet.getDouble("LT_REDUCTION_RATE");

		Lot lot = new Lot(code, preparationDate, dueDate, cost, reduction_rate);
		MedicalLot medLot = new MedicalLot();
		medLot.setLot(lot);

		MedicalType medicalType = new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"),
				resultSet.getString("MDSRT_DESC"), resultSet.getString("MDSRT_ACCOUNT"),
				resultSet.getString("MDSRT_EXPENSE_ACCOUNT"));

		Medical med = new Medical(resultSet.getInt("MDSR_ID"), medicalType, resultSet.getString("MDSR_CODE"),
				resultSet.getString("MDSR_DESC"), resultSet.getDouble("MDSR_INI_STOCK_QTI"),
				resultSet.getInt("MDSR_PCS_X_PCK"), resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
				resultSet.getDouble("MDSR_IN_QTI"), resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"),
				resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING"));

		medLot.setMedical(med);
		return medLot;
	}

	/**
	 * Converts the specified {@link java.sql.Date} to a
	 * {@link GregorianCalendar}.
	 * 
	 * @param date
	 *            the date to convert.
	 * @return the converted date.
	 */
	protected GregorianCalendar toCalendar(Date date) {
		if (date == null)
			return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * Converts the specified time in milliseconds to a
	 * {@link GregorianCalendar}.
	 * 
	 * @param date
	 *            the date to convert.
	 * @return the converted date.
	 */
	protected GregorianCalendar toCalendar(Long time) {
		if (time == null)
			return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	/**
	 * Retrieves the specified {@link Medical}.
	 * 
	 * @param code
	 *            the medical code
	 * @return the stored medical.
	 * @throws OHException
	 *             if an error occurs retrieving the stored medical.
	 */
	public Medical getMedical(String code) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(code);
		String query = "select * from MEDICALDSR join MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A where MDSR_CODE = ?";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			if (resultSet.next()) {

				MedicalType medicalType = new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"),
						resultSet.getString("MDSRT_DESC"), resultSet.getString("MDSRT_ACCOUNT"),
						resultSet.getString("MDSRT_EXPENSE_ACCOUNT"));

				Medical medical = new Medical(resultSet.getInt("MDSR_ID"), medicalType,
						resultSet.getString("MDSR_CODE"), resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"), resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"), resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"),
						resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING"));
				return medical;

			} else
				return null;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Gets all stored {@link Medical}s.
	 * @param pAGE_SIZE 
	 * @param sTART_INDEX 
	 * 
	 * @return all the stored medicals.
	 * @throws OHException
	 *             if an error occurs retrieving the stored medicals.
	 */
	public ArrayList<Medical> getMedicals() throws OHException {
		return getMedicals(null);
	}
	public ArrayList<Medical> getMedicals(int sTART_INDEX, int pAGE_SIZE) throws OHException {
		return getMedicals(null, sTART_INDEX, pAGE_SIZE);
	}
	public ArrayList<Medical> getMedicals1() throws OHException {
		return getMedicals2(null);
	}
	public ArrayList<Medical> getMedicals2() throws OHException { //MARCO include MEDIUMPRICE
		return getMedicals3(null);
	}
	public ArrayList<Medical> getMedicals4(String s) throws OHException { //MARCO include MEDIUMPRICE
		return getMedicals3(s);
	}
	/**
	 * Retrieves all stored {@link Medical}s. If a description value is provides
	 * the medicals are filtered.
	 * 
	 * @param description
	 *            the medical description.
	 * @return the stored medicals.
	 * @throws OHException
	 *             if an error occurs retrieving the stored medicals.
	 */
	public ArrayList<Medical> getMedicals(String description, int start_index, int page_size) throws OHException {
		ArrayList<Medical> medicals = null;
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();
		query.append("select * from MEDICALDSR join MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (description != null &&  !description.equals("")) {
			query.append("where MDSRT_DESC like ? ");
			parameters.add(description);
		}
		parameters.add(start_index);
		parameters.add(page_size);
		query.append("order BY MDSR_MDSRT_ID_A DESC, MDSR_DESC LIMIT ?, ?");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			medicals = new ArrayList<Medical>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicals.add(new Medical(resultSet.getInt("MDSR_ID"),
					new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"), resultSet.getString("MDSRT_DESC")),
					resultSet.getString("MDSR_CODE"), resultSet.getString("MDSR_DESC"),
					resultSet.getDouble("MDSR_INI_STOCK_QTI"), resultSet.getInt("MDSR_PCS_X_PCK"),
					resultSet.getDouble("MDSR_MIN_STOCK_QTI"), resultSet.getDouble("MDSR_IN_QTI"),
					resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"),// resultSet.getDouble("LASTPRICE"),
					resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING")));
			}
			
						
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicals;
	}
	
	public ArrayList<Medical> getMedicals(String description) throws OHException {
		ArrayList<Medical> medicals = null;
		List<Object> parameters = new ArrayList<Object>();

		StringBuilder query = new StringBuilder();
		query.append("select * from MEDICALDSR join MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (description != null) {
			query.append("where MDSRT_DESC like ? ");
			parameters.add(description);
		}
		query.append("order BY MDSR_MDSRT_ID_A DESC, MDSR_DESC ");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			medicals = new ArrayList<Medical>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicals.add(new Medical(resultSet.getInt("MDSR_ID"),
					new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"), resultSet.getString("MDSRT_DESC")),
					resultSet.getString("MDSR_CODE"), resultSet.getString("MDSR_DESC"),
					resultSet.getDouble("MDSR_INI_STOCK_QTI"), resultSet.getInt("MDSR_PCS_X_PCK"),
					resultSet.getDouble("MDSR_MIN_STOCK_QTI"), resultSet.getDouble("MDSR_IN_QTI"),
					resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"),// resultSet.getDouble("LASTPRICE"),
					resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING")));
			}
						
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicals;
	}
	
	public ArrayList<Medical> getMedicals2(String description) throws OHException {
		ArrayList<Medical> medicals = null;
		List<Object> parameters = new ArrayList<Object>();

		StringBuilder query = new StringBuilder();
		query.append("select MD.MDSR_ID, "
				+ "MD.MDSR_MDSRT_ID_A, "
				+ "MD.MDSR_CODE, "
				+ "MD.MDSR_DESC, "
				+ "MD.MDSR_MIN_STOCK_QTI, "
				+ "MD.MDSR_INI_STOCK_QTI, "
				+ "MD.MDSR_PCS_X_PCK, "
				+ "MD.MDSR_IN_QTI,"
				+ "MD.MDSR_OUT_QTI,"
				+ "MD.MDSR_LOCK, "
				+ "MD.MDSR_MDSRT_ID_A, "
				+ "MD.MDSR_CONDITIONING, "
				+ "MD.MDSR_SHAPE, "
				+ "MD.MDSR_DOSING, "
				+ "MT.MDSRT_ID_A,"
				+ "MT.MDSRT_DESC, "
				+ "( SELECT LT_COST AS PRICE FROM MEDICALDSRSTOCKMOV "
				+ " JOIN medicaldsrlot ON MMV_LT_ID_A=LT_ID_A WHERE MMV_MDSR_ID = MD.MDSR_ID and MMV_MMVT_ID_A= 'charge' ORDER BY MMV_ID DESC LIMIT 1  ) as LASTPRICE "
				+ "from MEDICALDSR MD join MEDICALDSRTYPE MT on MDSR_MDSRT_ID_A = MDSRT_ID_A ");
				
		if (description != null) {
			query.append("where MDSRT_DESC like ? ");
			parameters.add(description);
		}

		query.append("order BY MDSR_MDSRT_ID_A DESC, MDSR_DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			medicals = new ArrayList<Medical>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicals.add(new Medical(resultSet.getInt("MDSR_ID"),
						new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"), resultSet.getString("MDSRT_DESC")),
						resultSet.getString("MDSR_CODE"), resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"), resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"), resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"), resultSet.getDouble("LASTPRICE"),
						resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicals;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public ArrayList<Medical> getMedicals3(String description, int start_index, int page_size) throws OHException { //Ajout calcul mediumPrice
	
		ArrayList<Medical> medicals = null;
		List<Object> parameters = new ArrayList<Object>();

		StringBuilder query = new StringBuilder();
		query.append("select MD.MDSR_ID, "
				+ "MD.MDSR_MDSRT_ID_A, "
				+ "MD.MDSR_CODE, "
				+ "MD.MDSR_DESC, "
				+ "MD.MDSR_MIN_STOCK_QTI, "
				+ "MD.MDSR_INI_STOCK_QTI, "
				+ "MD.MDSR_PCS_X_PCK, "
				+ "MD.MDSR_IN_QTI,"
				+ "MD.MDSR_OUT_QTI,"
				+ "MD.MDSR_LOCK, "
				+ "MD.MDSR_MDSRT_ID_A, "
				+ "MD.MDSR_CONDITIONING, "
				+ "MD.MDSR_SHAPE, "
				+ "MD.MDSR_DOSING, "
				+ "MT.MDSRT_ID_A,"
				+ "MT.MDSRT_DESC, "
				+ "( SELECT LT_COST AS PRICE FROM MEDICALDSRSTOCKMOV "
				+ " JOIN medicaldsrlot ON MMV_LT_ID_A=LT_ID_A WHERE MMV_MDSR_ID = MD.MDSR_ID and MMV_MMVT_ID_A= 'charge' ORDER BY MMV_ID DESC LIMIT 1  ) as LASTPRICE "
	
				+ " from MEDICALDSR MD join MEDICALDSRTYPE MT on MDSR_MDSRT_ID_A = MDSRT_ID_A ");
		if (description != null) {
			query.append("where MDSRT_DESC like ? ");
			parameters.add(description);
		}
		query.append("order BY MDSR_MDSRT_ID_A DESC, MDSR_DESC  Limit ?, ?");
		parameters.add(start_index);
		parameters.add(page_size);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			medicals = new ArrayList<Medical>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicals.add(new Medical(
						resultSet.getInt("MDSR_ID"),
						new MedicalType(
							resultSet.getString("MDSR_MDSRT_ID_A"), 
							resultSet.getString("MDSRT_DESC")
						),
						resultSet.getString("MDSR_CODE"), 
						resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"), 
						resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"), 
						resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"), 
						resultSet.getInt("MDSR_LOCK"), 
						resultSet.getDouble("LASTPRICE"),
						resultSet.getString("MDSR_CONDITIONING"),
						resultSet.getString("MDSR_SHAPE"),
						resultSet.getString("MDSR_DOSING")
					)
				);				
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicals;
	}
	
	
	public ArrayList<Medical> getMedicals3(String description) throws OHException { //Ajout calcul mediumPrice
		
		ArrayList<Medical> medicals = null;
		List<Object> parameters = new ArrayList<Object>();

		StringBuilder query = new StringBuilder();
		query.append("select MD.MDSR_ID, "
				+ "MD.MDSR_MDSRT_ID_A, "
				+ "MD.MDSR_CODE, "
				+ "MD.MDSR_DESC, "
				+ "MD.MDSR_MIN_STOCK_QTI, "
				+ "MD.MDSR_INI_STOCK_QTI, "
				+ "MD.MDSR_PCS_X_PCK, "
				+ "MD.MDSR_IN_QTI,"
				+ "MD.MDSR_OUT_QTI,"
				+ "MD.MDSR_LOCK, "
				+ "MD.MDSR_MDSRT_ID_A, "
				+ "MD.MDSR_CONDITIONING, "
				+ "MD.MDSR_SHAPE, "
				+ "MD.MDSR_DOSING, "
				+ "MT.MDSRT_ID_A,"
				+ "MT.MDSRT_DESC, "
				+ "( SELECT LT_COST AS PRICE FROM MEDICALDSRSTOCKMOV "
				+ " JOIN medicaldsrlot ON MMV_LT_ID_A=LT_ID_A WHERE MMV_MDSR_ID = MD.MDSR_ID and MMV_MMVT_ID_A= 'charge' ORDER BY MMV_ID DESC LIMIT 1  ) as LASTPRICE "
	
				+ " from MEDICALDSR MD join MEDICALDSRTYPE MT on MDSR_MDSRT_ID_A = MDSRT_ID_A ");
		if (description != null) {
			query.append("where MDSRT_DESC like ? ");
			parameters.add(description);
		}
		query.append("order BY MDSR_MDSRT_ID_A DESC, MDSR_DESC ");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			medicals = new ArrayList<Medical>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicals.add(new Medical(
						resultSet.getInt("MDSR_ID"),
						new MedicalType(
							resultSet.getString("MDSR_MDSRT_ID_A"), 
							resultSet.getString("MDSRT_DESC")
						),
						resultSet.getString("MDSR_CODE"), 
						resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"), 
						resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"), 
						resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"), 
						resultSet.getInt("MDSR_LOCK"), 
						resultSet.getDouble("LASTPRICE"),
						resultSet.getString("MDSR_CONDITIONING"),
						resultSet.getString("MDSR_SHAPE"),
						resultSet.getString("MDSR_DOSING")
					)
				);				
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicals;
	}

	/**
	 * Retrieves the stored {@link Medical}s based on the specified filter
	 * criteria.
	 * 
	 * @param description
	 *            the medical description or <code>null</code>
	 * @param type
	 *            the medical type or <code>null</code>
	 * @param expiring
	 *            <code>true</code> if include only expiring medicals.
	 * @return the retrieved medicals.
	 * @throws OHException
	 *             if an error occurs retrieving the medicals.
	 */
	public ArrayList<Medical> getMedicals(String description, String type, boolean expiring) throws OHException {
		ArrayList<Medical> medicals = null;

		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();
		query.append("select * from MEDICALDSR join MEDICALDSRTYPE on MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (description != null) {
			query.append("where ");
			query.append("(MDSR_DESC like ? OR MDSR_CODE like ?) ");
			parameters.add("%" + description + "%");
			parameters.add("%" + description + "%");
		}

		if (type != null) {
			if (parameters.size() == 0)
				query.append("where ");
			else
				query.append("and ");

			query.append("(MDSRT_ID_A=?) ");
			parameters.add(type);
		}

		if (expiring) {
			if (parameters.size() == 0)
				query.append("where ");
			else
				query.append("and ");

			query.append("((MDSR_INI_STOCK_QTI+MDSR_IN_QTI-MDSR_OUT_QTI)<MDSR_MIN_STOCK_QTI) ");
		}

		query.append("order BY MDSR_MDSRT_ID_A, MDSR_DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			medicals = new ArrayList<Medical>(resultSet.getFetchSize());
			while (resultSet.next()) {
				
				medicals.add(new Medical(resultSet.getInt("MDSR_ID"),
						new MedicalType(resultSet.getString("MDSR_MDSRT_ID_A"), resultSet.getString("MDSRT_DESC")),
						resultSet.getString("MDSR_CODE"), resultSet.getString("MDSR_DESC"),
						resultSet.getDouble("MDSR_INI_STOCK_QTI"), resultSet.getInt("MDSR_PCS_X_PCK"),
						resultSet.getDouble("MDSR_MIN_STOCK_QTI"), resultSet.getDouble("MDSR_IN_QTI"),
						resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"),
						resultSet.getString("MDSR_CONDITIONING"), resultSet.getString("MDSR_SHAPE"), resultSet.getString("MDSR_DOSING")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicals;
	}

	/**
	 * Checks if the specified {@link Medical} exists or not.
	 * 
	 * @param medical
	 *            the medical to check.
	 * @return <code>true</code> if exists <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the check.
	 */
	public boolean medicalExists(Medical medical) throws OHException {
		boolean result = false;
		List<Object> parameters = new ArrayList<Object>(2);
		parameters.add(medical.getType().getCode());
		parameters.add(medical.getDescription());
		String query = "select MDSR_ID from MEDICALDSR where MDSR_MDSRT_ID_A = ? and MDSR_DESC = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
			result = set.first();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Stores the specified {@link Medical}.
	 * 
	 * @param medical
	 *            the medical to store.
	 * @return <code>true</code> if the medical has been stored,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs storing the medical.
	 */
	public boolean newMedical(Medical medical) throws OHException {

		List<Object> parameters = new ArrayList<Object>(5);
		parameters.add(medical.getType().getCode());
		parameters.add(medical.getProd_code());
		parameters.add(medical.getDescription());
		parameters.add(medical.getMinqty());
		parameters.add(medical.getPcsperpck());
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		
		parameters.add(medical.getConditioning());
		parameters.add(medical.getShape());
		parameters.add(medical.getDosing());
		String query = "insert into MEDICALDSR (MDSR_MDSRT_ID_A , MDSR_CODE, MDSR_DESC, MDSR_MIN_STOCK_QTI, MDSR_PCS_X_PCK,"
				+ "MDSR_CREATE_BY, MDSR_CREATE_DATE, MDSR_CONDITIONING, MDSR_SHAPE, MDSR_DOSING) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			ResultSet rs = dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, true);
			if (rs.first()) {
				medical.setCode(rs.getInt(1));
				result = true;
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	public boolean getStockSheet(GregorianCalendar dateFrom, GregorianCalendar dateTo, Medical medical){
		
		return false;
	}
	/**
	 * Returns the stored medical lock value.
	 * 
	 * @param code
	 *            the medical code.
	 * @return the stored lock value.
	 * @throws OHException
	 *             if an error occurs retrieving the lock value.
	 */
	public int getMedicalLock(int code) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(code);

		String query = "select MDSR_LOCK from MEDICALDSR where MDSR_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
			if (set.first())
				return set.getInt(1);
			else
				return -1;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Updates the specified {@link Medical}.
	 * 
	 * @param medical
	 *            the medical to update.
	 * @return <code>true</code> if the medical has been updated
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the update.
	 */
	public boolean updateMedical(Medical medical) throws OHException {

		boolean result = false;

		List<Object> parameters = new ArrayList<Object>(5);
		parameters.add(medical.getDescription());
		parameters.add(medical.getProd_code());
		parameters.add(medical.getType().getCode());
		parameters.add(medical.getPcsperpck());
		parameters.add(medical.getMinqty());
		
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));

		parameters.add(medical.getConditioning());
		parameters.add(medical.getShape());
		parameters.add(medical.getDosing());
		parameters.add(medical.getCode());
		
		String query = "update MEDICALDSR set MDSR_DESC = ?, MDSR_CODE = ?, MDSR_MDSRT_ID_A = ?,  MDSR_PCS_X_PCK = ?, "
				+ "MDSR_LOCK = MDSR_LOCK + 1 , MDSR_MIN_STOCK_QTI = ? , "
				+ "MDSR_MODIFY_BY = ? , MDSR_MODIFY_DATE = ? , MDSR_CONDITIONING = ?, MDSR_SHAPE = ?, MDSR_DOSING = ? where MDSR_ID = ?";
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		if (result)
			medical.setLock(getMedicalLock(medical.getCode()));
		return result;
	}

	/**
	 * Checks if the specified {@link Medical} is referenced in stock movement.
	 * 
	 * @param code
	 *            the medical code.
	 * @return <code>true</code> if the medical is referenced,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the check.
	 */
	public boolean isMedicalReferencedInStockMovement(int code) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(code);
		String query = "select * from MEDICALDSRSTOCKMOV where MMV_MDSR_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet rs = dbQuery.getDataWithParams(query, parameters, true);
			return rs.first();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Deletes the specified {@link Medical}.
	 * 
	 * @param medical
	 *            the medical to delete.
	 * @return <code>true</code> if the medical has been deleted,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the medical deletion.
	 */
	public boolean deleteMedical(Medical medical) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(medical.getCode());
		String query = "delete from MEDICALDSR where MDSR_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	public boolean deleteMedical(int medicalID) throws OHException {

		List<Object> parameters = Collections.<Object>singletonList(medicalID);
		String query = "delete from MEDICALDSR where MDSR_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	public Double getMediumQuantity(Integer code) throws OHException {
		
		GregorianCalendar today;
		GregorianCalendar before;
		List<Object> parameters = new ArrayList<Object>();
		Double mediumQuantity = 0.0;
		
		StringBuilder query = new StringBuilder();
		query.append(" SELECT SUM(BLI_QTY) AS MEDIUMQTY FROM BILLITEMS "
				+ " JOIN BILLS ON BLL_ID = BLI_ID_BILL "
				+ " WHERE BLI_ITEM_ID = ? and BLL_DATE > ? and BLL_DATE < ?  ");
		
		today = new GregorianCalendar();
		before = new GregorianCalendar();
		before.add(Calendar.MONTH, -10);
		
		parameters.add(code);
		parameters.add(before.getTime());
		parameters.add(today.getTime());
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			resultSet.next();
			if (resultSet.first())	mediumQuantity = round(resultSet.getDouble("MEDIUMQTY"), 2);
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return mediumQuantity/10;
	}

	
	public int getMedicalsTotalRows(String typologie) throws OHException {
		int Total_Ligne = 0;
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();
		query.append("select count(*) AS Total_Ligne from MEDICALDSR MD join MEDICALDSRTYPE MT on MDSR_MDSRT_ID_A = MDSRT_ID_A ");
		
		if (typologie != null && !typologie.equals("")) {
			query.append("where MDSRT_DESC like ? "); 
			parameters.add(typologie);
		}
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			if (resultSet.next()) {
				Total_Ligne = resultSet.getInt("Total_Ligne");
			}
			return Total_Ligne;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

}
