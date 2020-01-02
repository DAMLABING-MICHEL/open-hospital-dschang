package org.isf.admission.gui;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.distype.model.DiseaseType;
import org.isf.dlvrrestype.manager.DeliveryResultTypeBrowserManager;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.manager.DeliveryTypeBrowserManager;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.examination.gui.PatientExaminationEdit;
import org.isf.examination.model.GenderPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationOperations;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.gui.Menu;
import org.isf.opd.gui.OpdEditExtended;
import org.isf.operation.gui.OperationList;
import org.isf.operation.gui.OperationRowAD;
import org.isf.operation.gui.OperationRowEdit;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.gui.PatientSummary;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.gui.PregnancyCareBrowser;
import org.isf.pregnancy.gui.PregnancyEdit;
import org.isf.pregnancy.manager.PregnancyDeliveryManager;
import org.isf.pregnancy.model.Delivery;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.jobjects.ShadowBorder;
import org.isf.utils.jobjects.ValidationPatientGroup;
import org.isf.utils.jobjects.VoDateTextField;
import org.isf.utils.jobjects.VoIntegerTextField;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.RememberDates;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.isf.xmpp.gui.CommunicationFrame;
import org.isf.xmpp.manager.Interaction;

import com.toedter.calendar.JDateChooser;

import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;

/**
 * This class shows essential patient data and allows to create an admission
 * record or modify an existing one
 * 
 * release 2.5 nov-10-06
 * 
 * @author flavio
 * 
 */

/*----------------------------------------------------------
 * modification history
 * ====================
 * 23/10/06 - flavio - borders set to not resizable
 *                     changed Disease IN (/OUT) into Dignosis IN (/OUT)
 *                     
 * 10/11/06 - ross - added RememberDate for admission Date
 * 				   - only diseses with flag In Patient (IPD) are displayed
 *                 - on Insert. in edit all are displayed
 *                 - the correct way should be to display the IPD + the one aready registered
 * 18/08/08 - Alex/Andrea - Calendar added
 * 13/02/09 - Alex - Cosmetic changes to UI
 * 10/01/11 - Claudia - insert ward beds availability 
 * 01/01/11 - Alex - GUI and code reengineering
 * 29/12/11 - Nicola - insert alert IN/OUT patient for communication module
 -----------------------------------------------------------*/

public class AdmissionBrowser extends JDialog implements PatientInsertExtended.PatientListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EventListenerList admissionListeners = new EventListenerList();

	public interface AdmissionListener extends EventListener {
		public void admissionUpdated(AWTEvent e);

		public void admissionInserted(AWTEvent e);
	}

	public void addAdmissionListener(AdmissionListener l) {
		admissionListeners.add(AdmissionListener.class, l);
	}

	public void removeAdmissionListener(AdmissionListener listener) {
		admissionListeners.remove(AdmissionListener.class, listener);
	}

	private void fireAdmissionInserted(Admission anAdmission) {
		AWTEvent event = new AWTEvent(anAdmission, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = admissionListeners
				.getListeners(AdmissionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((AdmissionListener) listeners[i]).admissionInserted(event);
	}

	private void fireAdmissionUpdated(Admission anAdmission) {
		AWTEvent event = new AWTEvent(anAdmission, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
 
		EventListener[] listeners = admissionListeners
				.getListeners(AdmissionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((AdmissionListener) listeners[i]).admissionUpdated(event);
	}

	private Patient patient = null;

	private boolean editing = false;

	private Admission admission = null;

	private PatientSummary ps = null;

	private JTextArea textArea = null;

	private JTabbedPane jTabbedPaneAdmission;

	private JPanel jPanelAdmission;

	private JPanel jPanelOperation;

	private JPanel jPanelDelivery;

	private int pregnancyTabIndex;
	
	//
	private int operationTabIndex;
	private int admissionTabIndex;
	private int noteTabIndex;
	//

	private JPanel jContentPane = null;

	// enable is if patient is female
	private boolean enablePregnancy = false;

	// viewing is if you set ward to pregnancy
	private boolean viewingPregnancy = false;

	private GregorianCalendar visitDate = null;

	private float weight = 0.0f;

	private VoDateTextField visitDateField = null;

	private VoLimitedTextField weightField = null;

	private JDateChooser visitDateFieldCal = null; // Calendar

	private JComboBox treatmTypeBox = null;

	private final int preferredWidthDates = 110;

	private final int preferredWidthDiagnosis = 550;

	private final int preferredWidthTypes = 220;

	private final int preferredWidthTransfusionSpinner = 55;

	private final int preferredHeightLine = 24;

	private GregorianCalendar deliveryDate = null;

	private VoDateTextField deliveryDateField = null;

	private JDateChooser deliveryDateFieldCal = null;

	private JComboBox deliveryTypeBox = null;

	private JComboBox deliveryResultTypeBox = null;

	private ArrayList<PregnantTreatmentType> treatmTypeList = null;

	private ArrayList<DeliveryType> deliveryTypeList = null;

	private ArrayList<DeliveryResultType> deliveryResultTypeList = null;

	private GregorianCalendar ctrl1Date = null;

	private GregorianCalendar ctrl2Date = null;

	private GregorianCalendar abortDate = null;

	private JDateChooser ctrl1DateFieldCal = null;

	private JDateChooser ctrl2DateFieldCal = null;

	private JDateChooser abortDateFieldCal = null;

	private JComboBox wardBox;

	private ArrayList<Ward> wardList = null;

	// save value during a swith
	private Ward saveWard = null;

	private String saveYProg = null;

	private JTextField yProgTextField = null;

	private JTextField FHUTextField = null;

	private JPanel wardPanel;

	private JPanel fhuPanel;

	private JPanel yearProgPanel;

	private JComboBox diseaseInBox;

	private DiseaseBrowserManager dbm = new DiseaseBrowserManager();

	private ArrayList<Disease> diseaseInList = dbm.getDiseaseIpdIn();

	private ArrayList<Disease> diseaseOutList = dbm.getDiseaseIpdOut();

	private JCheckBox malnuCheck;

	private JPanel diseaseInPanel;

	private JPanel malnuPanel;

	private GregorianCalendar dateIn = null;

	private JDateChooser dateInFieldCal = null;

	private JComboBox admTypeBox = null;

	private ArrayList<AdmissionType> admTypeList = null;

	private DateFormat currentDateFormat = DateFormat.getDateInstance(
			DateFormat.SHORT, new Locale(Param.string("LANGUAGE")));

	private JPanel admissionDatePanel;

	private JPanel admissionTypePanel;

	private JComboBox diseaseOut1Box = null;

	private JComboBox diseaseOut2Box = null;

	private JComboBox diseaseOut3Box = null;

	private JPanel diseaseOutPanel;

	private JComboBox operationBox = null;

	private JRadioButton operationResultRadioP = null;

	private JRadioButton operationResultRadioN = null;

	private JRadioButton operationResultRadioU = null;

	private ArrayList<Operation> operationList = null;

	private GregorianCalendar operationDate = null;

	private JDateChooser operationDateFieldCal = null;

	private VoDateTextField operationDateField = null;

	private float trsfUnit = 0.0f;

	private JSpinner trsfUnitField = null;

	private GregorianCalendar dateOut = null;

	private JDateChooser dateOutFieldCal = null;

	private JComboBox disTypeBox = null;

	private ArrayList<DischargeType> disTypeList = null;

	private JPanel dischargeDatePanel;

	private JPanel dischargeTypePanel;

	private JPanel bedDaysPanel;

	private JPanel buttonPanel = null;

	private JLabel labelRequiredFields;

	private JButton closeButton = null;

	private JButton saveButton = null;

	private JButton jButtonExamination = null;

	private JPanel operationDatePanel;

	private JPanel transfusionPanel;

	private JPanel operationPanel;

	private JPanel resultPanel;

	private JPanel visitDatePanel;

	private JPanel weightPanel;

	private JPanel treatmentPanel;

	private JPanel deliveryDatePanel;

	private JPanel deliveryTypePanel;

	private JPanel deliveryResultTypePanel;

	private JPanel control1DatePanel;

	private JPanel control2DatePanel;

	private JPanel abortDatePanel;

	private VoLimitedTextField bedDaysTextField;

	private AdmissionBrowserManager admMan = new AdmissionBrowserManager();
	
	
	
	private OperationRowAD operationad;

	private JButton newPrenatalVisitButton;

	private PregnancyDeliveryManager deliveryManager= new PregnancyDeliveryManager();
	private AdmissionBrowser myFrame= null;

	private JPanel newBorn1Panel;
	private JPanel sexPanel1;

	private JRadioButton sex1Female;

	private JRadioButton sex1Male;

	private JPanel weightPanel1;
	
	//added panel Julio
	private JPanel fatherPanel;
	private JPanel fatherPanel2;
	private JTextField fatherNameField;
	private JTextField fatherOccupationField;
	private JTextField fatherResidenceField;
	private JLabel fatherNameLabel;
	private JLabel fatherOccupationLabel;
	private JLabel fatherResidenceLabel;
	private JTextField fatherBirthPlaceField ;
	private JLabel fatherBirthPlaceLabel;
	
	//private JTextField fatherAgeField; 
	private VoIntegerTextField fatherAgeField;
	private JLabel fatherAgeLabel ;
	
	private JPanel fatherGlobalPanel;
	//private JPanel motherPanel ;
	//private JTextField motherBirthPlaceField ;
	
	private JPanel namePanel1;
	private JPanel namePanel2;
	private JPanel namePanel3;
	
	private JTextField newBornNameField1;
	private JTextField newBornNameField2;
	private JTextField newBornNameField3;
	
	//private JLabel motherBirthPlaceLabel;
	//

	private VoLimitedTextField weight1TextField;

	private JPanel delrestype1;
	
	////hiv result
	private JPanel hivStatutPanel1;
	private JComboBox hivStatutBox1;
	private JPanel hivStatutPanel2;
	private JComboBox hivStatutBox2;
	private JPanel hivStatutPanel3;
	private JComboBox hivStatutBox3;

	private JComboBox delrestypeBox1;

	private DeliveryResultTypeBrowserManager drtbm;

	private JPanel newBorn2Panel;

	private JCheckBox newborn2EnableCheckbox;

	private JPanel sexPanel2;

	private JRadioButton sex2Female;

	private JRadioButton sex2Male;

	private JPanel weightPanel2;

	private VoLimitedTextField weight2TextField;

	private JPanel delrestype2;

	private JComboBox delrestypeBox2;

	private JPanel newBorn3Panel;

	private JCheckBox newborn3EnableCheckBox;

	private JPanel sexPanel3;

	private JRadioButton sex3Female;

	private JRadioButton sex3Male;

	private JPanel weightPanel3;

	private VoLimitedTextField weight3TextField;

	private JPanel delrestype3;

	private JComboBox delrestypeBox3;

	private JButton newPostnatalVisitButton;
	
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();

	private JButton seePregnancyVisitButton;
	
	
	
	private JButton searchButton;
	private JButton searchDiseaseOut1Button;
	private JButton searchDiseaseOut2Button;
	private JButton searchDiseaseOut3Button;
	/********/

	/*
	 * from AdmittedPatientBrowser
	 */
	public AdmissionBrowser(JFrame parentFrame, AdmittedPatient admPatient,
			boolean editing, boolean isPregnancyCare) {
		super(parentFrame, (editing ? MessageBundle
				.getMessage("angal.admission.editadmissionrecord")
				: MessageBundle.getMessage("angal.admission.newadmission")),
				true);
	
		myFrame=this;
		addAdmissionListener((AdmissionListener) parentFrame);
		this.editing = editing;
		patient = admPatient.getPatient();
		if (("" + patient.getSex()).equalsIgnoreCase("F")) {
			enablePregnancy = true;
		}
		ps = new PatientSummary(patient);
		
		/*** add julio ***/
		AdmissionBrowserManager abm = new AdmissionBrowserManager();
		Admission admiss = abm.getCurrentAdmission(patient);
		operationad = new OperationRowAD(admiss);
		addAdmissionListener((AdmissionListener) operationad);
		/**************/
		
		if (editing) {
			admission = admMan.getCurrentAdmission(patient);
			if (admission.getWardId().equalsIgnoreCase("M")) {
				viewingPregnancy = true;
				deliveries = deliveryManager.getDeliveriesOfAdmission(admission.getId());
			} 
		} else {
			admission = new Admission();
		}
		
		if(isPregnancyCare){
			viewingPregnancy=true;
			admission.setWardId("M");
			admission.setType("P");
		}
		
		
		initialize(parentFrame);

		
			
		
		
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				if (diseaseInList != null)
					diseaseInList.clear();
				if (diseaseOutList != null)
					diseaseOutList.clear();
				dispose();
			}
		});
		
		
	}
	
	//constructor when editing a delivery
	public AdmissionBrowser(JFrame parentFrame, Patient admPatient, int admissionId,
			boolean editing, boolean isPregnancyCare) {
		super(parentFrame, (editing ? MessageBundle
				.getMessage("angal.admission.editadmissionrecord")
				: MessageBundle.getMessage("angal.admission.newadmission")),
				true);
		myFrame=this;
		addAdmissionListener((AdmissionListener) parentFrame);
		this.editing = editing;
		patient = admPatient;
		if (("" + patient.getSex()).equalsIgnoreCase("F")) {
			enablePregnancy = true;
		}
		ps = new PatientSummary(patient);		
		/*** add julio ***/
		AdmissionBrowserManager abm = new AdmissionBrowserManager();		
		Admission admiss = abm.getAdmission(admissionId);
		operationad = new OperationRowAD(admiss);
		addAdmissionListener((AdmissionListener) operationad);
		/**************/		
		if (editing) {
			admission = admMan.getAdmission(admissionId);
			if (admission.getWardId().equalsIgnoreCase("M")) {
				viewingPregnancy = true;
				deliveries = deliveryManager.getDeliveriesOfAdmission(admission.getId());
			} 			
		} else {
			admission = new Admission();
		}		
		if(isPregnancyCare){
			viewingPregnancy=true;
			admission.setWardId("M");
			admission.setType("P");
		}
		initialize(parentFrame,editing);		
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (diseaseInList != null)
					diseaseInList.clear();
				if (diseaseOutList != null)
					diseaseOutList.clear();
				dispose();
			}
		 });				
	}
	//

	/*
	 * from PatientDataBrowser
	 */
	/**
	 * @wbp.parser.constructor
	 */
	public AdmissionBrowser(JFrame parentFrame, JFrame parentParentFrame,
			Patient aPatient, Admission anAdmission) {
		
		super(parentFrame, MessageBundle
				.getMessage("angal.admission.editadmissionrecord"), true);
//		super();
//		setTitle(MessageBundle.getMessage("angal.admission.editadmissionrecord"));
		
		addAdmissionListener((AdmissionListener) parentParentFrame);
		addAdmissionListener((AdmissionListener) parentFrame);
		this.editing = true;
		myFrame=this;
		patient = aPatient;
		if (("" + patient.getSex()).equalsIgnoreCase("F")) {
			enablePregnancy = true;
		}
		ps = new PatientSummary(patient);
		
		admission = admMan.getAdmission(anAdmission.getId());
		if (admission.getWardId().equalsIgnoreCase("M")) {
			viewingPregnancy = true;
			deliveries = deliveryManager.getDeliveriesOfAdmission(admission.getId());
		}
		
		initialize(parentFrame);
		
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				if (diseaseInList != null)
					diseaseInList.clear();
				if (diseaseOutList != null)
					diseaseOutList.clear();
				dispose();
			}
		});
	}

	private void initialize(JFrame parent) {
		getContentPane().add(getJContentPane(), BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		
		setVisible(true);
		
	}
	
	private void initialize(JFrame parent, boolean isEditing) {
		getContentPane().add(getJContentPane(), BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		if(isEditing){			
			if(admission.getDisDate()!=null){				
				jTabbedPaneAdmission.setEnabledAt(admissionTabIndex, false);
				jTabbedPaneAdmission.setEnabledAt(operationTabIndex, false);
				jTabbedPaneAdmission.setEnabledAt(noteTabIndex, false);
				newPrenatalVisitButton.setEnabled(false);
			}
		}
		jTabbedPaneAdmission.setSelectedIndex(pregnancyTabIndex);
		setVisible(true);
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);			
		}
		return jContentPane;
	}

	private JPanel getDataPanel() {
		JPanel data = new JPanel();
		data.setLayout(new BorderLayout());
		data.add(getPatientDataPanel(), java.awt.BorderLayout.WEST);
		data.add(getJTabbedPaneAdmission(), java.awt.BorderLayout.CENTER);
		return data;
	}

	private JPanel getPatientDataPanel() {
		JPanel data = new JPanel();
		data.add(ps.getPatientCompleteSummary());
		return data;
	}

	private JTabbedPane getJTabbedPaneAdmission() {
		if (jTabbedPaneAdmission == null) {
			jTabbedPaneAdmission = new JTabbedPane();
			jTabbedPaneAdmission.addTab(MessageBundle
					.getMessage("angal.admission.admissionanddischarge"),
					getAdmissionTab());
			admissionTabIndex = jTabbedPaneAdmission.getTabCount() - 1;
			jTabbedPaneAdmission.addTab(
					MessageBundle.getMessage("angal.admission.operation"),
					//getOperationTab());
					getMultiOperationTab());
			operationTabIndex = jTabbedPaneAdmission.getTabCount() - 1;
			if (enablePregnancy) {
				jTabbedPaneAdmission.addTab(
					MessageBundle.getMessage("angal.admission.delivery"),
					getDeliveryTab());
				pregnancyTabIndex = jTabbedPaneAdmission.getTabCount() - 1;
				if (!viewingPregnancy) {
					jTabbedPaneAdmission.setEnabledAt(pregnancyTabIndex, false);
				}
			}
			jTabbedPaneAdmission.addTab("Note", getJPanelNote());
			noteTabIndex = jTabbedPaneAdmission.getTabCount() - 1;
		}
		return jTabbedPaneAdmission;
	}

	private JPanel getAdmissionTab() {
		if (jPanelAdmission == null) {
			jPanelAdmission = new JPanel();

			GroupLayout layout = new GroupLayout(jPanelAdmission);
			jPanelAdmission.setLayout(layout);

			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			layout.setHorizontalGroup(layout
					.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(LEADING)
									.addComponent(getDiseaseInPanel())
									.addComponent(getDiseaseOutPanel())
									.addGroup(
											layout.createSequentialGroup()
													.addGroup(
															layout.createParallelGroup(
																	LEADING)
																	.addComponent(
																			getWardPanel())
																	.addComponent(
																			getAdmissionDatePanel())
																	.addComponent(
																			getDischargeDatePanel()))
													.addGroup(
															layout.createParallelGroup(
																	LEADING)
																	.addComponent(
																			getFHUPanel())
																	.addComponent(
																			getAdmissionTypePanel())
																	.addComponent(
																			getBedDaysPanel()))
													.addGroup(
															layout.createParallelGroup(
																	LEADING)
																	.addComponent(
																			getProgYearPanel())
																	.addComponent(
																			getMalnutritionPanel())
																	.addComponent(
																			getDischargeTypePanel())
																	.addComponent(
																			getJLabelRequiredFields())))));

			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(BASELINE)
									.addComponent(getWardPanel())
									.addComponent(getFHUPanel())
									.addComponent(getProgYearPanel()))
					.addGroup(
							layout.createParallelGroup(BASELINE)
									.addComponent(getAdmissionDatePanel())
									.addComponent(getAdmissionTypePanel())
									.addComponent(getMalnutritionPanel()))
					.addComponent(getDiseaseInPanel())
					.addGroup(
							layout.createParallelGroup(BASELINE)
									.addComponent(getDischargeDatePanel())
									.addComponent(getBedDaysPanel())
									.addComponent(getDischargeTypePanel()))
					.addComponent(getDiseaseOutPanel())
					.addComponent(getJLabelRequiredFields()));
		}
		return jPanelAdmission;
	}

	private JPanel getOperationTab() {
		if (jPanelOperation == null) {
			jPanelOperation = new JPanel();

			GroupLayout layout = new GroupLayout(jPanelOperation);
			jPanelOperation.setLayout(layout);

			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			layout.setHorizontalGroup(layout
					.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(LEADING)
									.addComponent(getOperationDatePanel(),
											GroupLayout.PREFERRED_SIZE,
											preferredWidthDates,
											GroupLayout.PREFERRED_SIZE)
									.addComponent(getOperationPanel(),
											GroupLayout.PREFERRED_SIZE,
											GroupLayout.PREFERRED_SIZE,
											GroupLayout.PREFERRED_SIZE)
									.addGroup(
											layout.createSequentialGroup()
													.addComponent(
															getOperationResultPanel())
													.addComponent(
															getTransfusionPanel()))));

			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addComponent(getOperationDatePanel(),
							GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE)
					.addComponent(getOperationPanel(),
							GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE)
					.addGroup(
							layout.createParallelGroup(BASELINE)
									.addComponent(getOperationResultPanel(),
											GroupLayout.PREFERRED_SIZE,
											GroupLayout.PREFERRED_SIZE,
											GroupLayout.PREFERRED_SIZE)
									.addComponent(getTransfusionPanel(),
											GroupLayout.PREFERRED_SIZE,
											GroupLayout.PREFERRED_SIZE,
											GroupLayout.PREFERRED_SIZE)));
		}
		return jPanelOperation; 
	}
    /***** new JPanel operation  *****/
	private JPanel getMultiOperationTab() {
		if (jPanelOperation == null) {
			jPanelOperation = new JPanel();
			jPanelOperation.setLayout(new BorderLayout(0, 0));
			//jPanelOperation.add(formOperation, BorderLayout.NORTH);
			if(this.operationad == null)
				this.operationad = new OperationRowAD(admission); //fff
			jPanelOperation.add(this.operationad);
		}
		return jPanelOperation; 
	}
	/*********************************/
	
	private JButton getnewPrenatalVisitButton(){
		if(newPrenatalVisitButton== null){
			newPrenatalVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newprenatalvisit"));
			newPrenatalVisitButton.setIcon(new ImageIcon("rsc/icons/plus_button.png"));
			newPrenatalVisitButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int visitcount = deliveryManager.patientVisitcount(patient.getCode());
					if(visitcount <1){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.pregnancy.nopregnancy"));
						return;

					}
					((JFrame)myFrame.getParent()).setAlwaysOnTop(false);
					((JFrame)myFrame.getParent()).toBack();
					myFrame.dispose();
					PregnancyEdit newPregnancyVisit = new PregnancyEdit(((JFrame)myFrame.getParent()), patient, -1);
					newPregnancyVisit.setVisible(true);
					newPregnancyVisit.toFront();
					newPregnancyVisit.requestFocus();
					
					
					
				}
			});
			
		}
		return newPrenatalVisitButton;
	}
	
	private JPanel getNewborn1Panel(){
		if(newBorn1Panel== null){
			newBorn1Panel= new JPanel();
			newBorn1Panel.setLayout(new BoxLayout(newBorn1Panel, BoxLayout.PAGE_AXIS));
			newBorn1Panel.add(getNewBornName1Panel());
			newBorn1Panel.add(getNewborn1SexPanel());
			newBorn1Panel.add(getNewborn1WeightPanel());
			newBorn1Panel.add(getNewborn1HivSatutPanel());
			newBorn1Panel.add(getNewborn1DeliveryResultTypePanel());
		}
		
		newBorn1Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newborn1details")));
		return newBorn1Panel;
	}
	
	private JPanel getNewborn1SexPanel(){
		if(sexPanel1== null){
			sexPanel1 = new JPanel();
			sex1Female = new JRadioButton("F");
			sex1Female.setSelected(true);
			sex1Male = new JRadioButton("M");
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(sex1Female);
			resultGroup.add(sex1Male);
			if(deliveries != null && deliveries.size()>0){
				String storedSex = deliveries.get(0).getSex();
				if (storedSex.equals("M"))
					sex1Male.setSelected(true);
			}
				
			sexPanel1.add(sex1Female);
			sexPanel1.add(sex1Male);
			
		}
		
			
		return sexPanel1;
	}
	
	private JPanel getNewborn1WeightPanel(){
		if (weightPanel1 == null) {
			weightPanel1 = new JPanel();
			
			weight1TextField = new VoLimitedTextField(5, 5);
			if(deliveries != null && deliveries.size()>0){
				weight1TextField.setText(new Float(deliveries.get(0).getWeight()).toString());
			}
			weightPanel1.add(weight1TextField);
			weightPanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weight")));
		}
		return weightPanel1;
	}
	
	//names children
	private JPanel getNewBornName1Panel(){
		if (namePanel1 == null) {
			namePanel1 = new JPanel();
			
			newBornNameField1 = new JTextField(17);
			if(deliveries != null && deliveries.size()>0){
				newBornNameField1.setText(deliveries.get(0).getChild_name()!=null?deliveries.get(0).getChild_name().toString():"");
			}
			namePanel1.add(newBornNameField1);
			namePanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newbornname")));
		}
		return namePanel1;
	}
	private JPanel getNewBornName2Panel(){
		if (namePanel2 == null) {
			namePanel2 = new JPanel();
			
			newBornNameField2 = new JTextField(17);
			if(deliveries != null && deliveries.size()>1){
				newBornNameField2.setText(deliveries.get(1).getChild_name()!=null?deliveries.get(1).getChild_name().toString():"");
			}
			namePanel2.add(newBornNameField2);
			namePanel2.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newbornname")));
		}
		return namePanel2;
	}
	private JPanel getNewBornName3Panel(){
		if (namePanel3 == null) {
			namePanel3 = new JPanel();
			
			newBornNameField3 = new JTextField(17);
			if(deliveries != null && deliveries.size()>2){
				newBornNameField3.setText(deliveries.get(2).getChild_name()!=null?deliveries.get(2).getChild_name().toString():"");
			}
			namePanel3.add(newBornNameField3);
			namePanel3.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newbornname")));
		}
		return namePanel3;
	}
	//
	
	private JPanel getFatherInfoPanel(){
		if (fatherPanel == null) {
			fatherPanel = new JPanel();
			//fatherPanel.setLayout(new BoxLayout(fatherPanel, BoxLayout.LINE_AXIS));
			 fatherNameField       = new JTextField(18);
			 fatherOccupationField = new JTextField(13);
			 fatherResidenceField  = new JTextField(13);
			 
			 fatherNameLabel = new JLabel(MessageBundle.getMessage("angal.admission.fathername"));
			 fatherOccupationLabel = new JLabel(" "+MessageBundle.getMessage("angal.admission.fatheroccupation"));
			 fatherResidenceLabel = new JLabel(" "+MessageBundle.getMessage("angal.admission.fatherresidence"));

			 
			if(deliveries != null && deliveries.size()>0){
				fatherNameField.setText(deliveries.get(0).getFather_name()!=null?deliveries.get(0).getFather_name().toString():"");
				fatherOccupationField.setText(deliveries.get(0).getFather_occupation()!=null?deliveries.get(0).getFather_occupation().toString():"");
				fatherResidenceField.setText(deliveries.get(0).getFather_residence()!=null?deliveries.get(0).getFather_residence().toString():"");
			}
			 fatherPanel.add(fatherNameLabel);
			 fatherPanel.add(fatherNameField);
			 
			 fatherPanel.add(fatherOccupationLabel);
			 fatherPanel.add(fatherOccupationField);
			 
			 fatherPanel.add(fatherResidenceLabel);
			 fatherPanel.add(fatherResidenceField);
			 
			 //fatherPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.fatherinfo")));
		}
		return fatherPanel;
	}
