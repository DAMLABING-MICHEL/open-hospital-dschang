package org.isf.admission.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.dicom.gui.DicomGui;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.model.Laboratory;
import org.isf.menu.gui.MainMenu;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.operation.gui.OperationList;
import org.isf.operation.model.OperationRow;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.gui.PatientSummary;
import org.isf.patient.model.Patient;
import org.isf.patvac.manager.PatVacManager;
import org.isf.patvac.model.PatientVaccine;
import org.isf.stat.manager.GenericReportAdmission;
import org.isf.stat.manager.GenericReportDischarge;
import org.isf.stat.manager.GenericReportOpd;
import org.isf.stat.manager.GenericReportPatient;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.table.TableSorter;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import javax.swing.JTabbedPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * This class shows patient data and the list of admissions and lab exams.
 * 
 * last release jun-14-08
 * 
 * @author chiara
 * 
 */

/*----------------------------------------------------------
 * modification history
 * ====================
 * 14/06/08 - chiara - first version
 *                     
 * 30/06/08 - fabrizio - implemented automatic selection of exams within the admission period
 * 
 * 05/09/08 - alessandro - second version:
 * 						 - same PatientSummary than PatientDataBrowser
 * 						 - includes OPD in the table
 -----------------------------------------------------------*/
public class PatientFolderBrowser extends ModalJFrame implements PatientInsert.PatientListener,
		PatientInsertExtended.PatientListener, AdmissionBrowser.AdmissionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3427327158197856822L;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private EventListenerList deleteAdmissionListeners = new EventListenerList();

	public interface DeleteAdmissionListener extends EventListener {
		public void deleteAdmissionUpdated(AWTEvent e);
	}

	public void addDeleteAdmissionListener(DeleteAdmissionListener l) {
		deleteAdmissionListeners.add(DeleteAdmissionListener.class, l);
	}

	public void removeDeleteAdmissionListener(DeleteAdmissionListener listener) {
		deleteAdmissionListeners.remove(DeleteAdmissionListener.class, listener);
	}

	// ---------------------------------------------------------------------

	public void patientInserted(AWTEvent e) {
	}

	public void patientUpdated(AWTEvent e) {
		jContentPane = null;
		initialize();
	}

	public void admissionInserted(AWTEvent e) {
	}

	public void admissionUpdated(AWTEvent e) {
		jContentPane = null;
		initialize();
	}

	private Patient patient = null;

	public PatientFolderBrowser(AdmittedPatientBrowser listener, Patient myPatient) {
		super();
		patient = myPatient;
		initialize();
	}

	private void initialize() {

		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(MessageBundle.getMessage("angal.admission.patientdata")); 
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private JPanel jContentPane = null;

	private JPanel getJContentPane() {

		if (jContentPane == null) {

			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getPatientDataPanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JPanel patientData = null;

	private JPanel getPatientDataPanel() {
		patientData = new JPanel();
		patientData.setLayout(new BorderLayout());
		patientData.add(getTablesPanel(), BorderLayout.EAST);

		PatientSummary ps = new PatientSummary(patient);
		JPanel pp = ps.getPatientCompleteSummary();
		patientData.add(pp, BorderLayout.WEST);

		return patientData;
	}

	private ArrayList<Admission> admList;
	private ArrayList<Laboratory> labList;
	private ArrayList<Disease> disease;
	// private ArrayList<Operation> operation;
	private ArrayList<Ward> ward;
	private ArrayList<Opd> opdList;
	
	private OperationList opeList;
	
	private List<OperationRow> operationList;
	private String[] plColums = {
			MessageBundle.getMessage("angal.common.datem"),
			MessageBundle.getMessage("angal.lab.examm"),
			MessageBundle.getMessage("angal.lab.resultm") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	//private int[] plColumwidth = { 150, 200, 50, 200 };
	private int[] plColumwidth = { 150, 200, 200 };

	private DefaultTableModel admModel;
	private DefaultTableModel labModel;
	private DefaultTableModel patVaccinesModel;

	OhDefaultCellRenderer cellRenderer = new OhDefaultCellRenderer();
	
	TableSorter sorter;
	TableSorter sorterLab;


	private JTable admTable;
	private JTable labTable;
	private JTable patVaccinesTable;
	
	
	private boolean[] pVacColumnsVisible = { true, false, false, false, true, true };

	private String[] patVacColums = { MessageBundle.getMessage("angal.common.datem"),
			MessageBundle.getMessage("angal.patvac.patientm"),
			MessageBundle.getMessage("angal.patvac.sexm"),
			MessageBundle.getMessage("angal.patvac.agem"),
			MessageBundle.getMessage("angal.patvac.vaccinem"),
			MessageBundle.getMessage("angal.patvac.vaccinetypem") };

	private String[] patAdmColumns = { 
			MessageBundle.getMessage("angal.common.datem"),
			MessageBundle.getMessage("angal.admission.wards"),
			MessageBundle.getMessage("angal.admission.diagnosisinm"),
			MessageBundle.getMessage("angal.admission.diagnosisoutm"),
			MessageBundle.getMessage("angal.admission.statusm"),
	};

	//private int[] patVacColumwidth = { 100, 150, 50, 50, 150, 150 };
	private int[] patAdmColumwidth = { 90, 130, 150, 150, 80 };

	private ArrayList<PatientVaccine> lPatVac;

	private JScrollPane scrollPane;
	private JScrollPane scrollPaneLab;

	private JPanel tablesPanel = null;

	private JPanel getTablesPanel() {

		tablesPanel = new JPanel(new BorderLayout());

		// Alex: added sorters, for Java6 only
		// admModel = new AdmissionBrowserModel();
		// admTable = new JTable(admModel);

		// Alex: Java5 compatible
		admModel = new AdmissionBrowserModel();
		sorter = new TableSorter(admModel);
		admTable = new JTable(sorter);
		

		/*** apply default oh cellRender *****/
		admTable.setDefaultRenderer(Object.class, cellRenderer);
		admTable.setDefaultRenderer(Double.class, cellRenderer);
		admTable.addMouseMotionListener(new MouseMotionListener() {			
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
		admTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				cellRenderer.setHoveredRow(-1);
			}
		});		
		sorter.sortByColumn(0, false); // sort by first column, descending

		for (int i = 0; i < patAdmColumns.length; i++) {
			admTable.getColumnModel().getColumn(i).setPreferredWidth(patAdmColumwidth[i]);
		}

		scrollPane = new JScrollPane(admTable);
		scrollPane.setPreferredSize(new Dimension(500, 200));
		tablesPanel.add(scrollPane, BorderLayout.NORTH);

		labModel = new LabBrowserModel();
		sorterLab = new TableSorter(labModel);
		sorterLab.sortByColumn(0, false);

		JTabbedPane tabbedPaneLabVacc = new JTabbedPane(JTabbedPane.TOP);
		tablesPanel.add(tabbedPaneLabVacc, BorderLayout.CENTER);
		labTable = new JTable(sorterLab);
		
		
		
		/*** apply default oh cellRender *****/
		labTable.setDefaultRenderer(Object.class, cellRenderer);
		labTable.setDefaultRenderer(Double.class, cellRenderer);
        labTable.addMouseMotionListener(new MouseMotionListener() {	
			@Override
			public void mouseMoved(MouseEvent e) {
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
        labTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				cellRenderer.setHoveredRow(-1);
			}
		});
		
		scrollPaneLab = new JScrollPane(labTable);
		tabbedPaneLabVacc.addTab(MessageBundle.getMessage("angal.patientfolder.tab.exams"), null, scrollPaneLab, null);
		scrollPaneLab.setPreferredSize(new Dimension(500, 200));
		
		JPanel patVaccinesPanel = new JPanel();
		patVaccinesPanel.setVisible(false);
		patVaccinesPanel.setPreferredSize(new Dimension(500, 200));
		patVaccinesPanel.setLayout(new BoxLayout(patVaccinesPanel, BoxLayout.Y_AXIS));

		JPanel label1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		label1Panel.add(new JLabel("Patient Vaccines"));

		patVaccinesPanel.add(label1Panel);
		
		
		JScrollPane scrollPanePatVac = new JScrollPane(getPatientVaccinesTable());
		scrollPanePatVac.setPreferredSize(new Dimension(500, 175));
		patVaccinesPanel.add(scrollPanePatVac);

		tablesPanel.add(patVaccinesPanel, BorderLayout.SOUTH);
		
		
		JScrollPane scrollPanePatVac2 = new JScrollPane(getPatientVaccinesTable());
		tabbedPaneLabVacc.addTab(MessageBundle.getMessage("angal.patientfolder.tab.vaccines"), null, scrollPanePatVac2, null);		
		
		opeList = new OperationList(patient);
		tabbedPaneLabVacc.addTab(MessageBundle.getMessage("angal.patientfolder.tab.operations"), null, opeList, null);
		///////////////////////////////////////////
        
		ListSelectionModel listSelectionModel = admTable.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				// Check that mouse has been released.
				if (!e.getValueIsAdjusting()) {
					GregorianCalendar startDate = null;
					GregorianCalendar endDate = null;
					int selectedRow = admTable.getSelectedRow();
					Object selectedObject = sorter.getValueAt(selectedRow, -1);
					Object selectedObject2;
					if (selectedObject instanceof Admission) {

						Admission ad = (Admission) selectedObject;
						startDate = ad.getAdmDate();
						endDate = ad.getDisDate();

					} else {
						Opd opd2 = null;
						Admission ad2 = null;
						if (selectedRow > 0) {
							selectedObject2 = sorter.getValueAt(selectedRow - 1, -1);
							if (selectedObject2 instanceof Opd)
								opd2 = (Opd) selectedObject2;
							else
								ad2 = (Admission) selectedObject2;
						}

						Opd opd = (Opd) selectedObject;
						// Opd opd = (Opd) (((AdmissionBrowserModel)
						// admModel)
						// .getValueAt(selectedRow, -1));
						startDate = opd.getVisitDate();
						if (opd2 != null)
							endDate = opd2.getVisitDate();
						if (ad2 != null)
							endDate = ad2.getAdmDate();
					}
					// Clear past selection, if any.
					labTable.clearSelection();
					for (int i = 0; i < labList.size(); i++) {
						// Laboratory laboratory = labList.get(i);
						Laboratory laboratory = (Laboratory) sorterLab.getValueAt(i, -1);
						Date examDate = laboratory.getExamDate().getTime();

						// Check that the exam date is included between
						// admission date and discharge date.
						// If the patient has not been discharged yet
						// (and then discharge date doesn't exist)
						// check only that the exam date is the same or
						// after the admission date.
						// On true condition select the corresponding
						// table row.
						if (!examDate.before(startDate.getTime())
								&& (null == endDate ? true : !examDate.after(endDate.getTime()))) {
							labTable.addRowSelectionInterval(i, i);
						}
					}
					/***** selection of operations ****/
					int index = admTable.getSelectedRow();
					Object object = admTable.getValueAt(index, -1);
					Opd currentOpd = null;
					Admission currentAdmission = null;
					int currentCode = -1;
					if(object instanceof Opd){
						currentOpd = (Opd)object;
						currentCode = currentOpd.getCode();
					}
					if(object instanceof Admission){
						currentAdmission = (Admission)object;
						currentCode = currentAdmission.getId();
					}
					operationList = opeList.getOprowData();
					opeList.getJtableData().clearSelection();
					for (int i = 0; i < operationList.size(); i++) {
						OperationRow oprRow = operationList.get(i);
						if((oprRow.getAdmissionId()==currentCode && currentAdmission!=null) || (oprRow.getOpdId()==currentCode && currentOpd!=null) ){
							//opeList.getJtableData().getSelectionModel().setSelectionInterval(i, i);
							opeList.getJtableData().addRowSelectionInterval(i, i);
						}									
					}
					///////////////////////////////////
				}
				
			}
		});
		return tablesPanel;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel;
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		if (MainMenu.checkUserGrants("btnpatfoldopdrpt"))buttonPanel.add(getOpdReportButton(), null); //$NON-NLS-1$
		if (MainMenu.checkUserGrants("btnpatfoldadmrpt"))buttonPanel.add(getAdmReportButton(), null); //$NON-NLS-1$
		if (MainMenu.checkUserGrants("btnpatfoldadmrpt"))buttonPanel.add(getDisReportButton(), null); //$NON-NLS-1$
		if (MainMenu.checkUserGrants("btnpatfoldpatrpt"))buttonPanel.add(getLaunchReportButton(), null); //$NON-NLS-1$
		if (Param.bool("DICOMMODULEENABLED") && MainMenu.checkUserGrants("btnpatfolddicom"))buttonPanel.add(getDICOMButton(), null); //$NON-NLS-1$
		buttonPanel.add(getCloseButton(), null);
		return buttonPanel;
	}

	private JButton opdReportButton = null;
	private JButton admReportButton = null;
	private JButton disReportButton = null;
	private JButton launchReportButton = null;
	private JButton dicomButton = null;
	private JButton closeButton = null;

	private JButton getOpdReportButton() {
		if (opdReportButton == null) {
			opdReportButton = new JButton();
			opdReportButton.setMnemonic(KeyEvent.VK_O);
			opdReportButton.setText(MessageBundle
					.getMessage("angal.admission.patientfolder.opdchart")); //$NON-NLS-1$
			opdReportButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (admTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(
								PatientFolderBrowser.this,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
						return;
					}

					int selectedRow = admTable.getSelectedRow();
					Object selectedObj = sorter.getValueAt(selectedRow, -1);

					if (selectedObj instanceof Opd) {

						Opd opd = (Opd) sorter.getValueAt(selectedRow, -1);
						new GenericReportOpd(opd.getCode(), opd.getpatientCode(),
								Param.string("OPDCHART"));
					} else {
						JOptionPane.showMessageDialog(
								PatientFolderBrowser.this,
								MessageBundle
										.getMessage("angal.admission.patientfolder.pleaseselectanopd"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
						return;
					}
				}
			});
		}
		return opdReportButton;
	}

	private JButton getDisReportButton() {
		if (disReportButton == null) {
			disReportButton = new JButton();
			disReportButton.setMnemonic(KeyEvent.VK_S);
			disReportButton.setText(MessageBundle
					.getMessage("angal.admission.patientfolder.dischart")); //$NON-NLS-1$
			disReportButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (admTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(
								PatientFolderBrowser.this,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
						return;
					}

					int selectedRow = admTable.getSelectedRow();
					Object selectedObj = sorter.getValueAt(selectedRow, -1);

					if (selectedObj instanceof Admission) {

						Admission adm = (Admission) sorter.getValueAt(selectedRow, -1);
						if (adm.getDisDate() == null) {
							JOptionPane.showMessageDialog(
									PatientFolderBrowser.this,
									MessageBundle
											.getMessage("angal.admission.patientfolder.thepatientisnotyetdischarged"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
							return;
						}
						new GenericReportDischarge(adm.getId(), adm.getPatId(),
								Param.string("DISCHART"));
					} else {
						JOptionPane.showMessageDialog(
								PatientFolderBrowser.this,
								MessageBundle
										.getMessage("angal.admission.patientfolder.pleaseselectanadmission"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
						return;
					}
				}
			});
		}
		return disReportButton;
	}

	private JButton getAdmReportButton() {
		if (admReportButton == null) {
			admReportButton = new JButton();
			admReportButton.setMnemonic(KeyEvent.VK_A);
			admReportButton.setText(MessageBundle
					.getMessage("angal.admission.patientfolder.admchart")); //$NON-NLS-1$
			admReportButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (admTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(
								PatientFolderBrowser.this,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
						return;
					}

					int selectedRow = admTable.getSelectedRow();
					Object selectedObj = sorter.getValueAt(selectedRow, -1);

					if (selectedObj instanceof Admission) {

						Admission adm = (Admission) sorter.getValueAt(selectedRow, -1);
						new GenericReportAdmission(adm.getId(), adm.getPatId(),
								Param.string("ADMCHART"));
					} else {
						JOptionPane.showMessageDialog(
								PatientFolderBrowser.this,
								MessageBundle
										.getMessage("angal.admission.patientfolder.pleaseselectanadmission"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$
						return;
					}
				}
			});
		}
		return admReportButton;
	}

	private JButton getLaunchReportButton() {
		if (launchReportButton == null) {
			launchReportButton = new JButton();
			launchReportButton.setMnemonic(KeyEvent.VK_R);
			launchReportButton.setText(MessageBundle
					.getMessage("angal.admission.patientfolder.launchreport")); //$NON-NLS-1$
			launchReportButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// GenericReportMY rpt3 = new GenericReportMY(new
					// Integer(6), new Integer(2008),
					// "hmis108_adm_by_diagnosis_in");
					new GenericReportPatient(patient.getCode(), Param.string("PATIENTSHEET") );
				}
			});
		}
		return launchReportButton;
	}

	DicomGui dg = null;

	public void resetDicomViewer() {
		dg = null;
	}

	private JButton getDICOMButton() {
		if (dicomButton == null) {
			dicomButton = new JButton();
			dicomButton.setMnemonic(KeyEvent.VK_D);
			dicomButton.setText(MessageBundle.getMessage("angal.admission.patientfolder.dicom")); //$NON-NLS-1$
			dicomButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (dg == null)
						dg = new DicomGui(patient, PatientFolderBrowser.this);
				}
			});
		}
		return dicomButton;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setMnemonic(KeyEvent.VK_C);
			closeButton.setText(MessageBundle.getMessage("angal.common.close")); //$NON-NLS-1$
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return closeButton;
	}

	/**
	 * This method initializes jTable, that contains the information about the
	 * patient's vaccines
	 * 
	 * @return jTable (JTable)
	 */
	private JTable getPatientVaccinesTable() {
		if (patVaccinesTable == null) {
			// patVaccinesModel = new PatVacBrowsingModel();
			patVaccinesModel = new PatVacBrowsingModel();
			patVaccinesTable = new JTable(patVaccinesModel);
			
			/*** apply default oh cellRender *****/
			patVaccinesTable.setDefaultRenderer(Object.class, cellRenderer);
			patVaccinesTable.setDefaultRenderer(Double.class, cellRenderer);
			patVaccinesTable.addMouseMotionListener(new MouseMotionListener() {
				
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
			patVaccinesTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					cellRenderer.setHoveredRow(-1);
				}
			});
			
			TableColumnModel columnModel = patVaccinesTable.getColumnModel();
			
		}
		return patVaccinesTable;
	}

	class AdmissionBrowserModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -453243229156512947L;

		public AdmissionBrowserModel() {
			AdmissionBrowserManager manager = new AdmissionBrowserManager();
			admList = manager.getAdmissions(patient);
			// Collections.sort(admList);
			// Collections.reverse(admList);
			DiseaseBrowserManager dbm = new DiseaseBrowserManager();
			disease = dbm.getDiseaseAll();
			// org.isf.operation.manager.OperationBrowserManager obm = new
			// org.isf.operation.manager.OperationBrowserManager();
			// operation = obm.getOperation();
			WardBrowserManager wbm = new WardBrowserManager();
			ward = wbm.getWards();
			OpdBrowserManager opd = new OpdBrowserManager();
			opdList = opd.getOpdList(patient.getCode());
		}

		public int getRowCount() {
			if (admList == null && opdList == null)
				return 0;

			return admList.size() + opdList.size();
		}

		public String getColumnName(int c) {
			return patAdmColumns[c];
		}

		public int getColumnCount() {
			return patAdmColumns.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				if (r < admList.size()) {
					return admList.get(r);
				} else {
					int z = r - admList.size();
					return opdList.get(z);
				}

			} else if (c == 0) {
				if (r < admList.size()) {
					Date myDate = (admList.get(r)).getAdmDate().getTime();
					DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT,
							Locale.ITALIAN);
					return currentDateFormat.format(myDate);
				} else {
					int z = r - admList.size();
					Date myDate = (opdList.get(z)).getVisitDate().getTime();
					DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT,
							Locale.ITALIAN);
					return currentDateFormat.format(myDate);
				}

			} else if (c == 1) {
				if (r < admList.size()) {
					String id = admList.get(r).getWardId();
					for (Ward elem : ward) {
						if (elem.getCode().equalsIgnoreCase(id))
							return elem.getDescription();
					}
				} else {
					return MessageBundle.getMessage("angal.admission.patientfolder.opd"); //$NON-NLS-1$
				}
			} else if (c == 2) {
				String id = null;
				if (r < admList.size()) {
					id = admList.get(r).getDiseaseInId();
					if (id == null) {
						id = ""; //$NON-NLS-1$
					}
				} else {
					int z = r - admList.size();
					id = opdList.get(z).getDisease();
					if (id == null) {
						id = ""; //$NON-NLS-1$
					}
				}
				for (Disease elem : disease) {
					if (elem.getCode().equalsIgnoreCase(id))
						return elem.getDescription();
				}
				return MessageBundle.getMessage("angal.admission.nodisease"); //$NON-NLS-1$

			} else if (c == 3) {
				String id = null;
				if (r < admList.size()) {
					id = admList.get(r).getDiseaseOutId1();
					if (id == null) {
						id = ""; //$NON-NLS-1$
					}
				} else {
					int z = r - admList.size();
					id = opdList.get(z).getDisease3();
					if (id == null) {
						id = opdList.get(z).getDisease2();
						if (id == null) {
							id = ""; //$NON-NLS-1$
						}
					}
				}
				for (Disease elem : disease) {
					if (elem.getCode().equalsIgnoreCase(id))
						return elem.getDescription();
				}
				return MessageBundle.getMessage("angal.admission.nodisease"); //$NON-NLS-1$

			} else if (c == 4) {
				if (r < admList.size()) {
					if (admList.get(r).getDisDate() == null)
						return MessageBundle.getMessage("angal.admission.present"); //$NON-NLS-1$
					else {
						Date myDate = admList.get(r).getDisDate().getTime();
						DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT,
								Locale.ITALIAN);
						return currentDateFormat.format(myDate);
					}
				} else {
					int z = r - admList.size();
					String status = opdList.get(z).getNewPatient();
					return (status.compareTo("R") == 0 ? MessageBundle.getMessage("angal.opd.reattendance") : MessageBundle.getMessage("angal.opd.newattendance")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	class LabBrowserModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8245833681073162426L;

		public LabBrowserModel() {
			org.isf.lab.manager.LabManager lbm = new org.isf.lab.manager.LabManager();
			labList = lbm.getLaboratory(patient.getCode());
		}

		public int getRowCount() {
			if (labList == null)
				return 0;
			return labList.size();
		}

		public String getColumnName(int c) {
			return plColums[c];
		}

		public int getColumnCount() {
			return plColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return labList.get(r);
			} else if (c == 0) {
				// System.out.println(labList.get(r).getExam().getExamtype().getDescription());

				Date examDate = labList.get(r).getExamDate().getTime();
				DateFormat currentDateFormat = DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.ITALIAN);
				return currentDateFormat.format(examDate);
			} else if (c == 1) {
				return labList.get(r).getExam().getDescription();
			} 
//			else if (c == 2) {
//				return labList.get(r).getCode();
//			} 
			else if (c == 2) {
				Laboratory lab=labList.get(r);
				String resultValue=lab.getResultValue();
				if(null!=resultValue && !"".equalsIgnoreCase(resultValue)){
					resultValue=" ("+resultValue+")";
				}
				else{
					resultValue="";
				}
				return lab.getResult()+resultValue;
//				return labList.get(r).getResult();
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
	 * This class defines the model for the Table
	 * 
	 * 
	 */

	class PatVacBrowsingModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PatVacManager manager = new PatVacManager();

		public PatVacBrowsingModel() {
			PatVacManager manager = new PatVacManager();
			
			lPatVac = manager.getPatientVaccine(null, null, "" + patient.getCode(), null, null,
					'A', 0, 0);
		}

		public PatVacBrowsingModel(String vaccineTypeCode, String vaccineCode,
				GregorianCalendar dateFrom, GregorianCalendar dateTo, char sex, int ageFrom,
				int ageTo) {
			lPatVac = manager.getPatientVaccine(vaccineTypeCode, vaccineCode, dateFrom, dateTo,
					sex, ageFrom, ageTo);

		}

		public PatVacBrowsingModel(String vaccineTypeCode, String vaccineCode, String patientCode,
				GregorianCalendar dateFrom, GregorianCalendar dateTo, char sex, int ageFrom,
				int ageTo) {
			lPatVac = manager.getPatientVaccine(vaccineTypeCode, vaccineCode, patientCode,
					dateFrom, dateTo, sex, ageFrom, ageTo);

		}

		public int getRowCount() {
			if (lPatVac == null)
				return 0;
			return lPatVac.size();
		}

		public String getColumnName(int c) {
			return patVacColums[getNumber(c)];
		}

		public int getColumnCount() {
			int c = 0;
			for (int i = 0; i < pVacColumnsVisible.length; i++) {
				if (pVacColumnsVisible[i]) {
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
				if (!pVacColumnsVisible[i]) {
					n++;
				}
				i++;
			} while (i < n);
			// If we are on an invisible column,
			// we have to go one step further
			while (!pVacColumnsVisible[n]) {
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
			PatientVaccine patVac = lPatVac.get(r);
			if (c == -1) {
				return patVac;
			} else if (getNumber(c) == 0) {
				return dateFormat.format(patVac.getVaccineDate().getTime());
			} else if (getNumber(c) == 1) {
				return patVac.getPatName();
			} else if (getNumber(c) == 2) {
				return patVac.getPatSex();
			} else if (getNumber(c) == 3) {
				return patVac.getPatAge();
			} else if (getNumber(c) == 4) {
				return patVac.getVaccine().getDescription();
			} else if (getNumber(c) == 5) {
				return patVac.getVaccine().getVaccineType().getDescription();
			}
			return null;
		}

		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}// PatVacBrowsingModel

}// class
