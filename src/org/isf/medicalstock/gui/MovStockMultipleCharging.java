package org.isf.medicalstock.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.apache.log4j.PropertyConfigurator;
import org.isf.accounting.gui.BillItemPicker;
import org.isf.accounting.gui.PatientBillEdit;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryEdit.InventoryListener;
import org.isf.medicals.gui.MedicalBrowser;
import org.isf.medicals.gui.MedicalEdit;
import org.isf.medicals.gui.MedicalEdit.MedicalEditListener;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.parameters.manager.Param;
import org.isf.supplier.gui.SupplierBrowser;
import org.isf.supplier.gui.SupplierEdit;
import org.isf.supplier.gui.SupplierEdit.SupplierListener;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierOperations;
import org.isf.utils.db.NormalizeString;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.BusyState;
import org.isf.utils.jobjects.DoubleDocumentFilter;
import org.isf.utils.jobjects.IntegerDocumentFilter;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.RequestFocusListener;
import org.isf.utils.jobjects.TextPrompt;
import org.isf.utils.jobjects.VoFloatTextField;
import org.isf.utils.jobjects.TextPrompt.Show;
import org.isf.utils.time.TimeTools;

import com.toedter.calendar.JDateChooser;
import java.awt.Dialog.ModalExclusionType;

//public class MovStockMultipleCharging extends JDialog {
//public class MovStockMultipleCharging extends JFrame {
public class MovStockMultipleCharging extends ModalJFrame implements SupplierListener, MedicalEditListener {	 
	//////////////
	private EventListenerList StockChargingListeners = new EventListenerList();

	public interface StockChargingListener extends EventListener {
		public void ChargingInserted(AWTEvent e);	
	}

	public void addStockChargingListener(StockChargingListener l) {
		StockChargingListeners.add(StockChargingListener.class, l);
	}

	public void removeStockChargingListener(StockChargingListener listener) {
		StockChargingListeners.remove(StockChargingListener.class, listener);
	}