//	private JPanel getMotherBirthPlacePanel(){
//		if (motherPanel == null) {
//			motherPanel = new JPanel();
//			motherBirthPlaceField  = new JTextField(15);			 
//			motherPanel.add(motherBirthPlaceField);
//			motherPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.motherbirthplace")));
//		}
//		return motherPanel;				
//	} 
	
	private JPanel fatherPanel(){
		if (fatherGlobalPanel == null) {
			fatherGlobalPanel = new JPanel();
			fatherGlobalPanel.setLayout(new BoxLayout(fatherGlobalPanel, BoxLayout.PAGE_AXIS));
			fatherGlobalPanel.setAlignmentY(LEFT_ALIGNMENT);
			fatherGlobalPanel.add(getFatherInfoPanel(),Alignment.LEADING);
			fatherGlobalPanel.add(getFatherInfoPanel2(),Alignment.LEADING);
			fatherGlobalPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.fatherinfo")));
		}
		return fatherGlobalPanel;				
	}
	
	private JPanel getFatherInfoPanel2(){
		if (fatherPanel2 == null) {
			fatherPanel2 = new JPanel();
			fatherPanel2.setAlignmentY(LEFT_ALIGNMENT);
			//fatherPanel2.setSize(300, 30);
			//fatherAgeField  = new JTextField(12);
			fatherAgeField  = new VoIntegerTextField(0, 12);
			fatherBirthPlaceField  = new JTextField(20);
			 
			fatherAgeLabel = new JLabel(MessageBundle.getMessage("angal.admission.fatherage"));
			fatherBirthPlaceLabel = new JLabel("    "+MessageBundle.getMessage("angal.admission.fatherbirthplace"));
			 
			if(deliveries != null && deliveries.size()>0){
				fatherAgeField.setText(Integer.toString(deliveries.get(0).getFather_age()));
				fatherBirthPlaceField.setText(deliveries.get(0).getFather_birth_place()!=null?deliveries.get(0).getFather_birth_place().toString():"");
			}
		 
			fatherPanel2.add(fatherAgeLabel);
			fatherPanel2.add(fatherAgeField);
			 
			fatherPanel2.add(fatherBirthPlaceLabel);
			fatherPanel2.add(fatherBirthPlaceField);			 			 
		}
		return fatherPanel2;
	}
	
	private JPanel getNewborn1DeliveryResultTypePanel(){
		if (delrestype1 == null) {
			delrestype1 = new JPanel();
			delrestypeBox1 = new JComboBox();
			delrestypeBox1.addItem("");
			if(deliveryResultTypeList == null)
				if (drtbm== null)
					drtbm = new DeliveryResultTypeBrowserManager();
				deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				if(elem.getDescription().length()>21)
					elem.setDescription(elem.getDescription().substring(0, 20));
				delrestypeBox1.addItem(elem);
				if (deliveries.size()>0 && deliveries.get(0).getDelrestypeid()!= null && deliveries.get(0).getDelrestypeid().equalsIgnoreCase(elem.getCode())) {
					delrestypeBox1.setSelectedItem(elem);
				}								
			}
//			FIXME: verificare se serve
//			if(deliveries!= null && deliveries.size()>0){
//				delrestypeBox1.setSelectedItem(deliveries.get(0).getDelrestypedesc().trim());
//			}
			delrestype1.add(delrestypeBox1);
			delrestype1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
		}
		return delrestype1;
	}
	
	private JPanel getNewborn1HivSatutPanel(){
		if (hivStatutPanel1 == null) {
			hivStatutPanel1 = new JPanel();
			hivStatutBox1 = new JComboBox();
			hivStatutBox1.addItem("                 ");
			hivStatutBox1.addItem("N");
			hivStatutBox1.addItem("P");
			
			if(deliveries!= null && deliveries.size()>0){
				hivStatutBox1.setSelectedItem(deliveries.get(0).getHiv_status()!=null?deliveries.get(0).getHiv_status().toString().trim():"");
			}
			
			hivStatutPanel1.add(hivStatutBox1);
			hivStatutPanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newbornhivstatut")));
		}
		return hivStatutPanel1;
	}
	private JPanel getNewborn2HivSatutPanel(){
		if (hivStatutPanel2 == null) {
			hivStatutPanel2 = new JPanel();
			hivStatutBox2 = new JComboBox();
			hivStatutBox2.addItem("                 ");
			hivStatutBox2.addItem("N");
			hivStatutBox2.addItem("P");
			if(deliveries!= null && deliveries.size()>1){
				hivStatutBox2.setSelectedItem(deliveries.get(1).getHiv_status()!=null?deliveries.get(1).getHiv_status().toString().trim():"");
			}
			hivStatutPanel2.add(hivStatutBox2);
			hivStatutPanel2.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newbornhivstatut")));
		}
		return hivStatutPanel2;
	}
	private JPanel getNewborn3HivSatutPanel(){
		if (hivStatutPanel3 == null) {
			hivStatutPanel3 = new JPanel();
			hivStatutBox3 = new JComboBox();
			hivStatutBox3.addItem("                 ");
			hivStatutBox3.addItem("N");
			hivStatutBox3.addItem("P");
			if(deliveries!= null && deliveries.size()>2){
				hivStatutBox3.setSelectedItem(deliveries.get(2).getHiv_status()!=null?deliveries.get(2).getHiv_status().toString().trim():"");
			}
			hivStatutPanel3.add(hivStatutBox3);
			hivStatutPanel3.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newbornhivstatut")));
		}
		return hivStatutPanel3;
	}
	
	private JPanel getNewborn2Panel(){
		if(newBorn2Panel== null){
			newBorn2Panel= new JPanel();
			newBorn2Panel.setLayout(new BoxLayout(newBorn2Panel, BoxLayout.PAGE_AXIS));
			newBorn2Panel.add(getNewBornName2Panel());
			newBorn2Panel.add(getNewborn2SexPanel());
			newBorn2Panel.add(getNewborn2WeightPanel());
			newBorn2Panel.add(getNewborn2HivSatutPanel());
			newBorn2Panel.add(getNewborn2DeliveryResultTypePanel());
		}
		if(!newborn2EnableCheckbox.isSelected())
			enableDisableFields(newBorn2Panel, false);		
		newBorn2Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newborn2details")));
		return newBorn2Panel;
	}
	
	private JPanel getNewborn2SexPanel(){
		if(sexPanel2== null){
			sexPanel2 = new JPanel();
			sex2Female = new JRadioButton("F");
			sex2Female.setSelected(true);
			sex2Male = new JRadioButton("M");
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(sex2Female);
			resultGroup.add(sex2Male);
			if(deliveries != null && deliveries.size()>1){
				String storedSex = deliveries.get(1).getSex();
				if (storedSex.equals("M"))
					sex2Male.setSelected(true);
			}
			sexPanel2.add(sex2Female);
			sexPanel2.add(sex2Male);
			
		}
		return sexPanel2;
	}
	
	private JPanel getNewborn2WeightPanel(){
		if (weightPanel2 == null) {
			weightPanel2 = new JPanel();
			
			weight2TextField = new VoLimitedTextField(5, 5);
			if(deliveries != null && deliveries.size()>1){
				weight2TextField.setText(new Float(deliveries.get(1).getWeight()).toString());
			}
			weightPanel2.add(weight2TextField);
			weightPanel2.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weight")));
		}
		return weightPanel2;
	}
	
	private JPanel getNewborn2DeliveryResultTypePanel(){
		if (delrestype2 == null) {
			delrestype2 = new JPanel();
			delrestypeBox2 = new JComboBox();
			delrestypeBox2.addItem("");
			if(deliveryResultTypeList == null)
				if(drtbm == null)
					drtbm = new DeliveryResultTypeBrowserManager();
				deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				if(elem.getDescription().length()>21)
					elem.setDescription(elem.getDescription().substring(0, 20));
				delrestypeBox2.addItem(elem);
				if (deliveries.size()>1 &&  deliveries.get(1).getDelrestypeid() != null && deliveries.get(1).getDelrestypeid().equalsIgnoreCase(elem.getCode())) {
					delrestypeBox2.setSelectedItem(elem);
				}
				
			}
			delrestype2.add(delrestypeBox2);
			delrestype2.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
		}
		return delrestype2;
	}
	
	private JPanel getNewborn3Panel(){
		if(newBorn3Panel== null){
			newBorn3Panel= new JPanel();
			newBorn3Panel.setLayout(new BoxLayout(newBorn3Panel, BoxLayout.PAGE_AXIS));
			newBorn3Panel.add(getNewBornName3Panel());
			newBorn3Panel.add(getNewborn3SexPanel());
			newBorn3Panel.add(getNewborn3WeightPanel());
			newBorn3Panel.add(getNewborn3HivSatutPanel());
			newBorn3Panel.add(getNewborn3DeliveryResultTypePanel());
		}
		if(!newborn3EnableCheckBox.isSelected())
			enableDisableFields(newBorn3Panel, false);
		newBorn3Panel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.newborn3details")));
		return newBorn3Panel;
	}
	
	private JPanel getNewborn3SexPanel(){
		if(sexPanel3== null){
			sexPanel3 = new JPanel();
			sex3Female = new JRadioButton("F");
			sex3Female.setSelected(true);
			sex3Male = new JRadioButton("M");
			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(sex3Female);
			resultGroup.add(sex3Male);
			if(deliveries != null && deliveries.size()>2){
				String storedSex = deliveries.get(2).getSex();
				if (storedSex.equals("M"))
					sex3Male.setSelected(true);
			}
			sexPanel3.add(sex3Female);
			sexPanel3.add(sex3Male);
			
		}
		
		return sexPanel3;
	}
	
	private JPanel getNewborn3WeightPanel(){
		if (weightPanel3 == null) {
			weightPanel3 = new JPanel();
			
			weight3TextField = new VoLimitedTextField(5, 5);
			if(deliveries != null && deliveries.size()>2){
				weight3TextField.setText(new Float(deliveries.get(2).getWeight()).toString());
			}
			weightPanel3.add(weight3TextField);
			weightPanel3.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.weight")));
		}
		
		return weightPanel3;
	}
	
	private JPanel getNewborn3DeliveryResultTypePanel(){
		if (delrestype3 == null) {
			delrestype3 = new JPanel();
			delrestypeBox3 = new JComboBox();
			delrestypeBox3.addItem("");
			if(deliveryResultTypeList == null)
				if(drtbm == null)
					drtbm = new DeliveryResultTypeBrowserManager();
				deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				if(elem.getDescription().length()>21)
					elem.setDescription(elem.getDescription().substring(0, 20));
				delrestypeBox3.addItem(elem);
				if (deliveries.size()>2 &&deliveries.get(2).getDelrestypeid()!= null &&deliveries.get(2).getDelrestypeid().equalsIgnoreCase(elem.getCode())) {
					delrestypeBox3.setSelectedItem(elem);
				}
			}
			
			delrestype3.add(delrestypeBox3);
			delrestype3.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.admission.deliveryresultype")));
		}
		return delrestype3;
	}
	
	private JButton getnewPostnatalVisitButton(){
		if(newPostnatalVisitButton== null){
			newPostnatalVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit"));
			newPostnatalVisitButton.setIcon(new ImageIcon("rsc/icons/plus_button.png"));
			newPostnatalVisitButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int visitcount = deliveryManager.patientVisitcount(patient.getCode());
					if(visitcount <1){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.pregnancy.nopregnancy"));
						return;

					}
					((JFrame)myFrame.getParent()).setAlwaysOnTop(false);
					((JFrame)myFrame.getParent()).toBack();
					myFrame.dispose();
					PregnancyEdit newPregnancyVisit = new PregnancyEdit(((JFrame)myFrame.getParent()), patient, 1);
					newPregnancyVisit.setVisible(true);
					newPregnancyVisit.toFront();
					newPregnancyVisit.requestFocus();
					
					
				}
			});
			
		}
		return newPostnatalVisitButton;
	}
	
	private JCheckBox getNewborn2EnableCheckbox(){
		if (newborn2EnableCheckbox== null){
			newborn2EnableCheckbox = new JCheckBox();
			
		}
		if(deliveries!= null && deliveries.size()>1){
			newborn2EnableCheckbox.setSelected(true);
		}
		newborn2EnableCheckbox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newborn2EnableCheckbox.isSelected())
					enableDisableFields(newBorn2Panel, true);
				else
					enableDisableFields(newBorn2Panel, false);
				
			}
		});
		return newborn2EnableCheckbox;
	}
	
	private JCheckBox getNewborn3EnableCheckbox(){
		if (newborn3EnableCheckBox== null){
			newborn3EnableCheckBox = new JCheckBox();
			
		}
		if(deliveries!= null && deliveries.size()>2){
			newborn3EnableCheckBox.setSelected(true);
		}
		newborn3EnableCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newborn3EnableCheckBox.isSelected())
					enableDisableFields(newBorn3Panel, true);
				else
					enableDisableFields(newBorn3Panel, false);
				
			}
		});
		return newborn3EnableCheckBox;
	}
	
	private void enableDisableFields(JPanel panel, boolean enabled){
		for(int a=0; a< panel.getComponentCount(); a++){
			if(panel.getComponent(a).getClass().equals(JPanel.class))
				enableDisableFields((JPanel)panel.getComponent(a), enabled);
			panel.getComponent(a).setEnabled(enabled);
		}
			
	}
	private JButton getSeePregnancyVisitsButton(){
		if(seePregnancyVisitButton== null){
			seePregnancyVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.seepregnancyvisits"));
			seePregnancyVisitButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			seePregnancyVisitButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					((JFrame)myFrame.getParent()).setAlwaysOnTop(false);
					((JFrame)myFrame.getParent()).toBack();
					myFrame.dispose();
					PregnancyCareBrowser pregnancyCareBrowser = new PregnancyCareBrowser(patient);
					pregnancyCareBrowser.setVisible(true);
					pregnancyCareBrowser.toFront();
					pregnancyCareBrowser.requestFocus();
				}
			});
			
		}
		return seePregnancyVisitButton;
	}
	private JPanel getDeliveryTab() {
		if (jPanelDelivery == null) {
			jPanelDelivery = new JPanel();

			GroupLayout layout = new GroupLayout(jPanelDelivery);
			jPanelDelivery.setLayout(layout);

			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			
			if(Param.bool("PREGNANCYCARE")){
				layout.setHorizontalGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(LEADING)
								.addGroup(layout.createSequentialGroup()
										.addGroup(layout.createParallelGroup(LEADING)	
												//.addComponent(getnewPrenatalVisitButton())
												.addComponent(getDeliveryDatePanel(),GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										)										
										.addGroup(layout.createParallelGroup(LEADING)		
												.addComponent(getDeliveryTypePanel(),GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										)
										.addGroup(layout.createParallelGroup(LEADING)		
												//.addComponent(getMotherBirthPlacePanel(),GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										)										
								)
								
								//
//								.addGroup(layout.createSequentialGroup()											
//										.addGroup(layout.createParallelGroup(LEADING)		
//												.addComponent(getFatherInfoPanel(),GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//										)											
//								)
//								.addGroup(layout.createSequentialGroup()																						
//										.addGroup(layout.createParallelGroup(LEADING)		
//												.addComponent(getFatherInfoPanel2(),GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//										)
//								)
								.addGroup(layout.createSequentialGroup()																						
										.addGroup(layout.createParallelGroup(LEADING)		
												.addComponent(fatherPanel(),Alignment.LEADING,GroupLayout.PREFERRED_SIZE,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
										)
								)
								//
																						
							.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(getnewPrenatalVisitButton(), Alignment.CENTER)
										.addComponent(getNewborn1Panel())										
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(getnewPostnatalVisitButton(), Alignment.CENTER)
										.addComponent(getNewborn2EnableCheckbox())
										.addComponent(getNewborn2Panel())										
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(getSeePregnancyVisitsButton(),Alignment.CENTER)
										.addComponent(getNewborn3EnableCheckbox())
										.addComponent(getNewborn3Panel())										
								)
							)
						)
					);


				
				
					layout.setVerticalGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(BASELINE)
									.addComponent(getnewPrenatalVisitButton())
									.addComponent(getnewPostnatalVisitButton())
									.addComponent(getSeePregnancyVisitsButton())
							)
							.addGroup(layout.createParallelGroup(BASELINE)
									.addComponent(getDeliveryDatePanel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(getDeliveryTypePanel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									//.addComponent(getMotherBirthPlacePanel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)									
							)
							//
//							.addGroup(layout.createParallelGroup(BASELINE)
//									.addComponent(getFatherInfoPanel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//							)
//							.addGroup(layout.createParallelGroup(BASELINE)
//									.addComponent(getFatherInfoPanel2(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)									
//							)
							.addGroup(layout.createParallelGroup(BASELINE)
									.addComponent(fatherPanel(),Alignment.LEADING,GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)									
							)
							//
							.addGroup(layout.createParallelGroup(BASELINE)
									.addComponent(getNewborn2EnableCheckbox())
									.addComponent(getNewborn3EnableCheckbox())
									
							)
							.addGroup(layout.createParallelGroup(BASELINE)
									.addComponent(getNewborn1Panel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(getNewborn2Panel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(getNewborn3Panel(),GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							)																					
					);		
			}
			
			else{
				layout.setHorizontalGroup(layout
						.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(LEADING)
										.addComponent(getVisitDatePanel(),
												GroupLayout.PREFERRED_SIZE,
												preferredWidthDates,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getDeliveryDatePanel(),
												GroupLayout.PREFERRED_SIZE,
												preferredWidthDates,
												GroupLayout.PREFERRED_SIZE))
						.addGroup(
								layout.createParallelGroup(LEADING)
										.addComponent(getWeightPanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getDeliveryTypePanel()))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.TRAILING)
										.addComponent(getTreatmentPanel())
										.addComponent(getDeliveryResultTypePanel())
										.addComponent(getControl1DatePanel(),
												GroupLayout.PREFERRED_SIZE,
												preferredWidthDates,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getControl2DatePanel(),
												GroupLayout.PREFERRED_SIZE,
												preferredWidthDates,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getAbortDatePanel(),
												GroupLayout.PREFERRED_SIZE,
												preferredWidthDates,
												GroupLayout.PREFERRED_SIZE)));

				layout.setVerticalGroup(layout
						.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(BASELINE)
										.addComponent(getVisitDatePanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getWeightPanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getTreatmentPanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE))
						.addGroup(
								layout.createParallelGroup(BASELINE)
										.addComponent(getDeliveryDatePanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getDeliveryTypePanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(getDeliveryResultTypePanel(),
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE))
						.addGroup(
								layout.createParallelGroup().addComponent(
										getControl1DatePanel(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGroup(
								layout.createParallelGroup().addComponent(
										getControl2DatePanel(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGroup(
								layout.createParallelGroup().addComponent(
										getAbortDatePanel(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)));

				layout.linkSize(SwingConstants.VERTICAL, getDeliveryDatePanel(),
						getDeliveryTypePanel(), getDeliveryResultTypePanel());
			}
			
		}
		return jPanelDelivery;
	}

	private JScrollPane getJPanelNote() {

		JScrollPane scrollPane = new JScrollPane(getJTextAreaNote());
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10, 50, 0, 50), // external
				new ShadowBorder(5, Color.LIGHT_GRAY))); // internal
		scrollPane.addAncestorListener(new AncestorListener() {

			public void ancestorRemoved(AncestorEvent event) {
			}

			public void ancestorMoved(AncestorEvent event) {
			}

			public void ancestorAdded(AncestorEvent event) {
				textArea.requestFocus();
			}
		});
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		scrollPane.setPreferredSize(new Dimension(screensize.width / 2,
				screensize.height / 2));
		return scrollPane;
	}

	private JTextArea getJTextAreaNote() {
		if (textArea == null) {
			textArea = new JTextArea();
			if (editing && admission.getNote() != null) {
				textArea.setText(admission.getNote());
			}
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setMargin(new Insets(10, 10, 10, 10));
		}
		return textArea;
	}

	private JPanel getTreatmentPanel() {
		if (treatmentPanel == null) {
			treatmentPanel = new JPanel();

			PregnantTreatmentTypeBrowserManager abm = new PregnantTreatmentTypeBrowserManager();
			treatmTypeBox = new JComboBox();
			treatmTypeBox.addItem("");
			treatmTypeList = abm.getPregnantTreatmentType();
			for (PregnantTreatmentType elem : treatmTypeList) {
				treatmTypeBox.addItem(elem);
				System.out.println("desciption "+elem.getDescription());
				if (editing) {
					if (admission.getPregTreatmentType() != null
							&& admission.getPregTreatmentType()
									.equalsIgnoreCase(elem.getCode())) {
						treatmTypeBox.setSelectedItem(elem);
					}
				}
			}

			treatmentPanel.add(treatmTypeBox);
			treatmentPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.treatmenttype")));
		}
		return treatmentPanel;
	}

	private JPanel getWeightPanel() {
		if (weightPanel == null) {
			weightPanel = new JPanel();

			weightField = new VoLimitedTextField(5, 5);
			if (editing && admission.getWeight() != null) {
				weight = admission.getWeight().floatValue();
				weightField.setText(String.valueOf(weight));
			}

			weightPanel.add(weightField);
			weightPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.weight")));
		}
		return weightPanel;
	}

	private JPanel getVisitDatePanel() {
		if (visitDatePanel == null) {
			visitDatePanel = new JPanel();

			Date myDate = null;
			if (editing && admission.getVisitDate() != null) {
				visitDate = admission.getVisitDate();
				myDate = visitDate.getTime();
			} else {
				visitDate = TimeTools.getServerDateTime();
			}
			visitDateFieldCal = new JDateChooser(myDate, "dd/MM/yy"); // Calendar
			visitDateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			visitDateFieldCal.setDateFormatString("dd/MM/yy");

			visitDatePanel.add(visitDateFieldCal); // Calendar
			visitDatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.visitdate")));
		}
		return visitDatePanel;
	}

	private JPanel getDeliveryResultTypePanel() {
		if (deliveryResultTypePanel == null) {
			deliveryResultTypePanel = new JPanel();

			DeliveryResultTypeBrowserManager drtbm = new DeliveryResultTypeBrowserManager();
			deliveryResultTypeBox = new JComboBox();
			deliveryResultTypeBox.addItem("");
			deliveryResultTypeList = drtbm.getDeliveryResultType();
			for (DeliveryResultType elem : deliveryResultTypeList) {
				deliveryResultTypeBox.addItem(elem);
				System.out.println("desciption "+elem.getDescription());
				if (editing) {
					if (admission.getDeliveryResultId() != null
							&& admission.getDeliveryResultId()
									.equalsIgnoreCase(elem.getCode())) {
						deliveryResultTypeBox.setSelectedItem(elem);
					}
				}
			}

			deliveryResultTypePanel.add(deliveryResultTypeBox);
			deliveryResultTypePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.deliveryresultype")));
		}
		return deliveryResultTypePanel;
	}

	private JPanel getDeliveryTypePanel() {
		if (deliveryTypePanel == null) {
			deliveryTypePanel = new JPanel();

			DeliveryTypeBrowserManager dtbm = new DeliveryTypeBrowserManager();
			deliveryTypeBox = new JComboBox();
			deliveryTypeBox.addItem("");
			deliveryTypeList = dtbm.getDeliveryType();
			for (DeliveryType elem : deliveryTypeList) {
				deliveryTypeBox.addItem(elem);
				//System.out.println("desciption "+elem.getDescription());
				if (editing) {
					if (admission.getDeliveryTypeId() != null
							&& admission.getDeliveryTypeId().equalsIgnoreCase(
									elem.getCode())) {
						deliveryTypeBox.setSelectedItem(elem);
					}
				}
			}
			deliveryTypePanel.add(deliveryTypeBox);
			deliveryTypePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.deliverytype")));
		}
		return deliveryTypePanel;
	}

	private JPanel getDeliveryDatePanel() {
		if (deliveryDatePanel == null) {
			deliveryDatePanel = new JPanel();

			Date myDate = null;
			if (editing && admission.getDeliveryDate() != null) {
				deliveryDate = admission.getDeliveryDate();
				myDate = deliveryDate.getTime();
			}
			deliveryDateFieldCal = new JDateChooser(myDate, "dd/MM/yy"); // Calendar
			deliveryDateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			deliveryDateFieldCal.setDateFormatString("dd/MM/yy");

			deliveryDatePanel.add(deliveryDateFieldCal);
			deliveryDatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.deliverydate")));
		}
		return deliveryDatePanel;
	}

	private JPanel getAbortDatePanel() {
		if (abortDatePanel == null) {
			abortDatePanel = new JPanel();
			Date myDate = null;
			if (editing && admission.getAbortDate() != null) {
				abortDate = admission.getAbortDate();
				myDate = abortDate.getTime();
			}
			abortDateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			abortDateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			abortDateFieldCal.setDateFormatString("dd/MM/yy");

			abortDatePanel.add(abortDateFieldCal);
			abortDatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.abortdate")));
		}
		return abortDatePanel;
	}

	private JPanel getControl1DatePanel() {
		if (control1DatePanel == null) {
			control1DatePanel = new JPanel();

			Date myDate = null;
			if (editing && admission.getCtrlDate1() != null) {
				ctrl1Date = admission.getCtrlDate1();
				myDate = ctrl1Date.getTime();
			}
			ctrl1DateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			ctrl1DateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			ctrl1DateFieldCal.setDateFormatString("dd/MM/yy");

			control1DatePanel.add(ctrl1DateFieldCal);
			control1DatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.controln1date")));
		}
		return control1DatePanel;
	}

	private JPanel getControl2DatePanel() {
		if (control2DatePanel == null) {
			control2DatePanel = new JPanel();

			Date myDate = null;
			if (editing && admission.getCtrlDate2() != null) {
				ctrl2Date = admission.getCtrlDate2();
				myDate = ctrl2Date.getTime();
			}
			ctrl2DateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			ctrl2DateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			ctrl2DateFieldCal.setDateFormatString("dd/MM/yy");

			control2DatePanel.add(ctrl2DateFieldCal);
			control2DatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.controln2date")));
		}
		return control2DatePanel;
	}

	private JPanel getProgYearPanel() {
		if (yearProgPanel == null) {
			yearProgPanel = new JPanel();

			if (saveYProg != null) {
				yProgTextField = new JTextField(saveYProg);
			} else if (editing) {
				yProgTextField = new JTextField("" + admission.getYProg());
			} else {
				yProgTextField = new JTextField("");
			}
			yProgTextField.setColumns(11);

			yearProgPanel.add(yProgTextField);
			yearProgPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.progressiveinyear")));

		}
		return yearProgPanel;
	}

	private JPanel getFHUPanel() {
		if (fhuPanel == null) {
			fhuPanel = new JPanel();

			if (editing) {
				FHUTextField = new JTextField(admission.getFHU());
			} else {
				FHUTextField = new JTextField();
			}
			FHUTextField.setColumns(20);

			fhuPanel.add(FHUTextField);
			fhuPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle
					.getMessage("angal.admission.fromhealthunit")));

		}
		return fhuPanel;
	}

	private JPanel getWardPanel() {
		if (wardPanel == null) {
			wardPanel = new JPanel();

			WardBrowserManager wbm = new WardBrowserManager();
			wardBox = new JComboBox();
			wardBox.addItem("");
			wardList = wbm.getWards();
			for (Ward ward : wardList) {
				// if patient is a male you don't see pregnancy case
				if (("" + patient.getSex()).equalsIgnoreCase("F")
						&& !ward.isFemale()) {
					continue;
				} else if (("" + patient.getSex()).equalsIgnoreCase("M")
						&& !ward.isMale()) {
					continue;
				} else {
					if (ward.getBeds() > 0)
						wardBox.addItem(ward);
				}
				if (saveWard != null) {
					if (saveWard.getCode().equalsIgnoreCase(ward.getCode())) {
						wardBox.setSelectedItem(ward);
					}
				} else if (editing) {
					if (admission.getWardId().equalsIgnoreCase(ward.getCode())) {
						wardBox.setSelectedItem(ward);
					}
				}else if (admission.getWardId() != null && admission.getWardId().length()>0) {
					if (admission.getWardId().equalsIgnoreCase(ward.getCode())) {
						wardBox.setSelectedItem(ward);
					}
				}
			}
			wardBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// set yProg
					if (wardBox.getSelectedIndex() == 0) {
						yProgTextField.setText("");
						return;
					} else {
						String wardId = ((Ward) wardBox.getSelectedItem())
								.getCode();
						if (wardId.equalsIgnoreCase(admission.getWardId())) {
							yProgTextField.setText("" + admission.getYProg());
						} else {
							AdmissionBrowserManager abm = new AdmissionBrowserManager();
							int nextProg = abm.getNextYProg(wardId);
							yProgTextField.setText("" + nextProg);

							// get default selected warn default beds number
							int nBeds = (((Ward) wardBox.getSelectedItem())
									.getBeds()).intValue();
							int usedBeds = abm.getUsedWardBed(wardId);
							int freeBeds = nBeds - usedBeds;
							if (freeBeds <= 0)
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.wardwithnobedsavailable"));
						}
					}

					// switch panel
					if (((Ward) wardBox.getSelectedItem()).getCode()
							.equalsIgnoreCase("M")) {
						if (!viewingPregnancy) {
							saveWard = (Ward) wardBox.getSelectedItem();
							saveYProg = yProgTextField.getText();
							viewingPregnancy = true;
							jTabbedPaneAdmission.setEnabledAt(
									pregnancyTabIndex, true);
							validate();
							repaint();
						}
					} else {
						if (viewingPregnancy) {
							saveWard = (Ward) wardBox.getSelectedItem();
							saveYProg = yProgTextField.getText();
							viewingPregnancy = false;
							jTabbedPaneAdmission.setEnabledAt(
									pregnancyTabIndex, false);
							validate();
							repaint();
						}
					}

				}
			});
			
			wardPanel.add(wardBox);
			wardPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle
					.getMessage("angal.admission.ward")));
		}
		return wardPanel;
	}

	private JPanel getDiseaseInPanel() {
		if (diseaseInPanel == null) {
			diseaseInPanel = new JPanel();
			/////
			diseaseInBox = new JComboBox();
			diseaseInBox.setPreferredSize(new Dimension(
					preferredWidthDiagnosis, preferredHeightLine));
			diseaseInBox.addItem("");
			/////
			boolean found = false;
			if (editing) {
				diseaseInList = dbm.getDiseaseAll();
				for (Disease elem : diseaseInList) {
					diseaseInBox.addItem(elem);
					if (admission.getDiseaseInId() != null
							&& admission.getDiseaseInId().equalsIgnoreCase(
									elem.getCode())) {
						diseaseInBox.setSelectedItem(elem);
						found = true;
					}
				}
			} else {
				for (Disease elem : diseaseInList) { // cycle for future uses
					boolean ok = true;
					if (ok)
						diseaseInBox.addItem(elem);
				}
			}
			if (editing && !found && admission.getDiseaseInId() != null) {
				diseaseInBox
						.addItem(MessageBundle.getMessage("angal.admission.no")
								+ admission.getDiseaseInId()
								+ MessageBundle
										.getMessage("angal.admission.notfoundasinpatientdisease"));
				diseaseInBox.setSelectedIndex(diseaseInBox.getItemCount() - 1);
			}

			diseaseInPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.diagnosisinstar")));
			GridBagLayout gbl_diseaseInPanel = new GridBagLayout();
			gbl_diseaseInPanel.columnWidths = new int[]{108, 32, 550, 0};
			gbl_diseaseInPanel.rowHeights = new int[]{24, 0};
			gbl_diseaseInPanel.columnWeights = new double[]{2.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_diseaseInPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			diseaseInPanel.setLayout(gbl_diseaseInPanel);
			
			searchDiseasetextField = new JTextField();
			GridBagConstraints gbc_searchDiseasetextField = new GridBagConstraints();
			gbc_searchDiseasetextField.insets = new Insets(0, 0, 0, 5);
			gbc_searchDiseasetextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_searchDiseasetextField.gridx = 0;
			gbc_searchDiseasetextField.gridy = 0;
			diseaseInPanel.add(searchDiseasetextField, gbc_searchDiseasetextField);
			searchDiseasetextField.setColumns(10);
			///
			searchDiseasetextField.addKeyListener(new KeyListener() {
				
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 searchButton.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {
				}
	
				public void keyTyped(KeyEvent e) {
				}
			});
			///
			
			searchButton = new JButton("");
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					getSearchDiagnosisBox(searchDiseasetextField.getText(), diseaseInBox, diseaseInList);
				}
			});
			searchButton.setPreferredSize(new Dimension(20, 20));
			searchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			GridBagConstraints gbc_searchButton = new GridBagConstraints();
			gbc_searchButton.insets = new Insets(0, 0, 0, 5);
			gbc_searchButton.gridx = 1;
			gbc_searchButton.gridy = 0;
			diseaseInPanel.add(searchButton, gbc_searchButton);
