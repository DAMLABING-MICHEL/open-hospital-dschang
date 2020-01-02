/**
 * 11-dic-2005
 * author bob
 */
package org.isf.medicals.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.eclipse.swt.events.MouseEvent;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryBrowser;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportMedicals;
import org.isf.stat.manager.GenericReportPharmaceuticalOrder;
import org.isf.stat.manager.GenericReportUserInDate;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.jobjects.JMonthYearChooser;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.time.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

public class MedicalBrowser extends ModalJFrame { // implements
													// RowSorterListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(MedicalBrowser.class);

	public void medicalInserted() {
		pMedicals.add(0, medical);
		((MedicalBrowsingModel) table.getModel()).fireTableDataChanged();
		table.updateUI();
		if (table.getRowCount() > 0)
			table.setRowSelectionInterval(0, 0);
		repaint();
	}

	public void medicalUpdated() {
		pMedicals.set(selectedrow, medical);
		((MedicalBrowsingModel) table.getModel()).fireTableDataChanged();
		table.updateUI();
		if ((table.getRowCount() > 0) && selectedrow > -1)
			table.setRowSelectionInterval(selectedrow, selectedrow);
		repaint();

	}

	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 400;
	private int pfrmWidth;
	private int pfrmHeight;
	private int selectedrow;
	private JLabel selectlabel;
	private JComboBox pbox;
	private ArrayList<Medical> pMedicals;
	private ArrayList<Medical> pMedicals2;
	ArrayList<Medical> medSearch = new ArrayList<Medical>();
	private Medical medicine;
	private String[] pColums = { 
		MessageBundle.getMessage("angal.medicals.typem"),
		MessageBundle.getMessage("angal.medicals.code"), 
		MessageBundle.getMessage("angal.medicals.descriptionm"),
		MessageBundle.getMessage("angal.medicals.pcsperpck"), 
		MessageBundle.getMessage("angal.medicals.stockm"),
		MessageBundle.getMessage("angal.medicals.critlevelm"),
		MessageBundle.getMessage("angal.medicals.outofstockm"),
		MessageBundle.getMessage("angal.medicals.lastprice"),
		MessageBundle.getMessage("angal.medicals.averagequantity")};
	
	private int[] pColumwidth = { 100, 75, 350, 50, 50, 80, 100, 75, 75 };
	private boolean[] pColumResizable = { true, true, true, true, true, true, true, true, true };
	private Medical medical;
	private DefaultTableModel model;
	private JTable table;
	private final JFrame me;
	private String lastKey = "";

	private String pSelection;
	private JTextField searchTextField;

	JButton next = new JButton(">");
	JButton previous = new JButton("<");
	JComboBox pagesCombo = new JComboBox();
    JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;
	private String typologie = "";
	public MedicalBrowser() {
		me = this;
		setTitle(MessageBundle.getMessage("angal.medicals.pharmaceuticalbrowsing"));
		
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		pfrmWidth = 1040;
		pfrmHeight = screensize.height / 2;
		setBounds((screensize.width - pfrmWidth) / 2, screensize.height / 4, pfrmWidth, pfrmHeight);
		
		MedicalBrowsingManager manager0 = new MedicalBrowsingManager();
		TOTAL_ROWS = manager0.getMedicalsTotalRows("");
		initialiseCombo(pagesCombo, TOTAL_ROWS);
		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE, typologie); 
		table = new JTable(model);
		previous.setEnabled(false);
		if(PAGE_SIZE > TOTAL_ROWS) next.setEnabled(false);
		table.setAutoCreateRowSorter(true);
	  
		JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
        next.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	if(!previous.isEnabled()) previous.setEnabled(true);
            	
