package org.isf.opd.gui;

/*------------------------------------------
 * OpdEdit - add/edit an OPD registration
 * -----------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  - first beta version 
 * 07/11/2006 - ross - renamed from Surgery 
 *                   - added visit date, disease 2, diseas3
 *                   - disease is not mandatory if re-attendance
 * 			         - version is now 1.0 
 * 28/05/2008 - ross - added referral to / referral from check boxes
 * 12/06/2008 - ross - added patient data
 * 					 - fixed error on checking "male"/"female" option: should check after translation
 * 					 - version is not a resource into the boundle, is locale to the form
 *                   - form rearranged in x,y coordinates 
 * 			         - version is now 1.1 
 * 26/08/2008 - teo  - added patient chooser 
 * 01/09/2008 - alex - added constructor for call from Admission
 * 					 - set Patient oriented OPD
 * 					 - history management for the patients
 * 					 - version now is 1.2
 * 01/01/2009 - Fabrizio - modified age fields back to Integer type
 * 13/02/2009 - Alex - added possibility to edit patient through EditButton
 * 					   added Edit.png icon
 * 					   fixed a bug on the first element in the comboBox
 * 13/02/2009 - Alex - added trash button for resetting searchfield
 * 03/13/2009 - Alex - lastOpdVisit appears at the bottom
 * 					   added control on duplicated diseases
 * 					   added re-attendance checkbox for a clear view
 * 					   new/re-attendance managed freely
 * 07/13/2009 - Alex - note field for the visit recall last visit note when start OPD from
	  				   Admission and added Note even in Last OPD Visit
	  				   Extended patient search to patient code
 *------------------------------------------*/


import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;

import org.isf.admission.gui.AdmittedPatientBrowser;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.distype.manager.DiseaseTypeBrowserManager;
import org.isf.distype.model.DiseaseType;
import org.isf.examination.gui.PatientExaminationEdit;
import org.isf.examination.model.GenderPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationOperations;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.gui.LabBrowser;
import org.isf.medicalinventory.gui.InventoryEdit;
import org.isf.menu.gui.MainMenu;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.therapy.gui.TherapyEdit;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.ValidationPatientGroup;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.utils.time.RememberDates;
import org.isf.utils.time.TimeTools;

import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingConstants;

