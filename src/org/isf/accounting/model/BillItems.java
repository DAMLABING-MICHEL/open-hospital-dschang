package org.isf.accounting.model;

import java.util.ArrayList;

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
	
	public BillItems() {
		super();
	}

	public BillItems(int id, int billID, boolean isPrice, String priceID,
			String itemDescription, double itemAmount, double itemQuantity) {
		super();
		this.id = id;
		this.billID = billID;
		this.isPrice = isPrice;
		this.priceID = priceID;
		this.itemDescription = itemDescription;
		this.itemAmount = itemAmount;
		this.itemQuantity = itemQuantity;
	}
	
	public BillItems(int id, int billID, boolean isPrice, String priceID,
			String itemDescription, double itemAmount, double itemQuantity, double itemAmountBrut) {
		super();
		this.id = id;
		this.billID = billID;
		this.isPrice = isPrice;
		this.priceID = priceID;
		this.itemDescription = itemDescription;
		this.itemAmount = itemAmount;
		this.itemQuantity = itemQuantity;
		this.itemAmountBrut = itemAmountBrut;
	}
	
	public Double toPayFrom(ArrayList<BillItemPayments> list) {
		ArrayList<BillItemPayments> items = new ArrayList<BillItemPayments>();
		Double sum = 0.0, amount = this.getItemAmount() * this.getItemQuantity();
		for(BillItemPayments item : list) {
			if(this.getId() == item.getItemID()) {
				items.add(item);
			}
		}
		if(items.isEmpty()) {
			return amount;
		}
		for (BillItemPayments item : items) {
			sum += item.getAmount();
		}
		return amount-sum;
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
				this.getItemAmount(), this.getItemQuantity());
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

//	public int getItemRealId() {
//		return itemRealId;
//	}
//
//	public void setItemRealId(int itemRealId) {
//		this.itemRealId = itemRealId;
//	}

	

	
}
