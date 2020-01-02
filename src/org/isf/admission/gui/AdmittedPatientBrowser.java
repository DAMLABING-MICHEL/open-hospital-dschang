package org.isf.admission.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.isf.accounting.gui.PatientBillEdit;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.examination.gui.PatientExaminationEdit;
import org.isf.examination.model.GenderPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationOperations;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.gui.LabBrowser;
import org.isf.lab.gui.LabNew;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.menu.gui.MainMenu;
import org.isf.opd.gui.OpdBrowser;
import org.isf.opd.gui.OpdEditExtended;
import org.isf.opd.model.Opd;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.therapy.gui.TherapyEdit;
import org.isf.utils.db.NormalizeString;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.jobjects.ValidationPatientGroup;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

/**
 * This class shows a list of all known patients and for each if (and where) they are actually admitted, 
 * you can:
 *  filter patients by ward and admission status
 *  search for patient with given name 
 *  add a new patient, edit or delete an existing patient record
 *  view extended data of a selected patient 
 *  add an admission record (or modify existing admission record, or set a discharge) of a selected patient
 * 
 * release 2.2 oct-23-06
 * 
 * @author flavio
 * 
 */


/*----------------------------------------------------------
 * modification history
 * ====================
 * 23/10/06 - flavio - lastKey reset
 * 10/11/06 - ross - removed from the list the deleted patients
 *                   the list is now in alphabetical  order (modified IoOperations)
 * 12/08/08 - alessandro - Patient Extended
 * 01/01/09 - Fabrizio   - The OPD button is conditioned to the extended funcionality of OPD.
 *                         Reorganized imports.
 * 13/02/09 - Alex - Search Key extended to patient code & notes
 * 29/05/09 - Alex - fixed mnemonic keys for Admission, OPD and PatientSheet
 * 14/10/09 - Alex - optimized searchkey algorithm and cosmetic changes to the code
 * 02/12/09 - Alex - search field get focus at begin and after Patient delete/update
 * 03/12/09 - Alex - added new button for merging double registered patients histories
 * 05/12/09 - Alex - fixed exception on filter after saving admission
 * 06/12/09 - Alex - fixed exception on filter after saving admission (ALL FILTERS)
 * 06/12/09 - Alex - Cosmetic changes to GUI
 -----------------------------------------------------------*/