//public class OpdEditExtended extends ModalJFrame 
public class OpdEditExtended extends JDialog implements PatientInsertExtended.PatientListener, PatientInsert.PatientListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//@Override
	public void patientInserted(AWTEvent e) {
		//owner.dispose();
		//new OpdBrowser((Patient)e.getSource());
		opdPatient = (Patient) e.getSource();
		setPatient(opdPatient);
		jComboPatResult.addItem(opdPatient);
		jComboPatResult.setSelectedItem(opdPatient);
		jPatientEditButton.setEnabled(true);
	}

	//@Override
	public void patientUpdated(AWTEvent e) {
		setPatient(opdPatient);
	}

	private EventListenerList surgeryListeners = new EventListenerList();
	
	public interface SurgeryListener extends EventListener {
		public void surgeryUpdated(AWTEvent e);
		public void surgeryInserted(AWTEvent e);
	}
	
	public void addSurgeryListener(SurgeryListener l) {
		surgeryListeners.add(SurgeryListener.class, l);
	}
	
	public void removeSurgeryListener(SurgeryListener listener) {
		surgeryListeners.remove(SurgeryListener.class, listener);
	}
	
	private void fireSurgeryInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = surgeryListeners.getListeners(SurgeryListener.class);
		for (int i = 0; i < listeners.length; i++)
			((SurgeryListener)listeners[i]).surgeryInserted(event);
	}
	private void fireSurgeryUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
		
		EventListener[] listeners = surgeryListeners.getListeners(SurgeryListener.class);
		for (int i = 0; i < listeners.length; i++)
			((SurgeryListener)listeners[i]).surgeryUpdated(event);
	}
	
	private static final String VERSION="1.3"; 

	private static final String LastOPDLabel = "<html><i>"+MessageBundle.getMessage("angal.opd.lastopdvisitm")+"</i></html>:";
	private static final String LastNoteLabel = "<html><i>"+MessageBundle.getMessage("angal.opd.lastopdnote")+"</i></html>:";
	
	private JPanel jPanelMain = null;
	private JPanel jPanelNorth;
	private JPanel jPanelCentral;
	private JPanel jPanelData = null;
	private JPanel jPanelButtons = null;
	private JLabel jLabelDate = null;
	private JLabel jLabelDiseaseType1 = null;
	private JLabel jLabelDisease1 = null;
	private JLabel jLabelDis2 = null;
	private JLabel jLabelDis3 = null;

	private JComboBox diseaseTypeBox = null;
	private JComboBox diseaseBox1 = null;
	private JComboBox diseaseBox2 = null;
	private JComboBox diseaseBox3 = null;
	private JLabel jLabelAge = null;
	private JLabel jLabelSex = null;
	private GregorianCalendar dateIn = null;
	private DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
	private JDateChooser OpdDateFieldCal = null; 
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JButton jButtonExamination = null;
	
	private JButton jButtonTherapy = null;
	private JButton jButtonExams = null;
	
	private JCheckBox rePatientCheckBox = null;
	private JCheckBox newPatientCheckBox = null;
	private JCheckBox referralToCheckBox = null;
	private JCheckBox referralFromCheckBox = null;
	private JPanel jPanelSex = null;
	private ButtonGroup group=null;

	private JLabel jLabelfirstName = null;
	private JLabel jLabelsecondName = null;
	private JLabel jLabeladdress = null;
	private JLabel jLabelcity = null;
	private JLabel jLabelnextKin = null;

	private JPanel jPanelPatient = null;

	private VoLimitedTextField jFieldFirstName= null;
	private VoLimitedTextField jFieldSecondName= null;
	private VoLimitedTextField jFieldAddress= null;
	private VoLimitedTextField jFieldCity= null;
	private VoLimitedTextField jFieldNextKin= null;
	private VoLimitedTextField jFieldAge = null;

	private Opd opd;
	private boolean insert;
	private DiseaseType allType= new DiseaseType(MessageBundle.getMessage("angal.opd.alltype"),MessageBundle.getMessage("angal.opd.alltype"));

	//ADDED : teo
	private VoLimitedTextField jTextPatientSrc;
	private JComboBox jComboPatResult;
	private JLabel jSearchLabel = null;
	private JRadioButton radiof;
	private JRadioButton radiom;
	//ADDED : alex
	private JButton jPatientEditButton = null;
	private JButton jSearchButton = null;
	private JLabel jLabelLastOpdVisit = null;
	private JLabel jFieldLastOpdVisit = null;
	private JLabel jLabelLastOpdNote = null;
	private JLabel jFieldLastOpdNote = null;
	
	private Patient opdPatient = null;
	private JPanel jNotePanel = null;
	private JScrollPane jNoteScrollPane = null;
	private JScrollPane jNoteScrollPane2 = null;
	private JTextArea jNoteTextArea = null;
	private JTextArea jNoteTextAreaComplaint = null;
	private JPanel jPatientNotePanel = null;
	private JScrollPane jPatientScrollNote = null;
	private JTextArea jPatientNote = null;
	private JPanel jOpdNumberPanel = null;
	private JTextField jOpdNumField = null;
	private JLabel jOpdNumLabel = null;
	
	/*
	 * Managers and Arrays
	 */
	private DiseaseTypeBrowserManager typeManager = new DiseaseTypeBrowserManager();
	private DiseaseBrowserManager manager = new DiseaseBrowserManager();
	private ArrayList<DiseaseType> types = typeManager.getDiseaseType();
	private ArrayList<Disease> diseasesOPD = manager.getDiseaseOpd();
	private ArrayList<Disease> diseasesAll = manager.getDiseaseAll();
	private OpdBrowserManager opdManager = new OpdBrowserManager();
	private ArrayList<Opd> opdArray = new ArrayList<Opd>();
	private PatientBrowserManager patBrowser = new PatientBrowserManager();
	private ArrayList<Patient> pat = new ArrayList<Patient>();

	private Disease lastOPDDisease1;
	private JLabel nextVisitLabel;
	private JDateChooser opdNextVisitDate ;
	//private GregorianCalendar opdNextVisitDateGre= TimeTools.getServerDateTime();
	private JTextArea textArea;
	private JTextField referralFromtextField;
	private JTextField referralTotextField;
	private JCheckBox isPregnantCheckBox;
	private JLabel isPregnantLabel;
	private JTextField searchDia1textField;
	private JTextField searchDia2textField;
	private JTextField searchDia3textField;
	private JButton btnSearchDia1Button;
	private JButton btnSearchDia2Button;
	private JButton btnSearchDia3Button;
	
	/**
	 * This method initializes 
	 * @wbp.parser.constructor
	 * 
	 */
	public OpdEditExtended(JFrame owner, Opd old, boolean inserting) {
		super(owner, true);
		//super();
		opd=old;
		insert=inserting;
		if(!insert) {
			if (opd.getpatientCode() != 0) { 
				PatientBrowserManager patBrowser = new PatientBrowserManager();
				opdPatient = patBrowser.getPatientAll(opd.getpatientCode());
			} else { //old OPD has no PAT_ID => Create Patient from OPD
				opdPatient = new Patient(opd);
				opdPatient.setCode(0);
			}
		}
		initialize();
	}
	
	public OpdEditExtended(JFrame owner, Opd opd, Patient patient, boolean inserting) {
		super(owner, true);
		//super();
		this.opd = opd;
		opdPatient = patient;
		insert=inserting;
		initialize();
	}
	
	private void setPatient(Patient p) {
			jFieldAge.setText(TimeTools.getFormattedAge(p.getBirthDate()));
			jFieldFirstName.setText(p.getFirstName());
			jFieldAddress.setText(p.getAddress());
			jFieldCity.setText(p.getCity());
			jFieldSecondName.setText(p.getSecondName());
			jFieldNextKin.setText(p.getNextKin());
			jPatientNote.setText(opdPatient.getNote());
			setMyMatteBorder(jPanelPatient, MessageBundle.getMessage("angal.opd.patient") + " (code: " + opdPatient.getCode() + ")");
			
			if(p.getSex().equals("M") ) {
				//Alex: SET SELECTED INSTEAD OF DOCLICK(), no listeners
				radiof.setSelected(false);	
				radiom.setSelected(true);				
			} else if(p.getSex().equals("F")) {
				//Alex: SET SELECTED INSTEAD OF DOCLICK(), no listeners
				radiom.setSelected(false);
				radiof.setSelected(true);			
			}
			if (insert) getLastOpd(p.getCode());
	}
	
	private void resetPatient() {
		jFieldAge.setText("");
		jFieldFirstName.setText("");
		jFieldAddress.setText("");
		jFieldCity.setText("");
		jFieldSecondName.setText("");
		jFieldNextKin.setText("");
		jPatientNote.setText("");
		setMyMatteBorder(jPanelPatient, MessageBundle.getMessage("angal.opd.patient"));
		radiom.setSelected(true);
		opdPatient=null;
	}
	
	//Alex: Resetting history from the last OPD visit for the patient
	private boolean getLastOpd(int code)
	{
		Opd lastOpd = opdManager.getLastOpd(code);
		
		if (lastOpd == null) {
			newPatientCheckBox.setSelected(true);
			rePatientCheckBox.setSelected(false);
			jLabelLastOpdVisit.setText("");
			jFieldLastOpdVisit.setText("");
			jLabelLastOpdNote.setText("");
			jFieldLastOpdNote.setText("");
			jNoteTextArea.setText("");
			
			return false;
		}
		
		lastOPDDisease1 = null;
		Disease lastOPDDisease2 = null;
		Disease lastOPDDisease3 = null;
		
		for (Disease disease : diseasesOPD) {
			
			if (lastOpd.getDisease() != null && disease.getCode().compareTo(lastOpd.getDisease()) == 0) {
					lastOPDDisease1 = disease;
			}
			if (lastOpd.getDisease2() != null && disease.getCode().compareTo(lastOpd.getDisease2()) == 0) {
				lastOPDDisease2 = disease;
			}
			if (lastOpd.getDisease3() != null && disease.getCode().compareTo(lastOpd.getDisease3()) == 0) {
				lastOPDDisease3 = disease;
			}
		}
		
		StringBuilder lastOPDDisease = new StringBuilder();
		lastOPDDisease.append(MessageBundle.getMessage("angal.opd.on")).append(" ").append(currentDateFormat.format(lastOpd.getVisitDate().getTime())).append(" - ");
		if (lastOPDDisease1 != null) {
			setAttendance();
			lastOPDDisease.append(lastOPDDisease1.getDescription());
		} 
		if (lastOPDDisease2 != null) lastOPDDisease.append(", ").append(lastOPDDisease2.getDescription());
		if (lastOPDDisease3 != null) lastOPDDisease.append(", ").append(lastOPDDisease3.getDescription());
		jLabelLastOpdVisit.setText(LastOPDLabel);
		jFieldLastOpdVisit.setText(lastOPDDisease.toString());
		jLabelLastOpdNote.setText(LastNoteLabel);
		String note = lastOpd.getNote();
		jFieldLastOpdNote.setText(note.equals("") ? MessageBundle.getMessage("angal.opd.nonote") : note);
		jNoteTextArea.setText(lastOpd.getNote());
		
		return true;		
	}
	
	private void setAttendance() {
		if (!insert) return;
		Object selectedObject = diseaseBox1.getSelectedItem();
		if(selectedObject instanceof Disease) {
			Disease disease = (Disease) selectedObject;
			if (lastOPDDisease1 != null && disease.getCode().equals(lastOPDDisease1.getCode())) {
				rePatientCheckBox.setSelected(true);
				newPatientCheckBox.setSelected(false);
			} else {
				rePatientCheckBox.setSelected(false);
				newPatientCheckBox.setSelected(true);
			}
		}
	}

	/**
	 * @return the jPanelNorth
	 */
	private JPanel getjPanelNorth() {
		if (jPanelNorth == null) {
			String referralTo="";
			String referralFrom="";
			jPanelNorth = new JPanel(new FlowLayout());
			rePatientCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.reattendance"));
			newPatientCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.newattendance"));
			newPatientCheckBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (newPatientCheckBox.isSelected()) {
						newPatientCheckBox.setSelected(true);
						rePatientCheckBox.setSelected(false);
					} else {
						newPatientCheckBox.setSelected(false);
						rePatientCheckBox.setSelected(true);
					}
				}
			});
			rePatientCheckBox.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (rePatientCheckBox.isSelected()) {
						rePatientCheckBox.setSelected(true);
						newPatientCheckBox.setSelected(false);
					} else {
						newPatientCheckBox.setSelected(true);
						rePatientCheckBox.setSelected(false);
					}
				}
			});
			jPanelNorth.add(rePatientCheckBox);
			jPanelNorth.add(newPatientCheckBox);
			if(!insert){
				if (opd.getNewPatient().equals("N"))
					newPatientCheckBox.setSelected(true);
				else
					rePatientCheckBox.setSelected(true);
			}
			referralFromCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.referral.from"));
			/***************/
			referralFromCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean selected = referralFromCheckBox.isSelected();
					if(selected){
						//referralFromtextField.setVisible(true);
						referralFromtextField.setEnabled(true);
						referralFromtextField.requestFocus();
					}
					else{
						referralFromtextField.setEnabled(false);
						//referralFromtextField.setVisible(false);
						//referralFromtextField.setText("");
					}
				}
			});
			/***************/
			
			jPanelNorth.add(referralFromCheckBox);
			if(!insert){				
				referralFrom = opd.getReferralFrom();
				if (referralFrom == null) referralFrom="";
				if (referralFrom.equals("R"))referralFromCheckBox.setSelected(true);
			}
			jPanelNorth.add(getReferralFromtextField());
			referralToCheckBox = new JCheckBox(MessageBundle.getMessage("angal.opd.referral.to"));
			/***************/
			referralToCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean selected = referralToCheckBox.isSelected();
					if(selected){
						//referralTotextField.setVisible(true);
						referralTotextField.setEnabled(true);
						referralTotextField.requestFocus();
					}
					else{
						//referralTotextField.setVisible(false);
						referralTotextField.setEnabled(false);
						//referralTotextField.setText("");
					}
				}
			});
			/***************/
			
			jPanelNorth.add(referralToCheckBox);
			jPanelNorth.add(getReferralTotextField());
			
			if(!insert){				
				referralTo = opd.getReferralTo();
				if (referralTo == null) referralTo="";
				if (referralTo.equals("R")){
					referralToCheckBox.setSelected(true);
				}
			}
		}
		return jPanelNorth;
	}

	/**
	 * @return the jPanelCentral
	 */
	private JPanel getjPanelCentral() {
		if (jPanelCentral == null) {
			jPanelCentral = new JPanel();
			jPanelCentral.setLayout(new BoxLayout(jPanelCentral, BoxLayout.Y_AXIS));
			jPanelCentral.add(getDataPanel());
			jPanelCentral.add(Box.createVerticalStrut(10));
			jPanelCentral.add(getJPanelPatient());
		}
		return jPanelCentral;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setContentPane(getMainPanel());
		pack();
		setMinimumSize(this.getSize());
		setLocationRelativeTo(null);

		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.opd.newopdregistration")+"("+VERSION+")");
		} else {
			this.setTitle(MessageBundle.getMessage("angal.opd.editopdregistration")+"("+VERSION+")");
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//this.setVisible(true);
		if (insert) {
			jTextPatientSrc.requestFocusInWindow();
		} else {
			jNoteTextArea.requestFocusInWindow();
		}
		this.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				pat.clear();
				opdArray.clear();
				diseasesAll.clear();
				diseasesOPD.clear();
				types.clear();
				jComboPatResult.removeAllItems();
				diseaseTypeBox.removeAllItems();
				diseaseBox1.removeAllItems();
				diseaseBox2.removeAllItems();
				diseaseBox3.removeAllItems();
				dispose();
			}			
		});
		//modal exclude
				if(!Param.bool("WITHMODALWINDOW")){
					setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
				}
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (jPanelMain == null) {
			jPanelMain = new JPanel();
			jPanelMain.setLayout(new BorderLayout());
			jPanelMain.add(getjPanelNorth(), BorderLayout.NORTH);
			jPanelMain.add(getJNotePanel(), BorderLayout.EAST);
			jPanelMain.add(getjPanelCentral(), BorderLayout.CENTER);
			jPanelMain.add(getJButtonPanel(), BorderLayout.SOUTH);
		}
		return jPanelMain;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDataPanel() {
		if (jPanelData == null) {
			jPanelData = new JPanel();
			GridBagLayout gbl_jPanelData = new GridBagLayout();
			gbl_jPanelData.columnWidths = new int[] {80, 0,40,20,80,20};
			gbl_jPanelData.rowHeights = new int[]{20,20,20,20,20,20,20, 0,20, 0};
			gbl_jPanelData.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 1.0, 0.0};
			gbl_jPanelData.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
			jPanelData.setLayout(gbl_jPanelData);
			
			jLabelDate= new JLabel(MessageBundle.getMessage("angal.opd.attendancedate"));
			GridBagConstraints gbc_jLabelDate = new GridBagConstraints();
			gbc_jLabelDate.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelDate.anchor = GridBagConstraints.WEST;
			gbc_jLabelDate.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDate.gridy = 0;
			gbc_jLabelDate.gridx = 0;
			jPanelData.add(jLabelDate, gbc_jLabelDate);
			GridBagConstraints gbc_DateFieldCal = new GridBagConstraints();
			gbc_DateFieldCal.fill = GridBagConstraints.HORIZONTAL;
			gbc_DateFieldCal.insets = new Insets(5, 5, 5, 5);
			gbc_DateFieldCal.gridy = 0;
			gbc_DateFieldCal.gridx = 2;
			jPanelData.add(getOpdDateFieldCal(), gbc_DateFieldCal);
			GridBagConstraints gbc_jOpdNumberPanel = new GridBagConstraints();
			gbc_jOpdNumberPanel.gridwidth = 1;
			gbc_jOpdNumberPanel.fill = GridBagConstraints.BOTH;
			gbc_jOpdNumberPanel.insets = new Insets(5, 5, 5, 5);
			gbc_jOpdNumberPanel.gridy = 0;			
			gbc_jOpdNumberPanel.gridx = 4;
			
			jPanelData.add(getJOpdNumberPanel(), gbc_jOpdNumberPanel);
			jSearchLabel = new JLabel(MessageBundle.getMessage("angal.opd.search"));
			GridBagConstraints gbc_jSearchLabel = new GridBagConstraints();
			gbc_jSearchLabel.fill = GridBagConstraints.VERTICAL;
			gbc_jSearchLabel.anchor = GridBagConstraints.WEST;
			gbc_jSearchLabel.insets = new Insets(5, 5, 5, 5);
			gbc_jSearchLabel.gridy = 1;
			gbc_jSearchLabel.gridx = 0;
			jPanelData.add(jSearchLabel, gbc_jSearchLabel);
			GridBagConstraints gbc_jTextPatientSrc = new GridBagConstraints();
			gbc_jTextPatientSrc.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextPatientSrc.insets = new Insets(5, 5, 5, 5);
			gbc_jTextPatientSrc.gridy = 1;
			gbc_jTextPatientSrc.gridx = 2;
			jPanelData.add(getJTextPatientSrc(), gbc_jTextPatientSrc);
			GridBagConstraints gbc_jSearchButton = new GridBagConstraints();
			gbc_jSearchButton.insets = new Insets(5, 5, 5, 5);
			gbc_jSearchButton.gridy = 1;
			gbc_jSearchButton.gridx = 3;
			jPanelData.add(getJSearchButton(), gbc_jSearchButton);
			GridBagConstraints gbc_jSearchBox = new GridBagConstraints();
			gbc_jSearchBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_jSearchBox.insets = new Insets(5, 5, 5, 5);
			gbc_jSearchBox.gridy = 1;
			gbc_jSearchBox.gridx = 4;
			jPanelData.add(getSearchBox(), gbc_jSearchBox);
			GridBagConstraints gbc_jPatientEditButton = new GridBagConstraints();
			gbc_jPatientEditButton.insets = new Insets(5, 5, 5, 0);
			gbc_jPatientEditButton.gridy = 1;
			gbc_jPatientEditButton.gridx = 5;
			jPanelData.add(getJPatientEditButton(), gbc_jPatientEditButton);
			
			jLabelDiseaseType1 = new JLabel(MessageBundle.getMessage("angal.opd.diseasetype"));
			GridBagConstraints gbc_jLabelDiseaseType1 = new GridBagConstraints();
			gbc_jLabelDiseaseType1.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelDiseaseType1.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDiseaseType1.anchor = GridBagConstraints.WEST;
			gbc_jLabelDiseaseType1.gridy = 2;
			gbc_jLabelDiseaseType1.gridx = 0;
			jPanelData.add(jLabelDiseaseType1, gbc_jLabelDiseaseType1);
			GridBagConstraints gbc_jLabelDiseaseTypeBox = new GridBagConstraints();
			gbc_jLabelDiseaseTypeBox.insets = new Insets(5, 5, 5, 0);
			gbc_jLabelDiseaseTypeBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelDiseaseTypeBox.gridwidth = 4;
			gbc_jLabelDiseaseTypeBox.gridy = 2;
			gbc_jLabelDiseaseTypeBox.gridx = 2;
			jPanelData.add(getDiseaseTypeBox(), gbc_jLabelDiseaseTypeBox);
			
			//begin  diagnosis
			jLabelDisease1 = new JLabel(MessageBundle.getMessage("angal.opd.diagnosis"));
			GridBagConstraints gbc_jLabelDisease1 = new GridBagConstraints();
			gbc_jLabelDisease1.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelDisease1.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDisease1.anchor = GridBagConstraints.WEST;
			gbc_jLabelDisease1.gridy = 3;
			gbc_jLabelDisease1.gridx = 0;
			jPanelData.add(jLabelDisease1, gbc_jLabelDisease1);
			GridBagConstraints gbc_jLabelDiseaseBox = new GridBagConstraints();
			gbc_jLabelDiseaseBox.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDiseaseBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelDiseaseBox.gridwidth = 2;
			gbc_jLabelDiseaseBox.gridy = 3;
			gbc_jLabelDiseaseBox.gridx = 4;
			jPanelData.add(getDiseaseBox(), gbc_jLabelDiseaseBox);
			GridBagConstraints gbc_searchDia1textField = new GridBagConstraints();
			gbc_searchDia1textField.insets = new Insets(0, 0, 5, 5);
			gbc_searchDia1textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_searchDia1textField.gridx = 2;
			gbc_searchDia1textField.gridy = 3;
			jPanelData.add(getSearchDia1textField(), gbc_searchDia1textField);
			GridBagConstraints gbc_btnSearchDia1Button = new GridBagConstraints();
			gbc_btnSearchDia1Button.insets = new Insets(0, 0, 5, 5);
			gbc_btnSearchDia1Button.gridx = 3;
			gbc_btnSearchDia1Button.gridy = 3;
			jPanelData.add(getBtnSearchDia1Button(), gbc_btnSearchDia1Button);
			
			jLabelDis2 = new JLabel(MessageBundle.getMessage("angal.opd.diagnosisnfulllist"));
			GridBagConstraints gbc_jLabelDis2 = new GridBagConstraints();
			gbc_jLabelDis2.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelDis2.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDis2.anchor = GridBagConstraints.WEST;
			gbc_jLabelDis2.gridy = 4;
			gbc_jLabelDis2.gridx = 0;
			jPanelData.add(jLabelDis2, gbc_jLabelDis2);
			GridBagConstraints gbc_jLabelDisBox2 = new GridBagConstraints();
			gbc_jLabelDisBox2.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDisBox2.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelDisBox2.gridwidth = 2;
			gbc_jLabelDisBox2.gridy = 4;
			gbc_jLabelDisBox2.gridx = 4;
			jPanelData.add(getDiseaseBox2(), gbc_jLabelDisBox2);
			GridBagConstraints gbc_searchDia2textField = new GridBagConstraints();
			gbc_searchDia2textField.insets = new Insets(0, 0, 5, 5);
			gbc_searchDia2textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_searchDia2textField.gridx = 2;
			gbc_searchDia2textField.gridy = 4;
			jPanelData.add(getSearchDia2textField(), gbc_searchDia2textField);
			GridBagConstraints gbc_btnSearchDia2Button = new GridBagConstraints();
			gbc_btnSearchDia2Button.insets = new Insets(0, 0, 5, 5);
			gbc_btnSearchDia2Button.gridx = 3;
			gbc_btnSearchDia2Button.gridy = 4;
			jPanelData.add(getBtnSearchDia2Button(), gbc_btnSearchDia2Button);
			
			jLabelDis3 = new JLabel(MessageBundle.getMessage("angal.opd.diagnosisnfulllist3"));
			GridBagConstraints gbc_jLabelDis3 = new GridBagConstraints();
			gbc_jLabelDis3.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelDis3.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDis3.anchor = GridBagConstraints.WEST;
			gbc_jLabelDis3.gridy = 5;
			gbc_jLabelDis3.gridx = 0;
			jPanelData.add(jLabelDis3, gbc_jLabelDis3);
			GridBagConstraints gbc_jLabelDisBox3 = new GridBagConstraints();
			gbc_jLabelDisBox3.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDisBox3.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelDisBox3.gridwidth = 2;
			gbc_jLabelDisBox3.gridy = 5;
			gbc_jLabelDisBox3.gridx = 4;
			jPanelData.add(getDiseaseBox3(), gbc_jLabelDisBox3);			
			GridBagConstraints gbc_searchDia3textField = new GridBagConstraints();
			gbc_searchDia3textField.insets = new Insets(0, 0, 5, 5);
			gbc_searchDia3textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_searchDia3textField.gridx = 2;
			gbc_searchDia3textField.gridy = 5;
			jPanelData.add(getSearchDia3textField(), gbc_searchDia3textField);
			GridBagConstraints gbc_btnSearchDia3Button = new GridBagConstraints();
			gbc_btnSearchDia3Button.insets = new Insets(0, 0, 5, 5);
			gbc_btnSearchDia3Button.gridx = 3;
			gbc_btnSearchDia3Button.gridy = 5;
			jPanelData.add(getBtnSearchDia3Button(), gbc_btnSearchDia3Button);
			//end diagnosis
			
			jLabelLastOpdVisit = new JLabel(" ");
			jLabelLastOpdVisit.setForeground(Color.RED);
			GridBagConstraints gbc_jLabelLastOpdVisit = new GridBagConstraints();
			gbc_jLabelLastOpdVisit.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelLastOpdVisit.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelLastOpdVisit.anchor = GridBagConstraints.EAST;
			gbc_jLabelLastOpdVisit.gridy = 6;
			gbc_jLabelLastOpdVisit.gridx = 0;
			jPanelData.add(jLabelLastOpdVisit, gbc_jLabelLastOpdVisit);
			jFieldLastOpdVisit = new JLabel(" ");
			jFieldLastOpdVisit.setFocusable(false);
			GridBagConstraints gbc_jFieldLastOpdVisit = new GridBagConstraints();
			gbc_jFieldLastOpdVisit.insets = new Insets(5, 5, 5, 0);
			gbc_jFieldLastOpdVisit.fill = GridBagConstraints.HORIZONTAL;
			gbc_jFieldLastOpdVisit.gridwidth = 4;
			gbc_jFieldLastOpdVisit.gridy = 6;
			gbc_jFieldLastOpdVisit.gridx = 2;
			jPanelData.add(jFieldLastOpdVisit, gbc_jFieldLastOpdVisit);
			GridBagConstraints gbc_isPregnantLabel = new GridBagConstraints();
			gbc_isPregnantLabel.anchor = GridBagConstraints.EAST;
			gbc_isPregnantLabel.insets = new Insets(0, 0, 5, 5);
			gbc_isPregnantLabel.gridx = 0;
			gbc_isPregnantLabel.gridy = 7;
			jPanelData.add(getIsPregnantLabel(), gbc_isPregnantLabel);
			GridBagConstraints gbc_isPregnantCheckBox = new GridBagConstraints();
			gbc_isPregnantCheckBox.insets = new Insets(0, 0, 5, 5);
			gbc_isPregnantCheckBox.gridx = 1;
			gbc_isPregnantCheckBox.gridy = 7;
			jPanelData.add(getIsPregnantCheckBox(), gbc_isPregnantCheckBox);
			
			jLabelLastOpdNote = new JLabel(" ");
			jLabelLastOpdNote.setForeground(Color.RED);
			GridBagConstraints gbc_jLabelLastOpdNote = new GridBagConstraints();
			gbc_jLabelLastOpdNote.fill = GridBagConstraints.VERTICAL;
			gbc_jLabelLastOpdNote.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelLastOpdNote.anchor = GridBagConstraints.EAST;
			gbc_jLabelLastOpdNote.gridy = 8;
			gbc_jLabelLastOpdNote.gridx = 0;
			jPanelData.add(jLabelLastOpdNote, gbc_jLabelLastOpdNote);
			jFieldLastOpdNote = new JLabel(" ");
			jFieldLastOpdNote.setFocusable(false);
			GridBagConstraints gbc_jFieldLastOpdNote = new GridBagConstraints();
			gbc_jFieldLastOpdNote.insets = new Insets(5, 5, 5, 0);
			gbc_jFieldLastOpdNote.fill = GridBagConstraints.HORIZONTAL;
			gbc_jFieldLastOpdNote.gridwidth = 4;
			gbc_jFieldLastOpdNote.gridy = 8;
			gbc_jFieldLastOpdNote.gridx = 2;
			jPanelData.add(jFieldLastOpdNote, gbc_jFieldLastOpdNote);
			GridBagConstraints gbc_nextVisitLabel = new GridBagConstraints();
			gbc_nextVisitLabel.insets = new Insets(0, 0, 0, 5);
			gbc_nextVisitLabel.gridx = 0;
			gbc_nextVisitLabel.gridy = 9;
			jPanelData.add(getNextVisitLabel(), gbc_nextVisitLabel);
			GridBagConstraints gbc_opdNextVisitDate = new GridBagConstraints();
			gbc_opdNextVisitDate.insets = new Insets(0, 0, 0, 5);
			gbc_opdNextVisitDate.fill = GridBagConstraints.BOTH;
			gbc_opdNextVisitDate.gridx = 2;
			gbc_opdNextVisitDate.gridy = 9;
			jPanelData.add(getOpdNextVisitDate(), gbc_opdNextVisitDate);
			
		}
		return jPanelData;
	}

	/**
	 * 
	 */
	private JDateChooser getOpdDateFieldCal() {
		if (OpdDateFieldCal == null) {
			String d = "";
	
			java.util.Date myDate = null;
			if (insert) {
				if (RememberDates.getLastOpdVisitDateGregorian()==null) {
					dateIn = TimeTools.getServerDateTime();
				}				
				else {
					dateIn=RememberDates.getLastOpdVisitDateGregorian();
				}
			}
			 else {
				dateIn  = opd.getVisitDate();
			}
			if (dateIn==null) {
				d="";
			}
			else {
				myDate = dateIn.getTime();
				d = currentDateFormat.format(myDate);
			}
			try {
				OpdDateFieldCal = new JDateChooser(currentDateFormat.parse(d), "dd/MM/yy");
				OpdDateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
				OpdDateFieldCal.setDateFormatString("dd/MM/yy");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return OpdDateFieldCal;
	}
	
	private JPanel getJOpdNumberPanel() {
		if (jOpdNumberPanel == null) {
			
			jOpdNumberPanel = new JPanel();
			
			jOpdNumLabel = new JLabel();
			jOpdNumLabel.setText(MessageBundle.getMessage("angal.opd.opdnumber"));
			
			jOpdNumField = new JTextField(10);
			jOpdNumField.setEditable(false);
			jOpdNumField.setFocusable(false);
			jOpdNumField.setText(getOpdNum());
			
			jOpdNumberPanel.add(jOpdNumLabel);
			jOpdNumberPanel.add(jOpdNumField);
		}
		return jOpdNumberPanel;
	}

	private String getOpdNum() {
		int OpdNum;
		if (!insert) return ""+opd.getYear();
		GregorianCalendar date = TimeTools.getServerDateTime();
		opd.setYear(opdManager.getProgYear(date.get(Calendar.YEAR))+1);
		//opd.setProgMonth(opdManager.getProgMonth(date.get(GregorianCalendar.MONTH), date.get(GregorianCalendar.YEAR))+1);
		opd.setProgMonth(opdManager.getProgMonth(date.get(GregorianCalendar.MONTH)+1, date.get(GregorianCalendar.YEAR))+1);
		
		OpdNum = opd.getYear();
		return ""+OpdNum;
	}
	
	private JPanel getJNotePanel() {
		if (jNotePanel == null) {
			jNotePanel = new JPanel();
			jNotePanel = setMyBorder(jNotePanel, MessageBundle.getMessage("angal.opd.noteandsymptom"));
			jNoteScrollPane = new JScrollPane(getJTextArea());
			jNoteScrollPane.setVerticalScrollBar(new JScrollBar());
			jNoteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jNoteScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jNoteScrollPane.validate();
			////
			jNoteScrollPane2 = new JScrollPane(getJTextAreaComplaint());
			jNoteScrollPane2.setVerticalScrollBar(new JScrollBar());
			jNoteScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jNoteScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jNoteScrollPane2.validate();
			////
			jNotePanel.setLayout(new BorderLayout(0, 0));
			
			jNotePanel.add(jNoteScrollPane,BorderLayout.NORTH);
			
			jNotePanel.add(jNoteScrollPane2,BorderLayout.SOUTH);
			
		}
		return jNotePanel;
	}
	
	private JTextArea getJTextArea() {
		if (jNoteTextArea == null) {
			jNoteTextArea = new JTextArea(15, 20);
			jNoteTextArea.setAutoscrolls(true);
			if (!insert) {
				jNoteTextArea.setText(opd.getNote());
			}
			jNoteTextArea.setWrapStyleWord(true);
			jNoteTextArea.setLineWrap(true);
			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.opd.notessymptome"), //$NON-NLS-1$
					jNoteTextArea, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
		}
		return jNoteTextArea;
	}
	
	private JTextArea getJTextAreaComplaint() {
		if (jNoteTextAreaComplaint == null) {
			jNoteTextAreaComplaint = new JTextArea(15, 20);
			jNoteTextAreaComplaint.setAutoscrolls(true);
			if (!insert) {
				jNoteTextAreaComplaint.setText(opd.getPatientComplaint());
			}
			jNoteTextAreaComplaint.setWrapStyleWord(true);
			jNoteTextAreaComplaint.setLineWrap(true);
			
			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.opd.complaints"), //$NON-NLS-1$
					jNoteTextAreaComplaint, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
		}
		return jNoteTextAreaComplaint;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getDiseaseTypeBox() {
		if (diseaseTypeBox == null) {
			diseaseTypeBox = new JComboBox();
			
			DiseaseType elem2=null;
			diseaseTypeBox.setMaximumSize(new Dimension(400,50));
			diseaseTypeBox.addItem(allType);
			for (DiseaseType elem : types) {
				if(!insert  && opd.getDiseaseType() != null){
					if(opd.getDiseaseType().equals(elem.getCode())){
						elem2=elem;}
				}
				diseaseTypeBox.addItem(elem);
			}
			if (elem2!=null) { 
				diseaseTypeBox.setSelectedItem(elem2);
			} else {
				diseaseTypeBox.setSelectedIndex(0);
			}
			diseaseTypeBox.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					searchDia1textField.setText("");
					diseaseBox1.removeAllItems();
					getDiseaseBox();					
				}
			});
		}
		return diseaseTypeBox;
	}
	
	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getDiseaseBox() {
		if (diseaseBox1 == null) {
			diseaseBox1 = new JComboBox();
			diseaseBox1.setMaximumSize(new Dimension(400, 50));
			diseaseBox1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setAttendance();
				}
			});
		};
		Disease elem2 = null;
		diseaseBox1.addItem("");
		
		for (Disease elem : diseasesOPD) {
			if (((DiseaseType)diseaseTypeBox.getSelectedItem()).equals(allType))
				diseaseBox1.addItem(elem);
			else if (elem.getType().equals((DiseaseType)diseaseTypeBox.getSelectedItem()))
				diseaseBox1.addItem(elem);
			if(!insert && opd.getDisease()!=null){
				if(opd.getDisease().equals(elem.getCode())){
					elem2 = elem;
				}				
			}
		}
		if (!insert) {
			if (elem2 != null) {
				diseaseBox1.setSelectedItem(elem2);
			} else { //try in the canceled diseases
				if (opd.getDisease()!=null) {
					for (Disease elem : diseasesAll) {
						if (opd.getDisease().compareTo(elem.getCode()) == 0) {
							JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.disease1mayhavebeencanceled"));
							diseaseBox1.addItem(elem);
							diseaseBox1.setSelectedItem(elem);
						}
					}
				}
			}
		}
		return diseaseBox1;
	}
	
	public JComboBox getDiseaseBox2() {
		if (diseaseBox2 == null) {
			diseaseBox2 = new JComboBox();
			diseaseBox2.setMaximumSize(new Dimension(400, 50));
		};
		Disease elem2=null;
		diseaseBox2.addItem("");

		for (Disease elem : diseasesOPD) {
			diseaseBox2.addItem(elem);
			if(!insert && opd.getDisease2()!=null){
				if(opd.getDisease2().equals(elem.getCode())){
					elem2 = elem;}
			} 
		}
		if (elem2!= null) {
			diseaseBox2.setSelectedItem(elem2);
		} else { //try in the canceled diseases
			if (opd.getDisease2()!=null) {
				for (Disease elem : diseasesAll) {
					
					if (opd.getDisease2().compareTo(elem.getCode()) == 0) {
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.disease2mayhavebeencanceled"));
						diseaseBox2.addItem(elem);
						diseaseBox2.setSelectedItem(elem);
					}
				}
			}
		}
		return diseaseBox2;
	}

	/**
	 * 
	 */
	private VoLimitedTextField getJTextPatientSrc() {
		if (jTextPatientSrc == null) {
			jTextPatientSrc = new VoLimitedTextField(16,16);			
			jTextPatientSrc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				}
			});
			jTextPatientSrc.addKeyListener(new KeyListener() {
	
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 jSearchButton.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {
				}
	
				public void keyTyped(KeyEvent e) {
				}
			});
		}
		return jTextPatientSrc;
	}
	
	private JButton getJSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setBorderPainted(false);
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					jComboPatResult.removeAllItems();
					pat = patBrowser.getPatientWithHeightAndWeight(jTextPatientSrc.getText());
					getSearchBox(jTextPatientSrc.getText());
				}
			});
		}
		return jSearchButton;
	}
	
	private void getSearchBox(String s) {
		String key = s;
		String[] s1;
		
		if (key == null || key.compareTo("") == 0) {
			jComboPatResult.addItem(MessageBundle.getMessage("angal.opd.selectapatient"));
			jComboPatResult.addItem(MessageBundle.getMessage("angal.opd.newpatient"));
			jLabelLastOpdVisit.setText(" ");
			jFieldLastOpdVisit.setText(" ");
			jLabelLastOpdNote.setText(" ");
			jFieldLastOpdNote.setText(" ");
			if (jNoteTextArea != null) jNoteTextArea.setText("");
			if (jPanelPatient != null) resetPatient();
		}
				
		for (Patient elem : pat) {
			if(key != null) {
				s1 = key.split(" ");
				String name = elem.getSearchString();
				int a = 0;
				for (int i = 0; i < s1.length; i++) {
					if(name.contains(s1[i].toLowerCase()) == true) {
						a++;
					}
				}
				if (a == s1.length)	jComboPatResult.addItem(elem);
			} else 
				jComboPatResult.addItem(elem);
		}
		//ADDED: Workaround for no items
		if (jComboPatResult.getItemCount() == 0) {
			
				opdPatient = null;
				//resetOpd();
				//disableOpd();
				if (jPanelPatient != null) resetPatient();
				jPatientEditButton.setEnabled(true);
		}
		//ADDED: Workaround for one item only
		if (jComboPatResult.getItemCount() == 1) {
			
				opdPatient = (Patient)jComboPatResult.getSelectedItem();
				setPatient(opdPatient);
				//getjPanelPatient();
				jPatientEditButton.setEnabled(true);
		}
		//ADDED: Workaround for first item
		if (jComboPatResult.getItemCount() > 0) {
			
			if (jComboPatResult.getItemAt(0) instanceof Patient) {
				opdPatient = (Patient)jComboPatResult.getItemAt(0);
				setPatient(opdPatient);
				//getjPanelPatient();
				jPatientEditButton.setEnabled(true);
			}
		}
		jTextPatientSrc.requestFocus();
	}
	///search functions diagnosis
	private void getSearchDiagnosisBox(String s, JComboBox comboDiagnosis) {
		String key = s;
		String[] s1;
		comboDiagnosis.removeAllItems();
		comboDiagnosis.addItem("");
		for (Disease elem : diseasesOPD) {
			if(key != null) {
				s1 = key.split(" ");
				String name = elem.getSearchString();
				int a = 0;
				for (int i = 0; i < s1.length; i++) {
					if(name.contains(s1[i].toLowerCase()) == true) {
						a++;
					}
				}
				if (a == s1.length)	comboDiagnosis.addItem(elem);
			} else 
				comboDiagnosis.addItem(elem);
		}		
		if(comboDiagnosis.getItemCount() >= 2){
			comboDiagnosis.setSelectedIndex(1);
		}
		comboDiagnosis.requestFocus();
		if(comboDiagnosis.getItemCount() > 2){
			comboDiagnosis.showPopup();
		}
	}
	private void getSearchDiagnosisByTypeBox(String s, JComboBox comboDiagnosis) {
		String key = s;
		String[] s1;
		comboDiagnosis.removeAllItems();
		comboDiagnosis.addItem("");
		for (Disease elem : diseasesOPD) {
			if(key != null) {
				s1 = key.split(" ");
				String name = elem.getSearchString();
				int a = 0;
				for (int i = 0; i < s1.length; i++) {
					if(name.contains(s1[i].toLowerCase()) == true) {
						a++;
					}
				}
				if (a == s1.length)	{
					if (((DiseaseType)diseaseTypeBox.getSelectedItem()).equals(allType))
						comboDiagnosis.addItem(elem);
					else if (elem.getType().equals((DiseaseType)diseaseTypeBox.getSelectedItem()))
						diseaseBox1.addItem(elem);
				}
			} else 
				comboDiagnosis.addItem(elem);
		}		
		if(comboDiagnosis.getItemCount() >= 2){
			comboDiagnosis.setSelectedIndex(1);
		}
		comboDiagnosis.requestFocus();
		if(comboDiagnosis.getItemCount() > 2){
			comboDiagnosis.showPopup();
		}		
		//searchDia1textField.requestFocus();
		//comboDiagnosis.requestFocus();
	}
	///
	
	private JComboBox getSearchBox() {
		if (jComboPatResult == null) {
			jComboPatResult = new JComboBox();
			jComboPatResult.setPrototypeDisplayValue("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"); //43 chars
			//jComboPatResult.setMaximumSize(new Dimension(400,50));
			if (opdPatient != null) {

				jComboPatResult.addItem(opdPatient);
				jComboPatResult.setEnabled(false);
				jTextPatientSrc.setEnabled(false);
				jSearchButton.setEnabled(false);
				
				return jComboPatResult;
			} else {
				jComboPatResult.addItem(MessageBundle.getMessage("angal.opd.selectapatient"));
				jComboPatResult.addItem(MessageBundle.getMessage("angal.opd.newpatient"));
			}

			jComboPatResult.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					if (jComboPatResult.getSelectedItem() != null) {
						if (jComboPatResult.getSelectedItem().toString().compareTo(MessageBundle.getMessage("angal.opd.newpatient")) == 0) {
							if (Param.bool("PATIENTEXTENDED")) {
								PatientInsertExtended newrecord = new PatientInsertExtended(OpdEditExtended.this, new Patient(), true);
								newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
								newrecord.addPatientListener(OpdEditExtended.this);
								newrecord.setVisible(true);
							} else {
								PatientInsert newrecord = new PatientInsert(OpdEditExtended.this, new Patient(), true);
								newrecord.addPatientListener(OpdEditExtended.this);
								newrecord.setVisible(true);
							}

						} else if (jComboPatResult.getSelectedItem().toString().compareTo(MessageBundle.getMessage("angal.opd.selectapatient")) == 0) {
							jPatientEditButton.setEnabled(false);

						} else {
							opdPatient = (Patient) jComboPatResult.getSelectedItem();
							setPatient(opdPatient);
							jPatientEditButton.setEnabled(true);
						}
					}
				}
			});
		}
		return jComboPatResult;
	}
	
	//ADDED: Alex
	private JButton getJPatientEditButton() {
		if (jPatientEditButton == null) {
			jPatientEditButton = new JButton();
			jPatientEditButton.setIcon(new ImageIcon("rsc/icons/edit_button.png"));
			jPatientEditButton.setBorderPainted(false);
			jPatientEditButton.setPreferredSize(new Dimension(20, 20));
			jPatientEditButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (opdPatient != null) {
						if (Param.bool("PATIENTEXTENDED")) {
							PatientInsertExtended editrecord = new PatientInsertExtended(OpdEditExtended.this, opdPatient, false);
							editrecord.addPatientListener(OpdEditExtended.this);
							editrecord.setVisible(true);
						} else {
							PatientInsert editrecord = new PatientInsert(OpdEditExtended.this, opdPatient, false);
							editrecord.addPatientListener(OpdEditExtended.this);
							editrecord.setVisible(true);
						}
					} 
				}
			});
			if (!insert) jPatientEditButton.setEnabled(false);
		}	
		return jPatientEditButton;
	}
	
	private JComboBox getDiseaseBox3() {
		if (diseaseBox3 == null) {
			diseaseBox3 = new JComboBox();
			diseaseBox3.setMaximumSize(new Dimension(400, 50));
		};
		Disease elem2=null;
		diseaseBox3.addItem("");

		for (Disease elem : diseasesOPD) {
			diseaseBox3.addItem(elem);
			if(!insert && opd.getDisease3()!=null){
				if(opd.getDisease3().equals(elem.getCode())){
					elem2 = elem;}
			}
		}
		if (elem2!= null) {
			diseaseBox3.setSelectedItem(elem2);
		} else { //try in the canceled diseases
			if (opd.getDisease3()!=null) {	
				for (Disease elem : diseasesAll) {
					if (opd.getDisease3().compareTo(elem.getCode()) == 0) {
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.disease3mayhavebeencanceled"));
						diseaseBox3.addItem(elem);
						diseaseBox3.setSelectedItem(elem);
					}
				}
			}
		}
		return diseaseBox3;
	}
	
	//alex: metodo ridefinito, i settaggi avvegono tramite SetPatient()
	private JPanel getJPanelPatient() {
		if (jPanelPatient == null){
			
			jPanelPatient = new JPanel();
			GridBagLayout gbl_jPanelPatient = new GridBagLayout();
			gbl_jPanelPatient.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
			gbl_jPanelPatient.columnWeights = new double[]{0.0, 1.0, 1.0};
			gbl_jPanelPatient.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			jPanelPatient.setLayout(gbl_jPanelPatient);
			setMyMatteBorder(jPanelPatient, MessageBundle.getMessage("angal.opd.patient"));
			
			jLabelfirstName = new JLabel();
			jLabelfirstName.setText(MessageBundle.getMessage("angal.opd.first.name") + "\t");
			GridBagConstraints gbc_jLabelfirstName = new GridBagConstraints();
			gbc_jLabelfirstName.fill = GridBagConstraints.BOTH;
			gbc_jLabelfirstName.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelfirstName.gridx = 0;
			gbc_jLabelfirstName.gridy = 0;
			jPanelPatient.add(jLabelfirstName, gbc_jLabelfirstName);
			jFieldFirstName= new VoLimitedTextField(50,20);
			jFieldFirstName.setEditable(false);
			jFieldFirstName.setFocusable(false);
			GridBagConstraints gbc_jFieldFirstName = new GridBagConstraints();
			gbc_jFieldFirstName.insets = new Insets(5, 5, 5, 5);
			gbc_jFieldFirstName.fill = GridBagConstraints.BOTH;
			gbc_jFieldFirstName.gridx = 1;
			gbc_jFieldFirstName.gridy = 0;
			jPanelPatient.add(jFieldFirstName, gbc_jFieldFirstName);
			jLabelsecondName = new JLabel();
			jLabelsecondName.setText(MessageBundle.getMessage("angal.opd.second.name") + "\t");
			GridBagConstraints gbc_jLabelsecondName = new GridBagConstraints();
			gbc_jLabelsecondName.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelsecondName.fill = GridBagConstraints.BOTH;
			gbc_jLabelsecondName.gridx = 0;
			gbc_jLabelsecondName.gridy = 1;
			jPanelPatient.add(jLabelsecondName, gbc_jLabelsecondName);
			jFieldSecondName= new VoLimitedTextField(50,20);
			jFieldSecondName.setEditable(false);
			jFieldSecondName.setFocusable(false);
			GridBagConstraints gbc_jFieldSecondName = new GridBagConstraints();
			gbc_jFieldSecondName.fill = GridBagConstraints.BOTH;
			gbc_jFieldSecondName.insets = new Insets(5, 5, 5, 5);
			gbc_jFieldSecondName.gridx = 1;
			gbc_jFieldSecondName.gridy = 1;
			jPanelPatient.add(jFieldSecondName, gbc_jFieldSecondName);
			jLabeladdress  = new JLabel();
			jLabeladdress.setText(MessageBundle.getMessage("angal.opd.address"));
			GridBagConstraints gbc_jLabeladdress = new GridBagConstraints();
			gbc_jLabeladdress.fill = GridBagConstraints.BOTH;
			gbc_jLabeladdress.insets = new Insets(5, 5, 5, 5);
			gbc_jLabeladdress.gridx = 0;
			gbc_jLabeladdress.gridy = 2;
			jPanelPatient.add(jLabeladdress, gbc_jLabeladdress);
			jFieldAddress= new VoLimitedTextField(50,20);
			jFieldAddress.setEditable(false);
			jFieldAddress.setFocusable(false);
			GridBagConstraints gbc_jFieldAddress = new GridBagConstraints();
			gbc_jFieldAddress.fill = GridBagConstraints.BOTH;
			gbc_jFieldAddress.insets = new Insets(5, 5, 5, 5);
			gbc_jFieldAddress.gridx = 1;
			gbc_jFieldAddress.gridy = 2;
			jPanelPatient.add(jFieldAddress, gbc_jFieldAddress);
			jLabelcity = new JLabel();
			jLabelcity.setText(MessageBundle.getMessage("angal.opd.city"));
			GridBagConstraints gbc_jLabelcity = new GridBagConstraints();
			gbc_jLabelcity.fill = GridBagConstraints.BOTH;
			gbc_jLabelcity.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelcity.gridx = 0;
			gbc_jLabelcity.gridy = 3;
			jPanelPatient.add(jLabelcity, gbc_jLabelcity );
			jFieldCity= new VoLimitedTextField(50,20);
			jFieldCity.setEditable(false);
			jFieldCity.setFocusable(false);
			GridBagConstraints gbc_jFieldCity = new GridBagConstraints();
			gbc_jFieldCity.fill = GridBagConstraints.BOTH;
			gbc_jFieldCity.insets = new Insets(5, 5, 5, 5);
			gbc_jFieldCity.gridx = 1;
			gbc_jFieldCity.gridy = 3;
			jPanelPatient.add(jFieldCity, gbc_jFieldCity);
			jLabelnextKin = new JLabel();
			jLabelnextKin.setText(MessageBundle.getMessage("angal.opd.nextkin"));
			GridBagConstraints gbc_jLabelnextKin = new GridBagConstraints();
			gbc_jLabelnextKin.fill = GridBagConstraints.BOTH;
			gbc_jLabelnextKin.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelnextKin.gridx = 0;
			gbc_jLabelnextKin.gridy = 4;
			jPanelPatient.add(jLabelnextKin, gbc_jLabelnextKin);
			jFieldNextKin= new VoLimitedTextField(50,20);
			jFieldNextKin.setEditable(false);
			jFieldNextKin.setFocusable(false);
			GridBagConstraints gbc_jFieldNextKin = new GridBagConstraints();
			gbc_jFieldNextKin.fill = GridBagConstraints.BOTH;
			gbc_jFieldNextKin.insets = new Insets(5, 5, 5, 5);
			gbc_jFieldNextKin.gridx = 1;
			gbc_jFieldNextKin.gridy = 4;
			jPanelPatient.add(jFieldNextKin, gbc_jFieldNextKin);
			jLabelAge = new JLabel();
			jLabelAge.setText(MessageBundle.getMessage("angal.opd.age"));
			GridBagConstraints gbc_jLabelAge = new GridBagConstraints();
			gbc_jLabelAge.fill = GridBagConstraints.BOTH;
			gbc_jLabelAge.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelAge.gridx = 0;
			gbc_jLabelAge.gridy = 5;
			jPanelPatient.add(jLabelAge, gbc_jLabelAge);
			jFieldAge = new VoLimitedTextField(50,20);
			jFieldAge.setEditable(false);
			jFieldAge.setFocusable(false);
			GridBagConstraints gbc_jFieldAge = new GridBagConstraints();
			gbc_jFieldAge.fill = GridBagConstraints.BOTH;
			gbc_jFieldAge.insets = new Insets(5, 5, 5, 5);
			gbc_jFieldAge.gridx = 1;
			gbc_jFieldAge.gridy = 5;
			jPanelPatient.add(jFieldAge, gbc_jFieldAge);
			jLabelSex = new JLabel();
			jLabelSex.setText(MessageBundle.getMessage("angal.opd.sex"));
			GridBagConstraints gbc_jLabelSex = new GridBagConstraints();
			gbc_jLabelSex.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelSex.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelSex.gridx = 0;
			gbc_jLabelSex.gridy = 6;
			jPanelPatient.add(jLabelSex, gbc_jLabelSex);
			radiom= new JRadioButton(MessageBundle.getMessage("angal.opd.male"));
			radiof= new JRadioButton(MessageBundle.getMessage("angal.opd.female"));
			jPanelSex = new JPanel();
			jPanelSex.add(radiom);
			jPanelSex.add(radiof);
			GridBagConstraints gbc_jPanelSex = new GridBagConstraints();
			gbc_jPanelSex.insets = new Insets(5, 5, 5, 5);
			gbc_jPanelSex.fill = GridBagConstraints.HORIZONTAL;
			gbc_jPanelSex.gridx = 1;
			gbc_jPanelSex.gridy = 6;
			jPanelPatient.add(jPanelSex, gbc_jPanelSex);
			GridBagConstraints gbc_jPatientNote = new GridBagConstraints();
			gbc_jPatientNote.fill = GridBagConstraints.BOTH;
			gbc_jPatientNote.insets = new Insets(5, 5, 5, 5);
			gbc_jPatientNote.gridx = 2;
			gbc_jPatientNote.gridy = 0;
			gbc_jPatientNote.gridheight = 7;
			jPanelPatient.add(getJPatientNote(), gbc_jPatientNote);
			
			group = new ButtonGroup();
			group.add(radiom);
			group.add(radiof);
			radiom.setSelected(true);
			radiom.setEnabled(false);
			radiof.setEnabled(false);
			//radiom.setFocusable(false);
			//radiof.setFocusable(false);

			if(opdPatient != null) {
				setPatient(opdPatient);
		    }
		}
		return jPanelPatient;
	}
	
	private JPanel getJPatientNote() {
		if (jPatientNotePanel == null) {
			jPatientNotePanel = new JPanel(new BorderLayout());
			jPatientScrollNote = new JScrollPane(getJPatientNoteArea());
			jPatientScrollNote.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jPatientScrollNote.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jPatientScrollNote.setAutoscrolls(false);
			jPatientScrollNote.validate();
			jPatientNotePanel.add(jPatientScrollNote, BorderLayout.CENTER);
		}
		return jPatientNotePanel;
	}
	
	private JTextArea getJPatientNoteArea() {
		if (jPatientNote == null) {
			jPatientNote = new JTextArea(15, 15);
			if (!insert) {
				jPatientNote.setText(opdPatient.getNote());
			}
			jPatientNote.setLineWrap(true);
			jPatientNote.setEditable(false);
			jPatientNote.setFocusable(false);
		}
		return jPatientNote;
	}

	/**
	 * This method initializes jPanelButtons	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonPanel() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getOkButton(), null);
			if (insert && MainMenu.checkUserGrants("btnopdnewexamination") || 
					!insert && MainMenu.checkUserGrants("btnopdeditexamination"))
				jPanelButtons.add(getJButtonExamination(), null);
			
//			if (insert && MainMenu.checkUserGrants("btnopdnewtherapy") || 
//					!insert && MainMenu.checkUserGrants("btnopdedittherapy"))
//				jPanelButtons.add(getjButtonTherapy(), null);
			
//			if (insert && MainMenu.checkUserGrants("btnopdnewexam") || 
//					!insert && MainMenu.checkUserGrants("btnopdeditexam"))
//				jPanelButtons.add(getjButtonExams(), null);
			
			jPanelButtons.add(getCancelButton(), null);
		}
		return jPanelButtons;
	}
	
	private JButton getJButtonExamination() {
		if (jButtonExamination == null) {
			jButtonExamination = new JButton(MessageBundle.getMessage("angal.opd.examination"));
			jButtonExamination.setMnemonic(KeyEvent.VK_E);
			
			jButtonExamination.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (opdPatient == null) {
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.pleaseselectapatient"));
						return;
					}
					
					PatientExamination patex;
					ExaminationOperations examOperations = new ExaminationOperations();
					
					PatientExamination lastPatex = examOperations.getLastByPatID(opdPatient.getCode());
					if (lastPatex != null) {
						patex = examOperations.getFromLastPatientExamination(lastPatex);
					} else {
						patex = examOperations.getDefaultPatientExamination(opdPatient);
					}
					
					GenderPatientExamination gpatex = new GenderPatientExamination(patex, opdPatient.getSex() == "M");
					
					PatientExaminationEdit dialog = new PatientExaminationEdit(OpdEditExtended.this, gpatex);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
			});
		}
		return jButtonExamination;
	}

	private JButton getjButtonTherapy()  {
		if (jButtonTherapy == null) {
			jButtonTherapy = new JButton(MessageBundle.getMessage("angal.opd.therapy"));
			jButtonTherapy.setMnemonic(KeyEvent.VK_T);
			
			jButtonTherapy.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (opdPatient == null) {
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.pleaseselectapatient"));
						return;
					}
					AdmissionBrowserManager admManager  = new AdmissionBrowserManager();
					Admission adm = admManager.getCurrentAdmission(opdPatient);
					TherapyEdit therapy = new TherapyEdit(OpdEditExtended.this, opdPatient, adm != null);
					therapy.setLocationRelativeTo(null);
					therapy.setVisible(true);
				}
			});
		}
		return jButtonTherapy;
	}
	
	private JButton getjButtonExams() {
		if (jButtonExams == null) {
			jButtonExams = new JButton(MessageBundle.getMessage("angal.opd.exam"));
			jButtonExams.setMnemonic(KeyEvent.VK_X);
			
			jButtonExams.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (opdPatient == null) {
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.pleaseselectapatient"));
						return;
					}
					
					LabBrowser dialog = new LabBrowser(opdPatient);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					//dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
						//dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE );
					dialog.setVisible(true);
					//dialog.setAlwaysOnTop(true);
					
					//dialog.toFront();
						//dialog.setState(Frame.NORMAL);
						//myFrame.setState();
						//dialog.showAsModal(OpdEditExtended.this);
//					JDialog d5 = new JDialog(dialog);
//					d5.pack();
//					d5.setLocationRelativeTo(null);
//					d5.setVisible(true);
					//Dialog.ModalityType.DOCUMENT_MODAL
				}
			});
		}
		return jButtonExams;
	}
	
	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */

	//alex: modified method to take data from Patient Object instead from jTextFields
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton(MessageBundle.getMessage("angal.common.ok"));
            okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					boolean result = false;
					GregorianCalendar gregDate = TimeTools.getServerDateTime();
					String newPatient="";
					String referralTo=null;
					String referralFrom=null;
					
					String referralToHospital=null;
					String referralFromHospital=null;
					
					String disease=null;
					String disease2=null;
					String disease3=null;
					String diseaseType=null;
					String diseaseDesc="";
					String diseaseTypeDesc="";

					if (diseaseBox1.getSelectedIndex()==0) {
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.pleaseselectadisease"));
						return;
					}
					if (opdPatient == null) {
						
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.opd.pleaseselectapatient"));
						return;
					}
					Date now = new Date();
					Date nextVisit = opdNextVisitDate.getDate();
					if(nextVisit!=null){
						if(nextVisit.compareTo(now) < 0){
							JOptionPane.showMessageDialog(OpdEditExtended.this,
									MessageBundle.getMessage("angal.opd.notpasseddate"),
									"",
									JOptionPane.INFORMATION_MESSAGE);
							return;
						}
					}

					//check patient information here
					String validationType="";
					if(Param.bool("PATIENTEXTENDED"))
						validationType=ValidationPatientGroup.GLOBAL; 
					else 
						validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
					String resultCheck = PatientBrowserManager.checkPatientInformation(opdPatient, validationType);
					if(resultCheck.length()>0){
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
						if(Param.bool("PATIENTEXTENDED")){
							PatientInsertExtended newrecord = new PatientInsertExtended(OpdEditExtended.this, opdPatient, false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							newrecord.addPatientListener(OpdEditExtended.this);
							newrecord.setVisible(true);
						}else{
							PatientInsert newrecord = new PatientInsert(OpdEditExtended.this, opdPatient, false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							newrecord.addPatientListener(OpdEditExtended.this);
							newrecord.setVisible(true);
						}
						return;
					}
					//end checking information patient
					
					if (newPatientCheckBox.isSelected()){
						newPatient="N";
					}else{
						newPatient="R";
					}
					if (referralToCheckBox.isSelected()){
						referralTo="R";
						referralToHospital = referralTotextField.getText().trim();
					}else{
						referralTo="";
						referralToHospital = "";
					}
					if (referralFromCheckBox.isSelected()){
						referralFrom="R";
						referralFromHospital = referralFromtextField.getText().trim();
					}else{
						referralFrom="";
						referralFromHospital = "";
					}
					//disease
					if (diseaseBox1.getSelectedIndex()>0) {
						disease=((Disease)diseaseBox1.getSelectedItem()).getCode();					
						diseaseDesc=((Disease)diseaseBox1.getSelectedItem()).getDescription();
						diseaseTypeDesc=((Disease)diseaseBox1.getSelectedItem()).getType().getDescription();
						diseaseType=(((Disease)diseaseBox1.getSelectedItem()).getType().getCode());
					}
					//disease2
					if (diseaseBox2.getSelectedIndex()>0) {
						disease2=((Disease)diseaseBox2.getSelectedItem()).getCode();
					}
					//disease3
					if (diseaseBox3.getSelectedIndex()>0) {
						disease3=((Disease)diseaseBox3.getSelectedItem()).getCode();					
					}
					//Check double diseases
					if (disease2 != null && disease == disease2) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.duplicatediseasesnotallowed"));
						disease2 = null;
						return;
					}
					if (disease3 != null && disease == disease3) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.duplicatediseasesnotallowed"));
						disease3 = null;
						return;
					}
					if (disease2 != null && disease3 != null && disease2 == disease3) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.duplicatediseasesnotallowed"));
						disease3 = null;
						return;
					}
					String d = currentDateFormat.format(OpdDateFieldCal.getDate());
					if (d.equals("")) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.pleaseinsertattendancedate"));
						return;
					}
					
					opd.setNote(jNoteTextArea.getText());
					opd.setpatientCode(opdPatient.getCode()); //ADDED : alex
					opd.setFullName(opdPatient.getName());
					opd.setNewPatient(newPatient);
					opd.setReferralFrom(referralFrom);
					opd.setReferralTo(referralTo);					
					opd.setReferralFromHospital(referralFromHospital);
					opd.setReferralToHospital(referralToHospital);
									
					opd.setAge(opdPatient.getAge());
					opd.setSex( opdPatient.getSex().charAt(0));
					
					opd.setfirstName(opdPatient.getFirstName());
					opd.setsecondName(opdPatient.getSecondName());
					opd.setaddress(opdPatient.getAddress());
					opd.setcity(opdPatient.getCity());
					opd.setnextKin(opdPatient.getNextKin());
					
					opd.setDisease(disease);						
					opd.setDiseaseType(diseaseType);
					opd.setDiseaseDesc(diseaseDesc);
					opd.setDiseaseTypeDesc(diseaseTypeDesc);
					opd.setDisease2(disease2);
					opd.setDisease3(disease3);
					/***************************/
					if(isPregnantCheckBox.isSelected()){
						opd.setIsPregnant(true);
					}
					else{
						opd.setIsPregnant(false);
					}
					/****************************/
					
					String user = MainMenu.getUser();
