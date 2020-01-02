package org.isf.stat.manager;

import java.awt.Desktop;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.jasperreports.engine.JRQuery;
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
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.excel.ExcelExporter;

/*--------------------------------------------------------
 * GenericReportLauncerMY - lancia tutti i report che come parametri hanno
 * 							anno e mese
 * 							la classe prevede l'inizializzazione attraverso 
 *                          anno, mese, nome del report (senza .jasper)
 *---------------------------------------------------------
 * modification history
 * 11/11/2006 - prima versione
 *
 *-----------------------------------------------------------------*/

public class GenericReportMY {

	public GenericReportMY(Integer month, Integer year, String jasperFileName, boolean toCSV) {
		try{
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("year", String.valueOf(year)); // real param
			parameters.put("month", String.valueOf(month)); // real param
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
		
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());

			Connection conn = DbSingleConn.getConnection();
			
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			if (toCSV) {
				JRQuery query = jasperReport.getMainDataset().getQuery();

				String queryString = query.getText();

				queryString = queryString.replace("$P{anno}", "!'" + String.valueOf(year) + "'");
				queryString = queryString.replace("$P{mese}", "'" + String.valueOf(month) + "'");

				DbQueryLogger dbQuery = new DbQueryLogger();
				ResultSet resultSet = dbQuery.getData(queryString, true);
				JFileChooser fcExcel = new JFileChooser();
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("CSV (*.csv)","csv");
				fcExcel.setFileFilter(excelFilter);
				fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);  
				
				int iRetVal = fcExcel.showSaveDialog(null);
				if(iRetVal == JFileChooser.APPROVE_OPTION)
				{ 
					File exportFile = fcExcel.getSelectedFile();
					if (!exportFile.getName().endsWith("csv")) exportFile = new File(exportFile.getAbsoluteFile() + ".csv");
					
					ExcelExporter xlsExport = new ExcelExporter();
					xlsExport.exportResultsetToCSV(resultSet, exportFile);
				}
				dbQuery.releaseConnection();
			} else {
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
				String PDFfile = "rpt/PDF/"+jasperFileName+"_"+year+"_"+month+".pdf";
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public GenericReportMY(Integer month, Integer year, String jasperFileName, boolean toCSV, String report_prefix) {
		if(report_prefix.equals("OH005")) 
			GenericReportMY_OH005(month, year, jasperFileName);
		if(report_prefix.equals("OH006")) 
			GenericReportMY_OH006(month, year, jasperFileName);
	}
	public void GenericReportMY_OH005(Integer month, Integer year, String jasperFileName) {
		try{
						
			String	queryString ="select (case when OPD_NEW_PAT='N' then 'new attendance' else 'reattendance' end) as CATEGORY, " +
				" (case when OPD_AGE <5 then '0-4 Years' else ' >= 5 ' end) as AGE, opd_sex 'SEX', count(*) as COUNT from OPD " +
				" where month(OPD_DATE_VIS) = $P{month} and year(OPD_DATE_VIS)=$P{year} group by case when OPD_NEW_PAT='N' " + 
				" then 'new attendance' else 'reattendance' end, (case when OPD_AGE <5 then '0-4 Years' else ' >= 5 ' end), opd_sex " +
				" union select 'total' as category, (case when OPD_AGE <5 then '0-4 Years' else ' >= 5 ' end) as AGE, " +
				" opd_sex 'SEX', count(*) as COUNT from OPD where month(OPD_DATE_VIS) = $P{month} and year(OPD_DATE_VIS)=$P{year} " +
				" group by 'total', (case when OPD_AGE <5 then '0-4 Years' else ' >= 5 ' end), opd_sex order by 1,2,3 desc";
			
			queryString = queryString.replace("$P{month}", "'" + month + "'");
			queryString = queryString.replace("$P{year}", "'" + year + "'");
			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);
			JFileChooser fcExcel = new JFileChooser();
			FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("XLSX (*.xlsx)","xlsx");
			fcExcel.setFileFilter(excelFilter);
			fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY); 
			
			int iRetVal = fcExcel.showSaveDialog(null);
			if(iRetVal == JFileChooser.APPROVE_OPTION)
			{
				File exportFile = fcExcel.getSelectedFile();
				if (!exportFile.getName().endsWith("xls")) 
					exportFile = new File(exportFile.getAbsoluteFile() + ".xls");
				
				ExcelExporter xlsExport = new ExcelExporter();
				xlsExport.exportResultsetToEXCEL_OH005(resultSet, exportFile);
				Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
			}
			dbQuery.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void GenericReportMY_OH006(Integer month, Integer year, String jasperFileName) {
		try{
						
			String queryString ="SELECT DCL_DESC, DIS_DESC, (case when OPD_AGE <5 then '0-4 Years' else ' >= 5 ' end) AS AGE, " +
					"OPD_SEX, count(*) as COUNT FROM (select OPD_DIS_ID_A, OPD_AGE, OPD_SEX FROM OPD where OPD_NEW_PAT = 'N' " +
					"and month(OPD_DATE_VIS) = $P{month} and year(OPD_DATE_VIS)=$P{year} union all select OPD_DIS_ID_A_2 as OPD_DIS_ID_A, " +
					"OPD_AGE, OPD_SEX FROM OPD where not OPD_DIS_ID_A_2 is null and OPD_NEW_PAT = 'N' and month(OPD_DATE_VIS) = $P{month} "+
					"and year(OPD_DATE_VIS)=$P{year} union all select OPD_DIS_ID_A_3 as OPD_DIS_ID_A, OPD_AGE, OPD_SEX FROM OPD where not "+
					"OPD_DIS_ID_A_3 is null and OPD_NEW_PAT = 'N' and month(OPD_DATE_VIS) = $P{month} and year(OPD_DATE_VIS)=$P{year} " +
					") as OPDALLDIAG, DISEASE , DISEASETYPE where  OPDALLDIAG.OPD_DIS_ID_A = DIS_ID_A and DIS_DCL_ID_A = DCL_ID_A " +
					"group by DCL_DESC , DIS_DESC , (case when OPD_AGE <5 then '0-4 Years' else ' >= 5 ' end) , OPD_SEX order by 1,2,3,4 desc";
			
			queryString = queryString.replace("$P{month}", "'" + month + "'");
			queryString = queryString.replace("$P{year}", "'" + year + "'");
			DbQueryLogger dbQuery = new DbQueryLogger();
			ResultSet resultSet = dbQuery.getData(queryString, true);
			JFileChooser fcExcel = new JFileChooser();
			FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("XLSX (*.xlsx)","xlsx");
			fcExcel.setFileFilter(excelFilter);
			fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY); 
			
			int iRetVal = fcExcel.showSaveDialog(null);
			if(iRetVal == JFileChooser.APPROVE_OPTION)
			{
				File exportFile = fcExcel.getSelectedFile();
				if (!exportFile.getName().endsWith("xls")) 
					exportFile = new File(exportFile.getAbsoluteFile() + ".xls");				
				ExcelExporter xlsExport = new ExcelExporter();
				xlsExport.exportResultsetToEXCEL_OH006(resultSet, exportFile);
				Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
			}
			dbQuery.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
