package org.isf.utils.jobjects;

import java.sql.Timestamp;
import java.util.Date;

public class MovementReportBean {
	
	private Timestamp  MMV_DATE;
	private String  MMV_LT_ID_A;
	private Date  LT_DUE_DATE;
	private Integer ENTRY_QTY;
	private String  ORIGIN;
	private Integer  OUT_QTY;
	private String  DESTINATION;
	private double MMV_STOCK_AFTER;
	private Integer MMV_ID;
	
	public MovementReportBean(Timestamp mMV_DATE, String mMV_LT_ID_A, Date lT_DUE_DATE, Integer eNTRY_QTY,
			String oRIGIN, Integer oUT_QTY, String dESTINATION, double mMV_STOCK_AFTER) {
		super();
		MMV_DATE = mMV_DATE;
		MMV_LT_ID_A = mMV_LT_ID_A;
		LT_DUE_DATE = lT_DUE_DATE;
		ENTRY_QTY = eNTRY_QTY;
		ORIGIN = oRIGIN;
		OUT_QTY = oUT_QTY;
		DESTINATION = dESTINATION;
		MMV_STOCK_AFTER = mMV_STOCK_AFTER;
	}

	public MovementReportBean(Timestamp mMV_DATE, String mMV_LT_ID_A, Date lT_DUE_DATE, Integer eNTRY_QTY,
			String oRIGIN, Integer oUT_QTY, String dESTINATION, double mMV_STOCK_AFTER, Integer mMV_ID) {
		super();
		MMV_DATE = mMV_DATE;
		MMV_LT_ID_A = mMV_LT_ID_A;
		LT_DUE_DATE = lT_DUE_DATE;
		ENTRY_QTY = eNTRY_QTY;
		ORIGIN = oRIGIN;
		OUT_QTY = oUT_QTY;
		DESTINATION = dESTINATION;
		MMV_STOCK_AFTER = mMV_STOCK_AFTER;
		MMV_ID = mMV_ID;
	}

	public Timestamp getMMV_DATE() {
		return MMV_DATE;
	}

	public void setMMV_DATE(Timestamp mMV_DATE) {
		MMV_DATE = mMV_DATE;
	}

	public String getMMV_LT_ID_A() {
		return MMV_LT_ID_A;
	}

	public void setMMV_LT_ID_A(String mMV_LT_ID_A) {
		MMV_LT_ID_A = mMV_LT_ID_A;
	}

	public Date getLT_DUE_DATE() {
		return LT_DUE_DATE;
	}

	public void setLT_DUE_DATE(Date lT_DUE_DATE) {
		LT_DUE_DATE = lT_DUE_DATE;
	}

	public Integer getENTRY_QTY() {
		return ENTRY_QTY;
	}

	public void setENTRY_QTY(Integer eNTRY_QTY) {
		ENTRY_QTY = eNTRY_QTY;
	}

	public String getORIGIN() {
		return ORIGIN;
	}

	public void setORIGIN(String oRIGIN) {
		ORIGIN = oRIGIN;
	}

	public Integer getOUT_QTY() {
		return OUT_QTY;
	}

	public void setOUT_QTY(Integer oUT_QTY) {
		OUT_QTY = oUT_QTY;
	}

	public String getDESTINATION() {
		return DESTINATION;
	}

	public void setDESTINATION(String dESTINATION) {
		DESTINATION = dESTINATION;
	}

	public double getMMV_STOCK_AFTER() {
		return MMV_STOCK_AFTER;
	}

	public void setMMV_STOCK_AFTER(double mMV_STOCK_AFTER) {
		MMV_STOCK_AFTER = mMV_STOCK_AFTER;
	}

	public Integer getMMV_ID() {
		return MMV_ID;
	}

	public void setMMV_ID(Integer mMV_ID) {
		MMV_ID = mMV_ID;
	}
	
	
}
