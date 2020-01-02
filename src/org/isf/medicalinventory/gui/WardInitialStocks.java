/**
 * 11-dic-2005
 * author bob
 */
package org.isf.medicalinventory.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryWardEdit.InventoryListener;
import org.isf.medicalinventory.gui.InventoryWardEdit.InventoryRowModel;
import org.isf.medicalinventory.manager.MedicalInventoryManager;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.stat.manager.GenericReportMedicals;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.InventoryState;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;

/**
 * This class shows a complete extended list of medical drugs,
 * supplies-sundries, diagnostic kits -reagents, laboratory chemicals. It is
 * possible to filter data with a selection combo box and edit-insert-delete
 * records
 * 
 * @author bob modified by alex: - product code - pieces per packet
 * 
 */

public class WardInitialStocks extends ModalJFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static EventListenerList InitialStockListeners = new EventListenerList();
	
	public interface InitialStockListener extends EventListener {
        public void InitialStockUpdated(AWTEvent e);
        public void InitialStockInserted(AWTEvent e);
        public void InitialStockValidated(AWTEvent e);
        public void InitialStockCancelled(AWTEvent e);
    }
	
	public static void addInitialStockListener(InitialStockListener l) {
		InitialStockListeners.add(InitialStockListener.class, l);
    }

    public static void removeInitialStockListener(InitialStockListener listener) {
    	InitialStockListeners.remove(InitialStockListener.class, listener);
    }
	
    private void fireInitialStockInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
        	
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
			
        EventListener[] listeners = InitialStockListeners.getListeners(InitialStockListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((InitialStockListener)listeners[i]).InitialStockInserted(event);
    }
    
    private void fireInitialStockUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = InitialStockListeners.getListeners(InitialStockListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((InitialStockListener)listeners[i]).InitialStockUpdated(event);
    }
    private void fireInitialStockValidated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};
			
        EventListener[] listeners = InitialStockListeners.getListeners(InitialStockListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((InitialStockListener)listeners[i]).InitialStockValidated(event);
    }
    private void fireInitialStockCancelled() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = InitialStockListeners.getListeners(InitialStockListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((InitialStockListener)listeners[i]).InitialStockCancelled(event);
    }
    
   /* 
	public void medicalInserted() {
		wardDrugs.add(0, medical);
		((DrugsModel) jTableDrugs.getModel()).fireTableDataChanged();
		jTableDrugs.updateUI();
		if (jTableDrugs.getRowCount() > 0)
			jTableDrugs.setRowSelectionInterval(0, 0);
		repaint();
	}

	public void medicalUpdated() {
		wardDrugs.set(selectedrow, medical);
		((DrugsModel) jTableDrugs.getModel()).fireTableDataChanged();
		jTableDrugs.updateUI();
		if ((jTableDrugs.getRowCount() > 0) && selectedrow > -1)
			jTableDrugs.setRowSelectionInterval(selectedrow, selectedrow);
		repaint();

	}
*/
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 400;
	private JComboBox jComboBoxWard;
	private int pfrmWidth;
	private int pfrmHeight;
	private int selectedrow;
	private JLabel selectlabel;
	private JComboBox pbox;
	private JButton saveButton;
	//private MedicalWard medicine;
	private ArrayList<Ward> wardList;
	private Ward wardSelected;
	private boolean added = false;
	private String[] columsDrugs = { 
			MessageBundle.getMessage("angal.medicalstockward.medical"), 
			MessageBundle.getMessage("angal.inventory.initialstocks")};
	private int[] columWidthDrugs = { 100, 40 };
	private boolean[] columsResizableDrugs = { false, false };
	private boolean[] columnEditable = { false, true};	
	private JTextField jTetFieldEditor;
	ArrayList<MedicalWard> medSearch;
	private ArrayList<MedicalWard> wardDrugs;
	private MedicalWard medicalWard;
	private JTable jTableDrugs;
	private final JFrame me;
	private String lastKey = "";

	private String pSelection;
	private JTextField searchTextField;
	
	public WardInitialStocks() {
		me = this;
		setTitle(MessageBundle.getMessage("angal.medicals.pharmaceuticalbrowsing"));
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		pfrmWidth = 1040;
		pfrmHeight = screensize.height / 2;
		setBounds((screensize.width - pfrmWidth) / 2, screensize.height / 4, pfrmWidth, pfrmHeight);
		/*
		jTableDrugs = new JTable(new DrugsModel());
		jTableDrugs.setAutoCreateRowSorter(true);
		jTableDrugs.updateUI();
*/
		JPanel panelSearch = new JPanel();
		panelSearch.setBorder(new EmptyBorder(4, 0, 4, 0));
		getContentPane().add(panelSearch, BorderLayout.NORTH);
		GridBagLayout gbl_panelSearch = new GridBagLayout();
		gbl_panelSearch.columnWidths = new int[]{252, -63, 166, 0, 144, 0};
		gbl_panelSearch.rowHeights = new int[]{20, 0};
		gbl_panelSearch.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSearch.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelSearch.setLayout(gbl_panelSearch);
		
		searchTextField = new JTextField();
		searchTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterMedicine();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterMedicine();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterMedicine();
			}
		});	
		
		GridBagConstraints gbc_wardsCombobox = new GridBagConstraints();
		gbc_wardsCombobox.anchor = GridBagConstraints.WEST;
		gbc_wardsCombobox.insets = new Insets(0, 0, 0, 5);
		gbc_wardsCombobox.gridx = 1;
		gbc_wardsCombobox.gridy = 0;
		panelSearch.add(getJComboBoxWard(), gbc_wardsCombobox);
		
		JLabel wardLabel = new JLabel(MessageBundle.getMessage("angal.admission.wards"));
		GridBagConstraints gbc_wardLabel = new GridBagConstraints();
		gbc_wardLabel.anchor = GridBagConstraints.WEST;
		gbc_wardLabel.insets = new Insets(0, 0, 0, 5);
		gbc_wardLabel.gridx = 1;
		gbc_wardLabel.gridy = 0;
		panelSearch.add(wardLabel, gbc_wardLabel);
		
		JLabel searchLabel = new JLabel(MessageBundle.getMessage("angal.medicals.find"));
		GridBagConstraints gbc_searchLabel = new GridBagConstraints();
		gbc_searchLabel.anchor = GridBagConstraints.WEST;
		gbc_searchLabel.insets = new Insets(0, 0, 0, 5);
		gbc_searchLabel.gridx = 1;
		gbc_searchLabel.gridy = 0;
		panelSearch.add(searchLabel, gbc_searchLabel);

		GridBagConstraints gbc_searchTextField = new GridBagConstraints();
		gbc_searchTextField.insets = new Insets(0, 0, 0, 5);
		gbc_searchTextField.anchor = GridBagConstraints.NORTHWEST;
		gbc_searchTextField.gridx = 2;
		gbc_searchTextField.gridy = 0;
		panelSearch.add(searchTextField, gbc_searchTextField);
		searchTextField.setColumns(20);
		
		JButton btnPrint = new JButton(MessageBundle.getMessage("angal.medicals.printlist"));
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//ask for medicals with empty stock	
					Double min_qty =1.0;
					int response = JOptionPane.showConfirmDialog(null,
							MessageBundle.getMessage("angal.medicalslistask.askforemptystock"),
							MessageBundle.getMessage("angal.medicalslistask.title"),
							JOptionPane.YES_NO_CANCEL_OPTION);
					switch (response) {
					case JOptionPane.OK_OPTION:
						min_qty = 0.0;
						break;
					case JOptionPane.NO_OPTION:
						min_qty = 1.0;
						break;
					case JOptionPane.CANCEL_OPTION:
						return;										
					default:
						return;										
				}															
				String codetype = "all";
				try{
					MedicalType elem = (MedicalType)pbox.getSelectedItem();
					codetype = elem.getCode();
				}catch (Exception e) {}
				new GenericReportMedicals(searchTextField.getText(), codetype, "medicalListReport", min_qty);
			}
		});
		GridBagConstraints gbc_btnPrint = new GridBagConstraints();
		gbc_btnPrint.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPrint.gridx = 4;
		gbc_btnPrint.gridy = 0;
		panelSearch.add(btnPrint, gbc_btnPrint);
		getContentPane().add(new JScrollPane(getJTableDrugs()), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		selectlabel = new JLabel(MessageBundle.getMessage("angal.medicals.selecttype"));
		buttonPanel.add(selectlabel);
		
		MedicalTypeBrowserManager manager = new MedicalTypeBrowserManager();
		pbox = new JComboBox();
		pbox.addItem(MessageBundle.getMessage("angal.medicals.allm"));
		ArrayList<MedicalType> type = manager.getMedicalType(); // for
																// efficiency in
																// the sequent
																// for
		for (MedicalType elem : type) {
			pbox.addItem(elem);
		}
		
		JButton buttonNew = new JButton(MessageBundle.getMessage("angal.common.new"));
		buttonNew.setMnemonic(KeyEvent.VK_N);
		buttonNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				medicalWard = new MedicalWard(null, 0.0);
				WardInitialStockEdit editrecord = new WardInitialStockEdit(medicalWard, (Ward)jComboBoxWard.getSelectedItem(), true, me);
				editrecord.setVisible(true);
				new DrugsModel();
				jTableDrugs.updateUI();
			}
		});
		if (MainMenu.checkUserGrants("btnpharmaceuticalnew"))
			buttonPanel.add(buttonNew);

		JButton buttonSave = new JButton(MessageBundle.getMessage("angal.common.save"));
		buttonSave.setMnemonic(KeyEvent.VK_N);
		buttonSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				MovWardBrowserManager wardManag = new MovWardBrowserManager();
				if(wardManag.updateMedicalsWard(medSearch, (Ward)jComboBoxWard.getSelectedItem())){
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.initialstock.updated")); 
				}else{
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.initialstock.noupdated")); 
				}
				return;
			}
		});
		if (MainMenu.checkUserGrants("btnpharmaceuticalnew"))
			buttonPanel.add(buttonSave);
		
		JButton buttonClose = new JButton(MessageBundle.getMessage("angal.common.close"));
		buttonClose.setMnemonic(KeyEvent.VK_C);
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int input = JOptionPane.showConfirmDialog(null, 
						MessageBundle.getMessage("angal.initialstock.confirmclose"), "",JOptionPane.YES_NO_CANCEL_OPTION);
				if(input == 0)
					dispose();
			}
		});
		buttonPanel.add(buttonClose);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent e) {
				if(medSearch!=null)	medSearch.clear();
				if(wardDrugs!=null) wardDrugs.clear();
				dispose();
			}			
		});
		
		setVisible(true);
	}
	
	class DrugsModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DrugsModel() {
			MovWardBrowserManager wardManag = new MovWardBrowserManager();
			if(wardSelected == null){
				String pharmaciCode = "P";
				wardDrugs = wardManag.getMedicalsWardWithPrice(pharmaciCode);
			}else{
				wardDrugs = wardManag.getMedicalsWardWithPrice(wardSelected.getCode());
			}
			medSearch = wardDrugs;
		}

		public Class<?> getColumnClass(int c) {
			if (c == 0) {
				return String.class;
			} else if (c == 1) {
				return Double.class;
			}
			return null;
		}
		public int getRowCount() {
			if (medSearch == null) return 0;
			return medSearch.size();
		} 
		public Object getValueAt(int r, int c) {
			
			MedicalWard wardDrug = medSearch.get(r);
			if (c == -1) {
				return wardDrug;
			}
			if (c == 0) {	
				return wardDrug.getMedical()==null?"":wardDrug.getMedical().getDescription();
			}
			if (c == 1) {
				return wardDrug.getInitialstock();
			}
			return null;
		}
		
		public String getColumnName(int c) {
			return columsDrugs[c];
		}

		public int getColumnCount() {
			return columsDrugs.length;
		}

		public boolean isCellEditable(int Index, int columnIndex) {
			return columnEditable[columnIndex];
		}
		
	
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	    {
			//System.out.println(medSearch.size());
			MedicalWard row = medSearch.get(rowIndex);
			//System.out.println("Colomnnn: " + rowIndex);
			
	        if(columnIndex == 1) {
	        	Double val = 0.0;
	        	try{
	        		val = Double.parseDouble(aValue.toString());
	        	}
	        	catch (NumberFormatException e) {			
	        		val=0.0;
				}
	            row.setInitialstock(val);
	            medSearch.set(rowIndex, row);
	        }        
	    }
	}
