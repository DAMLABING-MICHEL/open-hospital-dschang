package org.isf.accounting.model;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class BillItemListItem extends BillItems {

	private boolean isSelected;
	private Double payAmount;
	
	
	public BillItemListItem() {
		super();
	}
	
	public BillItemListItem(BillItems item, boolean isSelected, Double payAmount) {
		this.setSelected(isSelected);
		this.setPayAmount(payAmount);
		this.setBillID(item.getBillID());
		this.setId(item.getId());
		this.setItemAmount(item.getItemAmount());
		this.setItemAmountBrut(item.getItemAmountBrut());
		this.setItemDescription(item.getItemDescription());
		this.setItemDisplayCode(item.getItemDisplayCode());
		this.setItemGroup(item.getItemGroup());
		this.setItemQuantity(item.getItemQuantity());
		this.setItemId(item.getItemId());
		this.setExport_status(item.getExport_status());
		this.setPrescriptionId(item.getPrescriptionId());
		this.setPrice(item.isPrice());
		this.setPriceID(item.getPriceID());
	}

	public BillItemListItem(int id, int billID, boolean isPrice, String priceID, String itemDescription,
			double itemAmount, double itemQuantity, boolean isSelected) {
		super(id, billID, isPrice, priceID, itemDescription, itemAmount, itemQuantity);
		this.isSelected = isSelected;
	}

	public BillItemListItem(int id, int billID, boolean isPrice, String priceID, String itemDescription,
			double itemAmount, double itemQuantity, double itemAmountBrut, boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public Double getPayAmount() {
		return this.payAmount;
	}
	
	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}
	
	public boolean isSelected() {
		return this.isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public void toggleSelected() {
		this.isSelected = !this.isSelected;
	}

}