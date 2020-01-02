package org.isf.medicalstockward.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

/**
 * @author mwithi
 */
public class IoOperations {

	
	/**
	 * Get all {@link MovementWard}s with the specified criteria.
	 * 
	 * @param wardId
	 *            the ward id.
	 * @param dateFrom
	 *            the lower bound for the movement date range.
	 * @param dateTo
	 *            the upper bound for the movement date range.
	 * @param  
	 * @param start_index 
	 * @return the retrieved movements.
	 * @throws OHException
	 *             if an error occurs retrieving the movements.
	 */
	public ArrayList<MovementWard> getWardMovements(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo, MedicalType medicalTypeSelected)
			throws OHException {

		ArrayList<MovementWard> movements = null;

		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();

		query.append("SELECT * FROM ((((MEDICALDSRSTOCKMOVWARD LEFT JOIN ");
		query.append(
				"(PATIENT LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID ORDER BY PEX_DATE DESC) AS HW ON PAT_ID = HW.PEX_PAT_ID) ON MMVN_PAT_ID = PAT_ID) JOIN ");
		query.append("WARD ON MMVN_WRD_ID_A = WRD_ID_A)) JOIN ");
		query.append("MEDICALDSR ON MMVN_MDSR_ID = MDSR_ID) JOIN ");
		query.append("MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (wardId != null || dateFrom != null || dateTo != null || medicalTypeSelected != null)
			query.append("WHERE ");

		if (wardId != null && !wardId.equals("")) {
			if (parameters.size() != 0)
				query.append("AND ");
			parameters.add(wardId);
			query.append("WRD_ID_A = ? ");
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (parameters.size() != 0)
				query.append("AND ");
			query.append("MMVN_DATE > ? AND MMVN_DATE < ? ");
			parameters.add(toTimestamp(dateFrom));
			parameters.add(toTimestamp(dateTo));
		}
		
		if (medicalTypeSelected != null && !medicalTypeSelected.getCode().equals("")) {
			if (parameters.size() != 0)
				query.append("AND ");
			query.append(" MDSR_MDSRT_ID_A = ? ");
			parameters.add(medicalTypeSelected.getCode());
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			movements = new ArrayList<MovementWard>(resultSet.getFetchSize());

			while (resultSet.next()) {
				MovementWard movementWard = toMovementWard(resultSet);
				movements.add(movementWard);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return movements;
	}
	
	public int getRowCount(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo, MedicalType medicalTypeSelected)
			throws OHException {

		int total_row = 0;

		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();

		query.append("SELECT count(*) as TOTAL_ROWS FROM ((((MEDICALDSRSTOCKMOVWARD LEFT JOIN ");
		query.append(
				"(PATIENT LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID ORDER BY PEX_DATE DESC) AS HW ON PAT_ID = HW.PEX_PAT_ID) ON MMVN_PAT_ID = PAT_ID) JOIN ");
		query.append("WARD ON MMVN_WRD_ID_A = WRD_ID_A)) JOIN ");
		query.append("MEDICALDSR ON MMVN_MDSR_ID = MDSR_ID) JOIN ");
		query.append("MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (wardId != null || dateFrom != null || dateTo != null || medicalTypeSelected != null)
			query.append("WHERE ");

		if (wardId != null && !wardId.equals("")) {
			if (parameters.size() != 0)
				query.append("AND ");
			parameters.add(wardId);
			query.append("WRD_ID_A = ? ");
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (parameters.size() != 0)
				query.append("AND ");
			query.append("MMVN_DATE > ? AND MMVN_DATE < ? ");
			parameters.add(toTimestamp(dateFrom));
			parameters.add(toTimestamp(dateTo));
		}
		
		if (medicalTypeSelected != null && !medicalTypeSelected.getCode().equals("")) {
			if (parameters.size() != 0)
				query.append("AND ");
			query.append(" MDSR_MDSRT_ID_A = ? ");
			parameters.add(medicalTypeSelected.getCode());
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			if (resultSet.next()) {
				total_row = resultSet.getInt("TOTAL_ROWS");
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return total_row;
	}
	
	
	public ArrayList<MovementWard> getWardMovements(String wardId, GregorianCalendar dateFrom, GregorianCalendar dateTo, MedicalType medicalTypeSelected, int start_index, int page_size)
			throws OHException {

		ArrayList<MovementWard> movements = null;

		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();

		query.append("SELECT * FROM ((((MEDICALDSRSTOCKMOVWARD LEFT JOIN ");
		query.append(
				"(PATIENT LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID ORDER BY PEX_DATE DESC) AS HW ON PAT_ID = HW.PEX_PAT_ID) ON MMVN_PAT_ID = PAT_ID) JOIN ");
		query.append("WARD ON MMVN_WRD_ID_A = WRD_ID_A)) JOIN ");
		query.append("MEDICALDSR ON MMVN_MDSR_ID = MDSR_ID) JOIN ");
		query.append("MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");

		if (wardId != null || dateFrom != null || dateTo != null || medicalTypeSelected != null)
			query.append("WHERE ");

		if (wardId != null && !wardId.equals("")) {
			if (parameters.size() != 0)
				query.append("AND ");
			parameters.add(wardId);
			query.append("WRD_ID_A = ? ");
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (parameters.size() != 0)
				query.append("AND ");
			query.append("MMVN_DATE > ? AND MMVN_DATE < ? ");
			parameters.add(toTimestamp(dateFrom));
			parameters.add(toTimestamp(dateTo));
		}
		
		if (medicalTypeSelected != null && !medicalTypeSelected.getCode().equals("")) {
			if (parameters.size() != 0)
				query.append("AND ");
			query.append(" MDSR_MDSRT_ID_A = ? ");
			parameters.add(medicalTypeSelected.getCode());
		}
		
		parameters.add(start_index);
		parameters.add(page_size);
		query.append(" LIMIT ?, ?");

		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			movements = new ArrayList<MovementWard>(resultSet.getFetchSize());

			while (resultSet.next()) {
				MovementWard movementWard = toMovementWard(resultSet);
				movements.add(movementWard);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return movements;
	}

	/**
	 * Extracts a {@link MovementWard} from the current {@link ResultSet} row.
	 * 
	 * @param resultSet
	 *            the resultset.
	 * @return the extracted movement ward.
	 * @throws SQLException
	 *             if an error occurs during the extraction.
	 */
	protected MovementWard toMovementWard(ResultSet resultSet) throws SQLException {
		Patient patient;
		if (resultSet.getBoolean("MMVN_IS_PATIENT")) {
			patient = toPatient(resultSet);
		} else
			patient = new Patient();

		Ward ward = toWard(resultSet);

		Ward wardFrom = null; // to be set!!!!!!

		Medical medical = toMedical(resultSet);
		// MovementWard movementWard = new MovementWard(ward,
		// toCalendar(resultSet.getTimestamp("MMVN_DATE")),
		// resultSet.getBoolean("MMVN_IS_PATIENT"), patient,
		// resultSet.getInt("MMVN_PAT_AGE"),
		// resultSet.getFloat("MMVN_PAT_WEIGHT"),
		// resultSet.getString("MMVN_DESC"), medical,
		// resultSet.getDouble("MMVN_MDSR_QTY"),
		// resultSet.getString("MMVN_MDSR_UNITS"));
		MovementWard movementWard = new MovementWard(ward, toCalendar(resultSet.getTimestamp("MMVN_DATE")),
				resultSet.getBoolean("MMVN_IS_PATIENT"), patient, resultSet.getInt("MMVN_PAT_AGE"),
				resultSet.getFloat("MMVN_PAT_WEIGHT"), resultSet.getString("MMVN_DESC"), medical,
				resultSet.getDouble("MMVN_MDSR_QTY"), resultSet.getString("MMVN_MDSR_UNITS"), wardFrom);

		movementWard.setCode(resultSet.getInt("MMVN_ID"));
		return movementWard;
	}

	/**
	 * Extracts a {@link Patient} from the current {@link ResultSet} row.
	 * 
	 * @param resultSet
	 *            the resultset.
	 * @return the extracted patient.
	 * @throws SQLException
	 *             if an error occurs during the extraction.
	 */
	protected Patient toPatient(ResultSet resultSet) throws SQLException {
		Patient patient = new Patient();
		patient.setCode(resultSet.getInt("PAT_ID"));
		patient.setFirstName(resultSet.getString("PAT_FNAME"));
		patient.setSecondName(resultSet.getString("PAT_SNAME"));
		patient.setAddress(resultSet.getString("PAT_ADDR"));
		patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
		patient.setAge(resultSet.getInt("PAT_AGE"));
		patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
		try {patient.setSex(resultSet.getString("PAT_SEX").charAt(0));}catch (Exception e) {patient.setSex(' ');}
		patient.setCity(resultSet.getString("PAT_CITY"));
		patient.setTelephone(resultSet.getString("PAT_TELE"));
		patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
		patient.setBloodType(resultSet.getString("PAT_BTYPE"));
		try {patient.setFather(resultSet.getString("PAT_FATH").charAt(0));}catch (Exception e) {patient.setFather(' ');}
		patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
		try {patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));}catch (Exception e) {patient.setMother(' ');}
		patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
		try {patient.setHasInsurance(resultSet.getString("PAT_ESTA").charAt(0));}catch (Exception e) {patient.setHasInsurance(' ');}
		try {patient.setParentTogether(resultSet.getString("PAT_PTOGE").charAt(0));}catch (Exception e) {patient.setParentTogether(' ');}
		patient.setNote(resultSet.getString("PAT_NOTE"));
		patient.setHeight(resultSet.getFloat("PAT_HEIGHT"));
		patient.setWeight(resultSet.getFloat("PAT_WEIGHT"));
		patient.setLock(resultSet.getInt("PAT_LOCK"));
		return patient;
	}

	/**
	 * Converts a {@link ResultSet} row into an {@link Ward} object.
	 * 
	 * @param resultSet
	 *            the result set to read.
	 * @return the converted object.
	 * @throws SQLException
	 *             if an error occurs.
	 */
	private Ward toWard(ResultSet resultSet) throws SQLException {
		Ward ward = new Ward(resultSet.getString("WRD_ID_A"), resultSet.getString("WRD_NAME"),
				resultSet.getString("WRD_TELE"), resultSet.getString("WRD_FAX"), resultSet.getString("WRD_EMAIL"),
				resultSet.getInt("WRD_NBEDS"), resultSet.getInt("WRD_NQUA_NURS"), resultSet.getInt("WRD_NDOC"),
				resultSet.getBoolean("WRD_IS_PHARMACY"), resultSet.getBoolean("WRD_IS_MALE"),
				resultSet.getBoolean("WRD_IS_FEMALE"), resultSet.getInt("WRD_LOCK"));
		return ward;
	}

	/**
	 * Extracts a {@link Medical} from the current {@link ResultSet} row.
	 * 
	 * @param resultSet
	 *            the result set.
	 * @return the extracted medical.
	 * @throws SQLException
	 *             if an error occurs during the extraction.
	 */
	protected Medical toMedical(ResultSet resultSet) throws SQLException {
		MedicalType medicalType = toMedicalType(resultSet);
		Medical medical = new Medical(resultSet.getInt("MDSR_ID"), medicalType, resultSet.getString("MDSR_CODE"),
				resultSet.getString("MDSR_DESC"), resultSet.getDouble("MDSR_INI_STOCK_QTI"),
				resultSet.getInt("MDSR_PCS_X_PCK"), resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
				resultSet.getDouble("MDSR_IN_QTI"), resultSet.getDouble("MDSR_OUT_QTI"), resultSet.getInt("MDSR_LOCK"));
		return medical;
	}

	/**
	 * Extract a {@link MedicalType} from the current {@link ResultSet} row.
	 * 
	 * @param resultSet
	 *            the result set.
	 * @return the extracted medical type.
	 * @throws SQLException
	 *             if an error occurs during the extraction.
	 */
	protected MedicalType toMedicalType(ResultSet resultSet) throws SQLException {
		MedicalType medicalType = new MedicalType(resultSet.getString("MDSRT_ID_A"), resultSet.getString("MDSRT_DESC"));
		return medicalType;
	}

	/**
	 * Gets the current quantity for the specified {@link Medical}.
	 * 
	 * @param ward
	 *            if specified medical are filtered by the {@link Ward}.
	 * @param medical
	 *            the medical to check.
	 * @return the total quantity.
	 * @throws OHException
	 *             if an error occurs retrieving the quantity.
	 */
	public int getCurrentQuantity(Ward ward, Medical medical) throws OHException {

		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder query = new StringBuilder();

		query.append(
				"SELECT SUM(MMV_QTY) MAIN FROM MEDICALDSRSTOCKMOV M WHERE MMV_MMVT_ID_A = 'discharge' AND MMV_MDSR_ID = ? ");
		parameters.add(medical.getCode());

		if (ward != null) {
			parameters.add(ward.getCode());
			query.append(" AND MMV_WRD_ID_A = ?");
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			resultSet.next();
			int mainQuantity = resultSet.getInt("MAIN");

			query = new StringBuilder();
			parameters.clear();

			query.append("SELECT SUM(MMVN_MDSR_QTY) DISCHARGE FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_MDSR_ID = ?");
			parameters.add(medical.getCode());

			if (ward != null) {
				parameters.add(ward.getCode());
				query.append(" AND MMV_WRD_ID_A = ?");
			}

			resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			resultSet.next();
			int dischargeQuantity = resultSet.getInt("DISCHARGE");

			resultSet.close();
			return mainQuantity - dischargeQuantity;

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	public int getCurrentQuantity(String wardID, int medicalID) throws OHException {

		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder query = new StringBuilder();

		query.append(
				"SELECT SUM(MMV_QTY) MAIN,MMV_MMVT_ID_A,MMV_MDSR_ID,MMV_WRD_ID_A FROM MEDICALDSRSTOCKMOV M WHERE MMV_MMVT_ID_A = 'discharge' AND MMV_MDSR_ID = ? ");
		parameters.add(medicalID);

		if (wardID != "0") {
			parameters.add(wardID);
			query.append(" AND MMV_WRD_ID_A = ?");
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			resultSet.next();
			int mainQuantity = resultSet.getInt("MAIN");

			query = new StringBuilder();
			parameters.clear();

			query.append("SELECT SUM(MMVN_MDSR_QTY) DISCHARGE FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_MDSR_ID = ?");
			parameters.add(medicalID);

			if (wardID != "0") {
				parameters.add(wardID);
				query.append(" AND MMV_WRD_ID_A = ?");
			}

			resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			resultSet.next();
			int dischargeQuantity = resultSet.getInt("DISCHARGE");

			resultSet.close();
			return mainQuantity - dischargeQuantity;

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores the specified {@link Movement}.
	 * 
	 * @param movement
	 *            the movement to store.
	 * @return <code>true</code> if has been stored, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs.
	 */
	public boolean newMovementWard(MovementWard movement) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			boolean stored = newMovementWard(dbQuery, movement);

			dbQuery.commit();

			return stored;
		} finally {

			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores the specified {@link Movement} list.
	 * 
	 * @param movements
	 *            the movement to store.
	 * @return <code>true</code> if the movements have been stored,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs.
	 */
	public boolean newMovementWard(ArrayList<MovementWard> movements) throws OHException {
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			for (MovementWard movement : movements) {

				boolean inserted = newMovementWard(dbQuery, movement);
				if (!inserted) {
					dbQuery.rollback();
					return false;
				}
			}
			dbQuery.commit();
			return true;
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores the specified {@link MovementWard}.
	 * 
	 * @param dbQuery
	 *            the {@link DbQueryLogger} to use.
	 * @param movement
	 *            the movement ward to store.
	 * @return <code>true</code> if has been stored, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs.
	 */
	protected boolean newMovementWard(DbQueryLogger dbQuery, MovementWard movement) throws OHException {
		MovBrowserManager manager = new MovBrowserManager();
	//	Integer stock_after = manager.getStockQty(movement.getMedical().getCode(), new Timestamp(movement.getDate().getTimeInMillis()), movement.getWard().getCode(), "MEDICALDSRSTOCKMOVWARD");
		String query = "INSERT INTO MEDICALDSRSTOCKMOVWARD (MMVN_WRD_ID_A, MMVN_DATE, MMVN_IS_PATIENT, MMVN_PAT_ID, MMVN_PAT_AGE, MMVN_PAT_WEIGHT, MMVN_DESC, "
				+ "MMVN_MDSR_ID, MMVN_MDSR_QTY, "
				+ "MMVN_MDSR_UNITS, MMVN_WRD_ID_A_FROM, MMVN_CREATE_BY, MMVN_CREATE_DATE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		List<Object> parameters = new ArrayList<Object>(10);

		parameters.add(movement.getWard().getCode());
		parameters.add(toTimestamp(new GregorianCalendar()));
		parameters.add(movement.isPatient());
		if (movement.isPatient()) {
			parameters.add(movement.getPatient().getCode());
			parameters.add(movement.getPatient().getAge());
			parameters.add(movement.getPatient().getWeight());
		} else {			
			parameters.add("0");
			parameters.add(0);
			parameters.add(0);
		}
		parameters.add(movement.getDescription());
		parameters.add(movement.getMedical().getCode().toString());
		parameters.add(movement.getQuantity());
		parameters.add(movement.getUnits());
		String wardFromCode = null;
		if (movement.getWardFrom() != null) {
			wardFromCode = movement.getWardFrom().getCode();
		}
		parameters.add(wardFromCode);
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
	//	parameters.add(stock_after - movement.getQuantity()); //Calcul du stock de pavillon apres mouvement de vente
		
		boolean inserted = dbQuery.setDataWithParams(query, parameters, false);

		if (inserted) {			
			boolean updated = updateStockWardQuantity(dbQuery, movement);
			
			return updated;
		}
		return false;
	}

	/**
	 * Updates the quantity for the specified movement ward.
	 * 
	 * @param dbQuery
	 *            the {@link DbQueryLogger} to use.
	 * @param movement
	 *            the movement ward to update.
	 * @return <code>true</code> if has been updated, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs during the update.
	 */
	protected boolean updateStockWardQuantity(DbQueryLogger dbQuery, MovementWard movement) throws OHException {

		List<Object> parameters = new ArrayList<Object>(3);
		String query = "UPDATE MEDICALDSRWARD SET MDSRWRD_OUT_QTI = MDSRWRD_OUT_QTI + ?, MDSRWRD_MODIFY_BY= ?, MDSRWRD_MODIFY_DATE= ?"
				+ " WHERE MDSRWRD_WRD_ID_A = ? AND MDSRWRD_MDSR_ID = ?";
		parameters.add(movement.getQuantity());
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));		
		parameters.add(movement.getWard().getCode());
		parameters.add(movement.getMedical().getCode());


		boolean updated = dbQuery.setDataWithParams(query, parameters, false);

		if (!updated)
			dbQuery.rollback();

		return updated;
	}

	protected boolean updateStockWardQuantityIn(DbQueryLogger dbQuery, MovementWard movement) throws OHException {

		List<Object> parameters = new ArrayList<Object>(3);
		String query = "UPDATE MEDICALDSRWARD SET MDSRWRD_IN_QTI = MDSRWRD_IN_QTI + ? , MDSRWRD_MODIFY_BY= ?, MDSRWRD_MODIFY_DATE= ? WHERE MDSRWRD_WRD_ID_A = ? AND MDSRWRD_MDSR_ID = ?";
		parameters.add(movement.getQuantity());
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		parameters.add(movement.getWardFrom().getCode());
		parameters.add(movement.getMedical().getCode());

		boolean updated = dbQuery.setDataWithParams(query, parameters, false);

		if (!updated)
			dbQuery.rollback();

		return updated;
	}

	/**
	 * Updates the specified {@link MovementWard}.
	 * 
	 * @param movement
	 *            the movement ward to update.
	 * @return <code>true</code> if has been updated, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs during the update.
	 */
	public boolean updateMovementWard(MovementWard movement) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			List<Object> parameters = new ArrayList<Object>(11);
			String query = "UPDATE MEDICALDSRSTOCKMOVWARD SET MMVN_WRD_ID_A = ?, " + "MMVN_DATE = ?, "
					+ "MMVN_IS_PATIENT = ?, " + "MMVN_PAT_ID = ?, " + "MMVN_PAT_AGE = ?, " + "MMVN_PAT_WEIGHT = ?, "
					+ "MMVN_DESC = ?, " + "MMVN_MDSR_ID = ?, " + "MMVN_MDSR_QTY = ?, " + "MMVN_MDSR_UNITS = ? "
							+ ", MMVN_MODIFY_BY= ?, MMVN_MODIFY_DATE= ?"
					+ "WHERE MMVN_ID = ?";

			parameters.add(movement.getWard().getCode());
			parameters.add(toTimestamp(movement.getDate()));
			parameters.add(movement.isPatient());
			if (movement.isPatient())
				parameters.add(movement.getPatient().getCode().toString());
			else
				parameters.add("0");
			parameters.add(movement.getAge());
			parameters.add(movement.getWeight());
			parameters.add(movement.getDescription());
			parameters.add(movement.getMedical().getCode().toString());
			parameters.add(movement.getQuantity());
			parameters.add(movement.getUnits());
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(movement.getCode());

			boolean updated = dbQuery.setDataWithParams(query, parameters, true);
			return updated;

		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Deletes the specified {@link MovementWard}.
	 * 
	 * @param movement
	 *            the movement ward to delete.
	 * @return <code>true</code> if the movement has been deleted,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the delete.
	 */
	public boolean deleteMovementWard(MovementWard movement) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			List<Object> parameters = Collections.<Object>singletonList(movement.getCode());
			String query = "DELETE FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_ID = ?";

			boolean deleted = dbQuery.setDataWithParams(query, parameters, true);

			return deleted;

		} finally {
			dbQuery.releaseConnection();
		}
	}

	public boolean deleteMovementWard(int movementID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			List<Object> parameters = Collections.<Object>singletonList(movementID);
			String query = "DELETE FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_ID = ?";

			boolean deleted = dbQuery.setDataWithParams(query, parameters, true);

			return deleted;

		} finally {
			dbQuery.releaseConnection();
		}
	}

	public MovementWard getMovementWardById(int movementID) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		String query = "SELECT * FROM MEDICALDSRSTOCKMOVWARD WHERE MMVN_ID = ?";
		MovementWard movementWard = null;

		try {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(movementID);

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			while (resultSet.next()) {
				movementWard = toMovementWard(resultSet);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return movementWard;
	}

	/**
	 * Gets all the {@link Medical}s associated to specified {@link Ward}.
	 * 
	 * @param wardId
	 *            the ward id.
	 * @return the retrieved medicals.
	 * @throws OHException
	 *             if an error occurs during the medical retrieving.
	 */
	public ArrayList<MedicalWard> getMedicalsWard(String wardId) throws OHException {

		ArrayList<MedicalWard> medicalWards = new ArrayList<MedicalWard>();

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = Collections.<Object>singletonList(wardId);
			StringBuilder query = new StringBuilder(
					"SELECT MEDICALDSR.*, MDSRT_ID_A, MDSRT_DESC, MDSRWRD_WRD_ID_A, MDSRWRD_IN_QTI - MDSRWRD_OUT_QTI AS QTY ");
			query.append(
					"FROM (MEDICALDSRWARD JOIN MEDICALDSR ON MDSRWRD_MDSR_ID = MDSR_ID) JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
			query.append("WHERE MDSRWRD_WRD_ID_A = ? ");
			query.append("ORDER BY MDSR_DESC");

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			while (resultSet.next()) {
				Medical medical = toMedical(resultSet);
				MedicalWard medicalWard = new MedicalWard(medical, resultSet.getDouble("QTY"));
				medicalWards.add(medicalWard);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}

		return medicalWards;
	}
	public ArrayList<MedicalWard> getMedicalsWard2(String wardId) throws OHException {

		ArrayList<MedicalWard> medicalWards = new ArrayList<MedicalWard>();

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = Collections.<Object>singletonList(wardId);
			StringBuilder query = new StringBuilder(
					"SELECT MEDICALDSR.*, MDSRT_ID_A, MDSRT_DESC, MDSRWRD_WRD_ID_A, MDSRWRD_MDSR_ID, MDSRWRD_IN_QTI - MDSRWRD_OUT_QTI AS QTY, MDSRWRD_INI_STOCK_QTI, "
					+ " ( SELECT LT_COST AS PRICE FROM MEDICALDSRSTOCKMOV "
				+ " JOIN medicaldsrlot ON MMV_LT_ID_A=LT_ID_A WHERE MMV_MDSR_ID = MDSRWRD_MDSR_ID and MMV_MMVT_ID_A= 'charge' ORDER BY MMV_ID DESC LIMIT 1  ) as LASTPRICE ");
			query.append(
					"FROM (MEDICALDSRWARD JOIN MEDICALDSR ON MDSRWRD_MDSR_ID = MDSR_ID) JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
			query.append("WHERE MDSRWRD_WRD_ID_A = ? ");
			query.append("ORDER BY MDSR_DESC");

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			while (resultSet.next()) {
				Medical medical = toMedical(resultSet);
				//MedicalWard medicalWard = new MedicalWard(medical, resultSet.getDouble("QTY"),resultSet.getDouble("LASTPRICE"));
				MedicalWard medicalWard = new MedicalWard(medical, resultSet.getDouble("QTY"),resultSet.getDouble("LASTPRICE"), resultSet.getDouble("MDSRWRD_INI_STOCK_QTI"));
				medicalWards.add(medicalWard);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}

		return medicalWards;
	}

	/**
	 * Gets a {@link Medical}s associated to specified {@link Ward}.
	 * 
	 * @param wardId
	 *            the ward id.
	 * @param medId
	 *            the medical id.
	 * @return the retrieved medical.
	 * @throws OHException
	 *             if an error occurs during the medical retrieving.
	 */
	public MedicalWard getMedicalsWard(String wardId, int medId) throws OHException {

		MedicalWard medicalWard = null;

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			List<Object> parameters = new ArrayList<Object>(2);

			parameters.add(wardId);
			parameters.add(medId);

			StringBuilder query = new StringBuilder(
					"SELECT MEDICALDSR.*, MDSRT_ID_A, MDSRT_DESC, MDSRWRD_WRD_ID_A, MDSRWRD_IN_QTI - MDSRWRD_OUT_QTI AS QTY ");
			query.append(
					"FROM (MEDICALDSRWARD JOIN MEDICALDSR ON MDSRWRD_MDSR_ID = MDSR_ID) JOIN MEDICALDSRTYPE ON MDSR_MDSRT_ID_A = MDSRT_ID_A ");
			query.append("WHERE MDSRWRD_WRD_ID_A = ? ");
			query.append(" AND MDSRWRD_MDSR_ID = ? ");
			query.append("ORDER BY MDSR_DESC");

			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			if (resultSet.next()) {
				Medical medical = toMedical(resultSet);
				medicalWard = new MedicalWard(medical, resultSet.getDouble("QTY"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}

		return medicalWard;
	}

	/**
	 * Converts a {@link GregorianCalendar} to a {@link Timestamp}.
	 * 
	 * @param calendar
	 *            the calendar to convert.
	 * @return the converted value or <code>null</code> if the passed value is
	 *         <code>null</code>.
	 */
	protected Timestamp toTimestamp(GregorianCalendar calendar) {
		if (calendar == null)
			return null;
		return new Timestamp(calendar.getTime().getTime());
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
	 * Converts the specified {@link java.sql.Timestamp} to a
	 * {@link GregorianCalendar}.
	 * 
	 * @param timestamp
	 *            the timestamp to convert.
	 * @return the converted timestamp.
	 */
	public GregorianCalendar toCalendar(Timestamp timestamp) {
		if (timestamp == null)
			return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(timestamp);
		return calendar;
	}

	public boolean updateMedicalsWard(ArrayList<MedicalWard> medSearch, Ward wardSelected) throws OHException {
		List<Object> parameters = new ArrayList<Object>(3);
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean updated = true;
		try {
			String query = "UPDATE MEDICALDSRWARD SET MDSRWRD_INI_STOCK_QTI = ?, MDSRWRD_MODIFY_BY= ?, MDSRWRD_MODIFY_DATE= ?"
					+ " WHERE MDSRWRD_WRD_ID_A = ? AND MDSRWRD_MDSR_ID = ?";
			for (MedicalWard medicalWard : medSearch) {
				parameters.add(medicalWard.getInitialstock());
				parameters.add(MainMenu.getUser());
				parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));	
				parameters.add(wardSelected.getCode());
				parameters.add(medicalWard.getMedical().getCode());
				updated = dbQuery.setDataWithParams(query, parameters, false);
				if (!updated) dbQuery.rollback();
				parameters.clear();
			} 
			dbQuery.commit();
		} finally {
			dbQuery.releaseConnection();
		}
		return updated;
	}
	
}
