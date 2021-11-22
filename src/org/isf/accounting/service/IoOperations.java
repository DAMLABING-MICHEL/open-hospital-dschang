package org.isf.accounting.service;

import java.awt.Menu;
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
import org.isf.accounting.model.BillItemPayments;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.model.User;
import org.isf.opetype.model.OperationType;
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
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");///////////////////
		query.append(" WHERE BLL_STATUS = 'O'");
		if (patID != 0) {
			query.append(" AND BLL_ID_PAT = ?");
			parameters.add(patID);
			query.append(" ORDER BY BLL_DATE DESC");
		} 
		else if(patID == 0){
			query.append(" ORDER BY BLL_DATE DESC LIMIT 80");			
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}
	public ArrayList<Bill> getPendingBills2(int patID) throws OHException {
		ArrayList<Bill> pendingBills = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");///////////////////
		query.append(" WHERE BLL_STATUS = 'O'");
		if (patID != 0) {
			query.append(" AND BLL_ID_PAT = ?");
			parameters.add(patID);
			query.append(" ORDER BY BLL_DATE DESC");
		} 
		else if(patID == 0){
			query.append(" ORDER BY BLL_DATE DESC LIMIT 80");			
		}

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;
	}
	
	public ArrayList<Bill> getPendingBillsAffiliate(int patID) throws OHException {
		ArrayList<Bill> pendingBills = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");
		query.append(" WHERE BLL_STATUS = 'O'");
		if (patID != 0) {
			query.append("  AND ( BLL_ID_PAT=? OR BLL_PAT_AFFILIATED_PERSON=? )");
			parameters.add(patID);
			parameters.add(patID);
		}
		query.append(" ORDER BY BLL_DATE DESC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
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
	 * Get all the {@link Bill}s.
	 * 
	 * @return a list of bills.
	 * @throws OHException
	 *             if an error occurs retrieving the bills.
	 */
	public ArrayList<Bill> getBills() throws OHException {
		ArrayList<Bill> bills = null;
		String query = "SELECT * FROM BILLS ORDER BY BLL_DATE DESC";
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
		String query = "SELECT * FROM BILLS WHERE BLL_ID = ?";
		List<Object> parameters = Collections.<Object>singletonList(billID);
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
			while (resultSet.next()) {
				bill = toBill(resultSet);
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
						resultSet.getDouble("BLI_QTY"));
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
		String query1 = "SELECT BLI_ITEM_ID, BLI_ITEM_GROUP, BLI_ID, BLI_ID_BILL, BLI_ITEM_DESC,BLI_IS_PRICE,BLI_ITEM_AMOUNT,BLI_QTY,BLI_ID_PRICE FROM BILLITEMS GROUP BY BLI_ITEM_DESC";
		
		DbQueryLogger dbQuery = new DbQueryLogger();
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
						resultSet.getDouble("BLI_QTY"));
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
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * 
	 * @param dateFrom
	 *            low endpoint, inclusive, for the date range.
	 * @param dateTo
	 *            high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillItemPayments} for the specified date range.
	 * @throws OHException
	 *             if an error occurs retrieving the bill payments.
	 */
	public ArrayList<BillItemPayments> getItemPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo)
			throws OHException {
		ArrayList<BillItemPayments> payments = null;
		StringBuilder query = new StringBuilder("SELECT * FROM BILLITEMPAYMENTS");
		query.append(" WHERE BIP_DATE BETWEEN ? AND ?");
		query.append(" ORDER BY BIP_ID_BILL, BIP_DATE ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			payments = new ArrayList<BillItemPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillItemPayments(resultSet.getInt("BIP_ID"), resultSet.getInt("BIP_ID_BILL"),
						resultSet.getInt("BIP_ID_BILL_ITEM"),
						convertToGregorianCalendar(resultSet.getTimestamp("BIP_DATE")),
						resultSet.getDouble("BIP_AMOUNT"), resultSet.getString("BIP_USR_ID_A")));
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return payments;
	}
	public ArrayList<BillItemPayments> getItemPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient)
			throws OHException {
		ArrayList<BillItemPayments> payments = null;
		StringBuilder query = new StringBuilder("SELECT * FROM BILLITEMPAYMENTS BIP INNER JOIN BILLS BLL ON BIP.BIP_ID_BILL= BLL.BLL_ID ");
		query.append(" WHERE BIP.BIP_DATE BETWEEN ? AND ? ");
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
			query.append(" ORDER BY BLP_ID_BILL, BIP_DATE ASC ");
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);

			payments = new ArrayList<BillItemPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillItemPayments(resultSet.getInt("BIP_ID"), resultSet.getInt("BIP_ID_BILL"),
						resultSet.getInt("BIP_ID_BILL_ITEM"),
						convertToGregorianCalendar(resultSet.getTimestamp("BIP_DATE")),
						resultSet.getDouble("BIP_AMOUNT"), resultSet.getString("BIP_USR_ID_A")));
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
	 * Retrieves all the {@link BillItemPayments} for the specified {@link Bill} id,
	 * or all the stored {@link BillItemPayments} if no id is indicated.
	 * 
	 * @param billID
	 *            the bill id or <code>0</code>.
	 * @return the list of bill item payments.
	 * @throws OHException
	 *             if an error occurs retrieving the bill item payments.
	 */
	public ArrayList<BillItemPayments> getItemPayments(int billID) throws OHException {
		ArrayList<BillItemPayments> payments = null;

		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLITEMPAYMENTS");
		if (billID != 0) {
			query.append(" WHERE BIP_ID_BILL = ?");
			parameters.add(billID);
		}
		query.append(" ORDER BY BIP_ID_BILL, BIP_DATE ASC");

		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			payments = new ArrayList<BillItemPayments>(resultSet.getFetchSize());
			while (resultSet.next()) {
				payments.add(new BillItemPayments(resultSet.getInt("BIP_ID"), resultSet.getInt("BIP_ID_BILL"),
						resultSet.getInt("BIP_ID_BILL_ITEM"),
						convertToGregorianCalendar(resultSet.getTimestamp("BIP_DATE")),
						resultSet.getDouble("BIP_AMOUNT"), resultSet.getString("BIP_USR_ID_A")));
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
					+ "BLL_DATE, BLL_UPDATE, BLL_IS_LST, BLL_ID_LST, BLL_LST_NAME, BLL_IS_PAT, BLL_ID_PAT, BLL_PAT_NAME, BLL_STATUS, BLL_AMOUNT, BLL_BALANCE, BLL_USR_ID_A, BLL_WARD, BLL_RP_ID,BLL_PAT_AFFILIATED_PERSON,"
					+ "BLL_CREATE_BY, BLL_CREATE_DATE, BLL_GARANTE) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			List<Object> parameters = new ArrayList<Object>(11);
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
	public boolean newBillItems(int billID, ArrayList<BillItems> billItems) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		// ArrayList<BillItems> medicalItems = new ArrayList<BillItems>();
		try {
			// With this INSERT and UPDATE processes are equals
			String query = "DELETE FROM BILLITEMS WHERE BLI_ID_BILL = ?";
			List<Object> parameters = Collections.<Object>singletonList(billID);
			dbQuery.setDataWithParams(query, parameters, false);

			query = "INSERT INTO BILLITEMS ("
					+ " BLI_ID_BILL, BLI_IS_PRICE, BLI_ID_PRICE, BLI_ITEM_DESC, BLI_ITEM_AMOUNT, BLI_QTY, BLI_ITEM_ID, "
					+ " BLI_ITEM_GROUP, BLI_PRESC_ID, BLI_ITEM_AMOUNT_BRUT, BLI_EXPORT_STATUS) "
					+ " VALUES (?,?,?,?,?,?,?,?, ?,?,?)";

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
	 * Stores a list of {@link BillItemPayments} associated to a {@link Bill}.
	 * 
	 * @param billID
	 *            the bill id.
	 * @param payItems
	 *            the bill item payments.
	 * @return <code>true</code> if the payment have stored, <code>false</code>
	 *         otherwise.
	 * @throws OHException
	 *             if an error occurs during the store procedure.
	 */
	public boolean newBillItemPayments(int billID, ArrayList<BillItemPayments> payItems) throws OHException {
		DbQueryLogger dbQuery = new DbQueryLogger();
		boolean result = true;
		try {

			String query = "INSERT INTO BILLITEMPAYMENTS (" + "BIP_ID_BILL, BIP_ID_BILL_ITEM, BIP_DATE, BIP_AMOUNT, BIP_USR_ID_A,"
					+ "BIP_CREATE_BY, BIP_CREATE_DATE) "
					+ "VALUES (?,?,?,?,?,?,?)";

			for (BillItemPayments item : payItems) {
				if (item.getId() <= 0) {
					List<Object> parameters = new ArrayList<Object>(7);
					parameters.add(billID);
					parameters.add(item.getItemID());
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
	 * @param dateFrom
	 *            the low date range endpoint, inclusive.
	 * @param dateTo
	 *            the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s.
	 * @throws OHException
	 *             if an error occurs retrieving the bill list.
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo) throws OHException {
		ArrayList<Bill> bills = null;
		String query = "SELECT * FROM BILLS WHERE DATE(BLL_DATE) BETWEEN ? AND ?";
		query = query +" ORDER BY BLL_ID DESC ";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {

			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));

			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
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
	
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) throws OHException {
		ArrayList<Bill> bills = null;
		String query = "SELECT * FROM BILLS  WHERE (DATE(BLL_DATE) BETWEEN ? AND ? ) ";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			if(patient!=null){
				query = query + " AND ( BLL_ID_PAT=? OR BLL_PAT_AFFILIATED_PERSON=? ) ";
				parameters.add(patient.getCode());
				parameters.add(patient.getCode());
			}
			query = query + "ORDER BY BLL_ID DESC";

			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
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
	
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, User userGarant) throws OHException {
		ArrayList<Bill> bills = null;
		String query = "SELECT * FROM BILLS  WHERE (DATE(BLL_DATE) BETWEEN ? AND ? ) ";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			if(userGarant!=null){
				query = query + " AND ( BLL_GARANTE=? ) ";
				parameters.add(userGarant.getUserName());
			}
			query = query + "ORDER BY BLL_ID DESC";
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
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
	
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, BillItems billItem) throws OHException {
		ArrayList<Bill> bills = null;
		String query = "SELECT * FROM BILLS  WHERE (DATE(BLL_DATE) BETWEEN ? AND ? ) ";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			List<Object> parameters = new ArrayList<Object>(2);
			parameters.add(new Timestamp(dateFrom.getTime().getTime()));
			parameters.add(new Timestamp(dateTo.getTime().getTime()));
			if(billItem!=null){
				query = query + " AND  BLL_ID  IN (SELECT BLI_ID_BILL FROM BILLITEMS WHERE BLI_ITEM_ID = ? AND BLI_ITEM_GROUP = ? ) ";
				parameters.add(billItem.getItemId());
				parameters.add(billItem.getItemGroup());
				
			}
			query = query + "ORDER BY BLL_ID DESC";
			ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);

			bills = new ArrayList<Bill>(resultSet.getFetchSize());
			
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
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
	public ArrayList<Bill> getPendingBillsSpecificItem(String itemID) throws OHException {
		ArrayList<Bill> pendingBills = null;
		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");
		query.append(" WHERE BLL_STATUS = 'O'");
		if (itemID != null && !itemID.equals("")) {
			query.append("  AND BLL_ID  IN (SELECT BLI_ID_BILL FROM BILLITEMS WHERE BLI_ITEM_ID = ?)");
			parameters.add(itemID);			
		}
		query.append(" ORDER BY BLL_DATE DESC");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
			pendingBills = new ArrayList<Bill>(resultSet.getFetchSize());
			while (resultSet.next()) {
				Bill bill = toBill(resultSet);
				pendingBills.add(bill);
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		} finally {
			dbQuery.releaseConnection();
		}
		return pendingBills;		
	}
	public ArrayList<Bill> getPendingBillsSpecificGarante(User garante) throws OHException {
		ArrayList<Bill> pendingBills = null;
		List<Object> parameters = new ArrayList<Object>(1);
		StringBuilder query = new StringBuilder("SELECT * FROM BILLS");
		query.append(" WHERE BLL_STATUS = 'O'");
		if (garante!=null) {
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
	 * @param payments
	 *            the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed
	 *         {@link BillPayments}.
	 * @throws OHException
	 *             if an error occurs retrieving the bill list.
	 */
	public ArrayList<Bill> getBills(ArrayList<BillPayments> payments) throws OHException {

		ArrayList<Bill> bills = null;

		List<Object> parameters = new ArrayList<Object>();

		StringBuilder query = new StringBuilder("SELECT * FROM BILLS WHERE BLL_ID IN ( ");
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
		query.append(" ORDER BY BLL_ID DESC ");
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getDataWithParams(query.toString(), parameters, true);
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
	
	public ArrayList<BillItemReportBean> getTotalCountAmountByQuery12(int year, String status) throws OHException, ParseException, SQLException{
		String query ="Select groupe, descrip, totCount, totAmount from  ("
		     + " SELECT bli.BLI_ITEM_GROUP as groupe, bli.BLI_ITEM_DESC as descrip, SUM(bli.bli_qty) as totCount,"		
		     + " ABS(SUM(bli.BLI_ITEM_AMOUNT * bli.BLI_QTY)) as totAmount "
		     + " FROM BILLITEMS bli JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID WHERE bl.BLL_STATUS = '"+status+"' "
		     + " AND DATE(bl.BLL_DATE) BETWEEN ? AND ? GROUP BY descrip "
        + ") source_table ORDER BY groupe, descrip";
		
		if(status.equals("D")){
			query = "Select groupe, descrip, totCount, totAmount from  ("
				     + " SELECT bli.BLI_ITEM_GROUP as groupe, bli.BLI_ITEM_DESC as descrip, SUM(bli.bli_qty) as totCount,"		
				     + " ABS(SUM(bli.BLI_ITEM_AMOUNT * bli.BLI_QTY)) as totAmount "
				     + " FROM BILLITEMS bli JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID WHERE bl.BLL_STATUS != '"+status+"' "
				     + " AND DATE(bl.BLL_DATE) BETWEEN ? AND ? GROUP BY descrip "
		        + ") source_table ORDER BY groupe, descrip";
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>(3);
		
		String endFebruary;
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(); 
		endFebruary = cal.isLeapYear(year) ? "29/02/"+year: "28/02/"+year;
		String[] dates = {"01/01/"+year, "31/01/"+year, "01/02/"+year, endFebruary, "01/03/"+year, "31/03/"+year,
				          "01/04/"+year, "30/04/"+year, "01/05/"+year, "31/05/"+year, "01/06/"+year, "30/06/"+year
				         ,"01/07/"+year, "31/07/"+year, "01/08/"+year, "31/08/"+year, "01/09/"+year, "30/09/"+year
				         ,"01/10/"+year, "31/10/"+year, "01/11/"+year, "30/11/"+year, "01/12/"+year, "31/12/"+year};
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
			for(int i=0;i<13;i++){
				if(i==12){
					parameters.add(new Timestamp(datesG.get(0).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(23).getTime().getTime()));
				}else{
					parameters.add(new Timestamp(datesG.get(i*2).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(i*2+1).getTime().getTime()));
				}		
				ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
				String desc = "";
				String group = "";
				BillItemReportBean currentItem = null;
				while (resultSet.next()) {
					desc = resultSet.getString("descrip");
					group = resultSet.getString("groupe")==null?"":resultSet.getString("groupe");
					if(items.containsKey(desc)){
						currentItem = items.get(desc);
					    switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("totAmount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("totAmount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("totAmount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("totAmount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("totAmount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("totAmount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("totAmount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("totAmount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("totAmount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("totAmount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("totAmount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("totAmount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("totCount"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("totAmount"));
							break;
						default:
							break;
						}
						
					}else{
						currentItem = new BillItemReportBean();
						currentItem.setBLI_ITEM_DESC(desc);
						currentItem.setBLI_ITEM_GROUP(group);
						items.put(desc, currentItem);
						switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("totAmount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("totAmount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("totAmount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("totAmount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("totAmount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("totAmount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("totAmount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("totAmount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("totAmount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("totAmount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("totAmount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("totAmount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("totCount"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("totAmount"));
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

	
	public ArrayList<BillItemReportBean> getTotalCountAmountByQuery12(int year, String status, OperationType operationType) throws OHException, ParseException, SQLException{
		
		System.out.println("BBBBBBBBBBBBBBBBBBBBBBBB   " + operationType.getDescription());
		String query ="Select groupe, descrip, totCount, totAmount from  ("
		     + " SELECT bli.BLI_ITEM_GROUP as groupe, bli.BLI_ITEM_DESC as descrip, SUM(bli.bli_qty) as totCount,"		
		     + " ABS(SUM(bli.BLI_ITEM_AMOUNT * bli.BLI_QTY)) as totAmount "
		     + " FROM BILLITEMS bli JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID "
		     
		     + " JOIN OPERATION op ON op.OPE_ID_A = bli.BLI_ITEM_ID "
		     
		     + " WHERE bl.BLL_STATUS = '"+status+"' "
		     + " AND op.OPE_OCL_ID_A = '"+ operationType.getCode() +"' AND DATE(bl.BLL_DATE) BETWEEN ? AND ? AND bli.BLI_ITEM_GROUP = 'OPE' GROUP BY descrip "
        + ") source_table ORDER BY groupe, descrip";
		
		if(status.equals("D")){
			query = "Select groupe, descrip, totCount, totAmount from  ("
				     + " SELECT bli.BLI_ITEM_GROUP as groupe, bli.BLI_ITEM_DESC as descrip, SUM(bli.bli_qty) as totCount,"		
				     + " ABS(SUM(bli.BLI_ITEM_AMOUNT * bli.BLI_QTY)) as totAmount "
				     + " FROM BILLITEMS bli JOIN BILLS bl ON bli.BLI_ID_BILL = bl.BLL_ID "

 					 + " JOIN OPERATION op ON op.OPE_ID_A = bli.BLI_ITEM_ID "
 
				     + " WHERE bl.BLL_STATUS != '"+status+"' "
				     + " AND op.OPE_OCL_ID_A = '"+ operationType.getCode() +"' AND DATE(bl.BLL_DATE) BETWEEN ? AND ? AND bli.BLI_ITEM_GROUP = 'OPE'  GROUP BY descrip "
		        + ") source_table ORDER BY groupe, descrip";
		}
		
		DbQueryLogger dbQuery = new DbQueryLogger();
		List<Object> parameters = new ArrayList<Object>(3);
		
		//parameters.add(operationType.getCode());
		String endFebruary;
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance(); 
		endFebruary = cal.isLeapYear(year) ? "29/02/"+year: "28/02/"+year;
		String[] dates = {"01/01/"+year, "31/01/"+year, "01/02/"+year, endFebruary, "01/03/"+year, "31/03/"+year,
				          "01/04/"+year, "30/04/"+year, "01/05/"+year, "31/05/"+year, "01/06/"+year, "30/06/"+year
				         ,"01/07/"+year, "31/07/"+year, "01/08/"+year, "31/08/"+year, "01/09/"+year, "30/09/"+year
				         ,"01/10/"+year, "31/10/"+year, "01/11/"+year, "30/11/"+year, "01/12/"+year, "31/12/"+year};
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
			for(int i=0;i<13;i++){
				if(i==12){
					parameters.add(new Timestamp(datesG.get(0).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(23).getTime().getTime()));
				}else{
					parameters.add(new Timestamp(datesG.get(i*2).getTime().getTime()));
					parameters.add(new Timestamp(datesG.get(i*2+1).getTime().getTime()));
				}		
				ResultSet resultSet = dbQuery.getDataWithParams(query, parameters, true);
				String desc = "";
				String group = "";
				BillItemReportBean currentItem = null;
				while (resultSet.next()) {
					desc = resultSet.getString("descrip");
					group = resultSet.getString("groupe")==null?"":resultSet.getString("groupe");
					if(items.containsKey(desc)){
						currentItem = items.get(desc);
					    switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("totAmount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("totAmount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("totAmount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("totAmount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("totAmount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("totAmount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("totAmount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("totAmount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("totAmount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("totAmount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("totAmount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("totAmount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("totCount"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("totAmount"));
							break;
						default:
							break;
						}
						
					}else{
						currentItem = new BillItemReportBean();
						currentItem.setBLI_ITEM_DESC(desc);
						currentItem.setBLI_ITEM_GROUP(group);
						items.put(desc, currentItem);
						switch (i) {
						case 0:
							currentItem.setCOUNT_JANUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JANUARY(resultSet.getDouble("totAmount"));
							break;
						case 1:
							currentItem.setCOUNT_FEBRUARY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_FEBRUARY(resultSet.getDouble("totAmount"));
							break;
						case 2:
							currentItem.setCOUNT_MARCH(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MARCH(resultSet.getDouble("totAmount"));
							break;
						case 3:
							currentItem.setCOUNT_APRIL(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_APRIL(resultSet.getDouble("totAmount"));
							break;
						case 4:
							currentItem.setCOUNT_MAY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_MAY(resultSet.getDouble("totAmount"));
							break;
						case 5:
							currentItem.setCOUNT_JUNE(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JUNE(resultSet.getDouble("totAmount"));
							break;
						case 6:
							currentItem.setCOUNT_JULY(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_JULY(resultSet.getDouble("totAmount"));
							break;
						case 7:
							currentItem.setCOUNT_AUGUST(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_AUGUST(resultSet.getDouble("totAmount"));
							break;
						case 8:
							currentItem.setCOUNT_SEPTEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_SEPTEMBER(resultSet.getDouble("totAmount"));
							break;
						case 9:
							currentItem.setCOUNT_OCTOBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_OCTOBER(resultSet.getDouble("totAmount"));
							break;
						case 10:
							currentItem.setCOUNT_NOVEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_NOVEMBER(resultSet.getDouble("totAmount"));
							break;
						case 11:
							currentItem.setCOUNT_DECEMBER(resultSet.getDouble("totCount"));
							currentItem.setAMOUNT_DECEMBER(resultSet.getDouble("totAmount"));
							break;
						case 12:
							currentItem.setTOTAL_YEAR_COUNT(resultSet.getDouble("totCount"));
							currentItem.setTOTAL_YEAR_AMOUNT(resultSet.getDouble("totAmount"));
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
