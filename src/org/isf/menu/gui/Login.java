package org.isf.menu.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.*;
import org.isf.parameters.manager.Param;
import org.isf.utils.db.BCrypt;

import java.util.*;

import org.isf.generaldata.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login extends JDialog implements ActionListener, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 76205822226035164L;

	private final Logger logger = LoggerFactory.getLogger(Login.class);

	private EventListenerList loginListeners = new EventListenerList();

	public interface LoginListener extends EventListener {
		public void loginInserted(AWTEvent e);
	}
 
	public void addLoginListener(LoginListener listener) {
		loginListeners.add(LoginListener.class, listener);
	}

	public void removeLoginListener(LoginListener listener) {
		loginListeners.remove(LoginListener.class, listener);
	}

	private void fireLoginInserted(User aUser) {
		AWTEvent event = new AWTEvent(aUser, AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};
		EventListener[] listeners = loginListeners
				.getListeners(LoginListener.class);
		for (int i = 0; i < listeners.length; i++)
			((LoginListener) listeners[i]).loginInserted(event);
	}

	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ENTER) {
			String source = event.getComponent().getName();
			if (source.equalsIgnoreCase("pwd")) {
				acceptPwd();
			} else if (source.equalsIgnoreCase("submit")) {
				acceptPwd();
			} else if (source.equalsIgnoreCase("cancel")) {
				clearText();
			}
		}
	}

	public void keyTyped(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

	private ArrayList<User> users;
	private JComboBox usersList;
	//
	private JTextField username;
	//
	private JPasswordField pwd;
	private MainMenu parent;
	private User returnUser;

	public Login(MainMenu parent) {
		super(parent, "Login", true);

		ImageIcon img = new ImageIcon("./rsc/icons/oh.png");
		setIconImage(img.getImage());
		
		this.parent = parent;
		addLoginListener(parent);

		// add panel to frame
		LoginPanel panel = new LoginPanel(this);
		add(panel);
		pack();

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();

		Dimension mySize = getSize();

		setLocation((screensize.width - mySize.width) / 2,
				(screensize.height - mySize.height) / 2);

		setResizable(false);
		setVisible(true);
	}

	private void clearText() {
		pwd.setText("");
	}
	
	private void acceptPwd() {
		String userName = "";
		if(Param.bool("LOGINFORMWITHCHOOSINGUSERNAME")){
			userName = (String) usersList.getSelectedItem();
		}else{
			userName = username.getText();
		}
		
		String passwd = new String(pwd.getPassword());
		boolean found = false;
		boolean foundpass = false;
		boolean founduser = false;
		for (User u : users) {
			if (u.getUserName().equals(userName)) {
				//returnUser = u;
				founduser = true;
				if (BCrypt.checkpw(passwd, u.getPasswd())) {
					returnUser = u;
					foundpass = true;
				}
			}
		}
		if (!founduser) {
			String message = MessageBundle.getMessage("angal.menu.loginincorrectretry");
			//String message = "mauvais login";
			logger.warn("Login failed: " + message);
			JOptionPane.showMessageDialog(this, message, "",
					JOptionPane.PLAIN_MESSAGE);
		
			username.setText("");
		}
		if (founduser && !foundpass) {
			String message = MessageBundle.getMessage("angal.menu.passwordincorrectretry");
			//String message = "mauvais password";
			logger.warn("password failed: " + message);
			JOptionPane.showMessageDialog(this, message, "",
					JOptionPane.PLAIN_MESSAGE);
			pwd.setText("");
		}
		if (founduser && foundpass) {
			fireLoginInserted(returnUser);
			removeLoginListener(parent);
			this.dispose();
		} 
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals(MessageBundle.getMessage("angal.common.cancel"))) {
			logger.warn("Login cancelled.");
			dispose();
		} else if (command
				.equals(MessageBundle.getMessage("angal.menu.submit"))) {
			acceptPwd();
		}
	}

	private class LoginPanel extends JPanel {

		private static final long serialVersionUID = 4338749100444551874L;

		public LoginPanel(Login myFrame) {

			UserBrowsingManager manager = new UserBrowsingManager();
			users = manager.getUser();
			username = new JTextField(25);
			usersList = new JComboBox();
			for (User u : users)
				usersList.addItem(u.getUserName());

			Dimension d = usersList.getPreferredSize();
			usersList.setPreferredSize(new Dimension(160, d.height));
			JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
			
			if(Param.bool("LOGINFORMWITHCHOOSINGUSERNAME"))
				userPanel.add(usersList);
			else
				userPanel.add(username);	
			
			usersList.setSelectedItem("Admin U2G");  //JUST FOR TESTING PURPOSE
			pwd = new JPasswordField(25);
			pwd.setName("pwd");
			pwd.setText("");
			pwd.setText("u2g");  //JUST FOR TESTING PURPOSE
			pwd.addKeyListener(myFrame);

			JPanel pwdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
			pwdPanel.add(pwd);

			JButton submit = new JButton(
					MessageBundle.getMessage("angal.menu.submit"));
			submit.setMnemonic(KeyEvent.VK_S);
			JButton cancel = new JButton(
					MessageBundle.getMessage("angal.common.cancel"));
			cancel.setMnemonic(KeyEvent.VK_C);

			JPanel buttons = new JPanel();
			buttons.setLayout(new FlowLayout());
			buttons.add(submit);
			buttons.add(cancel);

			setLayout(new BorderLayout(10, 10));
			add(userPanel, BorderLayout.NORTH);
			add(pwdPanel, BorderLayout.CENTER);
			add(buttons, BorderLayout.SOUTH);

			submit.addActionListener(myFrame);
			submit.setName("submit");
			submit.addKeyListener(myFrame);
			cancel.addActionListener(myFrame);
			cancel.setName("cancel");
			cancel.addKeyListener(myFrame);

		}

	}
}
