package org.isf.menu.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.mail.Message;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.utils.db.BCrypt;
import org.isf.utils.jobjects.ModalJFrame;

import javassist.runtime.Desc;


public class UserProfileBrowser extends ModalJFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panelContent;
	private JPanel panelHeader;
	private JPanel panelFooter;
	private JButton editButton;
	private JButton buttonChangePassword;
	
	private UserProfileBrowser myFrame;
	private UserBrowsingManager manager = new UserBrowsingManager();
	
	public UserProfileBrowser() {
		myFrame = this;
		initComponents();
	}
	private void initComponents() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(350, 480));
		setLocationRelativeTo(null); // center
		setTitle(MessageBundle.getMessage("angal.menu.profiletitle"));
		setResizable(false);
		panelContent = getPanelContent();
		panelFooter = getPanelFooter();
		panelHeader = getPanelHeader();
		getContentPane().add(panelHeader,BorderLayout.NORTH);
		getContentPane().add(panelContent);
		getContentPane().add(panelFooter,BorderLayout.SOUTH);
		addWindowListener(new WindowAdapter(){	
			public void windowClosing(WindowEvent e) {
				dispose();
			}			
		});
	}
	GridBagLayout gbl = new GridBagLayout();
	private void initGridbagLayout() {
		gbl.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
	}
	private JPanel getPanelHeader() {
		panelHeader = new JPanel();
		initGridbagLayout();
		panelHeader.setLayout(new BorderLayout());
		
		JPanel jPanelIcon = new JPanel();
		jPanelIcon.setLayout(gbl);
		panelHeader.add(jPanelIcon,BorderLayout.NORTH);
		JLabel jLabelIcon = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "userIcon.png"));
		GridBagConstraints gbc_jLabelIcon = new GridBagConstraints();
		gbc_jLabelIcon.gridx = 0;
		gbc_jLabelIcon.gridy = 0;
		gbc_jLabelIcon.insets = new Insets(15, 90, 0, 90);
		gbc_jLabelIcon.gridwidth = GridBagConstraints.REMAINDER;
		jPanelIcon.add(jLabelIcon,gbc_jLabelIcon);
		
		JPanel jPaneluserNname = new JPanel();
		jPaneluserNname.setLayout(gbl);
		panelHeader.add(jPaneluserNname,BorderLayout.CENTER);
		JLabel jLabeluserNname = new JLabel(MainMenu.getCurrentUser().getUserName().substring(0, 1).toUpperCase() + 
				MainMenu.getCurrentUser().getUserName().substring(1));
		jLabeluserNname.setFont(new Font(getName(), Font.BOLD, 20));
		GridBagConstraints gbc_jLabeluserNname = new GridBagConstraints();
		gbc_jLabeluserNname.gridx = gbc_jLabeluserNname.gridy = 0;
		gbc_jLabeluserNname.gridwidth = GridBagConstraints.REMAINDER;
		gbc_jLabeluserNname.insets = new Insets(0, 90, 0, 90);
		jPaneluserNname.add(jLabeluserNname,gbc_jLabeluserNname);
		
		JLabel jLabelUserGroupName = new JLabel(MainMenu.getCurrentUser().getUserGroupName());
		jLabelUserGroupName.setFont(new Font(getName(), Font.BOLD, 15));
		jLabelUserGroupName.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelUserGroupName = new GridBagConstraints();
		gbc_jLabelUserGroupName.gridx = 0;
		gbc_jLabelUserGroupName.gridy = 1;
		gbc_jLabelUserGroupName.gridwidth = GridBagConstraints.REMAINDER;
		gbc_jLabelUserGroupName.insets = new Insets(0, 90, 40, 90);
		jPaneluserNname.add(jLabelUserGroupName,gbc_jLabelUserGroupName);
		return panelHeader;
	}
	@SuppressWarnings("deprecation")
	private JPanel getPanelContent() {
		panelContent = new JPanel();
		initGridbagLayout();
		panelContent.setLayout(gbl);
		JLabel jLabelIcon = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "user-group-icon.png"));
		GridBagConstraints gbc_jLabelIcon = new GridBagConstraints();
		gbc_jLabelIcon.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelIcon.gridx = 0;
		gbc_jLabelIcon.gridy = 0;
		gbc_jLabelIcon.insets = new Insets(5, 100, 0, 15);
		panelContent.add(jLabelIcon,gbc_jLabelIcon);
		
		JPanel jPanelUserGroupeName = new JPanel();
		jPanelUserGroupeName.setLayout(gbl);
		GridBagConstraints gbc_jPanelUserGroupeName = new GridBagConstraints();
		gbc_jPanelUserGroupeName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jPanelUserGroupeName.gridx = 1;
		gbc_jPanelUserGroupeName.gridy = 0;
		gbc_jPanelUserGroupeName.insets = new Insets(5, 10, 0, 0);
		panelContent.add(jPanelUserGroupeName,gbc_jPanelUserGroupeName);
		
		JLabel jLabelUserGroupNameTitle = new JLabel(MessageBundle.getMessage("angal.menu.usergroupname"));
		jLabelUserGroupNameTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelUserGroupNameTitle = new GridBagConstraints();
		gbc_jLabelUserGroupNameTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelUserGroupNameTitle.gridx = 0;
		gbc_jLabelUserGroupNameTitle.gridy = 0;
		gbc_jLabelUserGroupNameTitle.insets = new Insets(0, 0, 0, 0);
		jPanelUserGroupeName.add(jLabelUserGroupNameTitle,gbc_jLabelUserGroupNameTitle);
	
		
		JLabel jLabelUserGroupName = new JLabel(MainMenu.getCurrentUser().getUserGroupName().substring(0, 1).toUpperCase() +
				MainMenu.getCurrentUser().getUserGroupName().substring(1));
		jLabelUserGroupName.setFont(new Font(getName(), Font.BOLD, E_RESIZE_CURSOR));