            	pSelection = pbox.getSelectedItem().toString();
				if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.alltypology")) == 0){
					pSelection = "";
				}
            	START_INDEX += PAGE_SIZE;
        		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE, pSelection);
          	
				model.fireTableDataChanged();
    			if((START_INDEX + PAGE_SIZE) > TOTAL_ROWS){
            		next.setEnabled(false); 
    			}
    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
    			table.updateUI();
            }
       });
       
       previous.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	if(!next.isEnabled()) next.setEnabled(true);
            	pSelection = pbox.getSelectedItem().toString();
				if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.alltypology")) == 0){
					pSelection = "";
				}
        		START_INDEX -= PAGE_SIZE;
        		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE, typologie);
				model.fireTableDataChanged();
    			if(START_INDEX < PAGE_SIZE)	previous.setEnabled(false); 
    			pagesCombo.setSelectedItem(START_INDEX/PAGE_SIZE + 1);
    			table.updateUI();
            }
        });
        previous.setPreferredSize(new Dimension(30, 21));
        next.setPreferredSize(new Dimension(30, 21));
        pagesCombo.setPreferredSize(new Dimension(60, 21));
        under.setPreferredSize(new Dimension(60, 21));
        
        navigation.add(previous); 
        navigation.add(pagesCombo);
        navigation.add(under);
        navigation.add(next);
        
		table.addMouseListener(new java.awt.event.MouseAdapter(){ 	
			public void mouseClicked(java.awt.event.MouseEvent e){
				int col= table.columnAtPoint(e.getPoint());
				if(col == 8){
					MedicalBrowsingManager manager = new MedicalBrowsingManager();
					medical = (Medical) (((MedicalBrowsingModel) model).getValueAt(table.getSelectedRow(), -1));
					double mediumQuantity = manager.getMediumQuantity(medical.getCode());
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medicals.averagequantitytext") + "\n" + medical.getDescription() + "(" + medical.getProd_code() + "): " + mediumQuantity);
				}
			}
		}); 
		
		table.setDefaultRenderer(Object.class, new ColorTableCellRenderer());
		for (int i = 0; i < pColumwidth.length; i++) {
			table.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
			if (!pColumResizable[i])
				table.getColumnModel().getColumn(i).setMaxWidth(pColumwidth[i]);
		}

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
		
		
		JLabel searchLabel = new JLabel(MessageBundle.getMessage("angal.medicals.find"));
		GridBagConstraints gbc_searchLabel = new GridBagConstraints();
		gbc_searchLabel.anchor = GridBagConstraints.NORTHWEST;
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
				//															
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
		gbc_btnPrint.gridx = 3;
		gbc_btnPrint.gridy = 0;
		panelSearch.add(btnPrint, gbc_btnPrint);

		GridBagConstraints gbc_navigation = new GridBagConstraints();
		gbc_navigation.insets = new Insets(0, 0, 0, 5);
		gbc_navigation.anchor = GridBagConstraints.NORTHWEST;
		gbc_navigation.gridx = 4;
		gbc_navigation.gridy = 0;
		panelSearch.add(navigation, gbc_navigation);
		
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();

		
		MedicalTypeBrowserManager manager = new MedicalTypeBrowserManager();
		pbox = new JComboBox();
		pbox.addItem(MessageBundle.getMessage("angal.medicals.alltypology"));
		ArrayList<MedicalType> type = manager.getMedicalType(); // for
																// efficiency in
																// the sequent
																// for
		for (MedicalType elem : type) {
			pbox.addItem(elem);
		}
		pbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MedicalBrowsingManager manager0 = new MedicalBrowsingManager();
				pSelection = pbox.getSelectedItem().toString();
				if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.alltypology")) == 0){
					model = new MedicalBrowsingModel();
					pSelection = "";
				}
				else 
					model = new MedicalBrowsingModel(pSelection, true);
				TOTAL_ROWS = manager0.getMedicalsTotalRows(pSelection);
				initialiseCombo(pagesCombo, TOTAL_ROWS);
				START_INDEX = 0;
				previous.setEnabled(false);
				if(TOTAL_ROWS < PAGE_SIZE)	next.setEnabled(false);
				else next.setEnabled(true);

				model.fireTableDataChanged();
				table.updateUI();
				searchTextField.setText("");
			}
		});
		buttonPanel.add(pbox);

		JButton buttonNew = new JButton(MessageBundle.getMessage("angal.common.new"));
		buttonNew.setMnemonic(KeyEvent.VK_N);
		buttonNew.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				medical = new Medical(null, new MedicalType("", ""), "", "", 0, 0, 0, 0, 0, 0);
				MedicalEdit newrecord = new MedicalEdit(medical, true, me);
				newrecord.setVisible(true);

				if (medical.getCode() != null)
					medicalInserted();
			}
		});
		if (MainMenu.checkUserGrants("btnpharmaceuticalnew"))
			buttonPanel.add(buttonNew);

		JButton buttonEdit = new JButton(MessageBundle.getMessage("angal.common.edit"));
		buttonEdit.setMnemonic(KeyEvent.VK_E);
		buttonEdit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
					return;
				} else {
					selectedrow = table.getSelectedRow();
					medical = (Medical) (((MedicalBrowsingModel) model).getValueAt(table.getSelectedRow(), -1));
					MedicalEdit editrecord = new MedicalEdit(medical, false, me);
					editrecord.setVisible(true);

					if (medical.getCode() != null)
					medicalUpdated();
				}
			}
		});
		if (MainMenu.checkUserGrants("btnpharmaceuticaledit"))
			buttonPanel.add(buttonEdit);


		JButton buttonDelete = new JButton(MessageBundle.getMessage("angal.common.delete"));
		buttonDelete.setMnemonic(KeyEvent.VK_D);
		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
							MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
					return;
				} else {
					MedicalBrowsingManager manager = new MedicalBrowsingManager();
					Medical m = (Medical) (((MedicalBrowsingModel) model).getValueAt(table.getSelectedRow(), -1));
					int n = JOptionPane
							.showConfirmDialog(null,
									MessageBundle.getMessage("angal.medicals.deletemedical") + " \""
											+ m.getDescription() + "\" ?",
									MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
					
					if ((n == JOptionPane.YES_OPTION) && (manager.deleteMedical(m))) {
						pMedicals.remove(table.getSelectedRow());
						model.fireTableDataChanged();
						table.updateUI();
					}
				}
			}
		});
		if (MainMenu.checkUserGrants("btnpharmaceuticaldel"))
			buttonPanel.add(buttonDelete);
		//MARCO
		JButton buttonStockSheet = new JButton(MessageBundle.getMessage("angal.stocksheet.stocksheetbutton"));
		buttonStockSheet.setMnemonic(KeyEvent.VK_E);
		buttonStockSheet.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent event) {
				if (table.getSelectedRow() < 0) {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow"),
						MessageBundle.getMessage("angal.hospital"), JOptionPane.PLAIN_MESSAGE);
				return;
				} else {
					selectedrow = table.getSelectedRow();////////////////
					medical = (Medical) (((MedicalBrowsingModel) model).getValueAt(table.getSelectedRow(), -1));
					StockSheet stocksheet = new StockSheet(medical, false, me);
					stocksheet.setVisible(true);
					
					if (medical.getCode() != null) medicalUpdated();
				}
			}
		});
		buttonPanel.add(buttonStockSheet);
		
		JButton buttonExport = new JButton(MessageBundle.getMessage("angal.medicals.export"));
		buttonExport.setMnemonic(KeyEvent.VK_X);
		buttonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fcExcel = new JFileChooser();
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xls)", "xls");
				fcExcel.addChoosableFileFilter(excelFilter);
				fcExcel.setFileFilter(excelFilter);
				fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);				
				int iRetVal = fcExcel.showSaveDialog(MedicalBrowser.this);
				if (iRetVal == JFileChooser.APPROVE_OPTION) {
					File exportFile = fcExcel.getSelectedFile();
					if (!exportFile.getName().endsWith("xls"))
						exportFile = new File(exportFile.getAbsoluteFile() + ".xls");

					ExcelExporter xlsExport = new ExcelExporter();
					try {
						xlsExport.exportTableToExcel(table, exportFile);
					} catch (IOException exc) {
						logger.info("Export to excel error : " + exc.getMessage());
					}
				}
			}
		});
		buttonPanel.add(buttonExport);

		JButton buttonStock = new JButton(MessageBundle.getMessage("angal.medicals.stockm"));
		buttonStock.setMnemonic(KeyEvent.VK_S);
		buttonStock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new GenericReportPharmaceuticalOrder(Param.string("PHARMACEUTICALSTOCK"));
			}
		});
		buttonPanel.add(buttonStock);

		JButton buttonOrderList = new JButton(MessageBundle.getMessage("angal.medicals.orderlist"));
		buttonOrderList.setMnemonic(KeyEvent.VK_O);
		buttonOrderList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				new GenericReportPharmaceuticalOrder(Param.string("PHARMACEUTICALORDER"));
			}
		});
		buttonPanel.add(buttonOrderList);

		JButton buttonExpiring = new JButton(MessageBundle.getMessage("angal.medicals.expiring"));
		buttonExpiring.setMnemonic(KeyEvent.VK_X);
		buttonExpiring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				launchExpiringReport();

			}
		});
		buttonPanel.add(buttonExpiring);
		
		
		JButton buttonClose = new JButton(MessageBundle.getMessage("angal.common.close"));
		buttonClose.setMnemonic(KeyEvent.VK_C);
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(buttonClose);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setVisible(true);
		pagesCombo.setEditable(true);
		pagesCombo.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent arg0) {
		 		if(pagesCombo.getItemCount() != 0){
		 			int page_number = (Integer) pagesCombo.getSelectedItem();	
			 		START_INDEX = (page_number-1) * PAGE_SIZE;
			 		pSelection = pbox.getSelectedItem().toString();
					if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.alltypology")) == 0){
						pSelection = "";
					}
	        		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE, pSelection);
					model.fireTableDataChanged();
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
		 		}
		 		
    			table.updateUI();
		 	}
		 }); 
	}

	protected void launchExpiringReport() {

		ArrayList<String> options = new ArrayList<String>();
		options.add(MessageBundle.getMessage("angal.medicals.today"));
		options.add(MessageBundle.getMessage("angal.medicals.thismonth"));
		options.add(MessageBundle.getMessage("angal.medicals.othermonth"));

		Icon icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
		String option = (String) JOptionPane.showInputDialog(MedicalBrowser.this,
				MessageBundle.getMessage("angal.medicals.pleaseselectperiod"),
				MessageBundle.getMessage("angal.medicals.expiringreport"), JOptionPane.INFORMATION_MESSAGE, icon,
				options.toArray(), options.get(0));

		if (option == null)
			return;

		String from = null;
		String to = null;

		int i = 0;

		if (options.indexOf(option) == i) {
			GregorianCalendar gc = TimeTools.getServerDateTime();

			from = formatDateTimeReport(gc);
			to = from;
		}
		if (options.indexOf(option) == ++i) {
			GregorianCalendar gc = TimeTools.getServerDateTime();
			gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
			from = formatDateTimeReport(gc);

			gc.set(GregorianCalendar.DAY_OF_MONTH, gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			to = formatDateTimeReport(gc);
		}
		if (options.indexOf(option) == ++i) {
			GregorianCalendar monthYear;
			icon = new ImageIcon("rsc/icons/calendar_dialog.png"); //$NON-NLS-1$
			JMonthYearChooser monthYearChooser = new JMonthYearChooser();
			int r = JOptionPane.showConfirmDialog(MedicalBrowser.this, monthYearChooser,
					MessageBundle.getMessage("angal.billbrowser.month"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, icon);

			if (r == JOptionPane.OK_OPTION) {
				monthYear = monthYearChooser.getDate();
			} else {
				return;
			}

			GregorianCalendar gc = TimeTools.getServerDateTime();
			gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
			gc.set(GregorianCalendar.MONTH, monthYear.get(GregorianCalendar.MONTH));
			gc.set(GregorianCalendar.YEAR, monthYear.get(GregorianCalendar.YEAR));
			from = formatDateTimeReport(gc);

			gc.set(GregorianCalendar.DAY_OF_MONTH, gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			to = formatDateTimeReport(gc);
		}
		new GenericReportFromDateToDate(from, to, "PharmaceuticalExpiration", false);
	}

	private String formatDateTimeReport(GregorianCalendar date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
		return sdf.format(date.getTime());
	}

	class MedicalBrowsingModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MedicalBrowsingModel(String s) {
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals = manager.getMedicals(s);
			medSearch = pMedicals;
		}

		public MedicalBrowsingModel(String s, int start_index, int page_size) {
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals = manager.getMedicals(s, start_index, page_size);
			medSearch = pMedicals;
		}
		
		public MedicalBrowsingModel(String s, boolean ok) { //
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals = manager.getMedicals4(s);
			medSearch = pMedicals;
		}
 
		public MedicalBrowsingModel() {
			
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals = manager.getMedicals2();
			medSearch = pMedicals;
		}
	
		public MedicalBrowsingModel(int start_index, int page_size, String typologie) {
			
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals2 =  manager.getMedicals2();
			pMedicals = manager.getMedicals(typologie, start_index, page_size);
			medSearch = pMedicals;
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
				return String.class;
			} else if (c == 5) {
				return String.class;
			} else if (c == 6) {
				return Boolean.class;
			}else if (c == 7) {
				return String.class;
			}else if (c == 8) {
				return String.class;
			}
			
			return null;
		}

		public int getRowCount() {
			if (medSearch == null)
				return 0;
			return medSearch.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		
		
		public Object getValueAt(int r, int c) {
			Medical med = medSearch.get(r);
			//MedicalBrowsingManager manager = new MedicalBrowsingManager();
			double actualQty = med.getInitialqty() + med.getInqty() - med.getOutqty();
			double minQuantity = med.getMinqty();
			if (c == -1) {
				return med;
			} else if (c == 0) {
				return med.getType().getDescription();
			} else if (c == 1) {
				return med.getProd_code();
			} else if (c == 2) {
				return med.getDescription();
			} else if (c == 3) {
				return String.valueOf(med.getPcsperpck());
			} else if (c == 4) {
				return String.valueOf(actualQty);
			} else if (c == 5) {
				return String.valueOf(minQuantity);
			} else if (c == 6) {
				if (actualQty == 0)
					return true;
				else
					return false;
			}else if (c == 7) {
				return med.getLastprice();			
			}else if (c == 8) {
				return MessageBundle.getMessage("angal.medicals.averagequantitydetails"); 
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}

	class ColorTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(Color.BLACK);
			Medical med = medSearch.get(row);
			double actualQty = med.getInitialqty() + med.getInqty() - med.getOutqty();
			if (actualQty <= med.getMinqty())
				cell.setForeground(Color.RED); // under critical level
			if (((Boolean) table.getValueAt(row, 6)).booleanValue())
				cell.setForeground(Color.GRAY); // out of stock
			return cell;
		}
	}
	
	private void filterMedicine() {
		//String s = searchTextField.getText() + lastKey;
		String s = searchTextField.getText();
		s.trim();
		medSearch = new ArrayList<Medical>();

		for (Medical med : pMedicals2) {
			if (!s.equals("")) {
				String name = med.getSearchString();
				if (name.contains(s.toLowerCase()))
					// if (name.indexOf(s.toLowerCase())>-1)
					medSearch.add(med);
			} else {
				medSearch.add(med);
			}
		}

		if (table.getRowCount() == 0) {
			medicine = null;
		}
		if (table.getRowCount() == 1) {
			medicine = (Medical) table.getValueAt(0, -1);
		}
		table.updateUI();
		searchTextField.requestFocus();
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
