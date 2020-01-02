package org.isf.priceslist.model;

import org.isf.generaldata.MessageBundle;

public enum ItemGroup {
	
	MEDICAL ("MED", MessageBundle.getMessage("angal.priceslist.medicals")),
	EXAM ("EXA", MessageBundle.getMessage("angal.priceslist.exams")),
	OPERATION ("OPE", MessageBundle.getMessage("angal.priceslist.operations")),
	OTHER ("OTH", MessageBundle.getMessage("angal.priceslist.others"));
	
	String code;
	String label;
	
	private ItemGroup(String code, String label){
		this.code=code;
		this.label=label;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	

}
