package org.isf.utils.jobjects;

public class ParameterType {
	public ParameterType(){}
	public enum State
	 {		
		GENERAL ("1", "angal.parameters.type.general"),
		LOCAL ("2", "angal.parameters.type.local");						
		
		String code;
		String label;
			
		private State(String code, String label){
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
}
