package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.isf.parameters.manager.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaterialsExamList {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//private final String FILE_PROPERTIES = "materials.properties";
	private final String FILE_PROPERTIES = Param.string("MATERIALS_PROPERTIES");	
	private static MaterialsExamList mySingleData;
	private Properties p;

    private MaterialsExamList() {
    	try	{
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + FILE_PROPERTIES));
			logger.info("File materials.properties loaded. ");
			
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
//    private int myGetProperty(String property, int defaultValue) {
//    	int value;
//		try {
//			value = Integer.parseInt(p.getProperty(property));
//		} catch (Exception e) {
//			logger.warn(">> " + property + " property not found: default is " + defaultValue);
//			return defaultValue;
//		}
//		return value;
//	}

	/**
	 * 
	 * Method to retrieve a boolean property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
//	private boolean myGetProperty(String property, boolean defaultValue) {
//		boolean value;
//		try {
//			value = p.getProperty(property).equalsIgnoreCase("YES");
//		} catch (Exception e) {
//			logger.warn(">> " + property + " property not found: default is " + defaultValue);
//			return defaultValue;
//		}
//		return value;
//	}

    public static MaterialsExamList getMaterialsExamList() {
        if (mySingleData == null){ 
        	mySingleData = new MaterialsExamList();        	
        }
        return mySingleData;
    }

	public Properties getP() {
		return p;
	}

	public void setP(Properties p) {
		this.p = p;
	}
    
    /**
	 * 
	 * Method to retrieve a string property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
//	private String myGetProperty(String property, String defaultValue) {
//		String value;
//		value = p.getProperty(property);
//		if (value == null) {
//			logger.warn(">> " + property + " property not found: default is " + defaultValue);
//			return defaultValue;
//		}
//		return value;
//	}

}

