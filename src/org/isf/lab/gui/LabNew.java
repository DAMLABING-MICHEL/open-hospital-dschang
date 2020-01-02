package org.isf.lab.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.isf.accounting.gui.BillItemPicker;
import org.isf.admission.gui.AdmittedPatientBrowser;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.manager.ExamRowBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exa.model.ExamRow;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MaterialsExamList;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.manager.LabManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryRow;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.OhTableModel;
import org.isf.utils.jobjects.OhTableModelExam;
import org.isf.utils.jobjects.ValidationPatientGroup;
import org.isf.utils.time.RememberDates;
import org.isf.utils.time.TimeTools;

import com.toedter.calendar.JDateChooser;

//  public class LabNew extends JDialog
public class LabNew extends ModalJFrame  implements SelectionListener, PatientInsertExtended.PatientListener {

	// LISTENER INTERFACE
	// --------------------------------------------------------
	private EventListenerList labListener = new EventListenerList();

	public interface LabListener extends EventListener {
		public void labInserted();
	}

	public void addLabListener(LabListener l) {
		labListener.add(LabListener.class, l);

	}

	private void fireLabInserted() {
		new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = labListener.getListeners(LabListener.class);
		for (int i = 0; i < listeners.length; i++)
			((LabListener) listeners[i]).labInserted();
	}

	// ---------------------------------------------------------------------------

	public void patientSelected(Patient patient) {
		patientSelected = patient;
		// INTERFACE
		jTextFieldPatient.setText(patientSelected.getName());
		jTextFieldPatient.setEditable(false);
		jButtonPickPatient.setText(MessageBundle
				.getMessage("angal.labnew.changepatient")); //$NON-NLS-1$
		jButtonPickPatient
				.setToolTipText(MessageBundle
						.getMessage("angal.labnew.tooltip.changethepatientassociatedwiththisexams")); //$NON-NLS-1$
		jButtonTrashPatient.setEnabled(true);
		
		inOut = getIsAdmitted();
		
//		if (inOut.equalsIgnoreCase("R"))
//			jRadioButtonOPD.setSelected(true);
//		else
//			jRadioButtonIPD.setSelected(true);
	}

	private static final long serialVersionUID = 1L;
	private JTable jTableExams;
	private JScrollPane jScrollPaneTable;
	private JPanel jPanelNorth;
	private JButton jButtonRemoveItem;
	private JButton jButtonAddExam;
	private JPanel jPanelExamButtons;
	private JPanel jPanelEast;
	private JPanel jPanelSouth;
	private JPanel jPanelDate;
	private JPanel jPanelPatient;
	private JLabel jLabelPatient;
	private JTextField jTextFieldPatient;
	private JButton jButtonPickPatient;
	private JButton jButtonTrashPatient;
	private JLabel jLabelDate;
	private JDateChooser jCalendarDate;
	private JPanel jPanelMaterial;
	private JComboBox jComboBoxMaterial;
	private JComboBox jComboBoxExamResults;
	private JTextField txtResultValue;
	private JPanel jPanelResults;
	private JPanel jPanelNote;
	private JPanel jPanelButtons;
	private JButton jButtonOK;
	private JButton jButtonCancel;
	private JTextArea jTextAreaNote;
	private JScrollPane jScrollPaneNote;
	private JRadioButton jRadioButtonOPD;
	private JRadioButton jRadioButtonIPD;
	private ButtonGroup radioGroup;
	private JPanel jOpdIpdPanel;
	private String inOut;

	private static final Dimension PatientDimension = new Dimension(200, 20);
	private static final Dimension LabelDimension = new Dimension(50, 20);
	// private static final Dimension ResultDimensions = new Dimension(200,200);
	// private static final Dimension MaterialDimensions = new
	// Dimension(150,20);
	// private static final Dimension TextAreaNoteDimension = new Dimension(500,
	// 50);
	private static final int EastWidth = 200;
	private static final int ComponentHeight = 20;
	private static final int ResultHeight = 200;
	// private static final int ButtonHeight = 25;

