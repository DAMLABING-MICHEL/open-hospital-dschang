package org.isf.stat.reportlauncher.gui;

/*--------------------------------------------------------
 * ReportLauncher - lancia tutti i report che come parametri hanno
 * 					anno e mese
 * 					la classe prevede l'inizializzazione attraverso 
 *                  anno, mese, nome del report (senza .jasper)
 *---------------------------------------------------------
 * modification history
 * 01/01/2006 - rick - prima versione. lancia HMIS1081 e HMIS1081 
 * 11/11/2006 - ross - resa barbaramente generica (ad angal)
 * 16/11/2014 - eppesuig - show WAIT_CURSOR during generateReport()
 *-----------------------------------------------------------------*/

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.model.AgeType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.parameters.manager.Param;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportMY;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoDateTextField;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.isf.xmpp.gui.CommunicationFrame;
import org.isf.xmpp.manager.Interaction;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ReportLauncher extends ModalJFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pfrmExactWidth = 500;
	private int pfrmExactHeight = 165;
	private int pfrmBordX;
	private int pfrmBordY;
	private JPanel jPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jCloseButton = null;
	private JPanel jContentPanel = null;
	private JButton jOkButton = null;
	private JButton jCSVButton = null;
	private JPanel jMonthPanel = null;
	private JLabel jMonthLabel = null;
	private JComboBox jMonthComboBox = null;
	private JLabel jYearLabel = null;
	private JComboBox jYearComboBox = null;
	private JLabel jFromDateLabel = null;
	private JLabel jToDateLabel = null;
	private VoDateTextField jToDateField = null;
	private VoDateTextField jFromDateField = null;
	private int height = 0;
	private ButtonGroup group = new ButtonGroup();
	
	private JLabel jRptLabel = null;
	private JComboBox jRptComboBox = null;
	
	private final int BUNDLE = 0;
	private final int FILENAME = 1;
	private final int TYPE = 2;
	
	private String[][] reportMatrix = {
		{"angal.stat.registeredpatient", 				"OH001_RegisteredPatients", 										"twodates"},
		{"angal.stat.registeredpatientbyprovenance", 	"OH002_RegisteredPatientsByProvenance", 							"twodates"},
		{"angal.stat.registeredpatientbyageandsex", 	"OH003_RegisteredPatientsByAgeAndSex", 								"twodates"},
		{"angal.stat.incomesallbypricecodes", 			"OH004_IncomesAllByPriceCodes", 									"twodates"},
		{"angal.stat.incomesallbypricecodes_1", 		"OH004_IncomesAllByPriceCodes_closed_&_opened", 					"twodates"},
		
		{"angal.stat.incomesallbypricecodes_2", 		"OH004_IncomesAllByPriceCodes_3", 									"year"},
		{"angal.stat.incomesallbypricecodes_3", 		"OH004_IncomesAllByPriceCodes_3_closed_&_opened", 					"year"},
		{"angal.stat.outpatientcount", 					"OH005_opd_count_monthly_report", 									"monthyear"},
		{"angal.stat.outpatientdiagnoses", 				"OH006_opd_dis_monthly_report", 									"monthyear"},
		{"angal.stat.labmonthlybasic", 					"OH007_lab_monthly_report", 										"monthyear"},
		{"angal.stat.labsummaryforopd", 				"OH008_lab_summary_for_opd", 										"monthyear"},
		{"angal.stat.inpatientreport", 					"OH009_InPatientReport", 											"twodates"},
		{"angal.stat.outpatientreport", 				"OH010_OutPatientReport", 											"twodates"},
		{"angal.stat.pageonecensusinfo", 				"hmis108_cover", 													"twodatesfrommonthyear"},
		{"angal.stat.pageonereferrals", 				"hmis108_referrals", 												"monthyear"},
		{"angal.stat.pageoneoperations", 				"hmis108_operations", 												"monthyear"},
		{"angal.stat.inpatientdiagnosisin", 			"hmis108_adm_by_diagnosis_in", 										"monthyear"},
		{"angal.stat.inpatientdiagnosisout", 			"hmis108_adm_by_diagnosis_out", 									"monthyear"},
		{"angal.stat.opdattendance", 					"hmis105_opd_attendance", 											"monthyear"},
		{"angal.stat.opdreferrals", 					"hmis105_opd_referrals", 											"monthyear"},
		{"angal.stat.opdbydiagnosis", 					"hmis105_opd_by_diagnosis", 										"monthyear"},
		{"angal.stat.labmonthlyformatted", 				"hmis055b_lab_monthly_formatted", 									"monthyear"},
		{"angal.stat.weeklyepidemsurveil", 				"hmis033_weekly_epid_surv", 										"twodates"},
		{"angal.stat.weeklyepidemsurveilunder5", 		"hmis033_weekly_epid_surv_under_5", 								"twodates"},
		{"angal.stat.weeklyepidemsurveilover5", 		"hmis033_weekly_epid_surv_over_5", 									"twodates"},
		{"angal.stat.monthlyworkloadreportpage1", 		"MOH717_Monthly_Workload_Report_for_Hospitals_page1", 				"monthyear"},
		{"angal.stat.monthlyworkloadreportpage2", 		"MOH717_Monthly_Workload_Report_for_Hospitals_page2", 				"monthyear"},
		{"angal.stat.dailyopdmorbiditysummaryunder5", 	"MOH705A_Under_5_Years_Daily_Outpatient_Morbidity_Summary_Sheet", 	"monthyear"},
		{"angal.stat.dailyopdmorbiditysummaryover5", 	"MOH705B_Over_5_Years_Daily_Outpatient_Morbidity_Summary_Sheet", 	"monthyear"},
		{"angal.stat.pregnancycare", 	                "PregnancyCareReport", 	  											"twodates"},
		{"angal.stat.periodopdreport", 	                "opdHistory", 	  											        "twodatesmore"},
		{"angal.stat.synthesisActivitiesbyyear", 		"synthesisActivities", 		   							                "year"},
		{"angal.stat.outpatient.register", 	            "Outpatient_Register", 	  											"twodates"},
		{"angal.stat.admissionpatient.register.title",  "AdmissionPatient_Register", 	  	         						"twodates"},
		{"angal.stat.cpn.register",                     "CpnPatient_Register", 	        	         						"twodates"},
		{"angal.stat.lab.register",                     "Laboratory_Register", 	        	         						"twodates"},
		//{"angal.stat.admissionpatient.register.test",   "test_Register", 	  	         						"twodates"},
		{"angal.stat.corpsesliftedperperiod", 			"CorpsesLiftedPerPeriodOfTime", 									"twodates"},
		{"angal.stat.corpsestobeliftedperperiod", 		"CorpsesToBeLiftedPerPeriodOfTime", 								"twodates"},
	};
	
	private JComboBox shareWith=null;//nicola
	Interaction userOh=null;
	private JPanel panelMoreChoose;
	private JPanel rep1_1;
	private JRadioButton newCaseOnlyRadio;
	private JRadioButton oldCaseOnlyRadio;
	private JRadioButton newAndOldRadio;
	private JComboBox comboBoxAgeRange;
	private JLabel rangeAgeLabel;
	private JPanel panelAgeRange;
	private JLabel labelFrom;
	private JSpinner spinnerFrom;
	private JComboBox comboUnitFrom;
	private JLabel labelTo;
	private JSpinner spinnerTo;
	private JComboBox comboUnitTo;

	
	
