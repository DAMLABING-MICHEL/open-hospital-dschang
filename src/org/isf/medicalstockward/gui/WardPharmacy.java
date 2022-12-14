package org.isf.medicalstockward.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryBrowser;
import org.isf.medicalinventory.gui.InventoryWardEdit;
import org.isf.medicalinventory.gui.InventoryWardEdit.InventoryListener;
import org.isf.medicals.gui.MedicalBrowser;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.serviceprinting.manager.PrintManager;
import org.isf.serviceprinting.manager.WardEntryItems;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.utils.time.TimeTools;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;

import com.toedter.calendar.JDateChooser;

public class WardPharmacy extends ModalJFrame implements 
	WardPharmacyEdit.MovementWardListeners, 
	WardPharmacyNew.MovementWardListeners, 
	WardPharmacyRectify.MovementWardListeners,
	InventoryListener{

	public void movementInserted(AWTEvent e) {
		jTableOutcomes.setModel(new OutcomesModel());
		jTableDrugs.setModel(new DrugsModel());
		//jTabbedPaneWard.setSelectedComponent(jScrollPaneOutcomes);
	}

	public void movementUpdated(AWTEvent e) {
		jTableOutcomes.setModel(new OutcomesModel());
		jTableDrugs.setModel(new DrugsModel());
		//jTabbedPaneWard.setSelectedComponent(jScrollPaneOutcomes);
	}
	
	private static final long serialVersionUID = 1L;
	private JComboBox jComboBoxWard;
	private JLabel jLabelWard;
	private JLabel jLabelFrom;
	private JLabel jLabelTo;
	private JTable jTableOutcomes;
	private JPanel jPanelWardAndRange;
	private JPanel jPanelButtons;
	private JScrollPane jScrollPaneOutcomes;
	private JPanel jPanelCentral;
	private JPanel jPanelFilter;
	private JPanel jAgePanel;
	private JPanel sexPanel;
	private JRadioButton radiom;
	private JRadioButton radiof;
	private JRadioButton radioa;
	private JPanel jWeightPanel;
	private VoLimitedTextField jAgeFromTextField;
	private VoLimitedTextField jAgeToTextField;
	private VoLimitedTextField jWeightFromTextField;
	private VoLimitedTextField jWeightToTextField;
	private JComboBox jComboBoxTypes;
	private JComboBox jComboBoxMedicals;
	private JButton filterButton;
	private JButton resetButton;
	private JLabel rowCounterOutcomes;
	private JLabel rowCounterIncomes;
	private JTabbedPane jTabbedPaneWard;
	private JTable jTableDrugs;
	private JScrollPane jScrollPaneDrugs;
	private JTable jTableIncomes;
	private JScrollPane jScrollPaneIncomes;
	private JPanel jPanelWard;
	private JButton jButtonNew;
	private JPanel jPanelRange;
	private JButton jButtonClose;
	// private JButton jButtonDelete;
	private JButton jButtonEdit;
	private JDateChooser jCalendarFrom;
	private GregorianCalendar dateFrom = TimeTools.getServerDateTime();
	private GregorianCalendar dateTo = TimeTools.getServerDateTime();
	private JDateChooser jCalendarTo;
	private DefaultTableModel modelIncomes;
	private DefaultTableModel modelOutcomes;
	private DefaultTableModel modelDrugs;
	private ArrayList<Ward> wardList;
	private Ward wardSelected;
	private MovementWard movSelected;
	private boolean added = false;
	private String[] columsIncomes = { MessageBundle.getMessage("angal.common.date"), MessageBundle.getMessage("angal.medicalstockward.medical"),
			MessageBundle.getMessage("angal.medicalstockward.quantity") };
	private boolean[] columsResizableIncomes = { false, true, false };
	private int[] columWidthIncomes = { 150, 320, 200 };
	private String[] columsOutcomes = { MessageBundle.getMessage("angal.common.date"), MessageBundle.getMessage("angal.medicalstockward.patient"),
			MessageBundle.getMessage("angal.medicalstockward.age"), MessageBundle.getMessage("angal.medicalstockward.sex"), MessageBundle.getMessage("angal.medicalstockward.weight"),
			MessageBundle.getMessage("angal.medicalstockward.medical"), MessageBundle.getMessage("angal.medicalstockward.quantity") };
	private boolean[] columsResizableOutcomes = { false, false, false, false, false, true, false };
	private int[] columWidthOutcomes = { 150, 150, 50, 50, 50, 220, 100 };
	private String[] columsDrugs = { MessageBundle.getMessage("angal.medicalstockward.medical"), MessageBundle.getMessage("angal.medicalstockward.quantity"), "Last Price" };
	private boolean[] columsResizableDrugs = { true, false, true };
	private int[] columWidthDrugs = { 350, 100, 100 };
	private final int filterWidth = 250;
	private final int filterSpacing = 5;
	private String rowCounterOutcomesText = MessageBundle.getMessage("angal.medicalstockward.outcomes") + ": ";
	private String rowCounterIncomesText = MessageBundle.getMessage("angal.medicalstockward.incomes") + ": ";
	private int ageFrom;
	private int ageTo;
	private int weightFrom;
	private int weightTo;
	private boolean editAllowed;
	private JButton jPrintTableButton = null;
	private JButton jExportToExcelButton = null;
	private JButton jRectifyButton = null;

	/*
	 * Managers and datas
	 */
	private MovBrowserManager movManager = new MovBrowserManager();
	private ArrayList<Movement> listMovementCentral = new ArrayList<Movement>();
	private ArrayList<WardEntryItems> list1MovementIn = new ArrayList<WardEntryItems>();
	private MovWardBrowserManager wardManager = new MovWardBrowserManager();
	private ArrayList<MovementWard> listMovementWardFromTo = new ArrayList<MovementWard>();
	private ArrayList<MedicalWard> wardDrugs;
	private ArrayList<MovementWard> wardOutcomes;
	private ArrayList<Movement> wardIncomes;
	

	//MARCO FOR PAGINATION
	JButton next = new JButton(">");
	JButton previous = new JButton("<");
	JComboBox pagesCombo = new JComboBox();
    JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 100;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;

	public WardPharmacy() {
		if (MainMenu.checkUserGrants("btnmedicalswardedit"))
			editAllowed = true;
		initComponents();
		 
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//modal exclude
		if(!Param.bool("WITHMODALWINDOW")){
			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		}
		
		
		setVisible(true);
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// to free memory
				listMovementCentral.clear();
				listMovementWardFromTo.clear();
				if (wardDrugs != null)
					wardDrugs.clear();
				if (wardOutcomes != null)
					wardOutcomes.clear();
				if (wardIncomes != null)
					wardIncomes.clear();
				
				InventoryWardEdit.removeInventoryListener(WardPharmacy.this);
				
				dispose();
			}
		});
	}

	private void initComponents() {
		getContentPane().add(getJPanelWardAndRange(), BorderLayout.NORTH);
		// add(getJTabbedPaneWard(), BorderLayout.CENTER);
		getContentPane().add(getJPanelButtons(), BorderLayout.SOUTH);
		setTitle(MessageBundle.getMessage("angal.medicalstockward.title")); //$NON-NLS-1$
		setSize(900, 450);
		
		/******* add to inventoryWard listener ***/
		InventoryWardEdit.addInventoryListener(WardPharmacy.this);
		
		pagesCombo.setEditable(true);
		previous.setEnabled(false);
		next.setEnabled(false);
		next.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	if(!previous.isEnabled()) previous.setEnabled(true);
            	START_INDEX += PAGE_SIZE;
            	jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS) {
    				next.setEnabled(false); 
    			}
    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
            }
        });
       
		previous.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	if(!next.isEnabled()) next.setEnabled(true);
        		START_INDEX -= PAGE_SIZE;
        		jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
    			if(START_INDEX < PAGE_SIZE)	{
    				previous.setEnabled(false);
    			}
    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);    			
            }
        });
	       
		  pagesCombo.addActionListener(new ActionListener() {
			 	public void actionPerformed(ActionEvent arg0) {
			 		if(pagesCombo.getItemCount() != 0){
			 			int page_number = (Integer) pagesCombo.getSelectedItem();	
				 		START_INDEX = (page_number-1) * PAGE_SIZE;
				 	
		    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
		            		next.setEnabled(false); 
		    			}else{
		    				next.setEnabled(true); 
		    			}
		    			if(page_number == 1){
		            		previous.setEnabled(false); 
		    			}else{
		    				previous.setEnabled(true); 
		    			}
		    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
		    			jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
			 		}
			 		
			 	}
			 }); 
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel(new FlowLayout());
			
			previous.setPreferredSize(new Dimension(30, 21));
	        next.setPreferredSize(new Dimension(30, 21));
	        pagesCombo.setPreferredSize(new Dimension(60, 21));
	        under.setPreferredSize(new Dimension(60, 21));
	        
	        jPanelButtons.add(previous); 
	        jPanelButtons.add(pagesCombo);
	        jPanelButtons.add(under);
	        jPanelButtons.add(next);
	        
	        
			jPanelButtons.add(getJButtonNew());
			if (editAllowed)
				jPanelButtons.add(getJButtonEdit());
			if (MainMenu.checkUserGrants("btnmedicalswardreport"))
				jPanelButtons.add(getPrintTableButton());
			if (MainMenu.checkUserGrants("btnmedicalswardexcel"))
				jPanelButtons.add(getExportToExcelButton());
			if (MainMenu.checkUserGrants("btnmedicalswardrectify"))
				jPanelButtons.add(getJRectifyButton());
			jPanelButtons.add(getJButtonClose());
			
		}
		return jPanelButtons;
	}

	private JButton getJButtonNew() {
		if (jButtonNew == null) {
			jButtonNew = new JButton();
			jButtonNew.setText(MessageBundle.getMessage("angal.common.new")); //$NON-NLS-1$
			jButtonNew.setMnemonic(KeyEvent.VK_N);
			jButtonNew.setVisible(false);
			jButtonNew.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					WardPharmacyNew editor = new WardPharmacyNew(WardPharmacy.this, wardSelected, wardDrugs);
					editor.addMovementWardListener(WardPharmacy.this);
					//editor.setVisible(true);
					if(Param.bool("WITHMODALWINDOW")){
						editor.showAsModal(WardPharmacy.this);
					}else{
						editor.show(WardPharmacy.this);
					}
				}
			});
		}
		return jButtonNew;
	}

	private JButton getJButtonEdit() {
		if (jButtonEdit == null) {
			jButtonEdit = new JButton();
			jButtonEdit.setText(MessageBundle.getMessage("angal.common.edit")); //$NON-NLS-1$
			jButtonEdit.setMnemonic(KeyEvent.VK_E);
			jButtonEdit.setVisible(false);
			jButtonEdit.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					if (jTableOutcomes.getSelectedRow() < 0 || !jScrollPaneOutcomes.isShowing()) {
						JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.pleaseselectanoutcomesmovementfirst"), MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
					} else {
						movSelected = (MovementWard) ((jTableOutcomes.getModel()).getValueAt(jTableOutcomes.getSelectedRow(), -1));
						WardPharmacyEdit editor = new WardPharmacyEdit(WardPharmacy.this, movSelected, wardDrugs);
						editor.addMovementWardListener(WardPharmacy.this);
						//editor.setVisible(true);
						if(Param.bool("WITHMODALWINDOW")){
							editor.showAsModal(WardPharmacy.this);
						}else{
							editor.show(WardPharmacy.this);
						}
					}
				}
			});
		}
		return jButtonEdit;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(MessageBundle.getMessage("angal.common.close")); //$NON-NLS-1$
			jButtonClose.setMnemonic(KeyEvent.VK_C);
			jButtonClose.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					// to free memory
					listMovementCentral.clear();
					listMovementWardFromTo.clear();
					if (wardDrugs != null)
						wardDrugs.clear();
					dispose();
				}
			});
		}
		return jButtonClose;
	}

	private JPanel getJPanelWard() {
		if (jPanelWard == null) {
			jPanelWard = new JPanel();
			jPanelWard.setLayout(new FlowLayout());
			jPanelWard.add(getJComboBoxWard());
			jPanelWard.add(getJLabelWard());
		}
		return jPanelWard;
	}

	private JPanel getJPanelWardAndRange() {
		if (jPanelWardAndRange == null) {
			jPanelWardAndRange = new JPanel(new BorderLayout());
			jPanelWardAndRange.add(getJPanelWard(), BorderLayout.WEST);
			jPanelWardAndRange.add(getJPanelRange(), BorderLayout.EAST);
		}
		return jPanelWardAndRange;
	}

	private Component getJPanelRange() {
		if (jPanelRange == null) {
			jPanelRange = new JPanel();
			jPanelRange.setLayout(new FlowLayout());
			jPanelRange.add(getJLabelFrom());
			jPanelRange.add(getJCalendarFrom());
			jPanelRange.add(getJLabelTo());
			jPanelRange.add(getJCalendarTo());
		}
		return jPanelRange;
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
							dateTo.setTime((Date) evt.getNewValue());
							OutcomesModel outmodel = new OutcomesModel();
							TOTAL_ROWS = outmodel.getRowCount();
							rowCounterOutcomes.setText(rowCounterOutcomesText + TOTAL_ROWS);
							START_INDEX = 0;
							previous.setEnabled(false);
							if(TOTAL_ROWS <= PAGE_SIZE){
								next.setEnabled(false);
							}else{
								next.setEnabled(true);
							}
							jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
							jTableIncomes.setModel(new IncomesModel());
							initialiseCombo(pagesCombo, TOTAL_ROWS);
							rowCounterIncomes.setText(rowCounterIncomesText + jTableIncomes.getRowCount());
						}});
			jCalendarTo.setEnabled(false);
		}
		return jCalendarTo;
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
							dateFrom.setTime((Date) evt.getNewValue());
							OutcomesModel outmodel = new OutcomesModel();
							TOTAL_ROWS = outmodel.getRowCount();
							rowCounterOutcomes.setText(rowCounterOutcomesText + TOTAL_ROWS);
							START_INDEX = 0;
							previous.setEnabled(false);
							if(TOTAL_ROWS <= PAGE_SIZE){
								next.setEnabled(false);
							}else{
								next.setEnabled(true);
							}
							jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
							jTableIncomes.setModel(new IncomesModel());
							initialiseCombo(pagesCombo, TOTAL_ROWS);
							rowCounterIncomes.setText(rowCounterIncomesText + jTableIncomes.getRowCount());
						}
					});
			jCalendarFrom.setEnabled(false);
		}
		return jCalendarFrom;
	}

	private JScrollPane getJScrollPaneIncomes() {
		if (jScrollPaneIncomes == null) {
			jScrollPaneIncomes = new JScrollPane();
			jScrollPaneIncomes.setViewportView(getJTableIncomes());
		}
		return jScrollPaneIncomes;
	}

	private JTable getJTableIncomes() {
		if (jTableIncomes == null) {
			modelIncomes = new IncomesModel();
			jTableIncomes = new JTable(modelIncomes);
			for (int i = 0; i < columWidthIncomes.length; i++) {
				jTableIncomes.getColumnModel().getColumn(i).setMinWidth(columWidthIncomes[i]);
				if (!columsResizableIncomes[i])
					jTableIncomes.getColumnModel().getColumn(i).setMaxWidth(columWidthIncomes[i]);
			}
			jTableIncomes.setAutoCreateColumnsFromModel(false);
		}
		return jTableIncomes;
	}

	private JScrollPane getJScrollPaneDrugs() {
		if (jScrollPaneDrugs == null) {
			jScrollPaneDrugs = new JScrollPane();
			jScrollPaneDrugs.setViewportView(getJTableDrugs());
		}
		return jScrollPaneDrugs;
	}

	private JTable getJTableDrugs() {
		if (jTableDrugs == null) {
			modelDrugs = new DrugsModel();
			jTableDrugs = new JTable(modelDrugs);
			for (int i = 0; i < columWidthDrugs.length; i++) {
				jTableDrugs.getColumnModel().getColumn(i).setMinWidth(columWidthDrugs[i]);
				if (!columsResizableDrugs[i])
					jTableDrugs.getColumnModel().getColumn(i).setMaxWidth(columWidthDrugs[i]);
			}
			jTableDrugs.setAutoCreateColumnsFromModel(false);
		}
		return jTableDrugs;
	}

	private JPanel getJPanelCentral() {
		if (jPanelCentral == null) {
			jPanelCentral = new JPanel(new BorderLayout());
			jPanelCentral.add(getJPanelFilter(), BorderLayout.WEST);
			jPanelCentral.add(getJTabbedPaneWard(), BorderLayout.CENTER);
		}
		return jPanelCentral;
	}

	private JPanel getJPanelFilter() {
		if (jPanelFilter == null) {
			jPanelFilter = new JPanel();
			jPanelFilter.setLayout(new BoxLayout(jPanelFilter, BoxLayout.Y_AXIS));
			jPanelFilter.add(Box.createVerticalStrut(filterSpacing));
			JLabel jLabelMedical = new JLabel(MessageBundle.getMessage("angal.medicalstockward.medical"));
			jLabelMedical.setAlignmentX(Box.CENTER_ALIGNMENT);
			jPanelFilter.add(jLabelMedical);
			jPanelFilter.add(Box.createVerticalStrut(filterSpacing));
			jPanelFilter.add(getJComboBoxTypes());
			jPanelFilter.add(Box.createVerticalStrut(filterSpacing));
			jPanelFilter.add(getJComboBoxMedicals());
			jPanelFilter.add(Box.createVerticalStrut(filterSpacing));
			jPanelFilter.add(getJPanelAge());
			jPanelFilter.add(getSexPanel());
			jPanelFilter.add(getJPanelWeight());
			jPanelFilter.add(getFilterResetPanel());
			jPanelFilter.add(Box.createVerticalGlue());
			jPanelFilter.add(getRowCounterOutcomes());
			jPanelFilter.add(getRowCounterIncomes());

		}
		return jPanelFilter;
	}

	private JPanel getFilterResetPanel() {
		JPanel jFilterResetPanel = new JPanel();
		jFilterResetPanel.add(getFilterButton());
		jFilterResetPanel.add(getResetButton());
		return jFilterResetPanel;
	}

	private JLabel getRowCounterOutcomes() {
		if (rowCounterOutcomes == null) {
			rowCounterOutcomes = new JLabel();
			rowCounterOutcomes.setAlignmentX(Box.CENTER_ALIGNMENT);
		}
		return rowCounterOutcomes;
	}

	private JLabel getRowCounterIncomes() {
		if (rowCounterIncomes == null) {
			rowCounterIncomes = new JLabel();
			rowCounterIncomes.setAlignmentX(Box.CENTER_ALIGNMENT);
		}
		return rowCounterIncomes;
	}
	
	private JButton getFilterButton() {
		if (filterButton == null) {
			filterButton = new JButton(MessageBundle.getMessage("angal.medicalstockward.filter"));
			filterButton.setMnemonic(KeyEvent.VK_F);
			filterButton.setAlignmentX(Box.CENTER_ALIGNMENT);
			filterButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (ageFrom > ageTo) {
						JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.agefrommustbelowerthanageto"));
						jAgeFromTextField.setText(String.valueOf(ageTo));
						ageFrom = ageTo;
						return;
					}
					if (weightFrom > weightTo) {
						JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.weightfrommustbelowerthanweightto"));
						jWeightFromTextField.setText(String.valueOf(weightTo));
						weightFrom = weightTo;
						return;
					}
					OutcomesModel outmodel = new OutcomesModel();
					TOTAL_ROWS = outmodel.getRowCount();
					START_INDEX = 0;
					previous.setEnabled(false);
					if(TOTAL_ROWS <= PAGE_SIZE){
						next.setEnabled(false);
					}else{
						next.setEnabled(true);
					}
					jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
					rowCounterOutcomes.setText(rowCounterOutcomesText + TOTAL_ROWS);
					initialiseCombo(pagesCombo, TOTAL_ROWS);
					
					jTableIncomes.setModel(new IncomesModel());
					rowCounterIncomes.setText(rowCounterIncomesText + jTableIncomes.getRowCount());
				}
			});
		}
		return filterButton;
	}
	
	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton(MessageBundle.getMessage("angal.medicalstockward.reset"));
			resetButton.setMnemonic(KeyEvent.VK_R);
			resetButton.setAlignmentX(Box.CENTER_ALIGNMENT);
			resetButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					jAgeFromTextField.setText("0");
					jAgeToTextField.setText("0");
					jWeightFromTextField.setText("0");
					jWeightToTextField.setText("0");
					radioa.setSelected(true);
					jComboBoxTypes.setSelectedIndex(0);
					rowCounterOutcomes.setText(rowCounterOutcomesText + jTableOutcomes.getRowCount());
					TOTAL_ROWS = jTableOutcomes.getRowCount();
					initialiseCombo(pagesCombo, TOTAL_ROWS);
					rowCounterIncomes.setText(rowCounterIncomesText + jTableIncomes.getRowCount());
				}

			});
		}
		return resetButton;
	}

	private JPanel getJPanelWeight() {
		if (jWeightPanel == null) {
			jWeightPanel = new JPanel();
			jWeightPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.medicalstockward.weight")));

			JLabel jLabelWeightFrom = new JLabel(MessageBundle.getMessage("angal.medicalstockward.weightfrom"));
			jWeightPanel.add(jLabelWeightFrom, null);
			jWeightPanel.add(getJWeightFromTextField(), null);

			JLabel jLabelWeightTo = new JLabel(MessageBundle.getMessage("angal.medicalstockward.weightto"));
			jWeightPanel.add(jLabelWeightTo, null);
			jWeightPanel.add(getJWeightToTextField(), null);
		}
		return jWeightPanel;
	}

	private VoLimitedTextField getJWeightToTextField() {
		if (jWeightToTextField == null) {
			jWeightToTextField = new VoLimitedTextField(5, 5);
			jWeightToTextField.setText("0");
			jWeightToTextField.setMaximumSize(new Dimension(100, 50));
			jWeightToTextField.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					try {
						weightTo = Integer.parseInt(jWeightToTextField.getText());
						weightFrom = Integer.parseInt(jWeightFromTextField.getText());
						if ((weightTo < 0) || (weightTo > 200)) {
							jWeightToTextField.setText("");
							JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.insertavalidweight"));
						}
					} catch (NumberFormatException ex) {
						jWeightToTextField.setText("0");
					}
				}

				public void focusGained(FocusEvent e) {
				}
			});
		}
		return jWeightToTextField;
	}

	private VoLimitedTextField getJWeightFromTextField() {
		if (jWeightFromTextField == null) {
			jWeightFromTextField = new VoLimitedTextField(5, 5);
			jWeightFromTextField.setText("0");
			jWeightFromTextField.setMinimumSize(new Dimension(100, 50));
			jWeightFromTextField.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					try {
						weightFrom = Integer.parseInt(jWeightFromTextField.getText());
						weightTo = Integer.parseInt(jWeightToTextField.getText());
						if ((weightFrom < 0)) {
							jWeightFromTextField.setText("");
							JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.insertvalidweight"));
						}
					} catch (NumberFormatException ex) {
						jWeightFromTextField.setText("0");
					}
				}

				public void focusGained(FocusEvent e) {
				}
			});
		}
		return jWeightFromTextField;
	}

	public JPanel getSexPanel() {
		if (sexPanel == null) {
			sexPanel = new JPanel();
			sexPanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.medicalstockward.sex")));
			ButtonGroup group = new ButtonGroup();
			radiom = new JRadioButton(MessageBundle.getMessage("angal.medicalstockward.male"));
			radiof = new JRadioButton(MessageBundle.getMessage("angal.medicalstockward.female"));
			radioa = new JRadioButton(MessageBundle.getMessage("angal.medicalstockward.all"));
			radioa.setSelected(true);
			group.add(radiom);
			group.add(radiof);
			group.add(radioa);
			sexPanel.add(radioa);
			sexPanel.add(radiom);
			sexPanel.add(radiof);
		}
		return sexPanel;
	}

	private JPanel getJPanelAge() {
		if (jAgePanel == null) {
			jAgePanel = new JPanel();
			jAgePanel.setBorder(BorderFactory.createTitledBorder(MessageBundle.getMessage("angal.medicalstockward.age")));

			JLabel jLabelAgeFrom = new JLabel(MessageBundle.getMessage("angal.medicalstockward.agefrom"));
			jAgePanel.add(jLabelAgeFrom);
			jAgePanel.add(getJAgeFromTextField());

			JLabel jLabelAgeTo = new JLabel(MessageBundle.getMessage("angal.medicalstockward.ageto"));
			jAgePanel.add(jLabelAgeTo);
			jAgePanel.add(getJAgeToTextField());
		}
		return jAgePanel;
	}

	private VoLimitedTextField getJAgeToTextField() {
		if (jAgeToTextField == null) {
			jAgeToTextField = new VoLimitedTextField(3, 3);
			jAgeToTextField.setText("0");
			jAgeToTextField.setMaximumSize(new Dimension(100, 50));
			jAgeToTextField.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					try {
						ageTo = Integer.parseInt(jAgeToTextField.getText());
						ageFrom = Integer.parseInt(jAgeFromTextField.getText());
						if ((ageTo < 0) || (ageTo > 200)) {
							jAgeToTextField.setText("");
							JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.insertvalidage"));
						}
					} catch (NumberFormatException ex) {
						jAgeToTextField.setText("0");
					}
				}

				public void focusGained(FocusEvent e) {
				}
			});
		}
		return jAgeToTextField;
	}

	/**
	 * This method initializes jAgeFromTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private VoLimitedTextField getJAgeFromTextField() {
		if (jAgeFromTextField == null) {
			jAgeFromTextField = new VoLimitedTextField(3, 3);
			jAgeFromTextField.setText("0");
			jAgeFromTextField.setMinimumSize(new Dimension(100, 50));
			jAgeFromTextField.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					try {
						ageFrom = Integer.parseInt(jAgeFromTextField.getText());
						ageTo = Integer.parseInt(jAgeToTextField.getText());
						if ((ageFrom < 0) || (ageFrom > 200)) {
							jAgeFromTextField.setText("");
							JOptionPane.showMessageDialog(WardPharmacy.this, MessageBundle.getMessage("angal.medicalstockward.insertvalidage"));
						}
					} catch (NumberFormatException ex) {
						jAgeFromTextField.setText("0");
					}
				}

				public void focusGained(FocusEvent e) {
				}
			});
		}
		return jAgeFromTextField;
	}

	private JComboBox getJComboBoxTypes() {
		if (jComboBoxTypes == null) {
			jComboBoxTypes = new JComboBox();
			jComboBoxTypes.setMaximumSize(new Dimension(filterWidth, 24));
			jComboBoxTypes.setPreferredSize(new Dimension(filterWidth, 24));
			MedicalTypeBrowserManager medicalManager = new MedicalTypeBrowserManager();
			ArrayList<MedicalType> medicalTypes = medicalManager.getMedicalType();
			jComboBoxTypes.addItem(MessageBundle.getMessage("angal.medicalstockward.alltypes"));
			for (MedicalType aMedicalType : medicalTypes) {
				jComboBoxTypes.addItem(aMedicalType);
			}
			jComboBoxTypes.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					jComboBoxMedicals.removeAllItems();
					getJComboBoxMedicals();

				}
			});
		}
		return jComboBoxTypes;
	}

	private JComboBox getJComboBoxMedicals() {
		if (jComboBoxMedicals == null) {
			jComboBoxMedicals = new JComboBox();
			jComboBoxMedicals.setMaximumSize(new Dimension(filterWidth, 24));
			jComboBoxMedicals.setPreferredSize(new Dimension(filterWidth, 24));
		}
		MedicalBrowsingManager medicalManager = new MedicalBrowsingManager();
		ArrayList<Medical> medicals = medicalManager.getMedicals();
		jComboBoxMedicals.addItem(MessageBundle.getMessage("angal.medicalstockward.allmedicals"));
		MedicalType medicalType;
		if (jComboBoxTypes.getSelectedItem() instanceof String) {
			medicalType = null;
		} else {
			medicalType = (MedicalType) jComboBoxTypes.getSelectedItem();
		}
		for (Medical aMedical : medicals) {
			boolean ok = true;
			if (medicalType != null)
				ok = ok && aMedical.getType().equals(medicalType);
			if (ok)
				jComboBoxMedicals.addItem(aMedical);
		}
		return jComboBoxMedicals;
	}

	private JTabbedPane getJTabbedPaneWard() {
		if (jTabbedPaneWard == null) {
			jTabbedPaneWard = new JTabbedPane();
			jTabbedPaneWard.addTab(MessageBundle.getMessage("angal.medicalstockward.outcomes"), getJScrollPaneOutcomes()); //$NON-NLS-1$
			jTabbedPaneWard.addTab(MessageBundle.getMessage("angal.medicalstockward.incomings"), getJScrollPaneIncomes()); //$NON-NLS-1$
			jTabbedPaneWard.addTab(MessageBundle.getMessage("angal.medicalstockward.drugs"), getJScrollPaneDrugs()); //$NON-NLS-1$
		}
		return jTabbedPaneWard;
	}

	private JScrollPane getJScrollPaneOutcomes() {
		if (jScrollPaneOutcomes == null) {
			jScrollPaneOutcomes = new JScrollPane();
			jScrollPaneOutcomes.setViewportView(getJTableOutcomes());
		}
		return jScrollPaneOutcomes;
	}

	private JTable getJTableOutcomes() {
		if (jTableOutcomes == null) {
			modelOutcomes = new OutcomesModel();
			jTableOutcomes = new JTable(modelOutcomes);
			jTableOutcomes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			for (int i = 0; i < columWidthOutcomes.length; i++) {
				jTableOutcomes.getColumnModel().getColumn(i).setPreferredWidth(columWidthOutcomes[i]);
				if (!columsResizableOutcomes[i])
					jTableOutcomes.getColumnModel().getColumn(i).setMaxWidth(columWidthOutcomes[i]);
			}
			jTableOutcomes.setDefaultRenderer(Object.class, new BlueBoldTableCellRenderer());
			jTableOutcomes.setAutoCreateColumnsFromModel(false);
		}
		return jTableOutcomes;
	}

	private JLabel getJLabelTo() {
		if (jLabelTo == null) {
			jLabelTo = new JLabel();
			jLabelTo.setText(MessageBundle.getMessage("angal.medicalstockward.to")); //$NON-NLS-1$
			jLabelTo.setBounds(509, 15, 45, 15);
		}
		return jLabelTo;
	}

	private JLabel getJLabelFrom() {
		if (jLabelFrom == null) {
			jLabelFrom = new JLabel();
			jLabelFrom.setText(MessageBundle.getMessage("angal.medicalstockward.from")); //$NON-NLS-1$
			jLabelFrom.setBounds(365, 14, 45, 15);
		}
		return jLabelFrom;
	}

	private JLabel getJLabelWard() {
		if (jLabelWard == null) {
			jLabelWard = new JLabel();
			jLabelWard.setText(MessageBundle.getMessage("angal.medicalstockward.ward")); //$NON-NLS-1$
			jLabelWard.setBounds(148, 18, 45, 15);
		}
		return jLabelWard;
	}

	private JComboBox getJComboBoxWard() {
		if (jComboBoxWard == null) {
			jComboBoxWard = new JComboBox();
			WardBrowserManager wardManager = new WardBrowserManager();
			wardList = wardManager.getWards();
			jComboBoxWard.addItem(MessageBundle.getMessage("angal.medicalstockward.selectaward"));
			for (Ward ward : wardList) {
				if (ward.isPharmacy())
					jComboBoxWard.addItem(ward);
			}
			jComboBoxWard.setBorder(null);
			jComboBoxWard.setBounds(15, 14, 122, 24);
			jComboBoxWard.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					Object ward = jComboBoxWard.getSelectedItem();
					if (ward instanceof Ward) {
						wardSelected = (Ward) ward;
						if (!added) {
							getContentPane().add(getJPanelCentral());
							jCalendarFrom.setEnabled(true);
							jCalendarTo.setEnabled(true);
							jButtonNew.setVisible(true);
							if (MainMenu.checkUserGrants("btnmedicalswardreport"))
								jPrintTableButton.setVisible(true);
							if (MainMenu.checkUserGrants("btnmedicalswardexcel"))
								jExportToExcelButton.setVisible(true);
							if (MainMenu.checkUserGrants("btnmedicalswardrectify"))
								jRectifyButton.setVisible(true);
							if (editAllowed)
								jButtonEdit.setVisible(true);
							validate();
							setLocationRelativeTo(null);
							added = true;
						} else {
							if (wardSelected != null) {
								jTableIncomes.setModel(new IncomesModel());
								jTableOutcomes.setModel(new OutcomesModel());
								jTableDrugs.setModel(new DrugsModel());
							} else {
								remove(jTabbedPaneWard);
								jButtonNew.setVisible(false);
								if (MainMenu.checkUserGrants("btnmedicalswardreport"))
									jPrintTableButton.setVisible(false);
								if (MainMenu.checkUserGrants("btnmedicalswardexcel"))
									jExportToExcelButton.setVisible(false);
								if (MainMenu.checkUserGrants("btnmedicalswardrectify"))
									jRectifyButton.setVisible(false);
								added = false;
							}
						}
						OutcomesModel outmodel = new OutcomesModel();
						//MovBrowserManager wardManager = new MovBrowserManager();
						TOTAL_ROWS = outmodel.getRowCount();
						START_INDEX = 0;
						previous.setEnabled(false);
						if(TOTAL_ROWS <= PAGE_SIZE){
							next.setEnabled(false);
						}else{
							next.setEnabled(true);
						}
						jTableOutcomes.setModel(new OutcomesModel(START_INDEX, PAGE_SIZE));
						rowCounterOutcomes.setText(rowCounterOutcomesText + TOTAL_ROWS);
						initialiseCombo(pagesCombo, TOTAL_ROWS);
						rowCounterIncomes.setText(rowCounterIncomesText + jTableIncomes.getRowCount());
						validate();
						repaint();
					}
				}
			});
		}
		return jComboBoxWard;
	}

	class IncomesModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	
		public IncomesModel() {
			wardIncomes = new ArrayList<Movement>();			
			MedicalType medicalTypeSelected;
			if (jComboBoxTypes.getSelectedItem() instanceof String) {
				medicalTypeSelected = null;
			} else {
				medicalTypeSelected = (MedicalType) jComboBoxTypes.getSelectedItem();
			}
			
			list1MovementIn = movManager.getWardEntries(wardSelected.getCode(), dateFrom, dateTo, true, medicalTypeSelected);
			
			listMovementCentral = movManager.getMovements(wardSelected.getCode(), dateFrom, dateTo, true, medicalTypeSelected);
			Medical medicalSelected;
			if (jComboBoxMedicals.getSelectedItem() instanceof String) {
				medicalSelected = null;
			} else {
				medicalSelected = (Medical) jComboBoxMedicals.getSelectedItem();
			}
			
			for (Movement mov : listMovementCentral) {
				
				if(medicalSelected != null){
					if(medicalSelected.getCode().equals(mov.getMedical().getCode())){
						wardIncomes.add(mov);
						System.out.println("CODE DU LOT "+mov.getLot());
					}
				}else{
					System.out.println("CODE DU LOT "+mov.getLot());
					wardIncomes.add(mov);
				}
			}
		}

		public int getRowCount() {
			if (wardIncomes == null)
				return 0;
			return wardIncomes.size();
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return wardIncomes.get(r);
			}
			if (c == 0) {
				return formatDate(wardIncomes.get(r).getDate());
			}
			if (c == 1) {
				return wardIncomes.get(r).getMedical();
			}
			if (c == 2) {
				double pieces = wardIncomes.get(r).getQuantity();
				int pcsPerPck = wardIncomes.get(r).getMedical().getPcsperpck();
				double packets = 0;
				if (pcsPerPck != 0) {
					packets = pieces / pcsPerPck;
					return pieces + " (" + packets + " " + MessageBundle.getMessage("angal.medicalstockward.packets") + ")"; //$NON-NLS-1$;
				} else {
					return pieces;
				}
			}
			//LAST PRICE
			if(c == 3){
				return wardIncomes.get(r).getLot().getCost();
			}
			return null;
		}

		public String getColumnName(int c) {
			return columsIncomes[c];
		}

		public int getColumnCount() {
			return columsIncomes.length;
		}

		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	class OutcomesModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public OutcomesModel() {
			wardOutcomes = new ArrayList<MovementWard>();
			MedicalType medicalTypeSelected;
			if (jComboBoxTypes.getSelectedItem() instanceof String) {
				medicalTypeSelected = null;
			} else {
				medicalTypeSelected = (MedicalType) jComboBoxTypes.getSelectedItem();
			}
			listMovementWardFromTo = wardManager.getMovementWard(wardSelected.getCode(), dateFrom, dateTo, medicalTypeSelected); //VENTES

			Medical medicalSelected;
			if (jComboBoxMedicals.getSelectedItem() instanceof String) {
				medicalSelected = null;
			} else {
				medicalSelected = (Medical) jComboBoxMedicals.getSelectedItem();
			}
		
			//char sex;
			String sex;
			if (radioa.isSelected()) {
				sex = "A";
			} else if (radiom.isSelected()) {
				sex = "M";
			} else {
				sex = "F";
			}

			int ageFrom = Integer.valueOf(jAgeFromTextField.getText());
			int ageTo = Integer.valueOf(jAgeToTextField.getText());

			float weightFrom = Float.valueOf(jWeightFromTextField.getText());
			float weightTo = Float.valueOf(jWeightToTextField.getText());

			for (MovementWard mov : listMovementWardFromTo) {
				boolean ok = true;
				Patient patient = mov.getPatient();
				Medical medical = mov.getMedical();
				int age = mov.getAge();
				float weight = mov.getWeight();

				// Medical control
				if (medicalSelected != null) {
					ok = ok && medical.getCode().equals(medicalSelected.getCode());
				} 
				// sex control if sex not 'A'
				if (sex != "A")
					ok = ok && patient.getSex().equals(sex);
				// age control if ageTo > 0
				if (ageTo != 0)
					ok = ok && age >= ageFrom && age <= ageTo;
				// weight control if weightTo > 0
				if (weightTo != 0)
					ok = ok && weight >= weightFrom && weight <= weightTo;

				if (ok)
					wardOutcomes.add(mov);
				
			}
			Collections.reverse(wardOutcomes);
		}
		
		public OutcomesModel(int start_index, int page_size) {
			wardOutcomes = new ArrayList<MovementWard>();
			MedicalType medicalTypeSelected;
			if (jComboBoxTypes.getSelectedItem() instanceof String) {
				medicalTypeSelected = null;
			} else {
				medicalTypeSelected = (MedicalType) jComboBoxTypes.getSelectedItem();
			}
			//listMovementWardFromTo = wardManager.getMovementWard(wardSelected.getCode(), dateFrom, dateTo, medicalTypeSelected); //VENTES
			listMovementWardFromTo = wardManager.getMovementWard(wardSelected.getCode(), dateFrom, dateTo, medicalTypeSelected, start_index, page_size); //VENTES
			Medical medicalSelected;
			if (jComboBoxMedicals.getSelectedItem() instanceof String) {
				medicalSelected = null;
			} else {
				medicalSelected = (Medical) jComboBoxMedicals.getSelectedItem();
			}

			
		
			//char sex;
			String sex;
			if (radioa.isSelected()) {
				sex = "A";
			} else if (radiom.isSelected()) {
				sex = "M";
			} else {
				sex = "F";
			}

			int ageFrom = Integer.valueOf(jAgeFromTextField.getText());
			int ageTo = Integer.valueOf(jAgeToTextField.getText());

			float weightFrom = Float.valueOf(jWeightFromTextField.getText());
			float weightTo = Float.valueOf(jWeightToTextField.getText());

			for (MovementWard mov : listMovementWardFromTo) {
				boolean ok = true;
				Patient patient = mov.getPatient();
				Medical medical = mov.getMedical();
				int age = mov.getAge();
				float weight = mov.getWeight();

				// Medical control
				if (medicalSelected != null) {
					ok = ok && medical.getCode().equals(medicalSelected.getCode());
				} 
				// sex control if sex not 'A'
				if (sex != "A")
					ok = ok && patient.getSex().equals(sex);

				// age control if ageTo > 0
				if (ageTo != 0)
					ok = ok && age >= ageFrom && age <= ageTo;

				// weight control if weightTo > 0
				if (weightTo != 0)
					ok = ok && weight >= weightFrom && weight <= weightTo;

				if (ok)
					wardOutcomes.add(mov);
				
			}
			
			Collections.reverse(wardOutcomes);
		}

		public int getRowCount() {
			if (wardOutcomes == null)
				return 0;
			return wardOutcomes.size();
		}

		public Object getValueAt(int r, int c) {
			MovementWard mov = wardOutcomes.get(r);
			if (c == -1) {
				return mov;
			}
			if (c == 0) {
				return formatDateTime(mov.getDate());
			}
			if (c == 1) {
				if (mov.isPatient()){
					return mov.getPatient().getName();
				}
				return mov.getDescription();
			}
			if (c == 2) {
				if (mov.isPatient())
					return mov.getAge();
				else
					return MessageBundle.getMessage("angal.medicalstockward.notapplicable.abb");
			}
			if (c == 3) {
				if (mov.isPatient())
					return mov.getPatient().getSex();
				return MessageBundle.getMessage("angal.medicalstockward.notapplicable.abb");
			}
			if (c == 4) {
				if (mov.isPatient()) {
					float weight = mov.getWeight();
					return weight == 0 ? MessageBundle.getMessage("angal.medicalstockward.notdefined.abb") : weight;
				} else
					return MessageBundle.getMessage("angal.medicalstockward.notapplicable.abb");
			}
			if (c == 5) {
				return mov.getMedical().getDescription();
			}
			if (c == 6) {
				return "" + mov.getQuantity() + " " + //$NON-NLS-1$ 
						mov.getUnits();
			}
			return null;
		}

		public String getColumnName(int c) {
			return columsOutcomes[c];
		}

		public int getColumnCount() {
			return columsOutcomes.length;
		}

		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	class DrugsModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		int index = 0;
		int oldQty;
		int packets;
		int pieces;
		int newQty;

		public DrugsModel() {
			wardDrugs = wardManager.getMedicalsWardWithPrice(wardSelected.getCode());
		}

		public int getRowCount() {
			if (wardDrugs == null)
				return 0;
			return wardDrugs.size();
		}

		public Object getValueAt(int r, int c) {
			MedicalWard wardDrug = wardDrugs.get(r);
			if (c == -1) {
				return wardDrug;
			}
			if (c == 0) {
				return wardDrug.getMedical().getDescription();
			}
			if (c == 1) {
				//return wardDrug.getQty() + " " + MessageBundle.getMessage("angal.medicalstockward.pieces");
				return wardDrug.getQty();
			}
			if (c == 2) {
				//return wardDrug.getQty() + " " + MessageBundle.getMessage("angal.medicalstockward.pieces");
				return wardDrug.getLastprice();
			}
			return null;
		}

		public String getColumnName(int c) {
			return columsDrugs[c];
		}

		public int getColumnCount() {
			return columsDrugs.length;
		}

		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}
	
	
	private JButton getJRectifyButton() {
		if (jRectifyButton == null) {
			jRectifyButton = new JButton(MessageBundle.getMessage("angal.medicalstockward.rectify"));
			jRectifyButton.setMnemonic(KeyEvent.VK_R);
			jRectifyButton.setVisible(false);
			jRectifyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					WardPharmacyRectify wardRectify = new WardPharmacyRectify(WardPharmacy.this, wardSelected, wardDrugs);
					wardRectify.addMovementWardListener(WardPharmacy.this);
					//wardRectify.setVisible(true);
					if(Param.bool("WITHMODALWINDOW")){
						wardRectify.showAsModal(WardPharmacy.this);
					}else{
						wardRectify.show(WardPharmacy.this);
					}
				}
			});
		}
		return jRectifyButton;
	}
	
	private JButton getPrintTableButton() {
		if (jPrintTableButton == null) {
			jPrintTableButton = new JButton(MessageBundle.getMessage("angal.medicalstockward.report"));
			jPrintTableButton.setMnemonic(KeyEvent.VK_P);
			jPrintTableButton.setVisible(false);
			jPrintTableButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					
					if (jTabbedPaneWard.getSelectedIndex() == 0) {
						new PrintManager("WardPharmacyOutcomes", wardManager.convertMovementWardForPrint(wardOutcomes), 0);
					} else if (jTabbedPaneWard.getSelectedIndex() == 1) {
						//new PrintManager("WardPharmacyIncomes", wardManager.convertMovementForPrint(wardIncomes), 0);
						new PrintManager("WardPharmacyIncomes",list1MovementIn, 0);
						
						
					} else if (jTabbedPaneWard.getSelectedIndex() == 2) {
						new PrintManager("WardPharmacyDrugs", wardManager.convertWardDrugs(wardSelected, wardDrugs), 0);
					} 
				}
			});
		}
		return jPrintTableButton;
	}
	
	private JButton getExportToExcelButton() {
		if (jExportToExcelButton == null) {
			jExportToExcelButton = new JButton("Excel");
			jExportToExcelButton.setMnemonic(KeyEvent.VK_E);
			jExportToExcelButton.setVisible(false);
			jExportToExcelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fcExcel = new JFileChooser();
					FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xls)","xls");
					fcExcel.setFileFilter(excelFilter);
					fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);  
					
					int iRetVal = fcExcel.showSaveDialog(WardPharmacy.this);
					if(iRetVal == JFileChooser.APPROVE_OPTION) {
						try {
							File exportFile = fcExcel.getSelectedFile();
							
							if (!exportFile.getName().endsWith("xls")) exportFile = new File(exportFile.getAbsoluteFile() + ".xls");
							
							ExcelExporter xlsExport = new ExcelExporter();
							
							int index = jTabbedPaneWard.getSelectedIndex();
							if (index == 0) {
								xlsExport.exportTableToExcelOutcomes(jTableOutcomes, exportFile);
							} else if (index == 1) {
								xlsExport.exportTableToExcelIncomes(jTableIncomes, exportFile);
						
							} else if (index == 2) {
								xlsExport.exportTableToExcel2(jTableDrugs, exportFile);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			});
		}
		return jExportToExcelButton;
	}
	
	class CenterBoldTableCellRenderer extends DefaultTableCellRenderer {  
		 
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			setHorizontalAlignment(CENTER);
			cell.setFont(new Font(null, Font.BOLD, 12));
			return cell;
	   }
	}
	
	class BlueBoldTableCellRenderer extends DefaultTableCellRenderer {  
		   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {  
		   
			Component cell=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			cell.setForeground(Color.BLACK);
			cell.setFont(new Font(null, Font.PLAIN, 12));
			MovementWard mov = wardOutcomes.get(row);
			if (!mov.isPatient()) {
				cell.setForeground(Color.BLUE);
				cell.setFont(new Font(null, Font.BOLD, 12));
			}
			return cell;
	   }
	}

	public String formatDate(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(time.getTime());
	}

	public String formatDateTime(GregorianCalendar time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return format.format(time.getTime());
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
		modelDrugs = new DrugsModel();
		jTableDrugs = new JTable(modelDrugs);
		jTableDrugs.updateUI();
	}

	@Override
	public void InventoryCancelled(AWTEvent e) {
		// TODO Auto-generated method stub		
	}
	
	public void initialiseCombo(JComboBox pagesCombo, int total_rows){
		int j = 0;
		pagesCombo.removeAllItems();
		for(int i=0; i< total_rows/PAGE_SIZE; i++){
			j = i+1;
			pagesCombo.addItem(j);
		}
		if(j * PAGE_SIZE < total_rows){
			pagesCombo.addItem(j+1);
			under.setText("/" + (total_rows/PAGE_SIZE + 1 + " Pages"));
		}else{
			under.setText("/" + total_rows/PAGE_SIZE + " Pages");
		}
		
	}
}
