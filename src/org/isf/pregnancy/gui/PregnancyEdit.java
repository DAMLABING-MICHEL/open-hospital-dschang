package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.gui.OpdEditExtended;
import org.isf.parameters.manager.Param;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.pregnancy.manager.PregnancyCareManager;
import org.isf.pregnancy.model.Pregnancy;
import org.isf.pregnancy.model.PregnancyExam;
import org.isf.pregnancy.model.PregnancyExamResult;
import org.isf.pregnancy.model.PregnancyVisit;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.visits.model.Visit;

import com.toedter.calendar.JDateChooser;

//public class PregnancyEdit extends JDialog 
public class PregnancyEdit extends JDialog {

	private static final long serialVersionUID = 1L;
	/**
	 * @uml.property name="pregnancyListeners"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private EventListenerList pregnancyListeners = new EventListenerList();

	public interface PregnancyListener extends EventListener {
		public void pregnancyUpdated(AWTEvent e);

		public void pregnancyInserted(AWTEvent e);
	}

	public void addPregnancyListener(PregnancyListener l) {
		pregnancyListeners.add(PregnancyListener.class, l);
	}

	public void removePregnancyListener(PregnancyListener listener) {
		pregnancyListeners.remove(PregnancyListener.class, listener);
	}

	private void firePregnancyInserted(PregnancyVisit aVisit) {
		AWTEvent event = new AWTEvent(aVisit, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 *  
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = pregnancyListeners
				.getListeners(PregnancyListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PregnancyListener) listeners[i]).pregnancyInserted(event);
	}

	private void firePregnancyUpdated(PregnancyVisit aVisit) {
		AWTEvent event = new AWTEvent(aVisit, AWTEvent.RESERVED_ID_MAX + 1) {

			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = pregnancyListeners
				.getListeners(PregnancyListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PregnancyListener) listeners[i]).pregnancyUpdated(event);
	}

	private String[] vColums = {
			MessageBundle.getMessage("angal.pregnancyexam.code"),
			MessageBundle.getMessage("angal.pregnancyexam.name"),
			MessageBundle.getMessage("angal.pregnancy.examresult") };

	private JTable examTable = null;

	private int[] vColumwidth = { 10, 120, 90 };
	/**
	 * @uml.property name="myFrame"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private PregnancyEdit myFrame;
	/**
	 * @uml.property name="pregnancyvisits"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="org.isf.pregnancy.model.PregnancyVisit"
	 */
	private ArrayList<PregnancyVisit> pregnancyvisits = null;
	/**
	 * @uml.property name="patientsPregnancies"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="org.isf.pregnancy.model.Pregnancy"
	 */
	private ArrayList<Pregnancy> patientsPregnancies = null;

	/**
	 * @uml.property name="nonlabexams"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="org.isf.pregnancy.model.PregnancyExam"
	 */
	private ArrayList<PregnancyExam> pregnancyexams = null;
	/**
	 * @uml.property name="treatmTypeList"
	 */
	private ArrayList<PregnantTreatmentType> treatmTypeList = null;

	/**
	 * @uml.property name="nonlabexamoutcomes"
	 * @uml.associationEnd qualifier=
	 *                     "getCode:java.lang.String org.isf.pregnancy.model.PregnancyExamResult"
	 */
	private HashMap<String, PregnancyExamResult> examoutcomes = null;

	/**
	 * @uml.property name="generalvisits"
	 */
	private ArrayList<Visit> generalvisits = null;

