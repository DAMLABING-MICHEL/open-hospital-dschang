package org.isf.serviceprinting.manager;

public class WardEntryItems {
	private String medical;
	private String lot;
	private double qte;
	private double pu;
	private double montant;
	private String ward;
	public String getWard() {
		return ward;
	}
	public void setWard(String ward) {
		this.ward = ward;
	}


	@Override
	public String toString() {
		return "WardEntryItems [medical=" + medical + ", lot=" + lot + ", qte=" + qte + ", pu=" + pu + ", montant="
				+ montant + ", ward=" + ward + "]";
	}

	public WardEntryItems(String medical, String lot, double qte, double pu, double montant, String ward) {
		super();
		this.medical = medical;
		this.lot = lot;
		this.qte = qte;
		this.pu = pu;
		this.montant = montant;
		this.ward = ward;
	}
	public String getMedical() {
		return medical;
	}
	public void setMedical(String medical) {
		this.medical = medical;
	}
	public String getLot() {
		return lot;
	}
	public void setLot(String lot) {
		this.lot = lot;
	}
	public double getQte() {
		return qte;
	}
	public void setQte(double qte) {
		this.qte = qte;
	}
	public double getPu() {
		return pu;
	}
	public void setPu(double pu) {
		this.pu = pu;
	}
	public double getMontant() {
		return montant;
	}
	public void setMontant(double montant) {
		this.montant = montant;
	}
	
}
