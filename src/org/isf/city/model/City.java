package org.isf.city.model;

/**
 * Pure Model Exam : represents a patient's city 
 * @author Mafo
 *
 */
public class City {
	
    private String libelle;
	public City() {
		super();
		// TODO Auto-generated constructor stub
	}
	 /**
     * @param code
     * @param libelle
     */
	public City(String libelle) {
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
