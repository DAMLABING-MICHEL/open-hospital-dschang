package org.isf.accounting.model;

import java.awt.Component;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;
import org.isf.utils.time.TimeTools;

public class BillItemListItem extends BillItems {

	private boolean isSelected;
	private Double payAmount = 0.0;
	private Double paidAmount = 0.0;
	private Double toPay = 0.0;
	private GregorianCalendar date;
	
	
	public BillItemListItem() {
		super();
	}
	
	public BillItemListItem(BillItems item, ArrayList<BillItemPayments> billPaidItems, boolean isSelected, Double payAmount) {
		this.setSelected(isSelected);
		this.setPayAmount(payAmount);
		this.setDate(TimeTools.getServerDateTime());
		this.setToPay(item.toPayFrom(billPaidItems) - this.getPayAmount());
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
	
	public Boolean isPresentIn(ArrayList<BillItems> list) {
		for(int i=0; i<list.size(); i++) {
			BillItems item = list.get(i);
			if(item.getId() == this.getId()) {
				return true;
			}
		}
		return false;
	};
	
	public BillItemListItem updateWith(ArrayList<BillItems> list) {
		for(int i=0; i<list.size(); i++) {
			BillItems item = list.get(i);
			if(item.getId() == this.getId()) {
				list.remove(i);
				return this;
			}
		}
		return null;
	};
	
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	
	public Double getPayAmount() {
		return this.payAmount;
	}
	
	public void setPayAmount(Double payAmount) {
		this.payAmount = payAmount;
	}
	
	public Double getPaidAmount() {
		return this.paidAmount;
	}
	
	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}
	
	public Double getToPay() {
		return this.toPay;
	}
	
	public void setToPay(Double toPay) {
		this.toPay = toPay;
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