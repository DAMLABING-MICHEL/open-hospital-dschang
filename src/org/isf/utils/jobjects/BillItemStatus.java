package org.isf.utils.jobjects;

public class BillItemStatus {
	public BillItemStatus(){}
	public enum Status
	 {		
		EXPORTED ("exported", ""),
		NOTEXPORTED ("notexported", "");
				
		String code;
		String label;
			
		private Status(String code, String label){
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
