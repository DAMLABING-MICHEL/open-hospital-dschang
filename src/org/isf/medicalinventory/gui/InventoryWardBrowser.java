package org.isf.medicalinventory.gui;

import org.isf.utils.jobjects.InventoryState;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryWardEdit.InventoryListener;
import org.isf.medicalinventory.manager.MedicalInventoryManager;
import org.isf.medicalinventory.model.MedicalInventory;

import org.isf.parameters.manager.Param;
import org.isf.utils.jobjects.InventoryType;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

import com.toedter.calendar.JDateChooser;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dialog.ModalExclusionType;

import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class InventoryWardBrowser extends ModalJFrame implements InventoryListener{
	
	private JDateChooser jCalendarTo;
	private JDateChooser jCalendarFrom;
	private GregorianCalendar dateFrom = TimeTools.getServerDateTime();
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private JLabel jLabelTo;
	private JLabel jLabelFrom;
	private JPanel panelHeader;
	private JPanel panelFooter;
	private JPanel panelContent;
	private JButton closeButton;
	private JButton initialStocksButton;
	private JButton newButton;
	private JButton updateButton;
	private JButton printButton;
	private JButton deleteButton;
	private JButton viewButton;
	private JScrollPane scrollPaneInventory;
	private JTable jTableInventory;
	private ArrayList<MedicalInventory> inventoryList;
	private String[] pColums = { 
			MessageBundle.getMessage("angal.inventory.referenceshow"),
		    MessageBundle.getMessage("angal.inventory.ward"),
			MessageBundle.getMessage("angal.common.date"), 
			MessageBundle.getMessage("angal.inventory.state"),
			MessageBundle.getMessage("angal.inventory.user") 
	};
	private int[] pColumwidth = { 150,150, 100, 100, 150};
	private JComboBox stateComboBox;
	private JLabel stateLabel;
	
	public InventoryWardBrowser() {		
		initComponents();	
		//modal exclude
		if(!Param.bool("WITHMODALWINDOW")){
			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		}
	}
	
	private void initComponents() {
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(850, 550));
		setLocationRelativeTo(null); // center
		setTitle(MessageBundle.getMessage("angal.inventory.managementwardtitle"));
		
		panelHeader = getPanelHeader();
		getContentPane().add(panelHeader, BorderLayout.NORTH);
		
		panelContent = getPanelContent();
		getContentPane().add(panelContent, BorderLayout.CENTER);
		
		panelFooter = getPanelFooter();
		getContentPane().add(panelFooter, BorderLayout.SOUTH);
		
		ajustWidth();
		
		addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent e) {
				//to free memory
				if(inventoryList!=null){
					inventoryList.clear();
				}
				dispose();
			}			
		});
		//pack();
	}
	
	private JPanel getPanelHeader(){
		if(panelHeader==null){
			panelHeader = new JPanel();
			panelHeader.setBorder(new EmptyBorder(5, 0, 0, 5));
			GridBagLayout gbl_panelHeader = new GridBagLayout();
			gbl_panelHeader.columnWidths = new int[]{65, 103, 69, 105, 77, 146, 0};
			gbl_panelHeader.rowHeights = new int[]{32, 0};
			gbl_panelHeader.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
			gbl_panelHeader.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panelHeader.setLayout(gbl_panelHeader);
			GridBagConstraints gbc_jLabelFrom = new GridBagConstraints();
			gbc_jLabelFrom.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelFrom.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelFrom.gridx = 0;
			gbc_jLabelFrom.gridy = 0;
			panelHeader.add(getJLabelFrom(), gbc_jLabelFrom);
			GridBagConstraints gbc_jCalendarFrom = new GridBagConstraints();
			gbc_jCalendarFrom.fill = GridBagConstraints.HORIZONTAL;
			gbc_jCalendarFrom.insets = new Insets(0, 0, 0, 5);
			gbc_jCalendarFrom.gridx = 1;
			gbc_jCalendarFrom.gridy = 0;
			panelHeader.add(getJCalendarFrom(), gbc_jCalendarFrom);
			GridBagConstraints gbc_jLabelTo = new GridBagConstraints();
			gbc_jLabelTo.fill = GridBagConstraints.HORIZONTAL;
			gbc_jLabelTo.insets = new Insets(0, 0, 0, 5);
			gbc_jLabelTo.gridx = 2;
			gbc_jLabelTo.gridy = 0;
			panelHeader.add(getJLabelTo(), gbc_jLabelTo);
			GridBagConstraints gbc_jCalendarTo = new GridBagConstraints();
			gbc_jCalendarTo.fill = GridBagConstraints.HORIZONTAL;
			gbc_jCalendarTo.insets = new Insets(0, 0, 0, 5);
			gbc_jCalendarTo.gridx = 3;
			gbc_jCalendarTo.gridy = 0;
			panelHeader.add(getJCalendarTo(), gbc_jCalendarTo);
			GridBagConstraints gbc_stateLabel = new GridBagConstraints();
			gbc_stateLabel.fill = GridBagConstraints.HORIZONTAL;
			gbc_stateLabel.insets = new Insets(0, 0, 0, 5);
			gbc_stateLabel.gridx = 4;
			gbc_stateLabel.gridy = 0;
			panelHeader.add(getStateLabel(), gbc_stateLabel);
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 5;
			gbc_comboBox.gridy = 0;
			panelHeader.add(getComboBox(), gbc_comboBox);
		}
		return panelHeader;
	}
	private JPanel getPanelContent(){
		if(panelContent==null){
			panelContent = new JPanel();
			GridBagLayout gbl_panelContent = new GridBagLayout();
			gbl_panelContent.columnWidths = new int[]{452, 0};
			gbl_panelContent.rowHeights = new int[]{402, 0};
			gbl_panelContent.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_panelContent.rowWeights = new double[]{1.0, Double.MIN_VALUE};
			panelContent.setLayout(gbl_panelContent);
			GridBagConstraints gbc_scrollPaneInventory = new GridBagConstraints();
			gbc_scrollPaneInventory.fill = GridBagConstraints.BOTH;
			gbc_scrollPaneInventory.gridx = 0;
			gbc_scrollPaneInventory.gridy = 0;
			panelContent.add(getScrollPaneInventory(), gbc_scrollPaneInventory);
		}
		return panelContent;
	}
	private JPanel getPanelFooter(){
		if(panelFooter==null){
			panelFooter = new JPanel();
			panelFooter.add(getNewButton());
			panelFooter.add(getViewButton());
			panelFooter.add(getUpdateButton());
			panelFooter.add(getDeleteButton());
			panelFooter.add(getInitialStocksButton());
			panelFooter.add(getCloseButton());
		}
		return panelFooter;
	}
	private JDateChooser getJCalendarFrom() {
		if (jCalendarFrom == null) {
			dateFrom.set(GregorianCalendar.HOUR_OF_DAY, 0);
			dateFrom.set(GregorianCalendar.MINUTE, 0);
			dateFrom.set(GregorianCalendar.SECOND, 0);
			
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
					if(inventoryList!=null) inventoryList.clear();
					jTableInventory.setModel(new InventoryBrowsingModel());
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
					if(inventoryList!=null) inventoryList.clear();
					jTableInventory.setModel(new InventoryBrowsingModel());
				}
			});
		}
		return jCalendarTo;
	}
	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelTo.setText(MessageBundle.getMessage("angal.billbrowser.to")); //$NON-NLS-1$
		}
		return jLabelTo;
	}
	private JLabel getJLabelFrom() {
		if (jLabelFrom == null) {
			jLabelFrom = new JLabel();
			jLabelFrom.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelFrom.setText(MessageBundle.getMessage("angal.billbrowser.from")); //$NON-NLS-1$
		}
		return jLabelFrom;
	}
	
	private JButton getNewButton() {
		newButton = new JButton(MessageBundle.getMessage("angal.inventory.new"));
		newButton.setMnemonic(KeyEvent.VK_N);
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InventoryWardEdit inventoryEdit = new InventoryWardEdit();
				InventoryWardEdit.addInventoryListener(InventoryWardBrowser.this);
				if(Param.bool("WITHMODALWINDOW")){
					inventoryEdit.showAsModal(InventoryWardBrowser.this);
				}else{
					inventoryEdit.show(InventoryWardBrowser.this);
				}
			}
		});
		return newButton;
	}
	
	private JButton getViewButton() {
		viewButton = new JButton(MessageBundle.getMessage("angal.inventory.view"));
		viewButton.setMnemonic(KeyEvent.VK_V);
		viewButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				MedicalInventory inventory = new MedicalInventory();
				int selecedRow = jTableInventory.getSelectedRow();
				if(selecedRow >-1 ){
					inventory = inventoryList.get(selecedRow);
					InventoryWardEdit inventoryEdit = new InventoryWardEdit(inventory,"view");
					InventoryWardEdit.addInventoryListener(InventoryWardBrowser.this);
					if(Param.bool("WITHMODALWINDOW")){
						inventoryEdit.showAsModal(InventoryWardBrowser.this);
					}else{
						inventoryEdit.show(InventoryWardBrowser.this);
					}
				}
				else{
					JOptionPane.showMessageDialog(InventoryWardBrowser.this,
							MessageBundle.getMessage("angal.inventory.pleaseselectarow"), 
							MessageBundle.getMessage("angal.inventoryoperation.title"), 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		});
		return viewButton;
	}
	
	private JButton getUpdateButton() {
		updateButton = new JButton(MessageBundle.getMessage("angal.inventory.update"));
		updateButton.setMnemonic(KeyEvent.VK_M);
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MedicalInventory inventory = new MedicalInventory();
				int selecedRow = jTableInventory.getSelectedRow();
				if(selecedRow >-1 ){
					inventory = inventoryList.get(selecedRow);
					if(inventory.getState().equals("3")){
						JOptionPane.showMessageDialog(InventoryWardBrowser.this,
								MessageBundle.getMessage("angal.inventory.noteditable"), 
								MessageBundle.getMessage("angal.inventoryoperation.title"), 
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if(inventory.getState().equals("2")){
						JOptionPane.showMessageDialog(InventoryWardBrowser.this,
								MessageBundle.getMessage("angal.inventorycanceled.noteditable"), 
								MessageBundle.getMessage("angal.inventoryoperation.title"), 
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					InventoryWardEdit inventoryEdit = new InventoryWardEdit(inventory,"update");
					InventoryWardEdit.addInventoryListener(InventoryWardBrowser.this);
					if(Param.bool("WITHMODALWINDOW")){
						inventoryEdit.showAsModal(InventoryWardBrowser.this);
					}else{
						inventoryEdit.show(InventoryWardBrowser.this);
					}
				}
				else{
					JOptionPane.showMessageDialog(InventoryWardBrowser.this,
							MessageBundle.getMessage("angal.inventory.pleaseselectarow"), 
							MessageBundle.getMessage("angal.inventoryoperation.title"), 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		});
		return updateButton;
	}
	
	private JButton getPrintButton() {
		printButton = new JButton(MessageBundle.getMessage("angal.inventory.print"));
		printButton.setMnemonic(KeyEvent.VK_P);
		printButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
			}
		});
		return printButton;
	}
	private JButton getDeleteButton() {
		deleteButton = new JButton(MessageBundle.getMessage("angal.inventory.delete"));
		deleteButton.setMnemonic(KeyEvent.VK_D);
		deleteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
					int indexSelected = jTableInventory.getSelectedRow();
					if(indexSelected > -1 ){
						int response = JOptionPane.showConfirmDialog(InventoryWardBrowser.this,
								MessageBundle.getMessage("angal.inventory.deleteconfirm"), 
								MessageBundle.getMessage("angal.inventoryoperation.title"), 
								JOptionPane.OK_CANCEL_OPTION); 
						if(response==JOptionPane.OK_OPTION){
							MedicalInventory inventory = inventoryList.get(indexSelected);
							if(inventory.getState().equals("3")){
								JOptionPane.showMessageDialog(InventoryWardBrowser.this,
										MessageBundle.getMessage("angal.inventory.notDeleteable"), 
										MessageBundle.getMessage("angal.inventoryoperation.title"), 
										JOptionPane.INFORMATION_MESSAGE);
								return;
							}
							MedicalInventoryManager manager = new MedicalInventoryManager();
							if(manager.deleteMedicalInventory(inventory.getId())){
								JOptionPane.showMessageDialog(InventoryWardBrowser.this,
										MessageBundle.getMessage("angal.inventory.deletedsucces"), 
										MessageBundle.getMessage("angal.inventoryoperation.title"), 
										JOptionPane.INFORMATION_MESSAGE);
								if(inventoryList!=null) inventoryList.clear();
								jTableInventory.setModel(new InventoryBrowsingModel());
							}
							else{
								JOptionPane.showMessageDialog(InventoryWardBrowser.this,
										MessageBundle.getMessage("angal.inventory.deletederror"), 
										MessageBundle.getMessage("angal.inventoryoperation.title"), 
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}else{
						JOptionPane.showMessageDialog(InventoryWardBrowser.this,
								MessageBundle.getMessage("angal.inventory.pleaseselectarow"), 
								MessageBundle.getMessage("angal.inventoryoperation.title"), 
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
			}
		});
		return deleteButton;
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
	
	private JButton getInitialStocksButton() {
		initialStocksButton = new JButton(MessageBundle.getMessage("angal.inventory.initialstocks"));
		initialStocksButton.setMnemonic(KeyEvent.VK_N);
		initialStocksButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WardInitialStocks wardInitialStocks = new WardInitialStocks();
				
				if(Param.bool("WITHMODALWINDOW")){
					wardInitialStocks.showAsModal(InventoryWardBrowser.this);
				}else{
					wardInitialStocks.show(InventoryWardBrowser.this);
				}//
			}
		});
		return initialStocksButton;
	}

	private JScrollPane getScrollPaneInventory() {
		if (scrollPaneInventory == null) {
			scrollPaneInventory = new JScrollPane();
			scrollPaneInventory.setViewportView(getJTableInventory());
		}
		return scrollPaneInventory;
	}
	
	private JTable getJTableInventory() {
		if (jTableInventory == null) {
			jTableInventory = new JTable();
			jTableInventory.setFillsViewportHeight(true);
			jTableInventory.setModel(new InventoryBrowsingModel());
		}
		return jTableInventory;
	}
	
	
	class InventoryBrowsingModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public InventoryBrowsingModel() {
			MedicalInventoryManager manager = new MedicalInventoryManager();	
			String state = stateComboBox.getSelectedIndex() > 0 ? stateComboBox.getSelectedIndex()+"" : "";
			inventoryList = manager.getMedicalInventory(dateFrom, dateTo, state, InventoryType.WARD);	
		}
		


		public Class<?> getColumnClass(int c) {
			if (c == 0) {
				return String.class;
			}else if (c == 1) {
				return String.class;
			} else if (c == 2) {
				return String.class;
			} else if (c == 3) {
				return String.class;
			} else if (c == 4) {
				return String.class;
			}
			return null;
		}

		public int getRowCount() {
			if (inventoryList == null)
				return 0;
			return inventoryList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			MedicalInventory medInvt = inventoryList.get(r);
			
			if (c == -1) {
				return medInvt;
			} else if (c == 0) {
				return medInvt.getInventoryReference();
			}else if (c == 1) {
				Ward ward = new WardBrowserManager().getWard(medInvt.getWard());
				return ward!=null? ward.getDescription():"";
			} else if (c == 2) {
				return formatDateTime(medInvt.getInventoryDate());//date
			}
			else if (c == 3) {
				String state = medInvt.getState();
				if(state==null || state.equals("")) return "";
				try{
					for (InventoryState.State currentState : InventoryState.State.values()) {
						if(medInvt.getState().equals(currentState.getCode())){
							return MessageBundle.getMessage(currentState.getLabel());
						}
					}
					return "";
				}catch(Exception ex){
					return "";
				}
			} else if (c == 4) {
				return medInvt.getUser();
			} 
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}
	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");  //$NON-NLS-1$
		return format.format(time.getTime());
	}

	@Override
	public void InventoryUpdated(AWTEvent e) {
		if(inventoryList!=null) inventoryList.clear();
		jTableInventory.setModel(new InventoryBrowsingModel());
	}

	@Override
	public void InventoryInserted(AWTEvent e) {
		if(inventoryList!=null) inventoryList.clear();
		jTableInventory.setModel(new InventoryBrowsingModel());
	}
	
	private void ajustWidth(){
		for (int i=0;i<pColumwidth.length; i++){
			jTableInventory.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
		}
	}

	@Override
	public void InventoryValidated(AWTEvent e) {
		if(inventoryList!=null) inventoryList.clear();
		jTableInventory.setModel(new InventoryBrowsingModel());	
	}

	@Override
	public void InventoryCancelled(AWTEvent e) {
		if(inventoryList!=null) inventoryList.clear();
		jTableInventory.setModel(new InventoryBrowsingModel());	
	}
	private JComboBox getComboBox() {
		if (stateComboBox == null) {
			stateComboBox = new JComboBox();
			stateComboBox.addItem("");
			for(InventoryState.State currentState : InventoryState.State.values())
		    {
				stateComboBox.addItem(MessageBundle.getMessage(currentState.getLabel()));
		    }
			stateComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(inventoryList!=null) inventoryList.clear();
					jTableInventory.setModel(new InventoryBrowsingModel());	
				}
			});			
		}
		return stateComboBox;
	}
	private JLabel getStateLabel() {
		if (stateLabel == null) {
			stateLabel = new JLabel(MessageBundle.getMessage("angal.inventory.state"));
			stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return stateLabel;
	}
}