	/**
	 * @uml.property name="pPatient"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Patient pPatient = null;
	/**
	 * @uml.property name="pregnancy"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private Pregnancy pregnancy = null;
	/**
	 * @uml.property name="manager"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private PregnancyCareManager manager = new PregnancyCareManager();
	/**
	 * @uml.property name="patientmanager"
	 * @uml.associationEnd
	 */
	private PatientBrowserManager patientmanager = null;
	/**
	 * @uml.property name="pregnancyvisit"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private PregnancyVisit pregnancyvisit = null;
	/**
	 * @uml.property name="lmpDateChooser"
	 * @uml.associationEnd
	 */
	private JDateChooser lmpDateChooser = null;
	/**
	 * @uml.property name="scheduledDeliveryDateChooser"
	 * @uml.associationEnd
	 */
	private JDateChooser scheduledDeliveryDateChooser = null;
	/**
	 * @uml.property name="visitDateChooser"
	 * @uml.associationEnd
	 */
	private JDateChooser visitDateChooser = null;
	/**
	 * @uml.property name="nextvisitDateChooser1"
	 * @uml.associationEnd
	 */
	private JDateChooser nextvisitDateChooser = null;
	/**
	 * @uml.property name="pregnancyNrBox"
	 * @uml.associationEnd
	 */
	private JComboBox pregnancyNrBox = null;
	/**
	 * @uml.property name="visitHours"
	 * @uml.associationEnd
	 */
	private JComboBox visitHours = null;
	/**
	 * @uml.property name="visitMinutes"
	 * @uml.associationEnd
	 */
	private JComboBox visitMinutes = null;
	/**
	 * @uml.property name="nextvisitHours1"
	 * @uml.associationEnd
	 */
	private JComboBox nextvisitHours = null;
	/**
	 * @uml.property name="nextvisitMinutes1"
	 * @uml.associationEnd
	 */
	private JComboBox nextvisitMinutes = null;
	/**
	 * @uml.property name="bloodgroupBox"
	 * @uml.associationEnd
	 */
	private JComboBox bloodgroupBox = null;
	/**
	 * @uml.property name="notearea"
	 * @uml.associationEnd
	 */
	private JTextArea notearea = null;
	private JScrollPane examscrollpane = null;
	/**
	 * @uml.property name="treatmTypeBox"
	 * @uml.associationEnd
	 */
	private JComboBox treatmTypeBox = null;
	/**
	 * @uml.property name="today"
	 */
	private GregorianCalendar today;
	/**
	 * @uml.property name="newVisit"
	 */
	private boolean newVisit = true;
	/**
	 * @uml.property name="newPregnancy"
	 */
	private boolean newPregnancy = true;

	private boolean fromAdmission = false;

