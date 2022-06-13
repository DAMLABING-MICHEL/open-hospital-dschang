package org.isf.accounting.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.accounting.gui.PatientBillEdit;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.IoOperations;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.manager.LabManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryRow;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.model.OperationRow;
import org.isf.opetype.model.OperationType;
import org.isf.parameters.manager.Param;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.reduction.manager.ReductionPlanManager;
import org.isf.therapy.manager.TherapyManager;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.BillItemReportBean;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

public class BillBrowserManager {

	IoOperations ioOperations;

	public BillBrowserManager() {
		ioOperations = new IoOperations();
	}

	/**
	 * Returns all the stored {@link BillItems}.
	 * 
	 * @return a list of {@link BillItems} or null if an error occurs.
	 */
	public ArrayList<BillItems> getItems() {
		try {
			return ioOperations.getItems(0, true);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all the distinct stored {@link BillItems}.
	 * 
	 * @return a list of distinct {@link BillItems} or null if an error occurs.
	 */
	public ArrayList<BillItems> getDistinctItems() {
		try {
			return ioOperations.getDistictsBillItems();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link BillItems} associated to the passed {@link Bill} id.
	 * 
	 * @param billID
	 *            the bill id.
	 * @return a list of {@link BillItems} or <code>null</code> if an error
	 *         occurred.
	 */
	public ArrayList<BillItems> getItems(int billID) {
		if (billID == 0)
			return new ArrayList<BillItems>();
		try {
			return ioOperations.getItems(billID, true);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<BillItems> getItems(int billID, GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		if (billID == 0)
			return new ArrayList<BillItems>();
		try {
			return ioOperations.getItems(billID, true, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<BillItems> getItemsBy(int billID) {
		if (billID == 0)
			return new ArrayList<BillItems>();
		try {
			return ioOperations.getItemsBy(billID, true);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<BillItems> getItemsBy(int billID, GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		if (billID == 0)
			return new ArrayList<BillItems>();
		try {
			return ioOperations.getItemsBy(billID, true, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public ArrayList<BillItems> getItemsBy(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getItemsBy(true, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the stored {@link BillPayments}.
	 * 
	 * @return a list of bill payments or <code>null</code> if an error occurred.
	 */
	public ArrayList<BillPayments> getPayments() {
		try {
			return ioOperations.getPayments(0);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Gets all the {@link BillPayments} for the specified {@link Bill}.
	 * 
	 * @param billID
	 *            the bill id.
	 * @return a list of {@link BillPayments} or <code>null</code> if an error
	 *         occurred.
	 */
	public ArrayList<BillPayments> getPayments(int billID) {
		if (billID == 0)
			return new ArrayList<BillPayments>();
		try {
			return ioOperations.getPayments(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Stores a new {@link Bill}.
	 * 
	 * @param newBill
	 *            the bill to store.
	 * @param autoCommit
	 * @return the generated id.
	 */
	public int newBill(Bill newBill, ArrayList<BillItems> billItems, ArrayList<BillPayments> payItems) {

		boolean transactionState = DbQueryLogger.beginTrasaction();
		DbQueryLogger dbQueryLogger = new DbQueryLogger();
		try {
			int billID = ioOperations.newBill(newBill);

			if (billID == 0) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.newbill.failedtosavebill"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			} else {
				// getting patient
				PatientBrowserManager patManager = new PatientBrowserManager();
				Patient patient = patManager.getPatient(newBill.getPatID());
				//
				newBill.setId(billID);
				boolean itemsInserted = false;
				if (Param.bool("CREATELABORATORYAUTO")) {
					itemsInserted = newBillItemsWithAutomaticLaboratory(newBill, patient, billItems);
				} else {
					itemsInserted = newBillItems(billID, billItems, newBill.getDate());
				}

				boolean paymentInserted = false;
				if (itemsInserted) {
					paymentInserted = newBillPayments(billID, payItems);
				}

				if (itemsInserted && paymentInserted) {
					//// insert new laboratory according criteria
					// newLaboratory(newBill, null, billItems);
					////
					try {
						dbQueryLogger.commit(transactionState);
					} catch (OHException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
				} else {
					try {
						dbQueryLogger.rollback(transactionState);

					} catch (OHException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
					return 0;
				}
			}
			return billID;

		} catch (OHException e) {
			try {
				dbQueryLogger.rollback(transactionState);

			} catch (OHException ex) {
				ex.printStackTrace();
				// JOptionPane.showMessageDialog(null, e.getMessage());
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		} finally {
			try {
				dbQueryLogger.releaseConnection(transactionState);
				DbQueryLogger.releaseTrasaction(transactionState);
			} catch (OHException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Stores a list of {@link BillItems} associated to a {@link Bill}.
	 * 
	 * @param billID
	 *            the bill id.
	 * @param billItems
	 *            the bill items to store.
	 * @param autoCommit
	 * @return <code>true</code> if the {@link BillItems} have been store,
	 *         <code>false</code> otherwise.
	 * @throws OHException
	 */
	private boolean newBillItems(int billId, ArrayList<BillItems> billItems, GregorianCalendar itemDate) throws OHException {
		// try {
		ArrayList<BillItems> newItems = this.getNewItems(billItems);
		ArrayList<BillItems> deletedItems = this.getDeletedItems(billId, billItems);
		if (Param.bool("STOCKMVTONBILLSAVE")) {
			updateMedicalStock(deletedItems, billId, true);
			updateMedicalStock(newItems, billId, false);
		}
		// Update therapy and lab if applied
		updateTherapy(deletedItems, newItems);
		updateOpearionRow(deletedItems, newItems, billId);
		updateLaboratory(deletedItems, newItems, billId);
		// Update labs if applied
		return ioOperations.newBillItems(billId, billItems, itemDate);
		// } catch (OHException e) {
		//
		// throw e;
		// }
	}

	private boolean newBillItemsWithAutomaticLaboratory(Bill newBill, Patient patient, ArrayList<BillItems> billItems)
			throws OHException {
		ArrayList<BillItems> newItems = this.getNewItems(billItems);
		ArrayList<BillItems> deletedItems = this.getDeletedItems(newBill.getId(), billItems);
		if (Param.bool("STOCKMVTONBILLSAVE")) {
			updateMedicalStock(deletedItems, newBill.getId(), true);
			updateMedicalStock(newItems, newBill.getId(), false);
		}
		updateTherapy(deletedItems, newItems);
		updateOpearionRow(deletedItems, newItems, newBill.getId());
		updateLaboratory(deletedItems, newItems, newBill.getId());
		createOrDeleteAutomaticallyLaboratory(newBill, patient, deletedItems, newItems, newBill.getId());
		
		return ioOperations.newBillItems(newBill.getId(), billItems, TimeTools.getServerDateTime());
	}

	private void updateTherapy(ArrayList<BillItems> deletedItems, ArrayList<BillItems> newItems) {
		TherapyManager thManager = new TherapyManager();
		for (BillItems item : deletedItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.MEDICAL.getCode())) {
					// Update the related therapy
					Double qty = -Double.parseDouble(String.valueOf(item.getItemQuantity()));
					thManager.updateBougthQuantity(item.getPrescriptionId(), -qty);
				}
			}
		}
		for (BillItems item : newItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.MEDICAL.getCode())) {
					// Update the related therapy
					Double qty = Double.parseDouble(String.valueOf(item.getItemQuantity()));
					thManager.updateBougthQuantity(item.getPrescriptionId(), qty);
				}
			}
		}
	}

	private void updateOpearionRow(ArrayList<BillItems> deletedItems, ArrayList<BillItems> newItems, int billID) {
		OperationRowBrowserManager opRowManager = new OperationRowBrowserManager();
		for (BillItems item : deletedItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.OPERATION.getCode())) {
					opRowManager.updateBillIdOperationRow(item.getPrescriptionId(), 0);
				}
			}
		}
		for (BillItems item : newItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.OPERATION.getCode())) {
					opRowManager.updateBillIdOperationRow(item.getPrescriptionId(), billID);
				}
			}
		}
	}

	private void updateLaboratory(ArrayList<BillItems> deletedItems, ArrayList<BillItems> newItems, int billID) {
		LabManager labManager = new LabManager();
		for (BillItems item : deletedItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.EXAM.getCode())) {
					labManager.updateBillIdLaboratory(item.getPrescriptionId(), 0);
				}
			}
		}
		for (BillItems item : newItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.EXAM.getCode())) {
					labManager.updateBillIdLaboratory(item.getPrescriptionId(), billID);
				}
			}
		}
	}

	private void createOrDeleteAutomaticallyLaboratory(Bill newBill, Patient patient, ArrayList<BillItems> deletedItems,
			ArrayList<BillItems> newItems, int billID) {
		LabManager labManager = new LabManager();
		ExamBrowsingManager exaManager = new ExamBrowsingManager();
		Laboratory lab = null;
		Exam exa = null;
		for (BillItems item : deletedItems) {
			if (item.getPrescriptionId() > 0) {
				if (item.getItemGroup().equals(ItemGroup.EXAM.getCode())) {
					labManager.updateBillIdLaboratory(item.getPrescriptionId(), 0);
				}
				// System.out.println("deletedItems ss "+item.getPrescriptionId());
			}
		}
		GregorianCalendar datep = TimeTools.getServerDateTime();
		int currentProgNum = labManager.getProgMonth(datep.get(GregorianCalendar.MONTH) + 1,
				datep.get(GregorianCalendar.YEAR));
		UserBrowsingManager manager = new UserBrowsingManager();
		String userId = MainMenu.getUser();
		String userName = manager.getUsrName(userId);
		String isadmitted = getIsAdmitted(patient);
		ArrayList<LaboratoryRow> labRow = new ArrayList<LaboratoryRow>();
		boolean result = false;
		for (BillItems item : newItems) {
			// System.out.println("newItems "+item.getItemDescription());
			if (item.getItemGroup() != null && item.getItemGroup().equals(ItemGroup.EXAM.getCode())) {
				if (Param.bool("CREATELABORATORYAUTO")) {
					if ((newBill.getStatus().equals("C"))
							|| (newBill.getStatus().equals("O") && Param.bool("CREATELABORATORYAUTOWITHOPENEDBILL"))) {
						if (!(item.getPrescriptionId() > 0)) {
							exa = exaManager.getExam(item.getItemId());
							lab = new Laboratory();
							lab.setExam(exa);
							lab.setAge(patient.getAge());
							lab.setDate(datep);
							lab.setExamDate(datep);
							lab.setInOutPatient(isadmitted);
							lab.setPatId(patient.getCode());
							lab.setPatName(patient.getName());
							lab.setSex(patient.getSex() + "");
							lab.setPrescriber(userName);
							lab.setBillId(newBill.getId()); // setting bill id
							try {
								if (!(lab.getCode() > 0)) {
									lab.setMProg(++currentProgNum);
								}
							} catch (Exception exp) {
							}
							if (lab.getExam().getProcedure() == 1) {
								result = labManager.newLabFirstProcedure2(lab);
								System.out.println("code du lab cree proc 1 " + lab.getCode());
							} else {
								result = labManager.newLabSecondProcedure2(lab, labRow);
								System.out.println("code du lab cree proc 2 " + lab.getCode());
							}
							if (!result) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.labnew.thedatacouldnotbesaved"));
								return;
							} else {
								// item.setItemRealId(lab.getCode());
								item.setPrescriptionId(lab.getCode());
							}
						}
					}
				}
			}
		}
	}

	private ArrayList<BillItems> getDeletedItems(int billID, List<BillItems> newListItems) throws OHException {

		List<BillItems> oldListItems = ioOperations.getItemsBy(billID, false);
		if(oldListItems == null) {
			for(int i=0; i<oldListItems.size(); i++) {
				if(oldListItems.get(i).getItemQuantity() == 0.0) {
					oldListItems.remove(i);
				}
			}
		}

		if (oldListItems == null || oldListItems.size() == 0) {
			return new ArrayList<BillItems>();
		}

		ArrayList<BillItems> deletedList = new ArrayList<BillItems>();
		boolean found = false;
		boolean updated = false;

		for (BillItems oldItem : oldListItems) {
			found = false;
			updated = false;
			BillItems updatedItem = null;
			for (BillItems newItem : newListItems) {
				if (newItem.getId() > 0 && newItem.getId() == oldItem.getId()) {
					found = true;
					if (oldItem.getItemQuantity() != newItem.getItemQuantity()) {
						double diff = oldItem.getItemQuantity() - newItem.getItemQuantity();
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
					break;
				}
			}
			if (!found) {
				deletedList.add(oldItem);
			} else if (updated) {
				deletedList.add(updatedItem);
			}
		}
		return deletedList;
	}


	/**
	 * Return all new Item added to the bill
	 * 
	 * @param billItems
	 * @param billID
	 * @return
	 */
	private ArrayList<BillItems> getNewItems(ArrayList<BillItems> billItems) {

		ArrayList<BillItems> newList = new ArrayList<BillItems>();

		for (BillItems item : billItems) {
			if (item.getId() == 0) {
				newList.add(item);
			}
		}
		return newList;

	}

	private void updateMedicalStock(ArrayList<BillItems> medicalItems, int billID, boolean isCharge)
			throws OHException {

		try {
			BillBrowserManager billManager = new BillBrowserManager();
			Bill bill = billManager.getBill(billID);

			// if (bill.getStatus().equals("C")) {

			/*** recuperation du code ward dans la facture **/
			String wardCode = "";
			wardCode = bill.getWardCode();

			// String wardCode = MainMenu.getUserWard();
			WardBrowserManager manager = new WardBrowserManager();
			ArrayList<Ward> wardList = new ArrayList<Ward>();
			wardList = manager.getWards();
			Ward selectedWard = null;
			for (Ward ward : wardList) {
				if (ward.getCode().equals(wardCode)) {
					selectedWard = ward;
					break;
				}
			}

			PatientBrowserManager patmanager = new PatientBrowserManager();
			Patient patient = patmanager.getPatient(bill.getPatID());

			if (selectedWard != null) {
				for (BillItems billItem : medicalItems) {
					String itemGroup = billItem.getItemGroup();
					if (itemGroup != null && itemGroup.equalsIgnoreCase(ItemGroup.MEDICAL.getCode())) {
						addStockMvt(selectedWard, patient, billItem, isCharge);
					}
				}
			}
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new OHException(e.getMessage(), e);
		}

	}

	private void addStockMvt(Ward selectedWard, Patient patient, BillItems billItem, boolean isCharge)
			throws OHException {

		MovWardBrowserManager mvtManager = new MovWardBrowserManager();

		double qty = Double.parseDouble(String.valueOf(billItem.getItemQuantity()));
		if (isCharge) {
			qty = -qty;
			if (qty > 0) {
				// Check that the stock does not go negative
				MedicalWard medWard = mvtManager.getMedicalsWard(selectedWard.getCode(),
						Integer.parseInt(billItem.getItemId()));
				if (medWard.getQty() < qty) {
					throw new OHException(MessageBundle.getMessage("angal.newbill.qtynotinstock"));
				}
			}
		} else {
			// Check that the stock does not go negative
			MedicalWard medWard = mvtManager.getMedicalsWard(selectedWard.getCode(),
					Integer.parseInt(billItem.getItemId()));

			if (medWard == null || medWard.getQty() < qty) {
				throw new OHException(MessageBundle.getMessage("angal.newbill.qtynotinstock"));
			}
		}
		MovementWard mvt = new MovementWard();
		mvt.setWard(selectedWard);
		mvt.setPatient(patient);
		mvt.setDate(TimeTools.getServerDateTime());
		mvt.setIsPatient(true);
		mvt.setQuantity(qty);
		MedicalBrowsingManager medManager = new MedicalBrowsingManager();
		mvt.setMedical(medManager.getMedical(Integer.parseInt(billItem.getItemId())));
		mvt.setDescription("");
		mvt.setUnits("pieces");
		mvtManager.newMovementWard(mvt);
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
	 */
	private boolean newBillPayments(int billID, ArrayList<BillPayments> payItems) {
		try {
			return ioOperations.newBillPayments(billID, payItems);
		} catch (OHException e) {
			return false;
		}
	}

	/**
	 * Updates the specified {@link Bill}.
	 * 
	 * @param updateBill
	 *            the bill to update.
	 * @return <code>true</code> if the bill has been updated, <code>false</code>
	 *         otherwise.
	 */
	public boolean updateBill(Bill updateBill, ArrayList<BillItems> billItems, ArrayList<BillPayments> payItems) {
		boolean transactionState = DbQueryLogger.beginTrasaction();
		DbQueryLogger dbQueryLogger = new DbQueryLogger();
		try {
			// getting patient
			PatientBrowserManager patManager = new PatientBrowserManager();
			Patient patient = patManager.getPatient(updateBill.getPatID());
			//

			boolean res = ioOperations.updateBill(updateBill);
			// boolean itemsInserted=newBillItems(updateBill.getId(), billItems);
			boolean itemsInserted = false;
			if (Param.bool("CREATELABORATORYAUTO")) {
				itemsInserted = newBillItemsWithAutomaticLaboratory(updateBill, patient, billItems);
			} else {
				System.out.println(updateBill.getId()+" "+billItems.size());
				itemsInserted = newBillItems(updateBill.getId(), billItems, TimeTools.getServerDateTime());
			}
			boolean paymentsInserted = false;
			if (itemsInserted) {
				paymentsInserted = newBillPayments(updateBill.getId(), payItems);
			}

			if (itemsInserted && paymentsInserted) {
				try {
					dbQueryLogger.commit(transactionState);
				} catch (OHException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			} else {
				try {
					dbQueryLogger.rollback(transactionState);
				} catch (OHException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e.getMessage());
				}

				return false;
			}

			return res;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		} finally {
			try {
				dbQueryLogger.releaseConnection(transactionState);
				DbQueryLogger.releaseTrasaction(transactionState);
			} catch (OHException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns all the pending {@link Bill}s for the specified patient.
	 * 
	 * @param patID
	 *            the patient id.
	 * @return the list of pending bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getPendingBills(int patID) {
		try {
			return ioOperations.getPendingBills(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getPendingBills2(int patID) {
		try {
			return ioOperations.getPendingBills2(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getPendingBillsAffiliate(int patID) {
		try {
			return ioOperations.getPendingBillsAffiliate(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getPendingBillsSpecificItem(String itemId) {
		try {
			return ioOperations.getPendingBillsSpecificItem(itemId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getPendingBillsSpecificGarante(User garante) {
		try {
			return ioOperations.getPendingBillsSpecificGarante(garante);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Get all the {@link Bill}s.
	 * 
	 * @return a list of bills or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getBills() {
		try {
			return ioOperations.getBills();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Get the {@link Bill} with specified billID
	 * 
	 * @param billID
	 * @return the {@link Bill} or <code>null</code> if an error occurred.
	 */
	public Bill getBill(int billID) {
		try {
			return ioOperations.getBill(billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all user ids related to a {@link BillPayments}.
	 * 
	 * @return a list of user id or <code>null</code> if an error occurred.
	 */
	public ArrayList<String> getUsers() {
		try {
			return ioOperations.getUsers();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Deletes the specified {@link Bill}.
	 * 
	 * @param deleteBill
	 *            the bill to delete.
	 * @return <code>true</code> if the bill has been deleted, <code>false</code>
	 *         otherwise.
	 */
	public boolean deleteBill(Bill deleteBill) {

		boolean transactionState = DbQueryLogger.beginTrasaction();
		DbQueryLogger dbQueryLogger = new DbQueryLogger();
		try {
			ArrayList<BillItems> deletedItems = this.getItems(deleteBill.getId());
			ArrayList<BillItems> newItems = new ArrayList<BillItems>();

			boolean res = ioOperations.deleteBill(deleteBill);

			if (Param.bool("STOCKMVTONBILLSAVE")) {
				updateMedicalStock(deletedItems, deleteBill.getId(), true);
			}

			// Update therapy and lab if applied
			updateTherapy(deletedItems, newItems);
			updateOpearionRow(deletedItems, newItems, deleteBill.getId());
			updateLaboratory(deletedItems, newItems, deleteBill.getId());

			try {
				dbQueryLogger.commit(transactionState);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

			return res;
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		} finally {
			try {
				dbQueryLogger.releaseConnection(transactionState);
				DbQueryLogger.releaseTrasaction(transactionState);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Retrieves all the {@link Bill}s for the specified date range.
	 * 
	 * @param dateFrom
	 *            the low date range endpoint, inclusive.
	 * @param dateTo
	 *            the high date range endpoint, inclusive.
	 * @return a list of retrieved {@link Bill}s or <code>null</code> if an error
	 *         occurred.
	 */
	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getBills(dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		try {
			return ioOperations.getBills(dateFrom, dateTo, patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, User userGarant) {
		try {
			return ioOperations.getBills(dateFrom, dateTo, userGarant);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Bill> getBills(GregorianCalendar dateFrom, GregorianCalendar dateTo, BillItems billItem) {
		try {
			return ioOperations.getBills(dateFrom, dateTo, billItem);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Gets all the {@link Bill}s associated to the passed {@link BillPayments}.
	 * 
	 * @param payments
	 *            the {@link BillPayments} associated to the bill to retrieve.
	 * @return a list of {@link Bill} associated to the passed {@link BillPayments}
	 *         or <code>null</code> if an error occurred.
	 */
	public ArrayList<Bill> getBills(ArrayList<BillPayments> billPayments) {
		if (billPayments.isEmpty())
			return new ArrayList<Bill>();
		try {
			return ioOperations.getBills(billPayments);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link BillPayments} for the specified date range.
	 * 
	 * @param dateFrom
	 *            low endpoint, inclusive, for the date range.
	 * @param dateTo
	 *            high endpoint, inclusive, for the date range.
	 * @return a list of {@link BillPayments} for the specified date range or
	 *         <code>null</code> if an error occurred.
	 */
	public ArrayList<BillPayments> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getPayments(dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<BillPayments> getPayments(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		try {
			return ioOperations.getPayments(dateFrom, dateTo, patient);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves all the {@link BillPayments} associated to the passed {@link Bill}
	 * list.
	 * 
	 * @param bills
	 *            the bill list.
	 * @return a list of {@link BillPayments} associated to the passed bill list or
	 *         <code>null</code> if an error occurred.
	 */
	public ArrayList<BillPayments> getPayments(ArrayList<Bill> billArray) {
		try {
			return ioOperations.getPayments(billArray);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	/**
	 * return the price of the given item
	 * 
	 * @param item
	 * @param patien
	 * @return
	 */
	public Price getPrice(String itemId, ItemGroup group, Patient patient) {

		int pbiID = patient.getReductionPlanID();

		PriceListManager prcManager = new PriceListManager();
		ReductionPlanManager reductionPlanManager = new ReductionPlanManager();

		ArrayList<org.isf.priceslist.model.List> lstArray = prcManager.getLists();

		org.isf.priceslist.model.List listSelected = null;

		if (patient != null && patient.getListID() != 0) {
			listSelected = prcManager.getListById(patient.getListID());
		}
		if (listSelected == null) {
			listSelected = lstArray.get(0);
		}

		Price price = prcManager.getPrice(listSelected, group, itemId);

		switch (group) {
		case EXAM:
			price = reductionPlanManager.getExamPrice(price, pbiID);
			break;
		case MEDICAL:
			price = reductionPlanManager.getMedicalPrice(price, pbiID);
			break;
		case OPERATION:
			price = reductionPlanManager.getOperationPrice(price, pbiID);
			break;
		case OTHER:
			price = reductionPlanManager.getOtherPrice(price, pbiID);
			break;
		default:
			break;
		}

		return price;
	}

	public Price getPriceFromListWithoutReduction(String itemId, ItemGroup group, Patient patient) {

		int pbiID = patient.getReductionPlanID();

		PriceListManager prcManager = new PriceListManager();
		ReductionPlanManager reductionPlanManager = new ReductionPlanManager();

		ArrayList<org.isf.priceslist.model.List> lstArray = prcManager.getLists();

		org.isf.priceslist.model.List listSelected = null;

		if (patient != null && patient.getListID() != 0) {
			listSelected = prcManager.getListById(patient.getListID());
		}
		if (listSelected == null) {
			listSelected = lstArray.get(0);
		}

		Price price = prcManager.getPrice(listSelected, group, itemId);

		return price;
	}

	public boolean hasPrescription(Integer patCode) {
		TherapyManager thManager = new TherapyManager();
		boolean hasTherapy = thManager.hasTherapiesRowsNotYetBought(patCode);
		boolean hasOpe = new OperationRowBrowserManager().hasOperationWithoutBill(patCode.toString());
		boolean hasExam = new LabManager().hasLabWithoutBill(patCode.toString());
		return hasTherapy || hasOpe || hasExam;
	}

	public void commitTransaction() {
		try {
			DbSingleConn.commitConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	public boolean closeBill(Bill bill) throws OHException {
		bill.setClosedManually(true);
		bill.setBalance(0.0);
		bill.setStatus("C");
		return ioOperations.updateBill(bill);

	}

	public ArrayList<BillItemReportBean> getTotalCountAmountByQuery12(int year, String status)
			throws OHException, ParseException, SQLException {
		try {
			return ioOperations.getTotalCountAmountByQuery12(year, status);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public HashMap<String, Double> getTotalCountAmountByQuery(String billDesc, GregorianCalendar dateFrom,
			GregorianCalendar dateTo) throws OHException {
		try {
			return ioOperations.getTotalCountAmountByQuery(billDesc, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public boolean updateBillItemsExportStatus(BillItems updateBillItem) throws OHException {
		try {
			return ioOperations.updateBillItemsExportStatus(updateBillItem);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	// public ArrayList<String> getBillItemsDesc() throws OHException {
	// try {
	// return ioOperations.getBillItemsDesc();
	// } catch (OHException e) {
	// JOptionPane.showMessageDialog(null, e.getMessage());
	// return null;
	// }
	// }
	private String getIsAdmitted(Patient patientSelected) {
		AdmissionBrowserManager man = new AdmissionBrowserManager();
		Admission adm = new Admission();
		adm = man.getCurrentAdmission(patientSelected);
		return (adm == null ? "R" : "I");
	}

	public ArrayList<BillItemReportBean> getTotalCountAmountByQuery12(int year, String status,
			OperationType operationType) throws OHException, ParseException, SQLException {
		try {
			return ioOperations.getTotalCountAmountByQuery12(year, status, operationType);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
}
