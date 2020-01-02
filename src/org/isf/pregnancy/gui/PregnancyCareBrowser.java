package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.isf.admission.gui.AdmissionBrowser;
import org.isf.admission.gui.AdmittedPatientBrowser;
import org.isf.admission.gui.AdmissionBrowser.AdmissionListener;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.gui.LabBrowser;
import org.isf.medicals.model.Medical;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.gui.Menu;
import org.isf.opd.gui.OpdBrowser;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patvac.gui.PatVacBrowser;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.manager.PregnancyDeliveryManager;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.PregnancyReport;
import org.isf.utils.jobjects.ValidationPatientGroup;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dialog.ModalExclusionType;

/**
 * 
 * @author Martin Reinstadler
 *
 */
public class PregnancyCareBrowser extends JFrame implements
		PatientInsert.PatientListener, PatientInsertExtended.PatientListener,
		PregnancyEdit.PregnancyListener , AdmissionListener{

	/**
	 * @uml.property name="pColums" multiplicity="(0 -1)" dimension="1"
	 */
	private String[] pColums = {
			MessageBundle.getMessage("angal.admission.code"),
			MessageBundle.getMessage("angal.admission.name"),
			MessageBundle.getMessage("angal.admission.age"),
			MessageBundle.getMessage("angal.admission.address") };
	/**
	 * @uml.property name="pColumwidth" multiplicity="(0 -1)" dimension="1"
	 */
	private int[] pColumwidth = { 20, 200, 20, 150 };
	/**
	 * @uml.property name="vColums" multiplicity="(0 -1)" dimension="1"
	 */
	private String[] vColums = {
			MessageBundle.getMessage("angal.pregnancy.pregnancynumber"),
			MessageBundle.getMessage("angal.pregnancy.visitdate"),
			MessageBundle.getMessage("angal.pregnancy.visittype"),
			MessageBundle.getMessage("angal.pregnancy.visitnote") };
	/**
	 * @uml.property name="vColumwidth" multiplicity="(0 -1)" dimension="1"
	 */
	private int[] vColumwidth = { 20, 40, 40, 220 };
	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property name="myFrame"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private PregnancyCareBrowser myFrame = null;
	/**
	 * @uml.property name="pregnancyPatientList"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="org.isf.patient.model.Patient"
	 */
	private ArrayList<AdmittedPatient> pregnancyPatientList = null;
	/**
	 * @uml.property name="patientList"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="org.isf.patient.model.Patient"
	 */
	ArrayList<AdmittedPatient> patientList = new ArrayList<AdmittedPatient>();
	/**
	 * @uml.property name="pregnancyvisits"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="org.isf.pregnancy.model.PregnancyVisit"
	 */
	private ArrayList<PregnancyVisit> pregnancyvisits = null;
	/**
	 * @uml.property name="manager"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private PregnancyCareManager manager = new PregnancyCareManager();
	/**
	 * @uml.property name="admissionManager"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private PregnancyDeliveryManager pregdelManager = new PregnancyDeliveryManager();
	/**
	 * @uml.property name="patientTable"
	 * @uml.associationEnd
	 */
	private JTable patientTable = null;
	/**
	 * @uml.property name="visitTable"
	 * @uml.associationEnd
	 */
	private JTable visitTable = null;
	/**
	 * @uml.property name="newPatientButton"
	 * @uml.associationEnd
	 */
	private JButton newPatientButton = null;
	/**
	 * @uml.property name="editPatientButton"
	 * @uml.associationEnd
	 */
	private JButton editPatientButton = null;
	/**
	 * @uml.property name="deletePatientButton"
	 * @uml.associationEnd
	 */
	private JButton deletePatientButton = null;
	/**
	 * @uml.property name="patient"
	 * @uml.associationEnd
	 */
	private Patient patient = null;
	/**
	 * @uml.property name="pvisit"
	 * @uml.associationEnd
	 */
	private PregnancyVisit pvisit;
	/**
	 * @uml.property name="patientScrollPane"
	 * @uml.associationEnd
	 */
	private JScrollPane patientScrollPane = null;
	/**
	 * @uml.property name="visitScrollPane"
	 * @uml.associationEnd
	 */
	private JScrollPane visitScrollPane = null;
	/**
	 * @uml.property name="searchPatientTextField"
	 * @uml.associationEnd
	 */
	private JTextField searchPatientTextField = null;
		/**
	
	/**
	 * @uml.property name="deltypeLabel"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="javax.swing.JLabel"
	 */
	private ArrayList<JLabel> deltypeLabel = null;
	/**
	 * @uml.property name="deltypeResLabel"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="javax.swing.JLabel"
	 */
	private ArrayList<JLabel> deltypeResLabel = null;
	/**
	 * @uml.property name="jSearchButton"
	 * @uml.associationEnd
	 */
	private JButton jSearchButton = null;
	/**
	 * @uml.property name="lastKey"
	 */
	private String lastKey = "";
	private JButton jButtonExams;
	private JButton jButtonVaccin;
	private DefaultTableModel model;
	
	JButton next = new JButton(">");
	JButton previous = new JButton("<");
	JComboBox pagesCombo = new JComboBox();
    JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;
	/**
	 * Constructor called from the main menu
	 */
	public PregnancyCareBrowser() {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser"));
		myFrame = this;
		pregnancyvisits = new ArrayList<PregnancyVisit>();
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
				if (pregnancyvisits != null)
					pregnancyvisits.clear();
				dispose();
			}
		});

		 next.addActionListener( new ActionListener(){
	            public void actionPerformed(ActionEvent ae) {
	            	if(!previous.isEnabled()) previous.setEnabled(true);
	            	START_INDEX += PAGE_SIZE;
	            	model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
	    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
	            		next.setEnabled(false); 
	    			}
	    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
	    			model.fireTableDataChanged();
	    			patientTable.updateUI();
	            }
	       });
	       previous.addActionListener( new ActionListener(){
	            public void actionPerformed(ActionEvent ae) {
	            	if(!next.isEnabled()) next.setEnabled(true);
	        		START_INDEX -= PAGE_SIZE;
	        		model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
	    			if(START_INDEX < PAGE_SIZE)	previous.setEnabled(false); 
	    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
	    			model.fireTableDataChanged();
	    			patientTable.updateUI();
	            }
	        });
	       
	       pagesCombo.setEditable(true);
		   pagesCombo.addActionListener(new ActionListener() {
			 	public void actionPerformed(ActionEvent arg0) {
			 		if(pagesCombo.getItemCount() != 0){
			 			int page_number = (Integer) pagesCombo.getSelectedItem();	
				 		START_INDEX = (page_number-1) * PAGE_SIZE;
				 		model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
		    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
		            		next.setEnabled(false); 
		    			}
		    			if(page_number == 1){
		            		previous.setEnabled(false); 
		    			}else{
		    				previous.setEnabled(true); 
		    			}
		    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
		    			model.fireTableDataChanged();
			 		}
			 		patientTable.updateUI();
			 	}
			 });
	}

	/**
	 * constructor for the AdmissionBrowser to see only the pregnancyvisits for
	 * the patient
	 * 
	 * @param admittedpatient
	 *            the admitted patient
	 */
	public PregnancyCareBrowser(Patient admittedpatient) {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser"));
		myFrame = this;
		pregnancyPatientList = new ArrayList<AdmittedPatient>();
		pregnancyvisits = new ArrayList<PregnancyVisit>();
		this.patient = admittedpatient;
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
				if (pregnancyvisits != null)
					pregnancyvisits.clear();
				dispose();
			}
		});

	}

	/**
	 * intis the components
	 */
	private void initComponents() {
		getContentPane().add(getPatientPanel(), BorderLayout.NORTH);
		getContentPane().add(getVisitPanel(), BorderLayout.CENTER);
		getContentPane().add(getPregnancyButtonPanel(), BorderLayout.SOUTH);		
	}

	private JPanel getPatientPanel() {
		JPanel dataPatientListPanel = new JPanel(new BorderLayout());
		JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
		previous.setPreferredSize(new Dimension(30, 21));
        next.setPreferredSize(new Dimension(30, 21));
        pagesCombo.setPreferredSize(new Dimension(60, 21));
        under.setPreferredSize(new Dimension(60, 21));
        navigation.add(previous); 
        navigation.add(pagesCombo);
        navigation.add(under);
        navigation.add(next);
        dataPatientListPanel.add(navigation, BorderLayout.NORTH);
		dataPatientListPanel.add(getSearchPanel(), BorderLayout.WEST);
		dataPatientListPanel.add(getPatientScrollPane(), BorderLayout.CENTER);
		dataPatientListPanel.add(getPatientButtonPanel(), BorderLayout.EAST);
		return dataPatientListPanel;
	}

	private JPanel getVisitPanel() {
		JPanel visitListPanel = new JPanel(new BorderLayout());
		visitListPanel.add(getVisitScrollPane(), BorderLayout.NORTH);
		visitListPanel.add(getPregnancyDetailsPanel(), BorderLayout.EAST);
		return visitListPanel;
	}

	private JPanel getSearchPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

		/*searchPatientTextField = new JTextField();
		searchPatientTextField.setColumns(15);*/
		
		searchPatientTextField = new JTextField();
		searchPatientTextField.addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {
				
				if(searchPatientTextField.getText().length() > 7){  //Dchencher la recherche lorsqu'on a tape la 6ieme lettre
					filterPatient();
				}
			}

			public void keyPressed(KeyEvent e) {
			    int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					if(searchPatientTextField.getText().length() > 4){ 
						filterPatient();
					}
					
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});

		searchPanel.add(searchPatientTextField, BorderLayout.CENTER);
		if (Param.bool("ENHANCEDSEARCH"))
			searchPanel.add(getPatientSearchButton(), BorderLayout.EAST);
		searchPanel = setMyBorder(searchPanel,
				MessageBundle.getMessage("angal.admission.searchkey"));
		if (patient != null)
			searchPatientTextField.setEnabled(false);
		panel.add(searchPanel, BorderLayout.NORTH);
		{
			JPanel panelPregnantPrint = new JPanel();
			panel.add(panelPregnantPrint, BorderLayout.SOUTH);
			panelPregnantPrint.setLayout(new BorderLayout(0, 0));
			{
				JButton updateDelivery = new JButton(MessageBundle
						.getMessage("angal.pregnancy.updatedelivery"));
				updateDelivery.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try{
							PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(
									visitTable.getSelectedRow(), -1);
							if (visit.getType() == 0 ) {							
								//in this case, the visit ID is the admission Id
								int admission_id = visit.getVisitId();
								AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
										patientTable.getSelectedRow(), -1);
								if(adPatient!=null){									
									patient=adPatient.getPatient();
								}
								else{
									JOptionPane.showMessageDialog(null, MessageBundle
											.getMessage("angal.pregnancy.pleaseselectpatient"),
											MessageBundle
													.getMessage("angal.admission.editpatient"),
											JOptionPane.PLAIN_MESSAGE);
									return;
								}									
								new AdmissionBrowser(PregnancyCareBrowser.this, patient, admission_id,true, true);
							}
							else{
								JOptionPane.showMessageDialog(null, MessageBundle
										.getMessage("angal.pregnancy.pleaseselectdelivery"),
										MessageBundle
												.getMessage("angal.pregnancy.pleaseselectdelivery"),
										JOptionPane.PLAIN_MESSAGE);
							}
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null, MessageBundle
									.getMessage("angal.pregnancy.pleaseselectdelivery"),
									MessageBundle
											.getMessage("angal.pregnancy.pleaseselectdelivery"),
									JOptionPane.PLAIN_MESSAGE);
							System.out.println("error selecting delivery");
						}
					}
				});
				panelPregnantPrint.add(updateDelivery, BorderLayout.NORTH);
			}
			////////////////////
			{
				JButton declarationBirth = new JButton(MessageBundle
						.getMessage("angal.pregnancy.declaration_birth_but"));
				declarationBirth.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try{
							PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(
									visitTable.getSelectedRow(), -1);
							if (visit.getType() == 0 ) {
								int admission_id = visit.getVisitId();
								String user = MainMenu.getUser();
								new GenericReportFromDateToDate(admission_id,user, "declarationOfBirth", false);
								return;
							}
							else{
								JOptionPane.showMessageDialog(null, MessageBundle
										.getMessage("angal.pregnancy.pleaseselectdelivery"),
										MessageBundle
												.getMessage("angal.pregnancy.pleaseselectdelivery"),
										JOptionPane.PLAIN_MESSAGE);
							}
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null, MessageBundle
									.getMessage("angal.pregnancy.pleaseselectdelivery"),
									MessageBundle
											.getMessage("angal.pregnancy.pleaseselectdelivery"),
									JOptionPane.PLAIN_MESSAGE);
							System.out.println("error selecting delivery");
						}
					}
				});
				panelPregnantPrint.add(declarationBirth, BorderLayout.CENTER);
			}
			{
				JButton declarationCertificate = new JButton(MessageBundle
						.getMessage("angal.pregnancy.declaration_certificate_but"));
				declarationCertificate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try{
							PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(
									visitTable.getSelectedRow(), -1);
							if (visit.getType() == 0 ) {							
								//in this case, the visit ID is the admission Id
								int admission_id = visit.getVisitId();
								String user = MainMenu.getUser();
								new GenericReportFromDateToDate(admission_id,user, "certificateOfDeclaration", false);
								return;
							}
							else{
								JOptionPane.showMessageDialog(null, MessageBundle
										.getMessage("angal.pregnancy.pleaseselectdelivery"),
										MessageBundle
												.getMessage("angal.pregnancy.pleaseselectdelivery"),
										JOptionPane.PLAIN_MESSAGE);
							}
						}catch(Exception ex){
							JOptionPane.showMessageDialog(null, MessageBundle
									.getMessage("angal.pregnancy.pleaseselectdelivery"),
									MessageBundle
											.getMessage("angal.pregnancy.pleaseselectdelivery"),
									JOptionPane.PLAIN_MESSAGE);
							System.out.println("error selecting delivery");
						}
					}
				});
				panelPregnantPrint.add(declarationCertificate, BorderLayout.SOUTH);
			}
		}
		return panel;
	}

	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}

	private JButton getPatientSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					
					pregnancyPatientList = manager
							.getPregnancyPatients(searchPatientTextField
									.getText());
					filterPatient();
				}
			});

		}
		if (patient != null)
			jSearchButton.setEnabled(false);
		return jSearchButton;
	}

	private JPanel getPregnancyDetailsPanel() {

		JPanel panel = new JPanel();
		deltypeLabel = new ArrayList<JLabel>();
		deltypeResLabel = new ArrayList<JLabel>();
		for (int a = 0; a < 15; a++) {
			JLabel typeL = new JLabel("");
			JLabel typeR = new JLabel("");
			panel.add(typeL);
			panel.add(typeR);
			deltypeLabel.add(typeL);
			deltypeResLabel.add(typeR);
			typeL.setFont(new Font("Lucia Grande",
					0, 10));
			typeR.setFont(new Font("Lucia Grande",
					0, 10));

		}
		panel.setPreferredSize(new Dimension(180, 100));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		return panel;
	}

	private JPanel getPatientButtonPanel() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		int buttonsize = 0;
		getButtonNewPatient();
		getButtonEditPatient();
		getButtonDelPatient();
		if (newPatientButton.getText().length() > buttonsize)
			buttonsize = newPatientButton.getText().length();
		if (editPatientButton.getText().length() > buttonsize)
			buttonsize = editPatientButton.getText().length();
		if (deletePatientButton.getText().length() > buttonsize)
			buttonsize = deletePatientButton.getText().length();
		newPatientButton.setPreferredSize(new Dimension(180, 30));
		editPatientButton.setPreferredSize(new Dimension(180, 30));
		deletePatientButton.setPreferredSize(new Dimension(180, 30));
		newPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		editPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		deletePatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		newPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		editPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		deletePatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		buttonPanel.add(newPatientButton);
		buttonPanel.add(editPatientButton);
		
		buttonPanel.add(deletePatientButton);
		// in the case the browser is opened from the admission
		return buttonPanel;
	}

	/**
	 * @return
	 * @uml.property name="visitScrollPane"
	 */
	private JScrollPane getVisitScrollPane() {
		visitTable = new JTable(new PregnancyVisitBrowserModel());
		
		for (int i = 0; i < vColums.length; i++) {
			visitTable.getColumnModel().getColumn(i)
					.setPreferredWidth(vColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i < vColumwidth.length; i++) {
			tableWidth += vColumwidth[i];
		}
		visitScrollPane = new JScrollPane(visitTable);
		visitScrollPane.setPreferredSize(new Dimension(tableWidth + 400, 200));
		return visitScrollPane;
	}

	/**
	 * @return
	 * @uml.property name="patientScrollPane"
	 */
	private JScrollPane getPatientScrollPane() {
		TOTAL_ROWS = (new PregnancyPatientBrowserModel()).total_row; 
		model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
 		patientTable = new JTable(model);
		patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		previous.setEnabled(false);
		if(PAGE_SIZE > TOTAL_ROWS)
			next.setEnabled(false);
		patientTable.setAutoCreateRowSorter(true);
		initialiseCombo(pagesCombo, TOTAL_ROWS);
		
		for (int i = 0; i < pColums.length; i++) {
			patientTable.getColumnModel().getColumn(i)
					.setPreferredWidth(pColumwidth[i]);
		}
	
		int tableWidth = 0;
		for (int i = 0; i < pColumwidth.length; i++) {
			tableWidth += pColumwidth[i];
		}
		TableListener listener = new TableListener();
		patientTable.getSelectionModel().addListSelectionListener(listener);
		patientTable.getColumnModel().getSelectionModel()
				.addListSelectionListener(listener);
		if (patient != null) {
			int index = 0;
			for (int a = 0; a < pregnancyPatientList.size(); a++) {
				if (pregnancyPatientList.get(a).getPatient().getCode()
						.equals(patient.getCode()))
					break;
				else
					index++;
			}
			patientTable.setRowSelectionInterval(index, index);
			patientTable.setEnabled(false);
		}
		patientScrollPane = new JScrollPane(patientTable);
		patientScrollPane
				.setPreferredSize(new Dimension(tableWidth + 400, 300));
		return patientScrollPane;
	}

	private JPanel getPregnancyButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getButtonNewPregnancy());
		buttonPanel.add(getButtonNewPrenatalVisit());
		buttonPanel.add(getButtonNewPostnatalVisit());
		buttonPanel.add(getButtonDelivery());
		buttonPanel.add(getButtonVisitDetails());
		if (MainMenu.checkUserGrants("btnadmexamination")) buttonPanel.add(getJButtonExams());
		if (MainMenu.checkUserGrants("patientvaccine")) buttonPanel.add(getJButtonVaccin());
		//buttonPanel.add(getJButtonVaccin());
		buttonPanel.add(getButtonDeleteVisit());
		buttonPanel.add(getReportButton());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}

	private JButton getButtonNewPatient() {
		newPatientButton = new JButton(
				MessageBundle.getMessage("angal.admission.newpatient"));

		newPatientButton.setMnemonic(KeyEvent.VK_N);
		newPatientButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				if (Param.bool("PATIENTEXTENDED")) {
					PatientInsertExtended newrecord = new PatientInsertExtended(PregnancyCareBrowser.this,
							new Patient(), true);
					newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					newrecord.addPatientListener(PregnancyCareBrowser.this);
					newrecord.setVisible(true);
				} else {
					PatientInsert newrecord = new PatientInsert(PregnancyCareBrowser.this, new Patient(),
							true);
					newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					newrecord.addPatientListener(PregnancyCareBrowser.this);
					newrecord.setVisible(true);
				}

			}
		});
		if (patient != null)
			newPatientButton.setEnabled(false);
		return newPatientButton;
	}

	private JButton getButtonEditPatient() {
		editPatientButton = new JButton(
				MessageBundle.getMessage("angal.admission.editpatient"));

		editPatientButton.setMnemonic(KeyEvent.VK_E);
		editPatientButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle
							.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.admission.editpatient"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);

				if (Param.bool("PATIENTEXTENDED")) {

					PatientInsertExtended editrecord = new PatientInsertExtended(PregnancyCareBrowser.this,
							patient, false);
					editrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					editrecord.addPatientListener(PregnancyCareBrowser.this);
					editrecord.setVisible(true);
				} else {
					PatientInsert editrecord = new PatientInsert(PregnancyCareBrowser.this, patient, false);
					editrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					editrecord.addPatientListener(PregnancyCareBrowser.this);
					editrecord.setVisible(true);
				}
			}
		});
		if (patient != null)
			editPatientButton.setEnabled(false);
		return editPatientButton;
	}

	private JButton getButtonDelPatient() {
		deletePatientButton = new JButton(
				MessageBundle.getMessage("angal.admission.deletepatient"));

		deletePatientButton.setMnemonic(KeyEvent.VK_T);
		deletePatientButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.admission.deletepatient"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);
				int n = JOptionPane.showConfirmDialog(
						null,
						MessageBundle
								.getMessage("angal.admission.deletepatient")
								+ " " + patient.getName() + "?", MessageBundle
								.getMessage("angal.admission.deletepatient"),
						JOptionPane.YES_NO_OPTION);

				if (n == JOptionPane.YES_OPTION) {
					PatientBrowserManager manager = new PatientBrowserManager();
					boolean result = manager.deletePatient(patient);
					if (result) {
						AdmissionBrowserManager abm = new AdmissionBrowserManager();
						ArrayList<Admission> patientAdmissions = abm
								.getAdmissions(patient);
						for (Admission elem : patientAdmissions) {
							abm.setDeleted(elem.getId());
						}
						fireMyDeletedPatient(patient);
					}
				}
			}
		});
		if (patient != null)
			deletePatientButton.setEnabled(false);
		return deletePatientButton;
	}

	public void fireMyDeletedPatient(Patient p) {

		if (pregnancyPatientList == null) {
			filterPatient();
		}
		int cc = 0;
		boolean found = false;
		for (AdmittedPatient elem : pregnancyPatientList) {
			if (elem.getPatient().getCode() == p.getCode()) {
				found = true;
				break;
			}
			cc++;
		}
		if (found) {
			pregnancyPatientList.remove(cc);
			filterPatient();
		}
	}

	private JButton getButtonNewPrenatalVisit() {
		JButton buttonNewVisit = new JButton(
				MessageBundle.getMessage("angal.pregnancy.newprenatalvisit"));

		buttonNewVisit.setMnemonic(KeyEvent.VK_T);
		buttonNewVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.pregnancy.newprenatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}

				if (pregnancyvisits.size() < 1
						|| pregnancyvisits.get(0).getType() != -1) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseinsertpregnancy"),
							MessageBundle
									.getMessage("angal.pregnancy.prenatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					
					//check patient information here
					String validationType="";
					if(Param.bool("PATIENTEXTENDED"))
						validationType=ValidationPatientGroup.GLOBAL; 
					else 
						validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
					String resultCheck = PatientBrowserManager.checkPatientInformation(adPatient.getPatient(), validationType);
					if(resultCheck.length()>0){
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
						if(Param.bool("PATIENTEXTENDED")){
							PatientInsertExtended newrecord = new PatientInsertExtended(PregnancyCareBrowser.this, adPatient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							//newrecord.addPatientListener(PregnancyCareBrowser.this);
							newrecord.setVisible(true);
						}else{
							PatientInsert newrecord = new PatientInsert(PregnancyCareBrowser.this, adPatient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							//newrecord.addPatientListener(AdmittedPatientBrowser.this);
							newrecord.setVisible(true);
						}
						return;
					}
					//end checking information patient
					
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);
				PregnancyEdit b = new PregnancyEdit(myFrame, patient,
						pregnancyvisits, null, -1, false);
				b.addPregnancyListener(PregnancyCareBrowser.this);
				b.setVisible(true);


			}
		});
		return buttonNewVisit;
	}

	private JButton getButtonDeleteVisit() {
		JButton buttonDeleteVisit = new JButton(
				MessageBundle.getMessage("angal.pregnancy.deletevisit"));

		buttonDeleteVisit.setMnemonic(KeyEvent.VK_T);
		buttonDeleteVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle
							.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.pregnancy.deletevisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (visitTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle
							.getMessage("angal.pregnancy.pleaseselectvisit"),
							MessageBundle
									.getMessage("angal.pregnancy.deletevisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);
				PregnancyVisit visit = (PregnancyVisit) visitTable.getValueAt(
						visitTable.getSelectedRow(), -1);
				if (visit.getType() == 0 || visit.getType() == 10) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.impossibletodeletevisit"),
							MessageBundle
									.getMessage("angal.pregnancy.deletevisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				int visitcount = 0;
				for (int a = 0; a < pregnancyvisits.size(); a++) {
					if (pregnancyvisits.get(a).getPregnancyNr() == visit
							.getPregnancyNr())
						visitcount++;
				}
				manager.deletePregnancyVisitAndResults(visit.getVisitId());
				if (visitcount < 2)
					manager.deletePregnancy(visit.getPregnancyId());
				pregnancyvisits.remove(visit);
				filterVisit();

			}
		});
		return buttonDeleteVisit;
	}

	private JButton getReportButton() {
		JButton jButtonReport = new JButton();
		jButtonReport.setText(MessageBundle
				.getMessage("angal.pregnancy.report"));
		jButtonReport.setMnemonic(KeyEvent.VK_R);
		jButtonReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle
							.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.pregnancy.report"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (visitTable.getSelectedRow() > -1) {
					ArrayList<String> options = new ArrayList<String>();
					options.add(MessageBundle
							.getMessage("angal.pregnancy.singlepregnancy"));
					options.add(MessageBundle
							.getMessage("angal.pregnancy.allpregnancy"));
					String option = (String) JOptionPane.showInputDialog(
							PregnancyCareBrowser.this,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseselectareport"),
							MessageBundle.getMessage("angal.pregnancy.report"),
							JOptionPane.INFORMATION_MESSAGE, null, options
									.toArray(), options.get(0));
					if (option == null)
						return;
					if (options.indexOf(option) == 0) {
						pvisit = (PregnancyVisit) visitTable.getValueAt(
								visitTable.getSelectedRow(), -1);
						new PregnancyReport(patient.getCode(), pvisit
								.getPregnancyId());

					} else if (options.indexOf(option) == 1) {
						new PregnancyReport(patient.getCode(), 0);
					}
				} else {
					if (pregnancyvisits.size() < 1) {
						JOptionPane.showMessageDialog(null, MessageBundle
								.getMessage("angal.pregnancy.novisits"),
								MessageBundle
										.getMessage("angal.pregnancy.report"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}
					new PregnancyReport(patient.getCode(), 0);
				}

			}
		});
		return jButtonReport;
	}

	private JButton getButtonNewPostnatalVisit() {
		JButton buttonNewVisit = new JButton(
				MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit"));

		buttonNewVisit.setMnemonic(KeyEvent.VK_T);
		buttonNewVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.pregnancy.newpostnatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				if (pregnancyvisits.size() < 1) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseinsertpregnancy"),
							MessageBundle
									.getMessage("angal.pregnancy.postnatalvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);
				PregnancyEdit b = new PregnancyEdit(myFrame, patient,
						pregnancyvisits, null, 1, false);
				b.addPregnancyListener(PregnancyCareBrowser.this);
				b.setVisible(true);

			}
		});
		return buttonNewVisit;
	}

	private JButton getButtonNewPregnancy() {
		JButton buttonNewPregnancy = new JButton(
				MessageBundle.getMessage("angal.pregnancy.newpregnancy"));

		buttonNewPregnancy.setMnemonic(KeyEvent.VK_T);
		buttonNewPregnancy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.pregnancy.newpregnancyvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				
				
				
				if(adPatient!=null){
					//check patient information here
					String validationType="";
					if(Param.bool("PATIENTEXTENDED"))
						validationType=ValidationPatientGroup.GLOBAL; 
					else 
						validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
					String resultCheck = PatientBrowserManager.checkPatientInformation(adPatient.getPatient(), validationType);
					if(resultCheck.length()>0){
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
						if(Param.bool("PATIENTEXTENDED")){
							PatientInsertExtended newrecord = new PatientInsertExtended(PregnancyCareBrowser.this, adPatient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							//newrecord.addPatientListener(PregnancyCareBrowser.this);
							newrecord.setVisible(true);
						}else{
							PatientInsert newrecord = new PatientInsert(PregnancyCareBrowser.this, adPatient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							//newrecord.addPatientListener(AdmittedPatientBrowser.this);
							newrecord.setVisible(true);
						}
						return;
					}
					//end checking information patient
					patient=adPatient.getPatient();
					PregnancyEdit b = new PregnancyEdit(myFrame, patient,
							pregnancyvisits, null, -1, true);
					b.addPregnancyListener(PregnancyCareBrowser.this);
					b.setVisible(true);

				}
				

			}
		});
		return buttonNewPregnancy;
	}

	private JButton getButtonVisitDetails() {
		JButton buttonEditVisit = new JButton(
				MessageBundle.getMessage("angal.pregnancy.visitdetails"));

		buttonEditVisit.setMnemonic(KeyEvent.VK_T);
		buttonEditVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (visitTable.getSelectedRow() < 0
						|| patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle
							.getMessage("angal.pregnancy.pleaseselectvisit"),
							MessageBundle
									.getMessage("angal.pregnancy.editvisit"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);
				pvisit = (PregnancyVisit) visitTable.getValueAt(
						visitTable.getSelectedRow(), -1);
				if (pvisit.getType() == 0) {
// TODO Check this costructor
					AdmissionBrowser abm = null; //new AdmissionBrowser( patient, pvisit.getPregnancyId(), true);
					if(abm!=null)
						abm.setVisible(true);
					myFrame.dispose();
				} else {
					PregnancyEdit b = new PregnancyEdit(myFrame, patient,
							pregnancyvisits, pvisit, pvisit.getType(), false);
					b.addPregnancyListener(PregnancyCareBrowser.this);

					b.setVisible(true);
				}

			}
		});
		return buttonEditVisit;
	}

	private JButton getButtonDelivery() {
		JButton buttonDelivery = new JButton(
				MessageBundle.getMessage("angal.pregnancy.newdelivery"));

		buttonDelivery.setMnemonic(KeyEvent.VK_T);
		buttonDelivery.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle
							.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle
									.getMessage("angal.pregnancy.newdelivery"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(
						patientTable.getSelectedRow(), -1);
				if(adPatient!=null){
					patient=adPatient.getPatient();
				}
				else{
					patient=null;
				}
				
//				patient = (Patient) patientTable.getValueAt(
//						patientTable.getSelectedRow(), -1);

				// TODO check if the patient is currently admitted
				if (manager.isPatientCurrentlyAdmitted(patient)) {
					JOptionPane.showMessageDialog(
							null,
							MessageBundle
									.getMessage("angal.pregnancy.patientalreadyadmitted"),
							MessageBundle
									.getMessage("angal.pregnancy.newdelivery"),
							JOptionPane.PLAIN_MESSAGE);
					return;
				}
				myFrame.dispose();
				//TODO Check this costructor
				new AdmissionBrowser(myFrame, new AdmittedPatient(patient, null), false, true);

			}
		});
		if (patient != null)
			buttonDelivery.setEnabled(false);
		return buttonDelivery;
	}

	private JButton getJButtonExams() {
		if (jButtonExams == null) {
			
			jButtonExams = new JButton(MessageBundle.getMessage("angal.opd.exams"));
			
			jButtonExams.setMnemonic(KeyEvent.VK_E);
			jButtonExams.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
						return;
					}
					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
					Patient pat = adPatient.getPatient();
					
					
					LabBrowser dialog = new LabBrowser(pat);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
			});
		}
		return jButtonExams;
	}
	
	private JButton getJButtonVaccin() {
		if (jButtonVaccin == null) {
			
			jButtonVaccin = new JButton(MessageBundle.getMessage("angal.cpn.vaccin"));
			
			jButtonVaccin.setMnemonic(KeyEvent.VK_E);
			jButtonVaccin.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
						return;
					}
					AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
					Patient pat = adPatient.getPatient();
					
					
					PatVacBrowser dialog = new PatVacBrowser(pat);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
			});
		}
		return jButtonVaccin;
	}
	
	private JButton getButtonClose() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancy.close"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
				if (pregnancyvisits != null)
					pregnancyvisits.clear();
				dispose();
			}
		});
		return buttonClose;

	}

	class PregnancyVisitBrowserModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		ArrayList<PregnancyVisit> visits = new ArrayList<PregnancyVisit>();

		public PregnancyVisitBrowserModel() {
			if (patient != null) {
				if (pregnancyvisits != null) {
					pregnancyvisits = manager.getPregnancyVisits(patient
							.getCode());
				} else {
					pregnancyvisits = new ArrayList<PregnancyVisit>();
				}
				for (int a = 0; a < pregnancyvisits.size(); a++) {
					visits.add(pregnancyvisits.get(a));
				}
			}

		}

		public int getRowCount() {
			if (pregnancyvisits == null)
				return 0;
			return pregnancyvisits.size();
		}

		public String getColumnName(int c) {
			return vColums[c];
		}

		public int getColumnCount() {
			return vColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return pregnancyvisits.get(r);
			} else if (c == 0) {
				int pregnr = pregnancyvisits.get(r).getPregnancyNr();
				if (pregnr == 0)//0 is a delivery, and there is no pregnancy number defined
						return "";
				else
					return pregnr;

			} else if (c == 1) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				return sdf.format(pregnancyvisits.get(r).getTime());
			} else if (c == 2) {
				int type = pregnancyvisits.get(r).getType();
				
				switch (type) {

				case -1:
					return MessageBundle
							.getMessage("angal.pregnancy.prenatalvisit");
				case -0:					
					return MessageBundle
							.getMessage("angal.pregnancy.natalvisit");
				case 1:
					return MessageBundle
							.getMessage("angal.pregnancy.postnatalvisit");
				case 10:
					return MessageBundle
							.getMessage("angal.pregnancy.abortvisit");

				}
				return pregnancyvisits.get(r).getType();
			} else if (c == 3) {
				return pregnancyvisits.get(r).getNote();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}

	class PregnancyPatientBrowserModel extends DefaultTableModel {
		public int total_row;

		public PregnancyPatientBrowserModel() {			
			patientList.clear();
			total_row = manager.getPregnancyPatientsCount(null);
			if (Param.bool("ENHANCEDSEARCH")) {
				if (pregnancyPatientList != null) {
					pregnancyPatientList = manager
							.getPregnancyPatients(searchPatientTextField
									.getText());
				} else {
					pregnancyPatientList = new ArrayList<AdmittedPatient>();
				}
			}
			if (pregnancyPatientList != null) {
				pregnancyPatientList = manager.getPregnancyPatients(null);
			
			} else {
				pregnancyPatientList = new ArrayList<AdmittedPatient>();
			}
			//patientList = pregnancyPatientList;
			
			for (AdmittedPatient ap : pregnancyPatientList) {
				String s = "";
				if (searchPatientTextField != null)
					s = searchPatientTextField.getText() + lastKey;
				s.trim();
				String[] tokens = s.split(" ");

				if (!s.equals("")) {
					String name = ap.getPatient().getSearchString();
					int a = 0;
					for (int j = 0; j < tokens.length; j++) {
						String token = tokens[j].toLowerCase();
						if (name.contains(token)) {
							a++; 
						}
					}
					if (a == tokens.length)
						patientList.add(ap);
				} else
					patientList.add(ap);
			}
		}
		
		public PregnancyPatientBrowserModel(Object object, int sTART_INDEX, int pAGE_SIZE) {
			
			patientList.clear();
			
			if (Param.bool("ENHANCEDSEARCH")) {
				if (pregnancyPatientList != null) {
					pregnancyPatientList = manager.getPregnancyPatients(searchPatientTextField.getText(), sTART_INDEX, pAGE_SIZE);
				} else {
					pregnancyPatientList = new ArrayList<AdmittedPatient>();
				}
			}
			if (pregnancyPatientList != null) {
				pregnancyPatientList = manager.getPregnancyPatients(null, sTART_INDEX, pAGE_SIZE);
			} else {
				pregnancyPatientList = new ArrayList<AdmittedPatient>();
			}
			patientList = pregnancyPatientList;
			/*for (AdmittedPatient ap : pregnancyPatientList) {
				String s = "";
				if (searchPatientTextField != null)
					s = searchPatientTextField.getText() + lastKey;
				s.trim();
				String[] tokens = s.split(" ");

				if (!s.equals("")) {
					String name = ap.getPatient().getSearchString();
					int a = 0;
					for (int j = 0; j < tokens.length; j++) {
						String token = tokens[j].toLowerCase();
						if (name.contains(token)) {
							a++;
						}
					}
					if (a == tokens.length)
						patientList.add(ap);
				} else
					patientList.add(ap);
			}*/
		}


		public int getRowCount() {
			if (patientList == null)
				return 0;
			return patientList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return patientList.get(r);
			} else if (c == 0) {
				return (patientList.get(r)).getPatient().getCode() + "";
			}else if (c == 1) {
				return (patientList.get(r)).getPatient().getSecondName() + " "
						+ patientList.get(r).getPatient().getFirstName();
			} else if (c == 2) {
				return patientList.get(r).getPatient().getAge();

			} else if (c == 3) {
				return patientList.get(r).getPatient().getCity() + " "
						+ patientList.get(r).getPatient().getAddress();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	@Override
	public void patientUpdated(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		int row = patientTable.getSelectedRow();

		if (pregnancyPatientList == null) {
			lastKey = "";
			filterPatient();
		}
		for (int i = 0; i < pregnancyPatientList.size(); i++) {
			if ((pregnancyPatientList.get(i).getPatient().getCode()).equals(u.getCode())) {
				Admission admission = pregnancyPatientList.get(i).getAdmission();
				pregnancyPatientList.remove(i);
				pregnancyPatientList.add(i, new AdmittedPatient(u, admission));
				break;
			}
		}
		lastKey = "";
		filterPatient();
		try {
			patientTable.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}
		searchPatientTextField.requestFocus();

	}

	@Override
	public void patientInserted(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		if (pregnancyPatientList == null) {
//			pregnancyPatientList.add(0, u);
			pregnancyPatientList=new ArrayList<AdmittedPatient>();
			pregnancyPatientList.add(0, new AdmittedPatient(u, null));
		} else {
			pregnancyPatientList.add(0, new AdmittedPatient(u, null));
//			pregnancyPatientList.add(0, u);
			lastKey = "";
			filterPatient();
		}
		try {
			if (patientTable.getRowCount() > 0)
				patientTable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
		searchPatientTextField.requestFocus();

	}

	private void filterPatient2() {
		
		TOTAL_ROWS = (new PregnancyPatientBrowserModel()).total_row;
	
		model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
	
 		//patientTable.setModel(model); 
		model.fireTableDataChanged();
		previous.setEnabled(false);
		if(PAGE_SIZE > TOTAL_ROWS)
			next.setEnabled(false);
		patientTable.setAutoCreateRowSorter(true);
		initialiseCombo(pagesCombo, TOTAL_ROWS);
		patientTable.updateUI();

	}
	private void filterPatient() {
		patientTable.setModel(new PregnancyPatientBrowserModel());
	}

	/**
	 * fill the pregnancy details with abortions count and deliveries count
	 */
	private void filterPregnancyDetails() {
		//TODO the abort count and the delivery details
		for(int a=0; a< deltypeLabel.size(); a++){
			deltypeLabel.get(a).setText("");
			deltypeResLabel.get(a).setText("");
		}
		HashMap<String, Integer> deliveries= pregdelManager.getDeliveriesOfPatient(patient.getCode());
		Set<String> keys = deliveries.keySet();
		Object[] keyArray = keys.toArray();
		for (int a = 0; a < keys.size(); a++) {
				String deltype = keyArray[a].toString();
				if (deltype.equals("unknown"))
					deltype = MessageBundle
							.getMessage("angal.pregnancy.unknowndeliveryresult");
				if (deltype.length() > 23)
					deltype = deltype.substring(0, 22);
			    deltypeLabel.get(a).setText("  " + deltype + ": ");
			    deltypeResLabel.get(a).setText(deliveries.get(keyArray[a]).toString());
				
			} 
	}

	private void filterVisit() {
		visitTable.setModel(new PregnancyVisitBrowserModel());
		try {
			if (visitTable.getRowCount() > 0)
				visitTable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {

		}
	}

	class TableListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			int row = patientTable.getSelectedRow();
			if (arg0.getValueIsAdjusting() && row > -1) {				
//	old code    patient = pregnancyPatientList.get(row).getPatient();
//				filterPregnancyDetails();
//				filterVisit();
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(), -1);
				patient = adPatient.getPatient();
				filterPregnancyDetails();
				filterVisit();
			}
		}
	}

	@Override
	public void pregnancyUpdated(AWTEvent e) {
		int selectedrow = visitTable.getSelectedRow();
		filterVisit();
		try {
			if (visitTable.getRowCount() > 0)
				visitTable.setRowSelectionInterval(selectedrow, selectedrow);
		} catch (Exception e1) {
		}
	}

	@Override
	public void pregnancyInserted(AWTEvent e) {
		PregnancyVisit v = (PregnancyVisit) e.getSource();
		if (pregnancyvisits == null)
			pregnancyvisits = new ArrayList<PregnancyVisit>();
		pregnancyvisits.add(0, v);
		filterVisit();

	}

	@Override
	public void admissionUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void admissionInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void initialiseCombo(JComboBox pagesCombo, int total_rows){
		int j = 0;
		pagesCombo.removeAllItems();
		for(int i=0; i< total_rows/PAGE_SIZE; i++){
			j = i+1;
			pagesCombo.addItem(j);
		}
		if(j * PAGE_SIZE < total_rows){
			pagesCombo.addItem(j+1);
			under.setText("/" + (total_rows/PAGE_SIZE + 1 + " Pages"));
		}else{
			under.setText("/" + total_rows/PAGE_SIZE + " Pages");
		}
		
	}

}
