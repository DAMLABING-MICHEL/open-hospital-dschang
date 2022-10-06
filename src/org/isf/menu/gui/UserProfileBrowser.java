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
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.utils.db.BCrypt;
import org.isf.utils.jobjects.ModalJFrame;


public class UserProfileBrowser extends ModalJFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panelContent;
	private JPanel panelHeader;
	private JPanel panelFooter;
	private JButton buttonChangePassword;
	private JButton closeButton;
	
	private UserProfileBrowser myFrame;
	private UserBrowsingManager manager = new UserBrowsingManager();
	
	public UserProfileBrowser() {
		myFrame = this;
		initComponents();
	}
	private void initComponents() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(350, 550));
		setLocationRelativeTo(null); // center
		setTitle(MessageBundle.getMessage("angal.profile.profiletitle"));
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
	private void initGridBagLayoyt() {
		gbl.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0 };
		gbl.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
	}
	private JPanel getPanelHeader() {
		panelHeader = new JPanel();
		initGridBagLayoyt();
		panelHeader.setLayout(new BorderLayout());
		
		JPanel jPanelIcon = new JPanel();
		jPanelIcon.setLayout(gbl);
		panelHeader.add(jPanelIcon,BorderLayout.NORTH);
		JLabel jLabelIcon = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "userIcon.png"));
		GridBagConstraints gbc_jLabelIcon = new GridBagConstraints();
		gbc_jLabelIcon.gridx = 0;
		gbc_jLabelIcon.gridy = 0;
		gbc_jLabelIcon.insets = new Insets(25, 90, 0, 90);
		gbc_jLabelIcon.gridwidth = GridBagConstraints.REMAINDER;
		jPanelIcon.add(jLabelIcon,gbc_jLabelIcon);
		
		JPanel jPanelUserName = new JPanel();
		jPanelUserName.setLayout(gbl);
		panelHeader.add(jPanelUserName,BorderLayout.CENTER);
		JLabel jLabelUserName = new JLabel(MainMenu.getCurrentUser().getUserName().substring(0, 1).toUpperCase() + 
				MainMenu.getCurrentUser().getUserName().substring(1));
		jLabelUserName.setFont(new Font(getName(), Font.BOLD, 20));
		GridBagConstraints gbc_jLabelUserName = new GridBagConstraints();
		gbc_jLabelUserName.gridx = gbc_jLabelUserName.gridy = 0;
		gbc_jLabelUserName.gridwidth = GridBagConstraints.REMAINDER;
		gbc_jLabelUserName.insets = new Insets(0, 90, 0, 90);
		jPanelUserName.add(jLabelUserName,gbc_jLabelUserName);
		
		JLabel jLabelUserGroupName = new JLabel(MainMenu.getCurrentUser().getUserGroupName());
		jLabelUserGroupName.setFont(new Font(getName(), Font.BOLD, 15));
		jLabelUserGroupName.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelUserGroupName = new GridBagConstraints();
		gbc_jLabelUserGroupName.gridx = 0;
		gbc_jLabelUserGroupName.gridy = 1;
		gbc_jLabelUserGroupName.gridwidth = GridBagConstraints.REMAINDER;
		gbc_jLabelUserGroupName.insets = new Insets(0, 90, 60, 90);
		jPanelUserName.add(jLabelUserGroupName,gbc_jLabelUserGroupName);
		return panelHeader;
	}
	@SuppressWarnings("deprecation")
	private JPanel getPanelContent() {
		panelContent = new JPanel();
		initGridBagLayoyt();
		panelContent.setLayout(gbl);
		JLabel jLabelIcon = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "user-group-icon.png"));
		GridBagConstraints gbc_jLabelIcon = new GridBagConstraints();
		gbc_jLabelIcon.gridx = 0;
		gbc_jLabelIcon.gridy = 0;
		gbc_jLabelIcon.insets = new Insets(5, 100, 0, 15);
		panelContent.add(jLabelIcon,gbc_jLabelIcon);
		
		JPanel jpanelUserGroupeName = new JPanel();
		jpanelUserGroupeName.setLayout(gbl);
		GridBagConstraints gbc_jPanelUserGroupeName = new GridBagConstraints();
		gbc_jPanelUserGroupeName.gridx = 1;
		gbc_jPanelUserGroupeName.gridy = 0;
		gbc_jPanelUserGroupeName.insets = new Insets(5, 0, 0, 0);
		panelContent.add(jpanelUserGroupeName,gbc_jPanelUserGroupeName);
		
		JLabel jLabelUserGroupNameTitle = new JLabel("Groupe");
		jLabelUserGroupNameTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelUserGroupNameTitle = new GridBagConstraints();
		gbc_jLabelUserGroupNameTitle.gridx = 0;
		gbc_jLabelUserGroupNameTitle.gridy = 0;
		gbc_jLabelUserGroupNameTitle.insets = new Insets(0, 0, 0, 0);
		jpanelUserGroupeName.add(jLabelUserGroupNameTitle,gbc_jLabelUserGroupNameTitle);
		
		JLabel jLabelUserGroupName = new JLabel(MainMenu.getCurrentUser().getUserGroupName().substring(0, 1).toUpperCase() +
				MainMenu.getCurrentUser().getUserGroupName().substring(1));
		jLabelUserGroupName.setFont(new Font(getName(), Font.BOLD, E_RESIZE_CURSOR));