//			diseaseInBox = new JComboBox();
//			diseaseInBox.setPreferredSize(new Dimension(
//					preferredWidthDiagnosis, preferredHeightLine));
//			diseaseInBox.addItem("");
			GridBagConstraints gbc_diseaseInBox = new GridBagConstraints();
			gbc_diseaseInBox.anchor = GridBagConstraints.NORTHWEST;
			gbc_diseaseInBox.gridx = 2;
			gbc_diseaseInBox.gridy = 0;
			diseaseInPanel.add(diseaseInBox, gbc_diseaseInBox);
		}
		return diseaseInPanel;
	}

	/**
	 * @return
	 */
	private JPanel getMalnutritionPanel() {
		if (malnuPanel == null) {
			malnuPanel = new JPanel();

			malnuCheck = new JCheckBox();
			if (editing && admission.getType().equalsIgnoreCase("M")) {
				malnuCheck.setSelected(true);
			} else {
				malnuCheck.setSelected(false);
			}

			malnuPanel.add(malnuCheck);
			malnuPanel.add(
					new JLabel(MessageBundle
							.getMessage("angal.admission.malnutrition")),
					BorderLayout.CENTER);

		}
		return malnuPanel;
	}

	private JPanel getAdmissionTypePanel() {
		if (admissionTypePanel == null) {
			admissionTypePanel = new JPanel();

			admTypeBox = new JComboBox();
			admTypeBox.setPreferredSize(new Dimension(preferredWidthTypes,
					preferredHeightLine));
			admTypeBox.addItem("");
			admTypeList = admMan.getAdmissionType();
			for (AdmissionType elem : admTypeList) {
				admTypeBox.addItem(elem);
				if (editing) {
					if (admission.getAdmType().equalsIgnoreCase(elem.getCode())) {
						admTypeBox.setSelectedItem(elem);
					}
				}
			}

			admissionTypePanel.add(admTypeBox);
			admissionTypePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.admissiontype")));

		}
		return admissionTypePanel;
	}

	/**
	 * @return
	 */
	private JPanel getAdmissionDatePanel() {
		if (admissionDatePanel == null) {
			admissionDatePanel = new JPanel();

			if (editing) {
				dateIn = admission.getAdmDate();
			} else {
				dateIn = RememberDates.getLastAdmInDateGregorian();
			}
			dateInFieldCal = new JDateChooser(dateIn.getTime(), "dd/MM/yy"); // Calendar
			dateInFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			dateInFieldCal.setDateFormatString("dd/MM/yy");
			dateInFieldCal.addPropertyChangeListener("date",
					new PropertyChangeListener() {

						public void propertyChange(PropertyChangeEvent evt) {
							updateBedDays();
						}
					});

			admissionDatePanel.add(dateInFieldCal);
			admissionDatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.admissiondate")));
		}
		return admissionDatePanel;
	}

	private JPanel getDiseaseOutPanel() {
		if (diseaseOutPanel == null) {
			diseaseOutPanel = new JPanel();
			diseaseOutPanel.setLayout(new BoxLayout(diseaseOutPanel,
					BoxLayout.Y_AXIS));
			diseaseOutPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.diagnosisout")));
			diseaseOutPanel.add(getDiseaseOut1Panel());
			diseaseOutPanel.add(getDiseaseOut2Panel());
			diseaseOutPanel.add(getDiseaseOut3Panel());
		}
		return diseaseOutPanel;
	}

	/**
	 * @return
	 */
	private JPanel getDiseaseOut1Panel() {
		boolean found = false;
		//////
		diseaseOut1Box = new JComboBox();
		diseaseOut1Box.setPreferredSize(new Dimension(preferredWidthDiagnosis,
				preferredHeightLine));
		diseaseOut1Box.addItem("");
		//////
		if (editing) {
			diseaseOutList = dbm.getDiseaseAll();
			for (Disease elem : diseaseOutList) {
				diseaseOut1Box.addItem(elem);
				if (admission.getDiseaseOutId1() != null
						&& admission.getDiseaseOutId1().equalsIgnoreCase(
								elem.getCode())) {
					diseaseOut1Box.setSelectedItem(elem);
					found = true;
				}
			}
		} else {
			for (Disease elem : diseaseOutList) { // cycle for future uses
				boolean ok = true;
				if (ok)
					diseaseOut1Box.addItem(elem);
			}
		}
		if (editing && !found && admission.getDiseaseOutId1() != null) {
			diseaseOut1Box
					.addItem(MessageBundle.getMessage("angal.admission.no")
							+ admission.getDiseaseOutId1()
							+ " "
							+ MessageBundle
									.getMessage("angal.admission.notfoundasinpatientdisease"));
			diseaseOut1Box.setSelectedIndex(diseaseOut1Box.getItemCount() - 1);
		}
		JPanel diseaseOut1 = new JPanel();
		GridBagLayout gbl_diseaseOut1 = new GridBagLayout();
		gbl_diseaseOut1.columnWidths = new int[]{50, 0, 0, 550, 0};
		gbl_diseaseOut1.rowHeights = new int[]{50, 0};
		gbl_diseaseOut1.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_diseaseOut1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		diseaseOut1.setLayout(gbl_diseaseOut1);
		
				JLabel label = new JLabel(
						MessageBundle.getMessage("angal.admission.number1"),
						SwingConstants.RIGHT);
				label.setPreferredSize(new Dimension(50, 50));
				label.setHorizontalTextPosition(SwingConstants.RIGHT);
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.anchor = GridBagConstraints.NORTHEAST;
				gbc_label.insets = new Insets(0, 0, 0, 5);
				gbc_label.gridx = 0;
				gbc_label.gridy = 0;
				diseaseOut1.add(label, gbc_label);
				
				searchDiseaseOut1textField = new JTextField();
				GridBagConstraints gbc_searchDiseaseOut1textField = new GridBagConstraints();
				gbc_searchDiseaseOut1textField.insets = new Insets(0, 0, 0, 5);
				gbc_searchDiseaseOut1textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_searchDiseaseOut1textField.gridx = 1;
				gbc_searchDiseaseOut1textField.gridy = 0;
				diseaseOut1.add(searchDiseaseOut1textField, gbc_searchDiseaseOut1textField);
				searchDiseaseOut1textField.setColumns(10);
				///
				searchDiseaseOut1textField.addKeyListener(new KeyListener() {
					
					public void keyPressed(KeyEvent e) {
						int key = e.getKeyCode();
					     if (key == KeyEvent.VK_ENTER) {
					    	 searchDiseaseOut1Button.doClick();
					     }
					}
		
					public void keyReleased(KeyEvent e) {
					}
		
					public void keyTyped(KeyEvent e) {
					}
				});
				////
				
				searchDiseaseOut1Button = new JButton("");
				searchDiseaseOut1Button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getSearchDiagnosisBox(searchDiseaseOut1textField.getText(),diseaseOut1Box, diseaseOutList);
					}
				});
				searchDiseaseOut1Button.setPreferredSize(new Dimension(20, 20));
				searchDiseaseOut1Button.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
				GridBagConstraints gbc_searchDiseaseOut1Button = new GridBagConstraints();
				gbc_searchDiseaseOut1Button.insets = new Insets(0, 0, 0, 5);
				gbc_searchDiseaseOut1Button.gridx = 2;
				gbc_searchDiseaseOut1Button.gridy = 0;
				diseaseOut1.add(searchDiseaseOut1Button, gbc_searchDiseaseOut1Button);
		
