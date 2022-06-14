package org.isf.accounting.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
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
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.model.RefundBillItem;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.menu.gui.MainMenu;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.parameters.manager.Param;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.Price;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

import com.toedter.calendar.JDateChooser;
import javax.swing.JComboBox;
import java.awt.Font;

/**
 * Bill refund UI
 * 
 * Helps to refund some or all items of bill. Notice that only closed bill can
 * be refunded
 * 
 * @author Silevester D.
 * @since 18/05/2022
 * 
 */
public class BillRefund extends JDialog {

	private static final long serialVersionUID = 1L;
	
	// LISTENER INTERFACE
	// --------------------------------------------------------
	private static EventListenerList patientRefundBillListener = new EventListenerList();

	public interface PatientRefundBillListener extends EventListener {
		public void refundBillInserted(AWTEvent aEvent);
	}
	
	public void addPatientRefundBillListener(PatientRefundBillListener l) {
		patientRefundBillListener.add(PatientRefundBillListener.class, l);
	}

	private void fireRefundBillInserted(Bill aBill) {
		
		AWTEvent event = new AWTEvent(aBill, AWTEvent.RESERVED_ID_MAX + 1) {
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = patientRefundBillListener.getListeners(PatientRefundBillListener.class);
		
		for (int i = 0; i < listeners.length; i++)
			((PatientRefundBillListener) listeners[i]).refundBillInserted(event);
	}

	private JTable jTableBill;
	private JScrollPane jScrollPaneBill;
	private JPanel jPanelButtons;
	private JPanel jPanelDate;
	private JPanel jPanelPatient;
	private JTextField jTextFieldPatient;
	private JPanel jPanelData;
	private JPanel helpTextPanel;
	private JTable jTableAmountToRefund;
	private JScrollPane jScrollPaneAmountToRefund;
	private JPanel jPanelTop;
	private JDateChooser jCalendarDate;
	private JLabel jLabelDate;
	private JLabel jLabelPatient;
	private JPanel jPanelButtonsActions;
	private JButton jButtonClose;
	private JButton jButtonSave;

	private ArrayList<User> users;

	private static final Dimension PatientDimension = new Dimension(300, 30);
	private static final Dimension LabelsDimension = new Dimension(60, 30);
	private static final int PanelWidth = 620;
	private static final int ButtonWidth = 160;
	private static final int PriceWidth = 90;
	private static final int QuantityWidth = 80;
	private static final int RefundedQtyWidth = 90;
	private static final int RefundQtyWidth = 130;
	private static final int BillHeight = 300;
	private static final int TotalHeight = 30;
	private static final int AmountToRefundHeight = 30;
	private static final int ButtonHeight = 25;

	private int billID;
	private BigDecimal amountToRefund = new BigDecimal(0);
	private boolean insert;
	private Bill thisBill;
	private GregorianCalendar billDate = TimeTools.getServerDateTime();
	private GregorianCalendar today = TimeTools.getServerDateTime();

	private boolean[] columnEditable = { false, false, false, false, true };
	private String[] billColumnNames = { MessageBundle.getMessage("angal.billrefund.item"),
			MessageBundle.getMessage("angal.billrefund.qty"), MessageBundle.getMessage("angal.billrefund.amount"),
			MessageBundle.getMessage("angal.billrefund.refundedqty"),
			MessageBundle.getMessage("angal.billrefund.refundqty"), };

	// Items and Payments (ALL)
	private BillBrowserManager billManager = new BillBrowserManager();
	private PatientBrowserManager patManager = new PatientBrowserManager();

	// Prices, Items and Payments for the tables
	private ArrayList<RefundBillItem> refundBillItems = new ArrayList<RefundBillItem>();
	// User
	private String user = MainMenu.getUser();
	private JComboBox<Object> wardBox;
	private JLabel wardLabel;

	OhDefaultCellRenderer cellRenderer = new OhDefaultCellRenderer();

	private JPanel jPanelGarante;
	private JLabel lblGarante;
	private JComboBox<String> jComboGarante;
	
	JTextField textFieldEditor = new JTextField();

	public BillRefund(JFrame owner, Bill bill) {
		super(owner, true);

		loadBillInfos(bill);
		initComponents();

		updateTotals();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		setResizable(false);
	}

	private void loadBillInfos(Bill bill) {
		thisBill = bill;
		refundBillItems = billManager.getRefundItems(thisBill.getId());

		checkBill();

		if (thisBill.getGarante() != null && thisBill.getGarante() != "") {
			try {
				if (jComboGarante != null) {
					jComboGarante.setSelectedItem(thisBill.getGarante());
				}
			} catch (Exception e) {
				System.out.println("error " + e.getMessage());
			}
		}

		if (thisBill.getPatName() != null && !thisBill.getPatName().equals("") && thisBill.getPatID() == 0) {
			PatientBrowserManager patManager = new PatientBrowserManager();
			try {
				Patient pat = patManager.getPatient(thisBill.getPatName());
				thisBill.setPatID(pat.getCode());
				thisBill.setPatient(true);
			} catch (Exception e) {
				System.out.println("error " + e.getMessage());
			}
		}
	}

	private void initComponents() {
		getContentPane().add(getJPanelTop(), BorderLayout.NORTH);

		getContentPane().add(getJPanelData(), BorderLayout.CENTER);

		getContentPane().add(getJPanelButtons(), BorderLayout.SOUTH);

		setTitle(MessageBundle.getMessage("angal.billrefund.title") + " " + thisBill.getId()); //$NON-NLS-1$

		pack();

		/***************/
		WindowListener exitListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

			}
		};

