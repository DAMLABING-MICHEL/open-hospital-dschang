package org.isf.accounting.model;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


import org.isf.utils.jobjects.DateAdapter;
import org.isf.utils.time.TimeTools;

/**
 * Pure Model Bill : represents a Bill
 * @author Mwithi
 *
 *14/06/2022 - Silevester D. - Add props parentId and new constructor with parentId param
 */
@XmlRootElement
public class Bill implements Comparable<Bill> {
	private int id;
	private GregorianCalendar date;
	private GregorianCalendar update;
	private boolean isList;
	private int listID;
	private String listName;
	private boolean isPatient;
	private int patID;
	private String patName;
	private String status;
	private Double amount;
	private Double balance;
	private String user;
	private String wardCode;
	private int reductionPlan;
	private int affiliatedParent;
	private boolean closedManually;
	
	/**
	 * ParentId is the main bill id in refunds bills
	 */
	private int parentId;
	
	/**
	 * Refund amount is the amount of bill that has been refunded
	 */
	private double refundAmount;
	
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	//
		
	private String garante;
		
	public Bill() {
		super();
		this.id = 0;
		this.date = TimeTools.getServerDateTime();
		this.update = TimeTools.getServerDateTime();
		this.isList = true;
		this.listID = 0;
		this.listName = "";
		this.isPatient = false;
		this.patID = 0;
		this.patName = "";
		this.status = "";
		this.amount = 0.;
		this.balance = 0.;
		this.user = "admin";
		this.wardCode = "";
	}

	public Bill(int id, GregorianCalendar date, GregorianCalendar update,
			boolean isList, int listID, String listName, boolean isPatient,
			int patID, String patName, String status, Double amount, Double balance, String user, String wardcode) {
		super();
		this.id = id;
		this.date = date;
		this.update = update;
		this.isList = isList;
		this.listID = listID;
		this.listName = listName;
		this.isPatient = isPatient;
		this.patID = patID;
		this.patName = patName;
		this.status = status;
		this.amount = amount;
		this.balance = balance;
		this.user = user;
		this.wardCode = wardcode;
	}
	
	public Bill(int id, GregorianCalendar date, GregorianCalendar update,
			boolean isList, int listID, String listName, boolean isPatient,
			int patID, String patName, String status, Double amount, Double balance, String user, String wardcode, int reductPlan, int parent) {
		super();
		this.id = id;
		this.date = date;
		this.update = update;
		this.isList = isList;
		this.listID = listID;
		this.listName = listName;
		this.isPatient = isPatient;
		this.patID = patID;
		this.patName = patName;
		this.status = status;
		this.amount = amount;
		this.balance = balance;
		this.user = user;
		this.wardCode = wardcode;
		this.reductionPlan = reductPlan;
		this.affiliatedParent = parent;
	}
	
	public Bill(int id, GregorianCalendar date, GregorianCalendar update,
			boolean isList, int listID, String listName, boolean isPatient,
			int patID, String patName, String status, Double amount, Double balance, String user) {
		super();
		this.id = id;
		this.date = date;
		this.update = update;
		this.isList = isList;
		this.listID = listID;
		this.listName = listName;
		this.isPatient = isPatient;
		this.patID = patID;
		this.patName = patName;
		this.status = status;
		this.amount = amount;
		this.balance = balance;
		this.user = user;
		
	}
	
	/** Constructor with bill parent's id
	 * 
	 * @param id
	 * @param parentId
	 * @param date
	 * @param update
	 * @param isList
	 * @param listID
	 * @param listName
	 * @param isPatient
	 * @param patID
	 * @param patName
	 * @param status
	 * @param amount
	 * @param balance
	 * @param user
	 * @param wardcode
	 * @param reductPlan
	 * @param parent
	 */
	public Bill(int id, int parentId, GregorianCalendar date, GregorianCalendar update, boolean isList, int listID, String listName,
			boolean isPatient, int patID, String patName, String status, Double amount, Double balance, String user,
			String wardcode, int reductPlan, int parent) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.date = date;
		this.update = update;
		this.isList = isList;
		this.listID = listID;
		this.listName = listName;
		this.isPatient = isPatient;
		this.patID = patID;
		this.patName = patName;
		this.status = status;
		this.amount = amount;
		this.balance = balance;
		this.user = user;
		this.wardCode = wardcode;
		this.reductionPlan = reductPlan;
		this.affiliatedParent = parent;
	}


	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getDate() {
		return date;
	}
	
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	@XmlJavaTypeAdapter(DateAdapter.class)
	public GregorianCalendar getUpdate() {
		return update;
	}
	public void setUpdate(GregorianCalendar update) {
		this.update = update;
	}
	public boolean isList() {
		return isList;
	}
	public void setList(boolean isList) {
		this.isList = isList;
	}
	public int getListID() {
		return listID;
	}
	public void setListID(int listID) {
		this.listID = listID;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public boolean isPatient() {
		return isPatient;
	}
	public void setPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}
	public int getPatID() {
		return patID;
	}
	public void setPatID(int patID) {
		this.patID = patID;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int compareTo(Bill obj) {
		return this.id - obj.getId();
	}

	public String getWardCode() {
		return wardCode;
	}

	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}

	public int getReductionPlan() {
		return reductionPlan;
	}

	public void setReductionPlan(int reductionPlan) {
		this.reductionPlan = reductionPlan;
	}

	public int getAffiliatedParent() {
		return affiliatedParent;
	}

	public void setAffiliatedParent(int affiliatedParent) {
		this.affiliatedParent = affiliatedParent;
	}

	public boolean isClosedManually() {
		return closedManually;
	}

	public void setClosedManually(boolean closedManually) {
		this.closedManually = closedManually;
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

	public String getGarante() {
		return garante;
	}

	public void setGarante(String garante) {
		this.garante = garante;
	}
	
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
}