//				diseaseOut1Box = new JComboBox();
//				diseaseOut1Box.setPreferredSize(new Dimension(preferredWidthDiagnosis,
//						preferredHeightLine));
//				diseaseOut1Box.addItem("");
				GridBagConstraints gbc_diseaseOut1Box = new GridBagConstraints();
				gbc_diseaseOut1Box.anchor = GridBagConstraints.WEST;
				gbc_diseaseOut1Box.gridx = 3;
				gbc_diseaseOut1Box.gridy = 0;
				diseaseOut1.add(diseaseOut1Box, gbc_diseaseOut1Box);

		return diseaseOut1;
	}

	private JPanel getDiseaseOut2Panel() {
		boolean found = false;
		////
		diseaseOut2Box = new JComboBox();
		diseaseOut2Box.setPreferredSize(new Dimension(preferredWidthDiagnosis,
				preferredHeightLine));
		diseaseOut2Box.addItem("");
		/////
		if (editing) {
			diseaseOutList = dbm.getDiseaseAll();
			for (Disease elem : diseaseOutList) {
				diseaseOut2Box.addItem(elem);
				if (admission.getDiseaseOutId2() != null
						&& admission.getDiseaseOutId2().equalsIgnoreCase(
								elem.getCode())) {
					diseaseOut1Box.setSelectedItem(elem);
					found = true;
				}
			}
		} else {
			for (Disease elem : diseaseOutList) { // cycle for future uses
				boolean ok = true;
				if (ok)
					diseaseOut2Box.addItem(elem);
			}
		}
		if (editing && !found && admission.getDiseaseOutId2() != null) {
			diseaseOut2Box
					.addItem(MessageBundle.getMessage("angal.admission.no")
							+ admission.getDiseaseOutId1()
							+ " "
							+ MessageBundle
									.getMessage("angal.admission.notfoundasinpatientdisease"));
			diseaseOut2Box.setSelectedIndex(diseaseOut2Box.getItemCount() - 1);
		}
		JPanel diseaseOut2 = new JPanel();
		GridBagLayout gbl_diseaseOut2 = new GridBagLayout();
		gbl_diseaseOut2.columnWidths = new int[]{50, 0, 0, 550, 0};
		gbl_diseaseOut2.rowHeights = new int[]{50, 0};
		gbl_diseaseOut2.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_diseaseOut2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		diseaseOut2.setLayout(gbl_diseaseOut2);
		
				JLabel label = new JLabel(
						MessageBundle.getMessage("angal.admission.number2"),
						SwingConstants.RIGHT);
				label.setPreferredSize(new Dimension(50, 50));
				label.setHorizontalTextPosition(SwingConstants.RIGHT);
				GridBagConstraints gbc_label = new GridBagConstraints();
				gbc_label.anchor = GridBagConstraints.NORTHEAST;
				gbc_label.insets = new Insets(0, 0, 0, 5);
				gbc_label.gridx = 0;
				gbc_label.gridy = 0;
				diseaseOut2.add(label, gbc_label);
				
				searchDiseaseOut2textField = new JTextField();
				GridBagConstraints gbc_searchDiseaseOut2textField = new GridBagConstraints();
				gbc_searchDiseaseOut2textField.insets = new Insets(0, 0, 0, 5);
				gbc_searchDiseaseOut2textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_searchDiseaseOut2textField.gridx = 1;
				gbc_searchDiseaseOut2textField.gridy = 0;
				diseaseOut2.add(searchDiseaseOut2textField, gbc_searchDiseaseOut2textField);
				searchDiseaseOut2textField.setColumns(10);
				///
				searchDiseaseOut2textField.addKeyListener(new KeyListener() {
					
					public void keyPressed(KeyEvent e) {
						int key = e.getKeyCode();
					     if (key == KeyEvent.VK_ENTER) {
					    	 searchDiseaseOut2Button.doClick();
					     }
					}
		
					public void keyReleased(KeyEvent e) {
					}
		
					public void keyTyped(KeyEvent e) {
					}
				});
				////
				
				searchDiseaseOut2Button = new JButton("");
				searchDiseaseOut2Button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getSearchDiagnosisBox(searchDiseaseOut2textField.getText(),diseaseOut2Box,diseaseOutList);
					}
				});
				searchDiseaseOut2Button.setPreferredSize(new Dimension(20, 20));
				searchDiseaseOut2Button.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
				GridBagConstraints gbc_searchDiseaseOut2Button = new GridBagConstraints();
				gbc_searchDiseaseOut2Button.insets = new Insets(0, 0, 0, 5);
				gbc_searchDiseaseOut2Button.gridx = 2;
				gbc_searchDiseaseOut2Button.gridy = 0;
				diseaseOut2.add(searchDiseaseOut2Button, gbc_searchDiseaseOut2Button);
		
