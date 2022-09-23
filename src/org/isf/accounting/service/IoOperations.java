package org.isf.accounting.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.model.User;
import org.isf.opetype.model.OperationType;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.BillItemReportBean;
import org.isf.utils.jobjects.BillItemStatus;
import org.isf.utils.time.TimeTools;

/**
 * Persistence class for Accounting module.
 */
public class IoOperations {

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * 
	 * @param patID
	 *            the patient id.
	 * @return the list of pending bills.
	 * @throws OHException
	 *             if an error occurs retrieving the pending bills.
	 */
	public ArrayList<Bill> getPendingBills(int patID) throws OHException {
		ArrayList<Bill> pendingBills = null;
		List<Object> parameters = new ArrayList<Object>(1);
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";

		StringBuilder query = new StringBuilder("SELECT b.*, ");
		query.append(refundBillQuery);
		query.append("FROM BILLS b WHERE b.BLL_STATUS = 'O' AND b.BLL_PARENT_ID IS NULL");
		
		if (patID != 0) {
			query.append(" AND b.BLL_ID_PAT = ?");
			parameters.add(patID);
			query.append(" ORDER BY b.BLL_DATE DESC");
		} else if (patID == 0) {
			query.append(" ORDER BY b.BLL_DATE DESC LIMIT 80");
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}
	
	/**
	 * Gets pending {@link Bill}s for specific patient
	 * @param patID The patien's Id
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getPendingBills2(int patID) throws OHException {
		ArrayList<Bill> pendingBills = null;
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		List<Object> parameters = new ArrayList<Object>(1);

		StringBuilder query = new StringBuilder("SELECT b.*, ");

		query.append(refundBillQuery);
		query.append(" FROM BILLS b WHERE b.BLL_STATUS = 'O' AND b.BLL_PARENT_ID IS NULL");
		
		if (patID != 0) {
			query.append(" AND b.BLL_ID_PAT = ?");
			parameters.add(patID);
			query.append(" ORDER BY b.BLL_DATE DESC");
		} else if (patID == 0) {
			query.append(" ORDER BY b.BLL_DATE DESC LIMIT 80");
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}
	
	/**
	 * Gets pending {@link Bill}s filtered by specific affiliated patient
	 * 
	 * @param patID The affiliated patient's Id
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getPendingBillsAffiliate(int patID) throws OHException {
		ArrayList<Bill> pendingBills = null;
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		List<Object> parameters = new ArrayList<Object>(1);
		
		StringBuilder query = new StringBuilder("SELECT b.*, ");
		query.append(refundBillQuery);
		query.append(" FROM BILLS b WHERE b.BLL_STATUS = 'O' AND b.BLL_PARENT_ID IS NULL");
		if (patID != 0) {
			query.append("  AND ( b.BLL_ID_PAT = ? OR b.BLL_PAT_AFFILIATED_PERSON = ? )");
			parameters.add(patID);
			parameters.add(patID);
		}
		
		query.append(" ORDER BY b.BLL_DATE DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}

	/**
	 * Get all {@link Bill}s.
	 * 
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException if an error occurs retrieving the bills.
	 */
	public ArrayList<Bill> getBills() throws OHException {
		ArrayList<Bill> bills = null;
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		String query = "SELECT b.*, " + refundBillQuery + " FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL ORDER BY b.BLL_DATE DESC";
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}

	/**
	 * Returns only the reimbursed {@link BillItems} associated 
	 * to the specified {@link Bill}'s id or returns null.
	 * 
	 * @author Silevester D.
	 * @since 18/05/2022
	 * 
	 * @param billID  the bill id.
	 * @return a list of {@link BillItems} that has been reimbursed.
	 * @throws OHException if an error occurs retrieving the bill items.
	 */
	public ArrayList<BillItems> getOnlyRefundItems(int billID, boolean autoCommit) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT bli.*, sum(bli.BLI_QTY) as REFUND_QTY "
				+ "FROM BILLITEMS bli INNER JOIN bills b ON b.BLL_ID = bli.BLI_ID_BILL "
				+ "WHERE b.BLL_PARENT_ID = ? GROUP BY bli.BLI_ID_PRICE");

		parameters.add(billID);

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>(resultSet.getFetchSize());
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(resultSet.getInt("BLI_ID"), resultSet.getInt("BLI_ID_BILL"),
						resultSet.getBoolean("BLI_IS_PRICE"), resultSet.getString("BLI_ID_PRICE"),
						resultSet.getString("BLI_ITEM_DESC"), resultSet.getDouble("BLI_ITEM_AMOUNT"),
						resultSet.getDouble("BLI_QTY"), null);
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				bliItem.setPrescriptionId(resultSet.getInt("BLI_PRESC_ID"));
				bliItem.setItemAmountBrut(resultSet.getInt("BLI_ITEM_AMOUNT_BRUT"));
				bliItem.setExport_status(resultSet.getString("BLI_EXPORT_STATUS"));
				bliItem.setRefundedQty(resultSet.getInt("REFUND_QTY"));
				billItems.add(bliItem);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	/**
	 * Get all refund bills of a given {@link Bill}'s id.
	 * 
	 * @author Silevester D.
	 * @since 18/05/2022
	 * 
	 * @return a list of {@link Bill}.
	 * @throws OHException
	 *             if an error occurs retrieving the bills.
	 */
	public ArrayList<Bill> getRefundBills(int billId) throws OHException {
		ArrayList<Bill> bills = null;
		List<Object> parameters = new ArrayList<Object>(1);

		String query = "SELECT * FROM BILLS WHERE BLL_PARENT_ID = ? ORDER BY BLL_DATE DESC";
		parameters.add(billId);

		DbQueryLogger dbQuery = new DbQueryLogger();

		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}
	
