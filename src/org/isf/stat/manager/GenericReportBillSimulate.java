/*
 * Created on 15/giu/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.isf.stat.manager;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.serviceprinting.manager.PrintReceipt;
import org.isf.supplier.model.SimulateBill;
import org.isf.utils.db.DbSingleConn;
import org.joda.time.DateTime;

public class GenericReportBillSimulate {
	
	public GenericReportBillSimulate(Integer billID, ArrayList<SimulateBill>simulatebillitems, Double TOTAL_GLOBAL, String PAT_NAME, String jasperFileName) {
		new GenericReportBillSimulate(billID, simulatebillitems, TOTAL_GLOBAL, PAT_NAME, jasperFileName, true, true);
	}
	
	public GenericReportBillSimulate(){
		
	}
	public GenericReportBillSimulate(Integer billID, ArrayList<SimulateBill>simulatebillitems, Double TOTAL_GLOBAL, String PAT_NAME, String jasperFileName, boolean show, boolean askForPrint) {
		try {
			
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			Locale locale = new Locale("en", "US");
			parameters.put(JRParameter.REPORT_LOCALE, locale);
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("billID", String.valueOf(billID)); // real param
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
			parameters.put("TOTAL_GLOBAL", TOTAL_GLOBAL);
			parameters.put("PAT_NAME", PAT_NAME);
			parameters.put("CASHIER", MainMenu.getCurrentUser().getUserName());
			
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			File jasperFile = new File(sbFilename.toString());
			JRBeanCollectionDataSource beanSimulateDataSource = new JRBeanCollectionDataSource(simulatebillitems);
			//Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanSimulateDataSource);
			String PDFfile = "rpt/PDF/" + jasperFileName + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
			JasperViewer.viewReport(jasperPrint, false);
			
			if (show) {
				if (Param.bool("INTERNALVIEWER")) {	
					JasperViewer.viewReport(jasperPrint, false);
				} else {
					try {
						Runtime rt = Runtime.getRuntime();
						rt.exec(Param.string("VIEWER") + " " + PDFfile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (Param.bool("RECEIPTPRINTER")) {				
				sbFilename = new StringBuilder();
				sbFilename.append("rpt");
				sbFilename.append(File.separator);
				sbFilename.append(jasperFileName);
				sbFilename.append("Txt");
				sbFilename.append(".jasper");
				//System.out.println("Jasper Report Name:"+sbFilename.toString());

				jasperFile = new File(sbFilename.toString());
				JRBeanCollectionDataSource beanSimulateDataSourceTxt = new JRBeanCollectionDataSource(simulatebillitems);
				jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanSimulateDataSourceTxt);
				
				String TXTfile = "rpt/PDF/" + jasperFileName + ".txt";
				
				int print = JOptionPane.OK_OPTION;
				if (askForPrint) {
					print = JOptionPane.showConfirmDialog(null, MessageBundle.getMessage("angal.genericreportbill.doyouwanttoprintreceipt"));
				}
				if (print == JOptionPane.OK_OPTION) {
					new PrintReceipt(jasperPrint, TXTfile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