		this.addWindowListener(exitListener);
	}

	// check if PriceList and Patient still exist
	private void checkBill() {

		if (thisBill.isPatient()) {

			Patient patient = patManager.getPatient(thisBill.getPatID());
			if (patient == null) { // Patient not found
				Icon icon = new ImageIcon("rsc/icons/patient_dialog.png");
				JOptionPane.showMessageDialog(BillRefund.this,
						MessageBundle.getMessage("angal.newbill.patientassociatedwiththisbillnolongerexists")
								+ "no longer exists",
						"Warning", JOptionPane.WARNING_MESSAGE, icon);

				thisBill.setPatient(false);
				thisBill.setPatID(0);
			}
		}
	}

	private JPanel getJPanelData() {
		if (jPanelData == null) {
			jPanelData = new JPanel();
			jPanelData.setLayout(new BoxLayout(jPanelData, BoxLayout.Y_AXIS));
			jPanelData.add(getHelpTextPanel());
			jPanelData.add(getJScrollPaneBill());
			jPanelData.add(getJScrollPaneAmountToRefund());
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

	private JDateChooser getJCalendarDate() {
		if (jCalendarDate == null) {
			jCalendarDate = new JDateChooser(billDate.getTime());
			jCalendarDate.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarDate.setDateFormatString("dd/MM/yy - HH:mm:ss");
			jCalendarDate.setEnabled(false);
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
		}
		return jPanelDate;
	}

	private JPanel getJPanelTop() {
		if (jPanelTop == null) {
			jPanelTop = new JPanel();
			jPanelTop.setLayout(new BoxLayout(jPanelTop, BoxLayout.Y_AXIS));
			jPanelTop.add(getJPanelDate());
			jPanelTop.add(getJPanelPatient());
			if (Param.bool("ALLOWGARANTEPERSON")) {
				jPanelTop.add(getJPanelGarante());
			}
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

			DefaultCellEditor cellEditor = new DefaultCellEditor(textFieldEditor);

			jTableBill.addMouseMotionListener(new MouseMotionListener() {

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

			jTableBill.setFillsViewportHeight(true);
			jTableBill.setModel(new BillTableModel());

			jTableBill.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						jTableBill.editCellAt(jTableBill.getSelectedRow(), 4);
						jTableBill.setSurrendersFocusOnKeystroke(true);
						jTableBill.getEditorComponent().requestFocus();
						textFieldEditor.selectAll();
					}
				}
			});

			jTableBill.setDefaultEditor(Integer.class, cellEditor);

			jTableBill.getColumnModel().getColumn(1).setMinWidth(QuantityWidth);
			jTableBill.getColumnModel().getColumn(1).setMaxWidth(QuantityWidth);
			jTableBill.getColumnModel().getColumn(2).setMinWidth(PriceWidth);
			jTableBill.getColumnModel().getColumn(2).setMaxWidth(PriceWidth);
			jTableBill.getColumnModel().getColumn(3).setMinWidth(RefundedQtyWidth);
			jTableBill.getColumnModel().getColumn(3).setMaxWidth(RefundedQtyWidth);
			jTableBill.getColumnModel().getColumn(4).setMinWidth(RefundQtyWidth);
			jTableBill.getColumnModel().getColumn(4).setMaxWidth(RefundQtyWidth);
			jTableBill.setAutoCreateColumnsFromModel(false);
		}
		return jTableBill;
	}

	private JScrollPane getJScrollPaneAmountToRefund() {
		if (jScrollPaneAmountToRefund == null) {
			jScrollPaneAmountToRefund = new JScrollPane();
			jScrollPaneAmountToRefund.setViewportView(getJTableAmountToRefund());
			jScrollPaneAmountToRefund.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			jScrollPaneAmountToRefund.setMaximumSize(new Dimension(PanelWidth, AmountToRefundHeight));
			jScrollPaneAmountToRefund.setMinimumSize(new Dimension(PanelWidth, AmountToRefundHeight));
			jScrollPaneAmountToRefund.setPreferredSize(new Dimension(PanelWidth, AmountToRefundHeight));
		}
		return jScrollPaneAmountToRefund;
	}

	private JPanel getHelpTextPanel() {
		if (helpTextPanel == null) {
			helpTextPanel = new JPanel();
			helpTextPanel.setLayout(new FlowLayout());

			JLabel helpText = new JLabel(MessageBundle.getMessage("angal.billrefund.helptext"));
			Font font = helpText.getFont();
			helpText.setFont(font.deriveFont(font.getStyle() | Font.BOLD));

			helpTextPanel.add(helpText);

			helpTextPanel.setMaximumSize(new Dimension(PanelWidth, TotalHeight));
			helpTextPanel.setMinimumSize(new Dimension(PanelWidth, TotalHeight));
			helpTextPanel.setPreferredSize(new Dimension(PanelWidth, TotalHeight));
		}
		return helpTextPanel;
	}

	private JTable getJTableAmountToRefund() {
		if (jTableAmountToRefund == null) {
			jTableAmountToRefund = new JTable();
			jTableAmountToRefund.setModel(new DefaultTableModel(new Object[][] {
					{ "<html><b>" + MessageBundle.getMessage("angal.billrefund.amountorefound") + "</b></html>",
							amountToRefund } },
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
			jTableAmountToRefund.getColumnModel().getColumn(1).setMinWidth(RefundQtyWidth);
			jTableAmountToRefund.getColumnModel().getColumn(1).setMaxWidth(RefundQtyWidth);
			jTableAmountToRefund.setMaximumSize(new Dimension(PanelWidth, AmountToRefundHeight));
			jTableAmountToRefund.setMinimumSize(new Dimension(PanelWidth, AmountToRefundHeight));
			jTableAmountToRefund.setPreferredSize(new Dimension(PanelWidth, AmountToRefundHeight));
		}
		return jTableAmountToRefund;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			jPanelButtons.setLayout(new FlowLayout());
			jPanelButtons.add(getJPanelButtonsActions());
		}
		return jPanelButtons;
	}

	private JPanel getJPanelButtonsActions() {
		if (jPanelButtonsActions == null) {
			jPanelButtonsActions = new JPanel();
			jPanelButtonsActions.setLayout(new FlowLayout());
			jPanelButtonsActions.add(getJButtonSave());
			jPanelButtonsActions.add(getJButtonClose());
		}
		return jPanelButtonsActions;
	}

	private JButton getJButtonSave() {
		if (jButtonSave == null) {

			jButtonSave = new JButton();
			jButtonSave.setText(MessageBundle.getMessage("angal.common.save"));
			jButtonSave.setMnemonic(KeyEvent.VK_S);
			jButtonSave.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
			jButtonSave.setIcon(new ImageIcon("rsc/icons/save_button.png"));
			jButtonSave.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					GregorianCalendar upDate = TimeTools.getServerDateTime();

					if (billDate.after(today)) {

						JOptionPane.showMessageDialog(BillRefund.this,
								MessageBundle.getMessage("angal.newbill.billsinfuturenotallowed"),
								MessageBundle.getMessage("angal.newbill.title"), JOptionPane.ERROR_MESSAGE);
						return;
					}

					billID = thisBill.getId();

					Bill newBill = new Bill(0, billID, // Bill ID
							billDate, // from calendar
							upDate, // most recent payment
							thisBill.isList(), // is a List?
							thisBill.getListID(), // List
							thisBill.getListName(), // List name
							thisBill.isPatient(), // is a Patient?
							thisBill.getPatID(), // PatientID
							thisBill.getPatName(), // Patient
							"C", // Refund bill can only be closed
							amountToRefund.doubleValue(), // Total
							0.0, // Balance
							user, // User
							thisBill.getWardCode(), // wardCode
							thisBill.getReductionPlan(), thisBill.getAffiliatedParent());

					// add guarantee
					if (Param.bool("ALLOWGARANTEPERSON")) {
						String selected = (String) jComboGarante.getSelectedItem();
						if (selected != null && selected != "") {
							newBill.setGarante(selected);
						} else {
							newBill.setGarante(thisBill.getGarante());
						}
					}
					// adding guarantee

					ArrayList<BillItems> billItems = bundleBillItems(refundBillItems);

					if (billItems.size() == 0) {
						JOptionPane.showMessageDialog(BillRefund.this,
								MessageBundle.getMessage("angal.billrefund.pleaseselectitemtoberefunded"),
								MessageBundle.getMessage("angal.billrefund.title"), JOptionPane.ERROR_MESSAGE);
						return;
					}

					ArrayList<BillPayments> payItems = bundleBillPayment(billItems);

					billID = billManager.newBill(newBill, billItems, payItems);

					if (billID == 0) {
						JOptionPane.showMessageDialog(BillRefund.this,
								MessageBundle.getMessage("angal.billrefund.failedtosaverefund"),
								MessageBundle.getMessage("angal.billrefund.title"), JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					fireRefundBillInserted(newBill);

					dispose();
				}
			});
		}

		return jButtonSave;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(MessageBundle.getMessage("angal.billrefund.cancel"));
			jButtonClose.setMnemonic(KeyEvent.VK_C);
			jButtonClose.setMaximumSize(new Dimension(ButtonWidth, ButtonHeight));
			jButtonClose.setIcon(new ImageIcon("rsc/icons/close_button.png"));
			jButtonClose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Icon icon = new ImageIcon("rsc/icons/save_dialog.png");
					int ok = JOptionPane.showConfirmDialog(BillRefund.this,
							MessageBundle.getMessage("angal.billrefund.cancelrefund"),
							MessageBundle.getMessage("angal.billrefund.cancel"), JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE, icon);
					if (ok == JOptionPane.YES_OPTION) {
						dispose();
					} else if (ok == JOptionPane.NO_OPTION) {
						return;
					} else
						return;
				}
			});
		}
		return jButtonClose;
	}

	private void updateAmountToRefund() { // the big total (to pay) is made by all // items
		amountToRefund = new BigDecimal(0);
		amountToRefund.setScale(2, RoundingMode.CEILING);
		for (RefundBillItem item : refundBillItems) {
			BigDecimal itemAmount = new BigDecimal(item.getBillItem().getItemAmount());
			amountToRefund = amountToRefund.add(itemAmount.multiply(new BigDecimal(item.getRefundedQty())));
		}

		if (jTableAmountToRefund != null)
			jTableAmountToRefund.getModel().setValueAt(amountToRefund.doubleValue(), 0, 1);
	}

	/**
	 * Create bill items with quantity to be refunded. If the quantity to be
	 * refunded is 0, the items will be skipped.
	 * 
	 * @param refundItems
	 * @return ArrayList of <BillItems>
	 */
	private ArrayList<BillItems> bundleBillItems(ArrayList<RefundBillItem> refundItems) {
		ArrayList<BillItems> billItems = new ArrayList<BillItems>();

		for (RefundBillItem refundItem : refundItems) {
			if (refundItem.getRefundedQty() > 0) {
				BillItems item = refundItem.getBillItem();
				item.setItemQuantity(refundItem.getRefundedQty());

				billItems.add(item);
			}
		}

		return billItems;
	}

	/**
	 * Create bill payment if the amount of the items to be refunded is greater than
	 * 0. The payment amount is negative
	 * 
	 * @param billItems
	 * @return ArrayList of <BillPayments>
	 */
	private ArrayList<BillPayments> bundleBillPayment(ArrayList<BillItems> billItems) {
		ArrayList<BillPayments> payments = new ArrayList<BillPayments>();
		Double refundAmount = 0.0;

		for (BillItems item : billItems) {
			refundAmount += item.getItemQuantity() * item.getItemAmount();
		}

		if (refundAmount > 0.0) {
			GregorianCalendar datePay = TimeTools.getServerDateTime();
			BillPayments pay = new BillPayments(0, billID, datePay, (refundAmount * -1), user);
			payments.add(pay);
		}

		return payments;
	}

	/**
	 * 
	 */
	private void updateTotals() {
		updateAmountToRefund();
		jTableAmountToRefund.getModel().setValueAt(amountToRefund.doubleValue(), 0, 1);
	}

	public class BillTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		public BillTableModel() {
			updateAmountToRefund();
		}

		public Class<?> getColumnClass(int c) {
			if (c == 0) {
				return Price.class;
			} else if (c == 1) {
				return Integer.class;
			} else if (c == 2) {
				return Double.class;
			} else if (c == 3) {
				return Integer.class;
			} else if (c == 4) {
				return Integer.class;
			}

			return null;
		}

		public int getRowCount() {
			if (refundBillItems == null)
				return 0;
			return refundBillItems.size();
		}

		public String getColumnName(int c) {
			return billColumnNames[c];
		}

		public int getColumnCount() {
			return billColumnNames.length;
		}

		public Object getValueAt(int r, int c) {
			RefundBillItem item = refundBillItems.get(r);

			if (c == -1) {
				return item;
			}

			if (c == 0) {
				return item.getBillItem().getItemDescription();
			}

			if (c == 1) {
				return item.getBillItem().getItemQuantity();
			}

			if (c == 2) {
				BigDecimal qty = new BigDecimal(item.getBillItem().getItemQuantity());
				BigDecimal amount = new BigDecimal(item.getBillItem().getItemAmount());
				return amount.multiply(qty).doubleValue();
			}

			if (c == 3) {
				return item.getBillItem().getRefundedQty();
			}

			if (c == 4) {
				return item.getRefundedQty();
			}

			return null;
		}

		@Override
		public void setValueAt(Object item, int r, int c) {
			if (c == 4) {
				Double doubleValue = 0.0;
				try {
					doubleValue = Double.parseDouble(item.toString());
				} catch (NumberFormatException e) {
					doubleValue = 0.0;
				}

				RefundBillItem refundItem = refundBillItems.get(r);

				if (refundItem.getBillItem()
						.getItemQuantity() < (refundItem.getBillItem().getRefundedQty() + doubleValue)) {
					
					JOptionPane.showMessageDialog(BillRefund.this,
							MessageBundle.getMessage("angal.billrefund.refundqtygreatthanrefundableqty"),
							MessageBundle.getMessage("angal.billrefund.title"), JOptionPane.ERROR_MESSAGE);
					
					return;
				}

				refundBillItems.get(r).setRefundedQty(doubleValue.intValue());

				updateAmountToRefund();
			}
		}

		@Override
		public boolean isCellEditable(int r, int c) {
			return columnEditable[c];
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

	private JComboBox<Object> getWardBox() {
		org.isf.ward.manager.WardBrowserManager wbm = new org.isf.ward.manager.WardBrowserManager();
		Ward thisWard = new Ward();
		if (wardBox == null) {
			wardBox = new JComboBox<Object>();
			wardBox.setPreferredSize(new Dimension(130, 25));
			String wardCode = MainMenu.getUserWard();

			wardCode = thisBill.getWardCode();

			ArrayList<Ward> wardList = wbm.getWards();
			boolean trouve = false;
			for (Ward ward : wardList) {
				if (ward.getCode().equals(wardCode)) {
					wardBox.addItem(ward);
					trouve = true;
					MovWardBrowserManager manager = new MovWardBrowserManager();
					manager.getMedicalsWard(wardCode);
					break;
				}
			}
			if (!trouve) {
				wardBox.addItem("");
			}

			for (org.isf.ward.model.Ward elem : wardList) {
				wardBox.addItem(elem);
				if (insert && elem.getDescription().toUpperCase().equals("PHARMACIE"))
					thisWard = elem; // wardBox.setSelectedItem(elem);

				if (thisBill.getWardCode() != null && thisBill.getWardCode().equals(elem.getCode()))
					wardBox.setSelectedItem(elem);
			}

			wardBox.setEnabled(false);
		}

		wardBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Object item = e.getItem();
					Ward ward = (Ward) item;
					MovWardBrowserManager manager = new MovWardBrowserManager();
					manager.getMedicalsWard(ward.getCode());
				}
			}
		});

		wardBox.setSelectedItem(thisWard); // NE PAS DEPLACER CE BOUT DE CODE
		return wardBox;
	}

	private JLabel getWardLabel() {
		if (wardLabel == null) {
			wardLabel = new JLabel(MessageBundle.getMessage("angal.patientbill.editt"));
		}
		return wardLabel;
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

	private JComboBox<String> getJComboGarante() {
		if (jComboGarante == null) {
			jComboGarante = new JComboBox<String>();
			UserBrowsingManager manager = new UserBrowsingManager();
			users = manager.getUser();
			jComboGarante.addItem("");
			for (User u : users) {
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