	private Object[] examClasses = { Exam.class, String.class, String.class };
	private String[] examColumnNames = {
			MessageBundle.getMessage("angal.labnew.exam"), MessageBundle.getMessage("angal.labnew.result"), MessageBundle.getMessage("angal.labnew.resultvalue") }; //$NON-NLS-1$ //$NON-NLS-2$
	private int[] examColumnWidth = { 200, 150 };
	private boolean[] examResizable = { true, false };
	private String[] matList = { "",MessageBundle.getMessage("angal.lab.blood"),
			MessageBundle.getMessage("angal.lab.urine"),
			MessageBundle.getMessage("angal.lab.stool"),
			MessageBundle.getMessage("angal.lab.sputum"),
			MessageBundle.getMessage("angal.lab.cfs"),
			MessageBundle.getMessage("angal.lab.swabs"),
			MessageBundle.getMessage("angal.lab.tissues") };

	// TODO private boolean modified;
	private Patient patientSelected = null;
	private Laboratory selectedLab = null;


	// Exams (ALL)
	ExamBrowsingManager exaManager = new ExamBrowsingManager();
	//ArrayList<Exam> exaArray = exaManager.getExams();
	ArrayList<Exam> exaArray = exaManager.getExamsOrderedByDesc();

	// Results (ALL)
	ExamRowBrowsingManager examRowManager = new ExamRowBrowsingManager();
	ArrayList<ExamRow> exaRowArray = examRowManager.getExamRow();

	// Arrays for this Patient
	ArrayList<ArrayList<LaboratoryRow>> examResults = new ArrayList<ArrayList<LaboratoryRow>>();
	ArrayList<Laboratory> examItems = new ArrayList<Laboratory>();

	ArrayList<Laboratory> examList = null;
	
	boolean showPatientPicker = true;

	/**
	 * @wbp.parser.constructor
	 */
	public LabNew(JFrame owner) {
		
		super();
		initComponents();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(LabNew.DISPOSE_ON_CLOSE);
		setTitle(MessageBundle.getMessage("angal.labnew.title"));
		jButtonPickPatient.doClick();
	}

