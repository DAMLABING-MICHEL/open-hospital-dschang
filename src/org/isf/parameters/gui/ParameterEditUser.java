
/*------------------------------------------
 * ExamEdit - add/edit an exam
 * -----------------------------------------
 * modification history
 * 03/11/2006 - ross - Enlarged Destription from 50 to 100 
 *                   - removed toupper for the description
 * 			         - version is now 1.0 
 *------------------------------------------*/

package org.isf.parameters.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.*;
import org.isf.exatype.model.ExamType;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.gui.InventoryEdit.InventoryListener;
import org.isf.parameters.manager.Param;
import org.isf.parameters.manager.ParametersManager;
import org.isf.parameters.model.Parameter;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.EventListenerList;

public class ParameterEditUser extends ModalJFrame {

	private static final long serialVersionUID = 1L;
	//private final String FILE_PROPERTIES = "generalData.properties";
	private final String FILE_PROPERTIES = Param.string("GENERALDATA_PROPERTIES");
	
	// listeners/////////////////////////////////////
	private static EventListenerList parametersListeners = new EventListenerList();

	public interface ParameterListener extends EventListener {
		public void ParameterUpdated(AWTEvent e);

		public void ParameterInserted(AWTEvent e);		

		public void ParameterDeleted(AWTEvent e);
	}

	public static void addParameterListener(ParameterListener l) {
		parametersListeners.add(ParameterListener.class, l);
	}

	public static void removeParameterListener(ParameterListener listener) {
		parametersListeners.remove(ParameterListener.class, listener);
	}

