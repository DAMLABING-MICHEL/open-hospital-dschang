package org.isf.accounting.gui;

/**
 * Browsing of table BILLS
 * 
 * @author Mwithi
 * 
 */
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.isf.accounting.gui.BillRefund.PatientRefundBillListener;
import org.isf.accounting.gui.PatientBillEdit.PatientBillListener;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.Sage;
import org.isf.medicalstock.gui.MovStockBrowser;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.Price;
import org.isf.reduction.manager.ReductionPlanManager;
import org.isf.reduction.model.ReductionPlan;
import org.isf.stat.manager.GenericReportBill;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportUserInDate;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.jobjects.OhTableModel;
import org.isf.utils.sage.SageExporter;
import org.isf.utils.time.TimeTools;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.SystemColor;

public class BillBrowser extends ModalJFrame
		implements PatientBillListener, PatientRefundBillListener, SelectionListener {

	private static Logger logger = LoggerFactory.getLogger(MovStockBrowser.class);

	public void billInserted(AWTEvent event) {
		/*** send patient parameters if is set ******/
		if (patientParent != null) {
			chooseItem = null;
			garantUserChoose = null;
			comboGaranti.setSelectedItem(null);
			updateDataSet(dateFrom, dateTo, patientParent);
		} else {
			if (chooseItem != null) {
				patientParent = null;
				garantUserChoose = null;
				comboGaranti.setSelectedItem(null);
				updateDataSet(dateFrom, dateTo, chooseItem);
			} else if (garantUserChoose != null) {
				chooseItem = null;
				patientParent = null;
				updateDataSet(dateFrom, dateTo, garantUserChoose);
			} else {
				updateDataSet(dateFrom, dateTo);
			}
		}
		/*******/
		updateTables();
		updateTotals();
		if (event != null) {
			Bill billInserted = (Bill) event.getSource();
			if (billInserted != null) {
				int insertedId = billInserted.getId();
				for (int i = 0; i < jTableBills.getRowCount(); i++) {
					Bill aBill = (Bill) jTableBills.getModel().getValueAt(i, -1);
					if (aBill.getId() == insertedId)
						jTableBills.getSelectionModel().setSelectionInterval(i, i);
				}
			}
		}
	}

	public void refundBillInserted(AWTEvent event) {
		/*** send patient parameters if is set ******/
		if (patientParent != null) {
			chooseItem = null;
			garantUserChoose = null;
			comboGaranti.setSelectedItem(null);
			updateDataSet(dateFrom, dateTo, patientParent);
		} else {
			if (chooseItem != null) {
				patientParent = null;
				garantUserChoose = null;
				comboGaranti.setSelectedItem(null);
				updateDataSet(dateFrom, dateTo, chooseItem);
			} else if (garantUserChoose != null) {
				chooseItem = null;
				patientParent = null;
				updateDataSet(dateFrom, dateTo, garantUserChoose);
			} else {
				updateDataSet(dateFrom, dateTo);
			}
		}
		/*******/
		updateTables();
		updateTotals();
	}

	private static final long serialVersionUID = 1L;
	private JTabbedPane jTabbedPaneBills;
	private JTable jTableBills;
	private JScrollPane jScrollPaneBills;
	private JTable jTablePending;
	private JScrollPane jScrollPanePending;
	private JTable jTableClosed;
	private JTable jTableDeleted;
	private JScrollPane jScrollPaneClosed;
	private JScrollPane jScrollPaneDeleted;
	private JTable jTableToday;
	private JTable jTablePeriod;
	private JTable jTableUser;
	private JTable jTableUserRefund;
	private JTable jTableRefund;
	private JPanel jPanelRange;
	private JPanel jPanelButtons;
	private JPanel jPanelSouth;
	private JPanel jPanelTotals;
	private JButton jButtonNew;
	private JButton jButtonEdit;
	private JButton jButtonPrintReceipt;
	private JButton jButtonDelete;
	private JButton jButtonClose;
	private JButton jButtonReport;
	private JButton jButtonBack;
	@SuppressWarnings("rawtypes")
	private JComboBox jComboUsers;
	private JMonthChooser jComboBoxMonths;
	private JYearChooser jComboBoxYears;
	private JLabel jLabelTo;
	private JLabel jLabelFrom;
	private JDateChooser jCalendarTo;
	private JDateChooser jCalendarFrom;
	private GregorianCalendar dateFrom = TimeTools.getServerDateTime();
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private GregorianCalendar dateToday0 = TimeTools.getServerDateTime();
	private GregorianCalendar dateToday24 = TimeTools.getServerDateTime();

	private ArrayList<User> usersG;
	OhDefaultCellRenderer cellRenderer = new OhDefaultCellRenderer();

	private JButton jButtonToday;

	// private String status;
	private String[] columsNames = { MessageBundle.getMessage("angal.billbrowser.id"),
			MessageBundle.getMessage("angal.common.date"), MessageBundle.getMessage("angal.billbrowser.patientID"),
			MessageBundle.getMessage("angal.billbrowser.patient"), MessageBundle.getMessage("angal.billbrowser.amount"),
			MessageBundle.getMessage("angal.billbrowser.refunded"), MessageBundle.getMessage("angal.billbrowser.lastpayment"),
			MessageBundle.getMessage("angal.billbrowser.status"),
			MessageBundle.getMessage("angal.billbrowser.balance") };
	private int[] columsWidth = { 50, 120, 50, 50, 100, 100, 120, 50, 100 };
	private int[] maxWidth = { 100, 150, 100, 200, 100, 100, 100, 50, 100 };
	private boolean[] columsResizable = { false, false, false, true, false, false, false, false, false };
	private Class<?>[] columsClasses = { Integer.class, String.class, String.class, String.class, Double.class,
			Double.class, String.class, String.class, Double.class };
	private boolean[] alignCenter = { true, true, true, false, false, false, true, true, false };
	private boolean[] boldCenter = { true, false, false, false, false, false, false, false, false };

	private BigDecimal totalToday;
	private BigDecimal balanceToday;
	private BigDecimal totalPeriod;
	private BigDecimal balancePeriod;
	private BigDecimal userToday;
	private BigDecimal userPeriod;
	private BigDecimal userRefundToday;
	private BigDecimal userRefundPeriod;
	private BigDecimal refundToday;
	private BigDecimal refundPeriod;
	private int month;
	private int year;

	// Bills & Payments
	private BillBrowserManager billManager = new BillBrowserManager();
	private ArrayList<Bill> billPeriod;
	private HashMap<Integer, Bill> mapBill = new HashMap<Integer, Bill>();
	private ArrayList<BillPayments> paymentsPeriod;
	private ArrayList<Bill> billFromPayments;

	// Users
	private String user = MainMenu.getUser();
	private ArrayList<String> users = billManager.getUsers();
	private JButton exportSageButton;
	private JLabel lblNewLabel;
	private JButton jButtonSimulate;

	private Patient patientParent;
	private JButton jAffiliatePersonJButtonAdd = null;
	private JButton jAffiliatePersonJButtonSupp = null;
	private JTextField jAffiliatePersonJTextField = null;
	private JButton jButtonCloseBill;
	private JPanel panelChooseMedical;

	/***** for filtering medical ********/
	private BillItems chooseItem;
	private JButton chooseMedicalJButtonAdd = null;
	private JButton chooseMedicalJButtonSupp = null;
	private JLabel chooseMedicalJLabel = null;
	private JTextField medicalJTextField = null;
	private JPanel panelSupRange;
	private Image ico;
	private JLabel lblSpace;
	private JLabel lblChooseGaranti;
	private JComboBox<User> comboGaranti;
	/************************************/

	private User garantUserChoose;
	private JButton printGaranteButton;

	/**
	 * This variable temporally stores the selected bill, so that to perform any
	 * action on the bill, only use it without having to check all tables
	 */
	private Bill selectedBill = null;

	public BillBrowser() {
		updateDataSet();
		initComponents();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponents() {
		getContentPane().add(getJPanelRange(), BorderLayout.NORTH);
		getContentPane().add(getJTabbedPaneBills(), BorderLayout.CENTER);
		getContentPane().add(getJPanelSouth(), BorderLayout.SOUTH);
		setTitle(MessageBundle.getMessage("angal.billbrowser.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(800, 500));
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				billPeriod.clear();
				mapBill.clear();
				users.clear();
				dispose();
			}
		});
		pack();

		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
	}

	private JPanel getJPanelSouth() {
		if (jPanelSouth == null) {
			jPanelSouth = new JPanel();
			jPanelSouth.setLayout(new BoxLayout(jPanelSouth, BoxLayout.Y_AXIS));
			jPanelSouth.add(getJPanelTotals());
			jPanelSouth.add(getJPanelButtons());
		}
		return jPanelSouth;
	}

	private JPanel getJPanelTotals() {
		if (jPanelTotals == null) {
			jPanelTotals = new JPanel();

			jPanelTotals.setLayout(new BoxLayout(jPanelTotals, BoxLayout.Y_AXIS));
			jPanelTotals.add(getJTableToday());
			jPanelTotals.add(getJTablePeriod());
			if (!Param.bool("SINGLEUSER")) {
				jPanelTotals.add(getJTableUser());
				if (Param.bool("ENABLEMEDICALREFUND"))
					jPanelTotals.add(getJTableUserRefund());
			}

			if (Param.bool("ENABLEMEDICALREFUND"))
				jPanelTotals.add(getJTableRefund());
			
			updateTotals();
		}
		if (MainMenu.getCurrentUser().getUserGroupName().equals("admin")) {
			jPanelTotals.setVisible(true);
		} else {
			jPanelTotals.setVisible(false);
		}
		return jPanelTotals;
	}

	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setText(MessageBundle.getMessage("angal.billbrowser.to")); //$NON-NLS-1$
		}
		return jLabelTo;
	}

	private JDateChooser getJCalendarFrom() {
		if (jCalendarFrom == null) {
			dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateFrom.set(GregorianCalendar.MINUTE, 0);
			dateFrom.set(GregorianCalendar.SECOND, 0);
			dateToday0.setTime(dateFrom.getTime());
			jCalendarFrom = new JDateChooser(dateFrom.getTime()); // Calendar
			jCalendarFrom.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarFrom.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			jCalendarFrom.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarFrom.setDate((Date) evt.getNewValue());
					dateFrom.setTime((Date) evt.getNewValue());
					dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
					dateFrom.set(GregorianCalendar.MINUTE, 0);
					dateFrom.set(GregorianCalendar.SECOND, 0);
					// dateToday0.setTime(dateFrom.getTime());
					jButtonToday.setEnabled(true);
					// billFilter();
					billInserted(null);
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
					// dateToday24.setTime(dateTo.getTime());
					jButtonToday.setEnabled(true);
					billInserted(null);
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

	private JButton getJButtonReport() {
		if (jButtonReport == null) {
			jButtonReport = new JButton();
			jButtonReport.setMnemonic(KeyEvent.VK_R);
			jButtonReport.setText(MessageBundle.getMessage("angal.billbrowser.report")); //$NON-NLS-1$
			jButtonReport.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					ArrayList<String> options = new ArrayList<String>();
					options.add(MessageBundle.getMessage("angal.billbrowser.todayclosure"));
					options.add(MessageBundle.getMessage("angal.billbrowser.today"));
					options.add(MessageBundle.getMessage("angal.billbrowser.period"));
					options.add(MessageBundle.getMessage("angal.billbrowser.thismonth"));
					options.add(MessageBundle.getMessage("angal.billbrowser.othermonth"));

					// options.add(MessageBundle.getMessage("angal.billbrowser.billsbyreduction"));

					Icon icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
					String option = (String) JOptionPane.showInputDialog(BillBrowser.this,
							MessageBundle.getMessage("angal.billbrowser.pleaseselectareport"),
							MessageBundle.getMessage("angal.billbrowser.report"), JOptionPane.INFORMATION_MESSAGE, icon,
							options.toArray(), options.get(0));

					if (option == null)
						return;

					String from = null;
					String to = null;

					int i = 0;

					if (options.indexOf(option) == i) { // today closing

						from = formatDateTimeReport(dateToday0);
						to = formatDateTimeReport(dateToday24);
						String user;
						if (Param.bool("SINGLEUSER")) {
							user = "admin";
						} else {
							user = MainMenu.getUser();
						}
						new GenericReportUserInDate(from, to, user, "BillsReportUserAllInDate");
						return;
					}
					if (options.indexOf(option) == ++i) { // closing

						from = formatDateTimeReport(dateToday0);
						to = formatDateTimeReport(dateToday24);
					}
					if (options.indexOf(option) == ++i) { // periode

						from = formatDateTimeReport(dateFrom);
						to = formatDateTimeReport(dateTo);
					}
					if (options.indexOf(option) == ++i) { // this month

						month = jComboBoxMonths.getMonth();
						GregorianCalendar thisMonthFrom = dateFrom;
						GregorianCalendar thisMonthTo = dateTo;
						thisMonthFrom.set(GregorianCalendar.MONTH, month);
						thisMonthFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
						thisMonthTo.set(GregorianCalendar.MONTH, month);
						thisMonthTo.set(GregorianCalendar.DAY_OF_MONTH,
								dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
						from = formatDateTimeReport(thisMonthFrom);
						to = formatDateTimeReport(thisMonthTo);
					}
					if (options.indexOf(option) == ++i) { // other month

						icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$

						int month;
						JMonthChooser monthChooser = new JMonthChooser();
						monthChooser.setLocale(new Locale(Param.string("LANGUAGE")));

						int r = JOptionPane.showConfirmDialog(BillBrowser.this, monthChooser,
								MessageBundle.getMessage("angal.billbrowser.month"), JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, icon);

						if (r == JOptionPane.OK_OPTION) {
							month = monthChooser.getMonth();
						} else {
							return;
						}

						GregorianCalendar thisMonthFrom = dateFrom;
						GregorianCalendar thisMonthTo = dateTo;
						thisMonthFrom.set(GregorianCalendar.MONTH, month);
						thisMonthFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
						thisMonthTo.set(GregorianCalendar.MONTH, month);
						thisMonthTo.set(GregorianCalendar.DAY_OF_MONTH,
								dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
						from = formatDateTimeReport(thisMonthFrom);
						to = formatDateTimeReport(thisMonthTo);
					}
					options = new ArrayList<String>();
					options.add(MessageBundle.getMessage("angal.billbrowser.shortreportonlybaddebts"));
					options.add(MessageBundle.getMessage("angal.billbrowser.fullreportallbills"));
					options.add(MessageBundle.getMessage("angal.billbrowser.paymentreport"));
					options.add(MessageBundle.getMessage("angal.billbrowser.refundreport"));

					options.add(MessageBundle.getMessage("angal.billbrowser.billsbyreduction"));
					options.add(MessageBundle.getMessage("angal.report.oh004incomesallbypricecodes.title"));
					icon = new ImageIcon("rsc/icons/list_dialog.png"); //$NON-NLS-1$
					option = (String) JOptionPane.showInputDialog(BillBrowser.this,
							MessageBundle.getMessage("angal.billbrowser.pleaseselectareport"),
							MessageBundle.getMessage("angal.billbrowser.report"), JOptionPane.INFORMATION_MESSAGE, icon,
							options.toArray(), options.get(0));

					if (option == null)
						return;

					if (options.indexOf(option) == 0) {
						new GenericReportFromDateToDate(from, to, Param.string("BILLSREPORTMONTH"), false);
					}
					if (options.indexOf(option) == 1) {
						new GenericReportFromDateToDate(from, to, Param.string("BILLSREPORT"), false);
					}
					if (options.indexOf(option) == 2) {
						new GenericReportFromDateToDate(from, to, Param.string("BILLSPAYMENTREPORT"), false,
								formatDate(dateFrom), formatDate(dateTo));
					}

					// Added by Silevester D. on 27/05/2022
					if (options.indexOf(option) == 3) {
						new GenericReportFromDateToDate(from, to, Param.string("BILLSREFUNDREPORT"), false,
								formatDate(dateFrom), formatDate(dateTo));
					}

					if (options.indexOf(option) == 4) {
						ArrayList<String> optionsreduc = new ArrayList<String>();
						/**** get all reduction rate ***/
						ReductionPlanManager rplanMan = new ReductionPlanManager();
						ArrayList<ReductionPlan> rplanList = new ArrayList<ReductionPlan>();
						optionsreduc.add("A - " + MessageBundle.getMessage("angal.report.allreductionplans"));
						optionsreduc.add("S - " + MessageBundle.getMessage("angal.report.withnotreduction"));
						try {
							rplanList = rplanMan.getReductionPlans();
							for (int j = 0; j < rplanList.size(); j++) {
								optionsreduc.add(rplanList.get(j).getId() + " - " + rplanList.get(j).getDescription());
							}
						} catch (OHException e1) {
							e1.printStackTrace();
						}
						/*******************************/
						icon = new ImageIcon("rsc/icons/list_dialog.png");
						option = (String) JOptionPane.showInputDialog(BillBrowser.this,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectareductionplan"),
								MessageBundle.getMessage("angal.billbrowser.report"), JOptionPane.INFORMATION_MESSAGE,
								icon, optionsreduc.toArray(), optionsreduc.get(0));
						if (option == null)
							return;
						String reduc_code = option.trim().split("-")[0];
						new GenericReportUserInDate(from, to, reduc_code, user, "BillsReportGroupByReduction", true,
								false);
					}
					if (options.indexOf(option) == 5) {
						new GenericReportFromDateToDate(formatDate(dateFrom), formatDate(dateTo),
								"OH004_IncomesAllByPriceCodes", false, "C", "Nothing",
								"angal.report.oh004incomesallbypricecodes.title");
					}

				}
			});
		}
		return jButtonReport;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(MessageBundle.getMessage("angal.common.close")); //$NON-NLS-1$
			jButtonClose.setMnemonic(KeyEvent.VK_C);
			jButtonClose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// to free memory
					billPeriod.clear();
					mapBill.clear();
					users.clear();
					dispose();
				}
			});
		}
		return jButtonClose;
	}

	private JButton getJButtonEdit() {
		if (jButtonEdit == null) {
			jButtonEdit = new JButton();
			jButtonEdit.setText(MessageBundle.getMessage("angal.billbrowser.editbill")); //$NON-NLS-1$
			jButtonEdit.setMnemonic(KeyEvent.VK_E);
			jButtonEdit.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						if (jScrollPaneBills.isShowing()) {
							int rowSelected = jTableBills.getSelectedRow();
							Bill editBill = (Bill) jTableBills.getValueAt(rowSelected, -1);
							// if (user.equals("admin")) { //$NON-NLS-1$
							if (MainMenu.getCurrentUser().getUserGroupName().equals("admin")) { //$NON-NLS-1$
								if (editBill.getStatus().equals("D")) {
									JOptionPane.showMessageDialog(BillBrowser.this,
											MessageBundle.getMessage("angal.billbrowser.billdeleted"), //$NON-NLS-1$
											MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
											JOptionPane.CANCEL_OPTION);
									return;
								}
								// if(editBill.getStatus().equals("O") ||
								// Param.bool("ENABLEBILLEDITOVERMONTH")){
								if (Param.bool("ENABLEBILLEDITOVERMONTH")) {
									PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, false);
									pbe.addPatientBillListener(BillBrowser.this);
									pbe.setVisible(true);
								} else {
									new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"));
								}

							} else {
								new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"));
							}
						}
						if (jScrollPanePending.isShowing()) {
							int rowSelected = jTablePending.getSelectedRow();
							Bill editBill = (Bill) jTablePending.getValueAt(rowSelected, -1);
							PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, false);
							pbe.addPatientBillListener(BillBrowser.this);
							pbe.setVisible(true);
						}
						if (jScrollPaneClosed.isShowing()) {
							int rowSelected = jTableClosed.getSelectedRow();
							Bill editBill = (Bill) jTableClosed.getValueAt(rowSelected, -1);
							new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"));
						}
						if (jScrollPaneDeleted.isShowing()) {
							int rowSelected = jTableDeleted.getSelectedRow();
							Bill editBill = (Bill) jTableDeleted.getValueAt(rowSelected, -1);
							new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"));
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"),
								MessageBundle.getMessage("angal.billbrowser.title"), JOptionPane.PLAIN_MESSAGE);
					}
				}
			});
		}
		return jButtonEdit;
	}

	/*
	 * private JButton getJButtonEdit() { if (jButtonEdit == null) { jButtonEdit =
	 * new JButton();
	 * jButtonEdit.setText(MessageBundle.getMessage("angal.billbrowser.editbill"));
	 * //$NON-NLS-1$ jButtonEdit.setMnemonic(KeyEvent.VK_E);
	 * jButtonEdit.addActionListener(new ActionListener() {
	 * 
	 * public void actionPerformed(ActionEvent e) { try { if
	 * (jScrollPaneBills.isShowing()) { int rowSelected =
	 * jTableBills.getSelectedRow();
	 * 
	 * Bill editBill = (Bill)jTableBills.getValueAt(rowSelected, -1); if
	 * (editBill.getStatus().equals("O")) { //$NON-NLS-1$
	 * 
	 * PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, false);
	 * pbe.addPatientBillListener(BillBrowser.this); pbe.setVisible(true); } else if
	 * (editBill.getStatus().equals("D")) {
	 * JOptionPane.showMessageDialog(BillBrowser.this,
	 * MessageBundle.getMessage("angal.billbrowser.billdeleted"), //$NON-NLS-1$
	 * MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
	 * JOptionPane.CANCEL_OPTION); return; }else { new
	 * GenericReportBill(editBill.getId(), Param.string("PATIENTBILL")); } } if
	 * (jScrollPanePending.isShowing()) { int rowSelected =
	 * jTablePending.getSelectedRow(); Bill editBill =
	 * (Bill)jTablePending.getValueAt(rowSelected, -1); PatientBillEdit pbe = new
	 * PatientBillEdit(BillBrowser.this, editBill, false);
	 * pbe.addPatientBillListener(BillBrowser.this); pbe.setVisible(true); } if
	 * (jScrollPaneClosed.isShowing()) { int rowSelected =
	 * jTableClosed.getSelectedRow(); Bill editBill =
	 * (Bill)jTableClosed.getValueAt(rowSelected, -1); new
	 * GenericReportBill(editBill.getId(),Param.string("PATIENTBILL")); } if
	 * (jScrollPaneDeleted.isShowing()) { int rowSelected =
	 * jTableDeleted.getSelectedRow(); Bill editBill =
	 * (Bill)jTableDeleted.getValueAt(rowSelected, -1); new
	 * GenericReportBill(editBill.getId(),Param.string("PATIENTBILL")); } } catch
	 * (Exception ex) {
	 * 
	 * JOptionPane.showMessageDialog(null,
	 * MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"),
	 * MessageBundle.getMessage("angal.billbrowser.title"),
	 * JOptionPane.PLAIN_MESSAGE); } } }); } return jButtonEdit; }
	 */

	private JButton getJButtonPrintReceipt() {
		if (jButtonPrintReceipt == null) {
			jButtonPrintReceipt = new JButton();
			jButtonPrintReceipt.setText(MessageBundle.getMessage("angal.billbrowser.receipt")); //$NON-NLS-1$
			jButtonPrintReceipt.setMnemonic(KeyEvent.VK_R);
			jButtonPrintReceipt.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						if (jScrollPaneBills.isShowing()) {
							int rowsSelected = jTableBills.getSelectedRowCount();
							if (rowsSelected == 1) {
								int rowSelected = jTableBills.getSelectedRow();
								Bill editBill = (Bill) jTableBills.getValueAt(rowSelected, -1);
								if (editBill.getStatus().equals("C")) { //$NON-NLS-1$
									new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"), true, true);
								} else {
									if (editBill.getStatus().equals("D")) {
										JOptionPane.showMessageDialog(BillBrowser.this,
												MessageBundle.getMessage("angal.billbrowser.billdeleted"), //$NON-NLS-1$
												MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
												JOptionPane.CANCEL_OPTION);
										return;
									}
									JOptionPane.showMessageDialog(BillBrowser.this,
											MessageBundle.getMessage("angal.billbrowser.billnotyetclosed"), //$NON-NLS-1$
											MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
											JOptionPane.CANCEL_OPTION);
									return;
								}
							} else if (rowsSelected > 1) {
								if (patientParent == null) {
									JOptionPane.showMessageDialog(null,
											MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"), //$NON-NLS-1$
											MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
											JOptionPane.PLAIN_MESSAGE);
									return;
								}
								Bill billTemp = null;
								int[] billIdIndex = jTableBills.getSelectedRows();
								ArrayList<Integer> billsIdList = new ArrayList<Integer>();

								for (int i = 0; i < billIdIndex.length; i++) {
									billTemp = (Bill) jTableBills.getValueAt(billIdIndex[i], -1);
									if (!billTemp.getStatus().equals("D")) {
										billsIdList.add(billTemp.getId());
									}
								}
								java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
								String fromDate = sdf.format(dateFrom.getTime());
								String toDate = sdf.format(dateTo.getTime());
								new GenericReportBill(billsIdList.get(0), Param.string("PATIENTBILLGROUPED"),
										patientParent, billsIdList, fromDate, toDate, true, true);

							}
						}
						if (jScrollPanePending.isShowing()) {
							int rowSelected = jTablePending.getSelectedRow();
							Bill editBill = (Bill) jTablePending.getValueAt(rowSelected, -1);
							if (editBill.getStatus().equals("O") && Param.bool("ALLOWPRINTOPENEDBILL")) {
								rowSelected = jTablePending.getSelectedRow();
								editBill = (Bill) jTablePending.getValueAt(rowSelected, -1);
								new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"), true, true);
							} else {
								PatientBillEdit pbe = new PatientBillEdit(BillBrowser.this, editBill, false);
								pbe.addPatientBillListener(BillBrowser.this);
								pbe.setVisible(true);
							}
						}
						if (jScrollPaneClosed.isShowing()) {
							int rowSelected = jTableClosed.getSelectedRow();
							Bill editBill = (Bill) jTableClosed.getValueAt(rowSelected, -1);
							new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"));
						}
						if (jScrollPaneDeleted.isShowing()) {
							int rowSelected = jTableDeleted.getSelectedRow();
							Bill editBill = (Bill) jTableDeleted.getValueAt(rowSelected, -1);
							new GenericReportBill(editBill.getId(), Param.string("PATIENTBILL"));
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
					}
				}
			});
		}
		return jButtonPrintReceipt;
	}

	private JButton getJButtonNew() {
		if (jButtonNew == null) {
			jButtonNew = new JButton();
			jButtonNew.setText(MessageBundle.getMessage("angal.billbrowser.newbill")); //$NON-NLS-1$
			jButtonNew.setMnemonic(KeyEvent.VK_N);
			jButtonNew.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					PatientBillEdit newBill = new PatientBillEdit(BillBrowser.this, new Bill(), true);

					newBill.addPatientBillListener(BillBrowser.this);

					newBill.setVisible(true);
				}

			});
		}
		return jButtonNew;
	}

	private JButton getJButtonDelete() {
		if (jButtonDelete == null) {
			jButtonDelete = new JButton();
			jButtonDelete.setText(MessageBundle.getMessage("angal.billbrowser.deletebill")); //$NON-NLS-1$
			jButtonDelete.setMnemonic(KeyEvent.VK_D);
			jButtonDelete.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						Bill deleteBill = null;
						int ok = JOptionPane.NO_OPTION;
						if (jScrollPaneBills.isShowing()) {
							int rowSelected = jTableBills.getSelectedRow();
							deleteBill = (Bill) jTableBills.getValueAt(rowSelected, -1);

							// Check if the bill is already deleted. Show an alert and return if so
							if (deleteBill.getStatus().equalsIgnoreCase("D")) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.billbrowser.cannotdeleteadeletedbill"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
										JOptionPane.PLAIN_MESSAGE);

								return;
							}

							ok = JOptionPane.showConfirmDialog(null,
									MessageBundle
											.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.common.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);

						}
						if (jScrollPanePending != null && jScrollPanePending.isShowing()) {
							int rowSelected = jTablePending.getSelectedRow();
							deleteBill = (Bill) jTablePending.getValueAt(rowSelected, -1);

							// Check if the bill is already deleted. Show an alert and return if so
							if (deleteBill.getStatus().equalsIgnoreCase("D")) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.billbrowser.cannotdeleteadeletedbill"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
										JOptionPane.PLAIN_MESSAGE);

								return;
							}

							ok = JOptionPane.showConfirmDialog(null,
									MessageBundle
											.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.common.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
						}
						if (jScrollPaneClosed != null && jScrollPaneClosed.isShowing()) {
							int rowSelected = jTableClosed.getSelectedRow();
							deleteBill = (Bill) jTableClosed.getValueAt(rowSelected, -1);

							// Check if the bill is already deleted. Show an alert and return if so
							if (deleteBill.getStatus().equalsIgnoreCase("D")) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.billbrowser.cannotdeleteadeletedbill"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
										JOptionPane.PLAIN_MESSAGE);

								return;
							}

							ok = JOptionPane.showConfirmDialog(null,
									MessageBundle
											.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.common.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
						}
						if (jScrollPaneDeleted != null && jScrollPaneDeleted.isShowing()) {
							int rowSelected = jTableDeleted.getSelectedRow();
							deleteBill = (Bill) jTableDeleted.getValueAt(rowSelected, -1);

							// Check if the bill is already deleted. Show an alert and return if so
							if (deleteBill.getStatus().equalsIgnoreCase("D")) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.billbrowser.cannotdeleteadeletedbill"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
										JOptionPane.PLAIN_MESSAGE);

								return;
							}

							ok = JOptionPane.showConfirmDialog(null,
									MessageBundle
											.getMessage("angal.billbrowser.doyoureallywanttodeletetheselectedbill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.common.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
						}
						if (ok == JOptionPane.YES_OPTION) {
							billManager.deleteBill(deleteBill);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectabillfirst"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
					}
					billInserted(null);
				}
			});
		}

		// Disable bill delete button
		jButtonDelete.setEnabled(false);
		jButtonDelete.setVisible(false);

		return jButtonDelete;
	}

	@SuppressWarnings("static-access")
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			if (MainMenu.checkUserGrants("btnbillnew"))
				jPanelButtons.add(getJButtonNew());
			if (MainMenu.checkUserGrants("btnbilledit"))
				jPanelButtons.add(getJButtonEdit());
			if (MainMenu.checkUserGrants("btnbilldelete"))
				jPanelButtons.add(getJButtonDelete());
			if (MainMenu.checkUserGrants("btnbillreceipt") && Param.bool("RECEIPTPRINTER"))
				jPanelButtons.add(getJButtonPrintReceipt());
			if (MainMenu.checkUserGrants("btnbillreport"))
				jPanelButtons.add(getJButtonReport());
			if (MainMenu.checkUserGrants("btnbillreport"))
				jPanelButtons.add(getButtonExport());
			if (MainMenu.checkUserGrants("btnbillclosebill"))
				jPanelButtons.add(getJButtonCloseBill());
			if (Sage.getSage().ENABLE_SAGE_INTEGRATION) {
				jPanelButtons.add(getExportSageButton());
			}
			if (MainMenu.checkUserGrants("btnbillreport") && Param.bool("ENABLEBILLESTIMATE"))
				jPanelButtons.add(getJButtonSimulate());
			if (Param.bool("ENABLEMEDICALREFUND"))
				jPanelButtons.add(getJButtonBack());
			jPanelButtons.add(getJButtonClose());
		}
		return jPanelButtons;
	}

	/**
	 * Update on 16/05/2022 by Silevester D.
	 * 
	 * @return JButton
	 */
	private JButton getJButtonBack() {
		if (jButtonBack == null) {
			jButtonBack = new JButton();
			jButtonBack.setText(MessageBundle.getMessage("angal.newbill.refund"));
			jButtonBack.setMnemonic(KeyEvent.VK_N);
			jButtonBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selectedBill == null) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.pleaseselectabill"),
								MessageBundle.getMessage("angal.billbrowser.title"), JOptionPane.PLAIN_MESSAGE);

						return;
					}

					// Check if the selected bill is closed. If not, return.
					if (!selectedBill.getStatus().equalsIgnoreCase("C")) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.onlyclosedbillcanberefunded"),
								MessageBundle.getMessage("angal.billbrowser.title"), JOptionPane.PLAIN_MESSAGE);

						return;
					}

					// Check if selected bill a refund bill. If so, return.
					if (selectedBill.getParentId() != 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.billbrowser.cannotrefundarefundbill"),
								MessageBundle.getMessage("angal.billbrowser.title"), JOptionPane.PLAIN_MESSAGE);

						return;
					}

					BillRefund newBillRefund = new BillRefund(BillBrowser.this, selectedBill);

					newBillRefund.addPatientRefundBillListener(BillBrowser.this);

					newBillRefund.setVisible(true);
				}
			});

			jButtonBack.setEnabled(false);
		}
		
		return jButtonBack;
	}

	private Component getJButtonCloseBill() {
		if (jButtonCloseBill == null) {
			jButtonCloseBill = new JButton();
			jButtonCloseBill.setText(MessageBundle.getMessage("angal.billbrowser.closebill")); //$NON-NLS-1$
			jButtonCloseBill.setMnemonic(KeyEvent.VK_C);
			jButtonCloseBill.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						if (jScrollPaneBills.isShowing()) {
							int rowSelected = jTableBills.getSelectedRow();
							Bill bill = (Bill) jTableBills.getValueAt(rowSelected, -1);
							if (bill.getStatus().equals("O")) {
								int ok = JOptionPane.showConfirmDialog(null,
										MessageBundle
												.getMessage("angal.billbrowser.doyoureallywanttoclosetheselectedbill"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.common.delete"), //$NON-NLS-1$
										JOptionPane.YES_NO_OPTION);
								if (ok == JOptionPane.YES_OPTION) {
									boolean res = new BillBrowserManager().closeBill(bill);
									if (res) {
										// update table
										billInserted(null);
									}
								}

							} else {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.billbrowser.cancloseonlyopenbills"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
										JOptionPane.PLAIN_MESSAGE);
							}
						}
						if (jScrollPanePending.isShowing()) {
							int rowSelected = jTablePending.getSelectedRow();
							Bill bill = (Bill) jTablePending.getValueAt(rowSelected, -1);
							int ok = JOptionPane.showConfirmDialog(null,
									MessageBundle.getMessage("angal.billbrowser.doyoureallywanttoclosetheselectedbill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.common.delete"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION);
							if (ok == JOptionPane.YES_OPTION) {
								boolean res = new BillBrowserManager().closeBill(bill);
								if (res) {
									// update table
									billInserted(null);
								}
							}
						}
						if (jScrollPaneClosed.isShowing()) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.billbrowser.cancloseonlyopenbills"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
									JOptionPane.PLAIN_MESSAGE);
						}
						if (jScrollPaneDeleted.isShowing()) {
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.billbrowser.cancloseonlyopenbills"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
									JOptionPane.PLAIN_MESSAGE);
						}
					} catch (OHException ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), // $NON-NLS-1$
								MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.billbrowser.erroroccured"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.billbrowser.title"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
					}
				}
			});
		}
		
		// Disable bill close button
		jButtonCloseBill.setEnabled(false);
		jButtonCloseBill.setVisible(false);
		
		return jButtonCloseBill;
	}

	private JPanel getJPanelRange() {
		if (jPanelRange == null) {
			jPanelRange = new JPanel();

			jPanelRange.setLayout(new BorderLayout(0, 0));
			jPanelRange.add(getPanelSupRange(), BorderLayout.NORTH);
			// if( Param.bool("ALLOWFILTERBILLBYMEDICAL")){
			jPanelRange.add(getPanelChooseMedical(), BorderLayout.SOUTH);
			// }
		}
		return jPanelRange;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getJComboUsers() {
		if (jComboUsers == null) {
			jComboUsers = new JComboBox();
			for (String user : users)
				jComboUsers.addItem(user);

			jComboUsers.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					user = (String) jComboUsers.getSelectedItem();
					jTableUser.setValueAt("<html><b>" + user + "</b></html>", 0, 0);
					updateTotals();
				}
			});
		}
		return jComboUsers;
	}

	private JButton getJButtonToday() {
		if (jButtonToday == null) {
			jButtonToday = new JButton();
			jButtonToday.setText(MessageBundle.getMessage("angal.billbrowser.today")); //$NON-NLS-1$
			jButtonToday.setMnemonic(KeyEvent.VK_T);
			jButtonToday.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dateFrom.setTime(dateToday0.getTime());
					dateTo.setTime(dateToday24.getTime());

					jCalendarFrom.setDate(dateFrom.getTime());
					jCalendarTo.setDate(dateTo.getTime());

					jButtonToday.setEnabled(false);
				}
			});
			jButtonToday.setEnabled(false);
		}
		return jButtonToday;
	}

	private JMonthChooser getJComboMonths() {
		if (jComboBoxMonths == null) {
			jComboBoxMonths = new JMonthChooser();
			jComboBoxMonths.setLocale(new Locale(Param.string("ALLOWFILTERBILLBYMEDICAL")));
			jComboBoxMonths.addPropertyChangeListener("month", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					month = jComboBoxMonths.getMonth();
					dateFrom.set(GregorianCalendar.MONTH, month);
					dateFrom.set(GregorianCalendar.DAY_OF_MONTH, 1);
					dateTo.set(GregorianCalendar.MONTH, month);
					dateTo.set(GregorianCalendar.DAY_OF_MONTH,
							dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));

					jCalendarFrom.setDate(dateFrom.getTime());
					jCalendarTo.setDate(dateTo.getTime());
				}
			});
		}
		return jComboBoxMonths;
	}

	private JYearChooser getJComboYears() {
		if (jComboBoxYears == null) {
			jComboBoxYears = new JYearChooser();
			jComboBoxYears.setLocale(new Locale(Param.string("LANGUAGE")));
			jComboBoxYears.addPropertyChangeListener("year", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {
					year = jComboBoxYears.getYear();
					dateFrom.set(GregorianCalendar.YEAR, year);
					dateFrom.set(GregorianCalendar.MONTH, 1);
					dateFrom.set(GregorianCalendar.DAY_OF_YEAR, 1);
					dateTo.set(GregorianCalendar.YEAR, year);
					dateTo.set(GregorianCalendar.MONTH, 12);
					dateTo.set(GregorianCalendar.DAY_OF_YEAR, dateFrom.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
					jCalendarFrom.setDate(dateFrom.getTime());
					jCalendarTo.setDate(dateTo.getTime());
				}
			});
		}
		return jComboBoxYears;
	}

	private JButton getButtonExport() {
		JButton buttonExport = new JButton("Excel");
		buttonExport.setMnemonic(KeyEvent.VK_X);
		buttonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fcExcel = new JFileChooser();
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xls)", "xls");
				fcExcel.addChoosableFileFilter(excelFilter);
				fcExcel.setFileFilter(excelFilter);
				fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int iRetVal = fcExcel.showSaveDialog(BillBrowser.this);
				if (iRetVal == JFileChooser.APPROVE_OPTION) {
					File exportFile = fcExcel.getSelectedFile();
					if (!exportFile.getName().endsWith("xls"))
						exportFile = new File(exportFile.getAbsoluteFile() + ".xls");
					ExcelExporter xlsExport = new ExcelExporter();
					try {
						xlsExport.exportBillsTableToExcel(billPeriod, exportFile);
						Desktop.getDesktop().open(new File(exportFile.getAbsolutePath()));
					} catch (IOException exc) {
						logger.info("Export to excel error : " + exc.getMessage());
					}
				}
			}
		});
		return buttonExport;
	}

	private JScrollPane getJScrollPaneClosed() {
		if (jScrollPaneClosed == null) {
			jScrollPaneClosed = new JScrollPane();
			jScrollPaneClosed.setViewportView(getJTableClosed());
		}
		return jScrollPaneClosed;
	}

	private JScrollPane getJScrollPaneDeleted() {
		if (jScrollPaneDeleted == null) {
			jScrollPaneDeleted = new JScrollPane();
			jScrollPaneDeleted.setViewportView(getJTableDeleted());
		}
		return jScrollPaneDeleted;
	}

	private JTable getJTableClosed() {
		if (jTableClosed == null) {
			jTableClosed = new JTable();

			// apply cell rendered behaviour
			jTableClosed.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseMoved(MouseEvent e) {
					JTable aTable = (JTable) e.getSource();
					int itsRow = aTable.rowAtPoint(e.getPoint());
					if (itsRow >= 0) {
						cellRenderer.setHoveredRow(itsRow);
					} else {
						cellRenderer.setHoveredRow(-1);
					}
					aTable.repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {

				}
			});
			jTableClosed.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					cellRenderer.setHoveredRow(-1);
				}
			});

			jTableClosed.setModel(new BillTableModel("C")); //$NON-NLS-1$
			for (int i = 0; i < columsWidth.length; i++) {
				jTableClosed.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i])
					jTableClosed.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (alignCenter[i]) {
					jTableClosed.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTableClosed.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTableClosed.setAutoCreateColumnsFromModel(false);
			jTableClosed.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTableClosed.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTableClosed.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());

			// Enable Refund button if selected bill is closed
			if(Param.bool("ENABLEMEDICALREFUND")) {
				jTableClosed.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						if (!jTableClosed.getSelectionModel().isSelectionEmpty()) {
							selectedBill = (Bill) jTableClosed.getValueAt(jTableClosed.getSelectedRow(), -1);

							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if (selectedBill.getStatus().equalsIgnoreCase("C")) {
										jButtonBack.setEnabled(true);
									} else {
										jButtonBack.setEnabled(false);
									}
								}
							});
						} else {
							selectedBill = null;
							jButtonBack.setEnabled(false);
						}
					}
				});
			}
		}
		return jTableClosed;
	}

	private JTable getJTableDeleted() {
		if (jTableDeleted == null) {
			jTableDeleted = new JTable();

			// apply cell rendered behaviour
			jTableDeleted.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseMoved(MouseEvent e) {
					JTable aTable = (JTable) e.getSource();
					int itsRow = aTable.rowAtPoint(e.getPoint());
					if (itsRow >= 0) {
						cellRenderer.setHoveredRow(itsRow);
					} else {
						cellRenderer.setHoveredRow(-1);
					}
					aTable.repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {

				}
			});
			jTableDeleted.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					cellRenderer.setHoveredRow(-1);
				}
			});

			jTableDeleted.setModel(new BillTableModel("D")); //$NON-NLS-1$
			for (int i = 0; i < columsWidth.length; i++) {
				jTableDeleted.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i])
					jTableDeleted.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (alignCenter[i]) {
					jTableDeleted.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTableDeleted.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTableDeleted.setAutoCreateColumnsFromModel(false);
			jTableDeleted.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTableDeleted.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTableDeleted.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());

			// Enable Refund button if selected bill is closed
			if(Param.bool("ENABLEMEDICALREFUND")) {
				jTableDeleted.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						if (!jTableDeleted.getSelectionModel().isSelectionEmpty()) {
	
							selectedBill = (Bill) jTableDeleted.getValueAt(jTableDeleted.getSelectedRow(), -1);
	
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									jButtonBack.setEnabled(false);
								}
							});
						} else {
							selectedBill = null;
							jButtonBack.setEnabled(false);
						}
					}
				});
			}
		}
		return jTableDeleted;
	}

	private JScrollPane getJScrollPanePending() {
		if (jScrollPanePending == null) {
			jScrollPanePending = new JScrollPane();
			jScrollPanePending.setViewportView(getJTablePending());
		}
		return jScrollPanePending;
	}

	private JTable getJTablePending() {
		if (jTablePending == null) {
			jTablePending = new JTable();

			// apply cell rendered behaviour
			jTablePending.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseMoved(MouseEvent e) {
					JTable aTable = (JTable) e.getSource();
					int itsRow = aTable.rowAtPoint(e.getPoint());
					if (itsRow >= 0) {
						cellRenderer.setHoveredRow(itsRow);
					} else {
						cellRenderer.setHoveredRow(-1);
					}
					aTable.repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {

				}
			});
			jTablePending.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					cellRenderer.setHoveredRow(-1);
				}
			});

			jTablePending.setModel(new BillTableModel("O")); //$NON-NLS-1$
			for (int i = 0; i < columsWidth.length; i++) {
				jTablePending.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i])
					jTablePending.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (alignCenter[i]) {
					jTablePending.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTablePending.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTablePending.setAutoCreateColumnsFromModel(false);
			jTablePending.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTablePending.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTablePending.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());

			// Enable Refund button if selected bill is closed
			if(Param.bool("ENABLEMEDICALREFUND")) {
				jTablePending.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						if (!jTablePending.getSelectionModel().isSelectionEmpty()) {
							selectedBill = (Bill) jTablePending.getValueAt(jTablePending.getSelectedRow(), -1);

							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									jButtonBack.setEnabled(false);
								}
							});
						} else {
							selectedBill = null;
							jButtonBack.setEnabled(false);
						}
					}
				});
			}
		}
		return jTablePending;
	}

	private JScrollPane getJScrollPaneBills() {
		if (jScrollPaneBills == null) {
			jScrollPaneBills = new JScrollPane();
			jScrollPaneBills.setViewportView(getJTableBills());
		}
		return jScrollPaneBills;
	}

	private JTable getJTableBills() {
		if (jTableBills == null) {
			jTableBills = new JTable();

			// apply cell rendered behaviour
			jTableBills.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseMoved(MouseEvent e) {
					JTable aTable = (JTable) e.getSource();
					int itsRow = aTable.rowAtPoint(e.getPoint());
					if (itsRow >= 0) {
						cellRenderer.setHoveredRow(itsRow);
					} else {
						cellRenderer.setHoveredRow(-1);
					}
					aTable.repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {

				}
			});
			jTableBills.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					cellRenderer.setHoveredRow(-1);
				}
			});
			jTableBills.setModel(new BillTableModel("ALL"));
			for (int i = 0; i < columsWidth.length; i++) {
				jTableBills.getColumnModel().getColumn(i).setMinWidth(columsWidth[i]);
				if (!columsResizable[i])
					jTableBills.getColumnModel().getColumn(i).setMaxWidth(maxWidth[i]);
				if (alignCenter[i]) {
					jTableBills.getColumnModel().getColumn(i).setCellRenderer(new StringCenterTableCellRenderer());
					if (boldCenter[i]) {
						jTableBills.getColumnModel().getColumn(i).setCellRenderer(new CenterBoldTableCellRenderer());
					}
				}
			}
			jTableBills.setAutoCreateColumnsFromModel(false);
			jTableBills.setDefaultRenderer(String.class, new StringTableCellRenderer());
			jTableBills.setDefaultRenderer(Integer.class, new IntegerTableCellRenderer());
			jTableBills.setDefaultRenderer(Double.class, new DoubleTableCellRenderer());

			// Enable Refund button if selected bill is closed
			if(Param.bool("ENABLEMEDICALREFUND")) {
				jTableBills.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						if (!jTableBills.getSelectionModel().isSelectionEmpty()) {
							selectedBill = (Bill) jTableBills.getValueAt(jTableBills.getSelectedRow(), -1);
							
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if (selectedBill.getStatus().equalsIgnoreCase("C")) {
										jButtonBack.setEnabled(true);
									} else {
										jButtonBack.setEnabled(false);
									}
								}
							});
						} else {
							selectedBill = null;
							jButtonBack.setEnabled(false);
						}
					}
				});
			}
		}
		return jTableBills;
	}

	private JTabbedPane getJTabbedPaneBills() {
		if (jTabbedPaneBills == null) {
			jTabbedPaneBills = new JTabbedPane();
			jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.bills"), getJScrollPaneBills());
			jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.pending"), getJScrollPanePending());
			jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.closed"), getJScrollPaneClosed());
			jTabbedPaneBills.addTab(MessageBundle.getMessage("angal.billbrowser.deleted"), getJScrollPaneDeleted());
		}
		return jTabbedPaneBills;
	}

	private JTable getJTableToday() {
		if (jTableToday == null) {
			jTableToday = new JTable();
			jTableToday.setModel(new DefaultTableModel(new Object[][] {
					{ "<html><b>" + MessageBundle.getMessage("angal.billbrowser.todaypayments") + "</b></html>", totalToday, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"<html><b>" + MessageBundle.getMessage("angal.billbrowser.notpaid") + "</b></html>",
							balanceToday } },
					new String[] { "", "", "", "" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableToday.setRowSelectionAllowed(false);
			jTableToday.setGridColor(Color.WHITE);

		}
		return jTableToday;
	}

	private JTable getJTablePeriod() {
		if (jTablePeriod == null) {
			jTablePeriod = new JTable();
			jTablePeriod.setModel(new DefaultTableModel(new Object[][] {
					{ "<html><b>" + MessageBundle.getMessage("angal.billbrowser.periodpayments") + "</b></html>", totalPeriod, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"<html><b>" + MessageBundle.getMessage("angal.billbrowser.notpaid") + "</b></html>",
							balancePeriod } },
					new String[] { "", "", "", "" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTablePeriod.setRowSelectionAllowed(false);
			jTablePeriod.setGridColor(Color.WHITE);

		}
		return jTablePeriod;
	}

	private JTable getJTableUser() {
		if (jTableUser == null) {
			jTableUser = new JTable();
			jTableUser
					.setModel(
							new DefaultTableModel(
									new Object[][] {
											{ "<html><b>" + MessageBundle.getMessage("angal.billbrowser.userpayments") + " : " + user + "</b></html>", userToday, //$NON-NLS-1$ //$NON-NLS-2$
													"<html><b>" + MessageBundle.getMessage("angal.billbrowser.period") //$NON-NLS-1$
															+ "</b></html>",
													userPeriod } },
									new String[] { "", "", "", "" }) {
								private static final long serialVersionUID = 1L;
								Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class,
										Double.class };

								public Class<?> getColumnClass(int columnIndex) {
									return types[columnIndex];
								}

								public boolean isCellEditable(int row, int column) {
									return false;
								}
							});
			jTableUser.setRowSelectionAllowed(false);
			jTableUser.setGridColor(Color.WHITE);

		}
		return jTableUser;
	}

	private JTable getJTableUserRefund() {
		if (jTableUserRefund == null) {
			jTableUserRefund = new JTable();
			jTableUserRefund.setModel(new DefaultTableModel(new Object[][] { {
					"<html><b>" + MessageBundle.getMessage("angal.billbrowser.refund") + " : " + user + "</b></html>",
					userRefundToday, "<html><b>" + MessageBundle.getMessage("angal.billbrowser.period") + "</b></html>",
					userRefundPeriod } }, new String[] { "", "", "", "" }) {

				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableUserRefund.setRowSelectionAllowed(false);
			jTableUserRefund.setGridColor(Color.WHITE);

		}
		return jTableUserRefund;
	}

	private JTable getJTableRefund() {
		if (jTableRefund == null) {
			jTableRefund = new JTable();
			jTableRefund.setModel(new DefaultTableModel(new Object[][] {
					{ "<html><b>" + MessageBundle.getMessage("angal.billbrowser.refund") + "</b></html>", refundToday, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"<html><b>" + MessageBundle.getMessage("angal.billbrowser.period") + "</b></html>",
							refundPeriod } },
					new String[] { "", "", "", "" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, JLabel.class, Double.class };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableRefund.setRowSelectionAllowed(false);
			jTableRefund.setGridColor(Color.WHITE);

		}
		return jTableRefund;
	}

	// MARCO ADD
	private JButton getJButtonSimulate() {
		if (jButtonSimulate == null) {
			jButtonSimulate = new JButton();
			jButtonSimulate.setText(MessageBundle.getMessage("angal.billbrowser.simulate")); //$NON-NLS-1$
			jButtonSimulate.setMnemonic(KeyEvent.VK_N);
			jButtonSimulate.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					PatientBillEditSimulate newBill = new PatientBillEditSimulate(BillBrowser.this, new Bill(), true);

					newBill.setVisible(true);
				}
			});
		}
		return jButtonSimulate;
	}

	private JPanel getPanelChoosePatient() {
		JPanel priceListLabelPanel = new JPanel();
		// panelSupRange.add(priceListLabelPanel);
		priceListLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		jAffiliatePersonJButtonAdd = new JButton();
		jAffiliatePersonJButtonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		jAffiliatePersonJButtonAdd.setIcon(new ImageIcon("rsc/icons/pick_patient_button.png"));

		jAffiliatePersonJButtonSupp = new JButton();
		jAffiliatePersonJButtonSupp.setIcon(new ImageIcon("rsc/icons/remove_patient_button.png"));

		jAffiliatePersonJTextField = new JTextField(14);
		jAffiliatePersonJTextField.setEnabled(false);
		priceListLabelPanel.add(jAffiliatePersonJTextField);
		priceListLabelPanel.add(jAffiliatePersonJButtonAdd);
		priceListLabelPanel.add(jAffiliatePersonJButtonSupp);

		jAffiliatePersonJButtonAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SelectPatient selectPatient = new SelectPatient(BillBrowser.this, false, true);
				selectPatient.addSelectionListener(BillBrowser.this);
				selectPatient.setVisible(true);
			}
		});

		jAffiliatePersonJButtonSupp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				patientParent = null;
				garantUserChoose = null;
				comboGaranti.setSelectedItem(null);
				jAffiliatePersonJTextField.setText("");
				billInserted(null);
			}
		});

		return priceListLabelPanel;
	}

	private void updateTables() {
		jTableBills.setModel(new BillTableModel("ALL")); //$NON-NLS-1$
		jTablePending.setModel(new BillTableModel("O")); //$NON-NLS-1$
		jTableClosed.setModel(new BillTableModel("C")); //$NON-NLS-1$
		jTableDeleted.setModel(new BillTableModel("D")); //$NON-NLS-1$
	}

	private void updateDataSet() {
		updateDataSet(new DateTime().toDateMidnight().toGregorianCalendar(),
				new DateTime().toDateMidnight().plusDays(1).toGregorianCalendar());
	}

	private void updateDataSet(GregorianCalendar dateFrom, GregorianCalendar dateTo) {
		/*
		 * Bills in the period
		 */
		billPeriod = billManager.getBills(dateFrom, dateTo);

		/*
		 * Payments in the period
		 */
		paymentsPeriod = billManager.getPayments(dateFrom, dateTo);

		/*
		 * Bills not in the period but with payments in the period
		 */
		billFromPayments = billManager.getBills(paymentsPeriod);
	}

	private void updateDataSet(GregorianCalendar dateFrom, GregorianCalendar dateTo, Patient patient) {
		/*
		 * Bills in the period
		 */
		billPeriod = billManager.getBills(dateFrom, dateTo, patient);

		/*
		 * Payments in the period
		 */
		paymentsPeriod = billManager.getPayments(dateFrom, dateTo, patient);

		/*
		 * Bills not in the period but with payments in the period
		 */
		billFromPayments = billManager.getBills(paymentsPeriod);
	}

	private void updateDataSet(GregorianCalendar dateFrom, GregorianCalendar dateTo, BillItems billItem) {
		/*
		 * Bills in the period contending selected item
		 */
		billPeriod = billManager.getBills(dateFrom, dateTo, billItem);

		/*
		 * Payments in the period
		 */
		// paymentsPeriod = billManager.getPayments(dateFrom, dateTo, patient);
		paymentsPeriod = billManager.getPayments(billPeriod);

		/*
		 * Bills not in the period but with payments in the period
		 */
		billFromPayments = billManager.getBills(paymentsPeriod);
	}

	private void updateDataSet(GregorianCalendar dateFrom, GregorianCalendar dateTo, User userGarant) {
		/*
		 * Bills in the period contending selected item
		 */
		billPeriod = billManager.getBills(dateFrom, dateTo, userGarant);

		/*
		 * Payments in the period
		 */
		// paymentsPeriod = billManager.getPayments(dateFrom, dateTo, patient);
		paymentsPeriod = billManager.getPayments(billPeriod);

		/*
		 * Bills not in the period but with payments in the period
		 */
		billFromPayments = billManager.getBills(paymentsPeriod);
	}

	private void updateTotals() {
		ArrayList<Bill> billToday;
		ArrayList<BillPayments> paymentsToday;

		billToday = billManager.getBills(dateToday0, dateToday24, patientParent); // adding patient parameter
		paymentsToday = billManager.getPayments(dateToday0, dateToday24, patientParent); // adding patient parameter

		// if (MainMenu.getUser().equals("admin")) {
		// billToday = billManager.getBills(dateToday0, dateToday24, patientParent);
		// //adding patient parameter
		// paymentsToday = billManager.getPayments(dateToday0, dateToday24,
		// patientParent); //adding patient parameter
		// } else {
		// billToday = billPeriod;
		// paymentsToday = paymentsPeriod;
		// }

		totalPeriod = new BigDecimal(0);
		balancePeriod = new BigDecimal(0);
		totalToday = new BigDecimal(0);
		balanceToday = new BigDecimal(0);
		userToday = new BigDecimal(0);
		userPeriod = new BigDecimal(0);
		userRefundToday = new BigDecimal(0);
		userRefundPeriod = new BigDecimal(0);
		refundToday = new BigDecimal(0);
		refundPeriod = new BigDecimal(0);

		ArrayList<Integer> deletedBill = new ArrayList<Integer>();

		// Bills in range contribute for Not Paid (balance)
		for (Bill bill : billPeriod) {
			if (bill.getStatus().equals("D")) {
				deletedBill.add(bill.getId());
			} else {
				BigDecimal balance = new BigDecimal(Double.toString(bill.getBalance()));
				balancePeriod = balancePeriod.add(balance);
			}
		}

		// Payments in range contribute for Paid Period (total)
		for (BillPayments payment : paymentsPeriod) {
			if (!deletedBill.contains(payment.getBillID())) {
				BigDecimal payAmount = new BigDecimal(Double.toString(payment.getAmount()));
				String payUser = payment.getUser();
				if (payAmount.doubleValue() > 0) {
					totalPeriod = totalPeriod.add(payAmount);
				} else {
					refundPeriod = refundPeriod.add(payAmount);
				}

				if (payUser.equals(user)) {
					if (payAmount.doubleValue() > 0) {
						userPeriod = userPeriod.add(payAmount);
					} else {
						userRefundPeriod = userRefundPeriod.add(payAmount);
					}
				}
			}
		}

		// Bills in today contribute for Not Paid Today (balance)
		for (Bill bill : billToday) {
			if (!bill.getStatus().equals("D")) {
				BigDecimal balance = new BigDecimal(Double.toString(bill.getBalance()));
				balanceToday = balanceToday.add(balance);
			}
		}

		// Payments in today contribute for Paid Today (total)
		for (BillPayments payment : paymentsToday) {
			if (!deletedBill.contains(payment.getBillID())) {
				BigDecimal payAmount = new BigDecimal(Double.toString(payment.getAmount()));
				String payUser = payment.getUser();
				
				if (payAmount.doubleValue() > 0) {
					totalToday = totalToday.add(payAmount);					
				} else {
					refundToday = refundToday.add(payAmount);
				}
				
				if (!Param.bool("SINGLEUSER") && payUser.equals(user)) {
					if (payAmount.doubleValue() > 0) {
						userToday = userToday.add(payAmount);
					} else {
						userRefundToday = userRefundToday.add(payAmount);
					}
				}
			}
		}

		jTableToday.setValueAt(totalToday, 0, 1);
		jTableToday.setValueAt(balanceToday, 0, 3);
		jTablePeriod.setValueAt(totalPeriod, 0, 1);
		jTablePeriod.setValueAt(balancePeriod, 0, 3);
		if (jTableUser != null) {
			jTableUser.setValueAt(userToday, 0, 1);
			jTableUser.setValueAt(userPeriod, 0, 3);
		}

		if (Param.bool("ENABLEMEDICALREFUND")) {
			jTableRefund.setValueAt(refundToday.multiply(new BigDecimal(-1)), 0, 1);
			jTableRefund.setValueAt(refundPeriod.multiply(new BigDecimal(-1)), 0, 3);
	
			if (jTableUserRefund != null) {
				jTableUserRefund.setValueAt(userRefundToday.multiply(new BigDecimal(-1)), 0, 1);
				jTableUserRefund.setValueAt(userRefundPeriod.multiply(new BigDecimal(-1)), 0, 3);
			}
		}
	}

	public class BillTableModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Bill> tableArray = new ArrayList<Bill>();

		/*
		 * All Bills
		 */
		private ArrayList<Bill> billAll = new ArrayList<Bill>();

		public BillTableModel(String status) {
			loadData(status);
		}

		private void loadData(String status) {

			tableArray.clear();
			mapBill.clear();

			mapping(status);
		}

		private void mapping(String status) {
			int limit = -1;
			if (Param.bool("ALLOWLIMITRECORDDISPLAY")) {
				try {
					String limitS = Param.string("LIMITRECORDDISPLAY");

					limit = Integer.parseInt(limitS);
				} catch (Exception e) {
					System.out.println("Errors occurs with retreiving LIMIT PARAMETER in bill browser");
				}
			}
			/*
			 * Mappings Bills in the period
			 */
			for (Bill bill : billPeriod) {
				// mapBill.clear();
				mapBill.put(bill.getId(), bill);
			}
			/*
			 * Merging the two bills lists
			 */
			billAll.addAll(billPeriod);
			for (Bill bill : billFromPayments) {
				if (mapBill.get(bill.getId()) == null)
					billAll.add(bill);
			}

			if (status.equals("O")) {
				if (patientParent != null) {
					tableArray = billManager.getPendingBillsAffiliate(patientParent.getCode());
				} else {
					if (chooseItem != null) {
						tableArray = billManager.getPendingBillsSpecificItem(chooseItem.getItemId());
					} else if (garantUserChoose != null) {
						tableArray = billManager.getPendingBillsSpecificGarante(garantUserChoose);
					} else {
						tableArray = billManager.getPendingBills2(0);
					}
				}

				/*** filter by specific items ***/

			} else if (status.equals("ALL")) {

				Collections.sort(billAll);
				tableArray = billAll;
			} else if (status.equals("C")) {
				for (Bill bill : billPeriod) {
					if (bill.getStatus().equals(status)) {
						tableArray.add(bill);
					}
				}
			} else if (status.equals("D")) {
				for (Bill bill : billPeriod) {
					if (bill.getStatus().equals(status)) {
						tableArray.add(bill);
					}
				}
			}

			// minimiser la taille ici
			ArrayList<Bill> temp = new ArrayList<Bill>();
			// inverser quand c'est le premier tableau
			int id_last = 0;
			int id_first = 0;
			try {
				id_last = tableArray.get(tableArray.size() - 1).getId();
			} catch (Exception e) {
			}
			try {
				id_first = tableArray.get(0).getId();
			} catch (Exception e) {
			}
			if (id_last > id_first) {
				Collections.sort(tableArray, Collections.reverseOrder());
			}

			//
			for (Bill bill : tableArray) {
				if (temp.size() >= limit && limit != -1) {

					break;
				}
				temp.add(bill);
			}
			tableArray = temp;
			//
			Collections.sort(tableArray, Collections.reverseOrder());

		}

		public Class<?> getColumnClass(int columnIndex) {
			return columsClasses[columnIndex];
		}

		public int getColumnCount() {
			return columsNames.length;
		}

		public String getColumnName(int columnIndex) {
			return columsNames[columnIndex];
		}

		public int getRowCount() {
			if (tableArray == null)
				return 0;
			return tableArray.size();
		}

		// ["Date", "Patient", "Balance", "Update", "Status", "Amount"};

		public Object getValueAt(int r, int c) {
			// String name = "";
			int index = -1;
			Bill thisBill = tableArray.get(r);
			if (c == index) {
				return thisBill;
			}
			if (c == ++index) {
				return thisBill.getId();
			}
			if (c == ++index) {
				// return formatDateTime(thisBill.getDate());
				return formatDateTimeReport(thisBill.getDate());
			}
			if (c == ++index) {
				int patID = thisBill.getPatID();
				return patID == 0 ? "" : String.valueOf(patID);
			}
			if (c == ++index) {
				Integer parentId = 0;
				String parentFirstname = "";
				String parentlastname = "";
				// String garantePerson = "";
				String userGarante = null;
				try {
					// garante
					if (Param.bool("ALLOWGARANTEPERSON")) {
						userGarante = new UserBrowsingManager().getUsrFullName(thisBill.getGarante());
					}
					//
					parentId = thisBill.getAffiliatedParent();
					Patient parent = new PatientBrowserManager().getPatient(parentId);
					parentFirstname = parent.getFirstName();
					parentlastname = parent.getSecondName();
				} catch (Exception e) {
				}

				String returnValue = "";

				if (parentFirstname.equals("") && parentlastname.equals(""))
					returnValue = thisBill.getPatName();
				else
					returnValue = thisBill.getPatName() + " ( " + parentFirstname + " " + parentlastname + " ) ";

				if (Param.bool("ALLOWGARANTEPERSON") && userGarante != null)
					returnValue = returnValue + " [Garantie: " + userGarante + " ]";

				return returnValue;
				// add garante

				//
			}
			if (c == ++index) {
				return thisBill.getAmount();
			}
			
			if (c == ++index) {
				return thisBill.getRefundAmount();
			}
			
			if (c == ++index) {
				// return formatDateTime(thisBill.getUpdate());
				return formatDateTimeReport(thisBill.getUpdate());
			}
			if (c == ++index) {
				return thisBill.getStatus();
			}
			if (c == ++index) {
				return thisBill.getBalance();
			}
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

	}

	public String formatDate(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy"); //$NON-NLS-1$
		return format.format(time.getTime());
	}

	/*
	 * public String formatDateTime(GregorianCalendar time) { SimpleDateFormat
	 * format = new SimpleDateFormat("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$ return
	 * format.format(time.getTime()); }
	 */

	public String formatDateTimeReport(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
		return format.format(time.getTime());
	}

	public boolean isSameDay(GregorianCalendar aDate, GregorianCalendar today) {
		return (aDate.get(Calendar.YEAR) == today.get(Calendar.YEAR))
				&& (aDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
				&& (aDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH));
	}

	class StringTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			if (((String) table.getValueAt(row, 7)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (((String) table.getValueAt(row, 7)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			if (Double.parseDouble(table.getValueAt(row, 5).toString()) > 0) { //$NON-NLS-1$
				cell.setForeground(Color.DARK_GRAY);
			}
			return cell;
		}
	}

	class StringCenterTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);
			if (((String) table.getValueAt(row, 7)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (((String) table.getValueAt(row, 7)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			if (Double.parseDouble(table.getValueAt(row, 5).toString()) > 0) { //$NON-NLS-1$
				cell.setForeground(Color.DARK_GRAY);
			}
			return cell;
		}
	}

	class IntegerTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			cell.setFont(new Font(null, Font.BOLD, 12));
			setHorizontalAlignment(CENTER);
			if (((String) table.getValueAt(row, 7)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (((String) table.getValueAt(row, 7)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			if (Double.parseDouble(table.getValueAt(row, 5).toString()) > 0) { //$NON-NLS-1$
				cell.setForeground(Color.DARK_GRAY);
			}
			return cell;
		}
	}

	class DoubleTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(RIGHT);
			if (((String) table.getValueAt(row, 7)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (((String) table.getValueAt(row, 7)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			if (Double.parseDouble(table.getValueAt(row, 5).toString()) > 0) { //$NON-NLS-1$
				cell.setForeground(Color.DARK_GRAY);
			}
			return cell;
		}
	}

	class CenterBoldTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);
			cell.setFont(new Font(null, Font.BOLD, 12));
			if (((String) table.getValueAt(row, 7)).equals("C")) { //$NON-NLS-1$
				cell.setForeground(Color.GRAY);
			}
			if (((String) table.getValueAt(row, 7)).equals("D")) { //$NON-NLS-1$
				cell.setForeground(Color.RED);
			}
			if (Double.parseDouble(table.getValueAt(row, 5).toString()) > 0) { //$NON-NLS-1$
				cell.setForeground(Color.DARK_GRAY);
			}
			return cell;
		}
	}

	private JButton getExportSageButton() {
		if (exportSageButton == null) {
			exportSageButton = new JButton(MessageBundle.getMessage("angal.billbrowser.exportsage"));
			exportSageButton.setMnemonic(KeyEvent.VK_E);
			exportSageButton.addActionListener(new ActionListener() {
				@SuppressWarnings("static-access")
				public void actionPerformed(ActionEvent e) {
					SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
					JFileChooser fcTxt = new JFileChooser();
					fcTxt.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

					int iRetVal = fcTxt.showSaveDialog(BillBrowser.this);
					if (iRetVal == JFileChooser.APPROVE_OPTION) {
						Date today = new Date();
						String strDate = sdfDate.format(today).replace("/", "");
						File txtSageDirectory = fcTxt.getSelectedFile();
						String to = sdfDate.format(dateTo.getTime()).replace("/", "");
						String from = sdfDate.format(dateFrom.getTime()).replace("/", "");
						File salesFile = new File(txtSageDirectory.getAbsoluteFile() + File.separator
								+ Sage.getSage().FILE_PAID_NAME + strDate + "_from_" + from + "_to_" + to + ".txt");
						File cashFile = new File(txtSageDirectory.getAbsoluteFile() + File.separator
								+ Sage.getSage().FILE_CASHDESK_NAME + strDate + "_from_" + from + "_to_" + to + ".txt");
						try {
							boolean resultExportCashTable = SageExporter.exportCashTable(paymentsPeriod, cashFile);
							boolean resultExportSales = SageExporter.exportSales(salesFile, dateFrom, dateTo);
							if (resultExportCashTable && resultExportSales) {
								JOptionPane.showMessageDialog(BillBrowser.this,
										MessageBundle.getMessage("angal.medicalstock.exportsage.succes") + " \n" + "\n "
												+ MessageBundle.getMessage("angal.billbrowser.exportlocation") + ""
												+ "\n " + salesFile + "" + "\n " + cashFile + "",
										MessageBundle.getMessage("angal.exportation.title"),
										JOptionPane.INFORMATION_MESSAGE);
							}
							if (!resultExportSales && resultExportCashTable) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.exportation.problemsales_goodcash") + "\n\n"
												+ cashFile,
										MessageBundle.getMessage("angal.exportation.title"),
										JOptionPane.INFORMATION_MESSAGE);
							}
							if (resultExportSales && !resultExportCashTable) {
								JOptionPane.showMessageDialog(null,
										MessageBundle.getMessage("angal.exportation.goodsales_problemcash") + "\n\n"
												+ salesFile,
										MessageBundle.getMessage("angal.exportation.title"),
										JOptionPane.INFORMATION_MESSAGE);
							}
						} catch (IOException exc) {
							logger.info("Export to sage txt error : " + exc.getMessage());
							JOptionPane.showMessageDialog(BillBrowser.this,
									MessageBundle.getMessage("angal.medicalstock.exportsage.error"),
									MessageBundle.getMessage("angal.exportation.title"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
		return exportSageButton;
	}

	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel(MessageBundle.getMessage("angal.patient.select.apatient"));
		}
		return lblNewLabel;
	}

	public void patientSelected(Patient patient) {
		patientParent = patient;
		jAffiliatePersonJTextField.setText(
				patientParent != null ? patientParent.getFirstName() + " " + patientParent.getFirstName() : "");
		if (patientParent != null) {
			/*** remove selected item ***/
			chooseItem = null;
			/*** remove selected garante ***/
			garantUserChoose = null;
			if (medicalJTextField != null)
				medicalJTextField.setText("");
			/*****************************/
			updateDataSet(dateFrom, dateTo, patientParent);
			updateTables();
			updateTotals();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {

	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {

	}

	public Patient getPatientParent() {
		return patientParent;
	}

	public void setPatientParent(Patient patientParent) {
		this.patientParent = patientParent;
	}

	private JPanel getPanelChooseMedical() {
		if (panelChooseMedical == null) {
			panelChooseMedical = new JPanel();
			panelChooseMedical.setBorder(new LineBorder(new Color(0, 0, 0)));
			panelChooseMedical.setMinimumSize(new Dimension(300, 10));

			panelChooseMedical.setLayout(new FlowLayout(FlowLayout.LEFT));

			chooseMedicalJLabel = new JLabel("Choose Medical");

			chooseMedicalJButtonAdd = new JButton();
			chooseMedicalJButtonAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					PriceListManager pManager = new PriceListManager();
					@SuppressWarnings("unused")
					ArrayList<Price> priceList = new ArrayList<Price>();
					priceList = pManager.getPrices();
					//
					// get bill items
					BillBrowserManager billmanager = new BillBrowserManager();
					ArrayList<BillItems> itemsList = new ArrayList<BillItems>();
					itemsList = billmanager.getDistinctItems();
					//
					OhTableModel<BillItems> modelOh = new OhTableModel<BillItems>(itemsList, true);
					BillItemPicker itemChooser = new BillItemPicker(modelOh);

					itemChooser.setSize(300, 400);

					JDialog dialog = new JDialog();
					dialog.setLocationRelativeTo(null);
					dialog.setSize(600, 350);
					dialog.setLocationRelativeTo(null);
					dialog.setModal(true);

					itemChooser.setParentFrame(dialog);
					dialog.setContentPane(itemChooser);
					dialog.setIconImage(ico);
					dialog.setVisible(true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

					BillItems billItem = (BillItems) itemChooser.getSelectedObject();
					if (billItem != null) {
						chooseItem = billItem;
						if (medicalJTextField != null)
							medicalJTextField.setText(chooseItem != null ? chooseItem.getItemDescription() : "");
						if (chooseItem != null) {
							/** remove selected patient if any **/
							patientParent = null;
							jAffiliatePersonJTextField.setText("");
							/***********************************/
							/** remove selected garant if any **/
							garantUserChoose = null;
							comboGaranti.setSelectedItem(null);
							/***********************************/
							updateDataSet(dateFrom, dateTo, chooseItem);
							updateTables();
							updateTotals();
						}
					}
				}
			});
			chooseMedicalJButtonAdd.setIcon(new ImageIcon("rsc/icons/icon_pill.png"));

			chooseMedicalJButtonSupp = new JButton();
			chooseMedicalJButtonSupp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					chooseItem = null;
					garantUserChoose = null;
					comboGaranti.setSelectedItem(null);
					/***********************************/
					if (medicalJTextField != null)
						medicalJTextField.setText("");
					updateDataSet(dateFrom, dateTo);
					updateTables();
					updateTotals();
				}
			});
			chooseMedicalJButtonSupp.setIcon(new ImageIcon("rsc/icons/delete_button.png"));

			medicalJTextField = new JTextField(14);
			medicalJTextField.setEnabled(false);

			// if( Param.bool("ALLOWFILTERBILLBYMEDICAL")){
			panelChooseMedical.add(chooseMedicalJLabel);
			panelChooseMedical.add(medicalJTextField);
			panelChooseMedical.add(chooseMedicalJButtonAdd);
			panelChooseMedical.add(chooseMedicalJButtonSupp);
			panelChooseMedical.add(getLblSpace());
			panelChooseMedical.add(getLblChooseGaranti());
			panelChooseMedical.add(getComboGaranti());
			panelChooseMedical.add(getButton_1());
			// }
			return panelChooseMedical;

		}
		return panelChooseMedical;
	}

	private JPanel getPanelSupRange() {
		if (panelSupRange == null) {
			panelSupRange = new JPanel();
			panelSupRange.setLayout(new FlowLayout(FlowLayout.LEFT));
			if (!Param.bool("SINGLEUSER") && user.equals("admin"))
				panelSupRange.add(getJComboUsers());
			panelSupRange.add(getJButtonToday());
			panelSupRange.add(getJLabelFrom());
			panelSupRange.add(getJCalendarFrom());
			panelSupRange.add(getJLabelTo());
			panelSupRange.add(getJCalendarTo());
			panelSupRange.add(getJComboMonths());
			panelSupRange.add(getJComboYears());
			panelSupRange.add(getLblNewLabel());
			panelSupRange.add(getPanelChoosePatient());
		}
		return panelSupRange;
	}

	private JLabel getLblSpace() {
		if (lblSpace == null) {
			lblSpace = new JLabel("                   ");
			lblSpace.setForeground(SystemColor.control);
		}
		return lblSpace;
	}

	private JLabel getLblChooseGaranti() {
		if (lblChooseGaranti == null) {
			lblChooseGaranti = new JLabel("S\u00E9lectionnez une personne garante");
		}
		return lblChooseGaranti;
	}

	private JComboBox<User> getComboGaranti() {
		if (comboGaranti == null) {
			comboGaranti = new JComboBox<User>();
			comboGaranti.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					User userr = (User) comboGaranti.getSelectedItem();
					// String userr = (String) comboGaranti.getSelectedItem();
					if (userr != null) {
						garantUserChoose = userr;
						if (userr != null) {
							/** remove selected patient if any **/
							patientParent = null;
							jAffiliatePersonJTextField.setText("");
							/***********************************/

							/** remove selected medical if any **/
							chooseItem = null;
							medicalJTextField.setText("");
							chooseItem = null;
							/***********************************/
							updateDataSet(dateFrom, dateTo, userr);
							updateTables();
							updateTotals();
						}
					} else {
						garantUserChoose = null;
						updateDataSet(dateFrom, dateTo);
						updateTables();
						updateTotals();
					}
				}
			});
			UserBrowsingManager manager = new UserBrowsingManager();
			usersG = manager.getUser();
			comboGaranti.addItem(null);
			for (User u : usersG) {
				// comboGaranti.addItem(u.getUserName()+" | nom= "+u.getName()+"
				// "+u.getSurname());
				comboGaranti.addItem(u);
			}
			Dimension d = comboGaranti.getPreferredSize();
			comboGaranti.setPreferredSize(new Dimension(350, d.height));
		}
		return comboGaranti;
	}

	private JButton getButton_1() {
		if (printGaranteButton == null) {
			printGaranteButton = new JButton("Print Garante");
			printGaranteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (comboGaranti.getSelectedItem() == null) {
						JOptionPane.showMessageDialog(BillBrowser.this,
								MessageBundle.getMessage("angal.billbrowser.please.select_garante"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
								JOptionPane.CANCEL_OPTION);
						return;
					}

					if (jTableBills.getModel().getRowCount() <= 0 || jTablePending.getModel().getRowCount() <= 0) {
						JOptionPane.showMessageDialog(BillBrowser.this,
								MessageBundle.getMessage("angal.billbrowser.no.facture.for.this.garante"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.hospital"), //$NON-NLS-1$
								JOptionPane.CANCEL_OPTION);
						return;
					}

					User garante = (User) comboGaranti.getSelectedItem();
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

					String fromDate = sdf.format(dateFrom.getTime());
					String toDate = sdf.format(dateTo.getTime());
					new GenericReportBill(fromDate, toDate, Param.string("GARANTEBILLSREPORT"), garante);
				}
			});
		}
		return printGaranteButton;
	}
}
