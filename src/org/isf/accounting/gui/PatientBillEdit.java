package org.isf.accounting.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.chainsaw.Main;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.events.KeyAdapter;
import org.hibernate.internal.util.SerializationHelper;
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItemListItem;
import org.isf.accounting.model.BillItemPayments;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.TxtPrinter;
import org.isf.lab.manager.LabManager;
import org.isf.medicalinventory.gui.InventoryWardEdit;
import org.isf.medicalstock.gui.MovStockMultipleCharging;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.parameters.manager.Param;
import org.isf.patient.gui.SelectPatient;
import org.isf.patient.gui.SelectPatient.SelectionListener;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.List;
import org.isf.priceslist.model.Price;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.reduction.manager.ReductionPlanManager;
import org.isf.reduction.model.ReductionPlan;
import org.isf.stat.manager.GenericReportBill;
import org.isf.supplier.gui.SupplierEdit;
import org.isf.accounting.gui.SelectPrescriptions;
import org.isf.accounting.gui.SelectPrescriptions.PrescriptionSelectionListener;
import org.isf.accounting.gui.SelectPrescriptions.TherapySelectionListener;
import org.isf.therapy.manager.TherapyManager;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.jobjects.OhTableModel;
import org.isf.utils.time.RememberDates;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

import com.toedter.calendar.JDateChooser;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Component;

/**
 * Create a single Patient Bill it affects tables BILLS, BILLITEMS and
 * BILLPAYMENTS
 * 
 * @author Mwithi
 * 
 */
//public class PatientBillEdit extends JDialog
public class PatientBillEdit extends JDialog implements SelectionListener, PrescriptionSelectionListener {

	// LISTENER INTERFACE
	// --------------------------------------------------------
	private EventListenerList patientBillListener = new EventListenerList();

	public interface PatientBillListener extends EventListener {
		public void billInserted(AWTEvent aEvent);
	}

	public interface BillItemEditListener extends EventListener {
		public void billItemEdited(AWTEvent event);
	}

	public void addPatientBillListener(PatientBillListener l) {
		patientBillListener.add(PatientBillListener.class, l);
	}

