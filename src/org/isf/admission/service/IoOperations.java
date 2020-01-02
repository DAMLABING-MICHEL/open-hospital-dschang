package org.isf.admission.service;

/*----------------------------------------------------------
 * modification history
 * ====================
 * 10/11/06 - ross - removed from the list the deleted patients
 *                   the list is now in alphabetical  order
 * 11/08/08 - alessandro - addedd getFather&Mother Names
 * 26/08/08 - claudio - changed getAge for managing varchar type
 * 					  - added getBirthDate
 * 01/01/09 - Fabrizio - changed the calls to PAT_AGE fields to
 *                       return again an integer type
 * 20/01/09 - Chiara -   restart of progressive number of maternity 
 * 						 ward on 1st July conditioned to parameter 
 * 						 MATERNITYRESTARTINJUNE in generalData.properties                   
 *-----------------------------------------------------------*/

import java.sql.Date;
//import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

public class IoOperations {

	/**
	 * Admission insertion query, if you modify the query please update the related parameters generation method.
	 */
	protected static String ADMISSION_INSERTION_QUERY = "INSERT INTO ADMISSION (" +
			"ADM_IN, ADM_TYPE, ADM_WRD_ID_A, ADM_YPROG, ADM_MPROG, ADM_PAT_ID, ADM_DATE_ADM, ADM_ADMT_ID_A_ADM, ADM_FHU, " +
			"ADM_IN_DIS_ID_A, ADM_OUT_DIS_ID_A, ADM_OUT_DIS_ID_A_2, ADM_OUT_DIS_ID_A_3, ADM_OPE_ID_A, ADM_DATE_OP, " +
			"ADM_RESOP, ADM_DATE_DIS, ADM_DIST_ID_A, ADM_NOTE, ADM_TRANS, ADM_PRG_DATE_VIS, ADM_PRG_PTT_ID_A, " +
			"ADM_PRG_DATE_DEL, ADM_PRG_DLT_ID_A, ADM_PRG_DRT_ID_A, ADM_PRG_WEIGHT, ADM_PRG_DATE_CTRL1, " +
			"ADM_PRG_DATE_CTRL2, ADM_PRG_DATE_ABORT, ADM_USR_ID_A, ADM_LOCK, ADM_DELETED, ADM_CREATE_BY, ADM_CREATE_DATE) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

