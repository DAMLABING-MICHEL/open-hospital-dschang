package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.isf.parameters.manager.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryState {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//private final String FILE_PROPERTIES = "inventorystate.properties";
	private final String FILE_PROPERTIES = Param.string("INVENTORYSTATE_PROPERTIES");
	
	private static InventoryState mySingleData;
	private Properties p;

    private InventoryState() {
    	try	{
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + FILE_PROPERTIES));
			logger.info("File materials.properties loaded. ");
			
		} catch (Exception e) {//no file
    		logger.error(">> " + FILE_PROPERTIES + " file not found.");
			System.exit(1);
		}
    }
    
 
    public static InventoryState getInventoryState() {
        if (mySingleData == null){ 
        	mySingleData = new InventoryState();        	
        }
        return mySingleData;
    }

	public Properties getP() {
		return p;
	}

	public void setP(Properties p) {
		this.p = p;
	}
    
}

