package org.isf.medicalstock.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ListIterator;

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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.PlainDocument;

import org.apache.log4j.PropertyConfigurator;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryEdit;
import org.isf.medicalinventory.gui.InventoryEdit.InventoryListener;
import org.isf.medicals.gui.MedicalEdit;
import org.isf.medicals.gui.MedicalEdit.MedicalEditListener;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.parameters.manager.Param;
import org.isf.utils.db.NormalizeString;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.DoubleDocumentFilter;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.RequestFocusListener;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.utils.time.TimeTools;
import org.isf.ward.gui.WardEdit;
import org.isf.ward.gui.WardEdit.WardListener;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.isf.xmpp.manager.Interaction;

import com.toedter.calendar.JDateChooser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MovStockMultipleDischarging extends ModalJFrame implements WardListener,InventoryListener, MedicalEditListener {
	
	private EventListenerList StockDisChargingListeners = new EventListenerList();
	
	public interface StockDisChargingListener extends EventListener {
		public void DisChargingInserted(AWTEvent e);	
	}
	
	public void addStockDisChargingListener(StockDisChargingListener l) {
	StockDisChargingListeners.add(StockDisChargingListener.class, l);
	}
	
	public void removeStockDisChargingListener(StockDisChargingListener listener) {
	StockDisChargingListeners.remove(StockDisChargingListener.class, listener);
	}
	
	private void fireDisChargingInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
		
		EventListener[] listeners = StockDisChargingListeners.getListeners(StockDisChargingListener.class);			
		for (int i = 0; i < listeners.length; i++)
			((StockDisChargingListener) listeners[i]).DisChargingInserted(event);
	}
	////////////////
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DATE_FORMAT_DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss"; //$NON-NLS-1$
	private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy"; //$NON-NLS-1$
	private static final int CODE_COLUMN_WIDTH = 100;

	private JPanel mainPanel;
	private JPanel panelEntry;//
	private JTextField jTextFieldDescription;//
	private JTextField jTextFieldQty;//
	private JTextField jTextFieldLotId;//
	private Lot lot;//
	private boolean chooseLot = true;//
	private Movement movement = null;//
	private boolean isNew = true;//
	private static final String DATE_FORMAT_DD_MM_YY_HH_MM = "dd/MM/yy HH:mm:ss";
	private JTextField jTextFieldReference;
	private JTextField jTextFieldSearch;
	private JComboBox jComboBoxDischargeType;
	private JDateChooser jDateChooser;
	private JComboBox jComboBoxDestination;
	private JTable jTableMovements;
	private final String[] columnNames = { MessageBundle.getMessage("angal.medicalstock.multipledischarging.code"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.description"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.unitpack"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.qty"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.unitpack"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.total"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.lotnumberabb"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multipledischarging.expiringdate") }; //$NON-NLS-1$
	private final Class[] columnClasses = { String.class, String.class, Integer.class, Integer.class, String.class,
			Integer.class, String.class, String.class };
	private boolean[] columnEditable = { false, false, false, false, true, false, false, false };
	private int[] columnWidth = { 50, 100, 70, 50, 70, 50, 100, 80 };
	private boolean[] columnResizable = { false, true, false, false, false, false, false, false };

	private boolean[] columnVisible = { true, true, true, true, true, true, !Param.bool("AUTOMATICLOTDISCHARGE"),
			!Param.bool("AUTOMATICLOTDISCHARGE") };
	private int[] columnAlignment = { SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.CENTER,
			SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
			SwingConstants.CENTER };
	private boolean[] columnBold = { false, false, false, false, false, true, false, false };
	private HashMap<String, Medical> medicalMap;
	private ArrayList<Integer> units;
	private JTableModel model;
	private String[] qtyOption = new String[] { "", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	private final int UNITS = 0;
	private final int PACKETS = 1;
	private int optionSelected = UNITS;
	private JComboBox comboBoxUnits = new JComboBox(qtyOption);
	private JComboBox shareWith = null;
	private Interaction share;
	private ArrayList<Medical> pool = new ArrayList<Medical>();

	/**
	 * Launch the application. TODO: externalize strings
	 */
	public static void main(String[] args) {
		try {
			PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath()); //$NON-NLS-1$
			GeneralData.getGeneralData();
			new MovStockMultipleDischarging(new JFrame());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAutomaticLot() {
		return Param.bool("AUTOMATICLOTDISCHARGE");
	}

	private boolean isXmpp() {
		return Param.bool("XMPPMODULEENABLED");
	}

	/**
	 * Create the dialog.
	 */
	public MovStockMultipleDischarging(JFrame owner) {
		//super(owner, true);
		super();
		initialize();
		initcomponents();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		
		if(Param.bool("WITHMODALWINDOW")){
			this.showAsModal(owner);
		}else{
			this.show(owner);
		}
	}

	private void initialize() {
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicals();
		medicalMap = new HashMap<String, Medical>();
		for (Medical med : medicals) {
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) //$NON-NLS-1$
				key = med.getCode().toString().toLowerCase();
			medicalMap.put(key, med);
		}
		units = new ArrayList<Integer>();
		/***************/
		WindowListener exitListener = new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        int confirm = JOptionPane.showOptionDialog(null, MessageBundle.getMessage("angal.common.areyousuretoexit"), 
		        		MessageBundle.getMessage("angal.common.exitconfirmation"), JOptionPane.YES_NO_OPTION, 
		             JOptionPane.QUESTION_MESSAGE, null, null, null);
		        if (confirm == JOptionPane.YES_OPTION) {
		        	WardEdit.removeWardListener(MovStockMultipleDischarging.this);
		        	InventoryEdit.removeInventoryListener(MovStockMultipleDischarging.this);
		        	MedicalEdit.removeMedicalListener(MovStockMultipleDischarging.this);
		           dispose();
		        }else		        
			       return;			    
		    }
		    @Override
		    public void windowOpened(WindowEvent e) {
		    	/*************** adding to supplier listener*/
				WardEdit.addWardListener(MovStockMultipleDischarging.this);
				InventoryEdit.addInventoryListener(MovStockMultipleDischarging.this);
				MedicalEdit.addMedicalListener(MovStockMultipleDischarging.this);
		    }
		};
		this.addWindowListener(exitListener);
		/***************/	
	}

	private void initcomponents() {
		setTitle(MessageBundle.getMessage("angal.medicalstock.stockmovementinserting")); //$NON-NLS-1$
		getContentPane().add(getJPanelHeader(), BorderLayout.NORTH);
		getContentPane().add(getJMainPanel(), BorderLayout.CENTER);
		getContentPane().add(getJButtonPane(), BorderLayout.SOUTH);
		setPreferredSize(new Dimension(800, 600));
		pack();
		setLocationRelativeTo(null);
	}

	private JPanel getJButtonPane() {

		JPanel buttonPane = new JPanel();
		{
			JButton deleteButton = new JButton(MessageBundle.getMessage("angal.common.delete")); //$NON-NLS-1$
			deleteButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(jTableMovements.getSelectedRow()  >= 0){
						 int confirm = JOptionPane.showOptionDialog(null, MessageBundle.getMessage("angal.common.areyousuretodeleteselection"), 
					        	MessageBundle.getMessage("angal.common.exitconfirmation"), JOptionPane.YES_NO_OPTION, 
					             JOptionPane.QUESTION_MESSAGE, null, null, null);
						 if(confirm == JOptionPane.OK_OPTION) {
							 ((JTableModel) jTableMovements.getModel()).removeItem(jTableMovements.getSelectedRow());
							 jTableMovements.updateUI();
						 }
						newMovement();
					}
				}
			});
			buttonPane.add(deleteButton);
			
			JButton saveButton = new JButton(MessageBundle.getMessage("angal.common.save")); //$NON-NLS-1$
			saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					BusyState.setBusyState(MovStockMultipleDischarging.this, true);
					if (!checkAndPrepareMovements()) {
						BusyState.setBusyState(MovStockMultipleDischarging.this, false);
						return;
					}
					if (!save()) {
						BusyState.setBusyState(MovStockMultipleDischarging.this, false);
						return;
					}
					fireDisChargingInserted();
					//saveButton.setEnabled(false);
					dispose();
				}
			});
			buttonPane.add(saveButton);	
		}
		{
			JButton cancelButton = new JButton(MessageBundle.getMessage("angal.common.cancel")); //$NON-NLS-1$
			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int confirm = JOptionPane.showOptionDialog(null, MessageBundle.getMessage("angal.common.areyousuretoexit"), 
							MessageBundle.getMessage("angal.common.exitconfirmation"), JOptionPane.YES_NO_OPTION, 
				             JOptionPane.QUESTION_MESSAGE, null, null, null);
				    if (confirm == JOptionPane.YES_OPTION) {
				    	dispose();
				    }else		        
					    return;
					//dispose();
				}
			});
			buttonPane.add(cancelButton);
		}
		return buttonPane;
	}

	private JPanel getJMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			mainPanel.add(getPanelEntry(), BorderLayout.NORTH);
			mainPanel.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return mainPanel;
	}
	private String formatDateTime(GregorianCalendar time) {
		if (time == null)
			return MessageBundle.getMessage("angal.medicalstock.nodate");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY_HH_MM);
		return sdf.format(time.getTime());
	}
	private JTextField getJTextFieldSearch() {
		if (jTextFieldSearch == null) {
			jTextFieldSearch = new JTextField();
			jTextFieldSearch.setPreferredSize(new Dimension(300, 30));
			jTextFieldSearch.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldSearch.setColumns(12);
			jTextFieldSearch.setFocusTraversalKeysEnabled(false);

			TextPrompt suggestion = new TextPrompt(
					MessageBundle
							.getMessage("angal.medicalstock.multipledischarging.typeacodeoradescriptionandpressenter"), //$NON-NLS-1$
					jTextFieldSearch, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			jTextFieldSearch.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						// Move to next field
						String text = jTextFieldSearch.getText();
						Medical med = null;
						if (medicalMap.containsKey(text.toLowerCase())) {
							// Medical found
							med = medicalMap.get(text.toLowerCase());
						} else {
							med = chooseMedical(text.toLowerCase());
						}
						if (med != null) {

							if (isAutomaticLot() && isMedicalPresent(med))
								return;

							if (!isAvailable(med))
								return;

							jTextFieldSearch.setText(med.getProd_code().toUpperCase());
							jTextFieldDescription.setText(med.getDescription());
							jTextFieldQty.requestFocus();
							jTextFieldQty.selectAll();

							jTextFieldSearch.setEnabled(false);

							// Date
							GregorianCalendar date = new GregorianCalendar();
							date.setTime(jDateChooser.getDate());

							// RefNo
							String refNo = jTextFieldReference.getText();

							movement = new Movement(med, (MovementType) jComboBoxDischargeType.getSelectedItem(), null,
									null, date, 0, null, refNo);

						}
						e.consume();
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						String actual = jTextFieldSearch.getText();
						jTextFieldSearch.setText(actual.replaceAll(" ", "_"));
					}
				}
			});
		}
		return jTextFieldSearch;
	}

	protected boolean isAvailable(Medical med) {
		if (med.getTotalQuantity() == 0) {
			StringBuilder message = new StringBuilder();
			message.append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.outofstock")); //$NON-NLS-1$
			message.append("\n").append(med.getDescription()); //$NON-NLS-1$
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this, message.toString());
			return false;
		}
		return true;
	}

	private boolean isMedicalPresent(Medical med) {
		ArrayList<Movement> movements = model.getMovements();
		for (Movement mov : movements) {
			if (mov.getMedical() == med) {
				StringBuilder message = new StringBuilder();
				message.append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.alreadyinthisform")); //$NON-NLS-1$
				message.append("\n").append(med.getDescription()); //$NON-NLS-1$
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this, message.toString());
				return true;
			}
		}
		return false;
	}

	private JScrollPane getJScrollPane() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(getJTable());
		scrollPane.setPreferredSize(new Dimension(400, 450));
		return scrollPane;
	}

	private JTable getJTable() {
		if (jTableMovements == null) {
			model = new JTableModel();
			jTableMovements = new JTable(model);
			
			jTableMovements.addComponentListener(new ComponentAdapter() {
		        public void componentResized(ComponentEvent e) {
		        	jTableMovements.scrollRectToVisible(jTableMovements.getCellRect(jTableMovements.getRowCount()-1, 0, true));
		        }
		    });
			
			jTableMovements.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						if (jTableMovements.getRowCount() > 0) {
							if (jTableMovements.getSelectedRow() >= 0) {
								movement = ((JTableModel) jTableMovements.getModel())
										.getMovement(jTableMovements.getSelectedRow());
								if (movement != null) {
									lot = movement.getLot();
									loadMovement();
								}

							}
						}
					}
				}
			});
			jTableMovements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTableMovements.setRowHeight(24);
			jTableMovements.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						int row = jTableMovements.getSelectedRow();
						model.removeItem(row);
					}
				}
			});

			for (int i = 0; i < columnNames.length; i++) {
				jTableMovements.getColumnModel().getColumn(i).setCellRenderer(new EnabledTableCellRenderer());
				jTableMovements.getColumnModel().getColumn(i).setMinWidth(columnWidth[i]);
				if (!columnResizable[i]) {
					jTableMovements.getColumnModel().getColumn(i).setResizable(columnResizable[i]);
					jTableMovements.getColumnModel().getColumn(i).setMaxWidth(columnWidth[i]);
				}
				if (!columnVisible[i]) {
					jTableMovements.getColumnModel().getColumn(i).setMinWidth(0);
					jTableMovements.getColumnModel().getColumn(i).setMaxWidth(0);
					jTableMovements.getColumnModel().getColumn(i).setWidth(0);
				}
			}

			TableColumn qtyOptionColumn = jTableMovements.getColumnModel().getColumn(4);
			qtyOptionColumn.setCellEditor(new DefaultCellEditor(comboBoxUnits));
			comboBoxUnits.setSelectedIndex(optionSelected);
		}
		return jTableMovements;
	}

	private JPanel getJPanelHeader() {
		JPanel headerPanel = new JPanel();
		GridBagLayout gbl_headerPanel = new GridBagLayout();
		gbl_headerPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_headerPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_headerPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_headerPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		headerPanel.setLayout(gbl_headerPanel);
		{
			JLabel jLabelDate = new JLabel(MessageBundle.getMessage("angal.common.date")); //$NON-NLS-1$
			GridBagConstraints gbc_jLabelDate = new GridBagConstraints();
			gbc_jLabelDate.anchor = GridBagConstraints.WEST;
			gbc_jLabelDate.insets = new Insets(5, 5, 5, 5);
			gbc_jLabelDate.gridx = 0;
			gbc_jLabelDate.gridy = 0;
			headerPanel.add(jLabelDate, gbc_jLabelDate);
		}
		{
			GridBagConstraints gbc_dateChooser = new GridBagConstraints();
			gbc_dateChooser.anchor = GridBagConstraints.WEST;
			gbc_dateChooser.insets = new Insets(5, 0, 5, 5);
			gbc_dateChooser.fill = GridBagConstraints.VERTICAL;
			gbc_dateChooser.gridx = 1;
			gbc_dateChooser.gridy = 0;
			headerPanel.add(getJDateChooser(), gbc_dateChooser);
		}
		{
			JLabel jLabelReferenceNo = new JLabel(
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.referencenumber")); //$NON-NLS-1$
			GridBagConstraints gbc_jLabelReferenceNo = new GridBagConstraints();
			gbc_jLabelReferenceNo.anchor = GridBagConstraints.EAST;
			gbc_jLabelReferenceNo.insets = new Insets(5, 0, 5, 5);
			gbc_jLabelReferenceNo.gridx = 2;
			gbc_jLabelReferenceNo.gridy = 0;
			headerPanel.add(jLabelReferenceNo, gbc_jLabelReferenceNo);
		}
		{
			jTextFieldReference = new JTextField();
			GridBagConstraints gbc_jTextFieldReference = new GridBagConstraints();
			gbc_jTextFieldReference.insets = new Insets(5, 0, 5, 0);
			gbc_jTextFieldReference.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldReference.gridx = 3;
			gbc_jTextFieldReference.gridy = 0;
			headerPanel.add(jTextFieldReference, gbc_jTextFieldReference);
			jTextFieldReference.setColumns(10);
		}
		{
			JLabel jLabelChargeType = new JLabel(
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.dischargetype")); //$NON-NLS-1$
			GridBagConstraints gbc_jLabelChargeType = new GridBagConstraints();
			gbc_jLabelChargeType.anchor = GridBagConstraints.EAST;
			gbc_jLabelChargeType.insets = new Insets(0, 5, 5, 5);
			gbc_jLabelChargeType.gridx = 0;
			gbc_jLabelChargeType.gridy = 1;
			headerPanel.add(jLabelChargeType, gbc_jLabelChargeType);
		}
		{
			GridBagConstraints gbc_jComboBoxChargeType = new GridBagConstraints();
			gbc_jComboBoxChargeType.anchor = GridBagConstraints.WEST;
			gbc_jComboBoxChargeType.insets = new Insets(0, 0, 5, 5);
			gbc_jComboBoxChargeType.gridx = 1;
			gbc_jComboBoxChargeType.gridy = 1;
			headerPanel.add(getJComboBoxChargeType(), gbc_jComboBoxChargeType);
		}
		{
			JLabel jLabelSupplier = new JLabel(
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.destination")); //$NON-NLS-1$
			GridBagConstraints gbc_jLabelSupplier = new GridBagConstraints();
			gbc_jLabelSupplier.anchor = GridBagConstraints.WEST;
			gbc_jLabelSupplier.insets = new Insets(0, 5, 0, 5);
			gbc_jLabelSupplier.gridx = 0;
			gbc_jLabelSupplier.gridy = 3;
			headerPanel.add(jLabelSupplier, gbc_jLabelSupplier);
		}
		{
			GridBagConstraints gbc_jComboBoxSupplier = new GridBagConstraints();
			gbc_jComboBoxSupplier.anchor = GridBagConstraints.WEST;
			gbc_jComboBoxSupplier.insets = new Insets(0, 0, 0, 5);
			gbc_jComboBoxSupplier.gridx = 1;
			gbc_jComboBoxSupplier.gridy = 3;
			headerPanel.add(getJComboBoxDestination(), gbc_jComboBoxSupplier);
		}
		return headerPanel;
	}

	private JComboBox getShareUser() {

		share = new Interaction();
		Collection<String> contacts = share.getContactOnline();
		contacts.add(MessageBundle.getMessage("angal.medicalstock.multipledischarging.sharealertwithnobody")); //$NON-NLS-1$
		shareWith = new JComboBox(contacts.toArray());
		shareWith.setSelectedItem(
				MessageBundle.getMessage("angal.medicalstock.multipledischarging.sharealertwithnobody")); //$NON-NLS-1$

		return shareWith;
	}

	private JDateChooser getJDateChooser() {
		if (jDateChooser == null) {
			jDateChooser = new JDateChooser(new Date());
			jDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY_HH_MM_SS);
			jDateChooser.setPreferredSize(new Dimension(150, 24));
		}
		return jDateChooser;
	}

	private JComboBox getJComboBoxChargeType() {
		if (jComboBoxDischargeType == null) {
			jComboBoxDischargeType = new JComboBox();
			MedicaldsrstockmovTypeBrowserManager movMan = new MedicaldsrstockmovTypeBrowserManager();
			ArrayList<MovementType> movTypes = movMan.getMedicaldsrstockmovType();
			for (MovementType movType : movTypes) {
				if (movType.getType().equals("-")) //$NON-NLS-1$
					jComboBoxDischargeType.addItem(movType);
			}
		}
		return jComboBoxDischargeType;
	}

	protected double askCost() {
		String input = JOptionPane.showInputDialog(MovStockMultipleDischarging.this,
				MessageBundle.getMessage("angal.medicalstock.multipledischarging.unitcost"), 0.); //$NON-NLS-1$
		double cost = 0.;
		if (input != null) {
			try {
				cost = Double.parseDouble(input);
				if (cost < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
						MessageBundle.getMessage("angal.medicalstock.multipledischarging.pleaseinsertavalidvalue")); //$NON-NLS-1$
			}
		}
		return cost;
	}

	protected Lot askLot() {
		GregorianCalendar preparationDate = TimeTools.getServerDateTime();
		GregorianCalendar expiringDate = TimeTools.getServerDateTime();
		Lot lot = null;

		JTextField lotNameTextField = new JTextField(15);
		lotNameTextField.addAncestorListener(new RequestFocusListener());
		if (isAutomaticLot())
			lotNameTextField.setEnabled(false);
		TextPrompt suggestion = new TextPrompt(MessageBundle.getMessage("angal.medicalstock.multipledischarging.lotid"), //$NON-NLS-1$
				lotNameTextField);
		{
			suggestion.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
			suggestion.setForeground(Color.GRAY);
			suggestion.setHorizontalAlignment(JLabel.CENTER);
			suggestion.changeAlpha(0.5f);
			suggestion.changeStyle(Font.BOLD + Font.ITALIC);
		}
		JDateChooser preparationDateChooser = new JDateChooser(new Date());
		{
			preparationDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JDateChooser expireDateChooser = new JDateChooser(new Date());
		{
			expireDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multipledischarging.preparationdate"))); //$NON-NLS-1$
		panel.add(preparationDateChooser);
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multipledischarging.expiringdate"))); //$NON-NLS-1$
		panel.add(expireDateChooser);
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multipledischarging.lotnumberabb"))); //$NON-NLS-1$
		panel.add(lotNameTextField);

		int ok = JOptionPane.showConfirmDialog(MovStockMultipleDischarging.this, panel,
				MessageBundle.getMessage("angal.medicalstock.multipledischarging.lotinformations"), //$NON-NLS-1$
				JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION) {
			String lotName = lotNameTextField.getText();
			expiringDate.setTime(expireDateChooser.getDate());
			preparationDate.setTime(preparationDateChooser.getDate());
			lot = new Lot(lotName, preparationDate, expiringDate);
		}
		return lot;
	}

	protected Medical chooseMedical(String text) {
		ArrayList<Medical> medList = new ArrayList<Medical>();
		for (Medical aMed : medicalMap.values()) {
			if (NormalizeString.normalizeContains(aMed.getDescription().toLowerCase(), text))
				medList.add(aMed);
		}
		Collections.sort(medList);
		Medical med = null;

		if (!medList.isEmpty()) {
			MedicalPicker framas = new MedicalPicker(new StockMedModel(medList), medicalMap.values(), text);
			
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

	protected Lot chooseLot(ArrayList<Lot> lots) {
		
		Lot lot = null;
		if (!lots.isEmpty()) {
			stripeLots(lots);

			JTable lotTable = new JTable(new StockMovModel(lots));
			lotTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multipledischarging.selectalot")), //$NON-NLS-1$
					BorderLayout.NORTH);
			panel.add(new JScrollPane(lotTable), BorderLayout.CENTER);

			int ok = JOptionPane.showConfirmDialog(MovStockMultipleDischarging.this, panel,
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.lotinformations"), //$NON-NLS-1$
					JOptionPane.OK_CANCEL_OPTION);

			if (ok == JOptionPane.OK_OPTION) {
				int row = lotTable.getSelectedRow();
				lot = lots.get(row);
			}
			return lot;
		}
		return lot;
	}

	private void stripeLots(ArrayList<Lot> lots) {
		if (!lots.isEmpty()) {
			ArrayList<Movement> movements = model.getMovements();
			ListIterator<Lot> lotIterator = lots.listIterator();
			while (lotIterator.hasNext()) {
				Lot aLot = (Lot) lotIterator.next();
				for (Movement mov : movements) {
					if (aLot.getCode().equals(mov.getLot().getCode())) {
						double aLotQty = aLot.getQuantity();
						double newQty = aLotQty - mov.getQuantity();
						if (newQty == 0)
							lotIterator.remove();
						else {
							aLot.setQuantity(newQty);
							lotIterator.set(aLot);
						}
					}
				}
			}
		}
	}

	protected GregorianCalendar askExpiringDate() {
		GregorianCalendar date = TimeTools.getServerDateTime();
		JDateChooser expireDateChooser = new JDateChooser(new Date());
		{
			expireDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multipledischarging.expiringdate"))); //$NON-NLS-1$
		panel.add(expireDateChooser);

		int ok = JOptionPane.showConfirmDialog(MovStockMultipleDischarging.this, panel,
				MessageBundle.getMessage("angal.medicalstock.multipledischarging.expiringdate"), //$NON-NLS-1$
				JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION) {
			date.setTime(expireDateChooser.getDate());
		}
		return date;
	}

	private boolean checkQtyByLot(Medical med, double qty) {
		double totalQty = lot.getQuantity();
       
		double usedQty = getUsedQtyByLot(med,lot);
		
		totalQty = totalQty - usedQty;
		if (qty > totalQty) {
			StringBuilder message = new StringBuilder();
			message.append(MessageBundle.getMessage("angal.medicalstock.movementquantityisgreaterthanthequantityof")); //$NON-NLS-1$
			message.append("\n").append(MessageBundle.getMessage("angal.medicalstock.qtyavailableinlot")) //$NON-NLS-1$ //$NON-NLS-2$
					.append(totalQty);
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this, message.toString());
			return false;
		}		
		return true;
	}

	private double getUsedQtyByLot(Medical medical, Lot lotTested) {
		double currentQty = 0.00;
		ArrayList<Movement> movements = model.getMovements();
		for (Movement mov : movements) {
			if (mov != movement && mov.getMedical() == medical) {
				if(mov.getLot()!=null && mov.getLot().getCode().equals(lotTested.getCode())){
					currentQty += mov.getQuantity();
				}
			}
		}
		return currentQty;
	}
	
	private double getTotalQty(Medical med) {
		double totalQty = med.getTotalQuantity();
		// update remaining quantity with already inserted movements
		ArrayList<Movement> movements = model.getMovements();
		double usedQty = 0;
		for (Movement mov : movements) {
			if (mov != movement && mov.getMedical() == med) {
				usedQty += mov.getQuantity();
			}
		}
		totalQty = totalQty - usedQty;
		return totalQty;
	}

	private double getRemainingQty(Medical med) {
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
		double totalLot = 0.00;
		double totalQty = med.getTotalQuantity();
		for (int j = 0; j < lots.size(); j++) {
			totalLot += lots.get(j).getQuantity();
		}
		return totalQty - totalLot;
	}

	private double getUsedQty(Medical medical) {
		double currentQty = 0.00;
		ArrayList<Movement> movements = model.getMovements();
		for (Movement mov : movements) {
			if (mov != movement && mov.getMedical() == medical) {
				currentQty += mov.getQuantity();
			}
		}
		return currentQty;
	}
	
	

	private boolean checkQuantity(Medical med, double qty) {
		double totalQty = med.getTotalQuantity();
		double criticalLevel = med.getMinqty();

		// update remaining quantity with already inserted movements
		// ArrayList<Movement> movements = model.getMovements();
		double usedQty = getUsedQty(med);
		totalQty = totalQty - usedQty;

		
		double remaining = getRemainingQty(med) - usedQty;
		if (remaining >= qty) {
			if (totalQty - qty < criticalLevel) {
				StringBuilder message = new StringBuilder();
				message.append(MessageBundle
						.getMessage("angal.medicalstock.multipledischarging.youaregoingundercriticalevel")); //$NON-NLS-1$
				message.append(" (").append(criticalLevel).append(") "); //$NON-NLS-1$ //$NON-NLS-2$
				message.append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.procedere")); //$NON-NLS-1$
				int ok = JOptionPane.showConfirmDialog(MovStockMultipleDischarging.this, message.toString());

				if (ok != JOptionPane.OK_OPTION) {
					
					return false;
				} else {
					return true;
				}
			}
			return true;
		} else if (remaining > 0 && remaining < qty) {
			if(!isAutomaticLot()){
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
						MessageBundle.getMessage("angal.medicalstock.multipledischarging.finishremainingquantity") + " "
								+ remaining);
				return false;
			}
		}

		if (qty > totalQty) {
			StringBuilder message = new StringBuilder();
			message.append(
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.thequantityisnotavailable")); //$NON-NLS-1$
			message.append("\n").append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.lyinginstock")) //$NON-NLS-1$ //$NON-NLS-2$
					.append(totalQty);
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this, message.toString());
			return false;
		}

		if (totalQty - qty < criticalLevel) {
			StringBuilder message = new StringBuilder();
			message.append(
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.youaregoingundercriticalevel")); //$NON-NLS-1$
			message.append(" (").append(criticalLevel).append(") "); //$NON-NLS-1$ //$NON-NLS-2$
			message.append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.procedere")); //$NON-NLS-1$
			int ok = JOptionPane.showConfirmDialog(MovStockMultipleDischarging.this, message.toString());

			if (ok != JOptionPane.OK_OPTION) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	protected double askQuantity(Medical med) {
		double totalQty = med.getTotalQuantity();

		// update remaining quantity with already inserted movements
		ArrayList<Movement> movements = model.getMovements();
		double usedQty = 0;
		for (Movement mov : movements) {
			if (mov.getMedical() == med) {
				usedQty += mov.getQuantity();
			}
		}
		totalQty = totalQty - usedQty;

		StringBuilder message = new StringBuilder();
		message.append(med.toString());
		message.append("\n").append(MessageBundle.getMessage("angal.medicalstock.multipledischarging.lyinginstock")) //$NON-NLS-1$ //$NON-NLS-2$
				.append(totalQty);

		String quantity = JOptionPane.showInputDialog(MovStockMultipleDischarging.this, message.toString(), 0);
		double qty = 0;
		if (quantity != null) {
			try {
				qty = Double.parseDouble(quantity);
				if (qty == 0)
					return 0;
				if (qty < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
						MessageBundle.getMessage("angal.medicalstock.multipledischarging.pleaseinsertavalidvalue")); //$NON-NLS-1$
			}
		}

		return qty;
	}

	private JComboBox getJComboBoxDestination() {
		if (jComboBoxDestination == null) {
			jComboBoxDestination = new JComboBox();
			jComboBoxDestination.addItem(""); //$NON-NLS-1$
			WardBrowserManager wardMan = new WardBrowserManager();
			ArrayList<Ward> wards = wardMan.getWards();
			for (Ward ward : wards) {
				if (Param.bool("INTERNALPHARMACIES")) {
					if (ward.isPharmacy())
						jComboBoxDestination.addItem(ward);
				} else {
					jComboBoxDestination.addItem(ward);
				}
			}
		}
		return jComboBoxDestination;
	}

	public class JTableModel extends AbstractTableModel {

		private ArrayList<Movement> movements;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JTableModel() {
			movements = new ArrayList<Movement>();
		}

		public ArrayList<Movement> getMovements() {
			return movements;
		}

		public Movement getMovement(int row) {
			if (movements.size() > row && row >= 0) {
				return movements.get(row);
			}
			return null;
		}

		public void removeItem(int row) {
			pool.remove(movements.get(row).getMedical());
			movements.remove(row);
			units.remove(row);
			fireTableDataChanged();
		}

		public void addItem(Movement movement) {
			movements.add(movement);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return movements.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnClasses[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnEditable[columnIndex];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int r, int c) {
			Movement movement = movements.get(r);
			Medical medical = movement.getMedical();
			Lot lot = movement.getLot();
			String lotName = lot.getCode();
			double qty = movement.getQuantity();
			int ppp = medical.getPcsperpck().intValue();
			int option = units.get(r);
			double total = option == UNITS ? qty : (ppp == 0 ? qty : ppp * qty);
			if (c == -1) {
				return movement;
			} else if (c == 0) {
				return medical.getProd_code();
			} else if (c == 1) {
				return medical.getDescription();
			} else if (c == 2) {
				return ppp;
			} else if (c == 3) {
				return qty;
			} else if (c == 4) {
				return qtyOption[option];
			} else if (c == 5) {
				return total;
			} else if (c == 6) {
				return lotName.equals("") ? "AUTO" : lotName; //$NON-NLS-1$ //$NON-NLS-2$
			} else if (c == 7) {
				if (lot.getDueDate() != null)
					return format(lot.getDueDate());
				else
					return "AUTO"; //$NON-NLS-1$
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int,
		 * int)
		 */
		@Override
		public void setValueAt(Object value, int r, int c) {
			Movement movement = movements.get(r);
			if (c == 0) {
				String key = String.valueOf(value);
				if (medicalMap.containsKey(key)) {
					movement.setMedical(medicalMap.get(key));
					movements.set(r, movement);
				}
			} else if (c == 3) {
				int qty = (Integer) value;
				
				if (checkQuantity(movement.getMedical(), qty));
				movement.setQuantity(qty);
			} else if (c == 4) {
				units.set(r, comboBoxUnits.getSelectedIndex());
			} else if (c == 7) {
				Lot lot = movement.getLot();
				try {
					lot.setDueDate(convertToDate((String) value));
				} catch (ParseException e) {
				}
			}
			movements.set(r, movement);
			fireTableDataChanged();
		}
	}

	private boolean checkAndPrepareMovements() {
		boolean ok = true;
		MovStockInsertingManager manager = new MovStockInsertingManager();

		// Check the Date
		GregorianCalendar thisDate = new GregorianCalendar();
		thisDate.setTime(jDateChooser.getDate());
		GregorianCalendar lastDate = manager.getLastMovementDate();
		if (lastDate != null && thisDate.compareTo(lastDate) < 0) {
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.datebeforelastmovement") //$NON-NLS-1$
							+ format(lastDate)
							+ MessageBundle.getMessage("angal.medicalstock.multipledischarging.notallowed")); //$NON-NLS-1$
			return false;
		}

		// Check the RefNo
		String refNo = jTextFieldReference.getText();
		if (refNo.equals("")) { //$NON-NLS-1$
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.pleaseinsertareferencenumber")); //$NON-NLS-1$
			return false;
		} /*else if (manager.refNoExists(refNo)) {
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this, MessageBundle
					.getMessage("angal.medicalstock.multipledischarging.theinsertedreferencenumberalreadyexists")); //$NON-NLS-1$
			return false;
		}*/

		// Check destination
		Object ward = jComboBoxDestination.getSelectedItem();
		if (ward instanceof String) {
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.pleaseselectaward")); //$NON-NLS-1$
			return false;
		}

		// Check and set all movements
		ArrayList<Movement> movements = model.getMovements();
		for (int i = 0; i < movements.size(); i++) {
			Movement mov = movements.get(i);
			mov.setWard((Ward) jComboBoxDestination.getSelectedItem());
			mov.setDate(thisDate);
			mov.setRefNo(refNo + " " + formatDateTime(thisDate));
			//mov.setRefNo(refNo);
			mov.setType((MovementType) jComboBoxDischargeType.getSelectedItem());
			mov.getLot().setPreparationDate(thisDate);
		}
		return ok;
	}

	private boolean save() {
		boolean ok = true;
		ArrayList<Movement> movements = model.getMovements();
		if (movements.isEmpty()) {
			JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
					MessageBundle.getMessage("angal.medicalstock.multipledischarging.noelementtosave")); //$NON-NLS-1$
			return false;
		}

		int movSize = movements.size();
		MovStockInsertingManager movManager = new MovStockInsertingManager();
		int index = movManager.newMultipleDischargingMovements(movements);
		if (index < movSize) {
			jTableMovements.getSelectionModel().setSelectionInterval(index, index);
			ok = false;
		} else {
			if (isXmpp()) {
//				if (shareWith.isEnabled() && (!(((String) shareWith.getSelectedItem()) == MessageBundle
//						.getMessage("angal.medicalstock.multipledischarging.sharealertwithnobody")))) { //$NON-NLS-1$
//					CommunicationFrame frame = (CommunicationFrame) CommunicationFrame.getFrame();
//					for (Medical med : pool) {
//						frame.sendMessage(
//								MessageBundle.getMessage("angal.medicalstock.multipledischarging.alert") //$NON-NLS-1$
//										+ med.getDescription()
//										+ MessageBundle
//												.getMessage("angal.medicalstock.multipledischarging.isabouttoend"), //$NON-NLS-1$
//								(String) shareWith.getSelectedItem(), false);
//					}
//				}
			}
		}
		return ok;
	}

	public String format(GregorianCalendar gc) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
		return sdf.format(gc.getTime());
	}

	public GregorianCalendar convertToDate(String string) throws ParseException {
		GregorianCalendar date = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
		date.setTime(sdf.parse(string));
		return date;
	}

	class EnabledTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(columnAlignment[column]);
			if (!columnEditable[column]) {
				cell.setBackground(Color.LIGHT_GRAY);
			}
			if (columnBold[column]) {
				cell.setFont(new Font(null, Font.BOLD, 12));
			}
			return cell;
		}
	}

	class StockMovModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Lot> lotList;

		public StockMovModel(ArrayList<Lot> lots) {
			lotList = lots;
		}

		public int getRowCount() {
			if (lotList == null)
				return 0;
			return lotList.size();
		}

		public String getColumnName(int c) {
			if (c == 0) {
				return MessageBundle.getMessage("angal.medicalstock.lotid"); //$NON-NLS-1$
			}
			if (c == 1) {
				return MessageBundle.getMessage("angal.medicalstock.prepdate"); //$NON-NLS-1$
			}
			if (c == 2) {
				return MessageBundle.getMessage("angal.medicalstock.duedate"); //$NON-NLS-1$
			}
			if (c == 3) {
				return MessageBundle.getMessage("angal.medicalstock.quantity"); //$NON-NLS-1$
			}
			return ""; //$NON-NLS-1$
		}

		public int getColumnCount() {
			return 4;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return lotList.get(r);
			} else if (c == 0) {
				return lotList.get(r).getCode();
			} else if (c == 1) {
				return format(lotList.get(r).getPreparationDate());
			} else if (c == 2) {
				return format(lotList.get(r).getDueDate());
			} else if (c == 3) {
				return lotList.get(r).getQuantity();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	private JPanel getPanelEntry() {
		panelEntry = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		if (isAutomaticLot()) {
			gbl_panel.columnWidths = new int[] { 150, 150, 100, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		} else {
			gbl_panel.columnWidths = new int[] { 150, 150, 100, 120, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		}

		gbl_panel.rowHeights = new int[] { 0, 30, 0 };

		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelEntry.setLayout(gbl_panel);
		{
			JLabel lblNewLabel = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.codefind"));
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			panelEntry.add(lblNewLabel, gbc_lblNewLabel);
		}
		GridBagConstraints gbc_jTextFieldSearch = new GridBagConstraints();
		gbc_jTextFieldSearch.insets = new Insets(0, 0, 0, 5);
		gbc_jTextFieldSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldSearch.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldSearch.gridx = 0;
		gbc_jTextFieldSearch.gridy = 1;
		panelEntry.add(getJTextFieldSearch(), gbc_jTextFieldSearch);

		{
			JLabel lblNewLabel_1 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.description"));
			lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 1;
			gbc_lblNewLabel_1.gridy = 0;
			panelEntry.add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		GridBagConstraints gbc_jTextFieldDescription = new GridBagConstraints();
		gbc_jTextFieldDescription.insets = new Insets(0, 0, 0, 5);
		gbc_jTextFieldDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldDescription.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldDescription.gridx = 1;
		gbc_jTextFieldDescription.gridy = 1;
		panelEntry.add(getJTextFieldDescription(), gbc_jTextFieldDescription);

		{
			JLabel lblNewLabel_2 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.quantity"));
			lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_2.gridx = 2;
			gbc_lblNewLabel_2.gridy = 0;
			panelEntry.add(lblNewLabel_2, gbc_lblNewLabel_2);
		}
		GridBagConstraints gbc_jTextFieldQty = new GridBagConstraints();
		gbc_jTextFieldQty.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldQty.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldQty.gridx = 2;
		gbc_jTextFieldQty.gridy = 1;
		panelEntry.add(getJTextFieldQty(), gbc_jTextFieldQty);

		if (!isAutomaticLot()) {
			{
				JLabel lblNewLabel_3 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotid"));
				lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
				gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel_3.gridx = 3;
				gbc_lblNewLabel_3.gridy = 0;
				panelEntry.add(lblNewLabel_3, gbc_lblNewLabel_3);
			}
			GridBagConstraints gbc_jTextFieldLotId = new GridBagConstraints();
			gbc_jTextFieldLotId.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldLotId.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldLotId.gridx = 3;
			gbc_jTextFieldLotId.gridy = 1;
			panelEntry.add(getJTextFieldLotId(), gbc_jTextFieldLotId);

		}

		return panelEntry;
	}

	private JTextField getJTextFieldDescription() {
		if (jTextFieldDescription == null) {
			jTextFieldDescription = new JTextField();
			jTextFieldDescription.setEnabled(false);
			jTextFieldDescription.setPreferredSize(new Dimension(150, 30));
			jTextFieldDescription.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldDescription.setColumns(10);
		}
		return jTextFieldDescription;
	}

	private boolean hasInitialStock(Medical med) {
		double initialStock = getRemainingQty(med);
		double usedQty = getUsedQty(med);
		return initialStock > usedQty;
	}

	private void addMedicalFromInitialStock(Medical med, int qty) {
		lot = new Lot("", null, null);
		boolean res = updateTable();
		if (res)
			newMovement();
	}
	
	private JTextField getJTextFieldQty() {
		if (jTextFieldQty == null) {
			jTextFieldQty = new JTextField();
			jTextFieldQty.setPreferredSize(new Dimension(100, 30));
			jTextFieldQty.setHorizontalAlignment(SwingConstants.RIGHT);
			jTextFieldQty.setColumns(10);
			jTextFieldQty.setFocusTraversalKeysEnabled(false);

			PlainDocument doc = (PlainDocument) jTextFieldQty.getDocument();
			doc.setDocumentFilter(new DoubleDocumentFilter());

			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.typeqty"), //$NON-NLS-1$
					jTextFieldQty, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}

			jTextFieldQty.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						// Move to next field
						
						handleQty();
						
						if(!isAutomaticLot()){  //
							e.consume();   
						}
						double qty = getQty(true);
						if (qty <= 0) {
							return;
						}

						Medical med = null;

						if (movement != null) {
							med = movement.getMedical();
						}
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
				};
			});

			jTextFieldQty.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
				}
				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldQty) {
						if (jTextFieldQty.getText().trim().equals("")) {
							jTextFieldQty.setText("1");
						}
						jTextFieldQty.selectAll();
					}
				}
			});

		}
		return jTextFieldQty;
	}

	private void handleQty() {
		double qty = getQty(true);
		if (qty <= 0) {
			jTextFieldQty.requestFocus();
			return;
		} else {

			Medical med = null;
			boolean hasOnlyOneLot = false;

			if (movement != null) {
				med = movement.getMedical();
			}
			if (med != null) {
				
				boolean isAvailable = checkQuantity(med, qty);
				hasOnlyOneLot = hasOnlyOneLot(med);
				if (isAvailable) {
					movement.setQuantity(qty);
					boolean res1 = updateTable();
					if(res1)
						newMovement();
					if (hasInitialStock(med)) {
						double remaining = getRemainingQty(med) - getUsedQty(med);
						if (remaining >= qty) {
							lot = new Lot("", null, null);						
							boolean res = updateTable();
							if (res)
								newMovement();
						} else {
							if(!isAutomaticLot()){ //added for auto discharge
								JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
										MessageBundle.getMessage(
												"angal.medicalstock.multipledischarging.finishremainingquantity") + " "
												+ remaining);
							}
							//added for auto discharge
							if(isAutomaticLot() && isAvailable){
								boolean res = updateTable();
								if (res)
									newMovement();
							}
						}
						return;
					}
					if (isAutomaticLot() || hasOnlyOneLot) {
						if (hasOnlyOneLot) {
							
							lot = chooseLot(med);
							if (lot == null) {
								// No lot available
								// This should not happen
								lot = new Lot("", null, null);
								boolean res = updateTable();
								if (res)
									newMovement();
							} else {
								boolean lotHasQty = checkQtyByLot(med, qty);
								if (lotHasQty) {
									boolean res = updateTable();
									if (res)
										newMovement();
								}
							}
						}
						///add for only automatic lot in discharging
						if(isAutomaticLot() && lot == null){
							boolean res = updateTable();
							if (res)
								newMovement();
						}
						///

					} else {
						jTextFieldLotId.requestFocus();
						jTextFieldLotId.selectAll();
					}
				}
				newMovement(); //
			}
		}
	}

	protected boolean hasOnlyOneLot(Medical med) {
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
		if (!lots.isEmpty()) {
			return lots.size() == 1;
		}
		double totalQty = getTotalQty(med);
		if (totalQty > 0) {
			return true;
		}
		return false;
	}

	private JTextField getJTextFieldLotId() {
		if (jTextFieldLotId == null) {
			jTextFieldLotId = new JTextField();
			jTextFieldLotId.setPreferredSize(new Dimension(120, 30));
			jTextFieldLotId.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldLotId.setColumns(10);
			jTextFieldLotId.setFocusTraversalKeysEnabled(false);

			final TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.typelotid"), //$NON-NLS-1$
					jTextFieldLotId, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			if (isAutomaticLot()) {
				// jTextFieldLotId.setEnabled(false);
				{
					suggestion.setText("auto");
				}
			}

			jTextFieldLotId.getInputMap().put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
			jTextFieldLotId.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "none");

			jTextFieldLotId.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (!isAutomaticLot() && e.isAltDown() && e.getKeyCode() == KeyEvent.VK_L) {
						chooseLot = false;
						Medical med = null;
						if (movement != null) {
							med = movement.getMedical();
						}
						if (med != null) {
							//////////
							lot = null;
							if (!hasInitialStock(med))
								lot = chooseLot(med);
							//////////
							// lot = chooseLot(med);
							if (lot != null) {
								jTextFieldLotId.setText(lot.getCode());
							} else {

								MovStockInsertingManager movBrowser = new MovStockInsertingManager();
								ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
								if (lots.isEmpty()) {
									double totalQty = getTotalQty(med);
									if (totalQty > 0) {
										lot = new Lot("", null, null);
									}
								} else {
									lot = new Lot("", null, null);
								}
							}
						}
						return;
					} else {
						suggestion.setText("auto");
					}

					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						jTextFieldQty.requestFocus();
						jTextFieldQty.selectAll();
					}

					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB) {

						if (lot == null) {
							// No lot available
							// This should not happen
							JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
									MessageBundle.getMessage("angal.medicalstock.nolotfoundformedical"));
							return;
						} else {
							double qty = getQty(true);
							if (qty <= 0) {
								return;
							}

							Medical med = null;

							if (movement != null) {
								med = movement.getMedical();
							}
							if (hasInitialStock(med)) {
								lot = new Lot("", null, null);
								boolean res = updateTable();
								if (res)
									newMovement();
							} else {
								
								boolean lotHasQty = checkQtyByLot(med, qty);
								if (lotHasQty) {
									boolean res = updateTable();
									if (res)
										newMovement();
								}
							}

						}

					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
					if (!(e.isAltDown() && e.getKeyCode() == KeyEvent.VK_L)) {
						e.consume();
					}

				};
			});

			jTextFieldLotId.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {

				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldLotId) {
						if (!isAutomaticLot()) {
							Medical med = null;
							if (movement != null) {
								med = movement.getMedical();
							}

							double qty = getQty(false);
							if (qty <= 0) {
								jTextFieldQty.requestFocus();
								jTextFieldQty.selectAll();
								return;
							}

							String lotId = jTextFieldLotId.getText();
							if (lotId == null || lotId.trim().equals("")) {
								MovStockInsertingManager movBrowser = new MovStockInsertingManager();
								if (med != null) {
									if (chooseLot) {
										chooseLot = false;

										lot = chooseLot(med);
										///add control here
										ArrayList<Lot> lotss = movBrowser.getLotByMedical(med);
										if(lot==null && !isAutomaticLot() && !lotss.isEmpty()){
											jTextFieldQty.requestFocus();
											jTextFieldQty.selectAll();
											return;
										}
										//
										if (lot != null) {
											jTextFieldLotId.setText(lot.getCode());
										} else {
											
											ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
											if (lots.isEmpty()) {
												double totalQty = getTotalQty(med);
												if (totalQty > 0) {
													lot = new Lot("", null, null);
												}
											} else {
												lot = new Lot("", null, null);
											}
										}
										boolean res = updateTable();
										if (res)
											newMovement();
									}
								}
							}
						} else {
							{
								suggestion.setText("auto");
							}
						}
					}

				}
			});
		}
		return jTextFieldLotId;
	}

	private void newMovement() {
		chooseLot = true;
		isNew = true;

		jTextFieldQty.setText("1");
		jTextFieldSearch.setText("");
		jTextFieldDescription.setText("");

		if (jTextFieldLotId != null) {
			jTextFieldLotId.setText("");
		}

		movement = null;
		lot = null;
		jTextFieldSearch.setEnabled(true);
		jTextFieldSearch.requestFocus();
	}

	protected double getQty(boolean showMessage) {
		String strQty = jTextFieldQty.getText();
		if (strQty == null || strQty.trim().equals("")) {
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		double qty = 1;
		try {
			qty = Double.valueOf(strQty);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		if (qty <= 0) {
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleDischarging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return qty;
	}

	protected Lot chooseLot(Medical med) {
		
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
		Lot lot = null;
		if (!lots.isEmpty()) {

			if (lots.size() == 1) {
				return lots.get(0);
			}

			LotPicker framas = new LotPicker(new StockMovModel(lots));
			framas.setSize(300, 400);

			JDialog dialog = new JDialog();
			dialog.setLocationRelativeTo(null);
			dialog.setSize(600, 350);
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);

			dialog.setTitle(MessageBundle.getMessage("angal.medicalstock.multiplecharging.existinglot"));

			Image ico = new javax.swing.ImageIcon("rsc/icons/oh.png").getImage();

			framas.setParentFrame(dialog);
			dialog.setContentPane(framas);
			dialog.setIconImage(ico);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			int row = framas.getSelectedRow();

			if (row >= 0) {
				lot = lots.get(row);
			}
			return lot;

		}
		return lot;
	}

	private boolean updateTable() {
		//add automatic discharging
		if(movement==null){
			return false;
	    }
		///
		if (isAutomaticLot() || lot != null) {
			double qty = getQty(true);
			
			if (!checkQuantity(movement.getMedical(), qty))
				return false;
			if (lot != null && !isAutomaticLot()){			    
				    //control lot qty 
				    if(lot.getCode().length()>0){
					    if(!checkQtyByLot(movement.getMedical(),qty)) {				    
					    	return false;
					    }
				    }
				    //end controle qty
					movement.setLot(lot);
				}
			else {				
				lot = new Lot("", null, null);
				movement.setLot(lot);
			}
			movement.setQuantity(qty);

			if (isNew) {
				
				units.add(PACKETS);
				model.addItem(movement);
			}
			jTableMovements.updateUI();
		}
		return true;
	}

	private void loadMovement() {
		if (movement != null) {
			isNew = false;
			jTextFieldSearch.setText(movement.getMedical().getProd_code());
			jTextFieldDescription.setText(movement.getMedical().getDescription());
			jTextFieldQty.setText(String.valueOf(movement.getQuantity()));
			if(jTextFieldLotId != null){
				jTextFieldLotId.setText(lot.getCode());
			}
			
			// jTextFieldExpiringDate.setText(format(lot.getDueDate(),
			// DATE_FORMAT_DDMMYYYY));
			jTextFieldSearch.setEnabled(false);
			jTextFieldDescription.setEnabled(false);
			jTextFieldQty.requestFocus();
			jTextFieldQty.selectAll();
		}

	}

	@Override
	public void wardUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
		refreshJComboBoxDestination();
	}

	@Override
	public void wardInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		refreshJComboBoxDestination();
	}
	
	private void refreshJComboBoxDestination() {		
		if (jComboBoxDestination == null) {
			jComboBoxDestination = new JComboBox();
		}else{
			jComboBoxDestination.removeAllItems();
		}
		jComboBoxDestination.addItem("");
		WardBrowserManager wardMan = new WardBrowserManager();
		ArrayList<Ward> wards = wardMan.getWards();
		for (Ward ward : wards) {
			if (Param.bool("INTERNALPHARMACIES")) {
				if (ward.isPharmacy())
					jComboBoxDestination.addItem(ward);
			} else {
				jComboBoxDestination.addItem(ward);
			}
		}				
	}

	@Override
	public void InventoryUpdated(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void InventoryInserted(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void InventoryValidated(AWTEvent e) {		
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicals();		
		medicalMap.clear();
		for (Medical med : medicals) {
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) 
				key = med.getCode().toString().toLowerCase();
			medicalMap.put(key, med);
		}				
		String text = jTextFieldSearch.getText();
		Medical med = null;
		if (medicalMap.containsKey(text.toLowerCase())) {
			med = medicalMap.get(text.toLowerCase());
			if(med!=null && movement!=null){
				movement.setMedical(med);
			}
		}			
	}

	@Override
	public void InventoryCancelled(AWTEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MedicalUpdated(AWTEvent e) {
		refreshMedicalList();
		
	}

	@Override
	public void MedicalInserted(AWTEvent e) {
		refreshMedicalList();
		
	}
	
	private void refreshMedicalList(){
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicals();
		medicalMap.clear();
		for (Medical med : medicals) {
			String key = med.getProd_code().toLowerCase();
			if (key.equals("")) 
				key = med.getCode().toString().toLowerCase();
			medicalMap.put(key, med);
		}
	}
}