/*
	class ColorTableCellRenderer extends DefaultTableCellRenderer {
		 
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.GREEN);
			MedicalWard med = medSearch.get(row);
			if (med.getInitialstock() == 0.0)
				cell.setForeground(Color.ORANGE);
		
			return cell;
		}
	}
	*/
	private void filterMedicine() {
		String s = searchTextField.getText();
		s.trim();
		
		medSearch = new ArrayList<MedicalWard>();

		for (MedicalWard med : wardDrugs) {
			if (!s.equals("")) {
				
				String name = med.getSearchString();
				if (name.toLowerCase().contains(s.toLowerCase())){
					medSearch.add(med);
				}
					
			} else {
				medSearch.add(med);
			}
		}
		jTableDrugs.updateUI();
		searchTextField.requestFocus();
	}
	
	private JComboBox getJComboBoxWard() {
		Ward wrd = new Ward();
		if (jComboBoxWard == null) {
			jComboBoxWard = new JComboBox();
			WardBrowserManager wardManager = new WardBrowserManager();
			wardList = wardManager.getWards();
			jComboBoxWard.addItem(MessageBundle.getMessage("angal.medicalstockward.selectaward"));
			
			for (Ward ward : wardList) {
				if (ward.isPharmacy()){
					jComboBoxWard.addItem(ward);
					if(ward.getCode().equals("P")) wrd = ward;
				}
			}
			jComboBoxWard.setBorder(null);
			jComboBoxWard.setBounds(15, 14, 122, 24);
			jComboBoxWard.setSelectedItem(wrd);
			jComboBoxWard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Object ward = jComboBoxWard.getSelectedItem();
					if (ward instanceof Ward) {
						wardSelected = (Ward) ward;
						if (wardSelected != null) {
							jTableDrugs.setModel(new DrugsModel());
						}
						validate();
						repaint();
					}
				}
			});
		}
		return jComboBoxWard;
	}

	private JTable getJTableDrugs() {	
		if (jTableDrugs == null) {
			jTableDrugs = new JTable();
			jTetFieldEditor = new JTextField();
			jTableDrugs.setFillsViewportHeight(true);
			jTableDrugs.setModel(new DrugsModel());
			jTableDrugs.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					
					if(!e.getValueIsAdjusting()){
						jTableDrugs.editCellAt(jTableDrugs.getSelectedRow(), 1); 
						jTetFieldEditor.selectAll();
					}
				}
			});
			jTableDrugs.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {

				}
			});
			DefaultCellEditor cellEditor = new DefaultCellEditor(jTetFieldEditor);
			jTableDrugs.setDefaultEditor(Double.class, cellEditor);	
		}
		return jTableDrugs;
	}
}
