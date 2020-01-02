package org.isf.accounting.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.BillItems;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.lab.manager.LabManager;
import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryRow;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.menu.gui.MainMenu;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.parameters.manager.Param;
import org.isf.patient.model.Patient;
import org.isf.priceslist.manager.PriceListManager;
import org.isf.priceslist.model.ItemGroup;
import org.isf.priceslist.model.Price;
import org.isf.therapy.manager.TherapyManager;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.OhDefaultCellRenderer;
import org.isf.utils.jobjects.OhTableOperationModel;


import javax.media.jai.PropertySourceImpl;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class SelectPrescriptions extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableTherapy;
	Patient patient;
	
	List<TherapyRow> therapies;
	List<Laboratory> laboratories;
	List<OperationRow> operations;
	private Map<TherapyRow, Integer>mapTherapies=new HashMap<TherapyRow, Integer>();

	private EventListenerList selectionListener = new EventListenerList();
	
	private JPanel pTitle;
	private JPanel pTherapy;
	private JPanel pExams;
	private JPanel pOperations;
	private JPanel pTotal;
	private JPanel pButton;
	private JLabel lblTitle;
	private JScrollPane scrolTherapy;
	private JTable tableExamp;
	private JPanel panelExamTotal;
	private JLabel lblExamTotal;
	private JLabel lblExamTotalVal;
	private JLabel lblExamTotalSelection;
	private JLabel lblExamTotalSelectionVal;
	private JScrollPane scrollOperation;
	private JLabel lblOpeTotalSelectionVal;
	private JLabel lblOpeTotalSelection;
	private JLabel lblOpeTotalVal;
	private JLabel lblOpeTotal;
	private JTable tableOperation;
	private JLabel lblTotalVal;
	private JLabel lblTotalSelectionVal;
	
	private JLabel lblTherapyTotalSelectionVal;
	private JCheckBox chckbxSlectionnerToutesLes;
	private JCheckBox chckbxSlectionnerTousLes_1;
	private JCheckBox chckbxSlectionnerTousLes;
	private JCheckBox chBoxSelectAll;
	private JLabel lblNewMed;
	private JLabel lblExamens;
	private JLabel lblOprations;
	

	OhDefaultCellRenderer cellRenderer = new OhDefaultCellRenderer();
	
	public interface TherapySelectionListener extends EventListener {
		public void therapiesSelected(Map<TherapyRow, Integer> therapies);
	}
	
	public interface PrescriptionSelectionListener extends EventListener {
		public void prescriptionSelected(List<BillItems> prescriptions);
	}
	
	public void addPrescriptionSelectedListener(PrescriptionSelectionListener listener){
		selectionListener.add(PrescriptionSelectionListener.class, listener);
	}

	public void addSelectionListiner(TherapySelectionListener listener){
		selectionListener.add(TherapySelectionListener.class, listener);
	}
	
	private void fireSelectedPrescription(){
		new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX+1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
		};
		EventListener[] listeners = selectionListener.getListeners(PrescriptionSelectionListener.class);
		
		List<BillItems> prescriptions = new ArrayList<BillItems>();		
		//factory of billitems
		BillItems billItemTemp = null;
		/****** adding  therapies *******/
		TherapyRow currentTherapiRow;
		int[] rowsSelected = tableTherapy.getSelectedRows();
		for(int i = 0; i < rowsSelected.length ;i++){
			currentTherapiRow = therapies.get(rowsSelected[i]);
			billItemTemp = new BillItems();			
			BillBrowserManager billManager=new BillBrowserManager();
			Price price = billManager.getPrice(String.valueOf(currentTherapiRow.getMedical().getCode()), ItemGroup.MEDICAL, patient);
			/*** getting price without reductions ****/
			Price brut = billManager.getPriceFromListWithoutReduction(String.valueOf(currentTherapiRow.getMedical().getCode()), ItemGroup.MEDICAL, patient);
			double priceBrut = 0.0;
			if(brut!=null)
				priceBrut = brut.getPrice();
			/*****************************************/
			billItemTemp.setItemAmount(price.getPrice());
			billItemTemp.setItemDescription(currentTherapiRow.getMedical().getDescription());
			billItemTemp.setItemGroup(ItemGroup.MEDICAL.getCode());
			billItemTemp.setItemId(currentTherapiRow.getMedical().getCode()+"");
			billItemTemp.setItemQuantity((int)Math.ceil(currentTherapiRow.getQty()));
			billItemTemp.setPrescriptionId(currentTherapiRow.getTherapyID());
			billItemTemp.setPrice(true);
			billItemTemp.setPriceID(price.getId()+"");
			billItemTemp.setItemAmountBrut(priceBrut);
			prescriptions.add(billItemTemp);
		}  
		/****** adding  operations *******/
		OperationRow currentOpeRow = null;
		rowsSelected = tableOperation.getSelectedRows();
		for(int i = 0; i < rowsSelected.length ;i++){
			currentOpeRow = operations.get(rowsSelected[i]);
			billItemTemp = new BillItems();			
			BillBrowserManager billManager = new BillBrowserManager();
			String opeID = currentOpeRow.getOperationId();
			OperationBrowserManager opeManager = new OperationBrowserManager();
			Operation ope = opeManager.getOperationByCode(opeID);
			Price price = billManager.getPrice(ope.getCode(), ItemGroup.OPERATION, patient);
			/*** getting price without reductions ****/
			Price brut = billManager.getPriceFromListWithoutReduction(ope.getCode(), ItemGroup.OPERATION, patient);
			double priceBrut = 0.0;
			if(brut!=null)
				priceBrut = brut.getPrice();
			/*****************************************/
			billItemTemp.setItemAmount(price.getPrice());
			billItemTemp.setItemDescription(ope.getDescription());
			billItemTemp.setItemGroup(ItemGroup.OPERATION.getCode());
			billItemTemp.setItemId(ope.getCode());
			billItemTemp.setItemQuantity(1);
			billItemTemp.setPrescriptionId(currentOpeRow.getId());
			billItemTemp.setPrice(true);
			billItemTemp.setPriceID(price.getId()+"");
			billItemTemp.setItemAmountBrut(priceBrut);
			prescriptions.add(billItemTemp);
		}  
		/****** adding  exams *******/
		Laboratory currentLaboratory = null;
		rowsSelected = tableExamp.getSelectedRows();
		for(int i = 0; i < rowsSelected.length ;i++){
			currentLaboratory = laboratories.get(rowsSelected[i]);
			billItemTemp = new BillItems();			
			BillBrowserManager billManager = new BillBrowserManager();
			Price price = billManager.getPrice(currentLaboratory.getExam().getCode(), ItemGroup.EXAM, patient);
			/*** getting price without reductions ****/
			Price brut = billManager.getPriceFromListWithoutReduction(currentLaboratory.getExam().getCode(), ItemGroup.EXAM, patient);
			double priceBrut = 0.0;
			if(brut!=null)
				priceBrut = brut.getPrice();
			/*****************************************/
			billItemTemp.setItemAmount(price.getPrice());
			billItemTemp.setItemDescription(currentLaboratory.getExam().getDescription());
			billItemTemp.setItemGroup(ItemGroup.EXAM.getCode());
			billItemTemp.setItemId(currentLaboratory.getExam().getCode());
			billItemTemp.setItemQuantity(1);
			billItemTemp.setPrescriptionId(currentLaboratory.getCode());
			billItemTemp.setPrice(true);
			billItemTemp.setPriceID(price.getId()+"");
			billItemTemp.setItemAmountBrut(priceBrut);
			prescriptions.add(billItemTemp);
		}  
		for (int i = 0; i < listeners.length; i++)
			((PrescriptionSelectionListener)listeners[i]).prescriptionSelected(prescriptions);
	}
	
	private void fireSelectedTherapies(Map<TherapyRow, Integer> selectedTherapies){
		new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX+1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			
		};
		EventListener[] listeners = selectionListener.getListeners(TherapySelectionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((TherapySelectionListener)listeners[i]).therapiesSelected(selectedTherapies);
	}
	/**
	 * Create the frame.
	 */
	//public SelectPrescriptions(JFrame owner, Patient patient) {
	public SelectPrescriptions(JDialog owner, Patient patient) {	
		super(owner, true);
		this.patient=patient;
		
		setTitle(MessageBundle.getMessage("angal.patientbill.presciption"));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 767, 666);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{706, 0};
		gbl_contentPane.rowHeights = new int[]{36, 176, 156, 124, 38, 42, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
				pTitle = new JPanel();
				GridBagConstraints gbc_pTitle = new GridBagConstraints();
				gbc_pTitle.fill = GridBagConstraints.BOTH;
				gbc_pTitle.insets = new Insets(0, 0, 5, 0);
				gbc_pTitle.gridx = 0;
				gbc_pTitle.gridy = 0;
				contentPane.add(pTitle, gbc_pTitle);
						GridBagLayout gbl_pTitle = new GridBagLayout();
						gbl_pTitle.columnWidths = new int[]{268, 170, 0};
						gbl_pTitle.rowHeights = new int[]{14, 0, 0};
						gbl_pTitle.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
						gbl_pTitle.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
						pTitle.setLayout(gbl_pTitle);
						
								//lblTitle = new JLabel(MessageBundle.getMessage("angal.therapy.therapyofpatient"));
								lblTitle = new JLabel("");
								lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 14));
								lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
								lblTitle.setText(MessageBundle.getMessage("angal.therapy.therapyofpatientname")+" "+this.patient.getName() );
								GridBagConstraints gbc_lblTitle = new GridBagConstraints();
								gbc_lblTitle.gridwidth = 2;
								gbc_lblTitle.insets = new Insets(0, 0, 5, 0);
								gbc_lblTitle.anchor = GridBagConstraints.NORTH;
								gbc_lblTitle.gridx = 0;
								gbc_lblTitle.gridy = 0;
								pTitle.add(lblTitle, gbc_lblTitle);
								
								lblNewMed = new JLabel("   "+MessageBundle.getMessage("angal.selectprescription.medicallist"));
								lblNewMed.setFont(new Font("Tahoma", Font.PLAIN, 14));
								lblNewMed.setHorizontalAlignment(SwingConstants.LEFT);
								GridBagConstraints gbc_lblNewMed = new GridBagConstraints();
								gbc_lblNewMed.anchor = GridBagConstraints.WEST;
								gbc_lblNewMed.insets = new Insets(0, 0, 0, 5);
								gbc_lblNewMed.gridx = 0;
								gbc_lblNewMed.gridy = 1;
								pTitle.add(lblNewMed, gbc_lblNewMed);
		
				pTherapy = new JPanel();
				GridBagConstraints gbc_pTherapy = new GridBagConstraints();
				gbc_pTherapy.fill = GridBagConstraints.BOTH;
				gbc_pTherapy.insets = new Insets(0, 0, 5, 0);
				gbc_pTherapy.gridx = 0;
				gbc_pTherapy.gridy = 1;
				contentPane.add(pTherapy, gbc_pTherapy);
				pTherapy.setLayout(new BorderLayout(0, 0));
				
						scrolTherapy = new JScrollPane();
						pTherapy.add(scrolTherapy);
						
								tableTherapy = new JTable();
								tableTherapy.setSelectionModel(new DefaultListSelectionModel() {
								    @Override
								    public void setSelectionInterval(int index0, int index1) {
								        if(super.isSelectedIndex(index0)) {
								            super.removeSelectionInterval(index0, index1);
								        }
								        else {
								            super.addSelectionInterval(index0, index1);
								        }
								    }
								});
								tableTherapy.setModel(new TherapyRowModel());
								tableTherapy.getColumnModel().getColumn(1).setPreferredWidth(250);
								tableTherapy.getColumnModel().getColumn(2).setPreferredWidth(50);
								tableTherapy.getColumnModel().getColumn(3).setPreferredWidth(75);
								tableTherapy.getColumnModel().getColumn(5).setPreferredWidth(75);
								tableTherapy.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
								tableTherapy.setRowSelectionAllowed(true);
																
								tableTherapy.setDefaultRenderer(Object.class, cellRenderer);
								tableTherapy.setDefaultRenderer(Double.class, cellRenderer);

								tableTherapy.addMouseMotionListener(new MouseMotionListener() {			
									@Override
									public void mouseMoved(MouseEvent e) {
										// TODO Auto-generated method stub
										JTable aTable =  (JTable)e.getSource();
								        int itsRow = aTable.rowAtPoint(e.getPoint());
								        if(itsRow>=0){
								        	cellRenderer.setHoveredRow(itsRow);
								        }
								        else{
								        	cellRenderer.setHoveredRow(-1);
								        }
								        aTable.repaint();
									}
									
									@Override
									public void mouseDragged(MouseEvent e) {
										// TODO Auto-generated method stub
										
									}
								});
								tableTherapy.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseExited(MouseEvent e) {
										cellRenderer.setHoveredRow(-1);
									}
								});
								
								scrolTherapy.setViewportView(tableTherapy);
								
								JPanel panelTherapyTotal = new JPanel();
								pTherapy.add(panelTherapyTotal, BorderLayout.SOUTH);
								GridBagLayout gbl_panelTherapyTotal = new GridBagLayout();
								gbl_panelTherapyTotal.columnWidths = new int[]{288, 89, 23, 69, 23, 0};
								gbl_panelTherapyTotal.rowHeights = new int[]{23, 0, 0};
								gbl_panelTherapyTotal.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
								gbl_panelTherapyTotal.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
								panelTherapyTotal.setLayout(gbl_panelTherapyTotal);
								
								chckbxSlectionnerTousLes = new JCheckBox(MessageBundle.getMessage("angal.selectprescription.selectalldrug"));
								chckbxSlectionnerTousLes.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent arg0) {
										if(chckbxSlectionnerTousLes.isSelected()){
											selectRows(tableTherapy,0,tableTherapy.getModel().getRowCount()-1);
										}
										else{
											tableTherapy.clearSelection();
										}
										checkIfOthersAreCheked();
									}
								});
								GridBagConstraints gbc_chckbxSlectionnerTousLes = new GridBagConstraints();
								gbc_chckbxSlectionnerTousLes.anchor = GridBagConstraints.NORTHWEST;
								gbc_chckbxSlectionnerTousLes.insets = new Insets(0, 0, 5, 5);
								gbc_chckbxSlectionnerTousLes.gridx = 0;
								gbc_chckbxSlectionnerTousLes.gridy = 0;
								panelTherapyTotal.add(chckbxSlectionnerTousLes, gbc_chckbxSlectionnerTousLes);
								
								JLabel lblTherapyTotal = new JLabel(MessageBundle.getMessage("angal.selectprescription.totaldrug"));
								GridBagConstraints gbc_lblTherapyTotal = new GridBagConstraints();
								gbc_lblTherapyTotal.anchor = GridBagConstraints.WEST;
								gbc_lblTherapyTotal.insets = new Insets(0, 0, 5, 5);
								gbc_lblTherapyTotal.gridx = 1;
								gbc_lblTherapyTotal.gridy = 0;
								panelTherapyTotal.add(lblTherapyTotal, gbc_lblTherapyTotal);
								
								JLabel lblTherapyTotalVal = new JLabel("lblTotVal");
								lblTherapyTotalVal.setFont(new Font("Tahoma", Font.PLAIN, 16));
								GridBagConstraints gbc_lblTherapyTotalVal = new GridBagConstraints();
								gbc_lblTherapyTotalVal.anchor = GridBagConstraints.WEST;
								gbc_lblTherapyTotalVal.insets = new Insets(0, 0, 5, 5);
								gbc_lblTherapyTotalVal.gridx = 2;
								gbc_lblTherapyTotalVal.gridy = 0;
								panelTherapyTotal.add(lblTherapyTotalVal, gbc_lblTherapyTotalVal);
								lblTherapyTotalVal.setText(getTotalPriceOfJtable(tableTherapy,6)+"");
								
								JLabel lblTherapyTotalSelection = new JLabel(MessageBundle.getMessage("angal.selectprescription.totalselection"));
								GridBagConstraints gbc_lblTherapyTotalSelection = new GridBagConstraints();
								gbc_lblTherapyTotalSelection.anchor = GridBagConstraints.WEST;
								gbc_lblTherapyTotalSelection.insets = new Insets(0, 0, 5, 5);
								gbc_lblTherapyTotalSelection.gridx = 3;
								gbc_lblTherapyTotalSelection.gridy = 0;
								panelTherapyTotal.add(lblTherapyTotalSelection, gbc_lblTherapyTotalSelection);
								
								lblTherapyTotalSelectionVal = new JLabel("0.0");
								lblTherapyTotalSelectionVal.setFont(new Font("Tahoma", Font.PLAIN, 16));
								GridBagConstraints gbc_lblTherapyTotalSelectionVal = new GridBagConstraints();
								gbc_lblTherapyTotalSelectionVal.insets = new Insets(0, 0, 5, 0);
								gbc_lblTherapyTotalSelectionVal.anchor = GridBagConstraints.WEST;
								gbc_lblTherapyTotalSelectionVal.gridx = 4;
								gbc_lblTherapyTotalSelectionVal.gridy = 0;
								panelTherapyTotal.add(lblTherapyTotalSelectionVal, gbc_lblTherapyTotalSelectionVal);
								
								lblExamens = new JLabel("   "+MessageBundle.getMessage("angal.selectprescription.examslist"));
								lblExamens.setFont(new Font("Tahoma", Font.PLAIN, 14));
								lblExamens.setHorizontalAlignment(SwingConstants.LEFT);
								GridBagConstraints gbc_lblExamens = new GridBagConstraints();
								gbc_lblExamens.gridwidth = 4;
								gbc_lblExamens.anchor = GridBagConstraints.WEST;
								gbc_lblExamens.insets = new Insets(0, 0, 0, 5);
								gbc_lblExamens.gridx = 0;
								gbc_lblExamens.gridy = 1;
								panelTherapyTotal.add(lblExamens, gbc_lblExamens);
		
		pExams = new JPanel();
		GridBagConstraints gbc_pExams = new GridBagConstraints();
		gbc_pExams.fill = GridBagConstraints.BOTH;
		gbc_pExams.insets = new Insets(0, 0, 5, 0);
		gbc_pExams.gridx = 0;
		gbc_pExams.gridy = 2;
		contentPane.add(pExams, gbc_pExams);
		pExams.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollExam = new JScrollPane();
		pExams.add(scrollExam, BorderLayout.CENTER);
		
		tableExamp = new JTable();
		
		tableExamp.addMouseMotionListener(new MouseMotionListener() {			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				JTable aTable =  (JTable)e.getSource();
		        int itsRow = aTable.rowAtPoint(e.getPoint());
		        if(itsRow>=0){
		        	cellRenderer.setHoveredRow(itsRow);
		        }
		        else{
		        	cellRenderer.setHoveredRow(-1);
		        }
		        aTable.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		tableExamp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				cellRenderer.setHoveredRow(-1);
			}
		});
		
		tableExamp.setSelectionModel(new DefaultListSelectionModel() {
		    @Override
		    public void setSelectionInterval(int index0, int index1) {
		        if(super.isSelectedIndex(index0)) {
		            super.removeSelectionInterval(index0, index1);
		        }
		        else {
		            super.addSelectionInterval(index0, index1);
		        }
		    }
		});
		tableExamp.setModel(new ExamRowModel());
		tableExamp.setDefaultRenderer(Object.class, cellRenderer);
		tableExamp.setDefaultRenderer(Double.class, cellRenderer);
		scrollExam.setViewportView(tableExamp);
		
		panelExamTotal = new JPanel();
		pExams.add(panelExamTotal, BorderLayout.SOUTH);
		GridBagLayout gbl_panelExamTotal = new GridBagLayout();
		gbl_panelExamTotal.columnWidths = new int[]{324, 70, 23, 70, 23, 0};
		gbl_panelExamTotal.rowHeights = new int[]{23, 0, 0};
		gbl_panelExamTotal.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelExamTotal.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelExamTotal.setLayout(gbl_panelExamTotal);
		
		chckbxSlectionnerTousLes_1 = new JCheckBox(MessageBundle.getMessage("angal.selectprescription.selectallexams"));
		chckbxSlectionnerTousLes_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if(chckbxSlectionnerTousLes_1.isSelected()){
					selectRows(tableExamp,0,tableExamp.getModel().getRowCount()-1);
				}
				else{
					tableExamp.clearSelection();
				}
				checkIfOthersAreCheked();
			}
		});
		GridBagConstraints gbc_chckbxSlectionnerTousLes_1 = new GridBagConstraints();
		gbc_chckbxSlectionnerTousLes_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxSlectionnerTousLes_1.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxSlectionnerTousLes_1.gridx = 0;
		gbc_chckbxSlectionnerTousLes_1.gridy = 0;
		panelExamTotal.add(chckbxSlectionnerTousLes_1, gbc_chckbxSlectionnerTousLes_1);
		
		lblExamTotal = new JLabel(MessageBundle.getMessage("angal.selectprescription.totalexams"));
		GridBagConstraints gbc_lblExamTotal = new GridBagConstraints();
		gbc_lblExamTotal.anchor = GridBagConstraints.WEST;
		gbc_lblExamTotal.insets = new Insets(0, 0, 5, 5);
		gbc_lblExamTotal.gridx = 1;
		gbc_lblExamTotal.gridy = 0;
		panelExamTotal.add(lblExamTotal, gbc_lblExamTotal);
		
		lblExamTotalVal = new JLabel("New label");
		lblExamTotalVal.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblExamTotalVal = new GridBagConstraints();
		gbc_lblExamTotalVal.anchor = GridBagConstraints.WEST;
		gbc_lblExamTotalVal.insets = new Insets(0, 0, 5, 5);
		gbc_lblExamTotalVal.gridx = 2;
		gbc_lblExamTotalVal.gridy = 0;
		panelExamTotal.add(lblExamTotalVal, gbc_lblExamTotalVal);
		lblExamTotalVal.setText(getTotalPriceOfJtable(tableExamp,1)+"");
		
		lblExamTotalSelection = new JLabel(MessageBundle.getMessage("angal.selectprescription.totalselection"));
		GridBagConstraints gbc_lblExamTotalSelection = new GridBagConstraints();
		gbc_lblExamTotalSelection.anchor = GridBagConstraints.WEST;
		gbc_lblExamTotalSelection.insets = new Insets(0, 0, 5, 5);
		gbc_lblExamTotalSelection.gridx = 3;
		gbc_lblExamTotalSelection.gridy = 0;
		panelExamTotal.add(lblExamTotalSelection, gbc_lblExamTotalSelection);
		
		lblExamTotalSelectionVal = new JLabel("0.0");
		lblExamTotalSelectionVal.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblExamTotalSelectionVal = new GridBagConstraints();
		gbc_lblExamTotalSelectionVal.insets = new Insets(0, 0, 5, 0);
		gbc_lblExamTotalSelectionVal.anchor = GridBagConstraints.WEST;
		gbc_lblExamTotalSelectionVal.gridx = 4;
		gbc_lblExamTotalSelectionVal.gridy = 0;
		panelExamTotal.add(lblExamTotalSelectionVal, gbc_lblExamTotalSelectionVal);
		
		lblOprations = new JLabel("   "+MessageBundle.getMessage("angal.selectprescription.operationslist"));
		lblOprations.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblOprations.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblOprations = new GridBagConstraints();
		gbc_lblOprations.gridwidth = 5;
		gbc_lblOprations.anchor = GridBagConstraints.WEST;
		gbc_lblOprations.insets = new Insets(0, 0, 0, 5);
		gbc_lblOprations.gridx = 0;
		gbc_lblOprations.gridy = 1;
		panelExamTotal.add(lblOprations, gbc_lblOprations);
		
		pOperations = new JPanel();
		GridBagConstraints gbc_pOperations = new GridBagConstraints();
		gbc_pOperations.fill = GridBagConstraints.BOTH;
		gbc_pOperations.insets = new Insets(0, 0, 5, 0);
		gbc_pOperations.gridx = 0;
		gbc_pOperations.gridy = 3;
		contentPane.add(pOperations, gbc_pOperations);
		pOperations.setLayout(new BorderLayout(0, 0));
		
		JPanel panelOperationTotal = new JPanel();
		FlowLayout fl_panelOperationTotal = (FlowLayout) panelOperationTotal.getLayout();
		fl_panelOperationTotal.setAlignment(FlowLayout.RIGHT);
		pOperations.add(panelOperationTotal, BorderLayout.SOUTH);
		
		chckbxSlectionnerToutesLes = new JCheckBox(MessageBundle.getMessage("angal.selectprescription.selectalloperation"));
		chckbxSlectionnerToutesLes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxSlectionnerToutesLes.isSelected()){
					selectRows(tableOperation,0,tableOperation.getModel().getRowCount()-1);
				}
				else{
					tableOperation.clearSelection();
				}
				checkIfOthersAreCheked();
			}
		});
		panelOperationTotal.add(chckbxSlectionnerToutesLes);
		
		lblOpeTotal = new JLabel(MessageBundle.getMessage("angal.selectprescription.totaloperation"));
		panelOperationTotal.add(lblOpeTotal);
		
		lblOpeTotalVal = new JLabel("New label");
		lblOpeTotalVal.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelOperationTotal.add(lblOpeTotalVal);
		
		lblOpeTotalSelection = new JLabel(MessageBundle.getMessage("angal.selectprescription.totalselection"));
		panelOperationTotal.add(lblOpeTotalSelection);
		
		lblOpeTotalSelectionVal = new JLabel("0.0");
		lblOpeTotalSelectionVal.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panelOperationTotal.add(lblOpeTotalSelectionVal);
		
		scrollOperation = new JScrollPane();
		pOperations.add(scrollOperation, BorderLayout.CENTER);
		
		tableOperation = new JTable();
		
		tableOperation.addMouseMotionListener(new MouseMotionListener() {			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				JTable aTable =  (JTable)e.getSource();
		        int itsRow = aTable.rowAtPoint(e.getPoint());
		        if(itsRow>=0){
		        	cellRenderer.setHoveredRow(itsRow);
		        }
		        else{
		        	cellRenderer.setHoveredRow(-1);
		        }
		        aTable.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		tableOperation.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				cellRenderer.setHoveredRow(-1);
			}
		});
		
		tableOperation.setSelectionModel(new DefaultListSelectionModel() {
		    @Override
		    public void setSelectionInterval(int index0, int index1) {
		        if(super.isSelectedIndex(index0)) {
		            super.removeSelectionInterval(index0, index1);
		        }
		        else {
		            super.addSelectionInterval(index0, index1);
		        }
		    }
		});
		tableOperation.setModel(new OperationRowModel());
		tableOperation.setDefaultRenderer(Object.class, cellRenderer);
		tableOperation.setDefaultRenderer(Double.class, cellRenderer);
		scrollOperation.setViewportView(tableOperation);
		
		pTotal = new JPanel();
		FlowLayout fl_pTotal = (FlowLayout) pTotal.getLayout();
		fl_pTotal.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_pTotal = new GridBagConstraints();
		gbc_pTotal.fill = GridBagConstraints.HORIZONTAL;
		gbc_pTotal.gridx = 0;
		gbc_pTotal.gridy = 4;
		contentPane.add(pTotal, gbc_pTotal);
		
		JLabel lblTotal = new JLabel(MessageBundle.getMessage("angal.selectprescription.totalamount"));
		lblTotal.setFont(new Font("Dialog", Font.PLAIN, 14));
		pTotal.add(lblTotal);
		
		lblTotalVal = new JLabel("New label");
		lblTotalVal.setFont(new Font("Tahoma", Font.PLAIN, 18));
		pTotal.add(lblTotalVal);
		
		JLabel lblTotalSelection = new JLabel(MessageBundle.getMessage("angal.selectprescription.totalselection"));
		lblTotalSelection.setFont(new Font("Dialog", Font.PLAIN, 14));
		pTotal.add(lblTotalSelection);
		
		lblTotalSelectionVal = new JLabel("0.0");
		lblTotalSelectionVal.setFont(new Font("Tahoma", Font.PLAIN, 18));
		pTotal.add(lblTotalSelectionVal);
		
		/** getting totals price of jtables **/
		lblOpeTotalVal.setText(getTotalPriceOfJtable(tableOperation,1)+"");
		float bigTotal = getTotalPriceOfJtable(tableOperation,1) + getTotalPriceOfJtable(tableExamp,1) + getTotalPriceOfJtable(tableTherapy,6);
		lblTotalVal.setText(bigTotal+"");
        /**************************************/
		
		/**** adding listener for value change on all jtables *****/
		tableOperation.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					lblOpeTotalSelectionVal.setText(getTotalSelectedPriceOfJtable(tableOperation, 1)+"");
					lblTotalSelectionVal.setText(getTotalSelected()+"");
					checkIfAllOthersRowsAreCheked();
				}				
			}
		});
		tableExamp.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					lblExamTotalSelectionVal.setText(getTotalSelectedPriceOfJtable(tableExamp, 1)+"");
					lblTotalSelectionVal.setText(getTotalSelected()+"");
					checkIfAllOthersRowsAreCheked();
				}
			}
		});
		tableTherapy.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					lblTherapyTotalSelectionVal.setText(getTotalSelectedPriceOfJtable(tableTherapy, 6)+"");
					lblTotalSelectionVal.setText(getTotalSelected()+"");
					checkIfAllOthersRowsAreCheked();
				}
			}
		});
		/**********************************************************/
		
				pButton = new JPanel();
				GridBagConstraints gbc_pButton = new GridBagConstraints();
				gbc_pButton.fill = GridBagConstraints.BOTH;
				gbc_pButton.gridx = 0;
				gbc_pButton.gridy = 5;
				contentPane.add(pButton, gbc_pButton);
				GridBagLayout gbl_pButton = new GridBagLayout();
				gbl_pButton.columnWidths = new int[]{242, 81, 135, 0};
				gbl_pButton.rowHeights = new int[]{35, 0};
				gbl_pButton.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
				gbl_pButton.rowWeights = new double[]{0.0, Double.MIN_VALUE};
				pButton.setLayout(gbl_pButton);
				
				JPanel panel = new JPanel();
				GridBagConstraints gbc_panel = new GridBagConstraints();
				gbc_panel.anchor = GridBagConstraints.EAST;
				gbc_panel.insets = new Insets(0, 0, 0, 5);
				gbc_panel.gridx = 2;
				gbc_panel.gridy = 0;
				pButton.add(panel, gbc_panel);
				
				JButton btnAnnuler = new JButton(MessageBundle.getMessage("angal.common.cancel"));
				panel.add(btnAnnuler);
				

				

				JButton btnValider = new JButton(MessageBundle.getMessage("angal.therapy.validateselection"));
				panel.add(btnValider);
				btnValider.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						int[] selectedrows=tableTherapy.getSelectedRows();
						List<TherapyRow> selected=new ArrayList<TherapyRow>();
						for(int i : selectedrows){
							selected.add(therapies.get(i));
						}
						mapTherapies.keySet().retainAll(selected);
						
						//SelectPrescriptions.this.fireSelectedTherapies(mapTherapies);
						
						SelectPrescriptions.this.fireSelectedPrescription();
						SelectPrescriptions.this.setVisible(false);
					}
				});
				
				btnAnnuler.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						SelectPrescriptions.this.setVisible(false);
					}
				});
				
				JPanel panel_1 = new JPanel();
				GridBagConstraints gbc_panel_1 = new GridBagConstraints();
				gbc_panel_1.anchor = GridBagConstraints.NORTHWEST;
				gbc_panel_1.gridx = 0;
				gbc_panel_1.gridy = 0;
				pButton.add(panel_1, gbc_panel_1);
				
				chBoxSelectAll = new JCheckBox(MessageBundle.getMessage("angal.selectprescription.selectall"));
				chBoxSelectAll.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(chBoxSelectAll.isSelected()){
							selectRows(tableOperation,0,tableOperation.getModel().getRowCount()-1);
							selectRows(tableExamp,0,tableExamp.getModel().getRowCount()-1);
							selectRows(tableTherapy,0,tableTherapy.getModel().getRowCount()-1);
							chckbxSlectionnerToutesLes.setSelected(true);
							chckbxSlectionnerTousLes.setSelected(true);
							chckbxSlectionnerTousLes_1.setSelected(true);
						}
						else{
							chckbxSlectionnerToutesLes.setSelected(false);
							chckbxSlectionnerTousLes.setSelected(false);
							chckbxSlectionnerTousLes_1.setSelected(false);
							clearAllJtable();
							lblTherapyTotalSelectionVal.setText(getTotalSelectedPriceOfJtable(tableTherapy, 6)+"");
							lblExamTotalSelectionVal.setText(getTotalSelectedPriceOfJtable(tableExamp, 1)+"");
							lblOpeTotalSelectionVal.setText(getTotalSelectedPriceOfJtable(tableOperation, 1)+"");
							lblTotalSelectionVal.setText(getTotalSelected()+"");
						}
					}
				});
				panel_1.add(chBoxSelectAll);
		
		setLocationRelativeTo(null);
	}
	
	private class ExamRowModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;

		String[] cColumns = null;
		private List<Laboratory> medWard;
		public int totalExam = 0;
		BillBrowserManager billManager=new BillBrowserManager();

		public ExamRowModel() {
			super();
			//cColumns = new String[] {MessageBundle.getMessage("angal.lab.exam"), 
			//		MessageBundle.getMessage("angal.exal.price") };
			cColumns = new String[] {MessageBundle.getMessage("angal.agetype.description"), 
					MessageBundle.getMessage("angal.accounting.cost")};
			
			if (patient == null) {//getLaboratory
				laboratories = new ArrayList<Laboratory>(); 
			} else {
				//laboratories = new LabManager().getLaboratory(patient.getCode());
			    try {
					laboratories = new LabManager().getLabWithoutBill(patient.getCode()+"");
				} catch (OHException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				//Collections.sort(therapies);
//				for(TherapyRow therapy: therapies){
//					double qty=therapy.getQty();
//					double qtyBougth=therapy.getQtyBougth();
//					double remaining=qty-qtyBougth;
//					
//					mapTherapies.put(therapy, Integer.parseInt(String.valueOf(Math.round(remaining))));
//				}
			}
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			
			if (columnIndex == 1) {
				return Double.class;
			}
			return String.class;
		}
		@Override
		public int getColumnCount() {
			return cColumns.length;
		}

		@Override
		public String getColumnName(int column) {
			return cColumns[column];
		}

		@Override
		public int getRowCount() {
			if(laboratories==null){
				return 0;
			}
			return laboratories.size();
		}
		@Override
		public boolean isCellEditable(int row, int column) {
			//Only fifth column is editable
			//return column==4;
			return false;
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
//			Integer value=0;
//			
//			if(column==4){
//				try{
//					String strVal=String.valueOf(Math.round(Double.valueOf(aValue.toString())));
//					value=Integer.parseInt(strVal);
//				}
//				catch(Exception ex){
//					value=0;
//				}
//				
//			}
//			TherapyRow therapy=therapies.get(row);
//			mapTherapies.put(therapy, value);
		}
		@Override
		public Object getValueAt(int row, int column) {
			if (row < 0) {
				return null;
			}
			if (row >= laboratories.size()) {
				return null;
			}
			Laboratory laboratory = laboratories.get(row);
			//double thQty=laboratory.getQty();
			//double thQtyBougth=laboratory.getQtyBougth();
			
			if (column == 0) {
				return laboratory.getExam().getDescription();
			}
			if (column == 1) {

				Price price=billManager.getPrice(laboratory.getExam().getCode(), ItemGroup.EXAM, patient);
				return price.getPrice();
			}
			
			return null;
		}
		
		public int getTotalExam(){
			
			return 0;
		}
	}

	private class TherapyRowModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		String[] cColumns = null;
		private List<MedicalWard> medWard;
		BillBrowserManager billManager=new BillBrowserManager();

		public TherapyRowModel() {
			super();
			if (Param.bool("STOCKMVTONBILLSAVE")) {
				cColumns = new String[] {MessageBundle.getMessage("angal.therapy.startdate"), 
						MessageBundle.getMessage("angal.priceslist.medicals"), 
						MessageBundle.getMessage("angal.newbill.qty"),
						MessageBundle.getMessage("angal.therapy.qtybougth"), 
						MessageBundle.getMessage("angal.therapy.remaining"), 
						MessageBundle.getMessage("angal.therapy.stock"), 
						MessageBundle.getMessage("angal.accounting.cost") };
				String wardCode = MainMenu.getUserWard();
				MovWardBrowserManager manager = new MovWardBrowserManager();
				medWard = manager.getMedicalsWard(wardCode);
			} else {
				cColumns = new String[] { MessageBundle.getMessage("angal.therapy.startdate"), 
						MessageBundle.getMessage("angal.priceslist.medicals"), 
						MessageBundle.getMessage("angal.newbill.qty"),
						MessageBundle.getMessage("angal.therapy.qtybougth"),
						MessageBundle.getMessage("angal.therapy.remaining"), 
						MessageBundle.getMessage("angal.therapy.stock"), 
						MessageBundle.getMessage("angal.accounting.cost")  };
			}
			if (patient == null) {
				therapies = new ArrayList<TherapyRow>(); 
			} else {
				therapies = new TherapyManager().getTherapyRows(patient.getCode());
				Collections.sort(therapies);
				int size = therapies.size();
				TherapyRow therap;
				for(int i = 0; i < size; i++){
					therap = therapies.get(i);
					double qty=therap.getQty();
					double qtyBougth=therap.getQtyBougth();
					double remaining=qty-qtyBougth;
					if(remaining<=0){
						therapies.remove(therap);	
						size = size -1;
						i = i- 1;
					}				
				}
				for(TherapyRow therapy: therapies){
					double qty=therapy.getQty();
					double qtyBougth=therapy.getQtyBougth();
					double remaining=qty-qtyBougth;
					mapTherapies.put(therapy, Integer.parseInt(String.valueOf(Math.round(remaining))));
				}
			}

		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			
			if (columnIndex == 2) {
				return Double.class;
			}
			if (columnIndex == 3) {
				return Double.class;
			}
			if (columnIndex == 4) {
				return Double.class;
			}
			if (columnIndex == 5) {
				return Double.class;
			}
			if (columnIndex == 6) {
				return Double.class;
			}
			
			return String.class;
		}

		private Double getQuantityinWard(Medical med) {
			for (Iterator<MedicalWard> iterator = medWard.iterator(); iterator.hasNext();) {
				MedicalWard medicalWard = (MedicalWard) iterator.next();
				Integer code = medicalWard.getMedical().getCode();
				Double stock = medicalWard.getQty();
				if (code.equals(med.getCode())) {
					return stock;
				}

			}
			return 0.0;
		}

		@Override
		public int getColumnCount() {
			return cColumns.length;
		}

		@Override
		public String getColumnName(int column) {
			return cColumns[column];
		}

		@Override
		public int getRowCount() {
			if(therapies==null){
				return 0;
			}
			return therapies.size();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			//Only fifth column is editable
			return column==4;
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			Integer value=0;
			
			if(column==4){
				try{
					String strVal=String.valueOf(Math.round(Double.valueOf(aValue.toString())));
					value=Integer.parseInt(strVal);
				}
				catch(Exception ex){
					value=0;
				}
				
			}
			TherapyRow therapy=therapies.get(row);
			mapTherapies.put(therapy, value);
		}
		@Override
		public Object getValueAt(int row, int column) {
			if (row < 0) {
				return null;
			}
			if (row >= therapies.size()) {
				return null;
			}
			TherapyRow therapy = therapies.get(row);
			double thQty=therapy.getQty();
			double thQtyBougth=therapy.getQtyBougth();
			
			if (column == 0) {
				SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyy");
				return format.format(therapy.getStartDate().getTime());
			}
			if (column == 1) {
				return therapy.toString();
			}
			if (column == 2) {
				return thQty;
			}
			if (column == 3) {
				return thQtyBougth;
			}
			if (column == 4) {
				return mapTherapies.get(therapy);
			}
			if (column == 5) {
				if (Param.bool("STOCKMVTONBILLSAVE")) {
					return getQuantityinWard(therapy.getMedical());
				} else {
					Double qty = therapy.getMedical().getInitialqty()
							+ therapy.getMedical().getInqty() - therapy.getMedical().getOutqty();
					return qty;
				}

			}
			if (column == 6) {
				//Cost
				double remaining = mapTherapies.get(therapy);
				Price price=billManager.getPrice(String.valueOf(therapy.getMedical().getCode()), ItemGroup.MEDICAL, patient);
				return price.getPrice()*remaining;
			}
			
			return null;
		}

	}
     
	private class OperationRowModel extends DefaultTableModel{
		private static final long serialVersionUID = 1L;

		String[] cColumns = null;
		private List<Laboratory> medWard;
		
		BillBrowserManager billManager=new BillBrowserManager();

		public OperationRowModel() {
			super();
			//cColumns = new String[] {MessageBundle.getMessage("angal.lab.exam"), 
			//		MessageBundle.getMessage("angal.exal.price") };
			cColumns = new String[] {MessageBundle.getMessage("angal.agetype.description"), 
					MessageBundle.getMessage("angal.accounting.cost") };
			
			if (patient == null) {//getLaboratory
				operations = new ArrayList<OperationRow>(); 
			} else {
				//operations = new OperationRowBrowserManager().getOperationRowByPatient(patient.getCode()+"");
			    try {
					operations = new OperationRowBrowserManager().getOperationWithoutBill(patient.getCode()+"");
				} catch (OHException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			
			if (columnIndex == 1) {
				return Double.class;
			}
			return String.class;
		}
		@Override
		public int getColumnCount() {
			return cColumns.length;
		}

		@Override
		public String getColumnName(int column) {
			return cColumns[column];
		}

		@Override
		public int getRowCount() {
			if(operations==null){
				return 0;
			}
			return operations.size();
		}
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {

		}
		@Override
		public Object getValueAt(int row, int column) {
			if (row < 0) {
				return null;
			}
			if (row >= operations.size()) {
				return null;
			}
			OperationRow operation = operations.get(row);
			String opeID = operation.getOperationId();
			OperationBrowserManager opeManager = new OperationBrowserManager();
			Operation ope = opeManager.getOperationByCode(opeID);
			if ( column == 0) {
				return ope.getDescription();
			}
			if (column == 1) {
				//Cost
				//double remaining=mapTherapies.get(therapy);
				Price price=billManager.getPrice(ope.getCode(), ItemGroup.OPERATION, patient);
				//return 7;
				return price.getPrice();
			}
			
			return null;
		}
	}
	private float getTotalPriceOfJtable(JTable jtable, int colum){
		float total = 0;
		for(int i = 0; i < jtable.getModel().getRowCount();i++){
			total = total + Float.parseFloat(jtable.getModel().getValueAt(i, colum).toString());
		}
		return total;
	}
	
	private float getTotalSelectedPriceOfJtable(JTable jtable, int colum){
		float totalSelected = 0;
		int[] rowsSelected = jtable.getSelectedRows();
		for(int i = 0; i < rowsSelected.length ;i++){
			totalSelected = totalSelected + Float.parseFloat(jtable.getModel().getValueAt(rowsSelected[i], colum).toString());
		}
		return totalSelected;
	}
	private float getTotalSelected(){
		return getTotalSelectedPriceOfJtable(tableOperation, 1)
	            + getTotalSelectedPriceOfJtable(tableExamp, 1)
	            + getTotalSelectedPriceOfJtable(tableTherapy, 6);		 
	}
	
	private void selectRows(JTable table, int start, int end) {
	        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	        table.setRowSelectionAllowed(true);
	        if(table.getModel().getRowCount()>0)
	        	table.setRowSelectionInterval(start, end);
	}
	private void clearAllJtable(){
		tableOperation.clearSelection();
		tableExamp.clearSelection();
		tableTherapy.clearSelection();
	}
	private void checkIfOthersAreCheked(){
		 if(chckbxSlectionnerTousLes.isSelected() && chckbxSlectionnerTousLes_1.isSelected() && chckbxSlectionnerToutesLes.isSelected())
			 chBoxSelectAll.setSelected(true);
		 else
			 chBoxSelectAll.setSelected(false);
	}
	private void checkIfAllOthersRowsAreCheked(){
		 boolean checktable1= false, checktable2= false, checktable3 = false;
		 if(tableTherapy.getSelectedRowCount()==tableTherapy.getModel().getRowCount()){
			 checktable1 = true;
			 chckbxSlectionnerTousLes.setSelected(true);
		 }
		 else{
			 checktable1 = false;
			 chckbxSlectionnerTousLes.setSelected(false);
		 }
		 if(tableExamp.getSelectedRowCount()==tableExamp.getModel().getRowCount()){
			 checktable2 = true;
			 chckbxSlectionnerTousLes_1.setSelected(true);
		 }
		 else{
			 checktable2 = false;
			 chckbxSlectionnerTousLes_1.setSelected(false);
		 }
		 if(tableOperation.getSelectedRowCount()==tableOperation.getModel().getRowCount()){
			 checktable3 = true;
			 chckbxSlectionnerToutesLes.setSelected(true);
	     }
		 else{
			 checktable3 = false;
			 chckbxSlectionnerToutesLes.setSelected(false);
		 }
		 if (checktable1 && checktable2 && checktable3)
			 chBoxSelectAll.setSelected(true);
		 else
			 chBoxSelectAll.setSelected(false);
	}
}