//				diseaseOut2Box = new JComboBox();
//				diseaseOut2Box.setPreferredSize(new Dimension(preferredWidthDiagnosis,
//						preferredHeightLine));
//				diseaseOut2Box.addItem("");
				GridBagConstraints gbc_diseaseOut2Box = new GridBagConstraints();
				gbc_diseaseOut2Box.anchor = GridBagConstraints.WEST;
				gbc_diseaseOut2Box.gridx = 3;
				gbc_diseaseOut2Box.gridy = 0;
				diseaseOut2.add(diseaseOut2Box, gbc_diseaseOut2Box);

		return diseaseOut2;
	}

	private JPanel getDiseaseOut3Panel() {
		boolean found = false;
		/////
		diseaseOut3Box = new JComboBox();
		diseaseOut3Box.setPreferredSize(new Dimension(preferredWidthDiagnosis,
				preferredHeightLine));
		diseaseOut3Box.addItem("");
		/////
		if (editing) {
			diseaseOutList = dbm.getDiseaseAll();
			for (Disease elem : diseaseOutList) {
				diseaseOut3Box.addItem(elem);
				if (admission.getDiseaseOutId3() != null
						&& admission.getDiseaseOutId3().equalsIgnoreCase(
								elem.getCode())) {
					diseaseOut1Box.setSelectedItem(elem);
					found = true;
				}
			}
		} else {
			for (Disease elem : diseaseOutList) {// cycle for future uses
				boolean ok = true;
				if (ok)
					diseaseOut3Box.addItem(elem);
			}
		}
		if (editing && !found && admission.getDiseaseOutId3() != null) {
			diseaseOut3Box
					.addItem(MessageBundle.getMessage("angal.admission.no")
							+ admission.getDiseaseOutId3()
							+ " "
							+ MessageBundle
									.getMessage("angal.admission.notfoundasinpatientdisease"));
			diseaseOut3Box.setSelectedIndex(diseaseOut3Box.getItemCount() - 1);
		}
		JPanel diseaseOut3 = new JPanel();
		GridBagLayout gbl_diseaseOut3 = new GridBagLayout();
		gbl_diseaseOut3.columnWidths = new int[]{50, 0, 0, 550, 0};
		gbl_diseaseOut3.rowHeights = new int[]{50, 0};
		gbl_diseaseOut3.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_diseaseOut3.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		diseaseOut3.setLayout(gbl_diseaseOut3);
				
						JLabel label = new JLabel(
								MessageBundle.getMessage("angal.admission.number3"),
								SwingConstants.RIGHT);
						label.setPreferredSize(new Dimension(50, 50));
						GridBagConstraints gbc_label = new GridBagConstraints();
						gbc_label.anchor = GridBagConstraints.NORTHEAST;
						gbc_label.insets = new Insets(0, 0, 0, 5);
						gbc_label.gridx = 0;
						gbc_label.gridy = 0;
						diseaseOut3.add(label, gbc_label);
				
				searchDiseaseOut3textField = new JTextField();
				GridBagConstraints gbc_searchDiseaseOut3textField = new GridBagConstraints();
				gbc_searchDiseaseOut3textField.insets = new Insets(0, 0, 0, 5);
				gbc_searchDiseaseOut3textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_searchDiseaseOut3textField.gridx = 1;
				gbc_searchDiseaseOut3textField.gridy = 0;
				diseaseOut3.add(searchDiseaseOut3textField, gbc_searchDiseaseOut3textField);
				searchDiseaseOut3textField.setColumns(10);
				///
				searchDiseaseOut3textField.addKeyListener(new KeyListener() {
					
					public void keyPressed(KeyEvent e) {
						int key = e.getKeyCode();
					     if (key == KeyEvent.VK_ENTER) {
					    	 searchDiseaseOut3Button.doClick();
					     }
					}
		
					public void keyReleased(KeyEvent e) {
					}
		
					public void keyTyped(KeyEvent e) {
					}
				});
				////
				
				searchDiseaseOut3Button = new JButton("");
				searchDiseaseOut3Button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						getSearchDiagnosisBox(searchDiseaseOut3textField.getText(),diseaseOut3Box,diseaseOutList);
					}
				});
				searchDiseaseOut3Button.setPreferredSize(new Dimension(20, 20));
				searchDiseaseOut3Button.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
				GridBagConstraints gbc_searchDiseaseOut3Button = new GridBagConstraints();
				gbc_searchDiseaseOut3Button.insets = new Insets(0, 0, 0, 5);
				gbc_searchDiseaseOut3Button.gridx = 2;
				gbc_searchDiseaseOut3Button.gridy = 0;
				diseaseOut3.add(searchDiseaseOut3Button, gbc_searchDiseaseOut3Button);
		
