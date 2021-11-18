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
public class BillPayments implements Comparable<Object>{

	private int id;
	private int billID;
	private GregorianCalendar date;
	private double amount;
	private String user;
	
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	//
	
	public BillPayments(){
		
	}
	
	public BillPayments(int id, int billID, GregorianCalendar date,
			double amount, String user) {
		super();
		this.id = id;
		this.billID = billID;
		this.date = date;
		this.amount = amount;
		this.user = user;		
	}
	
	public static Double totalFrom(ArrayList<BillPayments> payItems) {
		Double sum = 0.0;
		for (BillPayments item : payItems) {
			sum += item.amount;
		}
		return sum;
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
		if (anObject instanceof BillPayments)
			if (this.date.after(((BillPayments)anObject).getDate()))
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
