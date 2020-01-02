package org.isf.medicals.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.parameters.manager.Param;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.jobjects.MovementReportBean;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

import com.toedter.calendar.JDateChooser;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

//public class MedicalEdit extends JDialog {
//public class MedicalEdit extends JFrame {
public class StockSheet extends JDialog {
	

	private static EventListenerList MedicalEditListeners = new EventListenerList();
	 
	public interface MedicalEditListener extends EventListener {
	        public void MedicalUpdated(AWTEvent e);
	        public void MedicalInserted(AWTEvent e);
	    }

	    public static void addMedicalListener(MedicalEditListener l) {
	    	MedicalEditListeners.add(MedicalEditListener.class, l);
	    }

	    public static void removeMedicalListener(MedicalEditListener listener) {
	    	MedicalEditListeners.remove(MedicalEditListener.class, listener);
	    }

	    private void fireMedicalInserted() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
	        	
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};
				
	        EventListener[] listeners = MedicalEditListeners.getListeners(MedicalEditListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MedicalEditListener)listeners[i]).MedicalInserted(event);
	    }
	    
	    private void fireMedicalUpdated() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};

	        EventListener[] listeners = MedicalEditListeners.getListeners(MedicalEditListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MedicalEditListener)listeners[i]).MedicalUpdated(event);
	    }
	    
	    
	   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int CODE_MAX_LENGTH = 25;

	private JPanel jContentPane = null;

	private JPanel dataPanel = null;

	private JPanel buttonPanel = null;

	private JButton cancelButton = null;

	private JButton okButton = null;

	private JComboBox jComboBoxDestination;
	
	private JTextField pcsperpckField = null;

	private VoLimitedTextField anneeTextField = null;
	
	private VoLimitedTextField codeTextField = null;

	private JTextField minQtiField = null;

	private JLabel moisLabel = null;

	private Medical medical = null;

	private JDateChooser jCalendarTo;
	private JDateChooser jCalendarFrom;
	private GregorianCalendar dateFrom = TimeTools.getServerDateTime();
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private GregorianCalendar dateToday0 = TimeTools.getServerDateTime();
	private GregorianCalendar dateToday24 = TimeTools.getServerDateTime();
	private JLabel jLabelTo;
	private JLabel jLabelFrom;
	private JLabel jLabelWard;
	
	/**
	 * 
	 * This is the default constructor; we pass the arraylist and the
	 * selectedrow because we need to update them
	 */
	public StockSheet(Medical old, boolean inserting,JFrame owner) {
		super(owner,true);
		//super();
	//	insert = inserting;
		medical = old; // medical will be used for every operation
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		//this.setBounds(300, 300, 350, 240);
		this.setContentPane(getJContentPane());
		this.setTitle(MessageBundle.getMessage("angal.stocksheet.title"));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.CENTER); // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH); // Generated
		}
		return jContentPane;
	}

	/**
	 * This method initializes dataPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS)); // Generated
			
			moisLabel = new JLabel();
			moisLabel.setText(MessageBundle.getMessage("angal.stocksheet.month")); // Generated
			moisLabel.setAlignmentX(CENTER_ALIGNMENT);
			dataPanel.add(getJLabelWard());
			dataPanel.add(getJComboBoxDestination()); 
			dataPanel.add(getJLabelFrom());
			dataPanel.add(getJCalendarFrom());
			dataPanel.add(getJLabelTo());
			dataPanel.add(getJCalendarTo());
		
		}
		return dataPanel;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null); // Generated
			buttonPanel.add(getCancelButton(), null); // Generated
		}
		return buttonPanel;
	}

	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setText(MessageBundle.getMessage("angal.billbrowser.to")); //$NON-NLS-1$
		}
		return jLabelTo;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel")); // Generated
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.common.ok")); // Generated
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
						MedicalBrowsingManager manager = new MedicalBrowsingManager();			
						MovBrowserManager movManager = new MovBrowserManager();
						try {
							// Check destination
							Ward ward = (Ward)jComboBoxDestination.getSelectedItem();
							if (ward.getCode().equals("MP")) {
								Double cmm = manager.getMediumQuantity(medical.getCode());
								ArrayList<MovementReportBean> movementReportBeans = movManager.getMovementsMagasin(medical.getCode(), dateFrom, dateTo);
							
								java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
								generateStockSheetReport(sdf.format(dateFrom.getTime()), sdf.format(dateTo.getTime()), medical, "StockSheet", false, cmm, movementReportBeans); 
			
							}else{
								Double cmm = manager.getMediumQuantity(medical.getCode());
								ArrayList<MovementReportBean> movementReportBeans = movManager.getMovements(medical.getCode(), dateFrom, dateTo, ward.getCode());		
								java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
								generateStockSheetReport(sdf.format(dateFrom.getTime()), sdf.format(dateTo.getTime()), medical, "StockSheet", false, cmm, movementReportBeans); 
							}			
							dispose();
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.medicals.insertavalidvalue"));
					}
				}
			});
		}
		return okButton;
	}

	public void generateStockSheetReport(String fromDateStr, String toDateStr, Medical medical, String jasperFileName, boolean toCSV, Double cmm, ArrayList<MovementReportBean> movementReportBeans) {
		try {
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
			Hospital hosp = hospManager.getHospital();
			JRBeanCollectionDataSource beanMovementDataSource = new JRBeanCollectionDataSource(movementReportBeans);
			parameters.put("Hospital", hosp.getDescription());
			parameters.put("Address", hosp.getAddress());
			parameters.put("City", hosp.getCity());
			parameters.put("Email", hosp.getEmail());
			parameters.put("Telephone", hosp.getTelephone());
			parameters.put("medicalCODE", String.valueOf(medical.getProd_code())); // real param
			parameters.put("REPORT_RESOURCE_BUNDLE", MessageBundle.getBundle());
			parameters.put("fromdate", fromDateStr);
			parameters.put("todate", toDateStr);
			parameters.put("cmm", cmm);
			parameters.put("niveauMax", cmm * 3);
			parameters.put("stockSecurite", cmm / 2);
			parameters.put("conditioning", medical.getConditioning());
			parameters.put("shape", medical.getShape());
			parameters.put("dosing", medical.getDosing());
			parameters.put("conditioning", medical.getConditioning());
			parameters.put("productName", medical.getDescription());
			parameters.put("productID", medical.getCode());   
			parameters.put("productMinQuantity", medical.getMinqty() +"");
			StringBuilder sbFilename = new StringBuilder();
			sbFilename.append("rpt");
			sbFilename.append(File.separator);
			sbFilename.append(jasperFileName);
			sbFilename.append(".jasper");
			File jasperFile = new File(sbFilename.toString());
			
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanMovementDataSource);
			
			String PDFfile = "rpt/PDF/" + jasperFileName + "_" + String.valueOf(medical.getProd_code()) + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, PDFfile);
			JasperViewer.viewReport(jasperPrint, false);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method initializes moisTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private VoLimitedTextField getAnneeTextField() {
		if (anneeTextField == null) {
			anneeTextField = new VoLimitedTextField(100,50);
			anneeTextField.setText(Calendar.getInstance().get(Calendar.YEAR) + "");
		}
		return anneeTextField;
	}
	
	private JDateChooser getJCalendarFrom() {
		if (jCalendarFrom == null) {
			dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateFrom.set(GregorianCalendar.MINUTE, 0);
			dateFrom.set(GregorianCalendar.SECOND, 0);
			dateToday0.setTime(dateFrom.getTime());
			jCalendarFrom = new JDateChooser(dateFrom.getTime()); // Calendar
			String beginingDate="01/01/19";  
			SimpleDateFormat formatter3=new SimpleDateFormat("dd/MM/yy");  
			 Date date3 = new Date();
			try {
				date3 = formatter3.parse(beginingDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			jCalendarFrom.setMinSelectableDate(date3);
			jCalendarFrom.getCalendarButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				}
			});
			jCalendarFrom.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarFrom.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			jCalendarFrom.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarFrom.setDate((Date) evt.getNewValue());
					dateFrom.setTime((Date) evt.getNewValue());
					dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
					dateFrom.set(GregorianCalendar.MINUTE, 0);
					dateFrom.set(GregorianCalendar.SECOND, 0);
				}
			});
		}			
		return jCalendarFrom;
	}

	private JDateChooser getJCalendarTo() {
		if (jCalendarTo == null) {
			dateTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
			dateTo.set(GregorianCalendar.MINUTE, 59);
			dateTo.set(GregorianCalendar.SECOND, 59);
			dateToday24.setTime(dateTo.getTime());
			jCalendarTo = new JDateChooser(dateTo.getTime()); // Calendar
			jCalendarTo.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarTo.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			jCalendarTo.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarTo.setDate((Date) evt.getNewValue());
					dateTo.setTime((Date) evt.getNewValue());
					dateTo.set(GregorianCalendar.HOUR_OF_DAY, 23);
					dateTo.set(GregorianCalendar.MINUTE, 59);
					dateTo.set(GregorianCalendar.SECOND, 59);
				}
			});
		}
		return jCalendarTo;
	}
	
	private JLabel getJLabelFrom() {
		if (jLabelFrom == null) {
			jLabelFrom = new JLabel();
			jLabelFrom.setText(MessageBundle.getMessage("angal.billbrowser.from")); //$NON-NLS-1$
		}
		return jLabelFrom;
	}
	
	private JLabel getJLabelWard() {
		if (jLabelWard == null) {
			jLabelWard = new JLabel();
			jLabelWard.setText(MessageBundle.getMessage("angal.inventory.selectward")); //$NON-NLS-1$
		}
		return jLabelWard;
	}
	private JComboBox getJComboBoxDestination() {
		Ward w = new Ward();
		w.setCode("MP");
		w.setDescription("MAGASIN CENTRAL");
		if (jComboBoxDestination == null) {
			jComboBoxDestination = new JComboBox();
			jComboBoxDestination.addItem(w); //$NON-NLS-null
			Ward pharmacie = null;
			WardBrowserManager wardMan = new WardBrowserManager();
			ArrayList<Ward> wards = wardMan.getWards();
			for (Ward ward : wards) {
				if (Param.bool("INTERNALPHARMACIES")) {
					if (ward.isPharmacy())
						jComboBoxDestination.addItem(ward);
				} else {
					jComboBoxDestination.addItem(ward);
				}
				if(ward.getCode().equals("P")) pharmacie = ward;
			}
			jComboBoxDestination.setSelectedItem(pharmacie);
		}
		return jComboBoxDestination;
	}

}
