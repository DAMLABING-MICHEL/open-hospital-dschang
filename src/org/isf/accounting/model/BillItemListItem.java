package org.isf.accounting.model;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;
import org.isf.utils.time.TimeTools;

public class BillItemListItem extends BillItems implements Comparable<Object> {

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
		//this.setPayAmount(payAmount);
		this.setDate(TimeTools.getServerDateTime());
		this.setToPay(item.toPayFrom(billPaidItems));
		this.setPaidAmount(item.paidAmount(billPaidItems));
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
	
	public static void sort(ArrayList<BillItemListItem> list, Boolean asc) {
		list.sort(new Comparator<BillItemListItem>() {
		    @Override
		    public int compare(BillItemListItem a, BillItemListItem b) {
		        if(asc == true) {
		        	return a.getPayAmount() > b.getPayAmount() ? 1 : (a.getPayAmount() < b.getPayAmount()) ? -1 : 0;
		        }
		        return a.getPayAmount() > b.getPayAmount() ? -1 : (a.getPayAmount() < b.getPayAmount()) ? 1 : 0;
		    }
		});
	}
	
	public static ArrayList<BillItemListItem> checkedItems(ArrayList<BillItemListItem> items) {
		ArrayList<BillItemListItem> checkedItems = new ArrayList<BillItemListItem>();
		for (BillItemListItem item : items) {
			if(item.isSelected) {
				items.add(item);
			}
		}
		return checkedItems;
	}
	
	public static Double remainingAmount(ArrayList<BillItemListItem> items, Double paymentAmount) {
		Double remainingAmount = new Double(paymentAmount);
		for (BillItemListItem item : items) {
			if(item.isSelected) {
				remainingAmount -= item.getPayAmount();
			}
		}

		return remainingAmount;
	}
	
	public static void setPayAmount(ArrayList<BillItemListItem> items, Double amount) {
		BillItemListItem.sort(items, true);
		Double amnt = new Double(amount);
		for (BillItemListItem item : items) {
			if(item.getToPay() <= amnt) {
				item.setPayAmount(item.getToPay());
				item.setSelected(true);
				amnt -= item.getToPay();
			}
			else if(amnt != 0.0) {
				item.setPayAmount(amnt);
				item.setSelected(true);
				break;
			}
		}
	}
	
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

	@Override
	public int compareTo(Object object) {
		if(object instanceof BillItemListItem) {
			if(this.getToPay() < ((BillItemListItem) object).getToPay()) return -1;
			if(this.getToPay() > ((BillItemListItem) object).getToPay()) return 1;
		}
		return 0;
	}

}