package org.isf.medtype.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.EventListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import org.isf.utils.jobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.menu.gui.MainMenu;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.Sage;

public class MedicalTypeBrowserEdit extends JDialog{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EventListenerList medicalTypeListeners = new EventListenerList();
	private final Logger logger = LoggerFactory.getLogger(MedicalTypeBrowserEdit.class);


    public interface MedicalTypeListener extends EventListener {
        public void medicalTypeUpdated(AWTEvent e);
        public void medicalTypeInserted(AWTEvent e);
    }

    public void addMedicalTypeListener(MedicalTypeListener l) {
        medicalTypeListeners.add(MedicalTypeListener.class, l);
    }

    public void removeMedicalTypeListener(MedicalTypeListener listener) {
        medicalTypeListeners.remove(MedicalTypeListener.class, listener);
    }

    private void fireMedicalInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = medicalTypeListeners.getListeners(MedicalTypeListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((MedicalTypeListener)listeners[i]).medicalTypeInserted(event);
    }
    private void fireMedicalUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = medicalTypeListeners.getListeners(MedicalTypeListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((MedicalTypeListener)listeners[i]).medicalTypeUpdated(event);
    }
    
	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JTextField descriptionTextField = null;
	private VoLimitedTextField codeTextField = null;
	private String lastdescription;
	private String lastaccount;
	private String lastexpenseaccount;
	private MedicalType medicalType = null;
	private boolean insert;
	private JPanel jDataPanel = null;
	private JLabel jCodeLabel = null;
	private JPanel jCodeLabelPanel = null;
	private JPanel jDescriptionLabelPanel = null;
	private JLabel jDescripitonLabel = null;
	private JPanel jAccoutLabelPanel;
	private JTextField accountTextField;
	private JLabel accountLabel;
	
	private JPanel jExpenseAccountLabelPanel;
	private JTextField expenseAccountTextField;
	private JLabel expenseAccountLabel;
	