//					String opdUser = opd.getUserID();
//					if (opdUser != null && !opdUser.equals(user)) {
//						int yes = JOptionPane.showConfirmDialog(OpdEditExtended.this, MessageBundle.getMessage("angal.opd.youaresigningnewdatawithyournameconfirm"));
//						if (yes != JOptionPane.YES_OPTION) return;
//					}
					opd.setUserID(user);
					opd.setPatientComplaint(jNoteTextAreaComplaint.getText());
					//alex: salva la vera data di visista scelta
					gregDate.setTime(OpdDateFieldCal.getDate());
					
					// next visit date
					try{
						GregorianCalendar gregNextVisit = new GregorianCalendar();
						gregNextVisit.setTime(opdNextVisitDate.getDate());
						opd.setNextVisitDate(gregNextVisit);
					}
					catch(Exception ex)
					{
					}
					//
					opd.setVisitDate(gregDate);
					if (insert){
						GregorianCalendar date =TimeTools.getServerDateTime();
						opd.setYear(opdManager.getProgYear(date.get(Calendar.YEAR))+1);
						//opd.setProgMonth(opdManager.getProgMonth(date.get(GregorianCalendar.MONTH), date.get(GregorianCalendar.YEAR))+1);
						opd.setProgMonth(opdManager.getProgMonth(date.get(GregorianCalendar.MONTH)+1, date.get(GregorianCalendar.YEAR))+1);
						//remember for later use
						RememberDates.setLastOpdVisitDate(gregDate);
						result = opdManager.newOpd(opd);
						if (result) {
							fireSurgeryInserted();
						}
						if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.thedatacouldnotbesaved"));
						else  dispose();
					}
					else {    //Update
						result = opdManager.updateOpd(opd);
						if (result) {
							fireSurgeryUpdated();
						};
						if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opd.thedatacouldnotbesaved"));
						else  dispose();
					};
				};
			}
			);	
		}
		return okButton;
	}
	
	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(MessageBundle.getMessage("angal.common.cancel"));
            cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//to free Memory
					pat.clear();
					opdArray.clear();
					diseasesAll.clear();
					diseasesOPD.clear();
					types.clear();
					jComboPatResult.removeAllItems();
					diseaseTypeBox.removeAllItems();
					diseaseBox1.removeAllItems();
					diseaseBox2.removeAllItems();
					diseaseBox3.removeAllItems();
					dispose();
				}
			});
		}
		return cancelButton;
	}
	
	/*
	 * set a specific border+title to a panel
	 */
	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title), BorderFactory
						.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}
	
	/*
	 * set a specific border+title+matte to a panel
	 */
	private JPanel setMyMatteBorder(JPanel c, String title) {
		c.setBorder(new TitledBorder(new MatteBorder(1, 20, 1, 1, (Color) new Color(153, 180, 209)), title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		return c;
	}
	
	private JLabel getNextVisitLabel() {
		if (nextVisitLabel == null) {
			nextVisitLabel = new JLabel(MessageBundle.getMessage("angal.opd.nextvisitdate"));
		}
		return nextVisitLabel;
	}
	
	private JDateChooser getOpdNextVisitDate() {
		if (opdNextVisitDate == null) {
			opdNextVisitDate = new JDateChooser((Date) null, "dd/MM/yy");
//			opdNextVisitDateGre.set(GregorianCalendar.HOUR_OF_DAY, 0);
//			opdNextVisitDateGre.set(GregorianCalendar.MINUTE, 0);
//			opdNextVisitDateGre.set(GregorianCalendar.SECOND, 0);
			
//			opdNextVisitDate.addPropertyChangeListener(new PropertyChangeListener() {
//				public void propertyChange(PropertyChangeEvent evt) {
//					System.out.println("the date change");
//					try{
//						opdNextVisitDateGre.setTime(opdNextVisitDate.getDate());
//					}catch(Exception ex){}
//					
//				}
//			});
			
//			opdNextVisitDate.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
//
//				public void propertyChange(PropertyChangeEvent evt) {
//					
//					opdNextVisitDate.setDate((Date) evt.getNewValue());
//					opdNextVisitDateGre.setTime((Date) evt.getNewValue());
//					opdNextVisitDateGre.set(GregorianCalendar.HOUR_OF_DAY, 0);
//					opdNextVisitDateGre.set(GregorianCalendar.MINUTE, 0);
//					opdNextVisitDateGre.set(GregorianCalendar.SECOND, 0);
//					System.out.println("the date change");
//				}
//			});
			
			opdNextVisitDate.setLocale(new Locale(Param.string("LANGUAGE")));
			opdNextVisitDate.setDateFormatString("dd/MM/yy");
			
			GregorianCalendar dateIn = null ;
			Date myDate;
			String d="";
			if(!insert) {
				dateIn  = opd.getNextVisitDate();
			}
			if (dateIn==null) {
				d="";
			}
			else {
				myDate = dateIn.getTime();
				d = currentDateFormat.format(myDate);
			}
			try {
				if(!d.equals(""))
					opdNextVisitDate = new JDateChooser(currentDateFormat.parse(d), "dd/MM/yy");
				else
					opdNextVisitDate = new JDateChooser();
				opdNextVisitDate.setLocale(new Locale(Param.string("LANGUAGE")));
				opdNextVisitDate.setDateFormatString("dd/MM/yy");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		return opdNextVisitDate;
	}
	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
		}
		return textArea;
	}
	private JTextField getReferralFromtextField() {
		if (referralFromtextField == null) {
			referralFromtextField = new JTextField();
			referralFromtextField.setColumns(14);
			//referralFromtextField.setVisible(false);
			referralFromtextField.setEnabled(false);
			if(!insert){
				String refFrom = opd.getReferralFromHospital();
				if(refFrom != null && !refFrom.equals("")){
					referralFromtextField.setText(refFrom);
					//referralFromtextField.setVisible(true);
					referralFromtextField.setEnabled(true);
				}
			}
		}
		return referralFromtextField;
	}
	
	private JTextField getReferralTotextField() {
		if (referralTotextField == null) {
			referralTotextField = new JTextField();
			referralTotextField.setColumns(14);
			//referralTotextField.setVisible(false);
			referralTotextField.setEnabled(false);
			if(!insert){
				String refTo = opd.getReferralToHospital();
				if(refTo != null && !refTo.equals("")){
					referralTotextField.setText(refTo);
					//referralTotextField.setVisible(true);
					referralTotextField.setEnabled(true);
				}
			}
		}
		return referralTotextField;
	}
	
	private JCheckBox getIsPregnantCheckBox() {
		if (isPregnantCheckBox == null) {
			isPregnantCheckBox = new JCheckBox("");
			if(!insert){
				if(opd.isPregnant()){
					isPregnantCheckBox.setSelected(true);
				}else{
					isPregnantCheckBox.setSelected(false);
				}			
			}
		}
		return isPregnantCheckBox;
	}
	private JLabel getIsPregnantLabel() {
		if (isPregnantLabel == null) {
			isPregnantLabel = new JLabel(MessageBundle.getMessage("angal.opd.ispregnant"));
		}
		return isPregnantLabel;
	}
	private JTextField getSearchDia1textField() {
		if (searchDia1textField == null) {
			searchDia1textField = new JTextField();
			searchDia1textField.setColumns(10);
			searchDia1textField.addKeyListener(new KeyListener() {
				
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 btnSearchDia1Button.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {
				}
	
				public void keyTyped(KeyEvent e) {
				}
			});
//			searchDia1textField.getDocument().addDocumentListener(new DocumentListener() {
//				@Override
//				public void insertUpdate(DocumentEvent e) {
//					btnSearchDia1Button.doClick();
//				}
//
//				@Override
//				public void removeUpdate(DocumentEvent e) {
//					btnSearchDia1Button.doClick();
//				}
//
//				@Override
//				public void changedUpdate(DocumentEvent e) {
//					btnSearchDia1Button.doClick();
//				}
//			});
		}
		return searchDia1textField;
	}
	private JTextField getSearchDia2textField() {
		if (searchDia2textField == null) {
			searchDia2textField = new JTextField();
			searchDia2textField.setColumns(10);
			searchDia2textField.addKeyListener(new KeyListener() {
				
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 btnSearchDia2Button.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {
				}
	
				public void keyTyped(KeyEvent e) {
				}
			});
		}
		return searchDia2textField;
	}
	private JTextField getSearchDia3textField() {
		if (searchDia3textField == null) {
			searchDia3textField = new JTextField();
			searchDia3textField.setColumns(10);
			searchDia3textField.addKeyListener(new KeyListener() {
				
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 btnSearchDia3Button.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {
				}
	
				public void keyTyped(KeyEvent e) {
				}
			});
		}
		return searchDia3textField;
	}
	private JButton getBtnSearchDia1Button() {
		if (btnSearchDia1Button == null) {
			btnSearchDia1Button = new JButton("");
			btnSearchDia1Button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//getSearchDiagnosisBox(searchDia1textField.getText(), diseaseBox1);
					getSearchDiagnosisByTypeBox(searchDia1textField.getText(), diseaseBox1);
				}
			});
			btnSearchDia1Button.setPreferredSize(new Dimension(20, 20));
			btnSearchDia1Button.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
		}
		return btnSearchDia1Button;
	}
	private JButton getBtnSearchDia2Button() {
		if (btnSearchDia2Button == null) {
			btnSearchDia2Button = new JButton("");
			btnSearchDia2Button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {					
					getSearchDiagnosisBox(searchDia2textField.getText(), diseaseBox2);
				}
			});
			btnSearchDia2Button.setPreferredSize(new Dimension(20, 20));
			btnSearchDia2Button.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
		}
		return btnSearchDia2Button;
	}
	private JButton getBtnSearchDia3Button() {
		if (btnSearchDia3Button == null) {
			btnSearchDia3Button = new JButton("");
			btnSearchDia3Button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					getSearchDiagnosisBox(searchDia3textField.getText(), diseaseBox3);
				}
			});
			btnSearchDia3Button.setPreferredSize(new Dimension(20, 20));
			btnSearchDia3Button.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
		}
		return btnSearchDia3Button;
	}
}