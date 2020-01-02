package org.isf.patient.service;

/*------------------------------------------
 * IoOperations - db operations for the patient entity
 * -----------------------------------------
 * modification history
 * 05/05/2005 - giacomo  - first beta version 
 * 03/11/2006 - ross - added toString method. Gestione apici per
 *                     nome, cognome, citta', indirizzo e note
 * 11/08/2008 - alessandro - added father & mother's names
 * 26/08/2008 - claudio    - added birth date
 * 							 modififed age
 * 01/01/2009 - Fabrizio   - changed the calls to PAT_AGE fields to
 *                           return again an int type
 * 03/12/2009 - Alex       - added method for merge two patients history
 *------------------------------------------*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

public class IoOperations {

	/**
	 * method that returns the full list of Patients not logically deleted
	 * 
	 * @return the list of patients
	 * @throws OHException
	 */
	public ArrayList<Patient> getPatients() throws OHException {
		ArrayList<Patient> pPatient = null;
		String query = "SELECT * FROM PATIENT WHERE (PAT_DELETED='N' OR PAT_DELETED IS NULL) ORDER BY PAT_NAME";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			pPatient = new ArrayList<Patient>(resultSet.getFetchSize());
			Patient patient;
			while (resultSet.next()) {
				patient = buildPatient(resultSet);

				pPatient.add(patient);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pPatient;
	}

	private Patient buildPatient(ResultSet resultSet) throws SQLException {
		Patient patient;
		patient = new Patient();
		patient.setCode(resultSet.getInt("PAT_ID"));
		patient.setFirstName(resultSet.getString("PAT_FNAME"));
		patient.setSecondName(resultSet.getString("PAT_SNAME"));
		patient.setAddress(resultSet.getString("PAT_ADDR"));
		patient.setOccupation(resultSet.getString("PAT_OCCUPATION"));
		patient.setPatGeographicArea(resultSet.getString("PAT_GEOGRAPHIC_AREA"));
		patient.setPatBirthPlace(resultSet.getString("PAT_BIRTH_PLACE"));
		patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
		patient.setAge(resultSet.getInt("PAT_AGE"));
		patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
		patient.setChildrenNumber(resultSet.getInt("CHILDREN_NUMBER"));
		patient.setParentResidence(resultSet.getString("PARENT_RESIDENCE"));
		String sex = resultSet.getString("PAT_SEX");
		if (sex != null && !sex.trim().equals("")) {
			patient.setSex(sex.charAt(0));
		}

		// patient.setSex(new Character(resultSet.getString("PAT_SEX")
		// .charAt(0)));
		patient.setCity(resultSet.getString("PAT_CITY"));
		patient.setTelephone(resultSet.getString("PAT_TELE"));
		patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
		patient.setBloodType(resultSet.getString("PAT_BTYPE"));

		String father = resultSet.getString("PAT_FATH");
		if (father != null && !father.trim().equals("")) {
			patient.setFather(father.charAt(0));
		}
		patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));

		String mother = resultSet.getString("PAT_MOTH");
		if (mother != null && !mother.trim().equals("")) {
			patient.setMother(mother.charAt(0));
		}

		patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));

		String insurance = resultSet.getString("PAT_ESTA");
		if (insurance != null && !insurance.trim().equals("")) {
			patient.setHasInsurance(insurance.charAt(0));
		}

		String parentTogether = resultSet.getString("PAT_PTOGE");
		if (parentTogether != null && !parentTogether.trim().equals("")) {
			patient.setParentTogether(parentTogether.charAt(0));
		}

		// patient.setParentTogether(resultSet.getString("PAT_PTOGE")
		// .charAt(0));
		patient.setNote(resultSet.getString("PAT_NOTE"));
		patient.setTaxCode(resultSet.getString("PAT_TAXCODE"));
		patient.setLock(resultSet.getInt("PAT_LOCK"));
		patient.setListID(resultSet.getInt("PAT_LST_ID"));
		patient.setReductionPlanID(resultSet.getInt("PAT_RP_ID"));
		patient.setAffiliatedPerson(resultSet.getInt("PAT_AFFILIATED_PERSON"));
		patient.setHeadAffiliation(resultSet.getBoolean("PAT_IS_HEAD_AFFILIATION"));
		patient.setStatus(resultSet.getString("PAT_STATUS"));
		// String photoPath = resultSet.getString("PAT_PHOTO");
		// patient.setPhoto(photoPath);
		return patient;
	}

	public ArrayList<Patient> getPatientsEmployee() throws OHException {
		ArrayList<Patient> pPatient = null;
		String query = "SELECT * FROM PATIENT WHERE (PAT_DELETED='N' OR PAT_DELETED IS NULL) AND (PAT_AFFILIATED_PERSON IS NULL OR PAT_AFFILIATED_PERSON = 0) ORDER BY PAT_NAME";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			pPatient = new ArrayList<Patient>(resultSet.getFetchSize());
			Patient patient;
			while (resultSet.next()) {
				patient = buildPatient(resultSet);

				// String photoPath = resultSet.getString("PAT_PHOTO");
				// patient.setPhoto(photoPath);

				pPatient.add(patient);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pPatient;
	}

	/**
	 * method that returns the full list of Patients not logically deleted with
	 * Height and Weight
	 * 
	 * @param regex
	 * @return the full list of Patients with Height and Weight
	 * @throws OHException
	 */
	public ArrayList<Patient> getPatientsWithHeightAndWeight(String regex) throws OHException {
		ArrayList<Patient> pPatient = null;
		ArrayList<Object> params = new ArrayList<Object>();
		// Recupera i pazienti arricchiti con Peso e Altezza
		StringBuilder queryBld = new StringBuilder(
				"SELECT * FROM PATIENT LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, "
				+ "PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID "
				+ "ORDER BY PEX_DATE DESC) AS HW ON PAT_ID = HW.PEX_PAT_ID "
				+ "WHERE (PAT_DELETED='N' or PAT_DELETED is null) ");
		if (regex != null && !regex.equals("")) {
			String s = regex.trim().toLowerCase();
			String[] s1 = s.split(" ");

			for (int i = 0; i < s1.length; i++) {
				queryBld.append(
						"AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				queryBld.append("LIKE CONCAT('%', ? , '%') ");
				params.add(s1[i]);
			}
		}

		/***** filter only head patient *****/
		if (regex != null && !regex.equals("") && regex.equals("affiliation")) {
			queryBld.append(" AND (PAT_AFFILIATED_PERSON < 1 OR PAT_AFFILIATED_PERSON is null)");
			queryBld.append(" AND (PAT_IS_HEAD_AFFILIATION = 1)");
		}
		/*** *******/

		queryBld.append(" ORDER BY PAT_ID DESC ");
		
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBld.toString(), params, true);
			pPatient = new ArrayList<Patient>(resultSet.getFetchSize());
			Patient patient;
			while (resultSet.next()) {
				patient = this.buildPatient(resultSet);
				pPatient.add(patient);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally {
			dbQuery.releaseConnection();
		}
		return pPatient;
	}
	/**
	 * method that returns the full list of Patients not logically deleted with
	 * Height and Weight
	 * 
	 * @param regex
	 * @return the full list of Patients with Height and Weight
	 * @throws OHException
	 */
	public ArrayList<Patient> getPatientsWithHeightAndWeight2(String regex) throws OHException {
		ArrayList<Patient> pPatient = null;
		ArrayList<Object> params = new ArrayList<Object>();
		// Recupera i pazienti arricchiti con Peso e Altezza
		StringBuilder queryBld = new StringBuilder(
				"SELECT * FROM PATIENT LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, "
				+ "PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID "
				+ "ORDER BY PEX_DATE DESC) AS HW ON PAT_ID = HW.PEX_PAT_ID "
				+ "WHERE (PAT_DELETED='N' or PAT_DELETED is null) ");
		if (regex != null && !regex.equals("")) {
			String s = regex.trim().toLowerCase();
			String[] s1 = s.split(" ");

			for (int i = 0; i < s1.length; i++) {
				queryBld.append(
						"AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME), LOWER(PAT_NOTE), LOWER(PAT_TAXCODE)) ");
				queryBld.append("LIKE CONCAT('%', ? , '%') ");
				params.add(s1[i]);
			}
		}

		/***** filter only head patient *****/
		if (regex != null && !regex.equals("") && regex.equals("affiliation")) {
			queryBld.append(" AND (PAT_AFFILIATED_PERSON < 1 OR PAT_AFFILIATED_PERSON is null)");
			queryBld.append(" AND (PAT_IS_HEAD_AFFILIATION = 1)");
		}
		/*** *******/

		queryBld.append(" ORDER BY PAT_ID DESC ");
		if (regex == null || regex.equals("")){
			queryBld.append(" LIMIT 5 ");
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBld.toString(), params, true);
			pPatient = new ArrayList<Patient>(resultSet.getFetchSize());
			Patient patient;
			while (resultSet.next()) {
				patient = this.buildPatient(resultSet);
				pPatient.add(patient);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		finally {
			dbQuery.releaseConnection();
		}
		return pPatient;
	}
	public ArrayList<Patient> getPatientsHeadWithHeightAndWeight() throws OHException {
		ArrayList<Patient> pPatient = null;
		ArrayList<Object> params = new ArrayList<Object>();
		// Recupera i pazienti arricchiti con Peso e Altezza
		StringBuilder queryBld = new StringBuilder(
				"SELECT * FROM PATIENT LEFT JOIN (SELECT PEX_PAT_ID, PEX_HEIGHT AS PAT_HEIGHT, PEX_WEIGHT AS PAT_WEIGHT FROM PATIENTEXAMINATION GROUP BY PEX_PAT_ID ORDER BY PEX_DATE DESC) AS HW ON PAT_ID = HW.PEX_PAT_ID WHERE (PAT_DELETED='N' or PAT_DELETED is null) ");

		/***** filter only head patient *****/
		queryBld.append(" AND (PAT_AFFILIATED_PERSON < 1 OR PAT_AFFILIATED_PERSON is null)");
		queryBld.append(" AND (PAT_IS_HEAD_AFFILIATION = 1)");
		/*** *******/

		queryBld.append(" ORDER BY PAT_ID DESC");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBld.toString(), params, true);
			pPatient = new ArrayList<Patient>(resultSet.getFetchSize());
			Patient patient;
			while (resultSet.next()) {
				patient = this.buildPatient(resultSet);
				// patient.setCode(resultSet.getInt("PAT_ID"));
				// patient.setFirstName(resultSet.getString("PAT_FNAME"));
				// patient.setSecondName(resultSet.getString("PAT_SNAME"));
				// patient.setAddress(resultSet.getString("PAT_ADDR"));
				// patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
				// patient.setAge(resultSet.getInt("PAT_AGE"));
				// patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
				// patient.setSex(resultSet.getString("PAT_SEX").charAt(0));
				// patient.setCity(resultSet.getString("PAT_CITY"));
				// patient.setTelephone(resultSet.getString("PAT_TELE"));
				// patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
				// patient.setBloodType(resultSet.getString("PAT_BTYPE")); //
				// added
				// patient.setFather(resultSet.getString("PAT_FATH").charAt(0));
				// patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
				// patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));
				// patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
				// patient.setHasInsurance(resultSet.getString("PAT_ESTA").charAt(
				// 0));
				// patient.setParentTogether(resultSet.getString("PAT_PTOGE")
				// .charAt(0));
				// patient.setNote(resultSet.getString("PAT_NOTE"));
				// patient.setTaxCode(resultSet.getString("PAT_TAXCODE"));
				// // patient.setHeight(resultSet.getFloat("PAT_HEIGHT"));
				// patient.setHeight(resultSet.getFloat("PAT_HEIGHT"));
				// patient.setWeight(resultSet.getFloat("PAT_WEIGHT"));
				// patient.setLock(resultSet.getInt("PAT_LOCK"));
				//
				// patient.setListID(resultSet.getInt("PAT_LST_ID"));
				// patient.setReductionPlanID(resultSet.getInt("PAT_RP_ID"));
				// patient.setAffiliatedPerson(resultSet.getInt("PAT_AFFILIATED_PERSON"));
				// patient.setHeadAffiliation(resultSet.getBoolean("PAT_IS_HEAD_AFFILIATION"));

				// String photoPath = resultSet.getString("PAT_PHOTO");
				// patient.setPhoto(photoPath);

				pPatient.add(patient);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		// catch (IOException e) {
		// throw new OHException(
		// MessageBundle
		// .getMessage("angal.sql.problemsoccurredwithserverconnection"),
		// e);
		// }
		finally {
			dbQuery.releaseConnection();
		}
		return pPatient;
	}

	/**
	 * method that get a Patient by his/her name
	 * 
	 * @param name
	 * @return the Patient that match specified name
	 * @throws OHException
	 */
	public Patient getPatient(String name) throws OHException {
		Patient patient = null;
		String query = "SELECT * FROM PATIENT WHERE PAT_NAME = ? AND (PAT_DELETED='N' OR PAT_DELETED IS NULL) ORDER BY PAT_SNAME,PAT_FNAME";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(name);
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			patient = new Patient();
			while (resultSet.next()) {
				patient = buildPatient(resultSet);
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		// catch (IOException e) {
		// throw new OHException(
		// MessageBundle
		// .getMessage("angal.sql.problemsoccurredwithserverconnection"),
		// e);
		// }
		finally {
			dbQuery.releaseConnection();
		}
		return patient;
	}

	/**
	 * method that get a Patient by his/her ID
	 * 
	 * @param code
	 * @return the Patient
	 * @throws OHException
	 */
	public Patient getPatient(Integer code) throws OHException {
		Patient patient = null;
		String query = "SELECT * FROM PATIENT WHERE PAT_ID = ? AND (PAT_DELETED='N' OR PAT_DELETED IS NULL)";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(code);
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			patient = new Patient();
			while (resultSet.next()) {
				patient = buildPatient(resultSet);
				// patient.setCode(resultSet.getInt("PAT_ID"));
				// patient.setFirstName(resultSet.getString("PAT_FNAME"));
				// patient.setSecondName(resultSet.getString("PAT_SNAME"));
				// patient.setAddress(resultSet.getString("PAT_ADDR"));
				// patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
				// patient.setAge(resultSet.getInt("PAT_AGE"));
				// patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
				// patient.setSex(resultSet.getString("PAT_SEX").charAt(0));
				// patient.setCity(resultSet.getString("PAT_CITY"));
				// patient.setTelephone(resultSet.getString("PAT_TELE"));
				// patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
				// patient.setBloodType(resultSet.getString("PAT_BTYPE")); //
				// added
				// patient.setFather(resultSet.getString("PAT_FATH").charAt(0));
				// patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
				// patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));
				// patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
				// patient.setHasInsurance(resultSet.getString("PAT_ESTA").charAt(
				// 0));
				// patient.setParentTogether(resultSet.getString("PAT_PTOGE")
				// .charAt(0));
				// patient.setNote(resultSet.getString("PAT_NOTE"));
				// patient.setTaxCode(resultSet.getString("PAT_TAXCODE"));
				// patient.setLock(resultSet.getInt("PAT_LOCK"));
				//
				// patient.setListID(resultSet.getInt("PAT_LST_ID"));
				// patient.setReductionPlanID(resultSet.getInt("PAT_RP_ID"));
				// patient.setAffiliatedPerson(resultSet.getInt("PAT_AFFILIATED_PERSON"));
				// patient.setHeadAffiliation(resultSet.getBoolean("PAT_IS_HEAD_AFFILIATION"));

				// String photoPath = resultSet.getString("PAT_PHOTO");
				// patient.setPhoto(photoPath);

			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		// catch (IOException e) {
		// throw new OHException(
		// MessageBundle
		// .getMessage("angal.sql.problemsoccurredwithserverconnection"),
		// e);
		// }
		finally {
			dbQuery.releaseConnection();
		}
		return patient;
	}

	/**
	 * get a Patient by his/her ID, even if he/her has been logically deleted
	 * 
	 * @param code
	 * @return the list of Patients
	 * @throws OHException
	 */
	public Patient getPatientAll(Integer code) throws OHException {
		Patient patient = null;
		String query = "SELECT * FROM PATIENT WHERE PAT_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(code);
			ResultSet resultSet = dbQuery.getDataWithParams(query, params, true);
			patient = new Patient();
			while (resultSet.next()) {
				patient = buildPatient(resultSet);
				// patient.setCode(resultSet.getInt("PAT_ID"));
				// patient.setFirstName(resultSet.getString("PAT_FNAME"));
				// patient.setSecondName(resultSet.getString("PAT_SNAME"));
				// patient.setAddress(resultSet.getString("PAT_ADDR"));
				// patient.setBirthDate(resultSet.getDate("PAT_BDATE"));
				// patient.setAge(resultSet.getInt("PAT_AGE"));
				// patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
				// patient.setSex(resultSet.getString("PAT_SEX").charAt(0));
				// patient.setCity(resultSet.getString("PAT_CITY"));
				// patient.setTelephone(resultSet.getString("PAT_TELE"));
				// patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
				// patient.setBloodType(resultSet.getString("PAT_BTYPE")); //
				// added
				// patient.setFather(resultSet.getString("PAT_FATH").charAt(0));
				// patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
				// patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));
				// patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
				// patient.setHasInsurance(resultSet.getString("PAT_ESTA").charAt(
				// 0));
				// patient.setParentTogether(resultSet.getString("PAT_PTOGE")
				// .charAt(0));
				// patient.setNote(resultSet.getString("PAT_NOTE"));
				// patient.setTaxCode(resultSet.getString("PAT_TAXCODE"));
				// patient.setLock(resultSet.getInt("PAT_LOCK"));
				//
				// patient.setListID(resultSet.getInt("PAT_LST_ID"));
				// patient.setReductionPlanID(resultSet.getInt("PAT_RP_ID"));
				// patient.setAffiliatedPerson(resultSet.getInt("PAT_AFFILIATED_PERSON"));
				// patient.setHeadAffiliation(resultSet.getBoolean("PAT_IS_HEAD_AFFILIATION"));

				// String photoPath = resultSet.getString("PAT_PHOTO");
				// patient.setPhoto(photoPath);

			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		// catch (IOException e) {
		// throw new OHException(
		// MessageBundle
		// .getMessage("angal.sql.problemsoccurredwithserverconnection"),
		// e);
		// }
		finally {
			dbQuery.releaseConnection();
		}
		return patient;
	}

	/**
	 * methot that insert a new Patient in the db
	 * 
	 * @param patient
	 * @return true - if the new Patient has been inserted
	 * @throws OHException
	 */
	public boolean newPatient(Patient patient) throws OHException {

		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {

			String query = "INSERT INTO PATIENT (PAT_NAME, PAT_FNAME, PAT_SNAME, PAT_BDATE, PAT_AGE, PAT_AGETYPE, PAT_SEX, PAT_ADDR, PAT_CITY, PAT_NEXT_KIN, PAT_TELE, PAT_MOTH_NAME, PAT_MOTH, PAT_FATH_NAME, PAT_FATH, PAT_BTYPE, PAT_ESTA, PAT_PTOGE, PAT_NOTE, PAT_TAXCODE, PAT_LST_ID, PAT_RP_ID, PAT_AFFILIATED_PERSON,PAT_IS_HEAD_AFFILIATION, PAT_STATUS, PAT_OCCUPATION, PAT_BIRTH_PLACE,PAT_GEOGRAPHIC_AREA,"
					+ "PAT_CREATE_BY, PAT_CREATE_DATE, CHILDREN_NUMBER, PARENT_RESIDENCE) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			ArrayList<Object> params = new ArrayList<Object>();
			params.add(patient.getName());
			params.add(patient.getFirstName());
			params.add(patient.getSecondName());
			params.add(patient.getBirthDate());
			params.add(patient.getAge());
			params.add(patient.getAgetype());
			params.add(String.valueOf(patient.getSex()));
			params.add(patient.getAddress());
			params.add(patient.getCity());
			params.add(patient.getNextKin());
			params.add(patient.getTelephone());
			params.add(patient.getMother_name());
			params.add(String.valueOf(patient.getMother()));
			params.add(patient.getFather_name());
			params.add(String.valueOf(patient.getFather()));
			params.add(patient.getBloodType());
			params.add(String.valueOf(patient.getHasInsurance()));
			params.add(String.valueOf(patient.getParentTogether()));
			params.add(patient.getNote());
			params.add(patient.getTaxCode());
			params.add(patient.getListID());
			params.add(patient.getReductionPlanID());
			params.add(patient.getAffiliatedPerson());
			params.add(patient.isHeadAffiliation());
			params.add(patient.getStatus());
			params.add(patient.getOccupation());
			params.add(patient.getPatBirthPlace());
			params.add(patient.getPatGeographicArea());
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			params.add(patient.getChildrenNumber());
			params.add(patient.getParentResidence());
			// params.add(createPatientPhotoInputStream(patient.getPhoto()));

			// params.add(patient.getPhoto());

			ResultSet r = dbQuery.setDataReturnGeneratedKeyWithParams(query, params, true);

			if (r.first()) {
				patient.setCode(r.getInt(1));
				result = true;
			}

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * 
	 * method that update an existing {@link Patient} in the db
	 * 
	 * @param patient
	 *            - the {@link Patient} to update
	 * @param check
	 *            - if <code>true</code> it will performs an integrity check
	 * @return true - if the existing {@link Patient} has been updated
	 * @throws OHException
	 */
	public boolean updatePatient(Patient patient, boolean check) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		ResultSet set = null;
		int lock = 0;
		try {
			if (check) {

				// we establish if someone else has updated/deleted the record
				// since the last read
				String query = "SELECT PAT_LOCK FROM PATIENT " + " WHERE PAT_ID = ?";
				ArrayList<Object> params = new ArrayList<Object>();
				params.add(patient.getCode());
				set = dbQuery.getDataWithParams(query, params, false); // we use
																		// manual
																		// commit
																		// of
																		// the
																		// transaction
				if (set.first()) {
					lock = set.getInt("PAT_LOCK");
					// ok the record is present, it was not deleted
					if (lock != patient.getLock()) {
						// the patient has been update by someone else
						return false;
					}
				}
			}

			String query = "UPDATE PATIENT SET PAT_FNAME = ?, PAT_SNAME = ?, PAT_NAME  = ?, PAT_BDATE = ?, PAT_AGE = ?, PAT_AGETYPE = ?, PAT_SEX = ?, PAT_ADDR = ?, PAT_CITY = ?, PAT_NEXT_KIN = ?, PAT_TELE = ?, PAT_MOTH = ?, PAT_MOTH_NAME = ?, PAT_FATH = ?, PAT_FATH_NAME = ?, PAT_BTYPE = ?, PAT_ESTA = ?, PAT_PTOGE = ?, PAT_NOTE = ?, PAT_TAXCODE = ?, PAT_LOCK = ?, PAT_LST_ID=?, PAT_RP_ID=? , PAT_AFFILIATED_PERSON=?, PAT_IS_HEAD_AFFILIATION=?, PAT_STATUS=? , PAT_OCCUPATION=?, PAT_BIRTH_PLACE=?, PAT_GEOGRAPHIC_AREA=?,"
					+ "PAT_MODIFY_BY = ?, PAT_MODIFY_DATE= ?, CHILDREN_NUMBER = ?, PARENT_RESIDENCE = ?  WHERE PAT_ID = ?";

			ArrayList<Object> params = new ArrayList<Object>();
			params.add(patient.getFirstName());
			params.add(patient.getSecondName());
			params.add(patient.getName());
			params.add(patient.getBirthDate());
			params.add(patient.getAge());
			params.add(patient.getAgetype());
			params.add(String.valueOf(patient.getSex()));
			params.add(patient.getAddress());
			params.add(patient.getCity());
			params.add(patient.getNextKin());
			params.add(patient.getTelephone());
			params.add(String.valueOf(patient.getMother()));
			params.add(patient.getMother_name());
			params.add(String.valueOf(patient.getFather()));
			params.add(patient.getFather_name());
			params.add(patient.getBloodType());
			params.add(String.valueOf(patient.getHasInsurance()));
			params.add(String.valueOf(patient.getParentTogether()));
			params.add(patient.getNote());
			params.add(patient.getTaxCode());
			params.add(lock + 1);
			params.add(patient.getListID());
			params.add(patient.getReductionPlanID());
			params.add(patient.getAffiliatedPerson());
			params.add(patient.isHeadAffiliation());
			params.add(patient.getStatus());
			params.add(patient.getOccupation());
			params.add(patient.getPatBirthPlace());
			params.add(patient.getPatGeographicArea());
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			params.add(patient.getChildrenNumber());
			params.add(patient.getParentResidence());
			// params.add(createPatientPhotoInputStream(patient.getPhoto()));

			// params.add(patient.getPhoto());

			params.add(patient.getCode());

			result = dbQuery.setDataWithParams(query.toString(), params, true);

			/*
			 * Occorre aggiornare il model perch� il paziente non viene
			 * riletto dal DB.
			 */
			if (result)
				patient.setLock(lock + 1);
			else
				throw new OHException(MessageBundle.getMessage("angal.patient.thedataisnomorepresent")); // the
																											// record
																											// has
																											// been
																											// deleted
																											// since
																											// the
																											// last
																											// read

		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * method that logically delete a Patient (not phisically deleted)
	 * 
	 * @param aPatient
	 * @return true - if the Patient has beeb deleted (logically)
	 * @throws OHException
	 */
	public boolean deletePatient(Patient aPatient) throws OHException {

		boolean result = false;
		String sqlString = "UPDATE PATIENT SET PAT_DELETED = 'Y' WHERE PAT_ID = ?";

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(aPatient.getCode());

		// System.out.println(sqlString);
		DbQueryLogger dbQuery = new DbQueryLogger();
		result = dbQuery.setDataWithParams(sqlString, params, true);
		return result;
	}

	/**
	 * method that check if a Patient is already present in the DB by his/her
	 * name
	 * 
	 * @param name
	 * @return true - if the patient is already present
	 * @throws OHException
	 */
	public boolean isPatientPresent(String name) throws OHException {
		boolean result = false;
		String string = "SELECT PAT_ID FROM PATIENT WHERE PAT_NAME = ? AND PAT_DELETED='N'";
		DbQueryLogger dbQuery = new DbQueryLogger();

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(name);

		try {
			ResultSet set = dbQuery.getDataWithParams(string, params, true);
			if (set.first()) {
				result = true;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * methot that get next PAT_ID is going to be used.
	 * 
	 * @return code
	 * @throws OHException
	 */
	public int getNextPatientCode() throws OHException {
		int code = 0;
		String string = "SELECT MAX(PAT_ID) FROM PATIENT";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet set = dbQuery.getData(string, false);
			if (set.first()) {
				code = set.getInt(1) + 1;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return code;
	}

	/**
	 * method that merge all clinic details under the same PAT_ID
	 * 
	 * @param mergedPatient
	 * @param patient2
	 * @return true - if no OHExceptions occurred
	 * @throws OHException
	 */
	public boolean mergePatientHistory(Patient mergedPatient, Patient patient2) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		int mergedID = mergedPatient.getCode();
		int obsoleteID = patient2.getCode();
		String query = "";
		ArrayList<Object> params = new ArrayList<Object>();

		// ADMISSION HISTORY
		query = "UPDATE ADMISSION SET ADM_PAT_ID = ? , ADM_MODIFY_BY = ?, ADM_MODIFY_DATE= ? WHERE ADM_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		//params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		//params.add(obsoleteID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// LABORATORY HISTORY
		query = "UPDATE LABORATORY SET LAB_PAT_ID = ?, LAB_PAT_NAME = ?, LAB_AGE = ?, LAB_SEX = ?, LAB_MODIFY_BY = ?, LAB_MODIFY_DATE= ? WHERE LAB_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		params.add(mergedPatient.getName());
		params.add(mergedPatient.getAge());
		params.add(String.valueOf(mergedPatient.getSex()));
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// OPD HISTORY
		query = "UPDATE OPD SET OPD_PAT_ID = ?, OPD_AGE = ?, OPD_SEX = ? , OPD_MODIFY_BY = ?, OPD_MODIFY_DATE= ? WHERE OPD_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		// params.add(mergedPatient.getName());
		params.add(mergedPatient.getAge());
		params.add(String.valueOf(mergedPatient.getSex()));
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// BILLS HISTORY
		query = "UPDATE BILLS SET BLL_ID_PAT = ?, BLL_PAT_NAME = ? , BLL_MODIFY_BY = ?, BLL_MODIFY_DATE= ? WHERE BLL_ID_PAT = ?";
		params.clear();
		params.add(mergedID);
		params.add(mergedPatient.getName());
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// PARENT BILLS HISTORY
		query = "UPDATE BILLS SET BLL_PAT_AFFILIATED_PERSON = ? , BLL_MODIFY_BY = ?, BLL_MODIFY_DATE= ? WHERE BLL_PAT_AFFILIATED_PERSON = ?";
		params.clear();
		params.add(mergedID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// MEDICALDSRSTOCKMOVWARD HISTORY
		query = "UPDATE MEDICALDSRSTOCKMOVWARD SET MMVN_PAT_ID = ? , MMVN_MODIFY_BY = ?, MMVN_MODIFY_DATE= ? WHERE MMVN_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// THERAPY HISTORY
		query = "UPDATE THERAPIES SET THR_PAT_ID = ? , THR_MODIFY_BY = ?, THR_MODIFY_DATE= ? WHERE THR_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// VISITS HISTORY
		query = "UPDATE VISITS SET VST_PAT_ID = ? , VST_MODIFY_BY = ?, VST_MODIFY_DATE= ? WHERE VST_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// PATIENTVACCINE HISTORY
		query = "UPDATE PATIENTVACCINE SET PAV_PAT_ID = ? , PAV_MODIFY_BY = ?, PAV_MODIFY_DATE= ? WHERE PAV_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);

		// PREGNANCY HISTORY
		query = "UPDATE PREGNANCY SET 	PREG_PAT_ID = ? , PREG_MODIFY_BY = ?, PREG_MODIFY_DATE= ?  WHERE PREG_PAT_ID = ?";
		params.clear();
		params.add(mergedID);
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);
		dbQuery.setDataWithParams(query, params, false);
		// DELETE OLD PATIENT (patient2)
		query = "UPDATE PATIENT SET PAT_DELETED = 'Y' , PAT_MODIFY_BY = ?, PAT_MODIFY_DATE= ? WHERE PAT_ID = ?";
		params.clear();
		params.add(MainMenu.getUser());
		params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
		params.add(obsoleteID);

		// FINAL CHECK
		boolean result = dbQuery.setDataWithParams(query, params, false);
		if (result)
			dbQuery.commit();
		else
			dbQuery.rollback();

		return result;
	}

	public Patient alreadyExistingPatient(Patient patient) throws OHException {
		Patient p = null;
		ArrayList<Object> params = new ArrayList<Object>();
		String string = "";
		DbQueryLogger dbQuery = new DbQueryLogger();
		if(patient.getTelephone().length() > 10){
			string = "SELECT * FROM PATIENT WHERE (PAT_FNAME = ? AND PAT_SNAME = ?) OR (PAT_FNAME = ? AND PAT_SNAME = ?) OR PAT_TELE = ?";
			params.add(patient.getFirstName());
			params.add(patient.getSecondName());
			params.add(patient.getSecondName());
			params.add(patient.getFirstName());
			params.add(patient.getTelephone());
		}
		else{
			string = "SELECT * FROM PATIENT WHERE (PAT_FNAME = ? AND PAT_SNAME = ?) OR (PAT_FNAME = ? AND PAT_SNAME = ?)";
			params.add(patient.getFirstName());
			params.add(patient.getSecondName());
			params.add(patient.getSecondName());
			params.add(patient.getFirstName());
		}
		try {
			ResultSet set = dbQuery.getDataWithParams(string, params, true);
			if (set.first()) {
				p = new Patient();
				p.setFirstName(set.getString("PAT_FNAME"));
				p.setSecondName(set.getString("PAT_SNAME"));
				p.setTelephone(set.getString("PAT_TELE"));
				p.setSex(set.getString("PAT_SEX").charAt(0));
				p.setAge(set.getInt("PAT_AGE"));
				
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return p;
	}
}