//	private final JFrame myFrame;
	
	/**
	 * This is the default constructor
	 */
	public ReportLauncher() {
		
		super();
//		myFrame = this;
		this.setResizable(true);
		initialize();
		setVisible(true);
		//modal exclude
		
	}

	/**
	 * This method initializes this	
	 * 	
	 * @return void	
	 */
	private void initialize() {
		this.setTitle(MessageBundle.getMessage("angal.stat.reportlauncher"));
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		pfrmBordX = (screensize.width / 3) - (pfrmExactWidth / 2);
		pfrmBordY = (screensize.height / 3) - (pfrmExactHeight / 2);
		this.setBounds(pfrmBordX,pfrmBordY,pfrmExactWidth,pfrmExactHeight);
		this.setContentPane(getJPanel());
		selectAction();
		pack();
		height = this.getHeight();
		if(!Param.bool("WITHMODALWINDOW")){
			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		}
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJButtonPanel(), BorderLayout.SOUTH);
			jPanel.add(getJContentPanel(), BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.setLayout(new FlowLayout());
//			if(Param.bool("XMPPMODULEENABLED"))
//				jButtonPanel.add(getComboShareReport(),null);
			jButtonPanel.add(getJOkButton(), null);
			jButtonPanel.add(getJCSVButton(), null);
			//jButtonPanel.add(getJShareButton(),null);
			jButtonPanel.add(getJCloseButton(), null);
			
		}
		return jButtonPanel;
	}

	private JComboBox getComboShareReport() {
		userOh= new Interaction();
		Collection<String> contacts = userOh.getContactOnline();
		contacts.add("-- Share report with : Nobody --");
		shareWith = new JComboBox(contacts.toArray());
		shareWith.setSelectedItem("-- Share report with : Nobody --");
		return shareWith;
	}

	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(MessageBundle.getMessage("angal.common.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}

	/**
	 * This method initializes jContentPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPanel() {
		if (jContentPanel == null) {
			
			jContentPanel = new JPanel();
			jContentPanel.setLayout(new BorderLayout());
			
			JPanel rep1;
			rep1_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

			rep1_1.add(getJParameterSelectionPanel());
			rep1_1 = setMyBorder(rep1_1, MessageBundle.getMessage("angal.stat.parametersselectionframe") + " ");
			
			jContentPanel.add(rep1_1, BorderLayout.NORTH);
//			jContentPanel.add(getPanelAgeRange(), BorderLayout.WEST);
			jContentPanel.add(getPanelMoreChoose(), BorderLayout.SOUTH);
			//jContentPanel.add(rep2, BorderLayout.SOUTH);			
				
		}
		return jContentPanel;
	}

	
	
	private JPanel getJParameterSelectionPanel() {

		if (jMonthPanel == null) {

			jMonthPanel = new JPanel();
			jMonthPanel.setLayout(new FlowLayout());
			
			//final DateFormat dtf = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
			//String dt = dtf.format(new java.util.Date());
			//Integer month = Integer.parseInt(dt.substring(3, 5));
			//Integer year = 2000 + Integer.parseInt(dt.substring(6, 8));

			java.util.GregorianCalendar gc = TimeTools.getServerDateTime();
			Integer month=gc.get(Calendar.MONTH);
			Integer year = gc.get(Calendar.YEAR);

			//System.out.println("m="+month +",y="+ year);
			
			jRptLabel = new JLabel();
			jRptLabel.setText(MessageBundle.getMessage("angal.stat.report"));
			
			
			jRptComboBox = new JComboBox();
			for (int i=0;i<reportMatrix.length;i++)
				jRptComboBox.addItem(MessageBundle.getMessage(reportMatrix[i][BUNDLE]));
			
			jRptComboBox.addActionListener(new ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (e.getActionCommand()!= null) {
						if (e.getActionCommand().equalsIgnoreCase("comboBoxChanged")) {
							selectAction();
						}
					}
				}
			});
			
			
			jMonthLabel = new JLabel();
			jMonthLabel.setText("        " + MessageBundle.getMessage("angal.stat.month"));
			
			jMonthComboBox = new JComboBox();
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.january"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.february"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.march"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.april"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.may"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.june"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.july"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.august"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.september"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.october"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.november"));
			jMonthComboBox.addItem(MessageBundle.getMessage("angal.stat.december"));

			jMonthComboBox.setSelectedIndex(month);

			jYearLabel = new JLabel();
			jYearLabel.setText("        " + MessageBundle.getMessage("angal.stat.year"));
			jYearComboBox = new JComboBox();

			for (int i=0;i<4;i++){
				jYearComboBox.addItem((year-i)+"");
			}
			
			jFromDateLabel = new JLabel();
			jFromDateLabel.setText(MessageBundle.getMessage("angal.stat.fromdate"));
			GregorianCalendar defaultDate = TimeTools.getServerDateTime();
			defaultDate.add(GregorianCalendar.DAY_OF_MONTH, -8);
			jFromDateField = new VoDateTextField("dd/mm/yyyy", defaultDate, 10);
			jToDateLabel = new JLabel();
			jToDateLabel.setText(MessageBundle.getMessage("angal.stat.todate"));
			defaultDate.add(GregorianCalendar.DAY_OF_MONTH, 7);
			jToDateField = new VoDateTextField("dd/mm/yyyy", defaultDate, 10);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			
			//jMonthPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
			jMonthPanel.add(jRptLabel, null);
			jMonthPanel.add(jRptComboBox, null);
			jMonthPanel.add(jMonthLabel, null);
			jMonthPanel.add(jMonthComboBox, null);
			jMonthPanel.add(jYearLabel, null);
			jMonthPanel.add(jYearComboBox, null);
			jMonthPanel.add(jFromDateLabel, null);
			jMonthPanel.add(jFromDateField, null);
			jMonthPanel.add(jToDateLabel, null);
			jMonthPanel.add(jToDateField, null);
		}
		return jMonthPanel;
	}


	protected void selectAction() {
		String sParType="";
		int rptIndex=jRptComboBox.getSelectedIndex();
		sParType = reportMatrix[rptIndex][TYPE];
		if (sParType.equalsIgnoreCase("twodates")) {
			jMonthComboBox.setVisible(false);
			jMonthLabel.setVisible(false);
			jYearComboBox.setVisible(false);
			jYearLabel.setVisible(false);
			jFromDateLabel.setVisible(true);
			jFromDateField.setVisible(true);
			jToDateLabel.setVisible(true);
			jToDateField.setVisible(true);
			panelMoreChoose.setVisible(false);
			//panelAgeRange.setVisible(false);
			this.setSize(this.getWidth(), height);
		}
		if (sParType.equalsIgnoreCase("twodatesmore")) {
			jMonthComboBox.setVisible(false);
			jMonthLabel.setVisible(false);
			jYearComboBox.setVisible(false);
			jYearLabel.setVisible(false);
			jFromDateLabel.setVisible(true);
			jFromDateField.setVisible(true);
			jToDateLabel.setVisible(true);
			jToDateField.setVisible(true);
			panelMoreChoose.setVisible(true);
			//panelAgeRange.setVisible(true);
			this.setSize(this.getWidth(), height +30);
		}
		if (sParType.equalsIgnoreCase("twodatesfrommonthyear")) {
			jMonthComboBox.setVisible(true);
			jMonthLabel.setVisible(true);
			jYearComboBox.setVisible(true);
			jYearLabel.setVisible(true);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			panelMoreChoose.setVisible(false);
			//panelAgeRange.setVisible(false);
			this.setSize(this.getWidth(), height);
		}
		if (sParType.equalsIgnoreCase("monthyear")) {
			jMonthComboBox.setVisible(true);
			jMonthLabel.setVisible(true);
			jYearComboBox.setVisible(true);
			jYearLabel.setVisible(true);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			panelMoreChoose.setVisible(false);
			//panelAgeRange.setVisible(false);
			this.setSize(this.getWidth(), height);
		}
		if (sParType.equalsIgnoreCase("year")) {
			jMonthComboBox.setVisible(false);
			jMonthLabel.setVisible(false);
			jYearComboBox.setVisible(true);
			jYearLabel.setVisible(true);
			jFromDateLabel.setVisible(false);
			jFromDateField.setVisible(false);
			jToDateLabel.setVisible(false);
			jToDateField.setVisible(false);
			panelMoreChoose.setVisible(false);
			//panelAgeRange.setVisible(false);
			this.setSize(this.getWidth(), height);
		}
		jCSVButton.setVisible(true);
		if(rptIndex == 9 || rptIndex == 2) {
			jCSVButton.setVisible(false);
		}
	}

	private JButton getJOkButton() {
		if (jOkButton == null) {
			jOkButton = new JButton();
			jOkButton.setBounds(new Rectangle(15, 15, 91, 31));
			jOkButton.setText(MessageBundle.getMessage("angal.stat.launchreport"));
			
			jOkButton.addActionListener(new ActionListener() {   
				public void actionPerformed(ActionEvent e) {
					BusyState.setBusyState(ReportLauncher.this, true);
					generateReport(false);
				BusyState.setBusyState(ReportLauncher.this, false);
				}
			});
		}
		return jOkButton;
	}
	
	private JButton getJCSVButton() {
		if (jCSVButton == null) {
			jCSVButton = new JButton();
			jCSVButton.setBounds(new Rectangle(15, 15, 91, 31));
			jCSVButton.setText("Excel");
			jCSVButton.addActionListener(new ActionListener() {   
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					BusyState.setBusyState(ReportLauncher.this, true);
					generateReport(true);
					BusyState.setBusyState(ReportLauncher.this, false);
				}
			});
		}
		return jCSVButton;
	}
	
	protected void generateReport(boolean toCSV) { //toCSV==TRUE==>EXCEL, toCSV==FALSE==>CSV
		   
		int rptIndex=jRptComboBox.getSelectedIndex();
		Integer month = jMonthComboBox.getSelectedIndex()+1;
		Integer year = (Integer.parseInt((String)jYearComboBox.getSelectedItem()));
		String fromDate=jFromDateField.getText().trim();
		String toDate=jToDateField.getText().trim();
		
		////////////////////////////////////////
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
		//String fromDate = sdf.format(dateFrom.getTime());
		//toDate = sdf.format(dateTo.getTime());
		fromDate=sdf.format(jFromDateField.getDate().getTime());
		toDate= sdf.format(jToDateField.getDate().getTime());
		if (rptIndex>=0) {
			String sParType = reportMatrix[rptIndex][TYPE];			
			if (sParType.equalsIgnoreCase("twodates")) {
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH001_RegisteredPatients")){
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], "OH001");
					return;
				}
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH003_RegisteredPatientsByAgeAndSex")){
					//new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], "OH003");
					return;
				}
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH004_IncomesAllByPriceCodes")){
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], "OH004");
					return;
				}
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH004_IncomesAllByPriceCodes_closed_&_opened")){
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], "OH004_1");
					return;
				}
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH002_RegisteredPatientsByProvenance")){
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], "OH002");
					return;
				}
				if(reportMatrix[rptIndex][FILENAME].equals("OH004_IncomesAllByPriceCodes")){
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], toCSV,"C","Nothing","angal.report.oh004incomesallbypricecodes.title");
					return;
				}
				if(reportMatrix[rptIndex][FILENAME].equals("OH004_IncomesAllByPriceCodes_closed_&_opened")){
					new GenericReportFromDateToDate(fromDate, toDate, "OH004_IncomesAllByPriceCodes", toCSV,"C","O","angal.report.oh004incomesallbypricecodes.title_1");
					return;
				}
				
				new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], toCSV);
			}
			if (sParType.equalsIgnoreCase("twodatesmore")) {
				Boolean onlyNew =   newCaseOnlyRadio.isSelected();
				Boolean onlyOld =   oldCaseOnlyRadio.isSelected();
				Boolean newAndOld = newAndOldRadio.isSelected();
				String Option = "";
				String Option1 = "";
				String rangeLibMin = "";
				String rangeLibMax = "";
				String rangeLibFinal= "";
				String caseType= MessageBundle.getMessage("angal.report.opdhistory.casetype");
				
				int ageRangeMax = 3600;
				int ageRangeMin = 0;
				int fromValue = (Integer) spinnerFrom.getValue();
				int toValue = (Integer) spinnerTo.getValue();
				int unitFrom = comboUnitFrom.getSelectedIndex();
				int unitTo = comboUnitTo.getSelectedIndex();
				AgeType agetype = new AgeType();
				ageRangeMin = unitFrom==0?fromValue:fromValue*12;
				ageRangeMax = unitTo==0?toValue:toValue*12;
				
				if(onlyNew) {
					Option = "N";
					Option1 = "";
					caseType=caseType+" "+MessageBundle.getMessage("angal.opdhistory.newcases");
				}
				if(onlyOld){ 
					Option = "R";
					Option1 = "";
					caseType=caseType+" "+MessageBundle.getMessage("angal.opdhistory.oldcases");
				}
				if(newAndOld){ 
					Option = "R";
					Option1 = "N";
					caseType=caseType+" "+MessageBundle.getMessage("angal.opdhistory.oldandnewscases");
				}
				if(ageRangeMin>=12){
					int rest = ageRangeMin%12;
					rangeLibMin = rest>0? (ageRangeMin/12)+" "+MessageBundle.getMessage("angal.medicals.year")+" "+rest+" "+MessageBundle.getMessage("angal.medicals.month"):(ageRangeMin/12)+" ans";
				}else{
					rangeLibMin = ageRangeMin+" "+MessageBundle.getMessage("angal.medicals.month");
				}
				
				if(ageRangeMax>=12){
					int rest = ageRangeMax%12;
					rangeLibMax = rest>0? (ageRangeMax/12)+" "+MessageBundle.getMessage("angal.medicals.year")+" "+rest+" "+MessageBundle.getMessage("angal.medicals.month"):(ageRangeMax/12)+" ans";
				}else{
					rangeLibMax = ageRangeMax+" "+MessageBundle.getMessage("angal.medicals.month");
				}
				if(ageRangeMax==0){
					ageRangeMax=3600;
					rangeLibFinal = "";
				}else{
					rangeLibFinal="Tranche d'age: "+rangeLibMin+" - "+rangeLibMax;
				}
				
				new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], toCSV,Option,Option1,ageRangeMin,ageRangeMax, rangeLibFinal, caseType);
			}
			
			if (sParType.equalsIgnoreCase("twodatesfrommonthyear")) {
				GregorianCalendar d = new GregorianCalendar();
				d.set(GregorianCalendar.DAY_OF_MONTH,1 );
				d.set(GregorianCalendar.MONTH, month-1);
				d.set(GregorianCalendar.YEAR, year);
				fromDate = sdf.format(d.getTime());
				d.set(GregorianCalendar.DAY_OF_MONTH, d.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
				toDate = sdf.format(d.getTime());
				new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], toCSV);
			}
			
			if (sParType.equalsIgnoreCase("monthyear")) {
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH005_opd_count_monthly_report")){
					new GenericReportMY(month, year, reportMatrix[rptIndex][FILENAME], toCSV, "OH005");
					return;
				}
				if(toCSV && reportMatrix[rptIndex][FILENAME].equals("OH006_opd_dis_monthly_report")){
					new GenericReportMY(month, year, reportMatrix[rptIndex][FILENAME], toCSV, "OH006");
					return;
				}
				new GenericReportMY(month, year, reportMatrix[rptIndex][FILENAME], toCSV);				
			}
			
			if (sParType.equalsIgnoreCase("year")) {
				if(reportMatrix[rptIndex][FILENAME].equals("OH004_IncomesAllByPriceCodes_3")){
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME],year,"C","angal.stat.incomesallbypricecodes_2_title", toCSV);
				}
				if(reportMatrix[rptIndex][FILENAME].equals("OH004_IncomesAllByPriceCodes_3_closed_&_opened")){
					
					new GenericReportFromDateToDate(fromDate, toDate, "OH004_IncomesAllByPriceCodes_3" ,year,"D","angal.stat.incomesallbypricecodes_3_title", toCSV);
				}
				if(reportMatrix[rptIndex][FILENAME].equals("synthesisActivities")){
					GregorianCalendar d = new GregorianCalendar();
					d.set(GregorianCalendar.DAY_OF_MONTH, 1);
					d.set(GregorianCalendar.MONTH, 1);
					d.set(GregorianCalendar.YEAR, year);
					fromDate = sdf.format(d.getTime());
					d.set(GregorianCalendar.DAY_OF_MONTH, 31);
					d.set(GregorianCalendar.MONTH, 12);
					fromDate = sdf.format(d.getTime());
					new GenericReportFromDateToDate(fromDate, toDate, reportMatrix[rptIndex][FILENAME], toCSV, year);
				}
			}
		}		
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

	private JPanel getPanelMoreChoose() {
		if (panelMoreChoose == null) {
			panelMoreChoose = new JPanel();
			panelMoreChoose.setVisible(false);
			panelMoreChoose.setBorder(new LineBorder(Color.GRAY, 1, true));
			GridBagLayout gbl_panelMoreChoose = new GridBagLayout();
			gbl_panelMoreChoose.columnWidths = new int[]{33, 10, 68, 28, 0, 20, 75, 10, 93, 81, 0, 0};
			gbl_panelMoreChoose.rowHeights = new int[]{23, 0};
			gbl_panelMoreChoose.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_panelMoreChoose.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panelMoreChoose.setLayout(gbl_panelMoreChoose);
			GridBagConstraints gbc_labelFrom = new GridBagConstraints();
			gbc_labelFrom.insets = new Insets(0, 0, 0, 5);
			gbc_labelFrom.gridx = 0;
			gbc_labelFrom.gridy = 0;
			panelMoreChoose.add(getLabelFrom(), gbc_labelFrom);
			GridBagConstraints gbc_spinnerFrom = new GridBagConstraints();
			gbc_spinnerFrom.fill = GridBagConstraints.HORIZONTAL;
			gbc_spinnerFrom.insets = new Insets(0, 0, 0, 5);
			gbc_spinnerFrom.gridx = 1;
			gbc_spinnerFrom.gridy = 0;
			panelMoreChoose.add(getSpinnerFrom(), gbc_spinnerFrom);
			GridBagConstraints gbc_comboUnitFrom = new GridBagConstraints();
			gbc_comboUnitFrom.insets = new Insets(0, 0, 0, 5);
			gbc_comboUnitFrom.gridx = 2;
			gbc_comboUnitFrom.gridy = 0;
			panelMoreChoose.add(getComboUnitFrom(), gbc_comboUnitFrom);
			GridBagConstraints gbc_labelTo = new GridBagConstraints();
			gbc_labelTo.insets = new Insets(0, 0, 0, 5);
			gbc_labelTo.gridx = 4;
			gbc_labelTo.gridy = 0;
			panelMoreChoose.add(getLabelTo(), gbc_labelTo);
			GridBagConstraints gbc_spinnerTo = new GridBagConstraints();
			gbc_spinnerTo.fill = GridBagConstraints.BOTH;
			gbc_spinnerTo.insets = new Insets(0, 0, 0, 5);
			gbc_spinnerTo.gridx = 5;
			gbc_spinnerTo.gridy = 0;
			panelMoreChoose.add(getSpinnerTo(), gbc_spinnerTo);
			GridBagConstraints gbc_comboUnitTo = new GridBagConstraints();
			gbc_comboUnitTo.insets = new Insets(0, 0, 0, 5);
			gbc_comboUnitTo.gridx = 6;
			gbc_comboUnitTo.gridy = 0;
			panelMoreChoose.add(getComboUnitTo(), gbc_comboUnitTo);
			GridBagConstraints gbc_newAndOldRadio = new GridBagConstraints();
			gbc_newAndOldRadio.anchor = GridBagConstraints.EAST;
			gbc_newAndOldRadio.insets = new Insets(0, 0, 0, 5);
			gbc_newAndOldRadio.gridx = 8;
			gbc_newAndOldRadio.gridy = 0;
			panelMoreChoose.add(getNewAndOldRadio(), gbc_newAndOldRadio);
			GridBagConstraints gbc_newCaseOnlyRadio = new GridBagConstraints();
			gbc_newCaseOnlyRadio.anchor = GridBagConstraints.EAST;
			gbc_newCaseOnlyRadio.insets = new Insets(0, 0, 0, 5);
			gbc_newCaseOnlyRadio.gridx = 9;
			gbc_newCaseOnlyRadio.gridy = 0;
			panelMoreChoose.add(getNewCaseOnlyRadio(), gbc_newCaseOnlyRadio);
			GridBagConstraints gbc_oldCaseOnlyRadio = new GridBagConstraints();
			gbc_oldCaseOnlyRadio.anchor = GridBagConstraints.EAST;
			gbc_oldCaseOnlyRadio.gridx = 10;
			gbc_oldCaseOnlyRadio.gridy = 0;
			panelMoreChoose.add(getOldCaseOnlyRadio(), gbc_oldCaseOnlyRadio);
			
			group.add(newCaseOnlyRadio);
			group.add(oldCaseOnlyRadio);
			group.add(newAndOldRadio);
		}
		return panelMoreChoose;
	}
	private JRadioButton getNewCaseOnlyRadio() {
		if (newCaseOnlyRadio == null) {
			newCaseOnlyRadio = new JRadioButton(MessageBundle.getMessage("angal.opdhistory.newcases"));
		}
		return newCaseOnlyRadio;
	}
	private JRadioButton getOldCaseOnlyRadio() {
		if (oldCaseOnlyRadio == null) {
			oldCaseOnlyRadio = new JRadioButton(MessageBundle.getMessage("angal.opdhistory.oldcases"));
		}
		return oldCaseOnlyRadio;
	}
	private JRadioButton getNewAndOldRadio() {
		if (newAndOldRadio == null) {
			newAndOldRadio = new JRadioButton(MessageBundle.getMessage("angal.opdhistory.oldandnewscases"));
			newAndOldRadio.setSelected(true);
		}
		return newAndOldRadio;
	}
	
	private JLabel getLabelFrom() {
		if (labelFrom == null) {
			labelFrom = new JLabel(MessageBundle.getMessage("angal.report.opdhistory.from"));
		}
		return labelFrom;
	}
	private JSpinner getSpinnerFrom() {
		if (spinnerFrom == null) {
			spinnerFrom = new JSpinner();
			spinnerFrom.setPreferredSize(new Dimension(75, 30));
		}
		return spinnerFrom;
	}
	private JComboBox getComboUnitFrom() {
		if (comboUnitFrom == null) {
			comboUnitFrom = new JComboBox();
			comboUnitFrom.addItem(MessageBundle.getMessage("angal.medicals.month"));
			comboUnitFrom.addItem(MessageBundle.getMessage("angal.medicals.year"));
		}
		return comboUnitFrom;
	}
	private JLabel getLabelTo() {
		if (labelTo == null) {
			labelTo = new JLabel(MessageBundle.getMessage("angal.report.opdhistory.at"));
		}
		return labelTo;
	}
	private JSpinner getSpinnerTo() {
		if (spinnerTo == null) {
			spinnerTo = new JSpinner();
			spinnerTo.setPreferredSize(new Dimension(75, 30));
		}
		return spinnerTo;
	}
	private JComboBox getComboUnitTo() {
		if (comboUnitTo == null) {
			comboUnitTo = new JComboBox();
			comboUnitTo.addItem(MessageBundle.getMessage("angal.medicals.month"));
			comboUnitTo.addItem(MessageBundle.getMessage("angal.medicals.year"));
		}
		return comboUnitTo;
	}
}  