	/**
     * 
	 * This is the default constructor; we pass the arraylist and the selectedrow
     * because we need to update them
	 */
	public MedicalTypeBrowserEdit(JFrame owner,MedicalType old,boolean inserting) {
		super(owner,true);
		insert = inserting;
		medicalType = old;//disease will be used for every operation
		lastdescription= medicalType.getDescription();
		lastaccount = medicalType.getAccount();
		initialize();
		if(Sage.getSage().ENABLE_SAGE_INTEGRATION)
			this.setSize(this.getWidth(), this.getHeight()+30);
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		logger.info("Initializing Medical type edit Form");
		this.setBounds(300,300,350,250);
		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.medtype.newmedicaltyperecord"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.medtype.editingmedicaltyperecord"));
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.NORTH);  // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);  // Generated
		}
		return jContentPane;
	}

	/**
	 * This method initializes dataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			//dataPanel.setLayout(new BoxLayout(getDataPanel(), BoxLayout.Y_AXIS));  // Generated
			dataPanel.add(getJDataPanel(), null);
		}
		return dataPanel;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);  // Generated
			buttonPanel.add(getCancelButton(), null);  // Generated
		}
		return buttonPanel;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));  // Generated
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(MessageBundle.getMessage("angal.common.ok"));  // Generated
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String key = codeTextField.getText();
					MedicalTypeBrowserManager manager = new MedicalTypeBrowserManager();
					if (key.equals("")){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.medtype.pleaseinsertacode"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}	
					//System.out.print(key.length());
					if (key.length()>1){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.medtype.codetoolongmaxchar"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						
						return;	
					}
					if(insert){
					if (manager.codeControl(key)){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.medtype.codealreadyinuse"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						codeTextField.setText("");
						return;	
					}};
					if (descriptionTextField.getText().equals("")){
						JOptionPane.showMessageDialog(				
		                        null,
		                        MessageBundle.getMessage("angal.medtype.pleaseinsertavaliddescription"),
		                        MessageBundle.getMessage("angal.hospital"),
		                        JOptionPane.PLAIN_MESSAGE);
						return;	
					}
					if (descriptionTextField.getText().equals(lastdescription) && accountTextField.getText().equals(lastaccount) && expenseAccountTextField.getText().equals(lastexpenseaccount)){
						dispose();	
					}
					medicalType.setDescription(descriptionTextField.getText());
					medicalType.setCode(codeTextField.getText());
					medicalType.setAccount(accountTextField.getText());
					medicalType.setExpenseAccount(expenseAccountTextField.getText());
					boolean result = false;
					if (insert) {      // inserting
						result = manager.newMedicalType(medicalType);
						if (result) {
                           fireMedicalInserted();
                        }
						if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medtype.thedatacouldnotbesaved"));
	                    else  dispose();
                    }
                    else {                          // updating
                    	if (descriptionTextField.getText().equals(lastdescription) && accountTextField.getText().equals(lastaccount) && expenseAccountTextField.getText().equals(lastexpenseaccount)){
    						dispose();	
    					}else{
    						result = manager.updateMedicalType(medicalType);
						if (result) {
							fireMedicalUpdated();
                        }
						if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.medtype.thedatacouldnotbesaved"));
                        else  dispose();
    					}
                    	
					}
					
                }
			});
		}
		return okButton;
	}

	/**
	 * This method initializes descriptionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextField(20);
			if (!insert) {
				descriptionTextField.setText(medicalType.getDescription());
				lastdescription=medicalType.getDescription();
			} 
		}
		return descriptionTextField;
	}
	
	/**
	 * This method initializes codeTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new VoLimitedTextField(2);
			if (!insert) {
				codeTextField.setText(medicalType.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}

	/**
	 * This method initializes jDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDataPanel() {
		if (jDataPanel == null) {
			jDataPanel = new JPanel();
			jDataPanel.setLayout(new BoxLayout(getJDataPanel(),BoxLayout.Y_AXIS));
			jDataPanel.add(getJCodeLabelPanel(), null);
			jDataPanel.add(getCodeTextField(), null);
			jDataPanel.add(getJDescriptionLabelPanel(), null);
			jDataPanel.add(getDescriptionTextField(), null);
			jDataPanel.add(getJAccoutLabelPanel());
			jDataPanel.add(getAccountTextField());
			jDataPanel.add(getJExpenseAccountLabelPanel());
			jDataPanel.add(getExpenseAccountTextField());
			
		}
		return jDataPanel;
	}

	/**
	 * This method initializes jCodeLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getJCodeLabel() {
		if (jCodeLabel == null) {
			jCodeLabel = new JLabel();
			jCodeLabel.setText(MessageBundle.getMessage("angal.medtype.codemaxchar"));
		}
		return jCodeLabel;
	}

	/**
	 * This method initializes jCodeLabelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJCodeLabelPanel() {
		if (jCodeLabelPanel == null) {
			jCodeLabelPanel = new JPanel();
			//jCodeLabelPanel.setLayout(new BorderLayout());
			jCodeLabelPanel.add(getJCodeLabel(), BorderLayout.CENTER);
		}
		return jCodeLabelPanel;
	}

	/**
	 * This method initializes jDescriptionLabelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDescriptionLabelPanel() {
		if (jDescriptionLabelPanel == null) {
			jDescripitonLabel = new JLabel();
			jDescripitonLabel.setText(MessageBundle.getMessage("angal.medtype.description"));
			jDescriptionLabelPanel = new JPanel();
			jDescriptionLabelPanel.add(jDescripitonLabel, null);
		}
		return jDescriptionLabelPanel;
	}
	


	private JPanel getJAccoutLabelPanel() {
		if (jAccoutLabelPanel == null) {
			jAccoutLabelPanel = new JPanel();
			jAccoutLabelPanel.add(getAccountLabel());
		}
		jAccoutLabelPanel.setVisible(Sage.getSage().ENABLE_SAGE_INTEGRATION);
		return jAccoutLabelPanel;
	}
	private JTextField getAccountTextField() {
		if (accountTextField == null) {
			accountTextField = new JTextField();
			accountTextField.setColumns(10);
			if (!insert) {
				accountTextField.setText(medicalType.getAccount());
				lastaccount = medicalType.getAccount();
				lastexpenseaccount = medicalType.getExpenseAccount();
			} 
		}
		accountTextField.setVisible(Sage.getSage().ENABLE_SAGE_INTEGRATION);
		return accountTextField;
	}
	private JLabel getAccountLabel() {
		if (accountLabel == null) {
			accountLabel = new JLabel(MessageBundle.getMessage("angal.medicals.accountnumber"));
		}
		return accountLabel;
	}
	
	private JPanel getJExpenseAccountLabelPanel() {
		if (jExpenseAccountLabelPanel == null) {
			jExpenseAccountLabelPanel = new JPanel();
			jExpenseAccountLabelPanel.add(getExpenseAccountLabel());			
		}
		jExpenseAccountLabelPanel.setVisible(Sage.getSage().ENABLE_SAGE_INTEGRATION);
		return jExpenseAccountLabelPanel;
	}
	private JTextField getExpenseAccountTextField() {
		if (expenseAccountTextField == null) {
			expenseAccountTextField = new JTextField();
			expenseAccountTextField.setColumns(10);
			if (!insert) {
				expenseAccountTextField.setText(medicalType.getExpenseAccount());
				lastexpenseaccount = medicalType.getExpenseAccount();
			}
		}
		expenseAccountTextField.setVisible(Sage.getSage().ENABLE_SAGE_INTEGRATION);
		return expenseAccountTextField;
	}
	private JLabel getExpenseAccountLabel() {
		if (expenseAccountLabel == null) {
			expenseAccountLabel = new JLabel(MessageBundle.getMessage("angal.supplier.account"));
		}
		return expenseAccountLabel;
	}
}  //  @jve:decl-index=0:visual-constraint="146,61"


