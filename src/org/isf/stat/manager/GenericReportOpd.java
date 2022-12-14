/*
 * Created on 15/giu/08
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.isf.stat.manager;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.parameters.manager.Param;
import org.isf.utils.db.DbSingleConn;

public class GenericReportOpd {

	public GenericReportOpd(int opdID, int patID, String jasperFileName) {
		try{
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("hospital", hosp.getDescription()); //"St.Luke Hospital");
			parameters.put("opdID", String.valueOf(opdID)); // real param
			parameters.put("patientID", String.valueOf(patID)); // real param
			parameters.put("imageFolder", Param.string("PHOTOSDIR")+File.separator);
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
		
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			//System.out.println("Clinical sheet jasper report name:"+sbFilename.toString());
			
			File jasperFile = new File(sbFilename.toString());
			
			Connection conn = DbSingleConn.getConnection();
			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			if (Param.bool("INTERNALVIEWER"))
				JasperViewer.viewReport(jasperPrint,false);
			else { 
				try{
					String PDFfile = "rpt/PDF/"+jasperFileName + "_" + String.valueOf(opdID)+".pdf";
					JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
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
