/*
 * Created on 15/giu/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.isf.stat.manager;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.parameters.manager.Param;
import org.isf.serviceprinting.manager.PrintReceipt;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.jobjects.InventoryState;
import org.isf.ward.manager.WardBrowserManager;

public class GenericReportInventory {
	
//	public GenericReportInventory(MedicalInventory inventory,  String jasperFileName) {
//		new GenericReportInventory(inventory, jasperFileName, true, boolean printQtyReal);
//	}

	public GenericReportInventory(MedicalInventory MedicalInventory, String jasperFileName, boolean show, int printQtyReal) {
		try {
			//////////////
			//JRBeanCollectionDataSource  collection = new JRBeanCollectionDataSource (new ArrayList<MedicalInventoryRow>());
			//////////////
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
            WardBrowserManager wManager = new WardBrowserManager();
			String state = "";
            for (InventoryState.State currentState : InventoryState.State.values()) {
				if(MedicalInventory.getState().equals(currentState.getCode())){
					state = MessageBundle.getMessage(currentState.getLabel());
					break;
				}
			}
            parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("inventoryID", String.valueOf(MedicalInventory.getId())); 
			parameters.put("inventoryDate", String.valueOf(formatDateTime(MedicalInventory.getInventoryDate()))); 
			parameters.put("inventoryReference", MedicalInventory.getInventoryReference()); 
			parameters.put("inventoryState", !state.equals("")?state:MedicalInventory.getState()); 
			parameters.put("inventoryWard", MedicalInventory.getWard()!=null? wManager.getWard(MedicalInventory.getWard()).getDescription():""); 
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
			parameters.put("printQtyReal", printQtyReal); 

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
			//JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, collection);
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(MedicalInventory.getId()) + ".pdf";
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");  
		return format.format(time.getTime());
	}
}
