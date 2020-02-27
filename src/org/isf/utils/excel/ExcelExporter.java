package org.isf.utils.excel;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.opetype.model.OperationType;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.BillItemReportBean;

public class ExcelExporter {
	
	public ExcelExporter() {
	}
	
	public void exportTableToCSV(JTable jtable, File file) throws IOException {
		exportTable(jtable, file, ";");
	}
	
	public void exportTableToExcel(JTable jtable, File file) throws IOException {
		exportTable2(jtable, file, "\t");
	}
	public void exportBillsTableToExcel(ArrayList<Bill> billPeriod, File exportFile) throws IOException{
		exportTableBills(billPeriod, exportFile, "\t");
	}
	

	public void exportBillsTableToExcel(JTable jtable, File file) throws IOException {
		exportTableBills(jtable, file, "\t");
	}
	public void exportTableToExcelStock(JTable jtable, File file) throws IOException {
		System.out.println("j'exporte");
		exportTableStock(jtable, file, "\t");
	}
	public void exportTableToExcel2(JTable jtable, File file) throws IOException {
		exportTable3(jtable, file, "\t");
	}
	public void exportTableToExcelIncomes(JTable jtable, File file) throws IOException {
		exportTableIncomes(jtable, file, "\t");
	}
	public void exportTableToExcelOutcomes(JTable jtable, File file) throws IOException {
		exportTableOutcomes(jtable, file, "\t");
	}
	
	private void exportTableBills(ArrayList<Bill> billPeriod, File file, String separator) throws IOException {
		
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		
		format.setMaximumFractionDigits(2);
		outFile.write("ID FACTURE"+ separator);
		outFile.write("DATE"+ separator);
		outFile.write("PATIENT"+ separator);
		outFile.write("MONTANT"+ separator);
		outFile.write("DERNIER PAIEMENT"+ separator);
		outFile.write("STATUS"+ separator);
		outFile.write("BALANCE"+ separator);
		outFile.write("\n");
		
		for(Bill bill: billPeriod){
			if(!bill.getStatus().equals("D")){
				outFile.write(bill.getId() + separator);
				outFile.write(sdf.format(bill.getDate().getTime()) + separator);
				outFile.write(bill.getPatName() + separator);
				outFile.write(bill.getAmount() + separator);
				outFile.write(sdf.format(bill.getUpdate().getTime()) + separator);  //verifier
				outFile.write(bill.getStatus() + separator);
				outFile.write(bill.getBalance() + separator);
				outFile.write("\n");
			}
		}
		outFile.close();
	}
	
