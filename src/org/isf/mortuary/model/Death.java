package org.isf.mortuary.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.isf.mortuary.manager.DeathReasonBrowserManager;
import org.isf.patient.model.Patient;

public class Death {
	
	private int id;
	private String lieu;
	private Patient patient;
	private int idPatient;
	private int idMotif;
	private String provenance;
	private GregorianCalendar dateDeces;
	private GregorianCalendar dateEntree;
	private GregorianCalendar dateSortie;
	private GregorianCalendar dateSortieProvisoire;
	private DeathReason motif;
	private String nomDeclarant;
	private String telDeclarant;
	private String nidDeclarant;
	private String nomFamille;
	private String telFamille;
	private String nidFamille;
	private String patientName;
	private String casier;
	public int getIdPatient() {
		return idPatient;
	}
	public void setIdPatient(int idPatient) {
		this.idPatient = idPatient;
	}
	public int getIdMotif() {
		return idMotif;
	}
	public void setIdMotif(int idMotif) {
		this.idMotif = idMotif;
	}
	public String getCasier() {
		return casier;
	}
	public void setCasier(String casier) {
		this.casier = casier;
	}
	public String getSearchString() {
		StringBuffer sbNameCode = new StringBuffer();
		sbNameCode.append(getPatientName().toLowerCase());
		sbNameCode.append(getNomDeclarant().toLowerCase());
		sbNameCode.append(getNomFamille().toLowerCase());
		sbNameCode.append(getLieu().toLowerCase());
		sbNameCode.append(getProvenance().toLowerCase());
		return sbNameCode.toString();
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientSex() {
		return patientSex;
	}
	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}
	public int getPatientAge() {
		return patientAge;
	}
	public void setPatientAge(int patientAge) {
		this.patientAge = patientAge;
	}
	private String patientSex;
	private int patientAge;

