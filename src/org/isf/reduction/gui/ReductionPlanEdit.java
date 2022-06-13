package org.isf.reduction.gui;

import java.awt.AWTEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.reduction.manager.ReductionPlanManager;
import org.isf.reduction.model.ExamsReduction;
import org.isf.reduction.model.MedicalsReduction;
import org.isf.reduction.model.OperationReduction;
import org.isf.reduction.model.OtherReduction;
import org.isf.reduction.model.ReductionPlan;
import org.isf.utils.exception.OHException;
import org.isf.utils.jobjects.ModalJFrame;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;

public class ReductionPlanEdit extends ModalJFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton jCloseButton;
	private String[] medReductionColumn = new String[] {
			MessageBundle.getMessage("angal.reduction.medical"),
			MessageBundle.getMessage("angal.reduction.reductionrate") };
	private String[] exaReductionColumn = new String[] {
			MessageBundle.getMessage("angal.reduction.exam"),
			MessageBundle.getMessage("angal.reduction.reductionrate") };
	private String[] opeReductionColumn = new String[] {
			MessageBundle.getMessage("angal.reduction.operation"),
			MessageBundle.getMessage("angal.reduction.reductionrate") };
	private String[] othReductionColumn = new String[] {
			MessageBundle.getMessage("angal.reduction.other"),
			MessageBundle.getMessage("angal.reduction.reductionrate") };
	private ReductionPlan reductionPlan=new ReductionPlan();
	ReductionPlanManager manager = new ReductionPlanManager();
	private JPanel buttonPanel;
	private JLabel lblDescription;
	private JTextField txtDescription;
	private String descriptionBackup;
	private JLabel lblMedicalrate;
	private JTextField txtMedicalRate;
	private JLabel lblOperationRate;
	private JTextField txtOperationRate;
	private JLabel lblExamRate;
	private JTextField txtExamRate;
	private JLabel lblOtherRate;
	private JTextField txtOtherRate;
	private ArrayList<MedicalsReduction> medicalReductionList;
	private JTable medicalReductionTable;

	private ArrayList<ExamsReduction> examReductionList;
	private JTable examReductionTable;

	private ArrayList<OperationReduction> opeReductionList;
	private JTable opeReductionTable;

	private ArrayList<OtherReduction> othReductionList;
	private JTable othReductionTable;
	private JButton jAddbutton;
	private JTabbedPane tabbedPane;
	private JButton jDeleteButton;
	private boolean isInsert;
	private JButton jSaveButton;

	
	//LISTENER INTERFACE --------------------------------------------------------
		private EventListenerList rpListener = new EventListenerList();
		
		public interface ReductionPlanListener extends EventListener {
			public void pbiInserted(AWTEvent aEvent);
		}
		
		public void addReductionPlanListener(ReductionPlanListener l) {
			rpListener.add(ReductionPlanListener.class, l);
			
		}
		
		private void fireReductionPlanInserted(ReductionPlan reductionPlan) {
			AWTEvent event = new AWTEvent(reductionPlan, AWTEvent.RESERVED_ID_MAX + 1) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};
			
			EventListener[] listeners = rpListener.getListeners(ReductionPlanListener.class);
			for (int i = 0; i < listeners.length; i++)
				((ReductionPlanListener)listeners[i]).pbiInserted(event);
		}
	//---------------------------------------------------------------------------
		
	/**
	 * Create the frame.
	 */
	public ReductionPlanEdit(ReductionPlan reductionPlan, boolean isInsert) {
		this.isInsert=isInsert;
		this.reductionPlan=reductionPlan;
		initialize();
		loadDataFromObject();
	}

	private void initialize() {
		setTitle(MessageBundle.getMessage("angal.reduction.editreductionplan"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 350);
		setContentPane(getMainContentPane());
		setLocationRelativeTo(null);
	}

	private JPanel getMainContentPane() {
		try {
			if (contentPane == null) {
				contentPane = new JPanel();
			}	
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] { 100 };
			gridBagLayout.rowHeights = new int[] { 20, 20, 20 };
			gridBagLayout.columnWeights = new double[] { 1.0 };
			gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0 };
			contentPane.setLayout(gridBagLayout);

			JPanel detailsPane = getDetailPane();
//			contentPane.add(detailsPane, BorderLayout.CENTER);
			GridBagConstraints gbcDetail = new GridBagConstraints();
			gbcDetail.anchor = GridBagConstraints.WEST;
			gbcDetail.fill = GridBagConstraints.HORIZONTAL;
			gbcDetail.insets = new Insets(5, 5, 5, 5);
			gbcDetail.gridx = 0;
			gbcDetail.gridy = 0;
			contentPane.add(detailsPane, gbcDetail);
			

			
			JPanel tabPane = getJTabbedPane();
			
			GridBagConstraints gbcTabbedPane = new GridBagConstraints();
			gbcTabbedPane.anchor = GridBagConstraints.WEST;
			gbcTabbedPane.fill = GridBagConstraints.BOTH;
//			gbcTabbedPane.fill = GridBagConstraints.VERTICAL;
			gbcTabbedPane.insets = new Insets(5, 5, 5, 5);
			gbcTabbedPane.gridx = 0;
			gbcTabbedPane.gridy = 1;
			contentPane.add(tabPane, gbcTabbedPane);
			
//			contentPane.add(tabPane, BorderLayout.SOUTH);
			
			GridBagConstraints gbcButtonPane = new GridBagConstraints();
			gbcButtonPane.anchor = GridBagConstraints.WEST;
			gbcButtonPane.insets = new Insets(5, 5, 5, 5);
			gbcButtonPane.gridx = 0;
			gbcButtonPane.gridy = 2;
			contentPane.add(getButtonPane(), gbcButtonPane);
			
//			contentPane.add(getButtonPane(), BorderLayout.SOUTH);
			return contentPane;
		} catch (OHException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}

	}

	private JPanel getDetailPane() {
		// TODO Auto-generated method stub
		JPanel detailPane = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 100, 100, 100, 100 };
		gridBagLayout.rowHeights = new int[] { 20, 20, 20 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		detailPane.setLayout(gridBagLayout);

		lblDescription = getJLabelDescription();
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		detailPane.add(lblDescription, gbc_lblNewLabel);

		txtDescription = getJtextFieldDescription();
		GridBagConstraints gbc_txtDescription = new GridBagConstraints();
		gbc_txtDescription.anchor = GridBagConstraints.NORTH;
		gbc_txtDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDescription.insets = new Insets(0, 0, 5, 0);
		gbc_txtDescription.gridwidth = 3;
		gbc_txtDescription.gridx = 1;
		gbc_txtDescription.gridy = 0;
		detailPane.add(txtDescription, gbc_txtDescription);
		txtDescription.setColumns(10);

		lblMedicalrate = getJlabelMedicalRate();
		GridBagConstraints gbc_lblTauxMedicaments = new GridBagConstraints();
		gbc_lblTauxMedicaments.anchor = GridBagConstraints.SOUTH;
		gbc_lblTauxMedicaments.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTauxMedicaments.insets = new Insets(5, 5, 5, 5);
		gbc_lblTauxMedicaments.gridx = 0;
		gbc_lblTauxMedicaments.gridy = 1;
		detailPane.add(lblMedicalrate, gbc_lblTauxMedicaments);

		txtMedicalRate = getJtextFieldMedicalRate();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.anchor = GridBagConstraints.NORTH;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		detailPane.add(txtMedicalRate, gbc_textField);
		txtMedicalRate.setColumns(10);

		lblOperationRate = getJLabelOperationRate();
		GridBagConstraints gbc_lblTauxOperation = new GridBagConstraints();
		gbc_lblTauxOperation.anchor = GridBagConstraints.NORTH;
		gbc_lblTauxOperation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTauxOperation.insets = new Insets(5, 5, 5, 5);
		gbc_lblTauxOperation.gridx = 2;
		gbc_lblTauxOperation.gridy = 1;
		detailPane.add(lblOperationRate, gbc_lblTauxOperation);

		txtOperationRate = getJtextFieldOperationRate();
		GridBagConstraints gbc_txtOperationRate = new GridBagConstraints();
		gbc_txtOperationRate.anchor = GridBagConstraints.NORTH;
		gbc_txtOperationRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOperationRate.insets = new Insets(0, 0, 5, 0);
		gbc_txtOperationRate.gridx = 3;
		gbc_txtOperationRate.gridy = 1;
		detailPane.add(txtOperationRate, gbc_txtOperationRate);
		txtOperationRate.setColumns(10);

		lblExamRate = getJLabelExamRate();
		GridBagConstraints gbc_lblTauxExamen = new GridBagConstraints();
		gbc_lblTauxExamen.anchor = GridBagConstraints.SOUTH;
		gbc_lblTauxExamen.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTauxExamen.insets = new Insets(5, 5, 5, 5);
		gbc_lblTauxExamen.gridx = 0;
		gbc_lblTauxExamen.gridy = 2;
		detailPane.add(lblExamRate, gbc_lblTauxExamen);

		txtExamRate = getTextFieldExamRate();
		GridBagConstraints gbc_txtExamRate = new GridBagConstraints();
		gbc_txtExamRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtExamRate.anchor = GridBagConstraints.SOUTH;
		gbc_txtExamRate.insets = new Insets(0, 0, 0, 5);
		gbc_txtExamRate.gridx = 1;
		gbc_txtExamRate.gridy = 2;
		detailPane.add(txtExamRate, gbc_txtExamRate);
		txtExamRate.setColumns(10);

		lblOtherRate = getJLabelOtherRate();
		GridBagConstraints gbc_lblTauxAutres = new GridBagConstraints();
		gbc_lblTauxAutres.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblTauxAutres.insets = new Insets(5, 5, 5, 5);
		gbc_lblTauxAutres.gridx = 2;
		gbc_lblTauxAutres.gridy = 2;
		detailPane.add(lblOtherRate, gbc_lblTauxAutres);

		txtOtherRate = getTextFieldOtherRate();
		GridBagConstraints gbc_txtOtherRate = new GridBagConstraints();
		gbc_txtOtherRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOtherRate.anchor = GridBagConstraints.NORTH;
		gbc_txtOtherRate.gridx = 3;
		gbc_txtOtherRate.gridy = 2;
		detailPane.add(txtOtherRate, gbc_txtOtherRate);
		txtOtherRate.setColumns(10);

		return detailPane;

	}

	private JPanel getMedicalReductionPanel() throws OHException {
		JPanel medicalReductionPanel = new JPanel();
		medicalReductionPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		medicalReductionPanel.add(scrollPane, BorderLayout.CENTER);

		medicalReductionTable = new JTable();
		medicalReductionTable.setModel(new MedicalReductionModel());

		scrollPane.setViewportView(medicalReductionTable);
		return medicalReductionPanel;
	}

	private JPanel getExamReductionPanel() throws OHException {
		JPanel examReductionPanel = new JPanel();
		examReductionPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		examReductionPanel.add(scrollPane, BorderLayout.CENTER);

		examReductionTable = new JTable();
		examReductionTable.setModel(new ExamReductionModel());

		scrollPane.setViewportView(examReductionTable);
		return examReductionPanel;
	}

	private JPanel getOpeReductionPanel() throws OHException {
		JPanel opeReductionPanel = new JPanel();
		opeReductionPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		opeReductionPanel.add(scrollPane, BorderLayout.CENTER);

		opeReductionTable = new JTable();
		opeReductionTable.setModel(new OperationReductionModel());

		scrollPane.setViewportView(opeReductionTable);
		return opeReductionPanel;
	}

	private JPanel getOtherReductionPanel() throws OHException {
		JPanel othReductionPanel = new JPanel();
		othReductionPanel.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		othReductionPanel.add(scrollPane, BorderLayout.CENTER);

		othReductionTable = new JTable();
		othReductionTable.setModel(new OtherReductionModel());

		scrollPane.setViewportView(othReductionTable);
		return othReductionPanel;
	}

	private JLabel getJLabelOtherRate() {
		if (lblOtherRate == null) {
			lblOtherRate = new JLabel(MessageBundle.getMessage("angal.reduction.otherrate"));
		}
		return lblOtherRate;
	}

	private JTextField getTextFieldOtherRate() {
		if (txtOtherRate == null) {
			txtOtherRate = new JTextField();
		}
		return txtOtherRate;
	}

	private JTextField getTextFieldExamRate() {
		if (txtExamRate == null) {
			txtExamRate = new JTextField();
		}
		return txtExamRate;
	}

	private JLabel getJLabelExamRate() {
		if (lblExamRate == null) {
			lblExamRate = new JLabel(MessageBundle.getMessage("angal.reduction.examrate"));
		}
		return lblExamRate;
	}

	private JLabel getJLabelOperationRate() {
		if (lblOperationRate == null) {
			lblOperationRate = new JLabel(MessageBundle.getMessage("angal.reduction.operate"));
		}
		return lblOperationRate;

	}

	private JLabel getJlabelMedicalRate() {
		if (lblMedicalrate == null) {
			lblMedicalrate = new JLabel(MessageBundle.getMessage("angal.reduction.medicalrate"));
		}
		return lblMedicalrate;
	}

	private JTextField getJtextFieldDescription() {
		if (txtDescription == null) {
			txtDescription = new JTextField();
		}
		return txtDescription;
	}

	private JTextField getJtextFieldMedicalRate() {
		if (txtMedicalRate == null) {
			txtMedicalRate = new JTextField();
		}
		return txtMedicalRate;
	}

	private JTextField getJtextFieldOperationRate() {
		if (txtOperationRate == null) {
			txtOperationRate = new JTextField();
		}
		return txtOperationRate;
	}

	private JLabel getJLabelDescription() {
		if (lblDescription == null) {
			lblDescription = new JLabel(MessageBundle.getMessage("angal.common.description"));
		}
		return lblDescription;
	}

	private JPanel getJTabbedPane() throws OHException {
		JPanel pane=new JPanel();
		pane.setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(MessageBundle.getMessage("angal.reduction.medical") , getMedicalReductionPanel());
		tabbedPane.addTab(MessageBundle.getMessage("angal.reduction.exam"), getExamReductionPanel());
		tabbedPane.addTab(MessageBundle.getMessage("angal.reduction.operation"), getOpeReductionPanel());
		tabbedPane.addTab(MessageBundle.getMessage("angal.reduction.other"), getOtherReductionPanel());
		pane.add(tabbedPane, BorderLayout.CENTER);
		JPanel buttonPane=new JPanel();
		buttonPane.add(getJAddButton());
		buttonPane.add(getJDeleteButton());
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
		pane.add(buttonPane, BorderLayout.EAST);
		return pane;
	}

	private JPanel getButtonPane() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getJSaveButton());
			buttonPanel.add(getJCloseButton());
		}
		return buttonPanel;
	}

	private JButton getJAddButton() {
		if (jAddbutton == null) {
			jAddbutton = new JButton();
			jAddbutton.setText(MessageBundle.getMessage("angal.common.add"));
			jAddbutton.setMnemonic(KeyEvent.VK_A);
			jAddbutton.setIcon(new ImageIcon("rsc/icons/plus_button.png"));
			jAddbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					int tabSelected=tabbedPane.getSelectedIndex();
					switch (tabSelected) {
					case 0:
						//Medical panel
						addMedicalReduction();
						break;
					case 1:
						//Exam panel
						addExamReduction();
						break;
					case 2:
						//Operation panel
						addOperationReduction();
						break;
					case 3:
						//Other panel
						addOtherReduction();
						break;
					default:
						break;
					}
				}
			});
		}
		return jAddbutton;
	}
	private JButton getJDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton();
			jDeleteButton.setText(MessageBundle.getMessage("angal.common.delete"));
			jDeleteButton.setMnemonic(KeyEvent.VK_D);
			jDeleteButton.setIcon(new ImageIcon("rsc/icons/delete_button.png"));
			jDeleteButton.addActionListener(new ActionListener() {
				private int index;
				public void actionPerformed(ActionEvent event) {
					int tabSelected=tabbedPane.getSelectedIndex();
					switch (tabSelected) {
					case 0:
						//Medical panel
						index=medicalReductionTable.getSelectedRow();
						medicalReductionList.remove(index);
						MedicalReductionModel medicalModel=(MedicalReductionModel)medicalReductionTable.getModel();
						medicalModel.fireTableDataChanged();
						break;
					case 1:
						//Exam panel
						index=examReductionTable.getSelectedRow();
						examReductionList.remove(index);
						ExamReductionModel examModel=(ExamReductionModel)examReductionTable.getModel();
						examModel.fireTableDataChanged();
						break;
					case 2:
						//Operation panel
						index=opeReductionTable.getSelectedRow();
						opeReductionList.remove(index);
						OperationReductionModel opeModel=(OperationReductionModel)opeReductionTable.getModel();
						opeModel.fireTableDataChanged();
						
						break;
					case 3:
						//Other panel
						index=othReductionTable.getSelectedRow();
						othReductionList.remove(index);
						OtherReductionModel othModel=(OtherReductionModel)othReductionTable.getModel();
						othModel.fireTableDataChanged();
						
						break;
						
					default:
						break;
					}
				}
			});
		}
		return jDeleteButton;
	}
	private JButton getJSaveButton() {
		if (jSaveButton == null) {
			jSaveButton = new JButton();
			jSaveButton.setText(MessageBundle.getMessage("angal.common.save"));
			jSaveButton.setMnemonic(KeyEvent.VK_S);
			jSaveButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					try {
						if(loadDataInObject()){
							if(reductionPlan.getDescription().trim().length() == 0) {
								throw new OHException(
										MessageBundle
										.getMessage("angal.reduction.descriptionnotvalid"),
								new SQLException("Cannot save invalid description"));
							}
							if(isInsert){
								manager.newReductionPlan(reductionPlan);
							}
							else{
								manager.updateReductionPlan(reductionPlan);
							}
							fireReductionPlanInserted(reductionPlan);
							ReductionPlanEdit.this.dispose();
						}
					} catch (OHException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(ReductionPlanEdit.this, e.getMessage());
					}
				}
			});
		}
		return jSaveButton;
	}

	
	private void addMedicalReduction(){
		MedicalBrowsingManager medManager=new MedicalBrowsingManager();
		ArrayList<Medical> medArray = medManager.getMedicals();
		
		Icon icon = new ImageIcon("rsc/icons/medical_dialog.png"); //$NON-NLS-1$
		Medical med = (Medical)JOptionPane.showInputDialog(
		                    ReductionPlanEdit.this,
		                    MessageBundle.getMessage("angal.newbill.selectamedical"), //$NON-NLS-1$
		                    MessageBundle.getMessage("angal.newbill.medical"), //$NON-NLS-1$
		                    JOptionPane.PLAIN_MESSAGE,
		                    icon,
		                    medArray.toArray(),
		                    ""); //$NON-NLS-1$
		if (med != null) {
			double rate = 0;
			String strRate = (String) JOptionPane.showInputDialog(
					ReductionPlanEdit.this,
                    MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
                    MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
                    JOptionPane.PLAIN_MESSAGE,
                    icon,
                    null,
                    rate);
			try {
				if (strRate == null || strRate.equals("")) return;
				rate = Double.valueOf(strRate);
				MedicalsReduction medREduction=new MedicalsReduction(reductionPlan.getId(), med.getCode(), rate);
				medicalReductionList.add(medREduction);
				MedicalReductionModel medicalModel=(MedicalReductionModel)medicalReductionTable.getModel();
				medicalModel.fireTableDataChanged();
				
			} catch (Exception eee) {
				JOptionPane.showMessageDialog(ReductionPlanEdit.this, 
						MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private void addExamReduction(){
		ExamBrowsingManager examManager=new ExamBrowsingManager();
		ArrayList<Exam> examArray = examManager.getExams();
		
		Icon icon = new ImageIcon("rsc/icons/medical_dialog.png"); //$NON-NLS-1$
		Exam exam = (Exam)JOptionPane.showInputDialog(
				ReductionPlanEdit.this,
				MessageBundle.getMessage("angal.newbill.selectanexam"), //$NON-NLS-1$
				MessageBundle.getMessage("angal.newbill.exam"), //$NON-NLS-1$
				JOptionPane.PLAIN_MESSAGE,
				icon,
				examArray.toArray(),
				""); //$NON-NLS-1$
				if (exam != null) {
					double rate = 0;
					String strRate = (String) JOptionPane.showInputDialog(
							ReductionPlanEdit.this,
							MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
							MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
							JOptionPane.PLAIN_MESSAGE,
							icon,
							null,
							rate);
					try {
						if (strRate == null || strRate.equals("")) return;
						rate = Double.valueOf(strRate);
						ExamsReduction exaReduction=new ExamsReduction(reductionPlan.getId(), exam.getCode(), rate);
						examReductionList.add(exaReduction);
						ExamReductionModel examModel=(ExamReductionModel)examReductionTable.getModel();
						examModel.fireTableDataChanged();
						
					} catch (Exception eee) {
						JOptionPane.showMessageDialog(ReductionPlanEdit.this, 
								MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
								MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
					}
				}
	}
	private void addOperationReduction(){
		OperationBrowserManager operationManager=new OperationBrowserManager();
		ArrayList<Operation> opeArray = operationManager.getOperation();
		
		Icon icon = new ImageIcon("rsc/icons/medical_dialog.png"); //$NON-NLS-1$
		Operation ope = (Operation)JOptionPane.showInputDialog(
				ReductionPlanEdit.this,
				MessageBundle.getMessage("angal.newbill.selectanoperation"), //$NON-NLS-1$
				MessageBundle.getMessage("angal.newbill.operation"), //$NON-NLS-1$
				JOptionPane.PLAIN_MESSAGE,
				icon,
				opeArray.toArray(),
				""); //$NON-NLS-1$
		if (ope != null) {
			double rate = 0;
			String strRate = (String) JOptionPane.showInputDialog(
					ReductionPlanEdit.this,
					MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
					JOptionPane.PLAIN_MESSAGE,
					icon,
					null,
					rate);
			try {
				if (strRate == null || strRate.equals("")) return;
				rate = Double.valueOf(strRate);
				OperationReduction opeReduction=new OperationReduction(reductionPlan.getId(), ope.getCode(), rate);
				opeReductionList.add(opeReduction);
				OperationReductionModel opeModel=(OperationReductionModel)opeReductionTable.getModel();
				opeModel.fireTableDataChanged();
				
			} catch (Exception eee) {
				JOptionPane.showMessageDialog(ReductionPlanEdit.this, 
						MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private void addOtherReduction(){
		PricesOthersManager otherManager=new PricesOthersManager();
		ArrayList<PricesOthers> othArray = otherManager.getOthers();
		
		Icon icon = new ImageIcon("rsc/icons/medical_dialog.png"); //$NON-NLS-1$
		PricesOthers oth = (PricesOthers)JOptionPane.showInputDialog(
				ReductionPlanEdit.this,
				MessageBundle.getMessage("angal.newbill.pleaseselectanitem"), //$NON-NLS-1$
				MessageBundle.getMessage("angal.newbill.item"), //$NON-NLS-1$
				JOptionPane.PLAIN_MESSAGE,
				icon,
				othArray.toArray(),
				""); //$NON-NLS-1$
		if (oth != null) {
			double rate = 0;
			String strRate = (String) JOptionPane.showInputDialog(
					ReductionPlanEdit.this,
					MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
					MessageBundle.getMessage("angal.reduction.reductionrate"), //$NON-NLS-1$
					JOptionPane.PLAIN_MESSAGE,
					icon,
					null,
					rate);
			try {
				if (strRate == null || strRate.equals("")) return;
				rate = Double.valueOf(strRate);
				OtherReduction othReduction=new OtherReduction(reductionPlan.getId(), oth.getId(), rate);
				othReductionList.add(othReduction);
				OtherReductionModel othModel=(OtherReductionModel)othReductionTable.getModel();
				othModel.fireTableDataChanged();
				
			} catch (Exception eee) {
				JOptionPane.showMessageDialog(ReductionPlanEdit.this, 
						MessageBundle.getMessage("angal.newbill.invalidquantitypleasetryagain"), //$NON-NLS-1$
						MessageBundle.getMessage("angal.newbill.invalidquantity"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private boolean loadDataInObject() throws OHException{
		try {
			if(isInsert) {
				if(manager.getReductionPlan(txtDescription.getText())!= null){
					JOptionPane.showMessageDialog(ReductionPlanEdit.this, MessageBundle.getMessage("angal.reduction.descriptionused"));
					return false;
				}
			}
			else {
				if(!descriptionBackup.equals(txtDescription.getText())) {
					if(manager.getReductionPlan(txtDescription.getText())!= null){
						JOptionPane.showMessageDialog(ReductionPlanEdit.this, MessageBundle.getMessage("angal.reduction.descriptionused"));
						return false;
					}
				}
			}
			reductionPlan.setDescription(txtDescription.getText());
			reductionPlan.setExamReductions(examReductionList);
			reductionPlan.setExamRate(Double.valueOf(txtExamRate.getText()));
			reductionPlan.setMedicalRate(Double.valueOf(txtMedicalRate.getText()));
			reductionPlan.setMedicalsReductions(medicalReductionList);
			reductionPlan.setOperationreductions(opeReductionList);
			reductionPlan.setOperationRate(Double.valueOf(txtOperationRate.getText()));
			reductionPlan.setOtherReductions(othReductionList);
			reductionPlan.setOtherRate(Double.valueOf(txtOtherRate.getText()));
			return true;
		} catch (NumberFormatException e) {
			throw new OHException(e.getMessage());
		}
	}
	private void loadDataFromObject(){
		txtDescription.setText(this.reductionPlan.getDescription());
		descriptionBackup = txtDescription.getText();
		txtMedicalRate.setText(String.valueOf(this.reductionPlan.getMedicalRate()));
		txtExamRate.setText(String.valueOf(this.reductionPlan.getExamRate()));
		txtOperationRate.setText(String.valueOf(this.reductionPlan.getOperationRate()));
		txtOtherRate.setText(String.valueOf(this.reductionPlan.getOtherRate()));
		
		try {
			medicalReductionList=manager.getMedicalsReductions(this.reductionPlan.getId());
			examReductionList = manager.getExamsReductions(this.reductionPlan
					.getId());
			opeReductionList=manager.getOperationsReductions(this.reductionPlan.getId());
			othReductionList = manager.getOtherReductions(this.reductionPlan
					.getId());
			
			((ExamReductionModel) examReductionTable.getModel()).fireTableDataChanged();
			examReductionTable.updateUI();
			((MedicalReductionModel) medicalReductionTable.getModel()).fireTableDataChanged();
			medicalReductionTable.updateUI();
			((OperationReductionModel) opeReductionTable.getModel()).fireTableDataChanged();
			opeReductionTable.updateUI();
			((OtherReductionModel) othReductionTable.getModel()).fireTableDataChanged();
			othReductionTable.updateUI();
			
		} catch (OHException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton
					.setText(MessageBundle.getMessage("angal.common.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}

	private class ExamReductionModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ExamBrowsingManager examManager = new ExamBrowsingManager();

		public ExamReductionModel() throws OHException {
			ReductionPlanManager manager = new ReductionPlanManager();
			try{
			examReductionList = manager.getExamsReductions(reductionPlan
					.getId());
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
		}

		public int getRowCount() {
			if (examReductionList == null)
				return 0;
			return examReductionList.size();
		}

		public String getColumnName(int c) {
			return exaReductionColumn[c];
		}

		public int getColumnCount() {
			return exaReductionColumn.length;
		}

		// { "CODE", "DESCRIPTION","MEDICALRATE","EXAMRATE","OPERATIONRATE",
		// "OTHERRATE"};
		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return examManager.getExam(
						examReductionList.get(r).getExaCode()).getDescription();
			} else if (c == -1) {
				return examReductionList.get(r);
			} else if (c == 1) {
				return examReductionList.get(r).getReductionRate();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	private class OperationReductionModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private OperationBrowserManager opeManager = new OperationBrowserManager();

		public OperationReductionModel() throws OHException {
			ReductionPlanManager manager = new ReductionPlanManager();
			try {
				opeReductionList = manager
						.getOperationsReductions(reductionPlan.getId());
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		public int getRowCount() {
			if (opeReductionList == null)
				return 0;
			return opeReductionList.size();
		}

		public String getColumnName(int c) {
			return opeReductionColumn[c];
		}

		public int getColumnCount() {
			return opeReductionColumn.length;
		}

		// { "CODE", "DESCRIPTION","MEDICALRATE","EXAMRATE","OPERATIONRATE",
		// "OTHERRATE"};
		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return opeManager.getOperationByCode(
						opeReductionList.get(r).getOpeCode()).getDescription();
			} else if (c == -1) {
				return opeReductionList.get(r);
			} else if (c == 1) {
				return opeReductionList.get(r).getReductionRate();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	private class OtherReductionModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PricesOthersManager othManager = new PricesOthersManager();

		public OtherReductionModel() throws OHException {
			ReductionPlanManager manager = new ReductionPlanManager();
			try{
				othReductionList = manager.getOtherReductions(reductionPlan
						.getId());
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
		}

		public int getRowCount() {
			if (othReductionList == null)
				return 0;
			return othReductionList.size();
		}

		public String getColumnName(int c) {
			return othReductionColumn[c];
		}

		public int getColumnCount() {
			return othReductionColumn.length;
		}

		// { "CODE", "DESCRIPTION","MEDICALRATE","EXAMRATE","OPERATIONRATE",
		// "OTHERRATE"};
		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return othManager.getOther(othReductionList.get(r).getOthID());
			} else if (c == -1) {
				return othReductionList.get(r);
			} else if (c == 1) {
				return othReductionList.get(r).getReductionRate();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

	private class MedicalReductionModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		MedicalBrowsingManager medManager = new MedicalBrowsingManager();

		public MedicalReductionModel() throws OHException {
			ReductionPlanManager manager = new ReductionPlanManager();
			try {
				medicalReductionList = manager
						.getMedicalsReductions(reductionPlan.getId());
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
		}

		public int getRowCount() {
			if (medicalReductionList == null)
				return 0;
			return medicalReductionList.size();
		}

		public String getColumnName(int c) {
			return medReductionColumn[c];
		}

		public int getColumnCount() {
			return medReductionColumn.length;
		}

		// { "CODE", "DESCRIPTION","MEDICALRATE","EXAMRATE","OPERATIONRATE",
		// "OTHERRATE"};
		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return medManager.getMedical(
						medicalReductionList.get(r).getMedID())
						.getDescription();
			} else if (c == -1) {
				return medicalReductionList.get(r);
			} else if (c == 1) {
				return medicalReductionList.get(r).getReductionRate();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			// return super.isCellEditable(arg0, arg1);
			return false;
		}
	}
}