	private void exportTableBills(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		
		format.setMaximumFractionDigits(2);
		for (int e = 0; e < model.getColumnCount(); e++) {
			if(e==0) {
				outFile.write("ID FACTURE"+ separator);
			}
			if(e==1) {
				outFile.write("DATE"+ separator);
			}
			if(e==3) {
				outFile.write("PATIENT"+ separator);
			}
			if(e==4) {
				outFile.write("MONTANT"+ separator);
			}
			if(e==5 ) {
				outFile.write("DERNIER PAIEMENT"+ separator);
			}
			if(e==6 ) {
				outFile.write("STATUS"+ separator);
			}
			if(e==7 ) {
				outFile.write("BALANCE"+ separator);
			}
		}
	//	outFile.write("Total");
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j <= model.getColumnCount(); j++) {
				String strVal;
				String formated;
				Object objVal = model.getValueAt(i, j);
				
				if(j==0 || j==1 || j==3 || j==4 || j==5 || j==6 || j==7){
					if (objVal != null) {
						if (objVal instanceof Long) {							
							Long val = (Long) objVal;
							formated = format.format(val);							
							strVal = trimCustom(formated);	
						}
						if (objVal instanceof Double) {							
							Double val = (Double) objVal;
							formated = format.format(val);							
							strVal = trimCustom(formated);	
							
						} else if (objVal instanceof Timestamp) {						
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString().replace('"', ' ');
						}
						outFile.write(strVal + separator);
					}
				}
			}
			outFile.write("\n");
		}

		outFile.close();
	}
	private void exportTable2(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		
		format.setMaximumFractionDigits(2);
		for (int e = 0; e < model.getColumnCount(); e++) {
			if(e==2) {
				outFile.write("Designation"+ separator);
			}
			if(e==4) {
				outFile.write("Quantite"+ separator);
			}
			if(e==7) {
				outFile.write("PU"+ separator);
			}
		}
		outFile.write("Total");
		
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j <= model.getColumnCount(); j++) {
				String strVal;
				String formated;
				Object objVal = model.getValueAt(i, j);
				if(j==2 || j==4 || j==7){
					if (objVal != null) {
						if (objVal instanceof Double) {							
							Double val = (Double) objVal;
							//strVal = format.format(val);
							formated = format.format(val);							
							strVal = trimCustom(formated);	
							
						} else if (objVal instanceof Timestamp) {						
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							if(j==4){
								Double val = Double.parseDouble(objVal.toString());					
								formated = format.format(val);							
								strVal = trimCustom(formated);
							}else{
								strVal = objVal.toString().replace('"', ' ');
							}
						}
						outFile.write(strVal + separator);
					}
				}
				if(j==8){
					Double qtyV;
					try{
						Object price = model.getValueAt(i, 7);
						Double priceV = Double.parseDouble(price.toString());
						Object qty = model.getValueAt(i, 4);
						qtyV = Double.parseDouble(qty.toString());
						qtyV = qtyV*priceV;	
					}catch (Exception e) {
						qtyV = 0.0;
					}													
					formated = format.format(qtyV);							
					strVal = trimCustom(formated);	
					outFile.write(strVal + separator);
				}
			}
			outFile.write("\n");
		}

		outFile.close();
	}
	
	private void exportTableStock(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		format.setMaximumFractionDigits(2);
		for (int e = 0; e < model.getColumnCount(); e++) {
			if(e==5) {//inversion des libelles
				outFile.write("Quantite"+ separator);
			}
			if(e==4) { //inversion des libelles
				outFile.write("Designation"+ separator);
			}
			if(e==11) {
				outFile.write("PU"+ separator);
			}
			if(e==12) {
				outFile.write("Reduction"+ separator);
			}
			
			
		}
		outFile.write("Total");
		
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j <= model.getColumnCount(); j++) {
				String strVal;
				
				String formated;
				Object objVal = model.getValueAt(i, j);
				if(j==4 || j==5 || j==11 || j==12 || j==13){
					if (objVal != null) {
						if (objVal instanceof Double) {							
							Double val = (Double) objVal;							
							formated = format.format(val);							
							strVal = trimCustom(formated);	
							if(j==4){								
								strVal = model.getValueAt(i, 5).toString();
							}
							
						} else if (objVal instanceof Timestamp) {						
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							if(j==5){								
								objVal = model.getValueAt(i, 4);
								Double val = Double.parseDouble(objVal.toString());					
								formated = format.format(val);							
								strVal = trimCustom(formated);
							}else{
								strVal = objVal.toString().replace('"', ' ');
							}
							if(j==11){
								Double val = Double.parseDouble(objVal.toString());					
								formated = format.format(val);							
								strVal = trimCustom(formated)+"%";
							}else{
								strVal = objVal.toString().replace('"', ' ');
							}
						}
					} else {						
							strVal = "";
					}
					outFile.write(strVal + separator);
				}
				

			}
			outFile.write("\n");
		}

		outFile.close();
	}
	private String trimCustom(String toTrim){
		char[] te = toTrim.toCharArray();		
		String result = "";
		for(int k=0;k<te.length;k++){
			if(te[k]=='1' || te[k]=='2' || te[k]=='3' || te[k]=='4' || te[k]=='5' || te[k]=='6' || te[k]=='7' || te[k]=='8' || te[k]=='9' || te[k]=='0' || te[k]==',' || te[k]=='.'){
				result = result+te[k]+"";
			}				
		}
		return 	result;
	}
	
	private void exportTable3(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		//format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		for (int i = 0; i < model.getColumnCount(); i++) {
			//outFile.write(model.getColumnName(i) + separator);
			if(i==0) {
				outFile.write("Designation"+ separator);
			}
			if(i==1) {
				outFile.write("Quantite"+ separator);
			}
			if(i==2) {
				outFile.write("PU"+ separator);
			}
		}
		outFile.write("Total");
		
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j <= model.getColumnCount(); j++) {
				String strVal;
				String formated;
				Object objVal = model.getValueAt(i, j);
				if (objVal != null) {					
					if (objVal instanceof Double) {						
						Double val = (Double) objVal;						
						formated = format.format(val);							
						strVal = trimCustom(formated);
					} else if (objVal instanceof Timestamp) {						
						Timestamp val = (Timestamp) objVal;
						strVal = sdf.format(val);
					} else {						
						strVal = objVal.toString().replace('"', ' ');					
					}
				} else {
					if(j==3){
						Object price = model.getValueAt(i, 1);
						Double qtyV;
						try{
							Double priceV = Double.parseDouble(price.toString());
							Object qty = model.getValueAt(i, 2);
							qtyV = Double.parseDouble(qty.toString());
							qtyV = qtyV*priceV;
						}catch (Exception e) {
							qtyV = 0.0;
						}
						formated = format.format(qtyV);							
						strVal = trimCustom(formated);
					}else
						strVal = " ";
				}
				outFile.write(strVal + separator);
			}
			outFile.write("\n");
		}
		outFile.close();
	}
	
	private void exportTableIncomes(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		
		format.setMaximumFractionDigits(2);
		for (int i = 0; i < model.getColumnCount(); i++) {
			
			if(i==0) {
				outFile.write("Date"+ separator);
			}
			if(i==1) {
				outFile.write("Designation"+ separator);
			}
			if(i==2) {
				outFile.write("Quantite"+ separator);
			}
		}
		
		outFile.write("Dernier PU");
		
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j <= model.getColumnCount(); j++) {
				String strVal="";
				String formated;
				Object objVal = model.getValueAt(i, j);
				if (objVal != null) {					
					if (objVal instanceof Double) {						
						Double val = (Double) objVal;						
						formated = format.format(val);							
						strVal = trimCustom(formated);
					} else if (objVal instanceof Timestamp) {						
						Timestamp val = (Timestamp) objVal;
						strVal = sdf.format(val);
					} else {						
						strVal = objVal.toString().replace('"', ' ');					
					}
				} 
				outFile.write(strVal + separator);
			}
		
			outFile.write("\n");
		}
		outFile.close();
	}
	
	private void exportTableOutcomes(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		NumberFormat format = NumberFormat.getInstance(Locale.FRENCH);
		
		format.setMaximumFractionDigits(2);
		for (int i = 0; i < model.getColumnCount(); i++) {
			
			if(i==0) {
				outFile.write("Date"+ separator);
			}
			if(i==1) {
				outFile.write("Patient"+ separator);
			}
			if(i==2) {
				outFile.write("Medicament"+ separator);
			}
			if(i==3) {
				outFile.write("Quantite"+ separator);
			}
		}
				
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j <= model.getColumnCount(); j++) {
				String strVal="";
				String formated;
				Object objVal = model.getValueAt(i, j);
				if (objVal != null) {		
					if(j !=2 && j !=3 && j != 4){
						if (objVal instanceof Double) {						
							Double val = (Double) objVal;						
							formated = format.format(val);							
							strVal = trimCustom(formated);
						} else if (objVal instanceof Timestamp) {						
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {						
							strVal = objVal.toString().replace('"', ' ');					
						}
						//if(!strVal.equals(""))
						outFile.write(strVal + separator);
					}
					
				} 
				
			}
		
			outFile.write("\n");
		}
		outFile.close();
	}
	
	private void exportTable(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		for (int i = 0; i < model.getColumnCount(); i++) {
			outFile.write(model.getColumnName(i) + separator);
		}
		outFile.write("\n");

		for (int i = 0; i < model.getRowCount(); i++) {
			for (int j = 0; j < model.getColumnCount(); j++) {
				String strVal;
				Object objVal = model.getValueAt(i, j);
				if (objVal != null) {
					if (objVal instanceof Double) {
						
						Double val = (Double) objVal;
						NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
						strVal = format.format(val);
					} else if (objVal instanceof Timestamp) {
						
						Timestamp val = (Timestamp) objVal;
						strVal = sdf.format(val);
					} else {
						
						strVal = objVal.toString();
					}
				} else {
					strVal = " ";
				}
				outFile.write(strVal + separator);

			}
			outFile.write("\n");
		}

		outFile.close();
	}

	public void exportResultsetToCSV(ResultSet resultSet, File exportFile) throws IOException, OHException {
		
		FileWriter outFile = new FileWriter(exportFile);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();

			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				outFile.write(rsmd.getColumnName(i) + ";");
			}
			outFile.write("\n");
			
			while(resultSet.next()) {
				
				String strVal;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object objVal = resultSet.getObject(i);
					if (objVal != null) {
						if (objVal instanceof Double) {
							
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					outFile.write(strVal + ";");
				}
				outFile.write("\n");
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}

	public void exportResultsetToEXCEL(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String separator = "\t";
		FileWriter outFile = new FileWriter(exportFile);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();

			outFile.write(MessageBundle.getMessage("angal.medicalstock.multiplecharging.description") + separator);
			outFile.write(MessageBundle.getMessage("angal.medicalstock.multiplecharging.quantity") + separator);
			outFile.write(MessageBundle.getMessage("angal.newbill.amount") + separator);
			outFile.write("\n");
			
			while(resultSet.next()) {
				
				String strVal;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object objVal = resultSet.getObject(i);
					if (objVal != null) {
						if (objVal instanceof Double) {
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					outFile.write(strVal + separator);
				}
				outFile.write("\n");
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}
	
	public void GenericReportFromDateToDate_OH004_3_EXCEL(String fromDate, String toDate, String jasperFileName, Integer year, String status, String code_title, File exportFile) throws IOException {
		String separator = "\t";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		FileWriter outFile = new FileWriter(exportFile);
		try{
			BillBrowserManager billManager = new BillBrowserManager();
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
			
			outFile.write(MessageBundle.getMessage("angal.medicalstock.multiplecharging.description") + separator);
			outFile.write(MessageBundle.getMessage("angal.stat.january") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.february") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.march") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.april") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.may") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.june") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.july") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.august") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.september") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.october") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.november") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.december") + separator);
			outFile.write("\n");
		
			for (BillItemReportBean billItemReportBean : collections) {
				outFile.write(billItemReportBean.getBLI_ITEM_DESC() + separator);
				outFile.write(billItemReportBean.getCOUNT_JANUARY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_FEBRUARY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_MARCH().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_APRIL().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_MAY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_JUNE().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_JULY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_AUGUST().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_SEPTEMBER().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_OCTOBER().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_NOVEMBER().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_DECEMBER().intValue() + separator);
				outFile.write("\n");					
			}
			Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		outFile.close();
	}
	
	public void GenericReportFromDateToDate_OH004_3_EXCEL(String fromDate, String toDate, String jasperFileName, Integer year, String status, String code_title, File exportFile, OperationType operationType) throws IOException {
		String separator = "\t";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		FileWriter outFile = new FileWriter(exportFile);
		try{
			BillBrowserManager billManager = new BillBrowserManager();
			List<BillItemReportBean> collections = new ArrayList<BillItemReportBean>();
			collections = billManager.getTotalCountAmountByQuery12(year,status, operationType);
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
			
			outFile.write(MessageBundle.getMessage("angal.medicalstock.multiplecharging.description") + separator);
			outFile.write(MessageBundle.getMessage("angal.stat.january") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.february") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.march") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.april") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.may") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.june") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.july") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.august") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.september") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.october") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.november") + separator);
			outFile.write(MessageBundle.getMessage("angal.stocksheet.december") + separator);
			outFile.write("\n");
		
			for (BillItemReportBean billItemReportBean : collections) {
				outFile.write(billItemReportBean.getBLI_ITEM_DESC() + separator);
				outFile.write(billItemReportBean.getCOUNT_JANUARY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_FEBRUARY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_MARCH().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_APRIL().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_MAY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_JUNE().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_JULY().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_AUGUST().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_SEPTEMBER().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_OCTOBER().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_NOVEMBER().intValue() + separator);
				outFile.write(billItemReportBean.getCOUNT_DECEMBER().intValue() + separator);
				outFile.write("\n");					
			}
			Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		outFile.close();
	}
	public void exportResultsetToEXCEL_OH001(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String separator = "\t";
		FileWriter outFile = new FileWriter(exportFile);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();			
			outFile.write(MessageBundle.getMessage("angal.admission.datem") + separator);
			outFile.write(MessageBundle.getMessage("angal.admission.count").toUpperCase() + separator);
			outFile.write("\n");
			
			while(resultSet.next()) {
				
				String strVal;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object objVal = resultSet.getObject(i);
					if (objVal != null) {
						if (objVal instanceof Double) {
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					outFile.write(strVal + separator);
				}
				outFile.write("\n");
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}
	
	public void exportResultsetToEXCEL_OH002(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String separator = "\t";
		FileWriter outFile = new FileWriter(exportFile);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();			
			outFile.write(MessageBundle.getMessage("angal.hospital.city") + separator);
			outFile.write(MessageBundle.getMessage("angal.hospital.address").toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.report.prescriberexams.totals").toUpperCase() + separator);
			outFile.write("\n");
			
			while(resultSet.next()) {
				
				String strVal;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object objVal = resultSet.getObject(i);
					if (objVal != null) {
						if (objVal instanceof Double) {
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					outFile.write(strVal + separator);
				}
				outFile.write("\n");
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}
	
	public void exportResultsetToEXCEL_OH004(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String separator = "\t";
		FileWriter outFile = new FileWriter(exportFile);
		try {		
			outFile.write(MessageBundle.getMessage("angal.stat.designation") + separator);
			outFile.write(MessageBundle.getMessage("angal.patientbilledit.qty") + separator);
			outFile.write(MessageBundle.getMessage("angal.report.prescriberexams.totals").toUpperCase() + separator);
			outFile.write("\n");
			while(resultSet.next()) {
				outFile.write(resultSet.getString("BLI_ITEM_DESC") + separator);
			
				outFile.write(resultSet.getInt("QTY") + separator);
				
				outFile.write((int)resultSet.getDouble("AMOUNT") + separator);
				
				outFile.write("\n");
							
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}
	public void exportResultsetToEXCEL_OH005(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String separator = "\t";
		FileWriter outFile = new FileWriter(exportFile);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();			
			outFile.write(MessageBundle.getMessage("angal.report.hmis055labmonthlyformatted.category" ).toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.report.oh005opdcountmonthlyreport.age").toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.report.oh005opdcountmonthlyreport.sex").toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.report.hmis108referrals.count").toUpperCase() + separator);
			outFile.write("\n");
			
			while(resultSet.next()) {
				
				String strVal;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object objVal = resultSet.getObject(i);
					if (objVal != null) {
						if (objVal instanceof Double) {
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					outFile.write(strVal + separator);
				}
				outFile.write("\n");
				
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}
	
	public void exportResultsetToEXCEL_OH006(ResultSet resultSet, File exportFile) throws IOException, OHException {
		String separator = "\t";
		FileWriter outFile = new FileWriter(exportFile);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();			
			outFile.write(MessageBundle.getMessage("angal.report.diseaseslist.diseasetype" ).toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.menu.btn.disease" ).toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.report.oh005opdcountmonthlyreport.age").toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.report.oh005opdcountmonthlyreport.sex").toUpperCase() + separator);
			outFile.write(MessageBundle.getMessage("angal.medicalstock.multipledischarging.total").toUpperCase() + separator);
			outFile.write("\n");
			while(resultSet.next()) {			
				String strVal;
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					Object objVal = resultSet.getObject(i);
					if (objVal != null) {
						if (objVal instanceof Double) {
							Double val = (Double) objVal;
							NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
							strVal = format.format(val);
						} else if (objVal instanceof Timestamp) {
							Timestamp val = (Timestamp) objVal;
							strVal = sdf.format(val);
						} else {
							strVal = objVal.toString();
						}
					} else {
						strVal = " ";
					}
					outFile.write(strVal + separator);
				}
				outFile.write("\n");			
			}
		} catch (SQLException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), e);
		}
		outFile.close();
	}
}
