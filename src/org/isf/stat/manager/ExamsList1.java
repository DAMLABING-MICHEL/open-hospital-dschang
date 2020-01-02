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

public class ExamsList1 {

	public ExamsList1() {
		try{
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospMan = new HospitalBrowsingManager();
			Hospital hospital = hospMan.getHospital();
		
			parameters.put("hospital", hospital.getDescription());
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
		
			String jasperFileName = "examslist";
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			
			StringBuilder pdfFilename = new StringBuilder();
			pdfFilename.append("rpt");
			pdfFilename.append(File.separator);
			pdfFilename.append("PDF");
			pdfFilename.append(File.separator);
			pdfFilename.append(jasperFileName);
			pdfFilename.append(".pdf");
			
			File jasperFile = new File(sbFilename.toString());
			
			Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFilename.toString());
			
			//mostra a video 
			if (Param.bool("INTERNALVIEWER"))
				JasperViewer.viewReport(jasperPrint,false);
			else { 
				try{
					Runtime rt = Runtime.getRuntime();
					rt.exec(Param.string("VIEWER") +" "+ pdfFilename.toString());
					
				} catch(Exception e){
					e.printStackTrace();
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
