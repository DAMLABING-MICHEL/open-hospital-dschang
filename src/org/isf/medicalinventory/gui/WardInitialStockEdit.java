package org.isf.medicalinventory.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.ward.model.Ward;

/**
 * 18-ago-2008
 * added by alex:
 * 	- product code
 *  - pieces per packet
 */

public class WardInitialStockEdit extends JDialog {
	

	private static EventListenerList MedicalEditListeners = new EventListenerList();
	 
	public interface MedicalEditListener extends EventListener {
	        public void MedicalUpdated(AWTEvent e);
	        public void MedicalInserted(AWTEvent e);
	    }

	    public static void addMedicalListener(MedicalEditListener l) {
	    	MedicalEditListeners.add(MedicalEditListener.class, l);
	    }

	    public static void removeMedicalListener(MedicalEditListener listener) {
	    	MedicalEditListeners.remove(MedicalEditListener.class, listener);
	    }

	    private void fireMedicalInserted() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
	        	
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};
				
	        EventListener[] listeners = MedicalEditListeners.getListeners(MedicalEditListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MedicalEditListener)listeners[i]).MedicalInserted(event);
	    }
	    
	    private void fireMedicalUpdated() {
	        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;};

	        EventListener[] listeners = MedicalEditListeners.getListeners(MedicalEditListener.class);
	        for (int i = 0; i < listeners.length; i++)
	            ((MedicalEditListener)listeners[i]).MedicalUpdated(event);
	    }
	    
	    
	   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int CODE_MAX_LENGTH = 25;

	private JPanel jContentPane = null;

	private JPanel dataPanel = null;

	private JPanel buttonPanel = null;

	private JButton cancelButton = null;

	private JButton okButton = null;
	
	
	private JLabel initialStockLabel = null;
	private JLabel medicalLabel = null;
	private JTextField initialStockField = null;
	private MedicalWard medicalward;
	private JComboBox medicalComboBox;
	private boolean insert = false;
	private Ward ward;


	/**
	 * 
	 * This is the default constructor; we pass the arraylist and the
	 * selectedrow because we need to update them
	 */
	public WardInitialStockEdit(MedicalWard old, Ward ward, boolean inserting,JFrame owner) {
		super(owner,true);
		//super();
		insert = inserting;
		medicalward = old; // medical will be used for every operation
		this.ward = ward;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.medicals.newmedicalrecord"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.medicals.editingmedicalrecord"));
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
			jContentPane.add(getDataPanel(), java.awt.BorderLayout.CENTER); // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH); // Generated
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
			dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS)); // Generated
			
			
			medicalLabel = new JLabel();
			medicalLabel.setText(MessageBundle.getMessage("angal.inventoryrow.medical")); // Generated
			medicalLabel.setAlignmentX(CENTER_ALIGNMENT);
			dataPanel.add(medicalLabel, null);
			dataPanel.add(getMedicalComboBox(), null);
			
			initialStockLabel = new JLabel();
			initialStockLabel.setText(MessageBundle.getMessage("angal.medicals.initialstocks")); // Generated
			initialStockLabel.setAlignmentX(CENTER_ALIGNMENT);
			dataPanel.add(initialStockLabel, null);
			dataPanel.add(getInitialStockField());
			
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
			buttonPanel.add(getOkButton(), null); // Generated
			buttonPanel.add(getCancelButton(), null); // Generated
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
			cancelButton.setText(MessageBundle.getMessage("angal.common.cancel")); // Generated
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
			okButton.setText(MessageBundle.getMessage("angal.common.ok")); // Generated
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MovBrowserManager manager = new MovBrowserManager();
					medicalward.setMedical((Medical) medicalComboBox.getSelectedItem());
					medicalward.setInitialstock(Double.parseDouble(initialStockField.getText()));
					medicalward.setMedical((Medical) medicalComboBox.getSelectedItem());
					try {
						boolean result = false;
						result = manager.updateMedicalWardInitialQuantity(ward.getCode(), medicalward.getMedical().getCode(), medicalward.getInitialstock());
						if (insert) { // inserting
							if (result) {
								System.out.println("medicalward created ");
								dispose();
							}
						} else { // updating
							if (result) {
								System.out.println("medicalward updated "); 
								dispose();
							}
						}
						fireMedicalUpdated();
						if (!result)
							JOptionPane.showMessageDialog(null,
									MessageBundle.getMessage("angal.medicals.thedatacouldnotbesaved"));

					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(null,
								MessageBundle.getMessage("angal.medicals.insertavalidvalue"));
					}
				}
			});
		}
		return okButton;
	}

	private JTextField getInitialStockField() {
		if (initialStockField == null) {
			if (insert)
				initialStockField = new JTextField(3);
			else{
				String initialStock = String.valueOf(medicalward.getInitialstock()) != null? String.valueOf(medicalward.getInitialstock()) : "";
				initialStockField = new JTextField(initialStock);
			}
		}
		initialStockField.setText(0.0 + "");
		return initialStockField;
	}
	


	/**
	 * This method initializes typeComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getMedicalComboBox() {
		if (medicalComboBox == null) {
			medicalComboBox = new JComboBox();
			MedicalBrowsingManager manager = new MedicalBrowsingManager();
			ArrayList<Medical> medicals = manager.getMedicals();
			if (insert) {
				for (Medical elem : medicals) {
					medicalComboBox.addItem(elem);
				}
				medicalComboBox.setSelectedIndex(-1);
			} else {
				medicalComboBox.addItem(medicalward.getMedical());
				for (Medical elem : medicals) {
					if(!(elem.getCode() == medicalward.getMedical().getCode())){
						medicalComboBox.addItem(elem);
					}					
				}
 				medicalComboBox.setEnabled(false);
			}
		}
		return medicalComboBox;
	}
}
