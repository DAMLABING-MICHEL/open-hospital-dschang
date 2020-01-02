package org.isf.mortuary.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.accounting.model.Bill;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;

import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.DeathReason;
import org.isf.mortuary.model.IntervalJours;
import org.isf.patient.model.Patient;
import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.model.VaccineType;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class DeathIoOperations {
	/**
	 * Store the specified {@link Death}.
	 * @param death the death  to store.
	 * @return <code>true</code> if the {@link Death} has been stored, <code>false</code> otherwise.
	 * @throws OHException if an error occurs during the store operation.
	 */
	public boolean newDeath(Death death) throws OHException, SQLException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		String query = "";
		try {
			
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(death.getPatient().getCode());
			parameters.add(death.getProvenance());
			parameters.add(new java.sql.Timestamp(death.getDateDeces().getTime().getTime())); 
			parameters.add(death.getLieu());
			parameters.add(new java.sql.Timestamp(death.getDateEntree().getTime().getTime()));
			if(death.getDateSortieProvisoire() == null) {
				
				parameters.add(null);
			
			}
			else {
				parameters.add(new java.sql.Timestamp(death.getDateSortieProvisoire().getTime().getTime()));
			}
			parameters.add(death.getMotif().getId());
			parameters.add(death.getNomDeclarant());
			parameters.add(death.getTelDeclarant());
			parameters.add(death.getNidDeclarant());
			parameters.add(death.getNomFamille());
			parameters.add(death.getTelFamille());
			parameters.add(death.getNidFamille());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(MainMenu.getUser());
			parameters.add(death.getCasier());
			if(death.getDateSortie() == null) {
				parameters.add(null);
			}
			else {
				parameters.add(new java.sql.Timestamp(death.getDateSortie().getTime().getTime()));
				
			}
			query = "insert into DECES (DECES_PAT_ID, DECES_PROVENANCE, DECES_DATE, DECES_LIEU, DECES_DATE_ENTREE, "
					  + "DECES_DATE_SORTIE_PROVISOIRE, DECES_MOTIF_ID, DECES_NOM_DECLARANT, DECES_TEL_DECLARANT, DECES_NID_DECLARANT, "
					  + "DECES_NOM_FAMILLE, DECES_TEL_FAMILLE, DECES_NID_FAMILLE, DECES_CREATE_DATE, DECES_CREATE_BY, DECES_CASIER, DECES_DATE_SORTIE) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			
			result = dbQuery.setDataWithParams(query, parameters, true);
		}catch(OHException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}	
			return result;
	 }
	/**
	 * Get all the {@link Death}s.
	 * 
	 * @return a list of deaths.
	 * @throws OHException
	 *             if an error occurs retrieving the deaths.
	 */
	public ArrayList<Death> getDeaths() throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Death> deaths = null;
		
		Death death = null;
		StringBuilder query = new StringBuilder("SELECT D.*, M.*, P.PAT_SNAME, P.PAT_FNAME, P.PAT_AGE, P.PAT_SEX "
				+"	FROM DECES D"
				+"	JOIN MOTIF_DECES M ON DECES_MOTIF_ID=MOTIF_ID "
				+"	JOIN PATIENT P ON DECES_PAT_ID = PAT_ID WHERE DECES_DELETED != 1");
		try {
			query.append(" ORDER BY DECES_ID");
			ResultSet resultSet = dbQuery.getData(query.toString(), true);			
			deaths = new ArrayList<Death>(resultSet.getFetchSize());
			while (resultSet.next()) {
				
				death = buildDeath(resultSet);				
				deaths.add(death);
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			//e.printStackTrace();
		} finally {
			dbQuery.releaseConnection();
		}
		return deaths;
	}
	
	public Death buildDeath(ResultSet resultSet) throws OHException{
		Death death = new Death();
		try {
			death.setId(resultSet.getInt("DECES_ID"));				
			death.setLieu(resultSet.getString("DECES_LIEU"));
				
			death.setProvenance(resultSet.getString("DECES_PROVENANCE"));
		
			death.setDateDeces(convertToGregorianCalendar(resultSet.getTimestamp("DECES_DATE")));
			death.setDateEntree(convertToGregorianCalendar(resultSet.getTimestamp("DECES_DATE_ENTREE")));
		
			death.setDateSortie(convertToGregorianCalendar(resultSet.getTimestamp("DECES_DATE_SORTIE")));
			death.setDateSortieProvisoire(convertToGregorianCalendar(resultSet.getTimestamp("DECES_DATE_SORTIE_PROVISOIRE")));
			death.setMotif(new DeathReason(resultSet.getInt("DECES_MOTIF_ID"), resultSet.getString("MOTIF_CODE"), resultSet.getString("MOTIF_DESCRIPTION")));
			death.setNomDeclarant(resultSet.getString("DECES_NOM_DECLARANT"));
			death.setTelDeclarant(resultSet.getString("DECES_TEL_DECLARANT"));
			death.setNidFamille(resultSet.getString("DECES_NID_FAMILLE"));
			death.setNidDeclarant(resultSet.getString("DECES_TEL_DECLARANT"));
			death.setNomFamille(resultSet.getString("DECES_NOM_FAMILLE"));
			death.setTelFamille(resultSet.getString("DECES_TEL_FAMILLE"));			
			death.setIdMotif(resultSet.getInt("DECES_MOTIF_ID"));
			death.setIdPatient(resultSet.getInt("DECES_PAT_ID"));
			death.setPatientName(resultSet.getString("PAT_SNAME") + " "+ resultSet.getString("PAT_FNAME"));
			death.setPatientAge(resultSet.getInt("PAT_AGE"));
			death.setPatientSex(resultSet.getString("PAT_SEX"));
			death.setCasier(resultSet.getString("DECES_CASIER"));
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
			//e.printStackTrace();
		} 
		return death;
	} 
	
	
	/**
	 * Converts the specified {@link Timestamp} to a {@link GregorianCalendar}
	 * instance.
	 * 
	 * @param aDate
	 *            the date to convert.
	 * @return the corresponding GregorianCalendar value or <code>null</code> if
	 *         the input value is <code>null</code>.
	 */
	public GregorianCalendar convertToGregorianCalendar(Timestamp aDate) {
		if (aDate == null)
			return null;
		GregorianCalendar time = new GregorianCalendar();
		time.setTime(aDate);
		return time;
	}
	/**
	 * 
	 * method that update an existing {@link Death} in the db
	 * 
	 * @param death
	 *            - the {@link Death} to update
	 * @return true - if the existing {@link Death} has been updated
	 * @throws OHException
	 */
	public boolean updateDeath(Death death) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		String query = "";
		try {

			ArrayList<Object> parameters = new ArrayList<Object>();
			parameters.add(death.getProvenance());
			parameters.add(new java.sql.Timestamp(death.getDateDeces().getTime().getTime())); 
			parameters.add(death.getLieu());
			
			parameters.add(new java.sql.Timestamp(death.getDateEntree().getTime().getTime()));
			
			
			parameters.add(death.getMotif().getId());
			parameters.add(death.getNomDeclarant());
			parameters.add(death.getTelDeclarant());
			parameters.add(death.getNidDeclarant());
			parameters.add(death.getNomFamille());
			parameters.add(death.getTelFamille());
			parameters.add(death.getNidFamille());	
			
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(death.getCasier());
			
			if(death.getDateSortie() == null && death.getDateSortieProvisoire() == null) {
				query = "UPDATE DECES SET DECES_PROVENANCE = ?, DECES_DATE  = ?, DECES_LIEU = ?, DECES_DATE_SORTIE = NULL, DECES_DATE_ENTREE = ?, DECES_DATE_SORTIE_PROVISOIRE = NULL, DECES_MOTIF_ID = ?, DECES_NOM_DECLARANT = ?, DECES_TEL_DECLARANT = ?, DECES_NID_DECLARANT = ?, DECES_NOM_FAMILLE = ?, DECES_TEL_FAMILLE = ?, DECES_NID_FAMILLE = ?,  DECES_MODIFY_BY = ?, DECES_MODIFY_DATE = ?, DECES_CASIER = ? ";
			}
			else if(death.getDateSortie() == null && death.getDateSortieProvisoire() != null) {
				query = "UPDATE DECES SET DECES_PROVENANCE = ?, DECES_DATE  = ?, DECES_LIEU = ?, DECES_DATE_SORTIE = NULL, DECES_DATE_ENTREE = ?, DECES_MOTIF_ID = ?, DECES_NOM_DECLARANT = ?, DECES_TEL_DECLARANT = ?, DECES_NID_DECLARANT = ?, DECES_NOM_FAMILLE = ?, DECES_TEL_FAMILLE = ?, DECES_NID_FAMILLE = ?,  DECES_MODIFY_BY = ?, DECES_MODIFY_DATE = ?, DECES_CASIER = ?, DECES_DATE_SORTIE_PROVISOIRE = ? ";
				parameters.add(new java.sql.Timestamp(death.getDateSortieProvisoire().getTime().getTime()));
			}
			else {
				
				query = "UPDATE DECES SET DECES_PROVENANCE = ?, DECES_DATE  = ?, DECES_LIEU = ?, DECES_DATE_ENTREE = ?, DECES_MOTIF_ID = ?, DECES_NOM_DECLARANT = ?, DECES_TEL_DECLARANT = ?, DECES_NID_DECLARANT = ?, DECES_NOM_FAMILLE = ?, DECES_TEL_FAMILLE = ?, DECES_NID_FAMILLE = ?, DECES_MODIFY_BY = ?, DECES_MODIFY_DATE = ?, DECES_CASIER = ?, DECES_DATE_SORTIE = ? , DECES_DATE_SORTIE_PROVISOIRE = ? ";
				parameters.add(new java.sql.Timestamp(death.getDateSortie().getTime().getTime()));
				parameters.add(new java.sql.Timestamp(death.getDateSortieProvisoire().getTime().getTime()));
			}
			parameters.add(death.getId());
			
			query = query + " WHERE DECES_ID = ?";		
			//System.out.println(query);
			result = dbQuery.setDataWithParams(query, parameters, true);
			
		}finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	/**
	 * method that delete a death
	 * 
	 * @param death id
	 * @return true - if the Death has been deleted 
	 * @throws OHException
	 */
	public boolean deleteDeath(int id) throws OHException {

		boolean result = false;
		//String sqlString = "DELETE FROM DECES WHERE DECES_ID = ?";
		String sqlString = "UPDATE DECES SET DECES_DELETED = 1 WHERE DECES_ID = ?";
		DbQueryLogger dbQuery = new DbQueryLogger();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(id);
		try {
		// System.out.println(sqlString);
		result = dbQuery.setDataWithParams(sqlString, params, true);
		
		}finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * returns all {@link Death}s within <code>dateFrom</code> and
	 * <code>dateTo</code>
	 * @param motif
	 * @param pavillon
	 * @param pat
	 * @param dateFrom
	 * @param dateFrom2
	 * @param dateTo
	 * @param isEntree
	 * @param isSortie
	 * @param start_index
	 * @param page_size
	 * @return 
	 */
	public ArrayList<Death> getDeaths(int motif, String pavillon, int pat, GregorianCalendar dateFrom, GregorianCalendar dateTo, boolean isEntree, boolean isSortie, int start_index, int page_size) throws OHException {
			ArrayList<Death> deaths = null;
			String sqlString = "";
			List<Object> parameters = new ArrayList<Object>();

			sqlString += "SELECT D.*, M.*, P.PAT_SNAME, P.PAT_FNAME, P.PAT_AGE, P.PAT_SEX "
							+ " FROM DECES D JOIN MOTIF_DECES M ON DECES_MOTIF_ID=MOTIF_ID "
							+ " JOIN PATIENT P ON DECES_PAT_ID = PAT_ID WHERE DECES_DELETED != 1 ";
			if(isEntree) {
				if (dateFrom != null && dateTo != null) {
					sqlString +=  " AND DATE(D.DECES_DATE_ENTREE) >= ? AND DATE(D.DECES_DATE_ENTREE) <= ? ";
						parameters.add(new Timestamp(dateFrom.getTime().getTime()));
						parameters.add(new Timestamp(dateTo.getTime().getTime()));
				}
			}
			if(isSortie) {
				if (dateFrom != null && dateTo != null) {
					sqlString +=  " AND DATE(D.DECES_DATE_SORTIE_PROVISOIRE) >= ? AND DATE(D.DECES_DATE_SORTIE_PROVISOIRE) <= ?  ";
							parameters.add(new Timestamp(dateFrom.getTime().getTime()));
							parameters.add(new Timestamp(dateTo.getTime().getTime()));
				}
			
			}
			/*if(!isEntree && !isSortie) {
				sqlString += " AND WHERE 1 ";
			}*/
			if (motif != 0) {
				sqlString +=  " AND D.DECES_MOTIF_ID = ? ";
				parameters.add(motif);
			}
			if (pat != 0) {
				sqlString +=  " AND D.DECES_PAT_ID = ? ";
				parameters.add(pat);
			}

			if (pavillon!= null) {
				if(!pavillon.equals(MessageBundle.getMessage("angal.mortuary.all")))
				{	sqlString +=  " AND DECES_PROVENANCE = ? ";
					parameters.add(pavillon);
				}
			}

			parameters.add(start_index);
			parameters.add(page_size);
			sqlString +=" ORDER BY DECES_ID";
			sqlString += " LIMIT ?, ? ";

			//System.out.println("getDeaths: sql=" + sqlString.toString());
			DbQueryLogger dbQuery = new DbQueryLogger();
			try {
				ResultSet resultSet = dbQuery.getDataWithParams(
						sqlString.toString(), parameters, true);
				deaths = new ArrayList<Death>(resultSet.getFetchSize());
				while (resultSet.next()) {
					deaths.add(buildDeath(resultSet));
				}
			} catch (SQLException e) {
				throw new OHException(
						MessageBundle
								.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
						e);
			} finally {
				dbQuery.releaseConnection();
			}
			return deaths;
		}
	
	
	
	/**
	 * returns all {@link Death}s within <code>dateFrom</code> and
	 * <code>dateTo</code>
	 * @param motif
	 * @param pavillon
	 * @param pat
	 * @param dateFrom
	 * @param dateTo
	 * @param isEntree
	 * @param isSortie
	 * @return 
	 */
	public ArrayList<Death> getDeaths(int motif, String pavillon, int pat, GregorianCalendar dateFrom, GregorianCalendar dateTo, boolean isEntree, boolean isSortie) throws OHException {
			ArrayList<Death> deaths = null;
			String sqlString = "";
			List<Object> parameters = new ArrayList<Object>();

			sqlString += "SELECT D.*, M.*, P.PAT_SNAME, P.PAT_FNAME, P.PAT_AGE, P.PAT_SEX "
							+ " FROM DECES D JOIN MOTIF_DECES M ON DECES_MOTIF_ID=MOTIF_ID "
							+ " JOIN PATIENT P ON DECES_PAT_ID = PAT_ID  WHERE DECES_DELETED != 1 ";
			if(isEntree) {
				if (dateFrom != null && dateTo != null) {
					sqlString +=  " AND DATE(D.DECES_DATE_ENTREE) >= ? AND DATE(D.DECES_DATE_ENTREE) <= ? ";
					parameters.add(new Timestamp(dateFrom.getTime().getTime()));
					parameters.add(new Timestamp(dateTo.getTime().getTime()));
				}
			}
			if(isSortie) {
				if (dateFrom != null && dateTo != null) {
					sqlString +=  " AND DATE(D.DECES_DATE_SORTIE_PROVISOIRE) >= ? AND DATE(D.DECES_DATE_SORTIE_PROVISOIRE) <= ?  ";
							parameters.add(new Timestamp(dateFrom.getTime().getTime()));
							parameters.add(new Timestamp(dateTo.getTime().getTime()));
				}
			
			}
			/*if(!isEntree && !isSortie) {
				sqlString += " AND WHERE 1 ";
			}*/
			if (motif != 0) {
				sqlString +=  " AND D.DECES_MOTIF_ID = ? ";
				parameters.add(motif);
			}
			if (pat != 0) {
				sqlString +=  " AND D.DECES_PAT_ID = ? ";
				parameters.add(pat);
			}

			if (pavillon!= null) {
				if(!pavillon.equals(MessageBundle.getMessage("angal.mortuary.all")))
				{	sqlString +=  " AND DECES_PROVENANCE = ? ";
					parameters.add(pavillon);
				}
			}
			DbQueryLogger dbQuery = new DbQueryLogger();
			try {
				sqlString +=" ORDER BY DECES_ID";
				ResultSet resultSet = dbQuery.getDataWithParams(
						sqlString.toString(), parameters, true);
				deaths = new ArrayList<Death>(resultSet.getFetchSize());
				while (resultSet.next()) {
					deaths.add(buildDeath(resultSet));
				}
			} catch (SQLException e) {
				throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),e);
			} finally {
				dbQuery.releaseConnection();
			}
			return deaths;
		}
	
	/**
	 * Count the number of rows in DEATH 
	 * @param motif
	 * @return the number of rows
	 * @throws OHException
	 */
	public int getMortuaryTotalRows() throws OHException {
		int Total_Ligne = 0;
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();
		query.append("select count(*) AS Total_Ligne from DECES");
		
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
	
	/**
	 * Retrieves all stored {@link Death}s.
	 * the Deaths are filtered.
	 * @return the stored deaths.
	 * @throws OHException
	 *             if an error occurs retrieving the stored deaths.
	 */
	public ArrayList<Death> getDeaths(int start_index, int page_size) throws OHException {
		ArrayList<Death> deaths = null;
		List<Object> parameters = new ArrayList<Object>();
		
		StringBuilder query = new StringBuilder("SELECT D.*, M.*, P.PAT_SNAME, P.PAT_FNAME, P.PAT_AGE, P.PAT_SEX "
				+"	FROM DECES D "
				+"	JOIN MOTIF_DECES M ON DECES_MOTIF_ID=MOTIF_ID "
				+"	JOIN PATIENT P ON DECES_PAT_ID = PAT_ID WHERE DECES_DELETED != 1");

		parameters.add(start_index);
		parameters.add(page_size);
		query.append(" ORDER BY DECES_ID");
		query.append(" LIMIT ?, ?");
		//System.out.println("QUERY...:"+query);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			deaths = new ArrayList<Death>(resultSet.getFetchSize());
			while (resultSet.next()) {
				deaths.add(buildDeath(resultSet));
			}
			
						
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return deaths;
	}
	/**
	 * return the numbers of deaths rows 
	 * @param motif
	 * @param pavillon
	 * @param pat
	 * @param dateFrom
	 * @param dateTo
	 * @param isEntree
	 * @param isSortie
	 * @return
	 */
	public int getMortuaryTotalRows(int motif, String pavillon, int pat, GregorianCalendar dateFrom, GregorianCalendar dateTo, boolean isEntree, boolean isSortie) 
	
	throws OHException{
		int Total_Ligne = 0;
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>();
		String sqlString = "select count(*) AS Total_Ligne from DECES D  "
				+ "JOIN MOTIF_DECES M on D.DECES_MOTIF_ID = M.MOTIF_ID  "
				+ "JOIN PATIENT  ON  D.DECES_PAT_ID = PAT_ID WHERE DECES_DELETED != 1 ";
		
		if(isEntree) {
			if (dateFrom != null && dateTo != null) {
				sqlString +=  " AND DATE(D.DECES_DATE_ENTREE) >= ? AND DATE(D.DECES_DATE_ENTREE) <= ? ";
							parameters.add(new Timestamp(dateFrom.getTime().getTime()));
							parameters.add(new Timestamp(dateTo.getTime().getTime()));
			}
		}
		if(isSortie) {
			if (dateFrom != null && dateTo != null) {
				sqlString +=  " AND DATE(D.DECES_DATE_SORTIE_PROVISOIRE) >= ? AND DATE(D.DECES_DATE_SORTIE_PROVISOIRE) <= ?  ";
						parameters.add(new Timestamp(dateFrom.getTime().getTime()));
						parameters.add(new Timestamp(dateTo.getTime().getTime()));
			}
		
		}
		/*if(!isEntree && !isSortie) {
			sqlString += " AND WHERE 1 ";
		}*/
		if (motif != 0) {
			sqlString +=  " AND D.DECES_MOTIF_ID = ? ";
			parameters.add(motif);
		}
		if (pat != 0) {
			sqlString +=  " AND D.DECES_PAT_ID = ? ";
			parameters.add(pat);
		}

		if (pavillon!= null) {
			if(!pavillon.equals(MessageBundle.getMessage("angal.mortuary.all")))
			{	sqlString +=  " AND DECES_PROVENANCE = ? ";
				parameters.add(pavillon);
			}
		}	
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(sqlString, parameters, true);
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
	/**
	 * Check if the patient is already died
	 * @param code
	 * @return
	 * @throws OHException
	 */
	public boolean patientIsDied(int code) throws OHException{
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean died= false;
		try {
			List<Object> parameters = Collections.<Object>singletonList(code);
			String query = "SELECT count(*) AS TOTAL FROM DECES where DECES_PAT_ID = ? AND DECES_DELETED != 1 ";
			ResultSet set = dbQuery.getDataWithParams(query, parameters, true);
 			
			while(set.next()) {					
				died = set.getInt("TOTAL") > 0;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"));
		} finally{
			dbQuery.releaseConnection();
		}
			
		return died;
	}
	
}