public class AdmittedPatientBrowser extends ModalJFrame implements
		PatientInsert.PatientListener,// AdmissionBrowser.AdmissionListener,
		PatientInsertExtended.PatientListener, AdmissionBrowser.AdmissionListener, //by Alex
		PatientDataBrowser.DeleteAdmissionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] patientClassItems = { MessageBundle.getMessage("angal.admission.all"), MessageBundle.getMessage("angal.admission.admitted"), MessageBundle.getMessage("angal.admission.notadmitted") };
	private JComboBox patientClassBox = new JComboBox(patientClassItems);
	private JCheckBox wardCheck[] = null;
	private JTextField searchTextField = null;
	private JButton jSearchButton = null;
	private JButton jButtonExamination;
	private JButton jButtonExams;
	private String lastKey = "";
	private ArrayList<Ward> wardList = null;
	private ArrayList<String> wardCodeList = new ArrayList<String>();
	private JLabel rowCounter = null;
	private String rowCounterText = MessageBundle.getMessage("angal.admission.count");
	private ArrayList<AdmittedPatient> pPatient = new ArrayList<AdmittedPatient>();
	ArrayList<AdmittedPatient> patientList = new ArrayList<AdmittedPatient>();
	private String informations = MessageBundle.getMessage("angal.admission.city") + " / " + MessageBundle.getMessage("angal.admission.addressm") + " / " + MessageBundle.getMessage("angal.admission.telephone") + " / " + MessageBundle.getMessage("angal.patient.note");
	private String[] pColums = { MessageBundle.getMessage("angal.admission.code"), MessageBundle.getMessage("angal.admission.name"), MessageBundle.getMessage("angal.admission.age"), MessageBundle.getMessage("angal.admission.sex"), informations, MessageBundle.getMessage("angal.admission.ward") };
	private int[] pColumwidth = { 100, 200, 80, 50, 150, 100 };
	private boolean[] pColumResizable = {false, false, false, false, true, false};
	private AdmittedPatient patient;
	private JTable table;
	private JScrollPane scrollPane;
	private AdmittedPatientBrowser myFrame;
	private AdmissionBrowserManager manager = new AdmissionBrowserManager();
	protected boolean altKeyReleased = true;
	private DefaultTableModel model;
	OhDefaultCellRenderer cellRenderer ; //= new OhDefaultCellRenderer();
	
	JButton next = new JButton(">");
	JButton previous = new JButton("<");
	JComboBox pagesCombo = new JComboBox();
    JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;
	
	
	public void fireMyDeletedPatient(Patient p){
				
		int cc = 0;
		boolean found = false;
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == p.getCode()) {
				found = true;
				break;
			}
			cc++;
		}
		if (found){
			pPatient.remove(cc);
			lastKey = "";
			filterPatient(searchTextField.getText());
		}
	}
	
	/*
	 * manage PatientDataBrowser messages
	 */
	public void deleteAdmissionUpdated(AWTEvent e) {
		Admission adm = (Admission) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == adm.getPatId()) {
				//found same patient in the list
				Admission elemAdm = elem.getAdmission();
				if (elemAdm != null) {
					//the patient is admitted
					if (elemAdm.getId() == adm.getId())
						//same admission --> delete
						elem.setAdmission(null);	
				}
				break;
			}
		}
		lastKey = "";
		filterPatient(searchTextField.getText());
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}
		
	}

	/*
	 * manage AdmissionBrowser messages
	 */
	public void admissionInserted(AWTEvent e) {
		Admission adm = (Admission) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		int patId = adm.getPatId();
		
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == patId) {
				//found same patient in the list
				elem.setAdmission(adm);
				break;
			}
		}
		lastKey = "";
		filterPatient(searchTextField.getText());
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}
	}

	/*
	 * param contains info about patient admission,
	 * ward can varying or patient may be discharged
	 * 
	 */
	public void admissionUpdated(AWTEvent e) {
		Admission adm = (Admission) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		int admId = adm.getId();
		int patId = adm.getPatId();
		
		for (AdmittedPatient elem : pPatient) {
			if (elem.getPatient().getCode() == patId) {
				//found same patient in the list
				Admission elemAdm = elem.getAdmission();
				if (adm.getDisDate() != null) {
					//is a discharge
					if (elemAdm != null) {
						//the patient is not discharged
						if (elemAdm.getId() == admId)
							//same admission --> discharge
							elem.setAdmission(null);
					}
				} else {
					//is not a discharge --> patient admitted
					elem.setAdmission(adm);
				}
				break;
			}
		}
		lastKey = "";
		filterPatient(searchTextField.getText());
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(row, row);
			
		} catch (Exception e1) {
		}
	}

	/*
	 * manage PatientEdit messages
	 * 
	 * mind PatientEdit return a patient patientInserted create a new
	 * AdmittedPatient for table
	 */
	public void patientInserted(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		pPatient.add(0, new AdmittedPatient(u, null));
		lastKey = "";
		filterPatient(searchTextField.getText());
		try {
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
		searchTextField.requestFocus();
		//rowCounter.setText(rowCounterText + ": " + pPatient.size());
	}

	public void patientUpdated(AWTEvent e) {
		
		Patient u = (Patient) e.getSource();
		
		//remember selected row
		int row = table.getSelectedRow();
		
		for (int i = 0; i < pPatient.size(); i++) {
			if ((pPatient.get(i).getPatient().getCode()).equals(u.getCode())) {
				Admission admission = pPatient.get(i).getAdmission();
				pPatient.remove(i);
				pPatient.add(i, new AdmittedPatient(u, admission));
				break;
			}
		}
		lastKey = "";
		filterPatient(searchTextField.getText());
		try {
			table.setRowSelectionInterval(row,row);
		} catch (Exception e1) {
		}
		
		searchTextField.requestFocus();
		//rowCounter.setText(rowCounterText + ": " + pPatient.size());
	}

	public AdmittedPatientBrowser() {

		
		
		setTitle(MessageBundle.getMessage("angal.admission.patientsbrowser"));
		myFrame = this;

		if (!Param.bool("ENHANCEDSEARCH")) {
			//Load the whole list of patients
		  /*  BusyState.setBusyState(this, true);
			pPatient = manager.getAdmittedPatients(null);
		    BusyState.setBusyState(this, false);*/
		}
		
		initComponents();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		rowCounter.setText(rowCounterText + ": " + TOTAL_ROWS);
		searchTextField.requestFocus();

		myFrame.addWindowListener(new WindowAdapter(){
			
			public void windowClosing(WindowEvent e) {
				//to free memory
				if (pPatient != null) pPatient.clear();
				if (wardList != null) wardList.clear();
				dispose();
			}			
		});
	}

	private void initComponents() {			
		getContentPane().add(getDataAndControlPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

		 next.addActionListener( new ActionListener(){
	            public void actionPerformed(ActionEvent ae) {
	            	if(!previous.isEnabled()) previous.setEnabled(true);
	            	START_INDEX += PAGE_SIZE;
	            	model = new AdmittedPatientBrowserModel(START_INDEX, PAGE_SIZE, null);
	    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
	            		next.setEnabled(false); 
	    			}
	    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
	    			model.fireTableDataChanged();
	    			table.updateUI();
	            }
	        });
	       previous.addActionListener( new ActionListener(){
	            public void actionPerformed(ActionEvent ae) {
	            	if(!next.isEnabled()) next.setEnabled(true);
	        		START_INDEX -= PAGE_SIZE;
	        		model = new AdmittedPatientBrowserModel(START_INDEX, PAGE_SIZE, null);
	    			if(START_INDEX < PAGE_SIZE)	previous.setEnabled(false); 
	    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
	    			model.fireTableDataChanged();
	    			table.updateUI();
	            }
	        });
	       
	       pagesCombo.setEditable(true);
		   pagesCombo.addActionListener(new ActionListener() {
			 	public void actionPerformed(ActionEvent arg0) {
			 		if(pagesCombo.getItemCount() != 0){
			 			int page_number = (Integer) pagesCombo.getSelectedItem();	
				 		START_INDEX = (page_number-1) * PAGE_SIZE;
				 		model = new AdmittedPatientBrowserModel(START_INDEX, PAGE_SIZE, null);
		    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
		            		next.setEnabled(false); 
		    			}else{
		    				next.setEnabled(true);
		    			}
		    			if(page_number == 1){
		            		previous.setEnabled(false); 
		    			}else{
		    				previous.setEnabled(true); 
		    			}
		    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
		    			model.fireTableDataChanged();
			 		}
	    			table.updateUI();
			 	}
			 }); 
		
	}

	private JPanel getDataAndControlPanel() {
		JPanel dataAndControlPanel = new JPanel(new BorderLayout());
		dataAndControlPanel.add(getControlPanel(), BorderLayout.WEST);
		
		JPanel panelSearch = new JPanel();
		panelSearch.setBorder(new EmptyBorder(4, 0, 4, 0));
		getContentPane().add(panelSearch, BorderLayout.NORTH);
		JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
		previous.setPreferredSize(new Dimension(30, 21));
        next.setPreferredSize(new Dimension(30, 21));
        pagesCombo.setPreferredSize(new Dimension(60, 21));
        under.setPreferredSize(new Dimension(60, 21));
        
        navigation.add(previous); 
        navigation.add(pagesCombo);
        navigation.add(under);
        navigation.add(next);
        panelSearch.add(navigation);
        
		searchTextField = new JTextField();
		searchTextField.setColumns(15);
		if (Param.bool("ENHANCEDSEARCH")) {
			searchTextField.addKeyListener(new KeyListener() {
				
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
				     if (key == KeyEvent.VK_ENTER) {
				    	 jSearchButton.doClick();
				     }
				}
	
				public void keyReleased(KeyEvent e) {}
				public void keyTyped(KeyEvent e) {}
			});
		} else {
			searchTextField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
					
					if (altKeyReleased) {
						lastKey = "";
						String s = "" + e.getKeyChar();
						if (Character.isLetterOrDigit(e.getKeyChar())) {
							lastKey = s;
						}
						if(searchTextField.getText().length() > 7){
							filterPatient(searchTextField.getText());
						}	
					}
				}
	
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					if (key == KeyEvent.VK_ALT)
						altKeyReleased = false;
					
					if (key == KeyEvent.VK_ENTER){
						lastKey = "";
				    	filterPatient(searchTextField.getText());
				    	
				    	
					}
				}
				
				public void keyReleased(KeyEvent e) {
					altKeyReleased = true;
				}
			});
		}
		JLabel searchLabel = new JLabel(MessageBundle.getMessage("angal.billbrowser.patient"));
		GridBagConstraints gbc_searchLabel = new GridBagConstraints();
		gbc_searchLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_searchLabel.insets = new Insets(0, 0, 0, 5);
		gbc_searchLabel.gridx = 1;
		gbc_searchLabel.gridy = 0;
		panelSearch.add(searchLabel, gbc_searchLabel);
		
		GridBagConstraints gbc_searchTextField = new GridBagConstraints();
		gbc_searchTextField.insets = new Insets(0, 0, 0, 5);
		gbc_searchTextField.anchor = GridBagConstraints.NORTHWEST;
		gbc_searchTextField.gridx = 2;
		gbc_searchTextField.gridy = 0;
		panelSearch.add(searchTextField, gbc_searchTextField);
		searchTextField.setColumns(20);
		
		if (Param.bool("ENHANCEDSEARCH")) {
			GridBagConstraints gbc_searchButton = new GridBagConstraints();
			gbc_searchTextField.insets = new Insets(0, 0, 0, 5);
			gbc_searchTextField.anchor = GridBagConstraints.NORTHWEST;
			gbc_searchTextField.gridx = 3;
			gbc_searchTextField.gridy = 0;
			panelSearch.add(getJSearchButton(), gbc_searchButton);
		}
		
		dataAndControlPanel.add(getScrollPane(), BorderLayout.CENTER);
		return dataAndControlPanel;
	}
	
	/*
	 * panel with filtering controls
	 */
	private JPanel getControlPanel() {

		JPanel mainPanel = new JPanel(new BorderLayout());

		patientClassBox = new JComboBox(patientClassItems);
		if (!Param.bool("ENHANCEDSEARCH")) {
			patientClassBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					lastKey = "";
					filterPatient(null);
				}
			});
		}
		JPanel northPanel = new JPanel(new FlowLayout());
		northPanel.add(patientClassBox);
		northPanel = setMyBorder(northPanel, MessageBundle.getMessage("angal.admission.admissionstatus"));

		mainPanel.add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		if (wardList == null) {
			WardBrowserManager wbm = new WardBrowserManager();
			ArrayList<Ward> wardWithBeds = wbm.getWards();
			
			wardList = new ArrayList<Ward>();
			for (Ward elem : wardWithBeds) {
				
				if (elem.getBeds() > 0){
					wardList.add(elem);
					wardCodeList.add(elem.getCode());
				}
			}
		} 
		
		JPanel checkPanel[] = new JPanel[wardList.size()];
		wardCheck = new JCheckBox[wardList.size()];

		for (int i = 0; i < wardList.size(); i++) {
			checkPanel[i] = new JPanel(new BorderLayout());
			wardCheck[i] = new JCheckBox();
			wardCheck[i].setSelected(true);
			int j = i;
			if (! Param.bool("ENHANCEDSEARCH")) {
				wardCheck[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						lastKey = "";
						filterPatient(null);
					}
				});
			}
			checkPanel[i].add(wardCheck[i], BorderLayout.WEST);
			checkPanel[i].add(new JLabel(wardList.get(i).getDescription()),
					BorderLayout.CENTER);
			checkPanel[i].setPreferredSize(new Dimension(200,40));
			checkPanel[i].setMaximumSize(new Dimension(200,20));
			checkPanel[i].setMinimumSize(new Dimension(200,20));
			centerPanel.add(checkPanel[i], null);
		}

		centerPanel = setMyBorder(centerPanel, MessageBundle.getMessage("angal.admission.wards"));
		
		rowCounter = new JLabel(rowCounterText + ": ");
		centerPanel.add(rowCounter);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		return mainPanel;
	}
	

	private JScrollPane getScrollPane() {
		TOTAL_ROWS = new AdmittedPatientBrowserModel(null).total_row;
		model = new AdmittedPatientBrowserModel(START_INDEX, PAGE_SIZE, null);
	    table = new JTable(model); 
		table.setAutoCreateColumnsFromModel(false);
		previous.setEnabled(false);
		if(PAGE_SIZE > TOTAL_ROWS)
			next.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		initialiseCombo(pagesCombo, TOTAL_ROWS);
		for (int i=0;i<pColums.length; i++){
			table.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
			if (!pColumResizable[i]) table.getColumnModel().getColumn(i).setMaxWidth(pColumwidth[i]);
		}
		
		/*** apply default oh cellRender *****/
		
		List<Integer> centeredColumns=new ArrayList<Integer>();
		centeredColumns.add(0);
		centeredColumns.add(2);
		centeredColumns.add(3);
		cellRenderer=new OhDefaultCellRenderer(centeredColumns);
		
		table.setDefaultRenderer(Object.class, cellRenderer);
		table.setDefaultRenderer(Double.class, cellRenderer);
		table.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				JTable aTable =  (JTable)e.getSource();
		        int itsRow = aTable.rowAtPoint(e.getPoint());
		        if(itsRow>=0){
		        	cellRenderer.setHoveredRow(itsRow);
		        }
		        else{
		        	cellRenderer.setHoveredRow(-1);
		        }
		        aTable.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				cellRenderer.setHoveredRow(-1);
			}
		});

		int tableWidth = 0;
		for (int i = 0; i<pColumwidth.length; i++){
			tableWidth += pColumwidth[i];
		}
		
		scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(tableWidth+200, 200));
		return scrollPane;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		if (MainMenu.checkUserGrants("btnadmnew")) buttonPanel.add(getButtonNew());
		if (MainMenu.checkUserGrants("btnadmedit")) buttonPanel.add(getButtonEdit());
		if (MainMenu.checkUserGrants("btnadmdel")) buttonPanel.add(getButtonDel());
		if (MainMenu.checkUserGrants("btnadmadm")) buttonPanel.add(getButtonAdmission());
		if (MainMenu.checkUserGrants("btnadmexamination")) buttonPanel.add(getJButtonExamination());
		if (MainMenu.checkUserGrants("btnadmexamination")) buttonPanel.add(getJButtonExams());
		if (Param.bool("OPDEXTENDED") && MainMenu.checkUserGrants("btnadmopd")) buttonPanel.add(getButtonOpd());
		if (MainMenu.checkUserGrants("btnadmbill")) buttonPanel.add(getButtonBill());
		if (MainMenu.checkUserGrants("data")) buttonPanel.add(getButtonData());
		if (MainMenu.checkUserGrants("btnadmpatientfolder")) buttonPanel.add(getButtonPatientFolderBrowser());
		if (MainMenu.checkUserGrants("btnadmtherapy")) buttonPanel.add(getButtonTherapy());
		if (Param.bool("MERGEFUNCTION") && MainMenu.checkUserGrants("btnadmmer")) buttonPanel.add(getButtonMerge());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}
	
	private JButton getJButtonExamination() {
		if (jButtonExamination == null) {
			jButtonExamination = new JButton(MessageBundle.getMessage("angal.opd.examination"));
			jButtonExamination.setMnemonic(KeyEvent.VK_E);
			jButtonExamination.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
						return;
					}
					patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
					Patient pat = patient.getPatient();
					
					PatientExamination patex;
					ExaminationOperations examOperations = new ExaminationOperations();
					
					PatientExamination lastPatex = examOperations.getLastByPatID(pat.getCode());
					if (lastPatex != null) {
						patex = examOperations.getFromLastPatientExamination(lastPatex);
					} else {
						patex = examOperations.getDefaultPatientExamination(pat);
					}
					
					GenderPatientExamination gpatex = new GenderPatientExamination(patex, pat.getSex().equals("M"));
					
					PatientExaminationEdit dialog = new PatientExaminationEdit(AdmittedPatientBrowser.this, gpatex);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.pack();
					dialog.setLocationRelativeTo(null);
					dialog.setVisible(true);
				}
			});
		}
		return jButtonExamination;
	}
	
	private JButton getJButtonExams() {
		if (jButtonExams == null) {
			
			jButtonExams = new JButton(MessageBundle.getMessage("angal.opd.exams"));
			
			jButtonExams.setMnemonic(KeyEvent.VK_E);
			jButtonExams.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
						return;
					}
					patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
					Patient pat = patient.getPatient();
					//check patient information here
					String validationType="";
					if(Param.bool("PATIENTEXTENDED"))
						validationType=ValidationPatientGroup.GLOBAL; 
					else 
						validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
					String resultCheck = PatientBrowserManager.checkPatientInformation(patient.getPatient(), validationType);
					if(resultCheck.length()>0){
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
						if(Param.bool("PATIENTEXTENDED")){
							PatientInsertExtended newrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, patient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							newrecord.addPatientListener(AdmittedPatientBrowser.this);
							newrecord.setVisible(true);
						}else{
							PatientInsert newrecord = new PatientInsert(AdmittedPatientBrowser.this, patient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							newrecord.addPatientListener(AdmittedPatientBrowser.this);
							newrecord.setVisible(true);
						}
						return;
					}
					//end checking information patient
					
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

	private JButton getButtonNew() {
		JButton buttonNew = new JButton(MessageBundle.getMessage("angal.admission.newpatient"));
		buttonNew.setMnemonic(KeyEvent.VK_N);
		buttonNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				if (Param.bool("PATIENTEXTENDED")) {
					PatientInsertExtended newrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, new Patient(), true);
					newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					newrecord.addPatientListener(AdmittedPatientBrowser.this);
					newrecord.setVisible(true);

				} else {
					PatientInsert newrecord = new PatientInsert(AdmittedPatientBrowser.this, new Patient(), true);
					newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					newrecord.addPatientListener(AdmittedPatientBrowser.this);
					newrecord.setVisible(true);

				}
				
			}
		});
		return buttonNew;
	}

	private JButton getButtonEdit() {
		JButton buttonEdit = new JButton(MessageBundle.getMessage("angal.admission.editpatient"));
		buttonEdit.setMnemonic(KeyEvent.VK_E);
		buttonEdit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (Param.bool("PATIENTEXTENDED")) {
					PatientInsertExtended editrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, patient.getPatient(), false);
					editrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					editrecord.addPatientListener(AdmittedPatientBrowser.this);
					editrecord.setVisible(true);
					
				} else {
					PatientInsert editrecord = new PatientInsert(AdmittedPatientBrowser.this, patient.getPatient(), false);
					editrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
					editrecord.addPatientListener(AdmittedPatientBrowser.this);
					editrecord.setVisible(true);
				}
			}
		});
		return buttonEdit;
	}

	private JButton getButtonDel() {
		JButton buttonDel = new JButton(MessageBundle.getMessage("angal.admission.deletepatient"));
		buttonDel.setMnemonic(KeyEvent.VK_T);
		buttonDel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.deletepatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				Patient pat = patient.getPatient();
				
				int n = JOptionPane.showConfirmDialog(null,
						MessageBundle.getMessage("angal.admission.deletepatient") + " " +pat.getName() + "?",
						MessageBundle.getMessage("angal.admission.deletepatient"), JOptionPane.YES_NO_OPTION);
				
				if (n == JOptionPane.YES_OPTION){
					PatientBrowserManager manager = new PatientBrowserManager();
					BusyState.setBusyState(AdmittedPatientBrowser.this, true);
					boolean result = manager.deletePatient(pat);
					BusyState.setBusyState(AdmittedPatientBrowser.this, false);
					if (result){
						AdmissionBrowserManager abm = new AdmissionBrowserManager();
						ArrayList<Admission> patientAdmissions = abm.getAdmissions(pat);
						for (Admission elem : patientAdmissions){
							abm.setDeleted(elem.getId());
						}
						fireMyDeletedPatient(pat);
					}
				}					
			}
		});
		return buttonDel;
	}

	private JButton getButtonAdmission() {
		JButton buttonAdmission = new JButton(MessageBundle.getMessage("angal.admission.admission"));
		buttonAdmission.setMnemonic(KeyEvent.VK_A);
		buttonAdmission.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.admission"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);

				//check patient information here
				String validationType="";
				if(Param.bool("PATIENTEXTENDED"))
					validationType=ValidationPatientGroup.GLOBAL; 
				else 
					validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
				String resultCheck = PatientBrowserManager.checkPatientInformation(patient.getPatient(), validationType);
				if(resultCheck.length()>0){
					JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
					if(Param.bool("PATIENTEXTENDED")){
						PatientInsertExtended newrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, patient.getPatient(), false);
						newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
						newrecord.addPatientListener(AdmittedPatientBrowser.this);
						newrecord.setVisible(true);

					}else{
						PatientInsert newrecord = new PatientInsert(AdmittedPatientBrowser.this, patient.getPatient(), false);
						newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
						newrecord.addPatientListener(AdmittedPatientBrowser.this);
						newrecord.setVisible(true);
					}
					return;
				}
				//end checking information patient
				
				
				if (patient.getAdmission() != null) {
					// edit previous admission or dismission
					new AdmissionBrowser(myFrame, patient, true, false);
				} else {
					// new admission
					new AdmissionBrowser(myFrame, patient, false, false);
				}
			}
		});
		return buttonAdmission;
	}

	private JButton getButtonOpd() {
		JButton buttonOpd = new JButton(MessageBundle.getMessage("angal.admission.opd"));
		buttonOpd.setMnemonic(KeyEvent.VK_O);
		buttonOpd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.opd"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (patient  != null) {
					
					//check patient information here
					String validationType="";
					if(Param.bool("PATIENTEXTENDED"))
						validationType=ValidationPatientGroup.GLOBAL; 
					else 
						validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
					String resultCheck = PatientBrowserManager.checkPatientInformation(patient.getPatient(), validationType);
					if(resultCheck.length()>0){
						JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
						if(Param.bool("PATIENTEXTENDED")){
							PatientInsertExtended newrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, patient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							newrecord.addPatientListener(AdmittedPatientBrowser.this);
							newrecord.setVisible(true);
						}else{
							PatientInsert newrecord = new PatientInsert(AdmittedPatientBrowser.this, patient.getPatient(), false);
							newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
							newrecord.addPatientListener(AdmittedPatientBrowser.this);
							newrecord.setVisible(true);
						}
						return;
					}
					
					Opd opd = new Opd(0,' ',-1,"0",0);
					OpdEditExtended newrecord = new OpdEditExtended(myFrame, opd, patient.getPatient(), true);
					newrecord.setVisible(true);
					
				} /*else {
					//new OpdBrowser(true);
				}*/
			}
		});
		return buttonOpd;
	}
	
	private JButton getButtonBill() {
		JButton buttonBill = new JButton(MessageBundle.getMessage("angal.admission.bill"));
		buttonBill.setMnemonic(KeyEvent.VK_B);
		buttonBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.bill"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				if (patient  != null) {
					Patient pat = patient.getPatient();
					BillBrowserManager billManager = new BillBrowserManager();
					ArrayList<Bill> patientPendingBills = billManager.getPendingBills(pat.getCode());
					if (patientPendingBills.isEmpty()) {
						new PatientBillEdit(AdmittedPatientBrowser.this, pat);
						//dispose();
					} else {
						if (patientPendingBills.size() == 1) {
							JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.thispatienthasapendingbill"),
									MessageBundle.getMessage("angal.admission.bill"), JOptionPane.PLAIN_MESSAGE);
							PatientBillEdit pbe = new PatientBillEdit(AdmittedPatientBrowser.this, patientPendingBills.get(0), false);
							pbe.setVisible(true);
							//dispose();
						} else {
							int ok = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.admission.thereismorethanonependingbillforthispatientcontinue"),
									MessageBundle.getMessage("angal.admission.bill"), JOptionPane.WARNING_MESSAGE);
							if (ok == JOptionPane.OK_OPTION) {
								new PatientBillEdit(AdmittedPatientBrowser.this, pat);
								//dispose();
							} else return;
						}
					} 
				}
			}
		});
		return buttonBill;
	}

	private JButton getButtonData() {
		JButton buttonData = new JButton(MessageBundle.getMessage("angal.admission.data"));
		buttonData.setMnemonic(KeyEvent.VK_D);
		buttonData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.data"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				PatientDataBrowser pdb = new PatientDataBrowser(myFrame, patient.getPatient());
				pdb.addDeleteAdmissionListener(myFrame);
				pdb.showAsModal(AdmittedPatientBrowser.this);
			}
		});
		return buttonData;
	}

	private JButton getButtonPatientFolderBrowser() { //Carton clinique
		JButton buttonPatientFolderBrowser = new JButton(MessageBundle.getMessage("angal.admission.patientfolder"));
		buttonPatientFolderBrowser.setMnemonic(KeyEvent.VK_S);
		buttonPatientFolderBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {								
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.patientfolder"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				new PatientFolderBrowser(myFrame, patient.getPatient()).showAsModal(AdmittedPatientBrowser.this);
			}
		});
		return buttonPatientFolderBrowser;
	}

	private JButton getButtonTherapy() {
		JButton buttonTherapy = new JButton(MessageBundle.getMessage("angal.admission.therapy"));
		buttonTherapy.setMnemonic(KeyEvent.VK_T);
		buttonTherapy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(AdmittedPatientBrowser.this, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.admission.therapy"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				patient = (AdmittedPatient) table.getValueAt(table.getSelectedRow(), -1);
				
				//check patient information here
				String validationType="";
				if(Param.bool("PATIENTEXTENDED"))
					validationType=ValidationPatientGroup.GLOBAL; 
				else 
					validationType=ValidationPatientGroup.GLOBAL_NOT_EXTENDED;					
				String resultCheck = PatientBrowserManager.checkPatientInformation(patient.getPatient(), validationType);
				if(resultCheck.length()>0){
					JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.patient.missinginformation")+"\n"+resultCheck);
					if(Param.bool("PATIENTEXTENDED")){
						PatientInsertExtended newrecord = new PatientInsertExtended(AdmittedPatientBrowser.this, patient.getPatient(), false);
						newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
						newrecord.addPatientListener(AdmittedPatientBrowser.this);
						newrecord.setVisible(true);
					}else{
						PatientInsert newrecord = new PatientInsert(AdmittedPatientBrowser.this, patient.getPatient(), false);
						newrecord.setValidationGroup(ValidationPatientGroup.GLOBAL);
						newrecord.addPatientListener(AdmittedPatientBrowser.this);
						newrecord.setVisible(true);
					}
					return;
				}
				//end checking information patient
				
				TherapyEdit therapy = new TherapyEdit(AdmittedPatientBrowser.this, patient.getPatient(), patient.getAdmission() != null);
				therapy.setLocationRelativeTo(null);
				therapy.setVisible(true);
				
			}
		});
		return buttonTherapy;
	}

	private JButton getButtonMerge() {
		JButton buttonMerge = new JButton(MessageBundle.getMessage("angal.admission.merge"));
		buttonMerge.setMnemonic(KeyEvent.VK_M);
		buttonMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRowCount() != 2) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.pleaseselecttwopatients"),
							MessageBundle.getMessage("angal.admission.merge"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
				
				int[] indexes = table.getSelectedRows();
				
				Patient mergedPatient;
				Patient patient1 = ((AdmittedPatient)table.getValueAt(indexes[0], -1)).getPatient();
				Patient patient2 = ((AdmittedPatient)table.getValueAt(indexes[1], -1)).getPatient();
				
				//MergePatient mergedPatient = new MergePatient(patient1, patient2);
				
				if (!patient1.getSex().equals(patient2.getSex())) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.selectedpatientshavedifferentsex"),
							MessageBundle.getMessage("angal.admission.merge"), JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//Select most recent patient
				if (patient1.getCode() > patient2.getCode()) { 
					mergedPatient = patient1;
				}
				else { 
					mergedPatient = patient2;
					patient2 = patient1;
				}

				//ASK CONFIRMATION
				int ok = JOptionPane.showConfirmDialog(null, 
						MessageBundle.getMessage("angal.admission.withthisoperationthepatient")+"\n"+MessageBundle.getMessage("angal.admission.code")+": "+
						patient2.getCode() + " " + patient2.getName() + " " + patient2.getAge() + " " + patient2.getAddress() +"\n"+
						MessageBundle.getMessage("angal.admission.willbedeletedandhisherhistorytransferedtothepatient")+"\n"+MessageBundle.getMessage("angal.admission.code")+": "+
						mergedPatient.getCode() + " " + mergedPatient.getName() + " " + mergedPatient.getAge() + " " + mergedPatient.getAddress() +"\n"+
						MessageBundle.getMessage("angal.admission.continue"),
						MessageBundle.getMessage("angal.admission.merge"), 
						JOptionPane.YES_NO_OPTION);
				if (ok != JOptionPane.YES_OPTION) return;
				
				if (mergedPatient.getName().toUpperCase().compareTo(
						patient2.getName().toUpperCase()) != 0) {
					String[] names = {mergedPatient.getName(), patient2.getName()};
					String whichName = (String) JOptionPane.showInputDialog(null, 
							MessageBundle.getMessage("angal.admission.pleaseselectthefinalname"), 
							MessageBundle.getMessage("angal.admission.differentnames"), 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							names, 
							null);
					if (whichName == null) return;
					if (whichName.compareTo(names[1]) == 0) {
						//patient2 name selected
						mergedPatient.setFirstName(patient2.getFirstName());
						mergedPatient.setSecondName(patient2.getSecondName());
					}
				}
				if (mergedPatient.getBirthDate() != null &&
						mergedPatient.getAgetype().compareTo("") == 0) {
					//mergedPatient only Age
					Date bdate2 = patient2.getBirthDate();
					int age2 = patient2.getAge();
					String ageType2 = patient2.getAgetype();
					if (bdate2 != null) {
						//patient2 has BirthDate
						mergedPatient.setAge(age2);
						mergedPatient.setBirthDate(bdate2);
					}
					if (bdate2 != null && ageType2.compareTo("") != 0) {
						//patient2 has AgeType 
						mergedPatient.setAge(age2);
						mergedPatient.setAgetype(ageType2);
					}
				}
				
				if (mergedPatient.getAddress().compareTo("") == 0)
					mergedPatient.setAddress(patient2.getAddress());
				
				if (mergedPatient.getCity().compareTo("") == 0)
					mergedPatient.setCity(patient2.getCity());
				
				if (mergedPatient.getNextKin().compareTo("") == 0)
					mergedPatient.setNextKin(patient2.getNextKin());
				
				if (mergedPatient.getTelephone().compareTo("") == 0)
					mergedPatient.setTelephone(patient2.getTelephone());
				
				if (mergedPatient.getMother_name().compareTo("") == 0)
					mergedPatient.setMother_name(patient2.getMother_name());
				
				if (mergedPatient.getMother().charAt(0) == 'U')
					mergedPatient.setMother(patient2.getMother().charAt(0));
				
				if (mergedPatient.getFather_name().compareTo("") == 0)
					mergedPatient.setFather_name(patient2.getFather_name());
				
				if (mergedPatient.getFather().charAt(0) == 'U')
					mergedPatient.setFather(patient2.getFather().charAt(0));
				
				if (mergedPatient.getBloodType().compareTo("") == 0)
					mergedPatient.setBloodType(patient2.getBloodType());
				
				if (mergedPatient.getHasInsurance().charAt(0) == 'U')
					mergedPatient.setHasInsurance(patient2.getHasInsurance().charAt(0));
				
				if (mergedPatient.getParentTogether().charAt(0) == 'U')
					mergedPatient.setParentTogether(patient2.getParentTogether().charAt(0));
				
				if (mergedPatient.getNote().compareTo("") == 0)
					mergedPatient.setNote(patient2.getNote());
				else {
					String note = mergedPatient.getNote();
					mergedPatient.setNote(patient2.getNote()+"\n\n"+note);
				}

				PatientBrowserManager patManager = new PatientBrowserManager();
				if (patManager.mergePatientHistory(mergedPatient, patient2)) {
					fireMyDeletedPatient(patient2);
				}
			}
		});
		return buttonMerge;
	}

	private JButton getButtonClose() {
		JButton buttonClose = new JButton(MessageBundle.getMessage("angal.common.close"));
		buttonClose.setMnemonic(KeyEvent.VK_C);
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//to free Memory
				if (pPatient != null) pPatient.clear();
				if (wardList != null) wardList.clear();
				dispose();
			}
		});
		return buttonClose;
	}
	
	private void filterPatient(String key) {
		if(key == null){
			TOTAL_ROWS = new AdmittedPatientBrowserModel(null).total_row;
			model = new AdmittedPatientBrowserModel(START_INDEX, PAGE_SIZE, null);
			START_INDEX = 0;
			previous.setEnabled(false);
			if(PAGE_SIZE > TOTAL_ROWS)	
				next.setEnabled(false);
			table.setAutoCreateRowSorter(true);
			initialiseCombo(pagesCombo, TOTAL_ROWS);
			model.fireTableDataChanged();
			table.updateUI();
		}
		table.setModel(new AdmittedPatientBrowserModel(key));
		
		table.updateUI();
		searchTextField.requestFocus();
	}
	private void filterPatient(JCheckBox wardCheck[], String key) {
		if(key == null){
			TOTAL_ROWS = new AdmittedPatientBrowserModel(null).total_row;
			model = new AdmittedPatientBrowserModel(START_INDEX, PAGE_SIZE, null);
			START_INDEX = 0;
			previous.setEnabled(false);
			if(PAGE_SIZE > TOTAL_ROWS)	
				next.setEnabled(false);
			table.setAutoCreateRowSorter(true);
			initialiseCombo(pagesCombo, TOTAL_ROWS);
			model.fireTableDataChanged();
			table.updateUI();
		}
		table.setModel(new AdmittedPatientBrowserModel(key));
		
		table.updateUI();
		searchTextField.requestFocus();
	}
	private void searchPatient() {
		
		String key = searchTextField.getText();
		
		if (key.equals("")) {
			int ok = JOptionPane.showConfirmDialog(AdmittedPatientBrowser.this, 
					MessageBundle.getMessage("angal.admission.thiscouldretrievealargeamountofdataproceed"),
					MessageBundle.getMessage("angal.hospital"),
					JOptionPane.OK_CANCEL_OPTION);
			if (ok != JOptionPane.OK_OPTION) return;
		}
		BusyState.setBusyState(this, true);
		pPatient = manager.getAdmittedPatients(key);
		BusyState.setBusyState(this, false);
		filterPatient(null);
	}
	
	private JButton getJSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					((JButton) e.getSource()).setEnabled(false);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							searchPatient();
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									((JButton) e.getSource()).setEnabled(true);
								}
							});
						}
					});
				}
			});
		}
		return jSearchButton;
	}
	
	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title), BorderFactory
						.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}

	class AdmittedPatientBrowserModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//ArrayList<AdmittedPatient> patientList = new ArrayList<AdmittedPatient>();
		int total_row;
		
		public AdmittedPatientBrowserModel(String key) {
			pPatient = manager.getAdmittedPatients(null);
			patientList.clear();
			for (AdmittedPatient ap : pPatient) {
				Admission adm = ap.getAdmission();
				// if not admitted stripes admitted
				if (((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[2])) {
					if (adm != null)
						continue;
				}
				// if admitted stripes not admitted
				else if (((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[1])) {
					if (adm == null)
						continue;
				}

				// if all or admitted filters not matching ward
				if (!((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[2])) {
					if (adm != null) {
						int cc = -1;
						for (int j = 0; j < wardList.size(); j++) {
							if (adm.getWardId().equalsIgnoreCase(
									wardList.get(j).getCode())) {
								cc = j;
								break;
							}
						}
						if (!wardCheck[cc].isSelected())
							continue;
					}
				}

				if (key != null) {
					String s = key + lastKey;
					s.trim();
					String[] tokens = s.split(" ");

					if (!s.equals("")) {
						String name = ap.getPatient().getSearchString();
						int a = 0;
						for (int j = 0; j < tokens.length ; j++) {
							String token = tokens[j].toLowerCase();
							if (NormalizeString.normalizeContains(name, token)) {
								a++;
							}
						}
						if (a == tokens.length){
							patientList.add(ap);
						}	
					} else {
						patientList.add(ap);
					}
				} else{
					patientList.add(ap);
				}
			}
			total_row = patientList.size();
		}

		public AdmittedPatientBrowserModel(String key, boolean b) { //for search text TABOU
			
			pPatient = manager.getAdmittedPatientsSearch(key + lastKey);
			patientList = pPatient;
			total_row = patientList.size();		
			
		}
		public AdmittedPatientBrowserModel(int sTART_INDEX, int pAGE_SIZE, String key, boolean search) { //for search text
			
			//pPatient = manager.getAdmittedPatients(sTART_INDEX, pAGE_SIZE, key + lastKey);
			pPatient = manager.getAdmittedPatientsSearch(sTART_INDEX, pAGE_SIZE, key + lastKey);
			patientList = pPatient;
		}
		
		public AdmittedPatientBrowserModel(int sTART_INDEX, int pAGE_SIZE, String key) {
		
			
			
			pPatient = manager.getAdmittedPatients(sTART_INDEX, pAGE_SIZE, null);
			patientList.clear();
			for (AdmittedPatient ap : pPatient) {
				Admission adm = ap.getAdmission();
				if (((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[2])) {
					if (adm != null)
						continue;
				}
				// if admitted stripes not admitted
				else if (((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[1])) {
					if (adm == null)
						continue;
				}

				// if all or admitted filters not matching ward
				if (!((String) patientClassBox.getSelectedItem())
						.equals(patientClassItems[2])) {
					if (adm != null) {
						int cc = -1;
						for (int j = 0; j < wardList.size(); j++) {
							if (adm.getWardId().equalsIgnoreCase(
									wardList.get(j).getCode())) {
								cc = j;
								break;
							}
						}
						if (!wardCheck[cc].isSelected())
							continue;
					}
				}

				if (key != null) {
					
					String s = key + lastKey;
					s.trim();
					String[] tokens = s.split(" ");

					if (!s.equals("")) {
						String name = ap.getPatient().getSearchString();
						int a = 0;
						for (int j = 0; j < tokens.length ; j++) {
							String token = tokens[j].toLowerCase();
							if (NormalizeString.normalizeContains(name, token)) {
								a++;
							}
						}
						if (a == tokens.length) patientList.add(ap);
					} else patientList.add(ap);
				} else patientList.add(ap);
			}
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
			AdmittedPatient admPat = patientList.get(r);
			Patient patient = admPat.getPatient();
			Admission admission = admPat.getAdmission();
			if (c == -1) {
				return admPat;
			} else if (c == 0) {
				return patient.getCode();
			} else if (c == 1) {
				//return patient.getName();
				Integer parentId = 0;
				String parentFirstname = "";
				String parentlastname = "";
				try{
					parentId = patient.getAffiliatedPerson();
					Patient parent  = new PatientBrowserManager().getPatient(parentId);
					parentFirstname = parent.getFirstName();
					parentlastname = parent.getSecondName();
				}catch(Exception e){}
				
                if(parentFirstname.equals("") && parentlastname.equals(""))
                	return patient.getName();
                else
                	return patient.getName()+" ( "+parentFirstname+" "+parentlastname+" ) ";
			} else if (c == 2) {
				return TimeTools.getFormattedAge(patient.getBirthDate());
			} else if (c == 3) {
				return patient.getSex();
			} else if (c == 4) {
				return patient.getInformations();
			} else if (c == 5) {
				if (admission == null) {
					return new String("");
				} else {
					for (int i = 0; i < wardList.size(); i++) {
						if (wardList.get(i).getCode()
								.equalsIgnoreCase(admission.getWardId())) {
							return wardList.get(i).getDescription();
						}
					}
					return new String("?");
				}
			}

			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
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