	public PregnancyEdit(JFrame owner, Patient patient,
			ArrayList<PregnancyVisit> visits, PregnancyVisit selectedvisit,
			int visittype, boolean insertpregnancy) {
		super(owner, true);
		//super();
		this.pPatient = patient;
		this.pregnancyvisits = visits;
		this.newPregnancy = insertpregnancy;

		this.patientsPregnancies = manager.getPatientsPregnancies(patient
				.getCode());
		if (selectedvisit != null) {
			newVisit = false;
			this.pregnancyvisit = selectedvisit;
			this.pregnancy = manager.getPregnancy(this.pregnancyvisit
					.getPregnancyId());

			examoutcomes = manager.getExamResults(pregnancyvisit.getVisitId());
			pregnancy = manager.getPregnancy(pregnancyvisit.getPregnancyId());

		} else {
			pregnancyvisit = new PregnancyVisit(pPatient.getCode(), 0,
					visittype);
			if (!newPregnancy) {
				pregnancy = manager.getPregnancy(pregnancyvisits.get(0)
						.getPregnancyId());
				pregnancyvisit.setPregnancId(pregnancy.getPregId());
				pregnancyvisit.setPregnancyNr(pregnancy.getPregnancynr());

			} else {
				pregnancy = new Pregnancy(pPatient.getCode());

			}
			examoutcomes = new HashMap<String, PregnancyExamResult>();
		}

		this.pregnancyexams = manager.getVisitExams_byVisitType(visittype);

		myFrame = this;
		today = new GregorianCalendar();
		this.setResizable(false);
		initComponents();
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (generalvisits != null)
					generalvisits.clear();
				if (treatmTypeList != null)
					treatmTypeList.clear();
				if (examoutcomes != null)
					examoutcomes.clear();
				if (patientsPregnancies != null)
					patientsPregnancies.clear();
				dispose();
			}
		});

	}

	// from AdmissionBrowser
	/**
	 * @wbp.parser.constructor
	 */
	public PregnancyEdit(JFrame owner, Patient patient, int visittype) {
		//super(owner, true);
		super();
		this.pPatient = patient;
		this.pregnancyvisits = manager.getPregnancyVisits(patient.getCode());
		this.newPregnancy = false;
		this.fromAdmission = true;
		this.patientsPregnancies = manager.getPatientsPregnancies(patient
				.getCode());

		pregnancyvisit = new PregnancyVisit(pPatient.getCode(), 0, visittype);
		
		pregnancy = manager.getPregnancy(pregnancyvisits.get(0)
					.getPregnancyId());
		pregnancyvisit.setPregnancId(pregnancy.getPregId());
		pregnancyvisit.setPregnancyNr(pregnancy.getPregnancynr());
		examoutcomes = new HashMap<String, PregnancyExamResult>();
		this.pregnancyexams = manager.getVisitExams_byVisitType(visittype);
		this.newVisit = true;
		myFrame = this;
		today = new GregorianCalendar();
		this.setResizable(false);
		initComponents();
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (generalvisits != null)
					generalvisits.clear();
				if (treatmTypeList != null)
					treatmTypeList.clear();
				if (examoutcomes != null)
					examoutcomes.clear();
				if (patientsPregnancies != null)
					patientsPregnancies.clear();
				dispose();
			}
		});

	}

	private void initComponents() {
		setTitle(MessageBundle.getMessage("angal.pregnancy.pregnancybrowser"));
		this.setBounds(200, 200, 750, 650);
		getContentPane().add(getDataPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		setLocationRelativeTo(null);

	}

	private JPanel getDataPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getPatientPanel(), java.awt.BorderLayout.NORTH);
		panel.add(getPregnancyDetailsPanel(), java.awt.BorderLayout.CENTER);
		panel.add(getAdditionalPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel getAdditionalPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getNextVisitDatePanel(), BorderLayout.WEST);
		panel.add(getNotePanel(), BorderLayout.EAST);
		return panel;
	}

	private JPanel getPregnancyDetailsPanel() {
		JPanel data = new JPanel(new BorderLayout());

		data.add(getExamsScrollPane(), BorderLayout.CENTER);
		data.add(getPregnancyPanel(), java.awt.BorderLayout.EAST);
		return data;
	}

	private JPanel getPatientPanel() {
		
		

		

		
		
		
		
		JPanel panel = new JPanel();
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 315, 200, 100, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowHeights = new int[] { 30, 0 };
		gbl_panel.rowWeights = new double[] { 0.0 };
		panel.setLayout(gbl_panel);
		
		
		GridBagConstraints gbc_visitDatePanel = new GridBagConstraints();
		gbc_visitDatePanel.fill = GridBagConstraints.BOTH;
		gbc_visitDatePanel.gridx = 0;
		gbc_visitDatePanel.gridy = 0;
		panel.add(getVisitDatePanel(), gbc_visitDatePanel);
		
		GridBagConstraints gbc_BloodgroupPanel = new GridBagConstraints();
		gbc_BloodgroupPanel.fill = GridBagConstraints.BOTH;
		gbc_BloodgroupPanel.gridx = 1;
		gbc_BloodgroupPanel.gridy = 0;
		panel.add(getBloodgroupPanel(), gbc_BloodgroupPanel);
		
		GridBagConstraints gbc_TreatmentPanel = new GridBagConstraints();
		gbc_TreatmentPanel.fill = GridBagConstraints.BOTH;
		gbc_TreatmentPanel.gridx = 2;
		gbc_TreatmentPanel.gridy = 0;
		panel.add(getTreatmentPanel(), gbc_TreatmentPanel);
		
//		panel.add(getVisitDatePanel());
//		panel.add(getBloodgroupPanel());
//		panel.add(getTreatmentPanel());
//		panel.setPreferredSize(new Dimension(740, 100));
		return panel;
	}

	private JPanel getPregnancyPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(getPregnancyNrPanel());
		panel.add(getLmpDatePanel());
		panel.add(getScheduledDeliveryDatePanel());
		panel.setPreferredSize(new Dimension(230, 300));
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.pregnancydetails")));
		return panel;
	}

	private JPanel getExamsScrollPane() {
		Object[][] data = new Object[pregnancyexams.size()][3];
		for (int a = 0; a < pregnancyexams.size(); a++) {
			String id = pregnancyexams.get(a).getExamId();
			data[a][0] = id;
			data[a][1] = pregnancyexams.get(a).getExamDesc();
			String def = pregnancyexams.get(a).getExamDefault();
			if (examoutcomes.containsKey(id)) {
				data[a][2] = examoutcomes.get(id).getOutcome();
			} else if (def != null && def.length() > 0) {
				data[a][2] = def;
			}
		}

		DefaultTableModel model = new DefaultTableModel(data, vColums);

		examTable = new JTable(model) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Determine editor to be used by row
			public TableCellEditor getCellEditor(int row, int column) {

				if (column == 2
						&& pregnancyexams.get(row).getExamValues().length() > 0) {
					String[] arr = pregnancyexams.get(row).getExamValues()
							.split(";");
					ArrayList<String> val = new ArrayList<String>();
					val.add("");
					for (int a = 0; a < arr.length; a++) {
						val.add(arr[a].trim());
					}

					JComboBox cellcombo = new JComboBox(val.toArray());
					DefaultCellEditor celleditor = new DefaultCellEditor(
							cellcombo);
					return celleditor;
				}

				else
					return super.getCellEditor(row, column);
			}

		};

		for (int i = 0; i < vColums.length; i++) {
			examTable.getColumnModel().getColumn(i)
					.setPreferredWidth(vColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i < vColumwidth.length; i++) {
			tableWidth += vColumwidth[i];
		}
		examscrollpane = new JScrollPane(examTable);

		examscrollpane.setPreferredSize(new Dimension(tableWidth + 280, 280));
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(examscrollpane);
		if (pregnancyvisit.getType() == -1)
			panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
					.getMessage("angal.pregnancy.prenatalexam")));
		else
			panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
					.getMessage("angal.pregnancy.postnatalexam")));
		return panel;
	}

	private JPanel getNotePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		notearea = new JTextArea();
		notearea.setLineWrap(true);
		notearea.setText(pregnancyvisit.getNote());
		panel.setPreferredSize(new Dimension(400, 100));

		panel.add(notearea, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.pregnancynote")));
		return panel;
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getButtonSave());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}

	private JButton getButtonSave() {
		JButton buttonSave = new JButton(
				MessageBundle.getMessage("angal.pregnancy.ok"));
		buttonSave.setMnemonic(KeyEvent.VK_T);
		buttonSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {

				ArrayList<PregnancyExamResult> nonLabResultsToInsert = new ArrayList<PregnancyExamResult>();
				pregnancy.setPregnancynr((Integer) pregnancyNrBox
						.getSelectedItem());
				pregnancy.setLmp((GregorianCalendar) lmpDateChooser
						.getCalendar());
				pregnancy
						.setScheduled_delivery((GregorianCalendar) scheduledDeliveryDateChooser
								.getCalendar());
				int origpregnr = pregnancyvisit.getPregnancyNr();
				for (int v = 0; v < pregnancyvisits.size(); v++) {
					if (pregnancyvisits.get(v).getPregnancyNr() == origpregnr)
						pregnancyvisits.get(v).setPregnancyNr(
								pregnancy.getPregnancynr());
				}
				///visitDateChooser controle
				try{
					Date date = visitDateChooser.getDate();
					if(date==null){
						JOptionPane.showMessageDialog(PregnancyEdit.this,
								MessageBundle.getMessage("angal.pregedit.pleaseinsertadate"),
								"",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}catch(Exception e){}
				///
				pregnancyvisit.setNote(notearea.getText());
				pregnancyvisit.setTime(visitDateChooser.getDate());
				pregnancyvisit.set(Calendar.HOUR_OF_DAY,
						(Integer) visitHours.getSelectedItem());
				pregnancyvisit.set(Calendar.MINUTE,
						(Integer) visitMinutes.getSelectedItem());
				pregnancyvisit.set(GregorianCalendar.SECOND, 0);
				if (nextvisitDateChooser.getDate() != null) {
					GregorianCalendar calendar = (GregorianCalendar) nextvisitDateChooser
							.getCalendar();
					calendar.set(Calendar.HOUR_OF_DAY,
							(Integer) nextvisitHours.getSelectedItem());
					calendar.set(Calendar.MINUTE,
							(Integer) nextvisitMinutes.getSelectedItem());
					calendar.set(GregorianCalendar.SECOND, 0);
					////controle nextvisitDateChooser
					Date dateVisit = visitDateChooser.getDate();
					Date nextVistDate = nextvisitDateChooser.getDate() ;
					if(nextVistDate.compareTo(dateVisit) < 0){
						JOptionPane.showMessageDialog(PregnancyEdit.this,
								MessageBundle.getMessage("angal.pregedit.notpasseddate"),
								"",
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					////
					pregnancyvisit.setNextVisitdate1(calendar);

				} else {
					pregnancyvisit.setNextVisitdate1(null);
				}

				if (treatmTypeBox.getSelectedIndex() > 0) {
					pregnancyvisit
							.setTreatmenttype(((PregnantTreatmentType) treatmTypeBox
									.getSelectedItem()).getCode());
				} else
					pregnancyvisit.setTreatmenttype(null);
				if (!bloodgroupBox.getSelectedItem().toString()
						.equals(pPatient.getBloodType())) {
					patientmanager = new PatientBrowserManager();
					pPatient.setBloodType(bloodgroupBox.getSelectedItem()
							.toString());
					patientmanager.updatePatient(pPatient);
				}

				for (int a = 0; a < pregnancyexams.size(); a++) {
					PregnancyExam exam = pregnancyexams.get(a);
					String outcome = (String) examTable.getModel().getValueAt(
							a, 2);
					if (examoutcomes.containsKey(exam.getExamId())) {
						PregnancyExamResult r = examoutcomes.get(exam
								.getExamId());
						r.setOutcome(outcome);
						manager.updateExamResult(pregnancyvisit.getVisitId(), r);
					} else {
						if (outcome != null && outcome.length() > 0) {
							PregnancyExamResult r = new PregnancyExamResult(
									pregnancyvisit.getVisitId(), exam
											.getExamId(), outcome);
							nonLabResultsToInsert.add(r);
						}
					}
				}
				if (newVisit) {

					if (newPregnancy) {
						int pid = manager.newPregnancy(pregnancy);
						pregnancy.setPregId(pid);
					} else
						manager.updatePregnancy(pregnancy);
					pregnancyvisit.setPregnancId(pregnancy.getPregId());
					int insertid = manager.newVisit(pregnancyvisit);
					pregnancyvisit.setVisitId(insertid);

					if (nonLabResultsToInsert.size() > 0)
						manager.newExamOutcomes(pregnancyvisit.getVisitId(),
								nonLabResultsToInsert);
					firePregnancyInserted(pregnancyvisit);

				} else {
					manager.updatePregnancy(pregnancy);
					manager.updateVisit(pregnancyvisit);

					if (nonLabResultsToInsert.size() > 0)
						manager.newExamOutcomes(pregnancyvisit.getVisitId(),
								nonLabResultsToInsert);
					firePregnancyUpdated(pregnancyvisit);
				}
				dispose();
			}
		});
		return buttonSave;

	}

	private JPanel getPregnancyNrPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		int startNumber = 1;
		int endNumber = 20;
		ArrayList<Integer> possiblePregnancyNumber = new ArrayList<Integer>();
		if (newPregnancy) {
			if (patientsPregnancies.size() > 0)
				startNumber = patientsPregnancies.get(0).getPregnancynr() + 1;
		} else {
			Pregnancy previouspreg = null;
			Pregnancy preg = null;
			Pregnancy nextpreg = null;
			for (int v = 0; v < patientsPregnancies.size(); v++) {
				preg = patientsPregnancies.get(v);
				if (pregnancyvisit.getPregnancyNr() == preg.getPregnancynr()) {
					if (v > 0)
						nextpreg = patientsPregnancies.get(v - 1);
					if (patientsPregnancies.size() > v + 1)
						previouspreg = patientsPregnancies.get(v + 1);
					break;
				}
			}
			if (previouspreg != null)
				startNumber = previouspreg.getPregnancynr() + 1;
			if (nextpreg != null)
				endNumber = nextpreg.getPregnancynr() - 1;

		}
		for (int a = startNumber; a <= endNumber; a++) {
			possiblePregnancyNumber.add(a);
		}

		pregnancyNrBox = new JComboBox(possiblePregnancyNumber.toArray());
		if (fromAdmission || (newVisit && !newPregnancy)) {
			pregnancyNrBox.setSelectedItem(pregnancyvisit.getPregnancyNr());
			pregnancyNrBox.setEnabled(false);

		} else if (!newVisit && !newPregnancy)
			pregnancyNrBox.setSelectedItem(pregnancyvisit.getPregnancyNr());

		panel.add(pregnancyNrBox);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.pregnancynumber")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}

	private JPanel getBloodgroupPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		String[] bloodgroups = new String[] { "", "0-", "0+", "A-", "A+", "B-",
				"B+", "AB-", "AB+" };
		bloodgroupBox = new JComboBox(bloodgroups);
		bloodgroupBox.setSelectedIndex(0);
		if (pPatient.getBloodType() != null
				&& pPatient.getBloodType().length() > 0) {
			for (int a = 1; a < bloodgroups.length; a++) {
				if (bloodgroups[a].equals(pPatient.getBloodType())) {
					bloodgroupBox.setSelectedItem(bloodgroups[a]);
					break;
				}
			}
		}
		bloodgroupBox.setPreferredSize(new Dimension(155, 20));
		panel.add(bloodgroupBox, BorderLayout.WEST);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.admission.bloodtype")));
		panel.setPreferredSize(new Dimension(175, 60));
		return panel;
	}

	private JPanel getVisitDatePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(315, 30));
		visitDateChooser = new JDateChooser(pregnancyvisit.getTime(),
				"dd/MM/yy");
		visitDateChooser.setMaxSelectableDate(today.getTime());
		visitDateChooser.setLocale(new Locale(Param.string("LANGUAGE")));
		visitDateChooser.setDateFormatString("dd/MM/yy");
		visitDateChooser.setPreferredSize(new Dimension(120, 20));
		visitHours = new JComboBox(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 });
		visitMinutes = new JComboBox(new Integer[] { 0, 10, 20, 30, 40, 50 });
		int minute = (pregnancyvisit.get(Calendar.MINUTE) / 10) * 10;
		visitHours.setSelectedItem(pregnancyvisit.get(Calendar.HOUR_OF_DAY));
		visitMinutes.setSelectedItem(minute);
		JLabel hourlabel = new JLabel("h");
		JLabel minlabel = new JLabel("m");
		panel.add(visitDateChooser);
		panel.add(hourlabel);
		panel.add(visitHours);
		panel.add(minlabel);
		panel.add(visitMinutes);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.visitdate")));

		
		return panel;
	}

	private JPanel getNextVisitDatePanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Date myDate = null;
		if (pregnancyvisit.getNextVisitdate() != null) {
			myDate = pregnancyvisit.getNextVisitdate().getTime();
		}
		nextvisitDateChooser = new JDateChooser(myDate, "dd/MM/yy");
		nextvisitDateChooser.setLocale(new Locale(Param.string("LANGUAGE")));
		nextvisitDateChooser.setDateFormatString("dd/MM/yy");
		nextvisitDateChooser.setPreferredSize(new Dimension(120, 20));

		nextvisitMinutes = new JComboBox(
				new Integer[] { 0, 10, 20, 30, 40, 50 });
		nextvisitHours = new JComboBox(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 });
		nextvisitMinutes = new JComboBox(
				new Integer[] { 0, 10, 20, 30, 40, 50 });
		if (pregnancyvisit.getNextVisitdate() != null) {
			nextvisitHours.setSelectedItem(pregnancyvisit.getNextVisitdate()
					.get(Calendar.HOUR_OF_DAY));
			int minute = (pregnancyvisit.getNextVisitdate().get(
					Calendar.MINUTE) / 10) * 10;
			nextvisitMinutes.setSelectedItem(minute);
		}
		JLabel hourlabel = new JLabel("h");
		JLabel minlabel = new JLabel("m");
		panel.add(nextvisitDateChooser);
		panel.add(hourlabel);
		panel.add(nextvisitHours);
		panel.add(minlabel);
		panel.add(nextvisitMinutes);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.nextvisitdate")));

		return panel;
	}

	private JPanel getTreatmentPanel() {
		treatmTypeBox = new JComboBox();
		treatmTypeBox.addItem("");
		JPanel treatmentPanel = new JPanel();
		PregnantTreatmentTypeBrowserManager abm = new PregnantTreatmentTypeBrowserManager();
		treatmTypeList = abm.getPregnantTreatmentType();
		for (PregnantTreatmentType elem : treatmTypeList) {
			treatmTypeBox.addItem(elem);
			if (pregnancyvisit.getTreatmenttype() != null
					&& pregnancyvisit.getTreatmenttype().equals(elem.getCode()))
				treatmTypeBox.setSelectedItem(elem);
		}
		GridBagLayout gbl_treatmentPanel = new GridBagLayout();
		gbl_treatmentPanel.columnWidths = new int[] {142, 0};
		gbl_treatmentPanel.rowHeights = new int[]{22, 0};
		gbl_treatmentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_treatmentPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		treatmentPanel.setLayout(gbl_treatmentPanel);
//		treatmentPanel.setPreferredSize(new Dimension(210, 20));
//		treatmentPanel.setPreferredSize(new Dimension(230, 60));
		treatmentPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.admission.treatmenttype")));
		
		GridBagConstraints gbc_treatmTypeBox = new GridBagConstraints();
		gbc_treatmTypeBox.fill = GridBagConstraints.BOTH;
		gbc_treatmTypeBox.gridx = 0;
		gbc_treatmTypeBox.gridy = 0;
		treatmentPanel.add(treatmTypeBox, gbc_treatmTypeBox);
		return treatmentPanel;
	}

	private JPanel getLmpDatePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		lmpDateChooser = new JDateChooser(pregnancy.getLmp().getTime(),
				"dd/MM/yy");
		lmpDateChooser.setMaxSelectableDate(today.getTime());
		lmpDateChooser.setLocale(new Locale(Param.string("LANGUAGE")));
		lmpDateChooser.setDateFormatString("dd/MM/yy");
		if (pregnancyvisit.getType() == 1 || fromAdmission) {
			lmpDateChooser.setEnabled(false);

		}
		lmpDateChooser.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (scheduledDeliveryDateChooser != null) {
					Calendar c = lmpDateChooser.getCalendar();
					c.add(2, 9);
					//added
					if(pregnancy.getScheduled_delivery()!=null){
						c = pregnancy.getScheduled_delivery();
					}
					//
					scheduledDeliveryDateChooser.setDate(c.getTime());
				}

			}
		});
		panel.add(lmpDateChooser);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.lmpdate")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}

	private JPanel getScheduledDeliveryDatePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		scheduledDeliveryDateChooser = new JDateChooser(pregnancy
				.getScheduled_delivery().getTime(), "dd/MM/yy");
		scheduledDeliveryDateChooser.setMinSelectableDate(today.getTime());
		scheduledDeliveryDateChooser
				.setLocale(new Locale(Param.string("LANGUAGE")));
		scheduledDeliveryDateChooser.setDateFormatString("dd/MM/yy");
		if (fromAdmission || pregnancyvisit.getType() == 1) {
			scheduledDeliveryDateChooser.setEnabled(false);

		}
		panel.add(scheduledDeliveryDateChooser);
		panel.setBorder(BorderFactory.createTitledBorder(MessageBundle
				.getMessage("angal.pregnancy.scheduleddelivery")));
		panel.setPreferredSize(new Dimension(200, 60));
		return panel;
	}

	private JButton getButtonClose() {
		JButton buttonClose = new JButton(
				MessageBundle.getMessage("angal.pregnancy.close"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		return buttonClose;

	}

}
