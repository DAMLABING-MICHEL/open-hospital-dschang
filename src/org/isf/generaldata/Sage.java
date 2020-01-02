package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.isf.parameters.manager.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sage {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//private final String FILE_PROPERTIES = "sage.properties";
	private final String FILE_PROPERTIES = Param.string("SAGE_PROPERTIES");

	public static boolean ENABLE_SAGE_INTEGRATION;
    public static String  SUPPLIER_GENERAL_ACCOUNT;
    public static String  CUSTOMER_GENERAL_ACCOUNT;
    public static String  CASH_ACCOUNT;
    public static String  INCOME_ACCOUNT;
    public static String  EXPENSE_ACCOUNT;
    public static String  JOURNAL_BUY_CODE;
    public static String  JOURNAL_PAID_CODE;
    public static String  JOURNAL_CASHDESK_CODE;
    public static String  FILE_PAID_NAME;
    public static String  FILE_CASHDESK_NAME;
    public static String  PAID_PREFIX;
    public static String  CASHDESK_PREFIX;
    
    public static String  MEDICALSALESACCOUNT;
    public static String  OTHERSALESACCOUNT;
    public static String  EXAMSALESACCOUNT;
    public static String  OPERATIONSALESACCOUNT;
    
    public static String  MEDICALEXPENSEACCOUNT;
    public static String  OTHEREXPENSEACCOUNT;
    public static String  EXAMEXPENSEACCOUNT;
    public static String  OPERATIONEXPENSEACCOUNT;
     
    public static boolean DEFAULT_ENABLE_SAGE_INTEGRATION = false;
    public static String  DEFAULT_SUPPLIER_GENERAL_ACCOUNT = "40100000";
    public static String  DEFAULT_CUSTOMER_GENERAL_ACCOUNT = "411";
    public static String  DEFAULT_CASH_ACCOUNT = "571110";
    public static String  DEFAULT_INCOME_ACCOUNT = "706102";
    public static String  DEFAULT_EXPENSE_ACCOUNT = "601100";
    public static String  DEFAULT_JOURNAL_BUY_CODE = "ACH";
    public static String  DEFAULT_JOURNAL_PAID_CODE = "VTE";
    public static String  DEFAULT_JOURNAL_CASHDESK_CODE = "CASH";
    public static String  DEFAULT_FILE_PAID_NAME = "exportvente";
    public static String  DEFAULT_FILE_CASHDESK_NAME = "exportcaisse";
    public static String  DEFAULT_PAID_PREFIX = "V";
    public static String  DEFAULT_CASHDESK_PREFIX = "C";
    
    public static String  DEFAULT_MEDICALSALESACCOUNT = "700001";
    public static String  DEFAULT_OTHERSALESACCOUNT = "700002";
    public static String  DEFAULT_EXAMSALESACCOUNT = "700003";
    public static String  DEFAULT_OPERATIONSALESACCOUNT = "700004";
    
    public static String  DEFAULT_MEDICALEXPENSEACCOUNT = "600001";
    public static String  DEFAULT_OTHEREXPENSEACCOUNT = "600002";
    public static String  DEFAULT_EXAMEXPENSEACCOUNT = "600003";
    public static String  DEFAULT_OPERATIONEXPENSEACCOUNT = "600004";
    
    private static Sage mySingleData;
	private Properties p;

    private Sage() {
    	try	{
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + FILE_PROPERTIES));
			logger.info("File sage.properties loaded. ");
			
			
			CASH_ACCOUNT = myGetProperty("CASH_ACCOUNT", DEFAULT_CASH_ACCOUNT);
			INCOME_ACCOUNT = myGetProperty("INCOME_ACCOUNT", DEFAULT_INCOME_ACCOUNT);
			EXPENSE_ACCOUNT = myGetProperty("EXPENSE_ACCOUNT", DEFAULT_EXPENSE_ACCOUNT);
			JOURNAL_BUY_CODE = myGetProperty("JOURNAL_BUY_CODE", DEFAULT_JOURNAL_BUY_CODE);
			JOURNAL_PAID_CODE = myGetProperty("JOURNAL_PAID_CODE", DEFAULT_JOURNAL_PAID_CODE);
			OTHEREXPENSEACCOUNT = myGetProperty("OTHEREXPENSEACCOUNT", DEFAULT_OTHEREXPENSEACCOUNT);
			JOURNAL_CASHDESK_CODE = myGetProperty("JOURNAL_CASHDESK_CODE", DEFAULT_JOURNAL_CASHDESK_CODE);
			ENABLE_SAGE_INTEGRATION = myGetProperty("ENABLE_SAGE_INTEGRATION", DEFAULT_ENABLE_SAGE_INTEGRATION);
			SUPPLIER_GENERAL_ACCOUNT = myGetProperty("SUPPLIER_GENERAL_ACCOUNT", DEFAULT_SUPPLIER_GENERAL_ACCOUNT);
			CUSTOMER_GENERAL_ACCOUNT = myGetProperty("CUSTOMER_GENERAL_ACCOUNT", DEFAULT_CUSTOMER_GENERAL_ACCOUNT);
			OPERATIONEXPENSEACCOUNT = myGetProperty("OPERATIONEXPENSEACCOUNT", DEFAULT_OPERATIONEXPENSEACCOUNT);
			OPERATIONSALESACCOUNT = myGetProperty("OPERATIONSALESACCOUNT", DEFAULT_OPERATIONSALESACCOUNT);
			MEDICALEXPENSEACCOUNT = myGetProperty("MEDICALEXPENSEACCOUNT", DEFAULT_MEDICALEXPENSEACCOUNT);
			MEDICALSALESACCOUNT = myGetProperty("MEDICALSALESACCOUNT", DEFAULT_MEDICALSALESACCOUNT);
			EXAMEXPENSEACCOUNT = myGetProperty("EXAMEXPENSEACCOUNT", DEFAULT_EXAMEXPENSEACCOUNT);
			FILE_CASHDESK_NAME = myGetProperty("FILE_CASHDESK_NAME", DEFAULT_FILE_CASHDESK_NAME);
			OTHERSALESACCOUNT = myGetProperty("OTHERSALESACCOUNT", DEFAULT_OTHERSALESACCOUNT);
			EXAMSALESACCOUNT = myGetProperty("EXAMSALESACCOUNT", DEFAULT_EXAMSALESACCOUNT);
			CASHDESK_PREFIX = myGetProperty("CASHDESK_PREFIX", DEFAULT_CASHDESK_PREFIX);
			FILE_PAID_NAME = myGetProperty("FILE_PAID_NAME", DEFAULT_FILE_PAID_NAME);
			PAID_PREFIX = myGetProperty("PAID_PREFIX", DEFAULT_PAID_PREFIX);
			
			
			
			
			
		} catch (Exception e) {//no file
    		logger.error(">> " + FILE_PROPERTIES + " file not found.");
			System.exit(1);
		}
    }
    
    /**
     * Method to retrieve an integer property
     * 
     * @param property
     * @param defaultValue
     * @return
     */
    private int myGetProperty(String property, int defaultValue) {
    	int value;
		try {
			value = Integer.parseInt(p.getProperty(property));
		} catch (Exception e) {
			logger.warn(">> " + property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}

	/**
	 * 
	 * Method to retrieve a boolean property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private boolean myGetProperty(String property, boolean defaultValue) {
		boolean value;
		try {
			value = p.getProperty(property).equalsIgnoreCase("YES");
		} catch (Exception e) {
			logger.warn(">> " + property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}

    public static Sage getSage() {
        if (mySingleData == null){ 
        	mySingleData = new Sage();        	
        }
        return mySingleData;
    }
    
    /**
	 * 
	 * Method to retrieve a string property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private String myGetProperty(String property, String defaultValue) {
		String value;
		value = p.getProperty(property);
		if (value == null) {
			logger.warn(">> " + property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}

}

