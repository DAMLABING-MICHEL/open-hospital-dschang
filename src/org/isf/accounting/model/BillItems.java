package org.isf.accounting.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Pure Model BillItems : represents an item in the Bill
 * @author Mwithi
 *
 */
public class BillItems {
	private int id;
	private int billID;
	private boolean isPrice;
	private String priceID;
	private String itemDescription;
	private double itemAmount;
	private double itemQuantity;
	private double itemAmountBrut;
	private String export_status;
	private GregorianCalendar itemDate;
	
	/**
	 * Store the item Real Id that is involved in this bill item (medId, exaId, opeId...)
	 */
	//private int itemRealId;
	
	/**
	 * Store the item Id that is involved in this bill item (medId, exaId, opeId...)
	 */
	private String itemId;
	/**
	 * Store the item type (MED, OPE, EXA, OTH)
	 */
	private String itemGroup;
	/**
	 * the Id of the therapy or the lab that lead to this bill
	 */
	private int prescriptionId;
	
	/**
	 * Store  the code of the item that is used for search purpose.
	 * For medical for example it will store the prod_code.
	 * it is necessary for medical and otherPRices items
	 * used in the patient bill edit
	 * The field is not persisted
	 */
	private String itemDisplayCode;
	
	/**
	 * Refunded quantity is the quantity of the item that has already been reimbursed.
	 * This props is never persisted.
	 * 
	 * Added on 18/05/2022 by Silevester D.
	 */
	private int refundedQty;
	
	public BillItems() {
		super();
	}

	public BillItems(int id, int billID, boolean isPrice, String priceID,
			String itemDescription, double itemAmount, double itemQuantity, GregorianCalendar itemDate) {
		super();
		this.id = id;
		this.billID = billID;
		this.isPrice = isPrice;
		this.priceID = priceID;
		this.itemDescription = itemDescription;
		this.itemAmount = itemAmount;
		this.itemQuantity = itemQuantity;
		this.itemDate = itemDate;
	}
	
	public BillItems(int id, int billID, boolean isPrice, String priceID, String itemDescription,
			double itemAmount, double itemQuantity, double itemAmountBrut, GregorianCalendar itemDate) {
		super();
		this.id = id;
		this.billID = billID;
		this.isPrice = isPrice;
		this.priceID = priceID;
		this.itemDescription = itemDescription;
		this.itemAmount = itemAmount;
		this.itemQuantity = itemQuantity;
		this.itemAmountBrut = itemAmountBrut;
		this.itemDate = itemDate;
	}
	
	public static void removeItemsWithNullQuantity(ArrayList<BillItems> items) {
		for(int i=0; i<items.size(); i++) {
			if(items.get(i).getItemQuantity() == 0.0) items.remove(i);
		}
	}
	
	public static NavigableMap<Integer, ArrayList<BillItems>> toMap(ArrayList<BillItems> items) {
		NavigableMap<Integer, ArrayList<BillItems>> mapBills = new TreeMap<Integer, ArrayList<BillItems>>();
		for (BillItems item : items) {
			ArrayList<BillItems> billItems = mapBills.get(item.getBillID());
			if(billItems == null) {
				mapBills.put(item.getBillID(), billItems = new ArrayList<BillItems>());
			}
			billItems.add(item);
		}
		return mapBills;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBillID() {
		return billID;
	}

	public void setBillID(int billID) {
		this.billID = billID;
	}

	public boolean isPrice() {
		return isPrice;
	}

	public void setPrice(boolean isPrice) {
		this.isPrice = isPrice;
	}

	public String getPriceID() {
		return priceID;
	}

	public void setPriceID(String priceID) {
		this.priceID = priceID;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public double getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(double itemAmount) {
		this.itemAmount = itemAmount;
	}

	public double getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(double itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
	public GregorianCalendar getItemDate() {
		return itemDate;
	}

	public void setItemDate(GregorianCalendar itemDate) {
		this.itemDate = itemDate;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(String itemGroup) {
		this.itemGroup = itemGroup;
	}

	public int getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(int prescriptionId) {
		this.prescriptionId = prescriptionId;
	}
	
	
	public String getItemDisplayCode() {
		if(itemDisplayCode==null || itemDisplayCode.equals("")){
			itemDisplayCode=itemId;
		}
		return itemDisplayCode;
	}

	public void setItemDisplayCode(String itemDisplayCode) {
		this.itemDisplayCode = itemDisplayCode;
	}

	@Override
	public BillItems clone() throws CloneNotSupportedException {
		BillItems clone=new BillItems(this.getId(), this.getBillID(), 
				this.isPrice(), this.getPriceID(), this.getItemDescription(), 
				this.getItemAmount(), this.getItemQuantity(), this.getItemDate());
		clone.setItemGroup(this.getItemGroup());
		clone.setItemId(this.getItemId());
		return clone;
	}

	public double getItemAmountBrut() {
		return itemAmountBrut;
	}

	public void setItemAmountBrut(double itemAmountBrut) {
		this.itemAmountBrut = itemAmountBrut;
	}

	public String getExport_status() {
		return export_status;
	}

	public void setExport_status(String export_status) {
		this.export_status = export_status;
	}

	public int getRefundedQty() {
		return refundedQty;
	}

	public void setRefundedQty(int refundedQty) {
		this.refundedQty = refundedQty;
	}
	
//	public int getItemRealId() {
//		return itemRealId;
//	}
//
//	public void setItemRealId(int itemRealId) {
//		this.itemRealId = itemRealId;
//	}

	

	
}
