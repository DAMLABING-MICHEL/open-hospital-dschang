package org.isf.menu.gui;

import java.io.File;

import javax.swing.JFrame;

import org.apache.log4j.PropertyConfigurator;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.Version;
import org.isf.parameters.manager.Param;
import org.isf.utils.licence.LicenceUtils;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Menu {

	private static Logger logger = LoggerFactory.getLogger(Menu.class);

	private final static float MIN_JAVA_VERSION = (float) 1.6;

	/**
	 * Create the GUI and show it.
	 * 
	 */
	private static void createAndShowGUI() {
		logger = LoggerFactory.getLogger(Menu.class);
		logger.info("\n\n=====================\nStarting OpenHospital\n=====================\n");
		checkOHVersion();
		checkJavaVersion();
		JFrame.setDefaultLookAndFeelDecorated(false);
		new SplashWindow3("rsc" + File.separator + "images" + File.separator + "Splash.jpg", null, 3000);
	}

	private static void checkOHVersion() {
		Version.getVersion();
		logger.info("OpenHospital version " + Version.VER_MAJOR + "." + Version.VER_MINOR + "." + Version.VER_RELEASE);
	}

	public static void checkJavaVersion() {
		String version = System.getProperty("java.version");
		logger.info("Java version " + version);
		Float f = Float.valueOf(version.substring(0, 3));
		if (f.floatValue() < MIN_JAVA_VERSION) {
			logger.error("Java version " + MIN_JAVA_VERSION + " or higher is required.");
			logger.info("\n\n=====================\n OpenHospital closed \n=====================\n");
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		
		PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Param();
				GeneralData.getGeneralData();
				if (!Param.bool("DEFAULTSKIN")) {
					SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				}
				createAndShowGUI();
			}
		});
	}
}
