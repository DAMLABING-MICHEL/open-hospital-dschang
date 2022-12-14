package org.isf.vaccine.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
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

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.parameters.manager.Param;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.model.VaccineType;

/**
 * This class allow vaccines edits and inserts
 *
 * @author Eva
 *
 * modification history
 *  20/10/2011 - Cla - insert vaccinetype managment
 *
 */
//public class VaccineEdit extends JDialog
public class VaccineEdit extends ModalJFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EventListenerList vaccineListeners = new EventListenerList();

    public interface VaccineListener extends EventListener {
        public void vaccineUpdated(AWTEvent e);
        public void vaccineInserted(AWTEvent e);
    }

    public void addVaccineListener(VaccineListener l) {
        vaccineListeners.add(VaccineListener.class, l);
    }

    public void removeVaccineListener(VaccineListener listener) {
        vaccineListeners.remove(VaccineListener.class, listener);
    }

    private void fireVaccineInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = vaccineListeners.getListeners(VaccineListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((VaccineListener)listeners[i]).vaccineInserted(event);
    }
    private void fireVaccineUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

        EventListener[] listeners = vaccineListeners.getListeners(VaccineListener.class);
        for (int i = 0; i < listeners.length; i++)
            ((VaccineListener)listeners[i]).vaccineUpdated(event);
    }

    private int pfrmWidth;
    private int pfrmHeight;
    private int pfrmBordX;
    private int pfrmBordY;
	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JLabel descLabel = null;
	private JLabel codeLabel = null;
	private JLabel vaccineTypeDescLabel = null;
	private JLabel requiredLabel = null;
	private JTextField descriptionTextField = null;
	private JTextField codeTextField = null;
	private JComboBox vaccineTypeComboBox = null;
    private Vaccine vaccine = null;
	private boolean insert = false;

	/**
     *
	 * This is the default constructor; we pass the arraylist and the selected row
     * because we need to update them
	 */
	public VaccineEdit(JFrame owner, Vaccine old, boolean inserting) {
		//super(owner, true);
		super();
		insert = inserting;
		vaccine = old;		//operation will be used for every operation
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize = kit.getScreenSize();
		pfrmWidth = 350;
		pfrmHeight = 250;
		pfrmBordX = screensize.width / 2 - (pfrmWidth / 2);
		pfrmBordY = screensize.height / 2 - (pfrmHeight / 2);
		this.setBounds(pfrmBordX,pfrmBordY,pfrmWidth,pfrmHeight);
		this.setContentPane(getJContentPane());
		if (insert) {
			this.setTitle(MessageBundle.getMessage("angal.vaccine.newvaccinerecord"));
		} else {
			this.setTitle(MessageBundle.getMessage("angal.vaccine.editingvaccinerecord"));
		}
		
		//modal exclude
  		if(!Param.bool("WITHMODALWINDOW")){
  			setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
  		}
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
			
			//vaccine type
			vaccineTypeDescLabel = new JLabel();
			vaccineTypeDescLabel.setText(MessageBundle.getMessage("angal.vaccine.vaccinetype"));
            //vaccine code
			codeLabel = new JLabel();
			codeLabel.setText(MessageBundle.getMessage("angal.vaccine.code"));
			// vaccine description
			descLabel = new JLabel();
			descLabel.setText(MessageBundle.getMessage("angal.vaccine.description"));
			// required fields
			requiredLabel= new JLabel();
			requiredLabel.setText(MessageBundle.getMessage("angal.vaccine.requiredfields"));
			
			dataPanel = new JPanel();
			dataPanel.setLayout(new BoxLayout(getDataPanel(), BoxLayout.Y_AXIS));  // Generated
			dataPanel.add(vaccineTypeDescLabel, null);
			dataPanel.add(getvaccineTypeComboBox(),null);
			dataPanel.add(codeLabel, null);
			dataPanel.add(getCodeTextField(), null);
			dataPanel.add(descLabel, null);
			dataPanel.add(getDescriptionTextField(),null);
			dataPanel.add(requiredLabel, null);
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
					if (insert){
						String key = codeTextField.getText().trim();
						if (key.equals("")){
							JOptionPane.showMessageDialog(
			                        null,
			                        MessageBundle.getMessage("angal.vaccine.pleaseinsertacode"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);
							return;
						}
					    VaccineBrowserManager manager = new VaccineBrowserManager();

					    if (manager.codeControl(key)){
					    	JOptionPane.showMessageDialog(
			                        null,
			                        MessageBundle.getMessage("angal.vaccine.codealreadyinuse"),
			                        MessageBundle.getMessage("angal.hospital"),
			                        JOptionPane.PLAIN_MESSAGE);

							return;
						}

					}
					if (descriptionTextField.getText().trim().equals("")){
						JOptionPane.showMessageDialog(
		                        null,
		                        MessageBundle.getMessage("angal.vaccine.pleaseinsertadescription"),
		                        MessageBundle.getMessage("angal.hospital"),
		                        JOptionPane.PLAIN_MESSAGE);
						return;
					}

					VaccineBrowserManager manager = new VaccineBrowserManager();

					vaccine.setDescription(descriptionTextField.getText());
					vaccine.setCode(codeTextField.getText());
					vaccine.setVaccineType(new VaccineType(((VaccineType)vaccineTypeComboBox.getSelectedItem()).getCode(),
							                        ((VaccineType)vaccineTypeComboBox.getSelectedItem()).getDescription()));
					
					boolean result = false;
					if (insert) {     
						result = manager.newVaccine(vaccine);
						if (result) {
                           fireVaccineInserted();
                        }
                    }
                    else {                  
                    	result = manager.updateVaccine(vaccine);
						if (result) {
							fireVaccineUpdated();
                        }
					}
					if (!result) JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.vaccine.thedatacouldnotbesaved"));
                    else  dispose();
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
			descriptionTextField = new VoLimitedTextField(50);
			if (!insert) {
				descriptionTextField.setText(vaccine.getDescription());
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
			codeTextField = new VoLimitedTextField(10);
			if (!insert) {
				codeTextField.setText(vaccine.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}

	/**
	 * This method initializes vaccineTypeComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getvaccineTypeComboBox() {
		if (vaccineTypeComboBox == null) {
			vaccineTypeComboBox = new JComboBox();
			VaccineTypeBrowserManager manager = new VaccineTypeBrowserManager();
			ArrayList<VaccineType> types = manager.getVaccineType();
			for (VaccineType elem : types) {
				vaccineTypeComboBox.addItem(elem);
			}
			if (!insert) {
				vaccineTypeComboBox.setSelectedItem(vaccine.getVaccineType());
				vaccineTypeComboBox.setEnabled(false);				
			}
		}
		return vaccineTypeComboBox;
	}
}