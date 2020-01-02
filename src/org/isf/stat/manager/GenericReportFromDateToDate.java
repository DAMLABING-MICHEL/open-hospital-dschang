package org.isf.stat.manager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.BillItems;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.lab.manager.LabManager;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.parameters.manager.Param;
import org.isf.patvac.manager.PatVacManager;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.manager.PregnancyDeliveryManager;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.BillItemReportBean;
import org.isf.ward.manager.WardBrowserManager;

/*--------------------------------------------------------
 * GenericReportLauncer2Dates
 *  - lancia tutti i report che come parametri hanno "da data" "a data"
 * 	- la classe prevede l'inizializzazione attraverso 
 *    dadata, adata, nome del report (senza .jasper)
 *---------------------------------------------------------
 * modification history
 * 09/06/2007 - prima versione
 *
 *-----------------------------------------------------------------*/
	public class GenericReportFromDateToDate {
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, boolean toCSV) {
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
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
				parameters.put("phone_lenght", Integer.parseInt(Param.string("PHONELENGTH")));
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

					queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
					queryString = queryString.replace("$P{todate}", "'" + toDate + "'");

					DbQueryLogger dbQuery = new DbQueryLogger();
					ResultSet resultSet = dbQuery.getData(queryString, true);
					JFileChooser fcExcel = new JFileChooser();
					//FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("CSV (*.csv)","csv");
					FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("XLSX (*.xlsx)","xlsx");
					fcExcel.setFileFilter(excelFilter);
					fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);  
					
					int iRetVal = fcExcel.showSaveDialog(null);
					if(iRetVal == JFileChooser.APPROVE_OPTION)
					{
						File exportFile = fcExcel.getSelectedFile();
						if (!exportFile.getName().endsWith("xls")) exportFile = new File(exportFile.getAbsoluteFile() + ".xls");
						
						ExcelExporter xlsExport = new ExcelExporter();
						xlsExport.exportResultsetToCSV(resultSet, exportFile);
						Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
					}
					dbQuery.releaseConnection();
					
				} else {
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
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, boolean toCSV,String fromDateShort, String toDateShort) {
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
				parameters.put("fromdateshort", fromDateShort + ""); // real param
				parameters.put("todateshort", toDateShort + ""); // real param
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

					queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
					queryString = queryString.replace("$P{todate}", "'" + toDate + "'");

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
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, boolean toCSV, String state, String state2, String code_title) {
			try{
				
				
		        HashMap<String, Object> parameters = new HashMap<String, Object>();
				HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
				Hospital hosp = hospManager.getHospital();
				
				parameters.put("Hospital", hosp.getDescription());
				parameters.put("Address", hosp.getAddress());
				parameters.put("City", hosp.getCity());
				parameters.put("Email", hosp.getEmail());
				parameters.put("Telephone", hosp.getTelephone());
				parameters.put("fromdate", fromDate); // real param
				parameters.put("todate", toDate); // real param
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
				parameters.put("state", state);
				parameters.put("state2", state2);
				parameters.put("title", code_title);
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

					queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
					queryString = queryString.replace("$P{todate}", "'" + toDate + "'");
					queryString = queryString.replace("$P{state}", "'" + state + "'");
					queryString = queryString.replace("$P{state2}", "'" + state2 + "'");
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
						xlsExport.exportResultsetToEXCEL(resultSet, exportFile);
						Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
					}
					dbQuery.releaseConnection();
					
				} else {
					System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
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
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, String report_prefix) {
			
			if(report_prefix.equals("OH001")){
				GenericReportFromDateToDate_OH001(fromDate, toDate, jasperFileName);
				return;
			}
			if(report_prefix.equals("OH002")){
				GenericReportFromDateToDate_OH002(fromDate, toDate, jasperFileName);
				return;
			}
			if(report_prefix.equals("OH004")){
				GenericReportFromDateToDate_OH004(fromDate, toDate, jasperFileName, "C");
				return;
			}
			if(report_prefix.equals("OH004_1")){
				GenericReportFromDateToDate_OH004(fromDate, toDate, jasperFileName, "CO");
				return;
			}
		}
		public  GenericReportFromDateToDate(String exam, String dateFrom,
				String dateTo, int resultFilter, String patientCode , String userCode, String jasperFileName, String prescribername, String patientname ,String paidStatus) {
			try{
		        HashMap<String, Object> parameters = new HashMap<String, Object>();
				HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
				Hospital hosp = hospManager.getHospital();				
				parameters.put("Hospital", hosp.getDescription());
				parameters.put("Address", hosp.getAddress());
				parameters.put("City", hosp.getCity());
				parameters.put("Email", hosp.getEmail());
				parameters.put("Telephone", hosp.getTelephone());
				parameters.put("fromdate", dateFrom + ""); // real param
				parameters.put("todate", dateTo + ""); // real param
				parameters.put("exam", exam);
				parameters.put("prescribername", prescribername);
				parameters.put("resultFilter", resultFilter);
				parameters.put("patientCode", ""+patientCode);
				parameters.put("patientname", ""+patientname);
				parameters.put("userCode", userCode);
				parameters.put("name", prescribername);	
				parameters.put("paidCode", paidStatus);	
				parameters.put("lab_auto_enabled", Param.bool("CREATELABORATORYAUTO")?"yes":"no");	
				
				
				
				
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());				
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
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, boolean toCSV, String optionTimeCase, String optionTimeCase1, Integer rangeAgeMin, Integer rangeAgeMax, String libRange, String caseTypeLib) {
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
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
				parameters.put("optionTimeCase", optionTimeCase);
				parameters.put("optionTimeCase1", optionTimeCase1);
				parameters.put("rangeAgeMin", rangeAgeMin);
				parameters.put("rangeAgeMax", rangeAgeMax);
				parameters.put("libRange", libRange);
				parameters.put("caseTypeLib", caseTypeLib);
			
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

					queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
					queryString = queryString.replace("$P{todate}", "'" + toDate + "'");

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
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, Integer year, String status, String code_title, boolean toCSV) {
			if(toCSV){ //EXCEL
				GenericReportFromDateToDate_OH004_3_EXCEL(fromDate, toDate, jasperFileName, year, status, code_title);
			}else{//CSVstatus
				GenericReportFromDateToDate_OH004_3_CVS(fromDate, toDate, jasperFileName, year, status, code_title);
			}
		}
		
		public void  GenericReportFromDateToDate_OH004_3_CVS(String fromDate, String toDate, String jasperFileName, Integer year, String status, String code_title) {
			try{
		        HashMap<String, Object> parameters = new HashMap<String, Object>();
				HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
				BillBrowserManager billManager = new BillBrowserManager();
				Hospital hosp = hospManager.getHospital();
				List<BillItemReportBean> collections = new ArrayList<BillItemReportBean>();
				collections = billManager.getTotalCountAmountByQuery12(year,status);
				
				Collections.sort(collections, new Comparator<BillItemReportBean>() {
					public int compare(BillItemReportBean o1, BillItemReportBean o2) {
						return o1.getBLI_ITEM_DESC().compareToIgnoreCase(o2.getBLI_ITEM_DESC());
					}					
				});
				
				Collections.sort(collections, new Comparator<BillItemReportBean>() {
					public int compare(BillItemReportBean o1, BillItemReportBean o2) {
						return o1.getBLI_ITEM_GROUP().compareToIgnoreCase(o2.getBLI_ITEM_GROUP());
					}					
				});
				
				JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(collections);
				parameters.put("Hospital", hosp.getDescription());
				parameters.put("Address", hosp.getAddress());
				parameters.put("City", hosp.getCity());
				parameters.put("Email", hosp.getEmail());
				parameters.put("Telephone", hosp.getTelephone());
				parameters.put("fromdate", fromDate + ""); 
				parameters.put("todate", toDate + ""); 
				
				parameters.put("code_title", code_title);
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
				StringBuilder sbFilename = new StringBuilder();
				sbFilename.append("rpt");
				sbFilename.append(File.separator);
				sbFilename.append(jasperFileName);
				sbFilename.append(".jasper");

				File jasperFile = new File(sbFilename.toString());
		
				JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
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
		
		public void GenericReportFromDateToDate_OH004_3_EXCEL(String fromDate, String toDate, String jasperFileName, Integer year, String status, String code_title) {
			try{
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
					xlsExport.GenericReportFromDateToDate_OH004_3_EXCEL(fromDate, toDate, jasperFileName, year, status, code_title, exportFile);
					Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
				}
								
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public  GenericReportFromDateToDate(String fromDate, String toDate, String jasperFileName, boolean toCSV, int year) {
			try{
		        HashMap<String, Object> parameters = new HashMap<String, Object>();
				HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
				Hospital hosp = hospManager.getHospital();
				OpdBrowserManager opdManager = new OpdBrowserManager();
				PregnancyCareManager cpnManager = new PregnancyCareManager();
				PatVacManager vacManager = new PatVacManager();
				AdmissionBrowserManager admManager = new AdmissionBrowserManager();
				PregnancyDeliveryManager pregDelManager = new PregnancyDeliveryManager();
				LabManager labManager = new LabManager();
				WardBrowserManager wardManager = new WardBrowserManager();
				OperationRowBrowserManager opemanager = new OperationRowBrowserManager();
				
				HashMap<String, Integer> dataOpd = opdManager.getCurativeOpd(year);
				HashMap<String, Integer> dataCpn = cpnManager.getCountCPN(year);
				HashMap<String, Integer> dataCps = opdManager.getCountCPS(year);
				//HashMap<String, Integer> dataVacBCG = vacManager.getCountSpecificVacc(year, 1);//BCG
				HashMap<String, Integer> dataAdm = admManager.getCountADM(year,"D");
				HashMap<String, Integer> dataPregDel = pregDelManager.getCountDelivery(year);
				HashMap<String, Integer> datalab = labManager.getCountLaboratory(year);
				HashMap<String, Integer> dataBed = wardManager.getCountAllBeds();
				
				
								
				parameters.put("Hospital", hosp.getDescription());
				parameters.put("Address", hosp.getAddress());
				parameters.put("City", hosp.getCity());
				parameters.put("Email", hosp.getEmail());
				parameters.put("Telephone", hosp.getTelephone());
				parameters.put("totalpopulation", (double) hosp.getPopulation_area());
				parameters.put("fromdate", fromDate + ""); // real param
				parameters.put("todate", toDate + ""); // real param
				parameters.put("year", year + ""); // real param
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
				//opd
				Double totalNewOpd = (double) dataOpd.get("total");
				parameters.put("total", dataOpd.get("total"));
				parameters.put("totalN", (double)dataOpd.get("totalN"));
				parameters.put("totalR", (double)dataOpd.get("totalR"));
				//cpn
				parameters.put("totalCpnN", (double)dataCpn.get("totalCpnN"));
				parameters.put("totalCpnR", (double)dataCpn.get("totalCpnR"));
				//cps
				parameters.put("totalCpsN", (double)dataCps.get("totalCpsN"));
				parameters.put("totalCpsR", (double)dataCps.get("totalCpsR"));
			    
				//BCG
				HashMap<String, Integer> dataVacBCG = vacManager.getCountSpecificVacc(year, 1);//BCG
				parameters.put("bcg", dataVacBCG.get("totalVcc"));
				//Polio 3
				HashMap<String, Integer> dataVacPolio3 = vacManager.getCountSpecificVacc(year, 5);//polio3
				parameters.put("polio3", dataVacPolio3.get("totalVcc"));
				
				//DTC 3
				HashMap<String, Integer> dataVacDTC3 = vacManager.getCountSpecificVacc(year, 5);//DTC3 TO COMPLETE
				parameters.put("dtc3", dataVacDTC3.get("totalVcc"));
				
				//VAR
				HashMap<String, Integer> dataVacVAR = vacManager.getCountSpecificVacc(year, 5);//VAR TO COMPLETE
				parameters.put("var", dataVacVAR.get("totalVcc"));
				
				//VAT2
				HashMap<String, Integer> dataVacVAT2 = vacManager.getCountSpecificVacc(year, 5);//VAR TO COMPLETE
				parameters.put("vat2", dataVacVAT2.get("totalVcc"));
				
				//malaria
				HashMap<String, Integer> dataVacMalaria = opdManager.getCountDisease(year, 39);//VAR TO COMPLETE
				parameters.put("totalMalaria", dataVacMalaria.get("totalDisease"));
				
				//TBC
				HashMap<String, Integer> dataLabTBC = labManager.getCountLaboratoryByIDPositive(year, "TBC", "POSITIF");//VAR TO COMPLETE
				parameters.put("totalEffectiveTBC", dataLabTBC.get("totalLaboratoryById"));
				
				//adm
				Double avg = (double) (dataAdm.get("totalAvgAdm")/86400);
				Double totalAdm = (double) dataAdm.get("totalAdm");
				Double numBed = (double) dataBed.get("totalbeds");
				parameters.put("totalAdm", totalAdm);
				parameters.put("totalAvgAdm", avg);
				parameters.put("totalAvgTimeAdm", avg/totalAdm);
				parameters.put("numBed", numBed);
				parameters.put("totalDeath", dataAdm.get("totalDeath"));
				
				//delivery
				Double totalDel = (double) dataPregDel.get("totalDelivery");
				parameters.put("totalDelivery", totalDel);
				parameters.put("totalDeliveryIndex", totalDel/totalNewOpd);
				//DecimalFormat df = new DecimalFormat("#.##");
				//df.setRoundingMode(RoundingMode.HALF_UP);
				//parameters.put("rateBedOccupation",df.format(avg /(numBed*365)));
				parameters.put("rateBedOccupation",avg /(numBed*365));
				
				//laboratory
				parameters.put("totalLaboratory", datalab.get("totalLaboratory"));
				
				//operation Minor and major
				HashMap<String, Integer> dataOperation = opemanager.getCountMinorMajorOperation(year, 0, 1);
				parameters.put("totalMinorOpe", dataOperation.get("totalMinorOpe"));
				parameters.put("totalMajorOpe", dataOperation.get("totalMajorOpe"));
				
				//HIV
				HashMap<String, Integer> dataHiv = labManager.getCountLaboratoryHIV(year, "HIV", "POSITIF");
				parameters.put("totalHivPositive", dataHiv.get("totalLaboratoryHivPositive"));
				parameters.put("totalHiv", dataHiv.get("totalLaboratoryHiv"));
				
				//HIV pregnant
				HashMap<String, Integer> dataHivPregnant = labManager.getCountLaboratoryHivPregnant(year, "HIV", "POSITIF");
				parameters.put("totalPregHivPositive", dataHivPregnant.get("totalLabPregnantHivPositive"));
				parameters.put("totalPregHiv", dataHivPregnant.get("totalLabPregnantHiv"));
				
				//totalReferal to
				HashMap<String, Integer> dataReferalTo = admManager.getCountTransByAdmissionAndOpd(year, "B", "R");
				parameters.put("totalTransfert", dataReferalTo.get("totalTransfert"));
				
				//delivry death result
				HashMap<String, Integer> dataDeliverydeaph = admManager.getCountDelivryResultDeath(year, "M", "B");
				parameters.put("totalMotherDeath", dataDeliverydeaph.get("totalMotherDeath"));
				parameters.put("totalChildDeath", dataDeliverydeaph.get("totalChildDeath"));
				
				//child delivery negative mother positive
				HashMap<String, Integer> dataDeliveryChildNegMotherPos= admManager.getCountDelivryRChildHivNegMotherPos(year, "N", "HIV", "POSITIF","P");
				parameters.put("totalChildNegativeMotherPositive", dataDeliveryChildNegMotherPos.get("totalChildNegativeMotherPositive"));
				parameters.put("totalChildtested", dataDeliveryChildNegMotherPos.get("totalChildPositiveMotherPositive")
						                          +dataDeliveryChildNegMotherPos.get("totalChildNegativeMotherPositive"));
				
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

					queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
					queryString = queryString.replace("$P{todate}", "'" + toDate + "'");

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
				}
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public  GenericReportFromDateToDate(int IdPregAdmission, String author,String jasperFileName, boolean toCSV) {
			try{
		        HashMap<String, Object> parameters = new HashMap<String, Object>();
				HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
				Hospital hosp = hospManager.getHospital();				
				parameters.put("Hospital", hosp.getDescription());
				parameters.put("Address", hosp.getAddress());
				parameters.put("City", hosp.getCity()); ;
				parameters.put("Email", hosp.getEmail());
				parameters.put("fax", hosp.getFax());
				parameters.put("Telephone", hosp.getTelephone());
				parameters.put("admission_id", IdPregAdmission);
				parameters.put("author", author);
				parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());			
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
		
		public void GenericReportFromDateToDate_OH001(String fromDate, String toDate, String jasperFileName){
			try{
				StringBuilder sbFilename = new StringBuilder();
				sbFilename.append("rpt");
				sbFilename.append(File.separator);
				sbFilename.append(jasperFileName);
				sbFilename.append(".jasper");
	
				File jasperFile = new File(sbFilename.toString());
				
				JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
			
				JRQuery query = jasperReport.getMainDataset().getQuery();
				String queryString = query.getText();
				
				queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
				queryString = queryString.replace("$P{todate}", "'" + toDate + "'");
								
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
					xlsExport.exportResultsetToEXCEL_OH001(resultSet, exportFile);
					Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
				}
				dbQuery.releaseConnection();
			
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void GenericReportFromDateToDate_OH002(String fromDate, String toDate, String jasperFileName){
			try{
			
				String queryString ="SELECT PAT_CITY, PAT_ADDR, COUNT(*) NUMBER FROM PATIENT WHERE DATE(PAT_TIMESTAMP) BETWEEN "
					+ " STR_TO_DATE($P{fromdate},'%d/%m/%Y') AND STR_TO_DATE($P{todate},'%d/%m/%Y') AND "
					+ "PAT_DELETED = 'N' GROUP BY PAT_CITY, PAT_ADDR ORDER BY PAT_CITY ";
				queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
				queryString = queryString.replace("$P{todate}", "'" + toDate + "'");			
				
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
					xlsExport.exportResultsetToEXCEL_OH002(resultSet, exportFile);
					Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
				}
				dbQuery.releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void GenericReportFromDateToDate_OH004(String fromDate, String toDate, String jasperFileName, String status){
			try{
				String queryString;
				if(status.equals("C")){
					queryString ="SELECT BLI_ITEM_DESC, SUM(BLI_QTY) AS QTY, SUM(BLI_ITEM_AMOUNT * BLI_QTY) AS AMOUNT " +
						" FROM BILLITEMS JOIN BILLS ON BLI_ID_BILL = BLL_ID WHERE BLL_STATUS = 'C' " +
						" AND DATE(BLL_DATE) BETWEEN STR_TO_DATE($P{fromdate},'%d/%m/%Y') AND STR_TO_DATE($P{todate},'%d/%m/%Y')" +
						" GROUP BY BLI_ITEM_DESC ORDER BY AMOUNT DESC";
				}else{
					queryString ="SELECT BLI_ITEM_DESC, SUM(BLI_QTY) AS QTY, SUM(BLI_ITEM_AMOUNT * BLI_QTY) AS AMOUNT " +
						" FROM BILLITEMS JOIN BILLS ON BLI_ID_BILL = BLL_ID WHERE (BLL_STATUS = 'C' or BLL_STATUS = 'O')" +
						" AND DATE(BLL_DATE) BETWEEN STR_TO_DATE($P{fromdate},'%d/%m/%Y') AND STR_TO_DATE($P{todate},'%d/%m/%Y')" +
						" GROUP BY BLI_ITEM_DESC ORDER BY AMOUNT DESC";
				}
				
				
				queryString = queryString.replace("$P{fromdate}", "'" + fromDate + "'");
				queryString = queryString.replace("$P{todate}", "'" + toDate + "'");			
				
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
					xlsExport.exportResultsetToEXCEL_OH004(resultSet, exportFile);
					Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
				}
				dbQuery.releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
