package org.isf.occupation.model;

/**
 * Pure Model Exam : represents a patient's occupation 
 * @author Mafo
 *
 */
public class Occupation {
	
    private String libelle;
	public Occupation() {
		super();
		// TODO Auto-generated constructor stub
	}
	 /**
     * @param code
     * @param libelle
     */
	public Occupation(String libelle) {
		super();
		this.libelle = libelle;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
    
    

}