	private void fireParameterInserted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = parametersListeners.getListeners(ParameterListener.class);
		for (int i = 0; i < listeners.length; i++)
			((ParameterListener) listeners[i]).ParameterInserted(event);
	}

	private void fireParameterUpdated() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = parametersListeners.getListeners(ParameterListener.class);
		for (int i = 0; i < listeners.length; i++)
			((ParameterListener) listeners[i]).ParameterUpdated(event);
	}

	

	private void fireParameterDeleted() {
		AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		};

		EventListener[] listeners = parametersListeners.getListeners(ParameterListener.class);
		for (int i = 0; i < listeners.length; i++)
			((ParameterListener) listeners[i]).ParameterDeleted(event);
	}

	/////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////

	private static final String VERSION=MessageBundle.getMessage("angal.versione");  
    
	private JPanel jContentPane = null;
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JLabel descLabel = null;
	private JLabel codeLabel= null;
	private JLabel procLabel = null;
	private JLabel defLabel = null;
	private JLabel typeLabel = null;
	private Parameter parameter = null;
	private boolean insert = false;
	private JLabel codesLabel;
	private JTextField codeTextField;
	private JLabel valueLabel;
	private JComboBox valueComboBox;
	private JComboBox defaultValueComboBox;
	private JPanel titlePanel;
	private JLabel titleLabel;
	private String[] patternExamples = { "",MessageBundle.getMessage("angal.parameteredit.yes"),
			MessageBundle.getMessage("angal.parameteredit.no")};
	private JRadioButton booleanRadio;
	private JRadioButton alphanumRadio;
	/**
     * 
	 * This is the default constructor; we pass the arraylist and the selectedrow
     * because we need to update them
	 */
	public ParameterEditUser(JFrame owner, Parameter old, boolean inserting) {
		//super();
		insert = inserting;
		parameter = old;		//medical will be used for every operation
		initialize();
	}
	/**
	 * @wbp.parser.constructor
	 */
	public ParameterEditUser(Parameter old, boolean inserting) {
		//super();
		insert = inserting;
		parameter = old;		//medical will be used for every operation
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
        final int pfrmBase = 20;
        final int pfrmWidth = 7;
        final int pfrmHeight = 8;
        this.setBounds((screensize.width - screensize.width * pfrmWidth / pfrmBase ) / 2, (screensize.height - screensize.height * pfrmHeight / pfrmBase)/2, 
                515, 230);
		this.setContentPane(getJContentPane());
		if (insert) {
			titleLabel.setText(MessageBundle.getMessage("angal.parameteredit.titleadd")); 
		} else {
			titleLabel.setText(MessageBundle.getMessage("angal.parameteredit.titleedit")); 
		}
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		///
		if(!insert && parameter!=null){
			String val = parameter.getValue()!=null?parameter.getValue():"";
			if(val !=null && !val.equals("yes") && !val.equals("no")){
				alphanumRadio.setSelected(true);
				valueComboBox.setEditable(true);
				valueComboBox.removeAllItems();
				valueComboBox.setSelectedItem(parameter.getValue());
				getButtonSubComponent(valueComboBox).setVisible(false);				
			}	
												
			String value = parameter.getValue();
			if((val !=null && val.equals("yes")) || (val !=null && val.equals("no"))){
				booleanRadio.setSelected(true);					
				if(value !=null && value.equals("yes"))
					valueComboBox.setSelectedIndex(1);
				if(value !=null && value.equals("no"))
					valueComboBox.setSelectedIndex(2);							
			}
		}
		///
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
			jContentPane.add(getTitlePanel(), BorderLayout.NORTH);
			jContentPane.add(getDataPanel(), BorderLayout.CENTER);  // Generated
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
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
			GridBagLayout gbl_dataPanel = new GridBagLayout();
			gbl_dataPanel.columnWidths = new int[]{49, 0, 0, 0, 0, 0, 42, 0};
			gbl_dataPanel.rowHeights = new int[]{20, 0, 0, 0, 0, 0, 0};
			gbl_dataPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
			gbl_dataPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			dataPanel.setLayout(gbl_dataPanel);
			GridBagConstraints gbc_codesLabel = new GridBagConstraints();
			gbc_codesLabel.anchor = GridBagConstraints.WEST;
			gbc_codesLabel.insets = new Insets(0, 0, 5, 5);
			gbc_codesLabel.gridx = 2;
			gbc_codesLabel.gridy = 1;
			dataPanel.add(getCodesLabel(), gbc_codesLabel);
			GridBagConstraints gbc_codeTextField = new GridBagConstraints();
			gbc_codeTextField.gridwidth = 3;
			gbc_codeTextField.insets = new Insets(0, 0, 5, 5);
			gbc_codeTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_codeTextField.gridx = 3;
			gbc_codeTextField.gridy = 1;
			dataPanel.add(getCodeTextField(), gbc_codeTextField);
			GridBagConstraints gbc_booleanRadio = new GridBagConstraints();
			gbc_booleanRadio.anchor = GridBagConstraints.WEST;
			gbc_booleanRadio.insets = new Insets(0, 0, 5, 5);
			gbc_booleanRadio.gridx = 3;
			gbc_booleanRadio.gridy = 2;
			dataPanel.add(getBooleanRadio(), gbc_booleanRadio);
			GridBagConstraints gbc_alphanumRadio = new GridBagConstraints();
			gbc_alphanumRadio.anchor = GridBagConstraints.WEST;
			gbc_alphanumRadio.insets = new Insets(0, 0, 5, 5);
			gbc_alphanumRadio.gridx = 4;
			gbc_alphanumRadio.gridy = 2;
			dataPanel.add(getAlphanumRadio(), gbc_alphanumRadio);
			GridBagConstraints gbc_valueLabel = new GridBagConstraints();
			gbc_valueLabel.anchor = GridBagConstraints.WEST;
			gbc_valueLabel.insets = new Insets(0, 0, 5, 5);
			gbc_valueLabel.gridx = 2;
			gbc_valueLabel.gridy = 4;
			dataPanel.add(getValueLabel(), gbc_valueLabel);
			GridBagConstraints gbc_valueComboBox = new GridBagConstraints();
			gbc_valueComboBox.gridwidth = 3;
			gbc_valueComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_valueComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_valueComboBox.gridx = 3;
			gbc_valueComboBox.gridy = 4;
			dataPanel.add(getValueComboBox(), gbc_valueComboBox);
			GridBagConstraints gbc_defaultValueComboBox = new GridBagConstraints();
			gbc_defaultValueComboBox.gridwidth = 3;
			gbc_defaultValueComboBox.insets = new Insets(0, 0, 0, 5);
			gbc_defaultValueComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_defaultValueComboBox.gridx = 3;
			gbc_defaultValueComboBox.gridy = 5;
			//dataPanel.add(getDefaultValueComboBox(), gbc_defaultValueComboBox);
			
			ButtonGroup group = new ButtonGroup();
			group.add(alphanumRadio);
			group.add(booleanRadio);
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
			buttonPanel.setBorder(new MatteBorder(1, 0, 0, 0, (Color) new Color(0, 0, 0)));
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
			okButton.setText(MessageBundle.getMessage("angal.parameters.save"));  // Generated
            okButton.setMnemonic(KeyEvent.VK_E);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if((codeTextField.getText().trim().equals(""))
						||(valueComboBox.getSelectedItem().toString().trim().equals(""))
						){
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.parameter.pleaseinsertrequiredfields"));
					}
					else{	
						//ParametersManager manager = new ParametersManager();
						boolean result = false;
						if (insert) {
							Parameter parameter = new Parameter();						
							parameter.setCode(codeTextField.getText().trim().toString());
							if(alphanumRadio.isSelected()){
								parameter.setValue(valueComboBox.getSelectedItem()!=null?valueComboBox.getSelectedItem().toString():"");								
							}
							if(booleanRadio.isSelected()){
								int index = valueComboBox.getSelectedIndex();
								parameter.setValue(index == 1?"yes":index == 2?"no":"");
							}
							try {
								result = newParameterInFile(parameter.getCode() , parameter.getValue());
							} catch (IOException e1) {
								e1.printStackTrace();
								result = false;
							}
						} else {
							parameter.setCode(codeTextField.getText().trim().toString());
							if(alphanumRadio.isSelected()){
								parameter.setValue(valueComboBox.getSelectedItem()!=null?valueComboBox.getSelectedItem().toString():"");								
							}
							if(booleanRadio.isSelected()){
								int index = valueComboBox.getSelectedIndex();
								parameter.setValue(index == 1?"yes":index == 2?"no":"");
							}
							try {
								result = updateParameterInFile(parameter.getCode() , parameter.getValue());
							} catch (IOException e1) {								
								e1.printStackTrace();
								result = false;
							}
						}
						if (!result){
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.exa.thedatacouldnotbesaved"));
						}
						else {
							JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.parameter.wellsaved"));							
							fireParameterInserted();
							dispose();
						}
					}
				}
			});
		}
		return okButton;
	}
	private boolean  newParameterInFile(String code, String value) throws IOException{
		Properties p = new Properties();
		FileInputStream in;
		FileOutputStream out;
		try {
			in = new FileInputStream("rsc" + File.separator + FILE_PROPERTIES);
			p.load(in);
			if(p.containsKey(code)) return false;
			p.put(code, value);
			out = new FileOutputStream("rsc" + File.separator + FILE_PROPERTIES);
			p.store(out, "");
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	private boolean  updateParameterInFile(String code, String value) throws IOException{		
		Properties p = new Properties();
		FileInputStream in;
		FileOutputStream out;
		try {
			in = new FileInputStream("rsc" + File.separator + FILE_PROPERTIES);
			p.load(in);
			if(!p.containsKey(code)) return false;
			p.setProperty(code, value);
			out = new FileOutputStream("rsc" + File.separator + FILE_PROPERTIES);
			p.store(out, "");
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;		
	}
	
	private JLabel getCodesLabel() {
		if (codesLabel == null) {
			codesLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.code")+"*");
			codesLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
		return codesLabel;
	}
	private JTextField getCodeTextField() {
		if (codeTextField == null) {
			codeTextField = new JTextField();
			codeTextField.setPreferredSize(new Dimension(0, 27));
			codeTextField.setMinimumSize(new Dimension(0, 27));
			codeTextField.setSize(new Dimension(0, 27));
			codeTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
			codeTextField.setColumns(10);
			if (parameter != null) {
				codeTextField.setText(parameter.getCode());
				codeTextField.setEnabled(false);
			}
		}
		return codeTextField;
	}
	private JLabel getValueLabel() {
		if (valueLabel == null) {
			valueLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.value"));
			valueLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		}
		return valueLabel;
	}
	private JComboBox getValueComboBox() {
		if (valueComboBox == null) {			
			valueComboBox = new JComboBox(patternExamples);
			valueComboBox.setPreferredSize(new Dimension(0, 27));
			valueComboBox.setMinimumSize(new Dimension(0, 27));
			valueComboBox.setSize(new Dimension(0, 27));
			valueComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
			valueComboBox.setEditable(false);
			if (parameter != null) {
				
				String val = parameter.getValue();
				System.out.println("ValueComboBox "+val);
				if(val !=null && val.equals("@true"))
					valueComboBox.setSelectedIndex(1);
				if(val !=null && val.equals("@false"))
					valueComboBox.setSelectedIndex(2);
			}
		}
		return valueComboBox;
	}
	private JComboBox getDefaultValueComboBox() {
		if (defaultValueComboBox == null) {			
			defaultValueComboBox = new JComboBox(patternExamples);
			defaultValueComboBox.setPreferredSize(new Dimension(0, 27));
			defaultValueComboBox.setMinimumSize(new Dimension(0, 27));
			defaultValueComboBox.setSize(new Dimension(0, 27));
			defaultValueComboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
			defaultValueComboBox.setEditable(false);
			if (parameter != null) {
				String val = parameter.getValue();
				if(val !=null && val.equals("@true"))
					defaultValueComboBox.setSelectedIndex(1);
				if(val !=null && val.equals("@false"))
					defaultValueComboBox.setSelectedIndex(2);
			}
		}
		return defaultValueComboBox;
	}
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new JPanel();
			titlePanel.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
			titlePanel.setPreferredSize(new Dimension(10, 35));
			titlePanel.setMinimumSize(new Dimension(10, 35));
			titlePanel.add(getTitleLabel());
		}
		return titlePanel;
	}
	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel(MessageBundle.getMessage("angal.parameteredit.titleadd"));
			titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
			titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return titleLabel;
	}
	
	private  JButton getButtonSubComponent(Container container) {
	      if (container instanceof JButton) {
	         return (JButton) container;
	      } else {
	         Component[] components = container.getComponents();
	         for (Component component : components) {
	            if (component instanceof Container) {
	               return getButtonSubComponent((Container)component);
	            }
	         }
	      }
	      return null;
	   }
	private JRadioButton getAlphanumRadio() {
		if (alphanumRadio == null) {
			alphanumRadio = new JRadioButton(MessageBundle.getMessage("angal.parameteredit.alphanum"));
			if(insert)
				alphanumRadio.setSelected(false);	
			else if(parameter!=null){
//				String val = parameter.getValue();
//				if(val !=null && !val.equals("@true") && !val.equals("@false")){
//					alphanumRadio.setSelected(true);
//					valueComboBox.setEditable(true);
//					defaultValueComboBox.setEditable(true);							
//					getButtonSubComponent(valueComboBox).setVisible(false);
//					getButtonSubComponent(defaultValueComboBox).setVisible(false);
//					valueComboBox.removeAllItems();
//					defaultValueComboBox.removeAllItems();
//				}					
			}
			alphanumRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (alphanumRadio.isSelected()) {
						valueComboBox.setEditable(true);							
						getButtonSubComponent(valueComboBox).setVisible(false);
						valueComboBox.removeAllItems();						
					}
				}
			});
		}
		return alphanumRadio;
	}
	private JRadioButton getBooleanRadio() {
		if (booleanRadio == null) {
			booleanRadio = new JRadioButton(MessageBundle.getMessage("angal.parameteredit.boolean"));
			if(insert)
				booleanRadio.setSelected(true);	
			else if(parameter!=null){
//				String val = parameter.getValue();
//				System.out.println("the value "+val);
//				if((val !=null && val.equals("@true")) || (val !=null && val.equals("@false"))){
//					booleanRadio.setSelected(true);	
//				}else{
//					System.out.println("the else "+val);
//				}					
			}
			booleanRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (booleanRadio.isSelected()) {
						valueComboBox.setEditable(false);
						getButtonSubComponent(valueComboBox).setVisible(true);
						valueComboBox.removeAllItems();
						valueComboBox.addItem("");
						valueComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.yes"));
						valueComboBox.addItem(MessageBundle.getMessage("angal.parameteredit.no"));
					}
				}
			});
		}
		return booleanRadio;  
	}
}
