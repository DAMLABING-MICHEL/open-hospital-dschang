package org.isf.medtype.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.Sage;
import org.isf.medtype.gui.MedicalTypeBrowserEdit.MedicalTypeListener;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.utils.jobjects.ModalJFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Browsing of table MedicalDsrType
 * 
 * @author Furlanetto, Zoia, Finotto
 * 
 */

public class MedicalTypeBrowser extends ModalJFrame implements MedicalTypeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<MedicalType> pMedicalType;
	//private String[] pColums = { MessageBundle.getMessage("angal.medtype.codem"), MessageBundle.getMessage("angal.medtype.descriptionm"), MessageBundle.getMessage("angal.medicals.accountnumber")};
	private String[] pColums = null;
	private ArrayList<String> pColumsTemp = new ArrayList<String>();
	private int[] pColumwidth = {80, 200, 77, 77};
	private JPanel jContainPanel = null;
	private JPanel jButtonPanel = null;
	private JButton jNewButton = null;
	private JButton jEditButton = null;
	private JButton jCloseButton = null;
	private JButton jDeteleButton = null;
	private JTable jTable = null;
	private MedicalTypeBrowserModel model;
	private int selectedrow;
	private MedicalTypeBrowserManager manager = new MedicalTypeBrowserManager();
	private MedicalType medicalType = null;
	private final JFrame myFrame;
	
	private final Logger logger = LoggerFactory.getLogger(MedicalTypeBrowserEdit.class);
	private boolean sageEnabled = Sage.getSage().ENABLE_SAGE_INTEGRATION;
	
	
	
	/**
	 * This method initializes 
	 * 
	 */
	public MedicalTypeBrowser() {
		super();
		myFrame=this;
		pColumsTemp.add(MessageBundle.getMessage("angal.opetype.codem"));
		pColumsTemp.add(MessageBundle.getMessage("angal.opetype.descriptionm"));
		pColums = new String[2];
		if(sageEnabled){ 
			pColumsTemp.add(MessageBundle.getMessage("angal.medicals.accountnumber"));
			pColumsTemp.add(MessageBundle.getMessage("angal.medicals.expenseaccountnumber"));
			pColums = new String[4];
		}
		pColumsTemp.toArray(pColums);
		initialize();
		setVisible(true);
	}
	
	
	private void initialize() {
		logger.info("initializing medical type browser");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		final int pfrmBase = 10;
        final int pfrmWidth = 5;
        final int pfrmHeight =4;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                screensize.width * pfrmWidth / pfrmBase, screensize.height * pfrmHeight / pfrmBase);
		this.setTitle(MessageBundle.getMessage("angal.medtype.medicaltypebrowsing"));
		this.setContentPane(getJContainPanel());
		//pack();	
	}
	
	
	private JPanel getJContainPanel() {
		if (jContainPanel == null) {
			jContainPanel = new JPanel();
			jContainPanel.setLayout(new BorderLayout());
			jContainPanel.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContainPanel.add(new JScrollPane(getJTable()),
					java.awt.BorderLayout.CENTER);
			validate();
		}
		return jContainPanel;
	}
	
	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			jButtonPanel.add(getJNewButton(), null);
			jButtonPanel.add(getJEditButton(), null);
			jButtonPanel.add(getJDeteleButton(), null);
			jButtonPanel.add(getJCloseButton(), null);
		}
		return jButtonPanel;
	}
	
	
	private JButton getJNewButton() {
		if (jNewButton == null) {
			jNewButton = new JButton();
			jNewButton.setText(MessageBundle.getMessage("angal.common.new"));
			jNewButton.setMnemonic(KeyEvent.VK_N);
			jNewButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					medicalType = new MedicalType("","");
					MedicalTypeBrowserEdit newrecord = new MedicalTypeBrowserEdit(myFrame,medicalType, true);
					newrecord.addMedicalTypeListener(MedicalTypeBrowser.this);
					newrecord.setVisible(true);
				}
			});
		}
		return jNewButton;
	}
	
	/**
	 * This method initializes jEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJEditButton() {
		if (jEditButton == null) {
			jEditButton = new JButton();
			jEditButton.setText(MessageBundle.getMessage("angal.common.edit"));
			jEditButton.setMnemonic(KeyEvent.VK_E);
			jEditButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						selectedrow = jTable.getSelectedRow();
						medicalType = (MedicalType) (((MedicalTypeBrowserModel) model)
								.getValueAt(selectedrow, -1));
						MedicalTypeBrowserEdit newrecord = new MedicalTypeBrowserEdit(myFrame,medicalType, false);
						newrecord.addMedicalTypeListener(MedicalTypeBrowser.this);
						newrecord.setVisible(true);
					}
				}
			});
		}
		return jEditButton;
	}
	
	/**
	 * This method initializes jCloseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(MessageBundle.getMessage("angal.common.close"));
			jCloseButton.setMnemonic(KeyEvent.VK_C);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
		}
		return jCloseButton;
	}
	
	/**
	 * This method initializes jDeteleButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeteleButton() {
		if (jDeteleButton == null) {
			jDeteleButton = new JButton();
			jDeteleButton.setText(MessageBundle.getMessage("angal.common.delete"));
			jDeteleButton.setMnemonic(KeyEvent.VK_D);
			jDeteleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (jTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.common.pleaseselectarow"), MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					} else {
						MedicalType dis = (MedicalType) (((MedicalTypeBrowserModel) model)
								.getValueAt(jTable.getSelectedRow(), -1));
						int n = JOptionPane.showConfirmDialog(null,
								MessageBundle.getMessage("angal.medtype.deletemedicaltype")+" \" "+dis.getDescription() + "\" ?",
								MessageBundle.getMessage("angal.hospital"), JOptionPane.YES_NO_OPTION);
						
						if ((n == JOptionPane.YES_OPTION)
								&& (manager.deleteMedicalType(dis))) {
							pMedicalType.remove(jTable.getSelectedRow());
							model.fireTableDataChanged();
							jTable.updateUI();
						}
					}
				}
				
			});
		}
		return jDeteleButton;
	}
	
	public JTable getJTable() {
		if (jTable == null) {
			model = new MedicalTypeBrowserModel();
			jTable = new JTable(model);
			jTable.getColumnModel().getColumn(0).setMinWidth(pColumwidth[0]);
			jTable.getColumnModel().getColumn(1).setMinWidth(pColumwidth[1]);
			if(sageEnabled){
				jTable.getColumnModel().getColumn(2).setMinWidth(pColumwidth[2]);
				jTable.getColumnModel().getColumn(2).setMinWidth(pColumwidth[3]);
			}
		}return jTable;
	}
	
	
class MedicalTypeBrowserModel extends DefaultTableModel {
		
		
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public MedicalTypeBrowserModel() {
			MedicalTypeBrowserManager manager = new MedicalTypeBrowserManager();
			pMedicalType = manager.getMedicalType();
		}
		
		public int getRowCount() {
			if (pMedicalType == null)
				return 0;
			return pMedicalType.size();
		}
		
		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == 0) {
				return pMedicalType.get(r).getCode();
			} else if (c == -1) {
				return pMedicalType.get(r);
			} else if (c == 1) {
				return pMedicalType.get(r).getDescription();
			}else if (c == 2 && sageEnabled) {
				return pMedicalType.get(r).getAccount();
			}else if (c == 3 && sageEnabled) {
				return pMedicalType.get(r).getExpenseAccount();
			}
			return null;
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			//return super.isCellEditable(arg0, arg1);
			return false;
		}
	}

public void medicalTypeUpdated(AWTEvent e) {
	pMedicalType.set(selectedrow, medicalType);
	((MedicalTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
	jTable.updateUI();
	if ((jTable.getRowCount() > 0) && selectedrow > -1)
		jTable.setRowSelectionInterval(selectedrow, selectedrow);
}


public void medicalTypeInserted(AWTEvent e) {
	pMedicalType.add(0, medicalType);
	((MedicalTypeBrowserModel) jTable.getModel()).fireTableDataChanged();
	if (jTable.getRowCount() > 0)
		jTable.setRowSelectionInterval(0, 0);
}
	
	
}
