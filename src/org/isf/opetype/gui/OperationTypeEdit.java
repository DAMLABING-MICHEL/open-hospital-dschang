package org.isf.opetype.gui;

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


import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.model.OperationType;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.Sage;

public class OperationTypeEdit extends JDialog{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EventListenerList operationTypeListeners = new EventListenerList();

    public interface OperationTypeListener extends EventListener {
        public void operationTypeUpdated(AWTEvent e);
        public void operationTypeInserted(AWTEvent e);
    }

    public void addOperationTypeListener(OperationTypeListener l) {
        operationTypeListeners.add(OperationTypeListener.class, l);
    }

    public void removeOperationTypeListener(OperationTypeListener listener) {
        operationTypeListeners.remove(OperationTypeListener.class, listener);
    }

    private void fireOperationInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = operationTypeListeners.getListeners(OperationTypeListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((OperationTypeListener)listeners[i]).operationTypeInserted(event);
    }
    private void fireOperationUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = operationTypeListeners.getListeners(OperationTypeListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((OperationTypeListener)listeners[i]).operationTypeUpdated(event);
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
	private OperationType operationType = null;
	private boolean insert;
	private JPanel jDataPanel = null;
	private JLabel jCodeLabel = null;
	private JPanel jCodeLabelPanel = null;
	private JPanel jDescriptionLabelPanel = null;
	private JLabel jDescripitonLabel = null;
	private JPanel jAccountLabelPanel;
	private JTextField accountTextField;
	private JLabel accountLabel;
	
//	private JPanel jExpenseAccountLabelPanel;
//	private JTextField expenseAccountTextField;
//	private JLabel expenseAccountLabel;
//	private String lastexpenseaccount;
	/**
     * 
	 * This is the default constructor; we pass the arraylist and the selectedrow
     * because we need to update them
	 */
	public OperationTypeEdit(JFrame owner,OperationType old,boolean inserting) {
		super(owner,true);
		insert = inserting;
		operationType = old;//operation will be used for every operation
		lastdescription= operationType.getDescription();
		initialize();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		//this.setBounds(300,300,350,180);
		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.opetype.newoperationtyperecord"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.opetype.editingoperationtyperecord"));
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
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
					OperationTypeBrowserManager manager = new OperationTypeBrowserManager();
					if (key.equals("")){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.opetype.pleaseinsertacode"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						return;
					}	
					//System.out.print(key.length());
					if (key.length()>2){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.opetype.codetoolongmaxchars"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						
						return;	
					}
					if(insert){
					if (manager.codeControl(key)){
						JOptionPane.showMessageDialog(				
								null,
								MessageBundle.getMessage("angal.opetype.codealreadyinuse"),
								MessageBundle.getMessage("angal.hospital"),
								JOptionPane.PLAIN_MESSAGE);
						codeTextField.setText("");
						return;	
					}};
					if (descriptionTextField.getText().equals("")){
						JOptionPane.showMessageDialog(				
		                        null,
		                        MessageBundle.getMessage("angal.opetype.pleaseinsertavaliddescription"),
		                        MessageBundle.getMessage("angal.hospital"),
		                        JOptionPane.PLAIN_MESSAGE);
						return;	
					}
					//if (descriptionTextField.getText().equals(lastdescription) && accountTextField.getText().equals(lastaccount) && expenseAccountTextField.getText().equals(lastexpenseaccount)){
					if (descriptionTextField.getText().equals(lastdescription) && accountTextField.getText().equals(lastaccount) ){	
						dispose();	
					}
					operationType.setDescription(descriptionTextField.getText());
					operationType.setCode(codeTextField.getText());
					operationType.setAccount(accountTextField.getText());//
					//operationType.setExpenseAccount(expenseAccountTextField.getText());
					boolean result = false;
					if (insert) {      // inserting
						result = manager.newOperationType(operationType);
						if (result) {
                           fireOperationInserted();
                        }
						if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opetype.thedatacouldnotbesaved"));
	                    else  dispose();
                    }
                    else {                          // updating
                    	//if (descriptionTextField.getText().equals(lastdescription) && accountTextField.getText().equals(lastaccount) && expenseAccountTextField.getText().equals(lastexpenseaccount)){
                    	if (descriptionTextField.getText().equals(lastdescription) && accountTextField.getText().equals(lastaccount) ){
    						dispose();	
    					}else{
    						result = manager.updateOperationType(operationType);
						if (result) {
							fireOperationUpdated();
                        }
						if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.opetype.thedatacouldnotbesaved"));
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
				descriptionTextField.setText(operationType.getDescription());
				lastdescription=operationType.getDescription();
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
				codeTextField.setText(operationType.getCode());
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
			jDataPanel.add(getJAccountLabelPanel());
			jDataPanel.add(getAccountTextField());	
			//jDataPanel.add(getJExpenseAccountLabelPanel());
			//jDataPanel.add(getExpenseAccountTextField());
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
			jCodeLabel.setText(MessageBundle.getMessage("angal.opetype.codemaxchars"));
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
			jDescripitonLabel.setText(MessageBundle.getMessage("angal.opetype.description"));
			jDescriptionLabelPanel = new JPanel();
			jDescriptionLabelPanel.add(jDescripitonLabel, null);
		}
		return jDescriptionLabelPanel;
	}
	


	private JPanel getJAccountLabelPanel() {
		if (jAccountLabelPanel == null) {
			jAccountLabelPanel = new JPanel();
			jAccountLabelPanel.add(getAccountLabel());		
		}
		jAccountLabelPanel.setVisible(Sage.getSage().ENABLE_SAGE_INTEGRATION);
		return jAccountLabelPanel;
	}
	private JTextField getAccountTextField() {
		if (accountTextField == null) {
			accountTextField = new JTextField();
			accountTextField.setColumns(10);
			if (!insert) {
				accountTextField.setText(operationType.getAccount());
				lastaccount = operationType.getAccount();
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
	
	/*private JPanel getJExpenseAccountLabelPanel() {
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
				expenseAccountTextField.setText(operationType.getExpenseAccount());
				lastexpenseaccount = operationType.getExpenseAccount();
			}
		}
		expenseAccountTextField.setVisible(Sage.getSage().ENABLE_SAGE_INTEGRATION);
		return expenseAccountTextField;
	}
	private JLabel getExpenseAccountLabel() {
		if (expenseAccountLabel == null) {
			expenseAccountLabel = new JLabel(MessageBundle.getMessage("angal.medicals.accountnumber"));
		}
		return expenseAccountLabel;
	}*/
}  //  @jve:decl-index=0:visual-constraint="146,61"


