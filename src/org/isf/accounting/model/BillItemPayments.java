package org.isf.accounting.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.isf.utils.jobjects.DateAdapter;

/**
 * Pure Model BillPayments : represents a patient Payment for a Bill
 * @author Mwithi
 *
 */
public class BillItemPayments implements Comparable<Object>{

	private int id;
	private int billID;
	private int itemID;
	private GregorianCalendar date;
	private double amount;
	private String user;
	
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	//
	
	public BillItemPayments(){
		
	}
	
	public BillItemPayments(int id, int billID, int itemID, GregorianCalendar date,
			double amount, String user) {
		super();
		this.id = 0;
		this.billID = billID;
		this.itemID = itemID;
		this.date = date;
		this.amount = amount;
		this.user = user;		
	}
	
	public BillItemPayments(BillItemListItem item) {
		super();
		this.id = 0;
		this.itemID = item.getId();
		this.date = item.getDate();
		this.amount = item.getPayAmount();	
	}
	
	public static ArrayList<BillItemPayments> fromList(ArrayList<BillItemListItem> list) {
		ArrayList<BillItemPayments> items = new ArrayList<BillItemPayments>();
		for (BillItemListItem item : list) {
			items.add(new BillItemPayments(item));
		}
		return items;
	}
	
	public static void fillWith(
			ArrayList<BillItemPayments> list, int billID, String user
	) {
		ArrayList<BillItemPayments> items = new ArrayList<BillItemPayments>();
		for (BillItemPayments item : list) {
			item.setBillID(billID);
			item.setUser(user);
		}
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
	
	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public int compareTo(Object anObject) {
		if (anObject instanceof BillItemPayments)
			if (this.date.after(((BillItemPayments)anObject).getDate()))
				return 1;
			else
				return 0;
		return 0;
	}

	public String getCreate_by() {
		return create_by;
	}

	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}

	public String getModify_by() {
		return modify_by;
	}

	public void setModify_by(String modify_by) {
		this.modify_by = modify_by;
	}

	public GregorianCalendar getCreate_date() {
		return create_date;
	}

	public void setCreate_date(GregorianCalendar create_date) {
		this.create_date = create_date;
	}
		
	public GregorianCalendar getModify_date() {
		return modify_date;
	}

	public void setModify_date(GregorianCalendar modify_date) {
		this.modify_date = modify_date;
	}
}
