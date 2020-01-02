/**
 * 11-dic-2005
 * author bob
 */
package org.isf.medicals.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.gui.MedicalBrowser.MedicalBrowsingModel;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicals.model.MedicalLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.stat.manager.GenericReportFromDateToDate;
import org.isf.stat.manager.GenericReportPharmaceuticalOrder;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.jobjects.JMonthYearChooser;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.time.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class shows a complete extended list of medical drugs,
 * supplies-sundries, diagnostic kits -reagents, laboratory chemicals. It is
 * possible to filter data with a selection combo box and edit-insert-delete
 * records
 * 
 * @author bob modified by alex: - product code - pieces per packet
 * 
 */

public class MedicalBrowserWithLot extends ModalJFrame { // implements
													// RowSorterListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(MedicalBrowserWithLot.class);
	private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy"; //$NON-NLS-1$
	
	private int expiringValue=3;
	private int expiringUnitIndex=0;
	

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
	private List<MedicalLot> pMedicals;
	private List<MedicalLot> pMedicals2;
	List<MedicalLot> medSearch = new ArrayList<MedicalLot>();
	private String[] pColums = { "", MessageBundle.getMessage("angal.medicals.typem"),
			MessageBundle.getMessage("angal.medicals.code"), MessageBundle.getMessage("angal.medicals.descriptionm"),
			MessageBundle.getMessage("angal.medicals.lotid"), MessageBundle.getMessage("angal.medicals.lotexpiring"),
			MessageBundle.getMessage("angal.medicals.pcsperpck"), MessageBundle.getMessage("angal.medicals.stockm"),
			MessageBundle.getMessage("angal.medicals.critlevelm"),
			MessageBundle.getMessage("angal.medicals.outofstockm") };
	private int[] pColumwidth = { 50, 100, 100, 400, 120, 120,  60, 60, 80, 100 };
	private boolean[] pColumResizable = { false, true, true, true, true, true, true, true, true, true };
	private MedicalLot medical;
	private DefaultTableModel model;
	private JTable table;
	private JCheckBox chkOpenAll;
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
	
	public MedicalBrowserWithLot() {
		me = this;
		setTitle(MessageBundle.getMessage("angal.medicals.pharmaceuticalbrowsing"));
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		int quater=screensize.width / 4;
		pfrmWidth = quater*3;//940; // screensize.width / 2;
		pfrmHeight = screensize.height / 2;
		setBounds((screensize.width - pfrmWidth) / 2, screensize.height / 4, pfrmWidth, pfrmHeight);
		
		model = new MedicalBrowsingModel();
		
		TOTAL_ROWS = model.getRowCount();
		initialiseCombo(pagesCombo, TOTAL_ROWS);
		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE);
		table = new JTable(model);
		previous.setEnabled(false);
		if(PAGE_SIZE > TOTAL_ROWS) next.setEnabled(false);
		table.setAutoCreateRowSorter(true);
		
