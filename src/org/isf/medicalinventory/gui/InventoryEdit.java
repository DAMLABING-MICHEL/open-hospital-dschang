package org.isf.medicalinventory.gui;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.manager.MedicalInventoryManager;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.gui.MedicalPicker;
import org.isf.medicalstock.gui.StockMedModel;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.stat.manager.GenericReportInventory;
import org.isf.utils.db.NormalizeString;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.InventoryState;
import org.isf.utils.jobjects.InventoryType;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.utils.time.TimeTools;

import com.toedter.calendar.JDateChooser;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.KeyAdapter;

public class InventoryEdit extends ModalJFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static EventListenerList InventoryListeners = new EventListenerList();

	//public interface InventoryListener extends EventListener {
	public interface InventoryListener extends java.util.EventListener {
		public void InventoryUpdated(AWTEvent e);

		public void InventoryInserted(AWTEvent e);

		public void InventoryValidated(AWTEvent e);

		public void InventoryCancelled(AWTEvent e);
	}

	public static void addInventoryListener(InventoryListener l) {
		InventoryListeners.add(InventoryListener.class, l);
	}

	public static void removeInventoryListener(InventoryListener listener) {
		InventoryListeners.remove(InventoryListener.class, listener);
	}

	private void fireInventoryInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = InventoryListeners.getListeners(InventoryListener.class);
		for (int i = 0; i < listeners.length; i++)
			((InventoryListener) listeners[i]).InventoryInserted(event);
	}

	private void fireInventoryUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = InventoryListeners.getListeners(InventoryListener.class);
		for (int i = 0; i < listeners.length; i++)
			((InventoryListener) listeners[i]).InventoryUpdated(event);
	}

	private void fireInventoryValidated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
		EventListener[] listeners = InventoryListeners.getListeners(InventoryListener.class);
		for (int i = 0; i < listeners.length; i++)
			((InventoryListener) listeners[i]).InventoryValidated(event);
	}

	private void fireInventoryCancelled() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = InventoryListeners.getListeners(InventoryListener.class);
		for (int i = 0; i < listeners.length; i++)
			((InventoryListener) listeners[i]).InventoryCancelled(event);
	}


	private JDateChooser jCalendarTo;
	private JDateChooser jCalendarInventory;
	private GregorianCalendar dateInventory = TimeTools.getServerDateTime();
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private JLabel jLabelTo;
	private JPanel panelHeader;
	private JPanel panelFooter;
	private JPanel panelContent;
	private JButton closeButton;
	private JButton saveButton;
	private JButton cancelButton;
	private JButton printButton;
	private JButton validateButton;
	private JScrollPane scrollPaneInventory;
	private JTable jTableInventoryRow;
	private ArrayList<MedicalInventoryRow> inventoryRowList;
	private ArrayList<MedicalInventoryRow> inventoryRowSearchList;
	private String[] pColums = { MessageBundle.getMessage("angal.common.code"),
			MessageBundle.getMessage("angal.inventoryrow.medical"),
			MessageBundle.getMessage("angal.inventoryrow.lotcode"),
			MessageBundle.getMessage("angal.inventoryrow.duedate"),
			MessageBundle.getMessage("angal.inventoryrow.theorticqty"),
			MessageBundle.getMessage("angal.inventoryrow.realqty") };
	private int[] pColumwidth = { 100, 300, 100, 100, 100, 100 };
	private boolean[] columnEditable = { false, false, false, false, false, true };
	private boolean[] columnEditableView = { false, false, false, false, false, false };
	private MedicalInventory inventory = null;
	private MedicalInventoryManager inventoryManager = new MedicalInventoryManager();
	private JRadioButton specificRadio;
	private JRadioButton allRadio;
	private JTextField searchTextField;
	private JLabel dateInventoryLabel;
	private JTextField codeTextField;
	private String code = null;
	private String mode = null;
	private JLabel referenceLabel;
	private JTextField referenceTextField;
	private JTextField jTetFieldEditor;
	private JLabel loaderLabel;

	private JButton moreData;
	private int MAX_COUNT = 30;
	private int CURRENT_INDEX = 0;
	private boolean MORE_DATA = true;
	
	public InventoryEdit() {
		initComponents();
		mode = "new";
		cancelButton.setVisible(false);
	}

	public InventoryEdit(MedicalInventory inventory, String modee) {
		this.inventory = inventory;
		mode = modee;
		initComponents();
		if (mode.equals("view")) {
			validateButton.setVisible(false);
			saveButton.setVisible(false);
			cancelButton.setVisible(false);
			columnEditable = columnEditableView;
		}
		
	}

	private void initComponents() {
		//setSize(new Dimension(850, 580));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(850, 580));
		setLocationRelativeTo(null); // center
		setTitle(MessageBundle.getMessage("angal.inventory.newedittitle"));

		getContentPane().setLayout(new BorderLayout());

		panelHeader = getPanelHeader();

		getContentPane().add(panelHeader, BorderLayout.NORTH);

		panelContent = getPanelContent();
		getContentPane().add(panelContent, BorderLayout.CENTER);

		panelFooter = getPanelFooter();
		getContentPane().add(panelFooter, BorderLayout.SOUTH);

		ajustWidth();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (inventoryRowList != null)
					inventoryRowList.clear();
				if (inventoryRowSearchList != null)
					inventoryRowSearchList.clear();
				dispose();
			}
		});
		// pack();
		//modal exclude
		if(!Param.bool("WITHMODALWINDOW")){
			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		}
	}

	private JPanel getPanelHeader() {
		if (panelHeader == null) {
			panelHeader = new JPanel();
			panelHeader.setBorder(new EmptyBorder(5, 0, 5, 0));
			GridBagLayout gbl_panelHeader = new GridBagLayout();
			gbl_panelHeader.columnWidths = new int[] { 159, 191, 192, 218, 51, 0 };
			gbl_panelHeader.rowHeights = new int[] { 30, 30, 0 };
			gbl_panelHeader.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			gbl_panelHeader.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			panelHeader.setLayout(gbl_panelHeader);
			GridBagConstraints gbc_dateInventoryLabel = new GridBagConstraints();
			gbc_dateInventoryLabel.insets = new Insets(0, 0, 5, 5);
			gbc_dateInventoryLabel.gridx = 0;
			gbc_dateInventoryLabel.gridy = 0;
			panelHeader.add(getDateInventoryLabel(), gbc_dateInventoryLabel);

			GridBagConstraints gbc_jCalendarInventory = new GridBagConstraints();
			gbc_jCalendarInventory.fill = GridBagConstraints.HORIZONTAL;
			gbc_jCalendarInventory.insets = new Insets(0, 0, 5, 5);
			gbc_jCalendarInventory.gridx = 1;
			gbc_jCalendarInventory.gridy = 0;
			panelHeader.add(getJCalendarFrom(), gbc_jCalendarInventory);
			GridBagConstraints gbc_referenceLabel = new GridBagConstraints();
			gbc_referenceLabel.anchor = GridBagConstraints.EAST;
			gbc_referenceLabel.insets = new Insets(0, 0, 5, 5);
			gbc_referenceLabel.gridx = 2;
			gbc_referenceLabel.gridy = 0;
			panelHeader.add(getReferenceLabel(), gbc_referenceLabel);
			GridBagConstraints gbc_referenceTextField = new GridBagConstraints();
			gbc_referenceTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_referenceTextField.insets = new Insets(0, 0, 5, 5);
			gbc_referenceTextField.gridx = 3;
			gbc_referenceTextField.gridy = 0;
			panelHeader.add(getReferenceTextField(), gbc_referenceTextField);
			GridBagConstraints gbc_loaderLabel = new GridBagConstraints();
			gbc_loaderLabel.insets = new Insets(0, 0, 5, 0);
			gbc_loaderLabel.gridx = 4;
			gbc_loaderLabel.gridy = 0;
			panelHeader.add(getLoaderLabel(), gbc_loaderLabel);
			GridBagConstraints gbc_specificRadio = new GridBagConstraints();
			gbc_specificRadio.anchor = GridBagConstraints.EAST;
			gbc_specificRadio.insets = new Insets(0, 0, 0, 5);
			gbc_specificRadio.gridx = 0;
			gbc_specificRadio.gridy = 1;
			panelHeader.add(getSpecificRadio(), gbc_specificRadio);
			GridBagConstraints gbc_codeTextField = new GridBagConstraints();
			gbc_codeTextField.insets = new Insets(0, 0, 0, 5);
			gbc_codeTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_codeTextField.gridx = 1;
			gbc_codeTextField.gridy = 1;
			panelHeader.add(getCodeTextField(), gbc_codeTextField);
			GridBagConstraints gbc_allRadio = new GridBagConstraints();
			gbc_allRadio.anchor = GridBagConstraints.EAST;
			gbc_allRadio.insets = new Insets(0, 0, 0, 5);
			gbc_allRadio.gridx = 2;
			gbc_allRadio.gridy = 1;
			panelHeader.add(getAllRadio(), gbc_allRadio);
			GridBagConstraints gbc_searchTextField = new GridBagConstraints();
			gbc_searchTextField.insets = new Insets(0, 0, 0, 5);
			gbc_searchTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_searchTextField.gridx = 3;
			gbc_searchTextField.gridy = 1;
				JPanel panSearchTextField = new JPanel();
				panSearchTextField.setLayout(new FlowLayout(FlowLayout.LEFT));
				panSearchTextField.add(getSearchTextField());
				panSearchTextField.add(getMoreDataBtn());
			panelHeader.add(panSearchTextField, gbc_searchTextField);
			// panelHeader.add(getJLabelTo());
			// panelHeader.add(getJCalendarTo());
			ButtonGroup group = new ButtonGroup();
			group.add(specificRadio);
			group.add(allRadio);

		}
		return panelHeader;
	}

	private JPanel getPanelContent() {
		if (panelContent == null) {
			panelContent = new JPanel();
			GridBagLayout gbl_panelContent = new GridBagLayout();
			gbl_panelContent.columnWidths = new int[] { 452, 0 };
			gbl_panelContent.rowHeights = new int[] { 402, 0 };
			gbl_panelContent.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_panelContent.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
			panelContent.setLayout(gbl_panelContent);
			GridBagConstraints gbc_scrollPaneInventory = new GridBagConstraints();
			gbc_scrollPaneInventory.fill = GridBagConstraints.BOTH;
			gbc_scrollPaneInventory.gridx = 0;
			gbc_scrollPaneInventory.gridy = 0;
			panelContent.add(getScrollPaneInventory(), gbc_scrollPaneInventory);
		}
		return panelContent;
	}

	private JPanel getPanelFooter() {
		if (panelFooter == null) {
			panelFooter = new JPanel();
			panelFooter.add(getSaveButton());
			panelFooter.add(getValidateButton());
			panelFooter.add(getCancelButton());
			panelFooter.add(getPrintButton());
			panelFooter.add(getCloseButton());
		}
		return panelFooter;
	}

	private JDateChooser getJCalendarFrom() {
		if (jCalendarInventory == null) {
			dateInventory.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateInventory.set(GregorianCalendar.MINUTE, 0);
			dateInventory.set(GregorianCalendar.SECOND, 0);

			jCalendarInventory = new JDateChooser(dateInventory.getTime());
			jCalendarInventory.setLocale(new Locale(Param.string("LANGUAGE")));
			jCalendarInventory.setDateFormatString("dd/MM/yy"); //$NON-NLS-1$
			/*** set date **/
			if (inventory != null) {
				jCalendarInventory.setDate(inventory.getInventoryDate().getTime());
				dateInventory.setTime(inventory.getInventoryDate().getTime());
				dateInventory.set(GregorianCalendar.HOUR_OF_DAY, 0);
				dateInventory.set(GregorianCalendar.MINUTE, 0);
				dateInventory.set(GregorianCalendar.SECOND, 0);
			}
			/***************/
			jCalendarInventory.addPropertyChangeListener("date", new PropertyChangeListener() { //$NON-NLS-1$
				public void propertyChange(PropertyChangeEvent evt) {
					jCalendarInventory.setDate((Date) evt.getNewValue());
					dateInventory.setTime((Date) evt.getNewValue());
					dateInventory.set(GregorianCalendar.HOUR_OF_DAY, 0);
					dateInventory.set(GregorianCalendar.MINUTE, 0);
					dateInventory.set(GregorianCalendar.SECOND, 0);
				}
			});
		}
		return jCalendarInventory;
	}

	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setText(MessageBundle.getMessage("angal.billbrowser.to")); //$NON-NLS-1$
		}
		return jLabelTo;
	}

	private JButton getSaveButton() {
		
		saveButton = new JButton(MessageBundle.getMessage("angal.common.save"));
		saveButton.setMnemonic(KeyEvent.VK_S);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				String user = MainMenu.getUser();
				
				String State = InventoryState.State.PROGRESS.getCode();
				int checkResults = 0;

				if (inventoryRowSearchList == null || inventoryRowSearchList.size() < 1) {
					JOptionPane.showMessageDialog(InventoryEdit.this,
							MessageBundle.getMessage("angal.inventory.noproduct"),
							MessageBundle.getMessage("angal.inventoryoperation.title"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				GregorianCalendar now = new GregorianCalendar();

				if (dateInventory.after(now)) {
					JOptionPane.showMessageDialog(InventoryEdit.this,
							MessageBundle.getMessage("angal.inventory.notdateinfuture"),
							MessageBundle.getMessage("angal.inventoryoperation.title"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				if ((inventory == null) && (mode.equals("new"))) { // new
																	// inventory
																	// //inserting
					String reference = referenceTextField.getText().trim();
					if (reference.equals("")) {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventory.mustenterareference"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (inventoryManager.refNoExists(reference)) {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventory.referencealreadyused"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					//loaderLabel.setVisible(true);
					inventory = new MedicalInventory();
					inventory.setInventoryReference(reference);
					inventory.setInventoryDate(dateInventory);
					inventory.setState(State);
					inventory.setUser(user);
					inventory.setInventoryType(InventoryType.PRINCIPAL);
					inventory.setWard("");

					int inventoryId = 0;
					try {
						inventoryId = inventoryManager.newMedicalInventoryGetId(inventory);
						if (inventoryId > 0) {
							inventory.setId(inventoryId);
							int currentInventoryRowCode = 0;
							for (Iterator iterator = inventoryRowSearchList.iterator(); iterator.hasNext();) {
								MedicalInventoryRow medicalInventoryRow = (MedicalInventoryRow) iterator.next();
								medicalInventoryRow.setInventory(inventory);
								currentInventoryRowCode = inventoryManager.newMedicalInventoryRowGetId(medicalInventoryRow);
								if (currentInventoryRowCode <= 0) {
									checkResults++;
								}
								else if(currentInventoryRowCode>0){
									medicalInventoryRow.setId(currentInventoryRowCode);
								}
							}
							
							if (checkResults == 0) {
								JOptionPane.showMessageDialog(InventoryEdit.this,
										MessageBundle.getMessage("angal.inventoryoperation.save.succes"),
										MessageBundle.getMessage("angal.inventoryoperation.title"),
										JOptionPane.INFORMATION_MESSAGE); // good
																			// insertion
								// enable validation
								mode = "update";
								validateButton.setEnabled(true);
								fireInventoryInserted();
							} else {
								JOptionPane.showMessageDialog(InventoryEdit.this,
										MessageBundle.getMessage("angal.inventoryrowoperation.save.error"),
										MessageBundle.getMessage("angal.inventoryoperation.title"),
										JOptionPane.INFORMATION_MESSAGE); // bad
																			// insertion
							}
						} else {
							JOptionPane.showMessageDialog(InventoryEdit.this,
									MessageBundle.getMessage("angal.inventoryoperation.save.error"),
									MessageBundle.getMessage("angal.inventoryoperation.title"),
									JOptionPane.INFORMATION_MESSAGE); // bad
																		// inserting
						}
					} catch (OHException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if ((inventory != null) && (mode.equals("update"))) { // updating
					checkResults = 0;
					boolean toUpdate = false;
					boolean result = true;
					if (!inventory.getInventoryDate().equals(dateInventory)) {
						inventory.setInventoryDate(dateInventory);
						toUpdate = true;
					}
					if (!inventory.getUser().equals(user)) {
						inventory.setUser(user);
						toUpdate = true;
					}
					if (toUpdate) {
						result = inventoryManager.updateMedicalInventory(inventory);
					}
					if (result) {
						try {
							for (Iterator iterator = inventoryRowSearchList.iterator(); iterator.hasNext();) {
								// for (Iterator iterator =
								// inventoryRowList.iterator();
								// iterator.hasNext();) {
								MedicalInventoryRow medicalInventoryRow = (MedicalInventoryRow) iterator.next();
								if (!inventoryManager.updateMedicalInventoryRow(medicalInventoryRow)) {
									checkResults++;
								}
							}
						} catch (OHException e1) {
							e1.printStackTrace();
						}
						if (checkResults == 0) {
							JOptionPane.showMessageDialog(InventoryEdit.this,
									MessageBundle.getMessage("angal.inventoryoperation.update.succes"),
									MessageBundle.getMessage("angal.inventoryoperation.title"),
									JOptionPane.INFORMATION_MESSAGE); // good
																		// modif
							validateButton.setEnabled(true);
							fireInventoryUpdated();
						} else {
							JOptionPane.showMessageDialog(InventoryEdit.this,
									MessageBundle.getMessage("angal.inventoryrowoperation.update.error"),
									MessageBundle.getMessage("angal.inventoryoperation.title"),
									JOptionPane.INFORMATION_MESSAGE); // bad
																		// modif
						}
					} else {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventoryoperation.update.error"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE); // bad modif
					}
				}
				//loaderLabel.setVisible(false);
			}
		});
		
		return saveButton;
	}

	private JButton getValidateButton() {

		validateButton = new JButton(MessageBundle.getMessage("angal.inventory.validate"));
		validateButton.setMnemonic(KeyEvent.VK_V);
		if (inventory == null) {
			validateButton.setEnabled(false);
		}
		validateButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (inventory == null) {
					JOptionPane.showMessageDialog(InventoryEdit.this,
							MessageBundle.getMessage("angal.inventorymustsavebeforevalidate"),
							MessageBundle.getMessage("angal.inventoryoperation.title"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (inventory != null) {
					if (inventory.getState().equals(InventoryState.State.VALIDATE.getCode())) {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventoryalreadyvalidate"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				MovStockInsertingManager movManager = new MovStockInsertingManager();
				MedicalInventoryManager invManager = new MedicalInventoryManager();
				Medical medical = null;
				Double ajustQty = 0.0;

				int checkError = 0;
				String errorList = "";
				Movement movement = null;
				MovementType dischargeType = new MedicaldsrstockmovTypeBrowserManager().getMovementType("discharge");
				GregorianCalendar today = new GregorianCalendar();
				String referenceNumber = "";
				Lot currentLot = null;
				ArrayList<Movement> movList = new ArrayList<Movement>();
				for (Iterator iterator = inventoryRowSearchList.iterator(); iterator.hasNext();) {
					MedicalInventoryRow medicalInventoryRow = (MedicalInventoryRow) iterator.next();
					if (medicalInventoryRow.getTheoreticqty() > medicalInventoryRow.getRealqty()) {
						ajustQty = medicalInventoryRow.getTheoreticqty() - medicalInventoryRow.getRealqty();
						medical = medicalInventoryRow.getMedical();
						currentLot = new Lot("", null, null);
						if (medicalInventoryRow.getLot() != null
								&& !medicalInventoryRow.getLot().getCode().equals("")) {
							currentLot = medicalInventoryRow.getLot();
						}
						referenceNumber = inventory.getInventoryReference();
						movement = new Movement(medical, dischargeType, null, currentLot, today, ajustQty.intValue(),
								null, referenceNumber);
						
						if (!movManager.prepareDishargingMovementInventory(movement)) {
							checkError++;
							errorList = errorList + medical.getDescription() + " Lot: " + currentLot.getCode() + "\n";
						}
					} else if (medicalInventoryRow.getTheoreticqty() < medicalInventoryRow.getRealqty()) {
						ajustQty = medicalInventoryRow.getRealqty() - medicalInventoryRow.getTheoreticqty();
						medical = medicalInventoryRow.getMedical();
						currentLot = new Lot("", null, null);
						if (medicalInventoryRow.getLot() != null
								&& !medicalInventoryRow.getLot().getCode().equals("")) {
							currentLot = medicalInventoryRow.getLot();
						}
						referenceNumber = inventory.getInventoryReference();
						movement = new Movement(medical, dischargeType, null, currentLot, today, -(ajustQty.intValue()),
								null, referenceNumber);
						
						if (!movManager.prepareDishargingMovementInventory(movement)) {
							checkError++;
							errorList = errorList + medical.getDescription() + " Lot: " + currentLot.getCode() + "\n";
						}
					}
					
				}
				// change state of inventory
				if (checkError == 0) {
					inventory.setState(InventoryState.State.VALIDATE.getCode());
					if (invManager.updateMedicalInventory(inventory)) {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventoryvalidate.succes"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
						fireInventoryValidated();
						columnEditable = columnEditableView;
						jTableInventoryRow.updateUI();
						saveButton.setEnabled(false);
					} else {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventoryvalidate.error"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(InventoryEdit.this,
							MessageBundle.getMessage("angal.inventoryvalidate.ajustinventoryerror") + "\n" + errorList,
							MessageBundle.getMessage("angal.inventoryoperation.title"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		return validateButton;
	}

	private JButton getCancelButton() {
		cancelButton = new JButton(MessageBundle.getMessage("angal.common.cancel"));
		cancelButton.setMnemonic(KeyEvent.VK_A);
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (inventory != null && inventory.getId() > 0) {
					inventory.setState(InventoryState.State.CANCELED.getCode());
					MedicalInventoryManager invtManager = new MedicalInventoryManager();
					if (invtManager.updateMedicalInventory(inventory)) {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventorycancel.succes"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
						columnEditable = columnEditableView;
						saveButton.setEnabled(false);
						validateButton.setEnabled(false);
						fireInventoryCancelled();
					} else {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventorycancel.error"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		return cancelButton;
	}

	private JButton getPrintButton() {
		printButton = new JButton(MessageBundle.getMessage("angal.inventory.print"));
		printButton.setMnemonic(KeyEvent.VK_P);
		printButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (inventory != null) {
					if (inventory.getId() > 0) {
						int printQtyReal = 0;
						if (inventory.getState().equals(InventoryState.State.PROGRESS.getCode())) {
							int response = JOptionPane.showConfirmDialog(InventoryEdit.this,
									MessageBundle.getMessage("angal.inventorywardedit.askforrealquantityempty"),
									MessageBundle.getMessage("angal.inventoryoperation.title"),
									JOptionPane.YES_NO_CANCEL_OPTION);
							if (response == JOptionPane.OK_OPTION) {
								printQtyReal = 1;
							}
						}
						new GenericReportInventory(inventory, Param.string("INVENTORYREPORT"), true, printQtyReal);
					} else {
						JOptionPane.showMessageDialog(InventoryEdit.this,
								MessageBundle.getMessage("angal.inventorypleasesavebeforprinting"),
								MessageBundle.getMessage("angal.inventoryoperation.title"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(InventoryEdit.this,
							MessageBundle.getMessage("angal.inventorypleasesavebeforprinting"),
							MessageBundle.getMessage("angal.inventoryoperation.title"),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		return printButton;
	}

	private JButton getCloseButton() {
		closeButton = new JButton(MessageBundle.getMessage("angal.inventory.close"));
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});
		return closeButton;
	}

	private JScrollPane getScrollPaneInventory() {
		if (scrollPaneInventory == null) {
			scrollPaneInventory = new JScrollPane();
			scrollPaneInventory.setViewportView(getJTableInventoryRow());
		}
		return scrollPaneInventory;
	}

	private JTable getJTableInventoryRow() {
		if (jTableInventoryRow == null) {
			jTableInventoryRow = new JTable();
			jTetFieldEditor = new JTextField();

			jTableInventoryRow.setFillsViewportHeight(true);
			jTableInventoryRow.setModel(new InventoryRowModel());
			
		
			jTableInventoryRow.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					// TODO Auto-generated method stub
					if(!e.getValueIsAdjusting()){
						jTableInventoryRow.editCellAt(jTableInventoryRow.getSelectedRow(), 5);
						jTetFieldEditor.selectAll();
					}
					
				}
			});
			jTableInventoryRow.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						if(inventory!=null){
							MedicalInventoryManager invtManager = new MedicalInventoryManager();
							if(inventory.getId()>0){
								if(InventoryState.State.VALIDATE.getCode().equals(inventory.getState()) || mode.equals("view")){
									e.consume();
									return;
								}
								if(InventoryState.State.PROGRESS.getCode().equals(inventory.getState()) && mode.equals("update")){
									int row = jTableInventoryRow.getSelectedRow();
									MedicalInventoryRow invtRow =  (MedicalInventoryRow) jTableInventoryRow.getModel().getValueAt(row, -1);
									try {
										invtManager.deleteMedicalInventoryRow(invtRow);
										jTableInventoryRow.editCellAt(row, 2);
										inventoryRowSearchList.remove(row);
										jTableInventoryRow.updateUI();
										return;
									} catch (OHException e1) {
										e1.printStackTrace();
									}
									return;
								}								
							}
						}
						int row = jTableInventoryRow.getSelectedRow();
						jTableInventoryRow.editCellAt(row, 2);
						inventoryRowSearchList.remove(row);
						jTableInventoryRow.updateUI();
					}
				}
			});
			DefaultCellEditor cellEditor=new DefaultCellEditor(jTetFieldEditor);
			jTableInventoryRow.setDefaultEditor(Integer.class, cellEditor);
//			jTableInventoryRow.setCellEditor(new DefaultCellEditor(jTetFieldEditor));

		}
		
		return jTableInventoryRow;
	}

	class InventoryRowModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		public InventoryRowModel() {
			//
			MedicalInventoryManager manager = new MedicalInventoryManager();
			if (inventory == null) {// inserting
				if (allRadio.isSelected()) {
					inventoryRowList = loadNewInventoryTable(null, CURRENT_INDEX, MAX_COUNT);
				} else if (specificRadio.isSelected() && code != null && !code.trim().equals("")) {
					inventoryRowList = loadNewInventoryTable(code, null, null);
				}
			} else if (inventory != null) {// updating
				if (allRadio.isSelected()) {
					inventoryRowList = manager.getMedicalInventoryRowByInventory(inventory.getId());
				} else if (specificRadio.isSelected() && code != null && !code.trim().equals("")) {
					inventoryRowList = manager.getMedicalInventoryRowByInventoryAndByMedicalCode(inventory.getId(),
							code);
				}
			}
			if(inventoryRowList != null) {
				inventoryRowSearchList = new ArrayList<MedicalInventoryRow>();
				inventoryRowSearchList.addAll(inventoryRowList);
			}
				
			//
		}

		public Class<?> getColumnClass(int c) {
			if (c == 0) {
				return String.class;
			} else if (c == 1) {
				return String.class;
			} else if (c == 2) {
				return String.class;
			} else if (c == 3) {
				return String.class;
			} else if (c == 4) {
				return Integer.class;
			} else if (c == 5) {
				return Integer.class;
			}
			return null;
		}

		public int getRowCount() {
			// if (inventoryRowList == null)
			// return 0;
			// return inventoryRowList.size();
			if (inventoryRowSearchList == null)
				return 0;
			return inventoryRowSearchList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			// MedicalInventoryRow medInvtRow = inventoryRowList.get(r);
			MedicalInventoryRow medInvtRow = inventoryRowSearchList.get(r);

			if (c == -1) {
				return medInvtRow;
			} else if (c == 0) {
				return medInvtRow.getMedical() == null ? "" : medInvtRow.getMedical().getProd_code();
			} else if (c == 1) {
				return medInvtRow.getMedical() == null ? "" : medInvtRow.getMedical().getDescription();// date
			} else if (c == 2) {
				return medInvtRow.getLot() == null ? "" : medInvtRow.getLot().getCode();
			} else if (c == 3) {
				return medInvtRow.getLot() == null ? "" : formatDateTime(medInvtRow.getLot().getDueDate());
			} else if (c == 4) {
				Double dblVal=medInvtRow.getTheoreticqty();
				return dblVal.intValue();
			} else if (c == 5) {
				Double dblValue=medInvtRow.getRealqty();
				return dblValue.intValue();
			} /*
				 * else if (c == 5) { return
				 * medInvtRow.getInventory()==null?"":medInvtRow.getInventory().
				 * getId(); }
				 */
			return null;
		}

		@Override
		public void setValueAt(Object value, int r, int c) {
			if(r < inventoryRowSearchList.size()){
				MedicalInventoryRow invRow = inventoryRowSearchList.get(r);
				if (c == 5) {
					Integer intValue=0;
					try{
						intValue=Integer.parseInt(value.toString());
					}
					catch (NumberFormatException e) {
						intValue=0;
					}
					
					invRow.setRealqty(intValue);
					inventoryRowSearchList.set(r, invRow);
					validateButton.setEnabled(false);
				}
			}
			// inventoryRowList.set(r, invRow);
			// inventoryRowSearchList.set(r, invRow);
			// fireTableDataChanged();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnEditable[columnIndex];
		}

	}

	public String formatDateTime(GregorianCalendar time) {
		if (time == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy"); //$NON-NLS-1$
		return format.format(time.getTime());
	}

	private ArrayList<MedicalInventoryRow> loadNewInventoryTable(String code, Integer start, Integer limit){	
		ArrayList<MedicalInventoryRow> inventoryRowsList = new ArrayList<MedicalInventoryRow>();
		ArrayList<Medical> medicalList = new ArrayList<Medical>();
		MedicalBrowsingManager medicalManager = new MedicalBrowsingManager();
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = null;
		Medical medical = null;
		MedicalInventoryRow inventoryRowTemp = null;
		if (code != null) {
			medical = medicalManager.getMedical(code);
			if (medical != null) {
				medicalList.add(medical);
			} else {
				JOptionPane.showMessageDialog(InventoryEdit.this,
						MessageBundle.getMessage("angal.inventory.noproductfound"),
						MessageBundle.getMessage("angal.inventoryoperation.title"), JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			if(start == null || limit == null) {
				medicalList = medicalManager.getMedicals();
			} else {
				medicalList = medicalManager.getMedicals(start, limit);
			}
		}

		double quantityOutsideLot = 0.0;
		double cost = 0.0;
		for (Iterator iterator = medicalList.iterator(); iterator.hasNext();) {
			medical = (Medical) iterator.next();
			lots = movBrowser.getLotByMedical(medical);
			quantityOutsideLot = medical.getTotalQuantity() - getQtyInALot(lots);
			if ((lots.size() == 0) || (quantityOutsideLot != 0)) {
				inventoryRowTemp = new MedicalInventoryRow(0, quantityOutsideLot, quantityOutsideLot, null, medical,
						new Lot("", null, null), 0.0);
				inventoryRowsList.add(inventoryRowTemp);
			}
			for (Iterator iterator2 = lots.iterator(); iterator2.hasNext();) {
				Lot lot = (Lot) iterator2.next();
				cost = lot.getCost() * lot.getQuantity();
				inventoryRowTemp = new MedicalInventoryRow(0, lot.getQuantity(), lot.getQuantity(), null, medical, lot,
						cost);
				inventoryRowsList.add(inventoryRowTemp);
			}
		}		
		return inventoryRowsList;
	}

	private double getQtyInALot(ArrayList<Lot> lots) {
		double qty = 0.0;
		for (Iterator iterator = lots.iterator(); iterator.hasNext();) {
			Lot lot = (Lot) iterator.next();
			qty += lot.getQuantity();
		}
		return qty;
	}

	class DecimalFormatRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final DecimalFormat formatter = new DecimalFormat("#,##0.00"); //$NON-NLS-1$

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// First format the cell value as required
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.addFocusListener(new java.awt.event.FocusListener()
		      {
		       
				@Override
				public void focusGained(java.awt.event.FocusEvent e) {
					// TODO Auto-generated method stub					
				}

				@Override
				public void focusLost(java.awt.event.FocusEvent e) {
					// TODO Auto-generated method stub					
				}
		    });
			
			value = formatter.format((Number) value);
			// setHorizontalAlignment(columnAlignment[column]);
			if (!columnEditable[column]) {
				cell.setBackground(Color.LIGHT_GRAY);
			}

			// if (columnBold[column]) {
			// cell.setFont(new Font(null, Font.BOLD, 12));
			// }
			// And pass it on to parent class
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

       
	
	public MedicalInventory getInventory() {
		return inventory;
	}

	public void setInventory(MedicalInventory inventory) {
		this.inventory = inventory;
	}

	private JRadioButton getSpecificRadio() {
		if (specificRadio == null) {
			specificRadio = new JRadioButton(MessageBundle.getMessage("angal.inventory.specificproduct"));
			if (inventory != null) {
				specificRadio.setSelected(false);
			} else {
				specificRadio.setSelected(true);
			}
			specificRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (specificRadio.isSelected()) {
						codeTextField.setEnabled(true);
						searchTextField.setEnabled(false);
						moreData.setEnabled(false);
						searchTextField.setText("");
						codeTextField.setText("");
						if (inventoryRowList != null) {
							inventoryRowList.clear();
						}
						if (inventoryRowSearchList != null) {
							inventoryRowSearchList.clear();
						}
						jTableInventoryRow.updateUI();
						ajustWidth();
					}
				}
			});
		}
		return specificRadio;
	}

	private JRadioButton getAllRadio() {
		if (allRadio == null) {
			allRadio = new JRadioButton(MessageBundle.getMessage("angal.inventory.allproduct"));
			if (inventory != null) {
				allRadio.setSelected(true);
			} else {
				allRadio.setSelected(false);
			}
			allRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {					
					if (allRadio.isSelected()) {
						codeTextField.setEnabled(false);
						searchTextField.setText("");
						codeTextField.setText("");
						searchTextField.setEnabled(true);
						if(inventory == null) {
							moreData.setEnabled(true);
						}
						if (inventoryRowList != null) {
							inventoryRowList.clear();
						}
						if (inventoryRowSearchList != null) {
							inventoryRowSearchList.clear();
						}
						jTableInventoryRow.setModel(new InventoryRowModel());
						jTableInventoryRow.updateUI();
						code = null;
						ajustWidth();
					}					
				}
			});
		}
		return allRadio;
	}

	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
			searchTextField.setColumns(16);
			TextPrompt suggestion = new TextPrompt(
					MessageBundle
							.getMessage("angal.inventory.searchproduct"), //$NON-NLS-1$
							searchTextField, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			searchTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					filterInventoryRow();
					ajustWidth();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					filterInventoryRow();
					ajustWidth();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					filterInventoryRow();
					ajustWidth();
				}
			});
			searchTextField.setEnabled(false);
			if (inventory != null) {
				searchTextField.setEnabled(true);
			} else {
				searchTextField.setEnabled(false);
			}
		}
		return searchTextField;
	}

	private JLabel getDateInventoryLabel() {
		if (dateInventoryLabel == null) {
			dateInventoryLabel = new JLabel(MessageBundle.getMessage("angal.inventory.date"));
		}
		return dateInventoryLabel;
	}

	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new JTextField();
			if (inventory != null) {
				codeTextField.setEnabled(false);
			} else {
				codeTextField.setEnabled(true);
			}
			codeTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						code = codeTextField.getText().trim();
						code = code.toLowerCase();
						addInventoryRow(code);
						codeTextField.setText("");
//						if (inventoryRowList != null) {
//							inventoryRowList.clear();
//						}
//						if (inventoryRowSearchList != null) {
//							inventoryRowSearchList.clear();
//						}
//						jTableInventoryRow.setModel(new InventoryRowModel());
//						jTableInventoryRow.updateUI();
						ajustWidth();
					}
				}
			});
			codeTextField.setColumns(10);
			TextPrompt suggestion = new TextPrompt(
					MessageBundle
							.getMessage("angal.inventory.productcode"), //$NON-NLS-1$
							codeTextField, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
		}
		return codeTextField;
	}

	private void filterInventoryRow() {
		String s = searchTextField.getText();
		s.trim();
		inventoryRowSearchList = new ArrayList<MedicalInventoryRow>();
		for (MedicalInventoryRow invRow : inventoryRowList) {
			if (!s.equals("")) {
				String name = invRow.getSearchString();
				if (name.contains(s.toLowerCase()))
					inventoryRowSearchList.add(invRow);
			} else {
				inventoryRowSearchList.add(invRow);
			}
		}
		jTableInventoryRow.updateUI();
		searchTextField.requestFocus();
	}

	private void ajustWidth() {
		for (int i = 0; i < pColumwidth.length; i++) {
			jTableInventoryRow.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
			//jTableInventoryRow.getColumnModel().getColumn(i).setMaxWidth(pColumwidth[i]);
		}
	}

	public EventListenerList getInventoryListeners() {
		return InventoryListeners;
	}

	public void setInventoryListeners(EventListenerList inventoryListeners) {
		InventoryListeners = inventoryListeners;
	}

	private JButton getMoreDataBtn() {
		if(moreData == null) {
			moreData = new JButton("...");
			moreData.setPreferredSize(new Dimension(20, 20));
			moreData.setEnabled(false);
		}
		moreData.addActionListener(e -> {
			if(MORE_DATA && inventory == null) {
				ArrayList<Medical> medicalList = new ArrayList<Medical>();
				MedicalBrowsingManager medicalManager = new MedicalBrowsingManager();
				CURRENT_INDEX+=MAX_COUNT;
				medicalList = medicalManager.getMedicals(CURRENT_INDEX, MAX_COUNT);
				if(medicalList.size() < MAX_COUNT) {
					MORE_DATA = false;
					moreData.setEnabled(false);
				}
				
				MovStockInsertingManager movBrowser = new MovStockInsertingManager();
				ArrayList<Lot> lots;
				MedicalInventoryRow inventoryRowTemp;
				double quantityOutsideLot;
				double cost;
				Iterator<Medical> medicalIterator = medicalList.iterator();
				Medical medical;
				while (medicalIterator.hasNext()) {
					medical = medicalIterator.next();
					lots = movBrowser.getLotByMedical(medical);
					quantityOutsideLot = medical.getTotalQuantity() - getQtyInALot(lots);
					if ((lots.size() == 0) || (quantityOutsideLot != 0)) {
						inventoryRowTemp = new MedicalInventoryRow(0, quantityOutsideLot, quantityOutsideLot, null, medical,
								new Lot("", null, null), 0.0);
						inventoryRowList.add(inventoryRowTemp);
					}
					for (Lot lot: lots) {
						cost = lot.getCost() * lot.getQuantity();
						inventoryRowTemp = new MedicalInventoryRow(0, lot.getQuantity(), lot.getQuantity(), null, medical, lot,
								cost);
						inventoryRowList.add(inventoryRowTemp);
					}
				}
				inventoryRowSearchList = new ArrayList<MedicalInventoryRow>();
				inventoryRowSearchList.addAll(inventoryRowList);
				if(medicalList.size() > 0) {
					jTableInventoryRow.updateUI();
				}
			}
		});
		return moreData;
	}
	
	private JLabel getReferenceLabel() {
		if (referenceLabel == null) {
			referenceLabel = new JLabel(MessageBundle.getMessage("angal.inventory.reference"));
		}
		return referenceLabel;
	}

	private JTextField getReferenceTextField() {
		if (referenceTextField == null) {
			referenceTextField = new JTextField();
			referenceTextField.setColumns(10);
			if (inventory != null && !mode.equals("new")) {
				referenceTextField.setText(inventory.getInventoryReference());
				referenceTextField.setEnabled(false);
			}
		}
		return referenceTextField;
	}
	
	private JLabel getLoaderLabel() {
		if (loaderLabel == null) {
			Icon icon = new ImageIcon("rsc/icons/oh_loader.GIF");
			loaderLabel = new JLabel("");
			loaderLabel.setIcon(icon);
			loaderLabel.setVisible(false);
		}
		return loaderLabel;
	}
	
	
	private void addInventoryRow(String code){	
		
		ArrayList<MedicalInventoryRow> inventoryRowsList = new ArrayList<MedicalInventoryRow>();
		ArrayList<Medical> medicalList = new ArrayList<Medical>();
		MedicalBrowsingManager medicalManager = new MedicalBrowsingManager();
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = null;
		Medical medical = null;
		MedicalInventoryRow inventoryRowTemp = null;
		if (code != null) {
			medical = medicalManager.getMedical(code);
			if (medical != null) {
				boolean found = false;
				if(inventoryRowSearchList!=null){
					for (MedicalInventoryRow row : inventoryRowSearchList) {
						if(row.getMedical().getCode().equals(medical.getCode())){
							found = true;
						}
					}
				}
				if(!found) 
					medicalList.add(medical);
			} else {
					medical = chooseMedical(code);
					if (medical != null) {
						boolean found = false;
						if(inventoryRowSearchList!=null){
							for (MedicalInventoryRow row : inventoryRowSearchList) {
								if(row.getMedical().getCode().equals(medical.getCode())){
									found = true;
								}
							}
						}
						if(!found){ 
							medicalList.add(medical);
						}
					}	
				}
		} else {
			medicalList = medicalManager.getMedicals();
		}

		double quantityOutsideLot = 0.0;
		double cost = 0.0;
		if(mode.equals("new")){
			for (Iterator iterator = medicalList.iterator(); iterator.hasNext();) {
				medical = (Medical) iterator.next();
				lots = movBrowser.getLotByMedical(medical);
				quantityOutsideLot = medical.getTotalQuantity() - getQtyInALot(lots);
				if ((lots.size() == 0) || (quantityOutsideLot != 0)) {
					inventoryRowTemp = new MedicalInventoryRow(0, quantityOutsideLot, quantityOutsideLot, null, medical,
							new Lot("", null, null), 0.0);
					inventoryRowsList.add(inventoryRowTemp);
				}
				for (Iterator iterator2 = lots.iterator(); iterator2.hasNext();) {
					Lot lot = (Lot) iterator2.next();
					cost = lot.getCost() * lot.getQuantity();
					inventoryRowTemp = new MedicalInventoryRow(0, lot.getQuantity(), lot.getQuantity(), null, medical, lot,
							cost);
					inventoryRowsList.add(inventoryRowTemp);
				}
			}
		}
		else if(mode.equals("update")){
			MedicalInventoryManager manager = new MedicalInventoryManager();
			if(medical!=null)
				inventoryRowsList =  manager.getMedicalInventoryRowByInventoryAndByMedicalCode(inventory.getId(), medical.getProd_code());
		}
		if(inventoryRowSearchList==null){
			inventoryRowSearchList = new ArrayList<MedicalInventoryRow>();
		}
		
		for (MedicalInventoryRow inventoryRow : inventoryRowsList) {
			boolean found = false;
			for (MedicalInventoryRow row : inventoryRowSearchList) {
				if(row.getMedical().getCode().equals(inventoryRow.getMedical().getCode()) &&
						(row.getLot().getCode().equals(inventoryRow.getLot().getCode()))){
					found = true;
				}
			}
			if(!found) inventoryRowSearchList.add(inventoryRow);
		}
		jTableInventoryRow.updateUI();
	}
	
	protected Medical chooseMedical(String text) {
		//////////////////////////
		HashMap<String, Medical> medicalMap;
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicals();
			////////////////////
			if(mode.equals("update")){
				medicals.clear();
				MedicalInventoryManager  manager = new MedicalInventoryManager();
				ArrayList<MedicalInventoryRow> inventoryRowListTemp = manager.getMedicalInventoryRowByInventory(inventory.getId());
				for (MedicalInventoryRow medicalInventoryRow : inventoryRowListTemp) {
					medicals.add(medicalInventoryRow.getMedical());
				}
			}
			////////////////////
		medicalMap = new HashMap<String, Medical>();
		for (Medical med : medicals) {
		// String key = med.getProd_code();
		String key = med.getProd_code().toLowerCase();
		if (key.equals("")) //$NON-NLS-1$
		key = med.getCode().toString().toLowerCase();
		medicalMap.put(key, med);
		}
		///////////////////////
		
		ArrayList<Medical> medList = new ArrayList<Medical>();
		for (Medical aMed : medicalMap.values()) {
			if (NormalizeString.normalizeContains(aMed.getDescription().toLowerCase(), text))
				medList.add(aMed);
		}
		Collections.sort(medList);
		Medical med = null;
		if (!medList.isEmpty()) {
			MedicalPicker framas = new MedicalPicker(new StockMedModel(medList));
			framas.setSize(300, 400);
			JDialog dialog = new JDialog();
			dialog.setLocationRelativeTo(null);
			dialog.setSize(600, 350);
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);
			dialog.setTitle(MessageBundle.getMessage("angal.medicalstock.multiplecharging.selectmedical"));
			Image ico = new javax.swing.ImageIcon("rsc/icons/medical_dialog.png").getImage();
			framas.setParentFrame(dialog);
			dialog.setContentPane(framas);
			dialog.setIconImage(ico);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			med = framas.getSelectedMedical();
			return med;
		}
		return null;
	}
}
