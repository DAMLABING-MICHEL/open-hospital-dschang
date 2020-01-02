package org.isf.mortuary.model;

public class EtatPrixSejours {
	
	private String designation;

	public EtatPrixSejours(String designation, float prix_journalier, int nombre_jours, float montant) {
		super();
		this.designation = designation;
		this.prix_journalier = prix_journalier;
		this.nombre_jours = nombre_jours;
		this.montant = montant;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public float getPrix_journalier() {
		return prix_journalier;
	}
	public void setPrix_journalier(float prix_journalier) {
		this.prix_journalier = prix_journalier;
	}
	public int getNombre_jours() {
		return nombre_jours;
	}
	public void setNombre_jours(int nombre_jours) {
		this.nombre_jours = nombre_jours;
	}
	public float getMontant() {
		return montant;
	}
	public void setMontant(float montant) {
		this.montant = montant;
	}
	@Override
	public String toString() {
		return "EtatPrixSejours [designation=" + designation + ", prix_journalier=" + prix_journalier
				+ ", nombre_jours=" + nombre_jours + ", montant=" + montant + "]";
	}
	private float prix_journalier;
	private int nombre_jours;
	private float montant;
	
	
	
}