	private void fireChargingInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = StockChargingListeners.getListeners(StockChargingListener.class);		
		for (int i = 0; i < listeners.length; i++)
			((StockChargingListener) listeners[i]).ChargingInserted(event);
	}
	////////////////
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DATE_FORMAT_DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss"; //$NON-NLS-1$
	private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy"; //$NON-NLS-1$
	private static final String DATE_FORMAT_DDMMYYYY = "ddMMyy"; //$NON-NLS-1$
	private static final int CODE_COLUMN_WIDTH = 100;

	private JPanel mainPanel;
	private JPanel panelEntry;
	private JTextField jTextFieldReference;
	private JTextField jTextFieldSearch;
	private JTextField jTextFieldDescription;
	private JTextField jTextFieldQty;
	private JLabel oldPriceLabel;
	private VoFloatTextField jTextFieldCost;
	private VoFloatTextField jTextFieldReductionRate;
	private JTextField jTextFieldLotId;
	private JTextField jTextFieldExpiringDate;
	private VoFloatTextField jTextFieldTotalCost;
	private JComboBox jComboBoxChargeType;
	private JDateChooser jDateChooser;
	private JComboBox jComboBoxSupplier;
	private JTable jTableMovements;
	private static final String DATE_FORMAT_DD_MM_YY_HH_MM = "dd/MM/yy HH:mm:ss";
	private final String[] columnNames = { MessageBundle.getMessage("angal.medicalstock.multiplecharging.code"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.description"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.qtypacket"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.qty"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.unitpack"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.total"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotnumberabb"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.expiringdate"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.cost"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.reductionrateshort"),
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.total") }; //$NON-NLS-1$
	private final Class[] columnClasses = { String.class, String.class, Integer.class, Integer.class, String.class,
			Integer.class, String.class, String.class, Double.class,String.class, Double.class };
	private boolean[] columnEditable = { false, false, false, true, true, false, ! Param.bool("AUTOMATICLOT"), true, true,
			false, false };
	private int[] columnWidth = { 90, 120, 70, 70, 80, 70, 70, 90, 70, 70, 90 };
	private boolean[] columnResizable = { false, true, false, false, false, false, false, false, false,false, false };
	private boolean[] columnVisible = { true, true, true, true, true, true, !Param.bool("AUTOMATICLOT"), true,
			Param.bool("LOTWITHCOST"),(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION")), Param.bool("LOTWITHCOST") };
	private int[] columnAlignment = { SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.CENTER,
			SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.CENTER,
			SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT };
	private boolean[] columnBold = { false, false, false, false, false, true, false, false, false, false, true };
	private HashMap<String, Medical> medicalMap;
	private ArrayList<Integer> units;
	private JTableModel model;
	private String[] qtyOption = new String[] { MessageBundle.getMessage("angal.medicalstock.multiplecharging.units"), //$NON-NLS-1$
			MessageBundle.getMessage("angal.medicalstock.multiplecharging.packet") }; //$NON-NLS-1$
	private JComboBox comboBox = new JComboBox(qtyOption);
	private final int UNITS = 0;
	private final int PACKETS = 1;
	private int optionSelected = UNITS;
	private Movement movement = null;
	private boolean isNew = true;
	private boolean chooseLot = true;
	private Lot lot;
	private JPanel jPanelTotal;
	private JLabel jLabelTotal;

	/**
	 * Launch the application. TODO: externalize strings
	 */
	public static void main(String[] args) {
		try {
			PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
			GeneralData.getGeneralData();
			new MovStockMultipleCharging(new JFrame());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAutomaticLot() {
		return Param.bool("AUTOMATICLOT");
	}

	/**
	 * Create the dialog.
	 */
	public MovStockMultipleCharging(JFrame owner) {
		//super(owner, true);
		super();
		initialize();
		initcomponents();
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		if(Param.bool("WITHMODALWINDOW")){
			this.showAsModal(owner);
		}else{
			this.show(owner);
		}
		jTextFieldReference.requestFocus();
	}
  
	
	private void initialize() {
		MedicalBrowsingManager medMan = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medMan.getMedicals();

		medicalMap = new HashMap<String, Medical>();
		for (Medical med : medicals) {
			// String key = med.getProd_code();
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
		           SupplierEdit.removeSupplierListener(MovStockMultipleCharging.this);
		           MedicalEdit.removeMedicalListener(MovStockMultipleCharging.this);
		           dispose();
		        }else		        
			       return;			    
		    }
		    @Override
		    public void windowOpened(WindowEvent e) {
		    	/*************** adding to supplier listener*/		    	
				SupplierEdit.addSupplierListener(MovStockMultipleCharging.this);
		    	MedicalEdit.addMedicalListener(MovStockMultipleCharging.this);
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
		if(Param.bool("COST_WITH_REDUCTION")){
			setPreferredSize(new Dimension(1200, 600));
		}
		else{
			setPreferredSize(new Dimension(1070, 600));
		}
		
		pack();
		setLocationRelativeTo(null);
	}

	private JPanel getJPanelHeader() {
		JPanel headerPanel = new JPanel();
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		GridBagLayout gbl_headerPanel = new GridBagLayout();
		gbl_headerPanel.columnWidths = new int[] { 0, 0, 29, 0, 0 };
		gbl_headerPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_headerPanel.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_headerPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		headerPanel.setLayout(gbl_headerPanel);
		{
			JLabel jLabelDate = new JLabel(MessageBundle.getMessage("angal.common.date") + ":"); //$NON-NLS-1$
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
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.referencenumberabb") + ":"); //$NON-NLS-1$
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
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.chargetype") + ":"); //$NON-NLS-1$
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
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.supplier") + ":"); //$NON-NLS-1$
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
			headerPanel.add(getJComboBoxSupplier(), gbc_jComboBoxSupplier);
		}
		{
			oldPriceLabel = new JLabel("");
			oldPriceLabel.setHorizontalAlignment(SwingConstants.LEFT);
			GridBagConstraints gbc_oldPriceLabel = new GridBagConstraints();
			gbc_oldPriceLabel.anchor = GridBagConstraints.WEST;
			gbc_oldPriceLabel.insets = new Insets(0, 0, 0, 5);
			gbc_oldPriceLabel.gridx = 2;
			gbc_oldPriceLabel.gridy = 3;
			headerPanel.add(oldPriceLabel, gbc_oldPriceLabel);
		}
		return headerPanel;
	}

	private JPanel getJButtonPane() {
		JPanel buttonPane = new JPanel();
		// buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		{
			JButton saveButton = new JButton(MessageBundle.getMessage("angal.common.save")); //$NON-NLS-1$
			saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					BusyState.setBusyState(MovStockMultipleCharging.this, true);
					if (!checkAndPrepareMovements()) {
						BusyState.setBusyState(MovStockMultipleCharging.this, false);
						return;
					}
					if (!save()) {
						BusyState.setBusyState(MovStockMultipleCharging.this, false);
						return;
					}
					/////
					fireChargingInserted();
					/////
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
			mainPanel.add(getTotalPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	private JPanel getTotalPanel() {
		// TODO Auto-generated method stub
		if (jPanelTotal == null) {
			jPanelTotal = new JPanel();
			jPanelTotal.setLayout(new FlowLayout(FlowLayout.RIGHT));
			jPanelTotal.add(getJLabelTotal());
		}

		return jPanelTotal;
	}

	private JLabel getJLabelTotal() {
		if (jLabelTotal == null) {
			jLabelTotal = new JLabel();
			jLabelTotal.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabelTotal.setText("");
		}
		return jLabelTotal;
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
						updateTotal();
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
			qtyOptionColumn.setCellEditor(new DefaultCellEditor(comboBox));

			TableColumn costColumn = jTableMovements.getColumnModel().getColumn(8);
			costColumn.setCellRenderer(new DecimalFormatRenderer());
			
			TableColumn totalColumn = jTableMovements.getColumnModel().getColumn(10);
			totalColumn.setCellRenderer(new DecimalFormatRenderer());

			comboBox.setSelectedIndex(optionSelected);

			jTableMovements.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {

				}

				public void mousePressed(java.awt.event.MouseEvent evt) {
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

		}
		return jTableMovements;
	}

	private void loadMovement() {
		if (movement != null) {
			isNew = false;
			jTextFieldSearch.setText(movement.getMedical().getProd_code());
			jTextFieldDescription.setText(movement.getMedical().getDescription());
			jTextFieldQty.setText(String.valueOf(movement.getQuantity()));
			if (Param.bool("LOTWITHCOST")) {
				Double costDbl = lot.getCost();
				jTextFieldCost.setText(String.valueOf(costDbl));
				if (Param.bool("COST_WITH_REDUCTION")) {
					Double redRate = lot.getReduction_rate();
					//int reducPlan = costDbl.intValue();
					jTextFieldReductionRate.setText(String.valueOf(redRate));
					Double reducCost = costDbl - costDbl*(redRate/100);
					jTextFieldTotalCost.setText(String.valueOf(reducCost * movement.getQuantity()));
				}else{
					jTextFieldTotalCost.setText(String.valueOf(costDbl * movement.getQuantity()));
				}
			}
			jTextFieldLotId.setText(lot.getCode());
			jTextFieldExpiringDate.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));
			jTextFieldSearch.setEnabled(false);
			jTextFieldDescription.setEnabled(false);
			jTextFieldQty.requestFocus();
			jTextFieldQty.selectAll();
		}
	}

	private JPanel getPanelEntry() {
		panelEntry =  new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		if (Param.bool("LOTWITHCOST")) {
			if (Param.bool("COST_WITH_REDUCTION")) {
				gbl_panel.columnWidths = new int[] { 200, 100, 100, 100, 40, 120, 100, 100,  0 };
				gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			}
			gbl_panel.columnWidths = new int[] { 200, 100, 100, 100, 100, 120, 100,  0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		} else {
			gbl_panel.columnWidths = new int[] { 200, 100, 100, 120, 100, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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
		{
			JLabel lblNewLabel_1 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.description"));
			lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_1.gridx = 1;
			gbc_lblNewLabel_1.gridy = 0;
			panelEntry.add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		{
			JLabel lblNewLabel_2 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.quantity"));
			lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_2.gridx = 2;
			gbc_lblNewLabel_2.gridy = 0;
			panelEntry.add(lblNewLabel_2, gbc_lblNewLabel_2);
		}
		GridBagConstraints gbc_jTextFieldSearch = new GridBagConstraints();
		gbc_jTextFieldSearch.insets = new Insets(0, 0, 0, 5);
		gbc_jTextFieldSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldSearch.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldSearch.gridx = 0;
		gbc_jTextFieldSearch.gridy = 1;
		panelEntry.add(getJTextFieldSearch(), gbc_jTextFieldSearch);

		GridBagConstraints gbc_jTextFieldDescription = new GridBagConstraints();
		gbc_jTextFieldDescription.insets = new Insets(0, 0, 0, 5);
		gbc_jTextFieldDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldDescription.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldDescription.gridx = 1;
		gbc_jTextFieldDescription.gridy = 1;
		panelEntry.add(getJTextFieldDescription(), gbc_jTextFieldDescription);

		GridBagConstraints gbc_jTextFieldQty = new GridBagConstraints();
		gbc_jTextFieldQty.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldQty.anchor = GridBagConstraints.NORTH;
		gbc_jTextFieldQty.gridx = 2;
		gbc_jTextFieldQty.gridy = 1;
		panelEntry.add(getJTextFieldQty(), gbc_jTextFieldQty);

		if (Param.bool("LOTWITHCOST")) {
			{
				JLabel lblNewLabel_3 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.price"));
				lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
				gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel_3.gridx = 3;
				gbc_lblNewLabel_3.gridy = 0;
				panelEntry.add(lblNewLabel_3, gbc_lblNewLabel_3);
			}
			GridBagConstraints gbc_jTextFieldCost = new GridBagConstraints();
			gbc_jTextFieldCost.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldCost.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldCost.gridx = 3;
			gbc_jTextFieldCost.gridy = 1;
			panelEntry.add(getJTextFieldCost(), gbc_jTextFieldCost);
			
			int i = 0;
			if (Param.bool("COST_WITH_REDUCTION")) {
				
				{
					JLabel lblNewLabel_4 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.reduction"));
					lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 12));
					GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
					gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
					gbc_lblNewLabel_4.gridx = 4;
					gbc_lblNewLabel_4.gridy = 0;
					panelEntry.add(lblNewLabel_4, gbc_lblNewLabel_4);
				}
				
				i=1;
				GridBagConstraints gbc_jTextFieldReductionRate = new GridBagConstraints();
				gbc_jTextFieldReductionRate.fill = GridBagConstraints.HORIZONTAL;
				gbc_jTextFieldReductionRate.anchor = GridBagConstraints.NORTH;
				gbc_jTextFieldReductionRate.gridx = 4;
				gbc_jTextFieldReductionRate.gridy = 1;
				panelEntry.add(getJTextFieldReductionRate(), gbc_jTextFieldReductionRate);					
			}
			
			{
				JLabel lblNewLabel_5 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.totalcost"));
				lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
				gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel_5.gridx = 4+i;
				gbc_lblNewLabel_5.gridy = 0;
				panelEntry.add(lblNewLabel_5, gbc_lblNewLabel_5);
			}
			GridBagConstraints gbc_jTextFieldTotalCost = new GridBagConstraints();
			gbc_jTextFieldTotalCost.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldTotalCost.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldTotalCost.gridx = 4+i;
			gbc_jTextFieldTotalCost.gridy = 1;
			panelEntry.add(getJTextFieldTotalCost(), gbc_jTextFieldTotalCost);

			{
				JLabel lblNewLabel_6 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotid"));
				lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
				gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel_6.gridx = 5+i;
				gbc_lblNewLabel_6.gridy = 0;
				panelEntry.add(lblNewLabel_6, gbc_lblNewLabel_6);
			}
			GridBagConstraints gbc_jTextFieldLotId = new GridBagConstraints();
			gbc_jTextFieldLotId.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldLotId.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldLotId.gridx = 5+i;
			gbc_jTextFieldLotId.gridy = 1;
			panelEntry.add(getJTextFieldLotId(), gbc_jTextFieldLotId);
			
			{
				JLabel lblNewLabel_7 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.expiringDate"));
				lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
				gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel_7.gridx = 6+i;
				gbc_lblNewLabel_7.gridy = 0;
				panelEntry.add(lblNewLabel_7, gbc_lblNewLabel_7);
			}
			
			GridBagConstraints gbc_jTextFieldExpiringDate = new GridBagConstraints();
			gbc_jTextFieldExpiringDate.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldExpiringDate.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldExpiringDate.gridx = 6+i;
			gbc_jTextFieldExpiringDate.gridy = 1;
			panelEntry.add(getJTextFieldExpiringDate(), gbc_jTextFieldExpiringDate);
			
		} else {
			{
				JLabel lblNewLabel_8 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotid"));
				lblNewLabel_8.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
				gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel_8.gridx = 3;
				gbc_lblNewLabel_8.gridy = 0;
				panelEntry.add(lblNewLabel_8, gbc_lblNewLabel_8);
			}
			GridBagConstraints gbc_jTextFieldLotId = new GridBagConstraints();
			gbc_jTextFieldLotId.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldLotId.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldLotId.gridx = 3;
			gbc_jTextFieldLotId.gridy = 1;
			panelEntry.add(getJTextFieldLotId(), gbc_jTextFieldLotId);

			{
				JLabel lblNewLabel_9 = new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.expiringDate"));
				lblNewLabel_9.setFont(new Font("Tahoma", Font.BOLD, 12));
				GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
				gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel_9.gridx = 4;
				gbc_lblNewLabel_9.gridy = 0;
				panelEntry.add(lblNewLabel_9, gbc_lblNewLabel_9);
			}
			GridBagConstraints gbc_jTextFieldExpiringDate = new GridBagConstraints();
			gbc_jTextFieldExpiringDate.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFieldExpiringDate.anchor = GridBagConstraints.NORTH;
			gbc_jTextFieldExpiringDate.gridx = 4;
			gbc_jTextFieldExpiringDate.gridy = 1;
			panelEntry.add(getJTextFieldExpiringDate(), gbc_jTextFieldExpiringDate);
			
		}

		return panelEntry;
	}
	

	private JTextField getJTextFieldDescription() {
		if (jTextFieldDescription == null) {
			jTextFieldDescription = new JTextField();
			jTextFieldDescription.setEnabled(false);
			jTextFieldDescription.setPreferredSize(new Dimension(100, 30));
			jTextFieldDescription.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldDescription.setColumns(10);
		}
		return jTextFieldDescription;
	}

	private JTextField getJTextFieldExpiringDate() {
		if (jTextFieldExpiringDate == null) {
			jTextFieldExpiringDate = new JTextField();
			jTextFieldExpiringDate.setPreferredSize(new Dimension(100, 30));
			jTextFieldExpiringDate.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldExpiringDate.setColumns(10);

			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.typeexpiringdate"), //$NON-NLS-1$
					jTextFieldExpiringDate, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}

			jTextFieldExpiringDate.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					int maxLength = 6;
					if (jTextFieldExpiringDate.getText().indexOf("/") >= 0) {
						maxLength = 10;
					}
					if (jTextFieldExpiringDate.getText().length() >= maxLength
							&& !(e.getKeyChar() == KeyEvent.VK_DELETE || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
						if (jTextFieldExpiringDate.getSelectionStart() == 0
								&& jTextFieldExpiringDate.getSelectionEnd() == maxLength) {
							return;
						} else {
							getToolkit().beep();
							e.consume();
						}

					}
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}

					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (!isAutomaticLot()) {
							jTextFieldLotId.requestFocus();
							jTextFieldLotId.selectAll();
						} else {
							if (Param.bool("LOTWITHCOST")) {
								jTextFieldCost.requestFocus();
								jTextFieldCost.selectAll();
								return;
							} else {
								jTextFieldQty.requestFocus();
								jTextFieldQty.selectAll();
								return;
							}
						}
					}

					if (e.getKeyCode() == KeyEvent.VK_ENTER) {

						String strDate = jTextFieldExpiringDate.getText();

						if (strDate == null || strDate.trim().equals("")) {
							JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
									MessageBundle.getMessage("angal.medicalstockwardedit.invaliddatetryagain"), //$NON-NLS-1$
									MessageBundle.getMessage("angal.medicalstockwardedit.invaliddate"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
							jTextFieldExpiringDate.requestFocus();
							jTextFieldExpiringDate.selectAll();
							e.consume();
							return;
						} else {
							try {
								GregorianCalendar dueDate = TimeTools.getDate(strDate, DATE_FORMAT_DDMMYYYY);
								if (dueDate != null) {
									dueDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
									dueDate.set(GregorianCalendar.MINUTE, 59);
									dueDate.set(GregorianCalendar.SECOND, 59);

									if (lot == null) {
										lot = new Lot(jTextFieldLotId.getText(), new GregorianCalendar(),
												new GregorianCalendar());
									}
									
									lot.setDueDate(dueDate);
								  
									if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION")){
										Double redrate = getReductionRate(false);
										lot.setReduction_rate(redrate<0 ? 0.: redrate);
									}
								}
								boolean res = updateTable();
								if (res)
									newMovement();
							} catch (ParseException e1) {
								JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
										MessageBundle.getMessage("angal.medicalstockwardedit.invaliddatetryagain"), //$NON-NLS-1$
										MessageBundle.getMessage("angal.medicalstockwardedit.invaliddate"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
								jTextFieldExpiringDate.requestFocus();
								jTextFieldExpiringDate.selectAll();
							}

						}
					}
				}
			});

			jTextFieldExpiringDate.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					
				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldExpiringDate) {
						if (jTextFieldExpiringDate.getText().trim().equals("")) {
							jTextFieldExpiringDate.setText(format(new GregorianCalendar(), DATE_FORMAT_DDMMYYYY));
						}
					}
					jTextFieldExpiringDate.selectAll();

				}
			});
		}
		return jTextFieldExpiringDate;
	}

	private void newMovement() {
		chooseLot = true;
		isNew = true;
		if (Param.bool("LOTWITHCOST")) {
			jTextFieldCost.setText("");
			jTextFieldTotalCost.setText("");
			if (Param.bool("COST_WITH_REDUCTION")) {
				//jTextFieldReductionRate.setText(String.valueOf(0));
				jTextFieldReductionRate.setText("");
			}
		}
		jTextFieldQty.setText("");
		jTextFieldSearch.setText("");
		jTextFieldDescription.setText("");
		jTextFieldExpiringDate.setText("");
		jTextFieldLotId.setText("");
		// jTextFieldLotPrepDate.setText("");
		movement = null;
		lot = null;
		jTextFieldSearch.setEnabled(true);
		jTextFieldSearch.requestFocus();
		/////
		oldPriceLabel.setText("");
		////
		
	}

	private boolean updateTable() {

		String lotCode = jTextFieldLotId.getText();
		if (!lotCode.trim().equals("")) {
			lot.setCode(lotCode);
		}
		
		movement.setLot(lot);
		double qty = getQty(true);
		if (qty < 0) {
			return false;
		}
		movement.setQuantity(qty);

		if (Param.bool("LOTWITHCOST")) {
			double cost = getPrice(true);
			if (cost <= 0) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidpricepleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidprice"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			lot.setCost(cost);
		}

		if (isNew) {
			units.add(PACKETS);
			model.addItem(movement);
		}
		jTableMovements.updateUI();

		updateTotal();
		return true;
	}

	private JTextField getJTextFieldLotId() {
		if (jTextFieldLotId == null) {
			jTextFieldLotId = new JTextField();
			jTextFieldLotId.setPreferredSize(new Dimension(100, 30));
			jTextFieldLotId.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldLotId.setColumns(10);

			TextPrompt suggestion = new TextPrompt(
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
				jTextFieldLotId.setEnabled(false);
			}

			jTextFieldLotId.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (Param.bool("LOTWITHCOST")) {
							jTextFieldCost.requestFocus();
							jTextFieldCost.selectAll();
							return;
						} else {
							jTextFieldQty.requestFocus();
							jTextFieldQty.selectAll();
							return;
						}

					}
					if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_L) {
						chooseLot = false;
						Medical med = null;
						if (movement != null) {
							med = movement.getMedical();
						}
						if (med != null) {
							lot = chooseLot(med);
							if (lot != null) {
								jTextFieldLotId.setText(lot.getCode());
								jTextFieldExpiringDate.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));
								if(Param.bool("LOTWITHCOST") ){
									jTextFieldCost.setText(String.valueOf(lot.getCost()));
									jTextFieldTotalCost.setText(String.valueOf(lot.getCost() * getQty(true)));
								}
								
								if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION")){
									
									Double lotCost = lot.getCost();
									jTextFieldCost.setText(String.valueOf(lotCost));
									Double reducRate = lot.getReduction_rate();
									reducRate = reducRate<0?0:reducRate;
									lotCost = lotCost - lotCost*(reducRate/100);
									jTextFieldTotalCost.setText(String.valueOf(lotCost * getQty(true)));
									jTextFieldReductionRate.setText(String.valueOf(reducRate));
									jTextFieldExpiringDate.requestFocus();
									jTextFieldExpiringDate.selectAll();		
								}			
								/////////////
							}
						}

						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						// Move to next field
						String lotId = jTextFieldLotId.getText();

						if (lotId == null || lotId.trim().equals("")) {
							jTextFieldLotId.requestFocus();
							jTextFieldLotId.selectAll();
							e.consume();
							return;
						} else {
							MovStockInsertingManager movBrowser = new MovStockInsertingManager();
							Medical med = null;
							if (lot == null) {
								lot = new Lot(lotId, new GregorianCalendar(), new GregorianCalendar());
							}

							if (Param.bool("LOTWITHCOST")) {
								double cost = getPrice(true);
								lot.setCost(cost);
								////j
								if(Param.bool("COST_WITH_REDUCTION")){
									Double redrate = getReductionRate(false);
									lot.setReduction_rate(redrate<0 ? 0.: redrate);
								}
								/////
							}

							jTextFieldExpiringDate.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));

							if (movement != null) {
								med = movement.getMedical();
								// movement.setLot(lot);
							}

							if (med != null) {
								if (isNew) {
									ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
									if (lots != null && lots.size() > 0) {
										for (Iterator<Lot> iterator = lots.iterator(); iterator.hasNext();) {
											Lot lot = (Lot) iterator.next();
											if (lot.getCode().equalsIgnoreCase(lotId)) {
												MovStockMultipleCharging.this.lot = lot;
												
												if (Param.bool("LOTWITHCOST")) {
													
													Double lotCost = MovStockMultipleCharging.this.lot.getCost();
													jTextFieldCost.setText(String.valueOf(MovStockMultipleCharging.this.lot.getCost()));
													jTextFieldTotalCost.setText(String.valueOf(lotCost * getQty(true)));
													
													if(Param.bool("COST_WITH_REDUCTION"))	{
														Double reducRate = MovStockMultipleCharging.this.lot.getReduction_rate();
														reducRate = reducRate<0?0:reducRate;
														lotCost = lotCost - lotCost*(reducRate/100);
														jTextFieldTotalCost.setText(String.valueOf(lotCost * getQty(true)));
														jTextFieldReductionRate.setText(String.valueOf(reducRate));
													}
													jTextFieldExpiringDate.requestFocus();
													jTextFieldExpiringDate.selectAll();		
													
												}
												jTextFieldExpiringDate
														.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));
											}

										}
									}
								}
							}
							jTextFieldExpiringDate.requestFocus();
							jTextFieldExpiringDate.selectAll();

						}
					}
				}
			});

			jTextFieldLotId.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {

					if (e.getComponent() == jTextFieldLotId) {
						String lotId = jTextFieldLotId.getText();

						if (lotId == null || lotId.trim().equals("")) {
							return;
						} else {
							MovStockInsertingManager movBrowser = new MovStockInsertingManager();
							Medical med = null;
							/*if (lot == null) {
								lot = new Lot(lotId, new GregorianCalendar(), new GregorianCalendar());
							}*/
							////////////////////////			
							jTextFieldLotId.setText(lotId);
							if (lot == null) {
								lot = new Lot(lotId, new GregorianCalendar(), new GregorianCalendar());
							} else {
								lot.setCode(lotId);
							}
							//////////////////////
							if (Param.bool("LOTWITHCOST")) {
								double cost = getPrice(true);
								lot.setCost(cost);
							
								if(Param.bool("COST_WITH_REDUCTION")){
									Double redrate = getReductionRate(false);
									lot.setReduction_rate(redrate<0 ? 0.: redrate);
								}
							}

							jTextFieldExpiringDate.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));
							if (movement != null) {
								med = movement.getMedical();
							}

							if (med != null) {
								if (isNew) {
									
									ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
									if (lots != null && lots.size() > 0) {
										for (Iterator<Lot> iterator = lots.iterator(); iterator.hasNext();) {
											Lot lot = (Lot) iterator.next();
											if (lot.getCode().equalsIgnoreCase(lotId)) {
												MovStockMultipleCharging.this.lot = lot;
												if (Param.bool("LOTWITHCOST")) {
												
													Double lotCost = MovStockMultipleCharging.this.lot.getCost();
													jTextFieldCost.setText(String.valueOf(MovStockMultipleCharging.this.lot.getCost()));
													jTextFieldTotalCost.setText(String.valueOf(lotCost * getQty(true)));
													
													if(Param.bool("COST_WITH_REDUCTION"))	{
														Double reducRate = MovStockMultipleCharging.this.lot.getReduction_rate();
														reducRate = reducRate<0?0:reducRate;
														lotCost = lotCost - lotCost*(reducRate/100);
														jTextFieldTotalCost.setText(String.valueOf(lotCost * getQty(true)));
														jTextFieldReductionRate.setText(String.valueOf(reducRate));
													}
													jTextFieldExpiringDate.requestFocus();
													jTextFieldExpiringDate.selectAll();		
													
												}
												jTextFieldExpiringDate
														.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));
											}

										}
									}
								}
							}

						}
					}

				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldLotId) {
						String lotId = jTextFieldLotId.getText();
						if (lotId == null || lotId.trim().equals("")) {
							Medical med = null;
							if (movement != null) {
								med = movement.getMedical();
							}
							if (med != null) {
								if (chooseLot) {
									chooseLot = false;
									lot = chooseLot(med);
									if (lot != null) {
									    jTextFieldLotId.setText(lot.getCode());
										jTextFieldExpiringDate.setText(format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY));
										if(Param.bool("LOTWITHCOST") ){
											jTextFieldCost.setText(String.valueOf(lot.getCost()));
											jTextFieldTotalCost.setText(String.valueOf(lot.getCost() * getQty(true)));
										}
										
										if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION")){
											Double lotCost = lot.getCost();
											jTextFieldCost.setText(String.valueOf(lotCost));
											//Double reducRate = getReductionRate(false);
											Double reducRate = lot.getReduction_rate();
											reducRate = reducRate<0?0:reducRate;
											lotCost = lotCost - lotCost*(reducRate/100);
											jTextFieldTotalCost.setText(String.valueOf(lotCost * getQty(true)));
											jTextFieldReductionRate.setText(String.valueOf(reducRate));
											jTextFieldExpiringDate.requestFocus();
											jTextFieldExpiringDate.selectAll();		
										}	
									}
								}

							}

						}
					}

				}
			});
		}
		return jTextFieldLotId;
	}
	
	private JTextField getJTextFieldReductionRate(){
		if (jTextFieldReductionRate == null) {
			//jTextFieldReductionRate = new VoFloatTextField(0,100);
			jTextFieldReductionRate = new VoFloatTextField("",10);
			jTextFieldReductionRate.setPreferredSize(new Dimension(40, 30));
			jTextFieldReductionRate.setMaximumSize(new Dimension(40, 30));
			jTextFieldReductionRate.setMinimumSize(new Dimension(40, 30));
			jTextFieldReductionRate.setHorizontalAlignment(SwingConstants.RIGHT);
			jTextFieldReductionRate.setColumns(5);
			
			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.reductionrate"), //$NON-NLS-1$
					jTextFieldReductionRate, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			jTextFieldReductionRate.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						jTextFieldCost.requestFocus();
						jTextFieldCost.selectAll();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						double cost = getPrice(true);
						if (cost < 0) {
							jTextFieldCost.requestFocus();
							e.consume();
							return;
						} else {
							if (cost > 0) {
								double reducPlan = getReductionRate(true);
								
								if(reducPlan<0){
									jTextFieldReductionRate.requestFocus();
									e.consume();
									return;
								}else{
									if (!isAutomaticLot()) {
										jTextFieldLotId.requestFocus();
										jTextFieldLotId.selectAll();
									} else {
										jTextFieldExpiringDate.requestFocus();
										jTextFieldExpiringDate.selectAll();
									}
									//Double costDbl = cost;
									Double reductedCost = cost - cost*(reducPlan/100);
									//jTextFieldTotalCost.setText(String.valueOf(costDbl.intValue() * getQty(true)));
									jTextFieldTotalCost.setText(String.valueOf(reductedCost * getQty(true)));
								}
							}
						}
					}
				}
				@Override
				public void keyReleased(KeyEvent e) {
					String actual = jTextFieldReductionRate.getText();
					if(actual.length()>0){
						Double typed = Double.parseDouble(actual);
						if(typed>100){
							jTextFieldReductionRate.setText(String.valueOf(100));
						}
					}
				}
			});
			
			jTextFieldReductionRate.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldReductionRate) {
						if (jTextFieldReductionRate.getText().trim().equals("")) {
							jTextFieldReductionRate.setText("0");
						}
						jTextFieldReductionRate.selectAll();
					}
				}
			});
		}
		return jTextFieldReductionRate;
	}
	
	private JTextField getJTextFieldCost() {
		if (jTextFieldCost == null) {
			jTextFieldCost = new VoFloatTextField("",100);
			jTextFieldCost.setPreferredSize(new Dimension(100, 30));
			jTextFieldCost.setHorizontalAlignment(SwingConstants.RIGHT);
			jTextFieldCost.setColumns(10);

			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.typecost"), //$NON-NLS-1$
					jTextFieldCost, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			
			jTextFieldCost.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						jTextFieldQty.requestFocus();
						jTextFieldQty.selectAll();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						// Move to next field
						double cost = getPrice(true);
						if (cost < 0) {
							jTextFieldCost.requestFocus();
							e.consume();
							return;
						} else {
							if (cost > 0) {
								/////j
								if(Param.bool("COST_WITH_REDUCTION")){
									jTextFieldReductionRate.requestFocus();
									jTextFieldReductionRate.selectAll();
								}else{
									if (!isAutomaticLot()) {
										jTextFieldLotId.requestFocus();
										jTextFieldLotId.selectAll();
									} else {
										jTextFieldExpiringDate.requestFocus();
										jTextFieldExpiringDate.selectAll();
									}
									Double costDbl = cost;
									jTextFieldTotalCost.setText(String.valueOf(costDbl * getQty(true)));
								}
							} else {
								/////j
								if(Param.bool("COST_WITH_REDUCTION")){
									jTextFieldReductionRate.requestFocus();
									jTextFieldReductionRate.selectAll();
								}else{
									jTextFieldTotalCost.setEnabled(true);
									jTextFieldTotalCost.requestFocus();
									jTextFieldTotalCost.selectAll();
								}
								
							}
						}
					}
				}
			});

			jTextFieldCost.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldCost) {
						if (jTextFieldCost.getText().trim().equals("")) {
							jTextFieldCost.selectAll();
						}
					}
				}
			});

			if (!Param.bool("LOTWITHCOST")) {
				jTextFieldCost.setVisible(false);
			}

		}
		return jTextFieldCost;
	}

	private JTextField getJTextFieldTotalCost() {
		if (jTextFieldTotalCost == null) {
			jTextFieldTotalCost = new VoFloatTextField("",100);
			jTextFieldTotalCost.setPreferredSize(new Dimension(100, 30));
			jTextFieldTotalCost.setHorizontalAlignment(SwingConstants.RIGHT);
			jTextFieldTotalCost.setColumns(10);

			TextPrompt suggestion = new TextPrompt(
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.typetotalcost"), //$NON-NLS-1$
					jTextFieldTotalCost, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
				suggestion.setForeground(Color.GRAY);
				suggestion.setHorizontalAlignment(JLabel.CENTER);
				suggestion.changeAlpha(0.5f);
				suggestion.changeStyle(Font.BOLD + Font.ITALIC);
			}
			jTextFieldTotalCost.setEnabled(false);
			jTextFieldTotalCost.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						newMovement();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						
						jTextFieldCost.requestFocus();
						jTextFieldCost.selectAll();
						return;
					}
					if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB
							|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
						double totalCost = getTotalPrice(true);
						if (totalCost <= 0) {
							jTextFieldTotalCost.requestFocus();
							e.consume();
							return;
						} else {
							if (totalCost > 0) {
								double cost = totalCost / getQty(true);
								jTextFieldCost.setText(String.valueOf(cost));
								if (isAutomaticLot()) {
									jTextFieldExpiringDate.requestFocus();
									jTextFieldExpiringDate.selectAll();
								} else {
									jTextFieldLotId.requestFocus();
									jTextFieldLotId.selectAll();
								}
							}
						}

					}
				}
			});

			jTextFieldTotalCost.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {
					if (e.getComponent() == jTextFieldTotalCost) {
						double totalCost = getTotalPrice(false);
						if (totalCost <= 0) {
							return;
						} else {
							if (totalCost > 0) {
								double cost = totalCost / getQty(false);
								jTextFieldCost.setText(String.valueOf(cost));
							}
						}
					}
				}

				@Override
				public void focusGained(FocusEvent e) {
					if (e.getComponent() == jTextFieldTotalCost) {
						if (jTextFieldTotalCost.getText().trim().equals("")) {
							jTextFieldTotalCost.selectAll();
						}
					}
				}
			});

			if (!Param.bool("LOTWITHCOST")) {
				jTextFieldTotalCost.setVisible(false);
			}
		}
		return jTextFieldTotalCost;
	}

	private JTextField getJTextFieldQty() {
		if (jTextFieldQty == null) {
			jTextFieldQty = new JTextField();
			jTextFieldQty.setPreferredSize(new Dimension(100, 30));
			jTextFieldQty.setHorizontalAlignment(SwingConstants.RIGHT);
			jTextFieldQty.setColumns(10);

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
						double qty = getQty(true);
						if (qty <= 0) {
							jTextFieldQty.requestFocus();
							e.consume();
							return;
						} else {
							if (Param.bool("LOTWITHCOST")) {
								jTextFieldCost.requestFocus();
								jTextFieldCost.selectAll();
							} else {
								if (isAutomaticLot()) {
									jTextFieldExpiringDate.requestFocus();
									jTextFieldExpiringDate
											.setText(format(new GregorianCalendar(), DATE_FORMAT_DDMMYYYY));
									jTextFieldExpiringDate.selectAll();
								} else {
									jTextFieldLotId.requestFocus();
									jTextFieldLotId.selectAll();
								}
							}

						}
					}
				}
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

	private JTextField getJTextFieldSearch() {
		if (jTextFieldSearch == null) {
			jTextFieldSearch = new JTextField();
			jTextFieldSearch.setPreferredSize(new Dimension(140, 30));
			jTextFieldSearch.setHorizontalAlignment(SwingConstants.LEFT);
			jTextFieldSearch.setFocusTraversalKeysEnabled(false);
			
			jTextFieldSearch.setColumns(10);
			TextPrompt suggestion = new TextPrompt(
					MessageBundle
							.getMessage("angal.medicalstock.multiplecharging.typeacodeoradescriptionandpressenter"), //$NON-NLS-1$
					jTextFieldSearch, Show.FOCUS_LOST);
			{
				suggestion.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
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
						
						String text = jTextFieldSearch.getText();
						
						Medical med = null;

						if (medicalMap.containsKey(text.toLowerCase())) {
							// Medical found
							med = medicalMap.get(text.toLowerCase());
						} else {

							med = chooseMedical(text.toLowerCase());
						}

						if (med != null) {

							String refNo = jTextFieldReference.getText();
                            ///////////// get last price
							MovStockInsertingManager manager = new MovStockInsertingManager();
							try {
								double cost = manager.getLastMovementPriceForAMedical(med.getCode());
								if(cost>0.0){
									oldPriceLabel.setText(MessageBundle.getMessage("angal.medicalstock.multiplecharging.oldprice")+" "+cost);
								}
							} catch (OHException e1) {
								e1.printStackTrace();
							}
							/////////////
							jTextFieldSearch.setText(med.getProd_code().toUpperCase());
							jTextFieldDescription.setText(med.getDescription());
							jTextFieldQty.requestFocus();
							jTextFieldQty.selectAll();

							jTextFieldSearch.setEnabled(false);

							GregorianCalendar date = new GregorianCalendar();
							date.setTime(jDateChooser.getDate());

							movement = new Movement(med, (MovementType) jComboBoxChargeType.getSelectedItem(), null,
									null, date, 0, new Supplier(), refNo);
						}

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

	protected Lot getLot() throws ParseException {
		// TODO Auto-generated method stub
		Lot lot = null;
		if (isAutomaticLot()) {
			GregorianCalendar preparationDate = TimeTools.getServerDateTime();
			GregorianCalendar expiringDate = TimeTools.getDate(jTextFieldExpiringDate.getText(), DATE_FORMAT_DDMMYYYY);
			lot = new Lot("", preparationDate, expiringDate); //$NON-NLS-1$
			// Cost
			if (Param.bool("LOTWITHCOST")) {
				double cost = getPrice(true);
				lot.setCost(cost);
				////j
				if(Param.bool("COST_WITH_REDUCTION")){
					Double redrate = getReductionRate(false);
					lot.setReduction_rate(redrate<0 ? 0. : redrate);
				}
				/////
			}
		} else {
			// lot = chooseLot(med);
			if (lot == null) {
				lot = askLot();
				if (lot == null) {
					return null;
				}
				if (Param.bool("LOTWITHCOST")) {
					double cost = getPrice(true);
					lot.setCost(cost);
					////j
					if(Param.bool("COST_WITH_REDUCTION")){
						Double redrate = getReductionRate(false);
						lot.setReduction_rate(redrate<0 ? 0. : redrate);
					}
				}
			}
		}
		return null;
	}

	private double getPrice(boolean showMessage) {
		String strCost = jTextFieldCost.getText();
		if (strCost == null || strCost.trim().equals("")) {
			jTextFieldCost.setText("0");
			return 0;
		}
		Double cost = 0.0;
		try {
			cost = Double.valueOf(strCost);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidpricepleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidprice"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return cost;
	}
	
	private double getReductionRate(boolean showMessage) {
		String strReducPlan = jTextFieldReductionRate.getText();
		if (strReducPlan == null || strReducPlan.trim().equals("")) {
			jTextFieldReductionRate.setText("0");
			return 0;
		}
		Double reducPlan = 0.0;
		try {
			reducPlan = Double.valueOf(strReducPlan);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidreductionrate"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidprice"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return reducPlan;
	}

	private double getTotalPrice(boolean showMessage) {
		String strCost = jTextFieldTotalCost.getText();
		if (strCost == null || strCost.trim().equals("")) {
			jTextFieldTotalCost.setText("0");
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidpricepleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidprice"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		Double cost = 0.0;
		try {
			cost = Double.valueOf(strCost);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidpricepleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidprice"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		if (cost == 0.0) {
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidpricepleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidprice"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return cost;
	}

	protected double getQty(boolean showMessage) {
		String strQty = jTextFieldQty.getText();
		if (strQty == null || strQty.trim().equals("")) {
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
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
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		if (qty <= 0 ) {
			if (showMessage)
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.medicalstockwardedit.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return qty;
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
		if (jComboBoxChargeType == null) {
			jComboBoxChargeType = new JComboBox();
			MedicaldsrstockmovTypeBrowserManager movMan = new MedicaldsrstockmovTypeBrowserManager();
			ArrayList<MovementType> movTypes = movMan.getMedicaldsrstockmovType();
			for (MovementType movType : movTypes) {
				if (movType.getType().equals("+")) //$NON-NLS-1$
					jComboBoxChargeType.addItem(movType);
			}
		}
		return jComboBoxChargeType;
	}

	protected double askCost() {
		String input = JOptionPane.showInputDialog(MovStockMultipleCharging.this,
				MessageBundle.getMessage("angal.medicalstock.multiplecharging.unitcost"), 0.); //$NON-NLS-1$
		double cost = 0.;
		if (input != null) {
			try {
				cost = Double.parseDouble(input);
				if (cost < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseinsertavalidvalue")); //$NON-NLS-1$
			}
		}
		return cost;
	}

	protected double askTotalCost() {
		String input = JOptionPane.showInputDialog(MovStockMultipleCharging.this,
				MessageBundle.getMessage("angal.medicalstock.multiplecharging.totalcost"), 0.); //$NON-NLS-1$
		double total = 0.;
		if (input != null) {
			try {
				total = Double.parseDouble(input);
				if (total < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseinsertavalidvalue")); //$NON-NLS-1$
			}
		}
		return total;
	}

	protected Lot askLot() {
		GregorianCalendar preparationDate = TimeTools.getServerDateTime();
		GregorianCalendar expiringDate = TimeTools.getServerDateTime();
		Lot lot = null;

		JTextField lotNameTextField = new JTextField(15);
		lotNameTextField.addAncestorListener(new RequestFocusListener());
		if (isAutomaticLot())
			lotNameTextField.setEnabled(false);
		TextPrompt suggestion = new TextPrompt(MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotid"), //$NON-NLS-1$
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
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotnumberabb"))); //$NON-NLS-1$
		panel.add(lotNameTextField);
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.preparationdate"))); //$NON-NLS-1$
		panel.add(preparationDateChooser);
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.expiringdate"))); //$NON-NLS-1$
		panel.add(expireDateChooser);

		int ok = JOptionPane.showConfirmDialog(MovStockMultipleCharging.this, panel,
				MessageBundle.getMessage("angal.medicalstock.multiplecharging.lotinformations"), //$NON-NLS-1$
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

	protected Lot chooseLot(Medical med) {
		MovStockInsertingManager movBrowser = new MovStockInsertingManager();
		ArrayList<Lot> lots = movBrowser.getLotByMedical(med);
		Lot lot = null;
		if (!lots.isEmpty()) {


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

	protected GregorianCalendar askExpiringDate() {
		GregorianCalendar date = TimeTools.getServerDateTime();
		JDateChooser expireDateChooser = new JDateChooser(new Date());
		{
			expireDateChooser.setDateFormatString(DATE_FORMAT_DD_MM_YYYY);
		}
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(new JLabel(MessageBundle.getMessage("angal.medicalstock.multiplecharging.expiringdate"))); //$NON-NLS-1$
		panel.add(expireDateChooser);

		int ok = JOptionPane.showConfirmDialog(MovStockMultipleCharging.this, panel,
				MessageBundle.getMessage("angal.medicalstock.multiplecharging.expiringdate"), //$NON-NLS-1$
				JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION) {
			date.setTime(expireDateChooser.getDate());
		}
		return date;
	}

	protected int askQuantity(Medical med) {
		String quantity = JOptionPane.showInputDialog(MovStockMultipleCharging.this,
				med.toString() + MessageBundle.getMessage("angal.medicalstock.multiplecharging.quantity"), 0); //$NON-NLS-1$
		int qty = 0;
		if (quantity != null) {
			try {
				qty = Integer.parseInt(quantity);
				if (qty == 0)
					return 0;
				if (qty < 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
						MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseinsertavalidvalue")); //$NON-NLS-1$
			}
		}
		return qty;
	}

	private JComboBox getJComboBoxSupplier() {
		if (jComboBoxSupplier == null) {
			jComboBoxSupplier = new JComboBox();
			jComboBoxSupplier.addItem(""); //$NON-NLS-1$
			SupplierOperations supOp = new SupplierOperations();
			ArrayList<Supplier> suppliers = (ArrayList<Supplier>) supOp.getList();
			for (Supplier sup : suppliers) {
				jComboBoxSupplier.addItem(sup);
			}
		}
		return jComboBoxSupplier;
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
			String lotName = "";
			GregorianCalendar dueDate = null;
			double cost = 0.0;
			double reducRate = 0.0;
			double reducCost = 0.0;
			if (lot != null) {
				lotName = lot.getCode();
				dueDate = lot.getDueDate();
				cost = lot.getCost();
				reducRate = lot.getReduction_rate();
				reducCost = cost - cost*(reducRate/100);
			}
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
				return format(dueDate);
			} else if (c == 8) {
				return cost;
			}else if (c == 9) {
				return reducRate+" %";
			} else if (c == 10) { 
				//return cost * qty;
				if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION"))
					return reducCost * qty;
				else
					return cost * qty;
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
			Lot lot = movement.getLot();
			if (c == 0) {
				String key = String.valueOf(value);
				if (medicalMap.containsKey(key)) {
					movement.setMedical(medicalMap.get(key));
					movements.set(r, movement);
				}
			} else if (c == 3) {
				movement.setQuantity((Integer) value);
			} else if (c == 4) {
				units.set(r, comboBox.getSelectedIndex());
			} else if (c == 6) {
				lot.setCode((String) value);
			} else if (c == 7) {
				try {
					lot.setDueDate(convertToDate((String) value));
				} catch (ParseException e) {
				}
			} else if (c == 8) {
				lot.setCost((Double) value);
			}
			movements.set(r, movement);
			fireTableDataChanged();
		}

	}

	private boolean checkAndPrepareMovements() {
		boolean ok = true;
		MovStockInsertingManager manager = new MovStockInsertingManager();

		// Check the Date
		GregorianCalendar thisDate = TimeTools.getServerDateTime();
		thisDate.setTime(jDateChooser.getDate());
		GregorianCalendar lastDate = manager.getLastMovementDate();
		if (lastDate != null && thisDate.compareTo(lastDate) < 0) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.datebeforelastmovement") //$NON-NLS-1$
							+ format(lastDate)
							+ MessageBundle.getMessage("angal.medicalstock.multiplecharging.notallowed")); //$NON-NLS-1$
			return false;
		}

		// Check the RefNo
		String refNo = jTextFieldReference.getText().trim();
		if (refNo.equals("")) { //$NON-NLS-1$
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseinsertareferencenumber")); //$NON-NLS-1$
			return false;
		} 
		else if (manager.refNoExists(refNo)) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this, MessageBundle
					.getMessage("angal.medicalstock.multiplecharging.theinsertedreferencenumberalreadyexists")); //$NON-NLS-1$
			return false;
		}

		// Check supplier
		Object supplier = jComboBoxSupplier.getSelectedItem();
		if (supplier instanceof String) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.pleaseselectasupplier")); //$NON-NLS-1$
			return false;
		}

		// Check and set all movements
		ArrayList<Movement> movements = model.getMovements();
		for (int i = 0; i < movements.size(); i++) {
			Movement mov = movements.get(i);
			Lot lot = mov.getLot();
			GregorianCalendar expiringDate = mov.getLot().getDueDate();
			if (expiringDate.compareTo(thisDate) < 0) {
				JOptionPane.showMessageDialog(MovStockMultipleCharging.this, MessageBundle
						.getMessage("angal.medicalstock.multiplecharging.expiringdateinthepastnotallowed")); //$NON-NLS-1$
				jTableMovements.getSelectionModel().setSelectionInterval(i, i);
				return false;
			}
			
			if (Param.bool("LOTWITHCOST")) {
				Double cost = lot.getCost();
				if (cost == null || cost.doubleValue() <= 0.) {
					JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
							MessageBundle.getMessage("angal.medicalstock.multiplecharging.zerocostsnotallowed")); //$NON-NLS-1$
					jTableMovements.getSelectionModel().setSelectionInterval(i, i);
					return false;
				}
			}
			mov.setDate(thisDate);
			mov.setRefNo(refNo + " " + formatDateTime(thisDate));
			mov.setType((MovementType) jComboBoxChargeType.getSelectedItem());
			mov.setSupplier(((Supplier) jComboBoxSupplier.getSelectedItem()));
			mov.getLot().setPreparationDate(thisDate);
		}
		return ok;
	}
	private String formatDateTime(GregorianCalendar time) {
		if (time == null)
			return MessageBundle.getMessage("angal.medicalstock.nodate");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YY_HH_MM);
		return sdf.format(time.getTime());
	}
	private boolean save() {
		boolean ok = true;
		MovStockInsertingManager movManager = new MovStockInsertingManager();
		ArrayList<Movement> movements = model.getMovements();
		if (movements.isEmpty()) {
			JOptionPane.showMessageDialog(MovStockMultipleCharging.this,
					MessageBundle.getMessage("angal.medicalstock.multiplecharging.noelementtosave")); //$NON-NLS-1$
			return false;
		}

		int movSize = movements.size();
		int index = movManager.newMultipleChargingMovements(movements);

		if (index < movSize) {
			jTableMovements.getSelectionModel().setSelectionInterval(index, index);
			ok = false;
		}
		return ok;
	}

	public String format(GregorianCalendar gc) {
		if (gc != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
			return sdf.format(gc.getTime());
		}
		return "";
	}

	public String format(GregorianCalendar gc, String format) {
		if (gc != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(gc.getTime());
		}
		return "";
	}

	public GregorianCalendar convertToDate(String string) throws ParseException {
		GregorianCalendar date = TimeTools.getServerDateTime();
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
			value = formatter.format((Number) value);
			setHorizontalAlignment(columnAlignment[column]);
			if (!columnEditable[column]) {
				cell.setBackground(Color.LIGHT_GRAY);
			}
			if (columnBold[column]) {
				cell.setFont(new Font(null, Font.BOLD, 12));
			}
			// And pass it on to parent class
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

	class StockMovModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<Lot> lotList;
		private ArrayList<Lot> initList = new ArrayList<Lot>();

		public StockMovModel(ArrayList<Lot> lots) {
			lotList = lots;
			initList.addAll(lotList);
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
			if (Param.bool("LOTWITHCOST")) {
				if (c == 4) {
					return MessageBundle.getMessage("angal.medicalstock.multiplecharging.cost"); //$NON-NLS-1$
				}
			}
			if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION")){
				if (c == 5) {
					return MessageBundle.getMessage("angal.medicalstock.multiplecharging.reductionrate"); //$NON-NLS-1$
				}
			}
			return ""; //$NON-NLS-1$
		}

		public int getColumnCount() {
			if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION"))
				return 6;
			if (Param.bool("LOTWITHCOST"))
				return 5;
			return 4;
		}

		public Object getValueAt(int r, int c) {
			Lot lot = lotList.get(r);
			if (c == -1) {
				return lot;
			} else if (c == 0) {
				return lot.getCode();
			} else if (c == 1) {
				return format(lot.getPreparationDate(), DATE_FORMAT_DDMMYYYY);
			} else if (c == 2) {
				return format(lot.getDueDate(), DATE_FORMAT_DDMMYYYY);
			} else if (c == 3) {
				return lot.getQuantity();
			} else if (c == 4) {
				return lot.getCost();
			}else if (c == 5) {
				return lot.getReduction_rate()+" %";
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

		public void filter(String search) {
			if (!search.trim().equals("")) {
				lotList.clear();
				for (Iterator<Lot> iterator = initList.iterator(); iterator.hasNext();) {
					Lot lot = (Lot) iterator.next();
					if (lot.getCode().equalsIgnoreCase(search)) {
						lotList.add(lot);
					}

				}
			} else {
				lotList.clear();
				lotList.addAll(initList);
			}

		}
	}
	
	private void updateTotal(){
		ArrayList<Movement> movements = model.getMovements();
		double totalCost = 0.0;
		double reducRate = 0.0;
		double cost = 0.0;
		for (Movement mov : movements) {
			if(Param.bool("LOTWITHCOST") && Param.bool("COST_WITH_REDUCTION")){
				reducRate = mov.getLot().getReduction_rate();
				cost = mov.getLot().getCost();
				cost = cost - cost*(reducRate/100);
				totalCost += mov.getQuantity() * cost;
			}else{
				totalCost += mov.getQuantity() * mov.getLot().getCost();
			}		
		}

		jLabelTotal.setText(MessageBundle.getMessage("angal.newbill.totalm") + ": "
				+ NumberFormat.getCurrencyInstance(new Locale("fr", "CM")).format(totalCost));
	}

	@Override
	public void supplierUpdated(AWTEvent e) {
		refreshJComboBoxSupplier();
	}

	@Override
	public void supplierInserted(AWTEvent e) {		
		refreshJComboBoxSupplier();		
	}
	
	private void refreshJComboBoxSupplier() {
		if (jComboBoxSupplier == null) {
			jComboBoxSupplier = new JComboBox();
		}else{
			jComboBoxSupplier.removeAllItems();
		}
		jComboBoxSupplier.addItem("");
		SupplierOperations supOp = new SupplierOperations();
		ArrayList<Supplier> suppliers = (ArrayList<Supplier>) supOp.getList();
		for (Supplier sup : suppliers) {
			jComboBoxSupplier.addItem(sup);
		}				
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
