package org.isf.xmpp.service;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;


public class Server {
	private static Server server;
	private XMPPConnection connection;
	private String domain;
	private int port;
	private Roster roster;
	private String user;

	private Server(){

	}

	public void login(String userName, String password) throws XMPPException
	{
		String normalizeUsername=userName.replaceAll(" ", "").toLowerCase();
		XmppData.getXmppData();
		domain= XmppData.DOMAIN;
		port = XmppData.PORT;
		user=normalizeUsername;
		ConnectionConfiguration config = new ConnectionConfiguration(domain,port);
		connection = new XMPPConnection(config);
		connection.connect();
		
		try{
//			connection.login("admin", "admin");
			AccountManager user= connection.getAccountManager(); //new AccountManager(connection);
			
			 Map<String, String> attributes = new HashMap<String, String>();
	            attributes.put("username", normalizeUsername);
	            attributes.put("password", normalizeUsername);
//	            attributes.put("email", "krishna@gmail.com");
	            attributes.put("name", userName);

//	            accountManager.createAccount("krishna@target.local", "123456",attributes);
	            
			user.createAccount(normalizeUsername, normalizeUsername, attributes);
			connection.login(normalizeUsername, normalizeUsername);
		}catch (XMPPException e) {
			
			connection.login(normalizeUsername, normalizeUsername);
		}
	}

	public Roster getRoster()
	{
		roster=connection.getRoster();
		return roster;
	}

	public Chat getChat(String to,String id,MessageListener listner)
	{
		Chat chat=null;

		if(connection.getChatManager().getThreadChat(id+user)==null){
			System.out.println("chat in creazione con : "+to+" id= "+id);
			chat = connection.getChatManager().createChat(to,id+user,listner);
		}
		else{
			System.out.println("chat già esistente con: "+to+" id= "+id );
			chat= connection.getChatManager().getThreadChat(id+user);
		}
		return chat;
	}

	public ChatManager getChatManager(){
		return connection.getChatManager();
	}

	public String getUserAddress(){

		return "@"+connection.getHost();
	}

	public FileTransferManager getTransferManager(){
		FileTransferManager manager= new FileTransferManager(connection);
		return manager;
	}
	public Connection getConnection()
	{
		return connection;
	}

	public static Server getInstance()
	{
		if(server==null){
			server=new Server();
		}
			
		return server;
	}
}