	public Death() {
		super();
	}
	//fields for history	
	private String create_by ; 
	private String modify_by ; 
	private GregorianCalendar create_date;
	private GregorianCalendar modify_date;
	//
	public int getId() {
		return id;
	}
	@Override
	public String toString() {
		return "Deces [id=" + id + ", lieu=" + lieu + ", patient=" + patientName + ", provenance=" + provenance
				+ ", dateDeces=" + dateDeces + ", dateEntree=" + dateEntree + ", dateSortie=" + dateSortie
				+ ", dateSortieProvisoire=" + dateSortieProvisoire + ", motif=" + motif + ", nomDeclarant="
				+ nomDeclarant + ", telDeclarant=" + telDeclarant + ", nidDeclarant=" + nidDeclarant + ", nomFamille="
				+ nomFamille + ", telFamille=" + telFamille + ", nidFamille=" + nidFamille + "]";
	}
	public Death(int id, String lieu, Patient patient, String provenance, GregorianCalendar dateDeces,
			GregorianCalendar dateEntree, GregorianCalendar dateSortie, GregorianCalendar dateSortieProvisoire,
			DeathReason motif, String nomDeclarant, String telDeclarant, String nidDeclarant, String nomFamille,
			String telFamille, String nidFamille) {
		super();
		this.id = id;
		this.lieu = lieu;
		this.patient = patient;
		this.provenance = provenance;
		this.dateDeces = dateDeces;
		this.dateEntree = dateEntree;
		this.dateSortie = dateSortie;
		this.dateSortieProvisoire = dateSortieProvisoire;
		this.motif = motif;
		this.nomDeclarant = nomDeclarant;
		this.telDeclarant = telDeclarant;
		this.nidDeclarant = nidDeclarant;
		this.nomFamille = nomFamille;
		this.telFamille = telFamille;
		this.nidFamille = nidFamille;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLieu() {
		return lieu;
	}
	public void setLieu(String lieu) {
		this.lieu = lieu;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}
	public GregorianCalendar getDateDeces() {
		return dateDeces;
	}
	public void setDateDeces(GregorianCalendar dateDeces) {
		this.dateDeces = dateDeces;
	}
	public GregorianCalendar getDateEntree() {
		return dateEntree;
	}
	public void setDateEntree(GregorianCalendar dateEntree) {
		this.dateEntree = dateEntree;
	}
	public GregorianCalendar getDateSortie() {
		return dateSortie;
	}
	public void setDateSortie(GregorianCalendar dateSortie) {
		this.dateSortie = dateSortie;
	}
	public GregorianCalendar getDateSortieProvisoire() {
		return dateSortieProvisoire;
	}
	public void setDateSortieProvisoire(GregorianCalendar dateSortieProvisoire) {
		this.dateSortieProvisoire = dateSortieProvisoire;
	}
	public DeathReason getMotif() {
		return motif;
	}
	public void setMotif(DeathReason motif) {
		this.motif = motif;
	}
	public String getNomDeclarant() {
		return nomDeclarant;
	}
	public void setNomDeclarant(String nomDeclarant) {
		this.nomDeclarant = nomDeclarant;
	}
	public String getTelDeclarant() {
		return telDeclarant;
	}
	public void setTelDeclarant(String telDeclarant) {
		this.telDeclarant = telDeclarant;
	}
	public String getNidDeclarant() {
		return nidDeclarant;
	}
	public void setNidDeclarant(String nidDeclarant) {
		this.nidDeclarant = nidDeclarant;
	}
	public String getNomFamille() {
		return nomFamille;
	}
	public void setNomFamille(String nomFamille) {
		this.nomFamille = nomFamille;
	}
	public String getTelFamille() {
		return telFamille;
	}
	public void setTelFamille(String telFamille) {
		this.telFamille = telFamille;
	}
	public String getNidFamille() {
		return nidFamille;
	}
	public void setNidFamille(String nidFamille) {
		this.nidFamille = nidFamille;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateDeces == null) ? 0 : dateDeces.hashCode());
		result = prime * result + ((dateEntree == null) ? 0 : dateEntree.hashCode());
		result = prime * result + ((dateSortie == null) ? 0 : dateSortie.hashCode());
		result = prime * result + ((dateSortieProvisoire == null) ? 0 : dateSortieProvisoire.hashCode());
		result = prime * result + id;
		result = prime * result + idMotif;
		result = prime * result + idPatient;
		result = prime * result + ((lieu == null) ? 0 : lieu.hashCode());
		result = prime * result + ((nidDeclarant == null) ? 0 : nidDeclarant.hashCode());
		result = prime * result + ((nidFamille == null) ? 0 : nidFamille.hashCode());
		result = prime * result + ((nomDeclarant == null) ? 0 : nomDeclarant.hashCode());
		result = prime * result + ((nomFamille == null) ? 0 : nomFamille.hashCode());
		result = prime * result + ((provenance == null) ? 0 : provenance.hashCode());
		result = prime * result + ((telDeclarant == null) ? 0 : telDeclarant.hashCode());
		result = prime * result + ((telFamille == null) ? 0 : telFamille.hashCode());
		return result;
	}

	public boolean equals(Death other) {
		 SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		 String decesold = fmt.format(other.dateDeces.getTime());		 
		 String deces = fmt.format(dateDeces.getTime());		 
		 String entreeold = fmt.format(other.dateEntree.getTime());		 
		 String entree = fmt.format(dateEntree.getTime());		   
		 String sortieold = fmt.format(other.dateSortieProvisoire.getTime());		 
		 String sortie = fmt.format(dateSortieProvisoire.getTime()); 
		if (!deces.equals(decesold))
			return false;
		if (!entree.equals(entreeold))
			return false;
		if (!sortie.equals(sortieold))
			return false;
		if (idMotif != other.idMotif)
			return false;
		if (idPatient != other.idPatient)
			return false;
		if (!lieu.equals(other.lieu))
			return false;
		if (!nidDeclarant.equals(other.nidDeclarant))
			return false;
		if(!nidFamille.equals(other.nidFamille))
			return false;
		 if (!nomDeclarant.equals(other.nomDeclarant))
			return false;
		if (!nomFamille.equals(other.nomFamille))
			return false;
		if (!provenance.equals(other.provenance))
			return false;
		if (!telDeclarant.equals(other.telDeclarant))
			return false;
		 if (!telFamille.equals(other.telFamille))
			return false;
		return true;
	}
	public static String calendarToString(GregorianCalendar date){
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date dateEntree = date.getTime();				 
		String retour = dateFormat.format(dateEntree);
		return  retour;
	}
	
}
