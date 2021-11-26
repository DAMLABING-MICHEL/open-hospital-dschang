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
import java.util.Map;
import java.util.Set;

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
import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.TxtPrinter;
import org.isf.lab.manager.LabManager;
import org.isf.medicalinventory.gui.InventoryWardEdit;
import org.isf.medicalstock.gui.MovStockMultipleCharging;
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
import org.isf.stat.manager.GenericReportBillSimulate;
import org.isf.supplier.gui.SupplierEdit;
import org.isf.supplier.model.SimulateBill;
import org.isf.accounting.gui.SelectPrescriptions;
import org.isf.accounting.gui.PatientBillEdit.PatientBillListener;
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
public class PatientBillEditSimulate extends JDialog implements SelectionListener, PrescriptionSelectionListener {

	// LISTENER INTERFACE
	// --------------------------------------------------------
	private EventListenerList patientBillListener = new EventListenerList();

	public interface PatientBillListener extends EventListener {
		public void billInserted(AWTEvent aEvent);
	}

	public interface BillItemEditSimulateListener extends EventListener {
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
					int response = JOptionPane.showConfirmDialog(PatientBillEditSimulate.this,
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
					int response = JOptionPane.showConfirmDialog(PatientBillEditSimulate.this,
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
						int resp = JOptionPane.showConfirmDialog(PatientBillEditSimulate.this,
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
	private JTextField jTextFieldPatient;
	private JPanel jPanelData;
	private JTable jTableTotal;
	private JScrollPane jScrollPaneTotal;
	private JTable jTableBigTotal;
	private JScrollPane jScrollPaneBigTotal;
	private JTable jTableBalance;
	private JScrollPane jScrollPaneBalance;
	private JPanel jPanelTop;
	//private JDateChooser jCalendarDate;
	//private JLabel jLabelDate;
	private JLabel jLabelPatient;
	private JButton jButtonRemoveItem;
	private JPanel jPanelButtonsPayment;
	private JButton jButtonRemovePayment;

	private JPanel jPanelButtonsBill;
	private JPanel jPanelButtonsActions;
	private JButton jButtonClose;
	private JButton jButtonPrintPayment;
	private JButton jButtonSave;
	private JButton jButtonBalance;
	private JButton jButtonCustom;
	private JButton jButtonPickPatient;
	private JButton jButtonTrashPatient;
	
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
	//private JPanel jPanelGarante;
	private JLabel lblGarante;
	private JComboBox jComboGarante;
	
	public PatientBillEditSimulate() {
		PatientBillEditSimulate newBill = new PatientBillEditSimulate(null, new Bill(), true);
		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
		newBill.setVisible(true);
		//modal exclude

	}

	public PatientBillEditSimulate(JFrame owner, Patient patient) {

		Bill bill = new Bill();
		//this.owner = owner;
		bill.setPatient(true);
		bill.setPatID(patient.getCode());
		bill.setPatName(patient.getName());
		PatientBillEditSimulate newBill = new PatientBillEditSimulate(owner, bill, true);
		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
		newBill.setPatientSelected(patient);
		newBill.setVisible(true);

	}

	public PatientBillEditSimulate(JFrame owner, Bill bill, boolean inserting) {
		super(owner, true);
		//super();					
		this.insert = inserting; //INSERT = TRUE
		setBill(bill);
		initComponents();
		ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();
		updateTotals();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

	}

	private void setBill(Bill bill) {
		this.thisBill = bill;
		billDate = bill.getDate();
		billItems = billManager.getItemsBy(thisBill.getId());
		payItems = billManager.getPayments(thisBill.getId());
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
			//System.out.println("la date de la facture: "+thisBill.getDate().getTime().toString());
			GregorianCalendar thisday = TimeTools.getServerDateTime();
			GregorianCalendar billDate = thisBill.getDate();
			int thisMonth = thisday.get(GregorianCalendar.MONTH);
			int billMonth = billDate.get(GregorianCalendar.MONTH);
			int thisYear = thisday.get(GregorianCalendar.YEAR);
			int billBillYear = billDate.get(GregorianCalendar.YEAR);
			if(thisYear>billBillYear || thisMonth>billMonth){
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
			//	jCalendarDate.grabFocus(); 
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
				JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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


	private JPanel getJPanelDate() {
		if (jPanelDate == null) {
			jPanelDate = new JPanel();
			jPanelDate.setLayout(new FlowLayout(FlowLayout.LEFT));
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

					SelectPatient sp = new SelectPatient(PatientBillEditSimulate.this, patientSelected);
					sp.addSelectionListener(PatientBillEditSimulate.this);
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
			/*if(Param.bool("ALLOWGARANTEPERSON")){
				jPanelTop.add(getJPanelGarante());
			}*/
			jPanelTop.add(getPanelLabel());
			jPanelTop.add(getPanelSearch());
			// jPanelTop.add(getJButtonAddMedicalPrescription());
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

	private JPanel getJPanelButtonsPayment() {
		if (jPanelButtonsPayment == null) {
			jPanelButtonsPayment = new JPanel();
			jPanelButtonsPayment.setLayout(new BoxLayout(jPanelButtonsPayment, BoxLayout.Y_AXIS));
			//if (Param.bool("RECEIPTPRINTER"))
			jPanelButtonsPayment.add(getJButtonPrintPayment());
			jPanelButtonsPayment.add(getJButtonClose());
		}
		return jPanelButtonsPayment;
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

	private JPanel getJPanelButtonsActions() {
		if (jPanelButtonsActions == null) {
			jPanelButtonsActions = new JPanel();
			jPanelButtonsActions.setLayout(new BoxLayout(jPanelButtonsActions, BoxLayout.Y_AXIS));
		}
		return jPanelButtonsActions;
	}

	private JButton getJButtonPrintPayment() { //jPanelButtonsPayment.add(getJButtonPrintPayment());
		if (jButtonPrintPayment == null) {
			jButtonPrintPayment = new JButton();
			jButtonPrintPayment.setText(MessageBundle.getMessage("angal.menu.btn.printing")); //$NON-NLS-1$
			jButtonPrintPayment.setMaximumSize(new Dimension(ButtonWidthPayment, ButtonHeight));
			jButtonPrintPayment.setHorizontalAlignment(SwingConstants.LEFT);
			jButtonPrintPayment.setIcon(new ImageIcon("rsc/icons/receipt_button.png")); //$NON-NLS-1$
			jButtonPrintPayment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ArrayList<SimulateBill> simulatebillitems = new ArrayList<SimulateBill>();
					int i = 0;
					if(Param.bool("ENABLEPRICECONTROL")){
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
					i = 0; 
					Double TOTAL_GLOBAL = 0.0;
					String PAT_NAME = patientSelected != null? patientSelected.getName() : "";
					while(i < billItems.size()){
						BillItems item = billItems.get(i);
						try {
							simulatebillitems.add(
							new SimulateBill(
								item.getItemDisplayCode(), 
								item.getItemDescription(), 
								item.getItemAmount(),
								getSettingReductionRate(item), //rEMISE, 
								new Double(item.getItemQuantity()).intValue(), 
								item.getItemAmount() * item.getItemQuantity()
							));
							TOTAL_GLOBAL += item.getItemAmount() * item.getItemQuantity();
						} catch (OHException e1) {
							e1.printStackTrace();
						}
						i++;
					}
					TxtPrinter.getTxtPrinter();
					new GenericReportBillSimulate(thisBill.getId(), simulatebillitems, TOTAL_GLOBAL, PAT_NAME, "PatientBillExtendedSimulate", false, true);
					dispose();
				}
			});
		}
		return jButtonPrintPayment;
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
				   dispose();
				}
			});
		}
		return jButtonClose;
	}

	private double getSettingPrice(BillItems item){
	//	if(Param.bool("ENABLEPRICECONTROL")){
			
		
		PriceListManager listManager = new PriceListManager();
		int priceListId = patientSelected.getListID();
		if(priceListId == 0) priceListId = 1;
		Price goodPrice = new Price();
		int reductionID = patientSelected.getReductionPlanID();
		if(reductionID == 0 ){
			goodPrice = listManager.getPrice(priceListId, item.getItemGroup(), item.getItemId());
		}else{
			String group = item.getItemGroup();
			Price price = prcManager.getPrice(priceListId, group, item.getItemId());
			if(group.equals("MED")) goodPrice = reductionPlanManager.getMedicalPrice(price, reductionID);
			if(group.equals("OPE")) goodPrice = reductionPlanManager.getOperationPrice(price, reductionID);
			if(group.equals("EXA")) goodPrice = reductionPlanManager.getExamPrice(price, reductionID);
			if(group.equals("OTH")) goodPrice = reductionPlanManager.getOtherPrice(price, reductionID);
		}
			return goodPrice.getPrice();
	/*	}else{
			
		}*/
	}
	
	private double getSettingReductionRate(BillItems item) throws OHException{
		
		Double reductionRate = 0.0;
		int reductionID = patientSelected.getReductionPlanID();
		if(reductionID != 0 ){
			ReductionPlan reductionPlan = reductionPlanManager.getReductionPlan(reductionID);
			String group = item.getItemGroup();
			if(group.equals("MED")) reductionRate = reductionPlan.getExamRate();
			if(group.equals("OPE")) reductionRate = reductionPlan.getOperationRate();
			if(group.equals("EXA")) reductionRate = reductionPlan.getExamRate();
			if(group.equals("OTH")) reductionRate = reductionPlan.getOtherRate();
		}
		return reductionRate;
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
						String quantity = (String) JOptionPane.showInputDialog(PatientBillEditSimulate.this,
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
							JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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

					if (pbiID != 0 && exa != null) {
						exa = reductionPlanManager.getExamPrice(exa, pbiID);
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
					String quantity = (String) JOptionPane.showInputDialog(PatientBillEditSimulate.this,
							MessageBundle.getMessage("angal.newbill.insertquantity"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.quantity"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE, icon, null, qty);
					try {
						if (quantity == null || quantity.equals(""))
							return;
						qty = Integer.valueOf(quantity);

						addMedical(med, qty);

					} catch (Exception eee) {
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
						JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
								MessageBundle.getMessage("angal.patvac.pleaseselectaward"));
						return;
					}

					SelectPrescriptions selectePrescriptions = new SelectPrescriptions(PatientBillEditSimulate.this,
							patientSelected);
					selectePrescriptions.addPrescriptionSelectedListener(PatientBillEditSimulate.this);
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
					String desc = (String) JOptionPane.showInputDialog(PatientBillEditSimulate.this,
							MessageBundle.getMessage("angal.newbill.chooseadescription"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.newbill.customitem"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE, icon, null,
							MessageBundle.getMessage("angal.newbill.newdescription")); //$NON-NLS-1$
					if (desc == null || desc.equals("")) { //$NON-NLS-1$
						return;
					} else {
						icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
						String price = (String) JOptionPane.showInputDialog(PatientBillEditSimulate.this,
								MessageBundle.getMessage("angal.newbill.howmuchisit"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.customitem"), //$NON-NLS-1$
								JOptionPane.PLAIN_MESSAGE, icon, null, "0"); //$NON-NLS-1$
						try {
							amount = Double.valueOf(price);
						} catch (Exception eee) {
							JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
									MessageBundle.getMessage("angal.newbill.invalidpricepleasetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.newbill.invalidprice"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					BillItems newItem = new BillItems(0, billID, false, "", 
							desc, amount, 1, amount, new GregorianCalendar());
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
	}
	
	private BillItems addItem(Price prc, int qty, boolean isPrice, int prescId)  {
		if (prc != null) {
		
			double amount = prc.getPrice();
			Price brut = new PriceListManager().getPrice(listSelected.getId(), prc.getGroup(), prc.getItem());
			double priceBrut = 0.0;
			if(brut!=null)
				priceBrut = brut.getPrice();
											
			BillItems item = new BillItems(0, billID, isPrice, prc.getGroup() + prc.getItem(), prc.getDesc(), amount, qty, priceBrut, new GregorianCalendar());
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

	//	jCalendarDate.setDate(thisBill.getDate().getTime());
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
				if(item.getItemGroup() != null && item.getItemGroup().equals(ItemGroup.EXAM.getCode())){
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
		if (wardBox == null) {
			wardBox = new JComboBox();
			wardBox.setPreferredSize(new Dimension(130, 25));
			String wardCode = MainMenu.getUserWard();

			if (!insert) {
				wardCode = this.thisBill.getWardCode();
			}
			// wardBox.addItem(MessageBundle.getMessage("angal.medicalstock.all")); 
			ArrayList<Ward> wardList = wbm.getWards();
			boolean trouve = false;
			for (Ward ward : wardList) {
				if (ward.getCode().equals(wardCode)) {
					wardBox.addItem(ward);
					trouve = true;
					MovWardBrowserManager manager = new MovWardBrowserManager();
					medWardList = manager.getMedicalsWard(wardCode);
				//	selectedWard = ward;					
					break;
				}
			}
			if (!trouve) {
				wardBox.addItem("");
			}
			for (org.isf.ward.model.Ward elem : wardList) {
				wardBox.addItem(elem);
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
			//		selectedWard = ward;
				}
			}
		});
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
						if(thisMonth>billMonth){
							return;
						}
					}
					if(!jTextFieldSearch.getText().equals("")){
						searchItem();
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					PatientBillEditSimulate.this.selectedBillItem = null;
					loadFields();
				}
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					if (!insert) {
						GregorianCalendar thisday = TimeTools.getServerDateTime();
						GregorianCalendar billDate = thisBill.getDate();
						int thisMonth = thisday.get(GregorianCalendar.MONTH);
						int billMonth = billDate.get(GregorianCalendar.MONTH);
						if(thisMonth>billMonth){
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
						PatientBillEditSimulate.this.selectedBillItem = null;
						loadFields();
					}
					
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					PatientBillEditSimulate.this.selectedBillItem = null;
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
						PatientBillEditSimulate.this.selectedBillItem = null;
						loadFields();
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					PatientBillEditSimulate.this.selectedBillItem = null;
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
			JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
			JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (qty == 0) {
			JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		////
		if (qty > 1 && Param.bool("CREATELABORATORYAUTO")) {
			if(this.selectedBillItem.getItemGroup() != null && this.selectedBillItem.getItemGroup().equals(ItemGroup.EXAM.getCode())){
					JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
				JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
				return false;
			}

			price = Double.valueOf(strPrice);

		} catch (Exception eee) {
			JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
					JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
			oth = reductionPlanManager.getOtherPrice(oth, pbiID);
		}

		if (qty <= 0)
			qty = 1;

		PricesOthersManager othManager = new PricesOthersManager();
		PricesOthers othPrice = null;

		if (oth != null) {

			othPrice = othManager.getOther(Integer.valueOf(oth.getItem()));
			if (othPrice.isUndefined()) {

				icon = new ImageIcon("rsc/icons/money_dialog.png"); //$NON-NLS-1$
				String price = (String) JOptionPane.showInputDialog(PatientBillEditSimulate.this,
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
					JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
			price = reductionPlanManager.getOperationPrice(price, pbiID);
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
				price = reductionPlanManager.getMedicalPrice(price, pbiID);
			}
			if (Param.bool("STOCKMVTONBILLSAVE")  ) {
				if (containPrice(price, qty)) {
					billItem = addItem(price, qty, true, 0);
				} else {
					JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
			JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
			JOptionPane.showMessageDialog(PatientBillEditSimulate.this,
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
