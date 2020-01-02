package org.isf.utils.sage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.Sage;
import org.isf.medicalinventory.gui.InventoryEdit;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.model.Movement;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.BillItemStatus;

public class SageExporter {
	private static final String DATE_FORMAT_DD_MM_YY = "dd/MM/yy";
	private static final String EXPORT_FORMAT = "%-6.6s%-6.6s%-17.17s%-17.17s%-13.13s%-17.17s%-35.35s%14.14s%14.14s\r\n";
	public SageExporter() {
	}
	
	public void exportTableToSage(JTable jtable, File file) throws IOException {
		exportBuyingTable(jtable, file, ";");
	}
	
	public static void exportBuyingTable(JTable jtable, File file, String separator) throws IOException {
		TableModel model = jtable.getModel();
		FileWriter outFile = new FileWriter(file);
		
		String journalCode = Sage.getSage().JOURNAL_BUY_CODE;
		String generalAccount = Sage.getSage().SUPPLIER_GENERAL_ACCOUNT;
		String expenseAccount = Sage.getSage().EXPENSE_ACCOUNT;
		String specificSupplierAccount = "";
		String date = "";
		String operationlabel = "";
		String output = "";
		String input = "";
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY);
		NumberFormat formatterAmount = new DecimalFormat("#0.00"); 
		String movCode = "";
		String factureNumber = "";
		String reference = "";
        for (int i = 0; i < model.getRowCount(); i++) {
        	input = "0,00";
        	if(model.getValueAt(i, 14)!=null)
        		movCode = model.getValueAt(i, 14).toString();
        	else
        		movCode = "";
        	if(model.getValueAt(i, 0)!=null){
        		factureNumber = model.getValueAt(i, 0).toString();
        		reference     = model.getValueAt(i, 0).toString();
        	}
        	if(movCode.equals("charge")){
        		if(model.getValueAt(i, 1)!=null){
        			date = model.getValueAt(i, 1).toString();
        			try {
						date = formatter.format(formatter.parse(date));
						date = date.replace("/", "");
					} catch (ParseException e) {
						e.printStackTrace();
					}
        		}
        		else
        			date = "";
            	if(model.getValueAt(i, 13)!=null)
            		specificSupplierAccount = model.getValueAt(i, 13).toString(); 
            	else
            		specificSupplierAccount = "";
            	operationlabel = journalCode+"-"+date+"-"+model.getValueAt(i, 10);
            	
            	Object objVal = model.getValueAt(i, 12);
				if (objVal != null) {
					if (objVal instanceof Double) {
						Double val = (Double) objVal;
						input = formatterAmount.format(val);
					}
				}
            	
            	input = input.replace(".", ",");
            	output = "0,00";
            	outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, expenseAccount, "", operationlabel, input, output));
            	outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, generalAccount, specificSupplierAccount, operationlabel, output, input));
            }
        				
		}
		outFile.close();
	}
    
	public static void exportExpense(GregorianCalendar dateFrom, GregorianCalendar dateTo, File file) throws IOException {
		FileWriter outFile = new FileWriter(file);
		String journalCode = Sage.getSage().JOURNAL_BUY_CODE;
		String generalAccount = Sage.getSage().SUPPLIER_GENERAL_ACCOUNT;
		String expenseAccount = Sage.getSage().EXPENSE_ACCOUNT;
		String date = "";
		String operationlabel1 = "";
		String operationlabel2 = "";
		String output = "0,00";
		String input = "";
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY);
		NumberFormat formatterAmount = new DecimalFormat("#0.00"); 
		String factureNumber = "";
		String reference = "";
		MovBrowserManager movManager = new MovBrowserManager();
		List<Movement> movList=movManager.getMovements(dateFrom,dateTo,"charge");
		String currentExpenseAccount = "";
		String currentSupplierAccount = "";
		double amount = 0.00;
		for (Iterator<Movement> iterator = movList.iterator(); iterator.hasNext();) {			
			Movement currentMovement = (Movement) iterator.next();
			currentExpenseAccount = currentMovement.getMedical().getType().getExpenseAccount();			
			if(currentExpenseAccount==null || currentExpenseAccount.equals("")){
				currentExpenseAccount = Sage.getSage().MEDICALEXPENSEACCOUNT;
			}
			currentSupplierAccount = currentMovement.getOrigin().getSupAccount();
			if(currentSupplierAccount==null){
				currentSupplierAccount="";
			}
			factureNumber = currentMovement.getRefNo();
    		reference     = currentMovement.getRefNo();
    		formatter.setCalendar(currentMovement.getDate());
    	    date = formatter.format(currentMovement.getDate().getTime());
    	    date = date.replace("/", "");
    	    operationlabel1 = journalCode+"-"+date+"-"+currentMovement.getOrigin().getSupName();
    	    operationlabel2 = journalCode+"-"+date+"-"+currentMovement.getMedical().getType().getDescription();
    	    //amount = currentMovement.getLot().getCost() * currentMovement.getQuantity();
    	    
    	    Double cost = currentMovement.getLot().getCost();
    	    Double reductionRate = currentMovement.getLot().getReduction_rate();
    	    cost = cost - cost*(reductionRate/100);
    	    amount = cost * currentMovement.getQuantity();
    	    
    	    input = formatterAmount.format(amount);
    	    input = input.replace(".", ",");
    	    outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, expenseAccount, currentExpenseAccount, operationlabel2, input, output));
        	outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, generalAccount, currentSupplierAccount, operationlabel1, output, input));
		}
		outFile.close();
	}
	
	public static boolean exportSales( File file, GregorianCalendar dateFrom, GregorianCalendar dateTo) throws IOException {
		
//		TableModel model = jtable.getModel();
		//FileWriter outFile = new FileWriter(file);
		boolean returnValue = true;
		String journalCode = Sage.getSage().JOURNAL_PAID_CODE;
		String generalAccount = Sage.getSage().CUSTOMER_GENERAL_ACCOUNT;
		String incomeAccount = Sage.getSage().INCOME_ACCOUNT;
		String specificIncomeAccount = "";
		String date = "";
		String operationlabel = "";
		String output = "";
		String input = "";
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY);
		NumberFormat formatterAmount = new DecimalFormat("#0.00"); 
		
		String factureNumber = "";
		String reference = "";
		BillBrowserManager billsManager = new BillBrowserManager();
		List<Bill> billList=billsManager.getBills(dateFrom, dateTo);
		ExamBrowsingManager examManager = new ExamBrowsingManager();
		MedicalBrowsingManager medicalManager = new MedicalBrowsingManager();
		OperationBrowserManager operationManager = new OperationBrowserManager();
		PricesOthersManager otherManager = new PricesOthersManager();
		HashMap<String,Double> account_Amount=new HashMap<String, Double>();
		account_Amount.put(Sage.getSage().MEDICALSALESACCOUNT,0.00);
		account_Amount.put(Sage.getSage().OTHERSALESACCOUNT,0.00);
		account_Amount.put(Sage.getSage().EXAMSALESACCOUNT,0.00);
		account_Amount.put(Sage.getSage().OPERATIONSALESACCOUNT,0.00);
		Exam exam = null;
		Operation ope = null;
		Medical med = null;
		PricesOthers other = null;
		String typologieAccount = "";
		double old_total = 0;
		///control here
		//System.out.println("size of "+billList.size());
		int numBill = 0;
		for (Bill bill : billList) {
			if(bill.getStatus().equalsIgnoreCase("D"))
				continue;
			numBill++;
		} 
		if(numBill==0){
			return false;
		}
		boolean exportWithAlready = false;
		boolean continueExport = true;
		int alreadyExportedCount = checkIfAlreadyExportedExist(billList);
		if( alreadyExportedCount > 0 ){
			int response = JOptionPane.showConfirmDialog(null,
					MessageBundle.getMessage("angal.sageexport.someproductarealreadyexported"),
					MessageBundle.getMessage("angal.exportation.title"),
					JOptionPane.YES_NO_CANCEL_OPTION);
			switch (response) {
			case JOptionPane.OK_OPTION:
				exportWithAlready = true;
				break;
			case JOptionPane.NO_OPTION:
				exportWithAlready = false;
				break;
			case JOptionPane.CANCEL_OPTION:
				continueExport = false;
				break;
			default:
				continueExport = false;
				break;
			}	
		}
		/// end control
		List<BillItems> currentBillItemsList = new ArrayList<BillItems>();
		List<BillItems> BillItemsListToUpdateStatus = new ArrayList<BillItems>();
		
		if(continueExport){
			FileWriter outFile = new FileWriter(file);
			for (Iterator<Bill> iterator = billList.iterator(); iterator.hasNext();) {
				
				Bill bill = (Bill) iterator.next();
				
				if(bill.getStatus().equalsIgnoreCase("D")){
					continue;
				}
				
				factureNumber = Sage.getSage().PAID_PREFIX+bill.getId();
				reference= Sage.getSage().PAID_PREFIX+bill.getId();
				
				date = formatter.format(bill.getDate().getTime());
				date = date.replace("/", "");
				
				input = "0,00";
				
				exam = null;
				ope  = null;
				med  = null;
				other = null;
				typologieAccount = "";
				account_Amount.clear();
				currentBillItemsList = billsManager.getItems(bill.getId());
				for (Iterator<BillItems> iterator2 = currentBillItemsList.iterator(); iterator2.hasNext();) {
					BillItems currentBillItem = (BillItems) iterator2.next();
					String itemId = currentBillItem.getItemId();
					String itemGroup = currentBillItem.getItemGroup();
					
					//test billItem exportStatus
					if(currentBillItem.getExport_status().equalsIgnoreCase(BillItemStatus.Status.EXPORTED.getCode())){
						if(!exportWithAlready) {
							bill.setAmount(bill.getAmount()-(currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
							continue;
						}
					}else{
						BillItemsListToUpdateStatus.add(currentBillItem);
					}
					//
					
					if(!(itemGroup==null) && !itemGroup.equals("")){  
						if(itemGroup.equalsIgnoreCase(ItemGroup.EXAM.getCode())){ 	
							exam = (Exam)examManager.getExam(itemId);
							if(exam!=null){
								typologieAccount = exam.getExamtype() != null? exam.getExamtype().getAccount(): "";			
							}
						}else if(itemGroup.equalsIgnoreCase(ItemGroup.MEDICAL.getCode())){
							med = (Medical)medicalManager.getMedical(Integer.parseInt(itemId));
							if(med!=null){
								typologieAccount = med.getType()!=null ? med.getType().getAccount():"";
							}
						}else if(itemGroup.equalsIgnoreCase(ItemGroup.OPERATION.getCode())){
							ope = (Operation)operationManager.getOperationByCode(itemId);
							if(ope!=null){
								typologieAccount = ope.getType()!=null ? ope.getType().getAccount() :"";
							}
						}else if(itemGroup.equalsIgnoreCase(ItemGroup.OTHER.getCode())){
							other = otherManager.getOther(Integer.parseInt(itemId));
							if(other!=null){
								typologieAccount = other.getAccount() !=null ? other.getAccount() :"";
							}
						}
						
						if(typologieAccount!=null && !typologieAccount.equals("")){
							if(account_Amount.containsKey(typologieAccount)){
								old_total = account_Amount.get(typologieAccount);
								account_Amount.put(typologieAccount, old_total + (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
							}
							else{
								account_Amount.put(typologieAccount, (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
							}
						}
						else{
							if(itemGroup.equalsIgnoreCase(ItemGroup.OTHER.getCode())){
								if(account_Amount.containsKey(Sage.getSage().OTHERSALESACCOUNT)){
									old_total = account_Amount.get(Sage.getSage().OTHERSALESACCOUNT);
									account_Amount.put(Sage.getSage().OTHERSALESACCOUNT, old_total + (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
								}
								else{
									account_Amount.put(Sage.getSage().OTHERSALESACCOUNT, currentBillItem.getItemAmount()*currentBillItem.getItemQuantity());
								}
							}
							if(itemGroup.equalsIgnoreCase(ItemGroup.MEDICAL.getCode())){
								if(account_Amount.containsKey(Sage.getSage().MEDICALSALESACCOUNT)){
									old_total = account_Amount.get(Sage.getSage().MEDICALSALESACCOUNT);
									account_Amount.put(Sage.getSage().MEDICALSALESACCOUNT, old_total + (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
								}
								else{
									account_Amount.put(Sage.getSage().MEDICALSALESACCOUNT, (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
								}
							}
							if(itemGroup.equalsIgnoreCase(ItemGroup.EXAM.getCode())){
								if(account_Amount.containsKey(Sage.getSage().EXAMSALESACCOUNT)){
									old_total = account_Amount.get(Sage.getSage().EXAMSALESACCOUNT);
									account_Amount.put(Sage.getSage().EXAMSALESACCOUNT, old_total + currentBillItem.getItemAmount()*currentBillItem.getItemQuantity());
								}
								else{
									account_Amount.put(Sage.getSage().EXAMSALESACCOUNT, currentBillItem.getItemAmount()*currentBillItem.getItemQuantity());
								}
							}
							if(itemGroup.equalsIgnoreCase(ItemGroup.OPERATION.getCode())){
								if(account_Amount.containsKey(Sage.getSage().OPERATIONSALESACCOUNT)){
									old_total = account_Amount.get(Sage.getSage().OPERATIONSALESACCOUNT);
									account_Amount.put(Sage.getSage().OPERATIONSALESACCOUNT, old_total + (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
								}
								else{
									account_Amount.put(Sage.getSage().OPERATIONSALESACCOUNT, currentBillItem.getItemAmount()*currentBillItem.getItemQuantity());
								}
							}
						}
					}
					else{
						if(account_Amount.containsKey(Sage.getSage().OTHERSALESACCOUNT)){
							old_total = account_Amount.get(Sage.getSage().OTHERSALESACCOUNT);
							account_Amount.put(Sage.getSage().OTHERSALESACCOUNT, old_total + (currentBillItem.getItemAmount()*currentBillItem.getItemQuantity()));
						}
						else{
							account_Amount.put(Sage.getSage().OTHERSALESACCOUNT, currentBillItem.getItemAmount()*currentBillItem.getItemQuantity());
						}
					}
					old_total = 0;
					typologieAccount = "";
				}
				////////////////////////	
	            operationlabel = journalCode+"-"+date+"-"+bill.getPatName();            	
	            input = formatterAmount.format(bill.getAmount());            	
	            input = input.replace(".", ",");
	            output = "0,00";
	            if(bill.getAmount()!=0){
	            	outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, generalAccount, "", operationlabel, input, output));
	            }
	            Iterator<String> account_AmountIter=account_Amount.keySet().iterator();           
	            while(account_AmountIter.hasNext())            
	            {            
	                 String account = account_AmountIter.next();
	                 Double amount = account_Amount.get(account);
	                 String converted_amount = "";
	                 if( amount != 0.00 && amount!=null ){
	                	 converted_amount = formatterAmount.format(amount); 
	                	 converted_amount = input = converted_amount.replace(".", ",");
	                	 outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, account, "", operationlabel, output, converted_amount));
	                 }
	            }           	
			}
			
			//update status of exported billItems
			for (BillItems billItems : BillItemsListToUpdateStatus) {
				try {
					billItems.setExport_status(BillItemStatus.Status.EXPORTED.getCode());
					billsManager.updateBillItemsExportStatus(billItems);
				} catch (OHException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			///
			returnValue = true;
			outFile.close();
		}else{
			returnValue = false;
			System.out.println(" i am cancelled");
		}
		
		return returnValue;
	}
	
	public static int checkIfAlreadyExportedExist(List<Bill> billList){
		int count = 0;
		BillBrowserManager billsManager = new BillBrowserManager();
		List<BillItems> currentBillItemsList = new ArrayList<BillItems>();
		for (Iterator<Bill> iterator = billList.iterator(); iterator.hasNext();) {
			Bill bill = (Bill) iterator.next();
			if(bill.getStatus().equalsIgnoreCase("D")){
				continue;
			}
			currentBillItemsList = billsManager.getItems(bill.getId());
			for (Iterator<BillItems> iterator2 = currentBillItemsList.iterator(); iterator2.hasNext();) {
				BillItems currentBillItem = (BillItems) iterator2.next();
				if(currentBillItem.getExport_status().equalsIgnoreCase(BillItemStatus.Status.EXPORTED.getCode())){
					count++;
				}
			}
		}
		return count;
	}
	
	public static boolean exportCashTable(ArrayList<BillPayments> BillPayments, File file) throws IOException {
		FileWriter outFile = new FileWriter(file);
		
		String journalCode = Sage.getSage().JOURNAL_CASHDESK_CODE;
		String customerAccount = Sage.getSage().CUSTOMER_GENERAL_ACCOUNT;
		String cashAccount = Sage.getSage().CASH_ACCOUNT;
		String date = "";
		String subCustomerAccount = "";
		String subCashAccount = "";
		String operationlabel = "";
		String output = "";
		String input = "";
		DateFormat formatter = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY);
		NumberFormat formatterAmount = new DecimalFormat("#0.00"); 
		
		String factureNumber = "";
		String reference = "";
		
		for (BillPayments BillPayment: BillPayments) {
			input = "0,00";
			formatter.setCalendar(BillPayment.getDate());
			date = formatter.format(BillPayment.getDate().getTime());
			date = date.replace("/", "");
			factureNumber = Sage.getSage().CASHDESK_PREFIX+BillPayment.getId()+"";
    		reference     = Sage.getSage().CASHDESK_PREFIX+BillPayment.getId()+"";
    		operationlabel = journalCode+"-"+date+"-"+new BillBrowserManager().getBill(BillPayment.getBillID()).getPatName();
    		output = "0,00";
            input = formatterAmount.format(BillPayment.getAmount());
            input = input.replace(".", ",");
            outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, cashAccount, subCashAccount, operationlabel, input, output));
            outFile.write(String.format(EXPORT_FORMAT, journalCode, date, factureNumber, reference, customerAccount, subCustomerAccount, operationlabel, output, input));       						
		}
		outFile.close();
		return true;
	}
}