//				diseaseOut3Box = new JComboBox();
//				diseaseOut3Box.setPreferredSize(new Dimension(preferredWidthDiagnosis,
//						preferredHeightLine));
//				diseaseOut3Box.addItem("");
				GridBagConstraints gbc_diseaseOut3Box = new GridBagConstraints();
				gbc_diseaseOut3Box.anchor = GridBagConstraints.WEST;
				gbc_diseaseOut3Box.gridx = 3;
				gbc_diseaseOut3Box.gridy = 0;
				diseaseOut3.add(diseaseOut3Box, gbc_diseaseOut3Box);

		return diseaseOut3;
	}

	/*
	 * simply an utility
	 */
	private JRadioButton getRadioButton(String label, char mn, boolean active) {
		JRadioButton rb = new JRadioButton(label);
		rb.setMnemonic(KeyEvent.VK_A + (mn - 'A'));
		rb.setSelected(active);
		rb.setName(label);
		return rb;
	}

	/*
	 * admission sheet: 5th row: insert select operation type and result
	 */
	private JPanel getOperationPanel() {
		if (operationPanel == null) {
			operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

			OperationBrowserManager obm = new OperationBrowserManager();
			operationBox = new JComboBox();
			operationBox.addItem("");
			operationList = obm.getOperation();
			for (Operation elem : operationList) {
				operationBox.addItem(elem);
				if (editing) {
					if (admission.getOperationId() != null
							&& admission.getOperationId().equalsIgnoreCase(
									elem.getCode())) {
						operationBox.setSelectedItem(elem);
					}
				}
			}
			operationBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (operationBox.getSelectedIndex() == 0) {
						// operationDateField.setText("");
						operationDateFieldCal.setDate(null);
					} else {
						/*
						 * if (!operationDateField.getText().equals("")){ //
						 * leave old date value }
						 */
						if (operationDateFieldCal.getDate() != null) {
							// leave old date value
						}

						else {
							// set today date
							operationDateFieldCal
									.setDate((TimeTools.getServerDateTime())
											.getTime());
						}
					}
				}
			});

			operationPanel.add(operationBox);
			operationPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.operationtype")));
		}
		return operationPanel;
	}

	/**
	 * @return
	 */
	private JPanel getOperationResultPanel() {
		if (resultPanel == null) {
			resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

			operationResultRadioP = getRadioButton(
					MessageBundle.getMessage("angal.admission.positive"), 'P',
					false);
			operationResultRadioN = getRadioButton(
					MessageBundle.getMessage("angal.admission.negative"), 'N',
					false);
			operationResultRadioU = getRadioButton(
					MessageBundle.getMessage("angal.admission.unknown"), 'U',
					true);

			ButtonGroup resultGroup = new ButtonGroup();
			resultGroup.add(operationResultRadioP);
			resultGroup.add(operationResultRadioN);
			resultGroup.add(operationResultRadioU);

			if (editing) {
				if (admission.getOpResult() != null) {
					if (admission.getOpResult().equalsIgnoreCase("P"))
						operationResultRadioP.setSelected(true);
					else
						operationResultRadioN.setSelected(true);
				}
			}

			resultPanel.add(operationResultRadioP);
			resultPanel.add(operationResultRadioN);
			resultPanel.add(operationResultRadioU);

			resultPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.operationresult")));
		}
		return resultPanel;
	}

	/*
	 * admission sheet: 6th row: insert operation date and transusional unit
	 */
	private JPanel getOperationDatePanel() {
		if (operationDatePanel == null) {
			operationDatePanel = new JPanel();

			Date myDate = null;
			if (editing && admission.getOpDate() != null) {
				operationDate = admission.getOpDate();
				myDate = operationDate.getTime();
			}
			operationDateFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			operationDateFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			operationDateFieldCal.setDateFormatString("dd/MM/yy");

			operationDatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.operationdate")));
			operationDatePanel.add(operationDateFieldCal);
		}
		return operationDatePanel;
	}

	/**
	 * @return
	 */
	private JPanel getTransfusionPanel() {
		if (transfusionPanel == null) {
			transfusionPanel = new JPanel();

			float start = 0;
			float min = 0;
			float step = (float) 0.5;

			SpinnerModel model = new SpinnerNumberModel(start, min, null, step);
			trsfUnitField = new JSpinner(model);
			trsfUnitField.setPreferredSize(new Dimension(
					preferredWidthTransfusionSpinner, preferredHeightLine));

			if (editing && admission.getTransUnit() != null) {
				trsfUnit = admission.getTransUnit().floatValue();
				trsfUnitField.setValue(trsfUnit);
			}

			transfusionPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.transfusionalunit")));
			transfusionPanel.add(trsfUnitField);
		}
		return transfusionPanel;
	}

	private JPanel getDischargeTypePanel() {
		if (dischargeTypePanel == null) {
			dischargeTypePanel = new JPanel();

			disTypeBox = new JComboBox();
			disTypeBox.setPreferredSize(new Dimension(preferredWidthTypes,
					preferredHeightLine));
			disTypeBox.addItem("");
			disTypeList = admMan.getDischargeType();
			for (DischargeType elem : disTypeList) {
				disTypeBox.addItem(elem);
				if (editing) {
					if (admission.getDisType() != null
							&& admission.getDisType().equalsIgnoreCase(
									elem.getCode())) {
						disTypeBox.setSelectedItem(elem);
					}
				}
			}

			dischargeTypePanel.add(disTypeBox);
			dischargeTypePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.dischargetype")));
		}
		return dischargeTypePanel;
	}

	private JPanel getBedDaysPanel() {
		if (bedDaysPanel == null) {
			bedDaysPanel = new JPanel();

			bedDaysTextField = new VoLimitedTextField(10, 10);
			bedDaysTextField.setEditable(false);

			bedDaysPanel.add(bedDaysTextField);
			bedDaysPanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.beddays")));
		}
		return bedDaysPanel;
	}

	private void updateBedDays() {
		try {
			int bedDays = TimeTools.getDaysBetweenDates(
					dateInFieldCal.getDate(), dateOutFieldCal.getDate());
			if (bedDays == 0)
				bedDays = 1;
			bedDaysTextField.setText(String.valueOf(bedDays));
		} catch (Exception e) {
			bedDaysTextField.setText("");
		}
	}

	/**
	 * @return
	 */
	private JPanel getDischargeDatePanel() {
		if (dischargeDatePanel == null) {
			dischargeDatePanel = new JPanel();

			Date myDate = null;
			if (editing && admission.getDisDate() != null) {
				dateOut = admission.getDisDate();
				myDate = dateOut.getTime();
			}
			dateOutFieldCal = new JDateChooser(myDate, "dd/MM/yy");
			dateOutFieldCal.setLocale(new Locale(Param.string("LANGUAGE")));
			dateOutFieldCal.setDateFormatString("dd/MM/yy");
			dateOutFieldCal.addPropertyChangeListener("date",
					new PropertyChangeListener() {

						public void propertyChange(PropertyChangeEvent evt) {
							updateBedDays();
						}
					});

			dischargeDatePanel.add(dateOutFieldCal);
			dischargeDatePanel.setBorder(BorderFactory
					.createTitledBorder(MessageBundle
							.getMessage("angal.admission.dischargedate")));

		}
		return dischargeDatePanel;
	}

	private JComboBox shareWith = null;
	private JTextField searchDiseasetextField;
	private JTextField searchDiseaseOut1textField;
	private JTextField searchDiseaseOut2textField;
	private JTextField searchDiseaseOut3textField;

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getSaveButton());
			if (MainMenu.checkUserGrants("btnadmadmexamination"))
				buttonPanel.add(getJButtonExamination());
			buttonPanel.add(getCloseButton());

