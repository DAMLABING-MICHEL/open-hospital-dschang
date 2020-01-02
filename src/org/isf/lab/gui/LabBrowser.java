package org.isf.lab.gui;

/*------------------------------------------
 * LabBrowser - list all exams
 * -----------------------------------------
 * modification history
 * 02/03/2006 - theo, Davide - first beta version
 * 08/11/2006 - ross - changed button Show into Results
 *                     fixed the exam deletion
 * 					   version is now 1.0 
 * 04/01/2009 - ross - do not use roll, use add(week,-1)!
 *                     roll does not change the year! 
 *------------------------------------------*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.BoxLayout;
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
import javax.swing.table.DefaultTableModel;

import org.isf.admission.gui.AdmittedPatientBrowser;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exatype.model.ExamType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.gui.LabEdit.LabEditListener;
import org.isf.lab.gui.LabEditExtended.LabEditExtendedListener;
import org.isf.lab.gui.LabNew.LabListener;
import org.isf.lab.manager.LabManager;
import org.isf.lab.manager.LabRowManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.opd.gui.OpdBrowser;
import org.isf.parameters.manager.Param;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoDateTextField;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.TimeTools;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class LabBrowser extends ModalJFrame implements LabListener,
		LabEditListener, LabEditExtendedListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void labInserted() {
		jTable.setModel(new LabBrowsingModel());
		//getting totals
		if(Param.bool("CREATELABORATORYAUTO")){
			LabManager Lmanager = new LabManager();
			int totalpaid = Lmanager.getLaboratoryCount(null, dateFrom
					.getDate(), dateTo.getDate(), -1, null,null, "C");
			int totalnotpaid = Lmanager.getLaboratoryCount(null, dateFrom
					.getDate(), dateTo.getDate(), -1, null,null, "O");
			int totalnotcharged = Lmanager.getLaboratoryCount(null, dateFrom
					.getDate(), dateTo.getDate(), -1, null,null, "0");
			
			totalPaidValueLabel.setText(totalpaid+"");
			totalNotPaidValueLabel.setText(totalnotpaid+"");
			totalNotFacturedValueLabel.setText(totalnotcharged+"");
		}
		
		//
		if(isModPatient){
			filterGrid();
		}

	}

	public void labUpdated() {
		filterButton.doClick();
	}

	private static final String VERSION = MessageBundle
			.getMessage("angal.versione");

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");
    private boolean isModPatient=false;
	private JPanel jContentPane = null;
	private JPanel jButtonPanel = null;
	private JButton buttonEdit = null;
	private JButton buttonNew = null;
	private JButton buttonDelete = null;
	private JButton buttonClose = null;
	private JButton printTableButton = null;
	private JButton filterButton = null;
	private JPanel jSelectionPanel = null;
	private JTable jTable = null;
	private JComboBox comboExams = null;
	private JComboBox comboWithResult = null;
	private int pfrmHeight;
	private ArrayList<Laboratory> pLabs;
	private String[] pColumswithpaid = {
			MessageBundle.getMessage("angal.common.datem"),
			MessageBundle.getMessage("angal.lab.patient"),
			MessageBundle.getMessage("angal.lab.examm"),
			MessageBundle.getMessage("angal.lab.prescriber"),
			MessageBundle.getMessage("angal.lab.resultm"),
			MessageBundle.getMessage("angal.lab.paid") };
	
	private String[] pColums =  {
			MessageBundle.getMessage("angal.common.datem"), 
			MessageBundle.getMessage("angal.lab.patient"),
			MessageBundle.getMessage("angal.lab.examm"),
			MessageBundle.getMessage("angal.lab.prescriber"),
			MessageBundle.getMessage("angal.lab.resultm") };
	//private boolean[] columnsResizable = { false, true, true, false };
	private boolean[] columnsResizable = { false, true, true, true, false };
	private boolean[] columnsResizablewithpaid = { false, true, true, true, false, false };
//	private int[] pColumwidth = { 100, 200, 200, 200 };
//	private int[] maxWidth = { 150, 200, 200, 200 };
	private int[] pColumwidth = { 100, 200, 200,150, 200 };
	private int[] maxWidth = { 150, 200, 200,150, 200 };
	
	private int[] pColumwidthwithpaid = { 100, 170, 200,150, 150, 80 };
	private int[] maxWidthwithpaid = { 150, 170, 200,150, 200, 80 };

	
	private boolean[] columnsVisible = { true,Param.bool("LABEXTENDED") , true, true, true };
	private boolean[] columnsVisiblewithpaid = { true, Param.bool("LABEXTENDED"), true, true, true,true };
	private LabManager manager;
	private LabBrowsingModel model;
	private Laboratory laboratory;
	private int selectedrow;
	private String typeSelected = null;
	private VoDateTextField dateFrom = null;
	private VoDateTextField dateTo = null;
	private final JFrame myFrame;
	
	private String lastKey;
	private JComboBox patientComboBox = null;
	private JTextField jTextPatientSrc;
	private Patient selectedPatient = null;
	private ArrayList<Patient> pat = null;
	private JPanel jPatientSearchPanel = null;
	private VoLimitedTextField patTextField = null;
	private JLabel patientLabel = null;
	private JPanel panelUser; 
	private JPanel panelPaid;  
	private JComboBox userComboBox;
	private JComboBox paidComboBox;
	
	private ArrayList<User> users = null;
	private JButton doctorPrescriptionButton;
	private JPanel panelButton;
	private JPanel panelTotal;
	private JLabel totalPaidLabel;
	private JLabel totalPaidValueLabel;
	private JLabel totalNotPaidLabel;
	private JLabel totalNotPaidValueLabel;
	private JLabel totalNotFacturedLabel;
	private JLabel totalNotFacturedValueLabel;

	/**
	 * This is the default constructor
	 */
	public LabBrowser() {
		super();
		myFrame = this;
		manager = new LabManager();
		initialize();
		setResizable(true);
		//
		setVisible(true);
		
	}

	public LabBrowser(Patient pat2) {
		
		isModPatient=true;
		myFrame = this;
		manager = new LabManager();
		
		initialize();
	
		setResizable(true);
		
		//setVisible(true);
		selectedPatient=pat2;
		patientComboBox.setSelectedItem(pat2);
		
		patientComboBox.setVisible(false);
		jTextPatientSrc.setVisible(false);
		patientLabel.setVisible(false);
		filterGrid();
		
	}
	
	/**
	 * This method initializes this Frame, sets the correct Dimensions
	 * 
	 * @return void
	 */
	private void initialize() {		
		getContentPane();
		pColums =Param.bool("CREATELABORATORYAUTO")? pColumswithpaid:pColums;
		columnsResizable = Param.bool("CREATELABORATORYAUTO")? columnsResizablewithpaid:columnsResizable;
		pColumwidth = Param.bool("CREATELABORATORYAUTO")? pColumwidthwithpaid:pColumwidth;
		maxWidth = Param.bool("CREATELABORATORYAUTO")? maxWidthwithpaid:maxWidth;
		columnsVisible = Param.bool("CREATELABORATORYAUTO")? columnsVisiblewithpaid:columnsVisible;
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 20;
		final int pfrmWidth = 17;
		final int pfrmHeight = 12;
		this.setBounds((screensize.width - screensize.width * pfrmWidth
				/ pfrmBase) / 2, (screensize.height - screensize.height
				* pfrmHeight / pfrmBase) / 2, screensize.width * pfrmWidth
				/ pfrmBase, screensize.height * pfrmHeight / pfrmBase);
		this.setContentPane(getJContentPane());
		this.setTitle(MessageBundle.getMessage("angal.lab.laboratorybrowsing")
				+ " (" + VERSION + ")");
		//modal exclude
		if(!Param.bool("WITHMODALWINDOW")){
			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		}
	}

	/**
	 * This method initializes jContentPane, adds the main parts of the frame
	 * 
	 * @return jContentPanel (JPanel)
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJSelectionPanel(), java.awt.BorderLayout.WEST);
			jContentPane.add(new JScrollPane(getJTable()),
					java.awt.BorderLayout.CENTER);
			validate();
		}
		return jContentPane;
	}
	
	
	/**
	 * This method initializes getPatientSearchPanel
	 * 
	 * @return JPanel
	 */

	private JPanel getPatientSearchPanel() {
		if (jPatientSearchPanel == null) {
			jPatientSearchPanel = new JPanel();
			jPatientSearchPanel.setLayout(new BoxLayout(jPatientSearchPanel,
					BoxLayout.Y_AXIS));
			jPatientSearchPanel.setPreferredSize(new Dimension(200, 175));
			// jPatientSearchPanel.setBounds(0, 10, 500, 70);

			patientLabel = new JLabel(
					MessageBundle.getMessage("angal.patvac.patient"));
			JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			label1Panel.add(patientLabel);
			jPatientSearchPanel.add(label1Panel);

			jTextPatientSrc = new JTextField();
			jTextPatientSrc.setPreferredSize(new Dimension(200, 30));
			JPanel textFieldPanel = new JPanel(
					new FlowLayout(FlowLayout.CENTER));
			textFieldPanel.add(jTextPatientSrc);
			jPatientSearchPanel.add(textFieldPanel);

			if (Param.bool("ENHANCEDSEARCH")) {
				jTextPatientSrc.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {
						int key = e.getKeyCode();
						if (key == KeyEvent.VK_ENTER) {
							filterPatient(jTextPatientSrc.getText());
						}
					}

					public void keyReleased(KeyEvent e) {
					}

					public void keyTyped(KeyEvent e) {
					}
				});
			} else {
				jTextPatientSrc.addKeyListener(new KeyListener() {
					public void keyTyped(KeyEvent e) {
						lastKey = "";
						String s = "" + e.getKeyChar();
						if (Character.isLetterOrDigit(e.getKeyChar())) {
							lastKey = s;
						}
						s = jTextPatientSrc.getText() + lastKey;
						s.trim();
						if(s.length() > 7){
							filterPatient(s);
						}
					}

					public void keyPressed(KeyEvent e) {
						int key = e.getKeyCode();
						if (key == KeyEvent.VK_ENTER) {
							if(jTextPatientSrc.getText().length() > 4)
							filterPatient(jTextPatientSrc.getText());
						}
					}

					public void keyReleased(KeyEvent e) {
					}
				});
			} 

			patientComboBox = new JComboBox();
			patientComboBox.setPreferredSize(new Dimension(200, 30));
			patientComboBox.addItem(MessageBundle
					.getMessage("angal.patvac.selectapatient"));

			patientComboBox = getPatientComboBox("");
			JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			 
			comboPanel.add(patientComboBox);
			jPatientSearchPanel.add(comboPanel);
			jPatientSearchPanel.add(getPanelUser());
			if(Param.bool("CREATELABORATORYAUTO")){
				jPatientSearchPanel.add(getPanelPaid());
			}
			
		}
		return jPatientSearchPanel;
	}

	/**
	 * This method initializes patientComboBox. It used to display available
	 * patients
	 * 
	 * @return patientComboBox (JComboBox)
	 */
	private JComboBox getPatientComboBox(String regExp) {

		Patient patSelected = null;
		PatientBrowserManager patBrowser = new PatientBrowserManager();

		if (Param.bool("ENHANCEDSEARCH"))
			pat = patBrowser.getPatientWithHeightAndWeight(regExp);
		else
			pat = patBrowser.getPatient();
		/*for (Patient elem : pat) {
			patientComboBox.addItem(elem);
		}
		if (patSelected != null) {
			patientComboBox.setSelectedItem(patSelected);
			selectedPatient = (Patient)patientComboBox.getSelectedItem();
		} else {
			if (patientComboBox.getItemCount() > 0
					&& Param.bool("ENHANCEDSEARCH")) {
				if (patientComboBox.getItemAt(0) instanceof Patient) {
					selectedPatient = (Patient) patientComboBox.getItemAt(0);
					setPatient(selectedPatient);
				} else
					selectedPatient = null;
			} else
				selectedPatient = null;
		}*/
		patientComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (patientComboBox.getSelectedIndex() > 0) {
					selectedPatient = (Patient) patientComboBox
							.getSelectedItem();
					setPatient(selectedPatient);														
				} else {
					selectedPatient = null;					
					resetPatVacPat();
				}
			}
		});

		return patientComboBox;
	}

	/**
	 * This method reset patient's additonal data
	 * 
	 * @return void
	 */
	private void resetPatVacPat() {
		// patTextField.setText("");
//		jTextPatientSrc.setText("");
		selectedPatient = null;
	}

	/**
	 * This method sets patient's additonal data
	 * 
	 * @return void
	 */
	private void setPatient(Patient selectedPatient) {
		// jTextPatientSrc.setText(selectedPatient.getName());
		// patTextField.setText(selectedPatient.getName());
		// ageTextField.setText(selectedPatient.getAge() + "");
		// sexTextField.setText(selectedPatient.getSex() + "");
	}

	/**
	 * This method filter patient based on search string
	 * 
	 * @return void
	 */
	private void filterPatient(String key) {
		patientComboBox.removeAllItems();

		if (key == null || key.compareTo("") == 0) {
			patientComboBox.addItem(MessageBundle.getMessage("angal.patvac.selectapatient"));
			resetPatVacPat();
		}
		for (Patient elem : pat) {
			String [] keytab = key.split(" ");
			if (key != null) {
				// Search key extended to name and code
				StringBuilder sbName = new StringBuilder();
				sbName.append(elem.getSecondName().toUpperCase());
				sbName.append(elem.getFirstName().toUpperCase());
				sbName.append(elem.getCode());
				String name = sbName.toString();
				boolean found = true;
				for(String val: keytab){  
					if (!name.toLowerCase().contains(val.toLowerCase())) {
						found = false;
					}
				}
				if(found){
					patientComboBox.addItem(elem);
				}
			} else {
				patientComboBox.addItem(elem);
			}
		}

		if (patientComboBox.getItemCount() == 1) {
			selectedPatient = (Patient) patientComboBox.getSelectedItem();
			setPatient(selectedPatient);
		}

		if (patientComboBox.getItemCount() > 0) {
			if (patientComboBox.getItemAt(0) instanceof Patient) {
				selectedPatient = (Patient) patientComboBox.getItemAt(0);
				setPatient(selectedPatient);
			} else
				selectedPatient = null;
		} else
			selectedPatient = null;
	}

	

	/**
	 * This method initializes JButtonPanel, that contains the buttons of the
	 * frame (on the bottom)
	 * 
	 * @return JButtonPanel (JPanel)
	 */
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new BorderLayout(0, 0));

			if(Param.bool("CREATELABORATORYAUTO")){
				jButtonPanel.add(getPanelTotal(), BorderLayout.NORTH);
			}
			jButtonPanel.add(getPanelButton(), BorderLayout.SOUTH);
		}
		return jButtonPanel;
	}

	private JButton getPrintTableButton() {
		if (printTableButton == null) {
			printTableButton = new JButton(
					MessageBundle.getMessage("angal.lab.printtable"));
			printTableButton.setMnemonic(KeyEvent.VK_P);
			printTableButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					LabRowManager rowManager = new LabRowManager();
					ArrayList<LaboratoryRow> rows = null;
					typeSelected = ((Exam) comboExams.getSelectedItem())
							.toString();
					if (typeSelected.equalsIgnoreCase(MessageBundle
							.getMessage("angal.lab.all")))
						typeSelected = null;
					ArrayList<LaboratoryForPrint> labs = manager
							.getLaboratoryForPrint(typeSelected,
									dateFrom.getDate(), dateTo.getDate());
					for (int i = 0; i < labs.size(); i++) {
						if(labs.get(i).getResult() == null)labs.get(i).setResult("");
						if (labs.get(i).getResult().equalsIgnoreCase(MessageBundle.getMessage("angal.lab.multipleresults"))) {
							rows = rowManager.getLabRow(labs.get(i).getCode());

							if (rows == null || rows.size() == 0) {
								labs.get(i).setResult(
												MessageBundle
														.getMessage("angal.lab.allnegative"));
							} else {
								labs.get(i).setResult(
												MessageBundle
														.getMessage("angal.lab.positive")
														+ " : "
														+ rows.get(0)
																.getDescription());
								for (int j = 1; j < rows.size(); j++) {
									labs.get(i).setResult(labs.get(i).getResult() + ","	+ rows.get(j).getDescription());
								}
							}
						}
					}
					if (!labs.isEmpty())
						new LabPrintFrame(myFrame, labs);
				}
			});
		}
		return printTableButton;
	}

	private JButton getButtonEdit() {
		if (buttonEdit == null) {
			buttonEdit = new JButton(
					MessageBundle.getMessage("angal.common.edit"));
			buttonEdit.setMnemonic(KeyEvent.VK_E);
			buttonEdit.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					selectedrow = jTable.getSelectedRow();
					if (selectedrow < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle
								.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}
					
					if(Param.bool("CREATELABORATORYAUTO")){
						if(!Param.bool("CREATELABORATORYAUTOWITHOPENEDBILL")){
							Laboratory lab = laboratory = (Laboratory) (((LabBrowsingModel) model).getValueAt(selectedrow, -1));
							if(lab.getPaidStatus()==null || !lab.getPaidStatus().equals("C")){
								JOptionPane.showMessageDialog(null, MessageBundle
										.getMessage("angal.common.notallowedtomodifies"),
										MessageBundle.getMessage("angal.hospital"),
										JOptionPane.PLAIN_MESSAGE);
								return;
							}							
						}						
					}					
					
					
					laboratory = (Laboratory) (((LabBrowsingModel) model)
							.getValueAt(selectedrow, -1));
					if (Param.bool("LABEXTENDED")) {
						LabEditExtended editrecord = new LabEditExtended(
								myFrame, laboratory, false);
						editrecord.addLabEditExtendedListener(LabBrowser.this);
						editrecord.setVisible(true);

					} else {
						LabEdit editrecord = new LabEdit(myFrame, laboratory,
								false);
						editrecord.addLabEditListener(LabBrowser.this);
						editrecord.setVisible(true);

					}
				}
			});
		}
		return buttonEdit;
	}

	/**
	 * This method initializes buttonNew, that loads LabEdit Mask
	 * 
	 * @return buttonNew (JButton)
	 */
	private JButton getButtonNew() {
		if (buttonNew == null) {
			buttonNew = new JButton(
					MessageBundle.getMessage("angal.common.new"));
			buttonNew.setMnemonic(KeyEvent.VK_N);
			buttonNew.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {

					laboratory = new Laboratory(0, new Exam("", "",
							new ExamType("", ""), 0, "", 0),
							TimeTools.getServerDateTime(), "P", 0, "", 0, "");

					if (Param.bool("LABEXTENDED")) {
						if (Param.bool("LABMULTIPLEINSERT")) {
							LabNew editrecord=null;
							if(isModPatient){
								editrecord = new LabNew(myFrame, selectedPatient);								
							}
							else{
								editrecord = new LabNew(myFrame);
							}
							editrecord.addLabListener(LabBrowser.this);
							
							if(Param.bool("WITHMODALWINDOW")){
								editrecord.showAsModal(LabBrowser.this);
							}else{
								editrecord.show(LabBrowser.this);
							}
						} else {
							LabEditExtended editrecord = new LabEditExtended(
									myFrame, laboratory, true);
							editrecord
									.addLabEditExtendedListener(LabBrowser.this);
							editrecord.setVisible(true);

						}
					} else {
						LabEdit editrecord = new LabEdit(myFrame, laboratory,
								true);
						editrecord.addLabEditListener(LabBrowser.this);
						editrecord.setVisible(true);

					}
				}
			});
		}
		return buttonNew;
	}

	/**
	 * This method initializes buttonDelete, that delets the selected records
	 * 
	 * @return buttonDelete (JButton)
	 */
	private JButton getButtonDelete() {
		if (buttonDelete == null) {
			buttonDelete = new JButton(
					MessageBundle.getMessage("angal.common.delete"));
			buttonDelete.setMnemonic(KeyEvent.VK_D);
			buttonDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle
								.getMessage("angal.common.pleaseselectarow"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						Laboratory lab = (Laboratory) (((LabBrowsingModel) model)
								.getValueAt(jTable.getSelectedRow(), -1));
						int n = JOptionPane.showConfirmDialog(
								null,
								MessageBundle
										.getMessage("angal.lab.deletefollowinglabexam")
										+ "; "
										+ "\n"
										+ MessageBundle
												.getMessage("angal.lab.registationdate")
										+ "="
										+ getConvertedString(lab.getDate())
										+ "\n "
										+ MessageBundle
												.getMessage("angal.lab.examdate")
										+ "="
										+ getConvertedString(lab.getExamDate())
										+ "\n "
										+ MessageBundle
												.getMessage("angal.lab.exam")
										+ "="
										+ lab.getExam()
										+ "\n "
										+ MessageBundle
												.getMessage("angal.lab.patient")
										+ " ="
										+ lab.getPatName()
										+ "\n "
										+ MessageBundle
												.getMessage("angal.lab.result")
										+ " =" + lab.getResult() + "\n ?",
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.YES_NO_OPTION);

						if ((n == JOptionPane.YES_OPTION)
								&& (manager.deleteLaboratory(lab))) {
							pLabs.remove(pLabs.size() - jTable.getSelectedRow()
									- 1);
							model.fireTableDataChanged();
							jTable.updateUI();
						}
					}
				}

			});
		}
		return buttonDelete;
	}

	/**
	 * This method initializes buttonClose, that disposes the entire Frame
	 * 
	 * @return buttonClose (JButton)
	 */
	private JButton getCloseButton() {
		if (buttonClose == null) {
			buttonClose = new JButton(
					MessageBundle.getMessage("angal.common.close"));
			buttonClose.setMnemonic(KeyEvent.VK_C);
			buttonClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return buttonClose;
	}

	/**
	 * This method initializes JSelectionPanel, that contains the filter objects
	 * 
	 * @return JSelectionPanel (JPanel)
	 */
	private JPanel getJSelectionPanel() {
		if (jSelectionPanel == null) {
			jSelectionPanel = new JPanel();
			jSelectionPanel.setPreferredSize(new Dimension(200, pfrmHeight));
			jSelectionPanel.add(
					new JLabel(MessageBundle
							.getMessage("angal.lab.selectanexam")), null);
			jSelectionPanel.add(getComboExams(), null);
			jSelectionPanel.add(getComboWithResult(), null);
			jSelectionPanel.add(getPatientSearchPanel(), null);
			jSelectionPanel
					.add(new JLabel(MessageBundle
							.getMessage("angal.common.datem")
							+ ": "
							+ MessageBundle.getMessage("angal.lab.from")), null);
			jSelectionPanel.add(getDateFromPanel());
			jSelectionPanel.add(
					new JLabel(MessageBundle.getMessage("angal.common.datem")
							+ ": " + MessageBundle.getMessage("angal.lab.to")
							+ "     "), null);
			jSelectionPanel.add(getDateToPanel());
			jSelectionPanel.add(getFilterButton());
		}
		return jSelectionPanel;
	}

	/**
	 * This method initializes jTable, that contains the information about the
	 * Laboratory Tests
	 * 
	 * @return jTable (JTable)
	 */
	private JTable getJTable() {
		if (jTable == null) {
			model = new LabBrowsingModel();
			//getting totals
			if(Param.bool("CREATELABORATORYAUTO")){
				LabManager Lmanager = new LabManager();
				int totalpaid = Lmanager.getLaboratoryCount(null, dateFrom
						.getDate(), dateTo.getDate(), -1, null,null, "C");
				int totalnotpaid = Lmanager.getLaboratoryCount(null, dateFrom
						.getDate(), dateTo.getDate(), -1, null,null, "O");
				int totalnotcharged = Lmanager.getLaboratoryCount(null, dateFrom
						.getDate(), dateTo.getDate(), -1, null,null, "0");
				
				totalPaidValueLabel.setText(totalpaid+"");
				totalNotPaidValueLabel.setText(totalnotpaid+"");
				totalNotFacturedValueLabel.setText(totalnotcharged+"");
			}
			
			//
			jTable = new JTable(model);
			int columnLengh = pColumwidth.length;
			if (!Param.bool("LABEXTENDED")) {
				columnLengh--;
			}
			for (int i = 0; i < columnLengh; i++) {
				jTable.getColumnModel().getColumn(i)
						.setMinWidth(pColumwidth[i]);
				if (!columnsResizable[i])
					jTable.getColumnModel().getColumn(i)
							.setMaxWidth(maxWidth[i]);
			}
		}
		return jTable;
	}

	/**
	 * This method initializes comboExams, that allows to choose which Exam the
	 * user want to display on the Table
	 * 
	 * @return comboExams (JComboBox)
	 */
	private JComboBox getComboExams() {
		ExamBrowsingManager managerExams = new ExamBrowsingManager();
		if (comboExams == null) {
			comboExams = new JComboBox();
			comboExams.setPreferredSize(new Dimension(200, 30));
			comboExams.addItem(new Exam("", MessageBundle
					.getMessage("angal.lab.all"), new ExamType("", ""), 0, "",
					0));
			ArrayList<Exam> type = managerExams.getExams(); // for
			// efficiency
			// in
			// the sequent for
			for (Exam elem : type) {
				comboExams.addItem(elem);
			}
			comboExams.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					typeSelected = ((Exam) comboExams.getSelectedItem())
							.toString();
					if (typeSelected.equalsIgnoreCase(MessageBundle
							.getMessage("angal.lab.all")))
						typeSelected = null;

				}
			});
		}
		return comboExams;
	}

	private JComboBox getComboWithResult() {
		ExamBrowsingManager managerExams = new ExamBrowsingManager();
		if (comboWithResult == null) {
			comboWithResult = new JComboBox();
			comboWithResult.setPreferredSize(new Dimension(200, 30));

			comboWithResult.addItem(MessageBundle
					.getMessage("angal.lab.withallresults"));
			comboWithResult.addItem(MessageBundle
					.getMessage("angal.lab.withresults"));
			comboWithResult.addItem(MessageBundle
					.getMessage("angal.lab.withoutresults"));

			// ArrayList<Exam> type = managerExams.getExams(); // for
			// // efficiency
			// // in
			// // the sequent for
			// for (Exam elem : type) {
			// comboExams.addItem(elem);
			// }

			// comboWithResult.addActionListener(new ActionListener() {
			//
			// public void actionPerformed(ActionEvent e) {
			//
			// if (typeSelected.equalsIgnoreCase(new String("With result")));
			// typeSelected = null;
			//
			// if (typeSelected.equalsIgnoreCase(new String("Without result")));
			// typeSelected = null;
			//
			// if
			// (typeSelected.equalsIgnoreCase(MessageBundle.getMessage("angal.lab.all")))
			// typeSelected = null;
			//
			// }
			// });
		}
		return comboWithResult;
	}

	/**
	 * This method initializes dateFrom, which is the Panel that contains the
	 * date (From) input for the filtering
	 * 
	 * @return dateFrom (JPanel)
	 */
	private VoDateTextField getDateFromPanel() {
		if (dateFrom == null) {
			GregorianCalendar now = TimeTools.getServerDateTime();
			// 04/01/2009 - ross - do not use roll, use add(week,-1)!
			// now.roll(GregorianCalendar.WEEK_OF_YEAR, false);
			now.add(GregorianCalendar.WEEK_OF_YEAR, -1);
			dateFrom = new VoDateTextField("dd/mm/yyyy", now, 10);
		}
		return dateFrom;
	}

	/**
	 * This method initializes dateTo, which is the Panel that contains the date
	 * (To) input for the filtering
	 * 
	 * @return dateTo (JPanel)
	 */
	private VoDateTextField getDateToPanel() {
		if (dateTo == null) {
			GregorianCalendar now = TimeTools.getServerDateTime();
			dateTo = new VoDateTextField("dd/mm/yyyy", now, 10);
			dateTo.setDate(now);
		}
		return dateTo;
	}

	/**
	 * This method initializes filterButton, which is the button that perform
	 * the filtering and calls the methods to refresh the Table
	 * 
	 * @return filterButton (JButton)
	 */
	private JButton getFilterButton() {
		if (filterButton == null) {
			filterButton = new JButton(
					MessageBundle.getMessage("angal.lab.search"));
			filterButton.setMnemonic(KeyEvent.VK_S);
			
			filterButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

						filterGrid();
				}
			});
		}
		return filterButton;
	}

	protected void filterGrid() {
		typeSelected = ((Exam) comboExams.getSelectedItem())
				.toString();

		int withResult = -1;

		if (typeSelected.equalsIgnoreCase(MessageBundle
				.getMessage("angal.lab.all")))
			typeSelected = null;

		if (comboWithResult
				.getSelectedItem()
				.toString()
				.equalsIgnoreCase(
						MessageBundle
								.getMessage("angal.lab.withresults"))) {
			withResult = 1;
		} else if (comboWithResult
				.getSelectedItem()
				.toString()
				.equalsIgnoreCase(
						MessageBundle
								.getMessage("angal.lab.withoutresults"))) {
			withResult = 0;
		}
		
		String patientCode = null;
		if (patientComboBox
				.getSelectedItem()
				.toString()
				.equalsIgnoreCase(
						MessageBundle
								.getMessage("angal.patvac.selectapatient")))
			patientCode = null;
		else
			patientCode = ""
					+ ((Patient) patientComboBox.getSelectedItem())
							.getCode();
		
		String userCode = null;
		if (userComboBox
				.getSelectedItem()
				.toString()
				.equalsIgnoreCase(
						MessageBundle
								.getMessage("angal.laboratory.selectaprescriber")))
			userCode = null;
		else			
			userCode = userComboBox.getSelectedItem().toString();
		
		String paidStatus = null;
		if(Param.bool("CREATELABORATORYAUTO")){
			if (paidComboBox
					.getSelectedItem()
					.toString()
					.equalsIgnoreCase(
							MessageBundle
									.getMessage("angal.laboratory.selectpaidstatus")))
				paidStatus = null;
			else{
				String inter = paidComboBox.getSelectedItem().toString();
				if(inter.equals(MessageBundle.getMessage("angal.laboratory.paid"))){
					paidStatus = "C";
				}
				if(inter.equals(MessageBundle.getMessage("angal.laboratory.notpaid"))){
					paidStatus = "O";
				}
				if(inter.equals(MessageBundle.getMessage("angal.lab.notfactured"))){
					//paidStatus = "-1";
					paidStatus = "0";
				}
				
			}
		}
		model = new LabBrowsingModel(typeSelected, dateFrom
				.getDate(), dateTo.getDate(), withResult, patientCode,userCode, paidStatus);
		//calculing totals
		if(Param.bool("CREATELABORATORYAUTO")){
			LabManager Lmanager = new LabManager();
			int totalpaid = Lmanager.getLaboratoryCount(typeSelected, dateFrom
					.getDate(), dateTo.getDate(), withResult, patientCode,userCode, "C");
			int totalnotpaid = Lmanager.getLaboratoryCount(typeSelected, dateFrom
					.getDate(), dateTo.getDate(), withResult, patientCode,userCode, "O");
			int totalnotcharged = Lmanager.getLaboratoryCount(typeSelected, dateFrom
					.getDate(), dateTo.getDate(), withResult, patientCode,userCode, "0");
			
			totalPaidValueLabel.setText(totalpaid+"");
			totalNotPaidValueLabel.setText(totalnotpaid+"");
			totalNotFacturedValueLabel.setText(totalnotcharged+"");
		}
		//
		model.fireTableDataChanged();
		jTable.updateUI();
		
	}

	/**
	 * This class defines the model for the Table
	 * 
	 * @author theo
	 * 
	 */
	class LabBrowsingModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private LabManager manager = new LabManager();

		public LabBrowsingModel(String exam, GregorianCalendar dateFrom,
				GregorianCalendar dateTo, int resultFilter, String patientCode) {
			pLabs = manager.getLaboratory(exam, dateFrom, dateTo, resultFilter, patientCode);
		}
		
