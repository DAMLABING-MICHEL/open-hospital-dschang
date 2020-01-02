package org.isf.patient.model;

import org.isf.generaldata.MessageBundle;

public enum MaritalStatus {
	
	UNDEFINED("", ""),
	MARIED_MONOGAMY("MM", MessageBundle.getMessage("angal.patient.status.mariedmonogamy")),
	MARIED_POLYGAMY("MP", MessageBundle.getMessage("angal.patient.status.mariedpolygamy")),
	CONCUBINAGE("CO", MessageBundle.getMessage("angal.patient.status.concubinage")),
	SINGLE("CE", MessageBundle.getMessage("angal.patient.status.single")),
	WIDOW("VE", MessageBundle.getMessage("angal.patient.status.widow")),
	DIVORCED("DI", MessageBundle.getMessage("angal.patient.status.divorced"));

	private String code;
	private String description;
	
	public static MaritalStatus getMaritalStatusByCode(String code){
		MaritalStatus[] statusList=MaritalStatus.values();
		for (int i = 0; i < statusList.length; i++) {
			if(statusList[i].getCode().equals(code)){
				return statusList[i];
			}
		}
		return null;
	}
	
	private MaritalStatus(String code, String description){
		this.setCode(code);
		this.setDescription(description);
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}
	
	public String toString(){
		return this.getDescription();
	}
	
	
	
}