//		jLabelUserGroupNameTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelUserGroupName = new GridBagConstraints();
		gbc_jLabelUserGroupName.gridx = 0;
		gbc_jLabelUserGroupName.gridy = 1;
		gbc_jLabelUserGroupName.insets = new Insets(0, 0, 0, 0);
		jpanelUserGroupeName.add(jLabelUserGroupName,gbc_jLabelUserGroupName);
		
		JLabel jLabelIcon2 = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "users-name-icon.png"));
		GridBagConstraints gbc_jLabelIcon2 = new GridBagConstraints();
		gbc_jLabelIcon2.gridx = 0;
		gbc_jLabelIcon2.gridy = 1;
		gbc_jLabelIcon2.insets = new Insets(20, 100, 0, 15);
		panelContent.add(jLabelIcon2,gbc_jLabelIcon2);
		
		JPanel jPanelFullName = new JPanel();
		jPanelFullName.setLayout(gbl);
		GridBagConstraints gbc_jPanelFullName = new GridBagConstraints();
		gbc_jPanelFullName.gridx = 1;
		gbc_jPanelFullName.gridy = 1;
		gbc_jPanelFullName.insets = new Insets(20, 0, 0, 0);
		panelContent.add(jPanelFullName,gbc_jPanelFullName);
		
		JLabel jLabelFullNameTitle = new JLabel("Nom de famille");
		jLabelFullNameTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelFullNameTitle = new GridBagConstraints();
		gbc_jLabelFullNameTitle.gridx = 0;
		gbc_jLabelFullNameTitle.gridy = 0;
		gbc_jLabelFullNameTitle.insets = new Insets(0, 0, 0, 0);
		jPanelFullName.add(jLabelFullNameTitle,gbc_jLabelFullNameTitle);
		
		JLabel jlabelFullName = new JLabel(MainMenu.getCurrentUser().getSurname());
		GridBagConstraints gbc_jLabelFullName = new GridBagConstraints();
		gbc_jLabelFullName.gridx = 0;
		gbc_jLabelFullName.gridy = 1;
		gbc_jLabelFullName.insets = new Insets(0, 0, 0, 0);
		jPanelFullName.add(jlabelFullName,gbc_jLabelFullName);
		
		JLabel jLabelIcon3 = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "document-write-icon.png"));
		GridBagConstraints gbc_jLabelIcon3 = new GridBagConstraints();
		gbc_jLabelIcon3.gridx = 0;
		gbc_jLabelIcon3.gridy = 2;
		gbc_jLabelIcon3.insets = new Insets(20, 100, 0, 15);
		panelContent.add(jLabelIcon3,gbc_jLabelIcon3);
		
		JPanel jpanelDesciption = new JPanel();
		jpanelDesciption.setLayout(gbl);
		GridBagConstraints gbc_jpanelDesciption = new GridBagConstraints();
		gbc_jpanelDesciption.gridx = 1;
		gbc_jpanelDesciption.gridy = 2;
		gbc_jpanelDesciption.insets = new Insets(20, 0, 0, 0);
		panelContent.add(jpanelDesciption,gbc_jpanelDesciption);
		
		JLabel jlabelDesciptionTitle = new JLabel("Desciption");
		jlabelDesciptionTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelDesciptionTitle = new GridBagConstraints();
		gbc_jLabelDesciptionTitle.gridx = 0;
		gbc_jLabelDesciptionTitle.gridy = 0;
		gbc_jLabelDesciptionTitle.insets = new Insets(0, 0, 0, 0);
		jpanelDesciption.add(jlabelDesciptionTitle,gbc_jLabelDesciptionTitle);
		
		JLabel jlabelDesciption = new JLabel(MainMenu.getCurrentUser().getDesc());
		GridBagConstraints gbc_jLabelDesciption = new GridBagConstraints();
		gbc_jLabelDesciption.gridx = 0;
		gbc_jLabelDesciption.gridy = 1;
		gbc_jLabelDesciption.insets = new Insets(0, 0, 0, 0);
		jpanelDesciption.add(jlabelDesciption,gbc_jLabelDesciption);
		
		JLabel jLabelIcon4 = new JLabel(new ImageIcon("rsc" + File.separator + "icons" + File.separator + "phone-icon.png"));
		GridBagConstraints gbc_jLabelIcon4 = new GridBagConstraints();
		gbc_jLabelIcon4.gridx = 0;
		gbc_jLabelIcon4.gridy = 3;
		gbc_jLabelIcon4.insets = new Insets(20, 100, 0, 15);
		panelContent.add(jLabelIcon4,gbc_jLabelIcon4);
		
		JPanel jpanelContact = new JPanel();
		jpanelContact.setLayout(gbl);
		GridBagConstraints gbc_jpanelContact = new GridBagConstraints();
		gbc_jpanelContact.gridx = 1;
		gbc_jpanelContact.gridy = 3;
		gbc_jpanelContact.insets = new Insets(20, 0, 0, 0);
		panelContent.add(jpanelContact,gbc_jpanelContact);
		
		JLabel jlabelContactTitle = new JLabel("Contact");
		jlabelContactTitle.setForeground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_jLabelContactTitle = new GridBagConstraints();
		gbc_jLabelContactTitle.gridx = 0;
		gbc_jLabelContactTitle.gridy = 0;
		gbc_jLabelContactTitle.insets = new Insets(0, 0, 0, 0);
		jpanelContact.add(jlabelContactTitle,gbc_jLabelContactTitle);
		
		JLabel jlabelContact = new JLabel(MainMenu.getCurrentUser().getPhone());
		GridBagConstraints gbc_jLabelContact = new GridBagConstraints();
		gbc_jLabelContact.gridx = 0;
		gbc_jLabelContact.gridy = 1;
		gbc_jLabelContact.insets = new Insets(0, 0, 0, 0);
		jpanelContact.add(jlabelContact,gbc_jLabelContact);
		return panelContent;
	}
	private JPanel getPanelFooter(){
		if(panelFooter==null){
			panelFooter = new JPanel();
			panelFooter.add(getButtonChangePassword());
			panelFooter.add(getCloseButton());
		}
		return panelFooter;
	}
	private JButton getButtonChangePassword() {
		buttonChangePassword = new JButton("Changer le mot de passe");
	buttonChangePassword.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ChangePassword();
			}
		});
		return buttonChangePassword;
		
	}
	private JButton getCloseButton() {
		closeButton = new JButton(MessageBundle.getMessage("angal.inventory.close"));
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return closeButton;
	}
	public class ChangePassword extends JDialog{
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
			super(myFrame, "Changer le mot de passe", true);
			initialize();
		}
		private void initialize() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setMinimumSize(new Dimension(450,300));
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
			initGridBagLayoyt();
			panelForm.setLayout(gbl);
			
			labelPassword = new JLabel("Ancien mot de passe");
			GridBagConstraints gbc_labelPassword = new GridBagConstraints();
			gbc_labelPassword.gridx = gbc_labelPassword.gridy = 0;
			gbc_labelPassword.insets = new Insets(20, 90, 0, 0);
			panelForm.add(labelPassword,gbc_labelPassword);
			
			password = new JPasswordField();
			GridBagConstraints gbc_password = new GridBagConstraints();
			gbc_password.gridx = 0;
			gbc_password.gridy = 1;
			gbc_password.gridwidth = GridBagConstraints.REMAINDER;
			gbc_password.fill = GridBagConstraints.HORIZONTAL;
			gbc_password.insets = new Insets(5, 90, 0, 90);
			panelForm.add(password,gbc_password);
			
			labelNewPassword = new JLabel("Nouveau mot de passe");
			GridBagConstraints gbc_labelNewPassword = new GridBagConstraints();
			gbc_labelNewPassword.gridx = 0;
			gbc_labelNewPassword.gridy = 2;
			gbc_labelNewPassword.insets = new Insets(5, 90, 0, 0);
			panelForm.add(labelNewPassword,gbc_labelNewPassword);
			
			newPassword = new JPasswordField();
			GridBagConstraints gbc_newPassword = new GridBagConstraints();
			gbc_newPassword.gridx = 0;
			gbc_newPassword.gridy = 3;
			gbc_newPassword.gridwidth = GridBagConstraints.REMAINDER;
			gbc_newPassword.fill = GridBagConstraints.HORIZONTAL;
			gbc_newPassword.insets = new Insets(5, 90, 0, 90);
			panelForm.add(newPassword,gbc_newPassword);
			
			labelNewPasswordConfirm = new JLabel("Confirmez mot de passe");
			GridBagConstraints gbc_labelNewPasswordConfirm = new GridBagConstraints();
			gbc_labelNewPasswordConfirm.gridx = 0;
			gbc_labelNewPasswordConfirm.gridy = 4;
			gbc_labelNewPasswordConfirm.insets = new Insets(5, 90, 0, 0);
			panelForm.add(labelNewPasswordConfirm,gbc_labelNewPasswordConfirm);
			
			newPasswordConfirm = new JPasswordField();
			GridBagConstraints gbc_newPasswordConfirm = new GridBagConstraints();
			gbc_newPasswordConfirm.gridx = 0;
			gbc_newPasswordConfirm.gridy = 5;
			gbc_newPasswordConfirm.gridwidth = GridBagConstraints.REMAINDER;
			gbc_newPasswordConfirm.fill = GridBagConstraints.HORIZONTAL;
			gbc_newPasswordConfirm.insets = new Insets(5, 90, 0, 90);
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
			okButton = new JButton("Ok");
			okButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
						String pwd = new String(password.getPassword());
						String newPwd = new String(newPassword.getPassword());
						String newPwdConfirm =  new String(newPasswordConfirm.getPassword());
						String oldPassword = MainMenu.getCurrentUser().getPasswd();
						if(!BCrypt.checkpw(pwd, oldPassword) || pwd.equals("")) {
							JOptionPane.showMessageDialog(ChangePassword.this, "Ancien mot de passe incorrect,veuillez réessayer!");
							password.setText("");
						}
						else if(newPwd.length() < 6 || newPwd.equals("")) {
							JOptionPane.showMessageDialog(ChangePassword.this, "Veuillez saisir un nouveau mot de passe valide!"
									+ " minimum six caractères");
							newPassword.setText("");
						}
						else if(!newPwd.equals(newPwdConfirm) || newPwdConfirm.equals("")) {
							JOptionPane.showMessageDialog(ChangePassword.this, "Veuillez confirmer votre mot de passe!");
							newPasswordConfirm.setText("");
						}
						if(BCrypt.checkpw(pwd, oldPassword) && !pwd.equals("")) {
							if(newPwd.length() >= 6 && !newPwd.equals("") && newPwd.equals(newPwdConfirm)) {
								String hashed = BCrypt.hashpw(new String(newPwd), BCrypt.gensalt());
								MainMenu.getCurrentUser().setPasswd(hashed);
								if(manager.updatePassword(MainMenu.getCurrentUser())) {
									JOptionPane.showMessageDialog(ChangePassword.this, "Votre mot de passe a été mis à jour avec succcès!");
									password.setText("");
									newPassword.setText("");
									newPasswordConfirm.setText("");
								}
							}
						}
				}
			});
			return okButton;
		}
		private JButton getCancelButton() {
			cancelButton = new JButton("Cancel");
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
