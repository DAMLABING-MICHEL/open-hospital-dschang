package org.isf.supplier.model;

public class SimulateBill {
	private String CODE;
	private String DESCRIPTION;
	private Double PU;
	private Double REMISE;
	private Integer QUANTITE;
	private Double TOTAL;
	
	public SimulateBill() {
		// TODO Auto-generated constructor stub
	}

	public SimulateBill(String cODE, String dESCRIPTION, Double pU, Double rEMISE, Integer qUANTITE, Double tOTAL) {
		super();
		CODE = cODE;
		DESCRIPTION = dESCRIPTION;
		PU = pU;
		REMISE = rEMISE;
		QUANTITE = qUANTITE;
		TOTAL = tOTAL;
	}

	public String getCODE() {
		return CODE;
	}

	public void setCODE(String cODE) {
		CODE = cODE;
	}

	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}

	public Double getPU() {
		return PU;
	}

	public void setPU(Double pU) {
		PU = pU;
	}

	public Double getREMISE() {
		return REMISE;
	}

	public void setREMISE(Double rEMISE) {
		REMISE = rEMISE;
	}

	public Integer getQUANTITE() {
		return QUANTITE;
	}

	public void setQUANTITE(Integer qUANTITE) {
		QUANTITE = qUANTITE;
	}

	public Double getTOTAL() {
		return TOTAL;
	}

	public void setTOTAL(Double tOTAL) {
		TOTAL = tOTAL;
	}

	@Override
	public String toString() {
		return "SimulateBill [CODE=" + CODE + ", DESCRIPTION=" + DESCRIPTION + ", PU=" + PU + ", REMISE=" + REMISE
				+ ", QUANTITE=" + QUANTITE + ", TOTAL=" + TOTAL + "]";
	}
	

}