	/**
	 * Returns all patients with ward in which they are admitted.
	 * @return the patient list with associated ward.
	 * @throws OHException if an error occurs during database request.
	 */
	public ArrayList<AdmittedPatient> getAdmittedPatients() throws OHException {
		return getAdmittedPatients(null);
	}
	
	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list.
	 * @throws OHException if an error occurs during database request.
	 */ 
	public ArrayList<AdmittedPatient> getAdmittedPatientsSearch(String searchTerms) throws OHException {
		ArrayList<AdmittedPatient> patients = null;

		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND PAT_ID = ? "
						+ "OR LOWER(PAT_SNAME) = ? "
						+ "OR LOWER(PAT_FNAME) = ? "
						+ "OR LOWER(PAT_NOTE) = ? "
						+ "OR LOWER(PAT_TAXCODE) = ? ");
				String parameter = term;
				parameters.add(parameter);
				parameters.add(parameter);
				parameters.add(parameter);
				parameters.add(parameter);
				parameters.add(parameter);
			}
		}

		query.append(" ORDER BY PAT_ID DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			patients = new ArrayList<AdmittedPatient>();

			while (resultSet.next()) {
				AdmittedPatient adPat = buildAdmittedPatient(resultSet);
				patients.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return patients;
	}

	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list.
	 * @throws OHException if an error occurs during database request.
	 */ 
	public ArrayList<AdmittedPatient> getAdmittedPatients(String searchTerms) throws OHException {
		ArrayList<AdmittedPatient> patients = null;

		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				query.append("LIKE ?");
				String parameter = term;
				parameters.add(parameter);
			}
		}

		query.append(" ORDER BY PAT_ID DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			patients = new ArrayList<AdmittedPatient>();

			while (resultSet.next()) {
				AdmittedPatient adPat = buildAdmittedPatient(resultSet);
				patients.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return patients;
	}
	
	public ArrayList<AdmittedPatient> getAdmittedPatients(ArrayList<String> wardCodeList, String searchTerms) throws OHException {
		ArrayList<AdmittedPatient> patients = null;

		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				query.append("LIKE ?");
				String parameter = term;
				parameters.add(parameter);
			}
		}

		query.append(" ORDER BY PAT_ID DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			patients = new ArrayList<AdmittedPatient>();
			boolean inWard;
			while (resultSet.next()) {
				inWard = false;
				for (String code : wardCodeList) {
					if( code.equals(resultSet.getString("ADM_WRD_ID_A"))){
						inWard = true;
						break;
					}
				}
				if(inWard){
					AdmittedPatient adPat = buildAdmittedPatient(resultSet);
					patients.add(adPat);
				}
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return patients;
	}
	 public ArrayList<AdmittedPatient> getAdmittedPatients(int sTART_INDEX, int pAGE_SIZE, String searchTerms) throws OHException {
			ArrayList<AdmittedPatient> patients = null;

			StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
			query.append("FROM PATIENT PAT LEFT JOIN ");
			query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
			query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
			query.append("WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

			List<Object> parameters = new ArrayList<Object>(); 

			if (searchTerms != null && !searchTerms.isEmpty()) {
				searchTerms = searchTerms.trim().toLowerCase();
				String[] terms = searchTerms.split(" ");

				for (String term:terms) {
					query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
					query.append("LIKE ?");
					String parameter = "%"+term+"%";
					parameters.add(parameter);
				}
			}

			query.append(" ORDER BY PAT_ID DESC");
			parameters.add(sTART_INDEX);
			parameters.add(pAGE_SIZE);
			query.append(" LIMIT ?, ?");
			DbQueryLogger dbQuery = new DbQueryLogger();

			try {
				ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
				patients = new ArrayList<AdmittedPatient>();

				while (resultSet.next()) {
					AdmittedPatient adPat = buildAdmittedPatient(resultSet);
					patients.add(adPat);
				}

			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			}
			finally{
				dbQuery.releaseConnection();
			}
			return patients;
		}
	 
	public ArrayList<AdmittedPatient> getAdmittedPatientsSearch(int sTART_INDEX, int pAGE_SIZE, String searchTerms) throws OHException {
		ArrayList<AdmittedPatient> patients = null;

		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND PAT_ID LIKE ? "
						+ "OR LOWER(PAT_SNAME) = ? "
						+ "OR LOWER(PAT_FNAME) = ? "
						+ "OR LOWER(PAT_NOTE) = ? "
						+ "OR LOWER(PAT_TAXCODE) = ? ");
				String parameter = "%"+term+"%";
				parameters.add(parameter);
				parameters.add(parameter);
				parameters.add(parameter);
				parameters.add(parameter);
				parameters.add(parameter);
			}
		}

		query.append(" ORDER BY PAT_ID DESC");
		parameters.add(sTART_INDEX);
		parameters.add(pAGE_SIZE);
		query.append(" LIMIT ?, ?");
		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			patients = new ArrayList<AdmittedPatient>();

			while (resultSet.next()) {
				AdmittedPatient adPat = buildAdmittedPatient(resultSet);
				patients.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return patients;
	}
	 public int getCountPatients(String searchTerms) throws OHException {
			int total_row = 0;

			StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
			query.append("FROM PATIENT PAT LEFT JOIN ");
			query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
			query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
			query.append("WHERE (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null) ");

			List<Object> parameters = new ArrayList<Object>(); 

			if (searchTerms != null && !searchTerms.isEmpty()) {
				searchTerms = searchTerms.trim().toLowerCase();
				String[] terms = searchTerms.split(" ");

				for (String term:terms) {
					query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
					query.append("LIKE ?");
					String parameter = "%"+term+"%";
					parameters.add(parameter);
				}
			}

			query.append(" ORDER BY PAT_ID DESC");
			DbQueryLogger dbQuery = new DbQueryLogger();
			try {
				ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
				while (resultSet.next()) {
					total_row++;
				}

			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			}
			finally{
				dbQuery.releaseConnection();
			}
			return total_row;
		}
	private AdmittedPatient buildAdmittedPatient(ResultSet resultSet) throws SQLException {
		Patient patient = new Patient();
		patient.setCode(resultSet.getInt("PAT_ID"));
		patient.setFirstName(resultSet.getString("PAT_FNAME"));
		patient.setSecondName(resultSet.getString("PAT_SNAME"));
		patient.setAddress(resultSet.getString("PAT_ADDR"));
		patient.setOccupation(resultSet.getString("PAT_OCCUPATION"));
		patient.setPatBirthPlace(resultSet.getString("PAT_BIRTH_PLACE"));
		patient.setPatGeographicArea(resultSet.getString("PAT_GEOGRAPHIC_AREA"));
		patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
		patient.setAge(resultSet.getInt("PAT_AGE"));
		patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
		String sex=resultSet.getString("PAT_SEX");
		patient.setChildrenNumber(resultSet.getInt("CHILDREN_NUMBER"));
		patient.setParentResidence(resultSet.getString("PARENT_RESIDENCE"));
		if(sex!=null && !sex.trim().equals("")){
			patient.setSex(sex.charAt(0));
		}
		
		patient.setCity(resultSet.getString("PAT_CITY"));
		patient.setTelephone(resultSet.getString("PAT_TELE"));
		patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
		patient.setBloodType(resultSet.getString("PAT_BTYPE"));
		patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
		
		String father=resultSet.getString("PAT_FATH");
		if(father!=null && !father.trim().equals("")){
			patient.setFather(father.charAt(0));
		}

		patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
		
		String mother=resultSet.getString("PAT_MOTH");
		if(mother!=null && !mother.trim().equals("")){
			patient.setMother(mother.charAt(0));
		}
		
		String insurance=resultSet.getString("PAT_ESTA");
		if(insurance!=null && !insurance.trim().equals("")){
			patient.setHasInsurance(insurance.charAt(0));
		}
		
		String parentTogether=resultSet.getString("PAT_PTOGE");
		if(parentTogether!=null && !parentTogether.trim().equals("")){
			patient.setParentTogether(parentTogether.charAt(0));
		}
		
		
		patient.setNote(resultSet.getString("PAT_NOTE"));
		patient.setTaxCode(resultSet.getString("PAT_TAXCODE"));
		patient.setReductionPlanID(resultSet.getInt("PAT_RP_ID"));
		patient.setListID(resultSet.getInt("PAT_LST_ID"));
		patient.setLock(resultSet.getInt("PAT_LOCK"));
		
		patient.setAffiliatedPerson(resultSet.getInt("PAT_AFFILIATED_PERSON"));
		patient.setHeadAffiliation(resultSet.getBoolean("PAT_IS_HEAD_AFFILIATION"));
		
		patient.setStatus(resultSet.getString("PAT_STATUS"));
		
		Admission admission = null;
		if (resultSet.getInt("ADM_IN") == 1)
			admission = toAdmission(resultSet);
		
		AdmittedPatient adPat=new AdmittedPatient(patient, admission);
		return adPat;
	}

	/**
	 * Returns the only one admission without dimission date (or null if none) for the specified patient.
	 * @param patient the patient target of the admission.
	 * @return the patient admission.
	 * @throws OHException if an error occurs during database request.
	 */
	public Admission getCurrentAdmission(Patient patient) throws OHException {
		Admission admission = null;

		String query = "SELECT * FROM ADMISSION WHERE ADM_PAT_ID=? AND ADM_DELETED='N' AND ADM_DATE_DIS IS NULL";
		List<Object> parameters = Collections.<Object>singletonList(patient.getCode());

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			if (!resultSet.first()) return null;
			admission = toAdmission(resultSet);
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return admission;
	}

	/**
	 * Returns the admission with the selected id.
	 * @param id the admission id.
	 * @return the admission with the specified id, <code>null</code> otherwise.
	 * @throws OHException if an error occurs during database request.
	 */
	public Admission getAdmission(int id) throws OHException {
		Admission admission = null;
		String query = "SELECT * FROM ADMISSION WHERE ADM_ID=?";
		List<Object> parameters = Collections.<Object>singletonList(id);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			if (!resultSet.first()) return null;
			admission = toAdmission(resultSet);
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}

		return admission;
	}

	/**
	 * Returns all the admissions for the specified patient.
	 * @param patient the patient.
	 * @return the admission list.
	 * @throws OHException if an error occurs during database request.
	 */
	public ArrayList<Admission> getAdmissions(Patient patient) throws OHException {
		ArrayList<Admission> admissions = null;
		String query = "SELECT * FROM ADMISSION WHERE ADM_PAT_ID=? and ADM_DELETED='N' ORDER BY ADM_DATE_ADM ASC";
		List<Object> parameters = Collections.<Object>singletonList(patient.getCode());

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			admissions = new ArrayList<Admission>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Admission admission = toAdmission(resultSet);
				admissions.add(admission);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return admissions;
	}

	/**
	 * Converts a {@link ResultSet} row into an {@link Admission} object.
	 * @param resultSet the result set to read.
	 * @return the converted object.
	 * @throws SQLException if an error occurs.
	 */
	protected Admission toAdmission(ResultSet resultSet) throws SQLException
	{
		Admission admission = new Admission();
		admission.setId(resultSet.getInt("ADM_ID"));
		admission.setAdmitted(resultSet.getInt("ADM_IN"));
		admission.setType(resultSet.getString("ADM_TYPE"));
		admission.setWardId(resultSet.getString("ADM_WRD_ID_A"));
		admission.setYProg(resultSet.getInt("ADM_YPROG"));
		
		admission.setMProg(resultSet.getInt("ADM_MPROG"));
		
		admission.setPatId(resultSet.getInt("ADM_PAT_ID"));
		admission.setAdmDate(toCalendar(resultSet.getTimestamp("ADM_DATE_ADM")));
		admission.setAdmType(resultSet.getString("ADM_ADMT_ID_A_ADM"));
		admission.setFHU(resultSet.getString("ADM_FHU"));
		admission.setDiseaseInId(resultSet.getString("ADM_IN_DIS_ID_A"));
		admission.setDiseaseOutId1(resultSet.getString("ADM_OUT_DIS_ID_A"));
		admission.setDiseaseOutId2(resultSet.getString("ADM_OUT_DIS_ID_A_2"));
		admission.setDiseaseOutId3(resultSet.getString("ADM_OUT_DIS_ID_A_3"));
		admission.setOperationId(resultSet.getString("ADM_OPE_ID_A"));
		admission.setOpResult(resultSet.getString("ADM_RESOP"));
		admission.setOpDate(toCalendar(resultSet.getTimestamp("ADM_DATE_OP")));
		admission.setDisDate(toCalendar(resultSet.getTimestamp("ADM_DATE_DIS")));
		admission.setDisType(resultSet.getString("ADM_DIST_ID_A"));
		admission.setNote(resultSet.getString("ADM_NOTE"));
		admission.setTransUnit(toFloat(resultSet.getString("ADM_TRANS")));
		admission.setVisitDate(toCalendar(resultSet.getTimestamp("ADM_PRG_DATE_VIS")));
		admission.setPregTreatmentType(resultSet.getString("ADM_PRG_PTT_ID_A"));
		admission.setDeliveryDate(toCalendar(resultSet.getTimestamp("ADM_PRG_DATE_DEL")));
		admission.setDeliveryTypeId(resultSet.getString("ADM_PRG_DLT_ID_A"));
		admission.setDeliveryResultId(resultSet.getString("ADM_PRG_DRT_ID_A"));
		admission.setWeight(toFloat(resultSet.getString("ADM_PRG_WEIGHT")));
		admission.setCtrlDate1(toCalendar(resultSet.getTimestamp("ADM_PRG_DATE_CTRL1")));
		admission.setCtrlDate2(toCalendar(resultSet.getTimestamp("ADM_PRG_DATE_CTRL2")));
		admission.setAbortDate(toCalendar(resultSet.getTimestamp("ADM_PRG_DATE_ABORT")));
		admission.setUserID(resultSet.getString("ADM_USR_ID_A"));
		admission.setLock(resultSet.getInt("ADM_LOCK"));
		admission.setDeleted(resultSet.getString("ADM_DELETED"));
		return admission;
	}

	/**
	 * Converts a {@link Date} value to {@link GregorianCalendar}.
	 * @param date the date to convert.
	 * @return the converted date or <code>null</code> if the passed date is <code>null</code>.
	 */
	protected GregorianCalendar toCalendar(Timestamp date)
	{
		if (date == null) return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * Converts the passed {@link String} value to a {@link Float} value.
	 * @param value the value to convert
	 * @return the converted float or <code>null</code> if the passed value is <code>null</code> or is not parsable.
	 */
	protected Float toFloat(String value)
	{
		if (value == null) return null;
		try {
			return Float.valueOf(value);
		}catch(NumberFormatException nfe)
		{
			//the value is not parsable
			return null;
		}
	}

	/**
	 * Inserts a new admission.
	 * @param admission the admission to insert.
	 * @return <code>true</code> if the admission has been successfully inserted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the insertion.
	 */
	public boolean newAdmission(Admission admission) throws OHException {
		boolean result = false;

		String query = ADMISSION_INSERTION_QUERY;
		List<Object> parameters = getInsertParameters(admission);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 * @param admission the admission to insert.
	 * @return the generated id.
	 * @throws OHException if an error occurs during the insertion.
	 */
	public int newAdmissionReturnKey(Admission admission) throws OHException {
		int key = -1;

		String query = ADMISSION_INSERTION_QUERY;
		List<Object> parameters = getInsertParameters(admission);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet rs = dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, true);
			if (rs.first()) {
				key = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return key;
	}

	/**
	 * Extracts all query parameters from the given {@link Admission} object.
	 * The parameters generation and order is strictly binded to the ADMISSION_INSERTION_QUERY.
	 * @param admission the extraction target.
	 * @return the list of parameters.
	 */
	protected List<Object> getInsertParameters(Admission admission)
	{
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(admission.getAdmitted());
		parameters.add(admission.getType());
		parameters.add(admission.getWardId());
		parameters.add(admission.getYProg());
		parameters.add(admission.getMProg());
		parameters.add(admission.getPatId());
		parameters.add(toTimestamp(admission.getAdmDate()));
		parameters.add(admission.getAdmType());
		parameters.add(sanitize(admission.getFHU()));
		parameters.add(admission.getDiseaseInId());
		parameters.add(admission.getDiseaseOutId1());
		parameters.add(admission.getDiseaseOutId2());
		parameters.add(admission.getDiseaseOutId3());
		parameters.add(admission.getOperationId());
		parameters.add(toTimestamp(admission.getOpDate()));
		parameters.add(admission.getOpResult());
		parameters.add(toTimestamp(admission.getDisDate()));
		parameters.add(admission.getDisType());
		parameters.add(sanitize(admission.getNote()));
		parameters.add(admission.getTransUnit());
		parameters.add(toTimestamp(admission.getVisitDate()));
		parameters.add(admission.getPregTreatmentType());
		parameters.add(toTimestamp(admission.getDeliveryDate()));
		parameters.add(admission.getDeliveryTypeId());
		parameters.add(admission.getDeliveryResultId());
		parameters.add(admission.getWeight());
		parameters.add(toTimestamp(admission.getCtrlDate1()));
		parameters.add(toTimestamp(admission.getCtrlDate2()));
		parameters.add(toTimestamp(admission.getAbortDate()));
		parameters.add(admission.getUserID());
		parameters.add(admission.getLock());
		parameters.add(admission.getDeleted());
		
		parameters.add(MainMenu.getUser());
		parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		
		
		return parameters;
	}

	/**
	 * Converts a {@link GregorianCalendar} to a {@link Date}.
	 * @param calendar the calendar to convert.
	 * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected Date toDate(GregorianCalendar calendar)
	{
		if (calendar == null) return null;
		return new Date(calendar.getTimeInMillis());
	}
	
	/**
	 * Converts a {@link GregorianCalendar} to a {@link Timestamp}.
	 * @param calendar the calendar to convert.
	 * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected Timestamp toTimestamp(GregorianCalendar calendar)
	{
		if (calendar == null) return null;
		return new Timestamp(calendar.getTimeInMillis());
	}

	/**
	 * Sanitize the given {@link String} value. 
	 * This method is maintained only for backward compatibility.
	 * @param value the value to sanitize.
	 * @return the sanitized value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected String sanitize(String value)
	{
		if (value == null) return null;
		return value.trim().replaceAll("'", "\'");
	}

	/**
	 * Checks if the specified {@link Admission} has been modified.
	 * @param admission the admission to check.
	 * @return <code>true</code> if has been modified, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the check.
	 */
	public boolean hasAdmissionModified(Admission admission) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;

		// we establish if someone else has updated/deleted the record since the last read
		String query = "SELECT ADM_LOCK FROM ADMISSION WHERE ADM_ID =?";
		List<Object> parameters = Collections.<Object>singletonList(admission.getId());

		try {
			// we use manual commit of the transaction
			ResultSet resultSet =  dbQuery.getDataWithParams(query, parameters, true);
			if (resultSet.first()) { 
				// ok the record is present, it was not deleted
				result = resultSet.getInt("ADM_LOCK") != admission.getLock();
			} else {
				throw new OHException(MessageBundle.getMessage("angal.sql.couldntfindthedataithasprobablybeendeleted"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Updates the specified {@link Admission} object.
	 * @param admission the admission object to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHException if an error occurs.
	 */
	public boolean updateAdmission(Admission admission) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		
		String query = "UPDATE ADMISSION SET " +
		"ADM_IN=?, ADM_TYPE=?, ADM_WRD_ID_A=?, ADM_YPROG=?, ADM_MPROG=?, ADM_PAT_ID=?, ADM_DATE_ADM=?, ADM_ADMT_ID_A_ADM=?, " +
		"ADM_FHU=?, ADM_IN_DIS_ID_A=?, ADM_OUT_DIS_ID_A=?, ADM_OUT_DIS_ID_A_2=?, ADM_OUT_DIS_ID_A_3=?, " +
		"ADM_DATE_OP=?, ADM_OPE_ID_A=?, ADM_RESOP=?, ADM_DATE_DIS=?, ADM_DIST_ID_A=?, ADM_NOTE=?, ADM_TRANS=?, " +
		"ADM_PRG_DATE_VIS=?, ADM_PRG_PTT_ID_A=?, ADM_PRG_DATE_DEL=?, ADM_PRG_DLT_ID_A=?, ADM_PRG_DRT_ID_A=?, " +
		"ADM_PRG_WEIGHT=?, ADM_PRG_DATE_CTRL1=?, ADM_PRG_DATE_CTRL2=?, ADM_PRG_DATE_ABORT=?, ADM_USR_ID_A=?, " +
		"ADM_LOCK= ADM_LOCK + 1, ADM_DELETED=?, ADM_MODIFY_BY=?, ADM_MODIFY_DATE=? " +
		"WHERE " +
		"ADM_ID=?";

		try {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(admission.getAdmitted());
			parameters.add(admission.getType());
			parameters.add(admission.getWardId());
			parameters.add(admission.getYProg());
			parameters.add(admission.getMProg());
			parameters.add(admission.getPatId());
			parameters.add(toTimestamp(admission.getAdmDate()));
			parameters.add(admission.getAdmType());
			parameters.add(sanitize(admission.getFHU()));
			parameters.add(admission.getDiseaseInId());
			parameters.add(admission.getDiseaseOutId1());
			parameters.add(admission.getDiseaseOutId2());
			parameters.add(admission.getDiseaseOutId3());
			parameters.add(toTimestamp(admission.getOpDate()));
			parameters.add(admission.getOperationId());
			parameters.add(admission.getOpResult());
			parameters.add(toTimestamp(admission.getDisDate()));
			parameters.add(admission.getDisType());
			parameters.add(sanitize(admission.getNote()));
			parameters.add(admission.getTransUnit());
			parameters.add(toTimestamp(admission.getVisitDate()));
			parameters.add(admission.getPregTreatmentType());
			parameters.add(toTimestamp(admission.getDeliveryDate()));
			parameters.add(admission.getDeliveryTypeId());
			parameters.add(admission.getDeliveryResultId());
			parameters.add(admission.getWeight());
			parameters.add(toTimestamp(admission.getCtrlDate1()));
			parameters.add(toTimestamp(admission.getCtrlDate2()));
			parameters.add(toTimestamp(admission.getAbortDate()));
			parameters.add(admission.getUserID());
			parameters.add(admission.getDeleted());
			
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			
			parameters.add(admission.getId());

			result = dbQuery.setDataWithParams(query, parameters, true);
			if (result)	admission.setLock(admission.getLock() + 1);
		} finally{
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 * @return the admission types.
	 * @throws OHException 
	 */
	public ArrayList<AdmissionType> getAdmissionType() throws OHException {
		ArrayList<AdmissionType> types = null;
		String query = "SELECT * FROM ADMISSIONTYPE";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			types = new ArrayList<AdmissionType>(resultSet.getFetchSize());
			while (resultSet.next()) {
				types.add(new AdmissionType(resultSet.getString("ADMT_ID_A"), resultSet.getString("ADMT_DESC")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return types;
	}

	/**
	 * Lists the {@link DischargeType}s.
	 * @return the discharge types.
	 * @throws OHException 
	 */
	public ArrayList<DischargeType> getDischargeType() throws OHException {
		ArrayList<DischargeType> types = null;
		String query = "SELECT * FROM DISCHARGETYPE";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			types = new ArrayList<DischargeType>(resultSet.getFetchSize());
			while (resultSet.next()) {
				types.add(new DischargeType(resultSet.getString("DIST_ID_A"), resultSet.getString("DIST_DESC")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return types;
	}

	/**
	 * Returns the next prog in the year for a certain ward.
	 * @param wardId the ward id.
	 * @return the next prog.
	 * @throws OHException if an error occurs retrieving the value.
	 */
	public int getNextYProg(String wardId) throws OHException {
		int next = 0;

		GregorianCalendar now = TimeTools.getServerDateTime();
		GregorianCalendar first = null;
		GregorianCalendar last = null;

		// de Felice - 20/01/2008 - richiesta di james che, per la sola
		// maternita', chiede di ripartire
		// da 1 il primo di luglio. questo e' implementato ora nel modo
		// seguente:
		// per il reparto maternity (M) il progressivo riparte il primo luglio
		// questo per ogni anno d'ora in poi se il parametro
		// MATERNITYRESTARTINJUNE in generalData.properties � uguale a yes!!
		if (wardId.equalsIgnoreCase("M") && Param.bool("MATERNITYRESTARTINJUNE")) {
			if (now.get(Calendar.MONTH) < 6) {
				first = new GregorianCalendar(now.get(Calendar.YEAR) - 1, Calendar.JULY, 1);
				last = new GregorianCalendar(now.get(Calendar.YEAR), Calendar.JUNE, 30);
			} else {
				first = new GregorianCalendar(now.get(Calendar.YEAR), Calendar.JULY, 1);
				last = new GregorianCalendar(now.get(Calendar.YEAR) + 1, Calendar.JUNE, 30);
			}

		} else {
			first = new GregorianCalendar(now.get(Calendar.YEAR), 0, 1);
			last = new GregorianCalendar(now.get(Calendar.YEAR), 11, 31);
		}
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(wardId);
		parameters.add(toDate(first));
		parameters.add(toDate(last));

		String query = "SELECT ADM_YPROG FROM ADMISSION " +
				"WHERE ADM_WRD_ID_A=? AND ADM_DATE_ADM >= ? AND ADM_DATE_ADM <= ? AND ADM_DELETED='N' " +
				"ORDER BY ADM_YPROG DESC";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			if (resultSet.first()) {
				next = resultSet.getInt(1) + 1;
			} else next = 1;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
		return next;
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
		StringBuilder sqlString = new StringBuilder("SELECT MAX(ADM_MPROG) FROM ADMISSION");

		sqlString.append(" WHERE YEAR(ADM_DATE_ADM	) = ? AND MONTH(ADM_DATE_ADM	) = ? ");
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(year);
		parameters.add(month);
		resultSet = dbQuery.getDataWithParams(sqlString.toString(), parameters, true);

		try {
			resultSet.next();
			progMonth = resultSet.getInt("MAX(ADM_MPROG)");
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return progMonth;
	}

	/**
	 * Sets an admission record to deleted.
	 * @param admissionId the admission id.
	 * @return <code>true</code> if the record has been set to delete.
	 * @throws OHException if an error occurs.
	 */
	public boolean setDeleted(int admissionId) throws OHException {

		boolean result = false;

		List<Object> parameters = Collections.<Object>singletonList(admissionId);
		String query = "UPDATE ADMISSION SET ADM_DELETED='Y' WHERE ADM_ID=?";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}


	/**
	 * Counts the number of used bed for the specified ward.
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 * @throws OHException if an error occurs retrieving the bed count.
	 */
	public int getUsedWardBed(String wardId) throws OHException {
		int result = 0;

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String query = "SELECT count(*) FROM ADMISSION WHERE ADM_IN = 1 AND ADM_WRD_ID_A = ? AND ADM_DELETED = 'N'";
			List<Object> parameters = Collections.<Object>singletonList(wardId);

			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			if (resultSet.first()) result = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Deletes the patient photo.
	 * @param patientId the patient id.
	 * @return <code>true</code> if the photo has been deleted, <code>false</code> otherwise.
	 * @throws OHException if an error occurs.
	 */
	public boolean deletePatientPhoto(int patientId) throws OHException
	{
		boolean result = false;

		String query = "UPDATE PATIENT SET PAT_PHOTO = null WHERE PAT_ID=?";
		List<Object> parameters = Collections.<Object>singletonList(patientId);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			result = dbQuery.setDataWithParams(query, parameters, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	public ArrayList<AdmittedPatient> getPregnancyAdmittedPatients(String searchTerms) throws OHException {
		ArrayList<AdmittedPatient> patients = null;
		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_SEX = 'F'  AND (PAT.PAT_DELETED='N' OR PAT.PAT_DELETED is null)) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				query.append("LIKE ?");
				String parameter = "%"+term+"%";
				parameters.add(parameter);
			}
		}

		query.append(" ORDER BY PAT_ID DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			patients = new ArrayList<AdmittedPatient>();

			while (resultSet.next()) {
				AdmittedPatient adPat = buildAdmittedPatient(resultSet);
				patients.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return patients;
	}
	
	public ArrayList<AdmittedPatient> getPregnancyAdmittedPatients(String searchTerms, int sTART_INDEX, int pAGE_SIZE) throws OHException {
		ArrayList<AdmittedPatient> patients = null;

		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_SEX = 'F'  AND (PAT.PAT_DELETED='N' OR PAT.PAT_DELETED is null)) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				query.append("LIKE ?");
				String parameter = "%"+term+"%";
				parameters.add(parameter);
			}
		}
		query.append(" ORDER BY PAT_ID DESC");
		parameters.add(sTART_INDEX);
		parameters.add(pAGE_SIZE);
		query.append(" LIMIT ?, ? ");
		DbQueryLogger dbQuery = new DbQueryLogger();
	
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			patients = new ArrayList<AdmittedPatient>();

			while (resultSet.next()) {
				AdmittedPatient adPat = buildAdmittedPatient(resultSet);
				patients.add(adPat);
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return patients;
	}
	
	public int getPregnancyPatientsCount(String searchTerms) throws OHException {
		int total_rows = 0;
		StringBuilder query = new StringBuilder("SELECT PAT.*, ADM.* ");
		query.append("FROM PATIENT PAT LEFT JOIN ");
		query.append("(SELECT * FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		query.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		query.append("WHERE (PAT.PAT_SEX = 'F'  AND (PAT.PAT_DELETED='N' OR PAT.PAT_DELETED is null)) ");

		List<Object> parameters = new ArrayList<Object>(); 

		if (searchTerms != null && !searchTerms.isEmpty()) {
			searchTerms = searchTerms.trim().toLowerCase();
			String[] terms = searchTerms.split(" ");

			for (String term:terms) {
				query.append(" AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				query.append("LIKE ?");
				String parameter = "%"+term+"%";
				parameters.add(parameter);
			}
		}

		query.append(" ORDER BY PAT_ID DESC");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			while (resultSet.next()) {
				total_rows++;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return total_rows;
	}
	
	
	public HashMap<String, Integer> getCountADM(int year, String dischargeType) throws OHException, ParseException{
		String query1 = "SELECT count(ADM_ID) as TOTAL  from admission "
				+ " where  ADM_DATE_DIS BETWEEN ? AND ? and ADM_DATE_DIS IS NOT NULL";
		
		String query2 = "SELECT SUM(unix_timestamp(ADM_DATE_DIS)-unix_timestamp(ADM_DATE_ADM)) as TOTAL  from admission "
				+ " where  ADM_DATE_DIS BETWEEN ? AND ? and ADM_DATE_DIS IS NOT NULL AND ADM_DATE_ADM IS NOT NULL";
		
		String query3 = "SELECT count(ADM_ID) as TOTAL  from admission "
				+ " where  ADM_DATE_DIS BETWEEN ? AND ? and ADM_DATE_DIS IS NOT NULL AND ADM_DATE_ADM IS NOT NULL and 	ADM_DIST_ID_A=?";
		List<Object> parameters1 = new ArrayList<Object>(2);
		List<Object> parameters2 = new ArrayList<Object>(2);
		List<Object> parameters3 = new ArrayList<Object>(3);
		
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
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters3.add(stringDateFrom);
		parameters3.add(stringDateTo);
		parameters3.add(dischargeType);
		
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalAdm", resultSet.getInt("TOTAL"));
			}
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalAvgAdm", resultSet.getInt("TOTAL"));
			}
			resultSet = dbQuery.getDataWithParams(query3, parameters3, true);
			if (resultSet.next()) {
				results.put("totalDeath", resultSet.getInt("TOTAL"));
			}
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	public HashMap<String, Integer> getCountTransByAdmissionAndOpd(int year, String dischargeType, String referralTo) throws OHException, ParseException{
		String query1 = "SELECT count(ADM_ID) as TOTAL from admission "
				+ " where ADM_DATE_ADM BETWEEN ? AND ? and ADM_DIST_ID_A=?";
		
		String query2 = "SELECT count(OPD_ID) as TOTAL from OPD "
				+ " where OPD_DATE BETWEEN ? AND ? AND OPD_REFERRAL_TO=?";
				
		List<Object> parameters1 = new ArrayList<Object>(2);
		List<Object> parameters2 = new ArrayList<Object>(2);
		
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
		parameters1.add(dischargeType);
		
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters2.add(referralTo);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		int byAdm = 0;
		int byOpd = 0;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				byAdm = resultSet.getInt("TOTAL");
			}
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				byOpd = resultSet.getInt("TOTAL");
			}	
			results.put("totalTransfert", byOpd+byAdm);
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	
	public HashMap<String, Integer> getCountDelivryResultDeath(int year, String motherDeathCode, String childDeathCode) throws OHException, ParseException{
		String query1 = "SELECT count(PDEL_ID) as TOTAL from  pregnancydelivery "
				+ " where PDEL_DATE_DEL BETWEEN ? AND ? and PDEL_DRT_ID_A=?";
		
		String query2 = "SELECT count(PDEL_ID) as TOTAL from  pregnancydelivery "
				+ " where PDEL_DATE_DEL BETWEEN ? AND ? AND PDEL_DRT_ID_A=?";
				
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
		parameters1.add(motherDeathCode);
		
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters2.add(childDeathCode);
				
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalMotherDeath", resultSet.getInt("TOTAL"));
			}
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalChildDeath", resultSet.getInt("TOTAL"));
			}	
			
		}catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally{
			dbQuery.releaseConnection();
		}
		return results;
	}
	
	public HashMap<String, Integer> getCountDelivryRChildHivNegMotherPos(int year, String childNegativeCode, String hivExamCode, String hivPositiveCode, String childPositiveCode) throws OHException, ParseException{
		String query1 = "SELECT count(PDEL_ID) as TOTAL from  pregnancydelivery pdel "
				+ " join admission adm on adm.ADM_ID=pdel.PDEL_ADM_ID "
				+ " where PDEL_DATE_DEL BETWEEN ? AND ? and PDEL_HIV_STATUT=?"
				+ " and adm.ADM_PAT_ID in (select LAB_PAT_ID from laboratory where LAB_EXA_ID_A=? and LAB_RES=?  )";
		
		String query2 = "SELECT count(PDEL_ID) as TOTAL from  pregnancydelivery pdel "
				+ " join admission adm on adm.ADM_ID=pdel.PDEL_ADM_ID "
				+ " where PDEL_DATE_DEL BETWEEN ? AND ? and PDEL_HIV_STATUT=?"
				+ " and adm.ADM_PAT_ID in (select LAB_PAT_ID from laboratory where LAB_EXA_ID_A=? and LAB_RES=?  )";
//				
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
		parameters1.add(childNegativeCode);
		parameters1.add(hivExamCode);
		parameters1.add(hivPositiveCode);
		
		parameters2.add(stringDateFrom);
		parameters2.add(stringDateTo);
		parameters2.add(childPositiveCode);
		parameters2.add(hivExamCode);
		parameters2.add(hivPositiveCode);
			
		HashMap<String, Integer> results = new HashMap<String, Integer>();
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query1, parameters1, true);
			if (resultSet.next()) {
				results.put("totalChildNegativeMotherPositive", resultSet.getInt("TOTAL"));
			}	
			resultSet = dbQuery.getDataWithParams(query2, parameters2, true);
			if (resultSet.next()) {
				results.put("totalChildPositiveMotherPositive", resultSet.getInt("TOTAL"));
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
