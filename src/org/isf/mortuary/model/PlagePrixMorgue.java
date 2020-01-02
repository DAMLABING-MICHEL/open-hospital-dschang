package org.isf.mortuary.model;

import java.util.GregorianCalendar;

public class PlagePrixMorgue {
	@Override
	public String toString() {
		return "PlagePrixMorgue [id=" + id + ", nbJourmin=" + nbJourmin + ", nbJourMax=" + nbJourMax + ", description="
				+ description + ", prixJournalier=" + prixJournalier + "]";
	}
	private int id;
	private int nbJourmin;
	private int nbJourMax;
	private String description;
	private float prixJournalier;	
	private String code;
	
	public PlagePrixMorgue(int id, int nbJourmin, int nbJourMax, float prixJournalier, String description, String code) {
		super();
		this.id = id;
		this.nbJourmin = nbJourmin;
		this.nbJourMax = nbJourMax;
		this.prixJournalier = prixJournalier;
		this.description = description;
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNbJourmin() {
		return nbJourmin;
	}
	public void setNbJourmin(int nbJourmin) {
		this.nbJourmin = nbJourmin;
	}
	public int getNbJourMax() {
		return nbJourMax;
	}
	public void setNbJourMax(int nbJourMax) {
		this.nbJourMax = nbJourMax;
	}
	public float getPrixJournalier() {
		return prixJournalier;
	}
	public void setPrixJournalier(float prixJournalier) {
		this.prixJournalier = prixJournalier;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	//
	
}