//			if (Param.bool("XMPPMODULEENABLED")) {
//				Interaction share = new Interaction();
//				Collection<String> contacts = share.getContactOnline();
//				contacts.add("-- Share alert with: nobody --");
//				shareWith = new JComboBox(contacts.toArray());
//				shareWith.setSelectedItem("-- Share alert with: nobody --");
//				buttonPanel.add(shareWith);
//			}
		}
		return buttonPanel;
	}

	private JButton getJButtonExamination() {
		if (jButtonExamination == null) {
			jButtonExamination = new JButton(
					MessageBundle.getMessage("angal.opd.examination"));
			jButtonExamination.setMnemonic(KeyEvent.VK_E);

			jButtonExamination.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					PatientExamination patex;
					ExaminationOperations examOperations = new ExaminationOperations();

					PatientExamination lastPatex = examOperations
							.getLastByPatID(patient.getCode());
					if (lastPatex != null) {
						patex = examOperations
								.getFromLastPatientExamination(lastPatex);
					} else {
						patex = examOperations
								.getDefaultPatientExamination(patient);
					}

					GenderPatientExamination gpatex = new GenderPatientExamination(
							patex, patient.getSex().equals(new Character('M')));

					PatientExaminationEdit dialog = new PatientExaminationEdit(
							AdmissionBrowser.this, gpatex);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
			});
		}
		return jButtonExamination;
	}

	/**
	 * @return
	 */
	private JLabel getJLabelRequiredFields() {
		if (labelRequiredFields == null) {
			labelRequiredFields = new JLabel(
					MessageBundle
							.getMessage("angal.admission.indicatesrequiredfields"));
		}
		return labelRequiredFields;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText(MessageBundle.getMessage("angal.common.close"));
			closeButton.setMnemonic(KeyEvent.VK_C);
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return closeButton;
	}

	private JButton getSaveButton() {

		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText(MessageBundle.getMessage("angal.common.save"));
			saveButton.setMnemonic(KeyEvent.VK_S);
			saveButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					
					try {
				
						/*
						 * During save, add a wait cursor to the window and
						 * disable all widgets it contains. They are enabled
						 * back in the <code>finally</code> block instead of
						 * enabling them before every single
						 * <code>return</code>.
						 */
						BusyState.setBusyState(AdmissionBrowser.this, true);

						/*
						 * Initizalize AdmissionBrowserManager
						 */
						AdmissionBrowserManager abm = new AdmissionBrowserManager();
						ArrayList<Admission> admList = abm
								.getAdmissions(patient);

						/*
						 * Today Gregorian Calendar
						 */
						GregorianCalendar today = TimeTools.getServerDateTime();

						/*
						 * is it an admission update or a discharge? if we have
						 * a valid discharge date isDischarge will be true
						 */
						boolean isDischarge = false;

						/*
						 * set if ward pregnancy is selected
						 */
						boolean isPregnancy = false;

						// get ward id (not null)
						if (wardBox.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.pleaseselectavalidward"));
							return;
						} else {
							admission.setWardId(((Ward) (wardBox
									.getSelectedItem())).getCode());
						}

						if (admission.getWardId().equalsIgnoreCase("M")) {
							isPregnancy = true;
						}

						// get disease in id ( it can be null)
						if (diseaseInBox.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.pleaseselectavaliddiseasein"));
							return;
						} else {
							try {
								Disease diseaseIn = (Disease) diseaseInBox
										.getSelectedItem();
								admission.setDiseaseInId(diseaseIn.getCode());
							} catch (IndexOutOfBoundsException e1) {
								/*
								 * Workaround in case a fake-disease is selected
								 * (ie when previous disease has been deleted)
								 */
								admission.setDiseaseInId(null);
							}
						}

						// get disease out id ( it can be null)
						int disease1index = diseaseOut1Box.getSelectedIndex();
						if (disease1index == 0) {
							admission.setDiseaseOutId1(null);
						} else {
							Disease diseaseOut1 = (Disease) diseaseOut1Box
									.getSelectedItem();
							admission.setDiseaseOutId1(diseaseOut1.getCode());
						}

						// get disease out id 2 ( it can be null)
						int disease2index = diseaseOut2Box.getSelectedIndex();
						if (disease2index == 0) {
							admission.setDiseaseOutId2(null);
						} else {
							Disease diseaseOut2 = (Disease) diseaseOut2Box
									.getSelectedItem();
							admission.setDiseaseOutId2(diseaseOut2.getCode());
						}

						// get disease out id 3 ( it can be null)
						int disease3index = diseaseOut3Box.getSelectedIndex();
						if (disease3index == 0) {
							admission.setDiseaseOutId3(null);
						} else {
							Disease diseaseOut3 = (Disease) diseaseOut3Box
									.getSelectedItem();
							admission.setDiseaseOutId3(diseaseOut3.getCode());
						}

						// get year prog ( not null)
						try {
							int x = Integer.parseInt(yProgTextField.getText());
							if (x < 0) {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertacorrectprogressiveid"));
								return;
							} else {
								admission.setYProg(x);
							}
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.pleaseinsertacorrectprogressiveid"));
							return;
						}
						
						//set prog month
						if(!editing){
							GregorianCalendar datep = TimeTools.getServerDateTime();
							admission.setMProg(abm.getProgMonth(datep.get(GregorianCalendar.MONTH)+1, datep.get(GregorianCalendar.YEAR))+1);
						}
						
						
												
						// get FHU (it can be null)
						String s = FHUTextField.getText();
						if (s.equals("")) {
							admission.setFHU(null);
						} else {
							admission.setFHU(FHUTextField.getText());
						}

						// check and get date in (not null)
						String d = currentDateFormat.format(dateInFieldCal
								.getDate());

						try {
							currentDateFormat.setLenient(false);
							Date date = currentDateFormat.parse(d);
							
							dateIn = TimeTools.getServerDateTime();
							dateIn.setTime(dateInFieldCal.getDate());
							
							//set the same time with the server
							GregorianCalendar ddatte = TimeTools.getServerDateTime();
							dateIn.set(Calendar.HOUR_OF_DAY,ddatte.get(Calendar.HOUR_OF_DAY));
							dateIn.set(Calendar.MINUTE,ddatte.get(Calendar.MINUTE));
							dateIn.set(Calendar.SECOND,ddatte.get(Calendar.SECOND));
							//
							
							if (dateIn.after(today)) {
								System.out.println(" ici erreur");
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.futuredatenotallowed"));
								d = currentDateFormat.format(today);
								dateInFieldCal.setDate(currentDateFormat
										.parse(d));
								return;
							}
							if (dateIn.before(today)) {
								// check for invalid date
								for (Admission ad : admList) {
									if (editing
											&& ad.getId() == admission.getId()) {
										continue;
									}
									if ((ad.getAdmDate().before(dateIn) || ad
											.getAdmDate().compareTo(dateIn) == 0)
											&& (ad.getDisDate() != null && ad
													.getDisDate().after(dateIn))) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.ininserteddatepatientwasalreadyadmitted"));
										d = currentDateFormat.format(today);
										dateInFieldCal
												.setDate(currentDateFormat
														.parse(d));
										return;
									}
								}
							}
							// updateDisplay
							d = currentDateFormat.format(date);
							dateInFieldCal.setDate(currentDateFormat.parse(d));
							admission.setAdmDate(dateIn);
							RememberDates.setLastAdmInDate(dateIn);

						} catch (ParseException pe) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.pleaseinsertavalidadmissiondate"));
							return;
						} catch (IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.pleaseinsertavalidadmissiondate"));
							return;
						}

						// get admission type (not null)
						if (admTypeBox.getSelectedIndex() == 0) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.pleaseselectavalidadmissiondate"));
							return;
						} else {
							admission.setAdmType(admTypeList.get(
									admTypeBox.getSelectedIndex() - 1)
									.getCode());
						}

						// check and get date out (it can be null)
						// if set date out, isDischarge is set
						if (dateOutFieldCal.getDate() != null) {
							d = currentDateFormat.format(dateOutFieldCal
									.getDate());
						} else
							d = "";

						if (d.equals("")) {
							// only if we are editing the last admission
							// or if it is a new admission
							// no if we are editing an old admission
							Admission last = null;
							if (admList.size() > 0) {
								last = admList.get(admList.size() - 1);
							} else {
								last = admission;
							}
							if (!editing
									|| (editing && admission.getId() == last
											.getId())) {
								// ok
							} else {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertadischargedate")
												+ ", "
												+ MessageBundle
														.getMessage("angal.admission.youareeditinganoldadmission"));
								return;

							}

							admission.setDisDate(null);
						} else {
							try {
								currentDateFormat.setLenient(false);
								Date date = currentDateFormat.parse(d);
								dateOut = TimeTools.getServerDateTime();
								// dateOut.setTime(date);
								dateOut.setTime(dateOutFieldCal.getDate());
								
								//set the same time with the server
								GregorianCalendar ddattee = TimeTools.getServerDateTime();
								dateOut.set(Calendar.HOUR_OF_DAY,ddattee.get(Calendar.HOUR_OF_DAY));
								dateOut.set(Calendar.MINUTE,ddattee.get(Calendar.MINUTE));
								dateOut.set(Calendar.SECOND,ddattee.get(Calendar.SECOND));
								//

								// date control
								if (dateOut.before(dateIn)) {
									JOptionPane
											.showMessageDialog(
													AdmissionBrowser.this,
													MessageBundle
															.getMessage("angal.admission.dischargedatemustbeafteradmissiondate"));
									return;
								}
								if (dateOut.after(today)) {
									System.out.println(" ici erreur 2");
									JOptionPane
											.showMessageDialog(
													AdmissionBrowser.this,
													MessageBundle
															.getMessage("angal.admission.futuredatenotallowed"));
									return;
								} else {
									// check for invalid date
									boolean invalidDate = false;
									Date invalidStart = new Date();
									Date invalidEnd = new Date();
									for (Admission ad : admList) {
										// case current admission : let it be
										if (editing
												&& ad.getId() == admission
														.getId()) {
											continue;
										}
										// found an open admission
										// only if i close my own first of it
										if (ad.getDisDate() == null) {
											if (!dateOut.after(ad.getAdmDate()))
												;// ok
											else {
												JOptionPane
														.showMessageDialog(
																AdmissionBrowser.this,
																MessageBundle
																		.getMessage("angal.admission.intheselecteddatepatientwasadmittedagain"));
												return;
											}
										}
										// general case
										else {
											// DateIn >= adOut
											if (dateIn.after(ad.getDisDate())
													|| dateIn.equals(ad
															.getDisDate())) {
												// ok
											}
											// dateOut <= adIn
											else if (dateOut.before(ad
													.getAdmDate())
													|| dateOut.equals(ad
															.getAdmDate())) {
												// ok
											} else {
												invalidDate = true;
												invalidStart = ad.getAdmDate()
														.getTime();
												invalidEnd = ad.getDisDate()
														.getTime();
												break;
											}
										}
									}
									if (invalidDate) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.invalidadmissionperiod")
																+ MessageBundle
																		.getMessage("angal.admission.theadmissionbetween")
																+ " "
																+ currentDateFormat
																		.format(invalidStart)
																+ " "
																+ MessageBundle
																		.getMessage("angal.admission.and")
																+ " "
																+ currentDateFormat
																		.format(invalidEnd)
																+ " "
																+ MessageBundle
																		.getMessage("angal.admission.alreadyexists"));
										dateOutFieldCal.setDate(null);
										return;
									}

								}

								// updateDisplay
								d = currentDateFormat.format(date);
								dateOutFieldCal.setDate(currentDateFormat
										.parse(d));
								admission.setDisDate(dateOut);
								isDischarge = true;
							} catch (ParseException pe) {
								System.out.println(pe);
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertavaliddischargedate"));
								return;
							} catch (IllegalArgumentException iae) {
								System.out.println(iae);
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertavaliddischargedate"));
								return;
							}
						}
// begin of repuperation operation
						/*
						// get operation ( it can be null)
						if (operationBox.getSelectedIndex() == 0) {
							admission.setOperationId(null);
						} else {
							admission.setOperationId(operationList.get(
									operationBox.getSelectedIndex() - 1)
									.getCode());
						}

						// get operation date (may be null)
						if (operationDateFieldCal.getDate() != null) {
							d = currentDateFormat.format(operationDateFieldCal
									.getDate());
						} else
							d = "";
						if (d.equals("")) {
							admission.setOpDate(null);
						} else {
							try {
								Date date = currentDateFormat.parse(d);
								operationDate = TimeTools.getServerDateTime();
								operationDate.setTime(date);
								// updateDisplay
								d = currentDateFormat.format(date);
								operationDateFieldCal.setDate(currentDateFormat
										.parse(d));

								GregorianCalendar limit;
								if (admission.getDisDate() == null) {
									limit = today;
								} else {
									limit = admission.getDisDate();
								}

								if (operationDate.before(dateIn)
										|| operationDate.after(limit)) {
									JOptionPane
											.showMessageDialog(
													AdmissionBrowser.this,
													MessageBundle
															.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
									return;
								}

								admission.setOpDate(operationDate);
							} catch (ParseException pe) {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
								operationDateField.setText(currentDateFormat
										.format(operationDate.getTime()));
								return;
							} catch (IllegalArgumentException iae) {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
								operationDateField.setText(currentDateFormat
										.format(operationDate.getTime()));
								return;
							}
						}// else

						// get operation result (can be null)
						if (operationResultRadioN.isSelected()) {
							admission.setOpResult("N");
						} else if (operationResultRadioP.isSelected()) {
							admission.setOpResult("P");
						} else {
							admission.setOpResult(null);
						}
//end reuperation operation
 
 */
						// get discharge type (it can be null)
						// if isDischarge, null value not allowed
						if (disTypeBox.getSelectedIndex() == 0) {
							if (isDischarge) {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseselectavaliddischargetype"));
								return;
							} else {
								admission.setDisType(null);
							}
						} else {
							if (dateOut == null) {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertadischargedate"));
								return;
							}
							if (isDischarge) {
								admission.setDisType(disTypeList.get(
										disTypeBox.getSelectedIndex() - 1)
										.getCode());
							} else {
								admission.setDisType(null);
							}
						}

						// get the disease out n.1 (it can be null)
						// if isDischarge, null value not allowed
						if (admission.getDiseaseOutId1() == null) {
							if (isDischarge) {
								int yes = JOptionPane.showConfirmDialog(
										null,
										MessageBundle
												.getMessage("angal.admission.diagnosisoutsameasdiagnosisin"));
								if (yes == JOptionPane.YES_OPTION) {
									if (diseaseOutList.contains(diseaseInBox
											.getSelectedItem()))
										admission.setDiseaseOutId1(admission
												.getDiseaseInId());
									else {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseselectavaliddiagnosisout"));
										return;
									}
								} else {
									JOptionPane
											.showMessageDialog(
													AdmissionBrowser.this,
													MessageBundle
															.getMessage("angal.admission.pleaseselectatleastfirstdiagnosisout"));
									return;
								}
							}
						} else {
							if (admission.getDisDate() == null) {
								JOptionPane.showMessageDialog(
										AdmissionBrowser.this,
										MessageBundle
												.getMessage("angal.admission.pleaseinsertadischargedate"));
								return;
							}
						}

						// field notes
						if (textArea.getText().equals("")) {
							admission.setNote(null);
						} else {
							admission.setNote(textArea.getText());
						}

						// get transfusional unit (it can be null)
