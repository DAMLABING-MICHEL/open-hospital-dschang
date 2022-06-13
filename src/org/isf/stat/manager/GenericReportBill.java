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
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.menu.model.User;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.serviceprinting.manager.PrintReceipt;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.excel.ExcelExporter;

public class GenericReportBill {
	
	public GenericReportBill(Integer billID, String jasperFileName) {
		new GenericReportBill(billID, jasperFileName, true, true);
	}
	
	public GenericReportBill(){
		
	}

	public GenericReportBill(Integer billID, String jasperFileName, boolean show, boolean askForPrint) {
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

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());

			Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			System.out.println("name "+jasperFileName+ "  billid "+String.valueOf(billID));
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(billID) + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);

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
				
				jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
				
				String TXTfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(billID) + ".txt";
				
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
	
	public GenericReportBill(Integer billID, String jasperFileName, Patient patient, ArrayList<Integer> billListId, String dataFrom, String dateTo, boolean show, boolean askForPrint) {
		try {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();

			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("billID", String.valueOf(billID)); // real param
			parameters.put("collectionbillsId", billListId); // real param
			//parameters.put("fromDate", dataFrom);
			//parameters.put("toDate", dateTo);
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());

			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			// System.out.println("Jasper Report Name:"+sbFilename.toString());

			File jasperFile = new File(sbFilename.toString());

			Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(billID) + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);

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
				
				jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
				jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
				
				String TXTfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(billID) + ".txt";
				
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
	//////////////print grante bills
	public  GenericReportBill(String fromDate, String toDate, String jasperFileName, User garante) {
		try{
	        HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("fromdate", fromDate + ""); // real param
			parameters.put("todate", toDate + ""); // real param
			parameters.put("id_garante", garante.getUserName()); 
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
			parameters.put("name_garante", garante.getName()+" "+garante.getSurname());
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());
	
			Connection conn = DbSingleConn.getConnection();
			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			String PDFfile = "rpt/PDF/"+jasperFileName+".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
			if (Param.bool("INTERNALVIEWER"))
					JasperViewer.viewReport(jasperPrint,false);
			else { 
				try{
					Runtime rt = Runtime.getRuntime();
					rt.exec(Param.string("VIEWER") +" "+ PDFfile);	
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
