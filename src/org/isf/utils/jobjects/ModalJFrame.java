package org.isf.utils.jobjects;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.isf.accounting.gui.BillBrowser;
import org.isf.generaldata.GeneralData;
import org.isf.parameters.manager.Param;

/**
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * 
 */
public class ModalJFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final JFrame frame = this;
	
	private final ImageIcon img = new ImageIcon("./rsc/icons/oh.png");
	

	/**
	 * method to enable/disable a owner JFrame launching this ModalJFrame
	 * @param owner - the JFrame owner
	 */
	public void showAsModal(final JFrame owner) {
		
		setIconImage(img.getImage());

		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				if(Param.bool("WITHMODALWINDOW")){
					owner.setEnabled(false);
				}else{
					owner.setEnabled(true);
				}
			}

			public void windowClosing(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}

			public void windowClosed(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}
		});
		
		owner.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (frame.isShowing()) {
					frame.setExtendedState(JFrame.NORMAL);
					frame.toFront();
				} else {
					owner.removeWindowListener(this);
				}
			}
		});
		
		frame.setVisible(true);
	}
	
	public void showAsModal(final JDialog owner) {
		
		setIconImage(img.getImage());

		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				//owner.setEnabled(false);
				if(Param.bool("WITHMODALWINDOW")){
					owner.setEnabled(false);
				}else{
					owner.setEnabled(true);
				}
			}

			public void windowClosing(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}

			public void windowClosed(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}
		});
		
		owner.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (frame.isShowing()) {
					frame.setExtendedState(JFrame.NORMAL);
					frame.toFront();
				} else {
					owner.removeWindowListener(this);
				}
			}
		});		
		frame.setVisible(true);
	}
	/////////////////////////////////////
public void show(final JFrame owner) {
		
		setIconImage(img.getImage());

		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				//owner.setEnabled(false);
				//System.out.println("windowOpened 1");
				if(Param.bool("WITHMODALWINDOW")){
					owner.setEnabled(false);
				}else{
					owner.setEnabled(true);
				}
			}

			public void windowClosing(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}

			public void windowClosed(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}
		});
		
		owner.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (frame.isShowing()) {
					//System.out.println("frame is showing 1");
					//frame.setExtendedState(JFrame.NORMAL);
					//frame.toFront();
				} else {
					//System.out.println("else  is showing 1");
					owner.removeWindowListener(this);
				}
			}
		});		
		frame.setVisible(true);
	}
	
	public void show(final JDialog owner) {
		
		setIconImage(img.getImage());

		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				//owner.setEnabled(false);
				//owner.setEnabled(true);
				//System.out.println("windowOpened");
				if(Param.bool("WITHMODALWINDOW")){
					owner.setEnabled(false);
				}else{
					owner.setEnabled(true);
				}
			}

			public void windowClosing(WindowEvent e) {
				owner.setEnabled(true);
				//owner.toFront();
				frame.removeWindowListener(this);
			}

			public void windowClosed(WindowEvent e) {
				owner.setEnabled(true);
				owner.toFront();
				frame.removeWindowListener(this);
			}
		});
		
		owner.addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				if (frame.isShowing()) {
					frame.setExtendedState(JFrame.NORMAL);
				} else {
					//System.out.println("else  is showing");
					owner.removeWindowListener(this);
				}
			}
		});
		
		frame.setVisible(true);
	}
	/////////////////////////////////////
}