//		public LabBrowsingModel(String exam, GregorianCalendar dateFrom,
//				GregorianCalendar dateTo, int resultFilter, String patientCode , String userCode) {
//			pLabs = manager.getLaboratory(exam, dateFrom, dateTo, resultFilter, patientCode,  userCode);
//		}
		public LabBrowsingModel(String exam, GregorianCalendar dateFrom,
				GregorianCalendar dateTo, int resultFilter, String patientCode , String userCode , String paidCode) {
			pLabs = manager.getLaboratory(exam, dateFrom, dateTo, resultFilter, patientCode,  userCode, paidCode);
		}
		
		public LabBrowsingModel(String exam, GregorianCalendar dateFrom,
				GregorianCalendar dateTo) {
			pLabs = manager.getLaboratory(exam, dateFrom, dateTo);
		}

		public LabBrowsingModel() {
			LabManager manager = new LabManager();
			pLabs = manager.getLaboratory();
		}

		public int getRowCount() {
			if (pLabs == null)
				return 0;
			return pLabs.size();
		}

		public String getColumnName(int c) {
			return pColums[getNumber(c)];
		}

		public int getColumnCount() {
			int c = 0;
			for (int i = 0; i < columnsVisible.length; i++) {
				if (columnsVisible[i]) {
					c++;
				}
			}
			return c;
		}

		/**
		 * This method converts a column number in the table to the right number
		 * of the datas.
		 */
		protected int getNumber(int col) {
			// right number to return
			int n = col;
			int i = 0;
			do {
				if (!columnsVisible[i]) {
					n++;
				}
				i++;
			} while (i < n);
			// If we are on an invisible column,
			// we have to go one step further
			while (!columnsVisible[n]) {
				n++;
			}
			return n;
		}

		/**
		 * Note: We must get the objects in a reversed way because of the query
		 * 
		 * @see org.isf.lab.service.IoOperations
		 */
		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return pLabs.get(pLabs.size() - r - 1);
			} else if (getNumber(c) == 0) {
				return // getConvertedString(pLabs.get(pLabs.size() - r -
						// 1).getDate());
				dateFormat.format(pLabs.get(pLabs.size() - r - 1).getExamDate()
						.getTime());
			} else if (getNumber(c) == 1) {
				return pLabs.get(pLabs.size() - r - 1).getPatName(); // Alex:
																		// added
			} else if (getNumber(c) == 2) {
				return pLabs.get(pLabs.size() - r - 1).getExam();
			} 
			else if (getNumber(c) == 3) {
				return pLabs.get(pLabs.size() - r - 1).getPrescriber();
			}
			else if (getNumber(c) == 4) {
				Laboratory lab=pLabs.get(pLabs.size() - r - 1);
				String resultValue=lab.getResultValue();
				if(null!=resultValue && !"".equalsIgnoreCase(resultValue)){
					resultValue=" ("+resultValue+")";
				}
				else{
					resultValue="";
				}
				return (lab.getResult()!=null?lab.getResult():"")+resultValue;
			}
			else if (getNumber(c) == 5) {
				Laboratory lab=pLabs.get(pLabs.size() - r - 1);
				if(lab.getPaidStatus()!=null)
					return lab.getPaidStatus().equals("C")?MessageBundle.getMessage("angal.lab.alreadypaid"):MessageBundle.getMessage("angal.lab.notalreadypaid");
				else{
					return MessageBundle.getMessage("angal.lab.notfactured");
				}
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	/**
	 * This method updates the Table because a laboratory test has been updated
	 * Sets the focus on the same record as before
	 * 
	 */
	/*
	 * public void laboratoryUpdated() { pLabs.set(pLabs.size() - selectedrow -
	 * 1, laboratory); ((LabBrowsingModel)
	 * jTable.getModel()).fireTableDataChanged(); jTable.updateUI(); if
	 * ((jTable.getRowCount() > 0) && selectedrow > -1)
	 * jTable.setRowSelectionInterval(selectedrow, selectedrow); }
	 */

	/**
	 * This method updates the Table because a laboratory test has been inserted
	 * Sets the focus on the first record
	 * 
	 */
	/*
	 * public void laboratoryInserted() { pLabs.add(pLabs.size(), laboratory);
	 * ((LabBrowsingModel) jTable.getModel()).fireTableDataChanged(); if
	 * (jTable.getRowCount() > 0) jTable.setRowSelectionInterval(0, 0); }
	 */
	/**
	 * This method is needed to display the date in a more understandable format
	 * 
	 * @param time
	 * @return String
	 */
	private String getConvertedString(GregorianCalendar time) {
		String string = "";
		if (time != null) {
			string = String.valueOf(time.get(GregorianCalendar.DAY_OF_MONTH));
			string += "/"
					+ String.valueOf(time.get(GregorianCalendar.MONTH) + 1);
			string += "/" + String.valueOf(time.get(GregorianCalendar.YEAR));
			string += "  "
					+ String.valueOf(time.get(GregorianCalendar.HOUR_OF_DAY));
			string += ":" + String.valueOf(time.get(GregorianCalendar.MINUTE));
			string += ":" + String.valueOf(time.get(GregorianCalendar.SECOND));
		}
		return string;
	}
	private JPanel getPanelUser() {
		if (panelUser == null) {
			panelUser = new JPanel();
			panelUser.add(getUserComboBox());
		}
		return panelUser;
	}
	
	private JPanel getPanelPaid() {
		if (panelPaid == null) {
			panelPaid = new JPanel();
			panelPaid.add(getPaidComboBox());
		}
		return panelPaid;
	}
	
	private JComboBox getUserComboBox() {
//		if (userComboBox == null) {
//			userComboBox = new JComboBox();
//			userComboBox.setPreferredSize(new Dimension(200, 30));
//			userComboBox.addItem(MessageBundle
//					.getMessage("angal.laboratory.selectaprescriber"));
//			UserBrowsingManager userBrowser = new UserBrowsingManager();
//            users = userBrowser.getUser();					
//			for (User elem : users) {				
//				userComboBox.addItem(elem);
//			}
//		}
		if (userComboBox == null) {
			userComboBox = new JComboBox();
			userComboBox.setPreferredSize(new Dimension(200, 30));
			LabManager manager = new LabManager();
			ArrayList<String> prescribers = manager.getPrescriber();
			userComboBox.addItem(MessageBundle.getMessage("angal.laboratory.selectaprescriber"));
			for (String elem : prescribers) {				
				userComboBox.addItem(elem);
			}							
		}
		return userComboBox;
		//return userComboBox;
	}
	private JComboBox getPaidComboBox() {
		if (paidComboBox == null) {
			paidComboBox = new JComboBox();
			paidComboBox.setPreferredSize(new Dimension(200, 30));
			paidComboBox.addItem(MessageBundle.getMessage("angal.laboratory.selectpaidstatus"));
			paidComboBox.addItem(MessageBundle.getMessage("angal.laboratory.paid"));	
			paidComboBox.addItem(MessageBundle.getMessage("angal.laboratory.notpaid"));
			paidComboBox.addItem(MessageBundle.getMessage("angal.lab.notfactured"));
		}
		return paidComboBox;		
	}
	private JButton getDoctorPrescriptionButton() {
		if (doctorPrescriptionButton == null) {
			doctorPrescriptionButton = new JButton(MessageBundle
					.getMessage("angal.laboratory.printexamlist"));
			doctorPrescriptionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
						int withResult = -1;
						if (comboWithResult
								.getSelectedItem()
								.toString()
								.equalsIgnoreCase(
										MessageBundle
												.getMessage("angal.lab.withresults"))) {
							withResult = 1;
						} else if (comboWithResult
								.getSelectedItem()
								.toString()
								.equalsIgnoreCase(
										MessageBundle
												.getMessage("angal.lab.withoutresults"))) {
							withResult = 0;
						}
						///
						String patientCode = "all";
						String patientname = "";
						if (patientComboBox
								.getSelectedItem()
								.toString()
								.equalsIgnoreCase(
										MessageBundle
												.getMessage("angal.patvac.selectapatient")))
							patientCode = "all";
						else{
							patientCode = ""+ ((Patient) patientComboBox.getSelectedItem()).getCode();
							patientname = ""+ ((Patient) patientComboBox.getSelectedItem()).getName();
							//patientname = patientname + ((Patient) patientComboBox.getSelectedItem()).getSecondName();
						}
						/////
						String userCode = "all";
						String name = "";
						if (userComboBox
								.getSelectedItem()
								.toString()
								.equalsIgnoreCase(
										MessageBundle
												.getMessage("angal.laboratory.selectaprescriber")))
							userCode = "all";
						else{
							userCode = userComboBox.getSelectedItem().toString();
							name = userComboBox.getSelectedItem().toString();							
						}
						////
						String codeexam = "all";
						//String name = "";
						if (comboExams
								.getSelectedItem()
								.toString()
								.equalsIgnoreCase(
										MessageBundle
												.getMessage("angal.lab.all")))
							codeexam = "all";
						else{
							codeexam = ((Exam) comboExams.getSelectedItem()).toString();	
							
						}
						////
						String paidStatus = "all";
						if(Param.bool("CREATELABORATORYAUTO")){
							if (paidComboBox
									.getSelectedItem()
									.toString()
									.equalsIgnoreCase(
											MessageBundle
													.getMessage("angal.laboratory.selectpaidstatus")))
								paidStatus = "all";
							else{
								String inter = paidComboBox.getSelectedItem().toString();
								if(inter.equals(MessageBundle.getMessage("angal.laboratory.paid"))){
									paidStatus = "C";
								}
								if(inter.equals(MessageBundle.getMessage("angal.laboratory.notpaid"))){
									paidStatus = "O";
								}
								if(inter.equals(MessageBundle.getMessage("angal.lab.notfactured"))){
									//paidStatus = "-1";
									paidStatus = "0";
								}
								
							}
						}
						new GenericReportFromDateToDate(codeexam, dateFrom.getText(),
								dateTo.getText(), withResult, patientCode , userCode, "Prescriber_list_exam", name,patientname, paidStatus);
						return;
					//}						
				}
			});
		}
		return doctorPrescriptionButton;
	}
	private JPanel getPanelButton() {
		if (panelButton == null) {
			panelButton = new JPanel();
			if (MainMenu.checkUserGrants("btnlaboratorynew"))
				panelButton.add(getButtonNew(), null);
			if(!isModPatient){
				if (MainMenu.checkUserGrants("btnlaboratoryedit"))
					panelButton.add(getButtonEdit(), null);
			}			
			if (MainMenu.checkUserGrants("btnlaboratorydel"))
				panelButton.add(getButtonDelete(), null);
			panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panelButton.add((getCloseButton()));
			panelButton.add((getPrintTableButton()));
			panelButton.add(getDoctorPrescriptionButton());
		}
		return panelButton;
	}
	private JPanel getPanelTotal() {
		if (panelTotal == null) {
			panelTotal = new JPanel();
			panelTotal.setBorder(new LineBorder(new Color(0, 0, 0)));
			panelTotal.add(getTotalPaidLabel());
			panelTotal.add(getTotalPaidValueLabel());
			panelTotal.add(getTotalNotPaidLabel());
			panelTotal.add(getTotalNotPaidValueLabel());
			panelTotal.add(getTotalNotFacturedLabel());
			panelTotal.add(getTotalNotFacturedValueLabel());
		}
		return panelTotal;
	}
	private JLabel getTotalPaidLabel() {
		if (totalPaidLabel == null) {
			totalPaidLabel = new JLabel(MessageBundle.getMessage("angal.lobaratory.totalPaidLabel")+": "); 
			totalPaidLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			totalPaidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);		
		}
		return totalPaidLabel;
	}
	private JLabel getTotalPaidValueLabel() {
		if (totalPaidValueLabel == null) {
			totalPaidValueLabel = new JLabel("");
			totalPaidValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
			totalPaidValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return totalPaidValueLabel;
	}
	private JLabel getTotalNotPaidLabel() {
		if (totalNotPaidLabel == null) {
			totalNotPaidLabel = new JLabel("  "+MessageBundle.getMessage("angal.lobaratory.totalNotPaidLabel")+": ");
			totalNotPaidLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			totalNotPaidLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return totalNotPaidLabel;
	}
	private JLabel getTotalNotPaidValueLabel() {
		if (totalNotPaidValueLabel == null) {
			totalNotPaidValueLabel = new JLabel("");
			totalNotPaidValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
			totalNotPaidValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return totalNotPaidValueLabel;
	}
	private JLabel getTotalNotFacturedLabel() {
		if (totalNotFacturedLabel == null) {
			totalNotFacturedLabel = new JLabel("  "+MessageBundle.getMessage("angal.lobaratory.totalNotFacturedLabel")+": ");
			totalNotFacturedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			totalNotFacturedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return totalNotFacturedLabel;
	}
	private JLabel getTotalNotFacturedValueLabel() {
		if (totalNotFacturedValueLabel == null) {
			totalNotFacturedValueLabel = new JLabel("");
			totalNotFacturedValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
			totalNotFacturedValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		return totalNotFacturedValueLabel;
	}
}
