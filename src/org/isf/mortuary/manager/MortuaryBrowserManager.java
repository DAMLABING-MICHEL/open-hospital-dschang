package org.isf.mortuary.manager;





import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.mortuary.model.Death;
import org.isf.mortuary.model.EtatPrixSejours;
import org.isf.mortuary.model.IntervalJours;
import org.isf.mortuary.model.PlagePrixMorgue;
import org.isf.mortuary.service.DeathIoOperations;
import org.isf.mortuary.service.PriceIoOperations;
import org.isf.parameters.manager.Param;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.exception.OHException;
import org.joda.time.LocalDate;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class MortuaryBrowserManager {
	private DeathIoOperations ioOperations =  new DeathIoOperations();
	PriceIoOperations pio = new PriceIoOperations();
	private ArrayList<Death> deaths = new ArrayList<Death>(); 
	
	public boolean newDeath(Death death) throws OHException {
		boolean result = false;
		try {
			result = ioOperations.newDeath(death);
		} catch (OHException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.mortuaryedit.error.patientalreadydied"));
			return false;
		}catch (SQLException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"));
			return false;
		}
		return result;
	}
	
	public ArrayList<Death> getDeaths() throws OHException{
		try {
			deaths =  ioOperations.getDeaths();
		}catch(OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return deaths;
	}

	public boolean updateDeath(Death death) throws OHException {
		boolean result = false;
		/**
		 * 
		 * method that update an existing Death in the db
		 * @param patient
		 * @return true - if the existing Death has been updated
		 */
				try {
					result = ioOperations.updateDeath(death);
				}catch (OHException e) {
					e.printStackTrace();
					throw new OHException(MessageBundle.getMessage("angal.mortuaryedit.error.patientalreadydied"));
			}
			return result;
		}
	public boolean deleteDeath(int id) {
		boolean result = false;
		/**
		 * 
		 * method that update an existing Death in the db
		 * @param patient
		 * @return true - if the existing Death has been updated
		 */
			try {
				result = ioOperations.deleteDeath(id);
			} catch (OHException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				return false;
			}
			return result;
		}
	/**
	 * return the number of row in a Death table given a death reason
	 * @param motif
	 * @return the row numbers
	 */
	public int getMortuaryTotalRows() {
		try {
			return ioOperations.getMortuaryTotalRows();
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}
	
	/**
	 * Returns all the deaths with the specified description.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @param description the medical description.
	 * @return all the Deaths with the specified description.
	 */
	public ArrayList<Death> getDeaths(int start_index, int page_size) {
		try {
			return ioOperations.getDeaths(start_index, page_size);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public ArrayList<Death> getDeaths(int motif, String pavillon, int pat, GregorianCalendar dateFrom,
			GregorianCalendar dateTo, boolean isEntree, boolean isSortie, int start_index, int page_size) {
		try {
			return ioOperations.getDeaths(motif, pavillon, pat, dateFrom, dateTo, isEntree, isSortie, start_index, page_size);
		} catch (OHException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}

	public int getMortuaryTotalRows(int motif, String pavillon, int pat, GregorianCalendar dateFrom,
			GregorianCalendar dateTo, boolean isEntree, boolean isSortie) {
		try {
			return ioOperations.getMortuaryTotalRows(motif, pavillon, pat, dateFrom, dateTo, isEntree, isSortie);
		} catch (OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
	}

	public void generateDeathCertificate(int deathID, String jasperFileName, boolean show, boolean c) {
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
			parameters.put("deathID", String.valueOf(deathID)); // real param
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
			
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(deathID) + ".pdf";
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
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}
	}

	public void generateEtatSejours(Death death, String jasperFileName, boolean show, boolean c) {
		try {
		
			GregorianCalendar dateSortie = death.getDateSortieProvisoire();
			
			if(dateSortie == null) {
				JOptionPane.showMessageDialog(				
	                    null,
	                    MessageBundle.getMessage("angal.mortuaryedit.providedateleaving"),
	                    MessageBundle.getMessage("angal.hospital"),
	                    JOptionPane.PLAIN_MESSAGE);
				return;
			}
			
			GregorianCalendar dateEntree = death.getDateEntree();
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		   
		    String date1 = fmt.format(dateEntree.getTime());
		    //fmt.setCalendar(death.getDateEntree());
		    
		    String date2 = fmt.format(dateSortie.getTime());
		    //fmt.setCalendar(dateSortie); 
		    
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
			parameters.put("deathID", String.valueOf(death.getId())); // real param
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
			parameters.put("PAT_NAME", death.getPatientName());
			parameters.put("DECES_DATE", date1);
			parameters.put("DECES_DATE_SORTIE", date2);
			
			int nbjours = 0;
			float total = 0;
			
			ArrayList<EtatPrixSejours> etats = getEtatPrix(death);
			if(etats == null) {
				return;
			}
			if(etats != null) {
				for(EtatPrixSejours state: etats) {
					nbjours += state.getNombre_jours();
					total += state.getMontant();
				}
			}
			parameters.put("TOTAL_JOURS", nbjours);
			parameters.put("TOTAL_MONTANT", total);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(etats);
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");

			File jasperFile = new File(sbFilename.toString());

			//Connection conn = DbSingleConn.getConnection();

			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(death.getId()) + ".pdf";
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
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}
		
	}

	private ArrayList<EtatPrixSejours> getEtatPrix(Death death) {
		ArrayList<EtatPrixSejours> etats = new ArrayList<EtatPrixSejours>();
		ArrayList<PlagePrixMorgue> plages = new ArrayList<PlagePrixMorgue>();
		EtatPrixSejours etatPrix = null;
		GregorianCalendar dateSortie = death.getDateSortieProvisoire();
		
		GregorianCalendar dateEntree = death.getDateEntree();
		try {
			int n = pio.dateDiff(dateSortie, dateEntree) + 1;			
			plages = pio.getPrices();			
			int i = 0;
			if(plages == null) {
				JOptionPane.showMessageDialog(				
                        null,
                        MessageBundle.getMessage("angal.distype.nondefineprice"),
                        MessageBundle.getMessage("angal.hospital"),
                        JOptionPane.PLAIN_MESSAGE);
				return null;	
			}
			final int N = n;
			while(n > 0 && i < plages.size()) {
				PlagePrixMorgue plage = plages.get(i);
				int k = i+1;
				int nb = (plage.getNbJourMax() - plage.getNbJourmin() + 1);
				int min0 = 1;
				if(min0 < plage.getNbJourmin() && i==0) {					
					JOptionPane.showMessageDialog(			
	                        null,
	                        MessageBundle.getMessage("angal.distype.nondefineprice") + " "+MessageBundle.getMessage("angal.distype.for")+ " "+min0+ " "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+(plage.getNbJourmin() -1)+" "+MessageBundle.getMessage("angal.distype.days"),
	                        MessageBundle.getMessage("angal.hospital"),
	                        JOptionPane.PLAIN_MESSAGE);
					return null;
				}
					
				if(k < plages.size()) {
					PlagePrixMorgue plage2 = plages.get(k);
					if(plage.getNbJourMax() < (plage2.getNbJourmin() -1) && (plage2.getNbJourmin() -1) == (plage.getNbJourMax() + 1)) {
						JOptionPane.showMessageDialog(			
		                        null,
		                        MessageBundle.getMessage("angal.distype.nondefineprice") + " "+MessageBundle.getMessage("angal.distype.for")+ " "+(plage2.getNbJourmin() - 1)+" "+MessageBundle.getMessage("angal.distype.days"),
		                        MessageBundle.getMessage("angal.hospital"),
		                        JOptionPane.PLAIN_MESSAGE);
						return null;
					}
					else if(plage.getNbJourMax() < (plage2.getNbJourmin() -1) && (plage2.getNbJourmin() -1) > (plage.getNbJourMax() + 1)) {
						JOptionPane.showMessageDialog(			
		                        null,
		                        MessageBundle.getMessage("angal.distype.nondefineprice") + " "+MessageBundle.getMessage("angal.distype.for")+ " "+(plage.getNbJourMax() +1)+ " "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+(plage2.getNbJourmin() - 1)+" "+MessageBundle.getMessage("angal.distype.days"),
		                        MessageBundle.getMessage("angal.hospital"),
		                        JOptionPane.PLAIN_MESSAGE);
						return null;
					}
				}
				if(n <= nb) {
					String desc = plage.getDescription()+" "+MessageBundle.getMessage("angal.mortuarybrowser.from")+" "+plage.getNbJourmin()+" "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+plage.getNbJourMax()+" "+MessageBundle.getMessage("angal.mortuarybrowser.days");
					etatPrix = new EtatPrixSejours(desc, plage.getPrixJournalier(),  n, n* plage.getPrixJournalier());
					etats.add(etatPrix);
					n = 0;
					i++;
				}
				else {
					String desc = plage.getDescription()+" "+MessageBundle.getMessage("angal.mortuarybrowser.from")+" "+plage.getNbJourmin()+" "+MessageBundle.getMessage("angal.mortuarybrowser.to")+" "+plage.getNbJourMax()+" "+MessageBundle.getMessage("angal.mortuarybrowser.days");
					etatPrix = new EtatPrixSejours(desc, plage.getPrixJournalier(), nb, nb* plage.getPrixJournalier());
					etats.add(etatPrix);
					n = n - nb;
					int j = i+1;
					if(j == plages.size() && n>0) {
						JOptionPane.showMessageDialog(	
		                        null,
		                        MessageBundle.getMessage("angal.distype.nondefinepricemore") + plage.getNbJourMax()+ " " +MessageBundle.getMessage("angal.distype.days"),
		                        MessageBundle.getMessage("angal.hospital"),
		                        JOptionPane.PLAIN_MESSAGE);
						return null;	
					}
					i++;
				}
			}
		}catch(OHException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
		return etats;
	}
	
	public boolean patientIsDied(int code) {
		boolean result = false;
		/**
		 * 
		 * method that update an existing Death in the db
		 * @param patient
		 * @return true - if the existing Death has been updated
		 */
			try {
				result = ioOperations.patientIsDied(code);
			} catch (OHException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				return false;
			}
			return result;
		}
	

}
