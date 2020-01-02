package org.isf.patient.model;

import org.isf.generaldata.MessageBundle;

public enum GeographicArea {
	
	UNDEFINED("", ""),
	INSIDE_AREA("IN", MessageBundle.getMessage("angal.patient.geaographic.inside")),
	OUTSIDE_AREA("OUT", MessageBundle.getMessage("angal.patient.geaographic.outside")),
	OUTSIDE_DISTRICT("OUTD", MessageBundle.getMessage("angal.patient.geaographic.outsidedistrict"));

	private String code;
	private String description;
	
	public static GeographicArea getGeographicAreaByCode(String code){
		GeographicArea[] geographicAreaList=GeographicArea.values();
		for (int i = 0; i < geographicAreaList.length; i++) {
			if(geographicAreaList[i].getCode().equals(code)){
				return geographicAreaList[i];
			}
		}
		return null;
	}
	
	private GeographicArea(String code, String description){
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
