/**
 * @(#) ExamType.java
 * 20-jan-2006
 */
package org.isf.exatype.model;

import org.isf.medtype.model.MedicalType;

/**
 * Pure Model ExamType (type of exams)
 * 
 * @author bob
 */
public class ExamType {

	private String code;

	private String description;
	
	private String account;
	 
	//private String expenseAccount;

	public ExamType(){
		
	}
	public ExamType(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	public ExamType(String code, String description, String account) {
		super();
		this.code = code;
		this.description = description;
		this.account = account;
		//this.expenseAccount = expense_account;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public boolean equals(Object anObject) {
		return (anObject == null) || !(anObject instanceof ExamType) ? false
				: (getCode().equals(((ExamType) anObject).getCode())
						&& getDescription().equalsIgnoreCase(
								((ExamType) anObject).getDescription())
						
//						&& getAccount()!=null? getAccount().equalsIgnoreCase(((ExamType) anObject).getAccount()):
//							((ExamType) anObject).getAccount()==null
						
//						&& getExpenseAccount()!=null? getExpenseAccount().equalsIgnoreCase(((ExamType) anObject).getExpenseAccount()):
//							((ExamType) anObject).getExpenseAccount()==null
				);
	}

	public String toString() {
		return getDescription();
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
//	public String getExpenseAccount() {
//		return expenseAccount;
//	}
//	public void setExpenseAccount(String expense_account) {
//		this.expenseAccount = expense_account;
//	}

}