	public LabNew(JFrame owner, Patient patient) {
		//super(owner, true);
		super();
		patientSelected = patient;

		initComponents(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(LabNew.DISPOSE_ON_CLOSE);
		setTitle(MessageBundle.getMessage("angal.labnew.title"));

		// setVisible(true);
	}

	private void initComponents() {
		getContentPane().add(getJPanelNorth(), BorderLayout.NORTH);
		getContentPane().add(getJScrollPaneTable(), BorderLayout.CENTER);
		getContentPane().add(getJPanelEast(), BorderLayout.EAST);
		getContentPane().add(getJPanelSouth(), BorderLayout.SOUTH);
		pack();

	}

	private void initComponents(boolean updatedMethod) {
		showPatientPicker = false;
		getContentPane().add(getJPanelNorth(), BorderLayout.NORTH);
		getContentPane().add(getJScrollPaneTable(), BorderLayout.CENTER);
		getContentPane().add(getJPanelEast(), BorderLayout.EAST);
		getContentPane().add(getJPanelSouth(), BorderLayout.SOUTH);
		pack();

	}

	private JScrollPane getJScrollPaneNote() {
		if (jScrollPaneNote == null) {
			jScrollPaneNote = new JScrollPane();
			jScrollPaneNote.setViewportView(getJTextAreaNote());
		}
		return jScrollPaneNote;
	}

	private JTextArea getJTextAreaNote() {
		if (jTextAreaNote == null) {
			jTextAreaNote = new JTextArea(3, 50);
			jTextAreaNote.setText("");
		}
		return jTextAreaNote;
	}

	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText(MessageBundle
					.getMessage("angal.common.cancel"));
			jButtonCancel.setMnemonic(KeyEvent.VK_C);
			jButtonCancel
					.addActionListener(new java.awt.event.ActionListener() {

						public void actionPerformed(java.awt.event.ActionEvent e) {
							dispose();
						}
					});
		}
		return jButtonCancel;
	}

	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText(MessageBundle.getMessage("angal.common.ok"));
			jButtonOK.setMnemonic(KeyEvent.VK_O);
			jButtonOK.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					// JOptionPane.showMessageDialog(null, "yes ");

					// Check Results
					if (examItems.size() == 0) {
						JOptionPane.showMessageDialog(
								LabNew.this,
								MessageBundle
										.getMessage("angal.labnew.noexamsinserted"), //$NON-NLS-1$
								"Error", //$NON-NLS-1$
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					for (Laboratory lab : examItems) {

						// if (lab.getResult() == null) {
						// JOptionPane.showMessageDialog(LabNew.this,
						//									MessageBundle.getMessage("angal.labnew.someexamswithoutresultpleasecheck"), //$NON-NLS-1$
						//									"Error", //$NON-NLS-1$
						// JOptionPane.WARNING_MESSAGE);
						// return;
						// }
					}
					// Check Patient
					if (patientSelected == null) {
						JOptionPane.showMessageDialog(
								LabNew.this,
								MessageBundle
										.getMessage("angal.labnew.pleaseselectapatient"), //$NON-NLS-1$
								"Error", //$NON-NLS-1$
								JOptionPane.WARNING_MESSAGE);
						return;
					}			
					
					// Check Date
					if (jCalendarDate.getDate() == null) {
						JOptionPane.showMessageDialog(
								LabNew.this,
								MessageBundle
										.getMessage("angal.labnew.pleaseinsertadate"), //$NON-NLS-1$
								"Error", //$NON-NLS-1$
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					// CREATING DB OBJECT
					GregorianCalendar newDate = TimeTools.getServerDateTime();
					newDate.setTimeInMillis(jCalendarDate.getDate().getTime());
					RememberDates.setLastLabExamDate(newDate);

					String inOut = null;
					inOut = getIsAdmitted();

					Laboratory labOne = (Laboratory) jTableExams.getValueAt(
							jTableExams.getSelectedRow(), -1);
					labOne.setNote(jTextAreaNote.getText().trim()); // Workaround
																	// if Note
																	// typed
																	// just
																	// before
																	// saving
					LabManager labManager = new LabManager();
					GregorianCalendar datep = TimeTools.getServerDateTime();
					int currentProgNum = labManager.getProgMonth(datep.get(GregorianCalendar.MONTH)+1, datep.get(GregorianCalendar.YEAR));
					
					UserBrowsingManager manager = new UserBrowsingManager();
					String userId = MainMenu.getUser();
					String userName = manager.getUsrName(userId);					
					for (Laboratory lab : examItems) {
						lab.setAge(patientSelected.getAge());
						lab.setDate(newDate);
						lab.setExamDate(newDate);
						lab.setInOutPatient(inOut);
						lab.setPatId(patientSelected.getCode());
						lab.setPatName(patientSelected.getName());
						lab.setSex(patientSelected.getSex() + "");
						//set prescriber
						lab.setPrescriber(userName);											
						//
						//set month prog number
						try{
							if(!(lab.getCode()>0)){
								lab.setMProg(++currentProgNum);
							}
						}catch(Exception exp){}
						//end
					}

					boolean result = false;
					//LabManager labManager = new LabManager();
					Laboratory lab;
					for (int i = 0; i < examItems.size(); i++) {

						lab = examItems.get(i);

						if (examList != null) {
							if (examList.indexOf(lab) == -1) {
								if (lab.getExam().getProcedure() == 1) {
									result = labManager
											.newLabFirstProcedure2(lab);
								} else {
									//All exams will be prescribe by the doctor
									result = labManager.newLabSecondProcedure2(
											lab, examResults.get(i));
								}

								if (!result) {
									JOptionPane
											.showMessageDialog(
													null,
													MessageBundle
															.getMessage("angal.labnew.thedatacouldnotbesaved"));
									return;
								}
							}
						} else {
							if (lab.getExam().getProcedure() == 1) {
								result = labManager.newLabFirstProcedure2(lab);
							} else {
								result = labManager.newLabSecondProcedure2(lab,
										examResults.get(i));
							}

							if (!result) {
								JOptionPane.showMessageDialog(
										null,
										MessageBundle
												.getMessage("angal.labnew.thedatacouldnotbesaved"));
								return;
							}
						}

					}

					fireLabInserted();
					dispose();
				}
			});
		}
		return jButtonOK;
	}

	private String getIsAdmitted() {
		AdmissionBrowserManager man = new AdmissionBrowserManager();
		Admission adm = new Admission();
		adm = man.getCurrentAdmission(patientSelected);
		return (adm == null ? "R" : "I");
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.add(getJButtonOK());
			jPanelButtons.add(getJButtonCancel());
		}
		return jPanelButtons;
	}

	private JPanel getJPanelNote() {
		if (jPanelNote == null) {
			jPanelNote = new JPanel();
			jPanelNote.setLayout(new BoxLayout(jPanelNote, BoxLayout.Y_AXIS));
			jPanelNote.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Color.LIGHT_GRAY),
					MessageBundle.getMessage("angal.labnew.note")));
			jPanelNote.add(getJScrollPaneNote());
		}
		return jPanelNote;
	}

	private JPanel getJPanelResults() {
		if (jPanelResults == null) {
			jPanelResults = new JPanel();
			jPanelResults.setPreferredSize(new Dimension(EastWidth,
					ResultHeight));
			jPanelResults.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Color.LIGHT_GRAY),
					MessageBundle.getMessage("angal.labnew.result")));
		} else {

			jPanelResults.removeAll();
			int selectedRow = jTableExams.getSelectedRow();
			final Laboratory selectedLab = (Laboratory) jTableExams.getValueAt(
					selectedRow, -1);
			Exam selectedExam = selectedLab.getExam();

			if (selectedExam.getProcedure() == 1) {

				txtResultValue=new JTextField();
				jComboBoxExamResults = new JComboBox();
				jComboBoxExamResults.setMaximumSize(new Dimension(EastWidth,
						ComponentHeight));
				jComboBoxExamResults.setMinimumSize(new Dimension(EastWidth,
						ComponentHeight));
				jComboBoxExamResults.setPreferredSize(new Dimension(EastWidth,
						ComponentHeight));
				txtResultValue.setMaximumSize(new Dimension(EastWidth,
						ComponentHeight));
				txtResultValue.setMinimumSize(new Dimension(EastWidth,
						ComponentHeight));
				txtResultValue.setPreferredSize(new Dimension(EastWidth,
						ComponentHeight));

				for (ExamRow exaRow : exaRowArray) {
					if (selectedExam.getCode().compareTo(exaRow.getExamCode()) == 0) {
						jComboBoxExamResults.addItem(exaRow.getDescription());
					}
				}
				txtResultValue.setText(selectedLab.getResultValue());
				jComboBoxExamResults.setSelectedItem(selectedLab.getResult());
				txtResultValue.addFocusListener(new FocusListener() {
					
					@Override
					public void focusLost(FocusEvent e) {
						selectedLab.setResultValue(txtResultValue.getText());
						jTableExams.updateUI();
					}
					
					@Override
					public void focusGained(FocusEvent e) {
						
					}
				});
				jComboBoxExamResults.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						selectedLab.setResult(jComboBoxExamResults
								.getSelectedItem().toString());
						jTableExams.updateUI();
					}
				});
				jPanelResults.add(jComboBoxExamResults);

			} else {

				jPanelResults.removeAll();
				jPanelResults.setLayout(new GridLayout(14, 1));

				ArrayList<LaboratoryRow> checking = examResults.get(jTableExams
						.getSelectedRow());
				boolean checked;

				for (ExamRow exaRow : exaRowArray) {
					if (selectedExam.getCode().compareTo(exaRow.getExamCode()) == 0) {

						checked = false;
						String resultValue="";
						for(LaboratoryRow row:checking){
							if(row.getDescription().equalsIgnoreCase(exaRow.getDescription())){
								checked = true;
								resultValue=row.getResultValue();
								break;
							}
						}
//						if (checking.contains(exaRow.getDescription()))
//							checked = true;
						jPanelResults.add(new CheckBox(exaRow, checked, resultValue));
					}
				}
			}
		}
		return jPanelResults;
	}

	public class CheckBox extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox check = null;
		private JTextField txtResultValue=null;

		public CheckBox(ExamRow exaRow, boolean checked, String resultValue) {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			check=new JCheckBox();
			check.setText(exaRow.getDescription());
			check.setSelected(checked);
			check.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// System.out.println(e.getActionCommand());
					LaboratoryRow labRow=new LaboratoryRow(null, null, e.getActionCommand(), "");
					if (check.isSelected()) {
						examResults.get(jTableExams.getSelectedRow()).add(
								labRow);
					} else {
						examResults.get(jTableExams.getSelectedRow()).remove(
								labRow);
					}
				}
			});
			
			txtResultValue=new JTextField();
			txtResultValue.setText(resultValue);
			
			txtResultValue.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					if(check.isSelected()){
						ArrayList<LaboratoryRow> listRow=examResults.get(jTableExams.getSelectedRow());
						for(LaboratoryRow row:listRow){
							if(row.getDescription().equalsIgnoreCase(check.getText())){
								row.setResultValue(txtResultValue.getText()+e.getKeyChar());
								break;
							}
						}
					}
				}
			});
			
			this.add(check);
			this.add(txtResultValue);			
		}
	}

	private JComboBox getJComboBoxMaterial() {
		if (jComboBoxMaterial == null) {
			//jComboBoxMaterial = new JComboBox(matList);
			jComboBoxMaterial = new JComboBox();
			jComboBoxMaterial.addItem("");
			for(int i = 0; i < MaterialsExamList.getMaterialsExamList().getP().size();i++){
				jComboBoxMaterial.addItem(MessageBundle.getMessage(MaterialsExamList.getMaterialsExamList().getP().getProperty((i+1)+"")));
			}
			jComboBoxMaterial.setPreferredSize(new Dimension(EastWidth,
					ComponentHeight));
			jComboBoxMaterial.setMaximumSize(new Dimension(EastWidth,
					ComponentHeight));
			jComboBoxMaterial.setEnabled(false);
		}
		return jComboBoxMaterial;
	}

	private JPanel getJPanelMaterial() {
		if (jPanelMaterial == null) {
			jPanelMaterial = new JPanel();
			jPanelMaterial.setLayout(new BoxLayout(jPanelMaterial,
					BoxLayout.Y_AXIS));
			jPanelMaterial.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Color.LIGHT_GRAY),
					MessageBundle.getMessage("angal.labnew.material")));
			jPanelMaterial.add(getJComboBoxMaterial());
		}
		return jPanelMaterial;
	}

	private JLabel getJLabelDate() {
		if (jLabelDate == null) {
			jLabelDate = new JLabel();
			jLabelDate.setText("Date");
			jLabelDate.setPreferredSize(LabelDimension);
		}
		return jLabelDate;
	}

	private JPanel getJOpdIpdPanel() {
		if (jOpdIpdPanel == null) {
			jOpdIpdPanel = new JPanel();

			jRadioButtonOPD = new JRadioButton("OPD");
			jRadioButtonIPD = new JRadioButton("IP");

			radioGroup = new ButtonGroup();
			radioGroup.add(jRadioButtonOPD);
			radioGroup.add(jRadioButtonIPD);

			jOpdIpdPanel.add(jRadioButtonOPD);
			jOpdIpdPanel.add(jRadioButtonIPD);

			jRadioButtonOPD.setSelected(true);
		}
		return jOpdIpdPanel;
	}

	private JButton getJButtonTrashPatient() {
		if (jButtonTrashPatient == null) {
			jButtonTrashPatient = new JButton();
			jButtonTrashPatient.setMnemonic(KeyEvent.VK_R);
			jButtonTrashPatient.setPreferredSize(new Dimension(25, 25));
			jButtonTrashPatient.setIcon(new ImageIcon(
					"rsc/icons/remove_patient_button.png")); //$NON-NLS-1$
			jButtonTrashPatient
					.setToolTipText(MessageBundle
							.getMessage("angal.labnew.tooltip.removepatientassociationwiththisexam")); //$NON-NLS-1$
			jButtonTrashPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					patientSelected = null;
					// INTERFACE
					jTextFieldPatient.setText(""); //$NON-NLS-1$
					jTextFieldPatient.setEditable(false);
					jButtonPickPatient.setText(MessageBundle
							.getMessage("angal.labnew.pickpatient"));
					jButtonPickPatient.setToolTipText(MessageBundle
							.getMessage("angal.labnew.tooltip.associateapatientwiththisexam")); //$NON-NLS-1$
					jButtonTrashPatient.setEnabled(false);
				}
			});
		}
		return jButtonTrashPatient;
	}

	private JButton getJButtonPickPatient() {
		if (jButtonPickPatient == null) {
			jButtonPickPatient = new JButton();
			jButtonPickPatient.setText(MessageBundle
					.getMessage("angal.labnew.pickpatient")); //$NON-NLS-1$
			jButtonPickPatient.setMnemonic(KeyEvent.VK_P);
			jButtonPickPatient.setIcon(new ImageIcon(
					"rsc/icons/pick_patient_button.png")); //$NON-NLS-1$
			jButtonPickPatient
					.setToolTipText(MessageBundle
							.getMessage("angal.labnew.tooltip.associateapatientwiththisexam")); //$NON-NLS-1$
			jButtonPickPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					SelectPatient sp = new SelectPatient(LabNew.this,
							patientSelected);
					sp.addSelectionListener(LabNew.this);
					sp.pack();
					sp.setVisible(true);
				}
			});
		}
		return jButtonPickPatient;
	}

	private JTextField getJTextFieldPatient() {
		if (jTextFieldPatient == null) {
			jTextFieldPatient = new JTextField();
			jTextFieldPatient.setText(""); //$NON-NLS-1$
			jTextFieldPatient.setPreferredSize(PatientDimension);
			jTextFieldPatient.setEditable(false);
		}
		return jTextFieldPatient;
	}

	private JLabel getJLabelPatient() {
		if (jLabelPatient == null) {
			jLabelPatient = new JLabel();
			jLabelPatient.setText("Patient");
			jLabelPatient.setPreferredSize(LabelDimension);
		}
		return jLabelPatient;
	}

	private JPanel getJPanelPatient() {
		if (jPanelPatient == null) {
			jPanelPatient = new JPanel(new FlowLayout(FlowLayout.LEFT));

			if (showPatientPicker) {
				jPanelPatient.add(getJLabelPatient());
				jPanelPatient.add(getJTextFieldPatient());
				jPanelPatient.add(getJButtonPickPatient());
				jPanelPatient.add(getJButtonTrashPatient());
			}

//			jPanelPatient.add(getJOpdIpdPanel());
		}
		return jPanelPatient;
	}

	private JPanel getJPanelDate() {
		if (jPanelDate == null) {
			jPanelDate = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jPanelDate.add(getJLabelDate());
			jPanelDate.add(getJCalendarDate());
		}
		return jPanelDate;
	}

	private JDateChooser getJCalendarDate() {
		if (jCalendarDate == null) {
			jCalendarDate = new JDateChooser(RememberDates
					.getLastLabExamDateGregorian().getTime()); // To remind last
																// used
			jCalendarDate.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarDate.setDateFormatString("dd/MM/yy (HH:mm:ss)"); //$NON-NLS-1$
		}
		return jCalendarDate;
	}

	private JPanel getJPanelSouth() {
		if (jPanelSouth == null) {
			jPanelSouth = new JPanel();
			jPanelSouth.setLayout(new BoxLayout(jPanelSouth, BoxLayout.Y_AXIS));
			jPanelSouth.add(getJPanelNote());
			jPanelSouth.add(getJPanelButtons());
		}
		return jPanelSouth;
	}

	private JPanel getJPanelEast() {
		if (jPanelEast == null) {
			jPanelEast = new JPanel();
			jPanelEast.setLayout(new BoxLayout(jPanelEast, BoxLayout.Y_AXIS));
			jPanelEast.add(getJPanelExamButtons());
			jPanelEast.add(getJPanelMaterial());
			jPanelEast.add(getJPanelResults());
		}
		return jPanelEast;
	}

	private JPanel getJPanelNorth() {
		if (jPanelNorth == null) {
			jPanelNorth = new JPanel();
			jPanelNorth.setLayout(new BoxLayout(jPanelNorth, BoxLayout.Y_AXIS));
			jPanelNorth.add(getJPanelDate());
			jPanelNorth.add(getJPanelPatient());
		}
		return jPanelNorth;
	}

	private JScrollPane getJScrollPaneTable() {
		if (jScrollPaneTable == null) {
			jScrollPaneTable = new JScrollPane();
			jScrollPaneTable.setViewportView(getJTableExams());
		}
		return jScrollPaneTable;
	}

	private JTable getJTableExams() {
		if (jTableExams == null) {
			jTableExams = new JTable();
			jTableExams.setModel(new ExamTableModel());
			for (int i = 0; i < examColumnWidth.length; i++) {

				jTableExams.getColumnModel().getColumn(i)
						.setMinWidth(examColumnWidth[i]);
				if (!examResizable[i])
					jTableExams.getColumnModel().getColumn(i)
							.setMaxWidth(examColumnWidth[i]);
			}

			jTableExams.getSelectionModel().setSelectionMode(
					ListSelectionModel.SINGLE_SELECTION);
			ListSelectionModel listSelectionModel = jTableExams
					.getSelectionModel();
			listSelectionModel
					.addListSelectionListener(new ListSelectionListener() {

						public void valueChanged(ListSelectionEvent e) {
							// Check that mouse has been released.
							if (!e.getValueIsAdjusting()) {
								// SAVE PREVIOUS EXAM SELECTED
								if (selectedLab != null) {
									selectedLab.setNote(jTextAreaNote.getText()
											.trim());
									selectedLab
											.setMaterial((String) jComboBoxMaterial
													.getSelectedItem());
								}
								// SHOW NEW EXAM SELECTED
								int selectedRow = jTableExams.getSelectedRow();
								selectedLab = (Laboratory) jTableExams
										.getValueAt(selectedRow, -1);
								jComboBoxMaterial.setSelectedItem(selectedLab
										.getMaterial());
								jTextAreaNote.setText(selectedLab.getNote());
								jPanelResults = getJPanelResults();
								jComboBoxMaterial.setEnabled(true);

								// modified = false;
								validate();
								repaint();
							}
						}
					});

		}
		return jTableExams;
	}

	public JPanel getJPanelExamButtons() {
		if (jPanelExamButtons == null) {
			jPanelExamButtons = new JPanel();
			jPanelExamButtons.setLayout(new BoxLayout(jPanelExamButtons,
					BoxLayout.X_AXIS));
			jPanelExamButtons.add(getJButtonAddExam());
			jPanelExamButtons.add(getJButtonRemoveItem());
		}
		return jPanelExamButtons;
	}

	public JButton getJButtonAddExam() {

		if (jButtonAddExam == null) {
			jButtonAddExam = new JButton();
			jButtonAddExam.setText(MessageBundle
					.getMessage("angal.labnew.exam")); //$NON-NLS-1$
			jButtonAddExam.setMnemonic(KeyEvent.VK_E);
			jButtonAddExam.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddExam.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Icon icon = new ImageIcon("rsc/icons/material_dialog.png"); 
					String mat = "";

					if (mat == null)
						return;

		////////////////////////////////////////////////////
		
		OhTableModelExam<Price> modelOh = new OhTableModelExam<Price>(exaArray);
		
		Image ico;
		
		ExamPicker examPicker = new ExamPicker(modelOh);
		
		examPicker.setSize(300,400);
		
		JDialog dialog = new JDialog();
		dialog.setLocationRelativeTo(null);
		dialog.setSize(600,350);
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		
		examPicker.setParentFrame(dialog);
		dialog.setContentPane(examPicker);
		//dialog.setIconImage(ico);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		//Icon icon = new ImageIcon("rsc/icons/operation_dialog.png"); 
		//Exam exa  = (Exam)examPicker.getSelectedObject();
		ArrayList<Exam> exams = examPicker.getAllSelectedObject();
		///////////////////////////////////////////////////
		Exam exa = null;
		Laboratory lab = null;
		boolean alreadyIn = false;
		
		icon = new ImageIcon("rsc/icons/exam_dialog.png"); //$NON-NLS-1$

		if(exams.size()<1)
			return;

		    //debut boucle
			for(int i=0;i<exams.size();i++){
				alreadyIn = false;
				lab = new Laboratory();
				exa = exams.get(i);
				
					for (Laboratory labItem : examItems) {
						if (labItem.getExam() == exa) {
							JOptionPane
									.showMessageDialog(
											LabNew.this,
											MessageBundle
													.getMessage("angal.labnew.thisexamisalreadypresent"),
											"Error", //$NON-NLS-1$
											JOptionPane.WARNING_MESSAGE);
							//return;
							alreadyIn = true;
						}
					}
					
                    if(alreadyIn) continue;
                    
					if (exa.getProcedure() == 1) {

						ArrayList<ExamRow> exaRowTemp = new ArrayList<ExamRow>();
						for (ExamRow exaRow : exaRowArray) {
							if (exa.getCode().compareTo(exaRow.getExamCode()) == 0) {
								exaRowTemp.add(exaRow);
							}
						}
						icon = new ImageIcon("rsc/icons/list_dialog.png"); //$NON-NLS-1$
				
					} else {
						
						lab.setResult("");
					}

					lab.setExam(exa);
					lab.setMaterial(mat);
					addItem(lab);
			}//fin boucle	
					
				}
			});
		}

		return jButtonAddExam;
	}

	private void addItem(Laboratory lab) {
		examItems.add(lab);
		examResults.add(new ArrayList<LaboratoryRow>());
		jTableExams.updateUI();
		int index = examItems.size() - 1;
		jTableExams.setRowSelectionInterval(index, index);

	}

	private void removeItem(int selectedRow) {
		examItems.remove(selectedRow);
		jTableExams.updateUI();
	}

	private JButton getJButtonRemoveItem() {
		if (jButtonRemoveItem == null) {
			jButtonRemoveItem = new JButton();
			jButtonRemoveItem.setText(MessageBundle
					.getMessage("angal.labnew.remove")); //$NON-NLS-1$
			jButtonRemoveItem.setIcon(new ImageIcon(
					"rsc/icons/delete_button.png")); //$NON-NLS-1$
			jButtonRemoveItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					if (jTableExams.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(
								LabNew.this,
								MessageBundle
										.getMessage("angal.labnew.pleaseselectanexam"), //$NON-NLS-1$
								"Error", //$NON-NLS-1$
								JOptionPane.WARNING_MESSAGE);
					} else {
						removeItem(jTableExams.getSelectedRow());
						jPanelResults.removeAll();
						// validate();
						repaint();
						jComboBoxMaterial.setEnabled(false);
					}
				}
			});

		}
		return jButtonRemoveItem;
	}

	public class ExamTableModel implements TableModel {

		public Class<?> getColumnClass(int columnIndex) {
			return examClasses[columnIndex].getClass();
		}

		public int getColumnCount() {
			return examColumnNames.length;
		}

		public String getColumnName(int columnIndex) {
			return examColumnNames[columnIndex];
		}

		public int getRowCount() {
			if (examItems == null)
				return 0;
			return examItems.size();
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return examItems.get(r);
			}
			if (c == 0) {
				return examItems.get(r).getExam().getDescription();
			}
			if (c == 1) {
				return examItems.get(r).getResult();
			}
			if (c == 2) {
				return examItems.get(r).getResultValue();
			}
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public void addTableModelListener(TableModelListener l) {
		}

		public void removeTableModelListener(TableModelListener l) {
		}

		public void setValueAt(Object value, int rowIndex, int columnIndex) {
		}

	}

	@Override
	public void patientUpdated(AWTEvent e) {
		Patient admPatient = (Patient) e.getSource();
		patientSelected = admPatient;
	}

	@Override
	public void patientInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}
}
