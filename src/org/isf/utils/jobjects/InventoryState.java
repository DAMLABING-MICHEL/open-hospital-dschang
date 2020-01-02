package org.isf.utils.jobjects;

public class InventoryState {
	public InventoryState(){}
	public enum State
	 {		
		PROGRESS ("1", "angalinventorystateinprogress"),
		CANCELED ("2", "angalinventorystatecanceled"),
		VALIDATE ("3", "angalinventorystatevalidate");
		
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