	/**
	 * Get the {@link Bill} with specified billID.
	 * 
	 * @param billID
	 * @return the {@link Bill}.
	 * @throws OHException
	 *             if an error occurs retrieving the bill.
	 */
	public Bill getBill(int billID) throws OHException {
		Bill bill = null;
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		String query = "SELECT b.*, " + refundBillQuery + " FROM BILLS b WHERE b.BLL_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(billID);
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			while (resultSet.next()) {
				bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bill;
	}

	/**
	 * Returns all user ids related to a {@link BillPayments}.
	 * 
	 * @return a list of user id.
	 * @throws OHException
	 *             if an error occurs retrieving the users list.
	 */
	public ArrayList<String> getUsers() throws OHException {
		ArrayList<String> userIds = null;
		String query = "SELECT DISTINCT(BLP_USR_ID_A) FROM BILLPAYMENTS ORDER BY BLP_USR_ID_A ASC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			userIds = new ArrayList<String>(resultSet.getFetchSize());
			while (resultSet.next()) {
				userIds.add(resultSet.getString("BLP_USR_ID_A"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return userIds;
	}

	/**
	 * Returns the {@link BillItems} associated to the specified {@link Bill} id
	 * or all the stored {@link BillItems} if no id is provided.
	 * 
	 * @param billID
	 *            the bill id or <code>0</code>.
	 * @return a list of {@link BillItems} associated to the bill id or all the
	 *         stored bill items.
	 * @throws OHException
	 *             if an error occurs retrieving the bill items.
	 */
	public ArrayList<BillItems> getItems(int billID, boolean autoCommit) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLITEMS");
		if (billID != 0) {
			query.append(" WHERE BLI_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" ORDER BY BLI_ID ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>(resultSet.getFetchSize());
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(resultSet.getInt("BLI_ID"), resultSet.getInt("BLI_ID_BILL"),
						resultSet.getBoolean("BLI_IS_PRICE"), resultSet.getString("BLI_ID_PRICE"),
						resultSet.getString("BLI_ITEM_DESC"), resultSet.getDouble("BLI_ITEM_AMOUNT"),
						resultSet.getDouble("BLI_QTY"), convertToGregorianCalendar(resultSet.getTimestamp("BLI_DATE")));
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				bliItem.setPrescriptionId(resultSet.getInt("BLI_PRESC_ID"));
				bliItem.setItemAmountBrut(resultSet.getInt("BLI_ITEM_AMOUNT_BRUT"));
				bliItem.setExport_status(resultSet.getString("BLI_EXPORT_STATUS"));
				billItems.add(bliItem);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	public ArrayList<BillItems> getItems(int billID, boolean autoCommit, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(3);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLITEMS");
		query.append(" WHERE DATE(BLI_DATE) BETWEEN ? AND ?");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		if (billID != 0) {
			query.append(" AND BLI_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" ORDER BY BLI_ID ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>(resultSet.getFetchSize());
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(resultSet.getInt("BLI_ID"), resultSet.getInt("BLI_ID_BILL"),
						resultSet.getBoolean("BLI_IS_PRICE"), resultSet.getString("BLI_ID_PRICE"),
						resultSet.getString("BLI_ITEM_DESC"), resultSet.getDouble("BLI_ITEM_AMOUNT"),
						resultSet.getDouble("BLI_QTY"), convertToGregorianCalendar(resultSet.getTimestamp("BLI_DATE")));
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				bliItem.setPrescriptionId(resultSet.getInt("BLI_PRESC_ID"));
				bliItem.setItemAmountBrut(resultSet.getInt("BLI_ITEM_AMOUNT_BRUT"));
				bliItem.setExport_status(resultSet.getString("BLI_EXPORT_STATUS"));
				billItems.add(bliItem);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	public ArrayList<BillItems> getItemsBy(int billID, boolean autoCommit) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(3);
		String queryString = "SELECT BLI_ITEM_ID, BLI_ITEM_GROUP, BLI_ID, BLI_ID_BILL, BLI_ITEM_DESC, BLI_IS_PRICE, BLI_ITEM_AMOUNT, BLI_QTY, BLI_ID_PRICE, BLI_DATE  FROM BILLITEMS"; //SUM(BLI_QTY) as
		StringBuilder query = new StringBuilder(queryString);
		if (billID != 0) {
			query.append(" WHERE BLI_ID_BILL = ?");
			parameters.add(billID);
		}
		//query.append(" GROUP BY BLI_ITEM_DESC");
		query.append(" ORDER BY BLI_ID ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>();
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(
					resultSet.getInt("BLI_ID"), 
					resultSet.getInt("BLI_ID_BILL"),
					resultSet.getBoolean("BLI_IS_PRICE"), 
					resultSet.getString("BLI_ID_PRICE"),
					resultSet.getString("BLI_ITEM_DESC"), 
					resultSet.getDouble("BLI_ITEM_AMOUNT"),
					resultSet.getDouble("BLI_QTY"),	
					null
				);
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				bliItem.setItemDate(convertToGregorianCalendar(resultSet.getTimestamp("BLI_DATE")));
				if(bliItem.getItemQuantity() != 0.0) {
					billItems.add(bliItem);
				}
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	public ArrayList<BillItems> getItemsBy(int billID, boolean autoCommit, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(3);
		String queryString = "SELECT BLI_ITEM_ID, BLI_ITEM_GROUP, BLI_ID, BLI_ID_BILL, BLI_ITEM_DESC, BLI_IS_PRICE, BLI_ITEM_AMOUNT, SUM(BLI_QTY) AS BLI_QTY, BLI_ID_PRICE FROM BILLITEMS";
		StringBuilder query = new StringBuilder(queryString);
		query.append(" WHERE DATE(BLI_DATE) BETWEEN ? AND ?");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		if (billID != 0) {
			query.append(" WHERE BLI_ID_BILL = ?");
			parameters.add(billID);
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>();
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(
					resultSet.getInt("BLI_ID"), 
					resultSet.getInt("BLI_ID_BILL"),
					resultSet.getBoolean("BLI_IS_PRICE"), 
					resultSet.getString("BLI_ID_PRICE"),
					resultSet.getString("BLI_ITEM_DESC"), 
					resultSet.getDouble("BLI_ITEM_AMOUNT"),
					resultSet.getDouble("BLI_QTY"),	
					null
				);
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				if(bliItem.getItemQuantity() != 0.0) {
					billItems.add(bliItem);
				}
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	public ArrayList<BillItems> getItemsBy(boolean autoCommit, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(3);
		String queryString = "SELECT BLI_ITEM_ID, BLI_ITEM_GROUP, BLI_ID, BLI_ID_BILL, BLI_ITEM_DESC, BLI_IS_PRICE, BLI_ITEM_AMOUNT, SUM(BLI_QTY) AS BLI_QTY, BLI_ID_PRICE, BLI_EXPORT_STATUS FROM BILLITEMS";
		StringBuilder query = new StringBuilder(queryString);
		query.append(" WHERE DATE(BLI_DATE) BETWEEN ? AND ?");
		query.append(" GROUP BY BLI_ITEM_DESC");
		query.append(" ORDER BY BLI_ID_BILL ASC");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>();
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(
					resultSet.getInt("BLI_ID"), 
					resultSet.getInt("BLI_ID_BILL"),
					resultSet.getBoolean("BLI_IS_PRICE"), 
					resultSet.getString("BLI_ID_PRICE"),
					resultSet.getString("BLI_ITEM_DESC"), 
					resultSet.getDouble("BLI_ITEM_AMOUNT"),
					resultSet.getDouble("BLI_QTY"),	
					null
				);
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				bliItem.setExport_status(resultSet.getString("BLI_EXPORT_STATUS"));
				if(bliItem.getItemQuantity() != 0.0) {
					billItems.add(bliItem);
				}
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	/**
	 * Retrieves all the {@link BillItems} grouped to avoid duplicates, 
	 * associated to the passed {@link Bill} id.
	 * 
	 * @author Silevester D.
	 * @since 18/05/2022
	 * 
	 * @param billID the bill id.
	 * @return a list of {@link BillItems} or <code>null</code> if an error occurred.
	 * @throws OHException if an error occurs retrieving the bill items.
	 */
	public ArrayList<BillItems> getGroupItems(int billID, boolean autoCommit) throws OHException {
		ArrayList<BillItems> billItems = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT bli.*, sum(bli.BLI_QTY) as ITEM_QTY FROM BILLITEMS bli");
		if (billID != 0) {
			query.append(" WHERE BLI_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" GROUP BY bli.BLI_ID_PRICE ORDER BY BLI_ID ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, autoCommit);
			billItems = new ArrayList<BillItems>(resultSet.getFetchSize());
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(
					resultSet.getInt("BLI_ID"), 
					resultSet.getInt("BLI_ID_BILL"),
					resultSet.getBoolean("BLI_IS_PRICE"), 
					resultSet.getString("BLI_ID_PRICE"),
					resultSet.getString("BLI_ITEM_DESC"), 
					resultSet.getDouble("BLI_ITEM_AMOUNT"),
					resultSet.getDouble("ITEM_QTY"),
					null
				);
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				bliItem.setPrescriptionId(resultSet.getInt("BLI_PRESC_ID"));
				bliItem.setItemAmountBrut(resultSet.getInt("BLI_ITEM_AMOUNT_BRUT"));
				bliItem.setExport_status(resultSet.getString("BLI_EXPORT_STATUS"));
				billItems.add(bliItem);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			if (autoCommit)
				dbQuery.releaseConnection();
		}
		return billItems;
	}
	
	public ArrayList<BillItems> getDistictsBillItems() throws OHException {
		ArrayList<BillItems> billItems = null;
		String query1 = "SELECT BLI_ITEM_ID, BLI_ITEM_GROUP, BLI_ID, BLI_ID_BILL, BLI_ITEM_DESC,BLI_IS_PRICE,BLI_ITEM_AMOUNT,BLI_QTY,BLI_ID_PRICE, BLI_DATE FROM BILLITEMS GROUP BY BLI_ITEM_DESC";
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		@SuppressWarnings("unused")
		int size = 0;
		try {
			
			ResultSet resultSet = dbQuery.getData(query1,true);
			billItems = new ArrayList<BillItems>(resultSet.getFetchSize());
			while (resultSet.next()) {
				BillItems bliItem = new BillItems(
						resultSet.getInt("BLI_ID"), 
						resultSet.getInt("BLI_ID_BILL"),
						resultSet.getBoolean("BLI_IS_PRICE"), 
						resultSet.getString("BLI_ID_PRICE"),
						resultSet.getString("BLI_ITEM_DESC"), 
						resultSet.getDouble("BLI_ITEM_AMOUNT"),
						resultSet.getDouble("BLI_QTY"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLI_DATE")));
				bliItem.setItemId(resultSet.getString("BLI_ITEM_ID"));
				bliItem.setItemGroup(resultSet.getString("BLI_ITEM_GROUP"));
				billItems.add(bliItem);
				size++;
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return billItems;
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * 
	 * @param dateFrom
	 *            low endpoint, inclusive, for the date range.
	 * @param dateTo
	 *            high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range.
	 * @throws OHException
	 *             if an error occurs retrieving the bill payments.
	 */
	public ArrayList<BillPayments> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo)
			throws OHException {
		ArrayList<BillPayments> payments = null;
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS");
		query.append(" WHERE DATE(BLP_DATE) BETWEEN ? AND ?");
		query.append(" ORDER BY BLP_ID_BILL, BLP_DATE ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			payments = new ArrayList<BillPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillPayments(resultSet.getInt("BLP_ID"), resultSet.getInt("BLP_ID_BILL"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLP_DATE")),
						resultSet.getDouble("BLP_AMOUNT"), resultSet.getString("BLP_USR_ID_A")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return payments;
	}
	public ArrayList<BillPayments> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient)
			throws OHException {
		ArrayList<BillPayments> payments = null;
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS BLP INNER JOIN BILLS BLL ON BLP.BLP_ID_BILL= BLL.BLL_ID ");
		query.append(" WHERE DATE(BLP.BLP_DATE) BETWEEN ? AND ? ");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			if(patient!=null){
				query.append(" AND (BLL.BLL_PAT_AFFILIATED_PERSON=? OR BLL.BLL_ID_PAT=? ) ");
				parameters.add(patient.getCode());
				parameters.add(patient.getCode());
			}
			query.append(" ORDER BY BLP_ID_BILL, BLP_DATE ASC ");
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			payments = new ArrayList<BillPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillPayments(resultSet.getInt("BLP_ID"), resultSet.getInt("BLP_ID_BILL"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLP_DATE")),
						resultSet.getDouble("BLP_AMOUNT"), resultSet.getString("BLP_USR_ID_A")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return payments;
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified {@link Bill} id,
	 * or all the stored {@link BillPayments} if no id is indicated.
	 * 
	 * @param billID
	 *            the bill id or <code>0</code>.
	 * @return the list of bill payments.
	 * @throws OHException
	 *             if an error occurs retrieving the bill payments.
	 */
	public ArrayList<BillPayments> getPayments(int billID) throws OHException {
		ArrayList<BillPayments> payments = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS");
		if (billID != 0) {
			query.append(" WHERE BLP_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" ORDER BY BLP_ID_BILL, BLP_DATE ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			payments = new ArrayList<BillPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillPayments(resultSet.getInt("BLP_ID"), resultSet.getInt("BLP_ID_BILL"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLP_DATE")),
						resultSet.getDouble("BLP_AMOUNT"), resultSet.getString("BLP_USR_ID_A")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return payments;
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
	 * Stores a new {@link Bill}.
	 * 
	 * @param newBill
	 *            the bill to store.
	 * @param autoCommit
	 * @return the generated {@link Bill} id.
	 * @throws OHException
	 *             if an error occurs storing the bill.
	 */
	public int newBill(Bill newBill) throws OHException {
		int billID;
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			String query = "INSERT INTO BILLS ("
					+ "BLL_PARENT_ID ,BLL_DATE, BLL_UPDATE, BLL_IS_LST, BLL_ID_LST, BLL_LST_NAME, BLL_IS_PAT, BLL_ID_PAT, BLL_PAT_NAME, BLL_STATUS, BLL_AMOUNT, BLL_BALANCE, BLL_USR_ID_A, BLL_WARD, BLL_RP_ID,BLL_PAT_AFFILIATED_PERSON,"
					+ "BLL_CREATE_BY, BLL_CREATE_DATE, BLL_GARANTE) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			List<Object> parameters = new ArrayList<Object>(19);
			parameters.add((newBill.getParentId() > 0 ? newBill.getParentId(): null));
			parameters.add(new java.sql.Timestamp(newBill.getDate().getTime().getTime()));
			parameters.add(new java.sql.Timestamp(newBill.getUpdate().getTime().getTime()));
			parameters.add(newBill.isList());
			parameters.add(newBill.getListID());
			parameters.add(newBill.getListName());
			parameters.add(newBill.isPatient());
			parameters.add(newBill.getPatID());
			parameters.add(newBill.getPatName());
			parameters.add(newBill.getStatus());
			parameters.add(newBill.getAmount());
			parameters.add(newBill.getBalance());
			parameters.add(newBill.getUser());
			parameters.add(newBill.getWardCode());
			parameters.add(newBill.getReductionPlan());
			parameters.add(newBill.getAffiliatedParent());
			
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(newBill.getGarante());
			
			ResultSet result = dbQuery.setDataReturnGeneratedKeyWithParams(query, parameters, true);
			if (result.next())
				billID = result.getInt(1);
			else
				return 0;
			return billID;
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 * 
	 * @param billID
	 *            the bill id.
	 * @param billItems
	 *            the bill items to store.
	 * @return <code>true</code> if the {@link BillItems} have been store,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the store operation.
	 */
	public boolean newBillItems(int billID, ArrayList<BillItems> billItems, GregorianCalendar itemDate) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		// ArrayList<BillItems> medicalItems = new ArrayList<BillItems>();
		try {
			// With this INSERT and UPDATE processes are equals
			String query = "DELETE FROM BILLITEMS WHERE BLI_ID_BILL = ?";
			List<Object> parameters = Collections.<Object>singletonList(billID);
			//dbQuery.setDataWithParams(query, parameters, false);

			query = "INSERT INTO BILLITEMS ("
					+ " BLI_ID_BILL, BLI_IS_PRICE, BLI_ID_PRICE, BLI_ITEM_DESC, BLI_ITEM_AMOUNT, BLI_QTY, BLI_ITEM_ID, "
					+ " BLI_ITEM_GROUP, BLI_PRESC_ID, BLI_ITEM_AMOUNT_BRUT,BLI_DATE, BLI_EXPORT_STATUS) "
					+ " VALUES (?,?,?,?,?,?,?,?, ?,?,?,?)";

			billItems = this.getNewItems(billID, billItems);
			
			for (BillItems item : billItems) {				
				parameters = new ArrayList<Object>(6);
				parameters.add(billID);
				parameters.add(item.isPrice());
				parameters.add(item.getPriceID());
				parameters.add(item.getItemDescription());
				parameters.add(item.getItemAmount());
				parameters.add(item.getItemQuantity());
				parameters.add(item.getItemId());
				parameters.add(item.getItemGroup());
				parameters.add(item.getPrescriptionId());
				parameters.add(item.getItemAmountBrut());
				parameters.add(new java.sql.Timestamp(itemDate.getTime().getTime()));
				if(BillItemStatus.Status.EXPORTED.getCode().equalsIgnoreCase(item.getExport_status())){
					parameters.add(item.getExport_status());
				}
				else{
					parameters.add(BillItemStatus.Status.NOTEXPORTED.getCode());
				}
				result = result && dbQuery.setDataWithParams(query, parameters, false);
			}
			if (result) {
				dbQuery.commit();
			}

		} finally {

			dbQuery.releaseConnection();
		}
		return result;
	}
	
	@SuppressWarnings("null")
	private ArrayList<BillItems> getNewItems(int billID, ArrayList<BillItems> newListItems) throws OHException {

		List<BillItems> oldListItems = this.getItemsBy(billID, false);

		if(oldListItems == null) {
			for(int i=0; i<oldListItems.size(); i++) {
				if(oldListItems.get(i).getItemQuantity() == 0.0) {
					oldListItems.remove(i);
				}
			}
		}
		
		if (oldListItems == null || oldListItems.size() == 0) {
			return newListItems;
		}

		ArrayList<BillItems> newList = new ArrayList<BillItems>();
		boolean found = false;
		boolean updated = false;
		int i = 0;
		BillItems newItem = null;
		for (BillItems oldItem : oldListItems) {
			BillItems updatedItem = null;
			found = false;
			updated = false;
			i=0;
			for (i = 0; i<newListItems.size(); i++) {
				newItem = newListItems.get(i);
				if (newItem.getItemId().equals(oldItem.getItemId())) {
					found = true;
					if (oldItem.getItemQuantity() != newItem.getItemQuantity()) {
						double diff = newItem.getItemQuantity() - oldItem.getItemQuantity();
						try {
							updatedItem = oldItem.clone();
							updatedItem.setItemQuantity(diff);
							updated = true;
						} catch (CloneNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							updatedItem = null;
						}
					}
					newListItems.remove(i);
					break;
				}
			}
			if (!found) {
				oldItem.setItemQuantity(-oldItem.getItemQuantity());
				newList.add(oldItem);
			} else if (updated) {	
				newList.add(updatedItem);
			}
		}
		for(BillItems item: newListItems) {
			newList.add(item);
		}
		return newList;
	}

	/**
	 * Stores a list of {@link BillPayments} associated to a {@link Bill}.
	 * 
	 * @param billID
	 *            the bill id.
	 * @param payItems
	 *            the bill payments.
	 * @return <code>true</code> if the payment have stored, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs during the store procedure.
	 */
	public boolean newBillPayments(int billID, ArrayList<BillPayments> payItems) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try {

			String query = "INSERT INTO BILLPAYMENTS (" + "BLP_ID_BILL, BLP_DATE, BLP_AMOUNT, BLP_USR_ID_A,"
					+ "BLP_CREATE_BY, BLP_CREATE_DATE) "
					+ "VALUES (?,?,?,?,?,?)";

			for (BillPayments item : payItems) {
				if (item.getId() <= 0) {
					List<Object> parameters = new ArrayList<Object>(6);
					parameters.add(billID);
					parameters.add(new java.sql.Timestamp(item.getDate().getTime().getTime()));
					parameters.add(item.getAmount());
					parameters.add(item.getUser());
					parameters.add(MainMenu.getUser());
					parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));										
					result = result && dbQuery.setDataWithParams(query, parameters, false);
				}
			}

			if (result) {
				dbQuery.commit();
			}
		} finally {

			dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Updates the specified {@link Bill}.
	 * 
	 * @param updateBill
	 *            the bill to update.
	 * @return <code>true</code> if the bill has been updated,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs during the update.
	 * @throws SQLException 
	 */
	public boolean updateBill(Bill updateBill) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			String query = "UPDATE BILLS SET " + "BLL_DATE = ?, " + "BLL_UPDATE = ?, " + "BLL_IS_LST = ?, "
					+ "BLL_ID_LST = ?, " + "BLL_LST_NAME = ?, " + "BLL_IS_PAT = ?, " + "BLL_ID_PAT = ?, "
					+ "BLL_PAT_NAME = ?, " + "BLL_STATUS = ?, " + "BLL_AMOUNT = ?, " + "BLL_BALANCE = ?, "
					+ "BLL_USR_ID_A = ?, " + "BLL_WARD = ?, " + "BLL_RP_ID = ?, "
					+ "BLL_PAT_AFFILIATED_PERSON = ?, BLL_IS_CLOSED_MANUALLY = ?, BLL_MODIFY_BY = ?, BLL_MODIFY_DATE = ?, BLL_GARANTE= ? " + "WHERE BLL_ID = ?";

			List<Object> parameters = new ArrayList<Object>(12);

			parameters.add(new java.sql.Timestamp(updateBill.getDate().getTimeInMillis()));
			parameters.add(new java.sql.Timestamp(updateBill.getUpdate().getTimeInMillis()));
			parameters.add(updateBill.isList());
			parameters.add(updateBill.getListID());
			parameters.add(updateBill.getListName());
			parameters.add(updateBill.isPatient());
			parameters.add(updateBill.getPatID());
			parameters.add(updateBill.getPatName());
			parameters.add(updateBill.getStatus());
			parameters.add(updateBill.getAmount());
			parameters.add(updateBill.getBalance());
			parameters.add(updateBill.getUser());
			parameters.add(updateBill.getWardCode());
			parameters.add(updateBill.getReductionPlan());
			parameters.add(updateBill.getAffiliatedParent());
			parameters.add(updateBill.isClosedManually());
			parameters.add(MainMenu.getUser());
			parameters.add(new java.sql.Timestamp(TimeTools.getServerDateTime().getTime().getTime()));
			parameters.add(updateBill.getGarante());	
			parameters.add(updateBill.getId());
			dbQuery.setDataWithParams(query, parameters, true);

			return true;

		} finally {

			dbQuery.releaseConnection();
		}
	}
	
	public boolean updateBillItemsExportStatus(BillItems updateBillItem) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			String query = "UPDATE billitems SET BLI_EXPORT_STATUS = ? WHERE BLI_ID	 = ?";
			List<Object> parameters = new ArrayList<Object>(12);			
			parameters.add(updateBillItem.getExport_status());
			parameters.add(updateBillItem.getId());
			dbQuery.setDataWithParams(query, parameters, true);
			return true;
		} finally {
			dbQuery.releaseConnection();
		}
	}

	/**
	 * Deletes the specified {@link Bill}.
	 * 
	 * @param deleteBill
	 *            the bill to delete.
	 * @return <code>true</code> if the bill has been deleted,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 *             if an error occurs deleting the bill.
	 */
	public boolean deleteBill(Bill deleteBill) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try {
			List<Object> parameters = Collections.<Object>singletonList(deleteBill.getId());
			String query = "UPDATE BILLS SET BLL_STATUS = 'D' WHERE BLL_ID = ?";
			dbQuery.setDataWithParams(query, parameters, true);
		} finally {
				dbQuery.commit();
				dbQuery.releaseConnection();
		}
		return result;
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * 
	 * @param dateFrom the low date range endpoint, inclusive.
	 * @param dateTo  the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 * @throws OHException if an error occurs retrieving the bill list.
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		ArrayList<Bill> bills = null;
		StringBuilder queryBuilder = new StringBuilder();
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		queryBuilder.append("SELECT b.*, ");
		queryBuilder.append(refundBillQuery);
		queryBuilder.append(" FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL AND DATE(b.BLL_DATE) BETWEEN ? AND ?");
		queryBuilder.append(" ORDER BY b.BLL_ID DESC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));

			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}

	/**
	 * Gets {@link Bill}s filtered by date and specific patient
	 * 
	 * @param dateFrom the min date
	 * @param dateTo the max date
	 * @param patient The specific patient
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient)
			throws OHException {
		ArrayList<Bill> bills = null;
		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder queryBuilder = new StringBuilder();
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		queryBuilder.append("SELECT b.*, ");
		queryBuilder.append(refundBillQuery);
		queryBuilder.append(" FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL AND DATE(b.BLL_DATE) BETWEEN ? AND ?");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		
		if (patient != null) {
			queryBuilder.append(" AND ( BLL_ID_PAT = ? OR BLL_PAT_AFFILIATED_PERSON = ? ) ");
			parameters.add(patient.getCode());
			parameters.add(patient.getCode());
		}
		
		queryBuilder.append(" ORDER BY b.BLL_ID DESC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());

			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}
	
	/**
	 * Get bills filtered by date, guaranteer user, status, patient, bill item
	 * 
	 * @param dateFrom the min date
	 * @param dateTo the max date
	 * @param userGarant The guaranteer user to be considered
	 * @param billItem The {@link BillItems} to be considered
	 * @param patient The {@link Patient} to be considered
	 * @param limit The pagination limit
	 * @param offset The pagination offset
	 * 
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 * 
	 * @since 20/09/2022
	 * @author Silevester D.
	 */
	public ArrayList<Bill> getBills(
			String status, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo, 
			User userGarant,
			BillItems billItem, 
			Patient patient,
			int limit,
			int offset
	) throws OHException 
	{
		ArrayList<Bill> bills = null;
		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder queryBuilder = new StringBuilder();
		
		queryBuilder.append("SELECT b.*, ");
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		queryBuilder.append(refundBillQuery);			
		
		queryBuilder.append(" FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL ");
		
		if (status != null) {
			queryBuilder.append(" AND b.BLL_STATUS = ? ");
			parameters.add(status);
		}
		
		if (dateFrom != null) {
			queryBuilder.append(" AND (DATE(b.BLL_DATE) >= ? OR DATE(b.BLL_UPDATE) >= ?) ");
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		}
		
		if (dateTo != null) {
			queryBuilder.append(" AND (DATE(b.BLL_DATE) <= ? OR DATE(b.BLL_UPDATE) >= ?) ");
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		}
		
		if (userGarant != null) {
			queryBuilder.append(" AND ( BLL_GARANTE = ? ) ");
			parameters.add(userGarant.getUserName());
		}
		
		if (billItem != null) {
			queryBuilder.append(" AND b.BLL_ID IN (SELECT BLI_ID_BILL FROM BILLITEMS WHERE BLI_ITEM_ID = ? AND BLI_ITEM_GROUP = ? ) ");
			parameters.add(billItem.getItemId());
			parameters.add(billItem.getItemGroup());
		}
		
		if (patient != null) {
			queryBuilder.append(" AND ( b.BLL_ID_PAT = ? OR b.BLL_PAT_AFFILIATED_PERSON = ? ) ");
			parameters.add(patient.getCode());
			parameters.add(patient.getCode());
		}
		
		queryBuilder.append(" ORDER BY b.BLL_ID DESC");
		
		if (limit > 0) {
			queryBuilder.append(" LIMIT ? OFFSET ?");
			parameters.add(limit);
			parameters.add(offset);
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());

			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		
		return bills;
	}
	
	/**
	 * Get bills filtered by date, guaranteer user, status, patient, bill item
	 * 
	 * @param dateFrom the start date
	 * @param dateTo the end date
	 * @param userGarant The guaranteer user to be considered
	 * @param billItem The {@link BillItems} to be considered
	 * @param patient The {@link Patient} to be considered
	 * 
	 * @return The number of matched {@link Bill}
	 * @throws OHException
	 * 
	 * @since 20/09/2022
	 * @author Silevester D.
	 */
	public int countBills(
			String status, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo, 
			User userGarant,
			BillItems billItem, 
			Patient patient
	) throws OHException 
	{
		int totalRecord = 0;
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder queryBuilder = new StringBuilder();
		
		queryBuilder.append("SELECT COUNT(*) as count ");
		
		queryBuilder.append(" FROM BILLS b USE INDEX(PRIMARY) WHERE b.BLL_PARENT_ID IS NULL ");
		
		if (status != null) {
			queryBuilder.append(" AND b.BLL_STATUS = ? ");
			parameters.add(status);
		}
		
		if (dateFrom != null) {
			queryBuilder.append(" AND (DATE(b.BLL_DATE) >= ? OR DATE(b.BLL_UPDATE) >= ?) ");
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		}
		
		if (dateTo != null) {
			queryBuilder.append(" AND (DATE(b.BLL_DATE) <= ? OR DATE(b.BLL_UPDATE) >= ?) ");
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		}
		
		if (userGarant != null) {
			queryBuilder.append(" AND ( BLL_GARANTE = ? ) ");
			parameters.add(userGarant.getUserName());
		}
		
		if (billItem != null) {
			queryBuilder.append(" AND b.BLL_ID IN (SELECT BLI_ID_BILL FROM BILLITEMS WHERE BLI_ITEM_ID = ? AND BLI_ITEM_GROUP = ? ) ");
			parameters.add(billItem.getItemId());
			parameters.add(billItem.getItemGroup());
		}
		
		if (patient != null) {
			queryBuilder.append(" AND ( b.BLL_ID_PAT = ? OR b.BLL_PAT_AFFILIATED_PERSON = ? ) ");
			parameters.add(patient.getCode());
			parameters.add(patient.getCode());
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			while (resultSet.next()) {
				totalRecord = resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		
		return totalRecord;
	}
	
	/**
	 * Get Bills Statistics for a period
	 * @param dateFrom Start date
	 * @param dateTo End date
	 * @param user User to be considered
	 * @return {@link HashMap} containing possible keys :
	 * - BALANCE : The total balance for the period
	 * - TOTAL_PAYMENTS : The total payments amount for the period
	 * - USER_PAYMENTS : User payment amount for the period
	 * - TOTAL_REFUNDS : The total amount refunded for the period
	 * - USER_REFUNDS : The total amount refunded by the user for the period
	 * @throws OHException
	 * 
	 * @since 20/09/2022
	 * @author Silevester D.
	 */
	public HashMap<String, Double> getStatsByPeriod(
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo, 
			String user
	)throws OHException 
	{
		HashMap<String, Double> periodStats = new HashMap<String, Double>();
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder queryBuilder = new StringBuilder();
		
		queryBuilder.append("SELECT SUM(b.BLL_BALANCE) as BALANCE, TOTAL_PAYMENTS ");
		
		if (user != null) {
			queryBuilder.append(", USER_PAYMENTS ");
		}
		
		if (Param.bool("ENABLEMEDICALREFUND")) {
			queryBuilder.append(", TOTAL_REFUNDS ");
			if (user != null) {
				queryBuilder.append(", USER_REFUNDS ");
			}
		}

		queryBuilder.append(" FROM BILLS b, ");
		queryBuilder.append(" ( SELECT SUM(IF(bp.BLP_AMOUNT > 0, bp.BLP_AMOUNT, 0)) AS TOTAL_PAYMENTS ");
		if (user != null) {
			queryBuilder.append(", SUM(IF(bp.BLP_AMOUNT > 0 AND bp.BLP_USR_ID_A = ?, bp.BLP_AMOUNT, 0)) AS USER_PAYMENTS  ");
			parameters.add(user);
		}
		
		if (Param.bool("ENABLEMEDICALREFUND")) {
			queryBuilder.append(", SUM(IF(bp.BLP_AMOUNT < 0, ABS(bp.BLP_AMOUNT), 0)) AS TOTAL_REFUNDS ");
			if (user != null) {
				queryBuilder.append(", SUM(IF(bp.BLP_AMOUNT < 0 AND bp.BLP_USR_ID_A = ?, ABS(bp.BLP_AMOUNT), 0)) AS USER_REFUNDS ");
				parameters.add(user);
			}
		}
		
		queryBuilder.append(" FROM BILLPAYMENTS bp WHERE DATE(bp.BLP_DATE) BETWEEN ? AND ?");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		queryBuilder.append(" ) PAYMENTS ");
		
		queryBuilder.append(" WHERE b.BLL_PARENT_ID IS NULL AND DATE(b.BLL_DATE) BETWEEN ? AND ? ");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			while (resultSet.next()) {
				periodStats.put("BALANCE", resultSet.getDouble("BALANCE"));
				periodStats.put("TOTAL_PAYMENTS", resultSet.getDouble("TOTAL_PAYMENTS"));
				
				if (user != null) {
					periodStats.put("USER_PAYMENTS", resultSet.getDouble("USER_PAYMENTS"));					
				}
				
				if (Param.bool("ENABLEMEDICALREFUND")) {
					periodStats.put("TOTAL_REFUNDS", resultSet.getDouble("TOTAL_REFUNDS"));
					if (user != null) {
						periodStats.put("USER_REFUNDS", resultSet.getDouble("USER_REFUNDS"));						
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		
		return periodStats;
	}

	/**
	 * Get bills filtered by date and guaranteer user
	 * 
	 * @param dateFrom the min date
	 * @param dateTo the max date
	 * @param userGarant The guaranteer user
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, User userGarant)
			throws OHException {
		ArrayList<Bill> bills = null;
		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder queryBuilder = new StringBuilder();
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		queryBuilder.append("SELECT b.*, ");
		queryBuilder.append(refundBillQuery);
		queryBuilder.append(" FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL AND DATE(b.BLL_DATE) BETWEEN ? AND ?");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		
		if (userGarant != null) {
			queryBuilder.append(" AND ( BLL_GARANTE = ? ) ");
			parameters.add(userGarant.getUserName());
		}
		
		queryBuilder.append(" ORDER BY b.BLL_ID DESC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());

			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}

	/**
	 * Get bills filtered by date and {@link BillItems}
	 * 
	 * @param dateFrom the min date
	 * @param dateTo the max date
	 * @param billItem The billItems
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, BillItems billItem)
			throws OHException {
		ArrayList<Bill> bills = null;
		List<Object> parameters = new ArrayList<Object>(2);
		StringBuilder queryBuilder = new StringBuilder();
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		queryBuilder.append("SELECT b.*, ");
		queryBuilder.append(refundBillQuery);
		queryBuilder.append(" FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL AND DATE(b.BLL_DATE) BETWEEN ? AND ?");
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		
		if (billItem != null) {
			queryBuilder.append(" AND  BLL_ID  IN (SELECT BLI_ID_BILL FROM BILLITEMS WHERE BLI_ITEM_ID = ? AND BLI_ITEM_GROUP = ? ) ");
			parameters.add(billItem.getItemId());
			parameters.add(billItem.getItemGroup());
		}
		
		queryBuilder.append(" ORDER BY b.BLL_ID DESC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(queryBuilder.toString(), parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());

			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}

	/**
	 * Get all opened bills filtered by specific item
	 * 
	 * @param itemID The item's id
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getPendingBillsSpecificItem(String itemID) throws OHException {
		ArrayList<Bill> pendingBills = null;
		List<Object> parameters = new ArrayList<Object>(1);
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		StringBuilder query = new StringBuilder("SELECT b.*, "); 
		
		query.append(refundBillQuery);
		query.append("FROM BILLS b WHERE b.BLL_STATUS = 'O' AND b.BLL_PARENT_ID IS NULL ");
		
		if (itemID != null && !itemID.equals("")) {
			query.append("  AND b.BLL_ID  IN (SELECT BLI_ID_BILL FROM BILLITEMS WHERE BLI_ITEM_ID = ?)");
			parameters.add(itemID);
		}
		
		query.append(" ORDER BY b.BLL_DATE DESC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}

	/**
	 * Get all opened bills filtered by specific guaranteer user
	 * 
	 * @param garante The guaranteer user
	 * @return {@link ArrayList} of filtered {@link Bill}
	 * @throws OHException
	 */
	public ArrayList<Bill> getPendingBillsSpecificGarante(User garante) throws OHException {
		ArrayList<Bill> pendingBills = null;
		List<Object> parameters = new ArrayList<Object>(1);
		
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";
		
		StringBuilder query = new StringBuilder("SELECT b.*, "); 
		
		query.append(refundBillQuery);
		query.append("FROM BILLS b WHERE b.BLL_STATUS = 'O' AND b.BLL_PARENT_ID IS NULL ");
		
		if (garante != null) {
			query.append("  AND BLL_GARANTE = ? ");
			parameters.add(garante.getUserName());
		}
		
		query.append(" ORDER BY BLL_DATE DESC");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * 
	 * @param payments the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments}.
	 * @throws OHException if an error occurs retrieving the bill list.
	 */
	public ArrayList<Bill> getBills(ArrayList<BillPayments> payments) throws OHException {
		ArrayList<Bill> bills = null;
		List<Object> parameters = new ArrayList<Object>();
		String refundBillQuery = " (SELECT SUM(BLL_AMOUNT) FROM BILLS WHERE BLL_PARENT_ID = b.BLL_ID) AS REFUNDED_AMOUNT ";

		StringBuilder query = new StringBuilder("SELECT b.*, ");
		query.append(refundBillQuery);
		query.append("FROM BILLS b WHERE b.BLL_PARENT_ID IS NULL AND b.BLL_ID IN ( ");
		
		for (int i = 0; i < payments.size(); i++) {
			BillPayments payment = payments.get(i);
			if (i == payments.size() - 1) {
				query.append("?");
				parameters.add(payment.getBillID());
			} else {
				query.append("?, ");
				parameters.add(payment.getBillID());
			}
		}
		
		query.append(")");
		query.append(" ORDER BY b.BLL_ID DESC ");
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				bill.setRefundAmount(resultSet.getDouble("REFUNDED_AMOUNT"));
				bills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return bills;
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed
	 * {@link Bill} list.
	 * 
	 * @param bills
	 *            the bill list.
	 * @return a list of {@link BillPayments} associated to the passed bill
	 *         list.
	 * @throws OHException
	 *             if an error occurs retrieving the payments.
	 */
	public ArrayList<BillPayments> getPayments(ArrayList<Bill> bills) throws OHException {
		ArrayList<BillPayments> payments = null;

		List<Object> parameters = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM BILLPAYMENTS WHERE BLP_ID_BILL IN (''");
		if (bills != null) {
			for (Bill bill : bills) {
				query.append(", ?");
				parameters.add(bill.getId());
			}
		}
		query.append(")");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			payments = new ArrayList<BillPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillPayments(resultSet.getInt("BLP_ID"), resultSet.getInt("BLP_ID_BILL"),
						convertToGregorianCalendar(resultSet.getTimestamp("BLP_DATE")),
						resultSet.getDouble("BLP_AMOUNT"), resultSet.getString("BLP_USR_ID_A")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return payments;
	}

	public Bill toBill(ResultSet resultSet) throws SQLException {
		Bill bill = new Bill(resultSet.getInt("BLL_ID"), convertToGregorianCalendar(resultSet.getTimestamp("BLL_DATE")),
				convertToGregorianCalendar(resultSet.getTimestamp("BLL_UPDATE")), resultSet.getBoolean("BLL_IS_LST"),
				resultSet.getInt("BLL_ID_LST"), resultSet.getString("BLL_LST_NAME"), resultSet.getBoolean("BLL_IS_PAT"),
				resultSet.getInt("BLL_ID_PAT"), resultSet.getString("BLL_PAT_NAME"), resultSet.getString("BLL_STATUS"),
				resultSet.getDouble("BLL_AMOUNT"), resultSet.getDouble("BLL_BALANCE"),
				resultSet.getString("BLL_USR_ID_A"), resultSet.getString("BLL_WARD"),resultSet.getInt("BLL_RP_ID"),
				resultSet.getInt("BLL_PAT_AFFILIATED_PERSON"));
		bill.setClosedManually(resultSet.getBoolean("BLL_IS_CLOSED_MANUALLY"));
		bill.setGarante(resultSet.getString("BLL_GARANTE"));
		return bill;
	}

	public HashMap<String, Double> getTotalCountAmountByQuery(String billDesc, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException{
		String query = "SELECT COUNT(bli.BLI_ITEM_DESC) as totCount,  ABS(SUM(bli.BLI_ITEM_AMOUNT * bli.BLI_QTY)) as totAmount FROM BILLITEMS bli JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID WHERE bl.BLL_STATUS = 'C' AND bli.BLI_ITEM_DESC = ? AND DATE(bl.BLL_UPDATE) BETWEEN ? AND ? GROUP BY bli.BLI_ITEM_DESC";
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>(3);
		parameters.add(billDesc);
		parameters.add(new Timestamp(dateFrom.getTime().getTime()));
		parameters.add(new Timestamp(dateTo.getTime().getTime()));
		HashMap<String, Double> result = new HashMap<String, Double>();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			if (resultSet.first()){
				result.put("totalCount", resultSet.getDouble("totCount"));
				result.put("totalAmount", resultSet.getDouble("totAmount"));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return result;
	}
	
	public ArrayList<BillItemReportBean> getTotalCountAmountByQuery12(int year, String status)
			throws OHException, ParseException, SQLException {
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT groupe, description, quantity, refunded_quantity, amount, refunded_amount FROM ( ");
		queryBuilder.append("SELECT	bli.BLI_ITEM_GROUP AS groupe, ");
		queryBuilder.append("bli.BLI_ITEM_DESC AS description, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NULL, bli.BLI_QTY, 0)) AS quantity, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NOT NULL, bli.BLI_QTY, 0)) AS refunded_quantity, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NULL, (bli.BLI_QTY * bli.BLI_ITEM_AMOUNT), 0)) AS amount, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NOT NULL, (bli.BLI_QTY * bli.BLI_ITEM_AMOUNT), 0)) AS refunded_amount ");
		queryBuilder.append("FROM BILLITEMS bli ");
		queryBuilder.append("JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID ");
		queryBuilder.append("WHERE bl.BLL_STATUS ");
		if (status.equals("D")) {
			queryBuilder.append("!= '" + status + "' ");
		} else {
			queryBuilder.append("= '" + status + "' ");
		}
		queryBuilder.append("AND DATE(bl.BLL_DATE) BETWEEN ? AND ? GROUP BY description ");
		queryBuilder.append(") source_table ORDER BY groupe, description");
		
		String query = queryBuilder.toString();

		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>(3);

		String endFebruary;
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		endFebruary = cal.isLeapYear(year) ? "29/02/" + year : "28/02/" + year;
		String[] dates = { "01/01/" + year, "31/01/" + year, "01/02/" + year, endFebruary, "01/03/" + year,
				"31/03/" + year, "01/04/" + year, "30/04/" + year, "01/05/" + year, "31/05/" + year, "01/06/" + year,
				"30/06/" + year, "01/07/" + year, "31/07/" + year, "01/08/" + year, "31/08/" + year, "01/09/" + year,
				"30/09/" + year, "01/10/" + year, "31/10/" + year, "01/11/" + year, "30/11/" + year, "01/12/" + year,
				"31/12/" + year };
		Date date = null;
		DateFormat dateFromat = new SimpleDateFormat("dd/MM/yyyy");
		GregorianCalendar gregoDate = null;
		HashMap<Integer, GregorianCalendar> datesG = new HashMap<Integer, GregorianCalendar>();
		for (int i = 0; i < dates.length; i++) {
			date = new Date();
			date = dateFromat.parse(dates[i]);
			gregoDate = new GregorianCalendar();
			gregoDate.setTime(date);
			datesG.put(i, gregoDate);
		}
		ArrayList<BillItemReportBean> returnList = new ArrayList<BillItemReportBean>();
		HashMap<String, BillItemReportBean> items = new HashMap<String, BillItemReportBean>();
		try {
			for (int i = 0; i < 13; i++) {
				if (i == 12) {
					parameters.add(new Timestamp(datesG.get(0).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(23).getTime().getTime()));
				} else {
					parameters.add(new Timestamp(datesG.get(i * 2).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(i * 2 + 1).getTime().getTime()));
				}
				ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
				String desc = "";
				String group = "";
				BillItemReportBean currentItem = null;
				while (resultSet.next()) {
					desc = resultSet.getString("description");
					group = resultSet.getString("groupe") == null ? "" : resultSet.getString("groupe");
					if (items.containsKey(desc)) {
						currentItem = items.get(desc);
						switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JANUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JANUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_FEBRUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_FEBRUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MARCH(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MARCH(resultSet.getDouble("refunded_amount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_APRIL(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_APRIL(resultSet.getDouble("refunded_amount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MAY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MAY(resultSet.getDouble("refunded_amount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JUNE(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JUNE(resultSet.getDouble("refunded_amount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JULY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JULY(resultSet.getDouble("refunded_amount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_AUGUST(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_AUGUST(resultSet.getDouble("refunded_amount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_SEPTEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_SEPTEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_OCTOBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_OCTOBER(resultSet.getDouble("refunded_amount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_NOVEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_NOVEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_DECEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_DECEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_TOTAL_YEAR_COUNT(resultSet.getDouble("refunded_quantity"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("amount"));
							currentItem.setREFUNDED_TOTAL_YEAR_AMOUNT(resultSet.getDouble("refunded_amount"));
							break;
						default:
							break;
						}

					} else {
						currentItem = new BillItemReportBean();
						currentItem.setBLI_ITEM_DESC(desc);
						currentItem.setBLI_ITEM_GROUP(group);
						items.put(desc, currentItem);
						switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JANUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JANUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_FEBRUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_FEBRUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MARCH(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MARCH(resultSet.getDouble("refunded_amount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_APRIL(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_APRIL(resultSet.getDouble("refunded_amount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MAY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MAY(resultSet.getDouble("refunded_amount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JUNE(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JUNE(resultSet.getDouble("refunded_amount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JULY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JULY(resultSet.getDouble("refunded_amount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_AUGUST(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_AUGUST(resultSet.getDouble("refunded_amount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_SEPTEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_SEPTEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_OCTOBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_OCTOBER(resultSet.getDouble("refunded_amount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_NOVEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_NOVEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_DECEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_DECEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_TOTAL_YEAR_COUNT(resultSet.getDouble("refunded_quantity"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("amount"));
							currentItem.setREFUNDED_TOTAL_YEAR_AMOUNT(resultSet.getDouble("refunded_amount"));
							break;
						default:
							break;
						}
						returnList.add(currentItem);
					}
				}
				parameters.clear();
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return returnList;
	}
	
	public ArrayList<BillItemReportBean> getTotalCountAmountByQuery12(int year, String status, OperationType operationType)
			throws OHException, ParseException, SQLException {
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT groupe, description, quantity, refunded_quantity, amount, refunded_amount FROM ( ");
		queryBuilder.append("SELECT	bli.BLI_ITEM_GROUP AS groupe, ");
		queryBuilder.append("bli.BLI_ITEM_DESC AS description, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NULL, bli.BLI_QTY, 0)) AS quantity, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NOT NULL, bli.BLI_QTY, 0)) AS refunded_quantity, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NULL, (bli.BLI_QTY * bli.BLI_ITEM_AMOUNT), 0)) AS amount, ");
		queryBuilder.append("SUM(IF(bl.BLL_PARENT_ID IS NOT NULL, (bli.BLI_QTY * bli.BLI_ITEM_AMOUNT), 0)) AS refunded_amount ");
		queryBuilder.append("FROM BILLITEMS bli ");
		queryBuilder.append("JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID ");
		queryBuilder.append("JOIN OPERATION op ON op.OPE_ID_A = bli.BLI_ITEM_ID");
		queryBuilder.append("WHERE bl.BLL_STATUS ");
		if (status.equals("D")) {
			queryBuilder.append("!= '" + status + "' ");
		} else {
			queryBuilder.append("= '" + status + "' ");
		}
		queryBuilder.append("AND op.OPE_OCL_ID_A = '" + operationType.getCode() + "' ");
		queryBuilder.append("AND DATE(bl.BLL_DATE) BETWEEN ? AND ? GROUP BY description ");
		queryBuilder.append(") source_table ORDER BY groupe, description");
		
		String query = queryBuilder.toString();

		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>(3);

		String endFebruary;
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		endFebruary = cal.isLeapYear(year) ? "29/02/" + year : "28/02/" + year;
		String[] dates = { "01/01/" + year, "31/01/" + year, "01/02/" + year, endFebruary, "01/03/" + year,
				"31/03/" + year, "01/04/" + year, "30/04/" + year, "01/05/" + year, "31/05/" + year, "01/06/" + year,
				"30/06/" + year, "01/07/" + year, "31/07/" + year, "01/08/" + year, "31/08/" + year, "01/09/" + year,
				"30/09/" + year, "01/10/" + year, "31/10/" + year, "01/11/" + year, "30/11/" + year, "01/12/" + year,
				"31/12/" + year };
		Date date = null;
		DateFormat dateFromat = new SimpleDateFormat("dd/MM/yyyy");
		GregorianCalendar gregoDate = null;
		HashMap<Integer, GregorianCalendar> datesG = new HashMap<Integer, GregorianCalendar>();
		for (int i = 0; i < dates.length; i++) {
			date = new Date();
			date = dateFromat.parse(dates[i]);
			gregoDate = new GregorianCalendar();
			gregoDate.setTime(date);
			datesG.put(i, gregoDate);
		}
		ArrayList<BillItemReportBean> returnList = new ArrayList<BillItemReportBean>();
		HashMap<String, BillItemReportBean> items = new HashMap<String, BillItemReportBean>();
		try {
			for (int i = 0; i < 13; i++) {
				if (i == 12) {
					parameters.add(new Timestamp(datesG.get(0).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(23).getTime().getTime()));
				} else {
					parameters.add(new Timestamp(datesG.get(i * 2).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(i * 2 + 1).getTime().getTime()));
				}
				ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
				String desc = "";
				String group = "";
				BillItemReportBean currentItem = null;
				while (resultSet.next()) {
					desc = resultSet.getString("description");
					group = resultSet.getString("groupe") == null ? "" : resultSet.getString("groupe");
					if (items.containsKey(desc)) {
						currentItem = items.get(desc);
						switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JANUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JANUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_FEBRUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_FEBRUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MARCH(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MARCH(resultSet.getDouble("refunded_amount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_APRIL(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_APRIL(resultSet.getDouble("refunded_amount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MAY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MAY(resultSet.getDouble("refunded_amount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JUNE(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JUNE(resultSet.getDouble("refunded_amount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JULY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JULY(resultSet.getDouble("refunded_amount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_AUGUST(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_AUGUST(resultSet.getDouble("refunded_amount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_SEPTEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_SEPTEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_OCTOBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_OCTOBER(resultSet.getDouble("refunded_amount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_NOVEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_NOVEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_DECEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_DECEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_TOTAL_YEAR_COUNT(resultSet.getDouble("refunded_quantity"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("amount"));
							currentItem.setREFUNDED_TOTAL_YEAR_AMOUNT(resultSet.getDouble("refunded_amount"));
							break;
						default:
							break;
						}

					} else {
						currentItem = new BillItemReportBean();
						currentItem.setBLI_ITEM_DESC(desc);
						currentItem.setBLI_ITEM_GROUP(group);
						items.put(desc, currentItem);
						switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JANUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JANUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_FEBRUARY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_FEBRUARY(resultSet.getDouble("refunded_amount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MARCH(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MARCH(resultSet.getDouble("refunded_amount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_APRIL(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_APRIL(resultSet.getDouble("refunded_amount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_MAY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_MAY(resultSet.getDouble("refunded_amount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JUNE(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JUNE(resultSet.getDouble("refunded_amount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_JULY(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_JULY(resultSet.getDouble("refunded_amount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_AUGUST(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_AUGUST(resultSet.getDouble("refunded_amount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_SEPTEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_SEPTEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_OCTOBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_OCTOBER(resultSet.getDouble("refunded_amount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_NOVEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_NOVEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_COUNT_DECEMBER(resultSet.getDouble("refunded_quantity"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("amount")); 
							currentItem.setREFUNDED_AMOUNT_DECEMBER(resultSet.getDouble("refunded_amount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("quantity"));
							currentItem.setREFUNDED_TOTAL_YEAR_COUNT(resultSet.getDouble("refunded_quantity"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("amount"));
							currentItem.setREFUNDED_TOTAL_YEAR_AMOUNT(resultSet.getDouble("refunded_amount"));
							break;
						default:
							break;
						}
						returnList.add(currentItem);
					}
				}
				parameters.clear();
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return returnList;
	}
	
}
