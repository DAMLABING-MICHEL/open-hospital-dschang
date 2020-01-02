package org.isf.opetype.model;

/**
 * Pure Model Exam : represents a disease type
 * 
 * @author Rick, Vero, Pupo
 *
 */
public class OperationType {
	 
    private String code;
    private String description;
    private String account;
    //private String expenseAccount;
    
    private volatile int hashCode = 0;
    
    
    public OperationType() {
    	super();
    }
    
    /**
     * @param aCode
     * @param aDescription
     */        
    public OperationType(String aCode, String aDescription) {
        super();
        this.code = aCode;
        this.description = aDescription;
    }
    public OperationType(String aCode, String aDescription, String account) {
        super();
        this.code = aCode;
        this.description = aDescription;
        this.account = account;
        //this.expenseAccount = expense_account;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String aCode) {
        this.code = aCode;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String aDescription) {
        this.description = aDescription;
    }
    
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
			return true;
		}
		
		if (!(anObject instanceof OperationType)) {
			return false;
		}
		
		OperationType operationType = (OperationType)anObject;
		boolean equals= (this.getCode().equals(operationType.getCode()) &&
				this.getDescription().equalsIgnoreCase(operationType.getDescription()) 
				
//				&& this.getAccount()!=null ? this.getAccount().equalsIgnoreCase(operationType.getAccount())
//						:operationType.getAccount()==null
//						
//				&& this.getExpenseAccount()!=null ? this.getExpenseAccount().equalsIgnoreCase(operationType.getExpenseAccount())
//						:operationType.getExpenseAccount()==null
				
				);
		
		return equals;
    }
    
    @Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        c = m * c + ((code == null) ? 0 : code.hashCode());
	        c = m * c + ((description == null) ? 0 : description.hashCode());
	        c = m * c + ((account == null) ? 0 : account.hashCode());
	        //c = m * c + ((expenseAccount == null) ? 0 : expenseAccount.hashCode());
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}

    public String toString() {
        return this.description;
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
//
//	public void setExpenseAccount(String expenseAccount) {
//		this.expenseAccount = expenseAccount;
//	}

}


