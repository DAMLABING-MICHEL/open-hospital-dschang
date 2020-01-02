package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GeneralData {

	private static GeneralData mySingleData;
	private Properties p;	

	private GeneralData() {
		
		MessageBundle.initialize();

	}

	public static GeneralData getGeneralData() {
		if (mySingleData == null) {
			mySingleData = new GeneralData();
		}
		return mySingleData;
	}
}