	private void fireBillInserted(Bill aBill) {
		AWTEvent event = new AWTEvent(aBill, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = patientBillListener.getListeners(PatientBillListener.class);
		for (int i = 0; i < listeners.length; i++)
			((PatientBillListener) listeners[i]).billInserted(event);
	}

	private void initPriceList() {
		prcListArray = new ArrayList<Price>();

		/*
		 * seleziona i prezzi del listino selezionato. se nessun listino e'
		 * selezionato (new bill) si prende il primo.
		 */
		if (listSelected == null)
			listSelected = lstArray.get(0);

		if (patientSelected != null && patientSelected.getListID() != 0) {
			listSelected = prcManager.getListById(patientSelected.getListID());
		}

		for (Price price : prcArray) {
			if (price.getList() == listSelected.getId())
				prcListArray.add(price);
		}

	}

	// ---------------------------------------------------------------------------

	public void patientSelected(Patient patient) {
	    patientSelected = patient;
		setPatientSelected(patient);
        ArrayList<Bill> patientPendingBills = billManager.getPendingBills(patient.getCode());
		if (patientPendingBills.isEmpty()) {
			// BILL
			thisBill.setPatID(patientSelected.getCode());
			thisBill.setPatient(true);
			thisBill.setPatName(patientSelected.getName());
		} else {
			if (patientPendingBills.size() == 1) {
				if(Param.bool("ALLOWMULTIPLEOPENEDBILL")){
					int response = JOptionPane.showConfirmDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.admission.thispatienthasapendingbillcreateanother"), 
							MessageBundle.getMessage("angal.admission.bill"), 
							JOptionPane.YES_NO_OPTION); 
					if(response==JOptionPane.YES_OPTION){
						insert = true;
							thisBill.setPatID(patientSelected.getCode());
							thisBill.setPatient(true);
							thisBill.setPatName(patientSelected.getName());							
					}else{
						insert = false;
						setBill(patientPendingBills.get(0));
						
						/******* Check if it is same month ***************/
						checkIfsameMonth();
						/*************************************************/
					}
				}
				else{
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.admission.thispatienthasapendingbill"),
							MessageBundle.getMessage("angal.admission.bill"), JOptionPane.PLAIN_MESSAGE);					
					insert = false;
					setBill(patientPendingBills.get(0));
					
					/******* Check if it is same month ***************/
					checkIfsameMonth();
					/*************************************************/
				}
			} else {

				if(Param.bool("ALLOWMULTIPLEOPENEDBILL")){
					int response = JOptionPane.showConfirmDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.admission.thereismorethanonependingbillforthispatientcontinuecreateanother"), 
							MessageBundle.getMessage("angal.admission.bill"), 
							JOptionPane.YES_NO_OPTION); 
					
					if(response==JOptionPane.YES_OPTION){
						insert = true;
							thisBill.setPatID(patientSelected.getCode());
							thisBill.setPatient(true);
							thisBill.setPatName(patientSelected.getName());						
					}else if(response==JOptionPane.NO_OPTION) {
						//il faut proposer quelque chose
						int resp = JOptionPane.showConfirmDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.admission.thereismorethanonependingbillforthispatientopenlastopenenedbill"), 
								MessageBundle.getMessage("angal.admission.bill"), 
								JOptionPane.YES_NO_OPTION);
						if(resp==JOptionPane.YES_OPTION){							
							insert = false;
							setBill(patientPendingBills.get(0));						
							/******* Check if it is same month ***************/
							checkIfsameMonth();
							/*************************************************/
						}
					}else{
						return;
					}
				}else{
					JOptionPane.showConfirmDialog(null,MessageBundle.getMessage("angal.admission.thereismorethanonependingbillforthispatientcontinue"),
							MessageBundle.getMessage("angal.admission.bill"), JOptionPane.WARNING_MESSAGE);
					return;
				}				
			}
		}
		updateUI();

		jTextFieldSearch.setEnabled(true);
		jTextFieldSearch.grabFocus();
		checkIfsameMonth();
		// TODO qsdfqsf
		// jTableBill.setModel(new BillTableModel());
		// updateTotals();
	}

	private static final long serialVersionUID = 1L;
	private JTable jTableBill;
	private JScrollPane jScrollPaneBill;
	private JButton jButtonAddMedical;
	private JButton jButtonAddPrescription;
	private JButton jButtonAddOperation;
	private JButton jButtonAddExam;
	private JButton jButtonAddOther;
	private JButton jButtonAddPayment;
	private JPanel jPanelButtons;
	private JPanel jPanelDate;
	private JPanel jPanelPatient;
	private JTable jTablePayment;
	private JScrollPane jScrollPanePayment;
	private JScrollPane jScrollPaneItemPayment;
	private JTextField jTextFieldPatient;
	private JPanel jPanelData;
	private JTable jTableTotal;
	private JScrollPane jScrollPaneTotal;
	private JTable jTableBigTotal;
	private JScrollPane jScrollPaneBigTotal;
	private JTable jTableBalance;
	private JScrollPane jScrollPaneBalance;
	private JPanel jPanelTop;
	private JDateChooser jCalendarDate;
	private JLabel jLabelDate;
	private JLabel jLabelPatient;
	private JLabel jLabelPaymentAmount = new JLabel();
	private JButton jButtonRemoveItem;
	// private JLabel jLabelPriceList;
	private JButton jButtonRemovePayment;
	private JButton jButtonAddRefund;
	private JPanel jPanelButtonsPayment;
	private JPanel jPanelButtonsBill;
	private JPanel jPanelButtonsActions;
	private JPanel jPanelButtonsItemPaymentActions;
	private JButton jButtonClose;
	private JButton jButtonPaid;
	private JButton jButtonPrintPayment;
	private JButton jButtonSave;
	private JButton jButtonSaveItemPayments = new JButton();
	private JButton jButtonCloseItemPayments = new JButton();
	private JButton jButtonBalance;
	private JButton jButtonCustom;
	private JButton jButtonPickPatient;
	private JButton jButtonTrashPatient;
	JDialog billItemPayDialog = new JDialog();
	ArrayList<BillItemListItem> itemListItems = new ArrayList<BillItemListItem>();
	
	private ArrayList<User> users;

	private static final Dimension PatientDimension = new Dimension(300, 30);
	private static final Dimension LabelsDimension = new Dimension(60, 30);
	private static final int PanelWidth = 450;
	private static final int ButtonWidth = 160;
	private static final int ButtonWidthBill = 160;
	private static final int ButtonWidthPayment = 160;
	private static final int PriceWidth = 150;
	private static final int QuantityWidth = 40;
	private static final int BillHeight = 200;
	private static final int TotalHeight = 30;
	private static final int BigTotalHeight = 30;
	private static final int PaymentHeight = 150;
	private static final int BalanceHeight = 30;
	// private static final int ActionHeight = 100;
	private static final int ButtonHeight = 25;

	private BigDecimal total = new BigDecimal(0);
	private BigDecimal bigTotal = new BigDecimal(0);
	private BigDecimal balance = new BigDecimal(0);
	private int billID;
	private List listSelected;
	private boolean insert;
	private boolean modified = false;
	private boolean keepDate = true;
	private boolean paid = false;
	private Bill thisBill;
	private Patient patientSelected = null;
	private int pbiID = 0;
	private Double paymentAmount = new Double(0);
	private GregorianCalendar paymentDate = new GregorianCalendar();
	//private boolean foundList;
	private GregorianCalendar billDate = TimeTools.getServerDateTime();
	private GregorianCalendar today = TimeTools.getServerDateTime();

	private Object[] billClasses = { Price.class, Integer.class, Double.class };
	private String[] billColumnNames = { MessageBundle.getMessage("angal.newbill.item"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.newbill.qty"), MessageBundle.getMessage("angal.newbill.amount") }; //$NON-NLS-1$ //$NON-NLS-2$
	private Object[] paymentClasses = { Date.class, Double.class };

	// Prices and Lists (ALL)
	private PriceListManager prcManager = new PriceListManager();
	private ReductionPlanManager reductionPlanManager = new ReductionPlanManager();
	private ArrayList<Price> prcArray = prcManager.getPrices();
	private ArrayList<List> lstArray = prcManager.getLists();

	// PricesOthers (ALL)
	private PricesOthersManager othManager = new PricesOthersManager();
	private ArrayList<PricesOthers> othPrices = othManager.getOthers();

	// Items and Payments (ALL)
	private BillBrowserManager billManager = new BillBrowserManager();
	private PatientBrowserManager patManager = new PatientBrowserManager();

	// Prices, Items and Payments for the tables
	private ArrayList<BillItems> billItems = new ArrayList<BillItems>();
	private ArrayList<BillItemPayments> billPayItems = new ArrayList<BillItemPayments>();
	private ArrayList<BillItemPayments> billPaidItems = new ArrayList<BillItemPayments>();
	private ArrayList<BillItems> billItemsRemoved = new ArrayList<BillItems>();
	private ArrayList<BillPayments> payItems = new ArrayList<BillPayments>();
	private ArrayList<Price> prcListArray = new ArrayList<Price>();
	private ArrayList<MedicalWard> medWardList = new ArrayList<MedicalWard>();
	//private Ward selectedWard;
	private BillItems selectedBillItem = null;
	//private int billItemsSaved;
	private int payItemsSaved;

	// User
	private String user = MainMenu.getUser();
	private JComboBox comboBox;
	private JComboBox wardBox;
	private JLabel wardLabel;

	OhDefaultCellRenderer cellRenderer = new OhDefaultCellRenderer();

	private Image ico;
	private JPanel jPanelSearch;
	
	private JPanel jPanelLabel;
	
	private JTextField jTextFieldSearch;
	private JTextField jTextFieldDescription;
	private JTextField jTextFieldQty;
	private JTextField jTextFieldPrice;
	private JLabel lblQty;
	private JLabel lblAmount;
	private JLabel lblDescription;
	private JLabel lblTexteDeRecherche;
	
	private JLabel enterPatientCodeLabel;
	private JPanel jPanelGarante;
	private JLabel lblGarante;
	private JComboBox jComboGarante;
	private JComboBox patientComboBox = null;

	private ArrayList<Integer> examsList = new ArrayList<Integer>();
	private ArrayList<Integer> operationsList = new ArrayList<Integer>();
	private ArrayList<Integer> medicalsList = new ArrayList<Integer>();
	private ArrayList<Integer> othersList = new ArrayList<Integer>();
	
	public PatientBillEdit() {
		PatientBillEdit newBill = new PatientBillEdit(null, new Bill(), true);
		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
		newBill.setVisible(true);
		//modal exclude

	}

	public PatientBillEdit(JFrame owner, Patient patient) {

		Bill bill = new Bill();
		//this.owner = owner;
		bill.setPatient(true);
		bill.setPatID(patient.getCode());
		bill.setPatName(patient.getName());
		PatientBillEdit newBill = new PatientBillEdit(owner, bill, true);
		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
		newBill.setPatientSelected(patient);
		newBill.setVisible(true);

	}

	public PatientBillEdit(JFrame owner, Bill bill, boolean inserting) {
		super(owner, true);		
		this.insert = inserting; //INSERT = TRUE
		
		setBill(bill);
		initComponents();
		
		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
		updateTotals();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setLocationRelativeTo(null);
		setResizable(false);	
		jButtonPickPatient.doClick();
	}

	private void setBill(Bill bill) {
		this.thisBill = bill; 
		billDate = bill.getDate();
		billItems = billManager.getItems(thisBill.getId());
		payItems = billManager.getPayments(thisBill.getId());
		billPayItems = billManager.getItemPayments(thisBill.getId());
		billPaidItems = billManager.getItemPayments(thisBill.getId());
		//billItemsSaved = billItems.size();
		payItemsSaved = payItems.size();
		if (!insert) {
			checkBill();
		}
		//populate combogarante 
		if (thisBill != null && !insert) {	
			if(thisBill.getGarante()!= null && thisBill.getGarante()!=""){
				try{	
					if(jComboGarante != null ){
						jComboGarante.setSelectedItem(thisBill.getGarante());
					}
				}catch (Exception e) {
					System.out.println("error "+ e.getMessage());
				}				
			}
			if(thisBill.getPatName() != null && !thisBill.getPatName().equals("") && thisBill.getPatID() == 0){
				PatientBrowserManager patManager = new PatientBrowserManager();
				try{	
					Patient pat = patManager.getPatient(thisBill.getPatName());
					setPatientSelected(pat);
					thisBill.setPatID(pat.getCode());
					thisBill.setPatient(true);
				}catch (Exception e) {
					System.out.println("error "+ e.getMessage());
				}
			}
		}
	}

	private void initComponents() {
		getContentPane().add(getJPanelTop(), BorderLayout.NORTH);
		
		getContentPane().add(getJPanelData(), BorderLayout.CENTER);
		
		getContentPane().add(getJPanelButtons(), BorderLayout.EAST);
		if (insert) {
			setTitle(MessageBundle.getMessage("angal.newbill.title")); //$NON-NLS-1$
		} else {
			setTitle(MessageBundle.getMessage("angal.newbill.title") + " " + thisBill.getId()); //$NON-NLS-1$
		}
		pack();
		
		/***************/
		WindowListener exitListener = new WindowAdapter() {
			
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	
		    }
		    
		    @Override
		    public void windowOpened(WindowEvent e) {
//		    	JFrame parent = (JFrame) ((JDialog)e.getSource()).getParent();   
//		    	parent.setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
		    }
		};
		this.addWindowListener(exitListener);
		/***************/
		
		/******* Check if it is same month ***************/
		checkIfsameMonth();
		/*************************************************/
	}
	
	public void checkIfsameMonth(){
		if (!insert) {
			GregorianCalendar thisday = TimeTools.getServerDateTime();
			GregorianCalendar billDate = thisBill.getDate();
			int thisMonth = thisday.get(GregorianCalendar.MONTH);
			int billMonth = billDate.get(GregorianCalendar.MONTH);
			int thisYear = thisday.get(GregorianCalendar.YEAR);
			int billBillYear = billDate.get(GregorianCalendar.YEAR);
			if(thisYear>billBillYear || (thisMonth>billMonth && !Param.bool("ENABLEBILLEDITOVERMONTH"))){
				jButtonAddMedical.setEnabled(false);
				jButtonAddOperation.setEnabled(false);
				jButtonAddExam.setEnabled(false);
				jButtonAddOther.setEnabled(false);
				jButtonCustom.setEnabled(false);
				jButtonRemoveItem.setEnabled(false);
				jTextFieldSearch.setEnabled(false);
				jTextFieldDescription.setEnabled(false);
				jTextFieldQty.setEnabled(false);
				jTextFieldPrice.setEnabled(false);
				jButtonAddPrescription.setEnabled(false);
				jCalendarDate.grabFocus(); 
			}
		}
	}


	// check if PriceList and Patient still exist
	private void checkBill() {

		if (thisBill.isPatient()) {

			Patient patient = patManager.getPatient(thisBill.getPatID());
			if (patient != null) {
				setPatientSelected(patient);
								
				
			} else { // Patient not found
				Icon icon = new ImageIcon("rsc/icons/patient_dialog.png"); //$NON-NLS-1$
				JOptionPane.showMessageDialog(PatientBillEdit.this,
						MessageBundle.getMessage("angal.newbill.patientassociatedwiththisbillnolongerexists") + //$NON-NLS-1$
								"no longer exists", //$NON-NLS-1$
						"Warning", //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE, icon);

				thisBill.setPatient(false);
				thisBill.setPatID(0);
			}
		}
	}

	private JPanel getJPanelData() {
		if (jPanelData == null) {
			jPanelData = new JPanel();
			jPanelData.setLayout(new BoxLayout(jPanelData, BoxLayout.Y_AXIS));
			jPanelData.add(getJScrollPaneTotal());
			jPanelData.add(getJScrollPaneBill());
			jPanelData.add(getJScrollPaneBigTotal());
			jPanelData.add(getJScrollPanePayment());
			jPanelData.add(getJScrollPaneBalance());
		}
		return jPanelData;
	}
	
	private JTable getJTableItemPayment() {
		for (BillItems billItem : this.billItems) {
			BillItemListItem itemListItem = new BillItemListItem(billItem, billPaidItems, false, 0.0);
			if(itemListItem.getToPay() > 0.0) {
				itemListItems.add(itemListItem);
			}
		}
		BillItemPaymentTableModel itemPaymentTableModel = new BillItemPaymentTableModel(
				itemListItems, billItemPayDialog, paymentAmount
		);
		JTable jTableItemPayment = new JTable(itemPaymentTableModel);
		
		return jTableItemPayment;
	}
	
	private void chooseItemsToPay(GregorianCalendar datePay, Double amount) {
		this.paymentAmount = amount;
		this.paymentDate = datePay;
		JTable jTableItemPayment = getJTableItemPayment();
		BillItemPaymentDialog.initComponent(billItemPayDialog, jTableItemPayment, getJPanelButtonsItemPaymentActions());
	}

	private JPanel getJPanelPatient() {
		if (jPanelPatient == null) {
			jPanelPatient = new JPanel();
			jPanelPatient.setLayout(new FlowLayout(FlowLayout.LEFT));
			jPanelPatient.add(getJLabelPatient());
			jPanelPatient.add(getJTextFieldPatient());
			jPanelPatient.add(getWardLabel());
			jPanelPatient.add(getWardBox());
		}
		return jPanelPatient;
	}
	private JLabel getJLabelPatient() {
		if (jLabelPatient == null) {
			jLabelPatient = new JLabel();
			jLabelPatient.setText(MessageBundle.getMessage("angal.newbill.patient")); //$NON-NLS-1$
			jLabelPatient.setPreferredSize(LabelsDimension);
		}
		return jLabelPatient;
	}

	private JTextField getJTextFieldPatient() {
		if (jTextFieldPatient == null) {
			jTextFieldPatient = new JTextField();
			jTextFieldPatient.setText(""); //$NON-NLS-1$
			jTextFieldPatient.setPreferredSize(PatientDimension);
			if (thisBill.isPatient()) {
				jTextFieldPatient.setText(thisBill.getPatName());
			}
			jTextFieldPatient.setEditable(false);
		}
		return jTextFieldPatient;
	}

	private JDateChooser getJCalendarDate() {
		if (jCalendarDate == null) {
			if (insert) {
				billDate.set(Calendar.YEAR, RememberDates.getLastBillDateGregorian().get(Calendar.YEAR));
				billDate.set(Calendar.MONTH, RememberDates.getLastBillDateGregorian().get(Calendar.MONTH));
				billDate.set(Calendar.DAY_OF_MONTH,RememberDates.getLastBillDateGregorian().get(Calendar.DAY_OF_MONTH));
				//System.out.println("la date 2 "+billDate.getTime().toString());
				
				jCalendarDate = new JDateChooser(billDate.getTime());
			} else {
				// get BillDate
				//System.out.println("insert is false get old date");
				jCalendarDate = new JDateChooser(thisBill.getDate().getTime());
				billDate.setTime(jCalendarDate.getDate());
			}
			jCalendarDate.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarDate.setDateFormatString("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$
			jCalendarDate.getJCalendar().addPropertyChangeListener("calendar", new PropertyChangeListener() { //$NON-NLS-1$

				public void propertyChange(PropertyChangeEvent evt) {

					if (!insert) {
						if (keepDate && evt.getNewValue().toString().compareTo(evt.getOldValue().toString()) != 0) {

							Icon icon = new ImageIcon("rsc/icons/clock_dialog.png"); //$NON-NLS-1$
							int ok = JOptionPane.showConfirmDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.doyoureallywanttochangetheoriginaldate"), //$NON-NLS-1$
									"Warning", //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
							if (ok == JOptionPane.YES_OPTION) {
								keepDate = false;
								modified = true;
								jCalendarDate.setDate(((Calendar) evt.getNewValue()).getTime());
							} else {
								jCalendarDate.setDate(((Calendar) evt.getOldValue()).getTime());
							}
						} else {
							jCalendarDate.setDate(((Calendar) evt.getNewValue()).getTime());
						}
						billDate.setTime(jCalendarDate.getDate());
					} else {
						jCalendarDate.setDate(((Calendar) evt.getNewValue()).getTime());
						billDate.setTime(jCalendarDate.getDate());
					}
				}
			});
		}
		return jCalendarDate;
	}

	private JLabel getJLabelDate() {
		if (jLabelDate == null) {
			jLabelDate = new JLabel();
			jLabelDate.setText(MessageBundle.getMessage("angal.common.date")); //$NON-NLS-1$
			jLabelDate.setPreferredSize(LabelsDimension);
		}
		return jLabelDate;
	}

	private JPanel getJPanelDate() {
		if (jPanelDate == null) {
			jPanelDate = new JPanel();
			jPanelDate.setLayout(new FlowLayout(FlowLayout.LEFT));
			jPanelDate.add(getJLabelDate());
			jPanelDate.add(getJCalendarDate());
			jPanelDate.add(getJButtonPickPatient());
			jPanelDate.add(getJButtonTrashPatient());
		}
		return jPanelDate;
	}

	private JButton getJButtonTrashPatient() {
		if (jButtonTrashPatient == null) {
			jButtonTrashPatient = new JButton();
			jButtonTrashPatient.setMnemonic(KeyEvent.VK_R);
			jButtonTrashPatient.setPreferredSize(new Dimension(25, 25));
			jButtonTrashPatient.setIcon(new ImageIcon("rsc/icons/remove_patient_button.png")); //$NON-NLS-1$
			jButtonTrashPatient.setToolTipText(
					MessageBundle.getMessage("angal.newbill.tooltip.removepatientassociationwiththisbill")); //$NON-NLS-1$
			jButtonTrashPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					patientSelected = null;
					// BILL
					thisBill.setPatient(false);
					thisBill.setPatID(0);
					thisBill.setPatName(""); //$NON-NLS-1$
					// INTERFACE
					jTextFieldPatient.setText(""); //$NON-NLS-1$
					jTextFieldPatient.setEditable(false);
					jButtonPickPatient.setText(MessageBundle.getMessage("angal.newbill.pickpatient"));
					jButtonPickPatient.setToolTipText(
							MessageBundle.getMessage("angal.newbill.tooltip.associateapatientwiththisbill")); //$NON-NLS-1$
					jButtonTrashPatient.setEnabled(false);
				}
			});
			if (!thisBill.isPatient()) {
				jButtonTrashPatient.setEnabled(false);
			}
		}
		return jButtonTrashPatient;
	}

	private JButton getJButtonPickPatient() {
		if (jButtonPickPatient == null) {
			jButtonPickPatient = new JButton();
			jButtonPickPatient.setText(MessageBundle.getMessage("angal.newbill.pickpatient")); //$NON-NLS-1$
			jButtonPickPatient.setMnemonic(KeyEvent.VK_P);
			jButtonPickPatient.setIcon(new ImageIcon("rsc/icons/pick_patient_button.png")); //$NON-NLS-1$
			jButtonPickPatient
					.setToolTipText(MessageBundle.getMessage("angal.newbill.tooltip.associateapatientwiththisbill")); //$NON-NLS-1$

			jButtonPickPatient.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					SelectPatient sp = new SelectPatient(PatientBillEdit.this, patientSelected);
					sp.addSelectionListener(PatientBillEdit.this);
					sp.pack();
					sp.setVisible(true);
				}
			});

			if (thisBill.isPatient()) {
				jButtonPickPatient.setText(MessageBundle.getMessage("angal.newbill.changepatient")); //$NON-NLS-1$
				jButtonPickPatient.setToolTipText(
						MessageBundle.getMessage("angal.newbill.tooltip.changethepatientassociatedwiththisbill")); //$NON-NLS-1$
				jButtonPickPatient.setEnabled(false);  //AJOUT MARCO
				getJButtonTrashPatient().setEnabled(false);
			}
		}
		return jButtonPickPatient;
	}

	public void setPatientSelected(Patient patientSelected) {
		this.patientSelected = patientSelected;
		
		System.out.println("lecture param "+Param.string("CLIENTXMPPLOCATION"));
		if (this.jButtonAddPrescription != null) {
			if (billManager.hasPrescription(this.patientSelected.getCode())) {
				this.jButtonAddPrescription.setVisible(true);
			} else {
				this.jButtonAddPrescription.setVisible(false);
			}
		}

		this.pbiID = patientSelected.getReductionPlanID();

		initPriceList();

		if (jTableBill != null) {
			
			jTableBill.setModel(new BillTableModel());
		}

	}

	private JPanel getJPanelTop() {
		if (jPanelTop == null) {
			jPanelTop = new JPanel();
			jPanelTop.setLayout(new BoxLayout(jPanelTop, BoxLayout.Y_AXIS));
			jPanelTop.add(getJPanelDate()); 
			jPanelTop.add(getJPanelPatient()); 
			if(Param.bool("ALLOWGARANTEPERSON")){
				jPanelTop.add(getJPanelGarante());
			} 
			jPanelTop.add(getPanelLabel());
			jPanelTop.add(getPanelSearch());
		}
		return jPanelTop;
	}

	private JScrollPane getJScrollPaneBill() {
		if (jScrollPaneBill == null) {
			jScrollPaneBill = new JScrollPane();
			jScrollPaneBill.setBorder(null);
			jScrollPaneBill.setViewportView(getJTableBill());
			jScrollPaneBill.setMaximumSize(new Dimension(PanelWidth, BillHeight));
			jScrollPaneBill.setMinimumSize(new Dimension(PanelWidth, BillHeight));
			jScrollPaneBill.setPreferredSize(new Dimension(PanelWidth, BillHeight));

		}
		return jScrollPaneBill;
	}

	private JTable getJTableBill() {
		if (jTableBill == null) {
			jTableBill = new JTable();
			/*** apply default oh cellRender *****/
			jTableBill.setDefaultRenderer(Object.class, cellRenderer);
			jTableBill.setDefaultRenderer(Double.class, cellRenderer);
			jTableBill.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
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
					// TODO Auto-generated method stub

				}
			});
			jTableBill.setModel(new BillTableModel());
			jTableBill.getColumnModel().getColumn(1).setMinWidth(QuantityWidth);
			jTableBill.getColumnModel().getColumn(1).setMaxWidth(QuantityWidth);
			jTableBill.getColumnModel().getColumn(2).setMinWidth(PriceWidth);
			jTableBill.getColumnModel().getColumn(2).setMaxWidth(PriceWidth);
			jTableBill.setAutoCreateColumnsFromModel(false);
			jTableBill.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						int selectedRow = jTableBill.getSelectedRow();
						if (selectedRow >= 0) {
							BillItems item = billItems.get(jTableBill.getSelectedRow());
							selectedBillItem = item;
							loadFields();							
						}
					}
				}
			});
		}
		return jTableBill;
	}

	private JScrollPane getJScrollPaneBigTotal() {
		if (jScrollPaneBigTotal == null) {
			jScrollPaneBigTotal = new JScrollPane();
			jScrollPaneBigTotal.setViewportView(getJTableBigTotal());
			jScrollPaneBigTotal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneBigTotal.setMaximumSize(new Dimension(PanelWidth, BigTotalHeight));
			jScrollPaneBigTotal.setMinimumSize(new Dimension(PanelWidth, BigTotalHeight));
			jScrollPaneBigTotal.setPreferredSize(new Dimension(PanelWidth, BigTotalHeight));
		}
		return jScrollPaneBigTotal;
	}

	private JScrollPane getJScrollPaneTotal() {
		if (jScrollPaneTotal == null) {
			jScrollPaneTotal = new JScrollPane();
			jScrollPaneTotal.setViewportView(getJTableTotal());
			jScrollPaneTotal.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneTotal.setMaximumSize(new Dimension(PanelWidth, TotalHeight));
			jScrollPaneTotal.setMinimumSize(new Dimension(PanelWidth, TotalHeight));
			jScrollPaneTotal.setPreferredSize(new Dimension(PanelWidth, TotalHeight));
		}
		return jScrollPaneTotal;
	}

	private JTable getJTableBigTotal() {
		if (jTableBigTotal == null) {
			jTableBigTotal = new JTable();
			jTableBigTotal.setModel(new DefaultTableModel(
					new Object[][] { { "<html><b>" + "TO PAY" + "</b></html>", bigTotal } }, new String[] { "", "" }) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableBigTotal.getColumnModel().getColumn(1).setMinWidth(PriceWidth);
			jTableBigTotal.getColumnModel().getColumn(1).setMaxWidth(PriceWidth);
			jTableBigTotal.setMaximumSize(new Dimension(PanelWidth, BigTotalHeight));
			jTableBigTotal.setMinimumSize(new Dimension(PanelWidth, BigTotalHeight));
			jTableBigTotal.setPreferredSize(new Dimension(PanelWidth, BigTotalHeight));
		}
		return jTableBigTotal;
	}

	private JTable getJTableTotal() {
		if (jTableTotal == null) {
			jTableTotal = new JTable();
			jTableTotal.setModel(new DefaultTableModel(
					new Object[][] {
							{ "<html><b>" + MessageBundle.getMessage("angal.newbill.totalm") + "</b></html>", total } }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					new String[] { "", "" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { JLabel.class, Double.class, };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableTotal.getColumnModel().getColumn(1).setMinWidth(PriceWidth);
			jTableTotal.getColumnModel().getColumn(1).setMaxWidth(PriceWidth);
			jTableTotal.setMaximumSize(new Dimension(PanelWidth, TotalHeight));
			jTableTotal.setMinimumSize(new Dimension(PanelWidth, TotalHeight));
			jTableTotal.setPreferredSize(new Dimension(PanelWidth, TotalHeight));
		}
		/*** apply default oh cellRender *****/
		jTableTotal.setDefaultRenderer(Object.class, cellRenderer);
		jTableTotal.setDefaultRenderer(Double.class, cellRenderer);
		return jTableTotal;
	}

	private JScrollPane getJScrollPanePayment() {
		if (jScrollPanePayment == null) {
			jScrollPanePayment = new JScrollPane();
			jScrollPanePayment.setBorder(null);
			jScrollPanePayment.setViewportView(getJTablePayment());
			jScrollPanePayment.setMaximumSize(new Dimension(PanelWidth, PaymentHeight));
			jScrollPanePayment.setMinimumSize(new Dimension(PanelWidth, PaymentHeight));
			jScrollPanePayment.setPreferredSize(new Dimension(PanelWidth, PaymentHeight));
		}
		return jScrollPanePayment;
	}

	private JTable getJTablePayment() {
		if (jTablePayment == null) {
			jTablePayment = new JTable();
			jTablePayment.setModel(new PaymentTableModel());
			jTablePayment.getColumnModel().getColumn(1).setMinWidth(PriceWidth);
			jTablePayment.getColumnModel().getColumn(1).setMaxWidth(PriceWidth);
		}
		/*** apply default oh cellRender *****/
		jTablePayment.setDefaultRenderer(Object.class, cellRenderer);
		jTablePayment.setDefaultRenderer(Double.class, cellRenderer);

		jTablePayment.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub

			}
		});
		return jTablePayment;
	}

	private JScrollPane getJScrollPaneBalance() {
		if (jScrollPaneBalance == null) {
			jScrollPaneBalance = new JScrollPane();
			jScrollPaneBalance.setViewportView(getJTableBalance());
			jScrollPaneBalance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneBalance.setMaximumSize(new Dimension(PanelWidth, BalanceHeight));
			jScrollPaneBalance.setMinimumSize(new Dimension(PanelWidth, BalanceHeight));
			jScrollPaneBalance.setPreferredSize(new Dimension(PanelWidth, BalanceHeight));
		}
		return jScrollPaneBalance;
	}

	private JTable getJTableBalance() {
		if (jTableBalance == null) {
			jTableBalance = new JTable();
			jTableBalance.setModel(new DefaultTableModel(
					new Object[][] { { "<html><b>" + MessageBundle.getMessage("angal.newbill.balancem") + "</b></html>", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							balance } },
					new String[] { "", "" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] types = new Class<?>[] { Object.class, Double.class, };

				public Class<?> getColumnClass(int columnIndex) {
					return types[columnIndex];
				}

				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			jTableBalance.getColumnModel().getColumn(1).setMinWidth(PriceWidth);
			jTableBalance.getColumnModel().getColumn(1).setMaxWidth(PriceWidth);
			jTableBalance.setMaximumSize(new Dimension(PanelWidth, BalanceHeight));
			jTableBalance.setMinimumSize(new Dimension(PanelWidth, BalanceHeight));
			jTableBalance.setPreferredSize(new Dimension(PanelWidth, BalanceHeight));
		}
		/*** apply default oh cellRender *****/
		jTableBalance.setDefaultRenderer(Object.class, cellRenderer);
		jTableBalance.setDefaultRenderer(Double.class, cellRenderer);

		return jTableBalance;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new BoxLayout(jPanelButtons, BoxLayout.Y_AXIS));
			jPanelButtons.add(getJPanelButtonsBill());
			jPanelButtons.add(getJPanelButtonsPayment());
			jPanelButtons.add(Box.createVerticalGlue());
			jPanelButtons.add(getJPanelButtonsActions());
		}
		return jPanelButtons;
	}

	private JPanel getJPanelButtonsBill() {
		if (jPanelButtonsBill == null) {
			jPanelButtonsBill = new JPanel();
			jPanelButtonsBill.setLayout(new BoxLayout(jPanelButtonsBill, BoxLayout.Y_AXIS));

			jPanelButtonsBill.add(getJButtonAddPrescription());
			jPanelButtonsBill.add(getJButtonAddMedical());
			jPanelButtonsBill.add(getJButtonAddOperation());
			jPanelButtonsBill.add(getJButtonAddExam());
			jPanelButtonsBill.add(getJButtonAddOther());
			jPanelButtonsBill.add(getJButtonAddCustom());
			jPanelButtonsBill.add(getJButtonRemoveItem());
			jPanelButtonsBill.setMinimumSize(new Dimension(ButtonWidth, BillHeight + TotalHeight));
			jPanelButtonsBill.setMaximumSize(new Dimension(ButtonWidth, BillHeight + TotalHeight));
			jPanelButtonsBill.setPreferredSize(new Dimension(ButtonWidth, BillHeight + TotalHeight));

		}
		return jPanelButtonsBill;
	}

	private JPanel getJPanelButtonsPayment() {
		if (jPanelButtonsPayment == null) {
			jPanelButtonsPayment = new JPanel();
			jPanelButtonsPayment.setLayout(new BoxLayout(jPanelButtonsPayment, BoxLayout.Y_AXIS));
			jPanelButtonsPayment.add(getJButtonAddPayment());
			jPanelButtonsPayment.add(getJButtonAddRefund());
			if (Param.bool("RECEIPTPRINTER"))
				jPanelButtonsPayment.add(getJButtonPrintPayment());
			jPanelButtonsPayment.add(getJButtonRemovePayment());
			jPanelButtonsPayment.add(getJButtonSave());
			jPanelButtonsPayment.setMinimumSize(new Dimension(ButtonWidth, PaymentHeight));
			jPanelButtonsPayment.setMaximumSize(new Dimension(ButtonWidth, PaymentHeight));
			// jPanelButtonsPayment.setPreferredSize(new Dimension(ButtonWidth,
			// PaymentHeight));

		}
		return jPanelButtonsPayment;
	}
	
	private JPanel getJPanelButtonsItemPaymentActions() {
		if (jPanelButtonsItemPaymentActions == null) {
			jPanelButtonsItemPaymentActions = new JPanel();
			jPanelButtonsItemPaymentActions.setAlignmentX(Component.RIGHT_ALIGNMENT);
			jPanelButtonsItemPaymentActions.setAlignmentY(Component.CENTER_ALIGNMENT);
			//jPanelButtonsItemPaymentActions.setLayout(new BoxLayout(jPanelButtonsItemPaymentActions, BoxLayout.LINE_AXIS));
			jPanelButtonsItemPaymentActions.add(getJButtonSaveItemPayments());
			jPanelButtonsItemPaymentActions.add(Box.createRigidArea(new Dimension(10, 0)));
			jPanelButtonsItemPaymentActions.add(getJButtonCloseItemPayments());
		}
		return jPanelButtonsItemPaymentActions;
	}

	private JPanel getJPanelButtonsActions() {
		if (jPanelButtonsActions == null) {
			jPanelButtonsActions = new JPanel();
			jPanelButtonsActions.setLayout(new BoxLayout(jPanelButtonsActions, BoxLayout.Y_AXIS));
			jPanelButtonsActions.add(getJButtonBalance());
			//jPanelButtonsActions.add(getJButtonSave());
			jPanelButtonsActions.add(getJButtonPaid());
			jPanelButtonsActions.add(getJButtonClose());
			// jPanelButtonsActions.setMinimumSize(new Dimension(ButtonWidth,
			// ActionHeight));
			// jPanelButtonsActions.setMaximumSize(new Dimension(ButtonWidth,
			// ActionHeight));
		}
		return jPanelButtonsActions;
	}

	private JButton getJButtonBalance() {
		if (jButtonBalance == null) {
			jButtonBalance = new JButton();
			jButtonBalance.setText(MessageBundle.getMessage("angal.newbill.givechange") + "..."); //$NON-NLS-1$
			jButtonBalance.setMnemonic(KeyEvent.VK_B);
			jButtonBalance.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
			jButtonBalance.setIcon(new ImageIcon("rsc/icons/money_button.png")); //$NON-NLS-1$
			if (insert)
				jButtonBalance.setEnabled(false);
			jButtonBalance.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					Icon icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
					BigDecimal amount = new BigDecimal(0);

					String quantity = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.entercustomercash"),
							MessageBundle.getMessage("angal.newbill.givechange"), JOptionPane.OK_CANCEL_OPTION, icon,
							null, amount);

					if (quantity != null) {
						try {
							amount = new BigDecimal(quantity);
							if (amount.equals(new BigDecimal(0)) || amount.compareTo(balance) < 0)
								return;
							StringBuffer balanceBfr = new StringBuffer(
									MessageBundle.getMessage("angal.newbill.givechange"));
							balanceBfr.append(": ").append(amount.subtract(balance));
							JOptionPane.showMessageDialog(PatientBillEdit.this, balanceBfr.toString(),
									MessageBundle.getMessage("angal.newbill.givechange"), JOptionPane.OK_OPTION, icon);
						} catch (Exception eee) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else
						return;
				}
			});
		}
		return jButtonBalance;
	}
	
	private JButton getJButtonSaveItemPayments() {
		if (jButtonSaveItemPayments == null) {
			jButtonSaveItemPayments = new JButton();
		}
		jButtonSaveItemPayments.setText(MessageBundle.getMessage("angal.common.save")); //$NON-NLS-1$
		jButtonSaveItemPayments.setMnemonic(KeyEvent.VK_S);
		jButtonSaveItemPayments.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
		jButtonSaveItemPayments.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(BillItemListItem.remainingAmount(itemListItems, paymentAmount));
				if(BillItemListItem.remainingAmount(itemListItems, paymentAmount) == 0.0) {
					addPayment(paymentDate, balance.doubleValue());
				} else {
					JOptionPane.showMessageDialog(billItemPayDialog,
							MessageBundle.getMessage("angal.newbill.invalidamount"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.invalidselection"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		return jButtonSaveItemPayments;
	}
	private JButton getJButtonCloseItemPayments() {
		if (jButtonCloseItemPayments == null) {
			jButtonCloseItemPayments = new JButton();
		}
		jButtonCloseItemPayments.setText(MessageBundle.getMessage("angal.common.close")); //$NON-NLS-1$
		jButtonCloseItemPayments.setMnemonic(KeyEvent.VK_DELETE);
		jButtonCloseItemPayments.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
		jButtonCloseItemPayments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		return jButtonCloseItemPayments;
	}
	private JButton getJButtonSave() {
		if (jButtonSave == null) {

			jButtonSave = new JButton();
			jButtonSave.setText(MessageBundle.getMessage("angal.common.save")); //$NON-NLS-1$
			jButtonSave.setMnemonic(KeyEvent.VK_S);
			jButtonSave.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
			jButtonSave.setIcon(new ImageIcon("rsc/icons/save_button.png")); //$NON-NLS-1$
			jButtonSave.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if(Param.bool("ENABLEPRICECONTROL")){
						int i = 0;
						while(i < billItems.size()){
							BillItems item = billItems.get(i);
							if(item.getItemGroup() != null && item.getItemGroup().equals("MED")){
								double soldPrice = item.getItemAmount();
								double settingAmount = getSettingPrice(item);
								if(soldPrice < settingAmount){
									JOptionPane.showMessageDialog(null, 
											MessageBundle.getMessage("angal.newbill.unsuficientprice") + 
											"[" + item.getItemDescription() + "]\n" + 
													MessageBundle.getMessage("angal.newbill.requiredprice") + settingAmount);
	
									return;
								}
							}
							i++;
						}
					}
					GregorianCalendar upDate = TimeTools.getServerDateTime();
					GregorianCalendar firstPay = TimeTools.getServerDateTime();

					if (payItems.size() > 0) {
						firstPay = payItems.get(0).getDate();
						upDate = payItems.get(payItems.size() - 1).getDate();
					} else {
						upDate = billDate;
					}

					if (billDate.after(today)) {

						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.billsinfuturenotallowed"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (billDate.after(firstPay)) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.billdateafterfirstpayment"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (jTextFieldPatient.getText().equals("")) { //$NON-NLS-1$

						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.pleaseinsertanameforthepatient"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (listSelected == null) {
						listSelected = lstArray.get(0);
					}

					/*** insertion of ward into the bill ****/
					Ward selectedWard = null;
					String wardCode = MainMenu.getUserWard(); 
					try {
						selectedWard = (Ward) wardBox.getSelectedItem();
						wardCode = selectedWard.getCode();
					} catch (Exception ex) {

					}
					// Ward selectedWard = (Ward) wardBox.getSelectedItem();
					// String wardCode = selectedWard.getCode();

					// Automatic close bill if balance is 0
					if (!paid) {
						if (balance.compareTo(new BigDecimal("0")) == 0) {
							if (Param.bool("AUTOMATICCLOSEBILL" )) {
								if (Param.bool("CLOSE_BILL_WITHOUD_ASK")) {
									paid = true;
								} else {
									if (JOptionPane.showConfirmDialog(PatientBillEdit.this, MessageBundle.getMessage(
											"angal.newbill.doyouwanttoclosebill")) == JOptionPane.YES_OPTION) {
										paid = true;
									}
								}

							}
						}
					}

					if (insert) {
						RememberDates.setLastBillDate(billDate); // to remember
																	// for next
																	// INSERT
						
						Bill newBill = new Bill(0, // Bill ID
								billDate, // from calendar
								upDate, // most recent payment
								true, // is a List?
								listSelected.getId(), // List
								listSelected.getName(), // List name
								thisBill.isPatient(), // is a Patient?
								thisBill.isPatient() ? thisBill.getPatID() : 0, // Patient
																				// ID
								thisBill.isPatient() ? patientSelected.getName() : jTextFieldPatient.getText(), // Patient
																												// Name
								paid ? "C" : "O", // CLOSED or OPEN
								total.doubleValue(), // Total
								balance.doubleValue(), // Balance
								user,                  // User
								wardCode,              //wardCode
								thisBill.isPatient() ? patientSelected.getReductionPlanID() : 0,
								//thisBill.isPatient() ? patientSelected.getAffiliatedPerson() : null	);  //reductionPlan
						        thisBill.isPatient() ? patientSelected.getAffiliatedPerson() : 0);
						
						
						//add garante
						if(Param.bool("ALLOWGARANTEPERSON")){
							if (balance.compareTo(new BigDecimal("0")) > 0) {							
								String selected = (String)jComboGarante.getSelectedItem();
								if (selected != null && selected != "") {
									newBill.setGarante(selected);
								}else{
									JOptionPane.showMessageDialog(PatientBillEdit.this,
											MessageBundle.getMessage("angal.newbill.bills.need.garante"), //$NON-NLS-1$
											MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
											JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							else{
								newBill.setGarante("");
							}
						}						
						//adding garante
						
						billID = billManager.newBill(newBill, user, billItems, payItems, itemListItems);
						if (billID == 0) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.failedtosavebill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						} else {
							fireBillInserted(newBill);
						}
						
						/**
						 * 
						 billID = billManager.newBill(newBill, billItems, payItems, billPayItems);
							if (billID == 0) {
								JOptionPane.showMessageDialog(PatientBillEdit.this,
										MessageBundle.getMessage("angal.newbill.failedtosavebill"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
								return;
							} else {
								fireBillInserted(newBill);
							}
						 * 
						 */
					} else {
						billID = thisBill.getId();
						Bill updateBill = new Bill(billID, // Bill ID
								billDate, // from calendar
								upDate, // most recent payment
								true, // is a List?
								listSelected.getId(), // List
								listSelected.getName(), // List name
								thisBill.isPatient(), // is a Patient?
								thisBill.isPatient() ? thisBill.getPatID() : 0, // Patient
																				// ID
								thisBill.isPatient() ? thisBill.getPatName() : jTextFieldPatient.getText(), // Patient
																											// Name
								paid ? "C" : "O", // CLOSED or OPEN
								total.doubleValue(), // Total
								balance.doubleValue(), // Balance
								user, // User
								wardCode, // wardCode
								thisBill.isPatient() ? patientSelected.getReductionPlanID() : 0,
								//thisBill.isPatient() ? patientSelected.getAffiliatedPerson() : null	);  //reductionPlan
						        thisBill.isPatient() ? patientSelected.getAffiliatedPerson() : 0);
						
						//add garante
						if(Param.bool("ALLOWGARANTEPERSON")){
							if (balance.compareTo(new BigDecimal("0")) > 0) {							
								String selected = (String)jComboGarante.getSelectedItem();
								if (selected != null && selected != "") {
									updateBill.setGarante(selected);
								}else{
									JOptionPane.showMessageDialog(PatientBillEdit.this,
											MessageBundle.getMessage("angal.newbill.bills.need.garante"), //$NON-NLS-1$
											MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
											JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							else{
								updateBill.setGarante("");
							}
						}
						//adding garante
						
						if(billManager.updateBill(updateBill, user, billItems, payItems, itemListItems)){
							fireBillInserted(updateBill);
						}
						else{
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.failedtosavebill"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						}

					}
					if (paid && Param.bool("RECEIPTPRINTER")) {

						TxtPrinter.getTxtPrinter();
						if (TxtPrinter.PRINT_AS_PAID)
							new GenericReportBill(billID, Param.string("PATIENTBILL") , false,
									!TxtPrinter.PRINT_WITHOUT_ASK);
					}

					dispose();
				}
			});
		}
		return jButtonSave;
	}

	private JButton getJButtonPrintPayment() {
		if (jButtonPrintPayment == null) {
			jButtonPrintPayment = new JButton();
			jButtonPrintPayment.setText(MessageBundle.getMessage("angal.newbill.paymentreceipt")); //$NON-NLS-1$
			jButtonPrintPayment.setMaximumSize(new Dimension(ButtonWidthPayment, ButtonHeight));
			jButtonPrintPayment.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonPrintPayment.setIcon(new ImageIcon("rsc/icons/receipt_button.png")); //$NON-NLS-1$
			jButtonPrintPayment.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					TxtPrinter.getTxtPrinter();
					new GenericReportBill(thisBill.getId(), "PatientBillPayments", false,
							!TxtPrinter.PRINT_WITHOUT_ASK);
				}
			});
		}
		if (insert)
			jButtonPrintPayment.setEnabled(false);
		return jButtonPrintPayment;
	}

	private JButton getJButtonPaid() {
		if (jButtonPaid == null) {
			jButtonPaid = new JButton();
			jButtonPaid.setText(MessageBundle.getMessage("angal.newbill.paid")); //$NON-NLS-1$
			jButtonPaid.setMnemonic(KeyEvent.VK_A);
			jButtonPaid.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
			jButtonPaid.setIcon(new ImageIcon("rsc/icons/ok_button.png")); //$NON-NLS-1$
			if (insert)
				jButtonPaid.setEnabled(false);
			jButtonPaid.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if(Param.bool("ENABLEPRICECONTROL")){
						int i = 0;
						while(i < billItems.size()){
							BillItems item = billItems.get(i);
							if(item.getItemGroup() != null && item.getItemGroup().equals("MED")){
								double soldPrice = item.getItemAmount();
								double settingAmount = getSettingPrice(item);
								if(soldPrice < settingAmount){
									JOptionPane.showMessageDialog(null, 
											MessageBundle.getMessage("angal.newbill.unsuficientprice") + 
											"[" + item.getItemDescription() + "]\n" + 
													MessageBundle.getMessage("angal.newbill.requiredprice") + settingAmount);
	
									return;
								}
							}
							i++;
						}
				    }
					GregorianCalendar datePay = TimeTools.getServerDateTime();
					GregorianCalendar lastPay = TimeTools.getServerDateTime(); 

					if (jTextFieldPatient.getText().equals("")) { //$NON-NLS-1$

						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.pleaseinsertanameforthepatient"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					Icon icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
					int ok = JOptionPane.showConfirmDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.doyouwanttosetaspaidcurrentbill"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.paid"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
					if (ok == JOptionPane.NO_OPTION)
						return;

					if (balance.compareTo(new BigDecimal(0)) > 0) {

						if (billDate.before(today)) { // if Bill is in the past
														// the user will be
														// asked for PAID date

							icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$

							//JDateChooser datePayChooser = new JDateChooser(new Date());
							JDateChooser datePayChooser = new JDateChooser(TimeTools.getServerDateTime().getTime());
							datePayChooser.setLocale(new Locale(Param.string("LANGUAGE")));
							datePayChooser.setDateFormatString("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$

							int r = JOptionPane.showConfirmDialog(PatientBillEdit.this, datePayChooser,
									MessageBundle.getMessage("angal.newbill.dateofpayment"),
									JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);

							if (r == JOptionPane.OK_OPTION) {
								datePay.setTime(datePayChooser.getDate());
							} else {
								return;
							}

							//GregorianCalendar now = new GregorianCalendar();
							// tocorrect 
							GregorianCalendar now = TimeTools.getServerDateTime();

							if (payItems.size() > 0) {
								lastPay = payItems.get(payItems.size() - 1).getDate();
							} else {
								lastPay = billDate;
							}

							if (datePay.before(lastPay)) {
								JOptionPane.showMessageDialog(PatientBillEdit.this,
										MessageBundle.getMessage("angal.newbill.datebeforelastpayment"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
								return;
							} else if (datePay.after(now)) {
								JOptionPane.showMessageDialog(PatientBillEdit.this,
										MessageBundle.getMessage("angal.newbill.payementinthefuturenotallowed"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
								return;
							} else {
								chooseItemsToPay(datePay, balance.doubleValue());
								//addPayment(datePay, balance.doubleValue());
							}

						} else {
							chooseItemsToPay(datePay, balance.doubleValue());
							//datePay = TimeTools.getServerDateTime();
							//addPayment(datePay, balance.doubleValue());
						}
					}
					paid = true;
					updateBalance();
					jButtonSave.doClick();
				}
			});
		}
		return jButtonPaid;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(MessageBundle.getMessage("angal.common.close")); //$NON-NLS-1$
			jButtonClose.setMnemonic(KeyEvent.VK_C);
			jButtonClose.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
			jButtonClose.setIcon(new ImageIcon("rsc/icons/close_button.png")); //$NON-NLS-1$
			jButtonClose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (modified) {

						Icon icon = new ImageIcon("rsc/icons/save_dialog.png"); //$NON-NLS-1$
						int ok = JOptionPane.showConfirmDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.billhasbeenchangedwouldyouliketosavechanges"),
								MessageBundle.getMessage("angal.common.save"), JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE, icon);
						if (ok == JOptionPane.YES_OPTION) {
							jButtonSave.doClick();
						} else if (ok == JOptionPane.NO_OPTION) {
							// fireBillInserted(null);
							dispose();
						} else
							return;
					} else {

						// fireBillInserted(null);
						dispose();
					}
				}
			});
		}
		return jButtonClose;
	}

	private JButton getJButtonAddRefund() {
		if (jButtonAddRefund == null) {
			jButtonAddRefund = new JButton();
			jButtonAddRefund.setText(MessageBundle.getMessage("angal.newbill.refund")); //$NON-NLS-1$
			jButtonAddRefund.setMaximumSize(new Dimension(ButtonWidthPayment, ButtonHeight));
			jButtonAddRefund.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddRefund.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddRefund.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					Icon icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
					BigDecimal amount = new BigDecimal(0);

					GregorianCalendar datePay = TimeTools.getServerDateTime();

					String quantity = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.insertquantity"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.quantity"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE, icon, null, amount);
					if (quantity != null) {
						try {
							amount = new BigDecimal(quantity).negate();
							if (amount.equals(new BigDecimal(0)))
								return;
						} catch (Exception eee) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else
						return;

					if (billDate.before(today)) { // if is a bill in the past
													// the user will be asked
													// for date of payment

						icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$

						//JDateChooser datePayChooser = new JDateChooser(new Date());
						JDateChooser datePayChooser = new JDateChooser(TimeTools.getServerDateTime().getTime());
						
						datePayChooser.setLocale(new Locale(Param.string("LANGUAGE")));
						datePayChooser.setDateFormatString("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$

						int r = JOptionPane.showConfirmDialog(PatientBillEdit.this, datePayChooser,
								MessageBundle.getMessage("angal.newbill.dateofpayment"), JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE);

						if (r == JOptionPane.OK_OPTION) {
							datePay.setTime(datePayChooser.getDate());
						} else {
							return;
						}
						
						// tocorrect 
						//GregorianCalendar now = new GregorianCalendar();
						GregorianCalendar now = TimeTools.getServerDateTime();

						if (datePay.before(billDate)) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.paymentbeforebilldate"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						} else if (datePay.after(now)) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.payementinthefuturenotallowed"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						} else {
							chooseItemsToPay(datePay, amount.doubleValue());
							//addPayment(datePay, amount.doubleValue());
						}
					} else {
						// tocorrect 
						//datePay = new GregorianCalendar();
						chooseItemsToPay(datePay, amount.doubleValue());
						//datePay =  TimeTools.getServerDateTime();
						//addPayment(datePay, amount.doubleValue());
					}
				}
			});
		}
		return jButtonAddRefund;
	}

	private JButton getJButtonAddPayment() {
		if (jButtonAddPayment == null) {
			jButtonAddPayment = new JButton();
			jButtonAddPayment.setText(MessageBundle.getMessage("angal.newbill.payment")); //$NON-NLS-1$
			jButtonAddPayment.setMnemonic(KeyEvent.VK_Y);
			jButtonAddPayment.setMaximumSize(new Dimension(ButtonWidthPayment, ButtonHeight));
			jButtonAddPayment.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddPayment.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddPayment.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					Icon icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
					if(Param.bool("ENABLEPRICECONTROL")){	
						int i = 0;
						while(i < billItems.size()){
							BillItems item = billItems.get(i);
							if(item.getItemGroup()!= null && item.getItemGroup().equals("MED")){
								double soldPrice = item.getItemAmount();
								double settingAmount = getSettingPrice(item);
								if(soldPrice < settingAmount){
									JOptionPane.showMessageDialog(null, 
											MessageBundle.getMessage("angal.newbill.unsuficientprice") + 
											"[" + item.getItemDescription() + "]\n" + 
													MessageBundle.getMessage("angal.newbill.requiredprice") + settingAmount);
	
									return;
								}
							}
							i++;
						}
				    }
					BigDecimal amount = balance;
					GregorianCalendar datePay = TimeTools.getServerDateTime();
					String enteredAmount = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.insertamount"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.payment"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE, icon, null, amount);
					
					if (enteredAmount != null) {
						try {
							amount = new BigDecimal(enteredAmount);
							if (amount.equals(new BigDecimal(0)))
								return;
						} catch (Exception eee) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else
						return;

					if (billDate.before(today)) { // if is a bill in the past
													// the user will be asked
													// for date of payment

						icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
						//solution du probleme de date de paiement dans la future
						JDateChooser datePayChooser = new JDateChooser(TimeTools.getServerDateTime().getTime());
						datePayChooser.setLocale(new Locale(Param.string("LANGUAGE")));
						datePayChooser.setDateFormatString("dd/MM/yy - HH:mm:ss"); //$NON-NLS-1$

						int r = JOptionPane.showConfirmDialog(PatientBillEdit.this, datePayChooser,
								MessageBundle.getMessage("angal.newbill.dateofpayment"), JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE);

						if (r == JOptionPane.OK_OPTION) {
							datePay.setTime(datePayChooser.getDate());
						} else {
							return;
						}

						// tocorrect 
						GregorianCalendar now = TimeTools.getServerDateTime();

						if (datePay.before(billDate)) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.paymentbeforebilldate"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						} else if (datePay.after(now)) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.payementinthefuturenotallowed"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invaliddate"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						} else {
							chooseItemsToPay(datePay, amount.doubleValue());
							//addPayment(datePay, amount.doubleValue());
						}
					} else {
						// tocorrect 
						chooseItemsToPay(datePay, amount.doubleValue());
						//datePay = TimeTools.getServerDateTime();
						//addPayment(datePay, amount.doubleValue());
					}
					if( balance.doubleValue() > 0) 
						jButtonPaid.setEnabled(false);
				}
			});
		}
		return jButtonAddPayment;
	}
	
	private double getSettingPrice(BillItems item){
		MovStockInsertingManager manager = new MovStockInsertingManager();
		double cost =   0.0;
		try {
			cost = manager.getLastMovementPriceForAMedical(Integer.parseInt(item.getItemId()));
		} catch (OHException e1) {
			e1.printStackTrace();
		}
		int priceListId = patientSelected.getListID();
		if(priceListId == 0) priceListId = 1;
		
		Price newPrice = new Price();
		newPrice.setGroup(item.getItemGroup());
		newPrice.setPrice(cost);
		newPrice.setList(priceListId);
		newPrice.setItem(item.getItemDescription());
		
		Price goodPrice = new Price();
		int reductionID = patientSelected.getReductionPlanID();
		if(reductionID != 0 ){
			goodPrice = reductionPlanManager.getMedicalPrice(newPrice, reductionID);
			return goodPrice.getPrice();
		}
		return newPrice.getPrice();
	}
	

	private JButton getJButtonRemovePayment() {
		if (jButtonRemovePayment == null) {
			jButtonRemovePayment = new JButton();
			jButtonRemovePayment.setText(MessageBundle.getMessage("angal.newbill.removepayment")); //$NON-NLS-1$
			// jButtonRemovePayment.setMnemonic(KeyEvent.VK_Y);
			jButtonRemovePayment.setMaximumSize(new Dimension(ButtonWidthPayment, ButtonHeight));
			jButtonRemovePayment.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonRemovePayment.setIcon(new ImageIcon("rsc/icons/delete_button.png")); //$NON-NLS-1$
			jButtonRemovePayment.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int row = jTablePayment.getSelectedRow();
					if (row > -1) {
						removePayment(row);
					}
				}
			});
		}
		return jButtonRemovePayment;
	}

	private JButton getJButtonAddOther() {
		if (jButtonAddOther == null) {
			jButtonAddOther = new JButton();
			jButtonAddOther.setText(MessageBundle.getMessage("angal.newbill.other")); //$NON-NLS-1$
			jButtonAddOther.setMnemonic(KeyEvent.VK_T);
			jButtonAddOther.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonAddOther.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddOther.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddOther.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (patientSelected == null) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
						return;
					}

					// boolean isPrice = true;

					HashMap<Integer, PricesOthers> othersHashMap = new HashMap<Integer, PricesOthers>();

					for (PricesOthers other : othPrices) {
						othersHashMap.put(other.getId(), other);
					}

					ArrayList<Price> othArray = new ArrayList<Price>();
					ArrayList<PricesOthers> pricesOthersArray = new ArrayList<PricesOthers>();

					for (Price price : prcListArray) {
						if (price.getGroup().equals(ItemGroup.OTHER.getCode())) // $NON-NLS-1$
							othArray.add(price);

					}
					for (Price price : othArray) {
						PricesOthers priceOth = getPricesOthers(othPrices, price);
						if (priceOth != null) {
							pricesOthersArray.add(priceOth);
						}
					}

					OhTableModel<Price> modelOh = new OhTableModel<Price>(othArray);
					BillItemPicker picker = new BillItemPicker(modelOh);

					picker.setSize(300, 400);

					JDialog dialog = new JDialog();
					dialog.setLocationRelativeTo(null);
					dialog.setSize(600, 350);
					dialog.setLocationRelativeTo(null);
					dialog.setModal(true);

					picker.setParentFrame(dialog);
					dialog.setContentPane(picker);
					dialog.setIconImage(ico);
					dialog.setVisible(true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

					Price oth = (Price) picker.getSelectedObject();

					PricesOthersManager othManager = new PricesOthersManager();
					PricesOthers othPrice = othManager.getOther(Integer.valueOf(oth.getItem()));

					if (othPrice.isDaily()) {
						int qty = 1;
						ImageIcon icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
						String quantity = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.howmanydays"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.days"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE, icon, null, qty);
						try {
							if (quantity == null || quantity.equals(""))
								return;
							qty = Integer.valueOf(quantity);
							// JOptionPane.showConfirmDialog(null,
							// " isDaily--" + oth.getPrice());
							addOtherPrice(oth, qty);
						} catch (Exception eee) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}

			});
		}
		return jButtonAddOther;
	}

	private JButton getJButtonAddExam() {
		if (jButtonAddExam == null) {
			jButtonAddExam = new JButton();
			jButtonAddExam.setText(MessageBundle.getMessage("angal.newbill.exam")); //$NON-NLS-1$
			jButtonAddExam.setMnemonic(KeyEvent.VK_E);
			jButtonAddExam.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonAddExam.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddExam.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddExam.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					if (patientSelected == null) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
						return;
					}

					ArrayList<Price> exaArray = new ArrayList<Price>();
					for (Price price : prcListArray) {

						if (price.getGroup().equals(ItemGroup.EXAM.getCode())) // $NON-NLS-1$
							exaArray.add(price);
					}

					OhTableModel<Price> modelOh = new OhTableModel<Price>(exaArray);
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

					Price exa = (Price) itemChooser.getSelectedObject();
					if(!examsList.contains(exa.getId())) {
						if (pbiID != 0 && exa != null) {
							exa = reductionPlanManager.getExamPrice(exa, pbiID);
						}
						examsList.add(exa.getId());
					}
					addItem(exa, 1, true, 0);
				}
			});
		}
		return jButtonAddExam;
	}

	private JButton getJButtonAddOperation() {
		if (jButtonAddOperation == null) {
			jButtonAddOperation = new JButton();
			jButtonAddOperation.setText(MessageBundle.getMessage("angal.newbill.operation")); //$NON-NLS-1$
			jButtonAddOperation.setMnemonic(KeyEvent.VK_O);
			jButtonAddOperation.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonAddOperation.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddOperation.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddOperation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (patientSelected == null) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
						return;
					}
					ArrayList<Price> opeArray = new ArrayList<Price>();
					JTable table = new JTable();
					table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
					for (Price price : prcListArray) {
						if (price.getGroup().equals(ItemGroup.OPERATION.getCode())) { // $NON-NLS-1$
							opeArray.add(price);
						}
					}

					OhTableModel<Price> modelOh = new OhTableModel<Price>(opeArray);
					table.setModel(modelOh);
					table.getTableHeader().setReorderingAllowed(false);
					BillItemPicker framas = new BillItemPicker(modelOh);

					framas.setSize(300, 400);

					JDialog dialog = new JDialog();
					dialog.setLocationRelativeTo(null);
					dialog.setSize(600, 350);
					dialog.setLocationRelativeTo(null);
					dialog.setModal(true);

					framas.setParentFrame(dialog);
					dialog.setContentPane(framas);
					dialog.setIconImage(ico);
					dialog.setVisible(true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

					Price ope = (Price) framas.getSelectedObject();

					addExamAndOperation(ope);
				}
			});
		}
		return jButtonAddOperation;
	}

	private JButton getJButtonAddMedical() {
		if (jButtonAddMedical == null) {
			jButtonAddMedical = new JButton();
			jButtonAddMedical.setText(MessageBundle.getMessage("angal.newbill.medical")); //$NON-NLS-1$
			jButtonAddMedical.setMnemonic(KeyEvent.VK_M);
			jButtonAddMedical.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonAddMedical.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddMedical.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddMedical.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// JOptionPane.showMessageDialog(null, "qsdqsfsqdf");

					if (patientSelected == null) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
						return;
					}

					// String wardCode = MainMenu.getUserWard();
					
					Ward selectedWard = null;
					String wardCode = "";
					if ((!wardBox.getSelectedItem().equals("")) && (wardBox.getSelectedItem() != null)) {
						selectedWard = (Ward) wardBox.getSelectedItem();
						wardCode = selectedWard.getCode();
					}

					if (wardCode == null || wardCode.equals("")) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectaward"));
						return;
					}

					ArrayList<Price> medArray = new ArrayList<Price>();
					ArrayList<MedicalWard> medWardArray = new ArrayList<MedicalWard>();
					MedicalWard mwd;
					for (Price price : prcListArray) {
						if (price.getGroup().equals(ItemGroup.MEDICAL.getCode())) {
							if (Param.bool("STOCKMVTONBILLSAVE") ) {
								if(!Param.bool("ALLOWPRODUCTINBILLWIHTEMPTYSTOCK")){
									if (containPrice(price, 1.0)) {
										medArray.add(price);
										mwd = getMedicalWard(price);
										if (mwd != null) {
											medWardArray.add(mwd);
										}
									}
								}else{
									medArray.add(price);
									mwd = getMedicalWard(price);
									if (mwd != null) {
										medWardArray.add(mwd);
									}
								}
							} else {
								medArray.add(price);
							}
						}
					}

					OhTableModel<MedicalWard> modelOh = new OhTableModel<MedicalWard>(medWardArray);
					BillItemPicker framas = new BillItemPicker(modelOh);

					framas.setSize(300, 400);

					JDialog dialog = new JDialog();
					dialog.setLocationRelativeTo(null);
					dialog.setSize(600, 350);
					dialog.setLocationRelativeTo(null);
					dialog.setModal(true);

					framas.setParentFrame(dialog);
					dialog.setContentPane(framas);
					dialog.setIconImage(ico);
					dialog.setVisible(true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

					MedicalWard med = (MedicalWard) framas.getSelectedObject();

					int qty = 1;
					Icon icon = new ImageIcon("rsc/icons/operation_dialog.png");
					String quantity = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.insertquantity"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.quantity"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE, icon, null, qty);
					try {
						if (quantity == null || quantity.equals(""))
							return;
						qty = Integer.valueOf(quantity);

						addMedical(med, qty);

					} catch (Exception eee) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain")+" xxxx", //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
					}

				}
			});
		}
		return jButtonAddMedical;
	}

	private JButton getJButtonAddPrescription() {
		if (jButtonAddPrescription == null) {
			jButtonAddPrescription = new JButton();
			jButtonAddPrescription.setText(MessageBundle.getMessage("angal.newbill.medicalprescription")); //$NON-NLS-1$
			jButtonAddPrescription.setMnemonic(KeyEvent.VK_R);
			jButtonAddPrescription.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonAddPrescription.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonAddPrescription.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonAddPrescription.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// JOptionPane.showMessageDialog(null, "qsdqsfsqdf");

					if (patientSelected == null) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
						return;
					}

					Ward selectedWard = null;
					String wardCode = "";
					if ((!wardBox.getSelectedItem().equals("")) && (wardBox.getSelectedItem() != null)) {
						selectedWard = (Ward) wardBox.getSelectedItem();
						wardCode = selectedWard.getCode();
					}

					if (wardCode == null || wardCode.equals("")) {
						JOptionPane.showMessageDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectaward"));
						return;
					}

					SelectPrescriptions selectePrescriptions = new SelectPrescriptions(PatientBillEdit.this,
							patientSelected);
					selectePrescriptions.addPrescriptionSelectedListener(PatientBillEdit.this);
					selectePrescriptions.setVisible(true);

				}
			});
		}
		// TherapyManager thManager=new TherapyManager();
		if (this.patientSelected != null && billManager.hasPrescription(this.patientSelected.getCode())) {
			this.jButtonAddPrescription.setVisible(true);
		} else {
			this.jButtonAddPrescription.setVisible(false);
		}

		// if(this.patientSelected !=null &&
		// thManager.hasTherapiesRowsNotYetBought(this.patientSelected.getCode())){
		// jButtonAddPrescription.setVisible(true);
		// }
		// else{
		// jButtonAddPrescription.setVisible(false);
		// }
		return jButtonAddPrescription;
	}

	private boolean containPrice(Price price, double qty) {
		for (Iterator<MedicalWard> iterator = medWardList.iterator(); iterator.hasNext();) {
			MedicalWard medicalWard = (MedicalWard) iterator.next();
			String code = medicalWard.getMedical().getCode() + "";
			Double stock = medicalWard.getQty();
			if (code.equals(price.getItem()) && stock >= qty) {
				return true;
			}
		}
		return false;
	}

	private PricesOthers getPricesOthers(ArrayList<PricesOthers> priceslist, Price price) {
		for (Iterator<PricesOthers> iterator = priceslist.iterator(); iterator.hasNext();) {
			PricesOthers priceO = (PricesOthers) iterator.next();
			String id = priceO.getId() + "";
			if (id.equals(price.getItem())) {
				return priceO;
			}
		}
		return null;
	}

	private JButton getJButtonAddCustom() {
		if (jButtonCustom == null) {
			jButtonCustom = new JButton();
			jButtonCustom.setText(MessageBundle.getMessage("angal.newbill.custom")); //$NON-NLS-1$
			jButtonCustom.setMnemonic(KeyEvent.VK_U);
			jButtonCustom.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonCustom.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonCustom.setIcon(new ImageIcon("rsc/icons/plus_button.png")); //$NON-NLS-1$
			jButtonCustom.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					double amount;
					Icon icon = new ImageIcon("rsc/icons/custom_dialog.png"); //$NON-NLS-1$
					String desc = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.chooseadescription"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.customitem"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE, icon, null,
							MessageBundle.getMessage("angal.newbill.newdescription")); //$NON-NLS-1$
					if (desc == null || desc.equals("")) { //$NON-NLS-1$
						return;
					} else {
						icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
						String price = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
								MessageBundle.getMessage("angal.newbill.howmuchisit"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.customitem"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE, icon, null, "0"); //$NON-NLS-1$
						try {
							amount = Double.valueOf(price);
						} catch (Exception eee) {
							JOptionPane.showMessageDialog(PatientBillEdit.this,
									MessageBundle.getMessage("angal.newbill.invalidpricepleasetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invalidprice"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						}

					}

					BillItems newItem = new BillItems(0, billID, false, "", 
							desc, amount, 1, amount);
					addItem(newItem);
				}
			});
		}
		return jButtonCustom;
	}

	private JButton getJButtonRemoveItem() {
		if (jButtonRemoveItem == null) {
			jButtonRemoveItem = new JButton();
			jButtonRemoveItem.setText(MessageBundle.getMessage("angal.newbill.removeitem")); //$NON-NLS-1$
			// jButtonRemoveItem.setMnemonic(KeyEvent.VK_R);
			jButtonRemoveItem.setMaximumSize(new Dimension(ButtonWidthBill, ButtonHeight));
			jButtonRemoveItem.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonRemoveItem.setIcon(new ImageIcon("rsc/icons/delete_button.png")); //$NON-NLS-1$
			jButtonRemoveItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int row = jTableBill.getSelectedRow();
					if (row > -1) {
						removeItem(row);
					}
				}
			});
		}
		return jButtonRemoveItem;
	}

	private void updateTotal() { // only positive items make the bill's total
		total = new BigDecimal(0);
		for (BillItems item : billItems) {
			double amount = item.getItemAmount();
			if (amount > 0) {
				BigDecimal itemAmount = new BigDecimal(Double.toString(amount));
				total = total.add(itemAmount.multiply(new BigDecimal(item.getItemQuantity())));
			}
		}
	}

	private void updateBigTotal() { // the big total (to pay) is made by all // items
		bigTotal = new BigDecimal(0);
		bigTotal.setScale(2, RoundingMode.CEILING);
		for (BillItems item : billItems) {
			BigDecimal itemAmount = new BigDecimal(item.getItemAmount());
			bigTotal = bigTotal.add(itemAmount.multiply(new BigDecimal(item.getItemQuantity())));
		}
	}

	private void updateBalance() { // the balance is what remaining after
									// payments
		balance = new BigDecimal(0);
		BigDecimal payments = new BigDecimal(0);
		for (BillPayments pay : payItems) {
			BigDecimal payAmount = new BigDecimal(Double.toString(pay.getAmount()));
			payments = payments.add(payAmount);
		}
		
		balance = bigTotal.subtract(payments);
		if (jButtonPaid != null)
			jButtonPaid.setEnabled(balance.compareTo(new BigDecimal(0)) >= 0);
		if (jButtonBalance != null)
			jButtonBalance.setEnabled(balance.compareTo(new BigDecimal(0)) >= 0);
	}
	
	private BillItems addItem(Price prc, int qty, boolean isPrice, int prescId)  {
		if (prc != null) {
		
			double amount = prc.getPrice();
			Price brut = new PriceListManager().getPrice(listSelected.getId(), prc.getGroup(), prc.getItem());
			double priceBrut = 0.0;
			if(brut!=null)
				priceBrut = brut.getPrice();
											
			BillItems item = new BillItems(0, billID, isPrice, prc.getGroup() + prc.getItem(), prc.getDesc(), amount, qty, priceBrut);
			item.setItemId(prc.getItem());
			item.setItemGroup(prc.getGroup());
			item.setPrescriptionId(prescId);
			billItems.add(item);
			modified = true;
			jTableBill.updateUI();
			updateTotals();
			return item;
		}
		return null;
	}

	private void updateUI() {

		jCalendarDate.setDate(thisBill.getDate().getTime());
		jTextFieldPatient.setText(patientSelected.getName());
		jTextFieldPatient.setEditable(false);
		jButtonPickPatient.setText(MessageBundle.getMessage("angal.newbill.changepatient")); //$NON-NLS-1$
		jButtonPickPatient
				.setToolTipText(MessageBundle.getMessage("angal.newbill.changethepatientassociatedwiththisbill")); //$NON-NLS-1$
		jButtonTrashPatient.setEnabled(true);
		jTableBill.updateUI();
		jTablePayment.updateUI();
		updateTotals();
	}

	/**
	 * 
	 */
	private void updateTotals() {
		updateTotal();
		updateBigTotal();
		updateBalance();
		jTableTotal.getModel().setValueAt(total.doubleValue(), 0, 1);
		jTableBigTotal.getModel().setValueAt(bigTotal.doubleValue(), 0, 1);
		jTableBalance.getModel().setValueAt(balance.doubleValue(), 0, 1);
	}

	private void addItem(BillItems item) {
		if (item != null) {
			billItems.add(item);
			modified = true;
			jTableBill.updateUI();
			updateTotals();
		}
	}

	private void addPayment(GregorianCalendar datePay, double qty) {
		if (qty != 0) {
			BillPayments pay = new BillPayments(0, billID, datePay, qty, user);
			payItems.add(pay);
			modified = true;
			Collections.sort(payItems);
			jTablePayment.updateUI();
			updateBalance();
			jTableBalance.getModel().setValueAt(balance, 0, 1);
		}
	}

	private void removeItem(int row) {
		// && row >= billItemsSaved

		if (row != -1) {
			BillItems item = billItems.get(row);
			///
			LabManager labManager = new LabManager();			
			if (item.getId() > 0) {
				if(item.getItemGroup().equals(ItemGroup.EXAM.getCode())){
					if(labManager.isHasResults(item.getPrescriptionId())){
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.newbill.youcannotdeletelaboratoryitemwhicharealreadypratiqued"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE);
						return;
					}
					else{// deletion of the laboratory
						
					}
				}
			}
			///
			if (item.getId() > 0) {
				billItemsRemoved.add(item);
			}
			billItems.remove(row);
			jTableBill.updateUI();
			jTableBill.clearSelection();
			updateTotals();
			this.selectedBillItem = null;
			loadFields();
		} else {
			JOptionPane.showMessageDialog(null,
					MessageBundle.getMessage("angal.newbill.youcannotdeletealreadysaveditems"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	private void removePayment(int row) {
		if (row != -1 && row >= payItemsSaved) {
			payItems.remove(row);
			jTablePayment.updateUI();
			jTablePayment.clearSelection();
			updateTotals();
		} else {
			JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.newbill.youcannotdeletepastpayments"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.newbill.title"), //$NON-NLS-1$
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	public class BillTableModel implements TableModel {

		public BillTableModel() {
			updateTotal();
			updateBigTotal();
			updateBalance();
		}

		public Class<?> getColumnClass(int i) {
			return billClasses[i].getClass();
		}

		public int getColumnCount() {
			return billClasses.length;
		}

		public int getRowCount() {
			if (billItems == null)
				return 0;
			return billItems.size();
		}

		public Object getValueAt(int r, int c) {
			BillItems item = billItems.get(r);
			if (c == -1) {
				return item;
			}
			if (c == 0) {
				return item.getItemDescription();
			}
			if (c == 1) {
				return item.getItemQuantity();
			}
			if (c == 2) {
				BigDecimal qty = new BigDecimal(item.getItemQuantity());
				BigDecimal amount = new BigDecimal(item.getItemAmount());
				return amount.multiply(qty).doubleValue();
			}
			return null;
		}

		public boolean isCellEditable(int r, int c) {
			if (c == 1)
				return true;
			return false;
		}

		public void setValueAt(Object item, int r, int c) {
			// if (c == 1) billItems.get(r).setItemQuantity((Integer)item);

		}

		public void addTableModelListener(TableModelListener l) {

		}

		public String getColumnName(int columnIndex) {
			return billColumnNames[columnIndex];
		}

		public void removeTableModelListener(TableModelListener l) {
		}

	}

	public class PaymentTableModel implements TableModel {

		public PaymentTableModel() {
			updateBalance();
		}

		public void addTableModelListener(TableModelListener l) {

		}

		public Class<?> getColumnClass(int columnIndex) {
			return paymentClasses[columnIndex].getClass();
		}

		public int getColumnCount() {
			return paymentClasses.length;
		}

		public String getColumnName(int columnIndex) {
			return null;
		}

		public int getRowCount() {
			return payItems.size();
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return payItems.get(r);
			}
			if (c == 0) {
				return formatDateTime(payItems.get(r).getDate());
			}
			if (c == 1) {
				return payItems.get(r).getAmount();
			}
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		public void removeTableModelListener(TableModelListener l) {
		}

		public void setValueAt(Object value, int rowIndex, int columnIndex) {
		}
	}

	public String formatDate(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
		return format.format(time.getTime());
	}

	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss"); //$NON-NLS-1$
		return format.format(time.getTime());
	}

	public boolean isSameDay(GregorianCalendar billDate, GregorianCalendar today) {
		return (billDate.get(Calendar.YEAR) == today.get(Calendar.YEAR))
				&& (billDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
				&& (billDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH));
	}

	
	private JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox();
			comboBox.setToolTipText("Choisir un pavillon");
		}
		return comboBox;
	}

	private JComboBox getWardBox() {
		org.isf.ward.manager.WardBrowserManager wbm = new org.isf.ward.manager.WardBrowserManager();
		Ward thisWard = new Ward();
		if (wardBox == null) {
			wardBox = new JComboBox();
			wardBox.setPreferredSize(new Dimension(130, 25));
			String wardCode = MainMenu.getUserWard();

			if (!insert) {
				wardCode = this.thisBill.getWardCode();
			} 
			ArrayList<Ward> wardList = wbm.getWards();
			boolean trouve = false;
			for (Ward ward : wardList) {
				if (ward.getCode().equals(wardCode)) {
					wardBox.addItem(ward);
					trouve = true;
					MovWardBrowserManager manager = new MovWardBrowserManager();
					medWardList = manager.getMedicalsWard(wardCode);				
					break;
				}
			}
			if(!trouve){
				wardBox.addItem("");
			}
			
			
			for (org.isf.ward.model.Ward elem : wardList) {
				wardBox.addItem(elem);
				if(insert && elem.getDescription().toUpperCase().equals("PHARMACIE"))
					thisWard= elem; //wardBox.setSelectedItem(elem);
				
				if(this.thisBill.getWardCode().equals(elem.getCode()))
					wardBox.setSelectedItem(elem);
			}
			
			wardBox.setEnabled(true);
			if (!insert && wardCode != null && !wardCode.trim().equals("")) {
				wardBox.setEnabled(false);
			}
			
		}
		wardBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Object item = e.getItem();
					Ward ward = (Ward) item;
					MovWardBrowserManager manager = new MovWardBrowserManager();
					medWardList = manager.getMedicalsWard(ward.getCode());
				}
			}
		});
		wardBox.setSelectedItem(thisWard); //NE PAS DEPLACER CE BOUT DE CODE
		return wardBox;
	}

	private JLabel getWardLabel() {
		if (wardLabel == null) {
			wardLabel = new JLabel(MessageBundle.getMessage("angal.patientbill.editt"));
		}
		return wardLabel;
	}

	@Override
	public void prescriptionSelected(java.util.List<BillItems> prescriptions) {
		// TODO Auto-generated method stub
		for (Iterator iterator = prescriptions.iterator(); iterator.hasNext();) {
			BillItems item = (BillItems) iterator.next();
			// item.setBillID(thisBill.getId());
			billItems.add(item);
			modified = true;
			jTableBill.updateUI();
			updateTotals();
		}
	}

	private JPanel getPanelSearch() {
		if (jPanelSearch == null) {
			jPanelSearch = new JPanel();
			// jPanelSearch.setBackground(UIManager.getColor("Button.background"));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 116, 116, 116, 116, 0 };
			gbl_panel.rowHeights = new int[] { 22, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
			jPanelSearch.setLayout(gbl_panel);

			GridBagConstraints gbc_jTextFieldSearch = new GridBagConstraints();
			gbc_jTextFieldSearch.anchor = GridBagConstraints.NORTHWEST;
			gbc_jTextFieldSearch.insets = new Insets(0, 10, 0, 5);
			gbc_jTextFieldSearch.gridx = 0;
			gbc_jTextFieldSearch.gridy = 0;
			jPanelSearch.add(getJTextFieldSearch(), gbc_jTextFieldSearch);

			GridBagConstraints gbc_jTextFieldDescription = new GridBagConstraints();
			gbc_jTextFieldDescription.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldDescription.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldDescription.insets = new Insets(0, 0, 0, 5);
			gbc_jTextFieldDescription.gridx = 1;
			gbc_jTextFieldDescription.gridy = 0;
			jPanelSearch.add(getJTextFieldDescription(), gbc_jTextFieldDescription);

			GridBagConstraints gbc_jTextFieldQty = new GridBagConstraints();
			gbc_jTextFieldQty.insets = new Insets(0, 0, 0, 5);
			gbc_jTextFieldQty.anchor = GridBagConstraints.NORTHWEST;
			gbc_jTextFieldQty.gridx = 2;
			gbc_jTextFieldQty.gridy = 0;
			jPanelSearch.add(getJTextFieldQty(), gbc_jTextFieldQty);

		/*	GridBagConstraints gbc_jTextFieldPrice = new GridBagConstraints();
			gbc_jTextFieldQty.insets = new Insets(0, 0, 0, 10);
			gbc_jTextFieldQty.anchor = GridBagConstraints.NORTHWEST;
			gbc_jTextFieldQty.gridx = 3;
			gbc_jTextFieldQty.gridy = 0;
			jPanelSearch.add(getJTextFieldPrice(), gbc_jTextFieldPrice);*/
			
			GridBagConstraints gbc_jTextFieldPrice = new GridBagConstraints();
			gbc_jTextFieldPrice.insets = new Insets(0, 0, 0, 10);
			gbc_jTextFieldPrice.anchor = GridBagConstraints.NORTHWEST;
			gbc_jTextFieldPrice.gridx = 3;
			gbc_jTextFieldPrice.gridy = 0;
			jPanelSearch.add(getJTextFieldPrice(), gbc_jTextFieldPrice);
		}
		return jPanelSearch;
	}
	private JPanel getPanelLabel() {
		if (jPanelLabel == null) {
			jPanelLabel = new JPanel();
			// jPanelSearch.setBackground(UIManager.getColor("Button.background"));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 116, 116, 116, 116, 0 };
			gbl_panel.rowHeights = new int[] { 22, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
			jPanelLabel.setLayout(gbl_panel);

			GridBagConstraints gbc_jTextFieldQty = new GridBagConstraints();
			gbc_jTextFieldQty.insets = new Insets(0, 0, 0, 5);
			gbc_jTextFieldQty.anchor = GridBagConstraints.NORTHWEST;
			gbc_jTextFieldQty.gridx = 2;
			gbc_jTextFieldQty.gridy = 0;
			jPanelLabel.add(getJTextFieldQty(), gbc_jTextFieldQty);

			GridBagConstraints gbc_jTextFieldPrice = new GridBagConstraints();
			gbc_jTextFieldQty.insets = new Insets(0, 0, 0, 10);
			gbc_jTextFieldQty.anchor = GridBagConstraints.NORTHWEST;
			gbc_jTextFieldQty.gridx = 3;
			gbc_jTextFieldQty.gridy = 0;
			jPanelLabel.add(getJTextFieldPrice(), gbc_jTextFieldPrice);
			GridBagConstraints gbc_lblTexteDeRecherche = new GridBagConstraints();
			gbc_lblTexteDeRecherche.insets = new Insets(0, 0, 0, 5);
			gbc_lblTexteDeRecherche.gridx = 0;
			gbc_lblTexteDeRecherche.gridy = 0;
			jPanelLabel.add(getLblTexteDeRecherche(), gbc_lblTexteDeRecherche);
			GridBagConstraints gbc_lblDescription = new GridBagConstraints();
			gbc_lblDescription.insets = new Insets(0, 0, 0, 5);
			gbc_lblDescription.gridx = 1;
			gbc_lblDescription.gridy = 0;
			jPanelLabel.add(getLblDescription(), gbc_lblDescription);
			GridBagConstraints gbc_lblQty = new GridBagConstraints();
			gbc_lblQty.insets = new Insets(0, 0, 0, 5);
			gbc_lblQty.gridx = 2;
			gbc_lblQty.gridy = 0;
			jPanelLabel.add(getLblQty(), gbc_lblQty);
			GridBagConstraints gbc_lblAmount = new GridBagConstraints();
			gbc_lblAmount.gridx = 3;
			gbc_lblAmount.gridy = 0;
			jPanelLabel.add(getLblAmount(), gbc_lblAmount);
		}
		return jPanelLabel;
	}

	private JTextField getJTextFieldSearch() {
		if (jTextFieldSearch == null) {
			jTextFieldSearch = new JTextField();
			jTextFieldSearch.setColumns(15);
		}
		jTextFieldSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					String actual = jTextFieldSearch.getText();
					jTextFieldSearch.setText(actual.replaceAll(" ", "_"));
				}
			}
			
			

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// The user pressed enter
					// Search the Correspondig item
					if (!insert) {
						
						GregorianCalendar thisday = TimeTools.getServerDateTime();
						GregorianCalendar billDate = thisBill.getDate();
						int thisMonth = thisday.get(GregorianCalendar.MONTH);
						int billMonth = billDate.get(GregorianCalendar.MONTH);
						if(thisMonth>billMonth && !Param.bool("ENABLEBILLEDITOVERMONTH")){
							return;
						}
					}
					if(!jTextFieldSearch.getText().equals("")){
						searchItem();
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					PatientBillEdit.this.selectedBillItem = null;
					loadFields();
				}
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					if (!insert) {
						GregorianCalendar thisday = TimeTools.getServerDateTime();
						GregorianCalendar billDate = thisBill.getDate();
						int thisMonth = thisday.get(GregorianCalendar.MONTH);
						int billMonth = billDate.get(GregorianCalendar.MONTH);
						if(thisMonth>billMonth && !Param.bool("ENABLEBILLEDITOVERMONTH")){
							return;
						}
					}
					
					
					searchItem();
				}
			}
		});
		jTextFieldSearch.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {

			}

			@Override
			public void focusGained(FocusEvent e) {
				jTextFieldSearch.selectAll();

			}
		});

		return jTextFieldSearch;
	}

	private void loadFields() {		

		if (this.selectedBillItem != null) {
			jTextFieldSearch.setText(this.selectedBillItem.getItemDisplayCode());
			jTextFieldDescription.setText(this.selectedBillItem.getItemDescription());
			jTextFieldQty.setText(String.valueOf(this.selectedBillItem.getItemQuantity()));
			jTextFieldPrice.setText(String.valueOf(this.selectedBillItem.getItemAmount()));
			jTextFieldQty.grabFocus();
			jTextFieldSearch.setEnabled(false);
		
			jTextFieldQty.grabFocus();
			//
		} else {
			jTextFieldSearch.setText("");
			jTextFieldDescription.setText("");
			jTextFieldQty.setText("");
			jTextFieldPrice.setText("");
			jTextFieldSearch.setEnabled(true);			
			jTextFieldSearch.grabFocus();
			
			jTextFieldQty.setEnabled(true);

		}
	}

	private JTextField getJTextFieldDescription() {
		if (jTextFieldDescription == null) {
			jTextFieldDescription = new JTextField();
			jTextFieldDescription.setColumns(10);
			jTextFieldDescription.setEnabled(false);
		}
		return jTextFieldDescription;
	}

	private JTextField getJTextFieldQty() {
		if (jTextFieldQty == null) {
			jTextFieldQty = new JTextField();
			jTextFieldQty.setColumns(10);
		}
		jTextFieldQty.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				jTextFieldQty.selectAll();

			}
		});
		jTextFieldQty.setHorizontalAlignment(JTextField.RIGHT);
		jTextFieldQty.addKeyListener(new KeyListener() {

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
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(updateBillItem()){
						PatientBillEdit.this.selectedBillItem = null;
						loadFields();
					}
					
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					PatientBillEdit.this.selectedBillItem = null;
					loadFields();
				}
			}
		});
		return jTextFieldQty;
	}

	private JTextField getJTextFieldPrice() {
		if (jTextFieldPrice == null) {
			jTextFieldPrice = new JTextField();
			jTextFieldPrice.setColumns(10);
		}
		jTextFieldPrice.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				jTextFieldPrice.selectAll();

			}
		});
		jTextFieldPrice.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					if(updateBillItem()){
						PatientBillEdit.this.selectedBillItem = null;
						loadFields();
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					PatientBillEdit.this.selectedBillItem = null;
					loadFields();
				}
			}
		});
		jTextFieldPrice.setHorizontalAlignment(JTextField.RIGHT);
		return jTextFieldPrice;
	}

	private boolean updateBillItem() {
		// TODO Auto-generated method stub
		if(selectedBillItem==null){
			return false;
		}
		String strQty = jTextFieldQty.getText();
		if (strQty == null || strQty.trim().equals("")) {
			JOptionPane.showMessageDialog(PatientBillEdit.this,
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		double qty = 1;
		try {
			strQty=strQty.replaceAll(",", ".");
			qty = Double.valueOf(strQty);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(PatientBillEdit.this,
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (qty == 0) {
			JOptionPane.showMessageDialog(PatientBillEdit.this,
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		////
		if (qty > 1 && Param.bool("CREATELABORATORYAUTO")) {
			if(this.selectedBillItem.getItemGroup() != null && this.selectedBillItem.getItemGroup().equals(ItemGroup.EXAM.getCode())){
					JOptionPane.showMessageDialog(PatientBillEdit.this,
							"Vous ne pouvez pas enregistrer une quantite superieure a un pour un examen dans ce cas", //$NON-NLS-1$
							MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
					qty =1;
					jTextFieldQty.setText(1+"");
					//return false;
			}
			
		}
		////

		String strPrice = jTextFieldPrice.getText();

		double price = 0;
		try {
			if (strPrice == null) {
				JOptionPane.showMessageDialog(PatientBillEdit.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
				return false;
			}

			price = Double.valueOf(strPrice);

		} catch (Exception eee) {
			JOptionPane.showMessageDialog(PatientBillEdit.this,
				MessageBundle.getMessage("angal.newbill.invalidpricepleasetryagain"), //$NON-NLS-1$
				MessageBundle.getMessage("angal.newbill.invalidprice"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		String itemGroup=selectedBillItem.getItemGroup();
		
		if(itemGroup!=null && itemGroup.equals(ItemGroup.MEDICAL.getCode())){
			//Check quantity
			if(Param.bool("STOCKMVTONBILLSAVE")  ){
				Price med=getPrice(selectedBillItem.getItemId(), ItemGroup.MEDICAL);
				if(!containPrice(med, qty)){
					JOptionPane.showMessageDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.qtynotinstock"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		this.selectedBillItem.setItemAmount(price);
		this.selectedBillItem.setItemQuantity(qty);

		jTableBill.updateUI();
		updateTotals();
		return true;

	}

	private BillItems addOtherPrice(Price oth, int qty) {
		Icon icon;
		boolean isPrice = true;
		BillItems item = null;
		if (pbiID != 0 && oth != null) {
			if(!othersList.contains(oth.getId())) {
				oth = reductionPlanManager.getOtherPrice(oth, pbiID);
				othersList.add(oth.getId());
			}
		}

		if (qty <= 0)
			qty = 1;

		PricesOthersManager othManager = new PricesOthersManager();
		PricesOthers othPrice = null;

		if (oth != null) {

			othPrice = othManager.getOther(Integer.valueOf(oth.getItem()));
			if (othPrice.isUndefined()) {

				icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
				String price = (String) JOptionPane.showInputDialog(PatientBillEdit.this,
						MessageBundle.getMessage("angal.newbill.howmuchisit"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.newbill.undefined"), //$NON-NLS-1$
						JOptionPane.PLAIN_MESSAGE, icon, null, "0"); //$NON-NLS-1$
				try {
					if (price == null)
						return null;
					double amount = Double.valueOf(price);
					oth.setPrice(amount);
					isPrice = false;

					// JOptionPane.showConfirmDialog(null,
					// " undef--" + oth.getPrice());

				} catch (Exception eee) {
					JOptionPane.showMessageDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.invalidpricepleasetryagain"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.invalidprice"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			if (othPrice.isDischarge()) {
				double amount = oth.getPrice();
				oth.setPrice(-amount);
				// JOptionPane.showConfirmDialog(null,
				// " isDischarge --" + oth.getPrice());
			}
			item = addItem(oth, qty, isPrice, 0);
		}
		if (item != null) {
			item.setItemDisplayCode(othPrice.getCode());
		}
		return item;
	}

	private BillItems addExamAndOperation(Price price) {
		if (pbiID != 0 && price != null) {
			if(!operationsList.contains(price.getId())) {
				price = reductionPlanManager.getOperationPrice(price, pbiID);
				operationsList.add(price.getId());
			}
		}
		BillItems item = addItem(price, 1, true, 0);
		if (item != null) {
			item.setItemDisplayCode(price.getItem());
		}
		return item;
	}

	private BillItems addMedical(MedicalWard med, int qty) {
		
		BillItems billItem = null;
		Price price = getPrice(med);

		if (price != null) {

			if (pbiID != 0) {
				//TO REMOVE
				if(!medicalsList.contains(price.getId())) {
					medicalsList.add(price.getId());
					price = reductionPlanManager.getMedicalPrice(price, pbiID);
				}
				
			}
			if (Param.bool("STOCKMVTONBILLSAVE")  ) {
				if (containPrice(price, qty)) {
					billItem = addItem(price, qty, true, 0);
				} else {
					JOptionPane.showMessageDialog(PatientBillEdit.this,
							MessageBundle.getMessage("angal.newbill.qtynotinstock"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				billItem = addItem(price, qty, true, 0);
			}
			// addItem(med, qty, true, 0);

		}
		if (billItem != null) {
			billItem.setItemDisplayCode(med.getMedical().getProd_code());
		}
		return billItem;
	}

	private Price getPrice(MedicalWard med) {

		for (Price price : prcListArray) {
			if (price.getGroup().equals(ItemGroup.MEDICAL.getCode())) {
				if (price.getItem().equals(String.valueOf(med.getMedical().getCode()))) {
					return price;
				}
			}
		}
		return null;
	}
	
	private Price getPrice(String itemCode, ItemGroup group) {

		for (Price price : prcListArray) {
			if (price.getGroup().equals(group.getCode())) {
				if (price.getItem().equals(itemCode)) {
					return price;
				}
			}
		}
		return null;
	}
	
	private Price getPrice(String itemCode, String group) {

		for (Price price : prcListArray) {
			if (price.getGroup().equals(group)) {
				if (price.getItem().equals(itemCode)) {
					return price;
				}
			}
		}
		return null;
	}
	

	private Price getPrice(PricesOthers oth) {

		for (Price price : prcListArray) {
			if (price.getGroup().equals(ItemGroup.OTHER.getCode())) {
				if (price.getItem().equals(String.valueOf(oth.getId()))) {
					return price;
				}
			}
		}
		return null;
	}

	private MedicalWard getMedicalWard(Price price) {
		for (Iterator<MedicalWard> iterator = medWardList.iterator(); iterator.hasNext();) {
			MedicalWard medicalWard = (MedicalWard) iterator.next();
			String code = medicalWard.getMedical().getCode() + "";
			if (code.equals(price.getItem())) {
				return medicalWard;
			}
		}
		return null;
	}

	private void searchItem() {
	
		String searchValue = jTextFieldSearch.getText();

		if (patientSelected == null) {
			JOptionPane.showMessageDialog(PatientBillEdit.this,
					MessageBundle.getMessage("angal.patvac.pleaseselectapatient"));
			return;
		}
		ArrayList<Object> itemArray = new ArrayList<Object>();
		for (Price price : prcListArray) {
			if (!price.getGroup().equals(ItemGroup.MEDICAL.getCode())) { // $NON-NLS-1$
				if(price.getGroup().equals(ItemGroup.OTHER.getCode())){
					PricesOthers othPrice = othManager.getOther(Integer.valueOf(price.getItem()));
					itemArray.add(othPrice);
				}
				else{
					itemArray.add(price);
				}				
			}
		}
		
		// AddMedicals
		/** getting the ward code from the combobox **/
		Ward selectedWard = null;
		String wardCode = "";
		if ((!wardBox.getSelectedItem().equals("")) && (wardBox.getSelectedItem() != null)) {
			selectedWard = (Ward) wardBox.getSelectedItem();
			wardCode = selectedWard.getCode();
		}

		if ((wardCode == null || wardCode.equals("")) && Param.bool("STOCKMVTONBILLSAVE")) {
			JOptionPane.showMessageDialog(PatientBillEdit.this,
					MessageBundle.getMessage("angal.patvac.pleaseselectaward"));
			return;
		}
		
		MedicalWard mwd;
		for (Price price : prcListArray) {
			if (price.getGroup().equals(ItemGroup.MEDICAL.getCode())) {
				if (Param.bool("STOCKMVTONBILLSAVE")) {
					if(!Param.bool("ALLOWPRODUCTINBILLWIHTEMPTYSTOCK")){
						if (containPrice(price, 1.0)) {
							mwd = getMedicalWard(price);
							if (mwd != null) {
								itemArray.add(mwd);
							}
						}
					}else{
						mwd = getMedicalWard(price);
						if (mwd != null) {
							itemArray.add(mwd);
						}
					}
				} else {
					itemArray.add(price);
				}
			}
		}
		
		OhTableModel<Object> modelOh = new OhTableModel<Object>(itemArray, true);

		Object selectedItem = null;
		selectedItem = modelOh.filter(searchValue);

		if (selectedItem == null) {
			BillItemPicker framas = new BillItemPicker(modelOh);

			framas.setSize(300, 400);

			JDialog dialog = new JDialog();
			dialog.setLocationRelativeTo(null);
			dialog.setSize(600, 350);
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);

			framas.setParentFrame(dialog);
			dialog.setContentPane(framas);
			dialog.setIconImage(ico);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			selectedItem = framas.getSelectedObject();
		}
		              
		
		Price price = null;
		MedicalWard med = null;
		PricesOthers oth = null;

		int qty = 1;
		
		BillItems billItem = null;
		if (selectedItem instanceof MedicalWard) {
			med = (MedicalWard) selectedItem;
			billItem = addMedical(med, qty);
			// Add medical
			
		} else if (selectedItem instanceof Price) {
			price = (Price) selectedItem;
			// Add price
			if (price.getGroup().equals(ItemGroup.MEDICAL.getCode())) {
				med = getMedicalWard(price);
				billItem = addMedical(med, qty);
				billItem.setItemDisplayCode(med.getMedical().getProd_code());
			} else if (price.getGroup().equals(ItemGroup.EXAM.getCode())) {
				billItem = addExamAndOperation(price);
			} else if (price.getGroup().equals(ItemGroup.OPERATION.getCode())) {
				billItem = addExamAndOperation(price);
			} else if (price.getGroup().equals(ItemGroup.OTHER.getCode())) {
				billItem = addOtherPrice(price, qty);
				
				PricesOthers othPrice = othManager.getOther(Integer.valueOf(price.getItem()));
				billItem.setItemDisplayCode(othPrice.getCode());
			}
		} else if (selectedItem instanceof PricesOthers) {
			oth = (PricesOthers) selectedItem;
			// Add other
			price = getPrice(oth);
			billItem = addOtherPrice(price, qty);
			billItem.setItemDisplayCode(oth.getCode());
		}

		if (billItem != null) {
			selectedBillItem = billItem;
			
			loadFields();
		}
		jTextFieldQty.setText("1");
	}
	private JLabel getLblQty() {
		if (lblQty == null) {
			lblQty = new JLabel(MessageBundle.getMessage("angal.patientbilledit.qty"));
			lblQty.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return lblQty;
	}
	
	private JLabel getLblAmount() {
		if (lblAmount == null) {
			lblAmount = new JLabel(MessageBundle.getMessage("angal.patientbilledit.amount"));
			lblAmount.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return lblAmount;
	}
	private JLabel getLblDescription() {
		if (lblDescription == null) {
			lblDescription = new JLabel(MessageBundle.getMessage("angal.patientbilledit.description"));
			lblDescription.setFont(new Font("Tahoma", Font.BOLD, 12));
		}
		return lblDescription;
	}
	private JLabel getLblTexteDeRecherche() {
		if (lblTexteDeRecherche == null) {
			lblTexteDeRecherche = new JLabel(MessageBundle.getMessage("angal.patientbilledit.recherche"));
			lblTexteDeRecherche.setFont(new Font("Tahoma", Font.BOLD, 12));
		}
		return lblTexteDeRecherche;
	}
	private JLabel getEnterPatientCodeLabel() {
		if (enterPatientCodeLabel == null) {
			enterPatientCodeLabel = new JLabel("Patient code");
		}
		return enterPatientCodeLabel;
	}
	private JPanel getJPanelGarante() {
		if (jPanelGarante == null) {
			jPanelGarante = new JPanel();
			jPanelGarante.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			jPanelGarante.add(getLblGarante());
			jPanelGarante.add(getJComboGarante());
		}
		return jPanelGarante;
	}
	private JLabel getLblGarante() {
		if (lblGarante == null) {
			lblGarante = new JLabel(MessageBundle.getMessage("angal.patient.bill.garante"));
			lblGarante.setFont(new Font("Tahoma", Font.PLAIN, 14));
		}
		return lblGarante;
	}
	
	private JComboBox getJComboGarante() {
		if (jComboGarante == null) {
			jComboGarante = new JComboBox();
			UserBrowsingManager manager = new UserBrowsingManager();
			users = manager.getUser();
			jComboGarante.addItem("");
			for (User u : users){
				jComboGarante.addItem(u.getUserName());
			}
			Dimension d = jComboGarante.getPreferredSize();
			jComboGarante.setPreferredSize(new Dimension(350, d.height));
		}
		if (thisBill != null) {			
			jComboGarante.setSelectedItem(thisBill.getGarante());
		}
		return jComboGarante;
	}
}