//						try {
//							float f = (Float) trsfUnitField.getValue();
//							admission.setTransUnit(new Float(f));
//						} catch (Exception ex) {
//							JOptionPane.showMessageDialog(
//									AdmissionBrowser.this,
//									MessageBundle
//											.getMessage("angal.admission.pleaseinsertavalidunitvalue"));
//							return;
//						}

						// fields for pregnancy status
						if (isPregnancy) {

							if(Param.bool("PREGNANCYCARE")){
								deliveries = new ArrayList<Delivery>();
								Delivery del1 = new Delivery();
								del1.setSex(sex1Female.isSelected()?"F":"M");
								if(delrestypeBox1.getSelectedIndex()==0){
									del1.setDelrestypeid(null);
								}
								else{
									del1.setDelrestypeid(deliveryResultTypeList.get(delrestypeBox1.getSelectedIndex()-1).getCode());
								}
								try {
									if (weight1TextField.getText().equals("")) {
										del1.setWeight(0);
									} else {
										float f = Float.parseFloat(weight1TextField.getText());
										if (f < 0.0f) {
											JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
											return;
										} else {
											del1.setWeight(new Float(f));
										}
									}
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
									return;
								}
								del1.setHiv_status(hivStatutBox1.getSelectedItem().toString().trim());
								
								del1.setFather_birth_place(fatherBirthPlaceField.getText().toString().trim());
								del1.setFather_name(fatherNameField.getText().toString().trim());
								del1.setFather_age(Integer.parseInt(fatherAgeField.getText().toString().trim()));
								del1.setFather_occupation(fatherOccupationField.getText().toString().trim());
								del1.setFather_residence(fatherResidenceField.getText().toString().trim());
								
								del1.setChild_name(newBornNameField1.getText().toString().trim());
								
								deliveries.add(del1);
								if(newborn2EnableCheckbox.isSelected()){// a second newborn
									Delivery del2 = new Delivery();
									del2.setSex(sex2Female.isSelected()?"F":"M");
									if(delrestypeBox2.getSelectedIndex()==0){
										del2.setDelrestypeid(null);
									}
									else{
										del2.setDelrestypeid(deliveryResultTypeList.get(delrestypeBox2.getSelectedIndex()-1).getCode());
									}
									try {
										if (weight2TextField.getText().equals("")) {
											del2.setWeight(0);
										} else {
											float f = Float.parseFloat(weight2TextField.getText());
											if (f < 0.0f) {
												JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
												return;
											} else {
												del2.setWeight(new Float(f));
											}
										}
									} catch (Exception ex) {
										JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
										return;
									}
									del2.setHiv_status(hivStatutBox2.getSelectedItem().toString().trim());
									
									del2.setFather_birth_place(fatherBirthPlaceField.getText().toString().trim());
									del2.setFather_name(fatherNameField.getText().toString().trim());
									del2.setFather_age(Integer.parseInt(fatherAgeField.getText().toString().trim()));
									del2.setFather_occupation(fatherOccupationField.getText().toString().trim());
									del2.setFather_residence(fatherResidenceField.getText().toString().trim());
									
									del2.setChild_name(newBornNameField2.getText().toString().trim());
									
									deliveries.add(del2);
								}
								if(newborn3EnableCheckBox.isSelected()){// a second newborn
									Delivery del3 = new Delivery();
									del3.setSex(sex3Female.isSelected()?"F":"M");
									if(delrestypeBox3.getSelectedIndex()==0){
										del3.setDelrestypeid(null);
									}
									else{
										del3.setDelrestypeid(deliveryResultTypeList.get(delrestypeBox3.getSelectedIndex()-1).getCode());
									}
									try {
										if (weight3TextField.getText().equals("")) {
											del3.setWeight(0);
										} else {
											float f = Float.parseFloat(weight3TextField.getText());
											if (f < 0.0f) {
												JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
												return;
											} else {
												del3.setWeight(new Float(f));
											}
										}
									} catch (Exception ex) {
										JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
										return;
									}
									del3.setHiv_status(hivStatutBox3.getSelectedItem().toString().trim());
									
									del3.setFather_birth_place(fatherBirthPlaceField.getText().toString().trim());
									del3.setFather_name(fatherNameField.getText().toString().trim());
									del3.setFather_age(Integer.parseInt(fatherAgeField.getText().toString().trim()));
									del3.setFather_occupation(fatherOccupationField.getText().toString().trim());
									del3.setFather_residence(fatherResidenceField.getText().toString().trim());
									
									del3.setChild_name(newBornNameField3.getText().toString().trim());
									
									deliveries.add(del3);
								}
								String deliveryType=null;
								if (deliveryTypeBox.getSelectedIndex() != 0) {
									deliveryType = deliveryTypeList.get(deliveryTypeBox.getSelectedIndex() - 1).getCode();
								}
								admission.setDeliveryTypeId(deliveryType);
								// get delivery date
								
								if (deliveryDateFieldCal.getDate() != null) {
									d = currentDateFormat.format(deliveryDateFieldCal.getDate());
									try {
										Date date = currentDateFormat.parse(d);
										deliveryDate = new GregorianCalendar();										
										deliveryDate.setTime(date);
										

										// date control
										GregorianCalendar start;
										if (admission.getVisitDate() == null) {
											start = admission.getAdmDate();											
										} else {
											start = admission.getVisitDate();											
										}

										GregorianCalendar limit;
										if (admission.getDisDate() == null) {
											limit = today;
										} else {
											limit = admission.getDisDate();
										}
//77777
										//if (deliveryDate.before(start) || deliveryDate.after(limit)) {
										//set the same time with the server
										GregorianCalendar ddatte = TimeTools.getServerDateTime();
										deliveryDate.set(Calendar.HOUR_OF_DAY,ddatte.get(Calendar.HOUR_OF_DAY));
										deliveryDate.set(Calendar.MINUTE,ddatte.get(Calendar.MINUTE));
										deliveryDate.set(Calendar.SECOND,ddatte.get(Calendar.SECOND));
										//
									    if (deliveryDate.before(start)) {
											JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
											deliveryDateFieldCal.setDate(today.getTime());
											return;
										}

										// updateDisplay
										d = currentDateFormat.format(date);
										deliveryDateFieldCal.setDate(currentDateFormat.parse(d));
										admission.setDeliveryDate(deliveryDate);

									} catch (ParseException pe) {
										JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
										deliveryDateFieldCal.setDate(today.getTime());
										return;
									} catch (IllegalArgumentException iae) {
										JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
										deliveryDateFieldCal.setDate(today.getTime());
										return;
									}
								} 
								else{
									JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
									return;
								}
								
								for(int a=0; a<deliveries.size();a++){
									deliveries.get(a).setDeltypeid(deliveryType);
									deliveries.get(a).setDeliveryDate(deliveryDate);
								}
							}
							else{
								// get visit date (may be null)
								if (visitDateFieldCal.getDate() != null) {
									d = currentDateFormat.format(visitDateFieldCal
											.getDate());
								} else
									d = "";
								if (d.equals("")) {
									admission.setVisitDate(null);
								} else {
									try {
										Date date = currentDateFormat.parse(d);
										visitDate = TimeTools.getServerDateTime();
										visitDate.setTime(date);
										// updateDisplay
										d = currentDateFormat.format(date);
										visitDateFieldCal.setDate(currentDateFormat
												.parse(d));

										GregorianCalendar limit;
										if (admission.getDisDate() == null) {
											limit = today;
										} else {
											limit = admission.getDisDate();
										}

										if (visitDate.before(dateIn)
												|| visitDate.after(limit)) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
											return;
										}

										admission.setVisitDate(visitDate);
									} catch (ParseException pe) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
										visitDateField.setText(currentDateFormat
												.format(visitDate.getTime()));// CONTROLLARE
										return;
									} catch (IllegalArgumentException iae) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidvisitdate"));
										visitDateField.setText(currentDateFormat
												.format(visitDate.getTime()));// CONTROLLARE
										return;
									}
								}// else

								// get weight (it can be null)
								try {
									if (weightField.getText().equals("")) {
										admission.setWeight(null);
									} else {
										float f = Float.parseFloat(weightField
												.getText());
										if (f < 0.0f) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
											return;
										} else {
											admission.setWeight(new Float(f));
										}
									}
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(
											AdmissionBrowser.this,
											MessageBundle
													.getMessage("angal.admission.pleaseinsertavalidweightvalue"));
									return;
								}

								// get treatment type(may be null)
								if (treatmTypeBox.getSelectedIndex() == 0) {
									admission.setPregTreatmentType(null);
								} else {
									admission.setPregTreatmentType(treatmTypeList
											.get(treatmTypeBox.getSelectedIndex() - 1)
											.getCode());

								}

								// get delivery date
								if (deliveryDateFieldCal.getDate() != null) {
									d = currentDateFormat
											.format(deliveryDateFieldCal.getDate());
								} else
									d = "";

								if (d.equals("")) {
									admission.setDeliveryDate(null);
								} else {
									try {
										Date date = currentDateFormat.parse(d);
										deliveryDate = TimeTools.getServerDateTime();
										deliveryDate.setTime(date);

										// date control
										GregorianCalendar start;
										if (admission.getVisitDate() == null) {
											start = admission.getAdmDate();
										} else {
											start = admission.getVisitDate();
										}

										GregorianCalendar limit;
										if (admission.getDisDate() == null) {
											limit = today;
										} else {
											limit = admission.getDisDate();
										}

										if (deliveryDate.before(start)
												|| deliveryDate.after(limit)) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
											deliveryDateFieldCal.setDate(null);
											return;
										}

										// updateDisplay
										d = currentDateFormat.format(date);
										deliveryDateFieldCal
												.setDate(currentDateFormat.parse(d));
										admission.setDeliveryDate(deliveryDate);

									} catch (ParseException pe) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
										deliveryDateField.setText("");// CONTROLLARE
										return;
									} catch (IllegalArgumentException iae) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavaliddeliverydate"));
										deliveryDateField.setText("");// CONTROLLARE
										return;
									}
								}

								// get delivery type
								if (deliveryTypeBox.getSelectedIndex() == 0) {
									admission.setDeliveryTypeId(null);
								} else {
									admission.setDeliveryTypeId(deliveryTypeList
											.get(deliveryTypeBox.getSelectedIndex() - 1)
											.getCode());
								}

								// get delivery result type
								if (deliveryResultTypeBox.getSelectedIndex() == 0) {
									admission.setDeliveryResultId(null);
								} else {
									admission
											.setDeliveryResultId(deliveryResultTypeList
													.get(deliveryResultTypeBox
															.getSelectedIndex() - 1)
													.getCode());
								}

								// get ctrl1 date
								if (ctrl1DateFieldCal.getDate() != null) {
									d = currentDateFormat.format(ctrl1DateFieldCal
											.getDate());
								} else
									d = "";

								if (d.equals("")) {
									admission.setCtrlDate1(null);
								} else {
									try {
										Date date = currentDateFormat.parse(d);
										ctrl1Date = TimeTools.getServerDateTime();
										ctrl1Date.setTime(date);

										// date control
										if (admission.getDeliveryDate() == null) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.controln1datenodeliverydatefound"));
											return;
										}
										GregorianCalendar limit;
										if (admission.getDisDate() == null) {
											limit = today;
										} else {
											limit = admission.getDisDate();
										}
										if (ctrl1Date.before(deliveryDate)
												|| ctrl1Date.after(limit)) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.pleaseinsertavalidcontroln1date"));
											return;
										}

										// updateDisplay
										d = currentDateFormat.format(date);
										ctrl1DateFieldCal.setDate(currentDateFormat
												.parse(d));
										admission.setCtrlDate1(ctrl1Date);

									} catch (ParseException pe) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidcontroln1date"));
										ctrl1DateFieldCal.setDate(null);
										return;
									} catch (IllegalArgumentException iae) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidcontroln1date"));
										ctrl1DateFieldCal.setDate(null);
										return;
									}
								}

								// get ctrl2 date
								if (ctrl2DateFieldCal.getDate() != null) {
									d = currentDateFormat.format(ctrl2DateFieldCal
											.getDate());
								} else
									d = "";

								if (d.equals("")) {
									admission.setCtrlDate2(null);
								} else {
									if (admission.getCtrlDate1() == null) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.controldaten2controldaten1notfound"));
										ctrl2DateFieldCal.setDate(null);
										return;
									}
									try {
										Date date = currentDateFormat.parse(d);
										ctrl2Date = TimeTools.getServerDateTime();
										ctrl2Date.setTime(date);

										// date control
										GregorianCalendar limit;
										if (admission.getDisDate() == null) {
											limit = today;
										} else {
											limit = admission.getDisDate();
										}
										if (ctrl2Date.before(ctrl1Date)
												|| ctrl2Date.after(limit)) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.pleaseinsertavalidcontroln2date"));
											return;
										}

										// updateDisplay
										d = currentDateFormat.format(date);
										ctrl2DateFieldCal.setDate(currentDateFormat
												.parse(d));
										admission.setCtrlDate2(ctrl2Date);

									} catch (ParseException pe) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidcontroln2date"));
										ctrl2DateFieldCal.setDate(null);
										return;
									} catch (IllegalArgumentException iae) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidcontroln2date"));
										ctrl2DateFieldCal.setDate(null);
										return;
									}
								}

								// get abort date
								if (abortDateFieldCal.getDate() != null) {
									d = currentDateFormat.format(abortDateFieldCal
											.getDate());
								} else
									d = "";

								if (d.equals("")) {
									admission.setAbortDate(null);
								} else {
									try {
										Date date = currentDateFormat.parse(d);
										abortDate = TimeTools.getServerDateTime();
										abortDate.setTime(date);

										// date control
										GregorianCalendar limit;
										if (admission.getDisDate() == null) {
											limit = today;
										} else {
											limit = admission.getDisDate();
										}
										if (ctrl2Date != null
												&& abortDate.before(ctrl2Date)
												|| ctrl1Date != null
												&& abortDate.before(ctrl1Date)
												|| abortDate.before(visitDate)
												|| abortDate.after(limit)) {
											JOptionPane
													.showMessageDialog(
															AdmissionBrowser.this,
															MessageBundle
																	.getMessage("angal.admission.pleaseinsertavalidabortdate"));
											return;
										}

										// updateDisplay
										d = currentDateFormat.format(date);
										abortDateFieldCal.setDate(currentDateFormat
												.parse(d));
										admission.setAbortDate(abortDate);

									} catch (ParseException pe) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidabortdate"));
										abortDateFieldCal.setDate(null);
										return;
									} catch (IllegalArgumentException iae) {
										JOptionPane
												.showMessageDialog(
														AdmissionBrowser.this,
														MessageBundle
																.getMessage("angal.admission.pleaseinsertavalidabortdate"));
										abortDateFieldCal.setDate(null);
										return;
									}
								}
							}
							

						}// isPregnancy

						// set not editable fields
						String user = MainMenu.getUser();
						// String admUser = admission.getUserID();
						// if (admUser != null && !admUser.equals(user)) {
						// int yes =
						// JOptionPane.showConfirmDialog(AdmissionBrowser.this,
						// MessageBundle.getMessage("angal.admission.youaresigningnewdatawithyournameconfirm"));
						// if (yes != JOptionPane.YES_OPTION) return;
						// }
						admission.setUserID(user);
						admission.setPatId(patient.getCode());

						if (admission.getDisDate() == null) {
							admission.setAdmitted(1);
						} else {
							admission.setAdmitted(0);
						}

						if (malnuCheck.isSelected()) {
							admission.setType("M");
						} else if (isPregnancy) {
							admission.setType("P");
						}else {
							admission.setType("N");
						}

						admission.setDeleted("N");

						// IOoperation result
						boolean result = false;

						// ready to save...
						if (!editing && !isDischarge) {
							/**** date operation controle ****/
							if(!checkAllOperationRowDate(operationad.getOprowData(), admission))
							{
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admition.check.operationdate"), MessageBundle.getMessage("angal.hospital"),
											JOptionPane.PLAIN_MESSAGE);
					  		    return;
							}	
						    /*********************************/
							int newKey = admMan
									.newAdmissionReturnKey(admission);
							if (newKey > 0) {
								for(int a=0; a< deliveries.size(); a++){
									deliveryManager.insertDelivery(newKey, deliveries.get(a));
								}
								result = true;
								admission.setId(newKey);
								fireAdmissionInserted(admission);
								if (Param.bool("XMPPMODULEENABLED")) {
//									CommunicationFrame frame = (CommunicationFrame) CommunicationFrame
//											.getFrame();
//									frame.sendMessage(
//											"new patient admission: "
//													+ patient.getName()
//													+ " in "
//													+ ((Ward) wardBox
//															.getSelectedItem())
//															.getDescription(),
//											(String) shareWith
//													.getSelectedItem(), false);
								}
								dispose();
							}
						} else if (!editing && isDischarge) {
//							result = admMan.newAdmission(admission);
//							System.out.println("instruction ici 2");
//							if (result) {
//								fireAdmissionUpdated(admission);
//								dispose();
//							}
							/**** date operation controle ****/
							if(!checkAllOperationRowDate(operationad.getOprowData(), admission))
							{								
						  		  JOptionPane.showMessageDialog(AdmissionBrowser.this,
							    			  MessageBundle.getMessage("angal.admition.check.operationdate") , MessageBundle.getMessage("angal.hospital"),
												JOptionPane.PLAIN_MESSAGE);
						  		  return;						    
							}
							int idAdmission = admMan.newAdmissionReturnKey(admission);
							if (idAdmission > 0) {
								for(int a=0; a< deliveries.size(); a++){
									deliveryManager.insertDelivery(idAdmission, deliveries.get(a));
								}
								result = true;
								admission.setId(idAdmission);
								fireAdmissionUpdated(admission);
								dispose();
							}
						} else {
							/**** date operation controle ****/
							if(!checkAllOperationRowDate(operationad.getOprowData(), admission))
							{
								JOptionPane.showMessageDialog(AdmissionBrowser.this,
										MessageBundle.getMessage("angal.admition.check.operationdate"), MessageBundle.getMessage("angal.hospital"),
											JOptionPane.PLAIN_MESSAGE);
					  		    return;
							}
							result = admMan.updateAdmission(admission);
							if (result) {
								deliveryManager.deleteDeliveries(admission.getId());
								for(int a=0; a< deliveries.size(); a++){
									deliveryManager.insertDelivery(admission.getId(), deliveries.get(a));
								}
								fireAdmissionUpdated(admission);
								if (Param.bool("XMPPMODULEENABLED")) {
//									CommunicationFrame frame = (CommunicationFrame) CommunicationFrame
//											.getFrame();
//									frame.sendMessage(
//											"discharged patient: "
//													+ patient.getName()
//													+ " for "
//													+ ((DischargeType) disTypeBox
//															.getSelectedItem())
//															.getDescription(),
//											(String) shareWith
//													.getSelectedItem(), false);
								}
								dispose();
							}
						}

						if (!result) {
							JOptionPane.showMessageDialog(
									AdmissionBrowser.this,
									MessageBundle
											.getMessage("angal.admission.thedatacouldnotbesaved"));
						} else {
							dispose();
						}
					} finally {
						BusyState.setBusyState(AdmissionBrowser.this, false);
					}
				}
			});
		}
		return saveButton;
	}
    
	public boolean checkAllOperationRowDate(List<OperationRow> list, Admission admission){
		Date beginDate,endDate;
		if(admission.getAdmDate()!=null)beginDate=admission.getAdmDate().getTime();else beginDate=null;
		if(admission.getDisDate()!=null)endDate=admission.getDisDate().getTime();else endDate=null;
		for (org.isf.operation.model.OperationRow opRow : list) {
			Date currentRowDate = opRow.getOpDate().getTime();
			if((beginDate!=null)&&(endDate!=null)){
				if((currentRowDate.before(beginDate))||(currentRowDate.after(endDate))){
					return false;
				}
			}
			if((beginDate!=null)&&(endDate==null)){
				if(currentRowDate.before(beginDate)){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void patientUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
//		Patient admPatient = (Patient) e.getSource();
//		this.patient = admPatient;
//		ps = new PatientSummary(patient);
		
		//jContentPane.repaint();
		//this.up;
		//jContentPane.add(getDataPanel(), java.awt.BorderLayout.CENTER);
		//jContentPane.repaint();
		//System.out.println("patientUpdated "+patient.getCity());
	}

	@Override
	public void patientInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		System.out.println("patientInserted");
	}
	
	private void getSearchDiagnosisBox(String s, JComboBox comboDiagnosis,ArrayList<Disease> diseaseList) {
		String key = s;
		String[] s1;
		comboDiagnosis.removeAllItems();
		comboDiagnosis.addItem("");
		for (Disease elem : diseaseList) {
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

}// class
