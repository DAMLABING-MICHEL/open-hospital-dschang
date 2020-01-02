package org.isf.lab.manager;

import java.text.ParseException;

/*------------------------------------------
 * LabManager - laboratory exam manager class
 * -----------------------------------------
 * modification history
 * 10/11/2006 - ross - added editing capability 
 *------------------------------------------*/

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.lab.service.IoOperations;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class LabManager {

	private IoOperations ioOperations = new IoOperations();

	/**
	 * Return the whole list of exams ({@link Laboratory}s) within last year.
	 * @return the list of {@link Laboratory}s. It could be <code>empty</code>.
	 */
	public ArrayList<Laboratory> getLaboratory() {
		try {
			return ioOperations.getLaboratory();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Laboratory>();
		}
	}

	/**
	 * Return an exam ({@link Laboratory}) found by its code.
	 * 
	 * @param code - the exam code.
	 * @return the {@link Laboratory}.
	 */
	public Laboratory getLaboratoryByCode(int code) {
		try {
			return ioOperations.getLaboratoryByCode(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	/**
	 * Return a list of exams ({@link Laboratory}s) related to a {@link Patient}.
	 * 
	 * @param aPatient - the {@link Patient}.
	 * @return the list of {@link Laboratory}s related to the {@link Patient}. It could be <code>empty</code>.
	 */
	public ArrayList<Laboratory> getLaboratory(int patID) {
		try {
			return ioOperations.getLaboratory(patID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Laboratory>();
		}
	}

	/*
	 * NO LONGER USED
	 * 
	 * public ArrayList<Laboratory> getLaboratory(String aCode){ return
	 * ioOperations.getLaboratory(); }
	 */

	/**
	 * Return a list of exams ({@link Laboratory}s) between specified dates and matching passed exam name
	 * @param exam - the exam name as <code>String</code>
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link Laboratory}s. It could be <code>empty</code>.
	 */
	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getLaboratory(exam, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Laboratory>();
		}
	}
	
	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo, int resultFilter, String patientCode) {
		try {
			return ioOperations.getLaboratory(exam, dateFrom, dateTo, resultFilter, patientCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Laboratory>();
		}
	}
//	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo, int resultFilter, String patientCode, String userCode) {
//		try {
//			return ioOperations.getLaboratory(exam, dateFrom, dateTo, resultFilter, patientCode, userCode);
//		} catch (OHException e) {
//			JOptionPane.showMessageDialog(null, e.getMessage());
//			return new ArrayList<Laboratory>();
//		}
//	}
	public ArrayList<Laboratory> getLaboratory(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo, int resultFilter, String patientCode, String userCode, String paidCode) {
		try {
			return ioOperations.getLaboratory(exam, dateFrom, dateTo, resultFilter, patientCode, userCode, paidCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<Laboratory>();
		}
	}
	
	public int getLaboratoryCount(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo, int resultFilter, String patientCode, String userCode, String paidCode) {
		try {
			return ioOperations.getLaboratoryCount(exam, dateFrom, dateTo, resultFilter, patientCode, userCode, paidCode);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) 
	 * between specified dates and matching passed exam name
	 * @param exam - the exam name as <code>String</code>
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link LaboratoryForPrint}s . It could be <code>empty</code>.
	 */
	public ArrayList<LaboratoryForPrint> getLaboratoryForPrint(String exam, GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		try {
			return ioOperations.getLaboratoryForPrint(exam, dateFrom, dateTo);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<LaboratoryForPrint>();
		}
	}

	/*
	 * NO LONGER USED
	 * 
	 * public ArrayList<LaboratoryForPrint> getLaboratoryForPrint() {
		return ioOperations.getLaboratoryForPrint();
	}*/

	/*
	 * NO LONGER USED
	 * 
	 * public ArrayList<LaboratoryForPrint> getLaboratoryForPrint(String exam,
	 * String result) { return ioOperations.getLaboratoryForPrint(exam,result);
	 * }
	 */

	/**
	 * Inserts one Laboratory exam {@link Laboratory} (Procedure One)
	 * @param laboratory - the {@link Laboratory} to insert
	 * @param dbQuery - the connection manager
	 * @return <code>true</code> if the exam has been inserted, <code>false</code> otherwise
	 */
	public boolean newLabFirstProcedure(Laboratory laboratory) {
		try {
			return ioOperations.newLabFirstProcedure(laboratory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean newLabFirstProcedure2(Laboratory laboratory) {
		try {
						
			return ioOperations.newLabFirstProcedure2(laboratory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Inserts one Laboratory exam {@link Laboratory} with multiple results (Procedure Two) 
	 * @param laboratory - the {@link Laboratory} to insert
	 * @param labRow - the list of results ({@link String}s)
	 * @return <code>true</code> if the exam has been inserted with all its results, <code>false</code> otherwise
	 */
	public boolean newLabSecondProcedure(Laboratory laboratory, ArrayList<LaboratoryRow> labRow) {
		try {
			return ioOperations.newLabSecondProcedure(laboratory, labRow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public boolean newLabSecondProcedure2(Laboratory laboratory, ArrayList<LaboratoryRow> labRow) {
		try {
			return ioOperations.newLabSecondProcedure2(laboratory, labRow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure One).
	 * If old exam was Procedure Two all its releated result are deleted.
	 * @param laboratory - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated, <code>false</code> otherwise
	 */
	public boolean editLabFirstProcedure(Laboratory laboratory) {
		try {
			return ioOperations.updateLabFirstProcedure(laboratory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean updateBillIdLaboratory(int laboratoryId, int billID) {
		try {
			return ioOperations.updateBillIdLaboratory(laboratoryId,  billID);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure Two).
	 * Previous results are deleted and replaced with new ones.
	 * @param laboratory - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated with all its results, <code>false</code> otherwise
	 */
	public boolean editLabSecondProcedure(Laboratory laboratory, ArrayList<LaboratoryRow> labRow) {
		try {
			return ioOperations.editLabSecondProcedure(laboratory, labRow);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	/**
	 * Delete a Laboratory exam {@link Laboratory} (Procedure One or Two).
	 * Previous results, if any, are deleted as well.
	 * @param laboratory - the {@link Laboratory} to delete
	 * @return <code>true</code> if the exam has been deleted with all its results, if any. <code>false</code> otherwise
	 */
	public boolean deleteLaboratory(Laboratory laboratory) {
		try {
			return ioOperations.deleteLaboratory(laboratory);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public List<Laboratory> getLabWithoutBill(String idPatient) throws OHException{
		return ioOperations.getLabWithoutBill(idPatient);
	}
	
	public boolean hasLabWithoutBill(String patCode){
		try {
			List<Laboratory> labList=this.getLabWithoutBill(patCode);
			return labList!=null && labList.size()>0;
		} catch (OHException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;	
	}
	
	public HashMap<String, Integer> getCountLaboratory(int year){	
		try {
			return ioOperations.getCountLaboratory(year);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public HashMap<String, Integer> getCountLaboratoryByID(int year,int examId){	
		try {
			return ioOperations.getCountLaboratoryByID(year,examId);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}	
	}
	public HashMap<String, Integer> getCountLaboratoryByIDPositive(int year,String examId,String valuePositive){	
		try {
			return ioOperations.getCountLaboratoryByIDPositive(year,examId,valuePositive);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}	
	}
	public HashMap<String, Integer> getCountLaboratoryHIV(int year,String examId,String valuePositive){	
		try {
			return ioOperations.getCountLaboratoryHIV(year,examId,valuePositive);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}	
	}
	public HashMap<String, Integer> getCountLaboratoryHivPregnant(int year,String examId,String valuePositive){	
		try {
			return ioOperations.getCountLaboratoryHivPregnant(year,examId,valuePositive);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}	
	}
	/**
	 * Returns the max progressive number within specified month of specified year.
	 * 
	 * @param month
	 * @param year
	 * @return <code>int</code> - the progressive number in the month
	 */
	public int getProgMonth(int month, int year) {
		try {
			return ioOperations.getProgMonth(month, year);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}
	
	/**
	 * Return the whole old prescriber.
	 * @return the list of prescriber. It could be <code>empty</code>.
	 */
	public ArrayList<String> getPrescriber() {
		try {
			return ioOperations.getPrescriber();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return new ArrayList<String>();
		}
	}
	
	public boolean isHasResults(Integer code)  {
		try {
			return ioOperations.isHasResults(code);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
}
