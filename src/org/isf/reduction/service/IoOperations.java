package org.isf.reduction.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.isf.reduction.model.ExamsReduction;
import org.isf.reduction.model.MedicalsReduction;
import org.isf.reduction.model.OperationReduction;
import org.isf.reduction.model.OtherReduction;
import org.isf.reduction.model.ReductionPlan;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

/**
 * Persistence class for Accounting module.
 */
public class IoOperations {

	/**
	 * Get all the {@link ReductionPlan}s.
	 * 
	 * @return a list of reductionPlans.
	 * @throws OHException
	 *             if an error occurs retrieving the reductionPlans.
	 */
	public ArrayList<ReductionPlan> getReductionPlans()
			throws OHException {
		ArrayList<ReductionPlan> reductionPlans = null;
		String query = "SELECT * FROM REDUCTIONPLAN ORDER BY RP_ID ";

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			reductionPlans = new ArrayList<ReductionPlan>(
					resultSet.getFetchSize());
			while (resultSet.next()) {
				ReductionPlan patientBillingInfo = toReductionPlan(resultSet);
				reductionPlans.add(patientBillingInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return reductionPlans;
	}

	/**
	 * Get all the {@link ExamsReduction}s.
	 * 
	 * @param rpId
	 *            the id of the {@link ReductionPlan} that we want to
	 *            retrieve exam rate exceptions
	 * @return a list of ExamsReduction for the given {@link ReductionPlan}
	 *         .
	 * @throws OHException
	 *             if an error occurs retrieving the reductionPlans.
	 */
	public ArrayList<ExamsReduction> getExamsReductions(int rpId)
			throws OHException {
		ArrayList<ExamsReduction> examsReductions = null;
		String query = "SELECT * FROM EXAMSREDUCTION WHERE ER_RP_ID = ? ";
		List<Object> parameters = Collections.<Object> singletonList(rpId);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters,
					true);
			examsReductions = new ArrayList<ExamsReduction>(
					resultSet.getFetchSize());
			while (resultSet.next()) {
				ExamsReduction examReduction = toExamReduction(resultSet);
				examsReductions.add(examReduction);
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return examsReductions;
	}

	/**
	 * Get all the {@link MedicalsReduction}s.
	 * 
	 * @param rpID
	 *            the id of the {@link ReductionPlan} that we want to
	 *            retrieve medicals rate exceptions
	 * @return a list of MedicalsReduction for the given
	 *         {@link ReductionPlan}.
	 * @throws OHException
	 *             if an error occurs retrieving the reductionPlans.
	 */
	public ArrayList<MedicalsReduction> getMedicalsReductions(int rpID)
			throws OHException {
		ArrayList<MedicalsReduction> medicalsReductions = null;
		String query = "SELECT * FROM MEDICALSREDUCTION WHERE MR_RP_ID = ?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters,
					true);
			medicalsReductions = new ArrayList<MedicalsReduction>(
					resultSet.getFetchSize());
			while (resultSet.next()) {
				MedicalsReduction medicalsReduction = toMedicalsReduction(resultSet);
				medicalsReductions.add(medicalsReduction);
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return medicalsReductions;
	}

	/**
	 * Get all the {@link OperationReduction}s.
	 * 
	 * @param rpID
	 *            the id of the {@link ReductionPlan} that we want to
	 *            retrieve operation rate exceptions
	 * @return a list of OperationReduction for the given
	 *         {@link ReductionPlan}.
	 * @throws OHException
	 *             if an error occurs retrieving the reductionPlans.
	 */
	public ArrayList<OperationReduction> getOperationsReductions(int rpID)
			throws OHException {
		ArrayList<OperationReduction> operationReductions = null;
		String query = "SELECT * FROM OPERATIONSREDUCTION WHERE OPR_RP_ID = ?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters,
					true);
			operationReductions = new ArrayList<OperationReduction>(
					resultSet.getFetchSize());
			while (resultSet.next()) {
				OperationReduction operationReduction = toOperationReduction(resultSet);
				operationReductions.add(operationReduction);
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return operationReductions;
	}

	/**
	 * Get all the {@link OtherReduction}s.
	 * 
	 * @param rpID
	 *            the id of the {@link ReductionPlan} that we want to
	 *            retrieve other services rate exceptions
	 * @return a list of OtherReduction for the given {@link ReductionPlan}
	 *         .
	 * @throws OHException
	 *             if an error occurs retrieving the OtherReduction.
	 */
	public ArrayList<OtherReduction> getOtherReductions(int rpID)
			throws OHException {
		ArrayList<OtherReduction> otherReductions = null;
		String query = "SELECT * FROM OTHERREDUCTION WHERE OTR_RP_ID = ? ";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters,
					true);
			otherReductions = new ArrayList<OtherReduction>(
					resultSet.getFetchSize());
			while (resultSet.next()) {
				OtherReduction otherReduction = toOtherReduction(resultSet);
				otherReductions.add(otherReduction);
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return otherReductions;
	}

	/**
	 * Update the exam reduction list for the given reductionPlan Id
	 * @param examReductions exam reduction list
	 * @param rpID reductionPlan Id
	 * @param dbQuery Current query being used to add or update the reductionPlan
	 * @return return true
	 * @throws OHException
	 */
	private boolean updateExamReduction(
			ArrayList<ExamsReduction> examReductions, int rpID,
			DbQueryLogger dbQuery) throws OHException {

		String query = "DELETE FROM EXAMSREDUCTION WHERE ER_RP_ID = ?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		dbQuery.setDataWithParams(query, parameters, false);

		for (Iterator<ExamsReduction> iterator = examReductions.iterator(); iterator
				.hasNext();) {
			ExamsReduction examReduction = (ExamsReduction) iterator.next();
			examReduction.setReductionPlanID(rpID);
			query = "INSERT INTO EXAMSREDUCTION ("
					+ "ER_RP_ID, ER_EXA_ID_A, ER_REDUCTIONRATE) "
					+ "VALUES (?,?,?)";

			parameters = new ArrayList<Object>(3);

			parameters.add(examReduction.getReductionPlanID());
			parameters.add(examReduction.getExaCode());
			parameters.add(examReduction.getReductionRate());

			dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, false);
		}
		return true;
	}
	
	/**
	 * Update the medical reduction list for the given patientBillingInfo Id
	 * @param medicalsReductions medical reduction list
	 * @param rpID reductionPlan Id
	 * @param dbQuery Current query being used to add or update the reductionPlan
	 * @return return true
	 * @throws OHException
	 */
	private boolean updateMedicalReduction(
			ArrayList<MedicalsReduction> medicalsReductions, int rpID,
			DbQueryLogger dbQuery) throws OHException {
		
		String query = "DELETE FROM MEDICALSREDUCTION WHERE MR_RP_ID=?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		dbQuery.setDataWithParams(query, parameters, false);
		
		for (Iterator<MedicalsReduction> iterator = medicalsReductions.iterator(); iterator
				.hasNext();) {
			MedicalsReduction medicalsReduction = (MedicalsReduction) iterator.next();
			medicalsReduction.setReductionPlanID(rpID);
			query = "INSERT INTO MEDICALSREDUCTION ("
					+ "MR_RP_ID, MR_MED_ID, MR_REDUCTIONRATE) "
					+ "VALUES (?,?,?)";
			
			parameters = new ArrayList<Object>(3);
			
			parameters.add(medicalsReduction.getReductionPlanID());
			parameters.add(medicalsReduction.getMedID());
			parameters.add(medicalsReduction.getReductionRate());
			
			dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, false);
		}
		return true;
	}
	/**
	 * Update the operation reduction list for the given reductionPlan ID
	 * @param operationReductions operation reduction list
	 * @param rpID reductionPlan Id
	 * @param dbQuery Current query being used to add or update the reductionPlan
	 * @return return true
	 * @throws OHException
	 */
	private boolean updateOperationReduction(
			ArrayList<OperationReduction> operationReductions, int rpID,
			DbQueryLogger dbQuery) throws OHException {
		
		String query = "DELETE FROM OPERATIONSREDUCTION WHERE OPR_RP_ID=?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		dbQuery.setDataWithParams(query, parameters, false);
		
		for (Iterator<OperationReduction> iterator = operationReductions.iterator(); iterator
				.hasNext();) {
			OperationReduction operationReduction = (OperationReduction) iterator.next();
			operationReduction.setReductionPlanID(rpID);
			query = "INSERT INTO OPERATIONSREDUCTION ("
					+ "OPR_RP_ID, OPR_OPE_ID_A, OPR_REDUCTIONRATE) "
					+ "VALUES (?,?,?)";
			
			parameters = new ArrayList<Object>(3);
			
			parameters.add(operationReduction.getReductionPlanID());
			parameters.add(operationReduction.getOpeCode());
			parameters.add(operationReduction.getReductionRate());
			
			dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, false);
		}
		return true;
	}
	/**
	 * Update the other reduction list for the given reductionPlan ID
	 * @param otherReductions other reduction list
	 * @param rpID reductionPlan Id
	 * @param dbQuery Current query being used to add or update the reductionPlan
	 * @return return true
	 * @throws OHException
	 */
	private boolean updateOtherReduction(
			ArrayList<OtherReduction> otherReductions, int rpID,
			DbQueryLogger dbQuery) throws OHException {
		
		String query = "DELETE FROM OTHERREDUCTION WHERE OTR_RP_ID=?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		dbQuery.setDataWithParams(query, parameters, false);
		
		for (Iterator<OtherReduction> iterator = otherReductions.iterator(); iterator
				.hasNext();) {
			OtherReduction otherReduction = (OtherReduction) iterator.next();
			otherReduction.setReductionPlanID(rpID);
			query = "INSERT INTO OTHERREDUCTION ("
					+ "OTR_RP_ID, OTR_OTH_ID, OTR_REDUCTIONRATE) "
					+ "VALUES (?,?,?)";
			
			parameters = new ArrayList<Object>(3);
			
			parameters.add(otherReduction.getReductionPlanID());
			parameters.add(otherReduction.getOthID());
			parameters.add(otherReduction.getReductionRate());
			
			dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, false);
		}
		return true;
	}

	/**
	 * Get the {@link ReductionPlan} with specified rpID.
	 * 
	 * @param rpID
	 * @return the {@link ReductionPlan}.
	 * @throws OHException
	 *             if an error occurs retrieving the ReductionPlan.
	 */
	public ReductionPlan getReductionPlan(int rpID)
			throws OHException {
		ReductionPlan reductionPlan = null;
		String query = "SELECT * FROM REDUCTIONPLAN WHERE RP_ID = ?";
		List<Object> parameters = Collections.<Object> singletonList(rpID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters,
					true);
			while (resultSet.next()) {
				reductionPlan = toReductionPlan(resultSet);
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return reductionPlan;
	}
	
	public ReductionPlan getReductionPlan(String description)
			throws OHException {
		ReductionPlan reductionPlan = null;
		String query = "SELECT * FROM REDUCTIONPLAN WHERE RP_DESCRIPTION = ?";
		List<Object> parameters = Collections.<Object> singletonList(description);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			while (resultSet.next()) {
				reductionPlan = toReductionPlan(resultSet);
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.releaseConnection();
		}
		return reductionPlan;
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
	private GregorianCalendar convertToGregorianCalendar(Timestamp aDate) {
		if (aDate == null)
			return null;
		GregorianCalendar time = new GregorianCalendar();
		time.setTime(aDate);
		return time;
	}

	/**
	 * Stores a new {@link ReductionPlan}.
	 * 
	 * @param reductionPlan
	 *            the ReductionPlan to store.
	 * @return the generated {@link ReductionPlan} id.
	 * @throws OHException
	 *             if an error occurs storing the reductionPlan.
	 */
	public int newReductionPlan(ReductionPlan reductionPlan)
			throws OHException {
		int reductionPlanId;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			String query = "INSERT INTO REDUCTIONPLAN ("
					+ "RP_DATE, RP_UPDATE, RP_DESCRIPTION, RP_OPERATIONRATE, RP_MEDICALRATE, RP_EXAMRATE, RP_OTHERRATE) "
					+ "VALUES (?,?,?,?,?,?,?)";

			List<Object> parameters = new ArrayList<Object>(7);
			parameters.add(new java.sql.Timestamp(reductionPlan.getDate()
					.getTime().getTime()));
			parameters.add(new java.sql.Timestamp(reductionPlan.getUpdate()
					.getTime().getTime()));
			parameters.add(reductionPlan.getDescription());
			parameters.add(reductionPlan.getOperationRate());
			parameters.add(reductionPlan.getMedicalRate());
			parameters.add(reductionPlan.getExamRate());
			parameters.add(reductionPlan.getOtherRate());
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(
					query, parameters, false);

			if (result.next())
				reductionPlanId = result.getInt(1);
			else
				return 0;
			
			if(reductionPlanId>0){
				reductionPlan.setId(reductionPlanId);
				updateExamReduction(reductionPlan.getExamreductions(), reductionPlan.getId(), dbQuery);
				updateMedicalReduction(reductionPlan.getMedicalsReductions(), reductionPlan.getId(), dbQuery);
				updateOperationReduction(reductionPlan.getOperationReductions(), reductionPlan.getId(), dbQuery);
				updateOtherReduction(reductionPlan.getOtherReductions(), reductionPlan.getId(), dbQuery);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQuery.commit();
			dbQuery.releaseConnection();
		}
		return reductionPlanId;
	}

	/**
	 * Updates the specified {@link ReductionPlan}.
	 * 
	 * @param reductionPlan
	 *            the reductionPlan to update.
	 * @return <code>true</code> if the reductionPlan has been updated,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the update.
	 */
	public boolean updateReductionPlan(
			ReductionPlan reductionPlan) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			String query = "UPDATE REDUCTIONPLAN SET " + "RP_DATE = ?, "
					+ "RP_UPDATE = ?, " + "RP_DESCRIPTION = ?, "
					+ "RP_OPERATIONRATE = ?, " + "RP_MEDICALRATE = ?, "
					+ "RP_EXAMRATE = ?, " + "RP_OTHERRATE = ? "
					+ "WHERE RP_ID = ?";

			List<Object> parameters = new ArrayList<Object>(8);

			parameters.add(new java.sql.Timestamp(reductionPlan.getDate()
					.getTimeInMillis()));
			GregorianCalendar update=TimeTools.getServerDateTime();
			parameters.add(new java.sql.Timestamp(update.getTimeInMillis()));
			parameters.add(reductionPlan.getDescription());
			parameters.add(reductionPlan.getOperationRate());
			parameters.add(reductionPlan.getMedicalRate());
			parameters.add(reductionPlan.getExamRate());
			parameters.add(reductionPlan.getOtherRate());
			parameters.add(reductionPlan.getId());

			// System.out.println(pstmt.toString());
			dbQuery.setDataWithParams(query, parameters, false);

			updateExamReduction(reductionPlan.getExamreductions(), reductionPlan.getId(), dbQuery);
			updateMedicalReduction(reductionPlan.getMedicalsReductions(), reductionPlan.getId(), dbQuery);
			updateOperationReduction(reductionPlan.getOperationReductions(), reductionPlan.getId(), dbQuery);
			updateOtherReduction(reductionPlan.getOtherReductions(), reductionPlan.getId(), dbQuery);
			
		} finally {
			dbQuery.commit();
			dbQuery.releaseConnection();
		}
		return true;
	}

	/**
	 * Deletes the specified {@link ReductionPlan}.
	 * 
	 * @param reductionPlan
	 *            the reductionPlan to delete.
	 * @param forceDelete
	 *            if true, the element is deleted and all patients assign to the
	 *            given element are assign to null can not have reduction on
	 *            bills
	 * @return <code>true</code> if the bill has been deleted,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs deleting the bill.
	 */
	public boolean deleteReductionPlan(ReductionPlan reductionPlan,
			boolean forceDelete) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try {
			List<Object> parameters = Collections
					.<Object> singletonList(reductionPlan.getId());

			boolean candelete = false;
			if (forceDelete) {
				String query = "UPDATE PATIENT SET RP_ID=NULL WHERE RP_ID = ?";
				dbQuery.setDataWithParams(query, parameters, false);
				candelete = true;
			} else {
				candelete = canDelete(reductionPlan.getId());
			}
			if (candelete) {
				String query = "DELETE FROM EXAMSREDUCTION WHERE ER_RP_ID=?";
				dbQuery.setDataWithParams(query, parameters, false);
				
				query = "DELETE FROM MEDICALSREDUCTION WHERE MR_RP_ID = ?";
				dbQuery.setDataWithParams(query, parameters, false);
				
				query = "DELETE FROM OPERATIONSREDUCTION WHERE OPR_RP_ID = ?";
				dbQuery.setDataWithParams(query, parameters, false);
				
				query = "DELETE FROM OTHERREDUCTION WHERE OTR_RP_ID = ?";
				dbQuery.setDataWithParams(query, parameters, false);
				
				query = "DELETE FROM REDUCTIONPLAN WHERE RP_ID = ?";
				dbQuery.setDataWithParams(query, parameters, false);
			}
		} finally {
			dbQuery.commit();
			dbQuery.releaseConnection();
		}
		return result;
	}

	public boolean canDelete(int rpID)
			throws OHException {
		boolean result=false;
		String query = "SELECT * FROM PATIENT WHERE PAT_RP_ID=?";
		DbQueryLogger dbQueryFetch = new DbQueryLogger();
		
		List<Object> parameters = Collections
				.<Object> singletonList(rpID);
		
		try {
			ResultSet resultSet = dbQueryFetch.getDataWithParams(query,
					parameters, true);
			if (resultSet.getFetchSize() <= 0) {
				// Allow deletion
				result = true;
			}
		} catch (SQLException e) {
			throw new OHException(
					MessageBundle
							.getMessage("angal.sql.problemsoccurredwiththesqlistruction"),
					e);
		} finally {
			dbQueryFetch.releaseConnection();
		}
		return result;
	}

	private ReductionPlan toReductionPlan(ResultSet resultSet)
			throws SQLException, OHException {
		ReductionPlan patientBillingInfo = new ReductionPlan(
				resultSet.getInt("RP_ID"),
				resultSet.getString("RP_DESCRIPTION"),
				resultSet.getDouble("RP_OPERATIONRATE"),
				resultSet.getDouble("RP_MEDICALRATE"),
				resultSet.getDouble("RP_EXAMRATE"),
				resultSet.getDouble("RP_OTHERRATE"),
				convertToGregorianCalendar(resultSet.getTimestamp("RP_DATE")),
				convertToGregorianCalendar(resultSet.getTimestamp("RP_UPDATE")));
		return patientBillingInfo;
	}

	private ExamsReduction toExamReduction(ResultSet resultSet)
			throws SQLException {
		ExamsReduction examReduction = new ExamsReduction(
				resultSet.getInt("ER_RP_ID"), resultSet.getString("ER_EXA_ID_A"),
				resultSet.getDouble("ER_REDUCTIONRATE"));
		return examReduction;
	}

	private MedicalsReduction toMedicalsReduction(ResultSet resultSet)
			throws SQLException {
		MedicalsReduction medicalReduction = new MedicalsReduction(
				resultSet.getInt("MR_RP_ID"), resultSet.getInt("MR_MED_ID"),
				resultSet.getDouble("MR_REDUCTIONRATE"));
		return medicalReduction;
	}

	private OperationReduction toOperationReduction(ResultSet resultSet)
			throws SQLException {
		OperationReduction operationReduction = new OperationReduction(
				resultSet.getInt("OPR_RP_ID"), resultSet.getString("OPR_OPE_ID_A"),
				resultSet.getDouble("OPR_REDUCTIONRATE"));
		return operationReduction;
	}

	private OtherReduction toOtherReduction(ResultSet resultSet)
			throws SQLException {
		OtherReduction otherReduction = new OtherReduction(
				resultSet.getInt("OTR_RP_ID"), resultSet.getInt("OTR_OTH_ID"),
				resultSet.getDouble("OTR_REDUCTIONRATE"));
		return otherReduction;
	}

}
