
package org.isf.menu.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.isf.menu.manager.*;
import org.isf.menu.model.*;
import org.isf.parameters.manager.Param;
import org.isf.utils.db.BCrypt;
import org.isf.utils.jobjects.VoFloatTextField;
import org.isf.ward.manager.WardBrowserManager;
import org.isf.ward.model.Ward;
import org.isf.generaldata.MessageBundle;
import org.isf.generaldata.SmsParameters;

public class UserEdit extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EventListenerList userListeners = new EventListenerList();

    public interface UserListener extends EventListener {
        public void userUpdated(AWTEvent e);
        public void userInserted(AWTEvent e);
    }

    public void addUserListener(UserListener l) {
        userListeners.add(UserListener.class, l);
    }

    public void removeUserListener(UserListener listener) {
        userListeners.remove(UserListener.class, listener);
    }

    private void fireUserInserted(User aUser) {
        AWTEvent event = new AWTEvent(aUser, AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = userListeners.getListeners(UserListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((UserListener)listeners[i]).userInserted(event);
    }
    private void fireUserUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = userListeners.getListeners(UserListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((UserListener)listeners[i]).userUpdated(event);
        
       
        
    }
    
	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JLabel descLabel = null;
	private JTextField descriptionTextField = null;
	private JTextField nameTextField = null;
	private JPasswordField pwdTextField = null;
	private JPasswordField pwd2TextField = null;
	private JLabel typeLabel = null;
	private JLabel nameLabel = null;
	private JLabel pwdLabel = null;
	private JLabel pwd2Label = null;
	private JComboBox typeComboBox = null;
	private JLabel wardLabel = null;
	private JComboBox wardCombobox = null;
    
	private JLabel firstnameLabel = null;
	private JLabel surnameLabel = null;
	private JLabel phoneLabel = null;
	
	private JTextField firstnameTextField = null;
	private JTextField surnameTextField = null;
	private JTextField phoneTextField = null;
	
	private User user = null;
	private boolean insert = false;
    
	/**
     * 
	 * This is the default constructor; we pass the arraylist and the selectedrow
     * because we need to update them
	 */
	public UserEdit(UserBrowsing parent, User old,boolean inserting) {
		super(parent, (inserting?MessageBundle.getMessage("angal.menu.newuserrecord"):MessageBundle.getMessage("angal.menu.editinguserrecord")),true);
		addUserListener(parent);
		insert = inserting;
		user = old;		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
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
	 * tipo combo
	 * nome text
	 * desc text
	 * pwd  text
	 * pwd2	text
	 */
	private JPanel getDataPanel() {
		if (dataPanel == null) {
			typeLabel = new JLabel();
			typeLabel.setText(MessageBundle.getMessage("angal.menu.group"));						
			nameLabel = new JLabel();
			nameLabel.setText(MessageBundle.getMessage("angal.menu.name")); 
			descLabel = new JLabel();
			descLabel.setText(MessageBundle.getMessage("angal.menu.description"));  
			
			//added julio
			firstnameLabel = new JLabel();
			firstnameLabel.setText(MessageBundle.getMessage("angal.users.firstname"));
			surnameLabel = new JLabel();
			surnameLabel.setText(MessageBundle.getMessage("angal.users.surname"));
			phoneLabel = new JLabel();
			phoneLabel.setText(MessageBundle.getMessage("angal.users.phone"));
			JPanel firstnamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
			JPanel surnamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
			JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
			//
			
			
			JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
			JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
			JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
			dataPanel = new JPanel();
			dataPanel.setLayout(new BoxLayout(getDataPanel(), BoxLayout.Y_AXIS));  
			
			dataPanel.add(typeLabel, null); 			
			comboPanel.add(getTypeComboBox());
			dataPanel.add(comboPanel);
			
			//added julio
			dataPanel.add(firstnameLabel, null); 			
			firstnamePanel.add(getFirstNameTextField());
			dataPanel.add(firstnamePanel);
			
			dataPanel.add(surnameLabel, null); 			
			surnamePanel.add(getSurnameTextField());
			dataPanel.add(surnamePanel);
			
			dataPanel.add(phoneLabel, null); 			
			phonePanel.add(getPhoneTextField());
			dataPanel.add(phonePanel);
			///
			
			dataPanel.add(nameLabel, null);  
			namePanel.add(getNameTextField());			
		    dataPanel.add(namePanel);
		    
		    if (insert) {
		    	JPanel pwdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		    	pwdLabel = new JLabel();
				pwdLabel.setText(MessageBundle.getMessage("angal.menu.password"));
				dataPanel.add(pwdLabel, null);  
				pwdPanel.add(getPwdTextField());
				dataPanel.add(pwdPanel);
				
				JPanel pwd2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
				pwd2Label = new JLabel();
				pwd2Label.setText(MessageBundle.getMessage("angal.menu.retypepassword"));
				dataPanel.add(pwd2Label, null);  
				pwd2Panel.add(getPwd2TextField());
				dataPanel.add(pwd2Panel);
		    }
				
		    if(Param.bool("STOCKMVTONBILLSAVE")){
		    	wardLabel=new JLabel();
				wardLabel.setText(MessageBundle.getMessage("angal.menu.ward"));
				JPanel wardPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		    	dataPanel.add(wardLabel, null);
		    	
		    	WardBrowserManager manager = new WardBrowserManager();
				ArrayList<Ward> wards = manager.getWards();
				if(wardCombobox==null){
					wardCombobox=new JComboBox();
				}
				wardCombobox.removeAllItems();
				Ward selected=null;
				for (Ward ward : wards) {
					if(ward.isPharmacy()){
						wardCombobox.addItem(ward);
						if(ward.getCode().equals(user.getWardCode())){
							selected=ward;
						}
					}
				}
				if(!insert){
					wardCombobox.setSelectedItem(selected);
				}
		    	wardPanel.add(wardCombobox);
		    	dataPanel.add(wardPanel);
		    }
		    
			dataPanel.add(descLabel, null);  
			descPanel.add(getDescriptionTextField());
			dataPanel.add(descPanel);
						 
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
			buttonPanel.add(getOkButton(), null);  
			buttonPanel.add(getCancelButton(), null); 
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
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel"));  
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
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (nameTextField.getText().equals("")){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.pleaseinsertavalidusername"));
						return;
					}	
					///added julio
					
					if (firstnameTextField.getText().equals("")){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.users.entervalidfirstname"));
						return;
					}
					if (surnameTextField.getText().equals("")){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.users.entervalidsurname"));
						return;
					}
					if (phoneTextField.getText().equals("")){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.users.entervalidphone"));
						return;
					}
					
					///
					char[] password = new char[0];							
					char[] repeatPassword = new char[0];											
					if (insert) {
						password=pwdTextField.getPassword();
						repeatPassword=pwd2TextField.getPassword();
						if (Arrays.equals(password, new char[0])){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.pleaseinsertapassword"));
							return;
						}
						if (Arrays.equals(repeatPassword, new char[0])){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.pleaseretypethepassword"));
							return;
						}
						if (!Arrays.equals(password, repeatPassword)){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.passwordincorrectpleaseretype"));
							return;
						}
					}
					UserBrowsingManager manager = new UserBrowsingManager();
					if (insert) {
						String hashed = BCrypt.hashpw(new String(password), BCrypt.gensalt());
						user.setUserGroupName(((UserGroup)typeComboBox.getSelectedItem()).getCode());
						user.setWardCode(((Ward)wardCombobox.getSelectedItem()).getCode());
						user.setUserName(nameTextField.getText());
						user.setPasswd(hashed);
						user.setDesc(descriptionTextField.getText());
						
						user.setName(firstnameTextField.getText());
						user.setSurname(surnameTextField.getText());
						user.setPhone(phoneTextField.getText());
					} else {
						user.setUserGroupName((String)typeComboBox.getSelectedItem());
						user.setWardCode(((Ward)wardCombobox.getSelectedItem()).getCode());
						user.setUserName(nameTextField.getText());
						user.setDesc(descriptionTextField.getText());
						
						user.setName(firstnameTextField.getText());
						user.setSurname(surnameTextField.getText());
						user.setPhone(phoneTextField.getText());
					}
					
					boolean result = false;
					if (insert) {      // inserting
						//System.out.println("saving... "+user);
						result = manager.newUser(user);
						if (result) {
                           fireUserInserted(user);
                        }
                    } else {                          // updating
						result = manager.updateUser(user);
						if (result) {
							fireUserUpdated();
							if(user.getUserName().equals(MainMenu.getUser())){
								MainMenu.setCurrentUser(user);
							}
                        }
					}
					if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.thedatacouldnotbesaved"));
                    else {
                    	Arrays.fill(password, '0');
                    	Arrays.fill(repeatPassword, '0');
                    	dispose();
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
			if (insert) {
				descriptionTextField = new JTextField();
			} else {
				descriptionTextField = new JTextField(user.getDesc());
			}
			descriptionTextField.setColumns(25);
		}	
		return descriptionTextField;
	}
	
	
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			if (insert) {
				nameTextField = new JTextField();
			} else {
				nameTextField = new JTextField(user.getUserName());
				nameTextField.setEnabled(false);
			}
			nameTextField.setColumns(15);
		}
		return nameTextField;
	}

	private JPasswordField getPwdTextField() {
		if (pwdTextField == null) {
			pwdTextField = new JPasswordField(15);
		}
		return pwdTextField;
	}
	
	private JTextField getPwd2TextField() {
		if (pwd2TextField == null) {
			pwd2TextField = new JPasswordField(15);
		}
		return pwd2TextField;
	}
	
	/////added julio
	private JTextField getFirstNameTextField() {
		if (firstnameTextField == null) {
			if (insert) {
				firstnameTextField = new JTextField();
			} else {
				firstnameTextField = new JTextField(user.getName());				
			}
			firstnameTextField.setColumns(15);
		}
		return firstnameTextField;
	}
	private JTextField getSurnameTextField() {
		if (surnameTextField == null) {
			if (insert) {
				surnameTextField = new JTextField();
			} else {
				surnameTextField = new JTextField(user.getSurname());				
			}
			surnameTextField.setColumns(15);
		}
		return surnameTextField;
	}

	private JTextField getPhoneTextField() {
		SmsParameters.getSmsParameters();
		if (phoneTextField == null) {
			if (insert) {
				phoneTextField = new VoFloatTextField("",15);
				phoneTextField.setColumns(15);
				phoneTextField.setText(SmsParameters.ICC);
			} else {
				phoneTextField = new VoFloatTextField("",15);
				phoneTextField.setColumns(15);
				phoneTextField.setText(user.getPhone());						
			}
			
			phoneTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String previous = 	phoneTextField.getText();
					int length = previous.length();
					int maxLength = SmsParameters.ICC.length() + Integer.parseInt(Param.string("PHONELENGTH"));
					if(length>maxLength){
						phoneTextField.setText(previous.substring(0, maxLength));
					}
				}
			});
		}
		return phoneTextField;
	}
	
	////

	/**
	 * This method initializes typeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getTypeComboBox() {
		if (typeComboBox == null) {
			typeComboBox = new JComboBox();
			if (insert) {
				UserBrowsingManager manager = new UserBrowsingManager();
				ArrayList<UserGroup> group = manager.getUserGroup();
				for (UserGroup elem : group) {
					typeComboBox.addItem(elem);
				}
			} else {
				typeComboBox.addItem(user.getUserGroupName());
				typeComboBox.setEnabled(false);
			}
			Dimension d = typeComboBox.getPreferredSize();
			typeComboBox.setPreferredSize(new Dimension(150,d.height));
			
		}
		return typeComboBox;
	}

	public JLabel getFirstnameLabel() {
		return firstnameLabel;
	}

	public void setFirstnameLabel(JLabel firstnameLabel) {
		this.firstnameLabel = firstnameLabel;
	}

	public JLabel getSurnameLabel() {
		return surnameLabel;
	}

	public void setSurnameLabel(JLabel surnameLabel) {
		this.surnameLabel = surnameLabel;
	}

	public JLabel getPhoneLabel() {
		return phoneLabel;
	}

	public void setPhoneLabel(JLabel phoneLabel) {
		this.phoneLabel = phoneLabel;
	}


}