//		jLabelUserGroupNameTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelUserGroupName = new GridBagConstraints();
		gbc_jLabelUserGroupName.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelUserGroupName.gridx = 0;
		gbc_jLabelUserGroupName.gridy = 1;
		gbc_jLabelUserGroupName.insets = new Insets(0, 0, 0, 0);
		jPanelUserGroupeName.add(jLabelUserGroupName,gbc_jLabelUserGroupName);
		
		JLabel jLabelIconFullname = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "users-name-icon.png"));
		GridBagConstraints gbc_jLabelIconFullname = new GridBagConstraints();
		gbc_jLabelIconFullname.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelIconFullname.gridx = 0;
		gbc_jLabelIconFullname.gridy = 1;
		gbc_jLabelIconFullname.insets = new Insets(20, 100, 0, 15);
		panelContent.add(jLabelIconFullname,gbc_jLabelIconFullname);
		
		JPanel jPanelFullname = new JPanel();
		jPanelFullname.setLayout(gbl);
		GridBagConstraints gbc_jPanelFullname = new GridBagConstraints();
		gbc_jLabelIconFullname.fill = GridBagConstraints.HORIZONTAL;
		gbc_jPanelFullname.gridx = 1;
		gbc_jPanelFullname.gridy = 1;
		gbc_jPanelFullname.insets = new Insets(20, 10, 0, 0);
		panelContent.add(jPanelFullname,gbc_jPanelFullname);
		
		JLabel jLabelFullnameTitle = new JLabel(MessageBundle.getMessage("angal.menu.userfullname"));
		jLabelFullnameTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelFullnameTitle = new GridBagConstraints();
		gbc_jLabelFullnameTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelFullnameTitle.gridx = 0;
		gbc_jLabelFullnameTitle.gridy = 0;
		gbc_jLabelFullnameTitle.insets = new Insets(0, 0, 0, 0);
		jPanelFullname.add(jLabelFullnameTitle,gbc_jLabelFullnameTitle);
		
		JLabel jlabelFullname = new JLabel(MainMenu.getCurrentUser().getName() + " " + MainMenu.getCurrentUser().getSurname());
		jlabelFullname.setFont(new Font(getName(), Font.BOLD, E_RESIZE_CURSOR));
		GridBagConstraints gbc_jLabelFullname = new GridBagConstraints();
		gbc_jLabelFullname.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelFullname.gridx = 0;
		gbc_jLabelFullname.gridy = 1;
		gbc_jLabelFullname.insets = new Insets(0, 0, 0, 0);
		jPanelFullname.add(jlabelFullname,gbc_jLabelFullname);
		
		JLabel jLabelIconDescription = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "document-write-icon.png"));
		GridBagConstraints gbc_jLabelIconDescription = new GridBagConstraints();
		gbc_jLabelIconDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelIconDescription.gridx = 0;
		gbc_jLabelIconDescription.gridy = 2;
		gbc_jLabelIconDescription.insets = new Insets(20, 100, 0, 15);
		panelContent.add(jLabelIconDescription,gbc_jLabelIconDescription);
		
		JPanel jpanelDesciption = new JPanel();
		jpanelDesciption.setLayout(gbl);
		GridBagConstraints gbc_jpanelDesciption = new GridBagConstraints();
		gbc_jpanelDesciption.fill = GridBagConstraints.HORIZONTAL;
		gbc_jpanelDesciption.gridx = 1;
		gbc_jpanelDesciption.gridy = 2;
		gbc_jpanelDesciption.insets = new Insets(20, 10, 0, 0);
		panelContent.add(jpanelDesciption,gbc_jpanelDesciption);
		
		JLabel jlabelDesciptionTitle = new JLabel(MessageBundle.getMessage("angal.menu.userdescription"));
		jlabelDesciptionTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelDesciptionTitle = new GridBagConstraints();
		gbc_jLabelDesciptionTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelDesciptionTitle.gridx = 0;
		gbc_jLabelDesciptionTitle.gridy = 0;
		gbc_jLabelDesciptionTitle.insets = new Insets(0, 0, 0, 0);
		jpanelDesciption.add(jlabelDesciptionTitle,gbc_jLabelDesciptionTitle);
		
		JLabel jlabelDesciption = new JLabel(MainMenu.getCurrentUser().getDesc());
		jlabelDesciption.setFont(new Font(getName(), Font.BOLD, E_RESIZE_CURSOR));
		GridBagConstraints gbc_jLabelDesciption = new GridBagConstraints();
		gbc_jLabelDesciption.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelDesciption.gridx = 0;
		gbc_jLabelDesciption.gridy = 1;
		gbc_jLabelDesciption.insets = new Insets(0, 0, 0, 0);
		jpanelDesciption.add(jlabelDesciption,gbc_jLabelDesciption);
		
		JLabel jLabelIconContact = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "phone-icon.png"));
		GridBagConstraints gbc_jLabelIconContact = new GridBagConstraints();
		gbc_jLabelIconContact.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelIconContact.gridx = 0;
		gbc_jLabelIconContact.gridy = 3;
		gbc_jLabelIconContact.insets = new Insets(20, 100, 60, 15);
		panelContent.add(jLabelIconContact,gbc_jLabelIconContact);
		
		JPanel jPanelContact = new JPanel();
		jPanelContact.setLayout(gbl);
		GridBagConstraints gbc_jPanelContact = new GridBagConstraints();
		gbc_jPanelContact.fill = GridBagConstraints.HORIZONTAL;
		gbc_jPanelContact.gridx = 1;
		gbc_jPanelContact.gridy = 3;
		gbc_jPanelContact.insets = new Insets(20, 10, 50, 0);
		panelContent.add(jPanelContact,gbc_jPanelContact);
		
		JLabel jLabelContactTitle = new JLabel(MessageBundle.getMessage("angal.menu.userphonenumber"));
		jLabelContactTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelContactTitle = new GridBagConstraints();
		gbc_jLabelContactTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelContactTitle.gridx = 0;
		gbc_jLabelContactTitle.gridy = 0;
		gbc_jLabelContactTitle.insets = new Insets(0, 0, 0, 0);
		jPanelContact.add(jLabelContactTitle,gbc_jLabelContactTitle);
		
		JLabel jLabelContact = new JLabel(MainMenu.getCurrentUser().getPhone());
		jLabelContact.setFont(new Font(getName(), Font.BOLD, E_RESIZE_CURSOR));
		GridBagConstraints gbc_jLabelContact = new GridBagConstraints();
		gbc_jLabelContact.fill = GridBagConstraints.HORIZONTAL;
		gbc_jLabelContact.gridx = 0;
		gbc_jLabelContact.gridy = 1;
		gbc_jLabelContact.insets = new Insets(0, 0, 0, 0);
		jPanelContact.add(jLabelContact,gbc_jLabelContact);
		return panelContent;
	}
	private JPanel getPanelFooter(){
		if(panelFooter==null){
			panelFooter = new JPanel();
			panelFooter.add(getEditButton());
			panelFooter.add(getButtonChangePassword());
		}
		return panelFooter;
	}
	private JButton getButtonChangePassword() {
		buttonChangePassword = new JButton(MessageBundle.getMessage("angal.menu.changepassword"));
	buttonChangePassword.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ChangePassword();
			}
		});
		return buttonChangePassword;
		
	}
	private JButton getEditButton() {
		editButton = new JButton(MessageBundle.getMessage("angal.menu.updateprofile"));
		editButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new EditProfile();
			}
		});
		return editButton;
	}
	private class EditProfile extends JDialog{
		private JPanel panelFormEdit;
		private JPanel panelButton;
		private JLabel labelName;
		private JLabel labelPhoneNumber;
		private JLabel labelUserDescription;
		private JLabel labelSurname;
		private JTextField name;
		private JTextField surname;
		private JTextField phoneNumber;
		private JTextField userDescription;
		private JButton okButton;
		private JButton cancelButton;
		
		public EditProfile() {
			super(myFrame, MessageBundle.getMessage("angal.menu.updateprofile"), true);
			initialize();
		}
		private void initialize() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setMinimumSize(new Dimension(350,290));
			JPanel panContent = new JPanel();
			setLocationRelativeTo(null);
			panContent.setLayout(new BorderLayout());
			panelFormEdit = getPanelFormEdit();
			panelButton = getPanelButton();
			panContent.add(panelFormEdit);
			panContent.add(panelButton,BorderLayout.SOUTH);
			getContentPane().add(panContent);
			setVisible(true);
		}
		private JPanel getPanelFormEdit() {
			panelFormEdit = new JPanel();
			GridBagLayout gbl = new GridBagLayout();
			gbl.columnWidths = new int[] { 0 };
			gbl.columnWeights = new double[] { 1.0 };
			panelFormEdit.setLayout(gbl);
			
			labelName = new JLabel(MessageBundle.getMessage("angal.menu.firstname"));
			GridBagConstraints gbc_labelName = new GridBagConstraints();
			gbc_labelName.gridx = gbc_labelName.gridy = 0;
			gbc_labelName.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelName.insets = new Insets(0, 30, 0, 30);
			panelFormEdit.add(labelName,gbc_labelName);
			
			name = new JTextField();
			name.setText(MainMenu.getCurrentUser().getName());
			GridBagConstraints gbc_userNname = new GridBagConstraints();
			gbc_userNname.gridx = 0;
			gbc_userNname.gridy = 1;
			gbc_userNname.gridwidth = GridBagConstraints.REMAINDER;
			gbc_userNname.fill = GridBagConstraints.HORIZONTAL;
			gbc_userNname.insets =  new Insets(5, 30, 0, 30);
			panelFormEdit.add(name,gbc_userNname);
			
			labelSurname = new JLabel(MessageBundle.getMessage("angal.menu.surname"));
			GridBagConstraints gbc_labelSurname = new GridBagConstraints();
			gbc_labelSurname.gridx = 0;
			gbc_labelSurname.gridy = 2;
			gbc_labelSurname.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelSurname.insets =  new Insets(5, 30, 0, 30);
			panelFormEdit.add(labelSurname,gbc_labelSurname);
			
			surname = new JTextField();
			surname.setText(MainMenu.getCurrentUser().getSurname());
			GridBagConstraints gbc_surname = new GridBagConstraints();
			gbc_surname.gridx = 0;
			gbc_surname.gridy = 3;
			gbc_surname.gridwidth = GridBagConstraints.REMAINDER;
			gbc_surname.fill = GridBagConstraints.HORIZONTAL;
			gbc_surname.insets =  new Insets(5, 30, 0, 30);
			panelFormEdit.add(surname,gbc_surname);
			
			labelPhoneNumber = new JLabel(MessageBundle.getMessage("angal.menu.phonenumber"));
			GridBagConstraints gbc_labelPhoneNumber = new GridBagConstraints();
			gbc_labelPhoneNumber.gridx = 0;
			gbc_labelPhoneNumber.gridy = 4;
			gbc_labelPhoneNumber.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelPhoneNumber.insets =  new Insets(5, 30, 0, 30);
			panelFormEdit.add(labelPhoneNumber,gbc_labelPhoneNumber);
			
			phoneNumber = new JTextField();
			phoneNumber.setText(MainMenu.getCurrentUser().getPhone());
			GridBagConstraints gbc_phoneNumber = new GridBagConstraints();
			gbc_phoneNumber.gridx = 0;
			gbc_phoneNumber.gridy = 5;
			gbc_phoneNumber.gridwidth = GridBagConstraints.REMAINDER;
			gbc_phoneNumber.fill = GridBagConstraints.HORIZONTAL;
			gbc_phoneNumber.insets = new Insets(5, 30, 0, 30);
			panelFormEdit.add(phoneNumber,gbc_phoneNumber);
			
			labelUserDescription = new JLabel(MessageBundle.getMessage("angal.menu.description"));
			GridBagConstraints gbc_labelUserDescription = new GridBagConstraints();
			gbc_labelUserDescription.gridx = 0;
			gbc_labelUserDescription.gridy = 6;
			gbc_labelUserDescription.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelUserDescription.insets =  new Insets(5, 30, 0, 30);
			panelFormEdit.add(labelUserDescription,gbc_labelUserDescription);
			
			userDescription = new JTextField();
			userDescription.setText(MainMenu.getCurrentUser().getDesc());
			GridBagConstraints gbc_userDescription = new GridBagConstraints();
			gbc_userDescription.gridx = 0;
			gbc_userDescription.gridy = 7;
			gbc_userDescription.gridwidth = GridBagConstraints.REMAINDER;
			gbc_userDescription.fill = GridBagConstraints.HORIZONTAL;
			gbc_userDescription.insets = new Insets(5, 30, 0, 30);
			panelFormEdit.add(userDescription,gbc_userDescription);
			
			return panelFormEdit;
		}
		private JPanel getPanelButton() {
			panelButton = new JPanel();
			panelButton.setBackground(Color.white);
			okButton = getOkButton();
			cancelButton = getCancelButton();
			panelButton.add(okButton);
			panelButton.add(cancelButton);
			return panelButton;
		}
		private JButton getOkButton() {
			okButton = new JButton(MessageBundle.getMessage("angal.menu.ok"));
			okButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
						if(name.getText().equals("")) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.pleaseenteryourfirstname"));
							name.setText("");
						}
						if(surname.getText().equals("")) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.pleaseenteryoursurname"));
							surname.setText("");
						}
						if(phoneNumber.getText().equals("")) {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.pleaseenteryourphonenumber"));
							phoneNumber.setText("");
						}
						if(!name.equals("") && !surname.getText().equals("") && !phoneNumber.getText().equals("")) {
									MainMenu.getCurrentUser().setName(name.getText());
									MainMenu.getCurrentUser().setSurname(surname.getText());
									MainMenu.getCurrentUser().setPhone(phoneNumber.getText());
									MainMenu.getCurrentUser().setDesc(userDescription.getText());
									if(manager.updateUser(MainMenu.getCurrentUser())) {
										JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.menu.successfullymodifieddata"));
										dispose();
									}
						}
				}
			});
			return okButton;
		}
		private JButton getCancelButton() {
			cancelButton = new JButton(MessageBundle.getMessage("angal.menu.cancel"));
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!name.getText().equals("") || !phoneNumber.getText().equals("") || !surname.getText().equals("") || !userDescription.getText().equals("")) {
						name.setText("");
						surname.setText("");
						phoneNumber.setText("");
						userDescription.setText("");
					}
					else
						dispose();
				}
			});
			return cancelButton;
		}
	}
	private class ChangePassword extends JDialog{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JPanel panButton;
		private JPanel panelForm;
		private JButton okButton;
		private JButton cancelButton;
		private JLabel labelPassword;
		private JLabel labelNewPassword;
		private JLabel labelNewPasswordConfirm;
		private JPasswordField password;
		private JPasswordField newPassword;
		private JPasswordField newPasswordConfirm;
		public ChangePassword() {
			super(myFrame, MessageBundle.getMessage("angal.menu.changepassword"), true);
			initialize();
		}
		private void initialize() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setMinimumSize(new Dimension(300,250));
			JPanel contentPane = new JPanel();
			pack();
			setLocationRelativeTo(null);
			contentPane.setLayout(new BorderLayout());
			panButton = getPanButton();
			panelForm = getPanelForm();
			contentPane.add(panelForm);
			contentPane.add(panButton,BorderLayout.SOUTH);
			setContentPane(contentPane);
			setVisible(true);
			
		}
		private JPanel getPanelForm() {
			panelForm = new JPanel();
			initGridbagLayout();
			panelForm.setLayout(gbl);
			
			labelPassword = new JLabel(MessageBundle.getMessage("angal.menu.oldpassword"));
			GridBagConstraints gbc_labelPassword = new GridBagConstraints();
			gbc_labelPassword.gridx = gbc_labelPassword.gridy = 0;
			gbc_labelPassword.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelPassword.insets = new Insets(50, 30, 0, 30);
			panelForm.add(labelPassword,gbc_labelPassword);
			
			password = new JPasswordField();
			GridBagConstraints gbc_password = new GridBagConstraints();
			gbc_password.gridx = 0;
			gbc_password.gridy = 1;
			gbc_password.gridwidth = GridBagConstraints.REMAINDER;
			gbc_password.fill = GridBagConstraints.HORIZONTAL;
			gbc_password.insets =  new Insets(5, 30, 0, 30);
			panelForm.add(password,gbc_password);
			
			labelNewPassword = new JLabel(MessageBundle.getMessage("angal.menu.newpassword"));
			GridBagConstraints gbc_labelNewPassword = new GridBagConstraints();
			gbc_labelNewPassword.gridx = 0;
			gbc_labelNewPassword.gridy = 2;
			gbc_labelNewPassword.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelNewPassword.insets =  new Insets(5, 30, 0, 30);
			panelForm.add(labelNewPassword,gbc_labelNewPassword);
			
			newPassword = new JPasswordField();
			GridBagConstraints gbc_newPassword = new GridBagConstraints();
			gbc_newPassword.gridx = 0;
			gbc_newPassword.gridy = 3;
			gbc_newPassword.gridwidth = GridBagConstraints.REMAINDER;
			gbc_newPassword.fill = GridBagConstraints.HORIZONTAL;
			gbc_newPassword.insets =  new Insets(5, 30, 0, 30);
			panelForm.add(newPassword,gbc_newPassword);
			
			labelNewPasswordConfirm = new JLabel(MessageBundle.getMessage("angal.menu.confirmthenewpassword"));
			GridBagConstraints gbc_labelNewPasswordConfirm = new GridBagConstraints();
			gbc_labelNewPasswordConfirm.gridx = 0;
			gbc_labelNewPasswordConfirm.gridy = 4;
			gbc_labelNewPasswordConfirm.fill = GridBagConstraints.HORIZONTAL;
			gbc_labelNewPasswordConfirm.insets =  new Insets(5, 30, 0, 30);
			panelForm.add(labelNewPasswordConfirm,gbc_labelNewPasswordConfirm);
			
			newPasswordConfirm = new JPasswordField();
			GridBagConstraints gbc_newPasswordConfirm = new GridBagConstraints();
			gbc_newPasswordConfirm.gridx = 0;
			gbc_newPasswordConfirm.gridy = 5;
			gbc_newPasswordConfirm.gridwidth = GridBagConstraints.REMAINDER;
			gbc_newPasswordConfirm.fill = GridBagConstraints.HORIZONTAL;
			gbc_newPasswordConfirm.insets = new Insets(5, 30, 50, 30);
			panelForm.add(newPasswordConfirm,gbc_newPasswordConfirm);
			return panelForm;
		}
		private JPanel getPanButton() {
			panButton = new JPanel();
			panButton.setBackground(Color.white);
			okButton = getOkButton();
			cancelButton = getCancelButton();
			panButton.add(okButton);
			panButton.add(cancelButton);
			return panButton;
		}
		private JButton getOkButton() {
			okButton = new JButton(MessageBundle.getMessage("angal.menu.ok"));
			okButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
						String pwd = new String(password.getPassword());
						String newPwd = new String(newPassword.getPassword());
						String newPwdConfirm =  new String(newPasswordConfirm.getPassword());
						String oldPassword = MainMenu.getCurrentUser().getPasswd();
						if(!BCrypt.checkpw(pwd, oldPassword) || pwd.equals("")) {
							JOptionPane.showMessageDialog(ChangePassword.this, MessageBundle.getMessage("angal.menu.oldpasswordincorrectpleaseretry"));
							password.setText("");
						}
						else if(newPwd.length() < 6 || newPwd.equals("")) {
							JOptionPane.showMessageDialog(ChangePassword.this, MessageBundle.getMessage("angal.menu.pleaseenteranewvalidpasswordminimumsixcaracters"));
							newPassword.setText("");
						}
						else if(!newPwd.equals(newPwdConfirm) || newPwdConfirm.equals("")) {
							JOptionPane.showMessageDialog(ChangePassword.this, MessageBundle.getMessage("angal.menu.pleaseconfirmyournewpassword"));
							newPasswordConfirm.setText("");
						}
						if(BCrypt.checkpw(pwd, oldPassword) && !pwd.equals("")) {
							if(newPwd.length() >= 6 && !newPwd.equals("") && newPwd.equals(newPwdConfirm)) {
								String hashed = BCrypt.hashpw(new String(newPwd), BCrypt.gensalt());
								MainMenu.getCurrentUser().setPasswd(hashed);
								if(manager.updatePassword(MainMenu.getCurrentUser())) {
									JOptionPane.showMessageDialog(ChangePassword.this, MessageBundle.getMessage("angal.menu.passwordupdatedsuccessfully"));
									dispose();
								}
							}
						}
				}
			});
			return okButton;
		}
		private JButton getCancelButton() {
			cancelButton = new JButton(MessageBundle.getMessage("angal.menu.cancel"));
			cancelButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!password.getText().equals("") || !newPassword.getText().equals("") || !newPasswordConfirm.getText().equals("")) {
						password.setText("");
						newPassword.setText("");
						newPasswordConfirm.setText("");
					}
					else
						dispose();
				}
			});
			return cancelButton;
		}
	}
}