		table.setDefaultRenderer(Object.class, new ColorTableCellRenderer());
		for (int i = 0; i < pColumwidth.length; i++) {
			table.getColumnModel().getColumn(i).setMinWidth(pColumwidth[i]);
			if (!pColumResizable[i])
				table.getColumnModel().getColumn(i).setMaxWidth(pColumwidth[i]);
		}

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int col=table.columnAtPoint(e.getPoint());
				if(col==0){
					MedicalBrowsingModel model=(MedicalBrowsingModel) table.getModel();
					int row=table.rowAtPoint(e.getPoint());
					
					MedicalLot parent=model.getMedicalLot(row);
					if(!parent.isOpen() && parent.hasChildren()){
						parent.setOpen(true);
						if(row+1>medSearch.size()){
							medSearch.addAll(parent.getChildren());
						}
						else{
							medSearch.addAll(row+1, parent.getChildren());
						}
					}
					else if (parent.isOpen() && parent.hasChildren()){
						parent.setOpen(false);
						List<MedicalLot> children=parent.getChildren();
						for (Iterator<MedicalLot> iterator = children.iterator(); iterator.hasNext();) {
							medSearch.remove(row+1);
							iterator.next();
							
						}
					}
				}
				table.updateUI();
			}
		});

		JPanel panelSearch = new JPanel();
		getContentPane().add(panelSearch, BorderLayout.NORTH);

		JLabel expiringWaring = new JLabel(MessageBundle.getMessage("angal.medicals.expiringwarning"));
		final JSpinner jSpinnerExpiringValue=new JSpinner();
		jSpinnerExpiringValue.setPreferredSize(new Dimension(75, 30));
		jSpinnerExpiringValue.setValue(3);
		JComponent comp = jSpinnerExpiringValue.getEditor();
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
	    formatter.setCommitsOnValidEdit(true);
	    jSpinnerExpiringValue.addChangeListener(new ChangeListener() {

	        @Override
	        public void stateChanged(ChangeEvent e) {
	             expiringValue=Integer.parseInt( String.valueOf(jSpinnerExpiringValue.getValue()));
	             table.updateUI();
	        }
	    });
		
		final JComboBox jComboExpiringUnit=new JComboBox();
		jComboExpiringUnit.addItem(MessageBundle.getMessage("angal.medicals.month"));
		jComboExpiringUnit.addItem(MessageBundle.getMessage("angal.medicals.day"));
		jComboExpiringUnit.addItem(MessageBundle.getMessage("angal.medicals.year"));
		
		jComboExpiringUnit.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				expiringUnitIndex=jComboExpiringUnit.getSelectedIndex();
				table.updateUI();
			}
		});
	    next.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent ae) {
            	if(!previous.isEnabled()) previous.setEnabled(true);
            	
            	pSelection = pbox.getSelectedItem().toString();
				if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.allm")) == 0){
					pSelection = "";
				}
            	START_INDEX += PAGE_SIZE;
        		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE);
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
				if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.allm")) == 0){
					pSelection = "";
				}
        		START_INDEX -= PAGE_SIZE;
        		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE);
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
        
		panelSearch.add(previous); 
		panelSearch.add(pagesCombo);
		panelSearch.add(under);
		panelSearch.add(next);
        
		panelSearch.add(expiringWaring);
		panelSearch.add(jSpinnerExpiringValue);
		panelSearch.add(jComboExpiringUnit);
		
		
		JLabel searchLabel = new JLabel(MessageBundle.getMessage("angal.medicals.find"));
		panelSearch.add(searchLabel);

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

		panelSearch.add(searchTextField);
		searchTextField.setColumns(20);
		getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();

		
		chkOpenAll = new JCheckBox(MessageBundle.getMessage("angal.medicals.openall"));
		chkOpenAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				int taille=medSearch.size();
				
				List<MedicalLot> copy=Arrays.asList(new MedicalLot[taille]);
			
				Collections.copy(copy, medSearch);
				
				if(chkOpenAll.isSelected()){
//					int row=0;
					MedicalBrowsingModel model=(MedicalBrowsingModel)table.getModel();
					for (int row=0; row<model.getRowCount();row++) {
						
						int col=0;
						if(col==0){
							MedicalLot parent=model.getMedicalLot(row);
							if(parent.isParent() && !parent.isOpen() && parent.hasChildren()){
								parent.setOpen(true);
								if(row+1>medSearch.size()){
									medSearch.addAll(parent.getChildren());
								}
								else{
									medSearch.addAll(row+1, parent.getChildren());
								}
							}
							
						}
						table.updateUI();
					}	
				}
				else{
					MedicalBrowsingModel model=(MedicalBrowsingModel)table.getModel();
					for (int row=0; row<model.getRowCount();row++) {
						int col=0;
						if(col==0){
							MedicalLot parent=model.getMedicalLot(row);
							if(parent.isParent() && parent.isOpen() && parent.hasChildren()){
								parent.setOpen(false);
								List<MedicalLot> children=parent.getChildren();
								for (Iterator<MedicalLot> iterator = children.iterator(); iterator.hasNext();) {
									medSearch.remove(row+1);
									iterator.next();
								}
							}
						}
						table.updateUI();
					}		
				}
			}
		});
		buttonPanel.add(chkOpenAll);
		
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
		pbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				pSelection = pbox.getSelectedItem().toString();
				if (pSelection.compareTo(MessageBundle.getMessage("angal.medicals.allm")) == 0)
					model = new MedicalBrowsingModel();
				else
					model = new MedicalBrowsingModel(pSelection);
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
				Medical med = new Medical(null, new MedicalType("", ""), "", "", 0, 0, 0, 0, 0, 0);
				// medical will reference the new record
				medical=new MedicalLot();
				medical.setMedical(med);
				medical.setParent(true);
				MedicalEdit newrecord = new MedicalEdit(med, true, me);
				newrecord.setVisible(true);

				if (med.getCode() != null)
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
					medical = (MedicalLot) (((MedicalBrowsingModel) model).getValueAt(table.getSelectedRow(), -1));
					MedicalEdit editrecord = new MedicalEdit(medical.getMedical(), false, me);
					editrecord.setVisible(true);

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
					MedicalLot m = (MedicalLot) (((MedicalBrowsingModel) model).getValueAt(table.getSelectedRow(), -1));
					if(m.isParent()){
						int n = JOptionPane
								.showConfirmDialog(null,
										MessageBundle.getMessage("angal.medicals.deletemedical") + " \""
												+ m.getMedical().getDescription() + "\" ?",
										MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);

						if ((n == JOptionPane.YES_OPTION) && (manager.deleteMedical(m.getMedical()))) {
							int rowNumber=m.getChildren().size()+1;
							int row=table.getSelectedRow();
							for(int i=0;i<rowNumber;i++){
								pMedicals.remove(row);
							}
							model.fireTableDataChanged();
							table.updateUI();
						}
					}
					else{
						
						JOptionPane.showMessageDialog(null,  MessageBundle.getMessage("angal.medicals.selectmedicalrow"));
					}
				}
			}
		});
		if (MainMenu.checkUserGrants("btnpharmaceuticaldel"))
			buttonPanel.add(buttonDelete);

		JButton buttonExport = new JButton(MessageBundle.getMessage("angal.medicals.export"));
		buttonExport.setMnemonic(KeyEvent.VK_X);
		buttonExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				JFileChooser fcExcel = new JFileChooser();
				FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel (*.xls)", "xls");
				fcExcel.setFileFilter(excelFilter);
				fcExcel.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int iRetVal = fcExcel.showSaveDialog(MedicalBrowserWithLot.this);
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
	        		model = new MedicalBrowsingModel(START_INDEX, PAGE_SIZE);
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
		String option = (String) JOptionPane.showInputDialog(MedicalBrowserWithLot.this,
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
			int r = JOptionPane.showConfirmDialog(MedicalBrowserWithLot.this, monthYearChooser,
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
			pMedicals = manager.getMedicalsWithLot(s);
			medSearch = pMedicals;
		}

		public MedicalBrowsingModel() {
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals = manager.getMedicalsWithLot();
			pMedicals2 = pMedicals;
			medSearch = pMedicals;
		}

		public MedicalBrowsingModel(int sTART_INDEX, int pAGE_SIZE) {
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			pMedicals = manager.getMedicalsWithLot(sTART_INDEX, pAGE_SIZE);
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
			}else if (c == 6) {
				return String.class;
			} else if (c == 7) {
				return String.class;
			}else if (c == 8) {
				return String.class;
			}else if (c == 9) {
				return Boolean.class;
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
			MedicalLot medLot = medSearch.get(r);
			double actualQty = medLot.getMedical().getInitialqty() + medLot.getMedical().getInqty() - medLot.getMedical().getOutqty();
			double minQuantity = medLot.getMedical().getMinqty();
			
			Lot lot=medLot.getLot();
			
			if (c == -1) {
				return medLot;
			} else if (c == 1) {
				if (! medLot.isParent()) return "";
				return medLot.getMedical().getType().getDescription();
			} else if (c == 2) {
				if (! medLot.isParent()) return "";
				return medLot.getMedical().getProd_code();
			} else if (c == 3) {
				if (! medLot.isParent()) return "";
				return medLot.getMedical().getDescription();
			} else if (c == 4) {
				if(lot==null && !(medLot.isParent() && medLot.getChildren().size()==1)){
					if(medLot.hasChildren()){
						return "Multiple";
					}
					else{
						return "";
					}
				}
				if(lot==null) return medLot.getChildren().get(0).getLot().getCode();
				return String.valueOf(medLot.getLot().getCode());
			} else if (c == 5) {
				if(lot==null && !(medLot.isParent() && medLot.getChildren().size()==1)){
					if(medLot.hasChildren()){
						return "Multiple";
					}
					else{
						return "";
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
				if(lot==null) return sdf.format(medLot.getChildren().get(0).getLot().getDueDate().getTime());
				return sdf.format(medLot.getLot().getDueDate().getTime());
			} else if (c == 6) {
				if (! medLot.isParent()) return "";
				return String.valueOf(medLot.getMedical().getPcsperpck());
			} else if (c == 7) {
				if(lot==null) return String.valueOf(actualQty);
				return String.valueOf(medLot.getLot().getQuantity());
			}else if (c == 8) {
				
				if(lot==null) return String.valueOf(minQuantity);
				return "";
			} else if (c == 9) {
				// if(actualQty<=minQuantity)return true;
				if (actualQty == 0)
					return true;
				else
					return false;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

		public MedicalLot getMedicalLot(int row) {
			return  medSearch.get(row);
		}

	}

	class ColorTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = null;
			if(column==0){
				MedicalBrowsingModel model=(MedicalBrowsingModel) table.getModel();
				MedicalLot medLot=model.getMedicalLot(row);
				
				final JLabel button=new JLabel("");
				button.setHorizontalAlignment(JLabel.CENTER);
				
				if(medLot.isParent() && medLot.hasChildren()){
					button.setIcon(new ImageIcon("rsc/icons/down16x16blue.png"));
				}
				
				if(medLot.isParent() && medLot.isOpen() &&  medLot.hasChildren()){
					button.setIcon(new ImageIcon("rsc/icons/up16x16blue.png"));
				}
				cell=button;
			}
			else{
				cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
			cell.setForeground(Color.BLACK);
			MedicalLot medLot=medSearch.get(row);
			Medical med = medLot.getMedical();
			Lot lot= medLot.getLot();
			GregorianCalendar cal3MonthLater=new GregorianCalendar();
			
			int calUnit=GregorianCalendar.MONTH;
			if(expiringUnitIndex==1){
				calUnit=GregorianCalendar.DAY_OF_MONTH;
			}
			if(expiringUnitIndex==2){
				calUnit=GregorianCalendar.YEAR;
			}
			cal3MonthLater.add(calUnit, expiringValue);
			
			double actualQty = med.getInitialqty() + med.getInqty() - med.getOutqty();
			
			List<MedicalLot> children=medLot.getChildren();
			
			if(lot==null && medLot.isParent() && children.size()==1){
				lot=children.get(0).getLot();
			}
			if (actualQty <= med.getMinqty() || (lot!=null && cal3MonthLater.after(lot.getDueDate())))
				cell.setForeground(Color.RED); // under critical level
			if (((Boolean) table.getValueAt(row, 9)).booleanValue())
				cell.setForeground(Color.GRAY); // out of stock
			return cell;
		}
	}

	private void filterMedicine() {
		// String s = searchTextField.getText() + lastKey;
		String s = searchTextField.getText();
		s.trim();
		medSearch = new ArrayList<MedicalLot>();

		for (MedicalLot medLot : pMedicals2) {
			Medical med=medLot.getMedical();
			if (!s.equals("")) {
				String name = med.getSearchString();
				if (name.contains(s.toLowerCase()))
					// if (name.indexOf(s.toLowerCase())>-1)
					medSearch.add(medLot);
			} else {
				medSearch.add(medLot);
			}
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
