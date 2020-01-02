package org.isf.operation.model;

import java.util.GregorianCalendar;

public class OperationRow {

	private int id;	
	private String operationId;	
	private String prescriber;// operation key (null)
	
	private String opResult;				// value is 'P' or 'N' (null)
	private GregorianCalendar opDate;
	private String remarks;
	private int admissionId;
	private int opdId;
	private int billId;
	private Float transUnit;
	//fields for history	
		private String create_by ; 
		private String modify_by ; 
		private GregorianCalendar create_date;
		private GregorianCalendar modify_date;
		//// transfusional unit
	
	public OperationRow(int id, String operationId, String prescriber,String opResult, GregorianCalendar opDate, String remarks, int admissionId,
			int opdId, int billId, Float transUnit) {
		super();
		this.id = id;
		this.operationId = operationId;
		this.prescriber = prescriber;
		this.opResult = opResult;
		this.opDate = opDate;
		this.remarks = remarks;
		this.admissionId = admissionId;
		this.opdId = opdId;
		this.billId = billId;
		this.transUnit = transUnit;
	}
	
	public OperationRow() {
		super();
	}
	
	public String getPrescriber() {
		return prescriber;
	}

	public void setPrescriber(String prescriber) {
		this.prescriber = prescriber;
	}
	
	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	public String getOpResult() {
		return opResult;
	}
	public void setOpResult(String opResult) {
		this.opResult = opResult;
	}
	public GregorianCalendar getOpDate() {
		return opDate;
	}
	public void setOpDate(GregorianCalendar opDate) {
		this.opDate = opDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getAdmissionId() {
		return admissionId;
	}
	public void setAdmissionId(int admissionId) {
		this.admissionId = admissionId;
	}
	public int getOpdId() {
		return opdId;
	}
	public void setOpdId(int opdId) {
		this.opdId = opdId;
	}

	public Float getTransUnit() {
		return transUnit;
	}

	public void setTransUnit(Float transUnit) {
		this.transUnit = transUnit;
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
