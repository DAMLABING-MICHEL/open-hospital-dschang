/**
 * @(#) TipoFarmaco.java
 * 11-dec-2005
 * 14-jan-2006
 */

package org.isf.medtype.model;

/**
 * Defines a medical type: D: k: S: R:
 * @author  bob
 */
public class MedicalType {
	/**
	 * Code
	 */
	private String code;

	/**
	 * Description
	 */
	private String description;
	
	/**
	 * Account
	 */
	private String account;
	
	/**
	 * Expense Account
	 */
	private String expenseAccount;

	public MedicalType() {
		super();
		this.code = "";
		this.description = " ";
		this.account = "";
	}
	
	public MedicalType(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	
	public MedicalType(String code, String description, String account, String expense_account) {
		super();
		this.code = code;
		this.description = description;
		this.account = account;
		this.expenseAccount = expense_account;
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

	public boolean equals(Object anObject) {
		return (anObject == null) || !(anObject instanceof MedicalType) ? false
				: (getCode().equalsIgnoreCase(((MedicalType) anObject).getCode()) 
						&& (getDescription().equalsIgnoreCase(((MedicalType) anObject).getDescription()))
						&& (getAccount()!=null? getAccount().equalsIgnoreCase(((MedicalType) anObject).getAccount()):
							((MedicalType) anObject).getAccount()==null)
						&& (getExpenseAccount()!=null? getExpenseAccount().equalsIgnoreCase(((MedicalType) anObject).getExpenseAccount()):
							((MedicalType) anObject).getExpenseAccount()==null)		
				  );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((expenseAccount == null) ? 0 : expenseAccount.hashCode());
		return result;
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

	public String getExpenseAccount() {
		return expenseAccount;
	}

	public void setExpenseAccount(String expense_account) {
		this.expenseAccount = expense_account;
	}
	
	
}
