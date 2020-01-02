package org.isf.medicalinventory.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.menu.gui.MainMenu;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;

public class IoOperation {
	
	/**
	 * Return the list of {@link MedicalInventory}s.
	 * @return the list of {@link MedicalInventory}s.
	 * @throws OHException
	 */
	public ArrayList<MedicalInventory> getMedicalInventory() throws OHException {
		ArrayList<MedicalInventory> medicalInventory = null;
		String string = "SELECT * FROM medicaldsrinventory ORDER BY INVT_DATE DESC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(string, true);
			medicalInventory = new ArrayList<MedicalInventory>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicalInventory.add(new MedicalInventory(
						resultSet.getInt("INVT_ID"),
						resultSet.getString("INVT_STATE"),
						toCalendar(resultSet.getDate("INVT_DATE")),
						resultSet.getString("INVT_US_ID_A"),
						resultSet.getString("INVT_REFERENCE"),
						resultSet.getString("INVT_TYPE"),
						resultSet.getString("INVT_WRD_ID_A")
						));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return medicalInventory;
	}
	
	
	public int getInventoryInProgress() throws OHException {
		int result = 0;

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String query = "SELECT count(*) FROM medicaldsrinventory WHERE INVT_STATE = 1 AND INVT_TYPE = 1 ";
			
			ResultSet resultSet = dbQuery.getData(query, true);
			if (resultSet.first()) result = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	public int getInventoryWardInProgress(String wardId) throws OHException {
		int result = 0;

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String query = "SELECT count(*) FROM medicaldsrinventory WHERE INVT_STATE = 1 AND INVT_TYPE = 2 AND INVT_WRD_ID_A = ?";		
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
	
	public ArrayList<MedicalInventory> getMedicalInventory(GregorianCalendar dateFrom, GregorianCalendar dateTo, String state , String type) throws OHException {
		ArrayList<MedicalInventory> medicalInventory = null;
		String string = "SELECT * FROM medicaldsrinventory WHERE DATE(INVT_DATE) BETWEEN ? AND ? AND INVT_TYPE = ? ORDER BY INVT_DATE DESC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			parameters.add(type);
			if(!state.equals("") && state!=null){
				string = "SELECT * FROM medicaldsrinventory WHERE DATE(INVT_DATE) BETWEEN ? AND ? AND INVT_TYPE = ? AND INVT_STATE = ? ORDER BY INVT_DATE DESC";
				parameters.add(state);
			}
			//ResultSet resultSet = dbQuery.getData(string, true);
			ResultSet resultSet =  dbQuery.getDataWithParams(string, parameters, true);
			medicalInventory = new ArrayList<MedicalInventory>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicalInventory.add(new MedicalInventory(
						resultSet.getInt("INVT_ID"),
						resultSet.getString("INVT_STATE"),
						toCalendar(resultSet.getDate("INVT_DATE")),
						resultSet.getString("INVT_US_ID_A"),
						resultSet.getString("INVT_REFERENCE"),
						resultSet.getString("INVT_TYPE"),
						resultSet.getString("INVT_WRD_ID_A")
						));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return medicalInventory;
	}
	
	
	public ArrayList<MedicalInventory> getMedicalInventory(GregorianCalendar dateFrom, GregorianCalendar dateTo, String state , String type, int start_index, int page_size) throws OHException {
		ArrayList<MedicalInventory> medicalInventory = null;
		String string = "SELECT * FROM medicaldsrinventory WHERE DATE(INVT_DATE) BETWEEN ? AND ? AND INVT_TYPE = ? ORDER BY INVT_DATE DESC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			parameters.add(type);
			if(!state.equals("") && state!=null){
				string = "SELECT * FROM medicaldsrinventory WHERE DATE(INVT_DATE) BETWEEN ? AND ? AND INVT_TYPE = ? AND INVT_STATE = ? ORDER BY INVT_DATE DESC";
				parameters.add(state);
			}
			parameters.add(start_index);
			parameters.add(page_size);
			string += " LIMIT ?, ?";
			ResultSet resultSet =  dbQuery.getDataWithParams(string, parameters, true);
			medicalInventory = new ArrayList<MedicalInventory>(resultSet.getFetchSize());
			while (resultSet.next()) {
				medicalInventory.add(new MedicalInventory(
						resultSet.getInt("INVT_ID"),
						resultSet.getString("INVT_STATE"),
						toCalendar(resultSet.getDate("INVT_DATE")),
						resultSet.getString("INVT_US_ID_A"),
						resultSet.getString("INVT_REFERENCE"),
						resultSet.getString("INVT_TYPE"),
						resultSet.getString("INVT_WRD_ID_A")
						));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return medicalInventory;
	}
	
	/**
	 * Update an already existing {@link MedicalInventory}.
	 * @param medicalinventory - the {@link MedicalInventory} to update
	 * @return <code>true</code> if the medicalinventory has been updated, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean updateMedicalInventory(MedicalInventory medicalinventory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "UPDATE medicaldsrinventory SET INVT_STATE = ?, INVT_DATE = ?, INVT_US_ID_A = ? , INVT_REFERENCE = ? , INVT_TYPE = ? , INVT_WRD_ID_A = ?  WHERE INVT_ID = ?";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(medicalinventory.getState());
			params.add(new Timestamp(medicalinventory.getInventoryDate().getTimeInMillis()));
			params.add(medicalinventory.getUser());
			params.add(medicalinventory.getInventoryReference());
			params.add(medicalinventory.getInventoryType());
			params.add(medicalinventory.getWard());
			params.add(medicalinventory.getId());
			result = dbQuery.setDataWithParams(query, params, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * Insert a new {@link MedicalInventory} in the DB.
	 * @param medicalinventory - the {@link MedicalInventory} to insert.
	 * @return <code>true</code> if the medicalinventory has been inserted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean newMedicalInventory(MedicalInventory medicalinventory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "INSERT INTO medicaldsrinventory (INVT_STATE, INVT_DATE, INVT_US_ID_A,"
					+ " INVT_REFERENCE, INVT_TYPE, INVT_WRD_ID_A, INVT_CREATE_BY, INVT_CREATE_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(medicalinventory.getState());
			params.add(new Timestamp(medicalinventory.getInventoryDate().getTimeInMillis()));
			params.add(medicalinventory.getUser());	
			params.add(medicalinventory.getInventoryReference());	
			params.add(medicalinventory.getInventoryType());
			params.add(medicalinventory.getWard());	
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			result = dbQuery.setDataWithParams(query, params, true);			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	public int newMedicalInventoryGetId(MedicalInventory medicalinventory) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();		
		int inventoryCode = 0;
		try {
			String query = "INSERT INTO medicaldsrinventory (INVT_STATE, INVT_DATE, INVT_US_ID_A , INVT_REFERENCE, INVT_TYPE , INVT_WRD_ID_A, INVT_CREATE_BY, INVT_CREATE_DATE) VALUES (?, ?, ?, ?, ?, ?,?,?)";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(medicalinventory.getState());
			params.add(new Timestamp(medicalinventory.getInventoryDate().getTimeInMillis()));
			params.add(medicalinventory.getUser());	
			params.add(medicalinventory.getInventoryReference());
			params.add(medicalinventory.getInventoryType());
			params.add(medicalinventory.getWard());
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(query, params, true);
			if (result.next())
				inventoryCode = result.getInt(1);
			else
				return 0;
			return inventoryCode;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbQuery.releaseConnection();
		}
		return inventoryCode;
	}
	
	/**
	 * Delete the passed {@link MedicalInventory}.
	 * @param medicalinventory - the {@link MedicalInventory} to delete.
	 * @return <code>true</code> if the MedicalInventory has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteMedicalInventory(int code) throws OHException {
		String query1 = "DELETE FROM medicaldsrinventoryrow WHERE INVTR_INVT_ID = ?";
		String query2 = "DELETE FROM medicaldsrinventory WHERE INVT_ID = ?";
		List<Object> params = Collections.<Object>singletonList(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query1, params, true);
			result = dbQuery.setDataWithParams(query2, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	/**
	 * Delete the passed {@link MedicalInventory}.
	 * @param medicalinventory - the {@link MedicalInventory} to delete.
	 * @return <code>true</code> if the MedicalInventory has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteMedicalInventory(MedicalInventory medicalinventory) throws OHException {
		String query = "DELETE FROM medicaldsrinventory WHERE INVT_ID = ?";
		List<Object> params = Collections.<Object>singletonList(medicalinventory.getId());
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * check if the reference number is already used
	 * @return <code>true</code> if is already used, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean refNoExists(String refNo) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = Collections.<Object>singletonList(refNo);
			String query = "SELECT INVT_REFERENCE FROM medicaldsrinventory WHERE INVT_REFERENCE LIKE ?";
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			return resultSet.next();
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally{
			dbQuery.releaseConnection();
		}
	}
	
	/////////////////////////// crud inventoryRow  
	/**
	 * Return the list of {@link MedicalInventoryRow}s.
	 * @return the list of {@link MedicalInventoryRow}s.
	 * @throws OHException
	 */
	public ArrayList<MedicalInventoryRow> getMedicalInventoryRowByInventory(int inventoryCode) throws OHException {
		ArrayList<MedicalInventoryRow> medicalInventoryrow = null;
		String query = "SELECT * FROM ((medicaldsrinventoryrow join medicaldsrinventory ON INVTR_INVT_ID=INVT_ID) "
				+ " join medicaldsr on INVTR_MDSR_ID=MDSR_ID) left join medicaldsrlot on INVTR_LT_ID_A=LT_ID_A"
				+ " where INVTR_INVT_ID = ? ORDER BY MDSR_DESC asc";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(inventoryCode);
			dbQuery.getDataWithParams(query.toString(), params, true);
			//ResultSet resultSet = dbQuery.getData(string, true);
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			medicalInventoryrow = new ArrayList<MedicalInventoryRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Lot lot = toLot(resultSet);
				medicalInventoryrow.add(new MedicalInventoryRow(
						resultSet.getInt("INVTR_ID"),
						resultSet.getDouble("INVTR_THEORETIC_QTY"),
						resultSet.getDouble("INVTR_REAL_QTY"),
						new MedicalInventory(
								resultSet.getInt("INVT_ID"),
								resultSet.getString("INVT_STATE"),
								toCalendar(resultSet.getDate("INVT_DATE")),
								resultSet.getString("INVT_US_ID_A"),
								resultSet.getString("INVT_REFERENCE"),
								resultSet.getString("INVT_TYPE"),
								resultSet.getString("INVT_WRD_ID_A")
								),
						new Medical(
								resultSet.getInt("MDSR_ID"),
								null, 
								resultSet.getString("MDSR_CODE"),
								resultSet.getString("MDSR_DESC"),
								resultSet.getDouble("MDSR_INI_STOCK_QTI"), 
								resultSet.getInt("MDSR_PCS_X_PCK"),
								resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
								resultSet.getDouble("MDSR_IN_QTI"),
								resultSet.getDouble("MDSR_OUT_QTI"),
								resultSet.getInt("MDSR_LOCK")),
						lot, 
						resultSet.getInt("INVTR_COST")
						));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return medicalInventoryrow;
	}
	
	/**
	 * Return the list of {@link MedicalInventoryRow}s.
	 * @return the list of {@link MedicalInventoryRow}s.
	 * @throws OHException
	 */
	public ArrayList<MedicalInventoryRow> getMedicalInventoryRowByInventoryAndByMedicalCode(int inventoryCode, String medicalCode) throws OHException {
		ArrayList<MedicalInventoryRow> medicalInventoryrow = null;
		String query = "SELECT * FROM ((medicaldsrinventoryrow join medicaldsrinventory ON INVTR_INVT_ID=INVT_ID) "
				+ " join medicaldsr on INVTR_MDSR_ID=MDSR_ID) left join medicaldsrlot on INVTR_LT_ID_A=LT_ID_A"
				+ " where INVTR_INVT_ID = ? And MDSR_CODE = ? ORDER BY MDSR_DESC asc";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(inventoryCode);
			params.add(medicalCode);
			dbQuery.getDataWithParams(query.toString(), params, true);
			//ResultSet resultSet = dbQuery.getData(string, true);
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), params, true);
			medicalInventoryrow = new ArrayList<MedicalInventoryRow>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Lot lot = toLot(resultSet);
				medicalInventoryrow.add(new MedicalInventoryRow(
						resultSet.getInt("INVTR_ID"),
						resultSet.getDouble("INVTR_THEORETIC_QTY"),
						resultSet.getDouble("INVTR_REAL_QTY"),
						new MedicalInventory(
								resultSet.getInt("INVT_ID"),
								resultSet.getString("INVT_STATE"),
								toCalendar(resultSet.getDate("INVT_DATE")),
								resultSet.getString("INVT_US_ID_A"),
								resultSet.getString("INVT_REFERENCE"),
								resultSet.getString("INVT_TYPE"),
								resultSet.getString("INVT_WRD_ID_A")
								),
						new Medical(
								resultSet.getInt("MDSR_ID"),
								null, 
								resultSet.getString("MDSR_CODE"),
								resultSet.getString("MDSR_DESC"),
								resultSet.getDouble("MDSR_INI_STOCK_QTI"), 
								resultSet.getInt("MDSR_PCS_X_PCK"),
								resultSet.getDouble("MDSR_MIN_STOCK_QTI"),
								resultSet.getDouble("MDSR_IN_QTI"),
								resultSet.getDouble("MDSR_OUT_QTI"),
								resultSet.getInt("MDSR_LOCK")),
						lot, 
						resultSet.getInt("INVTR_COST")
						));
			}
			
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} 
		finally{
			dbQuery.releaseConnection();
		}
		return medicalInventoryrow;
	}
	
	/**
	 * Insert a new {@link MedicalInventoryRow} in the DB.
	 * @param medicalinventoryrow - the {@link MedicalInventoryRow} to insert.
	 * @return <code>true</code> if the MedicalInventoryRow has been inserted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean newMedicalInventoryRow(MedicalInventoryRow medicalinventoryrow) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "INSERT INTO medicaldsrinventoryrow (INVTR_THEORETIC_QTY, INVTR_REAL_QTY, INVTR_COST, INVTR_INVT_ID, INVTR_MDSR_ID, INVTR_LT_ID_A"
					+ ",INVTR_CREATE_BY, INVTR_CREATE_DATE) VALUES (?, ?, ?, ?, ?, ?,?,?)";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(medicalinventoryrow.getTheoreticqty());
			params.add(medicalinventoryrow.getRealqty());
			params.add(medicalinventoryrow.getCost());
			params.add(medicalinventoryrow.getInventory()!=null?medicalinventoryrow.getInventory().getId():0);
			params.add(medicalinventoryrow.getMedical().getCode()!=null?medicalinventoryrow.getMedical().getCode():0);
			params.add(medicalinventoryrow.getLot().getCode()!=null?medicalinventoryrow.getLot().getCode():"");	
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			result = dbQuery.setDataWithParams(query, params, true);			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * Insert a new {@link MedicalInventoryRow} in the DB.
	 * @param medicalinventoryrow - the {@link MedicalInventoryRow} to insert.
	 * @return <code>true</code> if the MedicalInventoryRow has been inserted, <code>false</code> otherwise.
	 * @throws OHException
	 * @throws SQLException 
	 */
	public int newMedicalInventoryRowGetId(MedicalInventoryRow medicalinventoryrow) throws OHException, SQLException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		int inventoryRowCode = 0;
		try {
			String query = "INSERT INTO medicaldsrinventoryrow (INVTR_THEORETIC_QTY, INVTR_REAL_QTY, INVTR_COST, INVTR_INVT_ID, INVTR_MDSR_ID, INVTR_LT_ID_A,INVTR_CREATE_BY, INVTR_CREATE_DATE) VALUES (?, ?, ?, ?, ?, ?,?,?)";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(medicalinventoryrow.getTheoreticqty());
			params.add(medicalinventoryrow.getRealqty());
			params.add(medicalinventoryrow.getCost());
			params.add(medicalinventoryrow.getInventory()!=null?medicalinventoryrow.getInventory().getId():0);
			params.add(medicalinventoryrow.getMedical().getCode()!=null?medicalinventoryrow.getMedical().getCode():0);
			params.add(medicalinventoryrow.getLot().getCode()!=null?medicalinventoryrow.getLot().getCode():"");	
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			//result = dbQuery.setDataWithParams(query, params, true);
			ResultSet resultt = dbQuery.setDataReturnGeneratedKeyWithParams(query, params, true);
			if (resultt.next())
				inventoryRowCode = resultt.getInt(1);
			else
				return 0;
			return inventoryRowCode;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dbQuery.releaseConnection();
		}
		return inventoryRowCode;
	}
	
	/**
	 * Update an already existing {@link MedicalInventoryRow}.
	 * @param medicalinventoryrow - the {@link MedicalInventoryRow} to update
	 * @return <code>true</code> if the MedicalInventoryRow has been updated, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean updateMedicalInventoryRow(MedicalInventoryRow medicalinventoryrow) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			String query = "UPDATE medicaldsrinventoryrow SET INVTR_THEORETIC_QTY = ?, INVTR_REAL_QTY = ?, INVTR_COST = ?, INVTR_INVT_ID = ? , INVTR_MDSR_ID = ? ,"
					+ " INVTR_LT_ID_A = ?, INVTR_MODIFY_BY = ?, INVTR_MODIFY_DATE = ?  WHERE INVTR_ID = ?";
			ArrayList<Object> params = new ArrayList<Object>();
			params.add(medicalinventoryrow.getTheoreticqty());
			params.add(medicalinventoryrow.getRealqty());
			params.add(medicalinventoryrow.getCost());
			params.add(medicalinventoryrow.getInventory().getId());
			params.add(medicalinventoryrow.getMedical().getCode()!=null?medicalinventoryrow.getMedical().getCode():"");
			params.add(medicalinventoryrow.getLot().getCode()!=null?medicalinventoryrow.getLot().getCode():"");
			params.add(MainMenu.getUser());
			params.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			params.add(medicalinventoryrow.getId());
			result = dbQuery.setDataWithParams(query, params, true);
			
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	/**
	 * Delete the passed {@link MedicalInventoryRow}.
	 * @param code - the {@link MedicalInventoryRow} to delete.
	 * @return <code>true</code> if the MedicalInventoryrow has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteMedicalInventoryRow(int code) throws OHException {
		String query = "DELETE FROM medicaldsrinventoryrow WHERE INVTR_ID = ?";
		List<Object> params = Collections.<Object>singletonList(code);
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	/**
	 * Delete the passed {@link MedicalInventoryRow}.
	 * @param medicalinventoryrow - the {@link MedicalInventoryRow} to delete.
	 * @return <code>true</code> if the medicalinventoryrow has been deleted, <code>false</code> otherwise.
	 * @throws OHException
	 */
	public boolean deleteMedicalInventoryRow(MedicalInventoryRow medicalinventoryrow) throws OHException {
		String query = "DELETE FROM medicaldsrinventoryrow WHERE INVTR_ID = ?";
		List<Object> params = Collections.<Object>singletonList(medicalinventoryrow.getId());
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = false;
		try {
			result = dbQuery.setDataWithParams(query, params, true);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	//////////////////////////////////////////////
	protected GregorianCalendar toCalendar(Date date){
		if (date == null) return null;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}
	protected Lot toLot(ResultSet resultSet) throws SQLException
	{
		String code = resultSet.getString("LT_ID_A")!=null?resultSet.getString("LT_ID_A"):"";
		GregorianCalendar preparationDate = toCalendar(resultSet.getDate("LT_PREP_DATE"));
		GregorianCalendar dueDate = toCalendar(resultSet.getDate("LT_DUE_DATE"));
		Double cost = resultSet.getDouble("LT_COST");
		return new Lot(code, preparationDate, dueDate, cost);
	}
}
