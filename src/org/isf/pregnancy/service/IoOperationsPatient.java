package org.isf.pregnancy.service;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQuery;


/**
 * @author Martin Reinstadler
 * this class performs database readings and insertions for the tables:
 * PATIENT, ADMISSION, WARD
 */
public class IoOperationsPatient {
	
	
	/**
	 * @return a list of all female {@link PregnancyPatient}
	 */
	public ArrayList<Patient> getPregnancyPatients() {
		return getPregnancyPatients(null);
	}

	/**
	 * @param regex the searchstring (part of the patients name or surname)
	 * @return a list of all female {@link PregnancyPatient} containing the searchstring
	 */
	public ArrayList<Patient> getPregnancyPatients(String regex) {
		ArrayList<Patient> patients = null;
		
		StringBuffer stringBfr = new StringBuffer("SELECT PAT.* ");
		stringBfr.append("FROM PATIENT PAT ");
		//stringBfr.append("(SELECT ADM_PAT_ID, ADM_WRD_ID_A FROM ADMISSION WHERE (ADM_DELETED='N' or ADM_DELETED is null) AND ADM_IN = 1) ADM ");
		//stringBfr.append("ON ADM.ADM_PAT_ID = PAT.PAT_ID ");
		stringBfr.append("WHERE (PAT.PAT_SEX = 'F' AND (PAT.PAT_DELETED='N' or PAT.PAT_DELETED is null)) ");
		if (regex != null && !regex.equals("")) {
			String s = regex.trim().toLowerCase();
			String[] s1 = s.split(" ");
			
			for (int i = 0; i < s1.length; i++) {
				//stringBfr.append("AND LOWER(CONCAT(PAT_SNAME, PAT_FNAME, PAT_CITY, PAT_ADDR, PAT_ID, PAT_NOTE)) ");
				stringBfr.append("AND CONCAT(PAT_ID, LOWER(PAT_SNAME), LOWER(PAT_FNAME)) ");
				stringBfr.append("LIKE '%").append(s1[i]).append("%' ");
			}
		}
		
		stringBfr.append("ORDER BY PAT_NAME");
		
		DbQuery dbQuery = new DbQuery();

		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			patients = new ArrayList<Patient>();

			while (resultSet.next()) {
				Patient patient = new Patient();
				patient.setCode(resultSet.getInt("PAT_ID"));
				patient.setFirstName(resultSet.getString("PAT_FNAME"));
				patient.setSecondName(resultSet.getString("PAT_SNAME"));
				patient.setAddress(resultSet.getString("PAT_ADDR"));
//				patient.setBirthDate(resultSet.getString("PAT_BDATE"));
				patient.setAge(resultSet.getInt("PAT_AGE"));
				patient.setAgetype(resultSet.getString("PAT_AGETYPE"));
				patient.setSex(resultSet.getString("PAT_SEX").charAt(0));
				patient.setCity(resultSet.getString("PAT_CITY"));
				patient.setTelephone(resultSet.getString("PAT_TELE"));
				patient.setNextKin(resultSet.getString("PAT_NEXT_KIN"));
				// patient.setLevelEdu(resultSet.getString("PAT_LEDU").charAt(0));
				patient.setBloodType(resultSet.getString("PAT_BTYPE"));// added
				patient.setFather_name(resultSet.getString("PAT_FATH_NAME"));
				patient.setFather(resultSet.getString("PAT_FATH").charAt(0));
				// added
				patient.setMother_name(resultSet.getString("PAT_MOTH_NAME"));
				patient.setMother(resultSet.getString("PAT_MOTH").charAt(0));
//				patient.setHasAssurance(resultSet.getString("PAT_ESTA").charAt(0));
				patient.setParentTogether(resultSet.getString("PAT_PTOGE").charAt(0));
				patient.setNote(resultSet.getString("PAT_NOTE"));
				patient.setTaxCode(resultSet.getString("PAT_TAXCODE"));
				patient.setLock(resultSet.getInt("PAT_LOCK"));

				//String ward = resultSet.getString("WARD");

				Blob photoBlob = resultSet.getBlob("PAT_PHOTO");
					if (photoBlob != null && photoBlob.length() > 0) {
					BufferedInputStream is = new BufferedInputStream(photoBlob.getBinaryStream());

					Image image = ImageIO.read(is);
//					patient.setPhoto(image);
				}

				patients.add(patient);

			}

			dbQuery.releaseConnection();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patients;
	}
	/**
	 * 
	 * @param aPatient the {@link Patient}
	 * @return true if the Patient has an admission without discharge date defined
	 */
	public boolean isPatientCurrentlyAdmitted(Patient aPatient) {
		StringBuffer stringBfr = new StringBuffer("SELECT * FROM ADMISSION ");
		stringBfr.append("WHERE (ADM_PAT_ID= '");
		stringBfr.append( aPatient.getCode() + "' ");
		stringBfr.append("AND ADM_DELETED='N' and ADM_DATE_DIS is null)");

		DbQuery dbQuery = new DbQuery();
		try {
			ResultSet resultSet = dbQuery.getData(stringBfr.toString(), true);
			if (resultSet.first()) 
				return true;
		} catch (SQLException e) {
			
			 e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	
	/**
	 * This method is just for test purpose
	 * @param pat_taxcode
	 */
	public void deletePatient_byTaxCode(String pat_taxcode){
		StringBuffer stringBfr = new StringBuffer("DELETE FROM PATIENT WHERE PAT_TAXCODE = ");
		stringBfr.append(" '"+pat_taxcode+"' ;");
		DbQuery dbQuery = new DbQuery();
		try{
		dbQuery.setData(stringBfr.toString(), true);
		}
		catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
